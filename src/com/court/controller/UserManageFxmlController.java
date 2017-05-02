/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.ImageHandler;
import com.court.handler.ImageWithString;
import com.court.handler.LoggedSessionHandler;
import com.court.handler.PasswordHandler;
import com.court.handler.PropHandler;
import com.court.handler.TreeItemHandler;
import com.court.model.PrivCat;
import com.court.model.User;
import com.court.model.UserHasUserRole;
import com.court.model.UserRole;
import com.court.model.UsrRolePrivilage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.ParserConfigurationException;
import org.controlsfx.control.CheckTreeView;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class UserManageFxmlController implements Initializable {

    private ImageWithString imgString = null;
    private ValidationSupport validationSupport, va1, va2;
    private ObservableList<TreeItem<TreeItemHandler.ItemProp>> checkedItems = null;
    @FXML
    private CheckTreeView<TreeItemHandler.ItemProp> check_tree_view;
    @FXML
    private GridPane main_grid;
    @FXML
    private TextField full_name_txt;
    @FXML
    private TextField email_txt;
    @FXML
    private TextArea address_txt;
    @FXML
    private TextField tele_txt;
    @FXML
    private ComboBox<String> usr_role_combo;
    @FXML
    private ImageView usr_img_view;
    @FXML
    private TextField user_name_txt;
    @FXML
    private GridPane search_grid;
    @FXML
    private TextField full_name_src_txt;
    @FXML
    private TextField usr_role_sr_txt;
    @FXML
    private TableView<UserHasUserRole> user_tbl;
    @FXML
    private TableColumn<UserHasUserRole, String> usr_name_col;
    @FXML
    private TableColumn<UserHasUserRole, String> full_name_col;
    @FXML
    private TableColumn<UserHasUserRole, String> email_col;
    @FXML
    private TableColumn<UserHasUserRole, String> usr_role_col;
    @FXML
    private TableColumn<UserHasUserRole, String> status_col;
    @FXML
    private Button reset_btn;

    AutoCompletionBinding<String> ba1, ba2;
    @FXML
    private TableView<UserRole> role_tbl;
    @FXML
    private TableColumn<UserRole, String> name_col;
    @FXML
    private TableColumn<UserRole, Button> action_col;
    @FXML
    private TableColumn<UserRole, Boolean> stt_col;
    @FXML
    private TextField usr_role_name_txt;
    @FXML
    private Button ud_save_btn;
    @FXML
    private Button ud_deact_btn;
    @FXML
    private Button ud_delete_btn;
    @FXML
    private Button ud_srch_usr_btn;
    @FXML
    private Button ur_save_btn;
    @FXML
    private Button ur_deact_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        validationSupport = new ValidationSupport();
        va1 = new ValidationSupport();
        va2 = new ValidationSupport();

        usr_role_combo.getItems().addAll(getAvailableUserRoles());
        searchUser("", "");
        autoCompleteFieldData(ba1, getAvailableUserRoles(), usr_role_sr_txt);
        autoCompleteFieldData(ba2, getAvailableUserFullNames(), full_name_src_txt);

        initUserRoleTable(getAllUserRoles());
        disableButtonWithLoggingPrv(DashBoardFxmlController.controller.loggedSession());
        bindValidationOnPaneControlFocus(main_grid);
    }

    @FXML
    private void onBrowseImgBtnAction(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            imgString = ImageHandler.
                    getImageBy(file, user_name_txt.getText().trim(), ImageHandler.USER_PATH);
            usr_img_view.setImage(imgString.getImg());

        }
    }

    @FXML
    private void newBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(main_grid);
        imgString = null;
        usr_img_view.setImage(new Image(getClass().getResourceAsStream(ImageHandler.MEMBER_DEFAULT_IMG)));
        FxUtilsHandler.activeDeactiveChildrenControls(true, main_grid);
    }

    @FXML
    private void saveBtnAction(ActionEvent event) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        UserHasUserRole uhur;
        int id = getUHasRoleIdByUserName(session, user_name_txt.getText());
        if (id != 0) {
            uhur = (UserHasUserRole) session.load(UserHasUserRole.class, id);
        } else {
            uhur = new UserHasUserRole();
            registerInputValidation();
            if (!validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
                validationSupport.validationResultProperty().get().getErrors()
                        .forEach(e -> {
//                            System.out.println(e.getTarget().getId());
//                            System.out.println(e.getText());
//                            System.out.println("==============================");
                        });
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Missing Fields !");
                alert_error.setContentText("You have some error fields left."
                        + " Move the cursor to the red \"X\""
                        + " sign and find the error.");
                alert_error.show();
                return;
            }
        }
        User u = new User();
        u.setFullName(full_name_txt.getText());
        u.setUserName(user_name_txt.getText());
        u.setEmail(email_txt.getText());
        u.setAddress(address_txt.getText());
        u.setTel(tele_txt.getText());
        u.setImgPath(imgString == null ? "" : imgString.getImg_path().toString());
        u.setStatus(true);

        uhur.setUser(u);
        uhur.setUserRole(getUserRoleFrom(usr_role_combo.getSelectionModel()
                .getSelectedItem(), session));
        session.saveOrUpdate(uhur);
        session.getTransaction().commit();
        session.close();

        Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
        alert_success.setTitle("Success");
        alert_success.setHeaderText("Successfully Saved !");
        alert_success.setContentText("You have successfully saved"
                + " \"" + full_name_txt.getText() + "\".");
        Optional<ButtonType> result = alert_success.showAndWait();
        if (result.get() == ButtonType.OK) {
            initUserRoleTable(getAllUserRoles());
        }
    }

    @FXML
    private void deactiveBtnAction(ActionEvent event) {
        registerInputValidation();
    }

    @FXML
    private void deleteBtnAction(ActionEvent event) {
    }

    @FXML
    private void onNewRoleBtnAction(ActionEvent event) throws IOException, SAXException, ParserConfigurationException {
        fillCheckBoxTreeView(check_tree_view, 0);
        usr_role_name_txt.setText("");
    }

    @FXML
    private void onSaveRoleBtnAction(ActionEvent event) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        UserRole uuRole;
        int id = getIdByUserRole(session, usr_role_name_txt.getText().trim().toLowerCase());
        if (id != 0) {
            uuRole = (UserRole) session.load(UserRole.class, id);
        } else {
            uuRole = new UserRole();
        }

        uuRole.setRoleName(usr_role_name_txt.getText().trim().toLowerCase());
        uuRole.setStatus(true);

        if (checkedItems != null) {
            //first have to delete already exist privileges
            deleteAllDBExitsPrivilages(session, uuRole.getId());
            Set<PrivCat> pCats = new HashSet<>();
            checkedItems.forEach(e -> {
                PrivCat pc = new PrivCat();
                pc.setUserRole(uuRole);
                pc.setUsrRolePrivilage(getPrivilegeByPrivId(session, e.getValue().getId()));
                pCats.add(pc);
            });
            uuRole.setPrivCats(pCats);
        }

        session.saveOrUpdate(uuRole);
        session.getTransaction().commit();
        session.close();

        Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
        alert_success.setTitle("Success");
        alert_success.setHeaderText("Successfully Saved !");
        alert_success.setContentText("You have successfully save the \"" + usr_role_name_txt.getText() + " user Role\"");
        Optional<ButtonType> showAndWait = alert_success.showAndWait();
        if (showAndWait.get() == ButtonType.OK) {
            initUserRoleTable(getAllUserRoles());
            FxUtilsHandler.clearFields(main_grid);
            imgString = null;
            usr_img_view.setImage(new Image(getClass().getResourceAsStream(ImageHandler.MEMBER_DEFAULT_IMG)));
            FxUtilsHandler.activeDeactiveChildrenControls(true, main_grid);
        }

    }

    @FXML
    private void onDeaciveRoleBtnAction(ActionEvent event) {
    }

    @FXML
    private void resetPassAction(ActionEvent event) throws IOException {
        boolean flag = isPassAlreadyExist(user_name_txt.getText());
        if (flag) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/NewPassFxml.fxml"));
            VBox node = (VBox) loader.load();
            NewPassFxmlController controller = (NewPassFxmlController) loader.getController();
            Alert alert_custom = new Alert(Alert.AlertType.NONE);
            alert_custom.setTitle("New Password");
            alert_custom.getDialogPane().setContent(node);
            ButtonType buttonTypeCancel = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert_custom.getButtonTypes().add(buttonTypeCancel);
            alert_custom.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);
            alert_custom.show();
            validateNewPassAlertFields(controller);
            controller.getCancel_btn().setOnAction(e -> {
                alert_custom.hide();
            });
            controller.getSave_btn().setOnAction(e -> {
                //Save command here.......
                if (va1.validationResultProperty().get().getErrors().isEmpty()) {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    int userId = getUserIdByUserName(session, user_name_txt.getText());
                    User uLoaded = (User) session.load(User.class, userId);
                    uLoaded.setPassword(PasswordHandler.
                            encryptPassword(controller.getPass_again_field().getText()));
                    session.update(uLoaded);
                    session.getTransaction().commit();
                    session.close();
                    controller.getError_label().setStyle("-fx-text-fill: #349a46;");
                    controller.getError_label().setText("Successfully saved the password.");
                } else {
                    Collection<ValidationMessage> e_messages = va1.validationResultProperty()
                            .get().getMessages();
                    String error_txt = "";
                    for (ValidationMessage e_msg : e_messages) {
                        error_txt += e_msg.getText() + "\n";
                    }
                    controller.getError_label().setText(error_txt);
                }
            });
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/ResetPassFxml.fxml"));
            VBox node = (VBox) loader.load();
            ResetPassFxmlController controller = (ResetPassFxmlController) loader.getController();
            Alert alert_custom = new Alert(Alert.AlertType.NONE);
            alert_custom.setTitle("Reset Password");
            alert_custom.getDialogPane().setContent(node);
            ButtonType buttonTypeCancel = new ButtonType("", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert_custom.getButtonTypes().add(buttonTypeCancel);
            alert_custom.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);
            alert_custom.show();
            validateResetPassAlertFields(controller);
            controller.getCancel_btn().setOnAction(e -> {
                alert_custom.hide();
            });
            controller.getRest_btn().setOnAction(e -> {
                //Reset Command here.....
                if (va2.validationResultProperty().get().getErrors().isEmpty()) {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    int userId = getUserIdByUserName(session, user_name_txt.getText());
                    User uLoaded = (User) session.load(User.class, userId);
                    uLoaded.setPassword(PasswordHandler.
                            encryptPassword(controller.getPass_again_field().getText()));
                    session.update(uLoaded);
                    session.getTransaction().commit();
                    session.close();
                    controller.getError_label().setStyle("-fx-text-fill: #349a46;");
                    controller.getError_label().setText("Successfully saved the password.");
                } else {
                    Collection<ValidationMessage> e_messages = va2.validationResultProperty()
                            .get().getMessages();
                    String error_txt = "";
                    for (ValidationMessage e_msg : e_messages) {
                        error_txt += e_msg.getText() + "\n";
                    }
                    controller.getError_label().setText(error_txt);
                }
            });
        }
    }

    @FXML
    private void searchBtnAction(ActionEvent event) {
        if (usr_role_sr_txt.getText().isEmpty() && full_name_src_txt.getText().isEmpty()) {
            searchUser("", "");
        }
        searchUser(usr_role_sr_txt.getText(), full_name_src_txt.getText());
    }

    @FXML
    private void clearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(search_grid);
        searchUser("", "");
    }

    private UserRole getUserRoleFrom(String selectedItem, Session session) {
        Criteria c = session.createCriteria(UserRole.class);
        c.add(Restrictions.eq("roleName", selectedItem));
        c.add(Restrictions.eq("status", true));
        UserRole uRole = (UserRole) c.uniqueResult();
        return uRole;
    }

    private int getUHasRoleIdByUserName(Session session, String userName) {
        Criteria c = session.createCriteria(UserHasUserRole.class);
        c.createAlias("user", "u");
        c.add(Restrictions.eq("u.userName", userName));
        UserHasUserRole filteredUser = (UserHasUserRole) c.uniqueResult();
        return filteredUser != null ? filteredUser.getId() : 0;
    }

    private List<String> getAlreadyTakenUserNames() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(User.class);
        List<String> uNames = c.setProjection(Projections.property("userName")).list();
        session.close();
        return uNames;
    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
        validationSupport.registerValidator(full_name_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(email_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Must be a valid email address.",
                                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Severity.ERROR)));
        validationSupport.registerValidator(tele_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Should be a telephone number with 10 digits.",
                                "\\d{10}", Severity.ERROR)));
        validationSupport.registerValidator(usr_role_combo,
                Validator.createEmptyValidator("User Role Selection is required."));

        Validator<String> checkUserNames = ((control, value) -> {
            boolean condition = value != null ? getAlreadyTakenUserNames()
                    .contains(value) : value == null;
            reset_btn.setDisable(!condition);
            return ValidationResult.fromMessageIf(control,
                    "Username is already taken .", Severity.ERROR, condition);
        });

        validationSupport.registerValidator(user_name_txt, Validator.combine(
                Validator.createEmptyValidator("This field is not optional."), checkUserNames));
    }

    private List<String> getAvailableUserRoles() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(UserRole.class);
        List<String> roles = c.setProjection(Projections.property("roleName")).list();
        session.close();
        return roles;
    }

    private List<String> getAvailableUserFullNames() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(User.class);
        List<String> uFullNames = c.setProjection(Projections.property("fullName")).list();
        session.close();
        return uFullNames;
    }

    private boolean isPassAlreadyExist(String text) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(User.class);
        c.add(Restrictions.eq("userName", text));
        User u = (User) c.uniqueResult();
        session.close();
        return u.getPassword() == null || u.getPassword().trim().isEmpty();
    }

    private void autoCompleteFieldData(AutoCompletionBinding<String> binding,
            List<String> bindingList, TextField field) {

        if (binding != null) {
            binding.dispose();
        }
        binding = TextFields.bindAutoCompletion(field, bindingList);

    }

    private void searchUser(String role, String fullName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(UserHasUserRole.class);
        c.createAlias("userRole", "ur")
                .createAlias("user", "u");

        if (!role.isEmpty()) {
            c.add(Restrictions.eq("ur.roleName", role));
        }
        if (!fullName.isEmpty()) {
            c.add(Restrictions.eq("u.fullName", fullName));
        }
        List<UserHasUserRole> uhur = c.list();
        initUserTable(FXCollections.observableArrayList(uhur));
    }

    private void initUserTable(ObservableList<UserHasUserRole> userList) {

        usr_name_col.setCellValueFactory((TableColumn.CellDataFeatures<UserHasUserRole, String> param)
                -> new SimpleStringProperty(param.getValue().getUser().getUserName()));
        full_name_col.setCellValueFactory((TableColumn.CellDataFeatures<UserHasUserRole, String> param)
                -> new SimpleStringProperty(param.getValue().getUser().getFullName()));
        email_col.setCellValueFactory((TableColumn.CellDataFeatures<UserHasUserRole, String> param)
                -> new SimpleStringProperty(param.getValue().getUser().getEmail()));
        usr_role_col.setCellValueFactory((TableColumn.CellDataFeatures<UserHasUserRole, String> param)
                -> new SimpleStringProperty(param.getValue().getUserRole().getRoleName()));
        status_col.setCellValueFactory((TableColumn.CellDataFeatures<UserHasUserRole, String> param)
                -> new SimpleStringProperty(param.getValue().getUser().isStatus() ? "Active" : "Deactivated"));

        user_tbl.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (user_tbl.getSelectionModel().getSelectedItem() != null) {
                        UserHasUserRole uhur = user_tbl.getSelectionModel().getSelectedItem();
                        try {
                            getUserHasRoleById(uhur.getId());
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(UserManageFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        user_tbl.getItems().clear();
        user_tbl.getItems().addAll(userList);
    }

    private void getUserHasRoleById(Integer id) throws MalformedURLException {

        imgString = new ImageWithString();
        Session session = HibernateUtil.getSessionFactory().openSession();
        UserHasUserRole lu = (UserHasUserRole) session.load(UserHasUserRole.class, id);
        full_name_txt.setText(lu.getUser().getFullName());
        user_name_txt.setText(lu.getUser().getUserName());
        email_txt.setText(lu.getUser().getEmail());
        address_txt.setText(lu.getUser().getAddress());
        tele_txt.setText(lu.getUser().getTel());
        usr_role_combo.getSelectionModel().select(lu.getUserRole().getRoleName());
        if (!lu.getUser().getImgPath().trim().isEmpty()) {
            imgString.setImg_path(new File(lu.getUser().getImgPath()).toPath());
            usr_img_view.setImage(new Image(new File(lu.getUser().getImgPath()).toURI().toURL().toString()));
        }
        session.close();

    }

    private void validateNewPassAlertFields(NewPassFxmlController controller) throws IOException {
        va1.registerValidator(controller.getPass_field(),
                Validator.combine(Validator.createEmptyValidator("This field cannot be empty!"),
                        Validator.createRegexValidator(
                                PropHandler.getStringProperty("pass_valid_error_msg"),
                                PropHandler.getRegexProperty("pass_valid_regex"),
                                Severity.ERROR)));

        Validator<String> checkSamePass = ((control, value) -> {
            boolean condition = value != null ? controller.getPass_field()
                    .getText().equals(value) : value == null;
            try {
                return ValidationResult.fromMessageIf(control,
                        PropHandler.getStringProperty("pass_equal_valid"), Severity.ERROR, !condition);
            } catch (IOException ex) {
                Logger.getLogger(UserManageFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        });

        va1.registerValidator(controller.getPass_again_field(),
                Validator.combine(Validator.createEmptyValidator("This field cannot be empty!"),
                        checkSamePass));
    }

    private void validateResetPassAlertFields(ResetPassFxmlController controller) throws IOException {

        Validator<String> checkOldPass = ((control, value) -> {
            boolean condition = value != null ? isCorrectPassOf(value, user_name_txt.getText()) : value == null;
            try {
                return ValidationResult.fromMessageIf(control,
                        PropHandler.getStringProperty("old_pass_check"), Severity.ERROR, !condition);
            } catch (IOException ex) {
                Logger.getLogger(UserManageFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        });

        va2.registerValidator(controller.getOldpass_field(), Validator.combine(
                Validator.createEmptyValidator("This field cannot be empty!"), checkOldPass));

        va2.registerValidator(controller.getPass_field(),
                Validator.combine(Validator.createEmptyValidator("This field cannot be empty!"),
                        Validator.createRegexValidator(
                                PropHandler.getStringProperty("pass_valid_error_msg"),
                                PropHandler.getRegexProperty("pass_valid_regex"),
                                Severity.ERROR)));

        Validator<String> checkSamePass = ((control, value) -> {
            boolean condition = value != null ? controller.getPass_field()
                    .getText().equals(value) : value == null;
            try {
                return ValidationResult.fromMessageIf(control,
                        PropHandler.getStringProperty("pass_equal_valid"), Severity.ERROR, !condition);
            } catch (IOException ex) {
                Logger.getLogger(UserManageFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        });

        va2.registerValidator(controller.getPass_again_field(),
                Validator.combine(Validator.createEmptyValidator("This field cannot be empty!"),
                        checkSamePass));
    }

    private int getUserIdByUserName(Session session, String user_name) {
        Criteria c = session.createCriteria(User.class);
        c.add(Restrictions.eq("userName", user_name));
        User u = (User) c.uniqueResult();
        if (u != null) {
            return u.getId();
        } else {
            return 0;
        }
    }

    private boolean isCorrectPassOf(String value, String userName) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(User.class);
        User fUser = (User) c.add(Restrictions.eq("userName", userName))
                .uniqueResult();
        if (fUser != null) {
            return PasswordHandler.isValidPass(value, fUser.getPassword());
        }
        return false;
    }

    private void fillCheckBoxTreeView(CheckTreeView<TreeItemHandler.ItemProp> treeView, int roleId)
            throws IOException, SAXException, ParserConfigurationException {

        TreeItemHandler handler = new TreeItemHandler();
        handler.setRoleId(roleId);
        CheckBoxTreeItem<TreeItemHandler.ItemProp> items = handler
                .readData(new File("src/com/court/asserts/privilege_tree.xml"));
        treeView.setRoot(items);
        items.getChildren().get(0).getParent().setExpanded(true);

        treeView.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener.Change<? extends TreeItem<TreeItemHandler.ItemProp>> c) -> {
                    checkedItems = check_tree_view.getCheckModel().getCheckedItems();
                });
    }

    private void initUserRoleTable(ObservableList<UserRole> roles) {
        name_col.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        stt_col.setCellValueFactory(new PropertyValueFactory<>("status"));

        stt_col.setCellFactory(column -> {
            return new TableCell<UserRole, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(item ? "Active" : "Deactivated");
                    }
                }

            };
        });
        action_col.setCellFactory((TableColumn<UserRole, Button> param) -> {
            return new TableCell<UserRole, Button>() {
                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<UserRole> tableRow = getTableRow();
                    if (!empty) {
                        item = new Button("Delete");
                        item.getStyleClass().add("btn");
                        item.getStyleClass().add("btn-danger");
                        item.setStyle("-fx-text-fill:#ffffff;");
                        item.setOnAction((event) -> {
                            System.out.println(tableRow.getItem().getRoleName());
                        });
                        setGraphic(item);
                    }
                }

            };
        });
        role_tbl.setItems(roles);
        role_tbl.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
            if (role_tbl.getSelectionModel().getSelectedItem() != null) {
                UserRole selectedRole = role_tbl.getSelectionModel().getSelectedItem();
                usr_role_name_txt.setText(selectedRole.getRoleName());
                getRoleInfoById(selectedRole.getId());
            }
        });

        role_tbl.getSelectionModel().selectFirst();
    }

    private void getRoleInfoById(Integer id) {
        try {
            fillCheckBoxTreeView(check_tree_view, id);
        } catch (Exception ex) {
            Logger.getLogger(UserManageFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ObservableList<UserRole> getAllUserRoles() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<UserRole> rList = session.createCriteria(UserRole.class).list();
        session.close();
        return FXCollections.observableArrayList(rList);
    }

    private int getIdByUserRole(Session session, String usrRole) {
        Criteria c = session.createCriteria(UserRole.class);
        c.add(Restrictions.eq("roleName", usrRole));
        UserRole rol = (UserRole) c.uniqueResult();
        return rol != null ? rol.getId() : 0;
    }

    private UsrRolePrivilage getPrivilegeByPrivId(Session session, int id) {
        Criteria c = session.createCriteria(UsrRolePrivilage.class);
        UsrRolePrivilage prv = (UsrRolePrivilage) c.add(Restrictions.
                eq("privId", id)).uniqueResult();
        System.out.println(prv.getPrivName());
        return prv;
    }

    private void deleteAllDBExitsPrivilages(Session session, Integer id) {
        String sql = "DELETE FROM PrivCat WHERE userRole.id= :uroleId";
        session.createQuery(sql).setString("uroleId", String.valueOf(id)).executeUpdate();
    }

    private void disableButtonWithLoggingPrv(LoggedSessionHandler ls) {

        ud_save_btn.setDisable(!ls.checkPrivilegeExist(10102));
        ud_deact_btn.setDisable(!ls.checkPrivilegeExist(10103));
        ud_delete_btn.setDisable(!ls.checkPrivilegeExist(10104));
        reset_btn.setDisable(!ls.checkPrivilegeExist(10105));
        ur_save_btn.setDisable(!ls.checkPrivilegeExist(10107));
        ur_deact_btn.setDisable(!ls.checkPrivilegeExist(10108));
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
}
