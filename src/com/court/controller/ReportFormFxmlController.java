/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.ReportHandler;
import com.court.model.Branch;
import com.court.model.LoanPayCheque;
import com.court.model.MemberLoan;
import com.court.model.Member;
import com.court.model.SubscriptionPay;
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
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
    }

    @FXML
    private void onLoanAction(ActionEvent event) {

        Dialog<AllLoansReport> dialog = new Dialog<>();
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
        DatePicker fDate = new DatePicker();
        DatePicker tDate = new DatePicker();

        FxUtilsHandler.setDatePickerTimeFormat(fDate, tDate);

        grid.add(new Label("Branch:"), 0, 0);
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
            String reportPath = "com/court/reports/LoansReport.jasper";
            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(MemberLoan.class, "ml");
            c.createAlias("ml.member", "m");
            c.createAlias("m.branch", "b");
            if (!b.getBranch().equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("b.branchCode", b.getBranch()));
            }
            c.add(Restrictions.between("ml.grantedDate", FxUtilsHandler.getDateFrom(b.getFd()),
                    FxUtilsHandler.getDateFrom(b.getTd())));

            List<MemberLoan> list = (List<MemberLoan>) c.list();
            JRBeanCollectionDataSource memberLoanBeanCollection = new JRBeanCollectionDataSource(list);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Welfare Loan List Granted From " + sdf.format(FxUtilsHandler.getDateFrom(b.getFd())) + " To " + sdf.format(FxUtilsHandler.getDateFrom(b.getTd())));
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
        dialog.setHeaderText("Select Working Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getBranches(true));

        grid.add(new Label("Office:"), 0, 0);
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
            map.put("reportTitle", "Welfare Member List Of Working Office - " + b);
            ReportHandler rh = new ReportHandler(reportPath, map, memberBeanCollection);
            rh.genarateReport();
            rh.viewReport();
            session.close();
        });
    }

    @FXML
    private void onMemberPayOfficeAction(ActionEvent event) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Member List");
        dialog.setHeaderText("Select Payment Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getPaymentOffice());

        grid.add(new Label("Office:"), 0, 0);
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
            c.createAlias("m.payOffice", "po");
            c.createAlias("m.branch", "b");
            if (!b.equalsIgnoreCase("All")) {
                c.add(Restrictions.eq("po.branchCode", b));
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
            map.put("reportTitle", "Welfare Member List of Payment Office - " + b);
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
        dialog.setHeaderText("Select Payment Office");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextFields.bindAutoCompletion(bField, getPaymentOffice());

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(bField, 1, 0);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return getPayOfficeIdFromCode(bField.getText().split("-")[0]);
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
            map.put("p_brcode", Integer.parseInt(b));
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

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Branch Wise Cheque Payments");
        dialog.setHeaderText("Select Cheque and Branch ");
        ButtonType viewBtn = new ButtonType("View Report", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bField = new TextField();
        TextField cField = new TextField();
        TextFields.bindAutoCompletion(bField, getBranches(false));
        TextFields.bindAutoCompletion(cField, getCheques());

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(bField, 1, 0);
        grid.add(new Label("Cheque:"), 0, 1);
        grid.add(cField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(db -> {
            if (db == viewBtn) {
                return new Pair<>(bField.getText().split("-")[0], cField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(outs -> {
            if ((outs.getKey() != null && !outs.getKey().isEmpty()) && outs.getValue() != null && !outs.getValue().isEmpty()) {
                String reportPath = "com/court/reports/BranchWisePaymentMadeReport.jasper";
                Session s = HibernateUtil.getSessionFactory().openSession();
                SessionImpl smpl = (SessionImpl) s;
                Connection con = smpl.connection();
                Map<String, Object> map = new HashMap<>();
                map.put("companyName", ReportHandler.COMPANY_NAME);
                map.put("companyAddress", ReportHandler.ADDRESS);
                map.put("reportTitle", "Branch Wise Cheque Payments");
                map.put("br_code", outs.getKey());
                map.put("cheque_no", outs.getValue());
                ReportHandler rh = new ReportHandler(reportPath, map, null, con);
                rh.genarateReport();
                rh.viewReport();
            } else {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Empty fields found ! ");
                alert_error.setContentText("You have to enter both Cheque and Branch correctly to get the report.");
                alert_error.show();
            }
        });

    }

    @FXML

    private void onBranchAction(ActionEvent event) throws JRException {
        String reportPath = "com/court/reports/BranchReport.jasper";
        String subReportPath = "com/court/reports/BranchSubReport.jasper";
        Session s = HibernateUtil.getSessionFactory().openSession();
        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Criteria c = s.createCriteria(Branch.class, "b");
        c.add(Restrictions.eq("b.status", true));
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
        c.add(Restrictions.eq("status", true));
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

    private List<LoanPayCheque> getCheques() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(LoanPayCheque.class);
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
