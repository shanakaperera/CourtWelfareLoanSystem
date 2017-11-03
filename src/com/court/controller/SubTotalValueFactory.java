/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.model.Member;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Shanaka P
 */
public class SubTotalValueFactory implements Callback<TableColumn.CellDataFeatures<Member, Double>, ObservableValue<Double>> {


    @Override
    public ObservableValue<Double> call(TableColumn.CellDataFeatures<Member, Double> param) {
        Member m = param.getValue();
        double tot = m.getTotalSubscription() + m.getTotalPayment();
        return new SimpleObjectProperty<>(tot);
    }

}
