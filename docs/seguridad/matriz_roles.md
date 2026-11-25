# Matriz de roles – SGIVFC

El sistema define cuatro roles en la tabla `Usuario` (`Administrador`, `Vendedor`, `Almacen`, `WEB`). El acceso por rol se controla en `MainForm` (menú y apertura de paneles).

| Módulo | Administrador | Vendedor | Almacén | WEB |
|--------|:---:|:---:|:---:|:---:|
| Ventas | Sí | Sí | No | – |
| Clientes | Sí | Sí | No | – |
| Caja | Sí | Sí | No | – |
| Inventario | Sí | Sí | Sí | – |
| Kardex | Sí | Sí | Sí | – |
| Productos | Sí | No | Sí | – |
| Proveedores | Sí | No | Sí | – |
| Compras | Sí | No | Sí | – |
| Recepción | Sí | No | Sí | – |
| Usuarios | Sí | No | No | – |
| Dashboard | Sí | No | No | – |
| Inicio de sesión en escritorio | Sí | Sí | Sí | No (rechazado por `LoginController`) |

El rol `WEB` corresponde a los clientes de la tienda y solo opera a través del backend web.

**Limitación conocida (SEG-08):** el control se aplica en la interfaz. La validación del rol en la capa de servicio queda como mejora futura.