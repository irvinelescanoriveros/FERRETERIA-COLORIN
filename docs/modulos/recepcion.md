# Módulo de Recepción

## 1. Descripción general

El módulo de Recepción permite registrar y controlar la llegada de los productos adquiridos mediante una compra. Permite consultar compras pendientes de recepción, revisar sus productos, registrar observaciones, corregir productos observados y completar la recepción.

Cuando un producto se completa correctamente, el sistema registra su ingreso en el inventario y genera el movimiento correspondiente en el Kardex.

## 2. Arquitectura del módulo

El módulo utiliza una arquitectura por capas:

- **Modelo:** representa los datos de la recepción y sus detalles.
- **DAO:** ejecuta las operaciones de consulta, inserción y actualización en la base de datos.
- **Servicio:** coordina las reglas de negocio y las operaciones relacionadas con inventario y Kardex.
- **Controlador:** conecta la vista con los servicios.
- **Vista:** permite al usuario interactuar con el módulo.

## 3. Clases del modelo

### 3.1. `Recepcion.java`

Representa la recepción general asociada a una compra.

Atributos principales:

- `idRecepcion`: identificador de la recepción.
- `idCompra`: compra relacionada.
- `idUsuario`: usuario que registra la recepción.
- `fechaRecepcion`: fecha y hora de registro.
- `estado`: estado general de la recepción.
- `observacion`: observación general.

Incluye constructores, getters y setters para acceder y modificar sus atributos.

### 3.2. `DetalleRecepcion.java`

Representa la recepción individual de cada detalle de compra.

Atributos principales:

- `idDetalleRecepcion`: identificador del detalle de recepción.
- `idRecepcion`: recepción a la que pertenece.
- `idDetalleCompra`: detalle de compra relacionado.
- `estado`: estado del producto recibido.
- `fechaConfirmacion`: fecha y hora de confirmación.
- `cantidadRecibida`: cantidad que realmente llegó.
- `observacion`: motivo o descripción de una incidencia.

Esta clase permite registrar el resultado de la revisión de cada producto.

## 4. Capa de acceso a datos (DAO)

### 4.1. `RecepcionDAO.java`

Gestiona las operaciones de base de datos correspondientes a la recepción general.

Métodos principales:

- `listarComprasPendientesRecepcion()`: obtiene las compras que todavía tienen productos pendientes de recepción.
- `insertar(Recepcion recepcion)`: registra una nueva recepción y devuelve el identificador generado.
- `buscarUltimaPorCompra(int idCompra)`: obtiene la recepción más reciente de una compra.
- `actualizarEstadoSegunDetalles(int idRecepcion)`: calcula y actualiza el estado general según los estados de sus detalles.
- `actualizarEstadoSegunDetalles(Connection con, int idRecepcion)`: realiza la actualización usando una conexión proporcionada, permitiendo integrarla en una transacción.

### 4.2. `DetalleRecepcionDAO.java`

Gestiona los detalles individuales de una recepción.

Métodos principales:

- `listarPorRecepcion(int idRecepcion)`: lista los detalles registrados para una recepción.
- `insertar(DetalleRecepcion detalle)`: inserta un detalle de recepción.
- `insertar(Connection con, DetalleRecepcion detalle)`: inserta el detalle utilizando una conexión existente.
- `buscarPorRecepcionYDetalleCompra(int idRecepcion, int idDetalleCompra)`: busca un detalle específico dentro de una recepción.
- `actualizar(DetalleRecepcion detalle)`: actualiza el estado, la fecha de confirmación, la cantidad recibida y la observación.
- `actualizar(Connection con, DetalleRecepcion detalle)`: realiza la actualización utilizando una conexión existente.

Las variantes que reciben `Connection` permiten ejecutar operaciones dentro de una misma transacción.

## 5. Capa de servicio

### `RecepcionService.java`

Coordina las operaciones del módulo y aplica las reglas de negocio.

Entre sus principales responsabilidades están:

- Consultar las compras pendientes de recepción.
- Registrar recepciones y sus detalles.
- Consultar los detalles de una compra.
- Buscar la recepción más reciente y sus detalles.
- Actualizar el estado de los detalles y de la recepción general.
- Completar una recepción registrando simultáneamente el inventario y el movimiento de Kardex.

### 5.1. Registro de inventario y Kardex

