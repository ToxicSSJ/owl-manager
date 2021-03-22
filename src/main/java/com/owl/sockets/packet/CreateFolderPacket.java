package com.owl.sockets.packet;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CreateFolderPacket extends Packet implements Serializable {

    private String name;

}
