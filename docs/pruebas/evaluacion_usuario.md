# Prueba de usuario con una evaluadora del rubro – SGIVFC

## 1. Contexto y método

Como complemento a las pruebas automatizadas, el sistema fue probado por una usuaria ajena a la carrera de Ingeniería, con formación en química y experiencia en la gestión de una farmacia, un entorno con necesidades similares a las de una ferretería: inventario por lotes, Kardex, compras, ventas y caja. Utiliza a diario un sistema comercial de ese tipo. Por privacidad, no se consignan su nombre ni su lugar de trabajo.

La evaluadora es familiar del desarrollador, por lo que se declara expresamente que **no es una prueba independiente**, aunque sí es una prueba realizada por una persona que no desarrolló el sistema ni pertenece a la carrera.

| Dato | Detalle |
|------|---------|
| Evaluadora | Usuaria con formación en química y experiencia en gestión de farmacia |
| Fecha y hora | Domingo 22 de noviembre de 2026, 3:00 p. m. |
| Duración | Aproximadamente 2 horas |
| Método | La usuaria operó el sistema por su cuenta, módulo por módulo, y comparó su funcionamiento con el del sistema que utiliza en su trabajo. En varios módulos hizo dos intentos. El desarrollador observó, intervino solo cuando ella no podía avanzar y anotó las dificultades |
| Módulos probados | Productos, usuarios, clientes, proveedores, compras, recepción de mercadería, inventario, Kardex, ventas y dashboard. También revisó la tienda web |
| Tipo de prueba | Usabilidad y aceptación, complementaria a las pruebas automatizadas |

## 2. Resultados por módulo

| Módulo | Tareas realizadas | ¿Necesitó ayuda? | Observaciones |
|--------|-------------------|------------------|---------------|
| Productos | Agregó un producto | No | Le resultó fácil y las validaciones le parecieron adecuadas. En marcas escribió un nombre en minúsculas y no pudo corregirlo: no existe la opción de editar y, al darlo de baja para registrarlo de nuevo con mayúsculas, el sistema no se lo permitió. Eliminó un producto (baja lógica) y no encontró cómo revertirlo |
| Usuarios | Agregó un usuario; usó limpiar, guardar, editar y eliminar | No | Todo funcionó sin problemas. Prefiere un estado activo/inactivo en lugar de eliminar. Observó que un usuario con otro rol no puede editar su propia cuenta |
| Clientes | Agregó un cliente | No | Fácil de usar |
| Proveedores | Agregó un proveedor | No | Fácil de usar. Se profundizó menos en este módulo |
| Compras | Registró una compra con una lista de productos | Primer intento: sí. Segundo: no | En el primer intento no encontró dónde elegir el producto (solo aparece en un combo) ni cómo agregarlo. Al descubrir el botón de agregar, la validación le recordó confirmar la fecha y vio la lista sumarse en la tabla. Agregó el resto y preguntó con qué botón se envía; se le indicó registrar compra. En el segundo intento registró la compra sin problemas. Le incomodó que el campo «unidad» se repita cuando ya aparece en el nombre del producto |
| Recepción | Recepcionó una compra | Primer intento: sí. Segundo: parcial | No entendió el orden inicial: actualizar, elegir la compra en el combo, seleccionar cada fila y completar. Como el sistema no le permitió ingresar dos veces el mismo producto, dedujo qué le faltaba y usó completar. Le gustó que la lista se refresque al actualizar. No entendió los botones **observar** y **corregir recepción**, ni en el segundo uso, y no los utilizó. Le preocupó qué ocurre si el administrador no está para autorizar una corrección |
| Inventario | Revisó el stock y los botones de operaciones | Sí, y persistió | Primer intento: no pudo identificar qué stock se había modificado (el descontado por una venta). Segundo intento, después de la explicación: solo entendió **descuento** y **cambiar ubicación**; no entendió retiro de defectuosos, reingreso ni ajustar stock |
| Kardex | Revisó movimientos, filtros y botones | Sí, y persistió | Entendió el contenido y los filtros (usuario, movimiento, motivo). Primer intento: no pudo identificar su propio movimiento. Segundo intento: solo entendió **ver detalle**; no entendió revertir venta, ingresar stock defectuoso ni resolver devolución, aun después de la explicación |
| Ventas | Registró ventas con caja abierta | Primer intento: sí. Segundo: no | Le resultó fácil seleccionar cliente y producto, y le gustó que para cobrar siempre se exija una caja abierta y los filtros. En el primer intento dudó con el botón **agregar producto** y, al cobrar, apareció la ventana de apertura de caja, sobre la que preguntó; ingresó el monto, volvió a cobrar y la venta se registró. En el segundo intento vendió sin ayuda |
| Dashboard | Revisó el contenido y generó un PDF | Sí | No lo entendió por su cuenta: se le explicó qué muestra cada parte y cómo generar el PDF. Después lo entendió y generó su PDF. Lo consideró adecuado, aunque en su trabajo no dispone de un dashboard para compararlo |
| Tienda web | Revisó la tienda | – | Le pareció visualmente agradable. No profundizó por no ser su área |

