/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.handler.TextFormatHandler;
import com.court.model.Member;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Shanaka P
 */
public class SubTotalValueFactory implements Callback<TableColumn.CellDataFeatures<Member, Label>, ObservableValue<Label>> {

    @Override
    public ObservableValue<Label> call(TableColumn.CellDataFeatures<Member, Label> param) {
        Member m = param.getValue();
        double tot = m.getTotalSubscription() + m.getTotalPayment();
        Label label = new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot));
        label.setStyle("-fx-font-weight: bold;-fx-font-size: 15px;");
        return new SimpleObjectProperty<>(label);
    }

}
