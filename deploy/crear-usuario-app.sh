#!/bin/bash
# Crea el usuario de mínimo privilegio al inicializar el contenedor MySQL.
mysql -uroot -p"$MYSQL_ROOT_PASSWORD" <<SQL
CREATE USER IF NOT EXISTS 'colorin_app'@'%' IDENTIFIED BY '${DB_APP_PASSWORD}';
GRANT SELECT, INSERT, UPDATE, DELETE ON bd_colorin.* TO 'colorin_app'@'%';
FLUSH PRIVILEGES;
SQL
