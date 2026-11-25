# Defectos y observaciones conocidos (tomados de docs/modulos)

Resumen de lo que ya estaba documentado en cada módulo, con su prioridad para pruebas. Estado: **Abierto** salvo indicación.

| ID | Módulo | Observación | Tipo | Prueba sugerida | Estado |
|----|--------|-------------|------|-----------------|--------|
| DEF-01 | Inventario | `registrarInventario` con precio de compra nulo lanzaba NPE | Defecto | `InventarioServiceTest` | **Corregido** (sem. 13) |
| DEF-02 | Cliente / Producto / Proveedor / Marca / Categoría / Usuario | El DAO valida duplicados solo entre activos, pero `UNIQUE` en BD incluye inactivos: registrar un DNI/RUC/código/nombre de un registro dado de baja falla al insertar | Defecto de integración | Prueba de integración con MySQL; o validar también inactivos y ofrecer reactivar | **Corregido** (sem. 14): las verificaciones ya consideran también registros inactivos |
| DEF-03 | Cliente / Proveedor / Compra | `existeDni`, `existeRuc`, `existeNroComprobante` devuelven `false` ante error SQL (se confunde con "no existe") | Defecto | Simular `SQLException` y exigir que se propague | **Corregido** (sem. 14): ante error SQL devuelven `true` (falla segura); `VerificacionDuplicadosDAOTest` |
| DEF-04 | Cliente / Producto / Proveedor | `actualizar()` no filtra por `activo`; `Cliente.buscarPorId` devuelve inactivos | Observación | Prueba de DAO con BD de pruebas | Abierto |
| DEF-05 | Dashboard | La copia agrupada de la venta no copia `idVenta` (detalle consulta id 0) | Defecto | Prueba manual + test de `DashboardService` | Abierto |
| DEF-06 | Dashboard | Resumen y botones comparten la región inferior del `BorderLayout` (uno queda oculto) | Defecto de UI | Prueba manual | Abierto |
| DEF-07 | Usuario | `UsuarioPanel` no comprueba el resultado de `cambiarPassword` antes de mostrar "éxito" | Defecto de UI | Prueba manual | Abierto |
| DEF-08 | Caja | Sin pruebas de ventas consecutivas, anulaciones/devoluciones ni aperturas simultáneas | Cobertura pendiente | `CajaServiceTest` (parcial) | Parcial |
| DEF-09 | Kardex | Reembolso de defectuosos podría no considerar descuentos; sin control de concurrencia por lote | Por verificar | Pruebas de `KardexService` | Abierto |
| DEF-10 | Venta | `costo_envio` y SUNAT sin implementar en el flujo web | Funcionalidad pendiente | — | Pendiente |
| DEF-11 | Usuario | Contraseñas en texto plano | Seguridad | `SeguridadTest`, `UsuarioServiceSeguridadTest` | **Corregido** (sem. 14) |
| DEF-12 | Caja | El monto esperado de la caja suma todas las ventas como "Ingreso", sin distinguir el método de pago: tarjeta y transferencia se cuentan como efectivo en el cuadre | Simplificación | Prueba manual; mejora futura: separar efectivo y otros medios | Aceptado (simulación académica) |
| DEF-13 | Venta / Caja | El pago con tarjeta está simplificado (sin pasarela real ni tipo de tarjeta) y la venta se asocia a la caja abierta del sistema, no a la caja propia del usuario que vende | Simplificación | — | Aceptado (simulación académica) |

Origen: secciones "Observaciones / Consideraciones técnicas" de `docs/modulos/*.md`.
