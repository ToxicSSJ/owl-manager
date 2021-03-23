package com.owl.type;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum ApplicationType implements Serializable {

    CALCULATOR("calc", "Calculator.exe", "Calculadora"),
    NOTEPAD("notepad", "notepad.exe", "Bloc de notas"),
    PAINT("mspaint", "mspaint.exe", "Paint"),
    EXPLORER("explorer.exe", "explorer.exe", "Explorador"),

    ;

    private String cmd;
    private String name;
    private String menu;

    ApplicationType(String cmd, String name, String menu) {
        this.cmd = cmd;
        this.name = name;
        this.menu = menu;
    }

}
