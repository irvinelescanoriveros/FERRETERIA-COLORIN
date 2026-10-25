# Módulo de Clientes

## 1. Descripción general

El módulo de Clientes permite registrar, consultar, actualizar y eliminar lógicamente clientes de la ferretería. Admite dos tipos de cliente: **Persona Natural** y **Persona Jurídica**.

Está organizado en las capas Modelo, DAO, Servicio, Controlador e Interfaz gráfica. El acceso a los datos se realiza mediante JDBC y la conexión compartida del proyecto.

Sus principales funciones son:
- Registrar clientes naturales y jurídicos.
- Consultar los clientes activos.
- Buscar clientes por identificación y datos personales o comerciales.
- Editar la información de un cliente.
- Eliminar clientes mediante una baja lógica.
- Comprobar si el DNI o RUC ya está registrado.

## 2. Modelo

**Archivo:** `Cliente.java`

Representa la información de un cliente mediante los siguientes atributos:

| Atributo | Tipo Java | Descripción |
|---|---|---|
| `idCliente` | `int` | Identificador del cliente. |
| `tipoCliente` | `String` | Tipo de cliente: Natural o Jurídica. |
| `dni` | `String` | Documento de identidad de una persona natural. |
| `ruc` | `String` | Registro Único de Contribuyentes de una persona jurídica. |
| `nombre` | `String` | Nombre de la persona natural. |
| `apellidos` | `String` | Apellidos de la persona natural. |
| `razonSocial` | `String` | Razón social de la persona jurídica. |
| `contacto` | `String` | Persona de contacto de una empresa. |
| `telefono` | `String` | Número telefónico. |
| `direccion` | `String` | Dirección del cliente. |
| `activo` | `boolean` | Indica si el cliente está activo. |

La clase dispone de un constructor vacío, un constructor con todos los atributos y sus respectivos métodos getter y setter.

El método `toString()` devuelve el nombre completo cuando el tipo es `Natural`; para los demás tipos devuelve la razón social. Esto permite representar el cliente mediante su nombre personal o comercial cuando se utiliza el objeto como texto.

El modelo se encarga de representar los datos, mientras que las validaciones del formulario y las operaciones con la base de datos se realizan en otras capas.

## 3. Acceso a datos (DAO)

**Archivo:** `ClienteDAO.java`

La clase `ClienteDAO` implementa las operaciones de acceso a la tabla `Cliente` mediante JDBC.

### Métodos principales

- **`insertar(Cliente cliente)`**: registra un cliente con sus datos personales o comerciales y su estado de actividad. Devuelve `true` si se insertó al menos una fila y `false` si ocurre un error SQL.
- **`listar()`**: consulta únicamente los clientes activos y los ordena por `id_cliente`. Convierte los resultados de la consulta en objetos `Cliente`.
- **`actualizar(Cliente cliente)`**: modifica el tipo de cliente, DNI, RUC, nombre, apellidos, razón social, contacto, teléfono y dirección del registro indicado por su identificador.
- **`eliminar(int idCliente)`**: realiza una baja lógica estableciendo `activo = false`, sin borrar físicamente el registro.
- **`existeDni(String dni, int idCliente)`**: comprueba si otro cliente activo utiliza el mismo DNI, excluyendo el identificador indicado.
- **`existeRuc(String ruc, int idCliente)`**: comprueba si otro cliente activo utiliza el mismo RUC, excluyendo el identificador indicado.
- **`buscarPorId(int idCliente)`**: busca un cliente por su identificador y devuelve un objeto `Cliente` o `null` si no se encuentra o se produce un error SQL.

El DAO utiliza `PreparedStatement` para ejecutar consultas parametrizadas y `ResultSet` para recuperar los datos. Los recursos JDBC se administran mediante bloques `try-with-resources`.

La conexión se obtiene mediante la infraestructura compartida del proyecto; su implementación se documenta en la sección general de infraestructura para evitar repetirla en cada módulo.

