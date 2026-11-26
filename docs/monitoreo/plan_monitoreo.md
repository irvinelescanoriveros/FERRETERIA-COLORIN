# Plan de Monitoreo – SGIVFC

## 1. Objetivo
Detectar a tiempo caídas, lentitud y errores del sistema de escritorio, el backend web y la base de datos `bd_colorin`, y dejar evidencia para analizar incidentes.

## 2. Qué se monitorea (métricas clave)
| Componente | Indicador | Fuente | Umbral de alerta |
|-----------|-----------|--------|------------------|
| MySQL | Puerto 3306 responde | `revisar-salud.ps1` | no responde |
| Base de datos | Conexión y latencia (`SELECT 1`) | `ChequeoSalud` | falla o > 1 000 ms |
| Backend web | Disponibilidad | `GET /actuator/health` (200 = UP) | distinto de 200 |
| Backend web | Tiempo de respuesta | `revisar-salud.ps1` (web_ms) y `/actuator/metrics/http.server.requests` | > 1 000 ms |
| Backend web | Memoria JVM y CPU | `/actuator/metrics/jvm.memory.used`, `/actuator/metrics/process.cpu.usage` | revisión manual |
| Escritorio | Errores y operaciones críticas | `logs/ferreteria-N.log` (JSON), niveles WARNING y SEVERE | cualquier SEVERE |
| Ventas | Ventas registradas y ventas fallidas | log: "Venta registrada" / "No se pudo registrar la venta" | cualquier venta fallida |
| Seguridad | Intentos de acceso fallidos | log: "Login fallido" | 5 o más en 10 min para un mismo usuario |
| Equipo | Espacio en disco | `revisar-salud.ps1` (disco_pct) | > 85 % |

## 3. Herramientas implementadas
- **Logs estructurados (escritorio):** clase `Registro` (java.util.logging, sin librerías externas). Escribe una línea JSON por evento (`ts`, `nivel`, `origen`, `mensaje`, `error`) con niveles INFO, WARNING y SEVERE. Rotación: 1 MB por archivo, 5 archivos, en la carpeta `logs/`. Nunca se registran contraseñas ni hashes.
- **Eventos registrados:** inicio de la aplicación, errores no controlados, fallos de conexión a MySQL, login correcto y fallido, venta registrada y venta revertida por error.
- **Medición de rendimiento:** `Registro.medir(...)` registra `operacion=... duracion_ms=...` y sube a WARNING si la operación supera 1 segundo.
- **Backend web:** Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`) y archivo `logs/backend-web.log` con rotación (5 MB, 14 días).
- **Chequeo de salud programable:** `deploy/revisar-salud.ps1`. Revisa MySQL, la web, la base de datos y el disco; escribe una línea en `logs/monitoreo.log` y devuelve código 1 si hay ALERTA. Se programa con el Programador de tareas de Windows (ver plan de mantenimiento).
- **Herramientas a futuro:** Prometheus y Grafana (a partir de `/actuator/metrics`), Uptime Kuma para disponibilidad, OWASP ZAP para pruebas de seguridad.

## 4. Cómo consultar los registros (PowerShell)
- Últimos eventos: `Get-Content logs\ferreteria-0.log -Tail 20`
- Solo errores graves: `Select-String -Path logs\ferreteria-*.log -Pattern '"nivel":"SEVERE"'`
- Intentos de login fallidos: `Select-String -Path logs\ferreteria-*.log -Pattern 'Login fallido'`
- Resultado de las revisiones de salud: `Get-Content logs\monitoreo.log -Tail 10`
- Estado de la web: abrir `http://localhost:8080/actuator/health`

## 5. Alertas y respuesta
| Severidad | Condición | Acción | Plazo |
|-----------|-----------|--------|-------|
| Crítica | MySQL caído o web caída | Iniciar el servicio de MySQL; reiniciar con `levantar-sistema.ps1`; revisar `logs/web-error.log` | 15 min |
| Alta | Eventos SEVERE repetidos o ventas fallidas | Revisar la causa en el log y registrar la incidencia en GitHub | 4 h |
| Media | Operaciones lentas o disco > 85 % | Revisar consultas, limpiar logs y respaldos antiguos | 1 día |
| Baja | Intentos de login fallidos aislados | Observar | semanal |

## 6. Procedimiento de revisión
- Diario: revisar `logs/monitoreo.log` y buscar `ALERTA`.
- Semanal: revisar WARNING y SEVERE, uso de disco e intentos de login fallidos.
- Mensual: revisar tendencias de tiempos y las alertas de Dependabot y CodeQL.

## 7. Registro de incidentes
Cada incidente se documenta con: fecha y hora, síntoma, causa, acción realizada, duración y mejora propuesta.

## 8. Limitaciones conocidas
- Los logs de escritorio cubren los puntos críticos (arranque, login, ventas, conexión). Otros módulos todavía usan la consola y se migrarán gradualmente.
- El análisis automático (alertas por correo, paneles) queda como trabajo futuro.

## 9. Evidencias de la demostración
1. Mostrar `logs/ferreteria-0.log` con eventos JSON (inicio, login correcto, login fallido, venta).
2. Abrir `http://localhost:8080/actuator/health` y `/actuator/metrics`.
3. Ejecutar `deploy\revisar-salud.ps1` con todo encendido (resultado OK).
4. Detener MySQL y volver a ejecutarlo: debe mostrar `resultado=ALERTA`.
