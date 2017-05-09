/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayCheque;
import com.court.model.Member;
import eu.hansolo.tilesfx.Tile;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.IntegerType;

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
    @FXML
    private Tile timeTile;
    @FXML
    private Label mbr_count_txt;
    @FXML
    private Label tot_col_txt;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        XYChart.Series<String, Number> series_1 = new XYChart.Series<>();
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 0, 30).getTime()), 2));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 1, 28).getTime()), 4));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(
                        new GregorianCalendar(2017, 2, 30).getTime()), 6));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 3, 30).getTime()), 10));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 4, 30).getTime()), 4));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 5, 30).getTime()), 5));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 6, 30).getTime()), 2));
        series_1.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 7, 30).getTime()), 7));

        XYChart.Series<String, Number> series_2 = new XYChart.Series<>();
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 0, 30).getTime()), 23400));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 1, 28).getTime()), 24500));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 2, 30).getTime()), 24900));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 3, 30).getTime()), 23000));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 4, 30).getTime()), 25600));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 5, 30).getTime()), 24000));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 6, 30).getTime()), 22670));
        series_2.getData().add(new XYChart.Data<>(
                new SimpleDateFormat("MMM").format(new GregorianCalendar(2017, 7, 30).getTime()), 23900));

        loan_release_chart.getData().add(series_1);
        loan_collection_chart.getData().add(series_2);
        //live update time tile =============================
        FxUtilsHandler.startTimeOf(timeTile);
        updateMemberCountTile(mbr_count_txt);
        updateMonthlyCollection(tot_col_txt);

    }

    private void updateMemberCountTile(Label label) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        int count = ((Long) c.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        label.setText(String.valueOf(count));
        session.close();
    }

    private void updateMonthlyCollection(Label label) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(LoanPayCheque.class);
        c.add(new Criterion() {
            final String propertyName = "chequeDate";
            final int month = new Date().getMonth() + 1;

            @Override
            public String toSqlString(Criteria crtr, CriteriaQuery cq) throws HibernateException {
                String[] columns = cq.getColumns(propertyName, crtr);
                if (columns.length != 1) {
                    throw new HibernateException("monthEq may only be used with single-column properties");
                }
                return "month(" + columns[0] + ") = ?";
            }

            @Override
            public TypedValue[] getTypedValues(Criteria crtr, CriteriaQuery cq) throws HibernateException {
                return new TypedValue[]{new TypedValue(IntegerType.INSTANCE, month, EntityMode.POJO)};
            }

            @Override
            public String toString() {
                return "month(" + propertyName + ") = " + month;
            }

        });
        Object ob = c.setProjection(Projections.sum("chequeAmount"))
                .uniqueResult();
        Double sum = ob != null ? (Double) ob : 0.0;
        label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
    }

}
