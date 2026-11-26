# ============================================================
# levantar-sistema.ps1  (version mejorada, basada en la tuya)
# Cambios: comprueba la contrasena de MySQL antes de abrir el
# escritorio, muestra el motivo de los errores, muestra un cuadro
# de estado al terminar y agrega el modo -Estado.
# Todo el texto esta SIN tildes ni enie a proposito, para que se
# vea bien en Windows PowerShell 5.1 sin importar la codificacion.
# ============================================================
param(
    [string]$Usuario = "colorin_app",
    [string]$UrlBd = "jdbc:mysql://localhost:3306/bd_colorin",
    [switch]$Compilar,
    [switch]$Detener,
    [switch]$Estado
)

$ErrorActionPreference = "Stop"

# Rutas relativas a la ubicacion del script.
$Base = $PSScriptRoot
$RutaEscritorio = Join-Path $Base "FERRETERIA-SISTEMA"
$RutaWeb = Join-Path $Base "backend-web"

# Archivos de ejecucion.
$Runtime = Join-Path $Base ".runtime"
$PidFile = Join-Path $Runtime "backend.pid"
$LogOut = Join-Path $Runtime "backend.log"
$LogErr = Join-Path $Runtime "backend-error.log"

$JarEscritorio = Join-Path $RutaEscritorio "target\ferreteria-colorin.jar"
$JarWeb = Join-Path $RutaWeb "target\backend-web-0.0.1-SNAPSHOT.jar"
$BatEscritorio = Join-Path $RutaEscritorio "deploy\iniciar-escritorio.bat"

# Consulta que obliga al backend a usar la base de datos.
$UrlPrueba = "http://localhost:8080/api/productos"

New-Item -ItemType Directory -Path $Runtime -Force | Out-Null

function Test-Puerto {
    param([int]$Puerto)

    $Cliente = New-Object System.Net.Sockets.TcpClient

    try {
        $Resultado = $Cliente.BeginConnect("127.0.0.1", $Puerto, $null, $null)
        $Conectado = $Resultado.AsyncWaitHandle.WaitOne(1500, $false)

        if (-not $Conectado) {
            return $false
        }

        $Cliente.EndConnect($Resultado)
        return $true
    }
    catch {
        return $false
    }
    finally {
        $Cliente.Close()
    }
}

# Devuelve el codigo HTTP de una direccion (0 si no responde).
function Obtener-CodigoHttp {
    param([string]$Url)

    try {
        $Respuesta = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
        return [int]$Respuesta.StatusCode
    }
    catch {
        if ($_.Exception.Response) {
            return [int]$_.Exception.Response.StatusCode
        }
        return 0
    }
}

# Muestra las ultimas lineas de un archivo de registro.
function Mostrar-UltimasLineas {
    param(
        [string]$Archivo,
        [int]$Cantidad = 15
    )

    if ((Test-Path $Archivo) -and ((Get-Item $Archivo).Length -gt 0)) {
        Write-Host "`nUltimas lineas de $Archivo :" -ForegroundColor Yellow
        Get-Content $Archivo -Tail $Cantidad | ForEach-Object { Write-Host "  $_" }
    }
}

# Cuadro de estado de los programas.
function Mostrar-Estado {
    Write-Host "`n===== ESTADO DEL SISTEMA =====" -ForegroundColor Cyan

    # MySQL
    if (Test-Puerto -Puerto 3306) {
        Write-Host "MySQL (puerto 3306)  : ACTIVO" -ForegroundColor Green
    }
    else {
        Write-Host "MySQL (puerto 3306)  : CAIDO" -ForegroundColor Red
    }

    # Backend web
    $IdBackend = $null
    $ProcesoActivo = $false

    if (Test-Path $PidFile) {
        $IdBackend = [int](Get-Content $PidFile)
        $ProcesoActivo = [bool](Get-Process -Id $IdBackend -ErrorAction SilentlyContinue)
    }

    if ($ProcesoActivo -and (Test-Puerto -Puerto 8080)) {
        $Codigo = Obtener-CodigoHttp -Url $UrlPrueba

        if ($Codigo -eq 200) {
            Write-Host "Backend web (8080)   : ACTIVO (proceso $IdBackend, base de datos OK)" -ForegroundColor Green
        }
        else {
            Write-Host "Backend web (8080)   : ACTIVO pero con problemas (HTTP $Codigo)" -ForegroundColor Yellow
        }
    }
    elseif (Test-Puerto -Puerto 8080) {
        Write-Host "Backend web (8080)   : puerto ocupado por otro proceso" -ForegroundColor Yellow
    }
    else {
        Write-Host "Backend web (8080)   : DETENIDO" -ForegroundColor Red
    }

    # Escritorio
    $Escritorio = Get-CimInstance Win32_Process -Filter "Name = 'java.exe' OR Name = 'javaw.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -like "*ferreteria-colorin.jar*" }

    if ($Escritorio) {
        Write-Host "Escritorio           : ACTIVO (proceso $($Escritorio[0].ProcessId))" -ForegroundColor Green
    }
    else {
        Write-Host "Escritorio           : no detectado" -ForegroundColor Yellow
    }

    Write-Host "Registros del backend: $Runtime"
    Write-Host "==============================`n" -ForegroundColor Cyan
}

