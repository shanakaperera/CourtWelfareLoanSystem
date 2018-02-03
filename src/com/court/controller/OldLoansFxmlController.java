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
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
import org.joda.time.DateTime;

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
    private TableColumn<MemberLoan, HBox> ln_action_col;
    @FXML
    private TextField sloan_search_txt;
    @FXML
    private TextField sloan_amt_txt;
    @FXML
    private TextField sloan_int_txt;
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
    @FXML
    private TextField last_paid_ins;
    @FXML
    private TextField slast_paid_ins;
    @FXML
    private TextField pl_bal_contxt;
    @FXML
    private TextField cl_bal_contxt;
    @FXML
    private Label gur_notice_txt;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        va = new ValidationSupport();
        va2 = new ValidationSupport();

        floan_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        floan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());

        pl_bal_contxt.setTextFormatter(TextFormatHandler.currencyFormatter());
        cl_bal_contxt.setTextFormatter(TextFormatHandler.currencyFormatter());

        last_paid_ins.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
        slast_paid_ins.setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
        last_paid_ins.setText("0");
        slast_paid_ins.setText("0");

        sloan_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        sloan_int_txt.setTextFormatter(TextFormatHandler.percentageFormatter());
        gur_notice_txt.setText("");

        TextFields.bindAutoCompletion(mbr_search_txt, getMembers())
                .setOnAutoCompleted(e -> {
                    searchMbr = getSearchMember(e.getCompletion().getMemberId());
                    gur_btn.setDisable(false);
                    mbr_search_txt.setEditable(false);
                    loadOldLoans();
                });

        TextFields.bindAutoCompletion(floan_search_txt, getLoans())
                .setOnAutoCompleted(e -> {
                    Loan loan = e.getCompletion();
                    pLoan = loan;
                    floan_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT
                            .format(loan.getLoanInterest() / 100));
                });

        FxUtilsHandler.setDatePickerTimeFormat(floan_granted_picker);

        bindValidationOnPaneControlFocus(main_grid);
        disableFields(true);
    }

    public void loadOldLoans() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(MemberLoan.class, "ml");
        c.createAlias("ml.member", "m");
        c.add(Restrictions.eq("ml.oldLoan", true));
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
        mbr_search_txt.setEditable(true);
        gur_notice_txt.setText("");
        last_paid_ins.setText("0");
        slast_paid_ins.setText("0");
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

        boolean isValid = validateGDateWithInstallments(floan_granted_picker.getValue(), last_paid_ins);

        if (!isValid) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Ambiguous granted date or installments !");
            alert_error.setContentText("Installments number provided as paid is not match with the granted date. ");
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
        ml.setGuarantors(new Gson().toJson(gurList.stream().map(Member::getMemberId)
                .collect(Collectors.toList()), new TypeToken<List<String>>() {
        }.getType()));
        ml.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(floan_amt_txt));
        ml.setLoanInterest(TextFormatHandler.getPercentageFieldValue(floan_int_txt));
        ml.setInterestPer(pLoan.getInterestPer());
        ml.setInterestMethod(pLoan.getInterestMethod());
        ml.setLoanDuration(pLoan.getLoanDuration());
        ml.setDurationPer(pLoan.getDurationPer());
        ml.setRepaymentCycle(pLoan.getRepaymentCycle());
        ml.setNoOfRepay(pLoan.getNoOfRepay());
        ml.setlRequested(FxUtilsHandler.getDateFrom(floan_granted_picker.getValue()));

        double installmentAccordingToLoanType = FxUtilsHandler.roundNumber(getInstallmentAccordingToLoanType(
                pLoan.getInterestPer(),
                pLoan.getDurationPer(),
                TextFormatHandler.getCurrencyFieldValue(floan_amt_txt),
                TextFormatHandler.getPercentageFieldValue(floan_int_txt),
                pLoan.getNoOfRepay(),
                pLoan.getInterestMethod()), 0);

        ml.setLoanInstallment(installmentAccordingToLoanType);
        ml.setIsComplete(
                (pLoan.getLoanDuration() == Integer.parseInt(last_paid_ins.getText()))
                && (TextFormatHandler.getCurrencyFieldValue(cl_bal_contxt) == 0)
        );
        ml.setStatus(true);
        ml.setOldLoan(true);

        DateTime finalPay = new DateTime(
                Date.valueOf(floan_granted_picker.getValue()).getTime())
                .plusMonths(Integer.parseInt(last_paid_ins.getText()));

        ml.setLastInstall(Integer.parseInt(last_paid_ins.getText()));
        ml.setPaidSofar(
                installmentAccordingToLoanType * Integer.parseInt(last_paid_ins.getText())
        );
        ml.setPaidUntil(finalPay.toDate());
        ml.setKotaLeft(TextFormatHandler.getCurrencyFieldValue(pl_bal_contxt));

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
                ml.setlRequested(FxUtilsHandler.getDateFrom(floan_granted_picker.getValue()));

                double cInstallmentAccordingToLoanType = FxUtilsHandler.roundNumber(getInstallmentAccordingToLoanType(
                        cLoan.getInterestPer(),
                        cLoan.getDurationPer(),
                        TextFormatHandler.getCurrencyFieldValue(sloan_amt_txt),
                        TextFormatHandler.getPercentageFieldValue(sloan_int_txt),
                        cLoan.getNoOfRepay(),
                        cLoan.getInterestMethod()), 0);

                ml2.setLoanInstallment(cInstallmentAccordingToLoanType);

                DateTime finalPay2 = new DateTime(
                        Date.valueOf(floan_granted_picker.getValue()).getTime())
                        .plusMonths(Integer.parseInt(slast_paid_ins.getText()));

                //==================================================
                ml2.setLastInstall(Integer.parseInt(slast_paid_ins.getText()));
                ml2.setPaidSofar(cInstallmentAccordingToLoanType * Integer.parseInt(slast_paid_ins.getText()));
                ml2.setPaidUntil(ml.isIsComplete() ? finalPay2.toDate() : null);
                ml2.setKotaLeft(TextFormatHandler.getCurrencyFieldValue(cl_bal_contxt));
                //======================================================
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
                    System.out.println("Loaded" + " : Size - " + value.size());
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
                        if (!gurList.isEmpty()) {
                            gur_notice_txt.setText("Guarantors added successfully !");
                            gur_notice_txt.setTextFill(Color.web("#4fcc60"));
                        } else {
                            gur_notice_txt.setText("No any gurantors selected !");
                            gur_notice_txt.setTextFill(Color.web("#cc2828"));
                        }
                    });

                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected Set<Member> call() throws Exception {
                return getAvailableGuarantors();
                //return new HashSet(getMembers());
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
                .add(Projections.property("fullName"), "fullName")
        );
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
        va.registerValidator(pl_bal_contxt,
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
        va.registerValidator(cl_bal_contxt,
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
        cl_bal_contxt.setDisable(b);
        slast_paid_ins.setDisable(b);
    }

    private boolean isValidationEmpty(ValidationSupport vs) {
        return vs.validationResultProperty().get() == null;
    }

    private Set<Member> getAvailableGuarantors() {
        String mbrId = mbr_search_txt.getText().split("-")[0].trim();
        //GET ALL MEMBERS EXPECT GRANTOR======
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c2 = session.createCriteria(Member.class);
        c2.add(Restrictions.ne("memberId", mbrId));
        c2.add(Restrictions.eq("status", true));
        c2.setProjection(Projections.projectionList()
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName")
        );
        c2.setResultTransformer(Transformers.aliasToBean(Member.class));
        List<Member> list = c2.list();
        session.close();
        return new HashSet<>(list);
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
//            FxUtilsHandler.clearFields(main_grid);
//            Session s = HibernateUtil.getSessionFactory().openSession();
//            Criteria c = s.createCriteria(MemberLoan.class, "ml");
//            c.createAlias("ml.member", "m");
//            c.add(Restrictions.eq("ml.oldLoan", true));
//            c.add(Restrictions.eq("m.memberId", searchMbr.getMemberId()));
//            List<MemberLoan> list = c.list();
//            s.close();
//            initOldLoanTable(list);
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
    private void onFLoanIntMClicked(MouseEvent event) {
        floan_int_txt.selectRange(0, floan_int_txt.getText().length() - 1);
    }

    @FXML
    private void onFLoanAmtMClicked(MouseEvent event) {
        floan_amt_txt.selectRange(2, floan_amt_txt.getText().length());
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
                    .format(param.getValue().getLoanInterest()) + " " + param.getValue().getInterestPer());
        });

        ln_action_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, HBox> param) -> {
            Button btn_d = new Button("Delete");
            btn_d.setStyle("-fx-background-color:red");
            Button btn_v = new Button("View");
            HBox box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.setSpacing(2);

            btn_d.setOnAction(e -> {
                deleteLoan(param.getValue().getId());
            });
            btn_v.setOnAction(e -> {
                loadToFields(param.getValue());
            });

            box.getChildren().addAll(btn_v, btn_d);
            return new SimpleObjectProperty<>(box);
        });

        old_loan_tbl.setItems(FXCollections.observableArrayList(list));
    }

    private void loadToFields(MemberLoan ml) {

        Alert elDialog = new Alert(Alert.AlertType.INFORMATION);
        elDialog.setTitle("Old Loan Details");
        elDialog.setHeaderText("Member Loan - " + ml.getMemberLoanCode());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Loan Code :"), 0, 0);
        grid.add(new Label(ml.getMemberLoanCode()), 1, 0);
        grid.add(new Label("Loan Amount :"), 0, 1);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanAmount())), 1, 1);
        grid.add(new Label("Loan Installment :"), 0, 2);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanInstallment())), 1, 2);
        grid.add(new Label("Loan Interest :"), 0, 3);
        grid.add(new Label(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(ml.getLoanInterest() / 100)), 1, 3);
        grid.add(new Label("Granted Date :"), 0, 4);
        grid.add(new Label(new SimpleDateFormat("dd-MM-yyyy").format(ml.getGrantedDate())), 1, 4);
        grid.add(new Label("Paid So far :"), 0, 5);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getPaidSofar())), 1, 5);
        grid.add(new Label("Last Payment Date :"), 0, 6);
        grid.add(new Label(new SimpleDateFormat("dd-MM-yyyy").format(ml.getPaidUntil())), 1, 6);
        grid.add(new Label("Balance Continued :"), 0, 7);
        grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getKotaLeft())), 1, 7);
        elDialog.getDialogPane().setContent(grid);
        elDialog.show();

    }

    private void deleteLoan(Integer id) {

        Alert alert_confirm = new Alert(Alert.AlertType.CONFIRMATION);
        alert_confirm.setTitle("Warning");
        alert_confirm.setHeaderText("Confirm ?");
        alert_confirm.setContentText("Are you sure you want to delete the selected loan ?");
        Optional<ButtonType> rs = alert_confirm.showAndWait();
        if (rs.get() == ButtonType.OK) {

            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();
            MemberLoan ml = (MemberLoan) s.load(MemberLoan.class, id);
            //==================CHECK IF ANY LOAN PAYMENTS EXIST BEFORE DELETE 
            if (ml.getLoanPayments().isEmpty()) {
                s.delete(ml);
                s.getTransaction().commit();
                //================================================
                Criteria c = s.createCriteria(MemberLoan.class);
                c.createAlias("member", "m");
                c.add(Restrictions.eq("oldLoan", true));
                c.add(Restrictions.eq("oldLoan", true));
                c.add(Restrictions.eq("m.memberId", searchMbr.getMemberId()));
                List<MemberLoan> list = c.list();
                s.close();
                initOldLoanTable(list);
            } else {
                Alert error_alert = new Alert(Alert.AlertType.INFORMATION);
                error_alert.setTitle("Error");
                error_alert.setHeaderText("Error Occured !");
                error_alert.setContentText("There are already assigned payments for this loan.");
                error_alert.show();
            }
            //================================================
        }
    }

    @FXML
    private void onFLoanGntAction(ActionEvent event) {
    }

    private boolean validateGDateWithInstallments(LocalDate value, TextField last_paid_ins) {

        int installments = Integer.parseInt(last_paid_ins.getText());
        Date gDate = Date.valueOf(value);

        DateTime calculatedDate = new DateTime(gDate.getTime()).plusMonths(installments);
        DateTime now = new DateTime();
        long diff = calculatedDate.getMillis() - now.getMillis();
        System.out.println("calculatedDate - " + calculatedDate);
        System.out.println("nowDate - " + now);
        System.out.println("difference - " + diff);
        return diff <= 0;
    }
}
