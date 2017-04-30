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
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TextField chk_no_txt;
    @FXML
    private DatePicker from_date_chosr;
    @FXML
    private DatePicker to_date_chosr;
    @FXML
    private TableView<LoanPayment> payment_tbl;
    @FXML
    private TableColumn<LoanPayment, String> member_id_col;
    @FXML
    private TableColumn<LoanPayment, String> member_name_col;
    @FXML
    private TableColumn<LoanPayment, String> loan_id_col;
    @FXML
    private TableColumn<LoanPayment, String> loan_type_col;
    @FXML
    private TableColumn<LoanPayment, Date> date_col;
    @FXML
    private TableColumn<LoanPayment, String> loan_amt_col;
    @FXML
    private TableColumn<LoanPayment, String> inst_amt_col;
    @FXML
    private TableColumn<LoanPayment, Integer> ins_no_col;
    @FXML
    private GridPane grid_pane;
    @FXML
    private HBox date_box;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FxUtilsHandler.setDatePickerTimeFormat(from_date_chosr, to_date_chosr);
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        if (chk_no_txt.getText() == null
                && (from_date_chosr.getValue() == null || to_date_chosr.getValue() == null)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Something wrong with your inputs.");
            alert_error.setContentText("Please enter cheque number or date duration to filter payments.");
            alert_error.show();
            return;
        }
        if (chk_no_txt.getText() == null) {
            performPaymentSearchByDuration(from_date_chosr.getValue(), to_date_chosr.getValue());
        } else {
            performPaymentSearchByChaque(chk_no_txt.getText());
        }

    }

    private void performPaymentSearchByChaque(String chkNo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(LoanPayment.class);
        c.add(Restrictions.eq("chequeNo", chkNo));
        List<LoanPayment> lPayList = c.list();
        initPaymentTable(FXCollections.observableArrayList(lPayList));
        session.close();
    }

    private void initPaymentTable(ObservableList<LoanPayment> observableArrayList) {

        member_id_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param)
                -> new SimpleStringProperty(param.getValue()
                        .getMemberLoan().getMember().getMemberId()));
        member_name_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param)
                -> new SimpleStringProperty(param.getValue()
                        .getMemberLoan().getMember().getNameWithIns()));
        loan_id_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param)
                -> new SimpleStringProperty(param.getValue()
                        .getMemberLoan().getMemberLoanCode()));
        loan_type_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param)
                -> new SimpleStringProperty(param.getValue()
                        .getMemberLoan().getInterestMethod()));
        date_col.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        loan_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param)
                -> new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                        .format(param.getValue()
                                .getMemberLoan().getLoanAmount())));
        inst_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param)
                -> new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                        .format(param.getValue()
                                .getMemberLoan().getLoanInstallment())));
        ins_no_col.setCellValueFactory(new PropertyValueFactory<>("installmentNo"));

        payment_tbl.setItems(observableArrayList);
    }

    @FXML
    private void onSearchTxtMouseClickAction(MouseEvent event) {
        chk_no_txt.setEditable(true);
        from_date_chosr.setEditable(false);
        to_date_chosr.setEditable(false);
        from_date_chosr.setValue(null);
        to_date_chosr.setValue(null);
    }

    @FXML
    private void onFromDateMouseClickAction(MouseEvent event) {
        chk_no_txt.setEditable(false);
        from_date_chosr.setEditable(true);
        to_date_chosr.setEditable(true);
        chk_no_txt.setText(null);

    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(grid_pane, date_box);
        payment_tbl.getItems().clear();
    }

    @FXML
    private void onToDateMouseClickAction(MouseEvent event) {
        chk_no_txt.setEditable(false);
        from_date_chosr.setEditable(true);
        to_date_chosr.setEditable(true);
        chk_no_txt.setText(null);
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

}
