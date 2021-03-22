package com.owl.controller;

import com.owl.Main;
import com.owl.Util;
import com.owl.controller.data.FolderData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.LinkedList;

public class FilesController implements Controller {

    @FXML private TextArea textArea;

    @FXML private TableView table;

    @FXML private TableColumn nameColumn;
    @FXML private TableColumn dateColumn;
    @FXML private TableColumn pathColumn;

    private LinkedList<String> instructions;
    private LinkedList<FolderData> data;

    @FXML
    public void initialize() {

        instructions = new LinkedList<>();
        data = new LinkedList<>();

        textArea.setEditable(false);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

    }

    public void addInstruction(String instruction) {
        instructions.add(Util.logPrefix() + instruction);
        update();
    }

    @SneakyThrows
    public void update() {

        Platform.runLater(() -> textArea.setText(String.join("\n", instructions)));

        Platform.runLater(() -> {

            data.clear();

            String path = Main.getConfig().get("files", "path");
            File file = new File(path);

            if(file.isDirectory()) {

                String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

                for(String folder : directories) {

                    File cache = new File(path + "/" + folder);

                    if(cache.exists() && cache.isDirectory()) {

                        try {

                            BasicFileAttributes attributes = Files.readAttributes(cache.toPath(), BasicFileAttributes.class);

                            data.add(FolderData.builder()
                                    .name(folder)
                                    .date(Util.uiFormat(new Date(attributes.creationTime().toMillis())))
                                    .path(cache.getAbsolutePath())
                                    .build());


                        } catch (IOException e) { e.printStackTrace(); }

                    }

                }

                table.getItems().clear();
                table.getItems().addAll(data);

            }

        });

    }

}
