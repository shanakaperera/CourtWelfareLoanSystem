/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayment;
import com.court.model.MemberLoan;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class DisplayLoansTableFxmlController implements Initializable {

    @FXML
    private TableColumn<MemberLoan, String> loan_id_col;
    @FXML
    private TableColumn<MemberLoan, Set<LoanPayment>> ins_no_col;
    @FXML
    private TableColumn<MemberLoan, String> ln_type_col;
    @FXML
    private TableColumn<MemberLoan, Double> ln_amt_col;
    @FXML
    private TableColumn<MemberLoan, Double> ln_inst_col;
    @FXML
    private TableColumn<MemberLoan, Double> ln_int_col;
    @FXML
    private Label tot_inst_lbl;
    @FXML
    private TableView<MemberLoan> member_ln_tbl;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setTot_inst(String tot_ins) {
        this.tot_inst_lbl.setText(tot_ins);
    }

    public void createTableView(List<MemberLoan> mList) {
        ObservableList<MemberLoan> mlz = FXCollections.observableArrayList(mList);
        loan_id_col.setCellValueFactory(new PropertyValueFactory<>("memberLoanCode"));
        ln_type_col.setCellValueFactory(new PropertyValueFactory<>("interestMethod"));
        ln_amt_col.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        ln_inst_col.setCellValueFactory(new PropertyValueFactory<>("loanInstallment"));
        ln_int_col.setCellValueFactory(new PropertyValueFactory<>("loanInterest"));
        ins_no_col.setCellValueFactory(new PropertyValueFactory<>("loanPayments"));
        ln_amt_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                    }
                }
            };
        });

        ln_inst_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                    }
                }
            };
        });

        ln_int_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(item / 100));
                    }
                }
            };
        });

        ins_no_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Set<LoanPayment>>() {
                @Override
                protected void updateItem(Set<LoanPayment> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        if (!item.isEmpty()) {
                            setText(String.valueOf(item.stream().filter(p -> p.isIsLast()).findFirst().get().getInstallmentNo() + 1));
                        } else {
                            setText(String.valueOf(1));
                        }
                    }
                }

            };
        });

        member_ln_tbl.setItems(mlz);
    }

}
