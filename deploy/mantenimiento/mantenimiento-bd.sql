-- Mantenimiento mensual de bd_colorin (ejecutar como administrador, fuera de horario)
USE bd_colorin;
ANALYZE TABLE Producto, Inventario, Venta, Detalle_Venta, Compra, Kardex, Cliente;
-- Revision de integridad basica
CHECK TABLE Producto, Inventario, Venta, Detalle_Venta, Compra, Kardex, Cliente;
-- Deteccion de datos inconsistentes (deben devolver 0 filas)
SELECT id_inventario FROM Inventario WHERE stock < 0;
SELECT id_inventario FROM Inventario WHERE stock_defectuoso < 0;
