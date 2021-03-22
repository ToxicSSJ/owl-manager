package com.owl.controller;

import com.owl.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.SneakyThrows;

import java.util.LinkedList;

public class ApplicationController implements Controller {

    @FXML private TextArea textArea;

    private LinkedList<String> instructions;

    @FXML
    public void initialize() {

        instructions = new LinkedList<>();

        textArea.setEditable(false);

    }

    public void addInstruction(String instruction) {
        instructions.add(Util.logPrefix() + instruction);
        update();
    }

    @SneakyThrows
    public void update() {

        Platform.runLater(() -> textArea.setText(String.join("\n", instructions)));

    }

}
