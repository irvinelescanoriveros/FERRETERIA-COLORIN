 Sistema de Gestión de Inventario y Ventas – Ferretería Colorín

 Descripción

Sistema de escritorio desarrollado en Java para apoyar la gestión de compras, recepción de mercadería, inventario, ventas y control de movimientos de la Ferretería Colorín.

 Tecnologías

* Java 24
* Java Swing
* MySQL
* Maven
* NetBeans

 Arquitectura

El proyecto utiliza una organización por capas para separar las responsabilidades:

* Modelo: entidades del sistema.
* DAO: acceso y operaciones con la base de datos.
* Servicio: lógica de negocio.
* Controlador: coordinación entre la vista y los servicios.
* Vista: interfaces gráficas desarrolladas con Java Swing.
* Infraestructura: conexión, configuración, seguridad y sesión.
* Util: validadores y generación de archivos.
* Principal: punto de entrada de la aplicación.

 Estructura principal


pe/ferreteria/
├── modelo/
├── dao/
├── servicio/
├── controlador/
├── vista/
├── infraestructura/
├── util/
└── principal/


También se incluyen:


database/
├── 01_creacion_bd.sql
├── 02_tablas.sql
└── 03_datos_iniciales.sql

docs/
├── README.md
├── arquitectura.md
└── manual_usuario.md

test/
└── pruebas de servicios principales


Base de datos

El sistema utiliza MySQL como gestor de base de datos.

Base de datos: `bd_colorin`

La base de datos contempla la gestión de usuarios, clientes, proveedores, productos, compras, recepción, inventario, ventas, Kardex, Caja y movimientos relacionados.

Principales módulos

* Dashboard
* Productos
* Clientes
* Proveedores
* Compras
* Recepción
* Ventas
* Inventario
* Kardex
* Usuarios
* Caja

Archivos y recursos

El proyecto incluye scripts SQL para la creación de la base de datos, tablas y datos iniciales, además de documentación técnica y manual de usuario en formato Markdown.

Pruebas

El proyecto cuenta con pruebas para servicios principales, entre ellos:

* `ProductoServiceTest`
* `VentaServiceTest`
* `CompraServiceTest`
* `InventarioServiceTest`

 Ejecución

1. Configurar MySQL.
2. Crear la base de datos `bd_colorin`.
3. Ejecutar los scripts SQL de la carpeta `database`.
4. Configurar los datos de conexión.
5. Abrir el proyecto Maven en NetBeans.
6. Compilar y ejecutar la clase principal `Principal.java`.
