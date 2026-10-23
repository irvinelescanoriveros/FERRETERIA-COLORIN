-- ============================================
-- FERRETERÍA COLORÍN
-- BASE DE DATOS - CREACIÓN DE TABLAS
-- ============================================
drop database bd_colorin;
CREATE DATABASE bd_colorin;
USE bd_colorin;


-- ============================================
-- TABLA: CATEGORIA
-- ============================================

CREATE TABLE Categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo TINYINT(1) NOT NULL DEFAULT 1
);
-- ============================================
-- TABLA: MARCA
-- ============================================
CREATE TABLE Marca (
    id_marca INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo TINYINT(1) NOT NULL DEFAULT 1
);
-- ============================================
-- TABLA: PRODUCTO
-- Producto = raíz del artículo
-- La información específica de cada compra
-- pertenece al lote/inventario.
-- ============================================

CREATE TABLE Producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    id_categoria INT NOT NULL,
    id_marca INT NOT NULL,
    unidad VARCHAR(50) NOT NULL,
    -- Ubicación general/por defecto del producto
    estante VARCHAR(20),
    nivel VARCHAR(20),
    descripcion VARCHAR(255),
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES Categoria(id_categoria),
    CONSTRAINT fk_producto_marca
        FOREIGN KEY (id_marca)
        REFERENCES Marca(id_marca)
);


-- ============================================
-- TABLA: PROVEEDOR
-- ============================================

CREATE TABLE Proveedor (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    empresa VARCHAR(150) NOT NULL,
    ruc CHAR(11) NOT NULL UNIQUE,
    contacto VARCHAR(100),
    telefono VARCHAR(15),
    correo VARCHAR(100),
    direccion VARCHAR(150),
    activo TINYINT(1) NOT NULL DEFAULT 1
);


-- ============================================
-- TABLA: USUARIO
-- ============================================

CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM(
        'Administrador',
        'Vendedor',
        'Almacen'
    ) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1
);


-- ============================================
-- TABLA: CLIENTE
-- ============================================

CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    tipo_cliente ENUM(
        'Natural',
        'Juridica'
    ) NOT NULL,
    dni CHAR(8) UNIQUE,
    ruc CHAR(11) UNIQUE,
    nombre VARCHAR(100),
    apellidos VARCHAR(100),
    razon_social VARCHAR(150),
    contacto VARCHAR(100),
    telefono VARCHAR(15),
    direccion VARCHAR(150),
    activo TINYINT(1) NOT NULL DEFAULT 1
);

-- ============================================
-- TABLA: COMPRA
--
-- Registra una compra realizada por el Administrador.
-- La compra ya fue realizada/pagada al proveedor.
--
-- IMPORTANTE:
-- Esta tabla NO controla la recepción física.
-- La recepción se controla mediante Recepcion
-- y Detalle_Recepcion.
-- ============================================

CREATE TABLE Compra (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_compra DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nro_comprobante VARCHAR(30) NOT NULL,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_compra_proveedor
        FOREIGN KEY (id_proveedor)
        REFERENCES Proveedor(id_proveedor),
    CONSTRAINT fk_compra_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario)
);

-- ============================================
-- TABLA: DETALLE_COMPRA
--
-- Contiene los productos y lotes que forman
-- parte de una compra.
--
-- Esta tabla NO indica si Almacén ya recibió
-- físicamente el producto.
-- Eso se controla en Detalle_Recepcion.
-- ============================================

CREATE TABLE Detalle_Compra (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_producto INT NOT NULL,
    lote VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2)
        AS (cantidad * precio_compra) STORED,
    fecha_vencimiento DATE NULL,
    CONSTRAINT fk_detalle_compra_compra
        FOREIGN KEY (id_compra)
        REFERENCES Compra(id_compra),
    CONSTRAINT fk_detalle_compra_producto
        FOREIGN KEY (id_producto)
        REFERENCES Producto(id_producto),
    CHECK (cantidad > 0),
    CHECK (precio_compra >= 0),
    UNIQUE (id_compra, id_producto, lote)
);


-- ============================================
-- TABLA: RECEPCION
--
-- Representa el proceso físico de recepción
-- de una compra.
--
-- La realiza Almacén.
-- ============================================

CREATE TABLE Recepcion (
    id_recepcion INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_recepcion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM(
        'Pendiente',
        'Observada',
        'Completada'
    ) NOT NULL DEFAULT 'Pendiente',
    observacion VARCHAR(255),
    CONSTRAINT fk_recepcion_compra
        FOREIGN KEY (id_compra)
        REFERENCES Compra(id_compra),
    CONSTRAINT fk_recepcion_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario)
);


