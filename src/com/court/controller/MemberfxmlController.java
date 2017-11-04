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
import com.court.handler.Spouse;
import com.court.handler.TextFormatHandler;
import com.court.model.Branch;
import com.court.model.Document;
import com.court.model.LoanPayment;
import com.court.model.MemChild;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.court.model.MemberSubscription;
import com.court.model.MemberSubscriptions;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
    private ValidationSupport validationSupport, va;
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
    private TableColumn<LoanPayment, Double> p_due_col;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        va = new ValidationSupport();
        // registerInputValidation();
        fillMemberCodeTxt(member_code_txt);
        fillMemberDocCodeTxt(doc_id_txt);

        ObservableList<Member> allMembers = getAllMembers();
        ObservableList<Branch> allBranches = getAllBranches();
        List<Document> allDocs = getAllDocuments();
        List<String> memberCodes = allMembers.stream()
                .filter(FxUtilsHandler.distinctByKey(Member::getMemberId))
                .map(Member::getMemberId).collect(Collectors.toList());
        List<String> memberNames = allMembers.stream()
                .map(Member::getFullName).collect(Collectors.toList());
        List<String> memberJobs = allMembers.stream()
                .filter(FxUtilsHandler.distinctByKey(Member::getJobTitle))
                .map(Member::getJobTitle).collect(Collectors.toList());
        List<String> branchNames = allBranches.stream()
                .map(Branch::getBranchName).collect(Collectors.toList());

        p1 = SuggestionProvider.create(memberCodes);
        p2 = SuggestionProvider.create(memberNames);
        p3 = SuggestionProvider.create(memberJobs);
        p4 = SuggestionProvider.create(branchNames);
        new AutoCompletionTextFieldBinding<>(member_code_srch_txt, p1);
        new AutoCompletionTextFieldBinding<>(member_name_srch_txt, p2);
        new AutoCompletionTextFieldBinding<>(member_job_txt, p3);

        if (!branchNames.isEmpty()) {
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
                member_join_chooser, doc_date_chooser);

        ///Field formatters===================================
        mem_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        sav_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        hoi_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        aci_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        optional_txt.setTextFormatter(TextFormatHandler.currencyFormatter());
        adm_fee_txt.setTextFormatter(TextFormatHandler.currencyFormatter());

        //================== Disable Tabs============================
        disableTabs(true);
        mbr_gen_chk.setSelected(true);
        disableButtonWithLoggingPrv(DashBoardFxmlController.controller.loggedSession());
        bindValidationOnPaneControlFocus(nic_col_id, main_grid_pane, date_hbox, tel_hbox, working_box, job_title_box);
        bindFamilyValidationOnPaneControlFocus(fee_box);
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
            member.setPaymentOfficer(payment_officer_txt.getText());
            member.setFullName(member_full_name_txt.getText());
            member.setNameWithIns(member_namins_txt.getText());
            member.setAddress(member_adrs_txt.getText());
            member.setJobTitle(member_job_txt.getText());
            member.setBranch(getBranchByName(session, member_brch_txt.getText().trim()));
            member.setTel1(member_tel1_txt.getText());
            member.setTel2(member_tel2_txt.getText());
            member.setEmail(member_email_txt.getText());
            member.setSex(member_sex_combo.getSelectionModel().getSelectedItem());
            member.setMaritalStatus(member_maritial_combo.getSelectionModel().getSelectedItem());
            member.setDob(Date.valueOf(member_bday_chooser.getValue()));
            member.setAppintedDate(Date.valueOf(member_apo_chooser.getValue()));
            member.setJoinedDate(Date.valueOf(member_join_chooser.getValue()));
            member.setDescription(member_des_txt.getText().isEmpty() ? "No Description" : member_des_txt.getText());
            //  member.setImgPath(imgString == null ? "" : imgString.getImg_path().toString());
            member.setStatus(true);
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
            prfm_action.setStatus(set_status);
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
                deactive_label.setText(set_status ? "" : "Member Deactivated .");
            }
        }
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(search_grid_pane, main_grid_pane, date_hbox, tel_hbox, nic_col_id, working_box, job_title_box);
        identifyCodesEditable(true);
        fillMemberCodeTxt(member_code_txt);
        imgString = null;
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
    }

    @FXML
    private void onMemberSearchBtnAction(ActionEvent event) throws MalformedURLException {
        FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox, benifits_box, fee_box, parents_box, spouse_box, nic_col_id, working_box, job_title_box);
        getMemberByCodeOrName(member_code_srch_txt.getText(), member_name_srch_txt.getText());
        buildMemberLoanTable();
        identifyCodesEditable(false);
        initDocTable(FXCollections.observableArrayList(getAllDocumentsOf(member_code_txt.getText())));
        initMemChildTable(getChildrenOfMember(member_code_txt.getText()));
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
            payment_officer_txt.setText(filteredMember.getPaymentOfficer());
            job_status_combo.getSelectionModel().select(filteredMember.getJobStatus());
            member_adrs_txt.setText(filteredMember.getAddress());
            member_job_txt.setText(filteredMember.getJobTitle());
            member_brch_txt.setText(filteredMember.getBranch().getBranchName());
            member_tel1_txt.setText(filteredMember.getTel1());
            member_tel2_txt.setText(filteredMember.getTel2());
            member_email_txt.setText(filteredMember.getEmail());
            member_sex_combo.getSelectionModel().select(filteredMember.getSex());
            member_maritial_combo.getSelectionModel().select(filteredMember.getMaritalStatus());
            member_bday_chooser.setValue(FxUtilsHandler.getLocalDateFrom(filteredMember.getDob()));
            member_join_chooser.setValue(FxUtilsHandler.getLocalDateFrom(filteredMember.getAppintedDate()));
            member_apo_chooser.setValue(FxUtilsHandler.getLocalDateFrom(filteredMember.getJoinedDate()));
            member_des_txt.setText(filteredMember.getDescription());
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
        List<Member> mList = c.list();
        ObservableList<Member> members = FXCollections.observableArrayList(mList);
        session.close();
        return members;
    }

    private ObservableList<Branch> getAllBranches() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Branch.class);
        c.add(Restrictions.eq("status", true));
        List<Branch> bList = c.list();
        ObservableList<Branch> branches = FXCollections.observableArrayList(bList);
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

    private void getMemberLoanByCode(String mlCode, int childId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.add(Restrictions.eq("memberLoanCode", mlCode));
        c.add(Restrictions.eq("childId", childId));
        c.setMaxResults(1);
        MemberLoan ml = (MemberLoan) c.uniqueResult();

        gurantors_lstview.getItems().clear();
        loan_id_txt.setText(ml.getMemberLoanCode());
        g_date_txt.setText(new SimpleDateFormat("yyyy-MM-dd").format(ml.getGrantedDate()));
        l_type_txt.setText(ml.getInterestMethod());
        l_amount_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanAmount()));
        l_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(ml.getLoanInterest() / 100) + " " + ml.getInterestPer());
        l_du_txt.setText(ml.getLoanDuration() + " " + ml.getDurationPer());
        int_pls_prin_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanInstallment() * ml.getNoOfRepay()));
        loan_nm_txt.setText(ml.getLoanName());

        gurantors_lstview.getItems().addAll(getSignedGuarantors(ml.getGuarantors(), session));

        l_repay_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                .format(ml.getLoanInstallment()));

        Criteria cl = session.createCriteria(LoanPayment.class);
        cl.createAlias("memberLoan", "ml");
        // cl.add(Restrictions.eq("ml.memberLoanCode", ml.getMemberLoanCode()));
        cl.add(Restrictions.eq("ml.id", ml.getId()));
        List<LoanPayment> filteredList = cl.list();

        if (!filteredList.isEmpty()) {
            List<LoanPayment> collect = filteredList.stream()
                    .filter(FxUtilsHandler.distinctByKey(p -> p.getInstallmentNo()))
                    .collect(Collectors.toList());

            Double paymentDue = collect.stream()
                    .filter(p -> p.isIsLast())
                    .findFirst().get().getPaymentDue();

            double loanComplete = (paymentDue / (ml.getLoanInstallment() * ml.getNoOfRepay())) * 100;
            ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper();
            ProgressIndicatorBar bar = new ProgressIndicatorBar(workDone, loanComplete);
            bar.createProgressIndicatorBar(progress_box, workDone);

            initLoanPayTable(FXCollections.observableArrayList(collect));
        } else {
            if (!l_pay_tbl.getItems().isEmpty()) {
                l_pay_tbl.getItems().clear();
            }
            ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper();
            ProgressIndicatorBar bar = new ProgressIndicatorBar(workDone, 0);
            bar.createProgressIndicatorBar(progress_box, workDone);
        }

        session.close();
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
        validationSupport.registerValidator(member_code_txt, Validator.combine(
                Validator.createEmptyValidator("This field is not optional"),
                Validator.createRegexValidator("Must be a vaild membership Id.", "\\d{4,6}[A-Z]{1}", Severity.ERROR)
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
        controller.initFunction(this);
        // System.out.println("C Member = " + controller.getMember());
        Alert alert_custom = new Alert(Alert.AlertType.NONE);
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
                        buildMemberLoanTable();
                    }
                } else {
                    Alert error_alert = new Alert(Alert.AlertType.INFORMATION);
                    error_alert.setTitle("Error");
                    error_alert.setHeaderText("Error Occured !");
                    error_alert.setContentText("There are already assigned payments for this loan.");
                    error_alert.show();
                }
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
    }

    private void initMemberLoanTable(ObservableList<MemberLoan> mLoans) {
        l_id_col.setCellValueFactory(new PropertyValueFactory<>("memberLoanCode"));
        g_date_col.setCellValueFactory(new PropertyValueFactory<>("grantedDate"));
        l_type_col.setCellValueFactory(new PropertyValueFactory<>("interestMethod"));
        l_amt_col.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        l_stat_col.setCellValueFactory(new PropertyValueFactory<>("isComplete"));

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

        l_taken_tbl.setItems(mLoans);

        l_taken_tbl.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (l_taken_tbl.getSelectionModel().getSelectedItem() != null) {
                        MemberLoan selectedLoan = l_taken_tbl.getSelectionModel().getSelectedItem();
                        if (selectedLoan != null) {
                            getMemberLoanByCode(selectedLoan.getMemberLoanCode(), selectedLoan.getChildId());
                        }
                    }
                });
        l_taken_tbl.getSelectionModel().selectFirst();
    }

    private void initLoanPayTable(ObservableList<LoanPayment> lPays) {
        p_date_col.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        ins_no_col.setCellValueFactory(new PropertyValueFactory<>("installmentNo"));
        p_due_col.setCellValueFactory(new PropertyValueFactory<>("paymentDue"));

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
        p_due_col.setCellFactory(column -> {
            return new TableCell<LoanPayment, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                    }
                }

            };
        });
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
        doc.setDocDate(Date.valueOf(doc_date_chooser.getValue()));
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
        return docList;
    }

    private List<Document> getAllDocumentsOf(String mCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Document> docList = session.createCriteria(Document.class)
                .createAlias("member", "m")
                .add(Restrictions.eq("m.memberId", mCode))
                .list();
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
                    mchild.setDob(Date.valueOf(dob_p.getValue()));
                } else {
                    mchild = new MemChild();
                    mchild.setAci(aci_chk.isSelected());
                    mchild.setHoi(hoi_chk.isSelected());
                    mchild.setName(cname_txt.getText());
                    mchild.setSex(sex_box.getSelectionModel().getSelectedItem());
                    mchild.setDob(Date.valueOf(dob_p.getValue()));
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
        List<Member> grts = session.createCriteria(Member.class)
                .add(Restrictions.in("memberId", lst)).list();

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
        Branch bf = (Branch) c.add(Restrictions.eq("branchName", work_place)).uniqueResult();
        if (bf.getParentId() == 0) {
            return bf.getBranchName();
        } else {
            Criteria cc = s.createCriteria(Branch.class);
            Branch br = (Branch) cc.add(Restrictions.eq("id", bf.getParentId())).uniqueResult();
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

    @FXML
    private void onLoanRefreshAction(ActionEvent event) {
        buildMemberLoanTable();
    }
}
