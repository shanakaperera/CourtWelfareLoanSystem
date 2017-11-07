/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.TextFormatHandler;
import com.court.model.SubscriptionPay;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class SubscriptionPayChkFxmlController implements Initializable {

    @FXML
    private TableView<SubscriptionPay> subs_table;
    @FXML
    private TableColumn<SubscriptionPay, String> sub_mbr_id_col;
    @FXML
    private TableColumn<SubscriptionPay, String> sub_mbr_name_col;
    @FXML
    private TableColumn<SubscriptionPay, String> subs_tot_col;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void createDetailTable(ObservableList<SubscriptionPay> payments) {
        sub_mbr_id_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            String id = param.getValue().getMemberSubscriptions().getMember().getMemberId();
            return new SimpleStringProperty(id);
        });
        sub_mbr_name_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            String name = param.getValue().getMemberSubscriptions().getMember().getFullName();
            return new SimpleStringProperty(name);
        });
        subs_tot_col.setCellValueFactory((TableColumn.CellDataFeatures<SubscriptionPay, String> param) -> {
            SubscriptionPay value = param.getValue();
            double tot = value.getAciFee() + value.getAdmissionFee()
                    + value.getMembershipFee() + value.getOptional() + value.getSavingsFee() + value.getHoiFee();
            return new SimpleStringProperty(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot));
        });
        subs_table.setItems(payments);
    }

}
