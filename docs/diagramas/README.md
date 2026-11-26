# Diagramas de arquitectura – SGIVFC

Los diagramas se escriben en Mermaid (`src/*.mmd`), GitHub los muestra directamente y los PNG se generan con `mmdc -i src/x.mmd -o x.png -s 2`.

## 1. Arquitectura en capas (MVC)
```mermaid
flowchart TB
  subgraph V["Vista (Swing) - pe.ferreteria.vista"]
    LF[LoginForm]; MF[MainForm]; VP[VentaPanel]; PP[ProductoPanel]; IP[InventarioPanel]; OP[Otros paneles]
  end
  subgraph C["Controlador - pe.ferreteria.controlador"]
    LC[LoginController]; VC[VentaController]; PC[ProductoController]; IC[InventarioController]
  end
  subgraph S["Servicio (reglas de negocio) - pe.ferreteria.servicio"]
    US[UsuarioService]; VS[VentaService]; PS[ProductoService]; IS[InventarioService]
  end
  subgraph D["DAO (JDBC) - pe.ferreteria.dao"]
    UD[UsuarioDAO]; VD[VentaDAO]; DVD[DetalleVentaDAO]; PD[ProductoDAO]; ID[InventarioDAO]
  end
  subgraph M["Modelo - pe.ferreteria.modelo"]
    MO[Usuario, Venta, Producto, Inventario ...]
  end
  subgraph I["Infraestructura - pe.ferreteria.infraestructura"]
    CN[ConexionBD]; CF[Configuracion]; SG[Seguridad]; RG[Registro]
  end
  DB[(MySQL bd_colorin)]
  LF --> LC --> US --> UD
  VP --> VC --> VS --> VD & DVD & ID
  PP --> PC --> PS --> PD
  IP --> IC --> IS --> ID
  US --> SG
  UD & VD & DVD & PD & ID --> CN --> CF
  CN --> DB
  S -.-> M
  D -.-> M
  S -.-> RG
```
**Vista** (Swing) solo presenta y captura datos; **Controlador** traduce acciones a llamadas de servicio; **Servicio** contiene las reglas de negocio y las transacciones; **DAO** ejecuta las consultas JDBC; **Modelo** son las entidades. `ConexionBD` y `Configuracion` aíslan la conexión y sus credenciales.

## 2. Patrón DAO e inyección de dependencias
```mermaid
classDiagram
  direction LR
  class ConexionBD {
    +conectar() Connection
  }
  class Configuracion {
    +obtener(clave, variableEntorno, porDefecto) String
  }
  class UsuarioDAO {
    +insertar(Usuario) boolean
    +listar() List~Usuario~
    +buscarActivoPorUsername(String) Usuario
    +cambiarPassword(int, String) boolean
  }
  class ProductoDAO {
    +insertar(Producto) boolean
    +listar() List~Producto~
    +existeCodigo(String, int) boolean
  }
  class InventarioDAO {
    +buscarPorId(int) Inventario
    +reducirStock(...) boolean
  }
  class VentaDAO {
    +insertar(Venta) int
    +obtenerSiguienteNumero(...) int
  }
  class UsuarioService {
    -UsuarioDAO usuarioDAO
    +UsuarioService(UsuarioDAO)
    +autenticar(user, pass) Usuario
    +registrar(Usuario) boolean
  }
  class VentaService {
    -VentaDAO ventaDAO
    -InventarioDAO inventarioDAO
    +registrarVentaCompleta(...) boolean
  }
  class Usuario
  class Producto
  class Venta
  UsuarioService --> UsuarioDAO : inyectado por constructor
  VentaService --> VentaDAO
  VentaService --> InventarioDAO
  UsuarioDAO ..> ConexionBD
  ProductoDAO ..> ConexionBD
  InventarioDAO ..> ConexionBD
  VentaDAO ..> ConexionBD
  ConexionBD ..> Configuracion
  UsuarioDAO ..> Usuario
  ProductoDAO ..> Producto
  VentaDAO ..> Venta
```

