# Manual de Usuario
## Sistema de Gestión de Inventario y Ventas para Ferretería Colorín (SGIVFC)

**Versión:** 1.0  
**Institución:** Universidad Tecnológica del Perú (UTP)  
**Proyecto:** Integrador I

## 1. Introducción

El SGIVFC permite gestionar los productos, proveedores, compras, recepción de mercadería, inventario, ventas, caja y movimientos de almacén de Ferretería Colorín.

El sistema cuenta con tres roles: Administrador, Vendedor y Almacén. Cada usuario dispone de acceso a los módulos correspondientes a sus funciones.

## 2. Acceso al sistema

1. Ejecutar la aplicación de escritorio.
2. Ingresar el usuario y la contraseña asignados.
3. Presionar el botón para iniciar sesión.
4. Al ingresar correctamente, se mostrará la ventana principal con los módulos disponibles según el rol.

La ventana principal muestra el nombre del usuario y su rol. Para acceder a una función, seleccionar el módulo correspondiente.

## 3. Manual del Administrador

El Administrador tiene acceso a todos los módulos y puede supervisar las operaciones del sistema.

### 3.1. Productos

Permite registrar, consultar, modificar y desactivar productos.

**Registrar un producto:**
1. Ingresar el código y nombre del producto.
2. Seleccionar la categoría, unidad de medida y marca.
3. Completar la ubicación física mediante estante y nivel.
4. Añadir una descripción si corresponde.
5. Presionar **Guardar**.

Si la categoría o marca todavía no existe, utilizar el botón **(+)** correspondiente para registrarla.

**Modificar o desactivar un producto:**
1. Buscar y seleccionar el producto en la tabla.
2. Modificar los datos necesarios y presionar **Editar**.
3. Para desactivarlo, seleccionarlo y presionar **Eliminar**.

La eliminación es lógica: el producto queda inactivo y no se borra físicamente de la base de datos.

### 3.2. Categorías y marcas

Se administran desde la interfaz de Productos.

1. Presionar el botón **(+)** junto a Categoría o Marca.
2. Ingresar el nombre.
3. Presionar **Guardar**.

Los registros también pueden desactivarse mediante eliminación lógica, según las opciones disponibles.

### 3.3. Proveedores

Permite mantener los datos de las empresas que abastecen a la ferretería.

1. Ingresar empresa, RUC, dirección, contacto, correo y teléfono.
2. Presionar **Guardar**.
3. Para modificar información, buscar y seleccionar al proveedor, cambiar los datos y presionar **Editar**.
4. Para desactivarlo, seleccionarlo y presionar **Eliminar**.

Los proveedores deben estar registrados antes de utilizarlos en una compra.

### 3.4. Clientes

Permite administrar los datos de los clientes.

1. Seleccionar **Persona Natural** o **Persona Jurídica**.
2. Para una persona natural, ingresar DNI, nombres y apellidos.
3. Para una persona jurídica, ingresar RUC y razón social.
4. Completar los datos de contacto disponibles.
5. Presionar **Guardar**.

Para actualizar información, seleccionar al cliente de la tabla y utilizar **Editar**. Para desactivarlo, utilizar **Eliminar**.

El cliente **Consumidor Final** puede utilizarse en las ventas cuando no se selecciona un cliente específico.

### 3.5. Usuarios

Permite gestionar las cuentas de acceso al sistema.

- Registrar usuarios y asignarles un rol.
- Consultar las cuentas existentes.
- Actualizar los datos permitidos.
- Cambiar contraseñas.
- Desactivar cuentas que ya no deban utilizar el sistema.

Los permisos dependen del rol asignado: Administrador, Vendedor o Almacén.

### 3.6. Compras

Permite registrar las compras efectuadas a los proveedores.

1. Seleccionar un proveedor previamente registrado.
2. Verificar los datos del proveedor que se muestran automáticamente.
3. Confirmar la fecha e ingresar el número del comprobante.
4. Seleccionar un producto previamente registrado.
5. Ingresar la cantidad y el precio de compra.
6. Indicar la fecha de vencimiento cuando corresponda o marcar **Sin vencimiento**.
7. Agregar el producto al resumen.
8. Repetir el procedimiento para cada producto de la compra.
9. Revisar los subtotales y el total.
10. Presionar **Registrar compra**.

El botón **Actualizar** permite recargar las opciones de proveedores y productos disponibles.

