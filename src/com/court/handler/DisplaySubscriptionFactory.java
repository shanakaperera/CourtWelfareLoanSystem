/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.controller.CollectionSheetFxmlController;
import com.court.model.Member;
import com.court.model.MemberSubscriptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 *
 * @author Shanaka P
 */
public class DisplaySubscriptionFactory implements Callback<TableColumn.CellDataFeatures<Member, Button>, ObservableValue<Button>> {

    private final TableView<Member> collection_tbl;
    CollectionSheetFxmlController csController;
    private double total;
    private final TextField chk_amt_txt;

    public DisplaySubscriptionFactory(TableView<Member> collection_tbl, CollectionSheetFxmlController csController, TextField chk_amt_txt) {
        this.collection_tbl = collection_tbl;
        this.csController = csController;
        this.total = this.csController.getTotal();
        this.chk_amt_txt = chk_amt_txt;
    }

    @Override
    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Member, Button> param) {
        Member ml = param.getValue();

        List<MemberSubscriptions> mbrSubs = new ArrayList<>(ml.getMemberSubscriptions());

        double sum;
        boolean flag = FxUtilsHandler.hasPreviousSubscriptions(ml.getId());
        if (flag) {
            sum = mbrSubs.stream().mapToDouble(a -> a.getAmount()).sum();
        } else {
            sum = mbrSubs.stream().filter(s -> !s.getRepaymentType()
                    .equalsIgnoreCase("Once")).mapToDouble(a -> a.getAmount()).sum();
        }
        param.getValue().setTotalSubscription(sum);
        Button button = new Button("View Info");
        button.setOnAction((evt) -> {

            if (ml.isCollected()) {
                ButtonType updateBtnType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
                Dialog<List<MemberSubscriptions>> alert_details = new Dialog();
                alert_details.setTitle("Subscription Information");
                alert_details.setHeaderText("Member Subscription information for each installment");
                alert_details.getDialogPane().getButtonTypes().addAll(updateBtnType, ButtonType.CANCEL);
                Node grid = createContentGrid(mbrSubs, sum, flag);
                alert_details.getDialogPane().setContent(grid);
                alert_details.setResultConverter(dialogBtn -> {
                    if (dialogBtn == updateBtnType) {
                        return new ArrayList<>(mbrSubs);
                    }
                    return null;

                });
                Optional<List<MemberSubscriptions>> result = alert_details.showAndWait();
                result.ifPresent(subs -> {
                    if (grid instanceof GridPane) {
                        List<TextField> children = ((GridPane) grid).getChildren()
                                .stream().filter(p -> p instanceof TextField)
                                .map(p -> (TextField) p).collect(Collectors.toList());
                        for (int i = 0; i < subs.size(); i++) {
                            Node c = children.get(i);
                            if (c instanceof TextField) {
                                if (((TextField) c).getText() != null) {
                                    subs.get(i).setAmount(TextFormatHandler.getCurrencyFieldValue(((TextField) c)));
                                }
                            }
                        }
                    }
                    double newValue = subs.stream().mapToDouble(a -> a.getAmount()).sum();
                    param.getValue().setTotalSubscription(newValue);
                    collection_tbl.refresh();
                    double diff = newValue - sum;
                    total = total + diff;
                    chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
                    csController.setTotal(total);
                });
            }

        });

        return new SimpleObjectProperty<>(button);
    }

    private Node createContentGrid(List<MemberSubscriptions> mbrSubs, double sum, boolean flag) {

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        Label totLabel = new Label(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
        totLabel.setFont(Font.font("System Bold", 21.0));
        Label col_h_1 = new Label("Subscription");
        col_h_1.setFont(Font.font("System Bold", 17.0));
        Label col_h_2 = new Label("Amount");
        col_h_2.setFont(Font.font("System Bold", 17.0));

        grid.add(col_h_1, 0, 0);
        grid.add(col_h_2, 1, 0);
        Label[] labels = new Label[mbrSubs.size()];
        TextField[] fields = new TextField[mbrSubs.size()];
        for (int i = 0; i < mbrSubs.size(); i++) {
            MemberSubscriptions get = mbrSubs.get(i);
            labels[i] = new Label("label");
            fields[i] = new TextField();
            fields[i].setTextFormatter(TextFormatHandler.currencyFormatter());
            fields[i].setOnMouseClicked((MouseEvent event) -> {
                TextField tf = (TextField) event.getSource();
                tf.selectRange(2, tf.getText().length());
            });
            labels[i].setText(get.getMemberSubscription().getFeeName());
            fields[i].setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(!getparamValue(flag, get) ? 0.00 : get.getAmount()));
            grid.add(labels[i], 0, i + 1);
            grid.add(fields[i], 1, i + 1);
        }
        grid.add(totLabel, 1, mbrSubs.size() + 1);
        return grid;
    }

    private boolean getparamValue(boolean flag, MemberSubscriptions get) {
        return !(get.getRepaymentType().equalsIgnoreCase("Once") && !flag);
    }
}
