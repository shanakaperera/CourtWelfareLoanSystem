/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.DocSeqHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.Loan;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class LoanFxmlController implements Initializable {

    private static final String TABLE_NAME = "loan";
    private ValidationSupport validationSupport;
    @FXML
    private GridPane search_grid;
    @FXML
    private TextField loan_search_id_txt;
    @FXML
    private TextField loan_search_name_txt;
    @FXML
    private GridPane save_grid;
    @FXML
    private TextField loan_id_txt;
    @FXML
    private TextField loan_name_txt;
    @FXML
    private ComboBox<String> int_method_combo;
    @FXML
    private HBox loan_int_hbox;
    @FXML
    private TextField loan_int_txt;
    @FXML
    private ComboBox<String> loan_int_combo;
    @FXML
    private HBox loan_due_hbox;
    @FXML
    private TextField loan_due_txt;
    @FXML
    private ComboBox<String> loan_due_combo;
    @FXML
    private HBox loan_repay_hbox;
    @FXML
    private TextField repay_txt;
    @FXML
    private ComboBox<String> repay_combo;
    @FXML
    private TableView<Loan> loan_manage_table;
    @FXML
    private TableColumn<Loan, String> loan_id_col;
    @FXML
    private TableColumn<Loan, String> loan_name_col;
    @FXML
    private TableColumn<Loan, String> loan_inmeth_col;
    @FXML
    private TableColumn<Loan, Double> loan_in_col;
    @FXML
    private TableColumn<Loan, Integer> loan_due_col;
    @FXML
    private Button deactive_btn;
    @FXML
    private Label deactive_label;
    @FXML
    private TableColumn<Loan, Boolean> status_col;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        fillLoanCodeTxt(loan_id_txt);
        loan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());
        loan_due_txt.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());

        ObservableList<Loan> allLoans = getAllLoans();
        initLoanTable(allLoans);
        List<String> loanCodes = allLoans.stream()
                .map(Loan::getLoanId).collect(Collectors.toList());
        List<String> loanNames = allLoans.stream()
                .map(Loan::getLoanName).collect(Collectors.toList());
        TextFields.bindAutoCompletion(loan_search_id_txt, loanCodes);
        TextFields.bindAutoCompletion(loan_search_name_txt, loanNames);
        bindValidationOnPaneControlFocus(save_grid, loan_int_hbox, search_grid, loan_due_hbox, loan_repay_hbox);
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        FilteredList<Loan> filteredLoans = new FilteredList<>(getAllLoans(), p -> true);
        String newValue_id = loan_search_id_txt.getText();
        String newValue_name = loan_search_name_txt.getText();
        searchThroughTableByCode(newValue_id, filteredLoans);
        searchThroughTableByName(newValue_name, filteredLoans);
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(save_grid, loan_int_hbox, search_grid, loan_due_hbox, loan_repay_hbox);
        fillLoanCodeTxt(loan_id_txt);
        FxUtilsHandler.activeDeactiveChildrenControls(true,
                save_grid, loan_int_hbox, loan_due_hbox, loan_repay_hbox);
        FxUtilsHandler.activeBtnAppearanceChange(deactive_btn, true, false);
        deactive_label.setText("");
    }

    @FXML
    private void onNewBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(save_grid, loan_int_hbox, loan_due_hbox, loan_repay_hbox);
        fillLoanCodeTxt(loan_id_txt);
        FxUtilsHandler.activeDeactiveChildrenControls(true,
                save_grid, loan_int_hbox, loan_due_hbox, loan_repay_hbox);
        FxUtilsHandler.activeBtnAppearanceChange(deactive_btn, true, false);
        deactive_label.setText("");
    }

    @FXML
    private void onSaveBtnAction(ActionEvent event) {
        registerInputValidation();
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Loan loan;
            int id = getIdByLoanCode(session, loan_id_txt.getText().trim());
            if (id != 0) {
                loan = (Loan) session.load(Loan.class, id);
            } else {
                loan = new Loan();
            }

            autoDetectRepayCycle();
            loan.setLoanId(loan_id_txt.getText().trim());
            loan.setLoanName(loan_name_txt.getText());
            loan.setInterestMethod(int_method_combo.getSelectionModel().getSelectedItem());
            loan.setLoanInterest(TextFormatHandler.getPercentageFieldValue(loan_int_txt));
            loan.setInterestPer(loan_int_combo.getSelectionModel().getSelectedItem());
            loan.setLoanDuration(Integer.parseInt(loan_due_txt.getText().trim()));
            loan.setDurationPer(loan_due_combo.getSelectionModel().getSelectedItem());
            loan.setRepaymentCycle(repay_combo.getSelectionModel().getSelectedItem());
            loan.setNoOfRepay(Integer.parseInt(repay_txt.getText().trim()));
            loan.setStatus(true);
            session.saveOrUpdate(loan);
            session.getTransaction().commit();
            session.close();

            Alert alert_info = new Alert(Alert.AlertType.INFORMATION);
            alert_info.setTitle("Information");
            alert_info.setHeaderText("Successfully Saved !");
            alert_info.setContentText("You have successfully saved \""
                    + loan_name_txt.getText() + "\"");
            Optional<ButtonType> result = alert_info.showAndWait();
            if (result.get() == ButtonType.OK) {
                FxUtilsHandler.clearFields(save_grid, loan_int_hbox, loan_due_hbox, loan_repay_hbox);
                fillLoanCodeTxt(loan_id_txt);

                ObservableList<Loan> allLoans = getAllLoans();
                initLoanTable(allLoans);
                List<String> loanCodes = allLoans.stream()
                        .map(Loan::getLoanId).collect(Collectors.toList());
                List<String> loanNames = allLoans.stream()
                        .map(Loan::getLoanName).collect(Collectors.toList());
                TextFields.bindAutoCompletion(loan_search_id_txt, loanCodes);
                TextFields.bindAutoCompletion(loan_search_name_txt, loanNames);
            }
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
    private void onDeactiveBtnAction(ActionEvent event) {
        registerInputValidation();
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Loan prfm_action = (Loan) session.load(Loan.class,
                    getIdByLoanCode(session, loan_id_txt.getText()));
            boolean set_status = !prfm_action.isStatus();
            prfm_action.setStatus(set_status);
            session.update(prfm_action);
            session.getTransaction().commit();
            session.close();

            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            String what_happened = set_status ? "Activated" : "Deactivated";
            alert_inf.setHeaderText("Successfully " + what_happened + "!");
            alert_inf.setContentText("You have successfully " + what_happened + " the "
                    + loan_name_txt.getText() + " !");
            Optional<ButtonType> result = alert_inf.showAndWait();
            if (result.get() == ButtonType.OK) {
                //deactivation proccess----------
                ObservableList<Loan> allLoans = getAllLoans();
                initLoanTable(allLoans);
                FxUtilsHandler.activeDeactiveChildrenControls(set_status,
                        save_grid, loan_int_hbox, loan_due_hbox, loan_repay_hbox);
                FxUtilsHandler.activeBtnAppearanceChange(deactive_btn, set_status, false);
            }
        } else {
            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            alert_inf.setHeaderText("Nothing to deactive !");
            alert_inf.setContentText("You have not selected a valid branch to deactivate."
                    + " Select a loan and try again.");
            alert_inf.show();
        }
    }

    @FXML
    private void onDeleteBtnAction(ActionEvent event) {
    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
        validationSupport.registerValidator(int_method_combo,
                Validator.createEmptyValidator("Interest method Selection required !"));
        validationSupport.registerValidator(loan_name_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(loan_int_combo,
                Validator.createEmptyValidator("Selection Required !"));
        validationSupport.registerValidator(loan_due_combo,
                Validator.createEmptyValidator("Selection Required !"));
        validationSupport.registerValidator(repay_combo,
                Validator.createEmptyValidator("Selection Required !"));
        validationSupport.registerValidator(loan_due_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(loan_int_txt,
                Validator.createPredicateValidator((percentage) -> {
                    return TextFormatHandler.getPercentageFieldValue(loan_int_txt) < 100d;
                }, "Not a valid interest !"));
    }

    private void fillLoanCodeTxt(TextField branchCodeField) {
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        Loan ln = (Loan) c.uniqueResult();
        session.close();
        if (ln != null) {
            seqHandler.reqTable(TABLE_NAME, Integer.parseInt(ln.getLoanId().replaceAll("\\D+", "")) + 1);
            branchCodeField.setText(seqHandler.getSeq_code());
        } else {
            seqHandler.reqTable(TABLE_NAME, 0);
            branchCodeField.setText(seqHandler.getSeq_code());
        }

    }

    private int getIdByLoanCode(Session session, String loanCode) {
        Criteria c = session.createCriteria(Loan.class);
        c.add(Restrictions.eq("loanId", loanCode));
        Loan filteredLoan = (Loan) c.uniqueResult();
        return filteredLoan != null ? filteredLoan.getId() : 0;
    }

    private void autoDetectRepayCycle() {
        repay_txt.setText(loan_due_combo.getSelectionModel()
                .getSelectedIndex() == 0 ? loan_due_txt.getText()
                        : (Integer.parseInt(loan_due_txt.getText()) * 12) + "");
    }

    private ObservableList<Loan> getAllLoans() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        List<Loan> lList = c.list();
        ObservableList<Loan> loans = FXCollections.observableArrayList(lList);
        session.close();
        return loans;
    }

    private void initLoanTable(ObservableList<Loan> loans) {
        loan_id_col.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        loan_name_col.setCellValueFactory(new PropertyValueFactory<>("loanName"));
        loan_inmeth_col.setCellValueFactory(new PropertyValueFactory<>("interestMethod"));
        loan_in_col.setCellValueFactory(new PropertyValueFactory<>("loanInterest"));
        loan_due_col.setCellValueFactory(new PropertyValueFactory<>("loanDuration"));
        loan_in_col.setCellFactory((column) -> {
            return new TableCell<Loan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Double> currentRow = getTableRow();
                    if (!isEmpty()) {
                        setText(item + "% " + loans.get(currentRow.getIndex()).getInterestPer());
                    }
                }

            };
        });
        loan_due_col.setCellFactory((column) -> {
            return new TableCell<Loan, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Integer> currentRow = getTableRow();
                    if (!isEmpty()) {
                        setText(item + " " + loans.get(currentRow.getIndex()).getDurationPer());
                    }
                }

            };
        });
        status_col.setCellFactory((column) -> {
            return new TableCell<Loan, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Boolean> currentRow = getTableRow();
                    if (!isEmpty()) {
                        setText(item ? "Active" : "Deactive");
                        currentRow.setStyle(item ? "" : "-fx-background-color:#d9534f");
                    }
                }
            };
        });
        status_col.setCellValueFactory(new PropertyValueFactory<>("status"));

        loan_manage_table.setItems(loans);

        loan_manage_table.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (loan_manage_table.getSelectionModel().getSelectedItem() != null) {
                        Loan selectedLoan = loan_manage_table.getSelectionModel().getSelectedItem();
                        getLoanByCode(selectedLoan.getLoanId());
                    }
                });
    }

    private void getLoanByCode(String loanCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        c.add(Restrictions.eq("loanId", loanCode));
        Loan filteredLoan = (Loan) c.uniqueResult();
        session.close();
        loan_id_txt.setText(filteredLoan.getLoanId());
        loan_name_txt.setText(filteredLoan.getLoanName());
        loan_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.
                format(filteredLoan.getLoanInterest() / 100d));
        loan_due_txt.setText(String.valueOf(filteredLoan.getLoanDuration()));
        repay_txt.setText(String.valueOf(filteredLoan.getNoOfRepay()));
        repay_combo.getSelectionModel().select(filteredLoan.getRepaymentCycle());
        loan_due_combo.getSelectionModel().select(filteredLoan.getDurationPer());
        loan_int_combo.getSelectionModel().select(filteredLoan.getInterestPer());
        int_method_combo.getSelectionModel().select(filteredLoan.getInterestMethod());
        FxUtilsHandler.activeDeactiveChildrenControls(filteredLoan.isStatus(),
                save_grid, loan_int_hbox, loan_due_hbox, loan_repay_hbox);
        FxUtilsHandler.activeBtnAppearanceChange(deactive_btn, filteredLoan.isStatus(), false);
    }

    private void searchThroughTableByCode(String searchText, FilteredList<Loan> filteredLoans) {

        FilteredList<Loan> filtered = filteredLoans.filtered(loan -> {
            if (searchText == null || searchText.isEmpty()) {
                return false;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            if (loan.getLoanId().toLowerCase().equals(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
        if (!filtered.isEmpty()) {
            initLoanTable(filtered);
        }
    }

    private void searchThroughTableByName(String searchText, FilteredList<Loan> filteredLoans) {

        FilteredList<Loan> filtered = filteredLoans.filtered(loan -> {
            if (searchText == null || searchText.isEmpty()) {
                return false;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            if (loan.getLoanName().toLowerCase().equals(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
        if (!filtered.isEmpty()) {
            initLoanTable(filtered);
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
