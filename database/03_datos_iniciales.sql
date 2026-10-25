
-- ============================================
-- DATOS DE iniciales - FERRETERÍA COLORÍN
-- ============================================

-- ============================================
-- CATEGORÍAS
-- ============================================


INSERT INTO Categoria (nombre, descripcion) VALUES
('Niveles', 'Niveles manuales y magnéticos para medición y nivelación'),
('Winchas', 'Cintas métricas para medir distancias'),
('Escuadras', 'Herramientas para medición, trazado y verificación de ángulos'),
('Pinturas', 'Pinturas látex y pinturas para diferentes superficies'),
('Accesorios de Pintura', 'Accesorios y kits utilizados para pintar'),
('Rodillo', 'Rodillos y repuestos para aplicación de pintura'),
('Brochas', 'Brochas para aplicar pinturas, barnices y otros acabados'),
('Abrasivos y lijado', 'Lijas y productos abrasivos para preparar y pulir superficies'),
('Disolventes', 'Thinner y otros disolventes para pinturas y limpieza'),
('Pinturas en spray', 'Pinturas en aerosol e imprimantes en spray'),
('Anticorrosivos', 'Productos para tratar y proteger superficies metálicas contra la corrosión'),
('Cinta adhesivas', 'Cintas adhesivas para pintura, embalaje y otros usos'),
('Masillas epóxicas', 'Masillas epóxicas para reparar, rellenar y reconstruir superficies'),
('Pegamento', 'Pegamentos y adhesivos para unir diferentes materiales'),
('Siliconas', 'Siliconas, selladores y herramientas para su aplicación'),
('Selladores, temples y masillas', 'Selladores, pinturas al temple y productos para preparar superficies'),
('Imprimante', 'Productos de imprimación y preparación de superficies'),
('Barnices', 'Barnices para proteger y dar acabado a madera y otras superficies'),
('Espátulas', 'Herramientas para aplicar, extender y retirar masillas y productos similares'),
('Destornilladores', 'Herramientas manuales para ajustar y retirar tornillos'),
('Llaves de ajuste', 'Llaves manuales para ajustar y aflojar tuercas y pernos'),
('Remachadoras', 'Herramientas para colocar remaches'),
('Alicates', 'Herramientas para sujetar, cortar, doblar y engastar materiales'),
('Martillos', 'Herramientas de golpe para construcción y reparación'),
('Serruchos', 'Herramientas manuales para cortar madera y otros materiales');



-- ============================================
-- MARCAS
-- ============================================


INSERT INTO Marca (nombre, descripcion) VALUES
('STANLEY', 'Herramientas manuales y equipos de medición'),
('WERKEN', 'Herramientas manuales y accesorios para ferretería'),
('TRUPER', 'Herramientas manuales, equipos y accesorios'),
('CPP', 'Pinturas, imprimantes y recubrimientos'),
('Tekno', 'Pinturas, barnices y productos para acabados'),
('American Colors', 'Pinturas y productos para recubrimiento'),
('FAST', 'Pinturas y productos para acabados'),
('TUMI', 'Brochas y herramientas para pintar'),
('3M', 'Abrasivos, lijas y cintas adhesivas'),
('ISSA', 'Disolventes y productos para pintura'),
('ULTRA COVER', 'Pinturas en aerosol e imprimantes'),
('CHEMA', 'Productos químicos para construcción y tratamiento de superficies'),
('SCOTCH', 'Cintas adhesivas para pintura y protección'),
('PEGAFAN', 'Cintas adhesivas para embalaje'),
('SOLDIMIX', 'Adhesivos y masillas epóxicas'),
('TEKBOND', 'Adhesivos y pegamentos'),
('Sika', 'Selladores, siliconas y productos para construcción'),
('PRO', 'Herramientas y accesorios para aplicación de selladores'),
('SOUDAL', 'Selladores, adhesivos y productos para construcción'),
('MAJESTAD', 'Pinturas y productos para acabados'),
('DEXTER', 'Herramientas manuales'),
('KAMASA', 'Herramientas manuales y llaves de ajuste');



-- ============================================
-- PRODUCTOS
-- ============================================
INSERT INTO Producto
(codigo, nombre, id_categoria, id_marca, unidad, estante, nivel, descripcion)
VALUES
('9847563847', 'Nivel Magnético Stanley Torpedo 43511S de Aluminio 9"', 1, 1, 'Unidad', 'C', '1', 'Herramienta ideal para profesionales de la albañilería.'),
('6784230973', 'Wincha Global 5 metros Stanley', 2, 1, 'Unidad', 'C', '2', 'Caja de ABS resistente y hoja cubierta con polímero.'),
('1342834650', 'Wincha contra impactos 3m Truper', 2, 3, 'Unidad', 'C', '2', 'Wincha resistente y práctica para trabajos de medición.'),
('4386975431', 'Nivel de Aluminio Amarillo Stanley Thrifty STHT42076 48 pulgadas Estructural', 1, 1, 'Unidad', 'C', '1', 'Herramienta de precisión y durabilidad.'),
('4352678561', 'Escuadra de precisión 30cm Werken', 3, 2, 'Unidad', 'C', '3', 'Excelente calidad, resistencia y durabilidad.'),
('3142567385', 'Escuadra 12 de combinación con punzón para marcar Truper - 14380', 3, 3, 'Unidad', 'C', '3', 'Regla de acero inoxidable con graduación.'),
('9846378273', 'Pintura Látex Pato CPP Blanco 4 Galón', 4, 4, 'Galón', 'A', '1', 'Pintura de acabado mate con buen poder cubriente.'),
('7654392817', 'Pintura Látex Pato CPP Blanco 1 Galón', 4, 4, 'Galón', 'A', '2', 'Pintura para proyectos de interiores y exteriores.'),
('6759283740', 'Pintura Satinada Tekno Blanco 1 galón', 4, 5, 'Galón', 'A', '2', 'Pintura satinada para renovar espacios interiores.'),
('3892564301', 'Pintura Latex American Colors Mate Blanco 4 galones', 4, 6, 'Galón', 'A', '1', 'Pintura resistente al agua y a los rayos solares.'),
('2039847632', 'Pintura Látex Fast Blanco 1 galón', 4, 7, 'Galón', 'A', '2', 'Pintura para interiores y exteriores.'),
('0947302910', 'Pintura Látex Fast Blanco Humo 1 galón', 4, 7, 'Galón', 'A', '2', 'Pintura mate con buen poder cubriente.'),
('0390480192', 'Pintura Látex DuraLátex CPP Marfil 1 galón', 4, 4, 'Galón', 'A', '2', 'Pintura mate que deja una superficie lisa.'),
('9483794959', 'Kit Tekno: Rodillo Antigota 9" + Repuesto', 5, 5, 'Juego', 'A', '3', 'Kit de rodillo y repuesto para pintar.'),
('7347237487', 'Rodillo antigota Tekno 9"', 6, 5, 'Unidad', 'A', '3', 'Rodillo con tecnología antigota.'),
('83484382948', 'Rodillo para pintar 9 x 3/8 pulgadas superficies lisas Truper', 6, 3, 'Unidad', 'A', '3', 'Felpa de poliéster de alta densidad.'),
('0394387373', 'Brocha Tumi 2"', 7, 8, 'Unidad', 'A', '3', 'Brocha para pintura látex y similares.'),
('3243543762', 'Brocha para Barniz Tumi 4 pulgadas de Nylon Blanco con Cerdas Sintéticas', 7, 8, 'Unidad', 'A', '3', 'Brocha para barnices y pinturas.'),
('3424534644', 'Lija 3M Grano 120 para Fierro', 8, 9, 'Unidad', 'A', '4', 'Lija para fierro marca 3M.'),
('2652752972', 'Lija al agua 3M Grano 320', 8, 9, 'Unidad', 'A', '4', 'Lija para pulir metales y lograr acabados finos.'),
('5465359754', 'Thinner Acrílico P-55 3 litros Issa', 9, 10, 'Litro', 'B', '1', 'Disolvente para uso doméstico y comercial.'),
('4764094978', 'Spray Ultra Cover 2X Imprimante Gris Mate 340 gramos', 10, 11, 'Unidad', 'B', '3', 'Imprimante en spray de acabado mate.'),
('5465324765', 'Transformador de Óxido Chema de 1 litro Marrón Oscuro', 11, 12, 'Unidad', 'B', '1', 'Producto acuoso que reacciona con el metal oxidado.'),
('6547698614', 'Cinta Masking Tape Werken Blanco 1 pulgada x 40yd', 12, 2, 'Unidad', 'B', '2', 'Cinta de adhesión media para proteger superficies.'),
('6547659671', 'Cinta de Papel Scotchblue 3M 1.88 pulgadas Azul de 54.8m', 12, 13, 'Unidad', 'B', '2', 'Cinta adhesiva para proteger superficies.'),
('9659809329', 'Cinta De Embalaje Transparente 48 mm x 100 Mt', 12, 14, 'Unidad', 'B', '2', 'Cinta para sellar cajas.'),
('8658548687', 'Moldimix 32gr Soldimix', 13, 15, 'Unidad', 'B', '2', 'Masilla epóxica resistente al calor y la humedad.'),
('7547327571', 'Cola para madera Pur 500gr Tekbond', 14, 16, 'Unidad', 'B', '2', 'Adhesivo de poliuretano de alta resistencia.'),
('8798678578', 'Silicona Sanisil 280ml Sika Antihongos', 15, 17, 'Unidad', 'B', '3', 'Sellador antihongos para baños y cocinas.'),
('8748938873', 'Pistola para Silicona Pro', 15, 18, 'Unidad', 'B', '3', 'Herramienta para aplicar selladores y adhesivos.'),
('7657473711', 'Multiuso sellador universal x 300ml Soudal', 15, 19, 'Unidad', 'B', '3', 'Sellador para juntas en puertas, ventanas, baños y cocinas.'),
('4457765298', 'Temple Majestad 5kg Pintura Acuosa Ecológica Blanca', 16, 20, 'Kilogramos', 'D', '1', 'Pintura acuosa para cielos rasos y cercos.'),
('0049387632', 'Sellador CPP 1/4 galón', 16, 4, 'Galón', 'D', '1', 'Sellador para superficies con pintura antigua.'),
('0302030471', 'Imprimante Blanco 1 galón', 17, 4, 'Galón', 'D', '1', 'Imprimante con poder de relleno para concreto.'),
('4478112093', 'Barniz Poliuretano Brillante Tekno A5 Transparente 1 litro', 18, 5, 'Litro', 'D', '2', 'Barniz resistente a rayaduras y humedad.'),
('6577546112', 'Barniz Doble Acción 1 galón Tekno', 18, 5, 'Galón', 'D', '2', 'Barniz con película impermeable y resistencia UV.'),
('3057914036', 'Espátula Flexible Werken Modelo GSPE041 de 3 pulgadas', 19, 2, 'Unidad', 'F', '2', 'Herramienta para trabajos de juntas.'),
('8849334876', 'Juego de destornilladores de 4 piezas Werken', 20, 2, 'Juego', 'F', '2', 'Juego de destornilladores.'),
('0039280253', 'Juego de Destornilladores Stanley 6 Piezas', 20, 1, 'Juego', 'F', '2', 'Juego versátil de destornilladores.'),
('7784934410', 'Desarmador Dieléctrico de Cruz 1/4 X 4 Truper', 20, 3, 'Unidad', 'F', '2', 'Desarmador dieléctrico de cruz.'),
('0394876382', 'Llave Mixta 15mm Cr-v Acero Kamasa', 21, 22, 'Unidad', 'F', '3', 'Llave mixta de acero.'),
('0966744321', 'Llave Stilson 8 pulgadas Cuerpo Robusto Truper', 21, 3, 'Unidad', 'F', '3', 'Llave Stilson de cuerpo robusto.'),
('1234563421', 'Remachadora Manual Werken', 22, 2, 'Unidad', 'F', '3', 'Herramienta para colocar remaches.'),
('6675884931', 'Crimpeadora pelacables 195mm Dexter', 23, 21, 'Unidad', 'F', '4', 'Herramienta para engastar terminales eléctricos.'),
('8458998321', 'Martillo de uña mango de madera 27mm Werken', 24, 2, 'Unidad', 'F', '4', 'Martillo para construcción y reparaciones.'),
('3455737912', 'Serrucho Luctador Stanley Modelo 15469 de Acero con Hoja de 16 Pulgadas', 25, 1, 'Unidad', 'G', '1', 'Serrucho para trabajos de carpintería.'),
('8473329084', 'Alicate pico de loro 10 pulgadas Werken', 23, 2, 'Unidad', 'F', '4', 'Alicate multipropósito para diversos usos.');