function Detener-Backend {
    if (-not (Test-Path $PidFile)) {
        Write-Host "No hay un backend registrado por este script."
        return
    }

    $IdProceso = [int](Get-Content $PidFile)

    $Proceso = Get-CimInstance Win32_Process `
        -Filter "ProcessId = $IdProceso" `
        -ErrorAction SilentlyContinue

    if ($null -eq $Proceso) {
        Remove-Item $PidFile -Force
        Write-Host "El backend ya no estaba ejecutandose."
        return
    }

    # Evita detener un proceso ajeno.
    if ($Proceso.Name -notmatch "^javaw?\.exe$" -or
        $Proceso.CommandLine -notlike "*backend-web-0.0.1-SNAPSHOT.jar*") {
        throw "El PID registrado no corresponde al backend esperado. No se detuvo."
    }

    taskkill /PID $IdProceso /T /F | Out-Null
    Remove-Item $PidFile -Force

    Write-Host "Backend detenido correctamente."
}

function Compilar-Proyecto {
    param(
        [string]$Carpeta,
        [string]$Nombre
    )

    Write-Host "`nCompilando $Nombre..." -ForegroundColor Cyan

    Push-Location $Carpeta

    try {
        $Wrapper = Join-Path $Carpeta "mvnw.cmd"

        if (Test-Path $Wrapper) {
            & $Wrapper "package"
        }
        else {
            $Maven = Get-Command mvn -ErrorAction SilentlyContinue

            if (-not $Maven) {
                throw "No se encontro Maven ni mvnw.cmd para $Nombre."
            }

            & $Maven.Source "package"
        }

        if ($LASTEXITCODE -ne 0) {
            throw "Fallo la compilacion de $Nombre."
        }
    }
    finally {
        Pop-Location
    }

    Write-Host "$Nombre compilado correctamente." -ForegroundColor Green
}


