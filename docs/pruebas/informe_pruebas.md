# Informe de pruebas de software

**Proyecto:** Sistema de Gestión de Inventario y Ventas para Ferretería Colorín (SGIVFC)  
**Componente evaluado:** sistema de escritorio (Java Swing + MySQL)  
**Herramientas:** JUnit 5, Mockito 5, JaCoCo, GitHub Actions  

---

## 1. Conceptos aplicados

- **Prueba de software:** actividad que ejecuta el sistema (o parte de él) con datos controlados para comprobar que su comportamiento coincide con el esperado y detectar defectos antes de que lleguen al usuario.
- **Prueba unitaria:** verifica una unidad pequeña de código (una clase o método) de forma aislada. Es la base de la pirámide de pruebas: son rápidas, baratas y se pueden ejecutar en cada cambio.
- **Doble de prueba (mock):** objeto simulado que reemplaza a una dependencia real. En este proyecto se simulan los DAO y la conexión a MySQL, de modo que las reglas de negocio de la capa de servicio se prueban sin necesitar una base de datos.
- **Cobertura de código:** porcentaje de líneas y ramas del código que ejecutan las pruebas. Se mide con JaCoCo.
- **Integración continua (CI):** ejecución automática de las pruebas en cada `push` o Pull Request mediante GitHub Actions.

## 2. Estrategia y alcance

El sistema usa una arquitectura por capas (`vista` → `controlador` → `servicio` → `dao` → MySQL). Las **reglas de negocio** viven en la capa `servicio`, por lo que las pruebas unitarias se concentran allí:

| Capa | Se prueba | Cómo |
|---|---|---|
| `servicio` | Validaciones, cálculos (IGV, descuentos, vuelto), control de stock, Kardex y Caja, reversión de transacciones | JUnit 5 + Mockito (DAO y `ConexionBD` simulados) |
| `controlador` | Flujo de inicio de sesión | JUnit 5 + Mockito |
| `dao` | Pendiente: pruebas de integración contra una base MySQL de prueba | — |
| `vista` (Swing) | No se automatiza; se valida manualmente en las demostraciones | — |

Para poder aislar los servicios se agregó **inyección de dependencias por constructor** (`VentaService`, `CajaService`, `CompraService`, `InventarioService`, `ProductoService`). Los constructores sin parámetros se conservan, por lo que el resto de la aplicación no cambia.

## 3. Resumen de casos de prueba

| Módulo | Casos |
|---|---:|
| Ventas (RF-08) | 42 |
| Caja (RF-09) | 22 |
| Compras (RF-05) | 7 |
| Inventario (RF-07) | 24 |
| Productos (RF-01) | 8 |
| Usuarios / Login / Kardex (preexistentes) | 3 |
| **Total** | **106** |

## 4. Plan de pruebas (casos)

La columna **Resultado** se completa con la salida real de `mvn test` (ver sección 6).

