# Módulo de Productos

## 1. Descripción general

El módulo de Productos permite administrar el catálogo de productos de la Ferretería Colorín. Incluye el registro, consulta, edición, búsqueda y eliminación lógica de productos. También permite asociar cada producto con una categoría y una marca.

Está desarrollado en Java con Swing y utiliza MySQL para almacenar los datos en la tabla `Producto` de la base de datos `bd_colorin`.

La implementación utiliza una arquitectura por capas que separa la representación de los datos, el acceso a la base de datos, la lógica de servicio, el control de las operaciones y la interfaz gráfica.

## 2. Modelo: `Producto.java`

**Paquete:** `pe.ferreteria.modelo`

La clase `Producto` representa la información de cada producto del sistema.

**Atributos:**
- `idProducto`: identificador del producto.
- `codigo`: código único del producto.
- `nombre`: nombre del producto.
- `idCategoria`: identificador de la categoría asociada.
- `idMarca`: identificador de la marca asociada.
- `unidad`: unidad de medida.
- `estante`: ubicación del producto en el almacén.
- `nivel`: nivel o posición dentro del estante.
- `descripcion`: descripción adicional.
- `activo`: estado de actividad del registro.

La clase incluye constructores, getters y setters. Su función es representar los datos; no ejecuta consultas SQL ni implementa validaciones de negocio.

## 3. Acceso a datos: `ProductoDAO.java`

**Paquete:** `pe.ferreteria.dao`

La clase `ProductoDAO` implementa las operaciones de persistencia sobre la tabla `Producto` mediante JDBC y la conexión compartida del sistema.

Sus principales métodos son:

- `insertar()`: registra un producto.
- `listar()`: recupera los productos activos, ordenados por identificador.
- `actualizar()`: modifica los datos del producto seleccionado.
- `eliminar()`: realiza una eliminación lógica estableciendo `activo = false`.
- `existeCodigo()`: comprueba si otro producto activo tiene el mismo código, excluyendo el identificador indicado.
- `buscarParaDashboard()`: obtiene información básica de un producto activo por su identificador.

Las consultas parametrizadas utilizan `PreparedStatement` y los recursos se gestionan mediante `try-with-resources`.

**Manejo de errores:** las operaciones de inserción, actualización y eliminación devuelven `false` ante una excepción SQL. El listado imprime la excepción y devuelve la lista recopilada, que puede estar vacía o incompleta. La búsqueda para el dashboard devuelve `null` si no encuentra el producto y lanza una excepción de tipo `RuntimeException` si ocurre un error SQL.

## 4. Servicio: `ProductoService.java`

**Paquete:** `pe.ferreteria.servicio`

La clase `ProductoService` crea una instancia de `ProductoDAO` y delega en ella las operaciones de registro, listado, actualización, eliminación y comprobación de códigos. También permite obtener información básica de un producto para el dashboard.

En el código revisado, el servicio no incorpora validaciones de negocio adicionales; funciona como intermediario entre el controlador y la capa de acceso a datos.

## 5. Controlador: `ProductoController.java`

**Paquete:** `pe.ferreteria.controlador`

La clase `ProductoController` crea una instancia de `ProductoService` y expone los métodos que utiliza la interfaz gráfica para registrar, listar, actualizar y eliminar productos, comprobar códigos y obtener información para el dashboard.

El controlador delega las operaciones al servicio y no ejecuta directamente sentencias SQL.

## 6. Interfaz gráfica: `ProductoPanel.java`

**Paquete:** `pe.ferreteria.vista`

La clase `ProductoPanel` implementa la interfaz del módulo mediante componentes Swing. Utiliza `ProductoController` para ejecutar las operaciones y presenta los datos en una tabla.

### Funcionalidades

- **Listado:** muestra el identificador, código, nombre, categoría, marca, unidad, estante, nivel y descripción.
- **Registro:** valida los campos y comprueba si el código ya pertenece a otro producto activo antes de solicitar el registro.
- **Edición:** permite modificar los datos del producto seleccionado.
- **Eliminación:** solicita confirmación y llama al controlador para desactivar el registro.
- **Búsqueda:** filtra los productos por código, nombre, categoría, unidad, estante, nivel y marca.
- **Gestión de categorías y marcas:** permite registrar y eliminar categorías y marcas mediante las opciones correspondientes.
- **Limpieza:** permite limpiar los campos y actualizar el listado.

### Validaciones del formulario

La interfaz comprueba los campos obligatorios y aplica formatos al código, estante y nivel. El código debe contener dígitos, el estante debe contener letras y el nivel debe contener dígitos. La descripción es opcional.

### Dependencias de Categoría y Marca

`ProductoPanel` utiliza `CategoriaController` y `MarcaController` para cargar las opciones disponibles en los campos de selección y obtener los identificadores correspondientes. Estos identificadores se asignan a `idCategoria` e `idMarca` antes de registrar o actualizar un producto.

Los componentes propios de Categoría y Marca se documentan por separado en el archivo conjunto de esos módulos.

## 7. Estructura de la base de datos

La tabla `Producto` pertenece a la base de datos `bd_colorin`.

| Campo | Tipo o función |
|---|---|
| `id_producto` | Clave primaria autoincremental. |
| `codigo` | Código del producto, obligatorio y único. |
| `nombre` | Nombre del producto, obligatorio. |
| `id_categoria` | Clave foránea que referencia a `Categoria`. |
| `id_marca` | Clave foránea que referencia a `Marca`. |
| `unidad` | Unidad de medida, obligatoria. |
| `estante` | Ubicación del producto. |
| `nivel` | Nivel o posición dentro del estante. |
| `descripcion` | Descripción adicional. |
| `activo` | Estado del producto, con valor predeterminado `1`. |

Las claves foráneas permiten asociar cada producto con una categoría y una marca existentes.

## 8. Observaciones técnicas

1. **Eliminación lógica:** el registro permanece en la base de datos y se marca como inactivo.
2. **Código único:** MySQL aplica la restricción de unicidad al código, incluidos los registros inactivos. Por ello, un código perteneciente a un producto inactivo puede impedir el registro de otro producto con el mismo código.
3. **Actualización:** el método `actualizar()` modifica los datos del producto por su identificador y no incluye un filtro por estado activo.
4. **Búsqueda:** la búsqueda de la interfaz filtra por código, nombre, categoría, unidad, estante, nivel y marca; no incluye la descripción.
5. **Componentes compartidos:** la clase `ConexionBD` se documenta en la sección general de infraestructura, no dentro de este módulo.

## 9. Resultado del módulo

El módulo de Productos permite administrar el catálogo de la ferretería mediante operaciones de registro, consulta, actualización, búsqueda y eliminación lógica. La interfaz facilita la asociación de productos con categorías y marcas, mientras que el DAO gestiona la persistencia de los datos en MySQL mediante una arquitectura por capas.

La documentación se basa en los archivos Java y la estructura SQL compartidos. No implica que se hayan ejecutado pruebas funcionales de todas las operaciones ni que se hayan verificado todos los eventos internos de la interfaz gráfica.