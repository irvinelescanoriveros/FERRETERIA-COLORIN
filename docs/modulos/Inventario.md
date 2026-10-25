# Módulo de Inventario — Ferretería Colorín

## 1. Descripción general

El módulo de Inventario permite administrar las existencias de la ferretería mediante el control de lotes, cantidades disponibles, productos defectuosos, precios, descuentos, fechas de vencimiento y ubicación física en el almacén.

También facilita la identificación de productos con stock bajo, próximos a vencer y con poca rotación, contribuyendo a una mejor gestión de la mercadería.

## 2. Estructura del módulo

El módulo utiliza una arquitectura por capas que separa los datos, el acceso a la base de datos, las reglas de negocio y la interfaz gráfica.

| Capa | Clase | Responsabilidad |
|---|---|---|
| Modelo | `Inventario.java` | Representa los registros de inventario. |
| DAO | `InventarioDAO.java` | Ejecuta consultas y operaciones sobre la tabla `Inventario`. |
| Servicio | `InventarioService.java` | Aplica validaciones y reglas de negocio. |
| Controlador | `InventarioController.java` | Coordina las solicitudes entre la vista y el servicio. |
| Vista | `InventarioPanel.java` | Presenta la información y permite gestionar las existencias. |

## 3. Modelo: Inventario.java

**Paquete:** `pe.ferreteria.modelo`

La clase `Inventario` representa las existencias de un producto asociado a un proveedor y un lote específico.

Sus atributos principales son:

- Identificación del inventario, producto y proveedor.
- Lote y cantidades de stock normal y defectuoso.
- Precios de compra y venta.
- Estado y porcentaje del descuento.
- Fecha de ingreso y fecha de vencimiento.
- Ubicación física mediante estante y nivel.
- Estado activo del registro.

Incluye un constructor vacío y métodos *getters* y *setters*.

Para los valores monetarios y porcentajes utiliza `BigDecimal`, mientras que las fechas se representan mediante `LocalDateTime` y `LocalDate`.

## 4. Acceso a datos: InventarioDAO.java

**Paquete:** `pe.ferreteria.dao`

`InventarioDAO` centraliza las operaciones SQL sobre la tabla `Inventario`, utilizando conexiones administradas por `ConexionBD` y consultas parametrizadas mediante `PreparedStatement`.

### Operaciones principales

| Método | Función |
|---|---|
| `listar()` | Recupera los registros activos de inventario. |
| `insertar(Inventario)` | Registra existencias y devuelve el identificador generado. |
| `insertar(Connection, Inventario)` | Inserta un registro usando una conexión existente. |
| `actualizarUbicacion(...)` | Modifica el estante y el nivel. |
| `actualizarStock(...)` | Actualiza la cantidad disponible. |
| `reducirStock(...)` / `aumentarStock(...)` | Reduce o incrementa el stock de un registro activo. |
| `actualizarStockDefectuoso(...)` | Modifica la cantidad de unidades defectuosas. |
| `actualizarDescuento(...)` | Actualiza el estado y porcentaje del descuento. |
| `buscarPorId(...)` / `buscarParaDashboard(...)` | Consulta registros de inventario. |
| `listarRotacionParaReporte(...)` | Obtiene información sobre la rotación de productos. |
| `obtenerStockDefectuosoActual()` | Calcula el total de unidades defectuosas activas. |

Los métodos devuelven resultados según la operación, como listas, identificadores, objetos, valores booleanos o excepciones. Los métodos que reciben una conexión permiten participar en operaciones transaccionales administradas desde otra capa.

## 5. Reglas de negocio: InventarioService.java

**Paquete:** `pe.ferreteria.servicio`

`InventarioService` valida los datos y coordina las operaciones del inventario.

- **Registro:** recibe información de `DetalleCompra`, `Compra` y `Producto` para preparar el registro del lote, incluyendo cantidades, proveedor, precios y vencimiento.
- **Precio de venta:** calcula el precio aplicando un incremento del 20 % al precio de compra. En la variante que recibe una conexión, un precio de compra nulo se sustituye por cero.
- **Ubicación:** valida el identificador y que el estante y nivel no estén vacíos; luego limpia los espacios de los textos.
- **Ajuste de stock:** valida el identificador, la cantidad no negativa y la existencia de una sesión de usuario activa.
- **Descuentos:** permite activar descuentos con porcentajes mayores que 0 y menores o iguales a 100, redondeados a dos decimales, o desactivarlos estableciendo el porcentaje en cero.
- **Productos defectuosos:** valida la cantidad solicitada, la sesión activa y que el retiro no supere las unidades defectuosas disponibles.
- **Consultas:** proporciona datos para el panel, el reporte de rotación y el cálculo de unidades defectuosas.

## 6. Controlador: InventarioController.java

**Paquete:** `pe.ferreteria.controlador`

