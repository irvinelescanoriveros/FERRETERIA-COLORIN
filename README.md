# SGIVFC — Sistema de Gestión de Inventario y Ventas para Ferretería Colorín

## 1. Descripción del proyecto

SGIVFC es un sistema desarrollado para mejorar la gestión de productos, inventario, compras y ventas de Ferretería Colorín. Su objetivo es centralizar la información del negocio, facilitar la consulta de productos y contribuir a reducir los tiempos de atención al cliente.

El proyecto cuenta con una aplicación de escritorio desarrollada en Java y una aplicación web complementaria. Ambas utilizan la base de datos MySQL `bd_colorin`.

## 2. Objetivos

- Facilitar la consulta de precios, existencias, marcas, lotes y ubicación de los productos.
- Mejorar el control del inventario y los movimientos de mercadería.
- Organizar las operaciones de compras, recepción y ventas.
- Gestionar usuarios y controlar el acceso según sus roles.
- Proporcionar información del negocio mediante indicadores y reportes.
- Complementar la gestión mediante las funcionalidades disponibles en la aplicación web.

## 3. Funcionalidades principales

| Módulo | Descripción |
|---|---|
| Productos | Gestión de productos, categorías, marcas y datos relacionados. |
| Inventario | Control de existencias, lotes, ubicaciones, descuentos y ajustes. |
| Proveedores | Registro y gestión de proveedores. |
| Clientes | Registro y consulta de clientes. |
| Compras | Registro de compras y productos adquiridos. |
| Recepción | Verificación y registro de la mercadería recibida. |
| Ventas | Registro de ventas, detalles, comprobantes y métodos de pago. |
| Caja | Apertura, movimientos y cierre de caja. |
| Kardex | Seguimiento de entradas, salidas y ajustes de inventario. |
| Usuarios | Administración de usuarios y permisos de acceso. |
| Dashboard | Consulta de indicadores y reportes relacionados con las operaciones. |
| Aplicación web | Funcionalidades web complementarias para el negocio. |

## 4. Tecnologías utilizadas

### Aplicación de escritorio

- **Lenguaje:** Java 24.
- **Interfaz gráfica:** Java Swing.
- **Gestión del proyecto:** Apache Maven.
- **Base de datos:** MySQL.
- **Conectividad:** MySQL Connector/J.
- **Reportes PDF:** OpenPDF.
- **Gráficos e indicadores:** JFreeChart.

### Aplicación web

- **Lenguaje:** Java.
- **Framework:** Spring Boot.
- **Base de datos:** MySQL.
- **Integración:** conexión con la base de datos compartida `bd_colorin`.

## 5. Arquitectura general

La aplicación de escritorio está organizada en capas para separar las responsabilidades del sistema:

1. **Vista:** interfaz gráfica con la que interactúa el usuario.
2. **Controlador:** coordina las acciones de la interfaz.
3. **Servicio:** contiene las reglas y la lógica del negocio.
4. **DAO:** gestiona el acceso a los datos.
5. **Base de datos:** almacena la información de la ferretería.

La aplicación web se desarrolla como un proyecto independiente y utiliza la base de datos compartida para las operaciones contempladas en su alcance.

Para conocer la estructura técnica con mayor detalle, consultar [la documentación de arquitectura](docs/arquitectura.md).

## 6. Usuarios y roles

El sistema de escritorio contempla tres roles principales:

- **Administrador:** acceso a las funciones de administración y gestión del sistema.
- **Vendedor:** acceso a las funciones relacionadas con la atención y las ventas, según los permisos configurados.
- **Almacén:** acceso a las funciones de inventario, compras y recepción, según los permisos configurados.

El acceso se realiza mediante usuario y contraseña. Las opciones disponibles dependen del rol asignado.

## 7. Requisitos para la ejecución

- JDK 24 o una versión compatible con el proyecto.
- Apache Maven.
- Servidor MySQL.
- Base de datos `bd_colorin` creada y configurada.
- Parámetros de conexión a la base de datos correctamente establecidos.
- Entorno de desarrollo compatible, como Apache NetBeans, recomendado para facilitar la ejecución.

## 8. Instalación y ejecución

### Aplicación de escritorio

1. Descargar o clonar el repositorio.
2. Iniciar el servicio de MySQL.
3. Ejecutar los scripts SQL del directorio `database/` en el siguiente orden:
   - `01_creacion_bd.sql`: crea la base de datos `bd_colorin`.
   - `02_tablas.sql`: selecciona la base de datos y crea las tablas.
   - `03_datos_iniciales.sql`: inserta los datos iniciales de prueba.
4. Configurar los parámetros de conexión a MySQL mediante el archivo `ferreteria.properties`, las variables de entorno o las propiedades de la JVM, según el entorno de ejecución.
5. Abrir el proyecto desde su archivo `pom.xml` en Apache NetBeans.
6. Permitir que Maven resuelva las dependencias necesarias.
7. Ejecutar el proyecto utilizando la clase principal `pe.ferreteria.principal.Principal`.
8. Iniciar sesión con un usuario registrado en la base de datos.

Al ejecutar la clase principal, se muestra la ventana de inicio de sesión.

### Aplicación web

La aplicación web se ejecuta de manera independiente y requiere su propia configuración. Para ponerla en funcionamiento, deben seguirse las instrucciones correspondientes a su proyecto.

## 9. Configuración de la base de datos

