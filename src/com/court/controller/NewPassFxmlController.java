/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.FxUtilsHandler;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class NewPassFxmlController implements Initializable {

    @FXML
    private VBox boxfield_2;
    @FXML
    private VBox boxfield_1;
    @FXML
    private PasswordField pass_again_field;
    @FXML
    private PasswordField pass_field;
    @FXML
    private Button save_btn;
    @FXML
    private Button cancel_btn;
    @FXML
    private Label error_label;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FxUtilsHandler.showHidePasswordField(pass_field, boxfield_1);
        FxUtilsHandler.showHidePasswordField(pass_again_field, boxfield_2);
    }

    public PasswordField getPass_again_field() {
        return pass_again_field;
    }

    public PasswordField getPass_field() {
        return pass_field;
    }

    public Button getSave_btn() {
        return save_btn;
    }

    public Button getCancel_btn() {
        return cancel_btn;
    }

    public Label getError_label() {
        return error_label;
    }

    

}
