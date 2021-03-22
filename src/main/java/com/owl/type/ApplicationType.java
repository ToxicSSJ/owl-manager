package com.owl.type;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum ApplicationType implements Serializable {

    CALCULATOR("calc", "Calculator.exe"),
    NOTEPAD("notepad", "notepad.exe"),
    PAINT("mspaint", "mspaint.exe"),
    EXPLORER("explorer.exe", "explorer.exe"),

    ;

    private String cmd;
    private String name;

    ApplicationType(String cmd, String name) {
        this.cmd = cmd;
        this.name = name;
    }

}
