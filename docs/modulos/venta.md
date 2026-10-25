# Módulo de Ventas — Ferretería Colorín

## 1. Descripción general

El módulo de Ventas permite registrar las operaciones comerciales de la ferretería, gestionar los productos vendidos por lote, calcular los importes, controlar el stock y generar comprobantes en formato PDF.

También proporciona consultas y reportes para analizar las ventas, los productos más vendidos, los métodos de pago y el rendimiento de los vendedores.

## 2. Estructura del módulo

El módulo utiliza una arquitectura por capas que separa los modelos, el acceso a datos, las reglas de negocio y la interfaz gráfica.

| Capa | Clase | Responsabilidad |
|---|---|---|
| Modelo | `Venta.java` | Representa la información general de una venta. |
| Modelo | `DetalleVenta.java` | Representa los productos y cantidades incluidos en la venta. |
| DAO | `VentaDAO.java` | Gestiona las consultas y operaciones sobre la tabla `Venta`. |
| DAO | `DetalleVentaDAO.java` | Gestiona los detalles asociados a cada venta. |
| Servicio | `VentaService.java` | Aplica las reglas de negocio y coordina el registro de ventas. |
| Controlador | `VentaController.java` | Intermedia entre la vista y el servicio. |
| Vista | `VentaPanel.java` | Permite preparar, registrar y consultar ventas. |
| Utilidad | `GeneradorComprobante.java` | Organiza los datos del comprobante. |
| Utilidad | `GeneradorPDF.java` | Genera el comprobante en formato PDF. |

## 3. Modelos

### 3.1. Venta.java

**Paquete:** `pe.ferreteria.modelo`

Representa la información general de la venta.

Sus principales atributos son:

- Identificadores de la venta, cliente y usuario.
- Fecha y hora de la operación.
- Subtotal, IGV y total.
- Tipo, serie y número del comprobante.
- Método de pago, monto recibido y vuelto.
- Estado del comprobante y campos relacionados con SUNAT.
- Estado activo del registro.

Utiliza `BigDecimal` para los importes monetarios y `LocalDateTime` para la fecha y hora. Incluye constructores, *getters* y *setters*.

### 3.2. DetalleVenta.java

Representa cada producto incluido en una venta y lo relaciona con un registro específico de inventario, permitiendo identificar el lote vendido.

Sus atributos principales son `idVenta`, `idInventario`, `cantidad`, `precioUnitario`, `subtotal`, `descuento` e `importeTotal`.

Utiliza `BigDecimal` para los importes. En la base de datos, `subtotal` e `importe_total` son columnas generadas por MySQL a partir de la cantidad, el precio unitario y el descuento.

## 4. Acceso a datos (DAO)

### 4.1. VentaDAO.java

**Paquete:** `pe.ferreteria.dao`

Gestiona las operaciones de la tabla `Venta`. Sus principales funciones son:

- Registrar una venta y recuperar el identificador generado.
- Registrar utilizando una conexión existente para participar en una transacción.
- Obtener el siguiente número correlativo según la serie del comprobante.
- Buscar ventas por identificador.
- Consultar ventas por fecha, periodo o usuario.
- Obtener resúmenes de ventas, rendimiento de vendedores y métodos de pago.
- Consultar la evolución de las ventas y otros indicadores.

### 4.2. DetalleVentaDAO.java

**Paquete:** `pe.ferreteria.dao`

Gestiona la tabla `Detalle_Venta` y permite:

- Registrar los detalles de una venta.
- Registrar detalles utilizando una conexión existente.
- Consultar los productos asociados a una venta.
- Buscar detalles por venta e inventario.
- Obtener información para reportes de productos más vendidos, cantidades, importes y ganancias brutas estimadas.

Ambos DAO utilizan consultas parametrizadas y operaciones de acceso a MySQL. Los métodos que reciben una conexión existente permiten que varias operaciones formen parte de una misma transacción.

## 5. Reglas de negocio: VentaService.java

**Paquete:** `pe.ferreteria.servicio`

`VentaService` es el componente central del módulo. Su método principal es `registrarVentaCompleta(Venta, List<DetalleVenta>)`, encargado de coordinar el registro de la venta y sus operaciones relacionadas.

### Principales reglas

1. **Validación de datos:** comprueba la información necesaria para registrar la venta.
2. **Caja abierta:** exige que exista una caja abierta antes de completar la operación.
3. **Comprobante:** utiliza las series `B001` para boletas y `F001` para facturas, y obtiene el correlativo correspondiente.
4. **Inventario:** verifica la existencia del lote y la disponibilidad de stock suficiente.
5. **Precios y descuentos:** utiliza el precio de venta del lote y aplica el descuento configurado cuando corresponde.
6. **Desglose del IGV:** considera que el precio de venta ya incluye el IGV del 18 %. Por ello, desglosa el impuesto del importe total, sin añadirlo nuevamente. El IGV se obtiene mediante la fórmula IGV = Total × 18 / 118.
7. **Pago en efectivo:** verifica que el monto recibido sea suficiente y calcula el vuelto.
8. **Registro de la venta:** guarda la cabecera y los detalles.
9. **Actualización de existencias:** descuenta las cantidades vendidas del inventario correspondiente.
10. **Integración con Kardex y Caja:** registra las salidas de inventario y el movimiento de ingreso de dinero asociado a la venta.
11. **Control transaccional:** utiliza `commit` para confirmar la operación y `rollback` para revertir los cambios incluidos en la transacción cuando ocurre un error.

El servicio también coordina las consultas y reportes de ventas, vendedores, métodos de pago y productos comercializados.

## 6. Controlador: VentaController.java

**Paquete:** `pe.ferreteria.controlador`

