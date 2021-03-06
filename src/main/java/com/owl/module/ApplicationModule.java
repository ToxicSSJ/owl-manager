package com.owl.module;

import com.owl.Main;
import com.owl.controller.ApplicationController;
import com.owl.controller.data.ProcessData;
import com.owl.sockets.SocketClient;
import com.owl.sockets.packet.*;
import com.owl.type.ApplicationType;
import com.owl.type.ModuleType;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import lombok.SneakyThrows;
import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ApplicationModule extends Module {

    private Stage stage;
    private ApplicationController controller;

    private SocketClient client;
    private Thread monitor;

    private LinkedList<ProcessData> data;
    private Pair<Stage, ApplicationController> loader;

    private boolean closed = false;

    @Override
    @SneakyThrows
    public void start() {

        loader = Main.load("application", "console");

        stage = loader.getKey();
        controller = loader.getValue();
        data = new LinkedList<>();
        client = new SocketClient(Main.getConfig().get("socket", "host"), Main.getConfig().getInt("socket", "port"));

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Modulo Application (Console)");

        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {

            for(ProcessData info : data)
                JProcesses.killProcess(Integer.parseInt(info.getPid()));

            closed = true;

        });

        monitor = new Thread(new Runnable() {

            @Override
            @SneakyThrows
            public void run() {

                while(true) {

                    Thread.sleep(1000);

                    List<ProcessInfo> processes = JProcesses.getProcessList();
                    data.clear();

                    for(ProcessInfo process : processes)
                        for(ApplicationType type : ApplicationType.values())
                            if(process.getName().equals(type.getName())) {

                                data.add(ProcessData.builder()
                                        .name(process.getName())
                                        .pid(process.getPid())
                                        .startTime(process.getStartTime())
                                        .build());

                            }

                    update();

                }

            }

        });

        monitor.start();
        stage.show();

    }

    @Override
    public void socket() {

        client.listen(HelloPacket.class, (packet) -> {
            controller.addInstruction("Modulo registrado por el Kernel!");
        });

        client.listen(OpenApplicationModulePacket.class, (packet) -> {

            if(!closed) {

                controller.addInstruction("El modulo ya ha sido abierto!");
                return;

            }

            Platform.runLater(() -> {
                try {

                    loader = Main.load("application", "console");


                    stage = loader.getKey();
                    controller = loader.getValue();

                    stage.initStyle(StageStyle.UTILITY);
                    stage.setResizable(false);
                    stage.setTitle("Modulo Application (Console)");

                    stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {

                        for(ProcessData info : data)
                            JProcesses.killProcess(Integer.parseInt(info.getPid()));

                        closed = true;

                    });

                    stage.show();
                    closed = false;

                } catch (Exception e) {}

            });

        });

        client.listen(OpenApplicationPacket.class, (packet) -> {

            if(closed)
                return;

            try {

                Runtime.getRuntime().exec(packet.getType().getCmd());

                controller.addInstruction("Se ha ejecutado un comando para la inicializaci??n de un proceso (" + packet.getType().getCmd() + ")!");
                client.send(OpenedApplicationPacket.builder().type(packet.getType()).build());

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        client.listen(KillApplicationPacket.class, (packet) -> {

            if(closed)
                return;

            JProcesses.killProcess(Integer.parseInt(packet.getPid()));

            controller.addInstruction("Se ha enviado una petici??n de cerrado para el proceso con PID (" + packet.getPid() + ")!");
            client.send(KilledApplicationPacket.builder().pid(packet.getPid()).build());

        });

        controller.addInstruction("Registrando modulo...");
        client.send(HelloPacket.builder().module(ModuleType.APPLICATION_MODULE).build());

    }

    public void update() {
        controller.addInstruction("Enviando lista de procesos... " + "(" + data.size() + " procesos)");
        client.send(ListProcessPacket.builder().process(data).build());
    }

}
