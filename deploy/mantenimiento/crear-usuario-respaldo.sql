-- Usuario exclusivo para respaldos y pruebas de restauracion (ejecutar como administrador/root).
-- Principio de minimo privilegio: solo LEE bd_colorin y puede crear/borrar unicamente bd_colorin_prueba.
-- Cambie la clave y use la misma en respaldo.cnf.
CREATE USER IF NOT EXISTS 'colorin_respaldo'@'localhost' IDENTIFIED BY 'CAMBIAR_ESTA_CLAVE';
GRANT SELECT, SHOW VIEW, TRIGGER, EVENT, LOCK TABLES ON bd_colorin.* TO 'colorin_respaldo'@'localhost';
GRANT ALL PRIVILEGES ON bd_colorin_prueba.* TO 'colorin_respaldo'@'localhost';
FLUSH PRIVILEGES;
