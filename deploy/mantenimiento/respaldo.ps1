<#
.SYNOPSIS
  Respaldo de la base bd_colorin con mysqldump: comprimido, con SHA-256 y rotacion.
.DESCRIPTION
  Genera C:\respaldos-colorin\bd_colorin_AAAAMMDD_HHMMSS.zip (+ .sha256), valida que el
  volcado termine completo, borra los respaldos mas antiguos que -RetencionDias y escribe
  el resultado en respaldo.log. Codigo de salida 0 = correcto, 1 = fallo.
  La clave NO va en la linea de comandos: se lee de respaldo.cnf (ver respaldo.cnf.ejemplo).
.EXAMPLE
  .\respaldo.ps1
  .\respaldo.ps1 -Carpeta D:\respaldos -RetencionDias 30
#>
param(
    [string]$BaseDatos     = 'bd_colorin',
    [string]$Carpeta       = 'C:\respaldos-colorin',
    [string]$Config        = (Join-Path $PSScriptRoot 'respaldo.cnf'),
    [string]$HostBd        = 'localhost',
    [int]   $PuertoBd      = 3306,
    [int]   $RetencionDias = 14
)

$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force -Path $Carpeta | Out-Null
$log = Join-Path $Carpeta 'respaldo.log'

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

$sello = Get-Date -Format 'yyyyMMdd_HHmmss'
$sql   = Join-Path $Carpeta ('{0}_{1}.sql' -f $BaseDatos, $sello)
$zip   = Join-Path $Carpeta ('{0}_{1}.zip' -f $BaseDatos, $sello)
$err   = Join-Path $env:TEMP ('mysqldump_err_{0}.txt' -f $sello)

try {
    $dump = Buscar-Herramienta 'mysqldump'
    if (-not $dump) { throw 'No se encontro mysqldump (agregue la carpeta bin de MySQL al PATH)' }
    if (-not (Test-Path $Config)) { throw "Falta el archivo de credenciales: $Config (copie respaldo.cnf.ejemplo como respaldo.cnf)" }

    $argumentos = '--defaults-extra-file="{0}" --host={1} --port={2} --single-transaction --no-tablespaces --result-file="{3}" {4}' -f $Config, $HostBd, $PuertoBd, $sql, $BaseDatos
    $proceso = Start-Process -FilePath $dump -ArgumentList $argumentos -NoNewWindow -Wait -PassThru -RedirectStandardError $err
    if ($proceso.ExitCode -ne 0) {
        $detalle = ''
        if (Test-Path $err) { $detalle = (Get-Content $err -Raw) }
        throw ('mysqldump fallo (codigo {0}): {1}' -f $proceso.ExitCode, $detalle.Trim())
    }

    # Validacion: el volcado debe existir, no estar vacio y terminar con "Dump completed"
    if (-not (Test-Path $sql)) { throw 'No se genero el archivo de respaldo' }
    if ((Get-Item $sql).Length -lt 500) { throw 'El respaldo esta vacio o incompleto' }
    $cola = Get-Content $sql -Tail 5
    if (-not ($cola -match 'Dump completed')) { throw 'El respaldo no termina con "Dump completed" (volcado incompleto)' }

    Compress-Archive -Path $sql -DestinationPath $zip -CompressionLevel Optimal
    Remove-Item $sql -Force
    $hash = (Get-FileHash $zip -Algorithm SHA256).Hash
    Set-Content -Path ($zip + '.sha256') -Value ('{0}  {1}' -f $hash, (Split-Path $zip -Leaf)) -Encoding ASCII
    Registrar ('RESPALDO_OK archivo={0} bytes={1}' -f (Split-Path $zip -Leaf), (Get-Item $zip).Length)

    # Rotacion: borra respaldos mas antiguos que la retencion
    $limite = (Get-Date).AddDays(-$RetencionDias)
    Get-ChildItem $Carpeta -Filter ($BaseDatos + '_*.zip') | Where-Object { $_.LastWriteTime -lt $limite } | ForEach-Object {
        Remove-Item $_.FullName -Force
        Remove-Item ($_.FullName + '.sha256') -Force -ErrorAction SilentlyContinue
        Registrar ('RESPALDO_ROTADO archivo={0}' -f $_.Name)
    }
    $codigo = 0
}
catch {
    Registrar ('RESPALDO_FALLA motivo={0}' -f $_.Exception.Message)
    if (Test-Path $sql) { Remove-Item $sql -Force -ErrorAction SilentlyContinue }
    $codigo = 1
}
finally {
    if (Test-Path $err) { Remove-Item $err -Force -ErrorAction SilentlyContinue }
}
exit $codigo