**Importante:** registrar una compra no aumenta directamente las existencias. El ingreso al inventario se realiza mediante el módulo Recepción.

### 3.7. Recepción

Permite verificar la mercadería que llega de una compra.

1. Seleccionar el comprobante de compra.
2. Revisar el proveedor, la fecha y los productos asociados.
3. Seleccionar el producto o lote que se va a verificar.
4. Comparar la cantidad registrada con la mercadería recibida físicamente.

**Si la mercadería está conforme:**
- Presionar **Completar / Recibido**.
- El sistema registra el ingreso al inventario y genera el movimiento de entrada en Kardex.

**Si existen diferencias o productos dañados:**
1. Registrar la observación y la cantidad observada.
2. Presionar **Observar**.
3. Esperar la revisión correspondiente.

La opción **Corregir recepción** está reservada al Administrador. Después de investigar la diferencia, podrá corregirse la recepción para que el personal de Almacén vuelva a verificarla y complete el ingreso.

Los estados de recepción son **Pendiente**, **Observada** y **Completada**.

### 3.8. Inventario

Permite consultar y controlar las existencias por producto y lote.

La tabla muestra información como producto, marca, proveedor, stock por lote, stock total del producto, precios, vencimiento, ubicación, stock defectuoso y descuentos.

**Consultar existencias:**
1. Buscar el producto.
2. Aplicar los filtros de categoría o marca.
3. Presionar **Actualizar**.
4. Revisar las cantidades y la información del lote.

El módulo también permite identificar productos agotados o con pocas existencias y productos próximos a vencer.

**Operaciones disponibles:**

- **Cambiar ubicación:** seleccionar un lote y actualizar su estante o nivel.
- **Gestionar descuento:** seleccionar el lote e ingresar el porcentaje de descuento correspondiente.
- **Ajustar stock:** corregir una cantidad o retirar unidades del inventario según corresponda.
- **Retiro de stock defectuoso:** retirar unidades que ya figuran como defectuosas.
- **Reingreso de stock defectuoso:** registrar el reingreso correspondiente por reparación o garantía, indicando la cantidad y observación.

Antes de modificar existencias, verificar siempre el producto y el lote seleccionados.

### 3.9. Ventas

Permite registrar ventas, calcular importes, controlar existencias y generar comprobantes en PDF.

**Registrar una venta:**

1. Buscar al cliente en la tabla y seleccionarlo. Sus datos se completarán automáticamente y no podrán editarse desde esta pantalla.
2. Buscar el producto por sus opciones disponibles y, si es necesario, filtrar por marca o categoría.
3. Presionar **Actualizar** para recargar la información disponible.
4. Seleccionar el lote y revisar sus datos, existencias y precio.
5. Ingresar la cantidad que se va a vender. El precio final puede modificarse cuando corresponda.
6. Presionar **Agregar producto a resumen de venta**.
7. Repetir los pasos para agregar otros productos.
8. Revisar el resumen y eliminar cualquier producto que no deba incluirse.
9. Seleccionar el tipo de comprobante y el método de pago.
10. Revisar el valor de venta, el IGV y el importe total.
11. Presionar **Cobrar pedido** para confirmar la operación.

El sistema calcula los importes aplicables, incluido el IGV del 18 %, y registra la venta.

Si no hay una caja abierta, el sistema solicitará abrirla antes de continuar con el cobro.

**Después de confirmar la venta:**
- Se descuenta el stock de los lotes correspondientes.
- Se registran los movimientos relacionados en Kardex y Caja.
- Se genera un comprobante en PDF.

El PDF generado por el sistema no significa, por sí solo, que el comprobante haya sido emitido electrónicamente ante SUNAT.

### 3.10. Caja

Permite controlar el dinero de la caja durante las operaciones.

**Abrir caja:**
1. Ingresar al módulo Caja.
2. Revisar el usuario y la fecha mostrados.
3. Ingresar el monto inicial.
4. Presionar **Abrir**.

**Consultar una caja abierta:**

La interfaz muestra el estado de la caja, el usuario que la abrió, la fecha, el monto de apertura, los ingresos, las salidas, el monto esperado y los movimientos registrados.

Presionar **Actualizar** para recargar la información.

**Cerrar caja:**
1. Presionar **Cerrar Caja**.
2. Revisar el monto esperado.
3. Contar el dinero disponible e ingresar el monto contado.
4. Revisar la diferencia calculada.
5. Añadir una observación si es necesario.
6. Presionar **Confirmar**.

