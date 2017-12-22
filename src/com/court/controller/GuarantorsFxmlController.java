/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.Branch;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

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
                .filter(FxUtilsHandler.distinctByKey(p -> p.getFullName()))
                .map(Member::getFullName).collect(Collectors.toList());
        List<String> collect_ids = ags.stream()
                .filter(FxUtilsHandler.distinctByKey(p -> p.getMemberId()))
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
        g_payoff_col.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getValue().getPayOffice().getBranchName()));
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
                TableColumn<MemberLoan, String> loanStatus = new TableColumn<>("LoanStatus");

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
                loanStatus.setCellValueFactory((TableColumn.CellDataFeatures<MemberLoan, String> param) -> {
                    return new SimpleObjectProperty<>(param.getValue().isIsComplete() ? "Completed" : "Ongoing");
                });

                ObservableList<MemberLoan> selected_data = getLoanPaymentsOf(pr.getValue().getValue().getMemberId());
                table.getColumns().addAll(lnId, lnAmt, grantedDate, loanInterest, loanDuration, member, loanStatus);
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
        SQLQuery query = session.createSQLQuery("SELECT\n"
                + "    m.member_id AS memberId,\n"
                + "    m.full_name AS fullName,\n"
                + "    wb.branch_name AS workingOffice,\n"
                + "    pb.branch_name AS payingOffice,\n"
                + "    ml.member_loan_code AS loanCode,\n"
                + "    ml.granted_date AS grantedDate,\n"
                + "    ml.loan_amount AS loanAmount,\n"
                + "    ml.loan_interest AS loanInterest,\n"
                + "    ml.interest_per AS interestPer,\n"
                + "    ml.interest_method AS interMethod,\n"
                + "    ml.loan_duration AS loanDuration,\n"
                + "    ml.duration_per AS lDurationPer,\n"
                + "    ml.is_complete AS loanComplete\n"
                + "FROM\n"
                + "    court_loan.branch wb\n"
                + "INNER JOIN\n"
                + "    court_loan.member m\n"
                + "ON\n"
                + "    (\n"
                + "        wb.id = m.branch_id)\n"
                + "INNER JOIN\n"
                + "    court_loan.branch pb\n"
                + "ON\n"
                + "    (\n"
                + "        m.pay_office_id = pb.id)\n"
                + "LEFT OUTER JOIN\n"
                + "    court_loan.member_loan ml\n"
                + "ON\n"
                + "    (\n"
                + "        m.id = ml.member_id) ;")
                .addScalar("memberId", new StringType())
                .addScalar("fullName", new StringType())
                .addScalar("workingOffice", new StringType())
                .addScalar("payingOffice", new StringType())
                .addScalar("loanCode", new StringType())
                .addScalar("grantedDate", new DateType())
                .addScalar("loanAmount", new DoubleType())
                .addScalar("loanInterest", new DoubleType())
                .addScalar("interestPer", new StringType())
                .addScalar("interMethod", new StringType())
                .addScalar("loanDuration", new IntegerType())
                .addScalar("lDurationPer", new StringType())
                .addScalar("loanComplete", new BooleanType());

        List<Object[]> rows = query.list();
        List<Member> list = new ArrayList<>();
        for (Object[] row : rows) {
            Member m = new Member();
            m.setMemberId(row[0].toString());
            m.setFullName(row[1].toString());
            m.setBranch(new Branch(row[2].toString()));
            m.setPayOffice(new Branch(row[3].toString()));
            list.add(m);
        }

        session.close();
        return list;
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
