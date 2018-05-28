/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.GlyphIcons;
import com.court.handler.FileHandler;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    @FXML
    private MenuItem gen_menu_item;
    @FXML
    private Label dashboard_header;
    @FXML
    private Button guarant_btn;
    @FXML
    private Button old_loan_btn;
    @FXML
    private VBox dataPane2;
    @FXML
    private TabPane mainTabPane;
    private SingleSelectionModel<Tab> tabSelection;

    public LoggedSessionHandler loggedSession() {
        return sHandler;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabSelection = mainTabPane.getSelectionModel();
        progressIndicator = new ImageView();
        progressIndicator.setImage(new Image(FileHandler.LOADING_DEFAULT_GIF));
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

    public void loadDataPane2(String url) throws IOException {
        VBox v = new VBox(progressIndicator);
        v.setAlignment(Pos.CENTER);
        dataPane2.getChildren().setAll(v);
        Task<VBox> vboxTask = new Task<VBox>() {
            {
                setOnSucceeded(e -> {
                    dataPane2.getChildren().setAll(getValue());
                });
                setOnFailed(workerStateEvent -> getException().printStackTrace());
            }

            @Override
            protected VBox call() throws Exception {
                return FXMLLoader.load(getClass().getResource(url));
            }
        };
        Thread fillpaneThread = new Thread(vboxTask, "vbox-task-2");
        fillpaneThread.setDaemon(true);
        fillpaneThread.start();
    }

    @FXML
    private void dashboardBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/HomeFXML.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void collectSheetBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/CollectionSheetFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void memberBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/Memberfxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void branchBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/BranchFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void paymentBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/PaymentsFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void reportBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/ReportFormFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void usrmngBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/UserManageFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void logoutBtnAction(ActionEvent event) throws IOException {
        btnMenuBar.setText("Logged User");
        tabSelection.selectFirst();
        dataPane2.getChildren().clear();
        performLoginAction(MainClass.primaryStage);
    }

    @FXML
    private void loanmngBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/LoanFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void loancalBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/LoanCalculatorFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void onLoggedUsrBtnAction(ActionEvent event) throws IOException {
        if (loggedSession() != null) {
            loadDataPane("/com/court/view/ProfileFxml.fxml");
            tabSelection.selectFirst();
        }
    }

    @FXML
    private void genBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/GeneralSettingsFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void guarntBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/GuarantorsFxml.fxml");
        tabSelection.selectFirst();
    }

    @FXML
    private void oldLoanBtnAction(ActionEvent event) throws IOException {
        loadDataPane("/com/court/view/OldLoansFxml.fxml");
        tabSelection.selectFirst();
    }

    public Label getDashboard_header() {
        return dashboard_header;
    }

    public void disableButtonWithLoggingPrv(LoggedSessionHandler ls) {

        collect_sheet_btn.setDisable(!anyChildPrivExist("105", 2, ls));
        member_btn.setDisable(!anyChildPrivExist("102", 2, ls));
        loanmng_menu_item.setDisable(!anyChildPrivExist("103", 4, ls));
        loancal_menu_item.setDisable(!anyChildPrivExist("106", 1, ls));
        branch_btn.setDisable(!anyChildPrivExist("104", 4, ls));
        payment_btn.setDisable(!anyChildPrivExist("107", 1, ls));
        report_btn.setDisable(!anyChildPrivExist("108", 4, ls));
        usrmng_menu_item.setDisable(!anyChildPrivExist("101", 8, ls));
        guarant_btn.setDisable(!anyChildPrivExist("109", 1, ls));
        gen_menu_item.setDisable(!anyChildPrivExist("110", 3, ls));

        //MOST COMMON BUTTONS===========
        logout_btn.setDisable(false);
        dashboard_btn.setDisable(false);

    }

    public void disableAllButtons() {
        collect_sheet_btn.setDisable(true);
        member_btn.setDisable(true);
        loanmng_menu_item.setDisable(true);
        loancal_menu_item.setDisable(true);
        branch_btn.setDisable(true);
        payment_btn.setDisable(true);
        report_btn.setDisable(true);
        usrmng_menu_item.setDisable(true);
        guarant_btn.setDisable(true);
        gen_menu_item.setDisable(true);
        dashboard_btn.setDisable(true);
        logout_btn.setDisable(true);
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
    private void onCollectionSheetMousePressed(MouseEvent event) throws IOException {
        if (event.isSecondaryButtonDown()) {

            loadDataPane2("/com/court/view/CollectionSheetFxml.fxml");
            tabSelection.selectLast();
        }
    }

    @FXML
    private void onMemberMousePressed(MouseEvent event) throws IOException {
        if (event.isSecondaryButtonDown()) {

            loadDataPane2("/com/court/view/Memberfxml.fxml");
            tabSelection.selectLast();
        }
    }

    @FXML
    private void onGurantorsMousePressed(MouseEvent event) throws IOException {
        if (event.isSecondaryButtonDown()) {

            loadDataPane2("/com/court/view/GuarantorsFxml.fxml");
            tabSelection.selectLast();
        }
    }

    @FXML
    private void onOfficeMousePressed(MouseEvent event) throws IOException {
        if (event.isSecondaryButtonDown()) {

            loadDataPane2("/com/court/view/BranchFxml.fxml");
            tabSelection.selectLast();
        }
    }

    @FXML
    private void onReportsMousePressed(MouseEvent event) throws IOException {
        if (event.isSecondaryButtonDown()) {

            loadDataPane2("/com/court/view/ReportFormFxml.fxml");
            tabSelection.selectLast();
        }
    }

}
