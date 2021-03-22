package com.owl.module;

import com.owl.Main;
import com.owl.controller.ApplicationController;
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

    @Override
    @SneakyThrows
    public void start() {

        Pair<Stage, ApplicationController> loader = Main.load("application", "console");

        stage = loader.getKey();
        controller = loader.getValue();
        data = new LinkedList<>();
        client = new SocketClient(Main.getConfig().get("socket", "host"), Main.getConfig().getInt("socket", "port"));

        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle("Modulo Application (Console)");

        monitor = new Thread(new Runnable() {

            @Override
            @SneakyThrows
            public void run() {

                while(true) {

                    Thread.sleep(1000);

                    data.clear();
                    List<ProcessInfo> processes = JProcesses.getProcessList();

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

        client.listen(OpenApplicationPacket.class, (packet) -> {

            try {

                Runtime.getRuntime().exec(packet.getType().getCmd());

                controller.addInstruction("Se ha ejecutado un comando para la inicialización de un proceso (" + packet.getType().getCmd() + ")!");
                client.send(OpenedApplicationPacket.builder().type(packet.getType()).build());

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        controller.addInstruction("Registrando modulo...");
        client.send(HelloPacket.builder().module(ModuleType.APPLICATION_MODULE).build());

    }

    public void update() {
        controller.addInstruction("Enviando lista de procesos... " + "(" + data.size() + " procesos)");
        client.send(ListProcessPacket.builder().process(data).build());
    }

}
