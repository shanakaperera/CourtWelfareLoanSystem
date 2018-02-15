/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.Benifits;
import com.court.handler.DocSeqHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.FileHandler;
import com.court.handler.ImageWithString;
import com.court.handler.LoggedSessionHandler;
import com.court.handler.ProgressIndicatorBar;
import com.court.handler.PropHandler;
import com.court.handler.ReportHandler;
import com.court.handler.Spouse;
import com.court.handler.TextFormatHandler;
import com.court.model.Branch;
import com.court.model.ClosedLoan;
import com.court.model.Document;
import com.court.model.Loan;
import com.court.model.LoanPayment;
import com.court.model.MemChild;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.court.model.MemberSubscription;
import com.court.model.MemberSubscriptions;
import com.court.model.ReceiptPay;
import com.court.model.SubscriptionPay;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class MemberfxmlController implements Initializable {

    private static final String TABLE_NAME_M = "member";
    private static final String TABLE_NAME_DOC = "document";
    private ValidationSupport validationSupport, va, va_msub;
    private ImageWithString imgString = null;
    @FXML
    private TextField member_full_name_txt;
    @FXML
    private TextField member_namins_txt;
    @FXML
    private TextArea member_adrs_txt;
    @FXML
    private TextField member_tel1_txt;
    @FXML
    private TextField member_tel2_txt;
    @FXML
    private TextField member_email_txt;
    @FXML
    private TextField member_job_txt;
    @FXML
    private TextField member_code_txt;
    @FXML
    private ComboBox<String> member_sex_combo;
    @FXML
    private ComboBox<String> member_maritial_combo;
    @FXML
    private DatePicker member_bday_chooser;
    @FXML
    private DatePicker member_apo_chooser;
    @FXML
    private DatePicker member_join_chooser;
    @FXML
    private TextArea member_des_txt;
    @FXML
    private ImageView member_img;
    @FXML
    private TextField member_brch_txt;
    @FXML
    private GridPane main_grid_pane;
    @FXML
    private GridPane search_grid_pane;
    @FXML
    private HBox tel_hbox;
    @FXML
    private HBox date_hbox;
    @FXML
    private TextField member_code_srch_txt;
    @FXML
    private TextField member_name_srch_txt;
    @FXML
    private Button member_deactive_btn;
    @FXML
    private Label deactive_label;
    @FXML
    private Tab loan_inf_tab;
    @FXML
    private Tab doc_tab;
    @FXML
    private TableView<MemberLoan> l_taken_tbl;
    @FXML
    private TableColumn<MemberLoan, String> l_id_col;
    @FXML
    private TableColumn<MemberLoan, Date> g_date_col;
    @FXML
    private TableColumn<MemberLoan, String> l_type_col;
    @FXML
    private TableColumn<MemberLoan, Double> l_amt_col;
    @FXML
    private TableColumn<MemberLoan, Boolean> l_stat_col;
    @FXML
    private TableColumn<MemberLoan, String> remark_col;
    @FXML
    private TableColumn<MemberLoan, Double> l_bal_con;
    @FXML
    private TextField loan_id_txt;
    @FXML
    private TextField g_date_txt;
    @FXML
    private ListView<Member> gurantors_lstview;
    @FXML
    private TextField l_amount_txt;
    @FXML
    private TextField l_du_txt;
    @FXML
    private TextField l_type_txt;
    @FXML
    private TextField l_int_txt;
    @FXML
    private TextField l_repay_txt;
    @FXML
    private TableView<LoanPayment> l_pay_tbl;
    @FXML
    private TableColumn<LoanPayment, Date> p_date_col;
    @FXML
    private TableColumn<LoanPayment, Integer> ins_no_col;
    @FXML
    private TableColumn<LoanPayment, String> p_due_col;
    @FXML
    private TableColumn<LoanPayment, Date> instment_date_col;
    @FXML
    private TableColumn<LoanPayment, String> ins_remark_col;
    @FXML
    private VBox progress_box;
    @FXML
    private TextField int_pls_prin_txt;

    private LoggedSessionHandler hndler;
    @FXML
    private Button m_gen_sav_btn;
    @FXML
    private TextField doc_id_sr_txt;
    @FXML
    private TextField ln_id_sr_txt;
    @FXML
    private ComboBox<String> doc_type_combo;
    @FXML
    private TextField doc_id_txt;
    @FXML
    private TextField doc_typ_txt;
    @FXML
    private DatePicker doc_date_chooser;
    @FXML
    private ImageView doc_imgview;
    @FXML
    private TextField mbr_ln_txt;
    @FXML
    private CheckBox mbr_gen_chk;
    @FXML
    private CheckBox mbr_ln_chk;
    @FXML
    private TextField doc_desc_txt;

    private File docChoosen;
    @FXML
    private TableView<Document> doc_tbl_view;
    @FXML
    private TableColumn<Document, String> doc_id_col;
    @FXML
    private TableColumn<Document, String> doc_typ_col;
    @FXML
    private TableColumn<Document, String> doc_des_col;
    @FXML
    private TableColumn<Document, Date> doc_date_col;
    @FXML
    private TableColumn<Document, Button> doc_act_col;
    @FXML
    private TextField nic_no;
    @FXML
    private ComboBox<String> job_status_combo;
    @FXML
    private TextField payment_officer_txt;
    @FXML
    private Tab other_details_tab;
    @FXML
    private TableView<MemChild> children_tbl;
    @FXML
    private TextField mother_txt;
    @FXML
    private TextField father_txt;
    @FXML
    private TextField spouse_name_txt;
    @FXML
    private CheckBox s_hoi_check;
    @FXML
    private CheckBox s_aci_check;
    @FXML
    private TextField mem_fee_txt;
    @FXML
    private TextField sav_fee_txt;
    @FXML
    private TextField hoi_fee_txt;
    @FXML
    private TextField aci_fee_txt;
    @FXML
    private TextField optional_txt;
    @FXML
    private TextField adm_fee_txt;
    @FXML
    private ComboBox<String> mem_fee_repay_combo;
    @FXML
    private ComboBox<String> sav_fee_repay_combo;
    @FXML
    private ComboBox<String> hoi_fee_repay_combo;
    @FXML
    private ComboBox<String> aci_fee_repay_combo;
    @FXML
    private ComboBox<String> opt_fee_repay_combo;
    @FXML
    private ComboBox<String> adm_fee_repay_combo;
    @FXML
    private TextField mb_ben_name;
    @FXML
    private TextField ac_ben_name;
    @FXML
    private TextField mem_ben_rel;
    @FXML
    private TextField ac_ben_rel;
    @FXML
    private Button othr_d_sav_btn;
    @FXML
    private Button update_child_btn;
    @FXML
    private Button delete_child_btn;
    @FXML
    private VBox family_info_vbox;
    @FXML
    private GridPane family_gridbox;
    @FXML
    private TableColumn<MemChild, String> childName_col;
    @FXML
    private TableColumn<MemChild, Date> childDOB_col;
    @FXML
    private TableColumn<MemChild, String> childSexCol;
    @FXML
    private TableColumn<MemChild, Boolean> childHoi_col;
    @FXML
    private TableColumn<MemChild, Boolean> child_aci_col;
    @FXML
    private GridPane parents_box;
    @FXML
    private HBox spouse_box;
    @FXML
    private GridPane fee_box;
    @FXML
    private GridPane benifits_box;
    @FXML
    private TextField loan_nm_txt;
    @FXML
    private HBox nic_col_id;
    @FXML
    private TextField emp_id_txt;
    @FXML
    private HBox job_title_box;
    @FXML
    private HBox working_box;
    SuggestionProvider<String> p1, p2, p3, p4, p5, p6;
    @FXML
    private Tab mbrcon_tab;
    @FXML
    private TableView<SubscriptionPay> contr_tbl;
    @FXML
    private TableColumn<SubscriptionPay, String> con_paydate_col;
    @FXML
    private TableColumn<SubscriptionPay, Double> con_hoi_col;
    @FXML
    private TableColumn<SubscriptionPay, Double> con_acI_col;
    @FXML
    private TableColumn<SubscriptionPay, Double> con_savings_col;
    @FXML
    private TableColumn<SubscriptionPay, Double> con_mbr_col;
    @FXML
    private TableColumn<SubscriptionPay, Double> con_optional_col;
    @FXML
    private TableColumn<SubscriptionPay, Double> con_adm_col;
    @FXML
    private GridPane loan_info_grid;
    @FXML
    private HBox loan_name_hbox;
    @FXML
    private DatePicker m_subs_date;
    @FXML
    private TextField m_subs_hoi;
    @FXML
    private TextField m_subs_aci;
    @FXML
    private TextField m_subs_sav;
    @FXML
    private TextField m_subs_mf;
    @FXML
    private TextField m_subs_op;
    @FXML
    private GridPane subs_manual_grid;
    @FXML
    private TextField m_subs_adm;
    @FXML
    private Label tot_hoi_label;
    @FXML
    private Label tot_sav_label;
    @FXML
    private Label tot_aci_label;
    @FXML
    private Label tot_opt_label;
    @FXML
    private VBox invo_gen_box;
    @FXML
    private Label subs_invo_warning;
    @FXML
    private Button subs_invo_btn;
    @FXML
    private Tab gen_details_tab;

    private Branch payBranch;
    @FXML
    private Label bal_con_label;

    private ContGive cGive;

    List<String> memberCodes;

    private boolean isSearch = false;
    @FXML
    private TextField r_date_txt;
    @FXML
    private TextField bal_cont_txt;
    @FXML
    private Tab cash_out_tab;
    @FXML
    private Label credit_bal_txt;
    @FXML
    private TableView<MemberLoan> arrears_tbl;
    @FXML
    private TableColumn<MemberLoan, String> co_loan_id_col;
    @FXML
    private TableColumn<MemberLoan, String> co_arrears_amt_col;
    @FXML
    private TableColumn<MemberLoan, CheckBox> co_select_col;
    @FXML
    private Label tot_arrears_sel_txt;
    @FXML
    private TextField hnd_ovr_amt_txt;
    @FXML
    private ComboBox<String> member_status_combo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        va = new ValidationSupport();
        va_msub = new ValidationSupport();
        // registerInputValidation();
        fillMemberCodeTxt(member_code_txt);
        fillMemberDocCodeTxt(doc_id_txt);
        invo_gen_box.setVisible(false);
        ObservableList<Member> allMembers = getAllMembers();
        ObservableList<String> allBranches = getAllBranches();
        List<Document> allDocs = getAllDocuments();
        memberCodes = allMembers.stream()
                .filter(FxUtilsHandler.distinctByKey(Member::getMemberId))
                .map(Member::getMemberId).collect(Collectors.toList());
        List<String> memberNames = allMembers.stream()
                .map(Member::getFullName).collect(Collectors.toList());
//        List<String> memberJobs = allMembers.stream()
//                .filter(FxUtilsHandler.distinctByKey(Member::getJobTitle))
//                .map(Member::getJobTitle).collect(Collectors.toList());

        p1 = SuggestionProvider.create(memberCodes);
        p2 = SuggestionProvider.create(memberNames);
        p3 = SuggestionProvider.create(Arrays.asList("JUDGE", "REGISTAR", "IN:OFFICER", "MANEGEM", "ACCOUNT", "ACCOUNT ASSISTANT", "ADD:OFFICER", "CLERK", "TRANSLATOR", "STENO", "TYPIST", "BINDER", "PISCAL", "PROCESS", "MATRON", "INTERPRETER", "FAMILY", "DEV OFFICER", "REPORTER", "K.K.S", "PROGRAM", "DRIVER", "WATCHER", "LABOUR"));
        p4 = SuggestionProvider.create(allBranches);
        new AutoCompletionTextFieldBinding<>(member_code_srch_txt, p1);
        new AutoCompletionTextFieldBinding<>(member_name_srch_txt, p2);
        new AutoCompletionTextFieldBinding<>(member_job_txt, p3);

        if (!allBranches.isEmpty()) {
            member_brch_txt.setText("");
            member_brch_txt.setStyle("");
            member_brch_txt.setEditable(true);
            AutoCompletionTextFieldBinding<String> ab = new AutoCompletionTextFieldBinding<>(member_brch_txt, p4);
            ab.setOnAutoCompleted(evt -> {
                payment_officer_txt.setText(getPaymentOffice(evt.getCompletion()));
            });

        } else {
            member_brch_txt.setText("No Working places available. Add new branch !");
            member_brch_txt.setStyle("-fx-text-fill: red");
            member_brch_txt.setEditable(false);
        }
        p5 = SuggestionProvider.create(allDocs.stream()
                .map(d -> d.getDocType()).collect(Collectors.toList()));
        new AutoCompletionTextFieldBinding<>(doc_typ_txt, p5);

        FxUtilsHandler.setDatePickerTimeFormat(member_apo_chooser, member_bday_chooser,
                member_join_chooser, doc_date_chooser, m_subs_date);
        m_subs_date.setValue(TextFormatHandler.NowDate());

        ///Field formatters===================================
        mem_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        sav_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        hoi_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        aci_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        optional_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        adm_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());

        m_subs_hoi.setTextFormatter(TextFormatHandler.currencyFormatter());
        m_subs_aci.setTextFormatter(TextFormatHandler.currencyFormatter());
        m_subs_sav.setTextFormatter(TextFormatHandler.currencyFormatter());
        m_subs_mf.setTextFormatter(TextFormatHandler.currencyFormatter());
        m_subs_op.setTextFormatter(TextFormatHandler.currencyFormatter());
        m_subs_adm.setTextFormatter(TextFormatHandler.currencyFormatter());

        hnd_ovr_amt_txt.setTextFormatter(TextFormatHandler.currencyFormatter());

        //================== Disable Tabs============================
        disableTabs(true);
        mbr_gen_chk.setSelected(true);
        disableButtonWithLoggingPrv(DashBoardFxmlController.controller.loggedSession());
        bindValidationOnPaneControlFocus(nic_col_id, main_grid_pane, date_hbox, tel_hbox, working_box, job_title_box);
        bindFamilyValidationOnPaneControlFocus(fee_box);
        bindContValidationOnPaneControlFocus(subs_manual_grid);
        member_code_txt.requestFocus();
        member_code_srch_txt.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        member_name_srch_txt.setText("");
                    }
                });
        member_name_srch_txt.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        member_code_srch_txt.setText("");
                    }
                });
    }

    @FXML
    private void onMemberNewBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox, nic_col_id, working_box, job_title_box);
        identifyCodesEditable(true);
        fillMemberCodeTxt(member_code_txt);
        imgString = null;
        member_img.setImage(new Image(getClass().getResourceAsStream(FileHandler.MEMBER_DEFAULT_IMG)));
        FxUtilsHandler.activeDeactiveChildrenControls(true,
                main_grid_pane, date_hbox, tel_hbox);
        FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, true, true);
        deactive_label.setText("");
        disableTabs(true);
        isSearch = false;
    }

    @FXML
    private void onMemberSaveBtnAction(ActionEvent event) throws IOException {
        if (isValidationEmpty(validationSupport)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }

        Predicate<String> predicate = (t) -> {
            return memberCodes.contains(t);
        };

        if (!isSearch && predicate.test(member_code_txt.getText())) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Member already exist.");
            alert_error.setContentText("You have already saved this member " + member_code_txt.getText());
            alert_error.show();
            return;
        }

        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Member member;
            int id = getIdByMemberCode(session, member_code_txt.getText().trim());
            if (id != 0) {
                member = (Member) session.load(Member.class, id);
            } else {
                member = new Member();
                member.setMemberId(member_code_txt.getText().trim());
                member.setNic(nic_no.getText());
            }

            member.setEmpId(emp_id_txt.getText());
            member.setJobStatus(job_status_combo.getSelectionModel().getSelectedItem());
            //member.setPaymentOfficer(payment_officer_txt.getText());
            member.setFullName(member_full_name_txt.getText());
            member.setNameWithIns(member_namins_txt.getText());
            member.setAddress(member_adrs_txt.getText());
            member.setJobTitle(member_job_txt.getText());
            member.setBranch(getBranchByName(session, member_brch_txt.getText().trim()));
            member.setTel1(member_tel1_txt.getText());
            member.setTel2(member_tel2_txt.getText());
            member.setEmail(member_email_txt.getText());
            member.setSex(member_sex_combo.getSelectionModel().getSelectedItem());
            member.setCurStatus(member_status_combo.getSelectionModel().getSelectedItem());
            member.setMaritalStatus(member_maritial_combo.getSelectionModel().getSelectedItem());
            member.setDob(FxUtilsHandler.getDateFrom(member_bday_chooser.getValue()));
            member.setAppintedDate(Date.valueOf(member_apo_chooser.getValue()));
            member.setJoinedDate(FxUtilsHandler.getDateFrom(member_join_chooser.getValue()));
            member.setOverpay(0.0);
            member.setZeroOverpay(0.0);
            member.setStatus(member.getCurStatus().equalsIgnoreCase("Active"));
            member.setDescription(member_des_txt.getText().isEmpty() ? "No Description" : member_des_txt.getText());
            //  member.setImgPath(imgString == null ? "" : imgString.getImg_path().toString());
            //   member.setStatus(true);
            if (payBranch != null) {
                member.setPayOffice(payBranch);
            }
            session.saveOrUpdate(member);
            session.getTransaction().commit();
            session.close();

            Alert alert_info = new Alert(Alert.AlertType.INFORMATION);
            alert_info.setTitle("Information");
            alert_info.setHeaderText("Successfully Saved !");
            alert_info.setContentText("You have successfully saved member \""
                    + member_namins_txt.getText() + "\"");
            Optional<ButtonType> result = alert_info.showAndWait();
            if (result.get() == ButtonType.OK) {
                FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox, nic_col_id, working_box, job_title_box);
                fillMemberCodeTxt(member_code_txt);
                imgString = null;
                member_img.setImage(new Image(getClass().getResourceAsStream(FileHandler.MEMBER_DEFAULT_IMG)));

                ObservableList<Member> allMembers = getAllMembers();
                List<String> memberCodes = allMembers.stream()
                        .map(Member::getMemberId).collect(Collectors.toList());
                List<String> memberNames = allMembers.stream()
                        .map(Member::getFullName).collect(Collectors.toList());
                List<String> memberJobs = allMembers.stream()
                        .filter(FxUtilsHandler.distinctByKey(p -> p.getJobTitle()))
                        .map(Member::getJobTitle).collect(Collectors.toList());
                p1.clearSuggestions();
                p1.addPossibleSuggestions(memberCodes);
                p2.clearSuggestions();
                p2.addPossibleSuggestions(memberNames);
                p3.clearSuggestions();
                p3.addPossibleSuggestions(memberJobs);
                identifyCodesEditable(true);
                isSearch = false;
                disableTabs(true);
                FxUtilsHandler.activeDeactiveChildrenControls(true, main_grid_pane, date_hbox, tel_hbox);
                FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, true, true);
                deactive_label.setText("");

            }
        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Missing Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("missing_fields"));
            alert_error.show();
        }
    }

    @FXML
    private void onMemberDeactiveBtnAction(ActionEvent event) throws IOException {
        if (isValidationEmpty(validationSupport)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Invalid Entries !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Member prfm_action = (Member) session.load(Member.class,
                    getIdByMemberCode(session, member_code_txt.getText()));
            boolean set_status = !prfm_action.isStatus();
            boolean cur_job_stat = !prfm_action.getCurStatus().equalsIgnoreCase("Active");
            prfm_action.setStatus(set_status);
            if (set_status && cur_job_stat) {
                prfm_action.setCurStatus("Active");
            }
            session.update(prfm_action);
            session.getTransaction().commit();
            session.close();

            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            String what_happened = set_status ? "Activated" : "Deactivated";
            alert_inf.setHeaderText("Successfully " + what_happened + "!");
            alert_inf.setContentText("You have successfully " + what_happened + " the member "
                    + member_namins_txt.getText() + "!");
            Optional<ButtonType> result = alert_inf.showAndWait();
            if (result.get() == ButtonType.OK) {
                //deactivation proccess----------
                FxUtilsHandler.activeDeactiveChildrenControls(set_status, main_grid_pane, date_hbox, tel_hbox);
                FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, set_status, true);
                if (set_status && cur_job_stat) {
                    member_status_combo.getSelectionModel().select("Active");
                    deactive_label.setText("");
                }
                if (!set_status) {
                    deactive_label.setText("Member Deactivated .");
                }
                if (set_status && !member_status_combo.getSelectionModel().getSelectedItem().equalsIgnoreCase("Active")) {
                    deactive_label.setText("Member " + member_status_combo.getSelectionModel().getSelectedItem() + " .");
                }

            }
        }
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox, benifits_box, fee_box, parents_box, loan_name_hbox, loan_info_grid, spouse_box, nic_col_id, working_box, job_title_box);
        identifyCodesEditable(true);
        fillMemberCodeTxt(member_code_txt);
        imgString = null;
        isSearch = false;
        member_img.setImage(new Image(getClass().getResourceAsStream(FileHandler.MEMBER_DEFAULT_IMG)));
        FxUtilsHandler.activeDeactiveChildrenControls(true,
                main_grid_pane, date_hbox, tel_hbox);
        FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, true, true);
        deactive_label.setText("");
        disableTabs(true);
    }

    @FXML
    private void onImgChooseAction(ActionEvent event) throws MalformedURLException, IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            imgString = FileHandler.
                    getImageBy(file, member_code_txt.getText().trim(), FileHandler.MEMBER_IMG_PATH);
            member_img.setImage(imgString.getImg());

        }
    }

    public void buildMemberLoanTable() {

        List<MemberLoan> allMemberLoans = getAllMemberLoans(member_code_txt.getText());

        p5.clearSuggestions();
        p5.addPossibleSuggestions(allMemberLoans
                .stream().map(p -> p.getMemberLoanCode()).collect(Collectors.toList()));

        List<MemberLoan> filteredCollection = allMemberLoans
                .stream()
                .filter(FxUtilsHandler.distinctByKey(p -> p.getId()))
                .collect(Collectors.toList());
        initMemberLoanTable(FXCollections.observableArrayList(filteredCollection));

        ///==============BUILD ARREARS TABLE===================
        initArreasTable(allMemberLoans);

    }

    @FXML
    private void onMemberSearchBtnAction(ActionEvent event) throws MalformedURLException {
        FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox, benifits_box, fee_box, parents_box, loan_name_hbox, loan_info_grid, spouse_box, nic_col_id, working_box, job_title_box);
        clearProgressBar();
        getMemberByCodeOrName(member_code_srch_txt.getText(), member_name_srch_txt.getText());
        buildMemberLoanTable();
        identifyCodesEditable(false);
        initDocTable(FXCollections.observableArrayList(getAllDocumentsOf(member_code_txt.getText())));
        initMemChildTable(getChildrenOfMember(member_code_txt.getText()));
        initContributionTable(FXCollections.observableArrayList(getAllContributionsOf(member_code_txt.getText())));
        isSearch = true;
    }

    private void getMemberByCodeOrName(String mCode, String mName) throws MalformedURLException {
        if (mCode.trim().isEmpty() && mName.trim().isEmpty()) {
            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            alert_inf.setHeaderText("Search Fields are empty !");
            alert_inf.setContentText("You can filter a member by his/her code or her name by given fields.");
            Optional<ButtonType> result = alert_inf.showAndWait();
            if (result.get() == ButtonType.OK) {
                return;
            }

        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        if (!mCode.trim().isEmpty()) {
            c.add(Restrictions.eq("memberId", mCode));
        }
        if (!mName.trim().isEmpty()) {
            c.add(Restrictions.eq("fullName", mName));
        }

        Member filteredMember = (Member) c.uniqueResult();
        if (filteredMember != null) {
            imgString = new ImageWithString();

            member_code_txt.setText(filteredMember.getMemberId());
            member_full_name_txt.setText(filteredMember.getFullName());
            member_namins_txt.setText(filteredMember.getNameWithIns());
            nic_no.setText(filteredMember.getNic());
            emp_id_txt.setText(filteredMember.getEmpId());
            payment_officer_txt.setText(filteredMember.getPayOffice().getBranchName());
            job_status_combo.getSelectionModel().select(filteredMember.getJobStatus());
            member_adrs_txt.setText(filteredMember.getAddress());
            member_job_txt.setText(filteredMember.getJobTitle());
            member_brch_txt.setText(filteredMember.getBranch().getBranchName());
            member_tel1_txt.setText(filteredMember.getTel1());
            member_tel2_txt.setText(filteredMember.getTel2());
            member_email_txt.setText(filteredMember.getEmail());
            member_sex_combo.getSelectionModel().select(filteredMember.getSex());
            member_status_combo.getSelectionModel().select(filteredMember.getCurStatus());
            member_maritial_combo.getSelectionModel().select(filteredMember.getMaritalStatus());
            member_bday_chooser.setValue(FxUtilsHandler.getLocalDateFrom(filteredMember.getDob()));
            member_join_chooser.setValue(FxUtilsHandler.getLocalDateFrom(filteredMember.getJoinedDate()));
            member_apo_chooser.setValue(FxUtilsHandler.getLocalDateFrom(filteredMember.getAppintedDate()));
            member_des_txt.setText(filteredMember.getDescription());
            credit_bal_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(filteredMember.getOverpay()));
            if (filteredMember.getImgPath() != null && !filteredMember.getImgPath().trim().isEmpty()) {
                imgString.setImg_path(new File(filteredMember.getImgPath()).toPath());
                member_img.setImage(new Image(new File(filteredMember.getImgPath()).toURI().toURL().toString()));
            }
            mother_txt.setText(filteredMember.getMother());
            father_txt.setText(filteredMember.getFather());

            if (filteredMember.getSpouse() != null) {
                Spouse spouse = new Gson().fromJson(filteredMember.getSpouse(), Spouse.class);
                if (spouse != null) {
                    spouse_name_txt.setText(spouse.getSpouse());
                    s_hoi_check.setSelected(spouse.isHoi());
                    s_aci_check.setSelected(spouse.isAci());
                }
            }
            if (filteredMember.getMemBenifits() != null) {
                Benifits mb = new Gson().fromJson(filteredMember.getMemBenifits(), Benifits.class);
                if (mb != null) {
                    mb_ben_name.setText(mb.getName());
                    mem_ben_rel.setText(mb.getRelation());
                }
            }

            if (filteredMember.getAccBenifits() != null) {
                Benifits ab = new Gson().fromJson(filteredMember.getAccBenifits(), Benifits.class);
                if (ab != null) {
                    ac_ben_name.setText(ab.getName());
                    ac_ben_rel.setText(ab.getRelation());
                }
            }

            deactive_label.setText(filteredMember.isStatus() ? "" : "Member Deactivated .");

            if (!filteredMember.isStatus() && !filteredMember.getCurStatus().equalsIgnoreCase("Active")) {
                deactive_label.setText("Member " + filteredMember.getCurStatus() + " .");
            }

            Set<MemberSubscriptions> ms = filteredMember.getMemberSubscriptions();

            for (MemberSubscriptions m : ms) {

                switch (m.getMemberSubscription().getFeeName()) {
                    case "Membership Fee":
                        mem_fee_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(m.getAmount()));
                        mem_fee_repay_combo.getSelectionModel().select(m.getRepaymentType());
                        break;
                    case "Savings Fee":
                        sav_fee_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(m.getAmount()));
                        sav_fee_repay_combo.getSelectionModel().select(m.getRepaymentType());
                        break;
                    case "HOI Fee":
                        hoi_fee_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(m.getAmount()));
                        hoi_fee_repay_combo.getSelectionModel().select(m.getRepaymentType());
                        break;
                    case "ACI Fee":
                        aci_fee_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(m.getAmount()));
                        aci_fee_repay_combo.getSelectionModel().select(m.getRepaymentType());

                        break;
                    case "Optional":
                        optional_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(m.getAmount()));
                        opt_fee_repay_combo.getSelectionModel().select(m.getRepaymentType());
                        break;
                    case "Admission Fee":
                        adm_fee_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(m.getAmount()));
                        adm_fee_repay_combo.getSelectionModel().select(m.getRepaymentType());
                        break;
                }
            }

            FxUtilsHandler.activeDeactiveChildrenControls(filteredMember.isStatus(),
                    main_grid_pane, date_hbox, tel_hbox);
            FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, filteredMember.isStatus(), true);
            disableTabs(false);
        } else {
            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            alert_inf.setHeaderText("No data found!");
            alert_inf.setContentText("The member you have requested is not found in our database.");
            alert_inf.show();
        }
        session.close();
    }

    private int getIdByMemberCode(Session session, String memberCode) {
        Criteria c = session.createCriteria(Member.class);
        c.add(Restrictions.eq("memberId", memberCode));
        Member filteredMember = (Member) c.uniqueResult();
        return filteredMember != null ? filteredMember.getId() : 0;
    }

    private ObservableList<Member> getAllMembers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        c.setProjection(Projections.projectionList()
                .add(Projections.property("memberId"), "memberId")
                .add(Projections.property("fullName"), "fullName")
                .add(Projections.property("jobTitle"), "jobTitle")
        );
        c.setResultTransformer(Transformers.aliasToBean(Member.class));
        List<Member> mList = c.list();
        ObservableList<Member> members = FXCollections.observableArrayList(mList);
        session.close();
        return members;
    }

    private ObservableList<String> getAllBranches() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Branch.class);
        c.add(Restrictions.eq("status", true));
        c.setProjection(Projections.property("branchName"));
        List<String> bList = c.list();
        ObservableList<String> branches = FXCollections.observableArrayList(bList);
        session.close();
        return branches;
    }

    private Branch getBranchByName(Session session, String branchName) {
        Criteria c = session.createCriteria(Branch.class);
        c.add(Restrictions.eq("branchName", branchName));
        Branch filteredBrnch = (Branch) c.uniqueResult();
        return filteredBrnch;
    }

    private Member getMemberByCode(String mCode) {
        Session ses = HibernateUtil.getSessionFactory().openSession();
        Criteria c = ses.createCriteria(Member.class);
        c.add(Restrictions.eq("memberId", mCode));
        Member filteredM = (Member) c.uniqueResult();
        ses.close();
        return filteredM;
    }

    private Member getMemberByCode(String mCode, Session s) {
        Criteria c = s.createCriteria(Member.class);
        c.add(Restrictions.eq("memberId", mCode));
        Member filteredM = (Member) c.uniqueResult();
        return filteredM;
    }

    private void getMemberLoanByCode(String mlCode, int childId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.add(Restrictions.eq("memberLoanCode", mlCode));
        c.add(Restrictions.eq("childId", childId));
        c.setMaxResults(1);
        MemberLoan ml = (MemberLoan) c.uniqueResult();
        if (ml != null) {
            double prins_plus_ins = ml.getLoanInstallment() * ml.getNoOfRepay();
            gurantors_lstview.getItems().clear();
            loan_id_txt.setText(ml.getMemberLoanCode());
            g_date_txt.setText(new SimpleDateFormat("yyyy-MM-dd").format(ml.getGrantedDate()));
            r_date_txt.setText(ml.getlRequested() != null ? new SimpleDateFormat("yyyy-MM-dd").format(ml.getlRequested()) : "");
            l_type_txt.setText(ml.getInterestMethod());
            l_amount_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanAmount()));
            l_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(ml.getLoanInterest() / 100) + " " + ml.getInterestPer());
            l_du_txt.setText(ml.getLoanDuration() + " " + ml.getDurationPer());
            int_pls_prin_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(prins_plus_ins));
            bal_cont_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getKotaLeft()));
            loan_nm_txt.setText(ml.getLoanName());
            List<Member> signedGuarantors = getSignedGuarantors(ml.getGuarantors(), session);
            if (signedGuarantors != null) {
                gurantors_lstview.getItems().addAll(signedGuarantors);
            }

            double ins_only = prins_plus_ins - ml.getLoanAmount();
            l_repay_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                    .format(ml.getLoanInstallment()) + "( " + TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format((ml.getLoanInstallment() - FxUtilsHandler.roundNumber((ins_only / ml.getLoanDuration()), 0))) + " + " + TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(FxUtilsHandler.roundNumber((ins_only / ml.getLoanDuration()), 0)) + " )");

            Criteria cl = session.createCriteria(LoanPayment.class);
            cl.createAlias("memberLoan", "ml");
            // cl.add(Restrictions.eq("ml.memberLoanCode", ml.getMemberLoanCode()));
            cl.add(Restrictions.eq("ml.id", ml.getId()));
            List<LoanPayment> filteredList = cl.list();

            if (!filteredList.isEmpty()) {
                List<LoanPayment> collect = filteredList.stream()
                        .filter(FxUtilsHandler.distinctByKey(p -> p.getInstallmentNo()))
                        .collect(Collectors.toList());

                double tot_pay_lo = (ml.getLoanInstallment() * ml.getNoOfRepay());

                Double paymentDue = tot_pay_lo - collect.stream()
                        .mapToDouble(LoanPayment::getPaidAmt).sum();

                // System.out.println("ESTIMATE - " + paymentDue);
                double loanComplete = ml.isClosedLoan() ? 1.0 : (paymentDue / tot_pay_lo) * 100;
                ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper();
                ProgressIndicatorBar bar = new ProgressIndicatorBar(workDone, loanComplete);
                bar.createProgressIndicatorBar(progress_box, workDone);

                initLoanPayTable(FXCollections.observableArrayList(collect));
            } else {

                initLoanPayTable(FXCollections.observableArrayList());
                ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper();
                ProgressIndicatorBar bar = new ProgressIndicatorBar(workDone, 0);
                bar.createProgressIndicatorBar(progress_box, workDone);
            }

            session.close();

        }
    }

    private void fillMemberCodeTxt(TextField memberCodeField) {

        //=================== REMOVED DUE TO FIRST IMP======================================
//        DocSeqHandler seqHandler = new DocSeqHandler();
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Criteria c = session.createCriteria(Member.class);
//        c.addOrder(Order.desc("id"));
//        c.setMaxResults(1);
//        Member mb = (Member) c.uniqueResult();
//        session.close();
//        if (mb != null) {
//            seqHandler.reqTable(TABLE_NAME_M, Integer.parseInt(mb.getMemberId().replaceAll("\\D+", "")) + 1);
//            memberCodeField.setText(seqHandler.getSeq_code());
//        } else {
//            seqHandler.reqTable(TABLE_NAME_M, 0);
//            memberCodeField.setText(seqHandler.getSeq_code());
//        }
        //=================== REMOVED DUE TO FIRST IMP ======================================
    }

    private void registerContributionValidation() {
        if (!va_msub.getRegisteredControls().isEmpty()) {
            return;
        }
        va_msub.registerValidator(m_subs_date,
                Validator.createEmptyValidator("This field is not optional."));
    }

    private void registerOtherDetailsValidation() {

        if (!va.getRegisteredControls().isEmpty()) {
            return;
        }
        va.registerValidator(mem_fee_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(sav_fee_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(hoi_fee_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(aci_fee_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(optional_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(adm_fee_txt,
                Validator.createEmptyValidator("This field is not optional."));
        va.registerValidator(mem_fee_repay_combo,
                Validator.createEmptyValidator("Repay type should select."));
        va.registerValidator(sav_fee_repay_combo,
                Validator.createEmptyValidator("Repay type should select."));
        va.registerValidator(aci_fee_repay_combo,
                Validator.createEmptyValidator("Repay type should select."));
        va.registerValidator(opt_fee_repay_combo,
                Validator.createEmptyValidator("Repay type should select."));
        va.registerValidator(adm_fee_repay_combo,
                Validator.createEmptyValidator("Repay type should select."));
        va.registerValidator(hoi_fee_repay_combo,
                Validator.createEmptyValidator("Repay type should select."));
    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
//        Predicate<String> predicate = (t) -> {
//            return !memberCodes.contains(t);
//        };

        validationSupport.registerValidator(member_code_txt, Validator.combine(
                Validator.createEmptyValidator("This field is not optional"),
                Validator.createRegexValidator("Must be a vaild membership Id.", "\\d{4,6}[A-Z]{1}", Severity.ERROR)
        //,Validator.createPredicateValidator(predicate, "This member id is already saved to the system !")
        ));
        validationSupport.registerValidator(member_full_name_txt,
                Validator.createEmptyValidator("This field is not optional."));
        validationSupport.registerValidator(member_namins_txt,
                Validator.createEmptyValidator("This field is not optional."));
        validationSupport.registerValidator(member_adrs_txt,
                Validator.createEmptyValidator("This field is not optional."));
        validationSupport.registerValidator(member_job_txt,
                Validator.createEmptyValidator("This field is not optional."));
        validationSupport.registerValidator(member_brch_txt,
                Validator.createEmptyValidator("This field is not optional."));
//        validationSupport.registerValidator(member_tel1_txt,
//                Validator.createRegexValidator("Should be a telephone number with 10 digits.",
//                        "\\d{10}", Severity.ERROR));
//        validationSupport.registerValidator(member_email_txt,
//                Validator.createRegexValidator("Must be a valid email address.",
//                        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Severity.ERROR));
        validationSupport.registerValidator(member_sex_combo,
                Validator.createEmptyValidator("Sex selection required."));
        validationSupport.registerValidator(member_status_combo,
                Validator.createEmptyValidator("Member current status selection required."));
        validationSupport.registerValidator(job_status_combo,
                Validator.createEmptyValidator("Select current job status."));
        validationSupport.registerValidator(member_maritial_combo,
                Validator.createEmptyValidator("Marital status required."));
        validationSupport.registerValidator(member_bday_chooser,
                Validator.createEmptyValidator("Member DOB required."));
        validationSupport.registerValidator(member_join_chooser,
                Validator.createEmptyValidator("Member joined date required."));
        validationSupport.registerValidator(member_apo_chooser,
                Validator.createEmptyValidator("Member appoinment date required."));
//        validationSupport.registerValidator(nic_no, Validator.combine(
//                Validator.createEmptyValidator("This field is not optional."),
//                Validator.createRegexValidator("NIC should be in correct format.", "\\d{9}[a-zA-Z]{1}", Severity.ERROR)));
        validationSupport.registerValidator(emp_id_txt,
                Validator.createEmptyValidator("Employee Id field cannot be empty"));
    }
    Alert alert_custom;

    @FXML
    private void onAssignLoanBtnAction(ActionEvent event) throws IOException {

        //================MEMBER CANT REQUEST FOR ANY OTHER LOAN IF THERE IS AN ONGOING ONE========================
//        if (anyIncompleteLoansOfMember(member_code_txt.getText())) {
//            Alert alert_error = new Alert(Alert.AlertType.ERROR);
//            alert_error.setTitle("Error");
//            alert_error.setHeaderText("Incompleted Loan(s) are stil there !");
//            alert_error.setContentText(PropHandler.getStringProperty("incompleted_loans"));
//            alert_error.show();
//            return;
//        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/AssignNewLoanFxml.fxml"));
        VBox node = (VBox) loader.load();
        AssignNewLoanFxmlController controller = (AssignNewLoanFxmlController) loader.getController();
        controller.setMember(getMemberByCode(member_code_txt.getText()));

        ImageView progressIndicator = new ImageView();
        progressIndicator.setImage(new Image(FileHandler.LOADING_DEFAULT_GIF));
        VBox v = new VBox(progressIndicator);
        v.setAlignment(Pos.CENTER);
        Dialog alert_prog = new Alert(Alert.AlertType.NONE);
        alert_prog.setTitle("Ongoing progress");
        alert_prog.setHeaderText("Please wait until the loan window is loaded. ");
        alert_prog.getDialogPane().setContent(v);
        alert_prog.setResult(Boolean.TRUE);
        alert_prog.show();

        Task<Boolean> lTask = new Task<Boolean>() {
            {
                setOnSucceeded(d -> {
                    alert_prog.hide();
                    alert_custom = new Alert(Alert.AlertType.NONE);
                    alert_custom.setTitle("Assign New Loan");
                    alert_custom.setHeaderText("Member Loan Code - " + controller.fillMemberLoanCodeTxt());
                    alert_custom.getDialogPane().setContent(node);
                    ButtonType buttonTypeCancel = new ButtonType("", ButtonData.CANCEL_CLOSE);
                    alert_custom.getButtonTypes().add(buttonTypeCancel);
                    alert_custom.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);
                    alert_custom.show();
                    controller.registerDialogInputValidation();
                    controller.getCancel_btn().setOnAction(e -> {
                        alert_custom.hide();
                    });

                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected Boolean call() throws Exception {
                return controller.initFunction(MemberfxmlController.this);
            }
        };

        Thread gurThread = new Thread(lTask, "l-task");
        gurThread.setDaemon(true);
        gurThread.start();
    }

    @FXML
    private void onRemoveLoanBtnAction(ActionEvent event) {

        MemberLoan selectedLoan = l_taken_tbl.getSelectionModel().getSelectedItem();
        if (selectedLoan != null) {
            Alert alert_confirm = new Alert(Alert.AlertType.CONFIRMATION);
            alert_confirm.setTitle("Warning");
            alert_confirm.setHeaderText("Confirm ?");
            alert_confirm.setContentText("Are you sure you want to remove the selected loan ?");
            Optional<ButtonType> rs = alert_confirm.showAndWait();
            if (rs.get() == ButtonType.OK) {
                Session ses = HibernateUtil.getSessionFactory().openSession();
                MemberLoan ml = (MemberLoan) ses.load(MemberLoan.class, selectedLoan.getId());
                if (ml.getLoanPayments().isEmpty()) {
                    ses.beginTransaction();
                    ses.delete(ml);
                    ses.getTransaction().commit();
                    Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
                    alert_success.setTitle("Success");
                    alert_success.setHeaderText("Successfully Removed !");
                    alert_success.setContentText("You have successfully removed the member loan.");
                    Optional<ButtonType> result = alert_success.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        FxUtilsHandler.clearFields(loan_name_hbox, loan_info_grid);
                        clearProgressBar();
                        buildMemberLoanTable();
                    }
                } else {
                    Alert error_alert = new Alert(Alert.AlertType.INFORMATION);
                    error_alert.setTitle("Error");
                    error_alert.setHeaderText("Error Occured !");
                    error_alert.setContentText("There are already assigned payments for this loan.");
                    error_alert.show();
                }
                ses.close();
            }

        } else {
            Alert error_alert = new Alert(Alert.AlertType.INFORMATION);
            error_alert.setTitle("Error");
            error_alert.setHeaderText("Error Occured !");
            error_alert.setContentText("You have to select a member loan to proceed.");
            error_alert.show();
        }
    }

    private void disableTabs(boolean active) {
        loan_inf_tab.setDisable(active);
        other_details_tab.setDisable(active);
        doc_tab.setDisable(active);
        cash_out_tab.setDisable(active);
        mbrcon_tab.setDisable(active);
    }

    private void disableTabsExceptMbrContribution(boolean active) {
        loan_inf_tab.setDisable(active);
        other_details_tab.setDisable(active);
        doc_tab.setDisable(active);
        cash_out_tab.setDisable(active);
        gen_details_tab.setDisable(active);
    }

    private void initMemberLoanTable(ObservableList<MemberLoan> mLoans) {
        l_id_col.setCellValueFactory(new PropertyValueFactory<>("memberLoanCode"));
        g_date_col.setCellValueFactory(new PropertyValueFactory<>("grantedDate"));
        l_type_col.setCellValueFactory(new PropertyValueFactory<>("interestMethod"));
        l_amt_col.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        l_stat_col.setCellValueFactory(new PropertyValueFactory<>("isComplete"));
        l_bal_con.setCellValueFactory(new PropertyValueFactory<>("kotaLeft"));

        g_date_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(new SimpleDateFormat("yyy-MM-dd").format(item));
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

        l_stat_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Boolean> currentRow = getTableRow();
                    if (!isEmpty()) {
                        setText(item ? "Completed" : "On Going");
                        // currentRow.setStyle(item ? "" : "-fx-background-color:#d9534f");
                    }
                }

            };
        });

        remark_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
            MemberLoan vm = param.getValue();
            return new SimpleObjectProperty(vm.isAssigntoGurs() ? "Loan assigned to its guarantors" : "No remark");
        });

        l_bal_con.setCellFactory(column -> {
            return new TableCell<MemberLoan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Boolean> currentRow = getTableRow();
                    if (!isEmpty()) {
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                        currentRow.setStyle(item > 0 ? "-fx-background-color:#d9534f" : "");
                    }
                }

            };
        });

        l_taken_tbl.setItems(mLoans);

        if (!l_pay_tbl.getItems().isEmpty()) {
            l_pay_tbl.getItems().clear();
        }
        l_taken_tbl.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        MemberLoan selectedLoan = l_taken_tbl.getSelectionModel().getSelectedItem();
                        if (selectedLoan != null) {
                            getMemberLoanByCode(selectedLoan.getMemberLoanCode(), selectedLoan.getChildId());
                        }
                    }
                });
        l_taken_tbl.setRowFactory((TableView<MemberLoan> param) -> {

            final TableRow<MemberLoan> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();
            MenuItem makePayment = new MenuItem("Make Payment");
            MenuItem closeLoan = new MenuItem("Close Loan");
            MenuItem hndOvrToGurants = new MenuItem("Assign to Gurantors");

            hndOvrToGurants.setOnAction((ActionEvent evt) -> {

                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setTitle("Confirmation");
                conf.setHeaderText("Are you sure ?");
                conf.setContentText("Are you sure you want handover the installment amount straight to gurantors ?");
                Optional<ButtonType> confm = conf.showAndWait();
                if (confm.get() != ButtonType.OK) {
                    return;
                }

                if (row.getItem().isIsComplete()) {
                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Information");
                    success.setHeaderText("Already completed !");
                    success.setContentText("You cannot process this action to an already completed loan!");
                    Optional<ButtonType> rst = success.showAndWait();
                    return;
                }

                if (row.getItem().isAssigntoGurs()) {
                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Information");
                    success.setHeaderText("Already handover !");
                    success.setContentText("This loan has been already transferred to the guarantors!");
                    Optional<ButtonType> rst = success.showAndWait();
                    return;
                }

                String gurs = row.getItem().getGuarantors();
                Session s = HibernateUtil.getSessionFactory().openSession();
                List<Member> mbrs = getSignedGuarantorsActive(gurs, s);

                Dialog<List<Member>> dialog = new Dialog<>();
                dialog.setTitle("Assign loan to Guarantors");
                dialog.setHeaderText("Assign loan "
                        + row.getItem().getMemberLoanCode()
                        + " of " + row.getItem().getMember().getFullName() + " to its guarantors.");

                ButtonType savePayButtonType = new ButtonType("Assign Loan", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(savePayButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                CheckBox[] cba = new CheckBox[mbrs.size()];
                final TextField[] txf = new TextField[mbrs.size()];
                TextField[] txf_du = new TextField[mbrs.size()];

                List<Member> figList = new ArrayList<>();

                Button saveBtn = (Button) dialog.getDialogPane().lookupButton(savePayButtonType);
                //.setDisable(true);

                for (int i = 0; i < mbrs.size(); i++) {

                    final int j = i;

                    cba[i] = new CheckBox(mbrs.get(i).getFullName());
                    txf[i] = new TextField();
                    txf_du[i] = new TextField("0");
                    txf[i].setTextFormatter(TextFormatHandler.currencyFormatter());
                    txf[i].setOnMouseClicked(e -> {
                        TextField field = (TextField) e.getSource();
                        field.selectRange(2, field.getText().length());
                    });
                    txf_du[i].setTextFormatter(TextFormatHandler.numbersOnlyFieldFormatter());
                    txf[i].setDisable(true);
                    txf_du[i].setDisable(true);
                    cba[i].setOnAction(e -> {
                        if (cba[j].isSelected()) {
                            figList.add(mbrs.get(j));
                            txf[j].setDisable(false);
                            txf_du[j].setDisable(false);
                        } else {
                            figList.remove(mbrs.get(j));
                            txf[j].setDisable(true);
                            txf_du[j].setDisable(true);
                        }
                    });
                    grid.add(cba[i], 0, i);
                    grid.add(new Label("Payment Left: "), 1, i);
                    grid.add(txf[i], 2, i);
                    grid.add(new Label("Repayments count: "), 3, i);
                    grid.add(txf_du[i], 4, i);
                }

                saveBtn.addEventFilter(ActionEvent.ACTION, ev -> {
                    boolean hasFakeVals = false;
                    for (int i = 0; i < figList.size(); i++) {
                        if (TextFormatHandler.getCurrencyFieldValue(txf[i].getText()) == 0.0 || Integer.parseInt(txf_du[i].getText()) == 0) {
                            hasFakeVals = true;
                        }
                    }

                    if (figList.isEmpty() || hasFakeVals) {
                        ev.consume();
                    }

                });
                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == savePayButtonType) {
                        return new ArrayList<>(figList);
                    }
                    return null;
                });

                Optional<List<Member>> result = dialog.showAndWait();

                result.ifPresent(m -> {

                    Alert warning = new Alert(AlertType.WARNING);
                    warning.setTitle("Warning");
                    warning.setHeaderText("Are you sure you want to transfer the loan to guarantors ?");
                    warning.setContentText("This will also ends the future payments connected to the loan grantor. "
                            + "\nBe careful, this process cannot be undone.");

                    warning.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

                    Optional<ButtonType> rst = warning.showAndWait();

                    if (rst.get() == ButtonType.CANCEL) {
                        return;
                    }

                    List<Member> mmbrs = m;
                    Loan gtLoan = getGurantorTransLoan(s);
                    s.beginTransaction();
                    int code_int = Integer.parseInt(fillMemberLoanCodeTxt().replaceAll("\\D+", ""));
                    for (int mk = 0; mk < mmbrs.size(); mk++) {
                        //====================GURANTORS GOES HERE=======================

                        MemberLoan ml = new MemberLoan();
                        ml.setMemberLoanCode(extractSeqCode("ML{7nr}", code_int));
                        ml.setMember(mmbrs.get(mk));
                        ml.setLoanName(gtLoan.getLoanName());
                        ml.setGrantedDate(new java.util.Date());
                        ml.setGuarantors("[]");
                        ml.setLoanInterest(gtLoan.getLoanInterest());
                        ml.setInterestPer(gtLoan.getInterestPer());
                        ml.setInterestMethod(gtLoan.getInterestMethod());
                        ml.setRepaymentCycle(gtLoan.getRepaymentCycle());
                        ml.setDurationPer(gtLoan.getDurationPer());
                        ml.setLoanDuration(Integer.parseInt(txf_du[mk].getText()));
                        ml.setNoOfRepay(Integer.parseInt(txf_du[mk].getText()));
                        ml.setLoanInstallment(FxUtilsHandler.roundNumber((TextFormatHandler.getCurrencyFieldValue(txf[mk].getText()) / Integer.parseInt(txf_du[mk].getText())), 0));
                        ml.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(txf[mk].getText()));
                        ml.setDerivedFrom(row.getItem().getId());
                        ml.setlRequested(new java.util.Date());
                        ml.setStatus(true);
                        s.save(ml);
                        code_int++;

                    }
                    assignToGuarantors(s, row.getItem());
                    s.getTransaction().commit();
                    s.close();

                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Successfully Transferred !");
                    success.setContentText("You have successfully transferred the loan to its gurantors.");
                    Optional<ButtonType> pp = success.showAndWait();
                    if (pp.get() == ButtonType.OK) {
                        buildMemberLoanTable();
                    }
                });
            });

            closeLoan.setOnAction((ActionEvent evt) -> {

                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setTitle("Confirmation");
                conf.setHeaderText("Are you sure ?");
                conf.setContentText("Are you sure you want to continue this end loan process ?");
                Optional<ButtonType> confm = conf.showAndWait();
                if (confm.get() != ButtonType.OK) {
                    return;
                }

                if (row.getItem().isIsComplete()) {
                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Information");
                    success.setHeaderText("Already completed !");
                    success.setContentText("You cannot make any payment to an already completed loan!");
                    Optional<ButtonType> rst = success.showAndWait();
                    return;
                }

                Session s = HibernateUtil.getSessionFactory().openSession();
                s.beginTransaction();
                List<Integer> lpIds = new ArrayList<>();

                //IF CHILD LOAN SELECTED===============================================
                if (row.getItem().isIsChild()) {

                    MemberLoan childLoan = row.getItem();
                    //GET PARENT LOAN OF SELECTED LOAN (IF HAS ANY)
                    MemberLoan parentLoan = getParentOfChild(childLoan.getId(), s);

                    //IF PARENT LOAN IS NOT COMPLETED
                    if (!parentLoan.isIsComplete()) {

                        LoanPayment parentLastLoanPay = parentLoan.getLoanPayments()
                                .stream().filter(p -> p.isIsLast()).findFirst().orElse(null);

                        double installWithoutPolli = FxUtilsHandler.roundNumber(parentLoan.getLoanAmount() / parentLoan.getNoOfRepay(), 0);
                        if (parentLastLoanPay != null) {

                            int insts = parentLastLoanPay.getInstallmentDue();
                            int last_inst_paid = parentLastLoanPay.getInstallmentNo();
                            int no_of_repay = parentLoan.getNoOfRepay();
                            double payment_amt = installWithoutPolli * insts;

                            ClosedLoan pcl = new ClosedLoan();
                            pcl.setEndedDate(new java.util.Date());
                            pcl.setClosedStart(++last_inst_paid);
                            pcl.setMemberLoanId(parentLoan.getId());
                            pcl.setTotinstClosed(insts);
                            pcl.setActualinstAmt(installWithoutPolli);
                            pcl.setTotalPayment(insts * installWithoutPolli);
                            s.save(pcl);

                            java.util.Date[] instDates = setInstallmentDates(insts, parentLastLoanPay);
                            parentLastLoanPay.setIsLast(false);
                            s.update(parentLastLoanPay);

                            for (int i = 0; i < insts; i++) {
                                LoanPayment lp = new LoanPayment();
                                lp.setInstallmentNo(last_inst_paid);
                                lp.setInstallmentDue(no_of_repay - last_inst_paid);
                                lp.setPaymentDate(new java.util.Date());
                                lp.setInstallmentDate(instDates[i]);
                                lp.setPaidAmt(installWithoutPolli);
                                lp.setListedPay(installWithoutPolli);
                                lp.setRemark("Installment Pay");
                                lp.setPayOffice(parentLoan.getMember().getPayOffice().getId());
                                lp.setWorkOffice(parentLoan.getMember().getBranch().getId());
                                if (i == (insts - 1)) {
                                    lp.setIsLast(true);
                                } else {
                                    lp.setIsLast(false);
                                }
                                lp.setMemberLoan(parentLoan);
                                s.save(lp);
                                lpIds.add(lp.getId());
                                last_inst_paid++;
                            }

                            //==================IF HAS KOTA LEFT THEN PAY IT ALSO==========
                            if (parentLoan.getKotaLeft() > 0) {

                                LoanPayment lp = new LoanPayment();
                                lp.setInstallmentNo(last_inst_paid);
                                lp.setInstallmentDue(0);
                                lp.setPaymentDate(new java.util.Date());
                                lp.setInstallmentDate(instDates[instDates.length - 1]);
                                lp.setPaidAmt(parentLoan.getKotaLeft());
                                lp.setListedPay(parentLoan.getKotaLeft());
                                lp.setPayOffice(parentLoan.getMember().getPayOffice().getId());
                                lp.setWorkOffice(parentLoan.getMember().getBranch().getId());
                                lp.setIsLast(true);
                                lp.setRemark("Arrears Pay");
                                lp.setMemberLoan(parentLoan);
                                s.save(lp);
                                lpIds.add(lp.getId());

                                //====ADD KOTA AMOUNT ALSO TO TOTAL PAYMENT===========
                                payment_amt += parentLoan.getKotaLeft();
                            }
                            //==================IF HAS KOTA LEFT THEN PAY IT ALSO==========

                            //=============END PARENT LOAN================
                            parentLoan.setIsComplete(true);
                            parentLoan.setClosedLoan(true);
                            parentLoan.setKotaLeft(0.0);
                            s.update(parentLoan);
                            //==============END PARENT LOAN===============

                            Type type = new TypeToken<List<Integer>>() {
                            }.getType();
                            ReceiptPay rp = new ReceiptPay();
                            rp.setMember(parentLoan.getMember());
                            rp.setAmount(payment_amt);
                            rp.setPayDate(new java.util.Date());
                            rp.setPaymentType("installment");
                            rp.setPayIds(new Gson().toJson(lpIds, type));
                            rp.setPayOffice(parentLoan.getMember().getPayOffice().getId());
                            rp.setWorkOffice(parentLoan.getMember().getBranch().getId());
                            s.save(rp);

                            updateMemberLoan(parentLoan, insts, s, instDates[insts - 1]);
                        }
                    }

                    LoanPayment childLastLoanPay = childLoan.getLoanPayments()
                            .stream().filter(p -> p.isIsLast()).findFirst().orElse(null);

                    double installWithoutPolli = FxUtilsHandler.roundNumber(childLoan.getLoanAmount() / childLoan.getNoOfRepay(), 0);
                    if (childLastLoanPay != null) {
                        int insts = childLastLoanPay.getInstallmentDue();
                        int last_inst_paid = childLastLoanPay.getInstallmentNo();
                        int no_of_repay = childLoan.getNoOfRepay();
                        double payment_amt = installWithoutPolli * insts;

                        ClosedLoan ccl = new ClosedLoan();
                        ccl.setEndedDate(new java.util.Date());
                        ccl.setClosedStart(++last_inst_paid);
                        ccl.setMemberLoanId(childLoan.getId());
                        ccl.setTotinstClosed(insts);
                        ccl.setActualinstAmt(installWithoutPolli);
                        ccl.setTotalPayment(insts * installWithoutPolli);
                        s.save(ccl);

                        java.util.Date[] instDates = setInstallmentDates(insts, childLastLoanPay);
                        childLastLoanPay.setIsLast(false);
                        s.update(childLastLoanPay);

                        for (int i = 0; i < insts; i++) {
                            LoanPayment lp = new LoanPayment();
                            lp.setInstallmentNo(last_inst_paid);
                            lp.setInstallmentDue(no_of_repay - last_inst_paid);
                            lp.setPaymentDate(new java.util.Date());
                            lp.setInstallmentDate(instDates[i]);
                            lp.setPaidAmt(installWithoutPolli);
                            lp.setListedPay(installWithoutPolli);
                            lp.setPayOffice(childLoan.getMember().getPayOffice().getId());
                            lp.setWorkOffice(childLoan.getMember().getBranch().getId());
                            if (i == (insts - 1)) {
                                lp.setIsLast(true);
                            } else {
                                lp.setIsLast(false);
                            }
                            lp.setRemark("Installment Pay");
                            lp.setMemberLoan(childLoan);
                            s.save(lp);
                            lpIds.add(lp.getId());
                            last_inst_paid++;
                        }

                        //==================IF HAS KOTA LEFT THEN PAY IT ALSO==========
                        if (childLoan.getKotaLeft() > 0) {

                            LoanPayment lp = new LoanPayment();
                            lp.setInstallmentNo(last_inst_paid);
                            lp.setInstallmentDue(0);
                            lp.setPaymentDate(new java.util.Date());
                            lp.setInstallmentDate(instDates[instDates.length - 1]);
                            lp.setPaidAmt(childLoan.getKotaLeft());
                            lp.setListedPay(childLoan.getKotaLeft());
                            lp.setPayOffice(childLoan.getMember().getPayOffice().getId());
                            lp.setWorkOffice(childLoan.getMember().getBranch().getId());
                            lp.setIsLast(true);
                            lp.setRemark("Arrears Pay");
                            lp.setMemberLoan(childLoan);
                            s.save(lp);
                            lpIds.add(lp.getId());

                            //====ADD KOTA AMOUNT ALSO TO TOTAL PAYMENT===========
                            payment_amt += childLoan.getKotaLeft();
                        }

                        Type type = new TypeToken<List<Integer>>() {
                        }.getType();
                        ReceiptPay rp = new ReceiptPay();
                        rp.setMember(childLoan.getMember());
                        rp.setAmount(payment_amt);
                        rp.setPayDate(new java.util.Date());
                        rp.setPaymentType("installment");
                        rp.setPayIds(new Gson().toJson(lpIds, type));
                        rp.setPayOffice(childLoan.getMember().getPayOffice().getId());
                        rp.setWorkOffice(childLoan.getMember().getBranch().getId());
                        // rp.setReceiptCode("INV" + FxUtilsHandler.generateRandomNumber(7));
                        s.save(rp);

                        childLoan.setIsComplete(true);
                        childLoan.setClosedLoan(true);
                        childLoan.setKotaLeft(0.0);
                        s.update(childLoan);

                        updateMemberLoan(childLoan, insts, s, instDates[insts - 1]);

                    }
                    //IF PARENT LOAN SELECTED===============================================
                } else {
                    MemberLoan selectedLoan = row.getItem();

                    LoanPayment selectedLoanPay = selectedLoan.getLoanPayments()
                            .stream().filter(p -> p.isIsLast()).findFirst().orElse(null);

                    double installWithoutPolli = FxUtilsHandler.roundNumber(selectedLoan.getLoanAmount() / selectedLoan.getNoOfRepay(), 0);
                    if (selectedLoanPay != null) {

                        int insts = selectedLoanPay.getInstallmentDue();
                        int last_inst_paid = selectedLoanPay.getInstallmentNo();
                        int no_of_repay = selectedLoan.getNoOfRepay();
                        double payment_amt = installWithoutPolli * insts;

                        ClosedLoan ccl = new ClosedLoan();
                        ccl.setEndedDate(new java.util.Date());
                        ccl.setClosedStart(++last_inst_paid);
                        ccl.setMemberLoanId(selectedLoan.getId());
                        ccl.setTotinstClosed(insts);
                        ccl.setActualinstAmt(installWithoutPolli);
                        ccl.setTotalPayment(insts * installWithoutPolli);
                        s.save(ccl);

                        java.util.Date[] instDates = setInstallmentDates(insts, selectedLoanPay);
                        selectedLoanPay.setIsLast(false);
                        s.update(selectedLoanPay);

                        for (int i = 0; i < insts; i++) {
                            LoanPayment lp = new LoanPayment();
                            lp.setInstallmentNo(last_inst_paid);
                            lp.setInstallmentDue(no_of_repay - last_inst_paid);
                            lp.setPaymentDate(new java.util.Date());
                            lp.setInstallmentDate(instDates[i]);
                            lp.setPaidAmt(installWithoutPolli);
                            lp.setListedPay(installWithoutPolli);
                            lp.setRemark("Installment Pay");
                            lp.setPayOffice(selectedLoan.getMember().getPayOffice().getId());
                            lp.setWorkOffice(selectedLoan.getMember().getBranch().getId());
                            if (i == (insts - 1)) {
                                lp.setIsLast(true);
                            } else {
                                lp.setIsLast(false);
                            }
                            lp.setMemberLoan(selectedLoan);
                            s.save(lp);
                            lpIds.add(lp.getId());
                            last_inst_paid++;
                        }

                        //==================IF HAS KOTA LEFT THEN PAY IT ALSO==========
                        if (selectedLoan.getKotaLeft() > 0) {

                            LoanPayment lp = new LoanPayment();
                            lp.setInstallmentNo(last_inst_paid);
                            lp.setInstallmentDue(0);
                            lp.setPaymentDate(new java.util.Date());
                            lp.setInstallmentDate(instDates[instDates.length - 1]);
                            lp.setPaidAmt(selectedLoan.getKotaLeft());
                            lp.setListedPay(selectedLoan.getKotaLeft());
                            lp.setPayOffice(selectedLoan.getMember().getPayOffice().getId());
                            lp.setWorkOffice(selectedLoan.getMember().getBranch().getId());
                            lp.setIsLast(true);
                            lp.setRemark("Arrears Pay");
                            lp.setMemberLoan(selectedLoan);
                            s.save(lp);
                            lpIds.add(lp.getId());

                            //====ADD KOTA AMOUNT ALSO TO TOTAL PAYMENT===========
                            payment_amt += selectedLoan.getKotaLeft();
                        }

                        selectedLoan.setIsComplete(true);
                        selectedLoan.setKotaLeft(0.0);
                        selectedLoan.setClosedLoan(true);
                        s.update(selectedLoan);

                        Type type = new TypeToken<List<Integer>>() {
                        }.getType();
                        ReceiptPay rp = new ReceiptPay();
                        rp.setMember(selectedLoan.getMember());
                        rp.setAmount(payment_amt);
                        rp.setPayDate(new java.util.Date());
                        rp.setPaymentType("installment");
                        rp.setPayIds(new Gson().toJson(lpIds, type));
                        rp.setPayOffice(selectedLoan.getMember().getPayOffice().getId());
                        rp.setWorkOffice(selectedLoan.getMember().getBranch().getId());
                        // rp.setReceiptCode("INV" + FxUtilsHandler.generateRandomNumber(7));
                        s.save(rp);

                        updateMemberLoan(selectedLoan, insts, s, instDates[insts - 1]);
                    }

                    //GET CHILD LOAN OF SELECTED LOAN (IF HAS ANY)
                    MemberLoan childLoan = getChildOfSelected(selectedLoan.getChildId(), s);
                    //IF HAS CHILD LOAN AND CHILD LOAN IS NOT FINISHED
                    if (childLoan != null && !childLoan.isIsComplete()) {

                        LoanPayment childLastLoanPay = childLoan.getLoanPayments()
                                .stream().filter(p -> p.isIsLast()).findFirst().orElse(null);

                        double installWithoutPolli_2 = FxUtilsHandler.roundNumber(childLoan.getLoanAmount() / childLoan.getNoOfRepay(), 0);

                        int insts = 0;
                        int last_inst_paid = 0;
                        int no_of_repay = childLoan.getNoOfRepay();
                        double payment_amt = 0;

                        if (childLastLoanPay != null) {
                            insts = childLastLoanPay.getInstallmentDue();
                            last_inst_paid = childLastLoanPay.getInstallmentNo();
                            payment_amt = installWithoutPolli_2 * insts;
                        } else {
                            // last_inst_paid = childLoan.getLastInstall();
                            insts = childLoan.getNoOfRepay();
                            payment_amt = installWithoutPolli_2 * insts;
                        }

                        ClosedLoan pcl = new ClosedLoan();
                        pcl.setEndedDate(new java.util.Date());
                        pcl.setClosedStart(++last_inst_paid);
                        pcl.setMemberLoanId(childLoan.getId());
                        pcl.setTotinstClosed(insts);
                        pcl.setActualinstAmt(installWithoutPolli_2);
                        pcl.setTotalPayment(insts * installWithoutPolli_2);
                        s.save(pcl);

                        java.util.Date[] instDates = setInstallmentDates(insts, childLastLoanPay);
                        if (childLastLoanPay != null) {
                            childLastLoanPay.setIsLast(false);
                            s.update(childLastLoanPay);
                        }

                        for (int i = 0; i < insts; i++) {
                            LoanPayment lp = new LoanPayment();
                            lp.setInstallmentNo(last_inst_paid);
                            lp.setInstallmentDue(no_of_repay - last_inst_paid);
                            lp.setPaymentDate(new java.util.Date());
                            lp.setInstallmentDate(instDates[i]);
                            lp.setPaidAmt(installWithoutPolli_2);
                            lp.setListedPay(installWithoutPolli_2);
                            lp.setRemark("Installment Pay");
                            lp.setPayOffice(childLoan.getMember().getPayOffice().getId());
                            lp.setWorkOffice(childLoan.getMember().getBranch().getId());
                            if (i == (insts - 1)) {
                                lp.setIsLast(true);
                            } else {
                                lp.setIsLast(false);
                            }
                            lp.setMemberLoan(childLoan);
                            s.save(lp);
                            lpIds.add(lp.getId());
                            last_inst_paid++;
                        }

                        //==================IF HAS KOTA LEFT THEN PAY IT ALSO==========
                        if (childLoan.getKotaLeft() > 0) {

                            LoanPayment lp = new LoanPayment();
                            lp.setInstallmentNo(last_inst_paid);
                            lp.setInstallmentDue(0);
                            lp.setPaymentDate(new java.util.Date());
                            lp.setInstallmentDate(instDates[instDates.length - 1]);
                            lp.setPaidAmt(childLoan.getKotaLeft());
                            lp.setListedPay(childLoan.getKotaLeft());
                            lp.setPayOffice(childLoan.getMember().getPayOffice().getId());
                            lp.setWorkOffice(childLoan.getMember().getBranch().getId());
                            lp.setIsLast(true);
                            lp.setRemark("Arrears Pay");
                            lp.setMemberLoan(childLoan);
                            s.save(lp);
                            lpIds.add(lp.getId());

                            //====ADD KOTA AMOUNT ALSO TO TOTAL PAYMENT===========
                            payment_amt += childLoan.getKotaLeft();
                        }

                        childLoan.setIsComplete(true);
                        childLoan.setKotaLeft(0.0);
                        childLoan.setClosedLoan(true);
                        s.update(childLoan);

                        Type type = new TypeToken<List<Integer>>() {
                        }.getType();
                        ReceiptPay rp = new ReceiptPay();
                        rp.setMember(childLoan.getMember());
                        rp.setAmount(payment_amt);
                        rp.setPayDate(new java.util.Date());
                        rp.setPaymentType("installment");
                        rp.setPayIds(new Gson().toJson(lpIds, type));
                        rp.setPayOffice(childLoan.getMember().getPayOffice().getId());
                        rp.setWorkOffice(childLoan.getMember().getBranch().getId());
                        // rp.setReceiptCode("INV" + FxUtilsHandler.generateRandomNumber(7));
                        s.save(rp);

                        updateMemberLoan(childLoan, insts, s, instDates[insts - 1]);
                    }
                }
                s.getTransaction().commit();
                s.close();

                //END THE LOAN AND GENERATE INVOICE REPORT====================
                Alert success = new Alert(AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Loan Ended!");
                success.setContentText("You have successfully Ended the selected loan.");
                Optional<ButtonType> rst = success.showAndWait();
                if (rst.get() == ButtonType.OK) {
                    buildMemberLoanTable();
                    genaratePaymentReport(lpIds, row.getItem().getMember().getMemberId(), "Loan End Invoice");
                }
                //==========================================================
            });
            makePayment.setOnAction((ActionEvent evt) -> {

                Alert conf = new Alert(AlertType.CONFIRMATION);
                conf.setTitle("Confirmation");
                conf.setHeaderText("Are you sure ?");
                conf.setContentText("Are you sure you want to continue this payment process ?");
                Optional<ButtonType> confm = conf.showAndWait();
                if (confm.get() != ButtonType.OK) {
                    return;
                }

                if (row.getItem().isIsComplete()) {
                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Information");
                    success.setHeaderText("Already completed !");
                    success.setContentText("You cannot make any payment to an already completed loan!");
                    Optional<ButtonType> rst = success.showAndWait();
                    return;
                }
                Dialog<Pair<Integer, Double>> dialog = new Dialog<>();
                dialog.setTitle("Make Payment");
                dialog.setHeaderText("Make individual payment to " + row.getItem().getMemberLoanCode());
                ButtonType savePayButtonType = new ButtonType("Make Payment", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(savePayButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                LoanPayment lpLast = row.getItem().getLoanPayments().stream().filter(p -> p.isIsLast()).findFirst().orElse(null);
                int installmentDue;
                if (lpLast != null) {
                    installmentDue = lpLast.getInstallmentDue();
                } else {
                    installmentDue = row.getItem().getNoOfRepay();
                }
                List<Integer> collect = Arrays.stream(IntStream.rangeClosed(1, installmentDue)
                        .toArray()).boxed().collect(Collectors.toList());
                ObservableList<Integer> clist = FXCollections.observableArrayList(collect);
                ComboBox<Integer> installments = new ComboBox(clist);
                installments.getSelectionModel().select(0);

                TextField totPay = new TextField();
                totPay.setPromptText("Payment goes here");
                totPay.setTextFormatter(TextFormatHandler.currencyFormatter());
                totPay.setEditable(false);

                grid.add(new Label("Paying Installments:"), 0, 0);
                grid.add(installments, 1, 0);
                grid.add(new Label("Total Payment:"), 0, 1);
                grid.add(totPay, 1, 1);

                // Node savePayBtn = dialog.getDialogPane().lookupButton(savePayButtonType);
//                savePayBtn.setDisable(true);
                installments.setOnAction((ActionEvent event) -> {
//                    savePayBtn.setDisable(false);
                    double tot = row.getItem().getLoanInstallment() * installments.getSelectionModel().getSelectedItem();
                    totPay.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot));
                });

                dialog.getDialogPane().setContent(grid);

                // Request focus on the combobox by default.
                Platform.runLater(() -> {
                    installments.requestFocus();
                    totPay.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(row.getItem().getLoanInstallment()));
                }
                );

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == savePayButtonType) {
                        return new Pair<>(installments.getSelectionModel().getSelectedItem(), TextFormatHandler.getCurrencyFieldValue(totPay));
                    }
                    return null;
                });

                Optional<Pair<Integer, Double>> result = dialog.showAndWait();

                result.ifPresent(payment -> {
                    Session s = HibernateUtil.getSessionFactory().openSession();
                    s.beginTransaction();

                    List<Integer> lpIds = new ArrayList<>();
                    int insts = payment.getKey();
                    double payment_amt = payment.getValue();
                    double instAmt = row.getItem().getLoanInstallment();
                    int last_inst_paid = lpLast != null ? lpLast.getInstallmentNo() : (row.getItem().isOldLoan() ? row.getItem().getLastInstall() : 0);
                    int no_of_repay = row.getItem().getNoOfRepay();
                    last_inst_paid++;

                    java.util.Date[] instDates = setInstallmentDates(insts, lpLast);
                    if (lpLast != null) {
                        lpLast.setIsLast(false);
                        s.update(lpLast);
                    }
                    for (int i = 0; i < insts; i++) {
                        LoanPayment lp = new LoanPayment();
                        lp.setInstallmentNo(last_inst_paid);
                        lp.setInstallmentDue(no_of_repay - last_inst_paid);
                        lp.setPaymentDate(new java.util.Date());
                        lp.setInstallmentDate(instDates[i]);
                        lp.setPaidAmt(instAmt);
                        lp.setListedPay(instAmt);
                        lp.setRemark("Installment Pay");
                        lp.setPayOffice(row.getItem().getMember().getPayOffice().getId());
                        lp.setWorkOffice(row.getItem().getMember().getBranch().getId());
                        if (i == (insts - 1)) {
                            lp.setIsLast(true);
                        } else {
                            lp.setIsLast(false);
                        }
                        lp.setMemberLoan(row.getItem());
                        s.save(lp);
                        lpIds.add(lp.getId());
                        last_inst_paid++;
                        if (no_of_repay - last_inst_paid == 0) {
                            endLoan(s, row.getItem());
                        }
                    }
                    Type type = new TypeToken<List<Integer>>() {
                    }.getType();
                    ReceiptPay rp = new ReceiptPay();
                    rp.setMember(row.getItem().getMember());
                    rp.setAmount(payment_amt);
                    rp.setPayDate(new java.util.Date());
                    rp.setPaymentType("installment");
                    rp.setPayIds(new Gson().toJson(lpIds, type));
                    rp.setPayOffice(row.getItem().getMember().getPayOffice().getId());
                    rp.setWorkOffice(row.getItem().getMember().getBranch().getId());
                    // rp.setReceiptCode("INV" + FxUtilsHandler.generateRandomNumber(7));
                    s.save(rp);

                    updateMemberLoan(row.getItem(), insts, s, instDates[insts - 1]);
                    s.getTransaction().commit();
                    s.close();

                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Information");
                    success.setHeaderText("Successfully updated!");
                    success.setContentText("You have successfully updated the loan payments!");
                    Optional<ButtonType> rst = success.showAndWait();
                    if (rst.get() == ButtonType.OK) {
                        buildMemberLoanTable();
                        genaratePaymentReport(lpIds, row.getItem().getMember().getMemberId(), "Installment Pay Invoice");
                    }

                });

            });
            rowMenu.getItems().addAll(makePayment, closeLoan, hndOvrToGurants);
            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(rowMenu)
                            .otherwise((ContextMenu) null));
            return row;

        });
        l_taken_tbl.getSelectionModel().selectFirst();
    }

    private void initLoanPayTable(ObservableList<LoanPayment> lPays) {
        p_date_col.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        ins_no_col.setCellValueFactory(new PropertyValueFactory<>("installmentNo"));
        ins_remark_col.setCellValueFactory(new PropertyValueFactory<>("remark"));
        p_due_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment lp = param.getValue();
            int lp_installment = lp.getInstallmentNo();
            double sum = lp.getMemberLoan().getLoanPayments().stream()
                    .filter(p -> p.getInstallmentNo() <= lp_installment)
                    .mapToDouble(LoanPayment::getPaidAmt)
                    .sum();
            return new SimpleObjectProperty<>(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
        });
        instment_date_col.setCellValueFactory(new PropertyValueFactory<>("installmentDate"));

        p_date_col.setCellFactory(column -> {
            return new TableCell<LoanPayment, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(new SimpleDateFormat("yyyy-MM-dd").format(item));
                    }
                }
            };
        });

        instment_date_col.setCellFactory(column -> {
            return new TableCell<LoanPayment, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(new SimpleDateFormat("MMMMM, yyyy").format(item));
                    }
                }
            };
        });
        ins_remark_col.setCellFactory(column -> {
            return new TableCell<LoanPayment, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(item);
                    }
                }
            };
        });
        l_pay_tbl.getItems().clear();
        l_pay_tbl.setItems(lPays);
    }

    private void initMemChildTable(ObservableList<MemChild> mChildren) {
        childName_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        childDOB_col.setCellValueFactory(new PropertyValueFactory<>("dob"));
        childSexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
        childHoi_col.setCellValueFactory(new PropertyValueFactory<>("hoi"));
        child_aci_col.setCellValueFactory(new PropertyValueFactory<>("aci"));

        childDOB_col.setCellFactory(column -> {
            return new TableCell<MemChild, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(new SimpleDateFormat("yyy-MM-dd").format(item));
                    }
                }
            };
        });

        childHoi_col.setCellFactory(column -> {
            return new TableCell<MemChild, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(item ? "Yes" : "No");
                    }
                }

            };
        });
        child_aci_col.setCellFactory(column -> {
            return new TableCell<MemChild, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(item ? "Yes" : "No");
                    }
                }

            };
        });

        children_tbl.setItems(mChildren);
    }

    private List<MemberLoan> getAllMemberLoans(String mCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.createAlias("member", "m").
                add(Restrictions.disjunction()
                        .add(Restrictions.eq("m.memberId", mCode)));
        List<MemberLoan> mList = c.list();
        session.close();
        return mList;
    }

    private void disableButtonWithLoggingPrv(LoggedSessionHandler ls) {
        m_gen_sav_btn.setDisable(!ls.checkPrivilegeExist(10202));
    }

    @FXML
    private void onSrchBtnAction(ActionEvent event) {
    }

    @FXML
    private void onSaveDocBtnAction(ActionEvent event) throws IOException {

        String pdf_path = FileHandler.copyPdfToLocation(docChoosen,
                FileHandler.DOC_PDF_PATH, doc_id_txt.getText());

        Document doc = new Document();
        doc.setDocName(doc_desc_txt.getText());
        doc.setDocCode(doc_id_txt.getText());
        doc.setDocType(doc_typ_txt.getText());
        doc.setDocDate(FxUtilsHandler.getDateFrom(doc_date_chooser.getValue()));
        doc.setAttachPath(pdf_path);
        doc.setMember(getMemberByCode(member_code_txt.getText()));
        if (mbr_ln_chk.isSelected()) {
            doc.setMemberLoanId(mbr_ln_txt.getText());
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(doc);
        session.getTransaction().commit();
        session.close();

        Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
        alert_success.setTitle("Success");
        alert_success.setHeaderText("Successfully Saved !");
        alert_success.setContentText("You have successfully saved the selected document.");
        Optional<ButtonType> result = alert_success.showAndWait();

        if (result.get() == ButtonType.OK) {

            p6 = SuggestionProvider.create(getAllDocuments().stream()
                    .map(d -> d.getDocType()).collect(Collectors.toList()));
            new AutoCompletionTextFieldBinding<>(doc_typ_txt, p6);
        }

    }

    @FXML
    private void onBrwsDocBtnAction(ActionEvent event) throws PdfException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Pdf File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pdf Files", "*.pdf"));
        File file = chooser.showOpenDialog(new Stage());
        docChoosen = file;
        String filename = file.getAbsolutePath();
        PdfDecoder pdf = new PdfDecoder();
        pdf.openPdfFile(filename);

        FileHandler.setGivenPdfPageAsImage(1, pdf, doc_imgview);
    }

    @FXML
    private void onMbrGenChkAction(ActionEvent event) {
        mbr_ln_chk.setSelected(false);
        mbr_ln_txt.setDisable(true);
        mbr_ln_txt.setText("");
    }

    @FXML
    private void onMbrLoanChkAction(ActionEvent event) {
        mbr_gen_chk.setSelected(false);
        mbr_ln_txt.setDisable(false);
    }

    @FXML
    private void onDeleteDocBtnAction(ActionEvent event) {
    }

    private void fillMemberDocCodeTxt(TextField doc_id_txt) {

        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Document.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        Document doc = (Document) c.uniqueResult();
        session.close();
        if (doc != null) {
            seqHandler.reqTable(TABLE_NAME_DOC, Integer.parseInt(doc.getDocCode().replaceAll("\\D+", "")) + 1);
            doc_id_txt.setText(seqHandler.getSeq_code());
        } else {
            seqHandler.reqTable(TABLE_NAME_DOC, 0);
            doc_id_txt.setText(seqHandler.getSeq_code());
        }
    }

    private List<Document> getAllDocuments() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Document> docList = session.createCriteria(Document.class).list();
        session.close();
        return docList;
    }

    private List<Document> getAllDocumentsOf(String mCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Document> docList = session.createCriteria(Document.class)
                .createAlias("member", "m")
                .add(Restrictions.eq("m.memberId", mCode))
                .list();
        session.close();
        return docList;

    }

    private void initDocTable(ObservableList<Document> docs) {
        doc_id_col.setCellValueFactory(new PropertyValueFactory<>("docCode"));
        doc_typ_col.setCellValueFactory(new PropertyValueFactory<>("docType"));
        doc_des_col.setCellValueFactory(new PropertyValueFactory<>("docName"));
        doc_date_col.setCellValueFactory(new PropertyValueFactory<>("docDate"));

        doc_date_col.setCellFactory(column -> {
            return new TableCell<Document, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(new SimpleDateFormat("yyyy-MM-dd").format(item));
                    }
                }

            };
        });
        doc_act_col.setCellFactory((TableColumn<Document, Button> param) -> {
            return new TableCell<Document, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Document> tableRow = getTableRow();
                    if (!empty) {
                        item = new Button("View");
                        item.getStyleClass().add("btn");  //member code auto fill remeved===============================
                        item.getStyleClass().add("btn-primary");
                        item.setStyle("-fx-text-fill:#ffffff;");
                        item.setOnAction((event) -> {

                            if (Desktop.isDesktopSupported()) {
                                try {
                                    File myFile = new File(tableRow.getItem().getAttachPath());
                                    Desktop.getDesktop().open(myFile);
                                } catch (IOException ex) {
                                    Alert alert_error = new Alert(Alert.AlertType.ERROR);
                                    alert_error.setTitle("Error");
                                    alert_error.setHeaderText("PDF file cannot open. ");
                                    alert_error.setContentText("No application registered for PDFs");
                                    alert_error.show();
                                }
                            }

                        });
                        setGraphic(item);
                    }
                }

            };
        });

        doc_tbl_view.setItems(docs);

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

    private void bindFamilyValidationOnPaneControlFocus(Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            c.focusedProperty().addListener(e -> {
                registerOtherDetailsValidation();
            });
        }
    }

    private void bindContValidationOnPaneControlFocus(Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            c.focusedProperty().addListener(e -> {
                registerContributionValidation();
            });
        }
    }

    private boolean isValidationEmpty(ValidationSupport vs) {
        return vs.validationResultProperty().get() == null;
    }

    private boolean anyIncompleteLoansOfMember(String memberCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        List<MemberLoan> lns = c.createAlias("member", "m")
                .add(Restrictions.eq("m.memberId", memberCode))
                .add(Restrictions.eq("isComplete", false)).list();
        return !lns.isEmpty();
    }

    @FXML
    private void onOtherDetailsSaveBtnAction(ActionEvent event) throws IOException {

        if (isValidationEmpty(va)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Invalid Entries !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }
        if (va.validationResultProperty().get().getErrors().isEmpty()) {
            Spouse s = new Spouse();
            s.setSpouse(spouse_name_txt.getText());
            s.setHoi(s_hoi_check.isSelected());
            s.setAci(s_aci_check.isSelected());

            Benifits mb = new Benifits();
            mb.setName(mb_ben_name.getText());
            mb.setRelation(mem_ben_rel.getText());

            Benifits ab = new Benifits();
            ab.setName(ac_ben_name.getText());
            ab.setRelation(ac_ben_rel.getText());

            Session session = HibernateUtil.getSessionFactory().openSession();
            Criteria c = session.createCriteria(Member.class);
            Member member = (Member) c.add(Restrictions.eq("memberId", member_code_txt.getText())).uniqueResult();
            member.setFather(father_txt.getText());
            member.setMother(mother_txt.getText());
            member.setSpouse(s.getSpouse() == null || s.getSpouse().isEmpty() ? "" : new Gson().toJson(s));
            member.setMemBenifits(mb.getName() == null || mb.getName().isEmpty() ? "" : new Gson().toJson(mb));
            member.setAccBenifits(ab.getName() == null || mb.getName().isEmpty() ? "" : new Gson().toJson(ab));
            session.beginTransaction();
            session.update(member);

            Set<MemberSubscriptions> mss = member.getMemberSubscriptions();
            List<MemberSubscription> msList = session.createCriteria(MemberSubscription.class)
                    .list();
            if (mss.isEmpty()) {

                for (MemberSubscription ms : msList) {
                    MemberSubscriptions mm = new MemberSubscriptions();
                    hibernateSaveMemberSubscriptions(ms, mm, session, member);
                }
            } else {
                for (MemberSubscription ms : msList) {
                    int id = getSubscriptionsByMemberAndSubscription(session, ms.getFeeName(), member.getId());
                    MemberSubscriptions msm;
                    if (id != 0) {
                        msm = (MemberSubscriptions) session.load(MemberSubscriptions.class, id);
                    } else {
                        msm = new MemberSubscriptions();
                    }
                    hibernateSaveMemberSubscriptions(ms, msm, session, member);
                }
            }

            session.getTransaction().commit();
            session.close();

            Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
            alert_success.setTitle("Information");
            alert_success.setHeaderText("Successfully Saved !");
            alert_success.setContentText("You have successfully saved the family information.");
            alert_success.show();
        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Missing Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("missing_fields"));
            alert_error.show();
        }
    }

    private int getSubscriptionsByMemberAndSubscription(Session session, String fee_name, Integer id) {
        System.out.println("===============QUERY START=============");
        Criteria c = session.createCriteria(MemberSubscriptions.class);
        MemberSubscriptions ms = (MemberSubscriptions) c.createAlias("member", "m")
                .createAlias("memberSubscription", "ms")
                .add(Restrictions.eq("m.id", id))
                .add(Restrictions.eq("ms.feeName", fee_name))
                .uniqueResult();
        System.out.println("===============QUERY END=============");
        if (ms != null) {
            return ms.getId();
        } else {
            return 0;
        }
    }

    @FXML
    private void newChildBtnAction(ActionEvent event) {
        formChildrenInputDesign("add new child", null);
    }

    @FXML
    private void updateChildBtnAction(ActionEvent event) {
        MemChild mchild = children_tbl.getSelectionModel().getSelectedItem();
        if (mchild != null) {
            formChildrenInputDesign("update child info", mchild);
        } else {
            Alert alert_err = new Alert(Alert.AlertType.ERROR);
            alert_err.setTitle("Information");
            alert_err.setHeaderText("Nothing selected !");
            alert_err.setContentText("You have to select a child from table to perform update.");
            alert_err.show();
        }
    }

    @FXML
    private void deleteChildBtnAction(ActionEvent event) {
        MemChild mchild = children_tbl.getSelectionModel().getSelectedItem();
        if (mchild != null) {

            Alert alert_war = new Alert(Alert.AlertType.CONFIRMATION);
            alert_war.setTitle("Warning");
            alert_war.setHeaderText("Confirm ?");
            alert_war.setContentText("Are you sure you want to remove this child ?");
            Optional<ButtonType> result_w = alert_war.showAndWait();
            if (result_w.get() == ButtonType.OK) {
                deleteChildFromMember(mchild);
                Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
                alert_success.setTitle("Information");
                alert_success.setHeaderText("Success!");
                alert_success.setContentText("Successfully removed the child from the list.");
                Optional<ButtonType> result = alert_success.showAndWait();
                if (result.get() == ButtonType.OK) {
                    initMemChildTable(getChildrenOfMember(member_code_txt.getText()));
                }
            }

        } else {
            Alert alert_err = new Alert(Alert.AlertType.ERROR);
            alert_err.setTitle("Information");
            alert_err.setHeaderText("Nothing selected !");
            alert_err.setContentText("You have to select a child from table to perform delete.");
            alert_err.show();
        }
    }

    private void formChildrenInputDesign(String titleS, MemChild mc) {

        ValidationSupport va = new ValidationSupport();
        GridPane gpane = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.SOMETIMES);
        c1.setPrefWidth(100.0);
        c1.setMinWidth(10.0);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        c2.setPrefWidth(100.0);
        c2.setMinWidth(10.0);

        RowConstraints r1 = new RowConstraints();
        r1.setMinHeight(10.0);
        r1.setPrefHeight(30.0);
        r1.setVgrow(Priority.SOMETIMES);

        RowConstraints r2 = new RowConstraints();
        r2.setMinHeight(10.0);
        r2.setPrefHeight(30.0);
        r2.setVgrow(Priority.SOMETIMES);

        RowConstraints r3 = new RowConstraints();
        r3.setMinHeight(10.0);
        r3.setPrefHeight(30.0);
        r3.setVgrow(Priority.SOMETIMES);

        RowConstraints r4 = new RowConstraints();
        r4.setMinHeight(10.0);
        r4.setPrefHeight(30.0);
        r4.setVgrow(Priority.SOMETIMES);

        RowConstraints r5 = new RowConstraints();
        r5.setMinHeight(10.0);
        r5.setPrefHeight(30.0);
        r5.setVgrow(Priority.SOMETIMES);

        RowConstraints r6 = new RowConstraints();
        r6.setMinHeight(20.0);
        r6.setPrefHeight(40.0);
        r6.setVgrow(Priority.SOMETIMES);

        gpane.setStyle("-fx-border-color: #ffff; -fx-border-width: 2px;");
        gpane.getColumnConstraints().addAll(c1, c2);
        gpane.getRowConstraints().addAll(r1, r2, r3, r4, r5, r6);

        Label title = new Label(titleS.toUpperCase());
        title.setFont(new Font(18.0));
        title.setUnderline(true);
        GridPane.setColumnSpan(title, 2);
        GridPane.setRowIndex(title, 0);

        Label cname = new Label("Child Name");
        GridPane.setRowIndex(cname, 1);
        GridPane.setColumnIndex(cname, 0);

        Label dob = new Label("Date of Birth");
        GridPane.setRowIndex(dob, 2);
        GridPane.setColumnIndex(dob, 0);

        Label sex = new Label("Sex");
        GridPane.setRowIndex(sex, 3);
        GridPane.setColumnIndex(sex, 0);

        Label hoi = new Label("HOI");
        GridPane.setRowIndex(hoi, 4);
        GridPane.setColumnIndex(hoi, 0);

        Label aci = new Label("ACI");
        GridPane.setRowIndex(aci, 5);
        GridPane.setColumnIndex(aci, 0);

        TextField cname_txt = new TextField();
        GridPane.setRowIndex(cname_txt, 1);
        GridPane.setColumnIndex(cname_txt, 1);
        va.registerValidator(cname_txt, Validator.createEmptyValidator("This field is not optional !"));
        cname_txt.setText(mc != null ? mc.getName() : "");

        DatePicker dob_p = new DatePicker();
        FxUtilsHandler.setDatePickerTimeFormat(dob_p);
        GridPane.setRowIndex(dob_p, 2);
        GridPane.setColumnIndex(dob_p, 1);
        va.registerValidator(dob_p, Validator.createEmptyValidator("Please select the date of birth !"));
        dob_p.setValue(mc != null ? FxUtilsHandler.getLocalDateFrom(mc.getDob()) : null);

        ComboBox<String> sex_box = new ComboBox();
        sex_box.setPromptText("-- SELECT --");
        sex_box.setItems(FXCollections.observableArrayList("Male", "Female"));
        va.registerValidator(sex_box, Validator.createEmptyValidator("Sex cannot be empty !"));
        GridPane.setRowIndex(sex_box, 3);
        GridPane.setColumnIndex(sex_box, 1);
        if (mc != null) {
            sex_box.getSelectionModel().select(mc.getSex());
        }

        CheckBox hoi_chk = new CheckBox();
        GridPane.setRowIndex(hoi_chk, 4);
        GridPane.setColumnIndex(hoi_chk, 1);
        hoi_chk.setSelected(mc != null ? mc.isHoi() : false);

        CheckBox aci_chk = new CheckBox();
        GridPane.setRowIndex(aci_chk, 5);
        GridPane.setColumnIndex(aci_chk, 1);
        aci_chk.setSelected(mc != null ? mc.isAci() : false);

        Button sav_btn = new Button("Save");
        sav_btn.setStyle("-fx-text-fill: #ffff;");
        sav_btn.getStyleClass().add("btn");
        sav_btn.getStyleClass().add("btn-primary");
        sav_btn.setOnAction(((event) -> {
            if (isValidationEmpty(va)) {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Empty Fields !");
                alert_error.setContentText("You have some empty fields left !.");
                alert_error.show();
                return;
            }
            if (va.validationResultProperty().get().getErrors().isEmpty()) {
                MemChild mchild;
                if (mc != null) {
                    mchild = mc;
                    mchild.setAci(aci_chk.isSelected());
                    mchild.setHoi(hoi_chk.isSelected());
                    mchild.setName(cname_txt.getText());
                    mchild.setSex(sex_box.getSelectionModel().getSelectedItem());
                    mchild.setDob(FxUtilsHandler.getDateFrom(dob_p.getValue()));
                } else {
                    mchild = new MemChild();
                    mchild.setAci(aci_chk.isSelected());
                    mchild.setHoi(hoi_chk.isSelected());
                    mchild.setName(cname_txt.getText());
                    mchild.setSex(sex_box.getSelectionModel().getSelectedItem());
                    mchild.setDob(FxUtilsHandler.getDateFrom(dob_p.getValue()));
                    mchild.setMember(getMemberByCode(member_code_txt.getText()));
                }
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.saveOrUpdate(mchild);
                session.getTransaction().commit();
                session.close();
                Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
                alert_success.setTitle("Success");
                alert_success.setHeaderText("Successfully Saved");
                alert_success.setContentText("You have successfully save the child.");
                Optional<ButtonType> result = alert_success.showAndWait();
                if (result.get() == ButtonType.OK) {
                    initMemChildTable(getChildrenOfMember(member_code_txt.getText()));
                    family_info_vbox.getChildren().clear();
                    family_info_vbox.getChildren().add(family_gridbox);
                }
            }

        }));

        Button cancel_btn = new Button("Cancel");
        cancel_btn.setStyle("-fx-text-fill: #ffff;");
        cancel_btn.getStyleClass().add("btn");
        cancel_btn.getStyleClass().add("btn-warning");
        cancel_btn.setOnAction((ActionEvent event) -> {
            family_info_vbox.getChildren().clear();
            family_info_vbox.getChildren().add(family_gridbox);

        });

        HBox btn_box = new HBox(sav_btn, cancel_btn);
        btn_box.setAlignment(Pos.CENTER_RIGHT);
        btn_box.setSpacing(5.0);
        GridPane.setRowIndex(btn_box, 6);
        GridPane.setColumnIndex(btn_box, 1);

        gpane.getChildren().addAll(title, cname, dob, sex, hoi, aci, cname_txt,
                dob_p, sex_box, hoi_chk, aci_chk, btn_box);
        family_info_vbox.getChildren().clear();
        family_info_vbox.getChildren().add(gpane);
    }

    private ObservableList<MemChild> getChildrenOfMember(String memberCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<MemChild> children = session.createCriteria(MemChild.class)
                .createAlias("member", "m")
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("m.memberId", memberCode)))
                .list();
        session.close();
        return FXCollections.observableArrayList(children);
    }

    private void deleteChildFromMember(MemChild mchild) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        MemChild dc = (MemChild) session.load(MemChild.class, mchild.getId());
        session.delete(dc);
        session.getTransaction().commit();
        session.close();
    }

    private void hibernateSaveMemberSubscriptions(MemberSubscription ms, MemberSubscriptions mm, Session session, Member member) {
        switch (ms.getFeeName()) {

            case "Membership Fee":
                mm.setMemberSubscription(ms);
                mm.setMember(member);
                mm.setAmount(TextFormatHandler.getCurrencyFieldValue(mem_fee_txt));
                mm.setRepaymentType(mem_fee_repay_combo.getSelectionModel().getSelectedItem());
                session.saveOrUpdate(mm);
                break;
            case "Savings Fee":
                mm.setMemberSubscription(ms);
                mm.setMember(member);
                mm.setAmount(TextFormatHandler.getCurrencyFieldValue(sav_fee_txt));
                mm.setRepaymentType(sav_fee_repay_combo.getSelectionModel().getSelectedItem());
                session.saveOrUpdate(mm);
                break;
            case "HOI Fee":
                mm.setMemberSubscription(ms);
                mm.setMember(member);
                mm.setAmount(TextFormatHandler.getCurrencyFieldValue(hoi_fee_txt));
                mm.setRepaymentType(hoi_fee_repay_combo.getSelectionModel().getSelectedItem());
                session.saveOrUpdate(mm);
                break;
            case "ACI Fee":
                mm.setMemberSubscription(ms);
                mm.setMember(member);
                mm.setAmount(TextFormatHandler.getCurrencyFieldValue(aci_fee_txt));
                mm.setRepaymentType(aci_fee_repay_combo.getSelectionModel().getSelectedItem());
                session.saveOrUpdate(mm);
                break;
            case "Optional":
                mm.setMemberSubscription(ms);
                mm.setMember(member);
                mm.setAmount(TextFormatHandler.getCurrencyFieldValue(optional_txt));
                mm.setRepaymentType(opt_fee_repay_combo.getSelectionModel().getSelectedItem());
                session.saveOrUpdate(mm);
                break;
            case "Admission Fee":
                mm.setMemberSubscription(ms);
                mm.setMember(member);
                mm.setAmount(TextFormatHandler.getCurrencyFieldValue(adm_fee_txt));
                mm.setRepaymentType(adm_fee_repay_combo.getSelectionModel().getSelectedItem());
                session.saveOrUpdate(mm);
                break;
        }
    }

    private List<Member> getSignedGuarantors(String guarantors, Session session) {
        List<String> lst = new Gson().fromJson(guarantors, new TypeToken<List<String>>() {
        }.getType());

        if (!lst.isEmpty()) {
            List<Member> grts = session.createCriteria(Member.class)
                    .add(Restrictions.in("memberId", lst)).list();

            return grts;
        }

        return null;

    }

    private List<Member> getSignedGuarantorsActive(String guarantors, Session session) {
        List<String> lst = new Gson().fromJson(guarantors, new TypeToken<List<String>>() {
        }.getType());
        List<Member> grts = session.createCriteria(Member.class)
                .add(Restrictions.in("memberId", lst))
                .add(Restrictions.eq("status", true))
                .list();
        //   session.close();
        return grts;
    }

    //===========================On Enter Press Request Focus===============================
    @FXML
    private void onFullNameAction(ActionEvent event) {
        member_namins_txt.requestFocus();
    }

    @FXML
    private void onNwiAction(ActionEvent event) {
        member_adrs_txt.requestFocus();
    }

    @FXML
    private void onJobTitleAction(ActionEvent event) {
        member_apo_chooser.requestFocus();
    }

    @FXML
    private void onAptDateAction(ActionEvent event) {
        member_brch_txt.requestFocus();
    }

    @FXML
    private void onMemberCodeAction(ActionEvent event) {
        nic_no.requestFocus();
    }

    @FXML
    private void onNicAction(ActionEvent event) {
        emp_id_txt.requestFocus();
    }

    @FXML
    private void onEmpAction(ActionEvent event) {
        member_full_name_txt.requestFocus();
    }

    @FXML
    private void onWrkPlsAction(ActionEvent event) {
        job_status_combo.requestFocus();
    }

    @FXML
    private void onJobStatAction(ActionEvent event) {
        payment_officer_txt.requestFocus();
    }

    @FXML
    private void onPayOfAction(ActionEvent event) {
        member_tel1_txt.requestFocus();
    }

    @FXML
    private void onTel1Action(ActionEvent event) {
        member_tel2_txt.requestFocus();
    }

    @FXML
    private void onTel2Action(ActionEvent event) {
        member_email_txt.requestFocus();
    }

    @FXML
    private void onEmailAction(ActionEvent event) {
        member_sex_combo.requestFocus();
    }

    @FXML
    private void onSexAction(ActionEvent event) {
        member_status_combo.requestFocus();
    }

    @FXML
    private void onMbrStatusAction(ActionEvent event) {
        member_maritial_combo.requestFocus();
    }

    @FXML
    private void onMartStatAction(ActionEvent event) {
        member_bday_chooser.requestFocus();
    }

    @FXML
    private void onDobAction(ActionEvent event) {
        member_join_chooser.requestFocus();
    }

    @FXML
    private void onJdAction(ActionEvent event) {
        member_des_txt.requestFocus();
    }
    //===========================On Enter Press Request Focus===============================

    private String getPaymentOffice(String work_place) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Branch.class);
        Branch bf = (Branch) c.add(Restrictions.eq("branchName", work_place))
                .setMaxResults(1)
                .uniqueResult();
        if (bf.getParentId() == 0) {
            payBranch = bf;
            return bf.getBranchName();
        } else {
            Criteria cc = s.createCriteria(Branch.class);
            Branch br = (Branch) cc.add(Restrictions.eq("id", bf.getParentId()))
                    .setMaxResults(1)
                    .uniqueResult();
            payBranch = br;
            return br.getBranchName();
        }
    }

    private void identifyCodesEditable(boolean flag) {
        member_code_txt.setEditable(flag);
        // nic_no.setEditable(flag);
    }

    @FXML
    private void onMbrFeeClicked(MouseEvent event) {
        mem_fee_txt.selectRange(2, mem_fee_txt.getText().length());
    }

    @FXML
    private void onSavingsFeeClicked(MouseEvent event) {
        sav_fee_txt.selectRange(2, sav_fee_txt.getText().length());
    }

    @FXML
    private void onHoiFeeClicked(MouseEvent event) {
        hoi_fee_txt.selectRange(2, hoi_fee_txt.getText().length());
    }

    @FXML
    private void onAciFeeClicked(MouseEvent event) {
        aci_fee_txt.selectRange(2, aci_fee_txt.getText().length());
    }

    @FXML
    private void onOptionalClicked(MouseEvent event) {
        optional_txt.selectRange(2, optional_txt.getText().length());
    }

    @FXML
    private void onAdmFeeClicked(MouseEvent event) {
        adm_fee_txt.selectRange(2, adm_fee_txt.getText().length());
    }

    @FXML
    private void onMemberFeeAction(ActionEvent event) {
        mem_fee_repay_combo.requestFocus();
    }

    @FXML
    private void onSavFeeAction(ActionEvent event) {
        sav_fee_repay_combo.requestFocus();
    }

    @FXML
    private void onHoiFeeAction(ActionEvent event) {
        hoi_fee_repay_combo.requestFocus();
    }

    @FXML
    private void onAciFeeAction(ActionEvent event) {
        aci_fee_repay_combo.requestFocus();
    }

    @FXML
    private void onOptionalFeeAction(ActionEvent event) {
        opt_fee_repay_combo.requestFocus();
    }

    @FXML
    private void onAdmFeeAction(ActionEvent event) {
        adm_fee_repay_combo.requestFocus();
    }

    @FXML
    private void onMemberFeeSelAction(ActionEvent event) {
        sav_fee_txt.requestFocus();
        sav_fee_txt.selectRange(2, sav_fee_txt.getText().length());
    }

    @FXML
    private void onSavFeeSelAction(ActionEvent event) {
        hoi_fee_txt.requestFocus();
        hoi_fee_txt.selectRange(2, hoi_fee_txt.getText().length());
    }

    @FXML
    private void onHoiFeeSelAction(ActionEvent event) {
        aci_fee_txt.requestFocus();
        aci_fee_txt.selectRange(2, aci_fee_txt.getText().length());
    }

    @FXML
    private void onAciFeeSelAction(ActionEvent event) {
        optional_txt.requestFocus();
        optional_txt.selectRange(2, optional_txt.getText().length());
    }

    @FXML
    private void onOptionalFeeSelAction(ActionEvent event) {
        adm_fee_txt.requestFocus();
        adm_fee_txt.selectRange(2, adm_fee_txt.getText().length());
    }

    @FXML
    private void onAdmFeeSelAction(ActionEvent event) {
        mb_ben_name.requestFocus();
    }

    //=================================CONTRIBUTION TAB FIELDS==========================================
    @FXML
    private void onConHoiClicked(MouseEvent event) {
        m_subs_hoi.selectRange(2, m_subs_hoi.getText().length());
    }

    @FXML
    private void onConAciClicked(MouseEvent event) {
        m_subs_aci.selectRange(2, m_subs_aci.getText().length());
    }

    @FXML
    private void onConSavClicked(MouseEvent event) {
        m_subs_sav.selectRange(2, m_subs_sav.getText().length());
    }

    @FXML
    private void onConMFClicked(MouseEvent event) {
        m_subs_mf.selectRange(2, m_subs_mf.getText().length());
    }

    @FXML
    private void onConOptClicked(MouseEvent event) {
        m_subs_op.selectRange(2, m_subs_op.getText().length());
    }

    @FXML
    private void onConHoiAction(ActionEvent event) {
        m_subs_aci.requestFocus();
        m_subs_aci.selectRange(2, m_subs_aci.getText().length());
    }

    @FXML
    private void onConAciAction(ActionEvent event) {
        m_subs_sav.requestFocus();
        m_subs_sav.selectRange(2, m_subs_sav.getText().length());
    }

    @FXML
    private void onConSavAction(ActionEvent event) {
        m_subs_mf.requestFocus();
        m_subs_mf.selectRange(2, m_subs_mf.getText().length());
    }

    @FXML
    private void onConMbrAction(ActionEvent event) {
        m_subs_op.requestFocus();
        m_subs_op.selectRange(2, m_subs_op.getText().length());
    }

    @FXML
    private void onConOptAction(ActionEvent event) {
        m_subs_adm.requestFocus();
        m_subs_adm.selectRange(2, m_subs_adm.getText().length());
    }

    @FXML
    private void onConAdmClicked(MouseEvent event) {
        m_subs_adm.selectRange(2, m_subs_adm.getText().length());
    }

    @FXML
    private void onLoanRefreshAction(ActionEvent event) {
        buildMemberLoanTable();
    }

    private void initContributionTable(ObservableList<SubscriptionPay> subsPay) {

        con_paydate_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            return new SimpleObjectProperty<>(new SimpleDateFormat("dd-MM-yyyy").format(param.getValue().getPaymentDate()));
        });
        con_hoi_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Double> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getHoiFee());
        });
        con_acI_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Double> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getAciFee());
        });
        con_savings_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Double> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getSavingsFee());
        });
        con_mbr_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Double> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getMembershipFee());
        });
        con_optional_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Double> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getOptional());
        });
        con_adm_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, Double> param) -> {
            return new SimpleObjectProperty<>(param.getValue().getAdmissionFee());
        });

        contr_tbl.setItems(subsPay);

        double cBalance = getContinuingBalance(member_code_txt.getText());
        double sum_aci = subsPay.stream().mapToDouble(SubscriptionPay::getAciFee).sum();
        double sum_hoi = subsPay.stream().mapToDouble(SubscriptionPay::getHoiFee).sum();
        double sum_sav = subsPay.stream().mapToDouble(SubscriptionPay::getSavingsFee).sum();
        double sum_opt = subsPay.stream().mapToDouble(SubscriptionPay::getOptional).sum();
        tot_aci_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum_aci));
        tot_hoi_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum_hoi));
        tot_opt_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum_opt));
        tot_sav_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum_sav));
        bal_con_label.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cBalance));

        cGive = new ContGive(sum_hoi, sum_aci, sum_sav, sum_opt, cBalance);
    }

    private List<SubscriptionPay> getAllContributionsOf(String memberId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<SubscriptionPay> totCtr = session.createCriteria(SubscriptionPay.class, "sp")
                .createAlias("sp.memberSubscriptions", "ms")
                .createAlias("ms.member", "m")
                .add(Restrictions.eq("m.memberId", memberId))
                .addOrder(Order.asc("sp.paymentDate"))
                .list();
        session.close();
        return totCtr;
    }

    private java.util.Date[] setInstallmentDates(int insts, LoanPayment lpLast) {
        java.util.Date lstDate = lpLast != null ? lpLast.getInstallmentDate() : new java.util.Date();
        DateTimeZone zone = DateTimeZone.forID("Asia/Colombo");
        DateTime ld = new DateTime(new SimpleDateFormat("yyyy-MM-dd").format(lstDate), zone);
        java.util.Date furueDates[] = new java.util.Date[insts];
        for (int i = 0; i < insts; i++) {
            furueDates[i] = ld.plusMonths(i + 1).toDate();
        }
        return furueDates;
    }

    private void endLoan(Session s, MemberLoan ml) {
        int getEndingLoan = ml.getId();
        MemberLoan mml = (MemberLoan) s.load(MemberLoan.class, getEndingLoan);
        mml.setIsComplete((mml.getKotaLeft() <= 0));
        s.update(mml);
    }

    private void assignToGuarantors(Session s, MemberLoan ml) {
        int asgGurLoan = ml.getId();
        MemberLoan mml = (MemberLoan) s.load(MemberLoan.class, asgGurLoan);
        mml.setIsComplete(true);
        mml.setAssigntoGurs(true);
        s.update(mml);
    }

    private void updateMemberLoan(MemberLoan ml, int inst_count, Session s, java.util.Date payUntil) {
        int getLoan = ml.getId();
        MemberLoan mml = (MemberLoan) s.load(MemberLoan.class, getLoan);
        mml.setPaidUntil(payUntil);
        mml.setLastInstall(mml.getLastInstall() + inst_count);
        s.update(mml);
    }

    public void showSuccessAlert() throws MalformedURLException {
        alert_custom.close();
        Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
        alert_success.setTitle("Information");
        alert_success.setHeaderText("Loan Assigned successfully!");
        alert_success.setContentText("You have successfully assigned the loan to the member.");
        Optional<ButtonType> result = alert_success.showAndWait();
        if (result.get() == ButtonType.OK) {
            FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox, benifits_box, fee_box, parents_box, spouse_box, nic_col_id, working_box, job_title_box);
            getMemberByCodeOrName(member_code_srch_txt.getText(), member_name_srch_txt.getText());
            buildMemberLoanTable();
            identifyCodesEditable(false);
            initDocTable(FXCollections.observableArrayList(getAllDocumentsOf(member_code_txt.getText())));
            initMemChildTable(getChildrenOfMember(member_code_txt.getText()));
            initContributionTable(FXCollections.observableArrayList(getAllContributionsOf(member_code_txt.getText())));
        }

    }

    private void clearProgressBar() {
        ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper();
        ProgressIndicatorBar bar = new ProgressIndicatorBar(workDone, 0.0);
        bar.createProgressIndicatorBar(progress_box, workDone);
    }
    // this list use to add the ids of individual subscription payments
    List<Integer> subIds = new ArrayList<>();
    double amt_totsub = 0.0;

    @FXML
    private void onMbrSubManualAction(ActionEvent event) throws IOException {
        if (isValidationEmpty(va_msub)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }

        if (isFormZero(subs_manual_grid)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("No any payment detected !");
            alert_error.setContentText("All the values you have entered are \"0\" . Please check again.");
            alert_error.show();
            return;
        }

        if (va_msub.validationResultProperty().get().getErrors().isEmpty()) {

            Alert alert_conf = new Alert(Alert.AlertType.CONFIRMATION);
            alert_conf.setTitle("Confirm");
            alert_conf.setHeaderText("Are you sure ?");
            alert_conf.setContentText("Are you sure you want to perform this task ?");
            Optional<ButtonType> result = alert_conf.showAndWait();
            if (result.get() == ButtonType.OK) {
                Session s = HibernateUtil.getSessionFactory().openSession();
                s.beginTransaction();
                SubscriptionPay sp = new SubscriptionPay();
                sp.setAciFee(TextFormatHandler.getCurrencyFieldValue(m_subs_aci));
                sp.setHoiFee(TextFormatHandler.getCurrencyFieldValue(m_subs_hoi));
                sp.setAdmissionFee(TextFormatHandler.getCurrencyFieldValue(m_subs_adm));
                sp.setOptional(TextFormatHandler.getCurrencyFieldValue(m_subs_op));
                sp.setSavingsFee(TextFormatHandler.getCurrencyFieldValue(m_subs_sav));
                sp.setMembershipFee(TextFormatHandler.getCurrencyFieldValue(m_subs_mf));
                sp.setPaymentDate(getDayOfMonth(FxUtilsHandler.getDateFrom(m_subs_date.getValue())));
                sp.setAddedDate(new java.util.Date());
                sp.setMemberSubscriptions(getMemberSubscriptionsFromMember(member_code_txt.getText(), s));
                sp.setPayOffice(getMemberByCode(member_code_txt.getText(), s).getPayOffice().getId());
                sp.setWorkOffice(getMemberByCode(member_code_txt.getText(), s).getBranch().getId());
                s.save(sp);
                //adding each subids to the list======
                subIds.add(sp.getId());
                amt_totsub += subsTot(sp);
                s.getTransaction().commit();

                Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
                alert_success.setTitle("Success");
                alert_success.setHeaderText("Successfully Saved !");
                alert_success.setContentText("You have successfully saved the member subscription payment .");
                Optional<ButtonType> res = alert_success.showAndWait();
                if (res.get() == ButtonType.OK) {
                    invo_gen_box.setVisible(true);
                    freezeAtMemberContribution(true);
                    initContributionTable(FXCollections.observableArrayList(getAllContributionsOf(member_code_txt.getText())));
                    FxUtilsHandler.clearFields(subs_manual_grid);
                }
            }

        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Missing Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("missing_fields"));
            alert_error.show();
        }
    }

    @FXML
    private void onMbrSubCancelAction(ActionEvent event) {
        FxUtilsHandler.clearFields(subs_manual_grid);
    }

    @FXML
    private void onGenerateSubsInvoAction(ActionEvent event) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        ReceiptPay rp = new ReceiptPay();
        // rp.setReceiptCode("INV" + FxUtilsHandler.generateRandomNumber(7));
        rp.setPaymentType("subscription");
        rp.setPayDate(new java.util.Date());
        rp.setPayIds(new Gson().toJson(subIds, new TypeToken<List<Integer>>() {
        }.getType()));
        rp.setAmount(amt_totsub);
        rp.setMember(getMemberByCode(member_code_txt.getText(), s));
        s.save(rp);
        s.getTransaction().commit();

        // String reportPath = "com/court/reports/SubscriptionPayInvoiceReport.jasper";
        String reportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "SubscriptionPayInvoiceReport.jasper";
        } catch (IOException ex) {
            Logger.getLogger(MemberfxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }

        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Subscription Pay Invoice");
        map.put("member_code", member_code_txt.getText());
        map.put("sp_list", subIds.stream().map(i -> String.valueOf(i.intValue())).collect(Collectors.joining(",")));
        ReportHandler rh = new ReportHandler(reportPath, map, null, con);
        boolean blah = rh.genReport();
        if (blah) {
            rh.viewReport();
        }
        s.close();
        //==================//RESET ALL FREEZED NODES========================
        freezeAtMemberContribution(false);
        invo_gen_box.setVisible(false);
        initContributionTable(FXCollections.observableArrayList(getAllContributionsOf(member_code_txt.getText())));
    }

    private void genaratePaymentReport(List<Integer> lpIds, String mCode, String reportTitle) {
        // String reportPath = "com/court/reports/InstallmentPayInvoiceReport.jasper";

        String reportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "InstallmentPayInvoiceReport.jasper";
        } catch (IOException ex) {
            Logger.getLogger(MemberfxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        SessionImpl smpl = (SessionImpl) s;
        Connection con = smpl.connection();
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", reportTitle);
        map.put("member_code", mCode);
        map.put("lp_list", lpIds.stream().map(i -> String.valueOf(i.intValue())).collect(Collectors.joining(",")));
        ReportHandler rh = new ReportHandler(reportPath, map, null, con);
        boolean blah = rh.genReport();
        if (blah) {
            rh.viewReport();
        }
        s.close();
    }

    private boolean isFormZero(GridPane subs_form) {
        ObservableList<Node> children = subs_form.getChildren();
        double val = 0.0;
        for (Node c : children) {
            if (c instanceof TextField) {
                TextField text = (TextField) c;
                Double fv = TextFormatHandler.getCurrencyFieldValue(text);
                if (fv > val) {
                    val = fv;
                }
            }
        }
        return val == 0.0;
    }

    private MemberSubscriptions getMemberSubscriptionsFromMember(String member_code, Session s) {
        Criteria c = s.createCriteria(MemberSubscriptions.class, "ms");
        c.createAlias("ms.member", "m")
                .add(Restrictions.eq("m.memberId", member_code))
                .setMaxResults(1);
        MemberSubscriptions ms = (MemberSubscriptions) c.uniqueResult();
        return ms;
    }

    private void freezeAtMemberContribution(boolean flag) {
// FREAZING PROCCESS SHOULD BE IDENTIFIED IN THIS METHOD TO PREVENT USER FROM GOING WITHOUT REPORT
        if (flag) {
            DashBoardFxmlController.controller.disableAllButtons();
            disableTabsExceptMbrContribution(flag);
        } else {
            LoggedSessionHandler ls = DashBoardFxmlController.controller.loggedSession();
            DashBoardFxmlController.controller.disableButtonWithLoggingPrv(ls);
            disableTabsExceptMbrContribution(flag);
        }
    }

    private double subsTot(SubscriptionPay sp) {
        return sp.getAciFee() + sp.getAdmissionFee() + sp.getHoiFee()
                + sp.getOptional() + sp.getSavingsFee() + sp.getMembershipFee();
    }

    private MemberLoan getParentOfChild(Integer id, Session s) {
        Criteria c = s.createCriteria(MemberLoan.class);
        MemberLoan mul = (MemberLoan) c.add(Restrictions.eq("childId", id))
                .uniqueResult();
        return mul != null ? mul : null;
    }

    private MemberLoan getChildOfSelected(Integer id, Session s) {
        Criteria c = s.createCriteria(MemberLoan.class);
        MemberLoan mul = (MemberLoan) c.add(Restrictions.eq("id", id))
                .uniqueResult();
        return mul != null ? mul : null;
    }

    private java.util.Date getDayOfMonth(java.util.Date instDate) {
        DateTimeZone zone = DateTimeZone.forID("Asia/Colombo");
        DateTime insDateE = new DateTime(new SimpleDateFormat("yyyy-MM-dd").format(instDate), zone);
        DateTime nowDate = insDateE.withDayOfMonth(25);
        return nowDate.toDate();
    }

    private double getContinuingBalance(String mbrCode) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(MemberLoan.class, "ml");
        c.createAlias("ml.member", "m");
        c.add(Restrictions.eq("m.memberId", mbrCode));
        List<MemberLoan> list = c.list();
        double sum = list.stream().mapToDouble(MemberLoan::getKotaLeft).sum();
        s.close();
        return sum;
    }

    @FXML
    private void handoverContAction(ActionEvent event) {
        if (cGive != null) {
            Dialog<ContGive> dialog = new Dialog<>();
            dialog.setTitle("Handover Contribution");
            dialog.setHeaderText("Contribution Handover Note");
            ButtonType viewBtn = new ButtonType("Execute Transcation", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(viewBtn, ButtonType.CANCEL);
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(new Label("ACI :"), 0, 0);
            grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cGive.getAci())), 1, 0);
            grid.add(new Label("HOI :"), 0, 1);
            grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cGive.getHoi())), 1, 1);
            grid.add(new Label("SAVINGS :"), 0, 2);
            grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cGive.getSav())), 1, 2);
            grid.add(new Label("OPT :"), 0, 3);
            grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cGive.getOpt())), 1, 3);
            grid.add(new Label("BALANCE CONT. :"), 0, 4);
            grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cGive.getBalCont())), 1, 4);
            grid.add(new Label("GRAND TOTAL :"), 0, 5);
            grid.add(new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(cGive.getGiveAway())), 1, 5);
            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(db -> {
                if (db == viewBtn) {
                    return cGive;
                }
                return null;
            });

            Optional<ContGive> result = dialog.showAndWait();
            result.ifPresent(b -> {
                //=============REPORT GENERATE AND EXECUTION CODE GOES HERE================
            });
        }
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
            seqHandler.reqTable("member_loan", Integer.parseInt(ln.getMemberLoanCode().replaceAll("\\D+", "")) + 1);
            return seqHandler.getSeq_code();
        } else {
            seqHandler.reqTable("member_loan", 0);
            return seqHandler.getSeq_code();
        }
    }

    private Loan getGurantorTransLoan(Session s) {
        Criteria c = s.createCriteria(Loan.class);
        c.add(Restrictions.like("loanName", "GUARANTOR", MatchMode.START));
        c.setMaxResults(1);
        Loan gl = (Loan) c.uniqueResult();
        System.out.println("LOAN - " + gl.getLoanName());
        return gl;
    }

    private String extractSeqCode(String seq_format, int start_from) {
        String code = "";
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(seq_format);
        while (m.find()) {
            code = seq_format.replace(m.group(), "")
                    + String.format("%0" + m.group(1).replaceAll("\\D+", "") + "d",
                            start_from);
        }

        return code;
    }

    @FXML
    private void onSettleArrearsBtnAction(ActionEvent event) {

        double total_selected = TextFormatHandler.getCurrencyFieldValue(tot_arrears_sel_txt.getText());
        double credit_balance = TextFormatHandler.getCurrencyFieldValue(credit_bal_txt.getText());

        if (total_selected == 0) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Arreas amount is zero !.");
            alert_error.setContentText("Arreas amount cannot be zero to complete the payment .");
            alert_error.show();
            return;
        }

        if (total_selected > credit_balance) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Insufficient credit balance !.");
            alert_error.setContentText("Credit balance is not enough to pay the arrears amount .");
            alert_error.show();
            return;
        }

        Alert conf = new Alert(AlertType.CONFIRMATION);
        conf.setTitle("Confirmation");
        conf.setHeaderText("Are you sure ?");
        conf.setContentText("Are you sure you want to Settle " + tot_arrears_sel_txt.getText() + " with credit balance ?");
        Optional<ButtonType> confm = conf.showAndWait();

        if (confm.get() == ButtonType.OK) {
            Session s = HibernateUtil.getSessionFactory().openSession();
            s.beginTransaction();
            Criteria c = s.createCriteria(Member.class);
            c.add(Restrictions.eq("memberId", member_code_txt.getText()));
            Member ar_m = (Member) c.uniqueResult();
            ar_m.setOverpay(ar_m.getOverpay() - arrears_tot);
            s.update(ar_m);
            boolean flag = generateArrearsRecoverReport(mbrLoan_codes, arrears_tot, ar_m.getMemberId(), ar_m.getFullName(), s);
            if (flag) {
                updateKotaLeftOfLoans(s, mbrLoan_codes);
                credit_bal_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ar_m.getOverpay()));

            }
            s.getTransaction().commit();
            s.close();
        }
    }

    double arrears_tot = 0.0;
    List<Integer> mbrLoan_codes = new ArrayList();

    private void initArreasTable(List<MemberLoan> loans) {

        List<MemberLoan> arrearsLoans = loans.stream()
                .filter(a -> a.getKotaLeft() > 0).collect(Collectors.toList());

        if (!arrearsLoans.isEmpty()) {
            co_loan_id_col.setCellValueFactory(new PropertyValueFactory<>("memberLoanCode"));
            co_arrears_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                return new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(param.getValue().getKotaLeft()));
            });
            co_select_col.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, CheckBox> param) -> {
                CheckBox cb = new CheckBox();
                cb.selectedProperty().addListener((ov, old_val, new_val) -> {
                    if (new_val) {
                        arrears_tot += param.getValue().getKotaLeft();
                        mbrLoan_codes.add(param.getValue().getId());

                        System.out.println(mbrLoan_codes.toString());
                    } else {
                        arrears_tot -= param.getValue().getKotaLeft();
                        mbrLoan_codes.remove(param.getValue().getId());

                        System.out.println(mbrLoan_codes.toString());
                    }
                    tot_arrears_sel_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(arrears_tot));
                });
                return new SimpleObjectProperty<>(cb);
            });

            arrears_tbl.setItems(FXCollections.observableArrayList(arrearsLoans));
        }
    }

    @FXML
    private void onHndOverBtnAction(ActionEvent event) {
        double handover = TextFormatHandler.getCurrencyFieldValue(hnd_ovr_amt_txt);
        double credit_balance = TextFormatHandler.getCurrencyFieldValue(credit_bal_txt.getText());

        if (handover == 0) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Handover amount is zero !.");
            alert_error.setContentText("Handover amount cannot be zero to complete the transaction .");
            alert_error.show();
            return;
        }

        if (handover > credit_balance) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Insufficient credit balance !.");
            alert_error.setContentText("Credit balance is not enough to complete the transaction .");
            alert_error.show();
            return;
        }

        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        Criteria c = s.createCriteria(Member.class);
        c.add(Restrictions.eq("memberId", member_code_txt.getText()));
        Member ar_m = (Member) c.uniqueResult();
        ar_m.setOverpay(ar_m.getOverpay() - handover);
        s.update(ar_m);
        credit_bal_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ar_m.getOverpay()));
        s.getTransaction().commit();
        generateOverpayHandoverReport(ar_m.getMemberId(), ar_m.getFullName(), handover, s);
        s.close();
    }

    private boolean updateKotaLeftOfLoans(Session s, List<Integer> mbrLoan_codes) {

        Query query = s.createSQLQuery(
                "UPDATE member_loan ml SET ml.kota_left= 0.0 WHERE ml.id in (:ml_ids) ;")
                .setParameterList("ml_ids", mbrLoan_codes);
        query.executeUpdate();

        return true;
    }

    private boolean generateArrearsRecoverReport(List<Integer> codes, double arrears, String memberId, String fullName, Session s) {
        boolean b = false;

        String reportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "SettleArrearsWithOverpayReport.jasper";
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Arrears Settlement Report ");
            map.put("ml_ids", codes);
            map.put("settleTot", arrears);
            map.put("mbrCode", memberId);
            map.put("mbrName", fullName);
            ReportHandler rh = new ReportHandler(reportPath, map, null, con);

            b = rh.genReport();
            if (b) {
                rh.viewReport();
            }

        } catch (IOException ex) {
            Logger.getLogger(MemberfxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
    }

    private void generateOverpayHandoverReport(String memberId, String fullName, double handover, Session s) {

        String reportPath = null;
        try {
            reportPath = PropHandler.getStringProperty("report_path") + "OverpayHandoverReport.jasper";
            SessionImpl smpl = (SessionImpl) s;
            Connection con = smpl.connection();
            Map<String, Object> map = new HashMap<>();
            map.put("companyName", ReportHandler.COMPANY_NAME);
            map.put("companyAddress", ReportHandler.ADDRESS);
            map.put("reportTitle", "Overpay Customer Handover Report ");
            map.put("handoverTot", handover);
            map.put("mbrCode", memberId);
            map.put("mbrName", fullName);
            ReportHandler rh = new ReportHandler(reportPath, map, null, con);

            boolean b = rh.genReport();
            if (b) {
                rh.viewReport();
            }

        } catch (IOException ex) {
            Logger.getLogger(MemberfxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class ContGive {

        private double hoi;
        private double aci;
        private double sav;
        private double opt;
        private double balCont;

        public ContGive(double hoi, double aci, double sav, double opt, double balCont) {
            this.hoi = hoi;
            this.aci = aci;
            this.sav = sav;
            this.opt = opt;
            this.balCont = balCont;
        }

        public double getGiveAway() {
            return (this.hoi + this.aci + this.sav + this.opt) - this.balCont;
        }

        ;
        public double getHoi() {
            return hoi;
        }

        public double getAci() {
            return aci;
        }

        public double getSav() {
            return sav;
        }

        public double getOpt() {
            return opt;
        }

        public double getBalCont() {
            return balCont;
        }

    }
}