El monto esperado se calcula así:

**Monto esperado = Monto de apertura + Ingresos − Salidas**

**Diferencia = Monto contado − Monto esperado**

Si la diferencia es positiva, sobra dinero respecto del monto esperado; si es negativa, falta dinero.

El sistema solicita cerrar la caja antes de salir de la ventana principal cuando el usuario mantiene una caja abierta.

### 3.11. Kardex

Permite consultar el historial de movimientos de inventario y revisar las operaciones relacionadas con ventas, recepciones, ajustes y productos defectuosos.

La tabla muestra la fecha, el producto, el lote, el tipo de movimiento, el motivo, la cantidad, el stock anterior y posterior, el stock defectuoso, el origen, el comprobante y el usuario.

**Consultar movimientos:**
1. Buscar el producto o lote.
2. Aplicar los filtros de movimiento o usuario.
3. Presionar **Actualizar**.
4. Seleccionar un registro y presionar **Ver detalle** para revisar la información disponible y abrir el PDF correspondiente, cuando exista.

**Revertir una venta:**

Esta operación permite revertir una venta reciente y reingresar los productos correspondientes. Debe verificarse el comprobante y el estado de los productos. La reversión está limitada al mismo día y, cuando la solicita un Vendedor, requiere autorización del Administrador.

**Ingresar a stock defectuoso:**

1. Verificar el comprobante presentado por el cliente.
2. Localizar el movimiento de salida correspondiente a la venta.
3. Seleccionar el movimiento y utilizar la opción para ingresar a stock defectuoso.
4. Indicar la cantidad y el motivo.
5. Confirmar la operación conforme a la autorización correspondiente.

El sistema registra el movimiento y puede generar una etiqueta en PDF con información del producto. La mercadería defectuosa debe identificarse y mantenerse separada físicamente del stock disponible para la venta.

**Resolver una devolución:**

Después de verificar el comprobante y revisar el caso, se puede gestionar la devolución del importe pagado o el reemplazo por un producto en buen estado, según corresponda y con la autorización necesaria. La operación debe quedar registrada para mantener la trazabilidad del inventario y de las mermas.

### 3.12. Dashboard

El Dashboard está reservado al Administrador y permite consultar indicadores de ventas e inventario.

Incluye:

- Total vendido, cantidad de ventas y ganancia bruta.
- Rendimiento de vendedores.
- Productos más vendidos.
- Gráficos de ventas semanales y mensuales.
- Historial y detalle de ventas.
- Información de mermas y productos con baja o nula rotación.

**Consultar indicadores:**
1. Seleccionar los filtros de día, mes o año disponibles.
2. Presionar **Actualizar**.
3. Revisar los indicadores, tablas y gráficos.

**Consultar el historial:**
1. Aplicar los filtros de fecha o usuario.
2. Seleccionar una venta.
3. Utilizar **Ver detalles** para revisar la información correspondiente.
4. Presionar **Generar reporte PDF** para obtener el informe según los filtros seleccionados.

Los reportes pueden incluir resumen de ventas, ganancia bruta, ticket promedio, IGV, productos más vendidos, rendimiento de vendedores, métodos de pago, mermas y rotación de inventario.

## 4. Manual del Vendedor

El Vendedor se encarga principalmente de las ventas, la atención a los clientes y las operaciones de caja.

### 4.1. Ventas

Para registrar una venta, seguir el procedimiento descrito en la sección **3.9. Ventas**.

El cliente y los productos se seleccionan de las tablas, y sus datos se muestran automáticamente. El Vendedor puede indicar la cantidad y modificar el precio final según las opciones disponibles.

Antes de cobrar, debe verificar el resumen de venta, el método de pago y el importe total. Si no existe una caja abierta, el sistema solicitará abrirla antes de continuar con el cobro.

### 4.2. Inventario

El Vendedor tiene acceso únicamente a las funciones de consulta.

- **Buscar producto:** permite localizar productos.
- **Marca y Categoría:** permiten filtrar los resultados.
- **Actualizar:** recarga la información del inventario.

Puede consultar las existencias por lote, los precios y otros datos disponibles, pero no puede modificar el inventario, cambiar ubicaciones, gestionar descuentos ni ajustar o retirar stock.

### 4.3. Clientes

El Vendedor puede registrar y editar clientes, pero no eliminarlos.

