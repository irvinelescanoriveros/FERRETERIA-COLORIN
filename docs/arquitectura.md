 Arquitectura del sistema — Ferretería Colorín

 1. Descripción general

El sistema está desarrollado en Java y utiliza Java Swing para la interfaz gráfica y MySQL para el almacenamiento de información. El código se organiza en capas con responsabilidades diferenciadas, lo que facilita su comprensión, mantenimiento y evolución.

 2. Organización por capas

 2.1. Modelo (`modelo`)

Contiene las clases que representan las entidades y los datos utilizados por el sistema, como `Usuario`, `Cliente`, `Producto`, `Compra`, `Venta`, `Inventario`, `Kardex` y `Caja`.

 2.2. Acceso a datos (`dao`)

Contiene las clases encargadas de ejecutar consultas y operaciones SQL sobre la base de datos. Por ejemplo, `UsuarioDAO` permite realizar operaciones relacionadas con los usuarios y la autenticación.

 2.3. Servicios (`servicio`)

Contiene las clases que coordinan las operaciones de la aplicación y utilizan los DAO para acceder a los datos. Por ejemplo, `UsuarioService` delega las operaciones de usuarios a `UsuarioDAO`.

 2.4. Controladores (`controlador`)

Conectan las acciones de la interfaz con los servicios correspondientes. Por ejemplo, `LoginController` solicita la autenticación mediante `UsuarioService` y, cuando es correcta, inicia la sesión del usuario.

 2.5. Vistas (`vista`)

Contiene los formularios, ventanas y paneles gráficos desarrollados con Java Swing. Entre ellos se encuentran `LoginForm`, `MainForm`, `ProductoPanel`, `VentaPanel` e `InventarioPanel`.

 2.6. Infraestructura (`infraestructura`)

Contiene componentes de soporte, como `ConexionBD`, `Configuracion`, `Seguridad` y `SesionUsuario`.

 2.7. Utilidades (`util`)

Incluye clases auxiliares para validaciones, generación de lotes, comprobantes, etiquetas y reportes.

 2.8. Punto de entrada (`principal`)

Contiene `Principal.java`, asociado al inicio de la aplicación.

 3. Flujo de autenticación

El proceso de inicio de sesión sigue este flujo:

1. El usuario introduce sus credenciales en `LoginForm`.
2. La vista valida que los campos obligatorios estén completos.
3. `LoginController` recibe las credenciales y llama a `UsuarioService`.
4. `UsuarioService` delega la autenticación a `UsuarioDAO`.
5. `UsuarioDAO` consulta MySQL y comprueba las credenciales del usuario activo.
6. Si la autenticación es correcta, `LoginController` inicia la sesión mediante `SesionUsuario`.
7. `MainForm` utiliza los datos de la sesión para mostrar información del usuario y aplicar restricciones de navegación según su rol.

4. Flujo general de operaciones

En las operaciones que siguen esta organización, la vista recoge la interacción del usuario, el controlador coordina la acción, el servicio delega la operación correspondiente al DAO y este accede a la base de datos.

La información obtenida se devuelve a las capas correspondientes para actualizar la interfaz.

5. Conexión con la base de datos

La clase `ConexionBD` pertenece al paquete de infraestructura y proporciona el mecanismo de conexión utilizado por los DAO para acceder a MySQL.

6. Beneficios de la organización

- Separa las responsabilidades de la interfaz, la lógica de servicio y el acceso a datos.
- Facilita localizar errores y realizar mantenimiento.
- Permite identificar con claridad las clases responsables de cada operación.
- Favorece la organización y ampliación gradual del sistema.
