# Arquitectura del Sistema de Gestión de Inventario y Ventas para Ferretería Colorín

## 1. Descripción general

El sistema de Ferretería Colorín está compuesto por dos proyectos independientes que comparten la base de datos MySQL `bd_colorin`:

1. **FERRETERIA-SISTEMA:** aplicación de escritorio desarrollada en Java Swing, orientada a la gestión interna de la ferretería.
2. **Backend web - Ferretería Colorín:** backend desarrollado con Spring Boot, encargado de proporcionar la lógica y los servicios necesarios para la tienda web.

Ambos proyectos utilizan una base de datos común para centralizar la información compartida. El esquema incluye tablas para productos, categorías, marcas, clientes, usuarios, ventas e inventario, además de tablas específicas para gestionar las cuentas, direcciones y pedidos de la tienda web.

La arquitectura separa las aplicaciones por sus responsabilidades, mientras que MySQL sirve como punto común para almacenar los datos.

## 2. Tecnologías utilizadas

### 2.1. Aplicación de escritorio

- **Lenguaje:** Java 24 (JDK 24).
- **Interfaz gráfica:** Java Swing.
- **Gestión del proyecto:** Apache Maven.
- **Base de datos:** MySQL.
- **Conector de base de datos:** MySQL Connector/J 9.5.0.
- **Generación de documentos PDF:** OpenPDF 3.0.5.
- **Gráficos estadísticos:** JFreeChart 1.5.6.
- **Registro de eventos:** Logback, configurado mediante `logback.xml`.

El proyecto Maven utiliza el grupo `pe.ferreteria`, la versión `1.0-SNAPSHOT`, el empaquetado `jar` y la clase principal `pe.ferreteria.principal.Principal`.

La ejecución de `dependency:tree` con Maven 3.9.9 finalizó con `BUILD SUCCESS`, confirmando la resolución y visualización del árbol de dependencias en esa ejecución.

### 2.2. Backend web

- **Framework:** Spring Boot.
- **Base de datos:** MySQL.
- **Función:** gestionar la lógica del backend de la tienda web y sus operaciones con los datos.

La estructura interna y las dependencias específicas del backend se documentarán según su configuración real, sin asumir componentes que no se hayan verificado.

### 2.3. Dependencias adicionales del proyecto de escritorio

El árbol de dependencias incluye bibliotecas transitivas como Protobuf Java 4.31.1 y componentes de Brotli4j 1.23.0, entre ellos `service` y `native-windows-x86_64`.

## 3. Arquitectura general

Los proyectos mantienen sus propios códigos fuente y responsabilidades, pero comparten la base de datos `bd_colorin`.

```text
FERRETERIA-SISTEMA
Java Swing · Maven
Gestión interna
        │
        │
        ▼
   MySQL: bd_colorin
        ▲
        │
        │
Backend web - Ferretería Colorín
Spring Boot
Servicios de la tienda web
```

La aplicación de escritorio organiza sus operaciones mediante capas. El backend web mantiene su propia estructura, basada en Spring Boot.

La comunicación de cada aplicación con MySQL se realiza mediante sus respectivos componentes de acceso a datos. El diagrama representa la arquitectura general; los detalles concretos de cada integración dependen de la configuración de ambos proyectos.

## 4. Estructura del proyecto de escritorio

El proyecto `FERRETERIA-SISTEMA` mantiene una estructura Maven organizada por paquetes:

```text
FERRETERIA-SISTEMA/
├── comprobantes/2026/
├── etiquetas_defectuosas/2026/
├── reportes/2026/
├── src/
│   ├── main/
│   │   ├── java/pe/ferreteria/
│   │   │   ├── modelo/
│   │   │   ├── dao/
│   │   │   ├── servicio/
│   │   │   ├── controlador/
│   │   │   ├── vista/
│   │   │   ├── infraestructura/
│   │   │   ├── util/
│   │   │   └── principal/
│   │   └── resources/
│   │       ├── configuracion.properties
│   │       └── logback.xml
│   └── test/java/pe/ferreteria/
├── database/
│   ├── 01_creacion_bd.sql
│   ├── 02_tablas.sql
│   └── 03_datos_iniciales.sql
├── docs/
│   ├── modulos/
│   ├── README.md
│   ├── arquitectura.md
│   └── manual_usuario.md
├── pom.xml
└── README.md
```

