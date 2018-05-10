/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.main;

import com.court.controller.DashBoardFxmlController;
import com.court.db.HibernateUtil;
import com.court.handler.FileHandler;
import com.court.handler.PropHandler;
import com.court.model.Company;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        String id = "courtLoanAppId";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(id);
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        if (!alreadyRunning) {

            new Thread(() -> {
                String url = null;
                try {
                    url = PropHandler.getConnectionProperties().getProperty("hibernate.connection.url");
                } catch (IOException ex) {
                    System.out.println("IOExecption " + ex.toString());
                    System.exit(0);
                }
                String pattern1 = "//";
                String pattern2 = "/";

                Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                Matcher m = p.matcher(url);

                String port = "", host = "";
                while (m.find()) {
                    System.out.println(m.group(1));
                    String[] split = m.group(1).split(":");
                    host = split[0];
                    port = split[1];
                }

                try {
                    Socket socket = new Socket(host, Integer.parseInt(port));
                    socket.close();
                } catch (UnknownHostException e1) {
                    System.out.println("Unknown host - " + e1.toString());
                    System.exit(0);
                } catch (IOException e2) {
                    System.out.println("IOException - " + e2.toString());
                    System.exit(0);
                } catch (IllegalArgumentException e3) {
                    System.out.println("Illegal Argument exception - " + e3.toString());
                    System.exit(0);
                } catch (Exception e4) {
                    System.out.println("Some other exception - " + e4.toString());
                    System.exit(0);
                }
            }).start();
            launch(args);
        }
    }

}
