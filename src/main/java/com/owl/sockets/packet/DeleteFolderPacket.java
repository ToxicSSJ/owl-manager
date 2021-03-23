package com.owl.sockets.packet;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class DeleteFolderPacket extends Packet implements Serializable {

    private String name;

}
