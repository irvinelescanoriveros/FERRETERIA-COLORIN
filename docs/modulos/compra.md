# Módulo Compra

## 1. Descripción general

El módulo Compra permite registrar las adquisiciones realizadas a los proveedores de la Ferretería Colorín. Almacena la información general de cada compra y el detalle de los productos adquiridos, incluyendo cantidades, precios, lotes y fechas de vencimiento cuando corresponda.

Este módulo registra la operación de compra. La recepción física de los productos y su ingreso al inventario corresponden al módulo Recepción.

## 2. Objetivo

Registrar de manera organizada las compras realizadas a los proveedores, conservar la información de los productos adquiridos y sus detalles, y verificar que el número de comprobante no esté duplicado entre las compras activas.

## 3. Clases que conforman el módulo

### 3.1. `Compra.java`

**Paquete:** `pe.ferreteria.modelo`

Representa la información general de una compra.

**Atributos:**
- `idCompra`: identificador de la compra.
- `idProveedor`: identificador del proveedor.
- `idUsuario`: identificador del usuario que registra la compra.
- `fechaCompra`: fecha y hora de la compra.
- `nroComprobante`: número del comprobante.
- `total`: importe total de la compra.
- `activo`: indica si el registro está activo.

Incluye un constructor vacío, un constructor con todos los atributos y métodos getters y setters.

### 3.2. `DetalleCompra.java`

**Paquete:** `pe.ferreteria.modelo`

Representa cada producto incluido en una compra.

**Atributos:**
- `idDetalle`: identificador del detalle.
- `idCompra`: identificador de la compra asociada.
- `idProducto`: identificador del producto.
- `lote`: código del lote del producto.
- `cantidad`: cantidad comprada.
- `precioCompra`: precio de compra por unidad.
- `fechaVencimiento`: fecha de vencimiento, cuando corresponda.

Incluye un constructor vacío, un constructor con todos los atributos y métodos getters y setters.

### 3.3. `CompraDAO.java`

**Paquete:** `pe.ferreteria.dao`

Gestiona el acceso a los datos de la compra.

**Métodos principales:**

- `insertar(Compra compra, Connection conexion)`: registra la cabecera de la compra utilizando la conexión recibida y devuelve el identificador generado por la base de datos. Lanza una excepción SQL si la inserción falla o no se obtiene el identificador.
- `existeNroComprobante(String nroComprobante)`: comprueba si existe una compra activa con el número de comprobante indicado.

El método de inserción utiliza una conexión externa, mientras que la comprobación del comprobante abre su propia conexión.

### 3.4. `DetalleCompraDAO.java`

**Paquete:** `pe.ferreteria.dao`

Gestiona los detalles asociados a una compra.

**Métodos principales:**

- `insertar(DetalleCompra detalle, Connection conexion, LocalDate fechaCompra)`: registra un detalle utilizando la conexión recibida. Si el lote está vacío, solicita uno a `GeneradorLote`. También registra la fecha de vencimiento cuando corresponde.
- `listarPorCompra(int idCompra)`: consulta y devuelve los detalles asociados a una compra, ordenados por identificador de detalle.

### 3.5. `CompraService.java`

**Paquete:** `pe.ferreteria.servicio`

Contiene la lógica para registrar una compra y sus detalles.

**Métodos principales:**

- `registrarCompra(Compra compra, List<DetalleCompra> detalles)`: abre una conexión, desactiva el autocommit, registra la cabecera y los detalles, y confirma la transacción mediante `commit()`. Si ocurre un error, intenta ejecutar `rollback()` y devuelve `false`.
- `existeNroComprobante(String nroComprobante)`: delega la comprobación del número de comprobante al DAO.

La cabecera y sus detalles se registran dentro de una misma transacción para evitar que se guarde solamente una parte de la operación.

### 3.6. `CompraController.java`

**Paquete:** `pe.ferreteria.controlador`

Actúa como intermediario entre la interfaz gráfica y el servicio.

**Métodos principales:**

- `registrarCompra(Compra compra, List<DetalleCompra> detalles)`: delega el registro de la compra al servicio.
- `existeNroComprobante(String nroComprobante)`: delega la comprobación del número de comprobante.

