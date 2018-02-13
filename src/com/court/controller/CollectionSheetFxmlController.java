/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.CustomDoubleStringConverter;
import com.court.handler.DisplaySubscriptionFactory;
import com.court.handler.DisplayTotalInstallmentsFactory;
import com.court.handler.FileHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.PropHandler;
import com.court.handler.ReportHandler;
import com.court.handler.SubscriptionValueFactory;
import com.court.handler.TextFormatHandler;
import com.court.model.Branch;
import com.court.model.LoanPayCheque;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.court.model.MemberSubscriptions;
import com.court.model.SubscriptionPay;
import com.google.common.util.concurrent.AtomicDouble;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
    private TextField chk_no_txt;
    @FXML
    private DatePicker chk_date_chooser;
    @FXML
    private TextField chk_amt_txt;
    @FXML
    private GridPane collection_grid;

    private TableView<Member> collection_tbl;
    private TableColumn<Member, String> m_id_col;
    private TableColumn<Member, String> m_name_col;
    private TableColumn<Member, CheckBox> colection_stat_col;
    private TableColumn<Member, String> total_pay_col;
    private TableColumn<Member, Button> detail_view_col;
    private TableColumn<Member, Button> rtot_pay_col;
    private TableColumn<Member, String> tot_inst_amt_col;
    private TableColumn<Member, Label> sub_tot_col;
    private TableColumn<Member, String> wrking_of_col;
    private TableColumn<Member, Double> overpay_col;

    @FXML
    private TextField branch_txt;
    @FXML
    private TextField bank_code_txt;
    @FXML
    private DatePicker chk_of_month_chooser;
    @FXML
    private TextField user_enter_pay;

    private int rowsPerPage = 10000;
    @FXML
    private Pagination pagination;
    @FXML
    private BorderPane table_bpane;

    double total = 0.0;
    @FXML
    private CustomTextField tbl_filter_txt;
    private Button filterTxtClear;
    private FilteredList<Member> fList;
    // private boolean branchSearch = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        FxUtilsHandler.setDatePickerTimeFormat(chk_date_chooser, chk_of_month_chooser);
        chk_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        user_enter_pay.setTextFormatter(TextFormatHandler.currencyFormatter());
        createTable();
        pagination.setVisible(false);
        filterTxtClear = new Button("X");
        filterTxtClear.setOnAction(evt -> {
            tbl_filter_txt.setText("");
            fList.setPredicate(null);
        });
        tbl_filter_txt.setRight(filterTxtClear);
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        performSearch(search_typ_combo, search_txt);
        bindValidationOnPaneControlFocus(collection_grid);
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(collection_grid);
        search_typ_combo.getSelectionModel().select(0);
        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(0));
        // collection_tbl.getItems().clear();
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
            payCheque.setChequeDate(getDayOfMonth(Date.valueOf(chk_of_month_chooser.getValue())));
            payCheque.setChequeRealise(Date.valueOf(chk_date_chooser.getValue()));
            payCheque.setBankCode(bank_code_txt.getText());
            payCheque.setPaymentRecieved(new java.util.Date());
            payCheque.setBranch(branch_txt.getText());
            payCheque.setPaymentType("cheque");
            payCheque.setChequeAmount(TextFormatHandler.getCurrencyFieldValue(chk_amt_txt));
            session.save(payCheque);

            collection_tbl.getItems().forEach(m -> {
                //====================================MEMBERS SUBSCRIPTIONS SAVE=====================================
                List<MemberSubscriptions> mbrSubs = new ArrayList<>(m.getMemberSubscriptions());
                SubscriptionPay sp = new SubscriptionPay();
                for (MemberSubscriptions mbrSub : mbrSubs) {
                    switch (mbrSub.getMemberSubscription().getFeeName()) {
                        case "Membership Fee":
                            sp.setMembershipFee(mbrSub.getAmount());
                            break;
                        case "Savings Fee":
                            sp.setSavingsFee(mbrSub.getAmount());
                            break;
                        case "HOI Fee":
                            sp.setHoiFee(mbrSub.getAmount());
                            break;
                        case "ACI Fee":
                            sp.setAciFee(mbrSub.getAmount());
                            break;
                        case "Optional":
                            sp.setOptional(mbrSub.getAmount());
                            break;
                        case "Admission Fee":
                            boolean empty = FxUtilsHandler.previousSubscriptions(m.getId(), session).isEmpty();
                            sp.setAdmissionFee(empty ? mbrSub.getAmount() : 0.0);
                            break;
                    }
                    sp.setPaymentDate(getDayOfMonth(Date.valueOf(chk_of_month_chooser.getValue())));
                    sp.setChequeNo(chk_no_txt.getText());
                    sp.setAddedDate(new java.util.Date());
                    sp.setPayOffice(m.getPayOffice().getId());
                    sp.setWorkOffice(m.getBranch().getId());
                    sp.setMemberSubscriptions(mbrSub);
                }
                try {
                    updateMemberOverPay(m, session);
                    session.save(sp);
                } catch (Exception e) {
                    Alert alert_error = new Alert(Alert.AlertType.ERROR);
                    alert_error.setTitle("Error");
                    alert_error.setHeaderText("Some member subscription details are not correctly updated !");
                    alert_error.setContentText("You have some partially updated members in this list. Please complete thier details and try again.");
                    alert_error.show();
                    throw e;
                }

                //====================================END MEMBERS SUBSCRIPTIONS SAVE=========================
                //====================================MEMBERS LOANS SAVE=====================================
                List<MemberLoan> mLoanList = m.getMemberLoans().stream()
                        .sorted(Comparator.comparing(MemberLoan::getChildId).reversed())
                        .filter(p -> (!p.isIsComplete() && p.isStatus()))
                        .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                        //  .filter(FxUtilsHandler.checkIfLastPaidDateWithinCurrentMonth(p -> p.getPaidUntil()))
                        .collect(Collectors.toList());

                mLoanList.forEach(ml -> {
                    LoanPayment getLastPay = ml.getLoanPayments()
                            .stream().filter(p -> p.isIsLast()).findAny().orElse(null);

                    //==================IF LOAN INSTALLMENT IS 0.00 OR LESS THEN AVOID MEMBER LOAN FROM PAYING=======
                    if (ml.getLoanInstallment() <= 0.0) {
                        return;
                    }
                    //==================IF LOAN INSTALLMENT IS 0.00 OR LESS THEN AVOID MEMBER LOAN FROM PAYING=======

                    if (getLastPay != null) {
                        updatePreviousInstallmentsOfMemberLoan(session, getLastPay);
                        LoanPayment lp = new LoanPayment();
                        lp.setChequeNo(chk_no_txt.getText());
                        lp.setMemberLoan(ml);
                        lp.setPaymentDate(new java.util.Date());
                        lp.setIsLast(true);
                        lp.setInstallmentDue((ml.getNoOfRepay() - (getLastPay.getInstallmentNo() + 1)) >= 0 ? (ml.getNoOfRepay() - (getLastPay.getInstallmentNo() + 1)) : 0);
                        lp.setInstallmentNo(getLastPay.getInstallmentNo() + 1);
                        lp.setInstallmentDate(getInstallmentDate(getLastPay.getInstallmentDate()));
                        lp.setPaidAmt(ml.getLoanInstallment());
                        lp.setListedPay(getListedPayFrom(ml, session, lp.getInstallmentDue() == 0));
                        lp.setPayOffice(m.getPayOffice().getId());
                        lp.setWorkOffice(m.getBranch().getId());
                        lp.setRemark(lp.getInstallmentDue() == 0 ? "Arrears Pay" : "Installment Pay");
                        session.save(lp);

                        //========================Kota left checks if amount paying less than the installment, then assign to kotaya===============================
                        double kota_left = (lp.getListedPay() > lp.getPaidAmt()) ? (lp.getListedPay() - lp.getPaidAmt()) : 0.0;
                        double kotaTotNow = updateMemberLoan(ml, kota_left, session, (lp.getInstallmentDue() == 0 && (lp.getPaidAmt() == lp.getListedPay())), getInstallmentDate(getLastPay.getInstallmentDate()));

                        //end loan if installments completed and no kota left......
                        if (lp.getInstallmentDue() == 0 && kotaTotNow == 0) {
                            endLoan(session, ml);
                        }
                    } else {

                        LoanPayment lp = new LoanPayment();
                        lp.setChequeNo(chk_no_txt.getText());
                        lp.setMemberLoan(ml);
                        lp.setPaymentDate(new java.util.Date());
                        lp.setIsLast(true);
                        lp.setPaidAmt(ml.getLoanInstallment());
                        lp.setListedPay(getListedPayFrom(ml, session, false));
                        lp.setInstallmentDate(getDayOfMonth(Date.valueOf(chk_of_month_chooser.getValue())));

                        //=================IF MEMBER LOAN IS OLD LOAN THEN====================
                        if (ml.isOldLoan()) {

                            lp.setInstallmentDue(ml.getNoOfRepay() - ml.getLastInstall());
                            lp.setInstallmentNo(ml.getLastInstall() + 1);
                            //updateOldLoan(ml, session, ml.getLoanInstallment());
                        } else {

                            lp.setInstallmentDue(ml.getNoOfRepay() - 1);
                            lp.setInstallmentNo(1);
                        }

                        lp.setPayOffice(m.getPayOffice().getId());
                        lp.setWorkOffice(m.getBranch().getId());
                        lp.setRemark("Installment Pay");
                        session.save(lp);

                        //========================Kota left checks if amount paying less than the installment, then assign to kotaya===============================
                        double kota_left = (lp.getListedPay() > lp.getPaidAmt()) ? (lp.getListedPay() - lp.getPaidAmt()) : 0.0;
                        updateMemberLoan(ml, kota_left, session, false, getDayOfMonth(Date.valueOf(chk_of_month_chooser.getValue())));
                    }
                });

                //=======================END MEMBERS LOANS SAVE==============================================
            });

            session.getTransaction().commit();
            session.close();
            Alert alert_error = new Alert(Alert.AlertType.INFORMATION);
            alert_error.setTitle("Information Message");
            alert_error.setHeaderText("Successfully Updated !");
            alert_error.setContentText("You have successfully updated the payments of " + search_txt.getText().split("-")[1]
                    + " of this month.");
            Optional<ButtonType> result = alert_error.showAndWait();
            if (result.get() == ButtonType.OK) {
                //GENERATE REPORT AFTER PAYMENT======
                generateCollectionPaymentReport(payCheque.getChequeNo());
                FxUtilsHandler.clearFields(collection_grid);
                //  collection_tbl.getItems().clear();
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

        if (search_txt.getText() != null && !search_txt.getText().isEmpty()) {
            ImageView progressIndicator = new ImageView();
            progressIndicator.setImage(new Image(FileHandler.LOADING_DEFAULT_GIF));
            VBox v = new VBox(progressIndicator);
            v.setAlignment(Pos.CENTER);
            table_bpane.setCenter(v);

            Task<List<Member>> mTask = new Task<List<Member>>() {

                {
                    setOnSucceeded(d -> {
                        AtomicDouble ins_total = new AtomicDouble(0.0);
                        AtomicDouble sub_total = new AtomicDouble(0.0);
                        List<Member> mList = getValue();

                        mList.stream().forEach(m -> {
                            ins_total.addAndGet(m.getMemberLoans().stream().mapToDouble(MemberLoan::getLoanInstallment).sum());

                            List<MemberSubscriptions> mbrSubs = new ArrayList<>(m.getMemberSubscriptions());

                            boolean flag = FxUtilsHandler.previousSubscriptions(m.getId()).isEmpty();
                            if (flag) {
                                sub_total.addAndGet(mbrSubs.stream().mapToDouble(a -> a.getAmount()).sum());
                            } else {
                                sub_total.addAndGet(mbrSubs.stream().filter(s -> !s.getRepaymentType()
                                        .equalsIgnoreCase("Once")).mapToDouble(a -> a.getAmount()).sum());
                            }

                        });
                        total = ins_total.doubleValue() + sub_total.doubleValue();
                        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));

                        Pagination paginationTable = initCollectionTable(mList);
                        table_bpane.getChildren().remove(0);
                        table_bpane.setCenter(paginationTable);
                    });

                    setOnFailed(workerStateEvent -> getException().printStackTrace());
                }

                @Override
                protected List<Member> call() throws Exception {
                    return memberList();
                }

            };

            Thread mThread = new Thread(mTask, "m-task");
            mThread.setDaemon(true);
            mThread.start();
        }
    }

    private List<Member> memberList() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        c.createAlias("branch", "b");
        c.createAlias("payOffice", "po");

        //=====================================REMOVED DUE TO UNNASSARY FILTER=================================
        //  c.createAlias("memberLoans", "ml");
        //  c.add(Restrictions.eq("ml.isComplete", false));
        //        c.add(Restrictions.disjunction()
        //                .add(Restrictions.le("ml.paidUntil", new java.util.Date()))
        //                .add(Restrictions.isNull("ml.paidUntil"))
        //        );
        //=====================================REMOVED DUE TO UNNASSARY FILTER==================================
        int selected = search_typ_combo.getSelectionModel().getSelectedIndex();
        switch (selected) {
            case 0:
                c.add(Restrictions.disjunction()
                        //===================SEARCH CHANGED TO PAYMENT OFFICE INSTEAD OF USER BRANCH===================
                        // .add(Restrictions.eq("b.branchName", search_txt.getText())));
                        .add(Restrictions.eq("po.branchCode", search_txt.getText().split("-")[0])));
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

        c.add(Restrictions.eq("status", true));
        c.add(Restrictions.eq("b.status", true));
        c.addOrder(Order.asc("b.branchCode"));
        List<Member> mList = c.list();

        List<Member> filteredList = mList.stream()
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberId()))
                .collect(Collectors.toList());

        session.close();
        return filteredList;
    }

    private Node createPage(int pageIndex, List<Member> mList) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, mList.size());

        m_id_col.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        m_name_col.setCellValueFactory(new PropertyValueFactory<>("nameWithIns"));
        overpay_col.setCellValueFactory((TableColumn.CellDataFeatures<Member, Double> param) -> {
            return new SimpleObjectProperty(param.getValue().getZeroOverpay());
        });
        total_pay_col.setCellValueFactory(new SubscriptionValueFactory());
        sub_tot_col.setCellValueFactory(new SubTotalValueFactory());
        colection_stat_col.setCellValueFactory((TableColumn.CellDataFeatures<Member, CheckBox> param) -> {
            Member ml = param.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(ml.isCollected());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                ml.setCollected(new_val);
                double selectedRowTot = ml.getTotalSubscription() + ml.getTotalPayment();
                bindSubTotalTo(chk_amt_txt, selectedRowTot, new_val);
            });
            return new SimpleObjectProperty<>(checkBox);
        });
        detail_view_col.setCellValueFactory(new DisplaySubscriptionFactory(collection_tbl, total, chk_amt_txt));
        rtot_pay_col.setCellValueFactory(new DisplayTotalInstallmentsFactory(collection_tbl, total, chk_amt_txt));
        tot_inst_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<Member, String> param) -> {
            Member ml = param.getValue();
            List<MemberLoan> instOnly = ml.getMemberLoans().stream()
                    .sorted(Comparator.comparing(MemberLoan::getChildId).reversed())
                    .filter(p -> !p.isIsComplete())
                    // .filter(FxUtilsHandler.checkIfLastPaidDateWithinCurrentMonth(p -> p.getPaidUntil()))
                    .filter(p -> p.isStatus())
                    .filter(p -> (p.getLastInstall() + 1) < p.getLoanDuration())
                    .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                    .collect(Collectors.toList());

            //===================installments finished, but kotaonly loans================
            List<MemberLoan> kotaOnly = ml.getMemberLoans().stream()
                    .sorted(Comparator.comparing(MemberLoan::getChildId).reversed())
                    .filter(p -> !p.isIsComplete())
                    // .filter(FxUtilsHandler.checkIfLastPaidDateWithinCurrentMonth(p -> p.getPaidUntil()))
                    .filter(p -> p.isStatus())
                    .filter(p -> (p.getLastInstall() + 1) >= p.getLoanDuration())
                    .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                    .collect(Collectors.toList());

            double sum = instOnly.stream().mapToDouble(p -> p.getLoanInstallment()).sum()
                    + kotaOnly.stream().mapToDouble(p -> p.getKotaLeft()).sum();

            return new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
        });

        wrking_of_col.setCellValueFactory((TableColumn.CellDataFeatures<Member, String> param) -> {
            Member ml = param.getValue();
            return new SimpleObjectProperty<>(ml.getBranch().getBranchName());
        });

        overpay_col.setCellFactory(TextFieldTableCell.<Member, Double>forTableColumn(new CustomDoubleStringConverter()));

        overpay_col.setOnEditCommit((TableColumn.CellEditEvent<Member, Double> event) -> {
            if (event.getNewValue() != null) {

                double diff = event.getNewValue() - event.getOldValue();

                event.getTableView().getItems()
                        .get(event.getTablePosition().getRow())
                        .setZeroOverpay(event.getNewValue());

                total = total + diff;
                chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
            } else {
                overpay_col.getTableView().refresh();
            }

        });

        ObservableList<Member> mlz = FXCollections.observableArrayList(mList.subList(fromIndex, toIndex));
        fList = new FilteredList<>(mlz, p -> true);
        collection_tbl.setItems(fList);
        return new BorderPane(collection_tbl);
    }

    private Pagination initCollectionTable(List<Member> mList) {

        pagination.setVisible(true);
        pagination.setPageCount(mList.size() / rowsPerPage + 1);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory((Integer param) -> createPage(param, mList));
        return pagination;
    }

    private void bindSubTotalTo(TextField chk_amt_txt, double value, boolean b) {

        if (b) {

            total += value;
        } else {

            total -= value;
        }

        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
    }

    private void bindSubTotalTo(TextField chk_amt_txt) {

        AtomicDouble ins_total = new AtomicDouble(0.0);
        AtomicDouble sub_total = new AtomicDouble(0.0);
        List<Member> mList = collection_tbl.getItems();

        mList.stream().forEach(m -> {
            ins_total.addAndGet(m.getMemberLoans().stream().mapToDouble(MemberLoan::getLoanInstallment).sum());

            List<MemberSubscriptions> mbrSubs = new ArrayList<>(m.getMemberSubscriptions());

            boolean flag = FxUtilsHandler.previousSubscriptions(m.getId()).isEmpty();
            if (flag) {
                sub_total.addAndGet(mbrSubs.stream().mapToDouble(a -> a.getAmount()).sum());
            } else {
                sub_total.addAndGet(mbrSubs.stream().filter(s -> !s.getRepaymentType()
                        .equalsIgnoreCase("Once")).mapToDouble(a -> a.getAmount()).sum());
            }

        });
        total = ins_total.doubleValue() + sub_total.doubleValue();
        chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
    }

    private void performSearchTextActive(boolean b) {
        search_txt.setDisable(!b);
        search_txt.setText(null);
    }

    private void figureNLoadSearchTextSuggestions(int selectedIndex) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Branch.class);
        c.add(Restrictions.eq("status", true));
        switch (selectedIndex) {
            case 0:
                // c.setProjection(Projections.property("branchName"));
                c.add(Restrictions.eq("parentId", 0));
                List<Branch> bNames = c.list();
                //=== ADDED NEW RESTRICTION TO GET ONLY PAYMENT OFFICES INTO AUTO-COMPLETE==========
                autoCompletionList(bNames);
                break;
            case 1:
                c.setProjection(Projections.property("memberId"));
                List<String> mIds = c.list();
                // autoCompletionList(mIds);
                break;
            case 2:
                c.setProjection(Projections.property("nameWithIns"));
                List<String> mNames = c.list();
                // autoCompletionList(mNames);
                break;
        }
        session.close();
    }

    private void autoCompletionList(List<Branch> lst) {
        if (bindAutoCompletion != null) {
            bindAutoCompletion.dispose();
        }
        List<String> collect = lst.stream()
                .map(b -> b.getBranchCode() + "-" + b.getBranchName())
                .collect(Collectors.toList());
        HashSet<String> set = new HashSet<>(collect);
        bindAutoCompletion = TextFields.bindAutoCompletion(search_txt, set);
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
        Predicate<String> predicate = (t) -> {
            return !getCList().contains(t);
        };
        Predicate<String> p_cheque_tally = (t) -> {
            return Objects.equals(TextFormatHandler.getCurrencyFieldValue(t), TextFormatHandler.getCurrencyFieldValue(chk_amt_txt));
        };
        validationSupport.registerValidator(chk_amt_txt,
                Validator.createEmptyValidator("This field is not optional !")
        );
        validationSupport.registerValidator(user_enter_pay,
                Validator.combine(
                        Validator.createEmptyValidator("This field is not optional."),
                        Validator.createPredicateValidator(p_cheque_tally, "Cheque amount and total payment should tally to proceed.")
                ));
        validationSupport.registerValidator(chk_no_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional !"),
                        Validator.createRegexValidator("Only alphanumeric and hyphen(-) allowed !",
                                "^[a-zA-Z0-9\\-]*$", Severity.ERROR),
                        Validator.createPredicateValidator(predicate, "This cheque is already used !")
                ));
        validationSupport.registerValidator(chk_date_chooser,
                Validator.createEmptyValidator("Cheque realise date is required !"));
        validationSupport.registerValidator(chk_of_month_chooser,
                Validator.createEmptyValidator("Cheque date is required !"));
        validationSupport.registerValidator(branch_txt,
                Validator.createEmptyValidator("Branch is not optional !"));
        validationSupport.registerValidator(bank_code_txt,
                Validator.createEmptyValidator("Bank code is not optional !"));
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

    private List<String> getCList() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(LoanPayCheque.class);
        List<LoanPayCheque> list = c.add(Restrictions.eq("paymentType", "cheque")).list();
        List<String> collect = list.stream().map(LoanPayCheque::getChequeNo).collect(Collectors.toList());
        s.close();
        return collect;
    }

    private double updateMemberLoan(MemberLoan ml, double kota_left, Session session, boolean isKotaPay, java.util.Date payUntil) {
        boolean kotaKotaPay = (kota_left == 0) && isKotaPay;
        MemberLoan mll = (MemberLoan) session.load(MemberLoan.class, ml.getId());
        mll.setPaidUntil(payUntil);
        mll.setKotaLeft(kotaKotaPay ? 0.0 : (mll.getKotaLeft() + kota_left));
        mll.setLastInstall(mll.getLastInstall() + 1);
        session.update(mll);
//Return Current kota amount=====================
        return mll.getKotaLeft();
    }

    private java.util.Date getInstallmentDate(java.util.Date lastInstDate) {
        DateTimeZone zone = DateTimeZone.forID("Asia/Colombo");
        DateTime lastInst = new DateTime(new SimpleDateFormat("yyyy-MM-dd").format(lastInstDate), zone);
        DateTime nowDate = lastInst.plusMonths(1);
        return nowDate.toDate();
    }

    private java.util.Date getDayOfMonth(java.util.Date instDate) {
        DateTimeZone zone = DateTimeZone.forID("Asia/Colombo");
        DateTime insDateE = new DateTime(new SimpleDateFormat("yyyy-MM-dd").format(instDate), zone);
        DateTime nowDate = insDateE.withDayOfMonth(25);
        return nowDate.toDate();
    }

    @FXML
    private void onUserEnterChequeMouseClicked(MouseEvent event) {
        user_enter_pay.selectRange(2, user_enter_pay.getText().length());
    }

    private void generateCollectionPaymentReport(String chkNo) {
        // String reportPath = "com/court/reports/BranchWiseCollectionMadeReport.jasper";

        String reportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "BranchWiseCollectionMadeReport.jasper";
        } catch (IOException ex) {
            Logger.getLogger(CollectionSheetFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Branch Wise Cheque Payments");
        map.put("chk_no", chkNo);
        ReportHandler rh = new ReportHandler(reportPath, map, null, con);
        boolean blah = rh.genReport();
        if (blah) {
            rh.viewReport();
        }
        s.close();
    }

    private Double getListedPayFrom(MemberLoan ml, Session s, boolean isKotaPay) {
        MemberLoan mml = (MemberLoan) s.load(MemberLoan.class, ml.getId());
        return isKotaPay ? mml.getKotaLeft() : mml.getLoanInstallment();
    }

    private void createTable() {

        collection_tbl = new TableView<>();
        m_id_col = new TableColumn<>("Mbr ID");
        m_id_col.setPrefWidth(65);
        m_name_col = new TableColumn<>("Name");
        m_name_col.setPrefWidth(150);
        colection_stat_col = new TableColumn<>("Collected");
        colection_stat_col.setPrefWidth(65);
        total_pay_col = new TableColumn<>("Monthly Subscription");
        total_pay_col.setPrefWidth(120);
        detail_view_col = new TableColumn<>("Subscription Details");
        detail_view_col.setPrefWidth(100);
        rtot_pay_col = new TableColumn<>("Installments Details");
        rtot_pay_col.setPrefWidth(100);
        tot_inst_amt_col = new TableColumn<>("Total Inst Amount");
        tot_inst_amt_col.setPrefWidth(120);
        sub_tot_col = new TableColumn<>("SubTotal");
        sub_tot_col.setPrefWidth(120);
        wrking_of_col = new TableColumn<>("Working Office");
        wrking_of_col.setPrefWidth(250);
        overpay_col = new TableColumn<>("OverPay(Rs)");
        overpay_col.setPrefWidth(100);

        collection_tbl.getColumns().addAll(m_id_col, m_name_col,
                wrking_of_col, total_pay_col, detail_view_col, tot_inst_amt_col,
                rtot_pay_col, sub_tot_col, overpay_col, colection_stat_col);
        collection_tbl.setEditable(true);
    }

    @FXML
    private void filterTableKeyRel(KeyEvent event) {
        if (fList != null) {
            fList.setPredicate(p -> p.getMemberId().toLowerCase()
                    .contains(tbl_filter_txt.getText().toLowerCase().trim()));
        }
    }

    private void updateMemberOverPay(Member m, Session session) {
        Member mm = (Member) session.load(Member.class, m.getId());
        //   mm.setOverpay(mm.getOverpay() + m.getOverpay());
        mm.setOverpay(mm.getOverpay() + m.getZeroOverpay());
        session.update(mm);
    }
}