INSERT INTO Imagen_Producto_Web (id_producto, ruta_imagen)
SELECT
    id_producto,
    CONCAT('/imagenes/', codigo, '.jpg')
FROM Producto;

-- ============================================
-- PROVEEDORES
-- ============================================

INSERT INTO Proveedor
(empresa, ruc, contacto, telefono, correo, direccion)
VALUES
('Distribuidora A', '20123456789', 'Miguel Larco', '987654321', 'ventas@distribuidoraa.com', 'Lima'),
('Pinturas del Peru SAC', '20456789123', 'Carlos Torres', '986123456', 'ventas@pinturasperu.com', 'Chosica'),
('Distribuidora Tekno Peru SAC', '20512345678', 'Jorge Mendoza', '985123456', 'ventas@teknoperu.com', 'Ate'),
('Comercial de Pinturas Lima SAC', '20612345789', 'Luis Ramirez', '984234567', 'ventas@comercialpinturas.com', 'Chaclacayo'),
('Distribuidora Ferretera Central SAC', '20198765432', 'Rosa Castro', '983345678', 'ventas@ferreteracentral.com', 'Lima'),
('Importaciones Ferretools SAC', '20567891234', 'Pedro Salazar', '982456789', 'ventas@ferretools.com', 'Ate'),
('Comercial Los Andes SAC', '20432198765', 'Ana Flores', '981567890', 'ventas@comercialandes.com', 'Chosica'),
('Distribuidora El Constructor SAC', '20678912345', 'Mario Quispe', '980678901', 'ventas@elconstructor.com', 'Ñaña');

-- ============================================
-- USUARIOS
-- ============================================

INSERT INTO Usuario
    (username, nombre, apellidos, correo, password, rol)
VALUES
    ('admin', 'Irvin', 'Elescano Riveros', 'admin@colorin.com', '123', 'Administrador'),
    ('vendedor1', 'Juan', 'Perez', 'vendedor1@colorin.com', '123', 'Vendedor'),
    ('vendedor2', 'Maria', 'Gomez', 'vendedor2@colorin.com', '123', 'Vendedor'),
    ('vendedor3', 'Luis', 'Quispe', 'vendedor3@colorin.com', '123', 'Vendedor'),
    ('almacen', 'Carlos', 'Quispe', 'almacen@colorin.com', '123', 'Almacen'),
    ('OperacionWEB', 'Operacion', 'Web', 'operacionweb@colorin.com', 'WebColorin123', 'WEB');

-- ============================================
-- CLIENTES
-- ============================================

INSERT INTO Cliente
(tipo_cliente, dni, ruc, nombre, apellidos, razon_social, contacto, telefono, direccion)
VALUES
('Natural', NULL, NULL, 'Consumidor', 'Final', NULL, NULL, NULL, NULL),
('Natural', '45678912', NULL, 'Pedro', 'Gomez', NULL, NULL, '987111222', 'Chaclacayo'),
('Natural', '47852136', NULL, 'Lucia', 'Ramirez', NULL, NULL, '986222333', 'Ate'),
('Natural', '70123456', NULL, 'Carlos', 'Flores', NULL, NULL, '985444555', 'Chosica'),
('Natural', '72894561', NULL, 'Ana', 'Torres', NULL, NULL, '984555666', 'Ñaña'),
('Juridica', NULL, '20654321987', NULL, NULL, 'Constructora Lima SAC', 'Maria Lopez', '986333444', 'Chosica'),
('Juridica', NULL, '20456789123', NULL, NULL, 'Distribuidora El Sol SAC', 'Luis Mendoza', '983222111', 'Ate'),
('Juridica', NULL, '20567891234', NULL, NULL, 'Comercial Ferremax SAC', 'Rosa Castro', '982333444', 'Chaclacayo'),
('Natural', '41234567', NULL, 'Jorge', 'Mendoza', NULL, NULL, '987345612', 'Ñaña'),
('Natural', '43567891', NULL, 'Rosa', 'Castro', NULL, NULL, '986456723', 'Chaclacayo'),
('Natural', '46789123', NULL, 'Miguel', 'Torres', NULL, NULL, '985567834', 'Chosica'),
('Natural', '48912345', NULL, 'Carmen', 'Flores', NULL, NULL, '984678945', 'Ate'),
('Juridica', NULL, '20678945123', NULL, NULL, 'Ferretería Los Andes SAC', 'Miguel Quispe', '983789156', 'Chosica'),
('Juridica', NULL, '20543219876', NULL, NULL, 'Constructora Huascata SAC', 'Carmen Mendoza', '982891267', 'Chaclacayo'),
('Juridica', NULL, '20487651239', NULL, NULL, 'Comercial Ñaña SAC', 'Jorge Flores', '981912378', 'Ñaña');


-- INSERT DE LOS 47 DETALLES TECNICOS --
-- INSERT DE LOS 47 DETALLES TECNICOS --
-- INSERT DE LOS 47 DETALLES TECNICOS --
-- BLOQUE 1 de 9
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica
FROM Producto p
JOIN (
    SELECT
        '9847563847' AS codigo,
        'El Nivel Magnético Stanley Torpedo 43-511S de Aluminio 9 pulgadas es ideal para trabajos profesionales de albañilería. Su marco de aluminio, diseño Torpedo y versión magnética facilitan una nivelación precisa.' AS descripcion_comercial,
        'Marco de aluminio resistente; diseño Torpedo con burbuja central; cuerpo bimaterial con casquillos de goma; topes de goma para proteger la pieza de trabajo; borde de hule para utilizar sobre tubos; versión magnética para superficies metálicas.' AS caracteristicas,
        'Mayor exactitud y durabilidad; absorción de impactos; protección de la pieza de trabajo; versatilidad para nivelar superficies y trabajar sobre superficies metálicas.' AS ventajas,
        'Albañilería y trabajos de nivelación.' AS donde_usarlo,
        'Leer las indicaciones de uso. Mantener el producto en un lugar fresco, seco y ventilado.' AS recomendaciones_uso,
        'Mantener fuera del alcance de los niños.' AS advertencia_uso,
        'Material de aluminio con forro de plástico (polímero). Largo de 9 pulgadas. Magnético.' AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        '1 año' AS garantia,
        JSON_OBJECT(
            'altura', '23 cm',
            'ancho', '4.7 cm',
            'profundidad', '2 cm',
            'color_principal', 'Amarillo',
            'color', 'Amarillo',
            'area', 'Herramientas',
            'subcategoria', 'Niveles',
            'categoria', 'Herramientas de medición',
            'modelo', 'Torpedo 43-511S',
            'tipo_producto', 'Nivel',
            'subtipo_producto', 'Magnético',
            'material', 'Metal/Aluminio',
            'numero_piezas', 1,
            'resistencia_dieletrica', 'No',
            'magnetico', TRUE,
            'peso', '0.15 kg',
            'tipo_medida', 'Inclinación',
            'alcance', '9 pulgadas',
            'capacidad', '9 pulgadas',
            'precision', '±1.5 mm',
            'tipo_uso', 'Profesional',
            'uso', 'Para nivelar'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '6784230973',
        'Wincha Stanley modelo 30-496S de 5 metros, con caja de ABS resistente, hoja cubierta con polímero y gancho Tru-Zero para mediciones precisas.',
        'Caja de ABS fuerte; hoja cubierta con polímero; gancho Tru-Zero para mediciones internas y externas.',
        'Resistencia, durabilidad y precisión para mediciones en diferentes proyectos.',
        'Mediciones en interiores y exteriores.',
        'Mantener en un lugar limpio, ventilado y lejos de la humedad. Almacenar la herramienta lubricada si fuera necesario durante periodos prolongados. Respetar las capacidades óptimas de uso y no extender la cinta hasta su máximo.',
        'Mantener fuera del alcance de los niños. Leer las indicaciones del producto antes de utilizarlo. Utilizar equipos de seguridad adecuados, desconectar la energía eléctrica antes de trabajar y emplear la herramienta únicamente para el uso previsto.',
        'No brinda protección contra descargas eléctricas. Producto no inoxidable ni irrompible.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '6.5 cm',
            'ancho', '7.5 cm',
            'profundidad', '3.8 cm',
            'color_principal', 'Amarillo',
            'color', 'Amarillo',
            'modelo', '30-496S',
            'tipo_producto', 'Wincha',
            'material', 'Plástico ABS',
            'numero_piezas', 1,
            'resistencia_dieletrica', 'No',
            'magnetico', FALSE,
            'peso', '0.3 kg',
            'tipo_medida', 'Varios',
            'alcance', '5 m',
            'precision', '±2 mm',
            'tipo_uso', 'General',
            'uso', 'Mediciones en interiores y exteriores'
        )
    UNION ALL
    SELECT
        '1342834650',
        'Realiza diferentes trabajos de medición con esta resistente y práctica wincha.',
        NULL,
        'Resistente y práctica para trabajos de medición.',
        NULL,
        NULL,
        NULL,
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '10.5 cm',
            'ancho', '3.4 cm',
            'profundidad', '3 cm',
            'color_principal', 'Naranja',
            'material_empaque_primario', 'Cartón',
            'peso_empaque_primario', '20 g',
            'tipo_producto', 'Wincha',
            'material', 'ABS',
            'numero_piezas', 1,
            'peso_producto', '0.13 kg',
            'alcance', '3 m (según el catálogo)'
        )
) AS d
    ON p.codigo = d.codigo;

