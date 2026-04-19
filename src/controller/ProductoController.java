package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Producto;
import service.ProductoService;

public class ProductoController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private DatePicker dpFecha;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, Double> colCantidad;
    @FXML private TableColumn<Producto, String> colPrecio;
    @FXML private TableColumn<Producto, LocalDate> colFecha;
    @FXML private Label lblTotal;
    @FXML private Label lblSeleccionado;

    private final ProductoService productoService = new ProductoService();
    private FilteredList<Producto> productosFiltrados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComboBox();
        configurarTabla();
        configurarBusqueda();
        configurarSeleccionTabla();
        dpFecha.setValue(LocalDate.now());
        actualizarResumen(null);
    }

    private void configurarComboBox() {
        cmbTipo.getItems().setAll("Fertilizante", "Insecticida", "Abono", "Fungicida", "Herbicida");
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(cellData ->
            new SimpleStringProperty(String.format("$%,.0f", cellData.getValue().getPrecio())));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        productosFiltrados = new FilteredList<>(productoService.listar());
        SortedList<Producto> productosOrdenados = new SortedList<>(productosFiltrados);
        productosOrdenados.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(productosOrdenados);

        lblTotal.textProperty().bind(Bindings.size(productosFiltrados).asString("Total de productos: %d"));
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((_, _, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.trim().toLowerCase();
            productosFiltrados.setPredicate(producto -> {
                if (filtro.isEmpty()) {
                    return true;
                }
                return producto.getNombre().toLowerCase().contains(filtro)
                    || producto.getTipo().toLowerCase().contains(filtro)
                    || String.valueOf(producto.getId()).contains(filtro);
            });
        });
    }

    private void configurarSeleccionTabla() {
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((_, _, actual) -> {
            if (actual == null) {
                actualizarResumen(null);
                return;
            }
            cargarProductoEnFormulario(actual);
            actualizarResumen(actual);
        });
    }

    @FXML
    private void guardar() {
        try {
            Producto producto = construirProductoDesdeFormulario(false);
            productoService.agregar(producto);
            tablaProductos.getSelectionModel().select(producto);
            mostrarInfo("Producto guardado", "El producto se registro correctamente.");
            limpiar();
        } catch (IllegalArgumentException ex) {
            mostrarError("Datos invalidos", ex.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Sin seleccion", "Selecciona un producto de la tabla para actualizarlo.");
            return;
        }

        try {
            Producto actualizado = construirProductoDesdeFormulario(true);
            productoService.actualizar(actualizado);
            tablaProductos.refresh();
            tablaProductos.getSelectionModel().select(actualizado);
            actualizarResumen(actualizado);
            mostrarInfo("Producto actualizado", "Los datos del producto se actualizaron correctamente.");
        } catch (IllegalArgumentException ex) {
            mostrarError("Datos invalidos", ex.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Sin seleccion", "Selecciona un producto de la tabla para eliminarlo.");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminacion");
        alerta.setHeaderText("Eliminar producto");
        alerta.setContentText("Se eliminara el producto \"" + seleccionado.getNombre() + "\".");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            productoService.eliminar(seleccionado);
            limpiar();
            mostrarInfo("Producto eliminado", "El producto fue eliminado del inventario.");
        }
    }

    @FXML
    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        txtCantidad.clear();
        txtPrecio.clear();
        txtNombre.requestFocus();
        cmbTipo.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        tablaProductos.getSelectionModel().clearSelection();
        actualizarResumen(null);
    }

    @FXML
    private void verTodos() {
        txtBuscar.clear();
        tablaProductos.sort();
    }

    private Producto construirProductoDesdeFormulario(boolean usarIdExistente) {
        String nombre = textoObligatorio(txtNombre, "El nombre es obligatorio.");
        String tipo = valorObligatorio(cmbTipo.getValue(), "Debes seleccionar un tipo de producto.");
        double cantidad = parsearPositivo(txtCantidad.getText(), "La cantidad debe ser un numero mayor que cero.");
        double precio = parsearPositivo(txtPrecio.getText(), "El precio debe ser un numero mayor que cero.");
        LocalDate fecha = valorObligatorio(dpFecha.getValue(), "Debes seleccionar una fecha.");

        int id = usarIdExistente
            ? parsearEntero(txtId.getText(), "El ID seleccionado no es valido.")
            : productoService.generarSiguienteId();

        return new Producto(id, nombre, tipo, cantidad, precio, fecha);
    }

    private void cargarProductoEnFormulario(Producto producto) {
        txtId.setText(String.valueOf(producto.getId()));
        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        cmbTipo.setValue(producto.getTipo());
        dpFecha.setValue(producto.getFecha());
    }

    private void actualizarResumen(Producto producto) {
        if (producto == null) {
            lblSeleccionado.setText("Ningun registro seleccionado");
            return;
        }
        lblSeleccionado.setText("Editando: " + producto.getNombre() + " (ID " + producto.getId() + ")");
    }

    private String textoObligatorio(TextField campo, String mensaje) {
        String valor = campo.getText() == null ? "" : campo.getText().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    private <T> T valorObligatorio(T valor, String mensaje) {
        if (valor == null) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    private double parsearPositivo(String texto, String mensaje) {
        try {
            double valor = Double.parseDouble(texto.trim());
            if (valor <= 0) {
                throw new IllegalArgumentException(mensaje);
            }
            return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private int parsearEntero(String texto, String mensaje) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
