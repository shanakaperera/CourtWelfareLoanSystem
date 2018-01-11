/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Shanaka P
 */
public class ReportHandler {

    public static String COMPANY_NAME = "JUDICIAL SERVICE MUTUAL BENEFIT SOCIETY";
    public static String ADDRESS = "High court building colombo - 12";
    private final String reportPath;
    private final Map<String, Object> map;
    private final JRBeanCollectionDataSource ds;
    private final String outputFile = System.getProperty("user.home") + File.separatorChar + "JasperExample.pdf";
    private Connection con;
    private ImageView progressIndicator;
    
    public ReportHandler(String reportPath, Map<String, Object> map,
            JRBeanCollectionDataSource ds) {
        this.reportPath = reportPath;
        this.map = map;
        this.ds = ds;
    }

    public ReportHandler(String reportPath, Map<String, Object> map, JRBeanCollectionDataSource ds,
            Connection con) {
        this.reportPath = reportPath;
        this.map = map;
        this.con = con;
        this.ds = ds;
    }

    public void genarateReport() {

        progressIndicator = new ImageView();
        progressIndicator.setImage(new Image(FileHandler.LOADING_DEFAULT_GIF));
        VBox v = new VBox(progressIndicator);
        v.setAlignment(Pos.CENTER);
        Dialog alert_prog = new Alert(Alert.AlertType.NONE);
        alert_prog.setTitle("Ongoing progress");
        alert_prog.setHeaderText("Please wait until the report is generated. ");
        alert_prog.getDialogPane().setContent(v);
        alert_prog.setResult(Boolean.TRUE);
        alert_prog.show();

        Task<File> jprintTask = new Task<File>() {
            {
                setOnSucceeded(d -> {

                    alert_prog.close();
                    try {
                        Desktop.getDesktop().open(getValue());
                    } catch (IOException ex) {
                        Alert alert_error = new Alert(Alert.AlertType.ERROR);
                        alert_error.setTitle("Error");
                        alert_error.setHeaderText("PDF file cannot open. ");
                        alert_error.setContentText("No application registered for PDFs");
                        alert_error.show();
                    }

                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected File call() throws Exception {
                JasperReport jr = (JasperReport) JRLoader.loadObject(
                        ClassLoader.getSystemResourceAsStream(reportPath));
                JasperPrint jp;
                if (ds == null) {
                    if (con != null) {
                        jp = JasperFillManager.fillReport(jr, map, con);
                    } else {
                        jp = JasperFillManager.fillReport(jr, map, new JREmptyDataSource());
                    }
                } else {
                    jp = JasperFillManager.fillReport(jr, map, ds);
                }
                OutputStream outputStream = new FileOutputStream(new File(outputFile));

                JasperExportManager.exportReportToPdfStream(jp, outputStream);
                System.out.println("Successfully Generated !");
                System.out.println(outputFile);

                File myFile = null;
                if (Desktop.isDesktopSupported()) {
                    myFile = new File(outputFile);
                    Desktop.getDesktop().open(myFile);
                }

                return myFile;
            }
        };

        Thread reportGenThread = new Thread(jprintTask, "jprint-task");
        reportGenThread.setDaemon(true);
        reportGenThread.start();
    }

    public void genReport() {
        try {
//            JasperReport jr = (JasperReport) JRLoader.loadObject(
//                    ClassLoader.getSystemResourceAsStream(reportPath));

            //======NEW CHANGE====================
            File reportFile = new File(reportPath);
            JasperReport jr = (JasperReport) JRLoader.loadObject(reportFile);
            //======NEW CHANGE====================
            JasperPrint jp;
            if (ds == null) {
                if (con != null) {
                    jp = JasperFillManager.fillReport(jr, map, con);
                } else {
                    jp = JasperFillManager.fillReport(jr, map, new JREmptyDataSource());
                }
            } else {
                jp = JasperFillManager.fillReport(jr, map, ds);
            }
            OutputStream outputStream = new FileOutputStream(new File(outputFile));

            //////////////
//            JRProperties.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
//            JRProperties.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
            /////////////
            JasperExportManager.exportReportToPdfStream(jp, outputStream);
            System.out.println("Successfully Generated !");
            System.out.println(outputFile);
        } catch (FileNotFoundException | JRException e) {
            e.printStackTrace();
        }
    }

    public void viewReport() {

        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(outputFile);
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("PDF file cannot open. ");
                alert_error.setContentText("No application registered for PDFs");
                alert_error.show();
            }
        }
    }

}