function Verificar-Java {
    param([int]$VersionMinima)

    $Java = Get-Command java -ErrorAction SilentlyContinue

    if (-not $Java) {
        throw "Java no esta instalado o no esta configurado en PATH."
    }

    # java -version escribe en stderr; cmd permite capturarlo sin
    # convertirlo en una excepcion de PowerShell.
    $VersionTexto = cmd.exe /c "`"$($Java.Source)`" -version 2>&1" |
        Out-String

    if ($VersionTexto -notmatch 'version "(\d+)') {
        throw "No se pudo identificar la version de Java."
    }

    $Version = [int]$Matches[1]

    if ($Version -lt $VersionMinima) {
        throw "Se necesita Java $VersionMinima o superior. Se encontro Java $Version."
    }

    return $Java.Source
}


# Modo de detencion.
if ($Detener) {
    Detener-Backend
    exit 0
}

# Modo de estado: solo muestra el cuadro, no inicia nada.
if ($Estado) {
    Mostrar-Estado
    exit 0
}

try {
    # Comprobar carpetas.
    if (-not (Test-Path $RutaEscritorio)) {
        throw "No se encontro la carpeta FERRETERIA-SISTEMA."
    }

    if (-not (Test-Path $RutaWeb)) {
        throw "No se encontro la carpeta backend-web."
    }

    if (-not (Test-Path $BatEscritorio)) {
        throw "No se encontro deploy\iniciar-escritorio.bat."
    }

    # Comprobar Java.
    $Java = Verificar-Java -VersionMinima 24
    Write-Host "Java disponible: version compatible." -ForegroundColor Green

    # Comprobar MySQL.
    if (-not (Test-Puerto -Puerto 3306)) {
        throw "MySQL no responde en localhost:3306. Inicia el servicio de MySQL."
    }

    Write-Host "MySQL esta disponible." -ForegroundColor Green

    # Evitar iniciar un segundo backend en el mismo puerto.
    if (Test-Puerto -Puerto 8080) {
        throw "El puerto 8080 ya esta ocupado. Ejecuta .\levantar-sistema.ps1 -Detener o revisa con -Estado."
    }

    # Compilar si se solicito.
    if ($Compilar) {
        Compilar-Proyecto -Carpeta $RutaEscritorio -Nombre "Aplicacion de escritorio"
        Compilar-Proyecto -Carpeta $RutaWeb -Nombre "Backend web"
    }

    # Verificar los JAR.
    if (-not (Test-Path $JarEscritorio)) {
        throw "No existe el JAR del escritorio. Ejecuta el script con -Compilar."
    }

    if (-not (Test-Path $JarWeb)) {
        throw "No existe el JAR web. Ejecuta el script con -Compilar."
    }

    # Solicitar contrasena sin mostrarla.
    $PasswordSeguro = Read-Host "Contrasena de MySQL" -AsSecureString

    if ($PasswordSeguro.Length -eq 0) {
        throw "La contrasena no puede estar vacia."
    }

    $Puntero = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($PasswordSeguro)

    try {
        $Password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($Puntero)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($Puntero)
    }

    # Guardar temporalmente los valores anteriores.
    $VariablesAnteriores = @{
        DB_URL      = $env:DB_URL
        DB_USERNAME = $env:DB_USERNAME
        DB_PASSWORD = $env:DB_PASSWORD
    }

    try {
        # Compartir configuracion con los dos proyectos.
        $env:DB_URL = $UrlBd
        $env:DB_USERNAME = $Usuario
        $env:DB_PASSWORD = $Password

        # Iniciar backend (ventana oculta; se detiene con -Detener).
        Write-Host "`nIniciando backend web..." -ForegroundColor Cyan

        $ProcesoWeb = Start-Process `
            -FilePath $Java `
            -ArgumentList @("-jar", "`"$JarWeb`"") `
            -WorkingDirectory $RutaWeb `
            -WindowStyle Minimized `
            -RedirectStandardOutput $LogOut `
            -RedirectStandardError $LogErr `
            -PassThru

        Set-Content -Path $PidFile -Value $ProcesoWeb.Id

        # Esperar a que el backend abra el puerto 8080.
        Write-Host "Esperando respuesta del backend..."

        $Listo = $false

        for ($i = 0; $i -lt 30; $i++) {
            Start-Sleep -Seconds 2

            if ($ProcesoWeb.HasExited) {
                Mostrar-UltimasLineas -Archivo $LogErr
                Mostrar-UltimasLineas -Archivo $LogOut
                throw "El backend se cerro al iniciar. Revisa las lineas anteriores."
            }

            if (Test-Puerto -Puerto 8080) {
                $Listo = $true
                break
            }
        }

        if (-not $Listo) {
            Mostrar-UltimasLineas -Archivo $LogOut
            throw "El backend no respondio en el puerto 8080."
        }

        Write-Host "Backend iniciado. Comprobando la base de datos..." -ForegroundColor Cyan

        # Prueba real: esta consulta obliga al backend a conectarse a MySQL.
        $Codigo = Obtener-CodigoHttp -Url $UrlPrueba

        if ($Codigo -ne 200) {
            Start-Sleep -Seconds 1

            $TextoLog = ""
            if (Test-Path $LogOut) {
                $TextoLog = Get-Content $LogOut -Tail 300 | Out-String
            }

            if ($TextoLog -match "Access denied") {
                Detener-Backend
                throw "Contrasena de MySQL incorrecta para el usuario '$Usuario'. No se abrio el escritorio."
            }

            Mostrar-UltimasLineas -Archivo $LogOut -Cantidad 20
            Detener-Backend
            throw "El backend no pudo usar la base de datos (HTTP $Codigo). No se abrio el escritorio."
        }

        Write-Host "Base de datos OK: la contrasena es correcta." -ForegroundColor Green

        # Iniciar aplicacion de escritorio.
        Write-Host "`nIniciando aplicacion de escritorio..." -ForegroundColor Cyan

        Start-Process `
            -FilePath $env:ComSpec `
            -ArgumentList "/c `"$BatEscritorio`"" `
            -WorkingDirectory $RutaEscritorio `
            -WindowStyle Minimized

        # Abrir navegador.
        Start-Process "http://localhost:8080"

        # Dar un momento al escritorio para aparecer en el cuadro.
        Start-Sleep -Seconds 3

        Write-Host "`nSistema iniciado." -ForegroundColor Green
        Mostrar-Estado
        Write-Host "Para ver el estado:   .\levantar-sistema.ps1 -Estado"
        Write-Host "Para detener el web:  .\levantar-sistema.ps1 -Detener"
    }
    finally {
        # Restaurar las variables anteriores en esta sesion.
        foreach ($NombreVariable in $VariablesAnteriores.Keys) {
            $ValorAnterior = $VariablesAnteriores[$NombreVariable]

            if ($null -eq $ValorAnterior) {
                Remove-Item "Env:$NombreVariable" -ErrorAction SilentlyContinue
            }
            else {
                Set-Item "Env:$NombreVariable" $ValorAnterior
            }
        }

        $Password = $null
        $PasswordSeguro.Dispose()
    }
}
catch {
    Write-Host "`nERROR: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}