-- ============================================
-- TABLA: DETALLE_RECEPCION
--
-- Almacén verifica cada producto/lote.
--
-- Pendiente:
-- Todavía no ha sido verificado.
--
-- Observada:
-- Almacén encontró una diferencia o problema.
--
-- Completada:
-- Almacén verificó y acomodó correctamente
-- el lote.
-- ============================================

CREATE TABLE Detalle_Recepcion (
    id_detalle_recepcion INT AUTO_INCREMENT PRIMARY KEY,
    id_recepcion INT NOT NULL,
    id_detalle_compra INT NOT NULL,
    estado ENUM(
        'Pendiente',
        'Observada',
        'Completada'
    ) NOT NULL DEFAULT 'Pendiente',
    fecha_confirmacion DATETIME NULL,
cantidad_recibida INT NULL,
    observacion VARCHAR(255),
    CONSTRAINT fk_detalle_recepcion_recepcion
        FOREIGN KEY (id_recepcion)
        REFERENCES Recepcion(id_recepcion),
    CONSTRAINT fk_detalle_recepcion_compra
        FOREIGN KEY (id_detalle_compra)
        REFERENCES Detalle_Compra(id_detalle),
    UNIQUE (id_recepcion, id_detalle_compra)
);


-- ============================================
-- TABLA: INVENTARIO
--
-- Representa la existencia real de cada lote
-- recibido y disponible en la ferretería.
--
-- Cada registro corresponde a un lote específico
-- de un producto y contiene su stock, precios,
-- ubicación y fecha de vencimiento.
--
-- IMPORTANTE:
-- El registro se crea cuando Almacén completa
-- la recepción del lote.
--
-- STOCK:
-- stock = cantidad disponible para la venta.
--
-- PRODUCTOS DEFECTUOSOS:
-- stock_defectuoso = cantidad dañada o no apta
-- para la venta. No se considera dentro del
-- stock disponible.
--
-- PRECIO:
-- precio_compra = precio registrado en la compra.
-- precio_venta = precio normal de venta.
--
-- DESCUENTO:
-- descuento_activo controla si el lote tiene
-- una promoción vigente:
-- 0 = Desactivado
-- 1 = Activado
--
-- descuento_porcentaje = porcentaje aplicado
-- al precio de venta cuando el descuento está
-- activo.
--
-- IMPORTANTE:
-- El precio con descuento no se almacena;
-- Java lo calcula cuando el lote se utiliza
-- en una venta.
--
-- UBICACIÓN:
-- estante y nivel indican dónde se encuentra
-- físicamente el lote.
--
-- ESTADO:
-- activo permite mantener el registro del lote
-- sin eliminarlo físicamente de la base de datos.
--
-- CONTROL DE STOCK:
-- El stock disponible se reduce al realizar
-- una venta y puede recuperarse mediante una
-- reversión cuando corresponda.
--
-- El historial de entradas, salidas, ajustes
-- y reversiones se registra en la tabla Kardex.
-- ============================================
CREATE TABLE Inventario (
    id_inventario INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_proveedor INT NOT NULL,
    lote VARCHAR(50) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_defectuoso INT NOT NULL DEFAULT 0,
    precio_compra DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    descuento_activo TINYINT(1) NOT NULL DEFAULT 0,
    descuento_porcentaje DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NULL,
    estante VARCHAR(20) NULL,
    nivel VARCHAR(20) NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_inventario_producto
        FOREIGN KEY (id_producto)
        REFERENCES Producto(id_producto),
    CONSTRAINT fk_inventario_proveedor
        FOREIGN KEY (id_proveedor)
        REFERENCES Proveedor(id_proveedor),
    UNIQUE (id_producto, lote),
    CHECK (stock >= 0),
    CHECK (stock_defectuoso >= 0),
    CHECK (precio_compra >= 0),
    CHECK (precio_venta >= 0),
    CHECK (
        descuento_porcentaje >= 0
        AND descuento_porcentaje <= 100
    )
);


-- ============================================
-- TABLA: VENTA
--
-- La venta se registra directamente cuando
-- el vendedor realiza el cobro.
--
-- estado_comprobante queda preparado para
-- una futura integración con SUNAT.
-- ============================================

