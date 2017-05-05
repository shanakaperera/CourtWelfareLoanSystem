/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.GlyphIcons;
import com.court.handler.ImageHandler;
import com.court.handler.LoggedSessionHandler;
import com.court.main.MainClass;
import com.court.model.UserHasUserRole;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class DashBoardFxmlController implements Initializable {

    public static DashBoardFxmlController controller;
    private LoggedSessionHandler sHandler;
    @FXML
    private Button btnMenuBar;
    @FXML
    private VBox dataPane;
    @FXML
    private Button dashboard_btn;
    @FXML
    private Button collect_sheet_btn;
    @FXML
    private Button member_btn;
    @FXML
    private Button branch_btn;
    @FXML
    private Button payment_btn;
    @FXML
    private Button report_btn;
    @FXML
    private MenuItem usrmng_menu_item;
    @FXML
    private Button logout_btn;
    @FXML
    private HBox base_h_box;
    @FXML
    private SplitPane base_split_pane;

    public static Stage login;
    @FXML
    private MenuItem loanmng_menu_item;
    @FXML
    private MenuItem loancal_menu_item;

    private ImageView progressIndicator;

    public LoggedSessionHandler loggedSession() {
        return sHandler;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progressIndicator = new ImageView();
        progressIndicator.setImage(new Image(ImageHandler.LOADING_DEFAULT_GIF));
        btnMenuBar.setGraphic(new GlyphIcons().setFontAwesomeIconGlyph('\uf007', Color.WHITESMOKE, 20.0));
        try {
            loadDataPane("/com/court/view/HomeFXML.fxml");
            controller = this;

        } catch (IOException ex) {
            Logger.getLogger(DashBoardFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//
//    public void setDataPane(Node node) {
//        // update VBox with new form(FXML) depends on which button is clicked
//        dataPane.getChildren().setAll(node);
//    }

    public void loginSuccess() throws IOException {
        base_split_pane.setEffect(null);
        disableButtonWithLoggingPrv(loggedSession());
    }

    public void setLoggedSession(UserHasUserRole uhur) {
        sHandler = new LoggedSessionHandler(uhur);
        btnMenuBar.setText(loggedSession().loggedUser().getFullName());
    }

    public void performLoginAction(Stage stage) throws IOException {
        //navigate to dashboard 
        loadDataPane("/com/court/view/HomeFXML.fxml");

        login = new Stage();
        login.initStyle(StageStyle.TRANSPARENT);
        setFadeEffect(new GaussianBlur(10));
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/com/court/view/LoginFxml.fxml")));
        scene.setFill(null);
        login.setScene(scene);
        login.initModality(Modality.WINDOW_MODAL);
        login.initOwner(stage);
        login.show();

    }

    public void setFadeEffect(Effect effect) {
        base_split_pane.setEffect(effect);
    }

    public VBox fadeAnimate(String url) throws IOException {
        VBox v = (VBox) FXMLLoader.load(getClass().getResource(url));
        FadeTransition ft = new FadeTransition(Duration.millis(1500));
        ft.setNode(v);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
        return v;
    }

    public void loadDataPane(String url) throws IOException {
        VBox v = new VBox(progressIndicator);
        v.setAlignment(Pos.CENTER);
        dataPane.getChildren().setAll(v);
        Task<VBox> vboxTask = new Task<VBox>() {
            {
                setOnSucceeded(e -> {
                    dataPane.getChildren().setAll(getValue());
                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected VBox call() throws Exception {
                return FXMLLoader.load(getClass().getResource(url));
            }
        };
        Thread fillpaneThread = new Thread(vboxTask, "vbox-task");
        fillpaneThread.setDaemon(true);
        fillpaneThread.start();
    }

    @FXML
    private void dashboardBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/HomeFXML.fxml");
    }

    @FXML
    private void collectSheetBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/CollectionSheetFxml.fxml");
    }

    @FXML
    private void memberBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/Memberfxml.fxml");
    }

    @FXML
    private void branchBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/BranchFxml.fxml");
    }

    @FXML
    private void paymentBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/PaymentsFxml.fxml");
    }

    @FXML
    private void reportBtnAction(ActionEvent event) {
    }

    @FXML
    private void usrmngBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/UserManageFxml.fxml");
    }

    @FXML
    private void logoutBtnAction(ActionEvent event) throws IOException {
        btnMenuBar.setText("Logged User");
        performLoginAction(MainClass.primaryStage);
    }

    @FXML
    private void loanmngBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/LoanFxml.fxml");
    }

    @FXML
    private void loancalBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/LoanCalculatorFxml.fxml");
    }

    private void disableButtonWithLoggingPrv(LoggedSessionHandler ls) {

        collect_sheet_btn.setDisable(!anyChildPrivExist("105", 2, ls));
        member_btn.setDisable(!anyChildPrivExist("102", 2, ls));
        loanmng_menu_item.setDisable(!anyChildPrivExist("103", 4, ls));
        loancal_menu_item.setDisable(!anyChildPrivExist("106", 1, ls));
        branch_btn.setDisable(!anyChildPrivExist("104", 4, ls));
        payment_btn.setDisable(!anyChildPrivExist("107", 1, ls));
        report_btn.setDisable(!anyChildPrivExist("108", 4, ls));
        usrmng_menu_item.setDisable(!anyChildPrivExist("101", 8, ls));

    }

    private boolean anyChildPrivExist(String part, int till, LoggedSessionHandler ls) {
        int val = Integer.parseInt(part + "00");
        boolean flag = false;
        for (int i = 1; i <= till; i++) {
            val = val + 1;
            flag = ls.checkPrivilegeExist(val);
            if (flag) {
                break;
            }
        }
        return flag;
    }

    @FXML
    private void onLoggedUsrBtnAction(ActionEvent event) throws IOException {
        if (loggedSession() != null) {
            loadDataPane("/com/court/view/ProfileFxml.fxml");
        }
    }

}
