# Módulo Usuario

## 1. Descripción general

El módulo Usuario permite registrar, consultar, actualizar y desactivar las cuentas del sistema. También participa en el inicio de sesión e identifica al usuario autenticado para mostrar su nombre y rol, así como controlar el acceso a los módulos.

Además, proporciona información de los usuarios asociados a las operaciones de inventario y ventas, incluidas las realizadas mediante la cuenta técnica WEB.

## 2. Modelo

**Clase:** `Usuario.java`  
**Paquete:** `pe.ferreteria.modelo`

Representa la información de cada usuario del sistema.

Sus atributos son:

- `idUsuario`: identificador del usuario.
- `username`: nombre de usuario.
- `nombre`: nombres del usuario.
- `apellidos`: apellidos del usuario.
- `correo`: correo electrónico.
- `password`: contraseña.
- `rol`: rol asignado.
- `activo`: indica si la cuenta está activa.

Incluye constructores, getters, setters y el método `toString()`, que devuelve el nombre de usuario.

## 3. Acceso a datos (DAO)

**Clase:** `UsuarioDAO.java`  
**Paquete:** `pe.ferreteria.dao`

Gestiona las operaciones de la tabla Usuario mediante consultas SQL preparadas.

Sus principales métodos son:

- `insertar()`: registra un usuario.
- `listar()`: obtiene los usuarios activos, excluyendo las cuentas con rol WEB.
- `listarKardex()`: obtiene todos los usuarios activos, incluidas las cuentas con rol WEB, para identificar a los usuarios asociados a los movimientos de Kardex.
- `actualizar()`: modifica los datos del usuario, excepto la contraseña.
- `eliminar()`: desactiva la cuenta sin eliminar el registro.
- `existeUsername()`: verifica si un nombre de usuario ya existe, incluso si pertenece a una cuenta inactiva.
- `cambiarPassword()`: actualiza la contraseña.
- `autenticar()`: valida las credenciales y comprueba que la cuenta esté activa.
- `buscarPorId()`: busca un usuario mediante su identificador.

Las operaciones utilizan `PreparedStatement` y manejo de excepciones SQL.

## 4. Servicio

**Clase:** `UsuarioService.java`  
**Paquete:** `pe.ferreteria.servicio`

Actúa como intermediario entre los controladores y el DAO. Expone los métodos de registro, listado, actualización, desactivación, comprobación del nombre de usuario, autenticación y cambio de contraseña.

También expone `listarKardex()`, que permite consultar todos los usuarios activos, incluidas las cuentas WEB, sin modificar el filtro del listado habitual de usuarios de escritorio.

El servicio es utilizado por `UsuarioController` para las operaciones de mantenimiento, por `LoginController` para la autenticación y por `KardexPanel` para identificar a los usuarios asociados a los movimientos de inventario.

## 5. Controlador

**Clases:** `UsuarioController.java` y `LoginController.java`  
**Paquete:** `pe.ferreteria.controlador`

`UsuarioController` conecta la interfaz de gestión de usuarios con el servicio y permite ejecutar las operaciones de mantenimiento.

`LoginController` gestiona el inicio de sesión. Si las credenciales son válidas, verifica que el usuario no tenga el rol WEB y, si corresponde, registra la sesión mediante `SesionUsuario.iniciarSesion()`.

Si el usuario no existe, las credenciales son incorrectas, la cuenta está inactiva o tiene el rol WEB, el inicio de sesión de escritorio es rechazado.

## 6. Interfaz gráfica

**Clases:** `LoginForm.java`, `UsuarioPanel.java`, `MainForm.java` y `KardexPanel.java`  
**Paquete:** `pe.ferreteria.vista`

- `LoginForm`: permite ingresar el nombre de usuario y la contraseña, solicita la autenticación y muestra un mensaje si las credenciales son incorrectas.
- `UsuarioPanel`: permite registrar, consultar, editar y desactivar usuarios. También permite cambiar la contraseña durante la edición. El rol WEB no aparece en el listado ni entre las opciones del combo de roles.
- `MainForm`: muestra el nombre completo y el rol del usuario autenticado, configura la visibilidad de los módulos según su rol y selecciona la pantalla inicial.
- `KardexPanel`: utiliza `UsuarioService.listarKardex()` para identificar a los usuarios asociados a los movimientos de inventario, incluidas las operaciones realizadas mediante la cuenta WEB.

Los roles operativos de la interfaz son Administrador, Vendedor y Almacen. El administrador conserva el acceso general; los otros roles tienen restricciones en la navegación.

El rol WEB se excluye del listado habitual de usuarios de escritorio y no puede iniciar sesión mediante `LoginController`.

Además, `MainForm` impide cerrar la ventana cuando el usuario tiene una caja abierta.

## 7. Estructura de la base de datos

La tabla Usuario pertenece a la base de datos `bd_colorin`.

- `id_usuario`: Clave primaria, autoincremental.
- `username`: `VARCHAR(50)`, obligatorio y único.
- `nombre`: `VARCHAR(100)`, obligatorio.
- `apellidos`: `VARCHAR(100)`, obligatorio.
- `correo`: `VARCHAR(100)`, obligatorio y único.
- `password`: `VARCHAR(255)`, obligatorio.
- `rol`: `ENUM`, obligatorio; admite Administrador, Vendedor, Almacen y WEB.
- `activo`: `TINYINT(1)`, obligatorio; valor predeterminado 1. Indica si la cuenta está activa.

La base de datos impide repetir el nombre de usuario y el correo electrónico. La columna `activo` permite desactivar cuentas conservando sus registros.

## 8. Observaciones técnicas

- El método `listar()` obtiene únicamente usuarios activos y excluye las cuentas con rol WEB.
- El método `listarKardex()` incluye todas las cuentas activas para resolver la identificación de los usuarios en los movimientos de inventario.
- `UsuarioPanel` no muestra el rol WEB en el listado ni en el combo de roles.
- `LoginController` rechaza el inicio de sesión de escritorio para las cuentas WEB.
- La desactivación se realiza mediante una actualización del campo `activo`.
- `existeUsername()` considera también las cuentas inactivas, por lo que sus nombres de usuario no pueden reutilizarse mientras exista el registro.
- La autenticación compara directamente la contraseña recibida con el valor almacenado. En el código revisado no se observa el uso de hash de contraseñas.
- En `UsuarioPanel`, el resultado del cambio de contraseña no se comprueba antes de mostrar el mensaje de actualización exitosa. Este comportamiento queda como mejora pendiente.
- La exclusión de las cuentas WEB del listado y del inicio de sesión de escritorio no implica que exista una autenticación web implementada.

### Prueba automatizada

Se agregó `KardexServiceTest.java`, que utiliza Mockito para simular el DAO y comprobar que `UsuarioService.listarKardex()` devuelve un usuario con rol WEB.

La prueba finalizó correctamente con `BUILD SUCCESS`, sin fallos ni errores. Esta prueba verifica el comportamiento del servicio, pero no ejecuta la consulta SQL real contra MySQL.

## 9. Resultado del módulo

El módulo permite gestionar las cuentas de usuario y participar en la autenticación del sistema. La sesión identifica al usuario autenticado y su rol para adaptar la navegación, mientras que la integración con Caja permite asociar la apertura a un usuario específico.

La separación entre `listar()` y `listarKardex()` permite mantener las cuentas WEB fuera de la gestión habitual de usuarios de escritorio y, al mismo tiempo, identificar correctamente a los usuarios asociados a los movimientos de inventario.

De esta manera, se mantiene la separación entre las cuentas operativas del escritorio y las cuentas técnicas utilizadas en las operaciones web.
