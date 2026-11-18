# Plan de Despliegue – SGIVFC (Semana 15)


## 1. Arquitectura desplegada

| Componente | Tecnología | Puerto | Artefacto |
|-----------|-----------|--------|-----------|
| Base de datos `bd_colorin` | MySQL 8.4 | 3306 | Scripts SQL de inicialización |
| Backend web + tienda | Spring Boot (Java 24) | 8080 | JAR ejecutable / imagen Docker |
| Aplicación de escritorio | Java Swing (Java 24+) | – | `ferreteria-colorin.jar` |

Ambos sistemas comparten la base de datos `bd_colorin` y utilizan el usuario `colorin_app`, que dispone de permisos limitados de lectura, inserción, actualización y eliminación. No necesitan conectarse con el usuario `root`.

**Inicialización de la base de datos:**

- **Despliegue con Docker:** se ejecutan los scripts `01_creacion_bd.sql`, `02_tablas.sql`, `03_datos_iniciales.sql` y `crear-usuario-app.sh`.
- **Instalación manual:** se ejecutan los scripts `01_creacion_bd.sql`, `02_tablas.sql`, `03_datos_iniciales.sql` y `04_usuario_aplicacion.sql`.

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

- Pruebas ejecutadas: 136.
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

**Integración continua con GitHub Actions:**

Se configuró el workflow `.github/workflows/ci.yml` para utilizar Java 24 y ejecutar las siguientes tareas:

1. Descargar el código del repositorio.
2. Configurar el entorno Java y Maven.
3. Ejecutar las pruebas automatizadas.
4. Generar los informes de pruebas y cobertura.
5. Empaquetar el JAR ejecutable del escritorio.
6. Publicar los archivos generados como artefactos de GitHub Actions.

**Comprobaciones de GitHub:**

Durante las ejecuciones anteriores del repositorio, los siguientes mecanismos finalizaron correctamente, mostrando sus comprobaciones en estado satisfactorio (verde):

- **CI - Pruebas y cobertura:** ejecuta la compilación, las pruebas automatizadas y genera los informes de cobertura.
- **CodeQL:** realiza análisis estático del código para identificar posibles vulnerabilidades de seguridad.
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

**Configuración de Docker:**

El archivo `.env` ubicado en `deploy` contiene las contraseñas necesarias para inicializar MySQL y conectar el backend web. Este archivo también está excluido de Git.


## 4. Opción A – Despliegue con Docker

Se utiliza Docker Compose para ejecutar los servicios de MySQL y el backend web.

**Estructura de carpetas requerida:**

```text
C:\ProyectosJava\
├── FERRETERIA-SISTEMA\
│   ├── database\
│   ├── deploy\
│   │   ├── .env
│   │   ├── .env.example
│   │   └── docker-compose.yml
│   └── docs\
└── backend-web\
```

**Procedimiento de despliegue:**

1. Instalar y ejecutar Docker Desktop.
2. Ubicar los proyectos en las carpetas indicadas, manteniendo `FERRETERIA-SISTEMA` y `backend-web` como directorios hermanos.
3. Ingresar a la carpeta `deploy`:

   ```powershell
   cd C:\ProyectosJava\FERRETERIA-SISTEMA\deploy
   ```

4. Si todavía no existe el archivo `.env`, copiar la plantilla:

   ```powershell
   Copy-Item .env.example .env
   ```

5. Configurar las contraseñas en `.env`, sin compartirlas ni publicarlas en GitHub.
6. Comprobar que el puerto 3306 esté disponible antes de iniciar los contenedores:

   ```powershell
   Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue
   ```

7. Construir e iniciar los servicios:

   ```powershell
   docker compose up -d --build
   ```

8. Comprobar el estado de los contenedores:

   ```powershell
   docker compose ps
   ```

9. Abrir el navegador y acceder a:

   `http://localhost:8080`

**Servicios incluidos:**

- MySQL 8.4 para almacenar la información de la aplicación.
- Backend web Spring Boot para atender las solicitudes de la tienda.
- Volumen persistente `datos_mysql` para conservar la información de la base de datos.

**Aplicación de escritorio:**

La aplicación de escritorio se inicia mediante `deploy/iniciar-escritorio.bat` en Windows o `deploy/iniciar-escritorio.sh` en Linux. Ambos scripts utilizan la configuración externa de la aplicación para obtener las credenciales de conexión.

Para conectar el escritorio con el MySQL de Docker, debe estar disponible el puerto 3306 y las credenciales configuradas en `ferreteria.properties` deben coincidir con las del usuario de aplicación creado por Docker.


## 5. Opción B – Servidor Linux sin Docker

Como alternativa a Docker, el sistema puede desplegarse en un servidor Linux con MySQL y Java instalados directamente.

**Procedimiento general:**

1. Instalar Java 21 y MySQL Server:

   ```bash
   sudo apt update
   sudo apt install openjdk-21-jre mysql-server
   ```

2. Ejecutar como administrador de MySQL los siguientes scripts:

   - `01_creacion_bd.sql`
   - `02_tablas.sql`
   - `03_datos_iniciales.sql`
   - `04_usuario_aplicacion.sql`

   El último script crea el usuario `colorin_app` con permisos limitados. Antes de ejecutarlo, se debe reemplazar la contraseña de ejemplo por una contraseña segura.

