/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.PropHandler;
import com.court.handler.ReportHandler;
import com.court.model.Branch;
import com.court.model.LoanPayCheque;
import com.court.model.MemberLoan;
import com.court.model.Member;
import com.court.model.ReceiptPay;
import com.court.model.SubscriptionPay;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.controlsfx.control.textfield.TextFields;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class ReportFormFxmlController implements Initializable {

    @FXML
    private Button mbr_loans_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void onLoanAction(ActionEvent event) {

        Dialog<AllLoansReport> dialog = new Dialog<>();
        dialog.setTitle("Loans");
        dialog.setHeaderText("Select Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getBranches(true));
        DatePicker fDate = new DatePicker();
        DatePicker tDate = new DatePicker();

        FxUtilsHandler.setDatePickerTimeFormat(fDate, tDate);

        grid.add(new Label("Office:"), 0, 0);
        grid.add(bField, 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(fDate, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(tDate, 1, 2);
        dialog.getDialogPane().setContent(grid);
        fDate.setValue(LocalDate.now());
        tDate.setValue(LocalDate.now());
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return new AllLoansReport(bField.getText().split("-")[0], fDate.getValue(), tDate.getValue());

            }
            return null;
        });

        Optional<AllLoansReport> result = dialog.showAndWait();
        result.ifPresent(b -> {
            // String reportPath = "com/court/reports/LoansReport.jasper";
            String reportPath = null;
            try {
                reportPath = PropHandler.getStringProperty("report_path") + "LoansReport.jasper";
            } catch (IOException ex) {
                Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(MemberLoan.class, "ml");
            c.createAlias("ml.member", "m");
            c.createAlias("m.branch", "b");
            if (!b.getBranch().equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("b.branchCode", b.getBranch()));
            }
            c.add(Restrictions.between("ml.grantedDate", FxUtilsHandler.getDateFrom(b.getFd()),
                    FxUtilsHandler.getDateFrom(b.getTd())));
            c.addOrder(Order.asc("b.branchName"));

            List<MemberLoan> list = (List<MemberLoan>) c.list();
            JRBeanCollectionDataSource memberLoanBeanCollection = new JRBeanCollectionDataSource(list);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Welfare Loan List Granted From " + sdf.format(FxUtilsHandler.getDateFrom(b.getFd())) + " To " + sdf.format(FxUtilsHandler.getDateFrom(b.getTd())));
            ReportHandler rh = new ReportHandler(reportPath, map, memberLoanBeanCollection);
//            rh.genarateReport();
            boolean blah = rh.genReport();
            if (blah) {
                rh.viewReport();
            }
            session.close();
        });

    }

    @FXML
    private void onMemberAction(ActionEvent event) {

        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Member List");
        dialog.setHeaderText("Select Working Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        ComboBox<String> cbox = new ComboBox<>(FXCollections.observableArrayList("All", "Active", "Deactivated"));
        cbox.getSelectionModel().select(0);
        TextFields.bindAutoCompletion(bField, getBranches(true));

        grid.add(new Label("Office:"), 0, 0);
        grid.add(bField, 1, 0);
        grid.add(new Label("Member Status:"), 0, 1);
        grid.add(cbox, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return new Pair<>(bField.getText(), cbox.getSelectionModel().getSelectedIndex());
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();
        result.ifPresent(b -> {
            //String reportPath = "com/court/reports/MemberReport.jasper";

            String reportPath = null;
            try {
                reportPath = PropHandler.getStringProperty("report_path") + "MemberReport.jasper";
            } catch (IOException ex) {
                Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(Member.class, "m");
            c.createAlias("m.branch", "b");
            if (!b.getKey().equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("b.branchCode", b.getKey().split("-")[0]));
            }
            switch (b.getValue()) {
                case 1:
                    c.add(Restrictions.eq("m.status", true));
                    break;
                case 2:
                    c.add(Restrictions.eq("m.status", false));
                    break;
            }
            List<Member> list = (List<Member>) c.list();
            List<Member> orderedList = list.stream()
                    .sorted(Comparator.comparing(p -> p.getBranch().getBranchCode()))
                    .collect(Collectors.toList());
            JRBeanCollectionDataSource memberBeanCollection = new JRBeanCollectionDataSource(orderedList);
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Welfare Member List Of Working Office - " + b.getKey().split("-")[1]);
            ReportHandler rh = new ReportHandler(reportPath, map, memberBeanCollection);
//            rh.genarateReport();
            boolean blah = rh.genReport();
            if (blah) {
                rh.viewReport();
            }
            session.close();
        });
    }

    @FXML
    private void onMemberPayOfficeAction(ActionEvent event) {

        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Member List");
        dialog.setHeaderText("Select Payment Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        ComboBox<String> cbox = new ComboBox<>(FXCollections.observableArrayList("All", "Active", "Deactivated"));
        cbox.getSelectionModel().select(0);
        TextFields.bindAutoCompletion(bField, getPaymentOffice());

        grid.add(new Label("Office:"), 0, 0);
        grid.add(bField, 1, 0);
        grid.add(new Label("Member Status:"), 0, 1);
        grid.add(cbox, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return new Pair<>(bField.getText(), cbox.getSelectionModel().getSelectedIndex());
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();
        result.ifPresent(b -> {
            //  String reportPath = "com/court/reports/MemberReport.jasper";

            String reportPath = null;
            try {
                reportPath = PropHandler.getStringProperty("report_path") + "MemberReport.jasper";
            } catch (IOException ex) {
                Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(Member.class, "m");
            c.createAlias("m.payOffice", "po");
            c.createAlias("m.branch", "b");
            if (!b.getKey().equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("po.branchCode", b.getKey().split("-")[0]));
            }
            switch (b.getValue()) {
                case 1:
                    c.add(Restrictions.eq("m.status", true));
                    break;
                case 2:
                    c.add(Restrictions.eq("m.status", false));
                    break;
            }

            c.add(Restrictions.eq("b.status", true));
            List<Member> list = (List<Member>) c.list();
            List<Member> orderedList = list.stream()
                    .sorted(Comparator.comparing(p -> p.getBranch().getBranchCode()))
                    .collect(Collectors.toList());
            JRBeanCollectionDataSource memberBeanCollection = new JRBeanCollectionDataSource(orderedList);
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Welfare Member List of Payment Office - " + b.getKey().split("-")[1]);
            ReportHandler rh = new ReportHandler(reportPath, map, memberBeanCollection);
//            rh.genarateReport();
            boolean blah = rh.genReport();
            if (blah) {
                rh.viewReport();
            }
            session.close();
        });
    }

    @FXML
    private void onMemberWiseLoanAction(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Member History");
        dialog.setHeaderText("Select Member");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getMembers(true));

        grid.add(new Label("Member:"), 0, 0);
        grid.add(bField, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return bField.getText().split("-")[0];
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mId -> {
            try {
                // String reportPath = "com/court/reports/MemberWiseLoansReport.jasper";
                // String subReportPath = "com/court/reports/MemberWiseLoansSubreport.jasper";

                String reportPath = null, subReportPath = null;
                try {
                    reportPath = PropHandler.getStringProperty("report_path") + "MemberWiseLoansReport.jasper";
                    subReportPath = PropHandler.getStringProperty("report_path") + "MemberWiseLoansSubreport.jasper";
                } catch (IOException ex) {
                    Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                }

                Session session = HibernateUtil.getSessionFactory().openSession();
                Criteria c = session.createCriteria(Member.class);
                if (!mId.equalsIgnoreCase("All")) {
                    c.add(Restrictions.eq("memberId", mId));
                }
                List<Member> list = (List<Member>) c.list();
                JRBeanCollectionDataSource memberLoansBeanCollection = new JRBeanCollectionDataSource(list);
                Map<String, Object> map = new HashMap<>();
                map.put("companyName", ReportHandler.COMPANY_NAME);
                map.put("companyAddress", ReportHandler.ADDRESS);
                map.put("reportTitle", "Member wise loans List");
                JasperReport subReport = (JasperReport) JRLoader.loadObject(new File(subReportPath));
                map.put("SUBREPORT", subReport);
                ReportHandler rh = new ReportHandler(reportPath, map, memberLoansBeanCollection);
//                rh.genarateReport();
                boolean blah = rh.genReport();
                if (blah) {
                    rh.viewReport();
                }
                session.close();
            } catch (JRException | HibernateException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void onBranchCollectionDueAction(ActionEvent event) throws JRException {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Branch wise Collection Due");
        dialog.setHeaderText("Select Payment Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        ComboBox<String> cbox = new ComboBox<>(FXCollections.observableArrayList("Permanent", "Casual"));
        cbox.getSelectionModel().selectFirst();
        TextFields.bindAutoCompletion(bField, getPaymentOffice());

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(bField, 1, 0);
        grid.add(new Label("Job Status:"), 0, 1);
        grid.add(cbox, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                if (bField.getText().isEmpty()) {
                    return null;
                } else {
                    return new Pair<>(getPayOfficeIdFromCode(bField.getText().split("-")[0]), cbox.getSelectionModel().getSelectedItem());
                }
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(b -> {
            //String reportPath = "com/court/reports/BranchWiseCollection.jasper";

            String reportPath = null;
            try {
                reportPath = PropHandler.getStringProperty("report_path") + "BranchWiseCollection.jasper";
            } catch (IOException ex) {
                Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Session s = HibernateUtil.getSessionFactory().openSession();
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Branch Wise Collection");
            map.put("p_brcode", Integer.parseInt(b.getKey()));
            map.put("job_stat", b.getValue());
            System.out.println("JOB - " + b.getValue());
            ReportHandler rh = new ReportHandler(reportPath, map, null, con);
//            rh.genarateReport();
            boolean blah = rh.genReport();
            if (blah) {
                rh.viewReport();
            }
            s.close();
        });
    }

    @FXML
    private void onMemberHistoryAction(ActionEvent event) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Member History");
        dialog.setHeaderText("Select Member");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getMembers(false));

        grid.add(new Label("Member:"), 0, 0);
        grid.add(bField, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return bField.getText().split("-")[0];
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mId -> {
            // String reportPath = "com/court/reports/MbrHistoryReport.jasper";
            //String subReportPath_1 = "com/court/reports/MbrHistorySubReport.jasper";
            // String subReportPath_2 = "com/court/reports/MbrHistorySubPaymentsReport.jasper";

            String reportPath = null, subReportPath_1 = null, subReportPath_2 = null;
            try {
                reportPath = PropHandler.getStringProperty("report_path") + "MbrHistoryReport.jasper";
                subReportPath_1 = PropHandler.getStringProperty("report_path") + "MbrHistorySubReport.jasper";
                subReportPath_2 = PropHandler.getStringProperty("report_path") + "MbrHistorySubPaymentsReport.jasper";
            } catch (IOException ex) {
                Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                JasperReport subReport_1 = (JasperReport) JRLoader.loadObject(new File(subReportPath_1));

                JasperReport subReport_2 = (JasperReport) JRLoader.loadObject(new File(subReportPath_2));

                Session session = HibernateUtil.getSessionFactory().openSession();
                Criteria c = session.createCriteria(Member.class);
                Member filteredM = (Member) c.add(Restrictions.eq("memberId", mId))
                        .uniqueResult();

                System.out.println("DEEPAL - " + filteredM.getMemberLoans().size());

                Criteria c1 = session.createCriteria(SubscriptionPay.class, "sp");
                c1.createAlias("sp.memberSubscriptions", "ms");
                c1.createAlias("ms.member", "m");
                c1.add(Restrictions.eq("m.memberId", mId));
                List<SubscriptionPay> subscriptionPays = c1.list();

                Map<String, Object> map = new HashMap<>();
                map.put("companyName", ReportHandler.COMPANY_NAME);
                map.put("companyAddress", ReportHandler.ADDRESS);
                map.put("SUBREPORT_1", subReport_1);
                map.put("SUBREPORT_2", subReport_2);
                map.put("reportTitle", "Member History");
                map.put("member_id", filteredM.getMemberId());
                map.put("full_name", filteredM.getFullName());
                map.put("nic", filteredM.getNic());
                map.put("emp_id", filteredM.getEmpId());
                map.put("job_title", filteredM.getJobTitle());
                map.put("payment_office", filteredM.getPayOffice().getBranchName());
                map.put("mbr_status", filteredM.isStatus());
                map.put("branch", filteredM.getBranch().getBranchName());
                map.put("member_loans", filteredM.getMemberLoans());
                map.put("member_subpay", subscriptionPays);
                ReportHandler rh = new ReportHandler(reportPath, map, null);
//                rh.genarateReport();
                boolean blah = rh.genReport();
                if (blah) {
                    rh.viewReport();
                }
                session.close();
            } catch (JRException e) {
                e.printStackTrace();
            }

        });
    }

    @FXML
    private void onBranchCollectionMadeAction(ActionEvent event) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Branch Wise Cheque Payments");
        dialog.setHeaderText("Select Cheque and Branch ");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cField = new TextField();
        TextFields.bindAutoCompletion(cField, getCollectionInvoice());

        grid.add(new Label("Invoice:"), 0, 1);
        grid.add(cField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return cField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(invo -> {
            if ((invo != null && !invo.isEmpty())) {
                // String reportPath = "com/court/reports/BranchWisePaymentMadeReport.jasper";

                String reportPath = null;
                try {
                    reportPath = PropHandler.getStringProperty("report_path") + "BranchWisePaymentMadeReport.jasper";
                } catch (IOException ex) {
                    Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                }

                Session s = HibernateUtil.getSessionFactory().openSession();
                SessionImpl smpl = (SessionImpl) s;
                Connection con = smpl.connection();
                Map<String, Object> map = new HashMap<>();
                map.put("companyName", ReportHandler.COMPANY_NAME);
                map.put("companyAddress", ReportHandler.ADDRESS);
                map.put("reportTitle", "Office Wise Cheque Payments");
                map.put("invo_code", invo);
                ReportHandler rh = new ReportHandler(reportPath, map, null, con);
//                rh.genarateReport();
                boolean blah = rh.genReport();
                if (blah) {
                    rh.viewReport();
                }
                s.close();
            } else {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Empty fields found ! ");
                alert_error.setContentText("You have to enter both Cheque and Office correctly to get the report.");
                alert_error.show();
            }
        });

    }

    @FXML

    private void onBranchAction(ActionEvent event) throws JRException {
        //  String reportPath = "com/court/reports/BranchReport.jasper";
        // String subReportPath = "com/court/reports/BranchSubReport.jasper";

        String reportPath = null, subReportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "BranchReport.jasper";
            subReportPath = PropHandler.getStringProperty("report_path") + "BranchSubReport.jasper";
        } catch (IOException ex) {
            Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Criteria c = s.createCriteria(Branch.class, "b");
        c.add(Restrictions.eq("b.status", true));
        c.add(Restrictions.eq("b.parentId", 0));
        List<Branch> list = c.list();

        JasperReport subReport = (JasperReport) JRLoader.loadObject(new File(subReportPath));

        JRBeanCollectionDataSource memberLoanBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("report_dbcon", con);
        map.put("reportTitle", "Court List");
        map.put("SUBREPORT", subReport);
        ReportHandler rh = new ReportHandler(reportPath, map, memberLoanBeanCollection);
//        rh.genarateReport();
        boolean blah = rh.genReport();
        if (blah) {
            rh.viewReport();
        }
        s.close();
    }

    @FXML
    private void onMemberSubscriptionAction(ActionEvent event) {
        Dialog<SubReport> dialog = new Dialog<>();
        dialog.setTitle("Member Total Subscription");
        dialog.setHeaderText("Select Member");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getMembers(false));

        DatePicker fDate = new DatePicker();
        DatePicker tDate = new DatePicker();
        FxUtilsHandler.setDatePickerTimeFormat(fDate, tDate);

        grid.add(new Label("From:"), 0, 0);
        grid.add(fDate, 1, 0);
        grid.add(new Label("To:"), 0, 1);
        grid.add(tDate, 1, 1);
        grid.add(new Label("Member:"), 0, 2);
        grid.add(bField, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println(tDate.getValue());
                String to = tDate.getValue() != null ? sdf.format(FxUtilsHandler.getDateFrom(tDate.getValue())) : "9999-12-12";
                String from = fDate.getValue() != null ? sdf.format(FxUtilsHandler.getDateFrom(fDate.getValue())) : "1000-01-01";
                return new SubReport(bField.getText().split("-")[0], to, from);
            }
            return null;
        });

        Optional<SubReport> result = dialog.showAndWait();
        result.ifPresent(mId -> {
            //  String reportPath = "com/court/reports/MemberWiseSubscription.jasper";

            String reportPath = null;
            try {
                reportPath = PropHandler.getStringProperty("report_path") + "MemberWiseSubscription.jasper";
            } catch (IOException ex) {
                Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Session s = HibernateUtil.getSessionFactory().openSession();
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Member Wise Subscription");
            map.put("member_code", mId.getCode());
            map.put("from_date", mId.getfDate());
            map.put("to_date", mId.gettDate());
            ReportHandler rh = new ReportHandler(reportPath, map, null, con);
//            rh.genarateReport();
            boolean blah = rh.genReport();
            if (blah) {
                rh.viewReport();
            }
            s.close();
        });
    }

    @FXML
    private void onReceiptPaymentMadeAction(ActionEvent event) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Receipt Payment");
        dialog.setHeaderText("Select Receipt No and Payment Type");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField tfp = new TextField();
        TextField tfr = new TextField();
        tfr.setDisable(true);
        TextFields.bindAutoCompletion(tfp, "Subscription", "Installment").setOnAutoCompleted(e -> {
            tfr.setDisable(false);
            TextFields.bindAutoCompletion(tfr, getReceiptCodes(e.getCompletion()));
        });

        grid.add(new Label("Payment Type:"), 0, 0);
        grid.add(tfp, 1, 0);
        grid.add(new Label("Receipt No:"), 0, 1);
        grid.add(tfr, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return new Pair<>(tfp.getText(), tfr.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(invo -> {

            String reportPath = null;
            if (tfp.getText().equalsIgnoreCase("subscription")) {
                // reportPath = "com/court/reports/SubscriptionPayDoneInvoiceReport.jasper";
                try {
                    reportPath = PropHandler.getStringProperty("report_path") + "SubscriptionPayDoneInvoiceReport.jasper";
                } catch (IOException ex) {
                    Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                //reportPath = "com/court/reports/InstallmentPayDoneInvoiceReport.jasper";
                try {
                    reportPath = PropHandler.getStringProperty("report_path") + "InstallmentPayDoneInvoiceReport.jasper";
                } catch (IOException ex) {
                    Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Session s = HibernateUtil.getSessionFactory().openSession();
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Receipt Payments - " + tfp.getText());
            map.put("rcpt_code", invo.getValue());
            map.put("pay_type", invo.getKey());

            ReportHandler rh = new ReportHandler(reportPath, map, null, con);
//            rh.genarateReport();
            boolean blah = rh.genReport();
            if (blah) {
                rh.viewReport();
            }
            s.close();
        });
    }

    @FXML
    private void onWelfareLoansAction(ActionEvent event) {
        // String reportPath = "com/court/reports/LoanManagementReport.jasper";

        String reportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "LoanManagementReport.jasper";
        } catch (IOException ex) {
            Logger.getLogger(ReportFormFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Welfare Loan List");
        ReportHandler rh = new ReportHandler(reportPath, map, null, con);
//        rh.genarateReport();
        boolean blah = rh.genReport();
        if (blah) {
            rh.viewReport();
        }
        s.close();
    }

    /*
    Get All Branches for the autocomplete textfield
     */
    private List<Branch> getBranches(boolean withAll) {
        List<Branch> bList = new ArrayList<>();
        if (withAll) {
            bList.add(new Branch(null, "All"));
        }
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Branch.class);
        c.add(Restrictions.eq("status", true));
        c.setProjection(Projections.projectionList()
                .add(Projections.property("branchCode"), "branchCode")
                .add(Projections.property("branchName"), "branchName")
        );
        c.setResultTransformer(Transformers.aliasToBean(Branch.class));
        List<Branch> list = c.list();
        bList.addAll(list);
        s.close();
        return bList;
    }

    /*
    Get All Payment offices for the autocomplete textfield
     */
    private List<Branch> getPaymentOffice() {
        List<Branch> bList = new ArrayList<>();

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Branch.class);
        c.add(Restrictions.eq("status", true));
        c.add(Restrictions.eq("parentId", 0));
        c.setProjection(Projections.projectionList()
                .add(Projections.property("branchCode"), "branchCode")
                .add(Projections.property("branchName"), "branchName")
        );
        c.setResultTransformer(Transformers.aliasToBean(Branch.class));
        List<Branch> list = c.list();
        bList.addAll(list);
        s.close();
        return bList;
    }

    /*
    Get All Members for the autocomplete textfield
     */
    private List<Member> getMembers(boolean withAll) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Member.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName")
        );
        c.setResultTransformer(Transformers.aliasToBean(Member.class));
        List<Member> list = c.list();
        if (withAll) {
            list.add(new Member("All"));
        }
        s.close();
        return list;
    }

    private List<LoanPayCheque> getCollectionInvoice() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(LoanPayCheque.class);
        c.setProjection(Projections.property("invoCode"));
        c.setResultTransformer(Transformers.aliasToBean(LoanPayCheque.class));
        List<LoanPayCheque> list = c.list();
        s.close();
        return list;
    }

    private String getPayOfficeIdFromCode(String code) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Branch.class);
        Branch bb = (Branch) c.add(Restrictions.eq("branchCode", code))
                .uniqueResult();
        return String.valueOf(bb.getId());
    }

    private List<String> getReceiptCodes(String rptType) {

        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(ReceiptPay.class);
        c.add(Restrictions.eq("paymentType", rptType));
        c.setProjection(Projections.property("receiptCode"));
        List<String> list = c.list();
        return list;

    }

    class SubReport {

        private String code;
        private String tDate;
        private String fDate;

        SubReport(String code, String tDate, String fDate) {
            this.code = code;
            this.tDate = tDate;
            this.fDate = fDate;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String name) {
            this.code = name;
        }

        public String gettDate() {
            return tDate;
        }

        public void settDate(String tDate) {
            this.tDate = tDate;
        }

        public String getfDate() {
            return fDate;
        }

        public void setfDate(String fDate) {
            this.fDate = fDate;
        }

    }

    class AllLoansReport {

        private String branch;
        private LocalDate fd;
        private LocalDate td;

        public AllLoansReport(String branch, LocalDate fd, LocalDate td) {
            this.branch = branch;
            this.fd = fd;
            this.td = td;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public LocalDate getFd() {
            return fd;
        }

        public void setFd(LocalDate fd) {
            this.fd = fd;
        }

        public LocalDate getTd() {
            return td;
        }

        public void setTd(LocalDate td) {
            this.td = td;
        }

    }
}
