
#!/usr/bin/env bash
# Inicia el sistema de escritorio. Requiere JDK/JRE 24+ y MySQL con bd_colorin.
# Las credenciales se obtienen desde Configuracion.java.

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.." || exit 1

export DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/bd_colorin}"
export DB_USERNAME="${DB_USERNAME:-colorin_app}"

exec java -jar "target/ferreteria-colorin.jar"
