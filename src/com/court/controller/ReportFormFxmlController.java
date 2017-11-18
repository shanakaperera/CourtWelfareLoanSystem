/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.ReportHandler;
import com.court.model.Branch;
import com.court.model.MemberLoan;
import com.court.model.Member;
import com.court.model.SubscriptionPay;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.controlsfx.control.textfield.TextFields;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;

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
        // TODO
    }

    @FXML
    private void onLoanAction(ActionEvent event) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Loans");
        dialog.setHeaderText("Select Branch");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getBranches(true));

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(bField, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return bField.getText().split("-")[0];
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(b -> {
            String reportPath = "com/court/reports/LoansReport.jasper";
            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(MemberLoan.class, "ml");
            c.createAlias("ml.member", "m");
            c.createAlias("m.branch", "b");
            if (!b.equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("b.branchCode", b));
            }
            List<MemberLoan> list = (List<MemberLoan>) c.list();
            JRBeanCollectionDataSource memberLoanBeanCollection = new JRBeanCollectionDataSource(list);
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Welfare Loan List");
            ReportHandler rh = new ReportHandler(reportPath, map, memberLoanBeanCollection);
            rh.genarateReport();
            rh.viewReport();
            session.close();
        });

    }

    @FXML
    private void onMemberAction(ActionEvent event) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Member List");
        dialog.setHeaderText("Select Branch");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getBranches(true));

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(bField, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return bField.getText().split("-")[0];
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(b -> {
            String reportPath = "com/court/reports/MemberReport.jasper";

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(Member.class, "m");
            c.createAlias("m.branch", "b");
            if (!b.equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("b.branchCode", b));
            }
            List<Member> list = (List<Member>) c.list();
            List<Member> orderedList = list.stream()
                    .sorted(Comparator.comparing(p -> p.getBranch().getBranchCode()))
                    .collect(Collectors.toList());
            JRBeanCollectionDataSource memberBeanCollection = new JRBeanCollectionDataSource(orderedList);
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Welfare Member List");
            ReportHandler rh = new ReportHandler(reportPath, map, memberBeanCollection);
            rh.genarateReport();
            rh.viewReport();
            session.close();
        });
    }

    @FXML
    private void onMemberWiseLoanAction(ActionEvent event) throws JRException {
        String reportPath = "com/court/reports/MemberWiseLoansReport.jasper";
        String subReportPath = "com/court/reports/MemberWiseLoansSubreport.jasper";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        List<Member> list = (List<Member>) c.list();
        JRBeanCollectionDataSource memberLoansBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Member wise loans List");
        JasperReport subReport = (JasperReport) JRLoader.loadObject(
                ClassLoader.getSystemResourceAsStream(subReportPath));
        map.put("SUBREPORT", subReport);
        ReportHandler rh = new ReportHandler(reportPath, map, memberLoansBeanCollection);
        rh.genarateReport();
        rh.viewReport();
        session.close();
    }

    @FXML
    private void onBranchCollectionDueAction(ActionEvent event) throws JRException {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Branch wise Collection Due");
        dialog.setHeaderText("Select Branch");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getBranches(false));

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(bField, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return bField.getText().split("-")[0];
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(b -> {
            String reportPath = "com/court/reports/BranchWiseCollection.jasper";
            Session s = HibernateUtil.getSessionFactory().openSession();
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Branch Wise Collection");
            map.put("p_brcode", b);
            ReportHandler rh = new ReportHandler(reportPath, map, null, con);
            rh.genarateReport();
            rh.viewReport();
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
        TextFields.bindAutoCompletion(bField, getMembers());

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
            String reportPath = "com/court/reports/MbrHistoryReport.jasper";
            String subReportPath_1 = "com/court/reports/MbrHistorySubReport.jasper";
            String subReportPath_2 = "com/court/reports/MbrHistorySubPaymentsReport.jasper";

            try {
                JasperReport subReport_1 = (JasperReport) JRLoader.loadObject(
                        ClassLoader.getSystemResourceAsStream(subReportPath_1));

                JasperReport subReport_2 = (JasperReport) JRLoader.loadObject(
                        ClassLoader.getSystemResourceAsStream(subReportPath_2));

                Session session = HibernateUtil.getSessionFactory().openSession();
                Criteria c = session.createCriteria(Member.class);
                Member filteredM = (Member) c.add(Restrictions.eq("memberId", mId))
                        .uniqueResult();

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
                map.put("payment_office", filteredM.getPaymentOfficer());
                map.put("mbr_status", filteredM.isStatus());
                map.put("branch", filteredM.getBranch().getBranchName());
                map.put("member_loans", filteredM.getMemberLoans());
                map.put("member_subpay", subscriptionPays);
                ReportHandler rh = new ReportHandler(reportPath, map, null);
                rh.genarateReport();
                rh.viewReport();
                session.close();
            } catch (JRException e) {
                e.printStackTrace();
            }

        });
    }

    @FXML
    private void onBranchCollectionMadeAction(ActionEvent event) {

    }

    @FXML
    private void onBranchAction(ActionEvent event) throws JRException {
        String reportPath = "com/court/reports/BranchReport.jasper";
        String subReportPath = "com/court/reports/BranchSubReport.jasper";
        Session s = HibernateUtil.getSessionFactory().openSession();
        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Criteria c = s.createCriteria(Branch.class, "b");
        c.add(Restrictions.eq("b.parentId", 0));
        List<Branch> list = c.list();

        JasperReport subReport = (JasperReport) JRLoader.loadObject(
                ClassLoader.getSystemResourceAsStream(subReportPath));

        JRBeanCollectionDataSource memberLoanBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("report_dbcon", con);
        map.put("reportTitle", "Court List");
        map.put("SUBREPORT", subReport);
        ReportHandler rh = new ReportHandler(reportPath, map, memberLoanBeanCollection);
        rh.genarateReport();
        rh.viewReport();
        s.close();
    }

    @FXML
    private void onMemberSubscriptionAction(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Member Total Subscription");
        dialog.setHeaderText("Select Member");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getMembers());

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
            String reportPath = "com/court/reports/MemberWiseSubscription.jasper";
            Session s = HibernateUtil.getSessionFactory().openSession();
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Member Wise Subscription");
            map.put("member_code", mId);
            ReportHandler rh = new ReportHandler(reportPath, map, null, con);
            rh.genarateReport();
            rh.viewReport();
        });
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
        List<Branch> list = c.list();
        bList.addAll(list);
        s.close();
        return bList;
    }

    /*
    Get All Members for the autocomplete textfield
     */
    private List<Member> getMembers() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Member.class);
        List<Member> list = c.list();
        s.close();
        return list;
    }
}
