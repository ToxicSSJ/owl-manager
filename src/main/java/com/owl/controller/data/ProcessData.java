package com.owl.controller.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ProcessData implements Serializable {

    private String pid;
    private String name;
    private String startTime;

}
