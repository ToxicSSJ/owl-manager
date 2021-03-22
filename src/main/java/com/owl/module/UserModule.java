package com.owl.module;

import com.owl.Main;

import com.owl.controller.UserController;
import com.owl.controller.data.ProcessData;
import com.owl.sockets.SocketClient;
import com.owl.sockets.packet.HelloPacket;
import com.owl.sockets.packet.ListProcessPacket;
import com.owl.sockets.packet.OpenApplicationPacket;
import com.owl.sockets.packet.OpenedApplicationPacket;
import com.owl.type.ApplicationType;
import com.owl.type.ModuleType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import lombok.SneakyThrows;

import java.util.LinkedList;

public class UserModule extends Module {

    private Stage stage;
    private UserController controller;

    private SocketClient client;

    private LinkedList<ProcessData> data;

    @Override
    @SneakyThrows
    public void start() {

        Pair<Stage, UserController> loader = Main.load("primary", "console");

        stage = loader.getKey();
        controller = loader.getValue();
        data = new LinkedList<>();
        client = new SocketClient(Main.getConfig().get("socket", "host"), Main.getConfig().getInt("socket", "port"));

        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setTitle("Modulo de Usuario (GUI)");

        stage.show();

    }

    @Override
    public void socket() {

        client.listen(HelloPacket.class, (packet) -> {
            //controller.addInstruction("Modulo registrado por el Kernel!");
        });

        client.listen(OpenedApplicationPacket.class, (packet) -> {

        });

        //controller.addInstruction("Registrando modulo...");
        client.send(HelloPacket.builder().module(ModuleType.USER_GUI_MODULE).build());

    }

}