Actúa como intermediario entre la interfaz gráfica y `VentaService`.

Sus responsabilidades incluyen solicitar el registro de ventas, consultar una venta por identificador, recuperar sus detalles y facilitar el acceso a las consultas y reportes disponibles.

Las reglas de negocio y las operaciones principales de registro corresponden al servicio, mientras que el acceso SQL pertenece a los DAO.

## 7. Interfaz gráfica: VentaPanel.java

**Paquete:** `pe.ferreteria.vista`

`VentaPanel` utiliza Java Swing para gestionar el proceso de venta.

### Funciones principales

- **Clientes:** permite buscar y seleccionar clientes mediante datos como DNI/RUC, nombre, contacto o teléfono.
- **Productos:** permite filtrar por nombre, lote, marca y categoría, y consultar el stock disponible por lote y el stock total del producto.
- **Detalle de venta:** permite agregar productos, acumular cantidades del mismo lote y retirar productos antes del cobro.
- **Importes:** muestra precios, descuentos, subtotal, IGV y total.
- **Pago:** valida los datos del cobro y calcula el vuelto para pagos en efectivo.
- **Usuario responsable:** obtiene la identidad del usuario mediante `SesionUsuario`.
- **Registro:** envía la venta y sus detalles al controlador. Si la operación tiene éxito, actualiza la información del inventario y limpia el formulario.
- **Comprobante PDF:** genera automáticamente el comprobante después del registro exitoso y pregunta si el usuario desea abrirlo para imprimirlo. Abrir el archivo es opcional.

## 8. Integración con otros módulos

| Módulo o componente | Relación con Ventas |
|---|---|
| Clientes | Identifica al cliente de la operación. |
| Usuarios y sesión | Identifica al usuario que registra la venta. |
| Inventario y Productos | Proporcionan los productos y lotes disponibles; el stock se reduce al vender. |
| Marcas y Categorías | Permiten clasificar y filtrar productos. |
| Kardex | Registra los movimientos de salida relacionados con las ventas. |
| Caja | Verifica que exista una caja abierta y registra el ingreso de dinero. |
| Generación de comprobantes | Prepara los datos y genera el archivo PDF. |

### Relaciones principales en la base de datos

- `Venta.id_cliente` → `Cliente.id_cliente`.
- `Venta.id_usuario` → `Usuario.id_usuario`.
- `Detalle_Venta.id_venta` → `Venta.id_venta`.
- `Detalle_Venta.id_inventario` → `Inventario.id_inventario`.
- `Inventario.id_producto` → `Producto.id_producto`.
- `Movimiento_Caja.id_venta` → `Venta.id_venta`.
- `Movimiento_Caja.id_caja` → `Caja.id_caja`.

`Kardex` registra movimientos relacionados con el producto, el inventario, el usuario y la operación de referencia. En el caso de `Kardex.id_referencia`, se utiliza el tipo y el identificador de la operación sin una clave foránea directa hacia `Venta`.

## 9. Reportes de ventas

Los DAO, coordinados por `VentaService`, proporcionan consultas para analizar el comportamiento comercial.

- **Resumen de ventas:** total vendido, número de ventas, IGV, ganancia bruta estimada y ticket promedio.
- **Ventas por periodo:** consultas por día, mes o año.
- **Ventas por usuario:** operaciones asociadas a cada vendedor.
- **Rendimiento de vendedores:** información para comparar su desempeño.
- **Métodos de pago:** ventas agrupadas según el medio utilizado.
- **Evolución temporal:** comportamiento de las ventas a lo largo del tiempo.
- **Productos más vendidos:** cantidades, importes y ganancias brutas estimadas.
- **Detalle de ventas:** productos incluidos en cada operación.

## 10. Flujo general del módulo

1. El usuario ingresa a Ventas y selecciona un cliente.
2. Busca los productos y agrega las cantidades por lote.
3. La interfaz presenta el resumen de la operación y calcula los importes.
4. El usuario selecciona el comprobante y el método de pago.
5. `VentaController` envía la solicitud a `VentaService`.
6. El servicio valida la caja, el stock, los precios, los descuentos y el pago.
7. Se registra la venta y sus detalles, se actualiza el inventario y se registran los movimientos correspondientes.
8. Si la operación termina correctamente, se confirma la transacción.
9. La interfaz actualiza los datos y genera el comprobante PDF.

Si ocurre un error durante la transacción, se ejecuta `rollback` para revertir los cambios incluidos en ella.

## 11. Consideraciones técnicas

- **Precisión monetaria:** se utiliza `BigDecimal` para los importes.
- **Control por lote:** cada detalle de venta referencia un registro específico de inventario.
- **Transacciones:** el servicio coordina las operaciones principales mediante `commit` y `rollback`.
- **Numeración:** se utilizan series diferenciadas para boletas y facturas.
- **PDF:** `GeneradorComprobante` prepara la información y `GeneradorPDF` genera el documento en formato A6 vertical.
- **Organización de archivos:** los comprobantes se guardan en carpetas organizadas por año y mes.
- **Tienda web:** la tabla `Venta` contempla el campo `costo_envio`, pero su implementación en el proceso de compra web está pendiente.
- **SUNAT:** los campos de estado y respuesta permiten almacenar información relacionada con SUNAT, pero no demuestran por sí solos que la integración electrónica esté implementada y operativa.

## 12. Conclusión

El módulo de Ventas permite registrar operaciones comerciales, calcular importes, controlar el stock por lote, registrar movimientos de inventario y caja, y generar comprobantes PDF.

Además, sus consultas proporcionan información útil para analizar las ventas y el desempeño comercial. La arquitectura por capas y el control transaccional favorecen la organización del código y ayudan a prevenir registros incompletos ante errores.