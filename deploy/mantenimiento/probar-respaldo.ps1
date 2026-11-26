<#
.SYNOPSIS
  Prueba de restauracion: restaura el ultimo respaldo en bd_colorin_prueba y compara con la base real.
.DESCRIPTION
  1) Verifica el SHA-256 del respaldo. 2) Lo descomprime. 3) Recrea bd_colorin_prueba y carga el volcado.
  4) Compara el numero de tablas y los conteos de las tablas principales.
  No toca bd_colorin. Escribe PRUEBA_RESTAURACION_OK o PRUEBA_RESTAURACION_FALLA en prueba_restauracion.log.
  Para que los conteos coincidan, ejecute esta prueba justo despues de respaldo.ps1.
.EXAMPLE
  .\probar-respaldo.ps1
  .\probar-respaldo.ps1 -Archivo C:\respaldos-colorin\bd_colorin_20261125_020000.zip -Limpiar
#>
param(
    [string]$BaseDatos   = 'bd_colorin',
    [string]$BasePrueba  = 'bd_colorin_prueba',
    [string]$Carpeta     = 'C:\respaldos-colorin',
    [string]$Archivo     = '',
    [string]$Config      = (Join-Path $PSScriptRoot 'respaldo.cnf'),
    [string]$HostBd      = 'localhost',
    [int]   $PuertoBd    = 3306,
    [switch]$Limpiar
)

$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force -Path $Carpeta | Out-Null
$log = Join-Path $Carpeta 'prueba_restauracion.log'
$temporal = Join-Path $env:TEMP ('colorin_prueba_' + (Get-Date -Format 'yyyyMMddHHmmss'))

function Registrar([string]$mensaje) {
    $linea = '{0} {1}' -f (Get-Date -Format 'yyyy-MM-ddTHH:mm:ss'), $mensaje
    Write-Host $linea
    Add-Content -Path $log -Value $linea
}

function Buscar-Herramienta([string]$nombre) {
    $cmd = Get-Command $nombre -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $patrones = @("C:\Program Files\MySQL\MySQL Server *\bin\$nombre.exe",
                  "C:\xampp\mysql\bin\$nombre.exe")
    foreach ($p in $patrones) {
        $hallado = Get-ChildItem $p -ErrorAction SilentlyContinue | Sort-Object FullName -Descending | Select-Object -First 1
        if ($hallado) { return $hallado.FullName }
    }
    return $null
}

# Ejecuta una sentencia SQL con mysql.exe y devuelve su salida (texto)
function Ejecutar-Sql([string]$mysql, [string]$base, [string]$sentencia) {
    $salida = Join-Path $env:TEMP ('mysql_out_' + [guid]::NewGuid().ToString('N') + '.txt')
    $err    = Join-Path $env:TEMP ('mysql_err_' + [guid]::NewGuid().ToString('N') + '.txt')
    $argumentos = '--defaults-extra-file="{0}" --host={1} --port={2} --batch --skip-column-names {3} -e "{4}"' -f $Config, $HostBd, $PuertoBd, $base, $sentencia
    $p = Start-Process -FilePath $mysql -ArgumentList $argumentos -NoNewWindow -Wait -PassThru -RedirectStandardOutput $salida -RedirectStandardError $err
    $texto = ''
    if (Test-Path $salida) { $texto = (Get-Content $salida -Raw) }
    $detalle = ''
    if (Test-Path $err) { $detalle = (Get-Content $err -Raw) }
    Remove-Item $salida, $err -Force -ErrorAction SilentlyContinue
    if ($p.ExitCode -ne 0) { throw ('mysql fallo: {0}' -f $detalle.Trim()) }
    if ($null -eq $texto) { return '' }
    return $texto.Trim()
}

