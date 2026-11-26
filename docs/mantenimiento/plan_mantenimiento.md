# Plan de Mantenimiento – SGIVFC

## 1. Objetivo
Mantener disponible, íntegra y recuperable la información del sistema (base de datos `bd_colorin`, aplicación de escritorio y backend web), con tareas automáticas programadas y procedimientos documentados que cualquier administrador pueda seguir.

El plan se complementa con el [plan de monitoreo](../monitoreo/plan_monitoreo.md) (detecta los problemas) y el [plan de despliegue](../despliegue/plan_despliegue.md) (cómo se levanta el sistema). Aquí se describe cómo se **conserva** y se **corrige** el sistema en el tiempo.

## 2. Tipos de mantenimiento
| Tipo | Qué incluye en este proyecto | Cómo se realiza | Frecuencia |
|------|------------------------------|-----------------|------------|
| Preventivo | Respaldos de la base, prueba de restauración, limpieza de logs, chequeo de salud, `ANALYZE/CHECK TABLE`, revisión de dependencias | Scripts PowerShell programados en el Programador de tareas; Dependabot y CodeQL en GitHub | Diaria, semanal y mensual |
| Correctivo | Corrección de defectos registrados (`docs/pruebas/defectos_conocidos.md`) y de vulnerabilidades (`docs/seguridad/informe_seguridad.md`) | Corrección en el código, pruebas automáticas (`mvn test`), CI en GitHub Actions y commit con referencia al ID (DEF-xx / SEG-xx) | Según incidencia |
| Evolutivo | Mejoras surgidas de la evaluación con usuaria (`docs/pruebas/evaluacion_usuario.md`): gestión de estado activo/inactivo, edición de categorías y marcas, validación de roles en el servicio, cliente "Consumidor" por defecto | Priorización por criticidad; las no críticas quedan como trabajo futuro | Por versión |
| Adaptativo | Cambios de versión de Java, MySQL o Spring Boot; cambio de equipo o de carpeta de instalación | Actualizar `pom.xml`, volver a ejecutar las pruebas y el script de despliegue | Semestral o cuando cambie el entorno |

## 3. Tareas automáticas (cron de Windows)
En Windows el equivalente de `cron` es el **Programador de tareas**. Las tareas se registran con un solo comando: `deploy\mantenimiento\programar-tareas.ps1`.

| Tarea programada | Script | Programación | Qué hace |
|------------------|--------|--------------|----------|
| `Colorin-Respaldo` | `deploy\mantenimiento\respaldo.ps1` | Diaria, 02:00 | `mysqldump` de `bd_colorin`, comprime en `.zip`, guarda su SHA-256 y borra respaldos de más de 14 días |
| `Colorin-Salud` | `deploy\revisar-salud.ps1` | Cada 5 minutos | Comprueba MySQL, backend web y disco; escribe `logs\monitoreo.log` |
| `Colorin-LimpiezaLogs` | `deploy\mantenimiento\limpiar-logs.ps1` | Domingos, 03:00 | Borra logs de más de 30 días y registra lo borrado |
| `Colorin-PruebaRestaur` | `deploy\mantenimiento\probar-respaldo.ps1` | Domingos, 04:00 | Restaura el último respaldo en `bd_colorin_prueba` y compara tablas y conteos |

Si el equipo estaba apagado a la hora programada, la tarea se ejecuta al encenderlo (opción *StartWhenAvailable*).

Registrar las tareas:
```
cd C:\ProyectosJava\FERRETERIA-SISTEMA\deploy\mantenimiento
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\programar-tareas.ps1
```
Quitarlas: `.\programar-tareas.ps1 -Quitar`. Probar una al instante: `Start-ScheduledTask -TaskName Colorin-Respaldo`.

Mantenimiento mensual manual (fuera de horario, como administrador): ejecutar `mantenimiento-bd.sql` (`ANALYZE TABLE`, `CHECK TABLE` y detección de stock negativo; las consultas deben devolver 0 filas).

