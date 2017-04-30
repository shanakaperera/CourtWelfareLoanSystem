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
import com.court.model.Member;
import com.court.model.MemberLoan;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.textfield.TextFields;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class AssignNewLoanFxmlController implements Initializable {

    private static final String TABLE_NAME = "member_loan";
    @FXML
    private TextField loan_id_txt;
    @FXML
    private TextField loan_name_txt;
    @FXML
    private TextField principal_amt_txt;
    @FXML
    private ComboBox<String> int_method_combo;
    @FXML
    private TextField loan_int_txt;
    @FXML
    private ComboBox<String> loan_int_combo;
    @FXML
    private TextField loan_due_txt;
    @FXML
    private ComboBox<String> loan_due_combo;
    @FXML
    private ComboBox<String> repay_combo;
    @FXML
    private TextField repay_txt;
    @FXML
    private Button apply_btn;
    @FXML
    private Button cancel_btn;
    @FXML
    private Label error_label;
    @FXML
    private GridPane main_grid;
    @FXML
    private ListView<String> guarantor_list;
    @FXML
    private TextField search_txt;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<Loan> allLoans = getAllLoans();
        List<String> loanCodes = allLoans.stream()
                .map(Loan::getLoanId).collect(Collectors.toList());
        List<String> loanNames = allLoans.stream()
                .map(Loan::getLoanName).collect(Collectors.toList());

        loan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());
        loan_due_txt.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
        principal_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());

        TextFields.bindAutoCompletion(loan_id_txt, loanCodes).setOnAutoCompleted(e -> {
            getLoanByCode(loan_id_txt.getText());
        });
        TextFields.bindAutoCompletion(loan_name_txt, loanNames).setOnAutoCompleted(e -> {
        });

    }

    @FXML
    private void onResetBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(main_grid);
    }

    public TextField getLoan_id_txt() {
        return loan_id_txt;
    }

    public TextField getLoan_name_txt() {
        return loan_name_txt;
    }

    public TextField getPrincipal_amt_txt() {
        return principal_amt_txt;
    }

    public ComboBox<String> getInt_method_combo() {
        return int_method_combo;
    }

    public TextField getLoan_int_txt() {
        return loan_int_txt;
    }

    public ComboBox<String> getLoan_int_combo() {
        return loan_int_combo;
    }

    public TextField getLoan_due_txt() {
        return loan_due_txt;
    }

    public ComboBox<String> getLoan_due_combo() {
        return loan_due_combo;
    }

    public ComboBox<String> getRepay_combo() {
        return repay_combo;
    }

    public TextField getRepay_txt() {
        return repay_txt;
    }

    public Button getApply_btn() {
        return apply_btn;
    }

    public Button getCancel_btn() {
        return cancel_btn;
    }

    public Label getError_label() {
        return error_label;
    }

    public ListView<String> getGuarantor_list() {
        return guarantor_list;
    }

    public TextField getSearch_txt() {
        return search_txt;
    }

    private ObservableList<Loan> getAllLoans() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        List<Loan> lList = c.list();
        ObservableList<Loan> loans = FXCollections.observableArrayList(lList);
        session.close();
        return loans;
    }

    public ObservableList<Member> getAllMembers(String except) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        c.add(Restrictions.ne("memberId", except));
        List<Member> mList = c.list();
        ObservableList<Member> members = FXCollections.observableArrayList(mList);
        session.close();
        return members;
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
    }

    public String fillMemberLoanCodeTxt() {
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        MemberLoan ln = (MemberLoan) c.uniqueResult();
        session.close();
        if (ln != null) {
            seqHandler.reqTable(TABLE_NAME, Integer.parseInt(ln.getMemberLoanCode().replaceAll("\\D+", "")) + 1);
            return seqHandler.getSeq_code();
        } else {
            seqHandler.reqTable(TABLE_NAME, 0);
            return seqHandler.getSeq_code();
        }

    }

    @FXML
    private void onGuarantorAddAction(ActionEvent event) {
        if (!guarantor_list.getItems()
                .stream()
                .anyMatch(p -> p.equals(search_txt.getText())) && !search_txt.getText().trim().isEmpty()) {
            guarantor_list.getItems().add(search_txt.getText());
        }
    }

    @FXML
    private void onGuarantorRemoveAction(ActionEvent event) {
        if (!guarantor_list.getSelectionModel().getSelectedItems().isEmpty()) {
            guarantor_list.getItems()
                    .remove(guarantor_list.getSelectionModel().getSelectedIndex());
        }
    }

}