`InventarioController` conecta la interfaz con `InventarioService`. Expone las operaciones para listar y registrar existencias, modificar ubicaciones y stock, gestionar descuentos, retirar unidades defectuosas y consultar información para reportes.

No ejecuta directamente consultas SQL, ya que esa responsabilidad corresponde al DAO.

## 7. Interfaz gráfica: InventarioPanel.java

**Paquete:** `pe.ferreteria.vista`

`InventarioPanel` utiliza Swing para mostrar las existencias y ejecutar las acciones del módulo. Al inicializarse, carga los datos de inventario y la información relacionada con productos, categorías, marcas y proveedores.

### Funciones principales

- **Tabla de inventario:** muestra lote, producto, categoría, marca, proveedor, stock del lote y total del producto, precios, vencimiento, ubicación, unidades defectuosas y descuentos. Las tablas no permiten editar directamente sus celdas.
- **Búsqueda y filtros:** permite buscar por nombre y filtrar por categoría y marca.
- **Stock bajo:** identifica productos cuyo stock total es menor o igual a `2` unidades. El total se obtiene sumando las cantidades de sus diferentes lotes.
- **Vencimientos:** muestra lotes vencidos o que vencerán dentro de los próximos 90 días. Indica el estado mediante textos como «Vencido», «Hoy» o los días restantes. Excluye registros sin fecha de vencimiento.
- **Cambio de ubicación:** permite modificar el estante y el nivel del lote seleccionado, con validaciones en la interfaz.
- **Ajuste de stock:** permite retirar unidades del stock normal, solicitando motivo y observación. Valida la cantidad y utiliza `KardexController` para registrar el movimiento.
- **Descuentos:** permite activar o desactivar el descuento de un lote, validando el porcentaje y solicitando confirmación cuando corresponde.
- **Retiro de defectuosos:** consulta los movimientos de Kardex, permite seleccionar el ingreso correspondiente y valida las unidades que todavía pueden retirarse.
- **Reingreso de defectuosos:** permite devolver unidades al stock normal considerando los retiros y reingresos anteriores. Solicita cantidad, motivo y observación antes de confirmar la operación.

Después de las operaciones exitosas, el panel actualiza los datos en memoria y refresca las tablas para mostrar los cambios.

## 8. Integración con otros módulos

- **Compras y Recepción:** proporcionan la información necesaria para incorporar al inventario las existencias recibidas por lote.
- **Kardex:** registra los movimientos relacionados con ajustes de stock, retiros de unidades defectuosas y reingresos.
- **Productos, Categorías, Marcas y Proveedores:** proporcionan los datos descriptivos utilizados para identificar la mercadería en la interfaz.
- **Ventas:** aporta información de `Detalle_Venta` y `Venta` para analizar la rotación. El proceso de venta se mantiene en su propio módulo.

## 9. Reporte de rotación

El método `listarRotacionParaReporte(...)` obtiene información del producto y su lote, incluyendo marca, antigüedad, stock y unidades vendidas.

Permite aplicar filtros por día, mes y usuario, junto con el año requerido. Considera también productos sin ventas registradas y selecciona aquellos cuya cantidad vendida es menor o igual a dos unidades.

Los resultados se clasifican en:

- **Sin movimiento:** no registra unidades vendidas en la consulta.
- **Bajo movimiento:** registra una o dos unidades vendidas.

Este reporte ayuda a detectar mercadería con poca salida y facilita el análisis de las existencias.

## 10. Flujo general del módulo

1. El usuario realiza una consulta o acción desde `InventarioPanel`.
2. La vista solicita la operación a `InventarioController`.
3. El controlador delega la solicitud a `InventarioService`.
4. El servicio valida los datos y solicita la operación al DAO.
5. `InventarioDAO` ejecuta la consulta o actualización en la base de datos.
6. El resultado regresa a las capas superiores y, si la operación tiene éxito, la interfaz refresca la información.

En las operaciones relacionadas con movimientos de stock y productos defectuosos, el panel también utiliza `KardexController`.

## 11. Consideraciones técnicas

- El listado principal recupera registros activos.
- El stock total de un producto corresponde a la suma del stock de sus lotes.
- El stock defectuoso se administra por separado del stock normal.
- La fecha de vencimiento puede ser nula.
- Las validaciones se distribuyen entre la interfaz y el servicio.
- El DAO dispone de métodos que reciben una conexión existente para operaciones transaccionales.
- La consulta de rotación utiliza información de ventas, pero no reemplaza la lógica del módulo Venta.

## 12. Conclusión

El módulo de Inventario permite gestionar existencias por lote, controlar precios, descuentos, ubicaciones y productos defectuosos, además de consultar alertas de stock bajo, vencimientos y rotación.

Su integración con Kardex facilita el seguimiento de los movimientos de inventario, mientras que la arquitectura por capas separa las responsabilidades y favorece el mantenimiento del sistema.