package com.owl.sockets;

import com.owl.sockets.events.SocketServerEvent;
import com.owl.sockets.packet.Packet;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class SocketServer {

    private SocketServer instance;

    private int port;

    private ServerSocket socket;
    private Thread thread;

    private LinkedList<SocketFetcher> clients;
    private Map<Class, SocketServerEvent> events;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    public SocketServer(int port) {

        this.instance = this;

        this.port = port;

        this.clients = new LinkedList<>();
        this.events = new HashMap<>();

    }

    @SneakyThrows
    public void connect() {

        this.socket = new ServerSocket(port);

        thread = new Thread(new Runnable() {

            @Override
            @SneakyThrows
            public void run() {

                while(true) {

                    Socket client = socket.accept();
                    clients.add(new SocketFetcher(instance, client));

                }

            }

        });

        thread.start();

    }

    public void fire(SocketFetcher fetcher, Packet packet) {

        SocketServerEvent trigger = getTrigger(packet);

        if(trigger == null)
            return;

        trigger.fire(fetcher, packet);

    }

    public SocketServerEvent getTrigger(Packet packet) {
        if(events.containsKey(packet.getClass()))
            return events.get(packet.getClass());
        return null;
    }

    public <E extends Packet> void listen(Class<E> clazz, SocketServerEvent<E> event) {
        events.put(clazz, event);
    }

}
