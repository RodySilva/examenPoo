/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.appinvestigacion;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author lucho
 */
public class ProductoController implements Initializable {

    private static final Logger LOG = Logger.getLogger(ProductoController.class.getName());

    @FXML
    private TextField txfCodigo;
    @FXML
    private TextField txfDescripcion;
    private ConexionBD conex;
    @FXML
    private TextField txfCantidad;
    @FXML
    private ComboBox<String> cmbIva;
    @FXML
    private TextField txfPrecio;

    @FXML
    private ComboBox<Marca> cmbMarca;
    @FXML
    private TableColumn<Producto, Integer> colCodigo;
    @FXML
    private TableColumn<Producto, String> colDescripcion;
    @FXML
    private TableColumn<Producto, Double> colCantidad;
    @FXML
    private TableColumn<Producto, Integer> colIva;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colMarca;
    @FXML
    private TableView<Producto> tlbDatos2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        this.colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        this.colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        this.colIva.setCellValueFactory(new PropertyValueFactory<>("iva"));
        this.colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        this.colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));

        this.cmbMarca.setCellFactory((ListView<Marca> l) -> {

            return new ListCell<Marca>() {
                @Override
                protected void updateItem(Marca m, boolean empty) {
                    if (!empty) {
                        this.setText("(" + m.getCodigo() + ") " + m.getDescripcion());
                    } else {
                        this.setText("");
                    }
                    super.updateItem(m, empty);
                }
            };
        });

        this.cmbMarca.setButtonCell(new ListCell<Marca>() {
            @Override
            protected void updateItem(Marca m, boolean empty) {
                if (!empty) {
                    this.setText("(" + m.getCodigo() + ") " + m.getDescripcion());
                } else {
                    this.setText("");
                }
                super.updateItem(m, empty);
            }
        }
        );

        this.cmbIva.getItems().add("10%");
        this.cmbIva.getItems().add("5%");
        this.cmbIva.getItems().add("Excento");
        this.cmbIva.getSelectionModel().selectFirst();

        this.conex = new ConexionBD();

        this.cargarMarcas();
        this.cargarDatos();
    }

    private void cargarMarcas() {
        try {
            String sql = "SELECT * FROM marca";
            Statement stm = this.conex.getConexion().createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                this.cmbMarca.getItems().add(new Marca(resultado.getInt("idmarca"), resultado.getString("descripcion")));
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al cargar Marcas", ex);
        }
    }

    @FXML
    private void onActionRegistrar(ActionEvent event) {
        String codigo = this.txfCodigo.getText();
        String descripcion = this.txfDescripcion.getText();
        String cantidad = this.txfCantidad.getText();
        String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        Integer iv;
        switch (iva) {
            case "10%":
                iv = 10;
                break;
            case "5%":
                iv = 5;
                break;
            default:
                iv = 0;
                break;
        }
        String precio = this.txfPrecio.getText();
        Integer marca = this.cmbMarca.getSelectionModel().getSelectedItem().getCodigo();

        if (codigo.isEmpty() || descripcion.isEmpty() || cantidad.isEmpty() || iva.isEmpty() || precio.isEmpty()) {
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Error");
            al.setHeaderText("Complete todos lod campos");
            al.show();
        } else {

            try {

                String sql = "INSERT INTO producto(descripcion, cantidad, iva, precio, idmarca) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stm = conex.getConexion().prepareStatement(sql);
                stm.setString(1, descripcion);
                stm.setString(2, cantidad);
                stm.setInt(3, iv);
                stm.setString(4, precio);
                stm.setInt(5, marca);

                stm.execute();
                Alert al = new Alert(Alert.AlertType.INFORMATION);
                al.setTitle("Exito");
                al.setHeaderText("producto registrado correctamente");
                al.show();
                this.txfCodigo.clear();
                this.txfDescripcion.clear();
                this.txfCantidad.clear();
                this.txfPrecio.clear();
                this.cargarDatos();
                this.cargarMarcas();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error al insertar", ex);
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Error de conexion");
                al.setHeaderText("No se puede insertar registro en la base de datos");
                al.setContentText(ex.toString());
                al.showAndWait();
            }

        }
    }

    private void cargarDatos() {
        this.tlbDatos2.getItems().clear();
        try {
            String sql = "SELECT * FROM producto";
            Statement stm = this.conex.getConexion().createStatement();
            ResultSet resultado = stm.executeQuery(sql);
            while (resultado.next()) {
                Integer cod = resultado.getInt("idproducto");
                String desc = resultado.getString("descripcion");
                Double cant = resultado.getDouble("cantidad");
                Integer iva = resultado.getInt("iva");
                Double prec = resultado.getDouble("precio");
                Integer marc = resultado.getInt("idmarca");
                Producto p = new Producto(cod, desc, cant, iva, prec, marc);
                this.tlbDatos2.getItems().add(p);
            }

        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al cargar datos en la BD", ex);
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        String id = this.txfCodigo.getText();
        String descripcion = this.txfDescripcion.getText();
        String cantidad = this.txfCantidad.getText();
        String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        Integer iv;
        switch (iva) {
            case "10%":
                iv = 10;
                break;
            case "5%":
                iv = 5;
                break;
            default:
                iv = 0;
                break;
        }
        String precio = this.txfPrecio.getText();
        Integer marca = this.cmbMarca.getSelectionModel().getSelectedItem().getCodigo();

        String sql = "UPDATE producto SET descripcion = ?, cantidad=?, iva=?, precio=?, idmarca=? WHERE idproducto = ?";
        try {
            PreparedStatement stm = this.conex.getConexion().prepareStatement(sql);
            stm.setString(1, descripcion);
            stm.setString(2, cantidad);
            stm.setInt(3, iv);
            stm.setString(4, precio);
            stm.setInt(5, marca);
            stm.setInt(6, Integer.parseInt(id));
            stm.execute();
            Alert al = new Alert(Alert.AlertType.INFORMATION);
            al.setTitle("Exito");
            al.setHeaderText("Producto editado correctamente");
            al.show();
            this.txfCodigo.clear();
            this.txfDescripcion.clear();
            this.txfCantidad.clear();
            this.txfPrecio.clear();
            this.cargarDatos();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al editar", ex);
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setTitle("Error de conexion");
            al.setHeaderText("No se puede editar el registro de la base de datos");
            al.setContentText(ex.toString());
            al.showAndWait();
        }
        this.cargarDatos();
        this.cargarMarcas();
    }

    @FXML
    private void onActionEliminar(ActionEvent event) {

        String codigo = this.txfCodigo.getText();
        String descripcion = this.txfDescripcion.getText();
        String cantidad = this.txfCantidad.getText();
        String iva = this.cmbIva.getSelectionModel().getSelectedItem();
        String precio = this.txfPrecio.getText();

        if (codigo.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error al Eliminar");
            a.setHeaderText("Ingrese un codigo");
            a.show();
        } else {
            Alert alConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            alConfirm.setTitle("Confirmar");
            alConfirm.setHeaderText("Desea eliminar el producto?");
            alConfirm.setContentText(codigo + " - " + descripcion + " - " + cantidad + " - " + iva + " - " + precio);
            Optional<ButtonType> accion = alConfirm.showAndWait();

            if (accion.get().equals(ButtonType.OK)) {
                try {
                    String sql = "DELETE FROM producto WHERE idproducto = ?";
                    PreparedStatement stm = conex.getConexion().prepareStatement(sql);
                    Integer cod = Integer.parseInt(codigo);
                    stm.setInt(1, cod);
                    stm.execute();
                    int cant = stm.getUpdateCount();
                    if (cant == 0) {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setTitle("Error al Eliminar");
                        a.setHeaderText("No existe el producto con codigo" + codigo);
                        a.show();
                    } else {
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setTitle("Eliminado");
                        a.setHeaderText("Producto eliminado correctamente");
                        a.show();
                        this.txfDescripcion.clear();
                        this.txfCodigo.clear();
                        this.txfCantidad.clear();
                        this.txfPrecio.clear();

                        this.cargarDatos();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, "Error al eliminar", ex);
                }
            }
        }
    }

}
