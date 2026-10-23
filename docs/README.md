Documentación técnica — Ferretería Colorín

1. Descripción del sistema

El Sistema de Gestión de Inventario y Ventas para Ferretería Colorín es una aplicación de escritorio desarrollada para apoyar el registro y control de productos, compras, recepción de mercadería, inventario, ventas y movimientos de caja.

El sistema utiliza una base de datos MySQL para almacenar y consultar la información de las operaciones del negocio.

2. Tecnologías utilizadas

- Lenguaje: Java 24.
- Interfaz gráfica: Java Swing.
- Gestión de dependencias y compilación: Apache Maven.
- Base de datos: MySQL 8.
- Entorno de desarrollo: Apache NetBeans IDE 26.

3. Organización del proyecto

El código fuente se encuentra organizado dentro del paquete `pe.ferreteria`, dividido en los siguientes paquetes:

- `modelo`: clases que representan las entidades del sistema.
- `dao`: clases responsables del acceso a los datos.
- `servicio`: clases que coordinan las operaciones de la aplicación.
- `controlador`: clases que conectan las acciones de la interfaz con los servicios.
- `vista`: formularios, ventanas y paneles gráficos.
- `infraestructura`: componentes de conexión, configuración, sesión y seguridad.
- `util`: validadores y utilidades para generar comprobantes y reportes.
- `principal`: punto de entrada de la aplicación.

4. Base de datos

La aplicación utiliza la base de datos `bd_colorin`. El proyecto incluye scripts SQL para crear la base de datos, definir las tablas e insertar los datos iniciales.

5. Módulos principales

La aplicación cuenta con módulos para gestionar usuarios, clientes, proveedores, productos, compras, recepción, ventas, inventario, Kardex, caja y dashboard.

6. Documentación complementaria

- `arquitectura.md`: describe la organización técnica y el flujo entre las capas.
- `manual_usuario.md`: explica los pasos principales para utilizar la aplicación.

7. Consideraciones

La documentación se actualizará conforme se verifiquen las funcionalidades y se incorporen mejoras al sistema.