3. Copiar el JAR del backend web a:

   ```text
   /opt/colorin/backend-web.jar
   ```

4. Crear el archivo de variables de entorno:

   ```text
   /etc/colorin/web.env
   ```

   Debe contener las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `CORS_ORIGINS`. El archivo debe tener permisos restringidos.

5. Instalar el archivo `deploy/colorin-web.service` en:

   ```text
   /etc/systemd/system/colorin-web.service
   ```

6. Habilitar e iniciar el servicio:

   ```bash
   sudo systemctl enable --now colorin-web
   ```

7. Consultar el estado del servicio:

   ```bash
   systemctl status colorin-web
   ```

8. Consultar los registros de ejecución:

   ```bash
   journalctl -u colorin-web -f
   ```

**Seguridad adicional:**

Para un despliegue público se recomienda utilizar un servidor web inverso como Nginx y configurar HTTPS mediante un certificado SSL.


## 6. Lista de verificación posterior al despliegue

Después de realizar el despliegue, se deben comprobar los siguientes aspectos para verificar el correcto funcionamiento del sistema.

**Aplicación de escritorio:**

- [x] El archivo JAR ejecutable se genera correctamente.
- [x] La aplicación inicia mediante el script `iniciar-escritorio.bat` en Windows.
- [x] La aplicación establece conexión con MySQL y permite acceder a la información de la base de datos.
- [x] Las pruebas automatizadas finalizan correctamente: 136 ejecutadas, 0 fallidas, 0 errores y 1 omitida.
- [ ] Verificar el inicio de sesión con las credenciales configuradas.
- [ ] Registrar una venta de prueba y comprobar que se almacena correctamente en la base de datos.

**Backend web y tienda:**

- [x] El backend web se compila y genera su archivo JAR correctamente.
- [x] Iniciar el backend y comprobar que responde correctamente.
- [x] Verificar que `GET /api/productos` devuelve los productos registrados.
- [x] Realizar una compra de prueba y comprobar que el pedido se registra correctamente en la base de datos.

**Despliegue y seguridad:**

- [ ] Comprobar que los contenedores de Docker se encuentren funcionando correctamente.
- [ ] Verificar que MySQL y el backend web se comuniquen correctamente.
- [ ] Confirmar que la aplicación utiliza el usuario `colorin_app` y no el usuario administrador `root`.
- [ ] Verificar que las credenciales estén almacenadas fuera del repositorio y que los archivos de configuración sensibles no se publiquen en GitHub.



## 7. Reversión (rollback)

El procedimiento de reversión permite recuperar una versión estable del sistema cuando una actualización presenta errores o afecta el funcionamiento de la aplicación.

**Aplicación de escritorio:**

- Conservar una copia del JAR ejecutable de la versión anterior antes de realizar una actualización.
- Si la nueva versión presenta problemas, restaurar el JAR anterior.
- Reiniciar la aplicación y comprobar que funcione correctamente.

**Backend web con Docker:**

- Conservar la imagen Docker correspondiente a la versión estable anterior.
- Si la nueva versión presenta errores, detener el servicio y volver a utilizar la imagen anterior.
- Reiniciar los servicios y verificar que el backend web responda correctamente.

**Base de datos:**

Antes de realizar una actualización, generar una copia de seguridad de la base de datos mediante el siguiente comando:

```bash
mysqldump -u root -p bd_colorin > respaldo.sql
```

Si ocurre un problema que requiere recuperar la información, se puede restaurar la copia de seguridad mediante:

```bash
mysql -u root -p bd_colorin < respaldo.sql
```

La restauración debe realizarse con precaución, ya que puede reemplazar los datos actuales por los contenidos en la copia de seguridad.

**Consideraciones:**

- Realizar copias de seguridad antes de cada despliegue.
- Conservar los archivos JAR y las imágenes Docker de versiones estables.
- Verificar el funcionamiento del sistema después de cada reversión.
- No eliminar los datos persistentes de MySQL durante la recuperación.


## 8. Evidencias a adjuntar

Se recopilarán capturas de pantalla para demostrar la construcción, configuración y funcionamiento de los componentes del sistema.

1. **Aplicación de escritorio:** captura de la aplicación iniciada correctamente mediante `iniciar-escritorio.bat`.
2. **Pruebas automatizadas:** captura del resultado de las pruebas con JUnit 5 y Mockito, mostrando las 136 pruebas ejecutadas, sin fallos ni errores y una omitida.
3. **Construcción del backend web:** captura de la compilación exitosa mediante Maven y la generación del archivo JAR.
4. **GitHub Actions:** captura de las ejecuciones satisfactorias de CI - Pruebas y cobertura.
5. **CodeQL:** captura del análisis de seguridad completado correctamente.
6. **Dependabot:** captura de las comprobaciones de dependencias realizadas correctamente.
7. **Docker:** captura de `docker compose ps` mostrando el estado de los servicios.
8. **Tienda web:** captura del backend web funcionando y de la consulta de productos.
9. **Base de datos:** captura de una consulta que permita comprobar los registros almacenados después de una operación de prueba.