-- ============================================
-- BLOQUE 2
-- ============================================
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica
FROM Producto p
JOIN (
    SELECT
        '4386975431' AS codigo,
        'El Nivel de Aluminio Amarillo Stanley Thrifty STHT42-076 de 48 pulgadas es una herramienta resistente para mediciones precisas. Cuenta con tres burbujas de nivel, cubierta para mejorar la visibilidad y agujero para colgar.' AS descripcion_comercial,
        'Diseño resistente y duradero; tres burbujas de nivel; cubierta de burbujas para mejorar la visibilidad; agujero para colgar.' AS caracteristicas,
        'Durabilidad, mejor visibilidad y facilidad de almacenamiento.' AS ventajas,
        'Para determinar la horizontalidad o verticalidad de un elemento.' AS donde_usarlo,
        'Mantener en un lugar limpio, ventilado y lejos de la humedad. Si se almacena por largo tiempo, lubricar cuando sea necesario. Respetar las capacidades de uso. Los impactos fuertes pueden desnivelar y dañar la burbuja.' AS recomendaciones_uso,
        'Mantener fuera del alcance de los niños. Leer las indicaciones antes de usar. Utilizar equipos de seguridad adecuados. Desconectar la energía eléctrica antes de trabajar y utilizar la herramienta para el fin previsto.' AS advertencia_uso,
        'No brinda protección contra descargas eléctricas. Producto no irrompible.' AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        NULL AS garantia,
        JSON_OBJECT(
            'altura', '112 cm',
            'ancho', '5.3 cm',
            'profundidad', '2.4 cm',
            'color_principal', 'Amarillo',
            'color', 'Amarillo',
            'area', 'Herramientas',
            'subcategoria', 'Niveles',
            'categoria', 'Herramientas de medición',
            'modelo', 'Thrifty STHT42-076',
            'tipo_producto', 'Nivel',
            'subtipo_producto', 'Estructural',
            'material', 'Aluminio',
            'numero_piezas', 1,
            'resistencia_dieletrica', 'No',
            'magnetico', FALSE,
            'peso', '1.5 kg',
            'tipo_medida', 'Inclinación',
            'alcance', '48 pulgadas',
            'precision', 'Exacto',
            'tipo_uso', 'Profesional',
            'uso', 'Determinar la horizontalidad o verticalidad de un elemento'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '4352678561',
        'Escuadra de precisión Werken de 30 cm. La información proporcionada destaca su calidad, resistencia y durabilidad.',
        NULL,
        'Calidad, resistencia y durabilidad, según la descripción proporcionada.',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        JSON_OBJECT(
            'modelo', 'No especificado',
            'tipo_producto', 'Escuadra de precisión',
            'marca', 'Werken',
            'alcance', '30 cm'
        )
    UNION ALL
    SELECT
        '3142567385',
        'Escuadra de combinación Truper de 12 pulgadas con punzón para marcar superficies.',
        'Cabezal robusto; punzón para marcar; regla de acero inoxidable con graduación métrica y estándar estampada en bajo relieve con tinta negra; nivel de gota en la manija; perno de ajuste rápido; caras rectificadas.',
        'Graduaciones de mejor visibilidad y nivel de gota para mayor precisión.',
        'Medición y marcado de superficies.',
        NULL,
        NULL,
        'Empaque individual: tarjeta.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'tipo_producto', 'Escuadra de combinación',
            'marca', 'Truper',
            'alcance', '12 pulgadas (30 cm)',
            'ancho', '4.5 pulgadas (11.5 cm)',
            'material_regla', 'Acero inoxidable',
            'graduacion', 'Sistema métrico y estándar',
            'punzon_para_marcar', TRUE,
            'nivel_de_gota', TRUE,
            'tipo_uso', 'Comercial'
        )
    UNION ALL
    SELECT
        '9846378273',
        'Pintura Látex Pato CPP Blanco de 4 galones, con acabado mate, buen poder cubriente y fácil aplicación. Indicada para interiores y exteriores bajo sombra, sobre concreto o madera.',
        'Acabado mate; resina vinil acrílica; aplicación con brocha o rodillo de pelo corto o de esponja; presentación en balde.',
        'Buen poder cubriente, fácil aplicación y mayor rendimiento.',
        'Superficies de concreto o madera. Aplicación ideal en interiores o exteriores bajo sombra.',
        'La superficie debe estar libre de sales, suciedad, grasa, aceite, pintura suelta y cualquier otro material extraño.',
        'En caso de ingestión accidental, no provocar el vómito y solicitar atención médica de inmediato. Evitar el contacto con los ojos y el contacto prolongado con la piel.',
        'Aplicación ideal de látex para interiores o exteriores bajo sombra.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '32 cm',
            'ancho', '30.6 cm',
            'profundidad', '30.6 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'tipo_acabado', 'Mate',
            'modelo', 'Classic',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Resina vinil acrílica',
            'numero_piezas', 1,
            'resistencia_ray_uv', 'No',
            'resistencia_moho', 'No',
            'metodo_aplicacion', 'Brocha o rodillo de pelo corto o de esponja',
            'rendimiento', '84 m2 por 4 galones',
            'peso', '5.50 kg',
            'resistencia_solventes', 'No',
            'resistencia_humedad', 'No',
            'capacidad', '4 gal',
            'lavable', 'No',
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior',
            'uso', 'Paredes interiores y exteriores'
        )
    UNION ALL
    SELECT
        '7654392817',
        'Pintura Látex Classic CPP Blanco de 1 galón, con acabado mate, buen poder cubriente y fácil aplicación. Su fórmula de resina vinil acrílica está indicada para superficies de concreto o madera.',
        'Acabado mate; fórmula de resina vinil acrílica; aplicación con brocha o rodillo de pelo corto o de esponja; presentación en balde.',
        'Buen poder cubriente, fácil aplicación y mayor rendimiento.',
        'Superficies de concreto o madera. Aplicación ideal en interiores o exteriores bajo sombra.',
        'La superficie debe estar libre de sales, suciedad, grasa, aceite, pintura suelta y cualquier otro material extraño.',
        'En caso de ingestión accidental, no provocar el vómito y solicitar atención médica de inmediato. Evitar el contacto con los ojos y el contacto prolongado con la piel.',
        'Aplicación ideal de látex para interiores o exteriores bajo sombra.',
        'Las fotografías y la descripción del producto son referenciales.',
        '1 año',
        JSON_OBJECT(
            'altura', '20 cm',
            'ancho', '21 cm',
            'profundidad', '21 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'resistencia_ray_uv', 'No',
            'tipo_acabado', 'Mate',
            'modelo', 'Classic',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Resina vinil acrílica',
            'numero_piezas', 1,
            'resistencia_moho', 'No',
            'metodo_aplicacion', 'Brocha o rodillo de pelo corto o de esponja',
            'rendimiento', '21 m2',
            'peso', '5.50 kg',
            'resistencia_solventes', 'No',
            'resistencia_humedad', 'No',
            'capacidad', '1 gal',
            'lavable', 'No',
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior',
            'uso', 'Paredes interiores y exteriores'
        )
) AS d
    ON p.codigo = d.codigo;
-- ============================================
-- ============================================
-- BLOQUE 3
-- ============================================
-- ============================================

INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica
FROM Producto p
JOIN (
    SELECT
        '6759283740' AS codigo,
        'La Pintura Satinada Tekno Blanco de 1 galón tiene acabado satinado y fórmula a base de resina vinil-acrílica. Ofrece buen nivelamiento, fácil aplicación y una película suave y tersa que ayuda a mantener el color y brillo.' AS descripcion_comercial,
        'Pintura a base de resina vinil-acrílica; acabado satinado; fácil aplicación y buen nivelamiento. No contiene pigmentos a base de plomo, cadmio ni cromo.' AS caracteristicas,
        'Mayor adhesión y resistencia para las paredes. Brillo perfecto.' AS ventajas,
        'En concreto, madera y ladrillo, para pintar interiores.' AS donde_usarlo,
        'Para un mejor acabado, aplicar dos capas de pasta fina y usar un rodillo de pelo corto.' AS recomendaciones_uso,
        'No ingerir. En caso de ingestión, no provocar el vómito y solicitar atención médica de inmediato. Evitar el contacto con los ojos y el contacto prolongado con la piel. Trabajar en lugares ventilados. Mantener lejos de fuentes de calor y fuera del alcance de los niños.' AS advertencia_uso,
        'Secado al tacto de 30 a 60 minutos y 4 horas entre cada capa.' AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        NULL AS garantia,
        JSON_OBJECT(
            'altura', '19 cm',
            'ancho', '21.5 cm',
            'profundidad', '21.5 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'resistencia_ray_uv', TRUE,
            'tipo_acabado', 'Satinado',
            'acabado', 'Satinado',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Resina Acrílica',
            'numero_piezas', 1,
            'resistencia_moho', FALSE,
            'metodo_aplicacion', 'Brocha, rodillo o pistola',
            'rendimiento', '24 m2 por galón',
            'peso', '5.78 kg',
            'resistencia_solventes', FALSE,
            'resistencia_humedad', FALSE,
            'capacidad', '1 gal',
            'lavable', TRUE,
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior',
            'uso', 'Para pintar interiores'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '3892564301',
        'La Pintura Látex American Colors Mate Blanco de 4 galones ofrece acabado mate, buen poder cubriente, fácil aplicación y resistencia a la intemperie. Es indicada para interiores y exteriores.',
        'La resina acrílica actúa como capa protectora y dificulta la adhesión de suciedad y manchas. Acabado mate; fácil limpieza; resistencia al contacto con agua y jabones; buena brochabilidad y nivelación; resistencia a la intemperie y a los rayos solares UV.',
        'Excelente poder cubriente.',
        'Paredes interiores, como salas, comedores y dormitorios, y exteriores. Aplicable sobre concreto o madera.',
        'La superficie debe estar libre de suciedad, grasa, aceite, pintura suelta, humedad y otros materiales extraños. La brocha, rodillo o pistola deben estar en buen estado. Agitar la pintura hasta homogeneizarla. Para la primera capa, usar como máximo 1 volumen de agua por 6 volúmenes de pintura. Aplicar una capa delgada y uniforme. Después de 4 horas, aplicar la segunda capa; para esta, usar como máximo 1 volumen de agua por 8 volúmenes de pintura. La superficie puede lavarse después de 3 semanas con agua y jabón.',
        'La manipulación inapropiada puede ser nociva para la salud. Tomar las precauciones de seguridad indicadas, incluida una ventilación adecuada, lentes de seguridad y vestimenta apropiada.',
        'Secado al tacto de 30 a 60 minutos y 4 horas entre cada capa.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '32.8 cm',
            'ancho', '30.5 cm',
            'profundidad', '31 cm',
            'color_principal', 'Blanco',
            'resistencia_ray_uv', TRUE,
            'tipo_acabado', 'Mate',
            'modelo', 'Classic',
            'acabado', 'Mate',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Resina Vinil Acrílica',
            'numero_piezas', 1,
            'resistencia_moho', FALSE,
            'metodo_aplicacion', 'Brocha, rodillo o pistola',
            'rendimiento', '120 m2',
            'peso', '21 kg',
            'resistencia_solventes', FALSE,
            'resistencia_humedad', FALSE,
            'capacidad', '4 gal',
            'lavable', TRUE,
            'presentacion', 'Galón',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior/Exterior',
            'uso', 'Para pintado de paredes'
        )
    UNION ALL
    SELECT
        '2039847632',
        'La Pintura Látex Fast Blanco de 1 galón tiene acabado mate y buen poder cubriente. Puede aplicarse con brocha, rodillo o pistola en interiores y exteriores.',
        'Aplicación con brocha, rodillo o pistola. Tiempo de secado de 4 horas. Diluyente: agua potable.',
        'Buen poder cubriente.',
        'Áreas interiores y exteriores sobre concreto, fibrocemento y madera. Uso en cielos rasos y muros.',
        'La superficie debe estar libre de suciedad, grasa, aceite, pintura suelta, humedad y otros materiales extraños. La brocha o rodillo deben estar en buen estado. Agitar hasta homogeneizar. Agregar agua potable, como máximo 1 volumen de agua por 5 volúmenes de pintura. Aplicar una capa delgada y uniforme. Después de 4 horas, aplicar otra capa si se requiere. No añadir agua en exceso, porque puede reducir el poder cubriente y causar chorreado o veteamiento.',
        'La manipulación inapropiada puede ser nociva para la salud. No usar sin tomar las precauciones de seguridad necesarias, incluida una ventilación adecuada.',
        'El color puede variar según la iluminación del lugar y la configuración de la pantalla. El rendimiento real depende de las condiciones de aplicación y del estado de la superficie. No contiene plomo, mercurio ni cromo en su formulación.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '19.5 cm',
            'ancho', '17.5 cm',
            'profundidad', '18 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'resistencia_ray_uv', FALSE,
            'acabado', 'Mate',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Látex',
            'numero_piezas', 1,
            'resistencia_moho', FALSE,
            'metodo_aplicacion', 'Brocha o rodillo de pelo corto o de esponja',
            'rendimiento', '19 m2',
            'peso', '5 kg',
            'resistencia_solventes', FALSE,
            'resistencia_humedad', FALSE,
            'capacidad', '1 gal',
            'lavable', FALSE,
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior/Exterior',
            'uso', 'Para cielos rasos y muros, en interiores y exteriores'
        )
    UNION ALL
    SELECT
        '0947302910',
        'La Pintura Látex Fast Blanco Humo de 1 galón tiene acabado mate y buen poder cubriente. Es apta para interiores y exteriores sobre distintas superficies.',
        'Aplicación con brocha, rodillo o pistola. Tiempo de secado de 4 horas. Diluyente: agua potable.',
        'Buen poder cubriente.',
        'Áreas interiores y exteriores sobre concreto, fibrocemento y madera. Uso en cielos rasos y muros.',
        'La superficie debe estar libre de suciedad, grasa, aceite, pintura suelta, humedad y otros materiales extraños. La brocha o rodillo deben estar en buen estado. Agitar hasta homogeneizar. Agregar agua potable, como máximo 1 volumen de agua por 5 volúmenes de pintura. Aplicar una capa delgada y uniforme. Después de 4 horas, aplicar otra capa si se requiere. No añadir agua en exceso, porque puede reducir el poder cubriente y causar chorreado o veteamiento.',
        'La manipulación inapropiada puede ser nociva para la salud. No usar sin tomar las precauciones de seguridad necesarias.',
        'El color puede variar según la iluminación del lugar y la configuración de la pantalla. El rendimiento real depende de las condiciones de aplicación y del estado de la superficie. No contiene plomo, mercurio ni cromo en su formulación.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '19.5 cm',
            'ancho', '17.5 cm',
            'profundidad', '18 cm',
            'color_principal', 'Blanco',
            'color', 'Humo',
            'resistencia_ray_uv', FALSE,
            'acabado', 'Mate',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Látex',
            'tipo_color', 'Humo',
            'numero_piezas', 1,
            'resistencia_moho', FALSE,
            'metodo_aplicacion', 'Brocha o rodillo de pelo corto o de esponja',
            'rendimiento', '19 m2',
            'peso', '5 kg',
            'resistencia_solventes', FALSE,
            'resistencia_humedad', FALSE,
            'capacidad', '1 gal',
            'lavable', FALSE,
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior/Exterior',
            'uso', 'Para cielos rasos y muros, en interiores y exteriores'
        )
    UNION ALL
    SELECT
        '0390480192',
        'La Pintura Látex DuraLátex CPP Mate Marfil de 1 galón está indicada para revestir paredes interiores y exteriores. Tiene acabado mate, fácil aplicación y un rendimiento indicado de 25 m2 por galón.',
        'Deja la superficie lisa, suave y con buena apariencia. La emulsión ofrece mayor resistencia a la exposición de la luz. Los colores pueden matizarse entre sí.',
        'Mayor rendimiento.',
        'Paredes interiores, salas, comedores, dormitorios y superficies de concreto o madera. La ficha indica uso interior y revestimiento de paredes interiores y exteriores.',
        'La superficie debe estar libre de suciedad, grasa, aceite, óxido, pintura suelta, humedad y otros materiales extraños. La brocha, rodillo o pistola deben estar en buen estado. Agitar hasta homogeneizar. Agregar agua potable sin excederse. Para la primera mano, usar como máximo 1 volumen de agua por 6 volúmenes de pintura. Aplicar una capa delgada y uniforme. Después de 4 horas, aplicar otra capa si se requiere; para la segunda mano, usar como máximo 1 volumen de agua por 8 volúmenes de pintura.',
        'La manipulación inapropiada puede ser nociva para la salud. Tomar las precauciones de seguridad necesarias, incluida una ventilación adecuada.',
        'Se garantiza buena estabilidad en almacenamiento hasta por 12 meses si se almacena bajo techo a temperaturas entre 4 °C y 38 °C.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '20 cm',
            'ancho', '21 cm',
            'profundidad', '21 cm',
            'color_principal', 'Blanco',
            'color', 'Marfil',
            'resistencia_ray_uv', TRUE,
            'tipo_acabado', 'Mate',
            'modelo', 'Pato',
            'acabado', 'Mate',
            'tipo_producto', 'Pintura',
            'subtipo_producto', 'Látex',
            'material', 'Resina Vinil Acrílica',
            'tipo_color', 'Marfil',
            'numero_piezas', 1,
            'resistencia_moho', TRUE,
            'metodo_aplicacion', 'Brocha, rodillo o pistola',
            'rendimiento', '25 m2 por galón',
            'peso', '5.62 kg',
            'resistencia_solventes', FALSE,
            'resistencia_humedad', TRUE,
            'capacidad', '1 gal',
            'lavable', TRUE,
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Interior',
            'uso', 'Para revestimiento de paredes interiores y exteriores'
        )
) AS d
    ON p.codigo = d.codigo;
-- ============================================
-- ============================================
-- BLOQUE 4
-- ============================================
-- ============================================

INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica
FROM Producto p
JOIN (
    SELECT
        '9483794959' AS codigo,
        'Rodillo Antigota Tekno de 9 pulgadas, color azul, con mango de polipropileno y diseño ergonómico para aplicar pintura de manera uniforme.' AS descripcion_comercial,
        'Fabricado en polipropileno resistente. Diseño ergonómico y mango de polipropileno.' AS caracteristicas,
        'Comodidad, precisión y aplicación uniforme de pintura.' AS ventajas,
        'Para trabajos profesionales de pintura.' AS donde_usarlo,
        NULL AS recomendaciones_uso,
        NULL AS advertencia_uso,
        NULL AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        '1 año' AS garantia,
        JSON_OBJECT(
            'altura', '4.5 cm',
            'ancho', '25 cm',
            'profundidad', '38 cm',
            'color_principal', 'Azul',
            'material_empaque_primario', 'Otro',
            'peso_empaque_gramos', '5 g',
            'modelo', 'Kit + Repuesto',
            'tipo_producto', 'Rodillo',
            'subtipo_producto', 'Pintar',
            'material', 'Polipropileno',
            'numero_piezas', 1,
            'peso', '0.28 kg',
            'material_mango', 'Polipropileno',
            'medida', '9 pulgadas',
            'tipo_uso', 'Profesional'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '7347237487',
        'Rodillo antigota Tekno de 9 pulgadas, diseñado para cubrir superficies amplias y alcanzar superficies en altura con facilidad.',
        'Tecnología antigota. Fabricado en polipropileno y acero, con mango de polipropileno.',
        'Facilidad de uso, diseño ligero y ergonómico.',
        'Para aplicar pintura en superficies amplias y en altura.',
        'Mantener limpio y seco después de cada uso.',
        'No exponer a altas temperaturas ni durante periodos prolongados a la luz solar. Mantener fuera del alcance de los niños.',
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        'De por vida',
        JSON_OBJECT(
            'altura', '5.2 cm',
            'ancho', '22 cm',
            'profundidad', '33.5 cm',
            'color_principal', 'Blanco',
            'marca', 'Tekno',
            'material', 'Polipropileno/Acero',
            'modelo', 'Antigota',
            'tipo_producto', 'Rodillo',
            'numero_piezas', 1,
            'peso', '0.21 kg',
            'material_mango', 'Polipropileno',
            'medida', '9 pulgadas',
            'tipo_uso', 'Profesional',
            'uso', 'Para aplicar pintura'
        )
    UNION ALL
    SELECT
        '83484382948',
        'Rodillo para pintar Truper de 9 x 3/8 pulgadas, con felpa de poliéster de alta densidad y maneral de acero galvanizado con mango de polipropileno.',
        'Felpa de poliéster de alta densidad color blanco. Maneral de acero galvanizado y mango de polipropileno. Maneral y felpa reutilizables. Indicado para superficies lisas, pinturas, base sellador, imprimante y temple.',
        'Permite cubrir áreas de mayor tamaño en menos tiempo; la descripción indica que es 2 veces más eficiente.',
        'Superficies lisas y aplicación de todo tipo de pinturas, base sellador, imprimante y temple.',
        'Tener precaución durante el uso.',
        'Usar adecuadamente.',
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        '6 meses',
        JSON_OBJECT(
            'alto', '34 cm',
            'ancho', '6 cm',
            'peso', '0.2 kg',
            'material', 'Felpa de poliéster de alta densidad color blanco',
            'material_maneral', 'Acero galvanizado',
            'material_mango', 'Polipropileno',
            'medida', '9 x 3/8 pulgadas',
            'superficie_recomendada', 'Superficies lisas',
            'reutilizable', TRUE
        )
    UNION ALL
    SELECT
        '0394387373',
        'Brocha plana Tumi de 2 pulgadas para aplicar pintura látex o similares. Cuenta con mango de madera pintada y cerdas 100 % sintéticas.',
        'Brocha plana para látex. Mango de madera pintada y cabezal de cerdas 100 % sintéticas, con 612 cerdas afiladas y horquilladas que retienen gran cantidad de pintura.',
        'Fácil de usar y ayuda a retener pintura para facilitar la aplicación.',
        'Para aplicar pintura látex o similares.',
        'Aplicar capas delgadas para obtener un mejor acabado y evitar chorreaduras. Para limpiarla, lavar con abundante agua y jabón líquido; dejar secar antes del siguiente uso.',
        'Alejar del fuego directo. No cortar. Si el producto está deteriorado o tiene menos del 80 % de cerdas en el cabezal, suspender su uso. No dejar secar pintura o líquido en las cerdas y lavar después de cada uso.',
        'Antes de comprar, revisar el tipo de pintura o líquido y la superficie donde se aplicará; el tamaño de la brocha también depende de estos factores.',
        'Las fotografías y la descripción del producto son referenciales.',
        'De por vida',
        JSON_OBJECT(
            'altura', '21 cm',
            'ancho', '5 cm',
            'profundidad', '1.5 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'despacho_24_horas', TRUE,
            'modelo', 'Látex 2 pulgadas',
            'tipo_producto', 'Brocha',
            'material', 'Nylon',
            'numero_piezas', 1,
            'material_cerdas', 'Fibra sintética',
            'material_mango', 'Madera',
            'medida', '2 pulgadas',
            'tipo_uso', 'Profesional',
            'uso', 'Para aplicar pintura látex o similares'
        )
    UNION ALL
    SELECT
        '3243543762',
        'Brocha plana Tumi de 4 pulgadas para barniz, pintura o similares, con mango de madera pintada y cerdas 100 % sintéticas.',
        'Brocha plana para barniz. Mango de madera pintada y cabezal de cerdas 100 % sintéticas, con 612 cerdas afiladas y horquilladas que retienen gran cantidad de pintura.',
        'Fácil de usar y ayuda a retener pintura para facilitar la aplicación.',
        'Para aplicar barnices, pintura o similares.',
        'Aplicar capas delgadas para obtener un mejor acabado y evitar chorreaduras. Para limpiarla, lavar con abundante agua y jabón líquido; dejar secar antes del siguiente uso.',
        'Alejar del fuego directo. No cortar. Si el producto está deteriorado o tiene menos del 80 % de cerdas en el cabezal, suspender su uso. No dejar secar pintura o líquido en las cerdas y lavar después de cada uso.',
        'Antes de comprar, revisar el tipo de pintura o líquido y la superficie donde se aplicará; el tamaño de la brocha también depende de estos factores.',
        'Las fotografías y la descripción del producto son referenciales.',
        'De por vida',
        JSON_OBJECT(
            'altura', '29 cm',
            'ancho', '11 cm',
            'profundidad', '1.5 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'despacho_24_horas', TRUE,
            'modelo', '4 pulgadas',
            'tipo_producto', 'Brocha',
            'subtipo_producto', 'Barnices',
            'material', 'Nylon',
            'numero_piezas', 1,
            'material_cerdas', 'Fibra sintética',
            'material_mango', 'Madera',
            'medida', '4 pulgadas',
            'tipo_uso', 'Profesional',
            'uso', 'Para aplicar barnices, pintura o similares'
        )
    UNION ALL
    SELECT
        '3424534644',
        'Lija 3M grano 120 para fierro, elaborada con óxido de aluminio sobre una tela especial para lijar superficies y mejorar su acabado.',
        'Lija fina de grano 120, elaborada con óxido de aluminio apoyado en tela especial.',
        'Ayuda a dejar las superficies sin impurezas y mejorar el acabado.',
        'Para lijar superficies de fierro.',
        'Asegurarse de agarrar firmemente la lija para realizar un lijado correcto.',
        'Nunca utilizar la lija en dirección transversal.',
        'Usar la lija para el material adecuado.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura', '23 cm',
            'ancho', '28 cm',
            'profundidad', '0.07 cm',
            'color_principal', 'Negro',
            'color', 'Negro',
            'modelo', 'Fierro',
            'tipo_producto', 'Lija',
            'material', 'Otros',
            'numero_piezas', 1,
            'peso', '0.01 kg',
            'impermeable', FALSE,
            'grano', 120,
            'presentacion', 'Paquete',
            'tipo_uso', 'General',
            'uso', 'Para mejorar el acabado',
            'superficie_recomendada', 'Fierro'
        )
) AS d
    ON p.codigo = d.codigo;

