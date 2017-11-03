/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.controller.DisplayLoansTableFxmlController;
import com.court.model.Member;
import com.court.model.MemberLoan;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

/**
 *
 * @author Shanaka P
 */
public class DisplayTotalInstallmentsFactory implements Callback<TableColumn.CellDataFeatures<Member, Button>, ObservableValue<Button>> {

    @Override
    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Member, Button> param) {
        Member ml = param.getValue();
        List<MemberLoan> list = ml.getMemberLoans().stream()
                .sorted(Comparator.comparing(p -> p.getId()))
                .filter(p -> !p.isIsComplete())
                .filter(p -> p.isStatus())
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                .collect(Collectors.toList());

        double sum = list.stream().mapToDouble(p -> p.getLoanInstallment()).sum();
        param.getValue().setTotalPayment(sum);
        Button button = new Button("View Info");
        button.setOnAction((evt) -> {
            Alert alert_details = new Alert(Alert.AlertType.INFORMATION);
            alert_details.setTitle("Loan Information");
            alert_details.setHeaderText("Member installment information for each loan");
            alert_details.getDialogPane().setContent(createContentGrid(list, sum));
            alert_details.show();
        });
        return new SimpleObjectProperty<>(button);
    }

    private Node createContentGrid(List<MemberLoan> list, double sum) {
        VBox pane;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/DisplayLoansTableFxml.fxml"));
        try {
            pane = (VBox) loader.load();
            DisplayLoansTableFxmlController controller = (DisplayLoansTableFxmlController) loader.getController();
            controller.createTableView(list);
            if (!list.isEmpty()) {
                controller.setTot_inst(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                        .format(sum));
            }
        } catch (IOException ex) {
            Logger.getLogger(DisplayTotalInstallmentsFactory.class.getName()).log(Level.SEVERE, null, ex);
            pane = null;
        }
        return pane;
    }
;
}
