package com.owl.module;

import com.owl.Main;
import com.owl.Util;
import com.owl.controller.FilesController;

import com.owl.controller.data.FolderData;
import com.owl.controller.data.ProcessData;
import com.owl.sockets.SocketClient;
import com.owl.sockets.packet.*;
import com.owl.type.ApplicationType;
import com.owl.type.ModuleType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import lombok.SneakyThrows;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FilesModule extends Module {

    private Stage stage;
    private FilesController controller;

    private SocketClient client;
    private LinkedList<FolderData> data;

    @Override
    @SneakyThrows
    public void start() {

        Pair<Stage, FilesController> loader = Main.load("files", "console");

        stage = loader.getKey();
        controller = loader.getValue();
        data = new LinkedList<>();
        client = new SocketClient(Main.getConfig().get("socket", "host"), Main.getConfig().getInt("socket", "port"));

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Modulo Files (UI)");

        stage.show();

        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {

        });

    }

    @Override
    public void socket() {

        client.listen(HelloPacket.class, (packet) -> {
            controller.addInstruction("Modulo registrado por el Kernel!");
        });

        client.listen(DeleteFolderPacket.class, (packet) -> {

            String path = Main.getConfig().get("files", "path");
            File file = new File(path + "/" + packet.getName() + "/");

            if(!file.isDirectory()) {

                client.send(ErrorPacket.builder().code(301).message("La carpeta (" + packet.getName() + ") no existe!").build());
                return;

            }

            controller.addInstruction("Borrando carpeta (" + packet.getName() + ")...");
            file.delete();

            controller.addInstruction("Carpeta borrada!");
            controller.update();
            client.send(DeletedFolderPacket.builder().name(packet.getName()).build());

        });

        client.listen(CreateFolderPacket.class, (packet) -> {

            String path = Main.getConfig().get("files", "path");
            File file = new File(path + "/" + packet.getName());

            if(file.isDirectory()) {

                client.send(ErrorPacket.builder().code(301).message("La carpeta (" + packet.getName() + ") ya existe!").build());
                return;

            }

            controller.addInstruction("Creando carpeta (" + packet.getName() + ")...");
            file.mkdirs();

            controller.addInstruction("Carpeta creada!");
            client.send(CreatedFolderPacket.builder().data(
                    FolderData.builder().name(packet.getName()).date(Util.uiFormat(new Date())).path(file.getAbsolutePath()).build())
                    .build());

        });

        client.listen(RequestListFolderPacket.class, (packet) -> {

            List<FolderData> data = new ArrayList<>();

            String path = Main.getConfig().get("files", "path");
            File file = new File(path);

            if(file.isDirectory()) {

                String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

                for(String folder : directories) {

                    File cache = new File(path + "/" + folder);

                    if(cache.exists()) {

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

                client.send(ListFolderPacket.builder().folders(data).build());

            }

        });

        controller.addInstruction("Registrando modulo...");
        client.send(HelloPacket.builder().module(ModuleType.FILES_MODULE).build());

    }

}
