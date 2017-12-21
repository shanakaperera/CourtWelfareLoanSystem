/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.DocSeqHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.LoanCalculationHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.Loan;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

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
    private ListView<Member> guarantor_list;
    @FXML
    private TextField search_txt;
    @FXML
    private GridPane child_loan_grid;
    @FXML
    private TextField c_loan_id_txt;
    @FXML
    private TextField c_loan_name_txt;
    @FXML
    private TextField c_principal_amt_txt;
    @FXML
    private TextField c_repay_txt;
    @FXML
    private TextField c_loan_int_txt;
    @FXML
    private TextField c_loan_due_txt;
    @FXML
    private ComboBox<String> c_int_method_combo;
    @FXML
    private ComboBox<String> c_loan_int_combo;
    @FXML
    private ComboBox<String> c_loan_due_combo;
    @FXML
    private ComboBox<String> c_repay_combo;
    @FXML
    private CheckBox child_loan_checkbox;
    private ObservableList<Loan> allLoans;
    private ValidationSupport va, vs;

    private Member member;
    SuggestionProvider<String> p1, p3;
    SuggestionProvider<Member> p2;
    private MemberfxmlController mCtr;
    private final int UNIQUE_GUR_FRQUENCY = 3;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initFunction(MemberfxmlController m_ctr) {
        this.mCtr = m_ctr;
        allLoans = getAllLoans();
        List<String> loanCodes = allLoans.stream()
                .map(Loan::getLoanId).collect(Collectors.toList());
        List<String> loanNames = allLoans.stream()
                .map(Loan::getLoanName).collect(Collectors.toList());

        loan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());
        loan_due_txt.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
        principal_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());

        c_loan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());
        c_loan_due_txt.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
        c_principal_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());

        p1 = SuggestionProvider.create(loanCodes);
        p2 = SuggestionProvider.create(getAvailableGuarantors());
        p3 = SuggestionProvider.create(loanNames);

        AutoCompletionTextFieldBinding<String> ab1 = new AutoCompletionTextFieldBinding<>(loan_id_txt, p1);
        AutoCompletionTextFieldBinding<Member> ab2 = new AutoCompletionTextFieldBinding<>(search_txt, p2);
        AutoCompletionTextFieldBinding<String> ab3 = new AutoCompletionTextFieldBinding<>(loan_name_txt, p3);

        ab1.setOnAutoCompleted(e -> {
            getLoanByCode(loan_id_txt.getText());
        });

        ab2.setOnAutoCompleted(e -> {
            getMemberByMemberId(search_txt.getText().split("-")[0].trim());
        });

        ab3.setOnAutoCompleted(e -> {
            getLoanByName(loan_name_txt.getText());
        });

        FxUtilsHandler.disableFields(true, child_loan_grid);
    }

    public Button getCancel_btn() {
        return cancel_btn;
    }

    public void setCancel_btn(Button cancel_btn) {
        this.cancel_btn = cancel_btn;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @FXML
    private void onResetBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(main_grid, child_loan_grid);
    }

    private ObservableList<Loan> getAllLoans() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        c.add(Restrictions.eq("status", true));
        c.setProjection(Projections.projectionList()
                .add(Projections.property("loanId"), "loanId")
                .add(Projections.property("loanName"), "loanName")
        );
        c.setResultTransformer(Transformers.aliasToBean(Loan.class));
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

    private void getChildLoanByCode(String loanCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        c.add(Restrictions.eq("loanId", loanCode));
        Loan filteredLoan = (Loan) c.uniqueResult();
        session.close();
        c_loan_id_txt.setText(filteredLoan.getLoanId());
        c_loan_name_txt.setText(filteredLoan.getLoanName());
        c_loan_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.
                format(filteredLoan.getLoanInterest() / 100d));
        c_loan_due_txt.setText(String.valueOf(filteredLoan.getLoanDuration()));
        c_repay_txt.setText(String.valueOf(filteredLoan.getNoOfRepay()));
        c_repay_combo.getSelectionModel().select(filteredLoan.getRepaymentCycle());
        c_loan_due_combo.getSelectionModel().select(filteredLoan.getDurationPer());
        c_loan_int_combo.getSelectionModel().select(filteredLoan.getInterestPer());
        c_int_method_combo.getSelectionModel().select(filteredLoan.getInterestMethod());
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
        String mbrId = search_txt.getText().split("-")[0].trim();
        if (!guarantor_list.getItems()
                .stream()
                .anyMatch(p -> p.getMemberId().equalsIgnoreCase(mbrId)) && !search_txt.getText().trim().isEmpty()) {
            guarantor_list.getItems().add(getMemberByMemberId(mbrId));
            search_txt.setText("");
        }
    }

    @FXML
    private void onGuarantorRemoveAction(ActionEvent event) {
        if (!guarantor_list.getSelectionModel().getSelectedItems().isEmpty()) {
            guarantor_list.getItems()
                    .remove(guarantor_list.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    private void hasChildComboAction(ActionEvent event) {
        if (loan_id_txt.getText().isEmpty()) {
            error_label.setStyle("-fx-text-fill: #d32323;");
            error_label.setText("You have to fill first loan part before the second part !");
            child_loan_checkbox.setSelected(false);
            return;
        }
        if (child_loan_checkbox.isSelected()) {
            FxUtilsHandler.disableFields(false, child_loan_grid);
            FxUtilsHandler.clearFields(child_loan_grid);
            bindChildLoans(c_loan_id_txt);
            registerSecondPartValidation();
        } else {
            unregisterSeconPartValidation();
            FxUtilsHandler.disableFields(true, child_loan_grid);
            FxUtilsHandler.clearFields(child_loan_grid);
        }
    }

    private void bindChildLoans(TextField c_loan_id_txt) {

        List<String> loanCodes = allLoans.stream().filter((p -> !p.getLoanId().equalsIgnoreCase(loan_id_txt.getText())))
                .map(Loan::getLoanId).collect(Collectors.toList());
        TextFields.bindAutoCompletion(c_loan_id_txt, loanCodes).setOnAutoCompleted(e -> {
            getChildLoanByCode(c_loan_id_txt.getText());
        });
    }

    @FXML
    private void onApplyBtnAction(ActionEvent event) throws MalformedURLException {

        if (!(guarantor_list.getItems().size() >= 1)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText("Should have at least one guarantor for each loan !");
            alert_error.show();
            return;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String PARENT = loan_name_txt.getText();
        MemberLoan ml = new MemberLoan();
        ml.setMemberLoanCode(fillMemberLoanCodeTxt());
        ml.setMember(getMember());
        ml.setLoanName(PARENT);
        ml.setGrantedDate(new java.util.Date());
        ml.setGuarantors(new Gson().toJson(guarantor_list.getItems().stream().map(Member::getMemberId).collect(Collectors.toList()), new TypeToken<List<String>>() {
        }.getType()));
        ml.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(principal_amt_txt));
        ml.setLoanInterest(TextFormatHandler.getPercentageFieldValue(loan_int_txt));
        ml.setInterestPer(loan_int_combo.getSelectionModel().getSelectedItem());
        ml.setInterestMethod(int_method_combo.getSelectionModel().getSelectedItem());
        ml.setLoanDuration(Integer.parseInt(loan_due_txt.getText()));
        ml.setDurationPer(loan_due_combo.getSelectionModel().getSelectedItem());
        ml.setRepaymentCycle(repay_combo.getSelectionModel().getSelectedItem());
        ml.setNoOfRepay(Integer.parseInt(repay_txt.getText()));
        ml.setLoanInstallment(getInstallmentAccordingToLoanType(
                loan_int_combo.getSelectionModel().getSelectedItem(),
                loan_due_combo.getSelectionModel().getSelectedItem(),
                TextFormatHandler.getCurrencyFieldValue(principal_amt_txt),
                TextFormatHandler.getPercentageFieldValue(loan_int_txt),
                Integer.parseInt(repay_txt.getText()),
                int_method_combo.getSelectionModel().getSelectedItem()));
        ml.setIsComplete(false);
        ml.setStatus(true);

        if (child_loan_checkbox.isSelected()) {
            if (va.validationResultProperty().get().getErrors().isEmpty() && vs.validationResultProperty().get().getErrors().isEmpty()) {
                String CHILD = c_loan_name_txt.getText();
                MemberLoan ml2 = new MemberLoan();
                ml2.setMemberLoanCode(fillMemberLoanCodeTxt());
                ml2.setMember(getMember());
                ml2.setLoanName(CHILD);
                ml2.setGrantedDate(new java.util.Date());
                ml2.setGuarantors(new Gson().toJson(guarantor_list.getItems().stream().map(Member::getMemberId).collect(Collectors.toList()), new TypeToken<List<String>>() {
                }.getType()));
                ml2.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(c_principal_amt_txt));
                ml2.setLoanInterest(TextFormatHandler.getPercentageFieldValue(c_loan_int_txt));
                ml2.setInterestPer(c_loan_int_combo.getSelectionModel().getSelectedItem());
                ml2.setInterestMethod(c_int_method_combo.getSelectionModel().getSelectedItem());
                ml2.setLoanDuration(Integer.parseInt(c_loan_due_txt.getText()));
                ml2.setDurationPer(c_loan_due_combo.getSelectionModel().getSelectedItem());
                ml2.setRepaymentCycle(c_repay_combo.getSelectionModel().getSelectedItem());
                ml2.setNoOfRepay(Integer.parseInt(c_repay_txt.getText()));
                ml2.setLoanInstallment(getInstallmentAccordingToLoanType(c_loan_int_combo.getSelectionModel().getSelectedItem(),
                        c_loan_due_combo.getSelectionModel().getSelectedItem(),
                        TextFormatHandler.getCurrencyFieldValue(c_principal_amt_txt),
                        TextFormatHandler.getPercentageFieldValue(c_loan_int_txt),
                        Integer.parseInt(c_repay_txt.getText()),
                        c_int_method_combo.getSelectionModel().getSelectedItem()));
                ml2.setIsComplete(false);
                ml2.setStatus(true);
                ml2.setChildId(0);
                ml2.setIsChild(true);
                session.save(ml2);

                ml.setHasChild(true);
                ml.setChildId(ml2.getId());
                session.save(ml);
                session.getTransaction().commit();
                session.close();
                mCtr.showSuccessAlert();
//                error_label.setStyle("-fx-text-fill: #349a46;");
//                error_label.setText("Successfully assigned the loan.");
//                apply_btn.setDisable(true);
            } else {
                error_label.setStyle("-fx-text-fill: #d32323;");
                error_label.setText("Some error occured. Check again.");
            }
        } else {
            if (va.validationResultProperty().get().getErrors().isEmpty()) {
                session.save(ml);
                session.getTransaction().commit();
                session.close();
                mCtr.showSuccessAlert();
//                error_label.setStyle("-fx-text-fill: #349a46;");
//                error_label.setText("Successfully assigned the loan.");
//                apply_btn.setDisable(true);

            } else {
                error_label.setStyle("-fx-text-fill: #d32323;");
                error_label.setText("Some error occured. Check again.");
            }
        }
    }

    private double getInstallmentAccordingToLoanType(String interest_per, String duration_per, double loan_amt,
            double loan_int, int no_of_pay, String interest_method) {
        int interestPer = interest_per.contains("Month") ? 0 : 1;
        int tenureIn = duration_per.contains("Months") ? 0 : 1;
        double installment_amt = 0.0;
        switch (interest_method) {
            case "Flat Rate":
                installment_amt = LoanCalculationHandler
                        .flatRateCalculator(loan_amt,
                                loan_int,
                                no_of_pay, interestPer, tenureIn, 0)
                        .get(0).getInstallment_amount();
                break;
            case "Reducing Balance - Equal Installments":
                installment_amt = LoanCalculationHandler
                        .reducingBalanceEqInstallmentsCalculator(loan_amt,
                                loan_int,
                                no_of_pay, interestPer, tenureIn, 0)
                        .get(0).getInstallment_amount();
                break;
            case "Compound Interest":
                installment_amt = LoanCalculationHandler
                        .compoundInterestCalculator(loan_amt,
                                loan_int,
                                no_of_pay, interestPer, tenureIn, 0)
                        .get(0).getInstallment_amount();
                break;
        }
        return FxUtilsHandler.roundNumber(installment_amt, 0);
    }

    public void registerDialogInputValidation() {
        va = new ValidationSupport();
        va.registerValidator(int_method_combo,
                Validator.createEmptyValidator("Interest method Selection required !"));
        va.registerValidator(loan_name_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        va.registerValidator(loan_int_combo,
                Validator.createEmptyValidator("Selection Required !"));
        va.registerValidator(loan_due_combo,
                Validator.createEmptyValidator("Selection Required !"));
        va.registerValidator(repay_combo,
                Validator.createEmptyValidator("Selection Required !"));
        va.registerValidator(loan_due_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        va.registerValidator(loan_int_txt,
                Validator.createPredicateValidator(percentage -> {
                    return TextFormatHandler.getPercentageFieldValue(loan_int_txt) < 100d;
                }, "Not a valid interest !"));
        va.registerValidator(principal_amt_txt,
                Validator.createPredicateValidator((principal) -> {
                    return TextFormatHandler.getCurrencyFieldValue(principal_amt_txt) > 1000d;
                }, "Principal amount should be more than Rs 1000.00 !"));
        // List view should be validated =======================================
//        va.registerValidator(c.getGuarantor_list(), Validator.createPredicateValidator(p -> {
//            return c.getGuarantor_list().getItems().size() < 1;
//        }, "At least one guarantor should be included."));

    }

    private void unregisterSeconPartValidation() {
        vs = new ValidationSupport();
    }

    private void registerSecondPartValidation() {
        vs = new ValidationSupport();
        vs.registerValidator(c_int_method_combo,
                Validator.createEmptyValidator("Interest method Selection required !"));
        vs.registerValidator(c_loan_name_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        vs.registerValidator(c_loan_int_combo,
                Validator.createEmptyValidator("Selection Required !"));
        vs.registerValidator(c_loan_due_combo,
                Validator.createEmptyValidator("Selection Required !"));
        vs.registerValidator(c_repay_combo,
                Validator.createEmptyValidator("Selection Required !"));
        vs.registerValidator(c_loan_due_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        vs.registerValidator(c_loan_int_txt,
                Validator.createPredicateValidator(percentage -> {
                    return TextFormatHandler.getPercentageFieldValue(loan_int_txt) < 100d;
                }, "Not a valid interest !"));
        vs.registerValidator(c_principal_amt_txt,
                Validator.createPredicateValidator((principal) -> {
                    return TextFormatHandler.getCurrencyFieldValue(c_principal_amt_txt) > 1000d;
                }, "Principal amount should be more than Rs 1000.00 !"));
    }

    private void getLoanByName(String loanName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Loan.class);
        c.add(Restrictions.eq("loanName", loanName));
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

    private Set<Member> getAvailableGuarantors() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c1 = session.createCriteria(MemberLoan.class);
        c1.setProjection(Projections.projectionList()
                .add(Projections.property("isComplete"), "isComplete")
                .add(Projections.property("guarantors"), "guarantors")
                .add(Projections.property("member"), "member")
        );
        c1.setResultTransformer(Transformers.aliasToBean(MemberLoan.class));
        List<MemberLoan> ml = c1.list();
        //GET ALL GUARANTORS OF ONGOING LOANS
        List<String> guarantors = ml.stream()
                .filter(p -> !p.isIsComplete())
                .map(m -> m.getGuarantors() + "-" + m.getMember().getMemberId()).collect(Collectors.toList());

        Set<String> lkm = new HashSet<>(guarantors);
        List<String> guarantor_filtered = lkm.stream()
                .map(r -> r = r.split("-")[0])
                .collect(Collectors.toList());

        //GET ALREADY GUARANTED MEMBERS OF THE GARNTOR
        List<String> alreadyGurantedMembers = ml.stream()
                .filter(p -> !p.isIsComplete())
                .filter(p -> p.getMember().getMemberId().equalsIgnoreCase(getMember().getMemberId()))
                .map(MemberLoan::getGuarantors).collect(Collectors.toList());

        //========================
        Set<Member> set = new HashSet<>();
        //========================

        //GET ALL MEMBERS EXCEPT THE LOAN GRANTOR
        Criteria c2 = session.createCriteria(Member.class);
        c2.add(Restrictions.ne("memberId", getMember().getMemberId()));
        c2.add(Restrictions.eq("status", true));
        c2.setProjection(Projections.projectionList()
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName")
        );
        c2.setResultTransformer(Transformers.aliasToBean(Member.class));

        //IF NO GURANTORS FOUND THEN ALL MEMBERS CAN GUARANT FOR THE LOAN EXCEPT THE LOAN GRANTOR
        if (getUniqueGuarantors(guarantor_filtered, UNIQUE_GUR_FRQUENCY).isEmpty()) {
            List<Member> list = c2.list();
            set.addAll(list);
            //
        } else {
            List<Member> list = c2.add(Restrictions.not(Restrictions.
                    in("memberId", getUniqueGuarantors(guarantor_filtered, UNIQUE_GUR_FRQUENCY)))).list();
            set.addAll(list);
        }

        //IF NO GUARANTORS AVAILABLE THEY CAN GURANT THE GRANTOR ULTIMATELY UNTIL ALL GUARANTED LOANS END
        if (!alreadyGurantedMembers.isEmpty()) {
            Set<Member> arlm = getAlreadyGurantedMembers(alreadyGurantedMembers, session);
            set.addAll(arlm);
        }

        session.close();
        return set;
    }

    private Set<String> getUniqueGuarantors(List<String> guarantors, int frquency) {
        Set<String> ug = new HashSet<>();
        CopyOnWriteArrayList<String> ugc = new CopyOnWriteArrayList<>();
        for (String string : guarantors) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> yourList = new Gson().fromJson(string, type);
            for (String yl : yourList) {
                ugc.add(yl);
                if (Collections.frequency(ugc, yl) >= frquency) {
                    ug.add(yl);
                }
            }
        }
        return ug;
    }

    private Set<Member> getAlreadyGurantedMembers(List<String> agm, Session s) {
        Set<String> ug = new HashSet<>();
        for (String string : agm) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> yourList = new Gson().fromJson(string, type);
            for (String yl : yourList) {
                ug.add(yl);
            }
        }
        Criteria cc2 = s.createCriteria(Member.class);
        cc2.add(Restrictions.in("memberId", ug));
        cc2.setProjection(Projections.projectionList()
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName")
        );
        cc2.setResultTransformer(Transformers.aliasToBean(Member.class));
        List<Member> list = cc2.list();
        Set<Member> mbrs = new HashSet(list);
        return mbrs;
    }

    private Member getMemberByMemberId(String mbrId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        Member mbr = (Member) c.add(Restrictions.eq("memberId", mbrId)).uniqueResult();
        return mbr;
    }

    @FXML
    private void onLoanAmtClicked(MouseEvent event) {
        principal_amt_txt.selectRange(2, principal_amt_txt.getText().length());
    }

    @FXML
    private void onLoanIntClicked(MouseEvent event) {
        loan_int_txt.selectRange(0, loan_int_txt.getText().length() - 1);
    }

    @FXML
    private void onSecLoanAmtClicked(MouseEvent event) {
        c_principal_amt_txt.selectRange(2, c_principal_amt_txt.getText().length());
    }

    @FXML
    private void onSecLoanIntClicked(MouseEvent event) {
        c_loan_int_txt.selectRange(2, c_loan_int_txt.getText().length());
    }

}
