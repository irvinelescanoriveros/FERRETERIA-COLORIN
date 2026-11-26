# Plan de Despliegue – SGIVFC (Semana 15)


## 1. Arquitectura desplegada

| Componente | Tecnología | Puerto | Artefacto |
|-----------|-----------|--------|-----------|
| Base de datos `bd_colorin` | MySQL 8.4 | 3306 | Scripts SQL de inicialización |
| Backend web + tienda | Spring Boot (Java 21) | 8080 | `backend-web-0.0.1-SNAPSHOT.jar` (ejecutable) |
| Aplicación de escritorio | Java Swing (Java 24 o superior) | – | `ferreteria-colorin.jar` |

Ambos sistemas comparten la base de datos `bd_colorin` y utilizan el usuario `colorin_app`, que dispone de permisos limitados de lectura, inserción, actualización y eliminación. No necesitan conectarse con el usuario `root`.

**Inicialización de la base de datos:**

Se ejecutan, en este orden, los scripts `01_creacion_bd.sql`, `02_tablas.sql`, `03_datos_iniciales.sql` y `04_usuario_aplicacion.sql`. El último crea el usuario `colorin_app` con permisos limitados; antes de ejecutarlo se debe reemplazar la contraseña de ejemplo por una contraseña segura.

**Configuración de conexión:**

La aplicación de escritorio obtiene las credenciales mediante la clase `Configuracion`, siguiendo este orden de prioridad:

1. Propiedades de la JVM.
2. Variables de entorno.
3. Archivo externo `ferreteria.properties`.
4. Valores predeterminados.

El backend web recibe los datos de conexión mediante variables de entorno.


## 2. Construcción con Maven

El proyecto utiliza Maven para gestionar las dependencias, compilar el código fuente, ejecutar las pruebas y generar los archivos JAR ejecutables.

**Aplicación de escritorio:**

Se utiliza Maven desde Apache NetBeans para construir el proyecto. El archivo generado es:

```text
target/ferreteria-colorin.jar
```

El empaquetado utiliza Maven Shade Plugin 3.6.0, que permite incluir las dependencias necesarias para ejecutar la aplicación. La clase principal configurada es `pe.ferreteria.principal.Principal`.

**Resultados de las pruebas:**

La aplicación de escritorio cuenta con pruebas automatizadas utilizando JUnit 5 y Mockito.

- Pruebas ejecutadas: 142.
- Pruebas fallidas: 0.
- Errores: 0.
- Pruebas omitidas: 1.
- Resultado: BUILD SUCCESS.

También se utiliza JaCoCo para generar el informe de cobertura del código.

**Backend web:**

El backend web se construye mediante Maven Wrapper, utilizando el siguiente comando desde la carpeta `backend-web`:

```powershell
.\mvnw.cmd -B -DskipTests package
```

El archivo generado es:

```text
target/backend-web-0.0.1-SNAPSHOT.jar
```

El JAR incluye el servidor Tomcat embebido, por lo que no requiere instalar un servidor de aplicaciones aparte.

**Integración continua con GitHub Actions:**

Se configuró el workflow `.github/workflows/ci.yml` para utilizar Java 24 y ejecutar las siguientes tareas:

1. Descargar el código del repositorio.
2. Configurar el entorno Java y Maven.
3. Ejecutar las pruebas automatizadas.
4. Generar los informes de pruebas y cobertura.
5. Empaquetar el JAR ejecutable del escritorio.
6. Publicar los archivos generados como artefactos de GitHub Actions.

**Comprobaciones de GitHub:**

Las ejecuciones del repositorio finalizaron correctamente, con sus comprobaciones en estado satisfactorio (verde):

- **CI - Pruebas y cobertura:** ejecuta la compilación, las pruebas automatizadas y genera los informes de cobertura.
- **CodeQL:** realiza análisis estático del código para identificar posibles vulnerabilidades de seguridad. Se ejecutó sin alertas pendientes.
- **Dependabot:** supervisa las dependencias del proyecto para identificar versiones desactualizadas o vulnerables.

Estas herramientas complementan el proceso de desarrollo mediante pruebas automatizadas, análisis de seguridad y mantenimiento de dependencias.


## 3. Variables de configuración

| Variable | Uso | Ejemplo |
|----------|-----|---------|
| `DB_URL` | Cadena de conexión JDBC | `jdbc:mysql://localhost:3306/bd_colorin` |
| `DB_USERNAME` | Usuario de conexión a MySQL | `colorin_app` |
| `DB_PASSWORD` | Contraseña del usuario de aplicación | Definida por el administrador |
| `CORS_ORIGINS` | Orígenes permitidos para las solicitudes del backend web | `http://localhost:8080` |

**Configuración de la aplicación de escritorio:**

La aplicación de escritorio también permite configurar la conexión mediante un archivo externo llamado `ferreteria.properties`, ubicado en la carpeta raíz del proyecto.

Ejemplo de su estructura:

```properties
db.url=jdbc:mysql://localhost:3306/bd_colorin
db.username=colorin_app
db.password=contraseña_configurada
```

El archivo contiene credenciales de acceso, por lo que no debe publicarse en GitHub. Está excluido mediante `.gitignore`.

