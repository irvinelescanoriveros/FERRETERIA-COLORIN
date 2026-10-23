package pe.ferreteria.modelo;

public class Cliente {

    private int idCliente;
    private String tipoCliente;
    private String dni;
    private String ruc;
    private String nombre;
    private String apellidos;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String direccion;
    private boolean activo;

    public Cliente() {
    }

    public Cliente(int idCliente, String tipoCliente, String dni, String ruc,
                   String nombre, String apellidos, String razonSocial,
                   String contacto, String telefono, String direccion,
                   boolean activo) {

        this.idCliente = idCliente;
        this.tipoCliente = tipoCliente;
        this.dni = dni;
        this.ruc = ruc;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        if ("Natural".equals(tipoCliente)) {
            return nombre + " " + apellidos;
        }

        return razonSocial;
    }
}