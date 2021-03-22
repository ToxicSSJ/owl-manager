package com.owl.controller.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class FolderData implements Serializable {

    private String name;
    private String date;
    private String path;

}