**Contraseña en el despliegue:**

El script de despliegue (sección 4) solicita la contraseña de `colorin_app` al ejecutarse y la mantiene solo en memoria, mediante la variable de entorno `DB_PASSWORD`. No la guarda en ningún archivo.

## 4. Despliegue con script PowerShell (método utilizado)

El despliegue se realiza en Windows mediante el script `levantar-sistema.ps1`, que automatiza la verificación del entorno y el inicio de ambos sistemas. El script se ejecuta desde la carpeta que contiene a los dos proyectos; en el repositorio de escritorio se conserva una copia de referencia en `deploy/levantar-sistema.ps1`, que debe copiarse junto a las carpetas `FERRETERIA-SISTEMA` y `backend-web` para usarse (calcula sus rutas a partir de su propia ubicación).

**Requisitos previos:**

- Java 24 o superior instalado y disponible en el PATH.
- MySQL en ejecución en el puerto 3306, con la base de datos `bd_colorin` importada y el usuario `colorin_app` creado.
- Los proyectos y el script en la misma carpeta:

```text
C:\ProyectosJava\
├── levantar-sistema.ps1
├── FERRETERIA-SISTEMA\
│   ├── database\
│   ├── deploy\
│   │   └── iniciar-escritorio.bat
│   ├── target\ferreteria-colorin.jar
│   └── docs\
└── backend-web\
    └── target\backend-web-0.0.1-SNAPSHOT.jar
```

- Los dos JAR construidos. El JAR de escritorio se genera desde Apache NetBeans (Clean and Build) y el del backend web con `mvnw package` o con el parámetro `-Compilar` del script (este último requiere que Maven esté disponible en la máquina).

**Procedimiento:**

1. Abrir PowerShell en la carpeta que contiene el script:

   ```powershell
   cd C:\ProyectosJava
   ```

2. Permitir la ejecución de scripts solo para esta ventana de PowerShell (no cambia la configuración del equipo):

   ```powershell
   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
   ```

3. Ejecutar el script:

   ```powershell
   .\levantar-sistema.ps1
   ```

   Con el parámetro `-Compilar` el script construye primero los proyectos con Maven.

4. Ingresar la contraseña del usuario `colorin_app` cuando el script la solicite.

**Qué realiza el script:**