### 4.1. Responsabilidad de los paquetes

| Paquete | Responsabilidad |
|---|---|
| `modelo` | Representar las entidades del negocio. |
| `dao` | Ejecutar consultas y operaciones de persistencia en MySQL. |
| `servicio` | Aplicar reglas de negocio y coordinar operaciones. |
| `controlador` | Conectar las vistas con los servicios. |
| `vista` | Contener las ventanas, paneles y diálogos de Java Swing. |
| `infraestructura` | Gestionar conexión, configuración, seguridad y sesión del usuario. |
| `util` | Proporcionar validadores y herramientas para generar documentos. |
| `principal` | Contener el punto de entrada de la aplicación. |

El proyecto incluye pruebas para los servicios de Productos, Ventas, Compras e Inventario.

## 5. Diseño de la base de datos

La base de datos común se denomina `bd_colorin`. Su esquema contempla las operaciones internas de la ferretería y las necesidades de la tienda web.

### 5.1. Tablas compartidas

| Área funcional | Tablas |
|---|---|
| Clasificación de productos | `Categoria`, `Marca`, `Producto` |
| Información técnica y visual | `Detalle_Tecnico_Producto`, `Imagen_Producto_Web` |
| Personas y usuarios | `Proveedor`, `Usuario`, `Cliente` |
| Compras y recepción | `Compra`, `Detalle_Compra`, `Recepcion`, `Detalle_Recepcion` |
| Inventario y trazabilidad | `Inventario`, `Kardex` |
| Ventas | `Venta`, `Detalle_Venta` |
| Caja | `Caja`, `Movimiento_Caja` |

### 5.2. Tablas específicas de la tienda web

| Tabla | Propósito |
|---|---|
| `Cliente_Web` | Gestionar la cuenta y el perfil del comprador. |
| `Direccion_Cliente_Web` | Almacenar las direcciones guardadas por el cliente. |
| `Pedido_Web` | Registrar pedidos, estados de pago y entrega, y su vínculo con la venta confirmada. |
| `Detalle_Pedido_Web` | Almacenar los productos, cantidades y precios de cada pedido. |

Las tablas específicas de la web complementan las entidades compartidas y permiten gestionar el proceso de compra en línea.

### 5.3. Catálogo de productos

El catálogo inicial incluye 47 productos documentados, cada uno asociado con su información comercial, imagen y ficha técnica.

`Detalle_Tecnico_Producto` contiene información como características, ventajas, lugares de uso, recomendaciones, advertencias, observaciones, garantía y datos técnicos estructurados en formato JSON.

`Imagen_Producto_Web` almacena la ruta de la imagen vinculada a cada producto.

Estos registros conforman el catálogo inicial documentado del sistema.

### 5.4. Integridad de los datos

El esquema utiliza claves foráneas, restricciones de unicidad y comprobaciones de integridad para mantener las relaciones entre entidades.

Entre las restricciones existentes se encuentran las relacionadas con los códigos de producto, los documentos de clientes, los RUC de proveedores y los números de comprobante.

Las operaciones que modifican varias tablas deben respetar las relaciones y reglas de negocio para evitar inconsistencias entre las aplicaciones.

## 6. Integración entre escritorio y tienda web

La aplicación de escritorio y el backend web son proyectos independientes que utilizan la misma base de datos. Esto permite compartir información sin necesidad de unir ambos códigos fuente en un solo proyecto.

El escritorio se orienta a las operaciones internas, mientras que el backend web gestiona los procesos correspondientes a la tienda en línea.

### 6.1. Flujo de pedidos web

