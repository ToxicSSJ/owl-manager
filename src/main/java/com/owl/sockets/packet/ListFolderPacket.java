package com.owl.sockets.packet;

import com.owl.controller.data.FolderData;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ListFolderPacket extends Packet implements Serializable {

    private List<FolderData> folders;

}
