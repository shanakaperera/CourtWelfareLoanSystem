/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.GlyphIcons;
import com.court.handler.FileHandler;
import com.court.model.User;
import com.court.model.UserHasUserRole;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class LoginFxmlController implements Initializable {

    @FXML
    private TextField unameField;
    @FXML
    private Button loginBtn;
    @FXML
    private PasswordField passField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button closeBtn;
    private BasicPasswordEncryptor encryptor;
    @FXML
    private ImageView loading_imgV;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        encryptor = new BasicPasswordEncryptor();
        loginBtn.setGraphic(new GlyphIcons().setFontAwesomeIconGlyph('\uf023', Color.WHITESMOKE, 20.0));
        closeBtn.setGraphic(new GlyphIcons().setFontAwesomeIconGlyph('\uf00d', Color.WHITESMOKE, 20.0));

        //==========DEVELOPMENT PROGRESS GOING ON============================
        unameField.setText("nimesh@1234");
        passField.setText("Nimesh@1234");
        //==========DEVELOPMENT PROGRESS GOING ON============================
    }

    @FXML
    private void loginBtnAction(ActionEvent event) throws IOException {
        performUserLogin();
    }

    @FXML
    private void closeBtnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Warning !");
        alert.setContentText("Are you sure you want to Exit from\n "
                + "Welfare Loan System ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private UserHasUserRole getUserHasUsrRoleFromUser(Session session, User loggedU) {
        Criteria c = session.createCriteria(UserHasUserRole.class);
        UserHasUserRole uniqueResult = (UserHasUserRole) c.createAlias("user", "u").
                add(Restrictions.eq("u.id", loggedU.getId()))
                .uniqueResult();
        return uniqueResult;
    }

    private void performUserLogin() throws IOException {
        loading_imgV.setImage(new Image(FileHandler.LOADING_LOGIN_GIF));
        Task<UserSession> userTask = new Task<UserSession>() {

            {
                setOnSucceeded(e -> {
                    UserSession us = getValue();
                    if (us.getUser() != null) {
                        if (encryptor.checkPassword(passField.getText().trim(), us.getUser().getPassword())) {
                            DashBoardFxmlController.controller.
                                    setLoggedSession(getUserHasUsrRoleFromUser(us.getSession(), us.getUser()));
                            try {
                                DashBoardFxmlController.controller.loginSuccess();
                            } catch (IOException ex) {
                                Logger.getLogger(LoginFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            DashBoardFxmlController.login.close();
                        } else {
                            loading_imgV.setImage(null);
                            errorLabel.setText("Incorrect password!. Please try again !");
                        }
                    } else {
                        loading_imgV.setImage(null);
                        errorLabel.setText("Invalid username!. Please try again !");
                    }
                    us.getSession().close();

                });
                setOnFailed(f -> getException().printStackTrace());
            }

            @Override
            protected UserSession call() throws Exception {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Criteria c = session.createCriteria(User.class);
                User loggedU = (User) c.add(Restrictions.eq("userName", unameField.getText().trim()))
                        .uniqueResult();
                return new UserSession(loggedU, session);
            }
        };

        Thread usrfigThread = new Thread(userTask, "user-task");
        usrfigThread.setDaemon(true);
        usrfigThread.start();

    }

}
