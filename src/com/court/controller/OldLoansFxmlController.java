/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.DocSeqHandler;
import com.court.handler.FileHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.LoanCalculationHandler;
import com.court.handler.PropHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.Loan;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class OldLoansFxmlController implements Initializable {

    private static final String TABLE_NAME = "member_loan";
    @FXML
    private TextField floan_search_txt;
    @FXML
    private TextField floan_int_txt;
    @FXML
    private TextField floan_amt_txt;
    @FXML
    private DatePicker floan_granted_picker;
    @FXML
    private TextField floan_paidfar_txt;
    @FXML
    private DatePicker floan_lastpay_picker;
    @FXML
    private TextField floan_balance_con_txt;
    @FXML
    private CheckBox has_child_check;
    @FXML
    private TableView<MemberLoan> old_loan_tbl;
    @FXML
    private TableColumn<MemberLoan, String> ln_id_col;
    @FXML
    private TableColumn<MemberLoan, String> ln_name_col;
    @FXML
    private TableColumn<MemberLoan, String> ln_amt_col;
    @FXML
    private TableColumn<MemberLoan, String> ln_int_col;
    @FXML
    private TableColumn<MemberLoan, Button> ln_action_col;
    @FXML
    private TextField sloan_search_txt;
    @FXML
    private DatePicker sloan_lastpay_picker;
    @FXML
    private TextField sloan_balance_con_txt;
    @FXML
    private TextField sloan_amt_txt;
    @FXML
    private TextField sloan_int_txt;
    @FXML
    private TextField sloan_paidfar_txt;
    @FXML
    private TextField mbr_search_txt;

    private Member searchMbr;
    @FXML
    private GridPane main_grid;

    private ValidationSupport va, va2;
    private ObservableList<Member> gurList;

    private Loan pLoan, cLoan;
    @FXML
    private Button gur_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        va = new ValidationSupport();
        va2 = new ValidationSupport();

        floan_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        floan_paidfar_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        floan_balance_con_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        floan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());

        sloan_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        sloan_paidfar_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        sloan_balance_con_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        sloan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());

        TextFields.bindAutoCompletion(mbr_search_txt, getMembers()).setOnAutoCompleted(e -> {
            searchMbr = getSearchMember(e.getCompletion().getMemberId());
            gur_btn.setDisable(false);
        });

        TextFields.bindAutoCompletion(floan_search_txt, getLoans()).setOnAutoCompleted(e -> {
            Loan loan = e.getCompletion();
            pLoan = loan;
            floan_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT
                    .format(loan.getLoanInterest() / 100));
        });

        FxUtilsHandler.setDatePickerTimeFormat(floan_granted_picker, floan_lastpay_picker, sloan_lastpay_picker);

        bindValidationOnPaneControlFocus(main_grid);
        disableFields(true);
    }

    @FXML
    private void onMbrSearchBtnAction(ActionEvent event) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(MemberLoan.class);
        c.createAlias("member", "m");
        c.add(Restrictions.eq("oldLoan", true));
        c.add(Restrictions.eq("oldLoan", true));
        c.add(Restrictions.eq("m.memberId", searchMbr.getMemberId()));
        List<MemberLoan> list = c.list();
        s.close();
        initOldLoanTable(list);
    }

    @FXML
    private void onClearAllBtnAction(ActionEvent event) {
        gurList = null;
        pLoan = null;
        cLoan = null;
        gur_btn.setDisable(true);
        searchMbr = null;
        FxUtilsHandler.clearFields(main_grid);
    }

    @FXML
    private void onSaveBtnAction(ActionEvent event) throws IOException {

        if (searchMbr == null) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("No Member Selected !");
            alert_error.setContentText("You have to select member first to assign a loan.");
            alert_error.show();
            return;
        }

        if (gurList == null) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("No Guarantors Selected !");
            alert_error.setContentText("Should have at least one guarantor for each loan !");
            alert_error.show();
            return;
        }
        if (!(gurList.size() >= 1)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Guarantors Selection error !");
            alert_error.setContentText("Should have at least one guarantor for each loan !");
            alert_error.show();
            return;
        }

        if (isValidationEmpty(va)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String PARENT = floan_search_txt.getText();
        MemberLoan ml = new MemberLoan();
        ml.setMemberLoanCode(fillMemberLoanCodeTxt());
        ml.setMember(searchMbr);
        ml.setLoanName(PARENT.split("-")[1].trim());
        ml.setGrantedDate(FxUtilsHandler.getDateFrom(floan_granted_picker.getValue()));
        ml.setGuarantors(new Gson().toJson(gurList.stream().map(Member::getMemberId).collect(Collectors.toList()), new TypeToken<List<String>>() {
        }.getType()));
        ml.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(floan_amt_txt));
        ml.setLoanInterest(TextFormatHandler.getPercentageFieldValue(floan_int_txt));
        ml.setInterestPer(pLoan.getInterestPer());
        ml.setInterestMethod(pLoan.getInterestMethod());
        ml.setLoanDuration(pLoan.getLoanDuration());
        ml.setDurationPer(pLoan.getDurationPer());
        ml.setRepaymentCycle(pLoan.getRepaymentCycle());
        ml.setNoOfRepay(pLoan.getNoOfRepay());
        ml.setLoanInstallment(getInstallmentAccordingToLoanType(
                pLoan.getInterestPer(),
                pLoan.getDurationPer(),
                TextFormatHandler.getCurrencyFieldValue(floan_amt_txt),
                TextFormatHandler.getPercentageFieldValue(floan_int_txt),
                pLoan.getNoOfRepay(),
                pLoan.getInterestMethod())
        );
        ml.setIsComplete(false);
        ml.setStatus(true);
        ml.setOldLoan(true);
        ml.setPaidSofar(TextFormatHandler.getCurrencyFieldValue(floan_paidfar_txt));
        ml.setPaidUntil(FxUtilsHandler.getDateFrom(floan_lastpay_picker.getValue()));
        ml.setKotaLeft(TextFormatHandler.getCurrencyFieldValue(floan_balance_con_txt));

        if (has_child_check.isSelected()) {

            if (isValidationEmpty(va2)) {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Empty Fields !");
                alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
                alert_error.show();
                return;
            }

            if (va.validationResultProperty().get().getErrors().isEmpty() && va.validationResultProperty().get().getErrors().isEmpty()) {

                String CHILD = sloan_search_txt.getText();
                MemberLoan ml2 = new MemberLoan();
                ml2.setMemberLoanCode(fillMemberLoanCodeTxt());
                ml2.setMember(searchMbr);
                ml2.setLoanName(CHILD.split("-")[0].trim());
                ml2.setGrantedDate(FxUtilsHandler.getDateFrom(floan_granted_picker.getValue()));
                ml2.setGuarantors(new Gson().toJson(gurList.stream().map(Member::getMemberId).collect(Collectors.toList()), new TypeToken<List<String>>() {
                }.getType()));
                ml2.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(sloan_amt_txt));
                ml2.setLoanInterest(TextFormatHandler.getPercentageFieldValue(sloan_int_txt));
                ml2.setInterestPer(cLoan.getInterestPer());
                ml2.setInterestMethod(cLoan.getInterestMethod());
                ml2.setLoanDuration(cLoan.getLoanDuration());
                ml2.setDurationPer(cLoan.getDurationPer());
                ml2.setRepaymentCycle(cLoan.getRepaymentCycle());
                ml2.setNoOfRepay(cLoan.getNoOfRepay());
                ml2.setLoanInstallment(getInstallmentAccordingToLoanType(
                        cLoan.getInterestPer(),
                        cLoan.getDurationPer(),
                        TextFormatHandler.getCurrencyFieldValue(sloan_amt_txt),
                        TextFormatHandler.getPercentageFieldValue(sloan_int_txt),
                        cLoan.getNoOfRepay(),
                        cLoan.getInterestMethod())
                );
                ml2.setIsComplete(false);
                ml2.setStatus(true);
                ml2.setChildId(0);
                ml2.setIsChild(true);
                ml2.setOldLoan(true);
                session.save(ml2);

                ml.setHasChild(true);
                ml.setChildId(ml2.getId());
                session.save(ml);
                session.getTransaction().commit();
                session.close();
                LoanSaveSuccess();

            } else {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Missing Fields !");
                alert_error.setContentText(PropHandler.getStringProperty("missing_fields"));
                alert_error.show();
            }

        } else {

            if (va.validationResultProperty().get().getErrors().isEmpty()) {
                session.save(ml);
                session.getTransaction().commit();
                session.close();
                LoanSaveSuccess();

            } else {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Missing Fields !");
                alert_error.setContentText(PropHandler.getStringProperty("missing_fields"));
                alert_error.show();
            }
        }
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        gurList = null;
        pLoan = null;
        cLoan = null;
        gur_btn.setDisable(true);
        searchMbr = null;
        FxUtilsHandler.clearFields(main_grid);
    }

    @FXML
    private void onGuarBtnAction(ActionEvent event) {
        Dialog<ObservableList<Member>> dialog = new Dialog<>();
        dialog.setTitle("Guarantors");
        dialog.setHeaderText("Assign Guarantors to the loan");
        ButtonType applyGs = new ButtonType("Apply", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyGs);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ges = new TextField();
        ListView<Member> lv = new ListView<>();
        lv.setPrefHeight(100);
        Button rmBtn = new Button("Remove Guarantor");
        rmBtn.setOnAction(evt -> {
            if (!lv.getSelectionModel().getSelectedItems().isEmpty()) {
                lv.getItems()
                        .remove(lv.getSelectionModel().getSelectedIndex());
            }
        });
        if (gurList != null) {
            lv.getItems().addAll(gurList);
        }

        ImageView progressIndicator = new ImageView();
        progressIndicator.setImage(new Image(FileHandler.LOADING_DEFAULT_GIF));
        VBox v = new VBox(progressIndicator);
        v.setAlignment(Pos.CENTER);
        Dialog alert_prog = new Alert(Alert.AlertType.NONE);
        alert_prog.setTitle("Ongoing progress");
        alert_prog.setHeaderText("Please wait until the guarantors are loading. ");
        alert_prog.getDialogPane().setContent(v);
        alert_prog.setResult(Boolean.TRUE);
        alert_prog.show();

        Task<Set<Member>> gTask = new Task<Set<Member>>() {
            {
                setOnSucceeded(d -> {
                    alert_prog.close();
                    Set<Member> value = getValue();
                    System.out.println("Loaded");
                    TextFields.bindAutoCompletion(ges, getValue())
                            .setOnAutoCompleted(e -> {
                                CopyOnWriteArrayList<Member> cpa = new CopyOnWriteArrayList<>(lv.getItems());
                                if (!cpa.stream()
                                        .filter(p -> p.getMemberId().equalsIgnoreCase(e.getCompletion().getMemberId()))
                                        .findAny().isPresent()) {
                                    lv.getItems().add(e.getCompletion());
                                }
                                ges.setText("");
                            });

                    grid.add(new Label("Guarantor Select:"), 0, 0);
                    grid.add(ges, 1, 0);
                    grid.add(new Label("Gurantor List:"), 0, 1);
                    grid.add(lv, 1, 1);
                    grid.add(rmBtn, 1, 2);

                    dialog.getDialogPane().setContent(grid);
                    dialog.setResultConverter(db -> {
                        if (db == applyGs) {
                            return FXCollections.observableArrayList(lv.getItems());
                        }
                        return null;
                    });

                    Optional<ObservableList<Member>> result = dialog.showAndWait();
                    result.ifPresent(op -> {
                        gurList = FXCollections.observableArrayList(op);
                    });

                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected Set<Member> call() throws Exception {
                return getAvailableGuarantors();
            }
        };
        Thread gurThread = new Thread(gTask, "g-task");
        gurThread.setDaemon(true);
        gurThread.start();

    }

    @FXML
    private void onCheckboxCheckedAction(ActionEvent event) {
        CheckBox box = (CheckBox) event.getSource();
        if (box.isSelected()) {
            disableFields(false);
            registerInputValidation_2();
        } else {
            disableFields(true);
        }
    }

    private List<Member> getMembers() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Member.class);
        c.add(Restrictions.eq("status", true));
        ClassMetadata lpMeta = s.getSessionFactory().getClassMetadata(Member.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property(lpMeta.getIdentifierPropertyName()))
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName"));

        c.setResultTransformer(Transformers.aliasToBean(Member.class));
        List<Member> list = c.list();
        s.close();
        return list;
    }

    private Member getSearchMember(String mbrCode) {
        Session ses = HibernateUtil.getSessionFactory().openSession();
        Criteria c = ses.createCriteria(Member.class);
        c.add(Restrictions.eq("status", true));
        c.add(Restrictions.eq("memberId", mbrCode));
        Member filteredM = (Member) c.uniqueResult();
        ses.close();
        return filteredM;
    }

    private void registerInputValidation() {
        if (!va.getRegisteredControls().isEmpty()) {
            return;
        }
        va.registerValidator(floan_search_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(floan_amt_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(floan_int_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(floan_granted_picker,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(floan_paidfar_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(floan_lastpay_picker,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(floan_balance_con_txt,
                Validator.createEmptyValidator("This field is not optional."));
    }

    private void registerInputValidation_2() {
        if (!va2.getRegisteredControls().isEmpty()) {
            return;
        }
        va2.registerValidator(sloan_search_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va2.registerValidator(sloan_amt_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va2.registerValidator(sloan_int_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va2.registerValidator(sloan_paidfar_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va2.registerValidator(sloan_lastpay_picker,
                Validator.createEmptyValidator("This field is not optional."));
        va2.registerValidator(sloan_balance_con_txt,
                Validator.createEmptyValidator("This field is not optional."));
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

    private void disableFields(boolean b) {
        sloan_search_txt.setDisable(b);
        sloan_amt_txt.setDisable(b);
        sloan_int_txt.setDisable(b);
        sloan_paidfar_txt.setDisable(b);
        sloan_lastpay_picker.setDisable(b);
        sloan_balance_con_txt.setDisable(b);
    }

    private boolean isValidationEmpty(ValidationSupport vs) {
        return vs.validationResultProperty().get() == null;
    }

    private Set<Member> getAvailableGuarantors() {
        String mbrId = mbr_search_txt.getText().split("-")[0].trim();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c1 = session.createCriteria(MemberLoan.class, "ml");

        c1.setProjection(Projections.projectionList()
                .add(Projections.property("isComplete"), "isComplete")
                .add(Projections.property("guarantors"), "guarantors")
                .add(Projections.property("member"), "member")
        );
        c1.setResultTransformer(Transformers.aliasToBean(MemberLoan.class));
        List<MemberLoan> ml = c1.list();
        //GET ALL GUARANTORS OF ONGOING LOANS
        List<String> guarantors = ml.stream().filter(p -> !p.isIsComplete())
                .map(MemberLoan::getGuarantors).collect(Collectors.toList());

        //GET ALREADY GUARANTED MEMBERS OF THE GARNTOR
        List<String> alreadyGurantedMembers = ml.stream().filter(p -> !p.isIsComplete())
                .filter(p -> p.getMember().getMemberId().equalsIgnoreCase(mbrId))
                .map(MemberLoan::getGuarantors).collect(Collectors.toList());

        //========================
        Set<Member> set = new HashSet<>();
        //========================

        //IF NO GUARANTORS AVAILABLE THEY CAN GURANT THE GRANTOR ULTIMATELY UNTIL ALL GUARANTED LOANS END
        if (!alreadyGurantedMembers.isEmpty()) {
            Set<Member> arlm = getAlreadyGurantedMembers(alreadyGurantedMembers, session);
            set.addAll(arlm);
        }

        //GET ALL MEMBERS EXCEPT THE LOAN GRANTOR
        Criteria c2 = session.createCriteria(Member.class);
        c2.add(Restrictions.ne("memberId", mbrId));
        c2.add(Restrictions.eq("status", true));
        c2.setProjection(Projections.projectionList()
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName")
        );
        c2.setResultTransformer(Transformers.aliasToBean(Member.class));
        //IF NO GURANTORS FOUND THEN ALL MEMBERS CAN GUARANT FOR THE LOAN EXCEPT THE LOAN GRANTOR
        if (getUniqueGuarantors(guarantors, 3).isEmpty()) {
            List<Member> list = c2.list();
            set.addAll(list);
            //
        } else {
            List<Member> list = c2.add(Restrictions.not(Restrictions.
                    in("memberId", getUniqueGuarantors(guarantors, 3)))).list();
            set.addAll(list);
        }

        session.close();
        System.out.println("SIZE - " + set.size());
        return set;
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
        cc2.setProjection(Projections.property("memberId"));
        cc2.add(Restrictions.in("memberId", ug));
        cc2.setResultTransformer(Transformers.aliasToBean(Member.class));
        List<Member> list = cc2.list();
        Set<Member> mbrs = new HashSet(list);
        return mbrs;
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
                if (Collections.frequency(ugc, yl) > frquency) {
                    ug.add(yl);
                }
            }
        }
        return ug;
    }

    private List<Loan> getLoans() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Loan.class);
        c.add(Restrictions.eq("status", true));
//        c.setProjection(Projections.projectionList()
//                .add(Projections.property("loanId"), "loanId")
//                .add(Projections.property("loanName"), "loanName")
//        );
//        c.setResultTransformer(Transformers.aliasToBean(Loan.class));
        List<Loan> list = c.list();
        s.close();
        return list;
    }

    public String fillMemberLoanCodeTxt() {
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("memberLoanCode"), "memberLoanCode")
        );
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        c.setResultTransformer(Transformers.aliasToBean(MemberLoan.class));
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

    private void LoanSaveSuccess() {
        Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
        alert_success.setTitle("Information");
        alert_success.setHeaderText("Loan Assigned successfully!");
        alert_success.setContentText("You have successfully assigned the loan to the member.");
        Optional<ButtonType> result = alert_success.showAndWait();
        if (result.get() == ButtonType.OK) {
            //=====================================================================
            FxUtilsHandler.clearFields(main_grid);
            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c = s.createCriteria(MemberLoan.class);
            c.createAlias("member", "m");
            c.add(Restrictions.eq("oldLoan", true));
            c.add(Restrictions.eq("oldLoan", true));
            c.add(Restrictions.eq("m.memberId", searchMbr.getMemberId()));
            List<MemberLoan> list = c.list();
            s.close();
            initOldLoanTable(list);
            //====================================================================
            gurList = null;
            pLoan = null;
            cLoan = null;
            gur_btn.setDisable(true);
            searchMbr = null;
        }
    }

    @FXML
    private void onFLoanTxtAction(ActionEvent event) {
        floan_amt_txt.requestFocus();
        floan_amt_txt.selectRange(2, floan_amt_txt.getText().length());
    }

    @FXML
    private void onFLoanIntAction(ActionEvent event) {
        floan_granted_picker.requestFocus();
    }

    @FXML
    private void onFLoanAmtAction(ActionEvent event) {
        floan_int_txt.requestFocus();
        floan_int_txt.selectRange(0, floan_int_txt.getText().length() - 1);
    }

    @FXML
    private void onFLoanGntAction(ActionEvent event) {
        floan_paidfar_txt.requestFocus();
        floan_paidfar_txt.selectRange(2, floan_paidfar_txt.getText().length());
    }

    @FXML
    private void onFLoanPsAction(ActionEvent event) {
        floan_lastpay_picker.requestFocus();
    }

    @FXML
    private void onFLoanLpAction(ActionEvent event) {
        floan_balance_con_txt.requestFocus();
    }

    @FXML
    private void onFLoanBcAction(ActionEvent event) {

    }

    @FXML
    private void onFLoanIntMClicked(MouseEvent event) {
        floan_int_txt.selectRange(0, floan_int_txt.getText().length() - 1);
    }

    @FXML
    private void onFLoanAmtMClicked(MouseEvent event) {
        floan_amt_txt.selectRange(2, floan_amt_txt.getText().length());
    }

    @FXML
    private void onFLoanPsMClicked(MouseEvent event) {
        floan_paidfar_txt.selectRange(2, floan_paidfar_txt.getText().length());
    }

    @FXML
    private void onFLoanBcMClicked(MouseEvent event) {
        floan_balance_con_txt.selectRange(2, floan_balance_con_txt.getText().length());
    }

    private void initOldLoanTable(List<MemberLoan> list) {
        ln_id_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getMemberLoanCode());
        });

        ln_name_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getLoanName());
        });

        ln_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
            return new SimpleObjectProperty<>(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                    .format(param.getValue().getLoanAmount()));
        });

        ln_int_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
            return new SimpleObjectProperty<>(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT
                    .format(param.getValue().getLoanInterest() + " " + param.getValue().getInterestPer()));
        });

        ln_action_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, Button> param) -> {
            Button btn = new Button("Delete");
            btn.setOnAction(e -> {
            });
            return new SimpleObjectProperty<>(btn);
        });

        old_loan_tbl.setItems(FXCollections.observableArrayList(list));
    }
}