CREATE TABLE Venta (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_venta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    igv DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tipo_comprobante ENUM(
        'Boleta',
        'Factura'
    ) NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numero INT NOT NULL,
    nro_comprobante VARCHAR(30) NOT NULL UNIQUE,
    metodo_pago ENUM(
        'Efectivo',
        'Tarjeta',
        'Transferencia',
        'Yape'
    ) NOT NULL DEFAULT 'Efectivo',
    monto_recibido DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    vuelto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estado_comprobante ENUM(
        'Pendiente',
        'Aceptado',
        'Aceptado con observación',
        'Rechazado',
        'Anulado'
    ) NOT NULL DEFAULT 'Pendiente',
    fecha_envio_sunat DATETIME NULL,
    fecha_respuesta_sunat DATETIME NULL,
    codigo_respuesta_sunat VARCHAR(20) NULL,
    mensaje_respuesta_sunat VARCHAR(500) NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES Cliente(id_cliente),
    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario),
    CHECK (monto_recibido >= 0),
    CHECK (vuelto >= 0),
    UNIQUE (serie, numero)
);

-- ============================================
-- TABLA: DETALLE_VENTA
-- ============================================

CREATE TABLE Detalle_Venta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_inventario INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2)
        AS (cantidad * precio_unitario) STORED,
    descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    importe_total DECIMAL(10,2)
        AS ((cantidad * precio_unitario) - descuento) STORED,
    CONSTRAINT fk_detalle_venta_venta
        FOREIGN KEY (id_venta)
        REFERENCES Venta(id_venta),
    CONSTRAINT fk_detalle_venta_inventario
        FOREIGN KEY (id_inventario)
        REFERENCES Inventario(id_inventario),
    CHECK (cantidad > 0),
    CHECK (precio_unitario >= 0),
    CHECK (descuento >= 0),
    CHECK (descuento <= (cantidad * precio_unitario))
);

-- ============================================
-- TABLA: KARDEX
--
-- Registra el historial de movimientos del
-- inventario por producto y lote.
--
-- MOVIMIENTO:
--
-- ENTRADA:
-- Ingreso de mercadería al recibir una compra.
--
-- SALIDA:
-- Reducción del stock disponible, por ejemplo
-- por una venta o una reposición al cliente.
--
-- REVERSION:
-- Recuperación del stock cuando corresponde
-- revertir una venta.
--
-- AJUSTE:
-- Movimiento autorizado que modifica el stock
-- sin corresponder directamente a una compra,
-- venta o reversión.
--
-- MOTIVO DEL MOVIMIENTO:
--
-- COMPRA:
-- Ingreso de mercadería correspondiente
-- a una compra recibida.
--
-- VENTA:
-- Salida de productos realizada por una venta.
--
-- PRODUCTO DEFECTUOSO:
-- Una unidad vendida pasa de stock disponible
-- a stock defectuoso.
--
-- REPOSICION:
-- Salida de una unidad para reemplazar al
-- cliente un producto defectuoso.
--
-- REVERSION:
-- Recuperación del stock correspondiente
-- a una venta revertida.
--
-- RETIRO DEFECTUOSO:
-- Retiro definitivo de un producto registrado
-- como defectuoso.
--
-- CORRECCION:
-- Corrección autorizada de una diferencia
-- detectada en el inventario.
--
-- TRAZABILIDAD:
-- Cada movimiento conserva el stock anterior
-- y posterior, así como el stock defectuoso
-- anterior y posterior.
--
-- El Kardex controla únicamente el movimiento
-- físico de los productos.
-- Los movimientos de dinero se registran
-- independientemente en Caja.
-- ============================================
CREATE TABLE Kardex (
    id_kardex INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_inventario INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_movimiento DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,
    movimiento ENUM(
        'Entrada',
        'Salida',
        'Ajuste',
        'Reversion'
    ) NOT NULL,
    motivo_movimiento ENUM(
         'Compra',
    'Venta',
    'Producto Defectuoso',
    'Reposicion p. client.',
    'Reingreso',
    'Garantia',
    'Reversion',
    'Retiro Defectuoso',
    'Correccion',
    'Dar de baja',
    'Otros'
    ) NOT NULL,
    cantidad INT NOT NULL,
    stock_antes INT NOT NULL,
    stock_despues INT NOT NULL,
    stock_defectuoso_antes INT NOT NULL
        DEFAULT 0,
    stock_defectuoso_despues INT NOT NULL
        DEFAULT 0,
    precio_unitario DECIMAL(10,2) NOT NULL
        DEFAULT 0.00,
    monto_total DECIMAL(10,2)
        AS (cantidad * precio_unitario) STORED,
    tipo_referencia ENUM(
        'Compra',
        'Venta',
        'Ajuste'
    ) NOT NULL,
    id_referencia INT NULL,
    nro_comprobante VARCHAR(30) NULL,
    motivo VARCHAR(255) NULL,
    CONSTRAINT fk_kardex_producto
        FOREIGN KEY (id_producto)
        REFERENCES Producto(id_producto),
    CONSTRAINT fk_kardex_inventario
        FOREIGN KEY (id_inventario)
        REFERENCES Inventario(id_inventario),
    CONSTRAINT fk_kardex_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario),
    CHECK (cantidad > 0),
    CHECK (stock_antes >= 0),
    CHECK (stock_despues >= 0),
    CHECK (stock_defectuoso_antes >= 0),
    CHECK (stock_defectuoso_despues >= 0),
    CHECK (precio_unitario >= 0)
);

