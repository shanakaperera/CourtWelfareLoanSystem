/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.main;

import com.court.controller.DashBoardFxmlController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Shanaka P
 */
public class MainClass extends Application {

    DashBoardFxmlController dashboard_controller;
    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        // load main form in to VBox (Root)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/DashBoardFxml.fxml"));
        Rectangle2D s_size = Screen.getPrimary().getVisualBounds();
        VBox mainPane = (VBox) loader.load();
        mainPane.setPrefSize(s_size.getWidth(), s_size.getHeight());
        // add main form into the scene
        Scene scene = new Scene(mainPane);

        primaryStage.setTitle("Court Welfare Organization");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);    // make the main form fit to the screen
        primaryStage.show();
        dashboard_controller = loader.getController();
        dashboard_controller.performLoginAction(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
