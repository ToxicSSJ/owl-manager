package com.owl.sockets.packet;

import com.owl.type.ApplicationType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OpenedApplicationPacket extends Packet implements Serializable {

    private ApplicationType type;

}