Según el diseño del esquema, el flujo previsto para confirmar un pedido es:

1. Registrar y consultar el pedido web y sus detalles.
2. Gestionar el estado del pago.
3. Vincular el pedido con la venta correspondiente cuando se confirme.
4. Registrar los detalles de la venta y actualizar las existencias.
5. Registrar los movimientos de inventario relacionados en Kardex.

Las modificaciones relacionadas deben coordinarse de acuerdo con las reglas de negocio y las transacciones implementadas en el backend.

### 6.2. Usuario técnico de operaciones web

La tabla `Usuario` incluye una cuenta técnica denominada `OperacionWEB`, con el rol `WEB`. Según los comentarios del esquema, esta cuenta está destinada a identificar operaciones originadas desde la tienda web, como registros de ventas y movimientos en Kardex.

El uso concreto de esta cuenta debe corresponder a la implementación del backend.

## 7. Capas de la aplicación de escritorio

El proyecto de escritorio utiliza una arquitectura por capas:

**Vista → Controlador → Servicio → DAO → MySQL**

- **Vista:** presenta formularios, tablas, diálogos e indicadores.
- **Controlador:** recibe las acciones del usuario y las dirige al servicio correspondiente.
- **Servicio:** valida y coordina las reglas de negocio.
- **DAO:** ejecuta las operaciones sobre la base de datos.
- **MySQL:** conserva los registros y las relaciones entre entidades.

La clase `ConexionBD.java` se encuentra en `infraestructura` y proporciona el acceso compartido a la base de datos desde el proyecto de escritorio.

## 8. Descripción de los módulos de escritorio

### 8.1. Categorías y Marcas

Permite administrar las categorías y marcas utilizadas para clasificar productos. Sus operaciones se realizan desde `ProductoPanel.java`, mediante `CategoriaController.java` y `MarcaController.java`, sus servicios y DAO correspondientes.

Los identificadores de categoría y marca se relacionan con los productos. Las eliminaciones son lógicas y los registros inactivos continúan sujetos a las restricciones de unicidad.

### 8.2. Productos

Administra el catálogo mediante `ProductoPanel.java`. Permite registrar, consultar, editar, buscar y desactivar productos, además de gestionar su clasificación y ubicación.

Utiliza `Producto.java`, `ProductoDAO.java`, `ProductoService.java` y `ProductoController.java`. Los códigos de los registros inactivos no pueden reutilizarse mientras permanezca la restricción de unicidad.

### 8.3. Proveedores

Gestiona la información de las empresas proveedoras desde `ProveedorPanel.java`, incluyendo sus datos de contacto e identificación.

Utiliza `Proveedor.java`, `ProveedorDAO.java`, `ProveedorService.java` y `ProveedorController.java`. Se integra con Compras e Inventario para identificar el origen de la mercadería.

### 8.4. Clientes

Administra clientes de tipo Persona Natural y Persona Jurídica mediante `ClientePanel.java`, con validaciones de identificación y contacto.

Utiliza `Cliente.java`, `ClienteDAO.java`, `ClienteService.java` y `ClienteController.java`. Los datos de clientes se utilizan al registrar ventas.

### 8.5. Usuarios

Gestiona cuentas, autenticación y acceso por roles. Participan `UsuarioPanel.java`, `LoginForm.java`, `MainForm.java`, `Usuario.java`, `UsuarioDAO.java`, `UsuarioService.java`, `UsuarioController.java` y `LoginController.java`.

Los roles Administrador, Vendedor y Almacen controlan la navegación. Las cuentas con rol `WEB` se excluyen del inicio de sesión habitual de escritorio, aunque pueden aparecer en consultas de Kardex.

Quedan pendientes mejoras en el almacenamiento seguro de contraseñas y la validación del resultado de su actualización.

### 8.6. Compras

Registra las adquisiciones a proveedores y sus detalles, incluyendo productos, cantidades, precios, lotes y vencimientos cuando corresponda.

