# Módulos de Categorías y Marcas

## 1. Descripción general

Los módulos de Categorías y Marcas permiten administrar información complementaria del catálogo de productos de la Ferretería Colorín. Ambos facilitan la clasificación e identificación de los productos y se utilizan desde la interfaz gráfica del módulo de Productos.

Cada módulo cuenta con su propio modelo, DAO, servicio y controlador. No disponen de una interfaz gráfica independiente; sus operaciones se realizan desde `ProductoPanel`.

## 2. Modelo de Categoría: `Categoria.java`

**Paquete:** `pe.ferreteria.modelo`

Representa una categoría utilizada para clasificar los productos.

**Atributos:**
- `idCategoria`: identificador de la categoría.
- `nombre`: nombre de la categoría.
- `descripcion`: descripción adicional.
- `activo`: indica si la categoría está activa.

La clase incluye constructores, getters, setters y el método `toString()`, que devuelve el nombre de la categoría para facilitar su presentación en los campos de selección.

## 3. DAO de Categoría: `CategoriaDAO.java`

**Paquete:** `pe.ferreteria.dao`

Gestiona las operaciones de acceso a la tabla `Categoria`.

Sus principales operaciones son:

- `insertar()`: registra una categoría.
- `listar()`: consulta las categorías activas, ordenadas por nombre.
- `eliminar()`: realiza la eliminación lógica mediante el campo `activo`.
- `existeNombre()`: comprueba si existe una categoría activa con el mismo nombre, excluyendo el identificador indicado.

Utiliza JDBC, consultas SQL parametrizadas mediante `PreparedStatement` y bloques `try-with-resources` para gestionar los recursos de base de datos.

## 4. Servicio de Categoría: `CategoriaService.java`

**Paquete:** `pe.ferreteria.servicio`

Actúa como intermediario entre el controlador y el DAO de Categoría. Delega las operaciones de registro, listado, eliminación y comprobación de nombres.

En el código revisado, no incorpora validaciones adicionales de negocio.

## 5. Controlador de Categoría: `CategoriaController.java`

**Paquete:** `pe.ferreteria.controlador`

Conecta las solicitudes de la interfaz gráfica con `CategoriaService`. Expone los métodos necesarios para registrar, listar y eliminar categorías, así como para comprobar nombres existentes.

## 6. Modelo de Marca: `Marca.java`

**Paquete:** `pe.ferreteria.modelo`

Representa una marca asociada a los productos.

**Atributos:**
- `idMarca`: identificador de la marca.
- `nombre`: nombre de la marca.
- `descripcion`: descripción adicional.
- `activo`: indica si la marca está activa.

La clase incluye constructores, getters, setters y el método `toString()`, que devuelve el nombre de la marca para facilitar su presentación en los campos de selección.

## 7. DAO de Marca: `MarcaDAO.java`

**Paquete:** `pe.ferreteria.dao`

Gestiona las operaciones de acceso a la tabla `Marca`.

Sus principales operaciones son:

- `insertar()`: registra una marca.
- `listar()`: consulta las marcas activas, ordenadas por nombre.
- `eliminar()`: realiza la eliminación lógica mediante el campo `activo`.
- `existeNombre()`: comprueba si existe una marca activa con el mismo nombre, excluyendo el identificador indicado.

Utiliza JDBC, consultas SQL parametrizadas mediante `PreparedStatement` y bloques `try-with-resources`.

## 8. Servicio de Marca: `MarcaService.java`

**Paquete:** `pe.ferreteria.servicio`

Actúa como intermediario entre el controlador y el DAO de Marca. Delega las operaciones de registro, listado, eliminación y comprobación de nombres.

En el código revisado, no incorpora validaciones adicionales de negocio.

## 9. Controlador de Marca: `MarcaController.java`

**Paquete:** `pe.ferreteria.controlador`

Conecta las solicitudes de la interfaz gráfica con `MarcaService`. Expone los métodos necesarios para registrar, listar y eliminar marcas, así como para comprobar nombres existentes.

## 10. Base de datos

### Tabla `Categoria`

Almacena las categorías utilizadas para clasificar los productos.

- `id_categoria`: clave primaria autoincremental.
- `nombre`: nombre de la categoría, obligatorio y único.
- `descripcion`: descripción opcional.
- `activo`: estado de la categoría, con valor predeterminado `1`.

### Tabla `Marca`

Almacena las marcas asociadas a los productos.

- `id_marca`: clave primaria autoincremental.
- `nombre`: nombre de la marca, obligatorio y único.
- `descripcion`: descripción opcional.
- `activo`: estado de la marca, con valor predeterminado `1`.

Ambas tablas tienen una restricción de unicidad sobre el nombre. Esta restricción se aplica también a los registros inactivos.

## 11. Integración con el módulo de Productos

`ProductoPanel` utiliza `CategoriaController` y `MarcaController` para consultar las opciones disponibles en los campos de selección y para registrar o eliminar categorías y marcas.

Al registrar o actualizar un producto, la interfaz obtiene los identificadores correspondientes y los asigna a `idCategoria` e `idMarca`. De esta manera, la tabla `Producto` mantiene las relaciones con las tablas `Categoria` y `Marca`.

## 12. Observaciones técnicas

- La eliminación de categorías y marcas es lógica: el registro permanece en la base de datos y se marca como inactivo.
- Los listados muestran únicamente los registros activos.
- La comprobación de nombres en los DAO considera los registros activos, pero las restricciones `UNIQUE` de la base de datos también incluyen los inactivos. Por ello, intentar registrar nuevamente el nombre de un registro inactivo puede producir un error de duplicidad.
- La conexión con MySQL se realiza mediante la infraestructura compartida del sistema, documentada por separado. no se si esta bien asi o  lodetallamos mas