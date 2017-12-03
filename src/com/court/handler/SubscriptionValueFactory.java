/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.model.Member;
import com.court.model.MemberSubscriptions;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 *
 * @author Shanaka P
 */
public class SubscriptionValueFactory implements Callback<TableColumn.CellDataFeatures<Member, String>, ObservableValue<String>> {

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Member, String> param) {
        Member ml = param.getValue();
        List<MemberSubscriptions> mbrSubs = new ArrayList<>(ml.getMemberSubscriptions());
        double sum;
        if (FxUtilsHandler.previousSubscriptions(ml.getId()).isEmpty()) {
            sum = mbrSubs.stream().mapToDouble(a -> a.getAmount()).sum();
        } else {
            sum = mbrSubs.stream().filter(s -> !s.getRepaymentType().equalsIgnoreCase("Once")).mapToDouble(a -> a.getAmount()).sum();
        }

        return new SimpleObjectProperty<>(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
    }

}
