package com.owl.module;

import com.owl.Main;
import com.owl.controller.FilesController;

import com.owl.sockets.SocketClient;
import com.owl.sockets.packet.CreateFolderPacket;
import com.owl.sockets.packet.CreatedFolderPacket;
import com.owl.sockets.packet.ErrorPacket;
import com.owl.sockets.packet.HelloPacket;
import com.owl.type.ModuleType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import lombok.SneakyThrows;

import java.io.File;

public class FilesModule extends Module {

    private Stage stage;
    private FilesController controller;

    private SocketClient client;

    @Override
    @SneakyThrows
    public void start() {

        Pair<Stage, FilesController> loader = Main.load("files", "console");

        stage = loader.getKey();
        controller = loader.getValue();
        client = new SocketClient(Main.getConfig().get("socket", "host"), Main.getConfig().getInt("socket", "port"));

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Modulo Files (UI)");

        stage.show();

    }

    @Override
    public void socket() {

        client.listen(HelloPacket.class, (packet) -> {
            controller.addInstruction("Modulo registrado por el Kernel!");
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
            client.send(CreatedFolderPacket.builder().name(packet.getName()).build());

        });

        controller.addInstruction("Registrando modulo...");
        client.send(HelloPacket.builder().module(ModuleType.FILES_MODULE).build());

    }

}