El controlador no ejecuta directamente las consultas SQL ni administra la transacción.

### 3.7. `CompraPanel.java`

**Paquete:** `pe.ferreteria.vista`

Es la interfaz gráfica que permite al usuario registrar una compra.

Sus principales responsabilidades son:

- Cargar proveedores, productos, marcas y categorías.
- Mostrar información del proveedor y del producto seleccionado.
- Registrar la fecha de compra y el número de comprobante.
- Ingresar cantidades, precios y fechas de vencimiento.
- Calcular subtotales y el total de la compra.
- Agregar y eliminar productos de la tabla de resumen.
- Validar los datos antes de registrar la compra.
- Verificar la sesión del usuario y comprobar si el número de comprobante está duplicado.
- Crear los objetos `Compra` y `DetalleCompra`.
- Enviar la información al controlador y mostrar el resultado del registro.
- Limpiar los campos después de completar o cancelar la operación.

Cuando se agrega un producto que ya se encuentra en la tabla con el mismo identificador, precio y fecha de vencimiento, la interfaz acumula la cantidad en la fila existente. Si el precio o la fecha de vencimiento son diferentes, conserva una fila separada.

El usuario también puede indicar que un producto no tiene vencimiento; en ese caso, la fecha se registra como nula.

## 4. Flujo de funcionamiento

1. El usuario abre el panel Compra.
2. El sistema carga los proveedores y productos disponibles.
3. El usuario selecciona un proveedor e ingresa el número de comprobante y la fecha de compra.
4. Selecciona los productos e ingresa las cantidades, los precios y las fechas de vencimiento cuando corresponda.
5. La interfaz calcula los subtotales y el total.
6. Antes de guardar, valida los datos, la sesión del usuario, el proveedor, el comprobante y la existencia de al menos un producto.
7. El controlador envía la información a `CompraService`.
8. El servicio registra la cabecera y los detalles dentro de una transacción.
9. Si todas las operaciones terminan correctamente, confirma la transacción y la interfaz muestra un mensaje de éxito. Si ocurre un error, intenta revertir la operación y muestra un mensaje de fallo.

## 5. Relación con la base de datos

El módulo trabaja principalmente con las siguientes tablas:

- **`Compra`**: almacena la cabecera de la compra, incluyendo proveedor, usuario, fecha, comprobante, total y estado.
- **`Detalle_Compra`**: almacena los productos asociados a cada compra, incluyendo cantidad, precio, lote y vencimiento.

La cabecera se registra primero para obtener el identificador que se asigna a cada detalle. Ambas partes se guardan dentro de la misma transacción.

## 6. Relación con el módulo Recepción

El módulo Compra registra la adquisición y sus detalles. Posteriormente, el módulo Recepción utiliza el identificador `idCompra` para relacionar la recepción de los productos con la compra correspondiente.

La clase `Recepcion` contiene los atributos `idRecepcion`, `idCompra`, `idUsuario`, `fechaRecepcion`, `estado` y `observacion`. Estos permiten identificar la recepción, la compra relacionada, el usuario responsable y la información del proceso de recepción.

Recepción actúa como puente entre Compra e Inventario. El registro de la compra, por sí solo, no significa que los productos ya hayan ingresado al inventario.

## 7. Consideraciones técnicas

- Se utiliza `BigDecimal` para representar importes y precios.
- Se utiliza `LocalDateTime` para la fecha de compra y `LocalDate` para el vencimiento.
- Las inserciones de la cabecera y los detalles comparten una transacción.
- El número de comprobante se verifica considerando únicamente las compras activas.
- Los detalles pueden recibir un lote generado automáticamente cuando no se proporciona uno.
- El método `existeNroComprobante` devuelve `false` si ocurre una excepción SQL. Por ello, un error de conexión podría interpretarse como si el comprobante no existiera; este comportamiento debe tenerse en cuenta al evaluar la validación.

## 8. Conclusión

El módulo Compra organiza el registro de las adquisiciones de la ferretería mediante una separación de responsabilidades entre modelo, acceso a datos, servicio, controlador e interfaz gráfica. Registra la cabecera y sus detalles de forma transaccional y se relaciona con Recepción como paso posterior para vincular la compra con el inventario.
