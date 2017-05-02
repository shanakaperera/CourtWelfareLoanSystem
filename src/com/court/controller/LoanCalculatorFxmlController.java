/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.FxUtilsHandler;
import com.court.handler.LoanCalculationHandler;
import com.court.handler.TextFormatHandler;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class LoanCalculatorFxmlController implements Initializable {

    private ValidationSupport validationSupport;
    @FXML
    private TextField repay_no_txt;
    @FXML
    private ComboBox<String> interest_method_combo;
    @FXML
    private TextField loan_int_txt;
    @FXML
    private ComboBox<String> loan_int_combo;
    @FXML
    private TextField loan_du_txt;
    @FXML
    private ComboBox<String> loan_du_combo;
    @FXML
    private ComboBox<String> repay_cycle_combo;
    @FXML
    private TextField principal_amount_txt;
    @FXML
    private TableView<LoanCalculationHandler.LoanSchedule> loan_schedule_tbl;
    @FXML
    private TableColumn<LoanCalculationHandler.LoanSchedule, Integer> installment_col;
    @FXML
    private TableColumn<LoanCalculationHandler.LoanSchedule, Double> principal_col;
    @FXML
    private TableColumn<LoanCalculationHandler.LoanSchedule, Double> interest_col;
    @FXML
    private TableColumn<LoanCalculationHandler.LoanSchedule, Double> due_col;
    @FXML
    private Label repayment_label;
    @FXML
    private Label interest_label;
    @FXML
    private Label tot_interest_label;
    @FXML
    private Label tot_pay_label;
    @FXML
    private GridPane grid_pane;
    @FXML
    private HBox loan_int_hbox;
    @FXML
    private HBox loan_du_hbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        principal_amount_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        loan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());
        loan_du_txt.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
        bindValidationOnPaneControlFocus(grid_pane);
    }

    @FXML
    private void onCalculateBtnAction(ActionEvent event) throws ParseException {
        registerInputValidation();
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            int loan_int_com = loan_int_combo.getSelectionModel().getSelectedIndex();
            int loan_du_com = loan_du_combo.getSelectionModel().getSelectedIndex();
            int repay_cycle = repay_cycle_combo.getSelectionModel().getSelectedIndex();
            ObservableList<LoanCalculationHandler.LoanSchedule> schedule = null;
            switch (interest_method_combo.getSelectionModel().getSelectedIndex()) {
                case 0:
                    schedule = LoanCalculationHandler.flatRateCalculator(
                            TextFormatHandler.getCurrencyFieldValue(principal_amount_txt),
                            TextFormatHandler.getPercentageFieldValue(loan_int_txt),
                            Integer.parseInt(loan_du_txt.getText()), loan_int_com, loan_du_com, repay_cycle);
                    break;
                case 1:
                    schedule = LoanCalculationHandler.
                            reducingBalanceEqInstallmentsCalculator(
                                    TextFormatHandler.getCurrencyFieldValue(principal_amount_txt),
                                    TextFormatHandler.getPercentageFieldValue(loan_int_txt),
                                    Integer.parseInt(loan_du_txt.getText()), loan_int_com, loan_du_com, repay_cycle);
                    break;
                case 2:
                    schedule = LoanCalculationHandler.compoundInterestCalculator(
                            TextFormatHandler.getCurrencyFieldValue(principal_amount_txt),
                            TextFormatHandler.getPercentageFieldValue(loan_int_txt),
                            Integer.parseInt(loan_du_txt.getText()), loan_int_com, loan_du_com, repay_cycle);
                    break;
            }

            repay_no_txt.setText(loan_du_combo.getSelectionModel()
                    .getSelectedIndex() == 0 ? loan_du_txt.getText()
                            : (Integer.parseInt(loan_du_txt.getText()) * 12) + "");
            initLoanTable(schedule);
            setLoanSummary(schedule != null ? schedule.stream().findFirst().get() : null);
        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Missing Fields !");
            alert_error.setContentText("You have some missing fields left. Move the cursor to the red \"X\""
                    + " sign and find the error.");
            alert_error.show();
        }
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) throws ParseException {
        FxUtilsHandler.clearFields(grid_pane, loan_du_hbox, loan_int_hbox);
        loan_schedule_tbl.getItems().clear();
        resetLabels(repayment_label, interest_label, tot_interest_label, tot_pay_label);

    }

    private void initLoanTable(ObservableList<LoanCalculationHandler.LoanSchedule> lst) {
        installment_col.setCellValueFactory(new PropertyValueFactory<>("inst_no"));
        principal_col.setCellValueFactory(new PropertyValueFactory<>("principal"));
        interest_col.setCellValueFactory(new PropertyValueFactory<>("interest"));
        due_col.setCellValueFactory(new PropertyValueFactory<>("due_amt"));
        //cell value factory to alin text to right
        alignColumns(Pos.CENTER_RIGHT, principal_col, interest_col, due_col);
        loan_schedule_tbl.setItems(lst);
    }

    private void setLoanSummary(LoanCalculationHandler.LoanSchedule schedule) {
        repayment_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(schedule.getInstallment_amount()));
        interest_label.setText(loan_int_txt.getText() + " " + loan_int_combo.getSelectionModel().getSelectedItem());
        tot_interest_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(schedule.getTotal_interest()));
        tot_pay_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(schedule.getTotal_payment()));
    }

    private void resetLabels(Label... labels) {
        labels[0].setText("Rs0.00");
        labels[1].setText("0.00%");
        labels[2].setText("Rs0.00");
        labels[3].setText("Rs0.00");
    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
        validationSupport.registerValidator(interest_method_combo,
                Validator.createEmptyValidator("Interest method Selection required !"));
        validationSupport.registerValidator(loan_int_combo,
                Validator.createEmptyValidator("Selection Required !"));
        validationSupport.registerValidator(loan_du_combo,
                Validator.createEmptyValidator("Selection Required !"));
        validationSupport.registerValidator(loan_du_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(principal_amount_txt,
                Validator.createPredicateValidator((principal) -> {
                    return TextFormatHandler.getCurrencyFieldValue(principal_amount_txt) > 10000d;
                }, "Principal amount should be more than Rs 10000.00 !"));
        validationSupport.registerValidator(loan_int_txt,
                Validator.createPredicateValidator((percentage) -> {
                    return TextFormatHandler.getPercentageFieldValue(loan_int_txt) < 100d;
                }, "Not a valid interest !"));
    }

    private void alignColumns(Pos pos, TableColumn<LoanCalculationHandler.LoanSchedule, Double>... column) {
        for (TableColumn<LoanCalculationHandler.LoanSchedule, Double> tableColumn : column) {

            tableColumn.setCellFactory((col) -> {
                return new TableCell<LoanCalculationHandler.LoanSchedule, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                            setAlignment(pos);
                        }
                    }
                };
            });
        }
    }

    private void bindValidationOnPaneControlFocus(Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            c.focusedProperty().addListener(e -> {
                registerInputValidation();
            });

        }
    }
}