-- ============================================
-- ============================================
-- BLOQUE 5
-- ============================================
-- ============================================
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica
FROM Producto p
JOIN (
    SELECT
        '2652752972' AS codigo,
        'Lija 3M al agua grano 320 para lijado fino y acabado de superficies.' AS descripcion_comercial,
        'Grano 320; impermeable; dimensiones de 23 x 28 cm; presentación en paquete.' AS caracteristicas,
        'Permite un lijado suave y preciso.' AS ventajas,
        'Concreto, según la ficha técnica recibida.' AS donde_usarlo,
        'Usar la lija para el material adecuado y agarrarla firmemente durante el lijado.' AS recomendaciones_uso,
        'Nunca utilizar la lija en dirección transversal.' AS advertencia_uso,
        NULL AS observaciones,
        'Las fotografías y descripción del producto son referenciales.' AS observaciones_adicionales,
        NULL AS garantia,
        JSON_OBJECT(
            'marca', '3M',
            'modelo', 'Al Agua',
            'tipo_producto', 'Lija',
            'material', 'Otros',
            'altura_cm', 23,
            'ancho_cm', 28,
            'profundidad_cm', 0.07,
            'color', 'Negro',
            'numero_piezas', 1,
            'peso_kg', 0,
            'impermeable', TRUE,
            'grano', 320,
            'presentacion', 'Paquete',
            'tipo_uso', 'General',
            'uso', 'Pulir metales y dar acabado de empaste y acabados finos en madera'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '5465359754',
        'Thinner Acrílico P-55 Issa de 3 litros para diluir pinturas y barnices.',
        'Compuesto por hidrocarburos alifáticos, alcoholes, glicoles y ésteres.',
        NULL,
        NULL,
        'Usar en lugares ventilados y abiertos.',
        'No manipular incorrectamente el producto. La inhalación provoca dependencia. Evitar el contacto con los ojos.',
        'Mantener fuera del alcance de los niños.',
        'Las fotografías y descripción del producto son referenciales.',
        '4 meses',
        JSON_OBJECT(
            'marca', 'Issa',
            'modelo', 'P-55',
            'tipo_producto', 'Thinner',
            'material', 'Otros',
            'altura_cm', 27,
            'ancho_cm', 13.5,
            'profundidad_cm', 15,
            'color', 'Transparente',
            'estado_contenido', 'Líquido',
            'numero_piezas', 1,
            'metodo_aplicacion', 'Directo',
            'inflamable', TRUE,
            'peso_kg', 2.42,
            'capacidad_litros', 3,
            'presentacion', 'Bidón',
            'tipo_uso', 'Doméstico/Comercial'
        )
    UNION ALL
    SELECT
        '4764094978',
        'Spray Ultra Cover 2X imprimante gris mate de 340 gramos.',
        'Imprimante en aerosol de acabado mate que prepara la superficie y favorece la adhesión de la pintura posterior. Sistema de pulverización en distintos ángulos.',
        'Rápido y fácil de aplicar.',
        'Madera, metal no galvanizado, plástico, mimbre y otras superficies compatibles indicadas por el fabricante.',
        'Limpiar la superficie y retirar suciedad, grasa, aceite y material suelto. Agitar antes de usar. Aplicar dos o más capas finas con movimientos constantes, siguiendo los intervalos indicados en la etiqueta.',
        'Inflamable. Aplicar en lugares ventilados, lejos de llamas y fuentes de ignición. Evitar el contacto con piel y ojos y mantener fuera del alcance de los niños. No aplicar sobre metal galvanizado.',
        NULL,
        'Las fotografías y descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'marca', 'Rust-Oleum',
            'modelo', 'Ultra Cover 2X mate',
            'tipo_producto', 'Spray',
            'subtipo_producto', 'Spray',
            'material', 'Resina Acrílica',
            'altura_cm', 20,
            'ancho_cm', 6.6,
            'profundidad_cm', 6.6,
            'color', 'Gris mate',
            'despacho_24_horas', TRUE,
            'resistencia_ray_uv', FALSE,
            'tipo_acabado', 'Mate',
            'acabado', 'Mate',
            'numero_piezas', 1,
            'resistencia_moho', FALSE,
            'metodo_aplicacion', 'Agitar y pulverizar en capas finas según la etiqueta',
            'inflamable', TRUE,
            'rendimiento', '1.05 m2 por lata según la ficha recibida',
            'peso_kg', 0.48,
            'resistencia_solventes', FALSE,
            'resistencia_humedad', FALSE,
            'capacidad_kg', 0.340,
            'presentacion', 'Spray',
            'tiempo_secado', 'Aproximadamente 1 hora',
            'tipo_uso', 'General'
        )
    UNION ALL
    SELECT
        '5465324765',
        'Transformador de Óxido Chema de 1 litro para tratar superficies de hierro oxidadas.',
        'Producto acuoso emulsionado que reacciona con el óxido y forma una capa protectora marrón oscuro.',
        NULL,
        'Superficies metálicas oxidadas.',
        'Retirar el óxido suelto y limpiar la superficie. Aplicar con brocha, pincel o rodillo. Dejar secar aproximadamente 1 hora antes de la segunda mano y 24 horas antes de pintar.',
        'No aplicar a temperaturas inferiores a 5 °C ni dejar expuesto a la lluvia antes de aplicar la pintura.',
        'Mantener alejado el producto de los niños.',
        'Las fotografías y descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'marca', 'Chema',
            'modelo', 'de Óxido',
            'tipo_producto', 'Transformador',
            'material', 'Otros',
            'altura_cm', 24,
            'ancho_cm', 9.5,
            'profundidad_cm', 9.5,
            'color', 'Marrón oscuro',
            'resistencia_ray_uv', TRUE,
            'acabado', 'Mate',
            'numero_piezas', 1,
            'resistencia_moho', TRUE,
            'metodo_aplicacion', 'Aplicar con brocha, pincel o rodillo',
            'rendimiento', '25 a 30 m2 por galón aproximadamente en 2 capas',
            'resistencia_solventes', TRUE,
            'resistencia_humedad', TRUE,
            'capacidad_litros', 1,
            'presentacion', 'Botella',
            'tiempo_secado', '24 horas',
            'tipo_uso', 'Doméstico/Comercial'
        )
    UNION ALL
    SELECT
        '6547698614',
        'Cinta Masking Tape Werken blanca de 1 pulgada x 40 yardas.',
        'Cinta para enmascarar, sujetar, etiquetar y realizar trabajos de pintura. Longitud indicada de 36.6 m.',
        'Su adhesión media ayuda a evitar daños en paredes y que la cinta se dañe al pegarse consigo misma, según la ficha recibida.',
        'Madera, metal, plástico y paredes.',
        'Almacenar en su empaque original, en un área fresca y seca, lejos de la luz directa del sol.',
        'Aplicar sobre superficies limpias, secas y libres de grasas, aceites u otros contaminantes.',
        'Es la cinta estándar en la industria de la pintura.',
        'Las fotografías y descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'marca', 'Werken',
            'modelo', 'Cinta',
            'tipo_producto', 'Cinta',
            'subtipo_producto', 'Masking Tape',
            'material', 'Caucho Termoplástico',
            'altura_cm', 15,
            'ancho_cm', 13,
            'profundidad_cm', 2.7,
            'color', 'Blanco',
            'numero_piezas', 1,
            'peso_kg', 0.107,
            'longitud_metros', 36.6,
            'medida', '1 pulgada x 40 yardas',
            'presentacion', 'Rollo',
            'tipo_uso', 'Profesional',
            'uso', 'Enmascarar, sujetar, etiquetar y pintar'
        )
    UNION ALL
    SELECT
        '6547659671',
        'Cinta de papel ScotchBlue 3M azul para delimitar áreas de pintado.',
        'Cinta de papel de fácil remoción y adhesión media. La ficha recibida indica una longitud de 54.8 m y una anchura de 1.88 pulgadas; esta variante requiere confirmación, pues la ficha oficial consultada corresponde a una anchura diferente.',
        'La adhesión media permite delimitar áreas de pintado y retirar la cinta sin residuos dentro del plazo indicado para la variante compatible.',
        'Muros y pisos, según la ficha recibida.',
        'Aplicar sobre superficies limpias y secas. Retirar dentro de los 14 días indicados en la ficha recibida.',
        'No utilizar con o sobre laca, tapices, paneles de yeso sin pintar o pisos de madera.',
        'Confirmar la anchura de la variante antes de dar por validadas sus especificaciones.',
        'Las fotografías y descripción del producto son referenciales.',
        'Garantía por defecto de fabricación',
        JSON_OBJECT(
            'marca', '3M ScotchBlue',
            'modelo', '1.88 pulgadas',
            'tipo_producto', 'Cinta',
            'material', 'Papel',
            'altura_cm', 12,
            'ancho_cm', 12,
            'profundidad_cm', 5,
            'color', 'Azul',
            'numero_piezas', 1,
            'longitud_metros', 54.8,
            'medida', '1.88 pulgadas',
            'presentacion', 'Rollo',
            'tipo_uso', 'Profesional',
            'uso', 'Trabajos de pintura y delimitación de áreas de pintado',
            'variante_verificada', FALSE
        )
) AS d
    ON p.codigo = d.codigo;
