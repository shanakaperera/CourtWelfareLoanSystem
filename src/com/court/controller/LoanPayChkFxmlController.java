/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayment;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class LoanPayChkFxmlController implements Initializable {

    @FXML
    private TableColumn<LoanPayment, String> inr_m_id_col;
    @FXML
    private TableColumn<LoanPayment, String> inr_m_name_col;
    @FXML
    private TableColumn<LoanPayment, String> inr_l_id_col;
    @FXML
    private TableColumn<LoanPayment, String> inr_l_name_col;
    @FXML
    private TableColumn<LoanPayment, String> inr_amt_col;
    @FXML
    private TableColumn<LoanPayment, String> inr_ins_col;
    @FXML
    private TableView<LoanPayment> detail_tbl;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void createDetailTable(ObservableList<LoanPayment> payments) {
        inr_m_id_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleStringProperty(lp.getMemberLoan().getMember().getMemberId());
        });
        inr_m_name_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleStringProperty(lp.getMemberLoan().getMember().getNameWithIns());
        });

        inr_l_id_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleStringProperty(lp.getMemberLoan().getMemberLoanCode());
        });

        inr_l_name_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleStringProperty(lp.getMemberLoan().getLoanName());
        });

        inr_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(lp.getMemberLoan().getLoanAmount()));
        });

        inr_ins_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            return new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(lp.getMemberLoan().getLoanInstallment()));
        });

        detail_tbl.setItems(payments);
    }

}
