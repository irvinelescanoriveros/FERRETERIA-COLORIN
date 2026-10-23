package pe.ferreteria.modelo;

public class Producto {

    private int idProducto;
    private String codigo;
    private String nombre;
    private int idCategoria;
    private int idMarca;
    private String unidad;
    private String estante;
    private String nivel;
    private String descripcion;
    private boolean activo;

    public Producto() {
    }

    public Producto(int idProducto, String codigo, String nombre,
                    int idCategoria, int idMarca, String unidad,
                    String estante, String nivel, String descripcion,
                    boolean activo) {

        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.idCategoria = idCategoria;
        this.idMarca = idMarca;
        this.unidad = unidad;
        this.estante = estante;
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getEstante() {
        return estante;
    }

    public void setEstante(String estante) {
        this.estante = estante;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}