### Manejo de errores

Las operaciones capturan las excepciones `SQLException` e imprimen mensajes de error. Según el método, devuelven `false`, una lista con los resultados obtenidos hasta ese momento o `null`. En particular, los métodos `existeDni()` y `existeRuc()` devuelven `false` ante un error SQL, por lo que ese resultado no garantiza por sí solo que la consulta de duplicados se haya completado correctamente.

## 4. Servicio

**Archivo:** `ClienteService.java`

La clase `ClienteService` actúa como intermediaria entre el controlador y el DAO. Crea una instancia de `ClienteDAO` y delega en ella las operaciones de registro, consulta, actualización, eliminación lógica, verificación de DNI/RUC y búsqueda por identificador.

El servicio no añade validaciones de negocio en el código proporcionado; la validación de los campos se realiza principalmente en la interfaz gráfica.

## 5. Controlador

**Archivo:** `ClienteController.java`

El controlador expone las operaciones que necesita la interfaz y las delega a `ClienteService`.

Entre sus operaciones se encuentran:
- Registrar y listar clientes.
- Actualizar y eliminar clientes.
- Verificar la existencia de DNI y RUC.
- Buscar un cliente por su identificador.

El controlador no contiene consultas SQL ni incorpora validaciones adicionales en el código revisado. Su función es mantener la comunicación entre la interfaz y la capa de servicio.

## 6. Interfaz gráfica

**Archivo:** `ClientePanel.java`

La interfaz está desarrollada con Swing y utiliza `JPanel`, `JTextField`, `JRadioButton`, `JButton`, `JTable` y cuadros de diálogo `JOptionPane`.

### Formulario de Persona Natural

Incluye los campos:
- DNI.
- Nombre.
- Apellidos.
- Teléfono.
- Dirección.

El DNI, el nombre y los apellidos son obligatorios. El teléfono puede quedar vacío, pero si se proporciona debe contener únicamente dígitos.

### Formulario de Persona Jurídica

Incluye los campos:
- RUC.
- Razón social.
- Contacto.
- Teléfono.
- Dirección.

El RUC y la razón social son obligatorios. El teléfono es opcional y, cuando se ingresa, debe contener únicamente dígitos.

Los botones de opción permiten seleccionar el tipo de cliente. El método `actualizarCamposSegunTipo()` habilita los campos correspondientes a la selección, y los métodos de carga del formulario recuperan los datos del cliente elegido. Durante la edición, el DNI o el RUC se bloquea para impedir que se modifique desde el formulario.

### Botones y operaciones

La interfaz muestra los botones **GUARDAR**, **EDITAR**, **ELIMINAR** y **LIMPIAR**.

- **Guardar:** valida los datos, comprueba el DNI o RUC y solicita al controlador registrar el cliente.
- **Editar:** utiliza el cliente seleccionado, valida los cambios y actualiza el registro.
- **Eliminar:** solicita confirmación y ejecuta la baja lógica.
- **Limpiar:** vacía los campos y restablece el formulario al tipo Persona Natural.

El código incluye métodos para registrar, actualizar y eliminar clientes, además de métodos auxiliares para validar los datos. La inicialización generada por NetBeans conecta los botones con sus respectivos eventos.

### Tabla de clientes

La tabla `TableCliente` presenta las siguientes columnas:

| Columna | Contenido |
|---|---|
| ID | Identificador del cliente. |
| DNI/RUC | Documento de identificación según el tipo de cliente. |
| Nombre/Razón | Nombre personal o razón social. |
| Apellido/Contacto | Apellidos o contacto comercial. |
| Teléfono | Número telefónico. |
| Dirección | Dirección del cliente. |

El campo de búsqueda permite filtrar los clientes cargados en memoria por DNI/RUC, nombre o razón social, apellidos o contacto, teléfono y dirección. La búsqueda trabaja sobre la lista cargada por la interfaz y no realiza una nueva consulta a la base de datos con cada cambio de texto.

