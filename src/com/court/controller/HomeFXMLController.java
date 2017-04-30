/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class HomeFXMLController implements Initializable {

    @FXML
    private LineChart<String, Number> loan_release_chart;
    @FXML
    private LineChart<String, Number> loan_collection_chart;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        XYChart.Series<String, Number> series_1 = new XYChart.Series<>();
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 0, 30).getTime()), 2));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 1, 28).getTime()), 4));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 2, 30).getTime()), 6));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 3, 30).getTime()), 10));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 4, 30).getTime()), 4));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 5, 30).getTime()), 5));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 6, 30).getTime()), 2));
        series_1.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 7, 30).getTime()), 7));

        XYChart.Series<String, Number> series_2 = new XYChart.Series<>();
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 0, 30).getTime()), 23400));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 1, 28).getTime()), 24500));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 2, 30).getTime()), 24900));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 3, 30).getTime()), 23000));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 4, 30).getTime()), 25600));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 5, 30).getTime()), 24000));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 6, 30).getTime()), 22670));
        series_2.getData().add(new XYChart.Data<>(new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 7, 30).getTime()), 23900));

        loan_release_chart.getData().add(series_1);
        loan_collection_chart.getData().add(series_2);

    }

}
