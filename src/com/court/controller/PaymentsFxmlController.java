/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.FxUtilsHandler;
import com.court.handler.TextFormatHandler;
import com.court.model.LoanPayment;
import com.court.model.MemberSubscriptions;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class PaymentsFxmlController implements Initializable {

    @FXML
    private TextField chk_no_txt;
    @FXML
    private DatePicker from_date_chosr;
    @FXML
    private DatePicker to_date_chosr;
    @FXML
    private TableView<LoanPayment> payment_tbl;
    @FXML
    private GridPane grid_pane;
    @FXML
    private HBox date_box;
    @FXML
    private TableColumn<LoanPayment, String> chk_no_col;
    @FXML
    private TableColumn<LoanPayment, String> chk_date_col;
    @FXML
    private TableColumn<LoanPayment, String> chk_amt_col;
    @FXML
    private TableColumn<LoanPayment, Button> action_col;
    @FXML
    private TableColumn<LoanPayment, Button> subs_payments_col;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FxUtilsHandler.setDatePickerTimeFormat(from_date_chosr, to_date_chosr);
    }

    @FXML
    private void onSearchBtnAction(ActionEvent event) {
        if (chk_no_txt.getText() == null
                && (from_date_chosr.getValue() == null || to_date_chosr.getValue() == null)) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Something wrong with your inputs.");
            alert_error.setContentText("Please enter cheque number or date duration to filter payments.");
            alert_error.show();
            return;
        }
        if (chk_no_txt.getText() == null) {
            performPaymentSearchByDuration(from_date_chosr.getValue(), to_date_chosr.getValue());
        } else {
            performPaymentSearchByChaque(chk_no_txt.getText());
        }

    }

    private void performPaymentSearchByChaque(String chkNo) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(LoanPayment.class);
        c.add(Restrictions.eq("chequeNo", chkNo));
        c.setMaxResults(1);
        List<LoanPayment> lPayList = c.list();
        initPaymentTable(FXCollections.observableArrayList(lPayList));
        session.close();
    }

    private void initPaymentTable(ObservableList<LoanPayment> observableArrayList) {

        chk_no_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment payment = param.getValue();
            String chequeNo = payment.getChequeNo();
            if (chequeNo != null) {
                return new SimpleStringProperty(chequeNo);
            } else {
                return new SimpleStringProperty("No cheque no available.");
            }
        });

        chk_date_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment payment = param.getValue();
            Date chequeDate = payment.getLoanPayCheque().getChequeDate();
            if (chequeDate != null) {
                return new SimpleStringProperty(new SimpleDateFormat("yyyy-MM-dd")
                        .format(chequeDate));
            } else {
                return new SimpleStringProperty("No date available.");
            }
        });
        chk_amt_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, String> param) -> {
            LoanPayment payment = param.getValue();
            return new SimpleObjectProperty<>(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(payment.getLoanPayCheque().getChequeAmount()));
        });

        action_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, Button> param) -> {
            LoanPayment payment = param.getValue();
            Button b = new Button("View Info");
            b.setOnAction(evt -> {
                ObservableList<LoanPayment> loanPayments = FXCollections.observableArrayList(payment.getLoanPayCheque().getLoanPayments());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/LoanPayChkFxml.fxml"));

                try {
                    VBox pane = (VBox) loader.load();
                    LoanPayChkFxmlController ctr = (LoanPayChkFxmlController) loader.getController();
                    ctr.createDetailTable(loanPayments);
                    Alert alert_details = new Alert(Alert.AlertType.INFORMATION);
                    alert_details.setTitle("Cheque Payments");
                    alert_details.setHeaderText("All the meber loan payments included with the cheque.");
                    alert_details.getDialogPane().setContent(pane);
                    alert_details.show();

                } catch (IOException e) {
                    Logger.getLogger(PaymentsFxmlController.class.getName()).log(Level.SEVERE, null, e);
                }
            });
            return new SimpleObjectProperty<>(b);
        });

        subs_payments_col.setCellValueFactory((TableColumn.CellDataFeatures<LoanPayment, Button> param) -> {
            Button b = new Button("View Info");
            b.setOnAction(e -> {

            });
            return new SimpleObjectProperty<>(b);
        });

        payment_tbl.setItems(observableArrayList);
    }

    @FXML
    private void onSearchTxtMouseClickAction(MouseEvent event) {
        chk_no_txt.setEditable(true);
        from_date_chosr.setEditable(false);
        to_date_chosr.setEditable(false);
        from_date_chosr.setValue(null);
        to_date_chosr.setValue(null);
    }

    @FXML
    private void onFromDateMouseClickAction(MouseEvent event) {
        chk_no_txt.setEditable(false);
        from_date_chosr.setEditable(true);
        to_date_chosr.setEditable(true);
        chk_no_txt.setText(null);

    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(grid_pane, date_box);
        payment_tbl.getItems().clear();
    }

    @FXML
    private void onToDateMouseClickAction(MouseEvent event) {
        chk_no_txt.setEditable(false);
        from_date_chosr.setEditable(true);
        to_date_chosr.setEditable(true);
        chk_no_txt.setText(null);
    }

    private void performPaymentSearchByDuration(LocalDate from, LocalDate to) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(LoanPayment.class);
        c.add(Restrictions.between("paymentDate",
                FxUtilsHandler.getDateFrom(from), FxUtilsHandler.getDateFrom(to)));

        ProjectionList pList = Projections.projectionList();
        ClassMetadata lpMeta = session.getSessionFactory().getClassMetadata(LoanPayment.class);
        pList.add(Projections.property(lpMeta.getIdentifierPropertyName()));

        for (String prop : lpMeta.getPropertyNames()) {
            pList.add(Projections.property(prop), prop);
        }

        c.setProjection(pList.add(Projections.groupProperty("chequeNo")));
        c.addOrder(Order.asc("paymentDate"));
        c.setResultTransformer(Transformers.aliasToBean(LoanPayment.class));
        List<LoanPayment> lPayList = (List<LoanPayment>) c.list();
        initPaymentTable(FXCollections.observableArrayList(lPayList));
        session.close();
    }

    private Node createContentGrid(List<MemberSubscriptions> mbrSubs, double sum, LoanPayment payment) {

//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/court/view/DisplaySubscriptionFxml.fxml"));
//        GridPane pane;
//        try {
//            pane = (GridPane) loader.load();
//            DisplaySubscriptionFxmlController controller = (DisplaySubscriptionFxmlController) loader.getController();
//
//            callByName(controller, "setValueSubs_tot", TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(sum));
//
//            for (int i = 0; i < mbrSubs.size(); i++) {
//                int j = i + 1;
//                MemberSubscriptions get = mbrSubs.get(i);
//                callByName(controller, "setValueSub_" + j, get.getMemberSubscription().getFeeName());
//                callByName(controller, "setValueAmt_" + j, TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(get.getAmount()));
//
//                switch (get.getMemberSubscription().getFeeName()) {
//                    case "Membership Fee":
//                        callByName(controller, "setValueTkn_" + j, getparamValue(payment.getMembershipFee() == 0));
//                        break;
//                    case "Savings Fee":
//                        callByName(controller, "setValueTkn_" + j, getparamValue(payment.getSavingsFee() == 0));
//                        break;
//                    case "HOI Fee":
//                        callByName(controller, "setValueTkn_" + j, getparamValue(payment.getHoiFee() == 0));
//                        break;
//                    case "ACI Fee":
//                        callByName(controller, "setValueTkn_" + j, getparamValue(payment.getAciFee() == 0));
//                        break;
//                    case "Optional":
//                        callByName(controller, "setValueTkn_" + j, getparamValue(payment.getOptionalFee() == 0));
//                        break;
//                    case "Admission Fee":
//                        callByName(controller, "setValueTkn_" + j, getparamValue(payment.getAdmissionFee() == 0));
//                        break;
//                }
//            }
//
//        } catch (IOException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            pane = null;
//            Logger.getLogger(DisplaySubscriptionFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }

    private void callByName(DisplaySubscriptionFxmlController controller, String string, String arg)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        controller.getClass().getDeclaredMethod(string, String.class).invoke(controller, arg);
    }

    private String getparamValue(boolean flag) {
        if (flag) {
            return "No";
        } else {
            return "Yes";
        }
    }
}