-- ============================================
-- TABLA: CAJA
--
-- Controla cada apertura y cierre de caja.
--
-- Una caja representa una jornada/sesión de
-- trabajo del usuario.
--
-- Permite controlar:
-- - Apertura de caja
-- - Cierre de caja
-- - Efectivo inicial
-- - Efectivo esperado
-- - Efectivo contado
-- - Diferencia
-- - Usuario responsable
-- ============================================

CREATE TABLE Caja (
    id_caja INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_apertura INT NOT NULL,
    id_usuario_cierre INT NULL,
    fecha_apertura DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre DATETIME NULL,
    monto_apertura DECIMAL(10,2) NOT NULL
        DEFAULT 0.00,
    total_ingresos DECIMAL(10,2) NOT NULL
        DEFAULT 0.00,
    total_salidas DECIMAL(10,2) NOT NULL
        DEFAULT 0.00,
    monto_esperado DECIMAL(10,2) NOT NULL
        DEFAULT 0.00,
    monto_contado DECIMAL(10,2) NULL,
    diferencia DECIMAL(10,2) NULL,
    estado ENUM(
        'Abierta',
        'Cerrada'
    ) NOT NULL DEFAULT 'Abierta',
    observacion VARCHAR(255) NULL,
    CONSTRAINT fk_caja_usuario_apertura
        FOREIGN KEY (id_usuario_apertura)
        REFERENCES Usuario(id_usuario),
    CONSTRAINT fk_caja_usuario_cierre
        FOREIGN KEY (id_usuario_cierre)
        REFERENCES Usuario(id_usuario),
    CHECK (monto_apertura >= 0),
    CHECK (total_ingresos >= 0),
    CHECK (total_salidas >= 0),
    CHECK (monto_esperado >= 0),
    CHECK (monto_contado IS NULL OR monto_contado >= 0)
);
-- ============================================
-- TABLA: MOVIMIENTO_CAJA
--
-- Registra cada movimiento económico realizado
-- durante una jornada de caja.
--
-- INGRESO:
-- - Venta
-- - Otros ingresos
--
-- SALIDA:
-- - Devolución de dinero al cliente
-- - Compra pagada
-- - Gasto
-- - Otros egresos
--
-- IMPORTANTE:
-- Un movimiento de caja NO necesariamente
-- representa un movimiento de inventario.
--
-- Por ejemplo:
-- Devolver S/ 25 al cliente genera una
-- SALIDA en Caja, pero NO una salida en Kardex.
-- ============================================

CREATE TABLE Movimiento_Caja (
    id_movimiento_caja INT AUTO_INCREMENT PRIMARY KEY,
    id_caja INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_movimiento DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento ENUM(
        'Ingreso',
        'Salida'
    ) NOT NULL,
    concepto ENUM(
        'Venta',
        'Devolucion',
        'Compra',
        'Gasto',
        'Ajuste',
        'Otro'
    ) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM(
    'Efectivo',
    'Tarjeta',
    'Transferencia',
    'Yape'
) NOT NULL DEFAULT 'Efectivo',
    tipo_referencia ENUM(
        'Venta',
        'Compra',
        'Kardex',
        'Caja',
        'Ajuste',
        'Ninguna'
    ) NOT NULL DEFAULT 'Ninguna',
    id_referencia INT NULL,
    id_venta INT NULL,
    id_compra INT NULL,
    nro_comprobante VARCHAR(30) NULL,
    descripcion VARCHAR(255) NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_mov_caja_caja
        FOREIGN KEY (id_caja)
        REFERENCES Caja(id_caja),
    CONSTRAINT fk_mov_caja_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario(id_usuario),
    CONSTRAINT fk_mov_caja_venta
        FOREIGN KEY (id_venta)
        REFERENCES Venta(id_venta),
    CONSTRAINT fk_mov_caja_compra
        FOREIGN KEY (id_compra)
        REFERENCES Compra(id_compra),
    CHECK (monto > 0)
);