/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.court.model.SubscriptionPay;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class PaymentsFxmlController implements Initializable {

    @FXML
    private DatePicker from_date_chosr;
    @FXML
    private DatePicker to_date_chosr;
    @FXML
    private GridPane grid_pane;
    @FXML
    private HBox date_box;
    @FXML
    private TableView<LoanPayment> installment_tbl;
    @FXML
    private TableColumn<LoanPayment, String> ins_date_col;
    @FXML
    private TableColumn<LoanPayment, String> ins_amt_col;
    @FXML
    private TableColumn<LoanPayment, String> ins_act_date_col;
    @FXML
    private TableColumn<LoanPayment, String> ins_cheque_no_col;
    @FXML
    private TableColumn<LoanPayment, Button> ins_loan_col;
    @FXML
    private TableColumn<LoanPayment, Button> ins_mbr_col;
    @FXML
    private TableView<SubscriptionPay> subs_tbl;
    @FXML
    private TableColumn<SubscriptionPay, String> sub_date_col;
    @FXML
    private TableColumn<SubscriptionPay, Button> subcription_col;
    @FXML
    private TableColumn<SubscriptionPay, Button> sub_mbr_col;
    @FXML
    private TableColumn<SubscriptionPay, String> subs_cheque_col;
    @FXML
    private TableColumn<SubscriptionPay, String> subs_actual_pay_col;
    @FXML
    private Label inst_tot_label;
    @FXML
    private Label subs_tot_label;
    @FXML
    private Label grand_tot_label;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FxUtilsHandler.setDatePickerTimeFormat(from_date_chosr, to_date_chosr);
    }

    double subs_tot, inst_tot = 0.0;

    private void initPaymentTable(ObservableList<LoanPayment> observableArrayList) {
        ins_date_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleObjectProperty<>(new SimpleDateFormat("MMMMM, yyyy").format(lp.getInstallmentDate()));
        });
        ins_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleObjectProperty<>(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(lp.getPaidAmt().doubleValue()));
        });
        ins_act_date_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleObjectProperty<>(new SimpleDateFormat("yyyy-MM-dd").format(lp.getPaymentDate()));
        });
        ins_cheque_no_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleObjectProperty<>(lp.getChequeNo() != null || !lp.getChequeNo().isEmpty() ? lp.getChequeNo() : "Not a cheque pay");
        });
        ins_loan_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, Button> param) -> {
            LoanPayment lp = param.getValue();
            Button b = new Button("Loan Info");
            b.setOnAction(a -> {
                Node node = createMemberLoanContentGrid(lp.getMemberLoan());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText("Loan Information !");
                alert.getDialogPane().setContent(node);
                alert.show();
            });
            return new SimpleObjectProperty<>(b);
        });
        ins_mbr_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, Button> param) -> {
            LoanPayment lp = param.getValue();
            Button b = new Button(lp.getMemberLoan().getMember().getNameWithIns());
            b.setOnAction(a -> {
                Node node = createMemberContentGrid(lp.getMemberLoan().getMember());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText("Member Information !");
                alert.getDialogPane().setContent(node);
                alert.show();
            });
            return new SimpleObjectProperty<>(b);
        });
        installment_tbl.setItems(observableArrayList);
        inst_tot = bindInstTotalTo(inst_tot_label, installment_tbl);
    }

    private void initSubscriptionTable(ObservableList<SubscriptionPay> observableArrayList) {
        sub_date_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            SubscriptionPay sp = param.getValue();
            return new SimpleObjectProperty<>(new SimpleDateFormat("MMMMM, yyyy").format(sp.getPaymentDate()));
        });
        subcription_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Button> param) -> {
            SubscriptionPay sp = param.getValue();
            double tot = sp.getAciFee() + sp.getHoiFee() + sp.getAdmissionFee() + sp.getMembershipFee() + sp.getSavingsFee() + sp.getOptional();
            Button b = new Button(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot));
            b.setOnAction(a -> {
                Node node = createSubscriptionContentGrid(sp);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText("Subscription Information !");
                alert.getDialogPane().setContent(node);
                alert.show();
            });
            return new SimpleObjectProperty<>(b);
        });
        subs_cheque_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            SubscriptionPay sp = param.getValue();
            return new SimpleObjectProperty<>(sp.getChequeNo() != null || !sp.getChequeNo().isEmpty() ? sp.getChequeNo() : "Not a cheque pay");
        });

        subs_actual_pay_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            SubscriptionPay sp = param.getValue();
            return new SimpleObjectProperty<>(new SimpleDateFormat("yyyy-MM-dd").format(sp.getAddedDate()));
        });

        sub_mbr_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Button> param) -> {
            SubscriptionPay sp = param.getValue();
            Button b = new Button(sp.getMemberSubscriptions().getMember().getNameWithIns());
            b.setOnAction(a -> {
                Node node = createMemberContentGrid(sp.getMemberSubscriptions().getMember());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText("Member Information !");
                alert.getDialogPane().setContent(node);
                alert.show();
            });
            return new SimpleObjectProperty<>(b);
        });

        subs_tbl.setItems(observableArrayList);
        subs_tot = bindSubTotalTo(subs_tot_label, subs_tbl);
        grand_tot_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(subs_tot + inst_tot));
        subs_tot = 0.0;
        inst_tot = 0.0;
    }

    private void onSearchTxtMouseClickAction(MouseEvent event) {
        from_date_chosr.setEditable(false);
        to_date_chosr.setEditable(false);
        from_date_chosr.setValue(null);
        to_date_chosr.setValue(null);
    }

    @FXML
    private void onFromDateMouseClickAction(MouseEvent event) {
        from_date_chosr.setEditable(true);
        to_date_chosr.setEditable(true);
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(grid_pane, date_box);
    }

    @FXML
    private void onToDateMouseClickAction(MouseEvent event) {
        from_date_chosr.setEditable(true);
        to_date_chosr.setEditable(true);
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        performPaymentSearchByDuration(from_date_chosr.getValue(), to_date_chosr.getValue());
        performSubscriptionSearchByDuration(from_date_chosr.getValue(), to_date_chosr.getValue());
    }

    private void performPaymentSearchByDuration(LocalDate from, LocalDate to) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(LoanPayment.class);
        c.add(Restrictions.between("paymentDate",
                FxUtilsHandler.getDateFrom(from), FxUtilsHandler.getDateFrom(to)));

        List<LoanPayment> lPayList = c.list();
        initPaymentTable(FXCollections.observableArrayList(lPayList));
        session.close();
    }

    private void performSubscriptionSearchByDuration(LocalDate from, LocalDate to) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(SubscriptionPay.class);
        c.add(Restrictions.between("addedDate",
                FxUtilsHandler.getDateFrom(from), FxUtilsHandler.getDateFrom(to)));

        List<SubscriptionPay> sPayList = c.list();
        initSubscriptionTable(FXCollections.observableArrayList(sPayList));
        session.close();
    }

    private Node createMemberLoanContentGrid(MemberLoan mbrLoan) {

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        grid.add(new Label("Loan Code : "), 0, 0);
        grid.add(new Label(mbrLoan.getMemberLoanCode()), 1, 0);
        grid.add(new Label("Loan Name : "), 0, 1);
        grid.add(new Label(mbrLoan.getLoanName()), 1, 1);
        grid.add(new Label("Interest Method : "), 0, 2);
        grid.add(new Label(mbrLoan.getInterestMethod()), 1, 2);
        grid.add(new Label("Loan Amount : "), 0, 3);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(mbrLoan.getLoanAmount())), 1, 3);
        grid.add(new Label("Loan Installment : "), 0, 4);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(mbrLoan.getLoanInstallment())), 1, 4);
        grid.add(new Label("Loan Interest : "), 0, 5);
        grid.add(new Label(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(mbrLoan.getLoanInterest() / 100)), 1, 5);

        return grid;
    }

    private Node createMemberContentGrid(Member mbr) {

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        grid.add(new Label("Full Name : "), 0, 0);
        grid.add(new Label(mbr.getFullName()), 1, 0);
        grid.add(new Label("Job Title : "), 0, 1);
        grid.add(new Label(mbr.getJobTitle()), 1, 1);
        grid.add(new Label("Payment Officer : "), 0, 2);
        grid.add(new Label(mbr.getPaymentOfficer()), 1, 2);
        grid.add(new Label("Working Court : "), 0, 3);
        grid.add(new Label(mbr.getBranch().getBranchName()), 1, 3);

        return grid;
    }

    private Node createSubscriptionContentGrid(SubscriptionPay sp) {

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        grid.add(new Label("ACI : "), 0, 0);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sp.getAciFee())), 1, 0);
        grid.add(new Label("HOI : "), 0, 1);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sp.getHoiFee())), 1, 1);
        grid.add(new Label("Savings : "), 0, 2);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sp.getSavingsFee())), 1, 2);
        grid.add(new Label("Membership : "), 0, 3);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sp.getMembershipFee())), 1, 3);
        grid.add(new Label("Optional : "), 0, 4);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sp.getOptional())), 1, 4);
        grid.add(new Label("Admission : "), 0, 5);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sp.getAdmissionFee())), 1, 5);

        return grid;
    }

    double total = 0.0;

    private double bindSubTotalTo(Label label, TableView<SubscriptionPay> tbl) {
        for (int i = 0; i < tbl.getItems().size(); i++) {
            Button btn = (Button) tbl.getColumns().get(1)
                    .getCellObservableValue(i).getValue();
            double value = TextFormatHandler
                    .getCurrencyFieldValue(btn.getText());
            total += value;
        }
        double dd = total;
        label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
        total = 0.0;
        return dd;
    }

    private double bindInstTotalTo(Label label, TableView<LoanPayment> tbl) {
        for (int i = 0; i < tbl.getItems().size(); i++) {
            String val = tbl.getColumns().get(1)
                    .getCellObservableValue(i).getValue().toString();
            double value = TextFormatHandler
                    .getCurrencyFieldValue(val);
            total += value;
        }
        double dd = total;
        label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
        total = 0.0;
        return dd;
    }
}
