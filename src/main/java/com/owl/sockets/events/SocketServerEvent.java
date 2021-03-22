package com.owl.sockets.events;

import com.owl.sockets.SocketFetcher;
import com.owl.sockets.packet.Packet;

public interface SocketServerEvent<T extends Packet> {

    void fire(SocketFetcher fetcher, T packet);

}
