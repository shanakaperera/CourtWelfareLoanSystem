/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.TextFormatHandler;
import com.court.model.MemberSubscriptions;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class DisplaySubscriptionFxmlController implements Initializable {

    @FXML
    private Label sub_1;
    @FXML
    private Label sub_2;
    @FXML
    private Label sub_3;
    @FXML
    private Label sub_4;
    @FXML
    private Label sub_5;
    @FXML
    private Label sub_6;
    @FXML
    private Label subs_tot;
    @FXML
    private TextField amt_1;
    @FXML
    private TextField amt_2;
    @FXML
    private TextField amt_3;
    @FXML
    private TextField amt_4;
    @FXML
    private TextField amt_5;
    @FXML
    private TextField amt_6;
    @FXML
    private CheckBox tkn_1;
    @FXML
    private CheckBox tkn_2;
    @FXML
    private CheckBox tkn_3;
    @FXML
    private CheckBox tkn_4;
    @FXML
    private CheckBox tkn_5;
    @FXML
    private CheckBox tkn_6;

    public void setValueSub_1(String v) {
        this.sub_1.setText(v);
    }

    public void setValueAmt_1(String v) {
        this.amt_1.setText(v);
    }

    public void setValueSub_2(String v) {
        this.sub_2.setText(v);
    }

    public void setValueSub_3(String v) {
        this.sub_3.setText(v);
    }

    public void setValueSub_4(String v) {
        this.sub_4.setText(v);
    }

    public void setValueSub_5(String v) {
        this.sub_5.setText(v);
    }

    public void setValueSub_6(String v) {
        this.sub_6.setText(v);
    }

    public void setValueSubs_tot(String v) {
        this.subs_tot.setText(v);
    }

    public void setValueAmt_2(String v) {
        this.amt_2.setText(v);
    }

    public void setValueAmt_3(String v) {
        this.amt_3.setText(v);
    }

    public void setValueAmt_4(String v) {
        this.amt_4.setText(v);
    }

    public void setValueAmt_5(String v) {
        this.amt_5.setText(v);
    }

    public void setValueAmt_6(String v) {
        this.amt_6.setText(v);
    }

    public void setValueTkn_1(Boolean v) {
        this.tkn_1.setSelected(v);
    }

    public void setValueTkn_2(Boolean v) {
        this.tkn_2.setSelected(v);
    }

    public void setValueTkn_3(Boolean v) {
        this.tkn_3.setSelected(v);
    }

    public void setValueTkn_4(Boolean v) {
        this.tkn_4.setSelected(v);
    }

    public void setValueTkn_5(Boolean v) {
        this.tkn_5.setSelected(v);
    }

    public void setValueTkn_6(Boolean v) {
        this.tkn_6.setSelected(v);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        amt_1.setTextFormatter(TextFormatHandler.currencyFormatter());
        amt_2.setTextFormatter(TextFormatHandler.currencyFormatter());
        amt_3.setTextFormatter(TextFormatHandler.currencyFormatter());
        amt_4.setTextFormatter(TextFormatHandler.currencyFormatter());
        amt_5.setTextFormatter(TextFormatHandler.currencyFormatter());
        amt_6.setTextFormatter(TextFormatHandler.currencyFormatter());
    }

    public void initSubs(List<MemberSubscriptions> mbrSubs, double sum, boolean flag) {
        
    }

}
