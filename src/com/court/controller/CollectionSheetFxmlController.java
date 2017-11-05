/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.CheckBoxCellValueFactory;
import com.court.handler.DisplaySubscriptionFactory;
import com.court.handler.DisplayTotalInstallmentsFactory;
import com.court.handler.FxUtilsHandler;
import com.court.handler.PropHandler;
import com.court.handler.SubscriptionValueFactory;
import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayCheque;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.court.model.MemberSubscriptions;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
    private TableView<Member> collection_tbl;
    @FXML
    private TableColumn<Member, String> m_id_col;
    @FXML
    private TableColumn<Member, String> m_name_col;
    @FXML
    private TableColumn<Member, CheckBox> colection_stat_col;
    @FXML
    private TextField chk_no_txt;
    @FXML
    private DatePicker chk_date_chooser;
    @FXML
    private TextField chk_amt_txt;
    @FXML
    private GridPane collection_grid;
    @FXML
    private TableColumn<Member, String> total_pay_col;
    @FXML
    private TableColumn<Member, Button> detail_view_col;
    @FXML
    private TableColumn<Member, Button> rtot_pay_col;
    @FXML
    private TableColumn<Member, Double> tot_inst_amt_col;
    @FXML
    private TableColumn<Member, Double> sub_tot_col;

    private double subTotal = 0.0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        FxUtilsHandler.setDatePickerTimeFormat(chk_date_chooser);
        chk_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = +subTotal;
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        performSearch(search_typ_combo, search_txt);
        bindValidationOnPaneControlFocus(collection_grid);
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
    private void onProceedBtnAction(ActionEvent event) throws IOException {
        if (isValidationEmpty()) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {

            if (collection_tbl.getItems().isEmpty()) {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("No content in the table.");
                alert_error.setContentText("You have not filtered any members to the table. ");
                alert_error.show();
                return;
            }
            double tot_pay = TextFormatHandler.getCurrencyFieldValue(chk_amt_txt);

            if (FxUtilsHandler.roundNumber(tot_pay, 0) != TextFormatHandler.getCurrencyFieldValue(chk_amt_txt)) {
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

            LoanPayCheque payCheque = new LoanPayCheque();
            payCheque.setChequeNo(chk_no_txt.getText());
            payCheque.setChequeDate(Date.valueOf(chk_date_chooser.getValue()));
            payCheque.setChequeAmount(TextFormatHandler.getCurrencyFieldValue(chk_amt_txt));
            session.save(payCheque);

            collection_tbl.getItems().forEach(ml -> {
                List<MemberLoan> mLoanList = ml.getMemberLoans().stream()
                        .sorted(Comparator.comparing(p -> p.getId()))
                        .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                        .filter(p -> (!p.isIsComplete() && p.isStatus())).collect(Collectors.toList());

                List<MemberSubscriptions> mbrSubs = new ArrayList<>(ml.getMemberSubscriptions());

                mLoanList.forEach(l -> {
                    LoanPayment getLastPay = l.getLoanPayments()
                            .stream().filter(p -> p.isIsLast()).findAny().orElse(null);

                    if (getLastPay != null) {
                        updatePreviousInstallmentsOfMemberLoan(session, getLastPay);
                        LoanPayment lp = new LoanPayment();
                        lp.setChequeNo(chk_no_txt.getText());
                        lp.setMemberLoan(l);
                        lp.setPaymentDate(new java.util.Date());
                        lp.setIsLast(true);
                        lp.setInstallmentDue(l.getNoOfRepay() - (getLastPay.getInstallmentNo() + 1));
                        lp.setPaymentDue(FxUtilsHandler.roundNumber(l.getLoanInstallment() * (l.getNoOfRepay() - (getLastPay.getInstallmentNo() + 1)), 0));
                        lp.setInstallmentNo(getLastPay.getInstallmentNo() + 1);

                        for (MemberSubscriptions mbrSub : mbrSubs) {
                            switch (mbrSub.getMemberSubscription().getFeeName()) {
                                case "Membership Fee":
                                    lp.setMembershipFee(mbrSub.getAmount());
                                    break;
                                case "Savings Fee":
                                    lp.setSavingsFee(mbrSub.getAmount());
                                    break;
                                case "HOI Fee":
                                    lp.setHoiFee(mbrSub.getAmount());
                                    break;
                                case "ACI Fee":
                                    lp.setAciFee(mbrSub.getAmount());
                                    break;
                                case "Optional":
                                    lp.setOptionalFee(mbrSub.getAmount());
                                    break;
                                case "Admission Fee":
                                    lp.setAdmissionFee(0.0);
                                    break;
                            }
                        }
                        lp.setLoanPayCheque(payCheque);
                        session.save(lp);
                        //end loan if the final inatallment......
                        if (l.getNoOfRepay() == (getLastPay.getInstallmentNo() + 1)) {
                            endLoan(session, l);
                        }
                    } else {
                        LoanPayment lp = new LoanPayment();
                        lp.setChequeNo(chk_no_txt.getText());
                        lp.setMemberLoan(l);
                        lp.setPaymentDate(new java.util.Date());
                        lp.setIsLast(true);
                        lp.setInstallmentDue(l.getNoOfRepay() - 1);
                        lp.setPaymentDue(FxUtilsHandler.roundNumber(l.getLoanInstallment() * (l.getNoOfRepay() - 1), 0));
                        lp.setInstallmentNo(1);

                        for (MemberSubscriptions mbrSub : mbrSubs) {
                            switch (mbrSub.getMemberSubscription().getFeeName()) {
                                case "Membership Fee":
                                    lp.setMembershipFee(mbrSub.getAmount());
                                    break;
                                case "Savings Fee":
                                    lp.setSavingsFee(mbrSub.getAmount());
                                    break;
                                case "HOI Fee":
                                    lp.setHoiFee(mbrSub.getAmount());
                                    break;
                                case "ACI Fee":
                                    lp.setAciFee(mbrSub.getAmount());
                                    break;
                                case "Optional":
                                    lp.setOptionalFee(mbrSub.getAmount());
                                    break;
                                case "Admission Fee":
                                    lp.setAdmissionFee(mbrSub.getAmount());
                                    break;
                            }
                        }
                        lp.setLoanPayCheque(payCheque);
                        session.save(lp);
                    }
                });
            });

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
        Criteria c = session.createCriteria(Member.class);
        c.createAlias("branch", "b");
        c.createAlias("memberLoans", "ml");
        c.add(Restrictions.eq("ml.isComplete", false));
        int selected = search_typ_combo.getSelectionModel().getSelectedIndex();
        switch (selected) {
            case 0:
                c.add(Restrictions.disjunction()
                        .add(Restrictions.eq("b.branchName", search_txt.getText())));
                break;
            case 1:
                c.add(Restrictions.disjunction()
                        .add(Restrictions.eq("memberId", search_txt.getText())));
                break;
            case 2:
                c.add(Restrictions.disjunction()
                        .add(Restrictions.eq("nameWithIns", search_txt.getText())));
                break;
        }

        List<Member> mList = c.list();

        List<Member> filteredList = mList.stream()
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberId()))
                .collect(Collectors.toList());

        initCollectionTable(filteredList);
        session.close();
    }

    private void initCollectionTable(List<Member> mList) {

        ObservableList<Member> mlz = FXCollections.observableArrayList(mList);
        m_id_col.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        m_name_col.setCellValueFactory(new PropertyValueFactory<>("nameWithIns"));
        total_pay_col.setCellValueFactory(new SubscriptionValueFactory());
        colection_stat_col.setCellValueFactory(new CheckBoxCellValueFactory());
        detail_view_col.setCellValueFactory(new DisplaySubscriptionFactory());
        rtot_pay_col.setCellValueFactory(new DisplayTotalInstallmentsFactory());
        tot_inst_amt_col.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
        sub_tot_col.setCellValueFactory(new SubTotalValueFactory());

        tot_inst_amt_col.setCellFactory(col -> {
            return new TableCell<Member, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                    }
                }
            };
        });

        sub_tot_col.setCellFactory(col -> {
            return new TableCell<Member, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setSubTotal(item);
                        bindSubTotalTo(chk_amt_txt);
                        setStyle("-fx-font-weight: bold;-fx-font-size: 15px;");
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                    }
                }
            };
        });
        collection_tbl.setItems(mlz);
    }

    private void bindSubTotalTo(TextField chk_amt_txt) {
        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(getSubTotal()));
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
        mml.setIsComplete(true);
        session.update(mml);
    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
        validationSupport.registerValidator(chk_amt_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(chk_no_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional !"),
                        Validator.createRegexValidator("Only alphanumeric and hyphen(-) allowed !", 
                                "^[a-zA-Z0-9\\-]*$", Severity.ERROR)));
        validationSupport.registerValidator(chk_date_chooser,
                Validator.createEmptyValidator("Check date is required !"));
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

    private boolean isValidationEmpty() {
        return validationSupport.validationResultProperty().get() == null;
    }
}
