# Módulo de Caja — Ferretería Colorín

## 1. Descripción general

El módulo de Caja administra la apertura y el cierre de caja, registra los ingresos generados por las ventas y permite consultar los movimientos de dinero durante la jornada.

Controla el monto inicial, los ingresos, las salidas registradas, el efectivo esperado y la diferencia entre el monto esperado y el dinero contado al cierre.

Actualmente, la integración se centra principalmente en las ventas. El registro de pagos a proveedores y otros gastos queda pendiente.

## 2. Arquitectura del módulo

El módulo utiliza una arquitectura por capas para separar la representación de los datos, el acceso a la base de datos, las reglas de negocio y la interfaz gráfica.

| Componente | Clase | Responsabilidad |
|---|---|---|
| Modelo | `Caja.java` | Representa la sesión de caja y sus totales. |
| Modelo | `MovimientoCaja.java` | Representa los ingresos y las salidas de dinero. |
| DAO | `CajaDAO.java` | Gestiona las operaciones sobre la tabla `Caja`. |
| DAO | `MovimientoCajaDAO.java` | Registra y consulta los movimientos monetarios. |
| Servicio | `CajaService.java` | Valida las operaciones y coordina las transacciones. |
| Vista | `MainForm.java` | Permite acceder a la gestión de caja. |
| Vista | `CajaDialog.java` | Muestra los datos, los movimientos y las opciones de cierre. |

La gestión se realiza desde `MainForm` y `CajaDialog`. No se ha identificado una clase independiente `CajaController`.

## 3. Modelos

### 3.1. Caja.java

**Paquete:** `pe.ferreteria.modelo`

Representa una sesión de caja e incluye:

- Identificador de caja y usuarios de apertura y cierre.
- Fechas de apertura y cierre.
- Monto de apertura.
- Total de ingresos y salidas.
- Monto esperado, monto contado y diferencia.
- Estado (`Abierta` o `Cerrada`) y observación del cierre.

Los importes utilizan `BigDecimal` y las fechas, `LocalDateTime`.

### 3.2. MovimientoCaja.java

Representa cada movimiento monetario asociado a una caja.

Sus principales atributos son el identificador del movimiento, caja, usuario, fecha, tipo (`Ingreso` o `Salida`), concepto, monto y método de pago.

También incluye referencias opcionales a ventas, compras u otras operaciones, número de comprobante, descripción y estado activo.

Este modelo permite identificar el origen de los movimientos y relacionarlos con las operaciones del sistema.

## 4. Acceso a datos (DAO)

### 4.1. CajaDAO.java

Gestiona las operaciones de persistencia de la tabla `Caja`.

| Método | Función |
|---|---|
| `insertar(Connection, Caja)` | Registra una apertura utilizando una conexión existente. |
| `buscarPorId(int)` | Busca una caja por identificador. |
| `buscarCajaAbierta()` | Busca una caja abierta. |
| `buscarCajaAbierta(Connection)` | Busca una caja abierta utilizando una conexión existente. |
| `buscarCajaAbiertaPorUsuario(int)` | Consulta la caja abierta asociada a un usuario. |
| `listar()` | Obtiene las cajas registradas. |
| `actualizarTotales(Connection, ...)` | Actualiza los totales mientras la caja continúa abierta. |
| `cerrarCaja(Connection, ...)` | Registra el cierre y cambia el estado de la caja. |

### 4.2. MovimientoCajaDAO.java

Administra los movimientos monetarios de la caja.

Sus operaciones principales permiten insertar movimientos, listar los movimientos activos de una caja, calcular los totales de ingresos y salidas y consultar devoluciones relacionadas con registros de Kardex defectuoso.

Los métodos de cálculo consideran únicamente movimientos activos. Ambos DAO disponen de operaciones que reciben una conexión existente para participar en transacciones.

## 5. Reglas de negocio: CajaService.java

`CajaService` centraliza las operaciones de apertura, consulta, registro de movimientos y cierre.

Sus principales métodos son:

- `registrarApertura(Caja)`: valida los datos e inicializa los totales.
- `buscarCajaAbierta()` y `buscarCajaAbiertaPorUsuario(int)`: consultan cajas abiertas.
- `buscarPorId(int)` y `listar()`: consultan las cajas registradas.
- `registrarMovimiento(MovimientoCaja)`: valida y registra un movimiento dentro de una transacción.
- `listarMovimientosPorCaja(int)`: obtiene los movimientos de una caja.
- `cerrarCaja(...)`: valida los datos y registra el cierre.

Durante la apertura, el servicio comprueba que el usuario sea válido y que el monto inicial no sea nulo ni negativo. Los totales comienzan en cero y el monto esperado es igual al monto de apertura.

El registro de movimientos valida los identificadores y exige un importe positivo.

**Consideración técnica:** `registrarMovimiento()` inserta el movimiento, pero no actualiza por sí mismo los totales de caja. La integración con Ventas realiza ese cálculo desde `VentaService`.

## 6. Interfaz gráfica

### 6.1. MainForm.java

Es el punto de acceso a la gestión de caja.