## 3. Aspectos que funcionaron bien

- Los **módulos de registro** (productos, clientes, proveedores y usuarios) fueron fáciles desde el primer intento y los completó sin ayuda.
- Las **validaciones** le parecieron adecuadas y, en recepción y compras, la **guiaron**: el rechazo del producto repetido y el recordatorio de la fecha le indicaron cómo continuar.
- Los **flujos principales** (vender, comprar y completar una recepción) se aprendieron en el segundo intento.
- La exigencia de **caja abierta** para cobrar le pareció adecuada.
- El **refresco automático** de la lista de recepción y la generación del **PDF del dashboard** funcionaron correctamente.

## 4. Dificultades encontradas

1. **Botones de Inventario y Kardex:** varias operaciones no se entendieron por sus nombres, ni siquiera después de una explicación (retiro de defectuosos, reingreso, ajustar stock, revertir venta, ingresar stock defectuoso y resolver devolución).
2. **Trazabilidad:** no pudo identificar en Inventario ni en Kardex el movimiento generado por una venta.
3. **Recepción:** el orden de los pasos no es evidente y los botones observar y corregir no se entendieron.
4. **Compras:** poca claridad para elegir y agregar productos, que solo aparecen en un combo, sin búsqueda por escritura.
5. **Ventas:** el botón de agregar producto y la ventana de apertura de caja al cobrar generaron dudas en el primer intento.
6. **Dashboard:** no se entendió sin una explicación.
7. **Baja lógica:** no hay forma de ver ni reactivar los registros dados de baja, ni de editar categorías y marcas.

## 5. Mejoras identificadas

La prioridad corresponde al criterio del desarrollador, según el impacto en los datos, la seguridad y el uso diario.

