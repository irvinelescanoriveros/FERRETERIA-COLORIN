package pe.ferreteria.controlador;

import pe.ferreteria.modelo.Marca;
import pe.ferreteria.servicio.MarcaService;

import java.util.List;

public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController() {
        marcaService = new MarcaService();
    }

    public boolean registrar(Marca marca) {
        return marcaService.registrar(marca);
    }

    public List<Marca> listar() {
        return marcaService.listar();
    }

    public boolean eliminar(int idMarca) {
        return marcaService.eliminar(idMarca);
    }

    public boolean existeNombre(String nombre, int idMarca) {
        return marcaService.existeNombre(nombre, idMarca);
    }
}