-- ============================================
-- ============================================
-- BLOQUE 6
-- ============================================
-- ============================================
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica,
    activo
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica,
    1
FROM Producto p
JOIN (
    SELECT
        '9659809329' AS codigo,
        'Cinta de embalaje transparente Pegafan de 2 pulgadas y 100 m. Fabricada en polipropileno, es resistente al envejecimiento y a las variaciones de temperatura. Recomendada para sellar cajas, bolsas y paquetes de uso doméstico y comercial.' AS descripcion_comercial,
        'Resistente al envejecimiento y a las variaciones de temperatura. Fabricada en polipropileno, duradera y fácil de usar.' AS caracteristicas,
        'Buen desempeño en el sellado de todo tipo de cajas, incluso las más pesadas.' AS ventajas,
        'Cajas de cartón, bolsas y paquetes.' AS donde_usarlo,
        'Aplicar sobre una superficie limpia, seca y libre de grasa, aceite u otros contaminantes. Presionar después de colocar la cinta. Almacenar en su empaque original, bajo techo, en un lugar fresco y protegido de la luz solar directa.' AS recomendaciones_uso,
        'Mantener fuera del alcance de los niños.' AS advertencia_uso,
        'Puede complementar su compra con un dispensador de cinta.' AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        NULL AS garantia,
        JSON_OBJECT(
            'altura_producto', '4.8 cm',
            'ancho_producto', '10.2 cm',
            'profundidad_producto', '10.2 cm',
            'color_principal', 'Transparente',
            'color', 'Transparente',
            'material_acabado', 'Plastificado',
            'modelo', 'Embalaje',
            'tipo_producto', 'Cinta',
            'subtipo_producto', 'Embalaje',
            'material', 'Polipropileno',
            'tipo_color', 'Traslúcido',
            'numero_piezas', 1,
            'peso_producto', '0.15 kg',
            'capacidad', '2 pulgadas',
            'longitud', '100 m',
            'presentacion', 'Rollo',
            'tipo_uso', 'Doméstico/Comercial'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '8658548687',
        'Masilla moldeable Soldimix Moldimix de 32 g para reparar piezas metálicas. Permite sellar, pegar, rellenar y reconstruir. Al endurecerse, adquiere una consistencia similar al metal y presenta resistencia al calor y a la humedad. Se presenta en un blíster.',
        'Masilla moldeable. Sella, pega, rellena y reconstruye. Endurece como metal y es resistente al calor y a la humedad.',
        'Permite obtener un acabado fino en objetos metálicos.',
        'Tuberías domésticas, paredes, mangos de herramientas, cables, ventanas y muebles.',
        'Utilizar en lugares ventilados.',
        'Mantener fuera del alcance de los niños. Evitar el contacto con los ojos y las vías respiratorias.',
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '19.90 cm',
            'ancho_producto', '10.90 cm',
            'profundidad_producto', '1.00 cm',
            'color_principal', 'Negro',
            'color', 'Gris',
            'resistencia_ray os_uv', 'Sí',
            'estado_contenido', 'Sólido',
            'modelo', 'Moldimix',
            'tipo_producto', 'Pegamento',
            'material', 'Masilla',
            'tipo_color', 'Gris',
            'numero_piezas', 1,
            'resistencia_moho', 'Sí',
            'inflamable', 'No',
            'peso_producto', '0.04 kg',
            'impermeable', 'Sí',
            'resistencia_solventes', 'No',
            'resistencia_humedad', 'Sí',
            'capacidad', '0.032 kg',
            'presentacion', 'Blíster',
            'tiempo_secado', '45 min',
            'tipo_uso', 'Doméstico interior y exterior',
            'uso', 'Reparar piezas metálicas, reconstruir mangos de herramientas domésticas, industriales y automotrices. Sellar y rellenar agujeros.'
        )
    UNION ALL
    SELECT
        '7547327571',
        'Cola para madera PUR Tekbond de 500 g. Adhesivo monocomponente a base de poliuretano que cura con la humedad del aire. Ofrece alta resistencia en las uniones y resistencia al agua, según la información proporcionada.',
        'Adhesivo monocomponente a base de poliuretano que cura con la humedad del aire. Temperatura de aplicación: de +5 a 35 °C. Temperatura de servicio: de -40 a 90 °C. Olor característico.',
        'Presenta resistencia al envejecimiento, a la temperatura y al agua, según la información proporcionada.',
        'Ventanas, puertas, fabricación de escaleras, industria del mueble y otros trabajos de pegado de madera.',
        'Aplicar una cantidad suficiente para cubrir toda la pieza. Puede utilizarse una espátula para distribuir el adhesivo en una capa fina. Unir las partes y mantenerlas presionadas durante 2 horas. El curado total tarda 24 horas, aunque puede variar según la cantidad de adhesivo, la humedad y la temperatura.',
        'En caso de contacto con la piel o los ojos, lavar con abundante agua. Si los síntomas persisten, consultar a un médico. Para información sobre el manejo seguro, consultar la ficha de datos de seguridad del producto químico (FISPQ).',
        'Color miel.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '23 cm',
            'ancho_producto', '6 cm',
            'profundidad_producto', '6 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'estado_contenido', 'Líquido',
            'modelo', 'PUR',
            'tipo_producto', 'Cola sintética',
            'subtipo_producto', 'Pegamento',
            'material', 'Poliuretano',
            'numero_piezas', 1,
            'inflamable', 'No',
            'peso_producto', '0.56 kg',
            'resistencia_humedad', 'Sí',
            'capacidad', '500 g',
            'presentacion', 'Botella',
            'tiempo_secado', '24 h',
            'tipo_uso', 'Doméstico/Comercial'
        )
    UNION ALL
    SELECT
        '8798678578',
        'Silicona Sanisil Sika de 280 ml, antihongos. Está diseñada para sellar juntas en baños y cocinas. Es resistente a la mayoría de los mohos y hongos, así como a los jabones y detergentes de uso frecuente. Su fórmula tixotrópica evita que se escurra en juntas verticales o techos.',
        'Resistente a la mayoría de los mohos y hongos. Es tixotrópica y no se escurre en juntas verticales o techos. Ofrece un acabado transparente y resiste los microorganismos, jabones y detergentes de uso frecuente.',
        'Protección antihongos para baños y cocinas.',
        'Azulejos, gres, vidrio, hierro esmaltado y, en general, superficies vitrificadas.',
        'Limpiar las herramientas con diluyente inmediatamente después de utilizarlas. Evitar el contacto directo con los ojos, la piel y las vías respiratorias. Utilizar guantes de goma natural o sintética y anteojos de seguridad.',
        'No utilizar sobre soportes cementicios; para esos casos, emplear masillas. No utilizar sobre vidrios delicados o sensibles ni sobre metales fácilmente oxidables.',
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '23.50 cm',
            'ancho_producto', '7.00 cm',
            'profundidad_producto', '5.00 cm',
            'color_principal', 'Transparente',
            'resistencia_ray os_uv', 'No',
            'estado_contenido', 'Pasta',
            'modelo', 'Sanisil',
            'tipo_producto', 'Silicona',
            'material', 'Silicona',
            'numero_piezas', 1,
            'resistencia_moho', 'Sí',
            'peso_producto', '0.30 kg',
            'resistencia_solventes', 'No',
            'resistencia_humedad', 'Sí',
            'capacidad', '280 ml',
            'presentacion', 'Pote',
            'tiempo_secado', '8 h',
            'tipo_uso', 'General'
        )
    UNION ALL
    SELECT
        '8748938873',
        'La pistola para silicona Pro permite aplicar selladores y adhesivos de manera precisa. Su barril giratorio facilita la aplicación en trabajos de construcción y reparación.',
        'Barril giratorio.',
        NULL,
        NULL,
        'No dejar a la intemperie.',
        NULL,
        'Longitud del tubo: 23 cm.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '18.00 cm',
            'ancho_producto', '32.00 cm',
            'profundidad_producto', '6.00 cm',
            'color_principal', 'Azul',
            'material_empaque_primario', 'Acero',
            'peso_empaque_primario', '1 g',
            'resistencia_ray os_uv', 'No',
            'tipo_producto', 'Pistola',
            'subtipo_producto', 'Silicona',
            'material', 'Acero',
            'numero_piezas', 1,
            'resistencia_moho', 'No',
            'peso_producto', '0.58 kg',
            'resistencia_solventes', 'No',
            'resistencia_humedad', 'No',
            'capacidad', '14 oz',
            'presentacion', 'Bolsa',
            'tipo_uso', 'Doméstico interior y exterior'
        )
) AS d
    ON p.codigo = d.codigo;
-- ============================================
-- ============================================
-- BLOQUE 7
-- ============================================
-- ============================================
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica,
    activo
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica,
    1