El método `completarRecepcionConInventarioYKardex(...)` realiza el proceso principal de confirmación de ingreso.

1. Valida los objetos y los identificadores necesarios, así como que la cantidad recibida sea positiva.
2. Inicia una transacción en la base de datos.
3. Crea un registro de inventario con el producto, proveedor, lote, cantidad recibida, precios y datos adicionales.
4. Inserta o actualiza el detalle de recepción como completado.
5. Registra en el Kardex un movimiento de tipo `Entrada`, con motivo `Compra`, la cantidad ingresada y la referencia a la compra.
6. Actualiza el estado general de la recepción.
7. Confirma la transacción si todas las operaciones terminan correctamente.

Si ocurre un error, intenta revertir la transacción mediante `rollback` y devuelve `0` para indicar que el proceso no se completó correctamente.

Este procedimiento busca evitar que el inventario, el detalle de recepción y el Kardex queden registrados de manera inconsistente.

## 6. Capa de controlador

### `RecepcionController.java`

Actúa como intermediario entre la vista y `RecepcionService`.

Expone métodos para:

- Listar compras pendientes.
- Registrar una recepción.
- Consultar los detalles de una recepción o compra.
- Buscar la última recepción de una compra.
- Insertar y actualizar detalles.
- Recalcular el estado general.
- Completar una recepción y registrar el inventario y el Kardex.

El controlador delega estas operaciones al servicio, manteniendo separada la lógica de la interfaz gráfica.

## 7. Capa de vista

### `RecepcionPanel.java`

Es la interfaz gráfica utilizada para gestionar las recepciones.

Sus principales funciones son:

- Mostrar las compras pendientes en una lista desplegable.
- Presentar la fecha de compra y el proveedor seleccionado.
- Mostrar los productos con información de lote, nombre, categoría, marca, cantidad, vencimiento, estante, nivel y estado.
- Filtrar los productos por texto y estado.
- Seleccionar un producto para revisar sus datos.
- Registrar una observación cuando existe una incidencia.
- Corregir un producto observado.
- Completar la recepción de un producto.
- Actualizar la interfaz después de las operaciones.

### 7.1. Observar un producto

El usuario selecciona un producto, registra una observación y especifica una cantidad recibida válida. El sistema guarda el detalle con estado `Observada` y actualiza el estado general de la recepción.

No se registra el ingreso de inventario mediante esta operación.

### 7.2. Corregir un producto observado

La corrección está habilitada únicamente para el rol `Administrador`.

El sistema permite devolver el detalle observado al estado `Pendiente`, elimina la observación y la fecha de confirmación, y conserva la cantidad previamente observada. El producto debe confirmarse posteriormente para completar su recepción.

### 7.3. Completar un producto

El sistema verifica que el producto esté seleccionado y que no tenga una observación pendiente de corrección. Luego registra la recepción, crea el inventario y genera el movimiento de entrada en el Kardex.

Al finalizar correctamente, la vista actualiza la tabla y la lista de compras pendientes.

## 8. Estados de recepción

Los estados utilizados por el módulo son:

- **Pendiente:** todavía existen detalles que no se han completado.
- **Observada:** al menos un detalle presenta una incidencia.
- **Completada:** todos los detalles existentes están completados.

El estado general se actualiza a partir del estado de los detalles. Si existe algún detalle `Observada`, la recepción general queda `Observada`. Si no hay observaciones, pero quedan detalles sin completar, queda `Pendiente`. Si todos los detalles existentes están completados, queda `Completada`.

## 9. Flujo general del proceso

1. El usuario selecciona una compra pendiente.
2. El sistema muestra los productos de esa compra.
3. El usuario selecciona un producto y revisa su cantidad y demás datos.
4. Si detecta una incidencia, registra una observación.
5. Si la observación necesita corrección, un administrador devuelve el detalle a estado pendiente.
6. Cuando el producto está listo para confirmarse, el usuario completa su recepción.
7. El servicio registra el inventario y el movimiento de entrada en el Kardex dentro de una transacción.
8. El sistema actualiza el estado general y refresca la interfaz.

## 10. Resultado del módulo

El módulo de Recepción vincula la compra con la entrada física de los productos. Permite controlar las incidencias, mantener un registro de las cantidades recibidas y reflejar los ingresos en el inventario y el Kardex.

De esta manera, se mantiene la trazabilidad entre la compra realizada, su recepción y el movimiento de inventario generado.
