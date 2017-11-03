/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.model.Member;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Shanaka P
 */
public class CheckBoxCellValueFactory implements Callback<TableColumn.CellDataFeatures<Member, CheckBox>, ObservableValue<CheckBox>> {

    @Override
    public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Member, CheckBox> param) {
        Member ml = param.getValue();
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().setValue(true);
        checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            ml.setStatus(new_val);
        });
        return new SimpleObjectProperty<>(checkBox);
    }

}