FROM Producto p
JOIN (
    SELECT
        '7657473711' AS codigo,
        'Sellador universal Soudal de 300 ml, diseñado para sellar juntas en puertas, ventanas, baños y cocinas. Sellador multiuso para trabajos de construcción.' AS descripcion_comercial,
        'Excelente calidad y resistencia.' AS caracteristicas,
        'Sellador multiuso.' AS ventajas,
        'En construcción.' AS donde_usarlo,
        'Ideal para sellar juntas en puertas, ventanas, baños y cocinas. Se recomienda realizar pruebas previas.' AS recomendaciones_uso,
        'Seguir los procedimientos habituales de higiene laboral.' AS advertencia_uso,
        NULL AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        NULL AS garantia,
        JSON_OBJECT(
            'altura_producto', '23 cm',
            'ancho_producto', '5 cm',
            'profundidad_producto', '5 cm',
            'color_principal', 'Blanco',
            'color', 'Blanco',
            'resistencia_rayos_uv', 'Sí',
            'estado_contenido', 'Pasta',
            'modelo', 'Cartucho 300 ml',
            'tipo_producto', 'Sellador',
            'subtipo_producto', 'Universal',
            'material', 'Silicona',
            'numero_piezas', 1,
            'resistencia_moho', 'Sí',
            'peso_producto', '0.30 kg',
            'resistencia_solventes', 'Sí',
            'resistencia_humedad', 'Sí',
            'capacidad', '300 ml',
            'presentacion', 'Pote',
            'tiempo_secado', '1 h',
            'tipo_uso', 'General'
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '4457765298',
        'Temple Majestad blanco de 5 kg, pintura acuosa ecológica de cero VOC, elaborada con resina vegetal pigmentada. Se utiliza en cielos rasos, cercos perimétricos y otras superficies indicadas por el fabricante.',
        'Producto acuoso de cero VOC, de bajo olor y no inflamable. Como imprimante, diluir con 1/8 o 1/4 de agua por kilogramo de temple. Como empastador, mezclar con 1 litro de sellador por cada 25 kg de temple.',
        'Fácil aplicación y bajo olor.',
        'Cielos rasos, cercos perimétricos, señalización de árboles y jardines.',
        'Aplicar sobre una superficie seca y limpia de polvo, cemento mal adherido, aceite, grasa y otros contaminantes. En cemento nuevo, esperar 28 días antes de pintar. Aplicar con una temperatura ambiente superior a 10 °C e inferior a 40 °C y humedad relativa menor al 85 %. Esperar al menos 2 horas antes de aplicar la segunda capa. Aplicar con plancha, brocha, rodillo o pistola en buen estado.',
        'Puede causar irritación ocular. En caso de contacto, enjuagar inmediatamente con abundante agua durante al menos 15 minutos. Evitar el contacto prolongado con la piel. No ingerir. En caso de ingestión, buscar atención médica inmediata. Mantener fuera del alcance de los niños.',
        'Fácil aplicación. Para acabados de paredes intermedios y óptimos, utilizar antes del sellador y de la pintura final. El rendimiento real depende de las condiciones de aplicación y del estado de la superficie.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '3.50 cm',
            'ancho_producto', '24 cm',
            'profundidad_producto', '35 cm',
            'color_principal', 'Blanco',
            'estado_contenido', 'Pasta',
            'tipo_producto', 'Temple',
            'material', 'Otros',
            'numero_piezas', 1,
            'metodo_aplicacion', 'Brocha, rodillo o plancha',
            'rendimiento', '20 m2',
            'peso_producto', '5 kg',
            'capacidad', '5 kg',
            'presentacion', 'Bolsa',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Profesional',
            'uso', 'Como temple o imprimante de muros y techos interiores',
            'rendimiento_teorico', '2.8 a 3 m2/kg',
            'rendimiento_imprimante', '3.5 a 4 m2/kg'
        )
    UNION ALL
    SELECT
        '0049387632',
        'Sellador de resina látex transparente CPP que sella superficies con pintura antigua y las prepara para recibir acabados.',
        'Sella superficies con pintura antigua y las deja aptas para la aplicación de acabados.',
        'Secado rápido.',
        'En concreto, drywall, Superboard y superficies con pintura antigua, pastas o temple.',
        'En superficies con pintura en buen estado, eliminar polvo, grasa y otros contaminantes. Lijar con lija al agua n.º 120 y aplicar Sellador 150 CPP. En superficies con pintura en mal estado, humedecer con agua, retirar la pintura mal adherida con una espátula, eliminar el polvo, dejar secar y aplicar Sellador 150 CPP.',
        'El uso o la manipulación inadecuados pueden ser nocivos para la salud. Seguir las precauciones de seguridad, incluida una ventilación adecuada, vestimenta apropiada y guantes.',
        'Se garantiza una buena estabilidad durante el almacenamiento de hasta 12 meses si se conserva bajo techo, entre 4 °C y 38 °C.',
        'Las fotografías y la descripción del producto son referenciales.',
        '1 año',
        JSON_OBJECT(
            'altura_producto', '11.50 cm',
            'ancho_producto', '13 cm',
            'profundidad_producto', '13 cm',
            'color_principal', 'Transparente',
            'color', 'Transparente',
            'tipo_acabado', 'Mate',
            'tipo_producto', 'Sellador',
            'material', 'Resina látex',
            'numero_piezas', 1,
            'metodo_aplicacion', 'Brocha, rodillo o plancha',
            'rendimiento', '28–36 m2 por 4 l por mano',
            'peso_producto', '1.28 kg',
            'capacidad', '0.946 l',
            'presentacion', 'Balde',
            'tipo_uso', 'Interior/Exterior',
            'uso', 'Revestimiento de paredes interiores y exteriores'
        )
    UNION ALL
    SELECT
        '0302030471',
        'Imprimante CPP de resina acrílica de 1 galón, con buen poder de relleno sobre superficies de concreto nuevo.',
        'Imprimante con buen poder de relleno para concreto nuevo.',
        NULL,
        'En superficies de concreto nuevo.',
        NULL,
        NULL,
        'No contiene mercurio ni plomo.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '18 cm',
            'ancho_producto', '21 cm',
            'profundidad_producto', '21 cm',
            'color_principal', 'Blanco',
            'estado_contenido', 'Líquido',
            'tipo_producto', 'Imprimante',
            'material', 'Resina acrílica',
            'numero_piezas', 1,
            'metodo_aplicacion', 'Brocha, rodillo o plancha',
            'rendimiento', '53 m2',
            'peso_producto', '5.40 kg',
            'capacidad', '1 galón',
            'presentacion', 'Balde',
            'tiempo_secado', '4 h',
            'tipo_uso', 'Profesional',
            'uso', 'Para superficies de concreto nuevo'
        )
    UNION ALL
    SELECT
        '4478112093',
        'Barniz poliuretano brillante Tekno A5 transparente de 1 litro. Producto de dos componentes elaborado con resinas de poliuretano que, al secar, forma una película de excelente adherencia, dureza y alto brillo. Diseñado para proteger pisos y muebles de madera.',
        'Producto de dos componentes que ofrece adherencia, dureza, alto brillo y resistencia a las rayaduras, la humedad, los disolventes y los ácidos leves.',
        NULL,
        'Para pisos de madera.',
        'Mezclar los dos componentes y el disolvente según la proporción recomendada.',
        'Mantener fuera del alcance de los niños.',
        'No debe aplicarse solo; debe complementarse con el barniz B5. La mezcla de ambos productos rinde 120 m2 a una mano, utilizando un galón de cada componente, según la información proporcionada.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '24 cm',
            'ancho_producto', '9 cm',
            'profundidad_producto', '9 cm',
            'color_principal', 'Transparente',
            'color', 'Transparente',
            'resistencia_rayos_uv', 'No',
            'estado_contenido', 'Líquido',
            'tipo_acabado', 'Brillante',
            'modelo', 'A5',
            'tipo_producto', 'Barniz',
            'material', 'Poliuretano',
            'numero_piezas', 1,
            'trafico', 'Alto',
            'resistencia_moho', 'Sí',
            'metodo_aplicacion', 'Brocha y soplete',
            'inflamable', 'Sí',
            'rendimiento', '45 m2/gal',
            'resistencia_solventes', 'Sí',
            'resistencia_humedad', 'Sí',
            'diluyente', 'Sí',
            'capacidad', '1 l',
            'presentacion', 'Botella',
            'tiempo_secado', '8–12 h',
            'tipo_uso', 'Doméstico/Comercial'
        )
    UNION ALL
    SELECT
        '6577546112',
        'Barniz brillante Doble Acción Tekno de 1 galón, elaborado con resinas alquídicas. Al secar, forma una película impermeable que retiene el brillo y resiste la radiación ultravioleta.',
        'Elaborado con resinas alquídicas que forman una película impermeable, retienen el brillo y resisten la radiación UV.',
        'Alta resistencia a la humedad y a la radiación.',
        'En estructuras de madera interiores y exteriores.',
        'Verificar que se disponga del diluyente recomendado. La brocha y la pistola deben estar limpias y en buen estado.',
        'Mantener fuera del alcance de los niños.',
        'Contiene materiales inflamables. Almacenar bajo techo y aplicar lejos de fuentes de calor, en lugares limpios y secos.',
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_producto', '19.50 cm',
            'ancho_producto', '19 cm',
            'profundidad_producto', '19 cm',
            'color_principal', 'Transparente',
            'resistencia_rayos_uv', 'Sí',
            'estado_contenido', 'Líquido',
            'tipo_acabado', 'Brillante',
            'modelo', 'Doble acción',
            'tipo_producto', 'Barniz',
            'material', 'Resina alquídica',
            'numero_piezas', 1,
            'trafico', 'No transitable',
            'resistencia_moho', 'Sí',
            'metodo_aplicacion', 'Brocha y soplete',
            'inflamable', 'Sí',
            'rendimiento', '49 m2/gal',
            'resistencia_solventes', 'Sí',
            'resistencia_humedad', 'Sí',
            'diluyente', 'Sí',
            'capacidad', '1 galón',
            'presentacion', 'Tarro',
            'tiempo_secado', '24 h',
            'tipo_uso', 'Doméstico/Comercial'
        )
) AS d
    ON p.codigo = d.codigo;

-- ============================================
-- ============================================
-- BLOQUE 8
-- ============================================
-- ============================================
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica,
    activo
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica,
    1
FROM Producto p
JOIN (
    SELECT
        '3057914036' AS codigo,
        'Espátula flexible Werken modelo GSPE041 de 3 pulgadas, con hoja de acero al carbón y mango de plástico unido mediante remaches.' AS descripcion_comercial,
        'Espátula flexible para juntas, con mango de plástico y perforación para colgar.' AS caracteristicas,
        'Tamaño compacto, agarre cómodo y facilidad de almacenamiento.' AS ventajas,
        'Trabajos de juntas y tareas domésticas compatibles con una espátula.' AS donde_usarlo,
        'Mantener en un lugar limpio y ventilado, lejos de la humedad. Enjuagar después de utilizar y lubricar si se almacenará durante un periodo prolongado.' AS recomendaciones_uso,
        'Mantener fuera del alcance de los niños. Es una herramienta punzocortante. Utilizar equipos de seguridad adecuados y emplearla únicamente para el trabajo previsto.' AS advertencia_uso,
        'El producto no es inoxidable. Resistente a solventes.' AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        '1 año' AS garantia,
        JSON_OBJECT(
            'altura_cm', 21,
            'ancho_cm', 7.6,
            'profundidad_cm', 1.6,
            'color', 'Plata',
            'modelo', 'GSPE041',
            'tipo_producto', 'Espátula',
            'material', 'Metal/Plástico',
            'material_hoja', 'Acero al carbón',
            'material_mango', 'Plástico',
            'medida', '3 pulgadas',
            'numero_piezas', 1,
            'tipo_uso', 'Doméstico',
            'resistente_solventes', TRUE,
            'no_inoxidable', TRUE
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '8849334876',
        'Juego de destornilladores Werken de 4 piezas para montar y desmontar tornillos y pernos.',
        'Juego de herramientas manuales destinadas a operaciones de atornillado y desatornillado.',
        'Permite realizar tareas básicas de montaje, reparación y mantenimiento.',
        'Trabajos domésticos y tareas de montaje compatibles con los destornilladores incluidos.',
        'Seleccionar la punta que coincida con la cabeza del tornillo. Mantener las herramientas limpias y secas y evitar utilizarlas como palanca.',
        'No utilizar herramientas dañadas ni emplearlas en trabajos eléctricos como si fueran dieléctricas. Mantener fuera del alcance de los niños.',
        NULL,
        NULL,
        NULL,
        JSON_OBJECT(
            'marca', 'Werken',
            'tipo_producto', 'Juego de destornilladores',
            'numero_piezas', 4,
            'uso', 'Montaje y desmontaje de tornillos y pernos'
        )
    UNION ALL
    SELECT
        '0039280253',
        'Juego de destornilladores Stanley modelo 91-539 de 6 piezas, con destornilladores planos y de estrella.',
        'Destornilladores de acero con mangos ergonómicos y recubrimiento de PVC. Las medidas están identificadas en los mangos.',
        'Incluye diferentes tipos de punta para atender distintas tareas de atornillado.',
        'Trabajos de atornillado y desatornillado de uso profesional.',
        'Mantener en un lugar limpio y ventilado, lejos de la humedad. Utilizar el destornillador adecuado para cada tornillo.',
        'Mantener fuera del alcance de los niños. Utilizar implementos de seguridad adecuados y elegir el destornillador compatible con el tornillo.',
        'Las puntas presentan bajo magnetismo, adecuado para tornillos pequeños y livianos.',
        'Las fotografías y la descripción del producto son referenciales.',
        '1 año',
        JSON_OBJECT(
            'altura_cm', 37,
            'ancho_cm', 21,
            'profundidad_cm', 4,
            'color', 'Negro',
            'modelo', '91-539',
            'recubrimiento_mango', 'PVC',
            'acabado', 'Pulido',
            'tipo_producto', 'Juego de destornilladores',
            'material', 'Acero',
            'numero_piezas', 6,
            'resistencia_dieletrica', FALSE,
            'magnetico', TRUE,
            'peso_kg', 0.50,
            'tipos_punta', JSON_ARRAY('Estrella', 'Plano'),
            'tipo_uso', 'Profesional',
            'incluye', JSON_ARRAY(
                '3 destornilladores planos',
                '3 destornilladores de estrella'
            ),
            'observacion_magnetismo', 'Bajo magnetismo, ideal para tornillos pequeños y livianos'
        )
    UNION ALL
    SELECT
        '7784934410',
        'Desarmador dieléctrico de cruz Truper de 1/4 x 4 pulgadas.',
        'Herramienta de acero con punta de cruz y mango ergonómico antideslizante.',
        'Su diseño dieléctrico está destinado a trabajos que requieran una herramienta aislada adecuada.',
        'Trabajos profesionales de atornillado que requieran punta de cruz.',
        'Revisar que el aislamiento esté intacto antes de usar. Elegir la punta adecuada para el tornillo.',
        'No utilizar si el aislamiento presenta daños. En trabajos eléctricos, verificar la clasificación de tensión y seguir los procedimientos de seguridad correspondientes.',
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'altura_cm', 10.16,
            'ancho_cm', 2.5,
            'profundidad_cm', 2.5,
            'color', 'Naranja',
            'marca', 'Truper',
            'tipo_producto', 'Desarmador',
            'material', 'Acero',
            'material_empaque_primario', 'Cartón',
            'peso_empaque_primario_g', 20,
            'resistencia_dieletrica', TRUE,
            'magnetico', TRUE,
            'peso_kg', 0.16,
            'largo_barra_pulgadas', 4,
            'medida', '1/4 x 4 pulgadas',
            'numero_piezas', 1,
            'tipo_uso', 'Profesional',
            'punta', 'Cruz'
        )
    UNION ALL
    SELECT
        '0394876382',
        'Llave mixta Kamasa modelo KM7615 de 15 mm, fabricada en acero cromo-vanadio.',
        'Llave combinada de boca y corona fabricada en acero cromo-vanadio (Cr-V).',
        'Material resistente y duradero para tareas de ajuste y mantenimiento.',
        'Aflojar o ajustar tuercas y pernos compatibles con la medida de 15 mm, en espacios accesibles.',
        'Utilizar la medida adecuada para la tuerca o el perno y trabajar en espacios accesibles.',
        NULL,
        'La ficha indica que no posee resistencia dieléctrica. El acabado requiere verificación, ya que la descripción y la ficha técnica presentan datos distintos.',
        'Las fotografías y la descripción del producto son referenciales.',
        '6 meses',
        JSON_OBJECT(
            'altura_cm', 19,
            'ancho_cm', 1.5,
            'profundidad_cm', 1,
            'color', 'Plata',
            'marca', 'Kamasa',
            'modelo', 'KM7615',
            'material', 'Acero cromo-vanadio (Cr-V)',
            'tipo_producto', 'Llave',
            'subtipo', 'Combinada',
            'resistencia_dieletrica', FALSE,
            'agarre', 'Boca/Corona',
            'peso_kg', 0.140,
            'medida', '15 mm',
            'numero_piezas', 1,
            'tipo_uso', 'Profesional',
            'uso', 'Tuercas hexagonales y cuadradas'
        )
) AS d
    ON p.codigo = d.codigo;

