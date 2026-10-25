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
4. Configurar los parámetros de conexión a MySQL en `pe.ferreteria.infraestructura.ConexionBD`.
5. Abrir el proyecto desde su archivo `pom.xml` en Apache NetBeans.
6. Permitir que Maven resuelva las dependencias necesarias.
7. Ejecutar el proyecto utilizando la clase principal `pe.ferreteria.principal.Principal`.
8. Iniciar sesión con un usuario registrado en la base de datos.

Al ejecutar la clase principal, se muestra la ventana de inicio de sesión.

### Aplicación web

La aplicación web se ejecuta de manera independiente y requiere su propia configuración. Para ponerla en funcionamiento, deben seguirse las instrucciones correspondientes a su proyecto.

## 9. Configuración de la base de datos

La aplicación de escritorio utiliza MySQL y se conecta a la base de datos `bd_colorin` mediante la clase `pe.ferreteria.infraestructura.ConexionBD`.

Parámetros de conexión predeterminados:

| Parámetro | Valor |
|---|---|
| Servidor | `localhost` |
| Puerto | `3306` |
| Base de datos | `bd_colorin` |
| Usuario | Configurable según el entorno local |
| Contraseña | Configurar de forma segura |

Antes de ejecutar la aplicación, es necesario iniciar MySQL, preparar la base de datos y verificar los parámetros de conexión.

**Seguridad:** no se deben publicar contraseñas reales ni otros datos sensibles en el repositorio. Las credenciales deben configurarse de forma segura para cada entorno.

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

## 12. Información académica

- **Proyecto:** Sistema de Gestión de Inventario y Ventas para Ferretería Colorín.
- **Siglas:** SGIVFC.
- **Estudiante responsable:** Irvin Elescano Riveros.
- **Código universitario:** U22237774.
- **Institución:** Universidad Tecnológica del Perú (UTP).
- **Curso:** Integrador I.
- **Organización beneficiaria:** Ferretería Colorín.
- **Periodo planificado:** agosto a diciembre de 2026.
