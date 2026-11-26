<#
.SYNOPSIS
  Borra los logs (.log) con mas de -Dias dias de antiguedad.
.DESCRIPTION
  Por defecto limpia la carpeta logs\ del proyecto de escritorio y, si existe, la del backend web.
  Con -Simular solo muestra lo que borraria. El resultado queda en logs\mantenimiento.log.
.EXAMPLE
  .\limpiar-logs.ps1 -Simular
  .\limpiar-logs.ps1 -Dias 15
#>
param(
    [int]$Dias = 30,
    [string[]]$Carpetas = @(
        (Join-Path (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path 'logs'),
        'C:\ProyectosJava\backend-web\logs'
    ),
    [switch]$Simular
)

$ErrorActionPreference = 'Continue'
$limite = (Get-Date).AddDays(-$Dias)
$existentes = @($Carpetas | Where-Object { Test-Path $_ })
if ($existentes.Count -eq 0) { Write-Host 'No hay carpetas de logs para limpiar.'; exit 0 }
$registro = Join-Path $existentes[0] 'mantenimiento.log'

function Registrar([string]$mensaje) {
    $linea = '{0} {1}' -f (Get-Date -Format 'yyyy-MM-ddTHH:mm:ss'), $mensaje
    Write-Host $linea
    Add-Content -Path $registro -Value $linea
}

$borrados = 0
$bytes = 0
foreach ($carpeta in $existentes) {
    $viejos = Get-ChildItem $carpeta -Filter *.log -File | Where-Object { $_.LastWriteTime -lt $limite }
    foreach ($archivo in $viejos) {
        if ($Simular) {
            Registrar ('SIMULACION borraria {0} ({1} bytes)' -f $archivo.FullName, $archivo.Length)
        } else {
            try {
                $tam = $archivo.Length
                Remove-Item $archivo.FullName -Force -ErrorAction Stop
                $borrados++
                $bytes += $tam
                Registrar ('LOG_BORRADO {0}' -f $archivo.FullName)
            } catch {
                Registrar ('LOG_OMITIDO {0} motivo={1}' -f $archivo.FullName, $_.Exception.Message)
            }
        }
    }
}
Registrar ('LIMPIEZA_FIN dias={0} borrados={1} bytes={2} simulacion={3}' -f $Dias, $borrados, $bytes, [bool]$Simular)
exit 0
