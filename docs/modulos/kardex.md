# Módulo Kardex

## 1. Descripción general

El módulo Kardex registra y consulta los movimientos de inventario de la Ferretería Colorín. Permite mantener la trazabilidad de las entradas, salidas, ajustes y reversiones, incluyendo las cantidades y los niveles de stock antes y después de cada movimiento.

También permite gestionar productos defectuosos, registrar reposiciones, consultar devoluciones pendientes y generar reportes de mermas.

## 2. Modelo: `Kardex.java`

Representa los movimientos registrados en el Kardex.

Contiene información relacionada con:

- Identificadores del movimiento, producto, lote de inventario y usuario.
- Fecha, tipo de movimiento, motivo y cantidad.
- Stock normal y defectuoso antes y después del movimiento.
- Precio unitario y monto total.
- Tipo e identificador de referencia, comprobante y observación.

El modelo permite relacionar los movimientos con los productos, lotes y operaciones que los originaron.

## 3. Acceso a datos: `KardexDAO.java`

Gestiona las operaciones de consulta y registro en la base de datos.

Sus principales funciones son:

- Registrar movimientos utilizando una conexión propia o una conexión transaccional.
- Consultar el historial y buscar movimientos por identificador, producto o lote.
- Recuperar información del stock, usuario, comprobante y referencias asociadas.
- Consultar cantidades relacionadas con productos defectuosos, retiros, reingresos y reposiciones.
- Comprobar si una venta ya tiene una reversión registrada para un lote.
- Obtener reportes de mermas con filtros por fecha y usuario.
- Generar información gerencial sobre productos, marcas, motivos, cantidades y costos de las mermas.

## 4. Lógica de negocio: `KardexService.java`

Contiene las operaciones principales del módulo.

### Registro y consulta
- Registra movimientos del Kardex.
- Consulta el historial de movimientos por producto y lote.
- Realiza ajustes manuales del stock normal y registra la diferencia correspondiente.

### Reversión de ventas
- Revierte salidas asociadas a ventas y restaura el stock correspondiente.
- Registra el movimiento de reversión.
- Comprueba si la reversión ya existe para evitar repetirla.

### Gestión de productos defectuosos
- Registra productos defectuosos.
- Permite retirar y reingresar unidades defectuosas.
- Gestiona reposiciones al cliente, descontando el stock normal y registrando la salida correspondiente.
- Calcula las unidades pendientes de resolución mediante reposición o devolución de dinero.

### Devolución de dinero
Registra el movimiento de dinero en Caja y actualiza los totales correspondientes. Esta operación no modifica directamente el Kardex.

### Reportes
Proporciona información para los reportes de mermas y consultas relacionadas con los movimientos registrados.

## 5. Controlador: `KardexController.java`

Actúa como intermediario entre la vista y el servicio.

Expone las operaciones para:

- Registrar movimientos y consultar el historial.
- Revertir ventas y realizar ajustes de stock.
- Gestionar productos defectuosos.
- Registrar reposiciones y devoluciones de dinero.
- Consultar cantidades pendientes de resolución.
- Obtener reportes de mermas.

## 6. Vista: `KardexPanel.java`

Proporciona la interfaz gráfica para consultar y gestionar el Kardex.

### Historial de movimientos
Muestra información como fecha, producto, lote, movimiento, motivo, cantidad, stock anterior y posterior, stock defectuoso posterior, origen, comprobante y usuario.

Incluye búsqueda por producto, lote o comprobante, además de filtros por tipo de movimiento, motivo y usuario.

### Detalle del movimiento
Permite consultar información ampliada, como precio, monto, descuento y observación. También permite abrir documentos PDF asociados cuando corresponde.

### Reversión de ventas
Permite solicitar la reversión de una venta, validar la información requerida y registrar una observación obligatoria que justifique la operación.

El método `revertirVenta()` solicita confirmación antes de continuar. Se eliminó la comprobación redundante de `JOptionPane.YES_OPTION`, ya que la comprobación anterior con `JOptionPane.OK_OPTION` es suficiente para el tipo de diálogo utilizado.

La reversión se procesa mediante el servicio, que restaura el stock correspondiente y registra el movimiento en el Kardex.

### Productos defectuosos y devoluciones
Permite registrar unidades defectuosas, validar cantidades y motivos, generar etiquetas PDF y gestionar resoluciones parciales o completas mediante reposición o devolución monetaria.

Para la devolución de dinero, permite seleccionar el método de pago: Efectivo, Tarjeta, Transferencia o Yape.

También permite consultar documentos PDF asociados a ventas, reversiones y etiquetas de productos defectuosos.

## 7. Integración con otros módulos

### Recepción e inventario

El método `RecepcionService.completarRecepcionConInventarioYKardex()` integra la recepción con el inventario y el Kardex mediante una transacción.

El proceso realiza las siguientes operaciones:

1. Crea el lote de inventario con la cantidad recibida, el proveedor y los precios correspondientes.
2. Registra o actualiza el detalle de recepción.
3. Registra un movimiento de entrada por compra, con stock anterior igual a cero y stock posterior igual a la cantidad recibida.
4. Actualiza el estado de la recepción según sus detalles.

Si ocurre un error durante la transacción, se ejecuta `rollback()` para revertir las operaciones de esa transacción.

El precio de venta utilizado en este flujo se calcula aplicando un incremento del 20 % al precio de compra.

### Ventas

El Kardex registra las salidas relacionadas con las ventas y permite revertirlas cuando corresponde, restaurando el stock asociado a los lotes afectados.

### Caja

Las devoluciones de dinero por productos defectuosos se integran con Caja para registrar el movimiento monetario y actualizar sus totales.

### Productos, marcas y usuarios

El Kardex utiliza las referencias correspondientes para identificar los productos, sus lotes y los usuarios asociados a los movimientos.

## 8. Flujo general

1. Una operación de compra, venta, ajuste o gestión de defectuosos genera el movimiento correspondiente cuando lo establece la lógica del sistema.
2. El servicio coordina las operaciones necesarias con los módulos involucrados.
3. El DAO registra o consulta la información en la base de datos.
4. La vista permite consultar el historial, revisar detalles, aplicar filtros y ejecutar las operaciones disponibles.
5. Los reportes permiten consultar información relacionada con las mermas y los movimientos de inventario.

## 9. Consideraciones técnicas pendientes

- **Devoluciones monetarias:** verificar que el cálculo del reembolso por productos defectuosos considere el precio efectivamente pagado por el cliente, incluidos los descuentos aplicados en la venta.
- **Control de stock:** comprobar las validaciones de cantidades y el control de concurrencia para evitar inconsistencias cuando varias operaciones afectan al mismo lote.

Estas verificaciones quedan pendientes y no implican, por sí solas, que exista un error funcional confirmado.

## 10. Resultado

El módulo Kardex centraliza la trazabilidad de los movimientos de inventario, las reversiones de ventas y la gestión de productos defectuosos. Su integración con Recepción, Inventario, Ventas y Caja permite relacionar los movimientos físicos con las operaciones que los originan y registrar las devoluciones monetarias correspondientes.