1. Seleccionar **Persona Natural** o **Persona Jurídica**.
2. Completar los campos correspondientes.
3. Presionar **Guardar** para registrar un cliente.
4. Para modificarlo, seleccionarlo de la tabla, actualizar los datos permitidos y presionar **Editar**.

La opción **Eliminar** no está habilitada para el Vendedor.

### 4.4. Kardex

El Vendedor puede consultar los movimientos de inventario y acceder a las siguientes opciones:

- **Buscar producto o lote:** permite localizar movimientos relacionados.
- **Filtrar por movimiento y usuario:** permite acotar los resultados.
- **Actualizar:** recarga la información.
- **Ver detalle:** muestra los detalles del movimiento seleccionado y permite abrir el PDF, si está disponible.
- **Revertir venta:** permite gestionar la reversión de una venta del mismo día, previa autorización del Administrador y verificación del comprobante y del estado del producto.
- **Ingresar a stock defectuoso:** permite registrar productos devueltos que presentan defectos, con autorización del Administrador y verificación del comprobante.
- **Resolver devolución:** permite gestionar el reembolso o el reemplazo del producto, con autorización del Administrador y la verificación correspondiente.

Las operaciones de reversión, registro de productos defectuosos y resolución de devoluciones deben realizarse siguiendo el procedimiento establecido y con la autorización requerida.

### 4.5. Caja

El Vendedor puede abrir, consultar y cerrar la caja.

**Abrir caja:**
1. Ingresar al módulo Caja.
2. Verificar los datos del usuario y la fecha.
3. Ingresar el monto inicial.
4. Presionar **Abrir**.

Durante el turno, puede consultar los ingresos, las salidas, el monto esperado y los movimientos registrados. Puede utilizar **Actualizar** para recargar la información.

**Cerrar caja:**
1. Presionar **Cerrar Caja**.
2. Revisar el monto esperado.
3. Contar el dinero e ingresar el monto contado.
4. Revisar la diferencia calculada.
5. Añadir una observación si corresponde.
6. Presionar **Confirmar**.

El Vendedor debe cerrar la caja antes de salir de la ventana principal si mantiene una caja abierta.

### 4.6. Módulos no disponibles

El Vendedor no dispone de acceso habitual a **Productos, Proveedores, Compras, Usuarios, Recepción ni Dashboard**.

## 5. Manual de Almacén

El personal de Almacén se encarga de mantener los productos registrados, gestionar las compras y verificar la mercadería recibida, además de controlar las existencias y sus movimientos.

### 5.1. Productos

Antes de registrar compras de productos nuevos, verificar que estén registrados en el sistema.

1. Completar código, nombre, categoría, unidad, marca, estante, nivel y descripción.
2. Si la categoría o marca no existe, utilizar el botón **(+)** correspondiente.
3. Presionar **Guardar**.

Para editar o desactivar un producto, buscarlo en la tabla y utilizar **Editar** o **Eliminar**, según corresponda.

### 5.2. Categorías y marcas

Estas opciones se administran desde Productos.

1. Presionar **(+)** junto al campo correspondiente.
2. Ingresar el nombre.
3. Presionar **Guardar**.

### 5.3. Proveedores

Almacén puede registrar, editar y desactivar proveedores.

1. Ingresar empresa, RUC, contacto, teléfono, correo y dirección.
2. Presionar **Guardar**.
3. Para modificar un proveedor, seleccionarlo de la tabla y presionar **Editar**.
4. Para desactivarlo, seleccionarlo y presionar **Eliminar**.

Registrar los proveedores antes de utilizarlos en Compras.

### 5.4. Compras

Seguir el procedimiento de la sección **3.6. Compras**. Almacén debe seleccionar el proveedor y los productos registrados, ingresar el comprobante, cantidades, precios y vencimientos, añadir los productos al resumen y revisar el total antes de registrar la compra.

Después de registrar la compra, se debe realizar la recepción de la mercadería. La compra registrada todavía no representa stock disponible.

### 5.5. Recepción

La recepción debe realizarse verificando la mercadería física.

1. Seleccionar el comprobante de compra.
2. Revisar los productos y lotes asociados.
3. Seleccionar el producto que se va a verificar.
4. Comparar las cantidades y revisar el estado de la mercadería.

Si todo está conforme, presionar **Completar / Recibido** para registrar el ingreso.

Si hay diferencias, daños o cantidades incompletas, registrar la observación y la cantidad observada, y presionar **Observar**. El Administrador deberá revisar y corregir la recepción cuando corresponda.

Una vez completada la recepción, acomodar la mercadería en la ubicación física asignada.