try {
    $mysql = Buscar-Herramienta 'mysql'
    if (-not $mysql) { throw 'No se encontro mysql.exe (agregue la carpeta bin de MySQL al PATH)' }
    if (-not (Test-Path $Config)) { throw "Falta el archivo de credenciales: $Config" }

    if ($Archivo -eq '') {
        $ultimo = Get-ChildItem $Carpeta -Filter ($BaseDatos + '_*.zip') | Sort-Object LastWriteTime -Descending | Select-Object -First 1
        if (-not $ultimo) { throw "No hay respaldos en $Carpeta (ejecute primero respaldo.ps1)" }
        $Archivo = $ultimo.FullName
    }
    if (-not (Test-Path $Archivo)) { throw "No existe el archivo: $Archivo" }
    Registrar ('PRUEBA_INICIO archivo={0}' -f (Split-Path $Archivo -Leaf))

    # 1) Integridad: SHA-256
    $archivoHash = $Archivo + '.sha256'
    if (Test-Path $archivoHash) {
        $esperado = ((Get-Content $archivoHash -Raw).Trim() -split '\s+')[0]
        $real = (Get-FileHash $Archivo -Algorithm SHA256).Hash
        if ($esperado -ne $real) { throw 'El SHA-256 no coincide: el respaldo esta danado' }
        Registrar 'SHA256_OK'
    } else {
        Registrar 'AVISO no existe el archivo .sha256; se omite la verificacion de integridad'
    }

    # 2) Descompresion
    New-Item -ItemType Directory -Force -Path $temporal | Out-Null
    Expand-Archive -Path $Archivo -DestinationPath $temporal -Force
    $sql = Get-ChildItem $temporal -Filter *.sql | Select-Object -First 1
    if (-not $sql) { throw 'El zip no contiene un archivo .sql' }

    # 3) Restauracion en la base de prueba
    [void](Ejecutar-Sql $mysql '' ('DROP DATABASE IF EXISTS {0}; CREATE DATABASE {0};' -f $BasePrueba))
    $ruta = $sql.FullName -replace '\\', '/'
    [void](Ejecutar-Sql $mysql $BasePrueba ('source {0}' -f $ruta))
    Registrar ('RESTAURADO base={0}' -f $BasePrueba)

    # 4) Comparacion
    $consultaTablas = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='{0}'"
    $tablasReal   = Ejecutar-Sql $mysql '' ($consultaTablas -f $BaseDatos)
    $tablasPrueba = Ejecutar-Sql $mysql '' ($consultaTablas -f $BasePrueba)
    Registrar ('TABLAS real={0} prueba={1}' -f $tablasReal, $tablasPrueba)
    if ($tablasReal -ne $tablasPrueba -or [int]$tablasPrueba -eq 0) { throw 'El numero de tablas restauradas no coincide' }

    foreach ($tabla in @('Usuario', 'Producto', 'Cliente', 'Venta', 'Inventario', 'Kardex')) {
        $a = Ejecutar-Sql $mysql $BaseDatos ('SELECT COUNT(*) FROM {0}' -f $tabla)
        $b = Ejecutar-Sql $mysql $BasePrueba ('SELECT COUNT(*) FROM {0}' -f $tabla)
        $estado = 'igual'
        if ($a -ne $b) { $estado = 'difiere (puede deberse a movimientos posteriores al respaldo)' }
        Registrar ('CONTEO tabla={0} real={1} prueba={2} {3}' -f $tabla, $a, $b, $estado)
    }

    Registrar 'PRUEBA_RESTAURACION_OK'
    if ($Limpiar) {
        [void](Ejecutar-Sql $mysql '' ('DROP DATABASE IF EXISTS {0};' -f $BasePrueba))
        Registrar ('LIMPIEZA base={0} eliminada' -f $BasePrueba)
    }
    $codigo = 0
}
catch {
    Registrar ('PRUEBA_RESTAURACION_FALLA motivo={0}' -f $_.Exception.Message)
    $codigo = 1
}
finally {
    if (Test-Path $temporal) { Remove-Item $temporal -Recurse -Force -ErrorAction SilentlyContinue }
}
exit $codigo
