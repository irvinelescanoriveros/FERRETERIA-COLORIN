# Informe de Pruebas de Seguridad – SGIVFC (Semana 14)

Proyecto: Sistema de Gestión de Inventario y Ventas para Ferretería Colorín
Alcance: aplicación de escritorio (Java Swing + MySQL) y backend web (Spring Boot), base de datos compartida `bd_colorin`.
Autor: Irvin Elescano Riveros · Curso: Integrador I: Sistemas Software

## 1. Objetivo
Identificar y corregir vulnerabilidades según OWASP Top 10, y dejar evidencia verificable.

## 2. Metodología
1. Revisión estática manual del código (autenticación, acceso a datos, configuración).
2. Pruebas automatizadas JUnit de los controles implementados.
3. Análisis automático con herramientas (CodeQL y Dependabot configurados y ejecutados en GitHub Actions; OWASP ZAP, Dependency-Check y SpotBugs+FindSecBugs descritos en la sección 6 como trabajo futuro, aún no ejecutados).
## 3. Hallazgos y acciones

| ID | Hallazgo | OWASP | Severidad | Estado |
|----|----------|-------|-----------|--------|
| SEG-01 | Contraseñas guardadas y comparadas en texto plano en SQL (`WHERE password = ?`) | A02 / A07 | Alta | **Corregido**: PBKDF2-HMAC-SHA256, 210 000 iteraciones, sal aleatoria de 16 bytes; migración automática al iniciar sesión |
| SEG-02 | Credenciales de BD escritas en el código | A05 | Alta | **Corregido**: `Configuracion` (propiedad JVM → variable de entorno → `ferreteria.properties`); sin contraseña por defecto |
| SEG-03 | La aplicación se conecta como `root` | A05 / A01 | Media | **Mitigado**: script `database/04_usuario_aplicacion.sql` (usuario `colorin_app` con SELECT/INSERT/UPDATE/DELETE) |
| SEG-04 | CORS abierto (`@CrossOrigin(origins="*")`) en 4 controladores web | A05 | Media | **Corregido** (parche web): `CorsConfig` con orígenes configurables |
| SEG-05 | Cookies de sesión y errores sin endurecer en el web | A05 | Baja | **Corregido**: HttpOnly, SameSite=Lax, sin mensajes/stacktrace en errores |
| SEG-06 | Enumeración de usuarios por diferencia de tiempos en login | A07 | Baja | **Corregido**: verificación simulada si el usuario no existe; comparación en tiempo constante |
| SEG-07 | Dependencias sin monitoreo | A06 | Media | **Mitigado**: Dependabot (Maven y Actions) y CodeQL semanal |
| SEG-08 | Control de roles solo en la interfaz de escritorio | A01 | Media | **Pendiente / trabajo futuro**: validar el rol también en la capa de servicio (ver `docs/seguridad/matriz_roles.md`). Riesgo bajo: la aplicación de escritorio tiene una única vía de entrada, la interfaz, y el rol WEB no puede iniciar sesión en ella |
| SEG-09 | Sin bloqueo por intentos fallidos ni política de contraseñas en el registro web | A07 | Media | **Pendiente / trabajo futuro**: bloqueo temporal tras varios intentos y política de longitud mínima. Mitigación actual: los intentos fallidos se registran en el log (`Login fallido`) |
| SEG-10 | `LoginForm` recorta (trim) la contraseña y `UsuarioPanel` no | A07 | Baja | **Pendiente / trabajo futuro**: unificar el criterio de espacios en las contraseñas |
| SEG-11 | Datos iniciales con contraseñas de ejemplo en texto plano (`03_datos_iniciales.sql`) | A02 | Media | **Mitigado**: se migran a hash en el primer login; cambiarlas en producción |
| SEG-12 | Endpoints `simular-*` del web (pagos) | A04 | Baja | **Aceptado**: simulación académica, no usar en producción |

## 4. Detalle de los controles implementados
- `Seguridad`: `hashear`, `verificar`, `esHash`, `requiereActualizacion`, `simularVerificacion`. Formato `pbkdf2$iteraciones$salBase64$hashBase64`, idéntico al de `ClienteWebService` del web, por lo que ambos sistemas son interoperables sobre la misma BD.
- Compatibilidad: una contraseña antigua en texto plano se acepta una sola vez y se reemplaza por su hash.
- `UsuarioDAO.buscarActivoPorUsername` reemplaza a `autenticar(user, pass)`: la contraseña ya no viaja en la consulta SQL.
- `UsuarioService.autenticar` no devuelve la contraseña al resto de la aplicación.

## 5. Evidencia de verificación

- **Pruebas JUnit nuevas:** `SeguridadTest` (13 pruebas), `UsuarioServiceSeguridadTest` (12 pruebas) y `ConfiguracionTest` (5 pruebas).
- **Verificación de interoperabilidad:** 27 comprobaciones correctas y 0 fallidas. Se confirmó que Java y Python pueden generar y verificar hashes compatibles.
- **Pruebas automatizadas del proyecto de escritorio:** se ejecutó `mvn test`, obteniendo un resultado satisfactorio.

| Prueba | Resultado |
|---|---|
| `mvn test` (todas las clases) | 142 ejecutadas, 0 fallidas, 0 errores y 1 omitida. |
| Compilación Maven | BUILD SUCCESS |
| CodeQL (GitHub) | Ejecutado en GitHub Actions sin alertas pendientes. |

**Nota:** Las 27 comprobaciones de interoperabilidad y las 142 pruebas JUnit corresponden a verificaciones diferentes.

## 6. Herramientas y cómo ejecutarlas
- **CodeQL**: `.github/workflows/codeql.yml` (se ejecuta en cada push/PR a main y cada lunes).
- **Dependabot**: `.github/dependabot.yml`.
- **OWASP ZAP** (web) – *trabajo futuro, no ejecutado en este proyecto*: levantar el backend y ejecutar un análisis de línea base con la imagen oficial de ZAP (`zaproxy/zap-stable`, script `zap-baseline.py`) contra `http://localhost:8080`, y adjuntar el reporte generado. Requiere Docker o la instalación local de ZAP.
- **OWASP Dependency-Check**: `mvn org.owasp:dependency-check-maven:check`.
- **SpotBugs + FindSecBugs**: `mvn com.github.spotbugs:spotbugs-maven-plugin:check` con el plugin `findsecbugs`.

## 7. Conclusiones y recomendaciones
Se eliminaron los riesgos críticos de almacenamiento de contraseñas y credenciales en código. Quedan como mejoras: autorización por rol en la capa de servicio, bloqueo por intentos fallidos, política de contraseñas, HTTPS en el despliegue y rotación de las contraseñas de ejemplo.
