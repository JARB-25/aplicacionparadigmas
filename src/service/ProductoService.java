package service;

import java.time.LocalDate;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Producto;

public class ProductoService {

    private final ObservableList<Producto> lista = FXCollections.observableArrayList();

    public ProductoService() {
        cargarDatosIniciales();
    }

    public void agregar(Producto producto) {
        lista.add(producto);
    }

    public void actualizar(Producto productoActualizado) {
        Producto existente = buscarPorId(productoActualizado.getId());
        if (existente == null) {
            throw new IllegalArgumentException("No se encontro el producto a actualizar.");
        }

        existente.setNombre(productoActualizado.getNombre());
        existente.setTipo(productoActualizado.getTipo());
        existente.setCantidad(productoActualizado.getCantidad());
        existente.setPrecio(productoActualizado.getPrecio());
        existente.setFecha(productoActualizado.getFecha());
    }

    public void eliminar(Producto producto) {
        lista.remove(producto);
    }

    public ObservableList<Producto> listar() {
        return lista;
    }

    public int generarSiguienteId() {
        return lista.stream()
            .map(Producto::getId)
            .max(Comparator.naturalOrder())
            .orElse(0) + 1;
    }

    private Producto buscarPorId(int id) {
        return lista.stream()
            .filter(producto -> producto.getId() == id)
            .findFirst()
            .orElse(null);
    }

    private void cargarDatosIniciales() {
        lista.addAll(
            new Producto(1, "Nitrato de amonio", "Fertilizante", 25.0, 35000, LocalDate.now().minusDays(3)),
            new Producto(2, "Abono organico", "Abono", 80.0, 18000, LocalDate.now().minusDays(1)),
            new Producto(3, "Control plagas X", "Insecticida", 12.5, 52000, LocalDate.now())
        );
    }
}
