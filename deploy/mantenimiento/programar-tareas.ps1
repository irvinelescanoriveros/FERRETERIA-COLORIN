<#
.SYNOPSIS
  Registra (o quita) las tareas programadas de mantenimiento en el Programador de tareas de Windows.
.DESCRIPTION
  Equivalente en Windows de cron. Crea cuatro tareas (nombre Colorin-*):
    Colorin-Respaldo        diaria 02:00        respaldo.ps1
    Colorin-Salud           cada 5 minutos      ..\revisar-salud.ps1
    Colorin-LimpiezaLogs    domingos 03:00      limpiar-logs.ps1
    Colorin-PruebaRestaur.  domingos 04:00      probar-respaldo.ps1
  Si el equipo estaba apagado a la hora programada, la tarea se ejecuta al volver a encenderlo.
.EXAMPLE
  .\programar-tareas.ps1            (registrar)
  .\programar-tareas.ps1 -Quitar    (eliminar las tareas)
#>
param([switch]$Quitar)

$ErrorActionPreference = 'Stop'
$salud = Join-Path (Split-Path $PSScriptRoot -Parent) 'revisar-salud.ps1'

$tareas = @(
    @{ Nombre = 'Colorin-Respaldo';        Script = (Join-Path $PSScriptRoot 'respaldo.ps1');        Disparador = (New-ScheduledTaskTrigger -Daily -At '02:00');
       Descripcion = 'Respaldo diario de bd_colorin con mysqldump' },
    @{ Nombre = 'Colorin-Salud';           Script = $salud;                                          Disparador = (New-ScheduledTaskTrigger -Once -At (Get-Date) -RepetitionInterval (New-TimeSpan -Minutes 5) -RepetitionDuration (New-TimeSpan -Days 3650));
       Descripcion = 'Chequeo de salud cada 5 minutos (logs\monitoreo.log)' },
    @{ Nombre = 'Colorin-LimpiezaLogs';    Script = (Join-Path $PSScriptRoot 'limpiar-logs.ps1');    Disparador = (New-ScheduledTaskTrigger -Weekly -DaysOfWeek Sunday -At '03:00');
       Descripcion = 'Limpieza semanal de logs con mas de 30 dias' },
    @{ Nombre = 'Colorin-PruebaRestaur';   Script = (Join-Path $PSScriptRoot 'probar-respaldo.ps1'); Disparador = (New-ScheduledTaskTrigger -Weekly -DaysOfWeek Sunday -At '04:00');
       Descripcion = 'Prueba semanal de restauracion en bd_colorin_prueba' }
)

foreach ($t in $tareas) {
    if ($Quitar) {
        if (Get-ScheduledTask -TaskName $t.Nombre -ErrorAction SilentlyContinue) {
            Unregister-ScheduledTask -TaskName $t.Nombre -Confirm:$false
            Write-Host ('Eliminada: {0}' -f $t.Nombre)
        }
        continue
    }
    if (-not (Test-Path $t.Script)) {
        Write-Host ('OMITIDA {0}: no existe {1}' -f $t.Nombre, $t.Script) -ForegroundColor Yellow
        continue
    }
    $accion = New-ScheduledTaskAction -Execute 'powershell.exe' -Argument ('-NoProfile -ExecutionPolicy Bypass -File "{0}"' -f $t.Script)
    $ajustes = New-ScheduledTaskSettingsSet -StartWhenAvailable -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -ExecutionTimeLimit (New-TimeSpan -Hours 1)
    Register-ScheduledTask -TaskName $t.Nombre -Action $accion -Trigger $t.Disparador -Settings $ajustes -Description $t.Descripcion -Force | Out-Null
    Write-Host ('Registrada: {0}' -f $t.Nombre) -ForegroundColor Green
}

if (-not $Quitar) {
    Write-Host ''
    Write-Host 'Estado de las tareas:'
    Get-ScheduledTask -TaskName 'Colorin-*' | Get-ScheduledTaskInfo | Select-Object TaskName, NextRunTime, LastRunTime, LastTaskResult | Format-Table -AutoSize
    Write-Host 'Para probar una ahora:  Start-ScheduledTask -TaskName Colorin-Respaldo'
}
