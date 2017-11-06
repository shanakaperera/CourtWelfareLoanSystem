/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.TextFormatHandler;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class GuarantorsFxmlController implements Initializable {

    @FXML
    private TreeTableView<Member> gurantor_tbl;
    @FXML
    private TreeTableColumn<Member, String> g_id_col;
    @FXML
    private TreeTableColumn<Member, String> g_name_col;
    @FXML
    private TreeTableColumn<Member, String> g_payoff_col;
    @FXML
    private TreeTableColumn<Member, String> g_workoff_col;
    @FXML
    private TextField gur_name_txt;
    @FXML
    private TextField gur_id_txt;
    @FXML
    private TreeTableColumn<Member, Button> gur_action_col;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Member> ags = getAvailableGuarantors();
        initGTable(ags);

        List<String> collect_names = ags.stream()
                .map(Member::getFullName).collect(Collectors.toList());
        List<String> collect_ids = ags.stream()
                .map(Member::getMemberId).collect(Collectors.toList());
        TextFields.bindAutoCompletion(gur_name_txt, collect_names);
        TextFields.bindAutoCompletion(gur_id_txt, collect_ids);

        gur_name_txt.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        gur_id_txt.setText("");
                    }
                });
        gur_id_txt.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (newValue) {
                        gur_name_txt.setText("");
                    }
                });

    }

    private void initGTable(List<Member> guarantors) {
        g_id_col.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getMemberId()));
        g_name_col.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getFullName()));
        g_payoff_col.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getPaymentOfficer()));
        g_workoff_col.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getBranch().getBranchName()));
        gur_action_col.setCellValueFactory(pr -> {
            Button b = new Button("View Loans");
            b.setOnAction(evt -> {
                TableView<MemberLoan> table = new TableView<>();
                TableColumn<MemberLoan, String> lnId = new TableColumn<>("LoanId");
                TableColumn<MemberLoan, String> lnAmt = new TableColumn<>("LoanAmt");
                TableColumn<MemberLoan, String> loanInterest = new TableColumn<>("Interest");
                TableColumn<MemberLoan, String> grantedDate = new TableColumn<>("DateGranted");
                TableColumn<MemberLoan, String> loanDuration = new TableColumn<>("Duration");
                TableColumn<MemberLoan, String> member = new TableColumn<>("MbrGranted");

                lnId.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(param.getValue().getMemberLoanCode());
                });
                lnAmt.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(TextFormatHandler.CURRENCY_DECIMAL_FORMAT
                            .format(param.getValue().getLoanAmount()));
                });
                loanInterest.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT
                            .format(param.getValue().getLoanInterest() / 100.0) + " " + param.getValue().getInterestPer());
                });
                loanDuration.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(param.getValue().getLoanDuration() + " " + param.getValue().getDurationPer());
                });

                member.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(param.getValue().getMember().getFullName());
                });
                grantedDate.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(new SimpleDateFormat("yyyy-MM-dd")
                            .format(param.getValue().getGrantedDate()));
                });

                ObservableList<MemberLoan> selected_data = getLoanPaymentsOf(pr.getValue().getValue().getMemberId());
                table.getColumns().addAll(lnId, lnAmt, grantedDate, loanInterest, loanDuration, member);
                table.setItems(selected_data);

                VBox vbox = new VBox(table);
                vbox.setPrefSize(600, 300);
                Alert alert_custom = new Alert(Alert.AlertType.NONE);
                alert_custom.setTitle("Guaranted Loans");
                alert_custom.setHeaderText("Guaranted Loans of - " + pr.getValue().getValue().getFullName());
                alert_custom.getDialogPane().setContent(vbox);
                ButtonType buttonTypeCancel = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert_custom.getButtonTypes().add(buttonTypeCancel);
                alert_custom.showAndWait();
            });
            return new SimpleObjectProperty<>(b);

        });

        TreeItem<Member> root = new TreeItem<>(new Member("Root"));
        guarantors.forEach(e -> {
            root.getChildren().add(new MemberTreeItem(e).getmTreeItem());
        });
        gurantor_tbl.setRoot(root);
        gurantor_tbl.setShowRoot(false);
    }

    private List<Member> getAvailableGuarantors() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c1 = session.createCriteria(MemberLoan.class);
        List<MemberLoan> ml = c1.list();
        List<String> guarantors = ml.stream().filter(p -> !p.isIsComplete())
                .map(MemberLoan::getGuarantors).collect(Collectors.toList());

        Criteria c2 = session.createCriteria(Member.class);
        List<Member> list;
        if (getUniqueGuarantors(guarantors).isEmpty()) {
            list = c2.list();
        } else {
            list = c2.add(Restrictions.
                    in("memberId", getUniqueGuarantors(guarantors))).list();
        }
        session.close();
        return list;
    }

    private Set<String> getUniqueGuarantors(List<String> guarantors) {
        Set<String> ug = new HashSet<>();
        for (String string : guarantors) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> yourList = new Gson().fromJson(string, type);
            for (String yl : yourList) {
                ug.add(yl);
            }
        }
        return ug;
    }

    private ObservableList<MemberLoan> getLoanPaymentsOf(String memberId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(MemberLoan.class);
        List<MemberLoan> lpList = c.list();
        List<MemberLoan> collection = new ArrayList<>();
        for (MemberLoan ml : lpList) {
            if (isGuarantorOf(ml, memberId)) {
                collection.add(ml);
            }
        }

        s.close();
        return FXCollections.observableList(collection);
    }

    private boolean isGuarantorOf(MemberLoan ml, String memberId) {
        List<String> mList = new Gson().fromJson(ml.getGuarantors(), new TypeToken<List<String>>() {
        }.getType());
        return mList.contains(memberId);
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(Member.class);
        if (!gur_name_txt.getText().isEmpty()) {
            c.add(Restrictions.disjunction()
                    .add(Restrictions.eq("fullName", gur_name_txt.getText())));
        } else {
            c.add(Restrictions.disjunction()
                    .add(Restrictions.eq("memberId", gur_id_txt.getText())));
        }
        List<Member> list = c.list();
        initGTable(list);
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        gur_name_txt.setText("");
        gur_id_txt.setText("");
        initGTable(getAvailableGuarantors());
    }

    private class MemberTreeItem extends TreeItem<Member> {

        TreeItem<Member> mTreeItem;

        public MemberTreeItem(Member m) {
            this.mTreeItem = new TreeItem<>(m);
        }

        public TreeItem<Member> getmTreeItem() {
            return mTreeItem;
        }

    }

}