## 4. Política de respaldos
- **Contenido:** estructura y datos de `bd_colorin` (`mysqldump --single-transaction`, no bloquea las ventas mientras se respalda).
- **Ubicación:** `C:\respaldos-colorin\` (fuera del repositorio; los respaldos nunca se suben a GitHub).
- **Retención:** 14 días de respaldos diarios. Una vez al mes se conserva una copia en un disco externo o en la nube.
- **Integridad:** cada respaldo se valida (el volcado debe terminar con `Dump completed`) y se guarda su SHA-256 en un archivo `.sha256`. La prueba de restauración vuelve a verificarlo.
- **Seguridad:** la clave no se escribe en la línea de comandos; se lee de `respaldo.cnf`, que está en `.gitignore`. El respaldo usa un usuario propio (`colorin_respaldo`) con solo lectura sobre `bd_colorin` (ver `crear-usuario-respaldo.sql`), por el principio de mínimo privilegio.
- **Objetivos:** RPO ≤ 24 horas (se pierde como máximo un día de datos) y RTO ≤ 2 horas (tiempo máximo para volver a operar).

### Configuración inicial (una sola vez)
1. Ejecutar `crear-usuario-respaldo.sql` como administrador de MySQL (cambiando la clave).
2. Copiar `respaldo.cnf.ejemplo` como `respaldo.cnf` y escribir la misma clave.
3. Comprobar que `mysqldump` está disponible (`mysqldump --version`); si no, agregar la carpeta `bin` de MySQL al PATH.
4. Ejecutar `.\respaldo.ps1` una vez a mano y comprobar que aparezca `RESPALDO_OK` en `C:\respaldos-colorin\respaldo.log`.
5. Registrar las tareas con `.\programar-tareas.ps1`.

## 5. Procedimientos
### 5.1 Respaldo manual
`.\respaldo.ps1` (opcionalmente `-Carpeta D:\respaldos`). Resultado esperado: `RESPALDO_OK archivo=... bytes=...`. Si aparece `RESPALDO_FALLA`, el motivo queda en `respaldo.log`.

### 5.2 Prueba de restauración
`.\probar-respaldo.ps1` (restaura en `bd_colorin_prueba`, **no** toca la base real). Resultado esperado: `SHA256_OK`, `TABLAS real=N prueba=N`, conteos por tabla y `PRUEBA_RESTAURACION_OK` en `prueba_restauracion.log`. Para que los conteos coincidan, ejecutarla justo después de un respaldo.

### 5.3 Restaurar la base real (recuperación ante desastre)
1. Cerrar el sistema de escritorio y detener el backend web (`.\levantar-sistema.ps1 -Detener`).
2. Hacer un respaldo del estado actual (`.\respaldo.ps1`) por si la restauración no es la correcta.
3. Elegir el archivo en `C:\respaldos-colorin\` y ejecutar `.\restaurar.ps1 -Archivo <ruta.zip> -Config <admin.cnf>`. Pide escribir `SI` antes de reemplazar los datos.
4. Iniciar el sistema, comprobar el inicio de sesión y consultar las ventas del día.
5. Registrar el incidente (sección 5.6).

### 5.4 Actualizar la aplicación (con reversión)
1. Respaldo manual (`.\respaldo.ps1`).
2. Conservar el JAR anterior con extensión `.bak`.
3. Construir la nueva versión (escritorio desde NetBeans; web con `.\levantar-sistema.ps1 -Compilar`).
4. Ejecutar `mvn test` (todas las pruebas deben pasar) y levantar el sistema.
5. Verificar `http://localhost:8080/actuator/health` y una venta de prueba. Si falla, detener, restaurar el `.bak` y, si hubo cambios de base, restaurar el respaldo del paso 1.

### 5.5 Dependencias y seguridad
Revisar cada semana los Pull Requests de Dependabot; el flujo de CI (pruebas + CodeQL) debe pasar antes de fusionar. Las vulnerabilidades se registran en `informe_seguridad.md` con su identificador SEG-xx.

### 5.6 Registro de incidentes
Cada incidente se anota con: fecha y hora, síntoma, causa, acción tomada, tiempo de recuperación y mejora. Los defectos de código se registran en `defectos_conocidos.md` (DEF-xx) y se corrigen en una rama `fix/<id>`.

### 5.7 Gestión de usuarios
Altas y bajas desde el módulo Usuarios. Cambio de contraseñas cada 90 días y revisión trimestral de cuentas activas. Las contraseñas del script `03_datos_iniciales.sql` se cambian antes de usar el sistema con datos reales.

## 6. Calendario
| Frecuencia | Responsable | Actividades |
|------------|-------------|-------------|
| Diaria | Automática / administrador | Respaldo (02:00); revisar `respaldo.log` y `logs\monitoreo.log` buscando `FALLA` o `ALERTA` |
| Semanal | Administrador | Limpieza de logs y prueba de restauración (automáticas); revisar WARNING/SEVERE, Dependabot y espacio en disco |
| Mensual | Administrador | `mantenimiento-bd.sql`, copia mensual de respaldos a disco externo o nube |
| Trimestral | Administrador | Revisión de usuarios y contraseñas; revisión de este plan |

## 7. Evidencia de verificación
Los procedimientos se ejecutaron el 25 de noviembre de 2026 en el equipo de desarrollo (MySQL 8.0, servicio `MySQL80`). Las capturas se encuentran junto al informe.

| Procedimiento | Fecha | Resultado | Evidencia |
|---------------|-------|-----------|-----------|
| Respaldo manual (`respaldo.ps1`) | 25/11/2026 20:09 | Correcto: `RESPALDO_OK archivo=bd_colorin_20261125_200924.zip bytes=28511` | `respaldo.log`, archivo `.zip` y su `.sha256` |
| Prueba de restauración (`probar-respaldo.ps1`) | 25/11/2026 20:09 | Correcto: `SHA256_OK`, 22 tablas restauradas (real = 22, prueba = 22) y conteos iguales en Usuario (6), Producto (47), Cliente (16), Venta (11), Inventario (16) y Kardex (31). `PRUEBA_RESTAURACION_OK` | `prueba_restauracion.log` |
| Limpieza de logs en simulación (`limpiar-logs.ps1 -Simular`) | 25/11/2026 20:10 | Correcto: `LIMPIEZA_FIN dias=30 borrados=0` (no existen logs de más de 30 días) | Salida en consola y `logs\mantenimiento.log` |
| Registro de tareas (`programar-tareas.ps1`) | 25/11/2026 20:11 | Correcto: registradas `Colorin-Respaldo` (diaria 02:00), `Colorin-Salud` (cada 5 minutos), `Colorin-LimpiezaLogs` (domingos 03:00) y `Colorin-PruebaRestaur` (domingos 04:00) | Captura del Programador de tareas |
| Ejecución de una tarea programada (`Start-ScheduledTask -TaskName Colorin-Respaldo`) | 25/11/2026 20:11 | Correcto: `LastTaskResult = 0` y nueva línea `RESPALDO_OK archivo=bd_colorin_20261125_201126.zip bytes=28514` | `respaldo.log` y salida de `Get-ScheduledTaskInfo` |

**Conclusión:** el respaldo se genera y valida automáticamente, la restauración reproduce la base completa en `bd_colorin_prueba` y las tareas programadas se ejecutan sin intervención. El procedimiento de restauración sobre la base real (`restaurar.ps1`) está documentado en la sección 5.3 y no se ejecutó para no reemplazar los datos vigentes.