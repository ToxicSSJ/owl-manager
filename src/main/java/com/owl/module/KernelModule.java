package com.owl.module;

import com.owl.Main;
import com.owl.controller.KernelController;
import com.owl.sockets.SocketFetcher;
import com.owl.sockets.SocketServer;
import com.owl.sockets.packet.*;
import com.owl.type.ModuleType;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import lombok.SneakyThrows;

public class KernelModule extends Module {

    private Stage stage;
    private KernelController controller;

    private SocketServer server;

    private SocketFetcher userClient;
    private SocketFetcher appClient;
    private SocketFetcher filesClient;

    @Override
    @SneakyThrows
    public void start() {

        Pair<Stage, KernelController> loader = Main.load("kernel", "console");

        stage = loader.getKey();
        controller = loader.getValue();
        server = new SocketServer(Main.getConfig().getInt("socket", "port"));

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Modulo Kernel (Consola)");

        server.connect();
        stage.show();

        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            System.exit(-1);
        });

    }

    @Override
    public void socket() {

        server.listen(HelloPacket.class, (fetcher, packet) -> {

            switch (packet.getModule()) {

                case USER_GUI_MODULE:
                    userClient = fetcher;
                    fetcher.setModule(ModuleType.USER_GUI_MODULE);
                    fetcher.send(HelloPacket.builder().module(ModuleType.KERNEL_MODULE).build());
                    controller.addInstruction("Registrado USER_GUI_MODULE (" + fetcher.getSocket().getRemoteSocketAddress().toString() + ")!");
                    break;

                case APPLICATION_MODULE:
                    appClient = fetcher;
                    fetcher.setModule(ModuleType.APPLICATION_MODULE);
                    fetcher.send(HelloPacket.builder().module(ModuleType.KERNEL_MODULE).build());
                    controller.addInstruction("Registrado APPLICATION_MODULE (" + fetcher.getSocket().getRemoteSocketAddress().toString() + ")!");
                    break;

                case FILES_MODULE:
                    filesClient = fetcher;
                    fetcher.setModule(ModuleType.FILES_MODULE);
                    fetcher.send(HelloPacket.builder().module(ModuleType.KERNEL_MODULE).build());
                    controller.addInstruction("Registrado FILES_MODULE (" + fetcher.getSocket().getRemoteSocketAddress().toString() + ")!");
                    break;

            }

        });

        server.listen(CreateFolderPacket.class, ((fetcher, packet) -> remit(fetcher, filesClient, packet, filesNeeded())));
        server.listen(RequestListFolderPacket.class, ((fetcher, packet) -> remit(fetcher, filesClient, packet, filesNeeded())));
        server.listen(DeleteFolderPacket.class, ((fetcher, packet) -> remit(fetcher, filesClient, packet, filesNeeded())));

        server.listen(OpenApplicationPacket.class, ((fetcher, packet) -> remit(fetcher, appClient, packet, applicationsNeeded())));
        server.listen(KillApplicationPacket.class, ((fetcher, packet) -> remit(fetcher, appClient, packet, applicationsNeeded())));

        server.listen(CreatedFolderPacket.class, ((fetcher, packet) -> remit(fetcher, userClient, packet, userNeeded())));
        server.listen(DeletedFolderPacket.class, ((fetcher, packet) -> remit(fetcher, userClient, packet, userNeeded())));
        server.listen(OpenedApplicationPacket.class, ((fetcher, packet) -> remit(fetcher, userClient, packet, userNeeded())));
        server.listen(KilledApplicationPacket.class, ((fetcher, packet) -> remit(fetcher, userClient, packet, userNeeded())));

        server.listen(ListProcessPacket.class, ((fetcher, packet) -> remit(fetcher, userClient, packet, userNeeded())));
        server.listen(ListFolderPacket.class, ((fetcher, packet) -> remit(fetcher, userClient, packet, userNeeded())));

    }

    public void remit(SocketFetcher from, SocketFetcher to, Packet packet, ErrorPacket error) {

        controller.addInstruction("Remitiendo paquete (PACKET=" + packet.getClass().getName() + ")!");

        if(to == null || !to.getSocket().isConnected()) {

            from.send(error);
            controller.addInstruction("Se remitió un error al origen!");
            return;

        }

        to.send(packet);
        controller.addInstruction("Paquete remitido con exito!");

    }

    public ErrorPacket userNeeded() {
        return ErrorPacket.builder()
                .code(400)
                .message("El cliente de usuario (GUI) aún no se ha conectado al Kernel")
                .build();
    }

    public ErrorPacket filesNeeded() {
        return ErrorPacket.builder()
                .code(401)
                .message("El cliente de usuario (GUI) aún no se ha conectado al Kernel")
                .build();
    }

    public ErrorPacket applicationsNeeded() {
        return ErrorPacket.builder()
                .code(402)
                .message("El cliente de aplicaciones aún no se ha conectado al Kernel")
                .build();
    }

}
