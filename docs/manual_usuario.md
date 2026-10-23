 Manual de usuario — Ferretería Colorín

 1. Introducción

El Sistema de Gestión de Inventario y Ventas para Ferretería Colorín permite gestionar información de usuarios, clientes, proveedores, productos, compras, recepción de mercadería, inventario, ventas, movimientos de caja y reportes.

 2. Requisitos para ejecutar el sistema

- Tener instalado Java JDK 24.
- Tener instalado y funcionando MySQL 8.
- Contar con el proyecto Maven abierto en Apache NetBeans IDE 26.
- Tener configurada la conexión con la base de datos `bd_colorin`.

 3. Inicio de la aplicación

1. Iniciar el servicio de MySQL.
2. Abrir el proyecto `FERRETERIA-SISTEMA` en NetBeans.
3. Abrir la clase `LoginForm.java`.
4. Ejecutar el archivo desde el entorno de desarrollo.
5. Introducir el nombre de usuario y la contraseña.
6. Si las credenciales son correctas, se abrirá la ventana principal del sistema.

 4. Ventana principal

La ventana principal muestra el nombre y el rol del usuario autenticado. Desde ella se accede a los módulos disponibles según las restricciones de navegación configuradas para cada rol.

 5. Descripción de los módulos

 5.1. Usuarios

Permite registrar, consultar, editar y desactivar usuarios del sistema, así como gestionar sus datos y contraseñas.

 5.2. Clientes

Permite registrar y consultar clientes, modificar sus datos y realizar búsquedas.

 5.3. Proveedores

Permite gestionar la información de los proveedores, incluidos sus datos comerciales y de contacto.

 5.4. Productos

Permite registrar y consultar productos, con información como código, nombre, categoría, marca y ubicación dentro del almacén.

 5.5. Compras

Permite registrar compras a proveedores y agregar los productos adquiridos con sus cantidades y precios.

 5.6. Recepción

Permite revisar la mercadería recibida y gestionar su estado antes de confirmar el ingreso al inventario.

 5.7. Inventario

Permite consultar existencias, revisar productos por lote y visualizar información relacionada con su ubicación y estado.

 5.8. Ventas

Permite seleccionar clientes y productos, registrar cantidades, calcular importes y generar el comprobante correspondiente. El registro de una venta está sujeto a las condiciones de caja configuradas en el sistema.

 5.9. Kardex

Permite consultar los movimientos de inventario y revisar información relacionada con las entradas y salidas de productos.

 5.10. Caja

Permite gestionar la apertura y el cierre de caja, así como registrar movimientos y consultar los importes correspondientes.

 5.11. Dashboard y reportes

Permite consultar indicadores y gráficos de gestión, así como acceder a reportes disponibles en el sistema.

 6. Recomendaciones de uso

- Verificar que MySQL esté funcionando antes de iniciar la aplicación.
- Introducir correctamente las credenciales de acceso.
- Comprobar los datos antes de guardar compras, productos o ventas.
- Revisar la información de inventario después de registrar operaciones.
- Cerrar la caja antes de salir cuando el usuario tenga una caja abierta asociada a su cuenta.

 7. Consideraciones

Las opciones disponibles pueden variar según el rol del usuario. Este manual se actualizará cuando se incorporen o modifiquen funcionalidades del sistema.
