# Módulo de Proveedores

## 1. Descripción general

El módulo de Proveedores permite registrar, consultar, actualizar, buscar y eliminar lógicamente la información de las empresas proveedoras de la ferretería. Está desarrollado en Java con Swing y utiliza MySQL para almacenar los datos en la tabla `Proveedor` de la base de datos `bd_colorin`.

La implementación utiliza una arquitectura por capas que separa la representación de los datos, el acceso a la base de datos, la lógica de servicio, el control de las operaciones y la interfaz gráfica.

## 2. Modelo: `Proveedor.java`

**Paquete:** `pe.ferreteria.modelo`

La clase `Proveedor` representa la información de cada proveedor.

**Atributos:**
- `idProveedor`: identificador del proveedor.
- `empresa`: nombre de la empresa.
- `ruc`: número de RUC.
- `contacto`: persona de contacto.
- `telefono`: teléfono de contacto.
- `correo`: correo electrónico.
- `direccion`: dirección del proveedor.
- `activo`: estado de actividad del registro.

Dispone de un constructor vacío, un constructor con todos los atributos y métodos *getters* y *setters*. Su función es representar los datos; no ejecuta consultas SQL ni implementa validaciones de negocio.

## 3. Acceso a datos: `ProveedorDAO.java`

**Paquete:** `pe.ferreteria.dao`

La clase `ProveedorDAO` implementa las operaciones de persistencia sobre la tabla `Proveedor` mediante `ConexionBD`, `PreparedStatement` y `ResultSet`.

Sus principales métodos son:

- `insertar()`: registra un proveedor con sus datos y estado de actividad.
- `listar()`: recupera los proveedores activos, ordenados por identificador.
- `actualizar()`: modifica los datos del proveedor seleccionado.
- `eliminar()`: realiza una eliminación lógica estableciendo `activo = false`.
- `existeRuc()`: comprueba si otro proveedor activo tiene el mismo RUC, excluyendo el identificador indicado.

Las consultas parametrizadas utilizan `PreparedStatement` y los recursos se cierran mediante `try-with-resources`.

**Manejo de errores:** las operaciones de inserción, actualización, eliminación y consulta de existencia devuelven `false` ante una excepción SQL. El listado imprime la excepción y devuelve la lista recopilada, que puede estar vacía o incompleta.

## 4. Servicio: `ProveedorService.java`

**Paquete:** `pe.ferreteria.servicio`

La clase `ProveedorService` crea una instancia de `ProveedorDAO` y delega en ella las operaciones de registro, listado, actualización, eliminación y comprobación de existencia del RUC.

En el código revisado, el servicio no incorpora validaciones de negocio adicionales; funciona como intermediario entre el controlador y la capa de acceso a datos.

## 5. Controlador: `ProveedorController.java`

**Paquete:** `pe.ferreteria.controlador`

La clase `ProveedorController` crea una instancia de `ProveedorService` y expone los métodos que utiliza la interfaz gráfica:

- `registrar(Proveedor proveedor)`.
- `listar()`.
- `actualizar(Proveedor proveedor)`.
- `eliminar(int idProveedor)`.
- `existeRuc(String ruc, int idProveedor)`.

El controlador delega las operaciones al servicio y no ejecuta directamente sentencias SQL.

## 6. Interfaz gráfica: `ProveedorPanel.java`

**Paquete:** `pe.ferreteria.vista`

La clase `ProveedorPanel` implementa la interfaz del módulo mediante componentes Swing. Utiliza `ProveedorController` para ejecutar las operaciones y presenta los datos en una tabla.

### Funcionalidades

- **Listado:** muestra el identificador, empresa, RUC, contacto, teléfono, correo y dirección de los proveedores activos.
- **Registro:** valida los campos y comprueba si el RUC ya pertenece a otro proveedor activo antes de solicitar el registro.
- **Edición:** permite modificar los datos del proveedor seleccionado. Al seleccionarlo, los campos de empresa y RUC quedan bloqueados en el formulario.
- **Eliminación:** solicita confirmación y llama al controlador para desactivar el registro.
- **Búsqueda:** filtra los proveedores por empresa, RUC, contacto, teléfono, correo o dirección.
- **Limpieza:** vacía los campos, deselecciona la tabla y permite recuperar el listado completo.

### Validaciones del formulario

- La empresa es obligatoria.
- El RUC debe contener exactamente 11 dígitos numéricos.
- El contacto es obligatorio.
- El teléfono es obligatorio y solo puede contener números.
- La dirección es obligatoria.
- El correo es opcional.

La interfaz realiza estas validaciones antes de solicitar el registro o la actualización.

## 7. Estructura de la base de datos

La tabla `Proveedor` pertenece a la base de datos `bd_colorin`.

| Campo | Tipo | Restricción o función |
|---|---|---|
| `id_proveedor` | `INT` | Clave primaria autoincremental. |
| `empresa` | `VARCHAR(150)` | Obligatorio. |
| `ruc` | `CHAR(11)` | Obligatorio y único. |
| `contacto` | `VARCHAR(100)` | Opcional en SQL. |
| `telefono` | `VARCHAR(15)` | Opcional en SQL. |
| `correo` | `VARCHAR(100)` | Opcional. |
| `direccion` | `VARCHAR(150)` | Opcional en SQL. |
| `activo` | `TINYINT(1)` | Obligatorio, con valor predeterminado `1`. |

Aunque algunos campos permiten valores nulos en la base de datos, la interfaz exige que se ingresen empresa, contacto, teléfono y dirección.

## 8. Observaciones técnicas

1. **RUC único:** MySQL aplica la restricción `UNIQUE` a todos los registros, incluidos los inactivos. Por ello, un RUC perteneciente a un proveedor eliminado lógicamente puede impedir el registro de otro proveedor con el mismo número.
2. **Actualización:** `actualizar()` no comprueba directamente la existencia de un RUC duplicado ni limita la actualización a proveedores activos.
3. **Errores en la consulta de existencia:** `existeRuc()` devuelve `false` si ocurre un error SQL. Esto puede confundirse con la ausencia de un RUC duplicado.
4. **Configuración de conexión:** `ConexionBD` centraliza la conexión con MySQL. Las credenciales deben mantenerse fuera del código fuente público y no incluirse en la documentación.

## 9. Resultado del módulo

El módulo de Proveedores implementa las operaciones de registro, listado, actualización, búsqueda y eliminación lógica mediante una arquitectura por capas. La interfaz aplica validaciones de entrada y el DAO realiza las operaciones persistentes sobre MySQL.

La documentación se basa en los archivos Java y en la definición SQL compartidos. No implica que se hayan ejecutado pruebas funcionales de todas las operaciones ni que se hayan verificado los eventos internos de todos los componentes gráficos.