## 7. Estructura de la base de datos

La tabla `Cliente` pertenece a la base de datos `bd_colorin`.

| Campo | Tipo | Restricción o función |
|---|---|---|
| `id_cliente` | `INT` | Clave primaria autoincremental. |
| `tipo_cliente` | `ENUM('Natural', 'Juridica')` | Obligatorio. Define el tipo de cliente. |
| `dni` | `CHAR(8)` | Único. Permite valores nulos en SQL. |
| `ruc` | `CHAR(11)` | Único. Permite valores nulos en SQL. |
| `nombre` | `VARCHAR(100)` | Opcional en SQL. |
| `apellidos` | `VARCHAR(100)` | Opcional en SQL. |
| `razon_social` | `VARCHAR(150)` | Opcional en SQL. |
| `contacto` | `VARCHAR(100)` | Opcional en SQL. |
| `telefono` | `VARCHAR(15)` | Opcional en SQL. |
| `direccion` | `VARCHAR(150)` | Opcional en SQL. |
| `activo` | `TINYINT(1)` | Obligatorio, con valor predeterminado `1`. |

Aunque los campos personales y comerciales permiten valores nulos en la base de datos, la interfaz exige DNI, nombre y apellidos para las personas naturales, y RUC y razón social para las personas jurídicas.

Las restricciones `UNIQUE` de DNI y RUC también se aplican a los registros inactivos. Por ello, un documento que pertenece a un cliente dado de baja no puede reutilizarse mientras permanezca registrado en la tabla.

## 8. Observaciones técnicas

1. **DNI y RUC únicos:** el DAO comprueba duplicados únicamente entre clientes activos, mientras que las restricciones `UNIQUE` de la base de datos también se aplican a los registros inactivos. Por ello, intentar registrar un DNI o RUC que pertenezca a un cliente dado de baja puede superar la comprobación de la interfaz y fallar al insertar en la base de datos.

2. **Campos obligatorios:** la interfaz exige DNI, nombre y apellidos para personas naturales, y RUC y razón social para personas jurídicas. En cambio, la tabla permite valores nulos en esos campos; las reglas de obligatoriedad no están impuestas mediante restricciones `NOT NULL` en el esquema proporcionado.

3. **Actualización:** `actualizar()` no modifica el campo `activo` y tampoco filtra por dicho campo en la condición de actualización. Por tanto, el método no limita la operación a clientes activos.

4. **Búsqueda por identificador:** `buscarPorId()` no filtra por `activo`, así que puede devolver un cliente inactivo. En contraste, `listar()` solo muestra clientes activos.

5. **Errores en verificaciones de duplicados:** `existeDni()` y `existeRuc()` devuelven `false` cuando se produce una excepción SQL. Ese resultado puede confundirse con la ausencia de duplicados, aunque la consulta no se haya completado correctamente.

6. **Métodos con funciones similares:** el panel contiene métodos con responsabilidades parecidas, como `guardarCliente()` y `registrarCliente()`, `editarCliente()` y `actualizarCliente()`, y `eliminarCliente()` y `confirmarEliminacionCliente()`. Conviene revisar su uso desde los eventos para mantener una sola ruta de ejecución por operación cuando sea posible.

7. **Búsqueda en memoria:** el filtrado de la tabla utiliza la lista previamente cargada. Si los datos cambian en la base de datos por otra vía, la búsqueda no los incorpora hasta que se recargue la lista.

## 9. Resultado del módulo

El módulo de Clientes proporciona las operaciones básicas para administrar personas naturales y jurídicas desde una interfaz de escritorio. Centraliza las operaciones de datos en `ClienteDAO`, las expone mediante el servicio y el controlador, y permite registrar, consultar, modificar y dar de baja clientes desde `ClientePanel`.

El funcionamiento documentado corresponde al código y al esquema SQL proporcionados; no implica que se hayan ejecutado pruebas funcionales del módulo.