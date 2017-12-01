/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.Accumlator;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.court.model.SubscriptionPay;
import eu.hansolo.tilesfx.Tile;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import static java.time.temporal.TemporalAdjusters.*;

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
    @FXML
    private Tile collection_tile;
    @FXML
    private Tile ln_amt_total_tile;
    @FXML
    private Label ln_amt_tot_label;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        XYChart.Series<String, Number> series_1 = new XYChart.Series<>();
        Map<String, Number> map = getLoanReleasedData();
        
        for (Map.Entry<String, Number> entry : map.entrySet()) {
            String key = new SimpleDateFormat("MMM")
                    .format(DateTime.parse(entry.getKey(), DateTimeFormat.forPattern("yyyy-MM-dd")).toDate());
            Number value = entry.getValue();
            series_1.getData().add(new XYChart.Data<>(key, value));
        }

        //================================================================================================
        XYChart.Series<String, Number> series_2 = new XYChart.Series<>();
        Map<String, Number> map1 = getLoanCollectionData();
        
        for (Map.Entry<String, Number> entry : map1.entrySet()) {
            String key = new SimpleDateFormat("MMM")
                    .format(DateTime.parse(entry.getKey(), DateTimeFormat.forPattern("yyyy-MM-dd")).toDate());
            Number value = entry.getValue();
            series_2.getData().add(new XYChart.Data<>(key, value));
        }

        loan_release_chart.getData().add(series_1);
        loan_collection_chart.getData().add(series_2);
        //live update time tile =============================
        FxUtilsHandler.startTimeOf(timeTile);
        updateMemberCountTile(mbr_count_txt);
        updateMonthlyCollection(tot_col_txt);
        updateLoanAmtCollectionCurrentMonth(ln_amt_tot_label);

        //=========================
        collection_tile.setText("TOTAL COLLECTION - " + new SimpleDateFormat("MMMMM").format(new Date()));
        ln_amt_total_tile.setText("TOTAL LOAN AMOUNT GIVEN - " + new SimpleDateFormat("MMMMM").format(new Date()));

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
        Criteria c = session.createCriteria(LoanPayment.class);
        c.add(filterByMonthCriterion("paymentDate"));
        Object ob = c.setProjection(Projections.sum("paidAmt"))
                .uniqueResult();

        Criteria c1 = session.createCriteria(SubscriptionPay.class);
        c1.add(filterByMonthCriterion("addedDate"));
        List<SubscriptionPay> spay = c1.list();

        Accumlator sp = spay.stream()
                .filter(p -> p != null)
                .collect(Collector.of(Accumlator::new, Accumlator::accumlate, Accumlator::combine));
        double spayTot = sp.getAci() + sp.getHoi() + sp.getAdmission() + sp.getMembership() + sp.getMembership() + sp.getSavings();
        Double sum = ob != null ? (Double) ob : 0.0 + spayTot;
        label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
        session.close();
    }

    private void updateLoanAmtCollectionCurrentMonth(Label label) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.add(Restrictions.eq("status", true));
        c.add(filterByMonthCriterion("grantedDate"));
        Object ob = c.setProjection(Projections.sum("loanAmount"))
                .uniqueResult();

        Double sum = ob != null ? (Double) ob : 0.0;
        label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
    }

    private Criterion filterByMonthCriterion(final String propertyName) {

        return new Criterion() {

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

        };
    }

    private Map<String, Number> getLoanReleasedData() {
        LocalDate now = LocalDate.now();
        Map<String, Number> map = new HashMap<>();
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(MemberLoan.class);

        ProjectionList pList = Projections.projectionList();
        ClassMetadata lpMeta = s.getSessionFactory().getClassMetadata(MemberLoan.class);
        pList.add(Projections.property(lpMeta.getIdentifierPropertyName()));
        for (String prop : lpMeta.getPropertyNames()) {
            pList.add(Projections.property(prop), prop);
        }
        c.add(Restrictions.eq("status", true));
        c.add(Restrictions.between("grantedDate",
                FxUtilsHandler.getDateFrom(now.with(firstDayOfYear())),
                FxUtilsHandler.getDateFrom(now.with(lastDayOfYear()))
        ));
        c.setProjection(pList
                .add(Projections.sqlGroupProjection("DATE_FORMAT(granted_date, '%Y-%m-01') AS groupPro", "groupPro", new String[]{"groupPro"}, new Type[]{StringType.INSTANCE}))
                .add(Projections.sqlProjection("SUM(loan_amount) AS lSum", new String[]{"lSum"}, new Type[]{DoubleType.INSTANCE}))
        );

        c.addOrder(Order.asc("grantedDate"));
        c.setResultTransformer(Transformers.aliasToBean(MemberLoan.class));
        List<MemberLoan> list = (List<MemberLoan>) c.list();
        for (MemberLoan ml : list) {
            map.put(ml.getGroupPro(), ml.getlSum());
        }
        s.close();
        return map;
    }

    private Map<String, Number> getLoanCollectionData() {
        LocalDate now = LocalDate.now();
        Map<String, Number> map = new HashMap<>();
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(LoanPayment.class);

        ProjectionList pList = Projections.projectionList();
        ClassMetadata lpMeta = s.getSessionFactory().getClassMetadata(LoanPayment.class);
        pList.add(Projections.property(lpMeta.getIdentifierPropertyName()));
        for (String prop : lpMeta.getPropertyNames()) {
            pList.add(Projections.property(prop), prop);
        }
        c.add(Restrictions.between("paymentDate",
                FxUtilsHandler.getDateFrom(now.with(firstDayOfYear())),
                FxUtilsHandler.getDateFrom(now.with(lastDayOfYear()))
        ));
        c.setProjection(pList
                .add(Projections.sqlGroupProjection("DATE_FORMAT(payment_date, '%Y-%m-01') AS groupPro", "groupPro", new String[]{"groupPro"}, new Type[]{StringType.INSTANCE}))
                .add(Projections.sqlProjection("SUM(paid_amt) AS lSum", new String[]{"lSum"}, new Type[]{DoubleType.INSTANCE}))
        );

        c.addOrder(Order.asc("paymentDate"));
        c.setResultTransformer(Transformers.aliasToBean(LoanPayment.class));
        List<LoanPayment> list = (List<LoanPayment>) c.list();
        for (LoanPayment lp : list) {
            map.put(lp.getGroupPro(), lp.getlSum());
        }
        s.close();
        return map;
    }
}
