package com.owl;

import com.owl.controller.Controller;
import com.owl.module.Module;
import com.owl.type.ModuleType;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.LinkedList;

public class Main extends Application {

    private static Config config;

    private LinkedList<Module> loadedModules;

    @Override
    public void start(Stage stage) throws Exception {

        loadedModules = new LinkedList<>();

        if(config.getBoolean("general", "kernel")) {

            loadedModules.add(start(ModuleType.KERNEL_MODULE));
            loadedModules.add(start(ModuleType.FILES_MODULE));
            loadedModules.add(start(ModuleType.APPLICATION_MODULE));

        }

        if(config.getBoolean("general", "user"))
            loadedModules.add(start(ModuleType.USER_GUI_MODULE));

        for(Module module : loadedModules)
            module.socket();

    }

    public static Module start(ModuleType type) throws IllegalAccessException, InstantiationException {
        Module module = type.getClazz().newInstance();
        module.start();
        return module;
    }

    public static <E extends Controller> Pair<Stage, E> load(String fxml, String style) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("styles/" + style + ".css").toExternalForm());
        stage.setScene(scene);
        return new Pair(stage, controller);
    }

    public static Stage loadStage(String fxml) throws IOException {
        Stage stage = new Stage();
        stage.setScene(loadScene(fxml));
        return stage;
    }

    public static Scene loadScene(String fxml) throws IOException {
        Scene scene = new Scene(loadFXML(fxml));
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        return scene;
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Config getConfig() {
        return config;
    }

    public static void main(String[] args) {
        config = new Config();
        launch();
    }

}