| ID | Módulo | Caso de prueba | Método | Resultado |
|---|---|---|---|---|
| VT-01 | Ventas (RF-08) | Validación: una venta nula se rechaza | `VentaServiceTest.ventaNulaSeRechaza` | Aprobado |
| VT-02 | Ventas (RF-08) | Validación: la lista de detalles nula se rechaza | `VentaServiceTest.detallesNulosSeRechazan` | Aprobado |
| VT-03 | Ventas (RF-08) | Validación: una venta sin detalles se rechaza | `VentaServiceTest.ventaSinDetallesSeRechaza` | Aprobado |
| VT-04 | Ventas (RF-08) | Validación: el cliente es obligatorio | `VentaServiceTest.ventaSinClienteSeRechaza` | Aprobado |
| VT-05 | Ventas (RF-08) | Validación: el usuario vendedor es obligatorio | `VentaServiceTest.ventaSinUsuarioSeRechaza` | Aprobado |
| VT-06 | Ventas (RF-08) | Validación: el tipo de comprobante es obligatorio | `VentaServiceTest.ventaSinTipoDeComprobanteSeRechaza` | Aprobado |
| VT-07 | Ventas (RF-08) | Validación: el método de pago es obligatorio | `VentaServiceTest.ventaSinMetodoDePagoSeRechaza` | Aprobado |
| VT-08 | Ventas (RF-08) | Caja: sin caja abierta no se puede vender | `VentaServiceTest.sinCajaAbiertaNoSePuedeVender` | Aprobado |
| VT-09 | Ventas (RF-08) | Comprobante: un tipo desconocido se rechaza | `VentaServiceTest.tipoDeComprobanteDesconocidoSeRechaza` | Aprobado |
| VT-10 | Ventas (RF-08) | Lote: un lote inexistente se rechaza | `VentaServiceTest.loteInexistenteSeRechaza` | Aprobado |
| VT-11 | Ventas (RF-08) | Lote: un precio de venta nulo se rechaza | `VentaServiceTest.lotePrecioNuloSeRechaza` | Aprobado |
| VT-12 | Ventas (RF-08) | Lote: un precio de venta igual a cero se rechaza | `VentaServiceTest.lotePrecioCeroSeRechaza` | Aprobado |
| VT-13 | Ventas (RF-08) | Stock: no se puede vender más unidades que el stock del lote | `VentaServiceTest.stockInsuficienteSeRechaza` | Aprobado |
| VT-14 | Ventas (RF-08) | Stock: se puede vender exactamente todo el stock del lote | `VentaServiceTest.ventaDeTodoElStockEsValida` | Aprobado |
| VT-15 | Ventas (RF-08) | Detalle: una cantidad igual a cero se rechaza | `VentaServiceTest.detalleConCantidadCeroSeRechaza` | Aprobado |
| VT-16 | Ventas (RF-08) | Detalle: un lote con id inválido se rechaza | `VentaServiceTest.detalleConIdDeLoteInvalidoSeRechaza` | Aprobado |
| VT-17 | Ventas (RF-08) | Detalle: un detalle nulo dentro de la lista se rechaza | `VentaServiceTest.detalleNuloDentroDeLaListaSeRechaza` | Aprobado |
| VT-18 | Ventas (RF-08) | Descuento: un porcentaje mayor a 100 se rechaza | `VentaServiceTest.descuentoMayorACienSeRechaza` | Aprobado |
| VT-19 | Ventas (RF-08) | Descuento: un porcentaje negativo se rechaza | `VentaServiceTest.descuentoNegativoSeRechaza` | Aprobado |
| VT-20 | Ventas (RF-08) | Descuento: activo pero sin porcentaje no descuenta nada | `VentaServiceTest.descuentoActivoSinPorcentajeNoDescuenta` | Aprobado |
| VT-21 | Ventas (RF-08) | Efectivo: un monto recibido menor al total se rechaza | `VentaServiceTest.efectivoInsuficienteSeRechaza` | Aprobado |
| VT-22 | Ventas (RF-08) | Efectivo: sin monto recibido se rechaza | `VentaServiceTest.efectivoSinMontoRecibidoSeRechaza` | Aprobado |
| VT-23 | Ventas (RF-08) | Efectivo: pagar el monto exacto no genera vuelto | `VentaServiceTest.efectivoExactoNoGeneraVuelto` | Aprobado |
| VT-24 | Ventas (RF-08) | Pago digital: el monto recibido y el vuelto quedan en cero | `VentaServiceTest.pagoDigitalIgnoraMontoRecibidoYVuelto` | Aprobado |
| VT-25 | Ventas (RF-08) | Boleta en efectivo: calcula total, IGV incluido y vuelto | `VentaServiceTest.boletaEnEfectivoCalculaTotalesYVuelto` | Aprobado |
| VT-26 | Ventas (RF-08) | Factura con tarjeta y descuento del 10 %: usa serie F001 | `VentaServiceTest.facturaConTarjetaYDescuentoDelLote` | Aprobado |
| VT-27 | Ventas (RF-08) | El tipo de comprobante no distingue mayúsculas de minúsculas | `VentaServiceTest.tipoDeComprobanteIgnoraMayusculas` | Aprobado |
| VT-28 | Ventas (RF-08) | Varios detalles: el total es la suma de los importes | `VentaServiceTest.ventaConVariosDetallesSumaLosImportes` | Aprobado |
| VT-29 | Ventas (RF-08) | Fecha: se respeta la fecha de venta si ya viene informada | `VentaServiceTest.seRespetaLaFechaDeVentaInformada` | Aprobado |
| VT-30 | Ventas (RF-08) | Estado: se respeta el estado de comprobante ya informado | `VentaServiceTest.seRespetaElEstadoDeComprobanteInformado` | Aprobado |
| VT-31 | Ventas (RF-08) | Stock: la venta descuenta del lote la cantidad vendida | `VentaServiceTest.ventaDescuentaStockDelLote` | Aprobado |
| VT-32 | Ventas (RF-08) | Kardex: la venta registra una salida con el stock antes y después | `VentaServiceTest.ventaRegistraSalidaEnKardex` | Aprobado |
| VT-33 | Ventas (RF-08) | Caja: la venta registra un ingreso por el total cobrado | `VentaServiceTest.ventaRegistraIngresoEnCaja` | Aprobado |
| VT-34 | Ventas (RF-08) | Caja: el monto esperado = apertura + ingresos - salidas | `VentaServiceTest.ventaActualizaElMontoEsperadoDeLaCaja` | Aprobado |
| VT-35 | Ventas (RF-08) | Reversión: si no se guarda la cabecera de la venta | `VentaServiceTest.falloAlGuardarLaCabeceraRevierte` | Aprobado |
| VT-36 | Ventas (RF-08) | Reversión: si no se guarda un detalle de la venta | `VentaServiceTest.falloAlGuardarUnDetalleRevierte` | Aprobado |
| VT-37 | Ventas (RF-08) | Reversión: si el stock cambió y no se puede reducir | `VentaServiceTest.falloAlReducirStockRevierte` | Aprobado |
| VT-38 | Ventas (RF-08) | Reversión: si no se registra el movimiento de Kardex | `VentaServiceTest.falloAlRegistrarKardexRevierte` | Aprobado |
| VT-39 | Ventas (RF-08) | Reversión: si no se registra el ingreso en Caja | `VentaServiceTest.falloAlRegistrarMovimientoDeCajaRevierte` | Aprobado |
| VT-40 | Ventas (RF-08) | Reversión: si no se actualizan los totales de la Caja | `VentaServiceTest.falloAlActualizarTotalesDeCajaRevierte` | Aprobado |
| VT-41 | Ventas (RF-08) | Reversión: una excepción SQL inesperada devuelve false | `VentaServiceTest.excepcionSqlInesperadaRevierte` | Aprobado |
| VT-42 | Ventas (RF-08) | Conexión: si no hay conexión a la base de datos devuelve false | `VentaServiceTest.sinConexionALaBaseDeDatosDevuelveFalse` | Aprobado |
| CJ-01 | Caja (RF-09) | Apertura: una caja nula se rechaza | `CajaServiceTest.aperturaConCajaNulaSeRechaza` | Aprobado |
| CJ-02 | Caja (RF-09) | Apertura: se exige un usuario válido | `CajaServiceTest.aperturaSinUsuarioSeRechaza` | Aprobado |
| CJ-03 | Caja (RF-09) | Apertura: el monto inicial es obligatorio | `CajaServiceTest.aperturaSinMontoSeRechaza` | Aprobado |
| CJ-04 | Caja (RF-09) | Apertura: no se acepta un monto inicial negativo | `CajaServiceTest.aperturaConMontoNegativoSeRechaza` | Aprobado |
| CJ-05 | Caja (RF-09) | Apertura: un monto inicial de cero es válido | `CajaServiceTest.aperturaConMontoCeroEsValida` | Aprobado |
| CJ-06 | Caja (RF-09) | Apertura: asigna valores iniciales y confirma la transacción | `CajaServiceTest.aperturaValidaAsignaValoresInicialesYConfirma` | Aprobado |
| CJ-07 | Caja (RF-09) | Apertura: si el DAO falla se revierte la transacción | `CajaServiceTest.aperturaConFalloDelDaoRevierte` | Aprobado |
| CJ-08 | Caja (RF-09) | Movimiento: un movimiento nulo se rechaza | `CajaServiceTest.movimientoNuloSeRechaza` | Aprobado |
| CJ-09 | Caja (RF-09) | Movimiento: se exige una caja válida | `CajaServiceTest.movimientoSinCajaSeRechaza` | Aprobado |
| CJ-10 | Caja (RF-09) | Movimiento: se exige un usuario válido | `CajaServiceTest.movimientoSinUsuarioSeRechaza` | Aprobado |
| CJ-11 | Caja (RF-09) | Movimiento: el monto es obligatorio | `CajaServiceTest.movimientoSinMontoSeRechaza` | Aprobado |
| CJ-12 | Caja (RF-09) | Movimiento: el monto debe ser mayor que cero | `CajaServiceTest.movimientoConMontoCeroOMenorSeRechaza` | Aprobado |
| CJ-13 | Caja (RF-09) | Movimiento: un movimiento válido se registra y confirma | `CajaServiceTest.movimientoValidoSeRegistra` | Aprobado |
| CJ-14 | Caja (RF-09) | Movimiento: si el DAO falla se revierte | `CajaServiceTest.movimientoConFalloDelDaoRevierte` | Aprobado |
| CJ-15 | Caja (RF-09) | Cierre: se exige una caja válida | `CajaServiceTest.cierreSinCajaSeRechaza` | Aprobado |
| CJ-16 | Caja (RF-09) | Cierre: se exige el usuario que cierra | `CajaServiceTest.cierreSinUsuarioSeRechaza` | Aprobado |
| CJ-17 | Caja (RF-09) | Cierre: el monto contado es obligatorio y no puede ser negativo | `CajaServiceTest.cierreConMontoInvalidoSeRechaza` | Aprobado |
| CJ-18 | Caja (RF-09) | Cierre: un cierre válido se confirma | `CajaServiceTest.cierreValidoSeConfirma` | Aprobado |
| CJ-19 | Caja (RF-09) | Cierre: si el DAO falla se revierte | `CajaServiceTest.cierreConFalloDelDaoRevierte` | Aprobado |
| CJ-20 | Caja (RF-09) | Consulta: caja abierta de un usuario inválido devuelve null | `CajaServiceTest.cajaAbiertaDeUsuarioInvalidoDevuelveNull` | Aprobado |
| CJ-21 | Caja (RF-09) | Consulta: caja abierta de un usuario válido se delega al DAO | `CajaServiceTest.cajaAbiertaDeUsuarioValidoSeDelega` | Aprobado |
| CJ-22 | Caja (RF-09) | Consulta: listar movimientos se delega al DAO | `CajaServiceTest.listarMovimientosSeDelega` | Aprobado |
| CP-01 | Compras (RF-05) | Compra: se guarda la cabecera y todos los detalles | `CompraServiceTest.compraValidaGuardaCabeceraYDetalles` | Aprobado |
| CP-02 | Compras (RF-05) | Compra: cada detalle queda asociado al id de la compra | `CompraServiceTest.cadaDetalleRecibeElIdDeLaCompra` | Aprobado |
| CP-03 | Compras (RF-05) | Compra: si falla un detalle se revierte toda la compra | `CompraServiceTest.falloEnUnDetalleRevierteTodaLaCompra` | Aprobado |
| CP-04 | Compras (RF-05) | Compra: si falla la cabecera no se guarda ningún detalle | `CompraServiceTest.falloEnLaCabeceraNoGuardaDetalles` | Aprobado |
| CP-05 | Compras (RF-05) | Compra: una lista de detalles nula se rechaza y revierte | `CompraServiceTest.detallesNulosRevierten` | Aprobado |
| CP-06 | Compras (RF-05) | Compra: sin conexión a la base de datos devuelve false | `CompraServiceTest.sinConexionDevuelveFalse` | Aprobado |
| CP-07 | Compras (RF-05) | Comprobante: la consulta de duplicados se delega al DAO | `CompraServiceTest.existeNroComprobanteSeDelegaAlDao` | Aprobado |
| IN-01 | Inventario (RF-07) | Alta de lote: datos nulos o cantidad no positiva devuelven 0 | `InventarioServiceTest.altaDeLoteConDatosInvalidosDevuelveCero` | Aprobado |
| IN-02 | Inventario (RF-07) | Alta de lote: precio de venta = precio de compra + 20 % | `InventarioServiceTest.altaDeLoteCalculaPrecioDeVentaConMargenDel20` | Aprobado |
| IN-03 | Inventario (RF-07) | Alta de lote: el lote nuevo nace sin defectuosos ni descuento | `InventarioServiceTest.altaDeLoteInicializaStockYDescuento` | Aprobado |
| IN-04 | Inventario (RF-07) | Alta de lote: la ubicación se toma del producto | `InventarioServiceTest.altaDeLoteTomaLaUbicacionDelProducto` | Aprobado |
| IN-05 | Inventario (RF-07) | Alta de lote: un detalle sin precio de compra no lanza excepción | `InventarioServiceTest.altaDeLoteSinPrecioDeCompraNoLanzaExcepcion` | Aprobado |
| IN-06 | Inventario (RF-07) | Alta de lote dentro de una transacción usa la conexión recibida | `InventarioServiceTest.altaDeLoteConConexionUsaLaConexionRecibida` | Aprobado |
| IN-07 | Inventario (RF-07) | Alta de lote con conexión: datos inválidos devuelven 0 | `InventarioServiceTest.altaDeLoteConConexionYDatosInvalidosDevuelveCero` | Aprobado |
| IN-08 | Inventario (RF-07) | Ubicación: id inválido, estante o nivel vacíos se rechazan | `InventarioServiceTest.ubicacionInvalidaSeRechaza` | Aprobado |
| IN-09 | Inventario (RF-07) | Ubicación: se eliminan los espacios sobrantes antes de guardar | `InventarioServiceTest.ubicacionValidaSeGuardaSinEspacios` | Aprobado |
| IN-10 | Inventario (RF-07) | Stock: id inválido o stock negativo se rechazan | `InventarioServiceTest.ajusteDeStockInvalidoSeRechaza` | Aprobado |
| IN-11 | Inventario (RF-07) | Stock: sin usuario con sesión iniciada no se puede ajustar | `InventarioServiceTest.ajusteDeStockSinSesionSeRechaza` | Aprobado |
| IN-12 | Inventario (RF-07) | Stock: con sesión y datos válidos se delega al DAO | `InventarioServiceTest.ajusteDeStockValidoSeDelega` | Aprobado |
| IN-13 | Inventario (RF-07) | Descuento: porcentajes fuera de (0, 100] se rechazan | `InventarioServiceTest.descuentoFueraDeRangoSeRechaza` | Aprobado |
| IN-14 | Inventario (RF-07) | Descuento: sin sesión iniciada se rechaza | `InventarioServiceTest.descuentoSinSesionSeRechaza` | Aprobado |
| IN-15 | Inventario (RF-07) | Descuento: el porcentaje se redondea a 2 decimales | `InventarioServiceTest.descuentoSeRedondeaADosDecimales` | Aprobado |
| IN-16 | Inventario (RF-07) | Descuento: el 100 % es un valor permitido | `InventarioServiceTest.descuentoDelCienPorCientoEsValido` | Aprobado |
| IN-17 | Inventario (RF-07) | Descuento: al desactivarlo el porcentaje vuelve a cero | `InventarioServiceTest.desactivarDescuentoReiniciaElPorcentaje` | Aprobado |
| IN-18 | Inventario (RF-07) | Defectuosos: id o cantidad inválidos se rechazan | `InventarioServiceTest.retiroDefectuosoConDatosInvalidosSeRechaza` | Aprobado |
| IN-19 | Inventario (RF-07) | Defectuosos: sin sesión iniciada se rechaza | `InventarioServiceTest.retiroDefectuosoSinSesionSeRechaza` | Aprobado |
| IN-20 | Inventario (RF-07) | Defectuosos: un lote inexistente se rechaza | `InventarioServiceTest.retiroDefectuosoDeLoteInexistenteSeRechaza` | Aprobado |
| IN-21 | Inventario (RF-07) | Defectuosos: no se puede retirar más de lo registrado | `InventarioServiceTest.retiroMayorAlStockDefectuosoSeRechaza` | Aprobado |
| IN-22 | Inventario (RF-07) | Defectuosos: el retiro descuenta la cantidad del stock defectuoso | `InventarioServiceTest.retiroValidoDescuentaDelStockDefectuoso` | Aprobado |
| IN-23 | Inventario (RF-07) | Defectuosos: se puede retirar todo el stock defectuoso | `InventarioServiceTest.retiroDeTodoElStockDefectuosoQuedaEnCero` | Aprobado |
| IN-24 | Inventario (RF-07) | Consulta: buscar para dashboard con id inválido devuelve null | `InventarioServiceTest.buscarParaDashboardConIdInvalidoDevuelveNull` | Aprobado |
| PR-01 | Productos (RF-01) | Registrar: se delega al DAO y devuelve su resultado | `ProductoServiceTest.registrarSeDelegaAlDao` | Aprobado |
| PR-02 | Productos (RF-01) | Registrar: si el DAO falla devuelve false | `ProductoServiceTest.registrarConFalloDelDaoDevuelveFalse` | Aprobado |
| PR-03 | Productos (RF-01) | Listar: devuelve la lista del DAO | `ProductoServiceTest.listarDevuelveLaListaDelDao` | Aprobado |
| PR-04 | Productos (RF-01) | Actualizar: se delega al DAO | `ProductoServiceTest.actualizarSeDelegaAlDao` | Aprobado |
| PR-05 | Productos (RF-01) | Eliminar: se delega al DAO con el id indicado | `ProductoServiceTest.eliminarSeDelegaAlDao` | Aprobado |
| PR-06 | Productos (RF-01) | Código repetido: la verificación excluye al propio producto | `ProductoServiceTest.existeCodigoSeDelegaAlDao` | Aprobado |
| PR-07 | Productos (RF-01) | Dashboard: un id inválido devuelve null sin consultar la base | `ProductoServiceTest.buscarParaDashboardConIdInvalidoDevuelveNull` | Aprobado |
| PR-08 | Productos (RF-01) | Dashboard: un id válido se consulta en el DAO | `ProductoServiceTest.buscarParaDashboardConIdValidoConsultaElDao` | Aprobado |
| US-01 | Usuarios / Login (RF-04) | Un usuario con rol WEB no puede iniciar sesión en el escritorio | `UsuarioServiceTest.usuarioWebNoDebeIniciarSesionEnEscritorio` | Aprobado |
| US-02 | Usuarios / Login (RF-04) | Un usuario activo con rol Vendedor inicia sesión correctamente | `UsuarioServiceTest.usuarioActivoConRolVendedorPuedeIniciarSesion` | Aprobado |
| US-03 | Usuarios / Kardex (RF-10) | El listado de usuarios para Kardex incluye al usuario WEB | `KardexServiceTest.listarKardexDebeIncluirUsuarioWeb` | Aprobado |

