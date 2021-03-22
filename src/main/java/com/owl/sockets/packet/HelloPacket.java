package com.owl.sockets.packet;

import com.owl.type.ModuleType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class HelloPacket extends Packet implements Serializable {

    private ModuleType module;

}
