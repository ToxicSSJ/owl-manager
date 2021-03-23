package com.owl.controller;

import com.owl.Main;
import com.owl.Util;
import com.owl.controller.data.FolderData;
import com.owl.controller.data.ProcessData;
import com.owl.module.UserModule;
import com.owl.sockets.packet.*;
import com.owl.type.ApplicationType;
import com.owl.type.ModuleType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.LinkedList;
import java.util.List;

public class UserController implements Controller {

    @FXML public Text Mensaje;
    @FXML public Text Mensaje2;
    @FXML public MenuButton aplicacion;

    @FXML private TextField nombreCarpeta;
    @FXML private TextArea textArea;

    @FXML private TableView processTable;
    @FXML private TableView folderTable;

    @FXML private TableColumn processName;
    @FXML private TableColumn processPid;
    @FXML private TableColumn processClose;

    @FXML private TableColumn folderName;
    @FXML private TableColumn folderDate;
    @FXML private TableColumn folderDelete;

    private LinkedList<String> instructions;

    public void addFolder(FolderData data) {
        Platform.runLater(() -> folderTable.getItems().add(data));
    }

    public void updateFolders(List<FolderData> data) {

        Platform.runLater(() -> {

            folderTable.getItems().clear();
            folderTable.getItems().addAll(data);

        });

    }

    public void updateProcess(List<ProcessData> data) {

        Platform.runLater(() -> {

            processTable.getItems().clear();
            processTable.getItems().addAll(data);

        });

    }

    private UserModule getModule() {
        return Main.getModule(ModuleType.USER_GUI_MODULE);
    }

    private void send(Packet packet) {
        getModule().getClient().send(packet);
    }

    @FXML
    public void initialize() {

        instructions = new LinkedList<>();

        textArea.setEditable(false);

        folderName.setCellValueFactory(new PropertyValueFactory<>("name"));
        folderDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        processName.setCellValueFactory(new PropertyValueFactory<>("name"));
        processPid.setCellValueFactory(new PropertyValueFactory<>("pid"));

        Callback<TableColumn<FolderData, String>, TableCell<FolderData, String>> cellFactory = new Callback<>() {

            @Override
            public TableCell<FolderData, String> call(final TableColumn<FolderData, String> param) {

                final TableCell<FolderData, String> cell = new TableCell<>() {

                    private final Button btn = new Button("Borrar");

                    {
                        btn.setOnAction((ActionEvent event) -> {

                            FolderData data = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(data);

                            send(DeleteFolderPacket.builder().name(data.getName()).build());

                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };

                return cell;

            }

        };

        Callback<TableColumn<ProcessData, String>, TableCell<ProcessData, String>> processFactory = new Callback<>() {

            @Override
            public TableCell<ProcessData, String> call(final TableColumn<ProcessData, String> param) {

                final TableCell<ProcessData, String> cell = new TableCell<>() {

                    private final Button btn = new Button("Cerrar");

                    {
                        btn.setOnAction((ActionEvent event) -> {

                            ProcessData data = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(data);

                            send(KillApplicationPacket.builder().pid(data.getPid()).build());

                        });
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };

                return cell;

            }

        };

        folderDelete.setCellFactory(cellFactory);
        processClose.setCellFactory(processFactory);

    }

    @FXML
    public void Abrir(ActionEvent actionEvent) {

        if (aplicacion.getText().equals("Seleccione una opcion")) {
            this.Mensaje.setText("Seleccione una opcion");
            return;
        }

        this.Mensaje.setText("");

        for(ApplicationType type : ApplicationType.values())
            if(type.getMenu().equals(this.aplicacion.getText())) {
                addInstruction("Enviando petición para abrir (" + type.name() + ")...");
                send(OpenApplicationPacket.builder().type(type).build());
                return;
            }

    }

    public void addInstruction(String instruction) {
        instructions.add(Util.logPrefix() + instruction);
        update();
    }

    public void update() {
        Platform.runLater(() -> {
            try {
                textArea.setText(String.join("\n", instructions));
            } catch(Exception e) { }
        });
    }

    public void crearCarpeta(ActionEvent actionEvent) {

        if (nombreCarpeta.getText().equals("")) {

            this.Mensaje2.setText("Ingrese un nombre para la carpeta");

        } else {

            this.Mensaje2.setText("");
            send(CreateFolderPacket.builder().name(nombreCarpeta.getText()).build());

        }

    }

    public void setCalculadora(ActionEvent actionEvent) {
        this.aplicacion.setText("Calculadora");
    }

    public void setBloc(ActionEvent actionEvent) {
        this.aplicacion.setText("Bloc de notas");
    }

    public void setPaint(ActionEvent actionEvent) {
        this.aplicacion.setText("Paint");
    }

    public void setExplorador(ActionEvent actionEvent) {
        this.aplicacion.setText("Explorador");
    }
}
