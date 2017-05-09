/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.main;

import com.court.controller.DashBoardFxmlController;
import com.court.db.HibernateUtil;
import com.court.handler.FileHandler;
import com.court.model.Company;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.hibernate.Session;

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

        //check if software path is already exist
        FileHandler.createSoftwarePath();
        Company c = loadCompany();
        // load main form in to VBox (Root)
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/com/court/view/DashBoardFxml.fxml"));
        Rectangle2D s_size = Screen.getPrimary().getVisualBounds();
        VBox mainPane = (VBox) loader.load();
        mainPane.setPrefSize(s_size.getWidth(), s_size.getHeight());
        // add main form into the scene
        Scene scene = new Scene(mainPane);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(FileHandler.TITLEBAR_ICO)));
        primaryStage.setTitle(c.getTitleName());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);    // make the main form fit to the screen

        //primary stage close button action consume..........
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
        });

        primaryStage.show();
        dashboard_controller = loader.getController();
        dashboard_controller.getDashboard_header().setText(c.getCompanyName().toUpperCase());
        dashboard_controller.performLoginAction(primaryStage);
    }

    private Company loadCompany() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Company com = (Company) session.load(Company.class, 1);
        session.close();
        return com;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
