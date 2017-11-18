/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.controller.DisplaySubscriptionFxmlController;
import com.court.model.Member;
import com.court.model.MemberSubscriptions;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 *
 * @author Shanaka P
 */
public class DisplaySubscriptionFactory implements Callback<TableColumn.CellDataFeatures<Member, Button>, ObservableValue<Button>> {

    @Override
    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Member, Button> param) {
        Member ml = param.getValue();

        List<MemberSubscriptions> mbrSubs = new ArrayList<>(ml.getMemberSubscriptions());

        double sum;
        boolean flag = FxUtilsHandler.previousInstallments(ml.getId()).isEmpty();
        if (flag) {
            sum = mbrSubs.stream().mapToDouble(a -> a.getAmount()).sum();
        } else {
            sum = mbrSubs.stream().filter(s -> !s.getRepaymentType()
                    .equalsIgnoreCase("Once")).mapToDouble(a -> a.getAmount()).sum();
        }
        param.getValue().setTotalSubscription(sum);
        Button button = new Button("View Info");
        button.setOnAction((evt) -> {
            Alert alert_details = new Alert(Alert.AlertType.INFORMATION);
            alert_details.setTitle("Subscription Information");
            alert_details.setHeaderText("Member Subscription information for each installment");
            alert_details.getDialogPane().setContent(createContentGrid(mbrSubs, sum, flag));
            alert_details.show();
        });

        return new SimpleObjectProperty<>(button);
    }

    private Node createContentGrid(List<MemberSubscriptions> mbrSubs, double sum, boolean flag) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/DisplaySubscriptionFxml.fxml"));
        GridPane pane;
        try {
            pane = (GridPane) loader.load();
            DisplaySubscriptionFxmlController controller = (DisplaySubscriptionFxmlController) loader.getController();
            callByName(controller, "setValueSubs_tot", TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));

            for (int i = 0; i < mbrSubs.size(); i++) {
                int j = i + 1;
                MemberSubscriptions get = mbrSubs.get(i);
                callByName(controller, "setValueSub_" + j, get.getMemberSubscription().getFeeName());
                callByName(controller, "setValueAmt_" + j, TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(get.getAmount()));
                callByNameCheck(controller, "setValueTkn_" + j, getparamValue(flag, get));
            }

        } catch (IOException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            pane = null;
            Logger.getLogger(DisplaySubscriptionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pane;
    }

    private void callByName(DisplaySubscriptionFxmlController controller, String string, String arg)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        controller.getClass().getDeclaredMethod(string, String.class).invoke(controller, arg);
    }

    private void callByNameCheck(DisplaySubscriptionFxmlController controller, String string, boolean arg)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        controller.getClass().getDeclaredMethod(string, Boolean.class).invoke(controller, arg);
    }

    private boolean getparamValue(boolean flag, MemberSubscriptions get) {
        if (get.getRepaymentType().equalsIgnoreCase("Once") && !flag) {
            return false;
        } else {
            return true;
        }
    }
}