## 5. Defectos detectados durante el diseño de las pruebas

| ID | Descripción | Severidad | Estado |
|---|---|---|---|
| DEF-01 | `InventarioService.registrarInventario(DetalleCompra, Compra, Producto, int)` lanzaba `NullPointerException` cuando el detalle de compra no tenía precio. La versión con `Connection` sí validaba el caso. | Media | Corregido (se asume precio 0). Cubierto por `InventarioServiceTest.altaDeLoteSinPrecioDeCompraNoLanzaExcepcion`. |
| OBS-01 | `KardexServiceTest` prueba `UsuarioService.listarKardex()`, no la lógica de `KardexService`. | Baja | Pendiente: agregar pruebas propias de `KardexService`. |

## 6. Ejecución y evidencias

Las pruebas fueron ejecutadas correctamente utilizando JDK 24 y Maven.

### Resultados de ejecución

| Resultado | Cantidad |
|---|---:|
| Pruebas ejecutadas | 106 |
| Pruebas fallidas | 0 |
| Errores | 0 |
| Pruebas omitidas | 0 |
| Estado | BUILD SUCCESS |

### Cobertura de código con JaCoCo

Se utilizó JaCoCo para medir la cobertura de los servicios principales:

| Servicio | Cobertura |
|---|---:|
| VentaService | 84% |
| CajaService | 85% |
| InventarioService | 91% |
| CompraService | 88% |
| ProductoService | 86% |

### Ubicación de los resultados

- Resultados por clase: `target/surefire-reports/`
- Informe de cobertura: `target/site/jacoco/index.html`

### Evidencias

Se adjuntan las siguientes capturas:

1. Resultado de ejecución de las pruebas con `BUILD SUCCESS`.
2. Informe JaCoCo con la cobertura general.
3. Cobertura individual de los cinco servicios principales.
4. Ejecución satisfactoria de GitHub Actions, cuando se haya verificado en el repositorio.

## 7. Limitaciones y pruebas pendientes

- Las pruebas de la capa `dao` requieren una base MySQL de prueba (integración); no se incluyen en este avance.
- Aún no se cubren `RecepcionService` ni la lógica de `KardexService`.
- Las pantallas Swing se excluyen de la cobertura (`pe/ferreteria/vista/**`).
- El módulo web (Spring Boot) solo cuenta con la prueba de arranque del contexto; sus pruebas se ampliarán en la siguiente etapa.
