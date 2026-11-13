
package ni.edu.uam.equiposdelaboratorio.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import ni.edu.uam.equiposdelaboratorio.dao.EquipoDAO;
import ni.edu.uam.equiposdelaboratorio.dao.Tipo_EquipoDAO;
import ni.edu.uam.equiposdelaboratorio.model.Equipo;
import ni.edu.uam.equiposdelaboratorio.model.Tipo_Equipo;

import java.util.List;
import java.util.Optional;

public class LaboratorioController {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtCantidad;

    @FXML
    private ComboBox<Tipo_Equipo> cmbTipoEquipo;
    @FXML
    private ComboBox<String> cmbEstado;

    @FXML
    private TextField txtNombreBuscar;
    @FXML
    private ComboBox<Tipo_Equipo> cmbTipoEquipoBuscar;

    @FXML
    private TableView<Equipo> tblEquipos;

    @FXML
    private TableColumn<Equipo, String> colCodigo;
    @FXML
    private TableColumn<Equipo, String> colNombre;
    @FXML
    private TableColumn<Equipo, String> colMarca;
    @FXML
    private TableColumn<Equipo, String> colEstado;
    @FXML
    private TableColumn<Equipo, Integer> colCantidad;
    @FXML
    private TableColumn<Equipo, String> colTipo;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnMostrarTodo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final Tipo_EquipoDAO tipoEquipoDAO = new Tipo_EquipoDAO();

    private Long idSeleccionado = null;

    public void initialize() {

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(data -> {
            Tipo_Equipo tipo = data.getValue().getTipoEquipo();
            String nombre = tipo != null ? tipo.getNombre() : "Sin tipo";
            return new SimpleStringProperty(nombre);
        });

        tblEquipos.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                    if (newValue != null) {
                        seleccionarEquipo(newValue);
                        actualizarBotones();
                    } else {
                        idSeleccionado = null;
                        actualizarBotones();
                    }
                });

        cmbEstado.setItems(FXCollections.observableArrayList("Disponible", "En mantenimiento", "Fuera de servicio"));
        actualizarBotones();
        cargarTipos();
        cargarEquipos();
    }

    private void seleccionarEquipo(Equipo equipo) {
        idSeleccionado = equipo.getId();
        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca());
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));
        cmbEstado.setValue(equipo.getEstado());
        for (Tipo_Equipo tipo : cmbTipoEquipo.getItems()) {
            if (tipo.getId().equals(equipo.getTipoEquipo().getId())) {
                cmbTipoEquipo.setValue(tipo);
                break;
            }
        }
        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;
        btnActualizar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Equipo obtenerEquipoFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();
        String estado = cmbEstado.getValue();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            throw new IllegalArgumentException("El código y el nombre son obligatorios");
        }
        if (estado == null) {
            throw new IllegalArgumentException("Debe seleccionar un estado");
        }
        if (!estado.equals("Disponible") && !estado.equals("En mantenimiento") && !estado.equals("Fuera de servicio")) {
            throw new IllegalArgumentException("El estado seleccionado no es válido");
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero");
        }

        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        Tipo_Equipo tipo = cmbTipoEquipo.getValue();

        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de equipo");
        }
        return new Equipo(codigo, nombre, marca, estado, cantidad, tipo);
    }

    private void cargarTipos() {
        try {
            List<Tipo_Equipo> tipos = tipoEquipoDAO.listar();
            cmbTipoEquipo.setItems(FXCollections.observableArrayList(tipos));
            cmbTipoEquipoBuscar.setItems(FXCollections.observableArrayList(tipos));
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Error de carga",
                    "Error",
                    "No se pudieron cargar los tipos de equipo"
            );
        }
    }

    @FXML
    private void guardarEquipo() {
        try {
            Equipo equipo = obtenerEquipoFormulario();
            equipoDAO.guardar(equipo);
            limpiarEquipo();
            cargarEquipos();
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "Equipo guardado",
                    "El equipo fue registrado correctamente"
            );

        } catch (IllegalArgumentException ex) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Validación",
                    "Datos incorrectos",
                    ex.getMessage()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Error al guardar",
                    "Error",
                    "No se pudo guardar el equipo. Verifique que el código no esté duplicado y que la base de datos esté disponible"
            );
        }
    }

    @FXML
    private void actualizarEquipo() {
        if (idSeleccionado == null) {
            return;
        }
        try {
            Equipo equipo = obtenerEquipoFormulario();
            equipo.setId(idSeleccionado);
            boolean actualizado = equipoDAO.modificar(equipo);
            if (!actualizado) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Equipo no encontrado",
                        "El equipo seleccionado no existe"
                );
                return;
            }
            limpiarEquipo();
            cargarEquipos();
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Actualización exitosa",
                    "Equipo actualizado",
                    "El equipo fue actualizado correctamente"
            );
        } catch (IllegalArgumentException ex) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Validación",
                    "Datos incorrectos",
                    ex.getMessage()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Error al actualizar",
                    "No se pudo actualizar el equipo. Verifique que los datos sean válidos y que el código no esté duplicado"
            );
        }
    }

    @FXML
    private void eliminarEquipo() {

        if (idSeleccionado == null) {
            return;
        }

        try {
            Alert confirmacion = new Alert(
                    Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Confirmación");
            confirmacion.setHeaderText("Eliminar equipo");
            confirmacion.setContentText(
                    "¿Está seguro de que desea eliminar el equipo seleccionado?");

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isEmpty() ||
                    resultado.get() != ButtonType.OK) {
                return;
            }

            boolean eliminado = equipoDAO.eliminar(idSeleccionado);

            if (!eliminado) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Equipo no encontrado",
                        "El equipo seleccionado no existe"
                );
                return;
            }

            limpiarEquipo();
            cargarEquipos();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Eliminación exitosa",
                    "Equipo eliminado",
                    "El equipo fue eliminado correctamente"
            );

        } catch (Exception ex) {
            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Error al eliminar",
                    "No se pudo eliminar el equipo"
            );
        }
    }

    // Cargar equipos en TableView
    private void cargarEquipos() {

        try {
            List<Equipo> equipos = equipoDAO.listar();

            tblEquipos.setItems(
                    FXCollections.observableArrayList(equipos));

        } catch (Exception ex) {
            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error de carga",
                    "Error",
                    "No se pudieron cargar los equipos"
            );
        }
    }

    // Buscar equipos por nombre y tipo
    @FXML
    private void buscarEquipo() {

        try {
            String nombre = txtNombreBuscar.getText();

            Tipo_Equipo tipo = cmbTipoEquipoBuscar.getValue();

            Long idTipo = null;

            if (tipo != null) {
                idTipo = tipo.getId();
            }

            List<Equipo> equipos =
                    equipoDAO.buscar(nombre, idTipo);

            tblEquipos.setItems(
                    FXCollections.observableArrayList(equipos));

        } catch (Exception ex) {
            ex.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Error en la búsqueda",
                    "No se pudo realizar la búsqueda"
            );
        }
    }

    // Mostrar todos los equipos
    @FXML
    private void mostrarTodo() {

        txtNombreBuscar.clear();
        cmbTipoEquipoBuscar.setValue(null);

        cargarEquipos();
    }

    // Limpiar formulario
    @FXML
    private void limpiarEquipo() {

        tblEquipos.getSelectionModel().clearSelection();

        idSeleccionado = null;

        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();

        cmbTipoEquipo.setValue(null);
        cmbEstado.setValue(null);

        actualizarBotones();
    }

    // Mostrar alertas
    private void showAlert(Alert.AlertType type,
                           String title,
                           String header,
                           String message) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
