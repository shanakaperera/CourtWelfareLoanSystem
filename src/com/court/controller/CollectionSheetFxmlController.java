/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.CheckBoxCellValueFactory;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayCheque;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import java.net.URL;
import java.sql.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class CollectionSheetFxmlController implements Initializable {

    private AutoCompletionBinding<String> bindAutoCompletion;
    private ValidationSupport validationSupport;
    @FXML
    private ComboBox<String> search_typ_combo;
    @FXML
    private TextField search_txt;
    @FXML
    private TableView<MemberLoan> collection_tbl;
    @FXML
    private TableColumn<MemberLoan, Member> m_id_col;
    @FXML
    private TableColumn<MemberLoan, Member> m_name_col;
    @FXML
    private TableColumn<MemberLoan, String> loan_id_col;
    @FXML
    private TableColumn<MemberLoan, String> loan_type_col;
    @FXML
    private TableColumn<MemberLoan, Double> l_amt_col;
    @FXML
    private TableColumn<MemberLoan, Double> l_inst_amt_col;
    @FXML
    private TableColumn<MemberLoan, CheckBox> colection_stat_col;
    @FXML
    private TableColumn<MemberLoan, Integer> inst_no_col;
    @FXML
    private TextField chk_no_txt;
    @FXML
    private DatePicker chk_date_chooser;
    @FXML
    private TextField chk_amt_txt;
    @FXML
    private GridPane collection_grid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        FxUtilsHandler.setDatePickerTimeFormat(chk_date_chooser);
        registerInputValidation();
        chk_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        performSearch(search_typ_combo, search_txt);
        double tot_pay = collection_tbl.getItems().stream()
                .filter(ml -> ml.isStatus() && !ml.isIsComplete())
                .mapToDouble(MemberLoan::getLoanInstallment).sum();

        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot_pay));
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(collection_grid);
        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(0));
        collection_tbl.getItems().clear();
    }

    @FXML
    private void onSearchComboItemSelAction(ActionEvent event) {
        performSearchTextActive(true);
        figureNLoadSearchTextSuggestions(search_typ_combo.getSelectionModel().getSelectedIndex());
    }

    @FXML
    private void onProceedBtnAction(ActionEvent event) {

        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {

            if (collection_tbl.getItems().isEmpty()) {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("No content in the table.");
                alert_error.setContentText("You have not filtered any members to the table. ");
                alert_error.show();
                return;
            }
            double tot_pay = collection_tbl.getItems().stream()
                    .filter(ml -> ml.isStatus() && !ml.isIsComplete())
                    .mapToDouble(MemberLoan::getLoanInstallment).sum();

            if (FxUtilsHandler.roundNumber(tot_pay, 2) != TextFormatHandler.getCurrencyFieldValue(chk_amt_txt)) {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Different cheque amount entered !");
                alert_error.setContentText("Cheque amount you have entered is not"
                        + " match with the required payment (" + TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot_pay) + ")."
                        + "Please try again !");
                alert_error.show();
                return;
            }
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            collection_tbl.getItems().forEach(ml -> {
                LoanPayment getLastPay = ml.getLoanPayments().stream()
                        .filter(p -> p.isIsLast()).findAny().orElse(null);
                if (getLastPay != null) {
                    updatePreviousInstallmentsOfMemberLoan(session, getLastPay);
                    LoanPayment lp = new LoanPayment();
                    lp.setChequeNo(chk_no_txt.getText());
                    lp.setMemberLoan(ml);
                    lp.setPaymentDate(new java.util.Date());
                    lp.setIsLast(true);
                    lp.setInstallmentDue(ml.getNoOfRepay() - (getLastPay.getInstallmentNo() + 1));
                    lp.setPaymentDue(FxUtilsHandler.roundNumber(ml.getLoanInstallment() * (ml.getNoOfRepay() - (getLastPay.getInstallmentNo() + 1)), 2));
                    lp.setInstallmentNo(getLastPay.getInstallmentNo() + 1);
                    session.save(lp);
                    if (ml.getNoOfRepay() == (getLastPay.getInstallmentNo() + 1)) {
                        endLoan(session, ml);
                    }
                } else {
                    LoanPayment lp = new LoanPayment();
                    lp.setChequeNo(chk_no_txt.getText());
                    lp.setMemberLoan(ml);
                    lp.setPaymentDate(new java.util.Date());
                    lp.setIsLast(true);
                    lp.setInstallmentDue(ml.getNoOfRepay() - 1);
                    lp.setPaymentDue(FxUtilsHandler.roundNumber(ml.getLoanInstallment() * (ml.getNoOfRepay() - 1), 2));
                    lp.setInstallmentNo(1);
                    session.save(lp);
                }

            });

            LoanPayCheque payCheque = new LoanPayCheque();
            payCheque.setChequeNo(chk_no_txt.getText());
            payCheque.setChequeDate(Date.valueOf(chk_date_chooser.getValue()));
            payCheque.setChequeAmount(TextFormatHandler.getCurrencyFieldValue(chk_amt_txt));
            session.save(payCheque);

            session.getTransaction().commit();
            session.close();

            Alert alert_error = new Alert(Alert.AlertType.INFORMATION);
            alert_error.setTitle("Information Message");
            alert_error.setHeaderText("Successfully Updated !");
            alert_error.setContentText("You have successfully updated the payments of " + search_txt.getText()
                    + " of this month.");
            Optional<ButtonType> result = alert_error.showAndWait();
            if (result.get() == ButtonType.OK) {
                FxUtilsHandler.clearFields(collection_grid);
                chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(0));
                collection_tbl.getItems().clear();
            }
        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Something wrong with your inputs.");
            alert_error.setContentText("There are some errors with your inputs. Move the cursor to the red \"X\""
                    + " sign and find the error. ");
            alert_error.show();
        }
    }

    private void performSearch(ComboBox<String> search_typ_combo, TextField search_txt) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.createAlias("member", "m")
                .createAlias("member.branch", "b");
        c.add(Restrictions.eq("isComplete", false));
        c.add(Restrictions.eq("status", true));
        int selected = search_typ_combo.getSelectionModel().getSelectedIndex();
        switch (selected) {
            case 0:
                c.add(Restrictions.disjunction()
                        .add(Restrictions.eq("b.branchName", search_txt.getText())));
                break;
            case 1:
                c.add(Restrictions.disjunction()
                        .add(Restrictions.eq("m.memberId", search_txt.getText())));
                break;
            case 2:
                c.add(Restrictions.disjunction()
                        .add(Restrictions.eq("m.nameWithIns", search_txt.getText())));
                break;
        }

        List<MemberLoan> mLoanList = c.list();
        List<MemberLoan> filteredList = mLoanList.stream()
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMember()
                .getMemberId())).collect(Collectors.toList());

        initCollectionTable(filteredList);
        session.close();
    }

    private void initCollectionTable(List<MemberLoan> mLoanList) {

        ObservableList<MemberLoan> mlz = FXCollections.observableArrayList(mLoanList);

        m_id_col.setCellValueFactory(new PropertyValueFactory<>("member"));
        m_name_col.setCellValueFactory(new PropertyValueFactory<>("member"));
        loan_id_col.setCellValueFactory(new PropertyValueFactory<>("memberLoanCode"));
        loan_type_col.setCellValueFactory(new PropertyValueFactory<>("interestMethod"));
        l_amt_col.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        l_inst_amt_col.setCellValueFactory(new PropertyValueFactory<>("loanInstallment"));
        inst_no_col.setCellValueFactory(new PropertyValueFactory<>("installmentNo"));
        colection_stat_col.setCellValueFactory(new CheckBoxCellValueFactory());
        m_id_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Member>() {
                @Override
                protected void updateItem(Member item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(item.getMemberId());
                    }
                }
            };
        });
        m_name_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Member>() {
                @Override
                protected void updateItem(Member item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(item.getNameWithIns());
                    }
                }
            };
        });
        l_amt_col.setCellFactory(column -> {
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
        l_inst_amt_col.setCellFactory(column -> {
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
        inst_no_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        TableRow<MemberLoan> currentRow = getTableRow();

                        if (currentRow.getItem() == null
                                || currentRow.getItem().getLoanPayments().isEmpty()) {
                            setText(String.valueOf(1));
                        } else {
                            int ins_no = currentRow.getItem().getLoanPayments()
                                    .stream().filter(p -> p.isIsLast())
                                    .findFirst().get().getInstallmentNo();
                            setText(String.valueOf(ins_no + 1));
                        }

                    }
                }

            };
        });

        collection_tbl.setItems(mlz);
    }

    private void performSearchTextActive(boolean b) {
        search_txt.setDisable(!b);
        search_txt.setText(null);
    }

    private void figureNLoadSearchTextSuggestions(int selectedIndex) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.createAlias("member", "m")
                .createAlias("member.branch", "b");
        c.add(Restrictions.eq("isComplete", false));
        c.add(Restrictions.eq("status", true));
        switch (selectedIndex) {
            case 0:
                c.setProjection(Projections.property("b.branchName"));
                List<String> bNames = c.list();
                autoCompletionList(bNames);
                break;
            case 1:
                c.setProjection(Projections.property("m.memberId"));
                List<String> mIds = c.list();
                autoCompletionList(mIds);
                break;
            case 2:
                c.setProjection(Projections.property("m.nameWithIns"));
                List<String> mNames = c.list();
                autoCompletionList(mNames);
                break;
        }
        session.close();
    }

    private void autoCompletionList(List<String> lst) {
        if (bindAutoCompletion != null) {
            bindAutoCompletion.dispose();
        }
        bindAutoCompletion = TextFields.bindAutoCompletion(search_txt, new LinkedHashSet<>(lst));
    }

    private void updatePreviousInstallmentsOfMemberLoan(Session session, LoanPayment lp) {

        int getLastUpdated = lp.getId();
        LoanPayment lastPayment = (LoanPayment) session.load(LoanPayment.class, getLastUpdated);
        lastPayment.setIsLast(false);
        session.update(lastPayment);

    }

    private void endLoan(Session session, MemberLoan ml) {
        int getEndingLoan = ml.getId();
        MemberLoan mml = (MemberLoan) session.load(MemberLoan.class, getEndingLoan);
        session.update(mml);
    }

    private void registerInputValidation() {
        validationSupport.registerValidator(chk_amt_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(chk_no_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional !"),
                        Validator.createRegexValidator("Only alphanumeric and hyphen(-) allowed !", "^[a-zA-Z0-9\\-]*$", Severity.ERROR)));
        validationSupport.registerValidator(chk_date_chooser,
                Validator.createEmptyValidator("Check date is required !"));
    }

}
