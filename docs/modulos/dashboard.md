# Arquitectura del Módulo Dashboard – Ferretería Colorín

## 1. Descripción general

El módulo Dashboard permite visualizar y analizar la información comercial de Ferretería Colorín. Reúne datos de ventas, vendedores y productos para facilitar el seguimiento de los resultados del negocio.

Utiliza la información registrada en la base de datos existente, sin necesitar una tabla propia para el Dashboard.

## 2. Arquitectura del módulo

El módulo sigue una arquitectura por capas:

- **Modelo (`Dashboard.java`):** representa la información de ventas, productos, usuarios, costos, importes y ganancias.
- **DAO (`DashboardDAO.java`):** consulta la base de datos y obtiene los datos de las ventas y sus detalles.
- **Servicio (`DashboardService.java`):** procesa los datos y genera los gráficos estadísticos.
- **Controlador (`DashboardController.java`):** conecta la vista con los servicios del Dashboard.
- **Vista (`DashboardPanel.java`):** muestra los indicadores, las tablas, los gráficos, los filtros y las opciones para generar reportes.

### Servicio de reportes gerenciales

El módulo también utiliza `ReporteGerencialService.java`, que reúne información de los servicios de Ventas, Inventario y Kardex para preparar los datos de los reportes gerenciales.

La clase `DatosReporteGerencial.java` contiene los indicadores y las listas de información que utilizan los generadores PDF.

## 3. Funcionalidades principales

### 3.1. Resumen general

Muestra indicadores comerciales:

- Total vendido.
- Cantidad de ventas realizadas.
- Ganancia bruta.
- Ticket promedio.
- IGV generado.

### 3.2. Historial de ventas

Presenta las ventas agrupadas por comprobante, mostrando la fecha, el vendedor, el costo, el importe vendido y la ganancia.

Permite filtrar el historial por día, mes, año y usuario, además de consultar el detalle de una venta seleccionada.

### 3.3. Rendimiento de vendedores

Muestra información agrupada por vendedor, incluyendo la cantidad de registros de venta contabilizados, el importe vendido y la ganancia.

### 3.4. Productos más vendidos

Identifica los productos con mayor cantidad de unidades vendidas y muestra el importe correspondiente. La información se ordena principalmente por cantidad vendida.

### 3.5. Gráficos estadísticos

El módulo genera dos gráficos:

- **Ventas semanales:** presenta las ventas de lunes a sábado para la semana seleccionada.
- **Ventas mensuales:** agrupa las ventas del mes por semanas.

Los gráficos se generan con la biblioteca JFreeChart.

### 3.6. Detalle de venta

Permite consultar información de una venta, incluyendo:

- Fecha y hora.
- Comprobante y vendedor.
- Cliente y documento.
- Productos, lotes y cantidades.
- Precios, descuentos e importes.
- Subtotal, IGV y total.
- Método de pago, monto recibido y vuelto.

También permite abrir el PDF existente de la venta, cuando corresponde.

## 4. Reportes gerenciales en PDF

El Dashboard permite generar reportes gerenciales diarios, mensuales y anuales, aplicando los filtros disponibles.

El servicio de reportes reúne información de las siguientes áreas:

- **Ventas:** indicadores comerciales, detalle de ventas, productos más vendidos, rendimiento de vendedores y métodos de pago.
- **Inventario:** stock defectuoso actual y rotación de productos.
- **Kardex:** información sobre mermas registradas.

El reporte diario revisado incluye un resumen ejecutivo, detalle de ventas, productos más vendidos, rendimiento de vendedores cuando se consulta a todos los usuarios, métodos de pago, mermas, stock defectuoso y productos con baja o nula rotación.

Los archivos PDF se generan en carpetas organizadas por año dentro de `reportes`.

## 5. Tecnologías utilizadas

- Java y Swing para la aplicación de escritorio.
- MySQL para consultar la información registrada.
- JFreeChart para los gráficos estadísticos.
- OpenPDF para generar los reportes gerenciales en PDF.

## 6. Verificaciones técnicas pendientes

Antes de considerar el módulo completamente revisado, se deben corregir dos detalles identificados en `DashboardPanel.java`:

1. **Identificador de venta:** al crear la copia agrupada de una venta para el historial, se debe copiar también `idVenta`. De lo contrario, la consulta del detalle podría utilizar el identificador `0`.
2. **Panel de resumen:** el resumen de importes y el panel de botones se agregan a la misma región inferior del `BorderLayout`. Se deben organizar dentro de un panel contenedor para que ambos permanezcan visibles.

## 7. Conclusión

El módulo Dashboard centraliza información comercial y de inventario para facilitar el seguimiento de las ventas, el análisis del rendimiento de los vendedores, la identificación de productos más vendidos y la generación de reportes gerenciales. Su diseño por capas permite mantener separadas las consultas, la lógica de procesamiento y la presentación de los resultados.
