/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FileHandler;
import com.court.handler.ImageWithString;
import com.court.handler.LoggedSessionHandler;
import com.court.handler.PasswordHandler;
import com.court.handler.PropHandler;
import com.court.model.User;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
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
public class ProfileFxmlController implements Initializable {

    @FXML
    private TextField usr_name_txt;
    @FXML
    private TextField fullname_txt;
    @FXML
    private TextField email_txt;
    @FXML
    private TextArea adrs_txt;
    @FXML
    private TextField tel_text;
    @FXML
    private ImageView prof_imgView;
    @FXML
    private Button rest_btn;
    @FXML
    private GridPane main_grid;

    private LoggedSessionHandler loggedSession;
    private ValidationSupport validationSupport, va1;
    private ImageWithString imgString = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        va1 = new ValidationSupport();
        validationSupport = new ValidationSupport();
        loggedSession = DashBoardFxmlController.controller.loggedSession();
        loadLoggedUserToForm(loggedSession.loggedUser());
        usr_name_txt.textProperty().addListener(e -> {
            boolean b = getAlreadyTakenUserNames().contains(usr_name_txt.getText());
            rest_btn.setDisable(!b);
        });
        bindValidationOnPaneControlFocus(main_grid);
    }

    @FXML
    private void onImgBrowseBtnAction(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            imgString = FileHandler.
                    getImageBy(file, usr_name_txt.getText().trim(), FileHandler.USER_PATH);
            prof_imgView.setImage(imgString.getImg());

        }
    }

    @FXML
    private void onSaveBtnAction(ActionEvent event) throws IOException {
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

            User user = (User) session.load(User.class, loggedSession.loggedUser().getId());
            user.setUserName(usr_name_txt.getText());
            user.setFullName(fullname_txt.getText());
            user.setTel(tel_text.getText());
            user.setAddress(adrs_txt.getText());
            user.setEmail(email_txt.getText());
            user.setImgPath(imgString == null ? "" : imgString.getImg_path().toString());
            session.save(user);

            session.getTransaction().commit();
            session.close();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Successfully Updated !");
            success.setContentText("You have successfully updated your information !");
            Optional<ButtonType> result = success.showAndWait();
            if (result.get() == ButtonType.OK) {
                loadLoggedUserToForm(user);
                loggedSession.updateLoggedUser(user);
                DashBoardFxmlController.controller.setLoggedSession(loggedSession.getUrole());
            }
        }
    }

    @FXML
    private void onChangePassBtnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/ResetPassFxml.fxml"));
        VBox node = (VBox) loader.load();
        ResetPassFxmlController controller = (ResetPassFxmlController) loader.getController();
        Alert alert_custom = new Alert(Alert.AlertType.NONE);
        alert_custom.setTitle("Change Password");
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
            if (va1.validationResultProperty().get().getErrors().isEmpty()) {
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                int userId = getUserIdByUserName(session, usr_name_txt.getText());
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
    }

    private void loadLoggedUserToForm(User lu) {
        try {
            imgString = new ImageWithString();
            usr_name_txt.setText(lu.getUserName());
            fullname_txt.setText(lu.getFullName());
            adrs_txt.setText(lu.getAddress());
            email_txt.setText(lu.getEmail());
            tel_text.setText(lu.getTel());
            if (!lu.getImgPath().trim().isEmpty()) {
                imgString.setImg_path(new File(lu.getImgPath()).toPath());
                prof_imgView.setImage(new Image(new File(lu.getImgPath()).toURI().toURL().toString()));
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(ProfileFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validateResetPassAlertFields(ResetPassFxmlController controller) throws IOException {

        Validator<String> checkOldPass = ((control, value) -> {
            boolean condition = value != null ? isCorrectPassOf(value, usr_name_txt.getText()) : value == null;
            try {
                return ValidationResult.fromMessageIf(control,
                        PropHandler.getStringProperty("old_pass_check"), Severity.ERROR, !condition);
            } catch (IOException ex) {
                Logger.getLogger(UserManageFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        });

        va1.registerValidator(controller.getOldpass_field(), Validator.combine(
                Validator.createEmptyValidator("This field cannot be empty!"), checkOldPass));

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

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
        validationSupport.registerValidator(fullname_txt,
                Validator.createEmptyValidator("This field is not optional !"));
        validationSupport.registerValidator(email_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Must be a valid email address.",
                                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Severity.ERROR)));
        validationSupport.registerValidator(tel_text,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Should be a telephone number with 10 digits.",
                                "\\d{10}", Severity.ERROR)));

        validationSupport.registerValidator(usr_name_txt,
                Validator.createEmptyValidator("This field is not optional."));
    }

    private List<String> getAlreadyTakenUserNames() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(User.class);
        List<String> uNames = c.setProjection(Projections.property("userName")).list();
        session.close();
        return uNames;
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
