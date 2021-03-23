package com.owl.module;

import com.owl.Main;

import com.owl.controller.UserController;
import com.owl.controller.data.ProcessData;
import com.owl.sockets.SocketClient;
import com.owl.sockets.packet.*;

import com.owl.type.ModuleType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.LinkedList;

@Data
public class UserModule extends Module {

    private Stage stage;
    private UserController controller;

    private SocketClient client;

    private LinkedList<ProcessData> data;

    @Override
    @SneakyThrows
    public void start() {

        Pair<Stage, UserController> loader = Main.load("GuiUser", "console");

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
            controller.addInstruction("Modulo registrado por el Kernel!");
        });

        client.listen(OpenedApplicationPacket.class, (packet) -> {
            controller.addInstruction("Se esta abriendo la aplicación: (" + packet.getType().name() + ")!");
        });

        client.listen(ListProcessPacket.class, (packet) -> {
            controller.addInstruction("Se ha recibido la lista de procesos!");
            controller.updateProcess(packet.getProcess());
        });

        client.listen(ListFolderPacket.class, (packet) -> {
            controller.addInstruction("Se ha recibido la lista de carpetas!");
            controller.updateFolders(packet.getFolders());
        });

        client.listen(CreatedFolderPacket.class, (packet) -> {
            controller.addInstruction("Se ha creado una carpeta!");
            controller.addFolder(packet.getData());
        });

        client.listen(KilledApplicationPacket.class, (packet) -> {
            controller.addInstruction("Se está procesando la petición para cerrar el proceso (" + packet.getPid() + ")!");
        });

        controller.addInstruction("Registrando modulo...");
        client.send(HelloPacket.builder().module(ModuleType.USER_GUI_MODULE).build());

        controller.addInstruction("Pidiendo lista de carpetas registradas...");
        client.send(RequestListFolderPacket.builder().build());

    }

}
