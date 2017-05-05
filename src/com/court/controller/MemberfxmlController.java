/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.DocSeqHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.ImageHandler;
import com.court.handler.ImageWithString;
import com.court.handler.LoanCalculationHandler;
import com.court.handler.LoggedSessionHandler;
import com.court.handler.ProgressIndicatorBar;
import com.court.handler.PropHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.Branch;
import com.court.model.Document;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jpedal.PdfDecoder;
import org.jpedal.examples.viewer.OpenViewerFX;
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
    private ListView<String> gurantors_lstview;
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
    private Button m_delete_btn;
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

    AutoCompletionBinding<String> ba1, ba2;
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
                .map(Member::getMemberId).collect(Collectors.toList());
        List<String> memberNames = allMembers.stream()
                .map(Member::getNameWithIns).collect(Collectors.toList());
        List<String> memberJobs = allMembers.stream()
                .map(Member::getJobTitle).collect(Collectors.toList());
        List<String> branchNames = allBranches.stream()
                .map(Branch::getBranchName).collect(Collectors.toList());

        TextFields.bindAutoCompletion(member_code_srch_txt, memberCodes);
        TextFields.bindAutoCompletion(member_name_srch_txt, memberNames);
        TextFields.bindAutoCompletion(member_job_txt, memberJobs);
        TextFields.bindAutoCompletion(member_brch_txt, branchNames);
        autoCompleteFieldData(ba2, allDocs.stream()
                .map(d -> d.getDocType()).collect(Collectors.toList()), doc_typ_txt);
        FxUtilsHandler.setDatePickerTimeFormat(member_apo_chooser, member_bday_chooser,
                member_join_chooser, doc_date_chooser);

        //================== Disable Tabs============================
        disableTabs(true);
        mbr_gen_chk.setSelected(true);
        disableButtonWithLoggingPrv(DashBoardFxmlController.controller.loggedSession());
        bindValidationOnPaneControlFocus(main_grid_pane, date_hbox, tel_hbox);
    }

    @FXML
    private void onMemberNewBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox);
        fillMemberCodeTxt(member_code_txt);
        imgString = null;
        member_img.setImage(new Image(getClass().getResourceAsStream(ImageHandler.MEMBER_DEFAULT_IMG)));
        FxUtilsHandler.activeDeactiveChildrenControls(true,
                main_grid_pane, date_hbox, tel_hbox);
        FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, true, true);
        deactive_label.setText("");
        disableTabs(true);
    }

    @FXML
    private void onMemberSaveBtnAction(ActionEvent event) throws IOException {
        if (isValidationEmpty()) {
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
            }

            member.setMemberId(member_code_txt.getText().trim());
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
            member.setImgPath(imgString == null ? "" : imgString.getImg_path().toString());
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
                FxUtilsHandler.clearFields(main_grid_pane, date_hbox, tel_hbox);
                fillMemberCodeTxt(member_code_txt);
                imgString = null;
                member_img.setImage(new Image(getClass().getResourceAsStream(ImageHandler.MEMBER_DEFAULT_IMG)));

                ObservableList<Member> allMembers = getAllMembers();
                List<String> memberCodes = allMembers.stream()
                        .map(Member::getMemberId).collect(Collectors.toList());
                List<String> memberNames = allMembers.stream()
                        .map(Member::getNameWithIns).collect(Collectors.toList());
                List<String> memberJobs = allMembers.stream()
                        .map(Member::getJobTitle).collect(Collectors.toList());
                TextFields.bindAutoCompletion(member_code_srch_txt, memberCodes);
                TextFields.bindAutoCompletion(member_name_srch_txt, memberNames);
                TextFields.bindAutoCompletion(member_job_txt, memberJobs);
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
        if (isValidationEmpty()) {
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
        FxUtilsHandler.clearFields(search_grid_pane, main_grid_pane, date_hbox, tel_hbox);
        fillMemberCodeTxt(member_code_txt);
        imgString = null;
        member_img.setImage(new Image(getClass().getResourceAsStream(ImageHandler.MEMBER_DEFAULT_IMG)));
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
            imgString = ImageHandler.
                    getImageBy(file, member_code_txt.getText().trim(), ImageHandler.MEMBER_IMG_PATH);
            member_img.setImage(imgString.getImg());

        }
    }

    @FXML
    private void onMemberSearchBtnAction(ActionEvent event) throws MalformedURLException {
        getMemberByCodeOrName(member_code_srch_txt.getText(), member_name_srch_txt.getText());
        List<MemberLoan> allMemberLoans = getAllMemberLoans(member_code_srch_txt.getText());
        autoCompleteFieldData(ba1, allMemberLoans
                .stream().map(p -> p.getMemberLoanCode()).collect(Collectors.toList()), mbr_ln_txt);

        List<MemberLoan> filteredCollection = allMemberLoans
                .stream().filter(FxUtilsHandler
                        .distinctByKey(p -> p.getId()))
                .collect(Collectors.toList());
        initMemberLoanTable(FXCollections.observableArrayList(filteredCollection));
        initDocTable(FXCollections.observableArrayList(getAllDocumentsOf(member_code_txt.getText())));
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
            c.add(Restrictions.eq("nameWithIns", mName));
        }

        Member filteredMember = (Member) c.uniqueResult();
        if (filteredMember != null) {
            imgString = new ImageWithString();

            member_code_txt.setText(filteredMember.getMemberId());
            member_full_name_txt.setText(filteredMember.getFullName());
            member_namins_txt.setText(filteredMember.getNameWithIns());
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
            if (!filteredMember.getImgPath().trim().isEmpty()) {
                imgString.setImg_path(new File(filteredMember.getImgPath()).toPath());
                member_img.setImage(new Image(new File(filteredMember.getImgPath()).toURI().toURL().toString()));
            }

            deactive_label.setText(filteredMember.isStatus() ? "" : "Member Deactivated .");
            FxUtilsHandler.activeDeactiveChildrenControls(filteredMember.isStatus(),
                    main_grid_pane, date_hbox, tel_hbox);
            FxUtilsHandler.activeBtnAppearanceChange(member_deactive_btn, filteredMember.isStatus(), true);
            disableTabs(false);
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
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        c.add(Restrictions.eq("memberId", mCode));
        Member filteredM = (Member) c.uniqueResult();
        session.close();
        return filteredM;
    }

    private void getMemberLoanByCode(String mlCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        c.add(Restrictions.eq("memberLoanCode", mlCode));
        MemberLoan ml = (MemberLoan) c.uniqueResult();

        gurantors_lstview.getItems().clear();
        loan_id_txt.setText(ml.getMemberLoanCode());
        g_date_txt.setText(new SimpleDateFormat("yyyy-MM-dd").format(ml.getGrantedDate()));
        l_type_txt.setText(ml.getInterestMethod());
        l_amount_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanAmount()));
        l_int_txt.setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(ml.getLoanInterest() / 100) + " " + ml.getInterestPer());
        l_du_txt.setText(ml.getLoanDuration() + " " + ml.getDurationPer());
        int_pls_prin_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(ml.getLoanInstallment() * ml.getNoOfRepay()));

        Collection<String> gees = new Gson().fromJson(ml.getGuarantors(),
                new TypeToken<Collection<String>>() {
                }.getType());
        gurantors_lstview.getItems().addAll(gees);

        l_repay_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                .format(ml.getLoanInstallment()));

        Criteria cl = session.createCriteria(LoanPayment.class);
        cl.createAlias("memberLoan", "ml");
        cl.add(Restrictions.eq("ml.memberLoanCode", ml.getMemberLoanCode()));
        List<LoanPayment> filteredList = cl.list();

        if (!filteredList.isEmpty()) {
            List<LoanPayment> collect = filteredList.stream()
                    .filter(FxUtilsHandler
                            .distinctByKey(p -> p.getInstallmentNo())).collect(Collectors.toList());

            Double paymentDue = collect.stream().filter(p -> p.isIsLast()).findFirst().get().getPaymentDue();

            double loanComplete = (paymentDue / (ml.getLoanInstallment() * ml.getNoOfRepay())) * 100;
            ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper();
            ProgressIndicatorBar bar = new ProgressIndicatorBar(workDone, loanComplete);
            bar.createProgressIndicatorBar(progress_box, workDone);

            initLoanPayTable(FXCollections.observableArrayList(collect));
        }

        session.close();
    }

    private void fillMemberCodeTxt(TextField memberCodeField) {
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        Member mb = (Member) c.uniqueResult();
        session.close();
        if (mb != null) {
            seqHandler.reqTable(TABLE_NAME_M, Integer.parseInt(mb.getMemberId().replaceAll("\\D+", "")) + 1);
            memberCodeField.setText(seqHandler.getSeq_code());
        } else {
            seqHandler.reqTable(TABLE_NAME_M, 0);
            memberCodeField.setText(seqHandler.getSeq_code());
        }

    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
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
        validationSupport.registerValidator(member_tel1_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Should be a telephone number with 10 digits.",
                                "\\d{10}", Severity.ERROR)));
        validationSupport.registerValidator(member_email_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Must be a valid email address.",
                                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Severity.ERROR)));
        validationSupport.registerValidator(member_sex_combo,
                Validator.createEmptyValidator("Sex selection required."));
        validationSupport.registerValidator(member_maritial_combo,
                Validator.createEmptyValidator("Marital status required."));
        validationSupport.registerValidator(member_bday_chooser,
                Validator.createEmptyValidator("Member DOB required."));
        validationSupport.registerValidator(member_join_chooser,
                Validator.createEmptyValidator("Member joined date required."));
        validationSupport.registerValidator(member_apo_chooser,
                Validator.createEmptyValidator("Member appoinment date required."));

    }

    private void registerDialogInputValidation(AssignNewLoanFxmlController c) {
        va.registerValidator(c.getInt_method_combo(),
                Validator.createEmptyValidator("Interest method Selection required !"));
        va.registerValidator(c.getLoan_name_txt(),
                Validator.createEmptyValidator("This field is not optional !"));
        va.registerValidator(c.getLoan_int_combo(),
                Validator.createEmptyValidator("Selection Required !"));
        va.registerValidator(c.getLoan_due_combo(),
                Validator.createEmptyValidator("Selection Required !"));
        va.registerValidator(c.getRepay_combo(),
                Validator.createEmptyValidator("Selection Required !"));
        va.registerValidator(c.getLoan_due_txt(),
                Validator.createEmptyValidator("This field is not optional !"));
        va.registerValidator(c.getLoan_int_txt(),
                Validator.createPredicateValidator(percentage -> {
                    return TextFormatHandler.getPercentageFieldValue(c.getLoan_int_txt()) < 100d;
                }, "Not a valid interest !"));

        // List view should be validated =======================================
//        va.registerValidator(c.getGuarantor_list(), Validator.createPredicateValidator(p -> {
//            return c.getGuarantor_list().getItems().size() < 1;
//        }, "At least one guarantor should be included."));
        va.registerValidator(c.getPrincipal_amt_txt(),
                Validator.createPredicateValidator((principal) -> {
                    return TextFormatHandler.getCurrencyFieldValue(c.getPrincipal_amt_txt()) > 10000d;
                }, "Principal amount should be more than Rs 10000.00 !"));
    }

    @FXML
    private void onAssignLoanBtnAction(ActionEvent event) throws IOException {
        if (anyIncompleteLoansOfMember(member_code_txt.getText())) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Incompleted Loan(s) are stil there !");
            alert_error.setContentText(PropHandler.getStringProperty("incompleted_loans"));
            alert_error.show();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/AssignNewLoanFxml.fxml"));
        VBox node = (VBox) loader.load();
        AssignNewLoanFxmlController controller = (AssignNewLoanFxmlController) loader.getController();

        Alert alert_custom = new Alert(Alert.AlertType.NONE);
        alert_custom.setTitle("Assign New Loan");
        alert_custom.setHeaderText("Member Loan Code - " + controller.fillMemberLoanCodeTxt());
        alert_custom.getDialogPane().setContent(node);
        ButtonType buttonTypeCancel = new ButtonType("", ButtonData.CANCEL_CLOSE);
        alert_custom.getButtonTypes().add(buttonTypeCancel);
        alert_custom.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);
        alert_custom.show();
        registerDialogInputValidation(controller);

        ObservableList<Member> guarantors = controller.getAllMembers(member_code_txt.getText());
        List<String> guarantorNames = guarantors.stream()
                .map(Member::getNameWithIns).collect(Collectors.toList());

        TextFields.bindAutoCompletion(controller.getSearch_txt(), guarantorNames);
        controller.getCancel_btn().setOnAction(e -> {
            alert_custom.hide();
        });
        controller.getApply_btn().setOnAction(e -> {

            if (va.validationResultProperty().get().getErrors().isEmpty()) {

                MemberLoan ml = new MemberLoan();
                ml.setMemberLoanCode(controller.fillMemberLoanCodeTxt());
                ml.setMember(getMemberByCode(member_code_txt.getText()));
                ml.setGrantedDate(new java.util.Date());
                ml.setGuarantors(new Gson().toJson(controller.getGuarantor_list().getItems()));
                ml.setLoanAmount(TextFormatHandler.getCurrencyFieldValue(controller.getPrincipal_amt_txt()));
                ml.setLoanInterest(TextFormatHandler.getPercentageFieldValue(controller.getLoan_int_txt()));
                ml.setInterestPer(controller.getLoan_int_combo().getSelectionModel().getSelectedItem());
                ml.setInterestMethod(controller.getInt_method_combo().getSelectionModel().getSelectedItem());
                ml.setLoanDuration(Integer.parseInt(controller.getLoan_due_txt().getText()));
                ml.setDurationPer(controller.getLoan_due_combo().getSelectionModel().getSelectedItem());
                ml.setRepaymentCycle(controller.getRepay_combo().getSelectionModel().getSelectedItem());
                ml.setNoOfRepay(Integer.parseInt(controller.getRepay_txt().getText()));
                ml.setLoanInstallment(getInstallmentAccordingToLoanType(controller.getLoan_int_combo().getSelectionModel().getSelectedItem(),
                        controller.getLoan_due_combo().getSelectionModel().getSelectedItem(),
                        TextFormatHandler.getCurrencyFieldValue(controller.getPrincipal_amt_txt()),
                        TextFormatHandler.getPercentageFieldValue(controller.getLoan_int_txt()),
                        Integer.parseInt(controller.getRepay_txt().getText()),
                        controller.getInt_method_combo().getSelectionModel().getSelectedItem()));
                ml.setIsComplete(false);
                ml.setStatus(true);

                saveMemberLoan(ml);
                controller.getError_label().setStyle("-fx-text-fill: #349a46;");
                controller.getError_label().setText("Successfully assigned the loan.");
            } else {
                controller.getError_label().setStyle("-fx-text-fill: #d32323;");
                controller.getError_label().setText("Some error occured. Check again.");
            }
        });

    }

    @FXML
    private void onRemoveLoanBtnAction(ActionEvent event) {
    }

    private void saveMemberLoan(MemberLoan member_loan) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(member_loan);
        session.getTransaction().commit();
        session.close();
    }

    private void disableTabs(boolean active) {
        loan_inf_tab.setDisable(active);
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
                        getMemberLoanByCode(selectedLoan.getMemberLoanCode());
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

    private void autoCompleteFieldData(AutoCompletionBinding<String> binding,
            List<String> bindingList, TextField field) {

        if (binding != null) {
            binding.dispose();
        }
        binding = TextFields.bindAutoCompletion(field, bindingList);

    }

    private double getInstallmentAccordingToLoanType(String interest_per, String duration_per, double loan_amt,
            double loan_int, int no_of_pay, String interest_method) {
        int interestPer = interest_per.contains("Month") ? 0 : 1;
        int tenureIn = duration_per.contains("Months") ? 0 : 1;
        double installment_amt = 0.0;
        switch (interest_method) {
            case "Falt Rate":
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
        return installment_amt;
    }

    private void disableButtonWithLoggingPrv(LoggedSessionHandler ls) {
        m_gen_sav_btn.setDisable(!ls.checkPrivilegeExist(10202));
    }

    @FXML
    private void onSrchBtnAction(ActionEvent event) {
    }

    @FXML
    private void onSaveDocBtnAction(ActionEvent event) throws IOException {

        String pdf_path = ImageHandler.copyPdfToLocation(docChoosen,
                ImageHandler.DOC_PDF_PATH, doc_id_txt.getText());

        Document doc = new Document();
        doc.setDocName(doc_desc_txt.getText());
        doc.setDocCode(doc_id_txt.getText());
        doc.setDocType(doc_typ_txt.getText());
        doc.setDocDate(Date.valueOf(doc_date_chooser.getValue()));
        doc.setAttachPath(pdf_path);
        doc.setMember(getMemberByCode(member_code_txt.getText()));
        if (mbr_ln_chk.isSelected()) {
            doc.setMemeberLoanId(mbr_ln_txt.getText());
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
            autoCompleteFieldData(ba2, getAllDocuments().stream()
                    .map(d -> d.getDocType()).collect(Collectors.toList()), doc_typ_txt);
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

        ImageHandler.setGivenPdfPageAsImage(1, pdf, doc_imgview);
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
                        item.getStyleClass().add("btn");
                        item.getStyleClass().add("btn-primary");
                        item.setStyle("-fx-text-fill:#ffffff;");
                        item.setOnAction((event) -> {
//                            Stage stg = new Stage();
//                            BaseViewerFX fx = new BaseViewerFX();
//                            stg.setScene(fx.setupViewer(800, 600));
//                            stg.show();
//                            fx.loadPDF(new File(tableRow.getItem().getAttachPath()));

// ===========================Something there to consider again with pdf viewer print btn and left sidebar
                            VBox bx = new VBox();
                            String prf_path = new File("").getAbsolutePath() + "/src/pdf-settings.xml";
                            OpenViewerFX viewer = new OpenViewerFX(bx, prf_path);
                            viewer.getRoot().setPrefSize(800, 600);
                            viewer.setupViewer();
                            viewer.openDefaultFile(tableRow.getItem().getAttachPath());
                            Alert pdf_viewer = new Alert(Alert.AlertType.NONE);
                            pdf_viewer.setTitle("PDF Viewer");
                            pdf_viewer.getDialogPane().setContent(bx);
                            ButtonType buttonTypeCancel = new ButtonType("", ButtonData.CANCEL_CLOSE);
                            pdf_viewer.getButtonTypes().add(buttonTypeCancel);
                            pdf_viewer.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);
                            pdf_viewer.show();
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

    private boolean isValidationEmpty() {
        return validationSupport.validationResultProperty().get() == null;
    }

    private boolean anyIncompleteLoansOfMember(String memberCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        List<MemberLoan> lns = c.createAlias("member", "m")
                .add(Restrictions.eq("m.memberId", memberCode))
                .add(Restrictions.eq("isComplete", false)).list();
        return !lns.isEmpty();
    }
}