| # | Mejora | Módulos | Prioridad | Estado en el proyecto |
|---|--------|---------|:---:|-----------------------|
| 1 | **Gestión del estado:** columna activo/inactivo, filtro (activos, inactivos, todos) y botones **Desactivar** y **Activar** en lugar de **Eliminar**, con posibilidad de reactivar | Productos, usuarios, clientes, proveedores, categorías y marcas | Alta | Trabajo futuro. La baja es lógica (campo `activo`), pero la interfaz no permite consultarlos ni reactivarlos |
| 2 | **Editar categorías y marcas** | Productos | Alta | Trabajo futuro. Hoy solo se pueden crear y dar de baja |
| 3 | **Reorganizar Inventario y Kardex:** nombres de botones más claros, textos de ayuda y un módulo de Atención al Cliente para devoluciones, reversión de ventas y productos defectuosos | Inventario, Kardex | Alta | Trabajo futuro |
| 4 | **Control de roles en la capa de servicio** y **política de contraseñas** (longitud, combinación y restablecimiento) | Usuarios | Alta | **Parcial.** Existen los roles Administrador, Vendedor, Almacén y WEB, con restricción de menú y paneles, y las contraseñas se almacenan con hash PBKDF2. Falta validar el rol en el servicio y la política de contraseñas (SEG-08, SEG-09) |
| 5 | **Flujo de venta rápido por teclado:** cliente «Consumidor» por defecto para boletas; tecla rápida que abra la búsqueda de clientes registrados y, si no aparece, redirija al módulo de Clientes; tecla rápida para buscar productos y agregarlos a la tabla con Enter, sin botón de agregar. El mismo criterio en los demás módulos, empezando por Compras | Ventas, Compras y demás | Media | Trabajo futuro |
| 6 | **Edición de la propia cuenta** por usuarios de cualquier rol | Usuarios | Media | Trabajo futuro |
| 7 | **Recepción guiada:** texto con los pasos del flujo y revisión de la dependencia del administrador para corregir recepciones | Recepción | Media | Trabajo futuro |
| 8 | **Trazabilidad de ventas:** resaltar en Inventario y Kardex el movimiento generado por una venta | Inventario, Kardex | Media | Trabajo futuro |
| 9 | **Compras:** búsqueda de productos por escritura, filtros y tabla de productos | Compras | Media | Trabajo futuro |
| 10 | **Fotografías y fichas técnicas** registrables desde el escritorio, junto al producto | Productos | Media | **Parcial.** Los 47 productos tienen imagen y ficha, mostradas en la tienda web, pero fueron cargadas directamente en la base de datos |
| 11 | **Escaneo de códigos de barras** en la recepción, con registro manual como alternativa | Recepción | Media | Trabajo futuro |
| 12 | **Historial de compras** por cliente y por proveedor, consulta de facturas y **comparación de precios** entre proveedores | Clientes, proveedores | Media | Trabajo futuro. Los datos existen en la base de datos |
| 13 | **Pagos combinados:** dividir una venta entre varios medios de pago (por ejemplo, S/ 120 con Yape y S/ 30 en efectivo) | Ventas, caja | Media | Trabajo futuro. Hoy cada venta registra un único método de pago (DEF-12, DEF-13) |
| 14 | **Búsqueda avanzada** por categoría, marca, tipo de documento, proveedor, comprobante y fechas | Productos, clientes, recepción | Media | Trabajo futuro. Existe una búsqueda básica |
| 15 | **Aviso de caja cerrada** más claro antes de cobrar | Ventas | Baja | Trabajo futuro |
| 16 | **Compras:** retirar el campo «unidad» repetido | Compras | Baja | Trabajo futuro |
| 17 | **Historiales** (modificaciones, recepciones y actividad de usuarios) y **digitalización de comprobantes** | Varios | Baja | **Parcial.** El Kardex registra los movimientos de inventario; el registro de inicios de sesión y ventas se incorpora con el monitoreo (semana 16) |
| 18 | **Impresión, exportación a Excel** y **atajos con teclas de función** (por ejemplo F12 y F4, como en el sistema que utiliza la evaluadora) | Todos | Baja | Trabajo futuro |

## 6. Análisis

- **Los módulos con la misma estructura de pantalla se aprendieron al instante.** Productos, clientes, proveedores y usuarios comparten formulario, tabla y botones de limpiar, guardar, editar y eliminar; el desarrollador atribuye a esa consistencia que la usuaria los completara sin ayuda. Las dificultades se concentraron en los módulos con estructura distinta (inventario, Kardex y dashboard).
- **Los flujos normales se aprenden con la práctica; las operaciones excepcionales no.** Vender, comprar y completar una recepción funcionaron en el segundo intento. En cambio, observar y corregir recepciones, defectuosos, reingreso, ajuste, reversión de ventas y devoluciones no se entendieron aun después de una explicación, lo que indica que sus nombres y su organización no son autoexplicativos.
- **Las validaciones cumplieron una doble función:** además de impedir errores, orientaron a la usuaria sobre cómo continuar.
- **Parte de la dificultad puede deberse al dominio:** varias de esas operaciones son propias del manejo de una ferretería y pueden manejarse con otros nombres o procesos en una farmacia. Esto no invalida el hallazgo, porque se trata de una persona con experiencia en inventarios, pero conviene tenerlo en cuenta al interpretarlo.
- La **baja lógica** conserva los datos, pero sin una pantalla para reactivar, un error de baja no se puede deshacer desde la interfaz.

## 7. Conclusión y limitaciones

La prueba permitió observar a una persona ajena al desarrollo usando el sistema. Los módulos de registro y los flujos principales resultaron usables, y las validaciones funcionaron como apoyo. Las principales oportunidades de mejora están en la claridad de las operaciones de inventario y Kardex, la guía de los flujos de recepción y compras, y la gestión de registros dados de baja. Todas se incorporan como **trabajo futuro**.

**Limitaciones de la prueba:**

- Una sola evaluadora y una sola sesión.
- No se siguió un protocolo formal de tareas ni se midieron tiempos.
- El desarrollador intervino para explicar cuando la usuaria no podía avanzar, y las observaciones fueron anotadas por él a partir de lo observado.
- La evaluadora es familiar del desarrollador, por lo que los resultados deben leerse como orientación de mejora y no como validación independiente.
- Se dedicó menos tiempo a la tienda web y a los proveedores.