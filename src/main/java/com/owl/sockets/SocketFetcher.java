package com.owl.sockets;

import com.owl.sockets.packet.Packet;
import com.owl.type.ModuleType;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Data
public class SocketFetcher {

    private SocketFetcher instance;

    private ModuleType module;
    private SocketServer server;

    private Socket socket;
    private Thread thread;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    @SneakyThrows
    public SocketFetcher(SocketServer server, Socket socket) {

        this.instance = this;

        this.socket = socket;
        this.server = server;

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());

        thread = new Thread(new Runnable() {

            @Override
            @SneakyThrows
            public void run() {

                while(true) {

                    if(input.available() <= -1)
                        continue;

                    Packet packet = (Packet) input.readObject();
                    server.fire(instance, packet);

                }

            }

        });

        thread.start();

    }

    @SneakyThrows
    public void send(Packet packet) {
        try {

            //output.writeObject(packet);
            output.writeUnshared(packet);
            output.flush();
            output.reset();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
