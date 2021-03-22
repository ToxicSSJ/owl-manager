package com.owl.sockets;

import com.owl.sockets.events.SocketClientEvent;
import com.owl.sockets.packet.Packet;
import com.owl.type.ModuleType;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Data
public class SocketClient {

    private ModuleType module;

    private Socket socket;
    private Thread thread;

    private Map<Class, SocketClientEvent> events;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    @SneakyThrows
    public SocketClient(String url, int port) {

        this.socket = new Socket(url, port);
        this.events = new HashMap<>();

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());

        thread = new Thread(new Runnable() {

            @Override
            @SneakyThrows
            public void run() {

                while(true) {

                    if(input.available() <= -1)
                        continue;

                    try {

                        Packet packet = (Packet) input.readObject();
                        fire(packet);

                    } catch (Exception e) { }

                }

            }

        });

        thread.start();

    }

    @SneakyThrows
    public void send(Packet packet) {
        try {

            output.writeObject(packet);
            output.flush();

        } catch(Exception e) {

        }
    }

    public void fire(Packet packet) {

        SocketClientEvent trigger = getTrigger(packet);

        if(trigger == null)
            return;

        trigger.fire(packet);

    }

    public SocketClientEvent getTrigger(Packet packet) {
        if(events.containsKey(packet.getClass()))
            return events.get(packet.getClass());
        return null;
    }

    public <E extends Packet> void listen(Class<E> clazz, SocketClientEvent<E> event) {
        events.put(clazz, event);
    }

}
