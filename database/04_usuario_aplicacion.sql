-- Usuario de aplicacion con minimo privilegio (OWASP A05 / A01)
-- Ejecutar como administrador de MySQL. CAMBIAR la clave antes de usar.
CREATE USER IF NOT EXISTS 'colorin_app'@'localhost' IDENTIFIED BY 'CAMBIAR_ESTA_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE ON bd_colorin.* TO 'colorin_app'@'localhost';
FLUSH PRIVILEGES;
-- La aplicacion (escritorio y web) ya no necesita el usuario root:
--   DB_USERNAME=colorin_app   DB_PASSWORD=<la clave definida arriba>
-- Si se usa un servidor remoto, reemplazar 'localhost' por el host de la aplicacion.
