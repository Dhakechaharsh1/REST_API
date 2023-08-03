package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            VBox root = loader.load();

            // Create the Scene
            Scene scene = new Scene(root, 700, 700);

            // Set the scene on the stage
            primaryStage.setScene(scene);

            // Add an icon to the application window
            primaryStage.getIcons().add(new Image("F:/Georgian/SEM 3/Advance Object Oriented JAVA/Lab 10/demo/world_8044465.png"));

            // Set the stage title
            primaryStage.setTitle("Countries Information");

            // Show the stage
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
