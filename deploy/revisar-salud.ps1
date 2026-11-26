<#
.SYNOPSIS
  Revisa la salud del sistema SGIVFC y escribe el resultado en logs\monitoreo.log.
.DESCRIPTION
  Comprueba: (1) MySQL responde, (2) el backend web responde en /actuator/health,
  (3) la base de datos acepta consultas (ChequeoSalud, si hay clave), (4) espacio en disco.
  Termina con codigo 0 si todo esta bien y 1 si hay alguna ALERTA.
.EXAMPLE
  .\revisar-salud.ps1
  .\revisar-salud.ps1 -UrlWeb http://localhost:8080 -UmbralDiscoPct 85
#>
param(
    [string]$HostBd         = 'localhost',
    [int]   $PuertoBd       = 3306,
    [string]$UrlWeb         = 'http://localhost:8080',
    [string]$JarEscritorio  = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot '..')).Path 'target\ferreteria-colorin.jar'),
    [string]$CarpetaLogs    = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot '..')).Path 'logs'),
    [int]   $UmbralDiscoPct = 85
)

$ErrorActionPreference = 'Continue'
New-Item -ItemType Directory -Force -Path $CarpetaLogs | Out-Null
$alerta = $false
$ahora  = Get-Date -Format 'yyyy-MM-ddTHH:mm:ss'

# 1) MySQL: puerto abierto
$mysql = 'CAIDO'
try {
    $cliente = New-Object System.Net.Sockets.TcpClient
    $tarea = $cliente.ConnectAsync($HostBd, $PuertoBd)
    if ($tarea.Wait(3000) -and $cliente.Connected) { $mysql = 'OK' }
    $cliente.Close()
} catch { }
if ($mysql -ne 'OK') { $alerta = $true }

# 2) Backend web: /actuator/health y tiempo de respuesta
$webHttp = 0; $webMs = -1
try {
    $reloj = [System.Diagnostics.Stopwatch]::StartNew()
    $r = Invoke-WebRequest -Uri "$UrlWeb/actuator/health" -UseBasicParsing -TimeoutSec 5
    $reloj.Stop()
    $webHttp = [int]$r.StatusCode
    $webMs = [int]$reloj.ElapsedMilliseconds
} catch {
    if ($_.Exception.Response) { $webHttp = [int]$_.Exception.Response.StatusCode }
}
if ($webHttp -ne 200) { $alerta = $true }
elseif ($webMs -gt 1000) { $alerta = $true }

# 3) Base de datos desde la aplicacion de escritorio (necesita DB_PASSWORD en el entorno)
$bd = 'OMITIDO(sin DB_PASSWORD o sin JAR)'
if ($env:DB_PASSWORD -and (Test-Path $JarEscritorio)) {
    $salida = & java -cp "$JarEscritorio" pe.ferreteria.infraestructura.ChequeoSalud 2>$null
    $bd = ($salida | Select-Object -Last 1)
    if ($LASTEXITCODE -ne 0) { $alerta = $true }
}

# 4) Disco: porcentaje usado de la unidad donde estan los logs
$unidad = (Get-Item $CarpetaLogs).PSDrive
$usado = $unidad.Used; $libre = $unidad.Free
$discoPct = [int][math]::Round(100 * $usado / ($usado + $libre))
if ($discoPct -gt $UmbralDiscoPct) { $alerta = $true }

$resultado = if ($alerta) { 'ALERTA' } else { 'OK' }
$linea = "$ahora mysql=$mysql web_http=$webHttp web_ms=$webMs bd=[$bd] disco_pct=$discoPct resultado=$resultado"
Add-Content -Path (Join-Path $CarpetaLogs 'monitoreo.log') -Value $linea -Encoding UTF8
Write-Host $linea -ForegroundColor $(if ($alerta) { 'Red' } else { 'Green' })
if ($alerta) { exit 1 } else { exit 0 }
