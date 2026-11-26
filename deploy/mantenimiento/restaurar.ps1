<#
.SYNOPSIS
  Restaura un respaldo sobre la base real (bd_colorin). OPERACION DESTRUCTIVA: reemplaza los datos actuales.
.DESCRIPTION
  Pide escribir SI para continuar. Antes de restaurar, cierre el sistema de escritorio y detenga el
  backend web (.\levantar-sistema.ps1 -Detener). Requiere un usuario con permisos sobre bd_colorin
  (por ejemplo root), indicado en -Config. Haga primero un respaldo con respaldo.ps1.
.EXAMPLE
  .\restaurar.ps1 -Archivo C:\respaldos-colorin\bd_colorin_20261125_020000.zip -Config .\admin.cnf
#>
param(
    [Parameter(Mandatory = $true)][string]$Archivo,
    [Parameter(Mandatory = $true)][string]$Config,
    [string]$BaseDatos = 'bd_colorin',
    [string]$HostBd    = 'localhost',
    [int]   $PuertoBd  = 3306
)

$ErrorActionPreference = 'Stop'
$temporal = Join-Path $env:TEMP ('colorin_restaurar_' + (Get-Date -Format 'yyyyMMddHHmmss'))

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

function Ejecutar-Sql([string]$mysql, [string]$base, [string]$sentencia) {
    $err = Join-Path $env:TEMP ('mysql_err_' + [guid]::NewGuid().ToString('N') + '.txt')
    $argumentos = '--defaults-extra-file="{0}" --host={1} --port={2} {3} -e "{4}"' -f $Config, $HostBd, $PuertoBd, $base, $sentencia
    $p = Start-Process -FilePath $mysql -ArgumentList $argumentos -NoNewWindow -Wait -PassThru -RedirectStandardError $err
    $detalle = ''
    if (Test-Path $err) { $detalle = (Get-Content $err -Raw) }
    Remove-Item $err -Force -ErrorAction SilentlyContinue
    if ($p.ExitCode -ne 0) { throw ('mysql fallo: {0}' -f $detalle.Trim()) }
}

try {
    $mysql = Buscar-Herramienta 'mysql'
    if (-not $mysql) { throw 'No se encontro mysql.exe (agregue la carpeta bin de MySQL al PATH)' }
    if (-not (Test-Path $Archivo)) { throw "No existe el archivo: $Archivo" }
    if (-not (Test-Path $Config))  { throw "No existe el archivo de credenciales: $Config" }

    $archivoHash = $Archivo + '.sha256'
    if (Test-Path $archivoHash) {
        $esperado = ((Get-Content $archivoHash -Raw).Trim() -split '\s+')[0]
        if ($esperado -ne (Get-FileHash $Archivo -Algorithm SHA256).Hash) { throw 'El SHA-256 no coincide: el respaldo esta danado' }
        Write-Host 'Integridad SHA-256: OK'
    }

    Write-Host ''
    Write-Host ('ATENCION: se reemplazaran TODOS los datos de {0} por los del respaldo {1}.' -f $BaseDatos, (Split-Path $Archivo -Leaf)) -ForegroundColor Yellow
    $respuesta = Read-Host 'Escriba SI (en mayusculas) para continuar'
    if ($respuesta -cne 'SI') { Write-Host 'Cancelado. No se modifico nada.'; exit 0 }

    New-Item -ItemType Directory -Force -Path $temporal | Out-Null
    Expand-Archive -Path $Archivo -DestinationPath $temporal -Force
    $sql = Get-ChildItem $temporal -Filter *.sql | Select-Object -First 1
    if (-not $sql) { throw 'El zip no contiene un archivo .sql' }

    Ejecutar-Sql $mysql '' ('DROP DATABASE IF EXISTS {0}; CREATE DATABASE {0};' -f $BaseDatos)
    Ejecutar-Sql $mysql $BaseDatos ('source {0}' -f ($sql.FullName -replace '\\', '/'))
    Write-Host ('Restauracion completada en {0}.' -f $BaseDatos) -ForegroundColor Green
    Write-Host 'Siguiente paso: volver a crear los permisos de colorin_app si hiciera falta (database\04_usuario_aplicacion.sql),'
    Write-Host 'iniciar el sistema y comprobar el inicio de sesion y una consulta de ventas.'
    $codigo = 0
}
catch {
    Write-Host ('RESTAURACION_FALLA: {0}' -f $_.Exception.Message) -ForegroundColor Red
    $codigo = 1
}
finally {
    if (Test-Path $temporal) { Remove-Item $temporal -Recurse -Force -ErrorAction SilentlyContinue }
}
exit $codigo