La aplicación de escritorio utiliza MySQL y se conecta a la base de datos `bd_colorin` mediante la clase `pe.ferreteria.infraestructura.ConexionBD`.

Parámetros de conexión:

| Parámetro | Valor |
|---|---|
| Servidor | `localhost` |
| Puerto | `3306` |
| Base de datos | `bd_colorin` |
| Usuario | Configurable según el entorno local |
| Contraseña | Configurada de forma segura para cada entorno |

La configuración de las credenciales de la aplicación de escritorio se realiza mediante el siguiente orden de prioridad:

1. Propiedades de la JVM.
2. Variables de entorno.
3. Archivo `ferreteria.properties`.

Para mejorar la seguridad, se utiliza un usuario de MySQL independiente de `root`, con permisos limitados a las operaciones necesarias sobre `bd_colorin`.

La aplicación web también utiliza un usuario de aplicación y mantiene su configuración local de conexión en `application-local.properties`, archivo excluido del repositorio mediante `.gitignore`.

No se deben publicar contraseñas reales ni otros datos sensibles en el repositorio.

## 10. Documentación

La carpeta `docs/` contiene la documentación del proyecto:

- [Índice de documentación](docs/README.md)
- [Arquitectura del sistema](docs/arquitectura.md)
- [Manual de usuario](docs/manual_usuario.md)
- [Documentación de módulos](docs/modulos/)

## 11. Estado del proyecto

**Avance general estimado: 85 %.**

El alcance funcional inicial definido para el proyecto ha sido cubierto según lo desarrollado. También se han incorporado funcionalidades adicionales durante el proceso de implementación.

La aplicación de escritorio cuenta con los módulos de gestión previstos y la aplicación web complementaria se encuentra operativa. El trabajo pendiente se concentra en las correcciones finales, las verificaciones necesarias y la finalización de la documentación.

El porcentaje representa una estimación general del avance del proyecto, no una medición individual de cada módulo.

## 12. Pruebas de software

Se implementaron pruebas unitarias en la aplicación de escritorio para verificar el funcionamiento de los principales servicios del sistema y los componentes relacionados con la seguridad.

### Herramientas utilizadas

- **JUnit 5:** creación y ejecución de pruebas unitarias.
- **Mockito:** simulación de dependencias DAO para probar los servicios de manera aislada.
- **JaCoCo:** medición de la cobertura del código durante la ejecución de las pruebas.
- **Maven:** gestión y ejecución de las pruebas.

### Cobertura de los servicios principales

| Servicio | Cobertura de instrucciones | Cobertura de ramas |
|---|---:|---:|
| VentaService | 84 % | 96 % |
| CajaService | 85 % | 78 % |
| InventarioService | 91 % | 92 % |
| CompraService | 86 % | 75 % |
| ProductoService | 86 % | 100 % |

### Cobertura de componentes de seguridad

| Componente | Cobertura de instrucciones | Cobertura de ramas |
|---|---:|---:|
| Seguridad | 94 % | 96 % |
| Configuracion | 89 % | 85 % |
| SesionUsuario | 44 % | 0 % |

### Cobertura total del proyecto

- **Instrucciones cubiertas:** 81 %.
- **Ramas cubiertas:** 86 %.

### Resultados de ejecución

- **Pruebas ejecutadas:** 136.
- **Pruebas fallidas:** 0.
- **Errores:** 0.
- **Pruebas omitidas:** 1.
- **Resultado:** BUILD SUCCESS.

### Ejecución de las pruebas

Desde la raíz del proyecto, ejecutar:

```bash
mvn test
```

El informe de cobertura generado por JaCoCo se encuentra en:

```text
target/site/jacoco/index.html
```

Estas pruebas permiten verificar parte de la lógica de negocio y los controles de seguridad implementados, así como identificar componentes que requieren mayor cobertura de pruebas.
## 13. Información académica

- **Proyecto:** Sistema de Gestión de Inventario y Ventas para Ferretería Colorín.
- **Siglas:** SGIVFC.
- **Estudiante responsable:** Irvin Elescano Riveros.
- **Código universitario:** U22237774.
- **Institución:** Universidad Tecnológica del Perú (UTP).
- **Curso:** Integrador I.
- **Organización beneficiaria:** Ferretería Colorín.
- **Periodo planificado:** agosto a diciembre de 2026.

## 14. Seguridad

Durante la Semana 14 se implementaron medidas de seguridad para proteger la información y controlar el acceso a la aplicación de escritorio y al backend web.

Entre las principales medidas se encuentran:

- **Protección de contraseñas:** almacenamiento mediante PBKDF2-HMAC-SHA256, con 210 000 iteraciones y sal aleatoria.
- **Migración de contraseñas:** actualización automática de contraseñas antiguas en texto plano a formato hash después de una autenticación correcta.
- **Acceso a MySQL:** utilización del usuario `colorin_app` con privilegios limitados sobre la base de datos `bd_colorin`.
- **Configuración de credenciales:** separación de las credenciales de los archivos de código y exclusión de los archivos locales del repositorio.
- **Configuración CORS:** restricción de los orígenes permitidos en el backend web.
- **Protección de sesiones:** configuración de cookies HttpOnly y SameSite=Lax.
- **Manejo de errores:** ocultamiento de mensajes internos y trazas de errores en las respuestas web.
- **Análisis de seguridad:** configuración de CodeQL y Dependabot mediante GitHub Actions.

Para consultar los hallazgos, controles implementados y evidencias de verificación, revisar el documento `docs/seguridad/informe_seguridad.md`.