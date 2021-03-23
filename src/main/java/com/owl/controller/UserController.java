package com.owl.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class UserController implements Controller {

    public Text Mensaje;
    public Text Mensaje2;
    public MenuButton aplicacion;
    @FXML
    private TextField nombreCarpeta;

    @FXML
    public void Abrir(ActionEvent actionEvent) {
        if (aplicacion.getText().equals("Seleccione una opcion")){
            this.Mensaje.setText("Seleccione una opcion");
        }else{
            this.Mensaje.setText("");
        }
    }

    public void crearCarpeta(ActionEvent actionEvent) {
        if (nombreCarpeta.getText().equals("")){
            this.Mensaje2.setText("Ingrese un nombre para la carpeta");
        }else{
            this.Mensaje.setText("");
        }

    }

    public void setCalculadora(ActionEvent actionEvent) {
        this.aplicacion.setText("Calculadora");
    }

    public void setBloc(ActionEvent actionEvent) {
        this.aplicacion.setText("Bloc de notas");
    }

    public void setPaint(ActionEvent actionEvent) {
        this.aplicacion.setText("Paint");
    }

    public void setExplorador(ActionEvent actionEvent) {
        this.aplicacion.setText("Explorador");
    }
}