-- ============================================
-- ============================================
-- BLOQUE 9
-- ============================================
-- ============================================
INSERT INTO Detalle_Tecnico_Producto (
    id_producto,
    descripcion_comercial,
    caracteristicas,
    ventajas,
    donde_usarlo,
    recomendaciones_uso,
    advertencia_uso,
    observaciones,
    observaciones_adicionales,
    garantia,
    ficha_tecnica,
    activo
)
SELECT
    p.id_producto,
    d.descripcion_comercial,
    d.caracteristicas,
    d.ventajas,
    d.donde_usarlo,
    d.recomendaciones_uso,
    d.advertencia_uso,
    d.observaciones,
    d.observaciones_adicionales,
    d.garantia,
    d.ficha_tecnica,
    1
FROM Producto p
JOIN (
    SELECT
        '0966744321' AS codigo,
        'Llave Stilson Truper de 8 pulgadas, con cuerpo robusto de hierro dúctil y mordaza ajustable de acero al cromo-molibdeno.' AS descripcion_comercial,
        'Herramienta manual para tuberías, con mordaza ajustable y cuerpo robusto.' AS caracteristicas,
        'Permite sujetar y girar tuberías y piezas cilíndricas compatibles.' AS ventajas,
        'Trabajos de plomería y mantenimiento de tuberías.' AS donde_usarlo,
        'Ajustar la mordaza al tamaño de la pieza y comprobar que el agarre sea firme antes de aplicar fuerza.' AS recomendaciones_uso,
        'Mantener las manos alejadas de los puntos de atrapamiento. No utilizar una herramienta dañada.' AS advertencia_uso,
        NULL AS observaciones,
        'Las fotografías y la descripción del producto son referenciales.' AS observaciones_adicionales,
        NULL AS garantia,
        JSON_OBJECT(
            'altura_cm', 20,
            'ancho_cm', 4,
            'profundidad_cm', 2.5,
            'color', 'Naranja',
            'marca', 'Truper',
            'material_cuerpo', 'Hierro dúctil',
            'material_mordaza', 'Acero al cromo-molibdeno',
            'tipo_producto', 'Llave',
            'subtipo', 'Stilson',
            'peso_kg', 1,
            'numero_piezas', 1,
            'medida', '8 pulgadas',
            'apertura_maxima_mm', 19
        ) AS ficha_tecnica
    UNION ALL
    SELECT
        '1234563421',
        'Remachadora manual Werken para colocar remaches de distintos tamaños y diámetros compatibles con la herramienta.',
        'Herramienta manual destinada a realizar uniones mediante remaches.',
        'Permite fijar piezas mediante un proceso de remachado.',
        'Trabajos de montaje, reparación y unión de materiales compatibles con el remache seleccionado.',
        'Seleccionar el remache adecuado para la herramienta y el material que se va a unir.' ,
        'Mantener los dedos alejados de las partes móviles durante la operación y mantener la herramienta fuera del alcance de los niños.',
        NULL,
        NULL,
        NULL,
        JSON_OBJECT(
            'marca', 'Werken',
            'tipo_producto', 'Remachadora manual',
            'numero_piezas', 1,
            'uso', 'Colocación de remaches compatibles'
        )
    UNION ALL
    SELECT
        '6675884931',
        'Crimpeadora pelacables Dexter de 195 mm, fabricada en acero al carbono con pintura electroforética y mango de TPE.',
        'Herramienta con mango antideslizante, diseño compacto y longitud de barra de 195 mm.',
        'Facilita el engaste de terminales en cables de datos y de corriente eléctrica compatibles.',
        'Trabajos de cableado y conexión de terminales compatibles con la herramienta.',
        'Utilizar la herramienta con terminales y cables compatibles. Mantenerla limpia y guardarla en un lugar seco.',
        'No brinda protección contra descargas eléctricas. Es una herramienta con bordes afilados; mantener fuera del alcance de los niños.',
        'No posee resistencia dieléctrica.',
        'Las fotografías y la descripción del producto son referenciales.',
        '5 años',
        JSON_OBJECT(
            'altura_cm', 19.5,
            'ancho_cm', 7.5,
            'profundidad_cm', 2.5,
            'color', 'Azul',
            'marca', 'Dexter',
            'modelo', '195 mm',
            'tipo_producto', 'Alicates',
            'subtipo', 'Herramientas',
            'material', 'Acero/Plástico',
            'material_hoja', 'Acero al carbono',
            'recubrimiento', 'Pintura electroforética',
            'material_mango', 'TPE',
            'numero_piezas', 1,
            'resistencia_dieletrica', FALSE,
            'hoja_extraible', FALSE,
            'hoja_retractil', FALSE,
            'antideslizante', TRUE,
            'peso_kg', 0.27,
            'largo_barra_mm', 195,
            'tipo_uso', 'Semi-profesional'
        )
    UNION ALL
    SELECT
        '8458998321',
        'Martillo de uña Werken de 27 mm con mango de madera, para trabajos de construcción y pequeñas reparaciones.',
        'Herramienta manual de golpeo con uña para tareas relacionadas con clavos.',
        'Adecuado para trabajos básicos de carpintería y reparación.',
        'Trabajos domésticos, carpintería y pequeñas tareas de construcción compatibles.',
        'Revisar que la cabeza esté firmemente sujeta al mango antes de usar. Guardar en un lugar seco.',
        'Utilizar protección ocular y mantener las manos alejadas de la zona de golpeo.',
        NULL,
        NULL,
        NULL,
        JSON_OBJECT(
            'marca', 'Werken',
            'tipo_producto', 'Martillo de uña',
            'medida', '27 mm',
            'material_mango', 'Madera',
            'numero_piezas', 1
        )
    UNION ALL
    SELECT
        '3455737912',
        'Serrucho Stanley Luctador modelo 15-469 de 16 pulgadas, con hoja de acero de alto contenido de carbono y mango de madera.',
        'Hoja con dientes afilables, trabados y pulidos, recubierta con barniz protector.',
        'Permite realizar cortes manuales en trabajos de carpintería.',
        'Corte de madera y tareas de carpintería compatibles con un serrucho manual.',
        'Mantener el producto en un lugar limpio y ventilado, lejos de la humedad.',
        'Mantener fuera del alcance de los niños. Leer las indicaciones antes de utilizarlo y emplear equipos de seguridad adecuados.',
        'La hoja y el mango están unidos firmemente mediante tornillos y tuerca.',
        'Las fotografías y la descripción del producto son referenciales.',
        '1 año',
        JSON_OBJECT(
            'altura_cm', 12,
            'ancho_cm', 48,
            'profundidad_cm', 2.2,
            'color', 'Plata',
            'marca', 'Stanley',
            'modelo', '15-469',
            'tipo_producto', 'Serrucho',
            'subtipo', 'Luctador',
            'material_hoja', 'Acero de alto contenido de carbono',
            'numero_piezas', 1,
            'dientes_por_pulgada', 6,
            'peso_kg', 0.45,
            'material_mango', 'Madera',
            'largo_hoja_pulgadas', 16,
            'tipo_uso', 'Profesional'
        )
    UNION ALL
    SELECT
        '8473329084',
        'Alicate pico de loro Werken de 10 pulgadas, herramienta multipropósito para sujetar y girar piezas compatibles.',
        'Herramienta manual de agarre ajustable, según el diseño habitual de los alicates pico de loro.',
        'Permite sujetar piezas de distintos tamaños dentro de su capacidad de apertura.',
        'Trabajos generales de mantenimiento y ajuste.',
        'Ajustar la mordaza al tamaño de la pieza y comprobar que el agarre sea firme antes de aplicar fuerza.',
        'No utilizar como sustituto de una herramienta aislada para trabajos eléctricos. Mantener fuera del alcance de los niños.',
        NULL,
        'Las fotografías y la descripción del producto son referenciales.',
        NULL,
        JSON_OBJECT(
            'marca', 'Werken',
            'tipo_producto', 'Alicate',
            'subtipo', 'Pico de loro',
            'medida', '10 pulgadas',
            'numero_piezas', 1
        )
) AS d
    ON p.codigo = d.codigo;