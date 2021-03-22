package com.owl.sockets.events;

import com.owl.sockets.packet.Packet;

public interface SocketClientEvent<T extends Packet> {

    void fire(T packet);

}
