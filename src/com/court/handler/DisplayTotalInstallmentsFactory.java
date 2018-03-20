/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberLoan;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Shanaka P
 */
public class DisplayTotalInstallmentsFactory implements Callback<TableColumn.CellDataFeatures<Member, Button>, ObservableValue<Button>> {

    private final TableView<Member> collection_tbl;
    private double total;
    private final TextField chk_amt_txt;

    public DisplayTotalInstallmentsFactory(TableView<Member> collection_tbl, double total, TextField chk_amt_txt) {
        this.collection_tbl = collection_tbl;
        this.total = total;
        this.chk_amt_txt = chk_amt_txt;
    }

    @Override
    public ObservableValue<Button> call(TableColumn.CellDataFeatures<Member, Button> param) {
        Member ml = param.getValue();
        List<MemberLoan> instOnly = ml.getMemberLoans().stream()
                .sorted(Comparator.comparing(MemberLoan::getChildId).reversed())
                .filter(p -> !p.isIsComplete())
                .filter(FxUtilsHandler.checkIfAlreadyPaid(p -> p.getPaidUntil()))
                .filter(p -> p.isStatus())
                .filter(p -> (p.getLastInstall() < p.getLoanDuration()))
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                .collect(Collectors.toList());

        //===================installments finished, but kotaonly loans================
        List<MemberLoan> kotaOnly = ml.getMemberLoans().stream()
                .sorted(Comparator.comparing(MemberLoan::getChildId).reversed())
                .filter(p -> !p.isIsComplete())
                .filter(FxUtilsHandler.checkIfAlreadyPaid(p -> p.getPaidUntil()))
                .filter(p -> p.isStatus())
                .filter(p -> (p.getLastInstall() >= p.getLoanDuration()))
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberLoanCode()))
                .collect(Collectors.toList());

        //======= Set kota amount as the installment of each kota only loans
        kotaOnly.forEach(p -> p.setLoanInstallment(p.getKotaLeft()));

        //================Combine both lists====================
        instOnly.addAll(kotaOnly);

        double sum = instOnly.stream().mapToDouble(p -> p.getLoanInstallment()).sum();

        param.getValue().setTotalPayment(sum);
        Button button = new Button("View Info");
        button.setOnAction((evt) -> {
            Alert alert_details = new Alert(Alert.AlertType.INFORMATION);
            alert_details.setTitle("Loan Information");
            alert_details.setHeaderText("Member installment information for each loan");
            alert_details.getDialogPane().setContent(createContentGrid(instOnly, sum));
            alert_details.show();
        });
        return new SimpleObjectProperty<>(button);
    }
    private Label total_n;

    private Node createContentGrid(List<MemberLoan> list, double sum) {
        VBox pane = new VBox();
        HBox totBox = new HBox();
        totBox.setAlignment(Pos.CENTER_RIGHT);
        Label total_t = new Label("TOTAL");
        total_n = new Label("Rs00000.00");
        total_n.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
        total_t.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");
        total_n.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");
        totBox.getChildren().addAll(total_t, total_n);
        totBox.setSpacing(10.0);
        TableView<MemberLoan> table = createTable();
        table.setItems(FXCollections.observableList(list));
        pane.getChildren().addAll(table, totBox);
        pane.setPrefSize(700, 300);
        return pane;
    }

    private TableView<MemberLoan> createTable() {

        TableView<MemberLoan> member_ln_tbl = new TableView<>();
        TableColumn<MemberLoan, String> loan_id_col = new TableColumn<>("Loan ID");
        TableColumn<MemberLoan, Set<LoanPayment>> ins_no_col = new TableColumn<>("Installment Num");
        TableColumn<MemberLoan, String> ln_type_col = new TableColumn<>("Loan Type");
        TableColumn<MemberLoan, Double> ln_amt_col = new TableColumn<>("Loan Amount");
        TableColumn<MemberLoan, Double> ln_inst_col = new TableColumn<>("Loan Installment(Rs)");
        TableColumn<MemberLoan, Double> ln_int_col = new TableColumn<>("Loan Interest");

        member_ln_tbl.getColumns().addAll(loan_id_col, ins_no_col, ln_type_col, ln_amt_col, ln_inst_col, ln_int_col);
        member_ln_tbl.setEditable(true);

        loan_id_col.setCellValueFactory(new PropertyValueFactory<>("memberLoanCode"));
        ln_type_col.setCellValueFactory(new PropertyValueFactory<>("interestMethod"));
        ln_amt_col.setCellValueFactory(new PropertyValueFactory<>("loanAmount"));
        ln_inst_col.setCellValueFactory(new PropertyValueFactory<>("loanInstallment"));
        ln_int_col.setCellValueFactory(new PropertyValueFactory<>("loanInterest"));
        ins_no_col.setCellValueFactory(new PropertyValueFactory<>("loanPayments"));
        ln_amt_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(item));
                    }
                }
            };
        });

        ln_inst_col.setCellFactory(TextFieldTableCell.<MemberLoan, Double>forTableColumn(new CustomDoubleStringConverter()));

        ln_inst_col.setOnEditCommit((TableColumn.CellEditEvent<MemberLoan, Double> event) -> {

            if (event.getNewValue() != null) {

                double diff = event.getNewValue() - event.getOldValue();

                event.getTableView().getItems().get(event.getTablePosition().getRow())
                        .setLoanInstallment(event.getNewValue());

                collection_tbl.refresh();
                total_n.setText(getTableColumnTotal(event.getTableView(), 4));
                total = total + diff;
                chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
            } else {
                ln_inst_col.getTableView().refresh();
            }

//            double diff = event.getNewValue() - event.getOldValue();
//            event.getTableView().getItems().get(event.getTablePosition().getRow())
//                    .setLoanInstallment(event.getNewValue() != null ? event.getNewValue() : 0.00);
//            collection_tbl.refresh();
//            total_n.setText(getTableColumnTotal(event.getTableView(), 4));
//            total = total + diff;
//            chk_amt_txt.setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(total));
        });

        ln_int_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(item / 100));
                    }
                }
            };
        });

        ins_no_col.setCellFactory(column -> {
            return new TableCell<MemberLoan, Set<LoanPayment>>() {
                @Override
                protected void updateItem(Set<LoanPayment> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        if (!item.isEmpty()) {
                            setText(String.valueOf(item.stream().filter(p -> p.isIsLast()).findFirst().get().getInstallmentNo() + 1));
                        } else {
                            setText(String.valueOf(1));
                        }
                    }
                }

            };
        });

        return member_ln_tbl;
    }

    private String getTableColumnTotal(TableView<MemberLoan> table, int column) {
        double tot = 0.0;
        for (int i = 0; i < table.getItems().size(); i++) {
            Double value = (Double) table.getColumns().get(column)
                    .getCellObservableValue(i).getValue();
            tot += value;
        }
        return TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(tot);
    }

}
