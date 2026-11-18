
@echo off
REM Requiere Java 24+ y MySQL con la base bd_colorin.
REM Las credenciales se obtienen desde Configuracion.java.

cd /d "%~dp0.."

if "%DB_USERNAME%"=="" set DB_USERNAME=colorin_app
if "%DB_URL%"=="" set DB_URL=jdbc:mysql://localhost:3306/bd_colorin

java -jar "target\ferreteria-colorin.jar"