## 3. Secuencia: inicio de sesión (con hash PBKDF2)
```mermaid
sequenceDiagram
  actor U as Usuario
  participant LF as LoginForm
  participant LC as LoginController
  participant US as UsuarioService
  participant UD as UsuarioDAO
  participant SG as Seguridad
  participant DB as MySQL
  U->>LF: usuario y contraseña
  LF->>LC: iniciarSesion(user, pass)
  LC->>US: autenticar(user, pass)
  US->>UD: buscarActivoPorUsername(user)
  UD->>DB: SELECT ... WHERE username=? AND activo=true
  DB-->>UD: fila (hash o texto plano antiguo)
  UD-->>US: Usuario
  US->>SG: verificar(pass, almacenada)
  SG-->>US: true / false
  opt contraseña antigua
    US->>SG: hashear(pass)
    US->>UD: cambiarPassword(id, hash PBKDF2)
  end
  US-->>LC: Usuario sin contraseña (o null)
  LC-->>LF: true / false (rechaza rol WEB)
  LF-->>U: abre MainForm o muestra error
```

## 4. Secuencia: registrar una venta (transacción)
```mermaid
sequenceDiagram
  actor V as Vendedor
  participant VP as VentaPanel
  participant VC as VentaController
  participant VS as VentaService
  participant DB as MySQL (una transaccion)
  V->>VP: confirma la venta
  VP->>VC: registrarVentaCompleta(...)
  VC->>VS: registrarVentaCompleta(...)
  VS->>DB: setAutoCommit(false)
  VS->>DB: caja abierta (CajaDAO)
  VS->>DB: numero de comprobante (VentaDAO)
  VS->>DB: INSERT Venta
  loop cada producto
    VS->>DB: leer lote y validar stock (InventarioDAO)
    VS->>DB: INSERT Detalle_Venta
    VS->>DB: reducir stock del lote
    VS->>DB: INSERT Kardex (salida)
  end
  VS->>DB: INSERT Movimiento_Caja y actualizar totales de Caja
  alt todo correcto
    VS->>DB: commit
    VS-->>VP: true (se genera el PDF del comprobante)
  else error
    VS->>DB: rollback
    VS-->>VP: false
  end
```

## 5. Despliegue
```mermaid
flowchart LR
  subgraph PC["PC de la tienda"]
    ESC["Escritorio Swing<br/>ferreteria-colorin.jar (Java 24+)"]
  end
  subgraph SRV["Servidor / Docker Compose"]
    WEB["Backend web Spring Boot<br/>:8080 (Java 21)<br/>/actuator/health"]
    DB[("MySQL 8.4 :3306<br/>bd_colorin")]
    CRON["cron: respaldo.sh, revisar-salud.sh"]
    BK[("/var/backups/colorin")]
  end
  CLI["Navegador del cliente"]
  GH["GitHub Actions: pruebas, CodeQL, JAR"]
  ESC -->|JDBC colorin_app| DB
  WEB -->|JDBC colorin_app| DB
  CLI -->|HTTP| WEB
  CRON --> DB
  CRON --> BK
  GH -.artefacto JAR.-> ESC
```

## 6. Principios SOLID en el proyecto
| Principio | Dónde se aplica | Evaluación |
|-----------|-----------------|-----------|
| **S** – Responsabilidad única | Cada capa tiene una función: `*DAO` (datos), `*Service` (reglas), `*Controller` (puente), `*Panel` (UI); `Seguridad` solo maneja contraseñas; `Configuracion` solo lee configuración; `Registro` solo logs. | Cumple en general. **Mejora:** `VentaService.registrarVentaCompleta` concentra validación, transacción, stock, Kardex y caja en un método de ~700 líneas; conviene dividirlo en pasos privados o servicios. |
| **O** – Abierto/cerrado | Nuevos reportes se agregan como clases `*Reporte` y generadores PDF sin modificar los existentes. | Parcial: agregar un método de pago o un tipo de comprobante exige modificar `VentaService`. **Mejora:** estrategia por tipo de comprobante. |
| **L** – Sustitución de Liskov | Las pruebas sustituyen los DAO por dobles de Mockito (subclases) sin cambiar el comportamiento esperado del servicio. | Cumple en el uso actual; no hay jerarquías de herencia propias. |
| **I** – Segregación de interfaces | Servicios y controladores exponen solo las operaciones de su módulo. | Parcial: los DAO son clases concretas, no interfaces. **Mejora:** definir interfaces (`UsuarioRepositorio`, …). |
| **D** – Inversión de dependencias | Los servicios reciben sus DAO por constructor (`UsuarioService(UsuarioDAO)`, `VentaService(...)`), con constructor sin argumentos que usa los DAO reales. Permite pruebas unitarias. | Cumple parcialmente: depende de clases concretas, no de interfaces; los controladores crean su servicio con `new` en el constructor por defecto. |

Este análisis reconoce explícitamente lo que no se cumple para planificar la refactorización y para el informe.