Utiliza `CompraPanel.java`, los modelos `Compra.java` y `DetalleCompra.java`, los DAO correspondientes, `CompraService.java` y `CompraController.java`.

La cabecera y sus detalles se registran mediante una transacción. Los importes utilizan `BigDecimal`. La compra no incrementa directamente el inventario: primero debe completarse la recepción física.

### 8.7. Recepción

Controla la llegada de la mercadería y permite registrar cantidades recibidas, observaciones e incidencias.

Utiliza `RecepcionPanel.java`, `Recepcion.java`, `DetalleRecepcion.java`, sus DAO, `RecepcionService.java` y `RecepcionController.java`.

Maneja los estados Pendiente, Observada y Completada. Al confirmar el ingreso, registra el lote en Inventario y la entrada correspondiente en Kardex. Las operaciones relacionadas se coordinan mediante transacciones.

### 8.8. Inventario

Administra existencias por lote, stock normal y defectuoso, precios, descuentos, vencimientos y ubicación física.

Utiliza `InventarioPanel.java`, `Inventario.java`, `InventarioDAO.java`, `InventarioService.java` e `InventarioController.java`.

El stock total de un producto se obtiene sumando sus lotes. Los productos defectuosos se controlan por separado y los descuentos se aplican individualmente por lote.

### 8.9. Ventas

Registra ventas, calcula los importes, desglosa el IGV incluido en el precio de venta, controla existencias por lote, gestiona pagos y genera comprobantes PDF.

Utiliza `VentaPanel.java`, `Venta.java`, `DetalleVenta.java`, `VentaDAO.java`, `DetalleVentaDAO.java`, `VentaService.java`, `VentaController.java`, `GeneradorComprobante.java` y `GeneradorPDF.java`.

**Tratamiento del IGV:** el precio de venta incluye el IGV del 18 %. Por ello, el sistema desglosa el impuesto del importe total cobrado, en lugar de añadirlo nuevamente al precio. Para un importe que incluye IGV, el impuesto se obtiene mediante la fórmula: `IGV = Total × 18 / 118`, y el subtotal sin IGV corresponde a `Total − IGV`.

El módulo utiliza `BigDecimal` para los importes y requiere una caja abierta para completar una venta. La operación se integra con Inventario, Kardex, Caja y Dashboard.

La generación de comprobantes PDF está implementada; los campos relacionados con SUNAT no demuestran que la emisión electrónica esté operativa.

### 8.10. Caja

Administra apertura, cierre, ingresos, salidas, monto esperado y diferencias de cierre.

Utiliza `CajaDialog.java`, `Caja.java`, `MovimientoCaja.java`, `CajaDAO.java`, `MovimientoCajaDAO.java` y `CajaService.java`.

El monto esperado se calcula sumando la apertura y los ingresos, y restando las salidas. La diferencia corresponde al monto contado menos el esperado.

La integración actual se centra en las ventas; los pagos a proveedores y otros gastos están pendientes.

### 8.11. Kardex

Registra y consulta entradas, salidas, ajustes, reversiones y movimientos relacionados con productos defectuosos. Mantiene la trazabilidad del stock anterior y posterior a cada movimiento.

Utiliza `KardexPanel.java`, `Kardex.java`, `KardexDAO.java`, `KardexService.java` y `KardexController.java`.

Se integra con Recepción, Inventario, Ventas, Caja, Productos y Usuarios. Quedan pendientes la revisión de los cálculos de reembolso con descuentos y el refuerzo de las validaciones de stock y concurrencia por lote.

### 8.12. Dashboard

Centraliza indicadores comerciales y de inventario, incluyendo ventas, ganancias, rendimiento de vendedores, productos vendidos y rotación.

Utiliza `DashboardPanel.java`, `Dashboard.java`, `DashboardDAO.java`, `DashboardService.java` y `DashboardController.java`. Los reportes gerenciales se apoyan en `ReporteGerencialService.java` y `DatosReporteGerencial.java`.