### 5.6. Inventario

Almacén puede consultar y gestionar las existencias de acuerdo con las opciones habilitadas.

**Consultar stock:**
1. Buscar el producto.
2. Aplicar filtros de categoría o marca.
3. Presionar **Actualizar**.
4. Revisar el lote, el stock, los precios, el vencimiento, la ubicación y el stock defectuoso.

**Cambiar ubicación:**
1. Seleccionar el lote.
2. Presionar **Cambiar ubicación**.
3. Ingresar la ubicación física correspondiente y guardar el cambio.

**Gestionar descuentos:**
1. Seleccionar el lote.
2. Presionar **Gestionar descuento**.
3. Ingresar el porcentaje correspondiente. Por ejemplo, 10 representa un descuento del 10 %.
4. Confirmar el cambio.

El descuento corresponde al lote seleccionado.

**Ajustar stock:**

Utilizar **Ajustar stock** para corregir cantidades o retirar productos del inventario cuando corresponda, por ejemplo, si están vencidos, oxidados o en mal estado. Verificar siempre el lote y la cantidad antes de confirmar.

**Retirar o reingresar stock defectuoso:**

- Utilizar **Retiro stock defectuoso** para retirar unidades que ya estén registradas como defectuosas.
- Utilizar **Reingreso stock defectuoso** cuando corresponda registrar su reingreso por reparación o garantía. Seleccionar la opción disponible, indicar la cantidad y añadir la observación necesaria.

### 5.7. Kardex

Almacén puede consultar los movimientos de inventario, buscar productos o lotes, aplicar filtros y revisar detalles.

**Para consultar:**
1. Buscar el producto o lote.
2. Seleccionar el tipo de movimiento o usuario.
3. Presionar **Actualizar**.
4. Seleccionar un movimiento y presionar **Ver detalle**.

Para gestionar un producto defectuoso, se debe comprobar el comprobante de compra, localizar el movimiento de salida por venta y seguir el procedimiento autorizado para registrarlo como defectuoso. La mercadería debe identificarse con la etiqueta correspondiente, cuando se genere, y almacenarse separada del stock disponible.

Las devoluciones y los reemplazos deben registrarse según el procedimiento autorizado para mantener actualizado el inventario.

### 5.8. Módulos no disponibles

Almacén no dispone de acceso a Ventas, Clientes, Usuarios, Caja ni Dashboard.

## 6. Recomendaciones generales

- Verificar los datos antes de guardar, registrar, cobrar o confirmar una operación.
- Buscar y seleccionar el producto y lote correctos antes de modificar existencias.
- Registrar primero los productos y proveedores necesarios antes de realizar compras.
- Completar la recepción antes de considerar disponible la mercadería recién comprada.
- Mantener separados los productos defectuosos del stock disponible para la venta.
- Verificar los comprobantes antes de gestionar una reversión o devolución.
- Abrir la caja antes de realizar cobros y cerrarla al finalizar el turno.
- Utilizar los filtros y el botón **Actualizar** para consultar la información más reciente disponible.
- No compartir las credenciales de acceso.
- Recordar que la generación de un PDF no equivale a la emisión electrónica del comprobante ante SUNAT.

## 7. Solución de problemas básicos

- **No se puede completar una operación:** leer el mensaje que muestra el sistema y corregir los datos indicados.
- **Los datos no aparecen actualizados:** utilizar el botón **Actualizar**, cuando esté disponible.
- **No se puede acceder a un módulo:** verificar que la cuenta tenga el rol correspondiente. Si el acceso sigue sin estar disponible, solicitar apoyo al Administrador.
- **Se detecta una diferencia en la mercadería recibida:** registrar la observación en Recepción y solicitar la revisión correspondiente.
- **Se necesita corregir una operación de inventario, venta o caja:** verificar el movimiento relacionado y seguir el procedimiento autorizado para ese caso.

**Recomendación:** antes de repetir una operación, revisar el mensaje mostrado por el sistema para evitar registrar acciones duplicadas.
## 8. Consideraciones finales

El uso correcto del SGIVFC depende de registrar las operaciones en el módulo correspondiente y verificar los datos antes de confirmarlas. La coordinación entre Ventas, Compras, Recepción, Inventario, Caja y Kardex permite mantener la información de existencias y movimientos organizada.

Ante una operación que requiera autorización o que no pueda completarse con el rol asignado, se debe solicitar apoyo al Administrador.