El método `abrirGestionCaja()` comprueba si existe una caja abierta. Si no existe, solicita el monto inicial, valida que sea numérico y no negativo y registra la apertura mediante `CajaService`.

Además, `puedeSalir()` impide que un usuario salga de la aplicación mientras tenga una caja abierta asociada a su usuario. El botón de Caja se oculta para el rol `Almacen`.

### 6.2. CajaDialog.java

Muestra la información de la caja seleccionada, sus movimientos y las opciones de cierre.

Presenta el identificador, el usuario y la fecha de apertura, el monto inicial, los ingresos, las salidas, el monto esperado y la tabla de movimientos.

Sus principales acciones son:

- **Actualizar:** vuelve a consultar la información de la caja y sus movimientos.
- **Cerrar Caja:** solicita el monto contado y una observación opcional, calcula la diferencia y solicita el cierre al servicio.

La diferencia se calcula de la siguiente manera:

`Diferencia = Monto contado - Monto esperado`

Una diferencia positiva indica un excedente; una negativa, un faltante; y cero significa que ambos importes coinciden.

## 7. Base de datos

### 7.1. Tabla `Caja`

Almacena las sesiones de caja, los usuarios responsables de la apertura y el cierre, las fechas, los importes y el estado.

`id_caja` es la clave primaria. Los usuarios se relacionan mediante claves foráneas, y el usuario de cierre es opcional. Los importes principales utilizan `DECIMAL(10,2)` y la estructura contempla restricciones para evitar valores negativos en los campos correspondientes.

### 7.2. Tabla `Movimiento_Caja`

Almacena los ingresos y las salidas de dinero.

Incluye referencias a la caja y al usuario, tipo de movimiento, concepto, importe, método de pago, referencias opcionales a otras operaciones, número de comprobante, descripción y estado activo.

El importe debe ser mayor que cero. Los movimientos pueden desactivarse mediante `activo`, y los cálculos de ingresos y salidas consideran solo los movimientos activos.

## 8. Integración con Ventas

La integración se realiza en `VentaService.registrarVentaCompleta(Venta, List<DetalleVenta>)`.

Dentro de una misma transacción, el servicio:

1. Comprueba que exista una caja abierta.
2. Registra la venta y sus detalles.
3. Actualiza el inventario y registra las salidas correspondientes en Kardex.
4. Inserta un movimiento de tipo `Ingreso` y concepto `Venta`, asociado a la caja, el usuario y la venta.
5. Calcula los ingresos, las salidas y el monto esperado.
6. Actualiza los totales de caja.
7. Confirma los cambios mediante `commit()`.

Si ocurre un error, ejecuta `rollback()` para revertir los cambios incluidos en esa transacción.

La regla principal es que **no se puede completar una venta cobrada si no existe una caja abierta**.

## 9. Reglas y fórmulas de caja

Las reglas principales son:

- El monto de apertura no puede ser negativo.
- Los movimientos deben registrar importes positivos.
- Los ingresos y las salidas se calculan considerando los movimientos activos.
- El monto esperado se actualiza a partir de la apertura, los ingresos y las salidas.
- Al cerrar la caja se registra el monto contado, la diferencia, el usuario de cierre y la fecha.
- El cierre cambia el estado de la caja a `Cerrada`.
- La actualización de los totales está condicionada a que la caja permanezca abierta.

**Monto esperado**

`Monto esperado = Monto de apertura + Total de ingresos - Total de salidas`

**Diferencia de cierre**

`Diferencia = Monto contado - Monto esperado`

## 10. Flujo de funcionamiento

1. El usuario accede a la gestión de Caja.
2. El sistema busca una caja abierta; si no existe, solicita el monto inicial y registra la apertura.
3. El usuario registra ventas desde el módulo correspondiente.
4. Cada venta completada genera un ingreso y actualiza los totales de caja.
5. El usuario consulta los movimientos desde `CajaDialog`.
6. Al finalizar, introduce el dinero contado y una observación opcional.
7. El sistema calcula la diferencia y registra el cierre.

## 11. Funcionalidades pendientes y consideraciones técnicas

- Integrar las salidas de dinero por pagos a proveedores y gastos cuando se implementen esos procesos.
- Probar varias ventas consecutivas y cierres con faltantes, excedentes o diferencias iguales a cero.
- Revisar el tratamiento de ventas anuladas, devoluciones y reversiones para mantener coherentes los movimientos de caja.
- Verificar que no se produzcan aperturas simultáneas no deseadas y que la selección de la caja abierta respete las reglas por usuario.
- Comprobar el comportamiento ante errores durante el registro de movimientos y el cierre.

La existencia de `obtenerTotalDevolucionDefectuoso()` no implica que todas las devoluciones estén integradas con Caja; esto debe verificarse en el código correspondiente.

## 12. Conclusión

El módulo Caja permite controlar la apertura, los ingresos por ventas, los movimientos registrados y el cierre de caja. Su integración transaccional con Ventas relaciona las operaciones comerciales con los ingresos monetarios.

La estructura permite ampliar el control hacia pagos a proveedores, gastos y otros movimientos cuando esas funcionalidades se incorporen al sistema.