Emplea JFreeChart para los gráficos y OpenPDF para los informes. Quedan pendientes la corrección de la copia de `idVenta` al agrupar ventas y la reorganización de algunos componentes de la interfaz.

## 9. Flujos operativos principales

### 9.1. Compra y recepción

1. Se registra la compra y sus detalles.
2. Se revisa la mercadería recibida.
3. Se documentan cantidades e incidencias.
4. Se confirma la recepción.
5. Se crea o actualiza el lote correspondiente en Inventario.
6. Se registra la entrada en Kardex.

### 9.2. Venta de escritorio

1. Se seleccionan el cliente y los productos.
2. Se calculan los importes, descuentos e IGV.
3. Se valida el pago y la apertura de Caja.
4. Se registra la venta y se actualizan las existencias.
5. Se registra la salida en Kardex y el ingreso en Caja.
6. Se genera el comprobante PDF.

### 9.3. Pedido web

El backend web gestiona los pedidos mediante las tablas específicas de la tienda y se relaciona con las tablas compartidas cuando corresponde confirmar la operación comercial. El flujo de confirmación debe mantener la coherencia entre el pedido, la venta, el inventario y Kardex.

## 10. Seguridad, validaciones y transacciones

El sistema incorpora roles de usuario, validaciones de datos, claves foráneas, restricciones de unicidad y eliminación lógica en diversas entidades.

En las operaciones que afectan a varias tablas se emplean transacciones con `commit` y `rollback` para reducir el riesgo de registros parciales.

Los importes monetarios se gestionan con `BigDecimal`. La aplicación de escritorio y el backend web deben respetar las reglas de negocio al modificar datos compartidos.

Entre las mejoras identificadas están el almacenamiento seguro de contraseñas, la validación del cambio de contraseña y el refuerzo de las comprobaciones de stock y concurrencia por lote.

## 11. Pruebas automatizadas

El proyecto de escritorio incluye las siguientes clases de prueba:

- `ProductoServiceTest.java`
- `VentaServiceTest.java`
- `CompraServiceTest.java`
- `InventarioServiceTest.java`

Estas pruebas permiten verificar parte del comportamiento de los servicios principales. Se ha informado que la prueba de `listarKardex()` finalizó correctamente.

La presencia de estas clases no implica que todas las funcionalidades estén cubiertas por pruebas automatizadas.

## 12. Mejoras técnicas pendientes

Los resúmenes de los módulos identifican los siguientes puntos:

- **Usuarios:** reforzar el almacenamiento seguro de contraseñas y validar el resultado de su actualización.
- **Kardex:** verificar los cálculos de reembolsos con descuentos y reforzar las validaciones de stock y concurrencia.
- **Caja:** completar la integración de pagos a proveedores y otros gastos.
- **Dashboard:** corregir la asociación de `idVenta` al agrupar información y reorganizar la distribución de componentes.
- **Integración web:** documentar con mayor detalle el flujo efectivo entre el backend, las tablas compartidas y las operaciones de inventario.
- **Pruebas:** ampliar la cobertura según las necesidades de ambos proyectos.

Estos puntos corresponden a mejoras identificadas y no deben interpretarse como funcionalidades terminadas.

## 13. Conclusión

La solución de Ferretería Colorín se organiza en dos proyectos independientes: una aplicación de escritorio Java Swing para las operaciones internas y un backend web desarrollado con Spring Boot. Ambos comparten la base de datos MySQL `bd_colorin`, que centraliza la información común y contiene tablas específicas para los pedidos y las cuentas de la tienda web.

La arquitectura del escritorio separa las responsabilidades mediante capas y módulos, mientras que la integración con la tienda web permite relacionar las operaciones en línea con los datos compartidos. El catálogo de 47 productos, sus imágenes y sus fichas técnicas complementan la gestión operativa con información comercial y técnica estructurada.

Este documento describe la organización general, las responsabilidades de los módulos y los principales flujos de información, además de identificar las mejoras técnicas que pueden abordarse en futuras etapas del proyecto.