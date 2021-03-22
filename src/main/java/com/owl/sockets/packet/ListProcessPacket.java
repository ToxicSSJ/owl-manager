package com.owl.sockets.packet;

import com.owl.controller.data.ProcessData;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ListProcessPacket extends Packet implements Serializable {

    private List<ProcessData> process;

}
