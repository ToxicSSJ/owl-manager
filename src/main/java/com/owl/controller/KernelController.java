package com.owl.controller;

import com.owl.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.LinkedList;

public class KernelController implements Controller {

    @FXML
    private TextArea textArea;

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

    public void update() {
        Platform.runLater(() -> {
            try {
                textArea.setText(String.join("\n", instructions));
            } catch(Exception e) { }
        });
    }

}
