package com.owl.sockets.packet;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class KilledApplicationPacket extends Packet implements Serializable {

    private String pid;

}
