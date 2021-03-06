package com.owl.sockets.packet;

import com.owl.controller.data.FolderData;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CreatedFolderPacket extends Packet implements Serializable {

    private FolderData data;

}