1. Comprueba que existan las carpetas de ambos proyectos y el archivo `deploy\iniciar-escritorio.bat`.
2. Verifica que Java sea de versión 24 o superior.
3. Verifica que MySQL responda en el puerto 3306 y que el puerto 8080 esté libre, para no iniciar un segundo backend.
4. Comprueba que existan los JAR de ambos proyectos (y los construye antes si se indicó `-Compilar`).
5. Solicita la contraseña de forma oculta y la entrega a las aplicaciones mediante variables de entorno (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`). No se guarda en ningún archivo y las variables se restauran al terminar.
6. Inicia el backend web en segundo plano y espera a que el puerto 8080 responda.
7. Realiza una consulta real al backend (`/api/productos`) para comprobar que la contraseña es correcta. Si MySQL rechaza la contraseña, detiene el backend, muestra el motivo y no abre la aplicación de escritorio.
8. Inicia la aplicación de escritorio mediante `iniciar-escritorio.bat`, abre el navegador en `http://localhost:8080` y muestra un cuadro con el estado de MySQL, backend web y escritorio.

Los registros del backend se guardan en la carpeta `.runtime` (`backend.log` y `backend-error.log`), junto con el identificador del proceso (`backend.pid`).

**Consultar el estado:**

```powershell
.\levantar-sistema.ps1 -Estado
```

**Detener el backend web:**

```powershell
.\levantar-sistema.ps1 -Detener
```

El script comprueba que el proceso registrado sea realmente el backend antes de detenerlo, para no cerrar un proceso ajeno, y finaliza también los procesos hijos.

**Aplicación de escritorio de forma independiente:**

También puede iniciarse con `deploy/iniciar-escritorio.bat` en Windows o `deploy/iniciar-escritorio.sh` en Linux. Ambos scripts utilizan la configuración externa de la aplicación para obtener las credenciales de conexión.

## 5. Alternativas de despliegue (trabajo futuro)

Las siguientes alternativas están preparadas en el repositorio, pero **no se utilizaron ni se verificaron** en este proyecto. Se dejan como mejora futura.

**Opción A – Docker Compose:**

El repositorio incluye `deploy/docker-compose.yml` y `deploy/.env.example` para ejecutar MySQL y el backend web en contenedores. Requeriría instalar Docker Desktop, copiar `.env.example` como `.env` y configurar las contraseñas. Se descartó para este proyecto porque agrega una herramienta adicional de instalación y el script de PowerShell cubre el mismo objetivo en el entorno disponible.

**Opción B – Servidor Linux sin Docker:**

El repositorio incluye `deploy/colorin-web.service` para ejecutar el backend web como servicio `systemd` en un servidor Linux con Java 21 y MySQL instalados. Para un despliegue público se recomienda además utilizar un servidor web inverso como Nginx y configurar HTTPS mediante un certificado SSL.

**Empaquetado como aplicación instalable:**

Como mejora futura, la herramienta `jpackage` del JDK permitiría generar un paquete de la aplicación de escritorio que se abra con doble clic, sin necesidad de ejecutar el JAR manualmente.


## 6. Lista de verificación posterior al despliegue

Después de realizar el despliegue, se deben comprobar los siguientes aspectos para verificar el correcto funcionamiento del sistema.

**Aplicación de escritorio:**

- [x] El archivo JAR ejecutable se genera correctamente.
- [x] La aplicación inicia mediante el script de despliegue o `iniciar-escritorio.bat` en Windows.
- [x] La aplicación establece conexión con MySQL y permite acceder a la información de la base de datos.
- [x] Las pruebas automatizadas finalizan correctamente: 142 ejecutadas, 0 fallidas, 0 errores y 1 omitida.
- [ ] Verificar el inicio de sesión con las credenciales configuradas.
- [ ] Registrar una venta de prueba y comprobar que se almacena correctamente en la base de datos.

**Backend web y tienda:**

- [x] El backend web se compila y genera su archivo JAR correctamente.
- [x] Iniciar el backend y comprobar que responde correctamente.
- [x] Verificar que `GET /api/productos` devuelve los productos registrados.
- [x] Realizar una compra de prueba y comprobar que el pedido se registra correctamente en la base de datos.

**Despliegue y seguridad:**

- [X] Comprobar que el script `levantar-sistema.ps1` inicia ambos sistemas correctamente.
- [ ] Verificar que MySQL y el backend web se comuniquen correctamente.
- [ ] Confirmar que la aplicación utiliza el usuario `colorin_app` y no el usuario administrador `root`.
- [ ] Verificar que las credenciales estén almacenadas fuera del repositorio y que los archivos de configuración sensibles no se publiquen en GitHub.


## 7. Reversión (rollback)

El procedimiento de reversión permite recuperar una versión estable del sistema cuando una actualización presenta errores o afecta el funcionamiento de la aplicación.

**Aplicación de escritorio:**

- Conservar una copia del JAR ejecutable de la versión anterior antes de realizar una actualización.
- Si la nueva versión presenta problemas, restaurar el JAR anterior.
- Reiniciar la aplicación y comprobar que funcione correctamente.

**Backend web:**

- Conservar una copia del JAR de la versión estable anterior.
- Si la nueva versión presenta errores, detener el servicio con `.\levantar-sistema.ps1 -Detener` y volver a iniciar con el JAR anterior.
- Verificar que el backend web responda correctamente en `http://localhost:8080`.

**Base de datos:**

Antes de realizar una actualización, generar una copia de seguridad de la base de datos con el script de mantenimiento:

    cd C:\ProyectosJava\FERRETERIA-SISTEMA\deploy\mantenimiento
    .\respaldo.ps1

El script genera un archivo comprimido con fecha en `C:\respaldos-colorin\` (por ejemplo, `bd_colorin_20261125_200924.zip`) junto con su suma de verificación SHA-256, y registra el resultado en `respaldo.log` (`RESPALDO_OK`).

Si ocurre un problema que requiere recuperar la información, se restaura la copia con:

    .\restaurar.ps1 -Archivo C:\respaldos-colorin\<archivo>.zip -Config <archivo_de_credenciales_administrador>.cnf

El script verifica la integridad del respaldo y solicita escribir `SI` antes de continuar, ya que la restauración reemplaza los datos actuales por los de la copia. El procedimiento completo está en la sección 5.3 de `docs/mantenimiento/plan_mantenimiento.md`. Para comprobar que un respaldo es válido sin tocar la base real se utiliza `probar-respaldo.ps1`, que lo restaura en `bd_colorin_prueba`.

**Consideraciones:**

- Realizar copias de seguridad antes de cada despliegue.
- Conservar los archivos JAR de versiones estables.
- Verificar el funcionamiento del sistema después de cada reversión.
- No eliminar los datos de MySQL durante la recuperación.


## 8. Evidencias a adjuntar

Se recopilarán capturas de pantalla para demostrar la construcción, configuración y funcionamiento de los componentes del sistema.

1. **Aplicación de escritorio:** captura de la aplicación iniciada correctamente.
2. **Pruebas automatizadas:** captura del resultado de las pruebas con JUnit 5 y Mockito, mostrando las 142 pruebas ejecutadas, sin fallos ni errores y una omitida.
3. **Construcción del backend web:** captura de la compilación exitosa mediante Maven y la generación del archivo JAR.
4. **GitHub Actions:** captura de las ejecuciones satisfactorias de CI - Pruebas y cobertura.
5. **CodeQL:** captura del análisis de seguridad completado correctamente, sin alertas.
6. **Dependabot:** captura de las comprobaciones de dependencias realizadas correctamente.
7. **Script de despliegue:** captura de `levantar-sistema.ps1` ejecutándose y mostrando "Backend web activo en http://localhost:8080".
8. **Tienda web:** captura del backend web funcionando y de la consulta de productos.
9. **Base de datos:** captura de una consulta que permita comprobar los registros almacenados después de una operación de prueba.