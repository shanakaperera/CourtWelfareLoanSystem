/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.CompanyDefaults;
import com.court.handler.PropHandler;
import com.court.model.Branch;
import com.court.model.Company;
import com.court.model.DocSequnce;
import com.court.model.Document;
import com.court.model.Loan;
import com.court.model.Member;
import com.court.model.MemberLoan;
import com.google.gson.Gson;
import com.mysql.jdbc.DatabaseMetaData;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class GeneralSettingsFxmlController implements Initializable {

    @FXML
    private TextField dis_name_txt;
    @FXML
    private TextField title_txt;
    @FXML
    private TextField branch_ftxt;
    @FXML
    private TextField branch_stxt;
    @FXML
    private TextField member_stxt;
    @FXML
    private TextField member_loan_stxt;
    @FXML
    private TextField loan_stxt;
    @FXML
    private TextField document_stxt;
    @FXML
    private TextField member_ftxt;
    @FXML
    private TextField member_loan_ftxt;
    @FXML
    private TextField loan_ftxt;
    @FXML
    private TextField document_ftxt;

    private ValidationSupport v2, v1;
    @FXML
    private GridPane main_grid;
    @FXML
    private HBox hb_1;
    @FXML
    private HBox hb_2;
    @FXML
    private HBox hb_3;
    @FXML
    private HBox hb_4;
    @FXML
    private HBox hb_5;
    @FXML
    private HBox hb_6;
    @FXML
    private HBox hb_7;
    @FXML
    private HBox hb_8;
    @FXML
    private HBox hb_9;
    @FXML
    private HBox hb_10;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        v1 = new ValidationSupport();
        v2 = new ValidationSupport();
        loadDisplaySettings();
        loadFormatSettings();
        bindValidationOnPaneControlFocus(main_grid, hb_1, hb_2, hb_3, hb_4, hb_5,
                hb_6, hb_7, hb_8, hb_9, hb_10);
    }

    @FXML
    private void onFmtSetSaveBtnAction(ActionEvent event) throws IOException {
        Map<String, Boolean> map = getAllNonUpdatableFomatsAsMap();
        if (!map.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                String key = entry.getKey();
                keys.add(key);
            }
            //if all being used then give this alert and return cannot upate any formats
            List<DocSequnce> dses = getAllUpdatableFormatsAsList(keys);
            if (dses.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Update cannot be done !");
                alert.setContentText(PropHandler.getStringProperty("alreay_used"));
                alert.show();
                return;
            }
            //if there is something to update then filter them and give below alert
            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            alert_inf.setHeaderText("Information Message !");
            alert_inf.setContentText("You have already saved " + String.join(",", keys) + " with given formats."
                    + " So instead of the relevant formats others will be updated !");
            Optional<ButtonType> result = alert_inf.showAndWait();

            if (result.get() == ButtonType.OK) {
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                for (DocSequnce dse : dses) {
                    updateReleventDocSeq(dse, session);
                }
                session.getTransaction().commit();
                session.close();
            }
        } else {
            List<DocSequnce> dses = getAllUpdatableFormatsAsList(Arrays.asList(""));
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            for (DocSequnce dse : dses) {
                updateReleventDocSeq(dse, session);
            }
            session.getTransaction().commit();
            session.close();
        }
        Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
        alert_success.setTitle("Success");
        alert_success.setHeaderText("Successfully Saved !");
        alert_success.setContentText("You have successfully saved the updates.");
        Optional<ButtonType> result = alert_success.showAndWait();
        if (result.get() == ButtonType.OK) {
            loadFormatSettings();
        }
    }

    @FXML
    private void onFmtSetRestoreBtnAction(ActionEvent event) throws IOException {
        Alert alert_warning = new Alert(Alert.AlertType.CONFIRMATION);
        alert_warning.setTitle("Warning");
        alert_warning.setHeaderText("Are you sure you want to proceed ?");
        alert_warning.setContentText(PropHandler.getStringProperty("restore_warning"));
        Optional<ButtonType> result = alert_warning.showAndWait();
        if (result.get() == ButtonType.OK) {
            List<DocSequnce> docs = getSequenceFormats();
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            for (DocSequnce doc : docs) {
                doc.setFormat(doc.getDefaultFormat());
                doc.setStartFrom(1);
                session.update(doc);
            }
            session.getTransaction().commit();
            session.close();
            loadFormatSettings();
        }
    }

    @FXML
    private void onDsplySetSaveBtnAction(ActionEvent event) throws IOException {
        if (v1.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Company c = (Company) session.load(Company.class, 1);
            c.setCompanyName(dis_name_txt.getText());
            c.setTitleName(title_txt.getText());
            session.update(c);
            session.getTransaction().commit();
            session.close();

            Alert alert_sucess = new Alert(Alert.AlertType.INFORMATION);
            alert_sucess.setTitle("Success");
            alert_sucess.setHeaderText("Successfully Saved !");
            alert_sucess.setContentText("You have successfully Saved the display settings.");
            Optional<ButtonType> result = alert_sucess.showAndWait();
            if (result.get() == ButtonType.OK) {
                loadDisplaySettings();
            }

        }
    }

    @FXML
    private void onDsplySetRestoreBtnAction(ActionEvent event) throws IOException {
        Alert alert_warning = new Alert(Alert.AlertType.CONFIRMATION);
        alert_warning.setTitle("Warning");
        alert_warning.setHeaderText("Are you sure you want to proceed ?");
        alert_warning.setContentText(PropHandler.getStringProperty("restore_warning"));
        Optional<ButtonType> result = alert_warning.showAndWait();
        if (result.get() == ButtonType.OK) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Company c = getCompany();
            CompanyDefaults cd = new Gson().fromJson(c.getDefaults(), CompanyDefaults.class);
            c.setCompanyName(cd.getCompanyName());
            c.setTitleName(cd.getTitleName());
            session.update(c);
            session.getTransaction().commit();
            session.close();
            loadDisplaySettings();
        }
    }

    private void loadDisplaySettings() {
        Company c = getCompany();
        dis_name_txt.setText(c.getCompanyName());
        title_txt.setText(c.getTitleName());
        DashBoardFxmlController.controller.
                getDashboard_header().setText(c.getCompanyName().toUpperCase());
    }

    private void loadFormatSettings() {
        List<DocSequnce> docs = getSequenceFormats();
        branch_ftxt.setText(docs.get(0).getFormat());
        branch_stxt.setText(String.valueOf(docs.get(0).getStartFrom()));
        member_ftxt.setText(docs.get(1).getFormat());
        member_stxt.setText(String.valueOf(docs.get(1).getStartFrom()));
        loan_ftxt.setText(docs.get(2).getFormat());
        loan_stxt.setText(String.valueOf(docs.get(2).getStartFrom()));
        member_loan_ftxt.setText(docs.get(3).getFormat());
        member_loan_stxt.setText(String.valueOf(docs.get(3).getStartFrom()));
        document_ftxt.setText(docs.get(4).getFormat());
        document_stxt.setText(String.valueOf(docs.get(4).getStartFrom()));

    }

    private void bindValidationOnPaneControlFocus(Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            c.focusedProperty().addListener(e -> {
                registerDisplayInputValidation();
                registerFormatInputValidation();
            });

        }
    }

    private List<DocSequnce> getSequenceFormats() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(DocSequnce.class);
        List<DocSequnce> docs = c.list();
        session.close();
        return docs;
    }

    private Company getCompany() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Company c = (Company) session.load(Company.class, 1);
        session.close();
        return c;
    }

    private void registerDisplayInputValidation() {
        if (!v1.getRegisteredControls().isEmpty()) {
            return;
        }
        v1.registerValidator(title_txt,
                Validator.createEmptyValidator("This field is not optional."));
        v1.registerValidator(dis_name_txt,
                Validator.createEmptyValidator("This field is not optional."));
    }

    private void registerFormatInputValidation() {
        if (!v2.getRegisteredControls().isEmpty()) {
            return;
        }
        List<Control> notoptionalFields = new ArrayList<>(Arrays.asList(branch_ftxt, member_ftxt,
                member_loan_ftxt, loan_ftxt, document_ftxt));
        List<Control> numberOnlyFields = new ArrayList<>(Arrays.asList(branch_stxt, member_stxt,
                member_loan_stxt, loan_stxt, document_stxt));
        for (Control field : numberOnlyFields) {
            v2.registerValidator(field,
                    Validator.combine(
                            Validator.createEmptyValidator("This field is not optional."),
                            Validator.createRegexValidator("Only numbers allowed here !",
                                    "\\d+", Severity.ERROR)));
        }
        for (Control field : notoptionalFields) {
            v2.registerValidator(field,
                    Validator.createEmptyValidator("This field is not optional."));
        }

    }

    private boolean ifTableEmpty(Class type) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(type);
        boolean flag = c.list().isEmpty();
        session.close();
        return flag;
    }

    private Map<String, Boolean> getAllNonUpdatableFomatsAsMap() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("branch", ifTableEmpty(Branch.class));
        map.put("member", ifTableEmpty(Member.class));
        map.put("loan", ifTableEmpty(Loan.class));
        map.put("member_loan", ifTableEmpty(MemberLoan.class));
        map.put("document", ifTableEmpty(Document.class));
//remove all elements where value is true
        map.values().removeAll(Collections.singleton(true));
        return map;
    }

    private List<DocSequnce> getAllUpdatableFormatsAsList(List<String> keys) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(DocSequnce.class);
        c.add(Restrictions.not(Restrictions.in("tableName", keys)));
        List<DocSequnce> list = c.list();
        session.close();
        return list;
    }

    private void updateReleventDocSeq(DocSequnce dse, Session session) {
        switch (dse.getTableName()) {
            case "branch":
                dse.setFormat(branch_ftxt.getText());
                dse.setId(Integer.parseInt(branch_stxt.getText()));
                session.update(dse);
                break;
            case "member":
                dse.setFormat(member_ftxt.getText());
                dse.setId(Integer.parseInt(member_stxt.getText()));
                session.update(dse);
                break;
            case "loan":
                dse.setFormat(loan_ftxt.getText());
                dse.setId(Integer.parseInt(loan_stxt.getText()));
                session.update(dse);
                break;
            case "member_loan":
                dse.setFormat(member_loan_ftxt.getText());
                dse.setId(Integer.parseInt(member_loan_stxt.getText()));
                session.update(dse);
                break;
            case "document":
                dse.setFormat(document_ftxt.getText());
                dse.setId(Integer.parseInt(document_stxt.getText()));
                session.update(dse);
                break;
        }
    }

    @FXML
    private void onDbBacupAction(ActionEvent event) throws MalformedURLException, IOException, InterruptedException, SQLException {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        SessionFactoryImpl sfi = (SessionFactoryImpl) sessionFactory;
        Properties p = sfi.getProperties();
        String db_user = p.getProperty("hibernate.connection.username");
        String db_pass = p.getProperty("hibernate.connection.password");
        String db_url = p.getProperty("hibernate.connection.url").replace("jdbc:mysql:", "http:");

        String server_v = getMysqlServerV(sessionFactory);

        URL aURL = new URL(db_url);

        String db_host = aURL.getHost();
        String db_port = String.valueOf(aURL.getPort());
        String db_name = "court_loan";
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select backup directory");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File file = chooser.showDialog(null);

        if (file != null) {
            String path = file.getAbsolutePath() + "\\courtbackup_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".sql";

            //System.out.println("PATH - " + path);
            Runtime r = Runtime.getRuntime();
            String command = System.getenv("ProgramFiles") + "\\MYSQL\\MySQL Server " + server_v + "\\bin\\mysqldump.exe"
                    + " --user=" + db_user
                    + " --password=" + db_pass
                    + " --host=" + db_host
                    + " --protocol=tcp"
                    + " --port=" + db_port
                    + " --default-character-set=utf8 --routines --events"
                    + " --result-file=\"" + path + "\""
                    + " --databases " + db_name;
            //System.out.println("COMMAND - " + command);
            Process pr = r.exec(command);
            int pComplete = pr.waitFor();
            if (pComplete == 0) {
                Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
                alert_success.setTitle("Success");
                alert_success.setHeaderText("Backup created successfully !");
                alert_success.show();

            } else {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Backup create failure !");
                alert_error.show();
            }

        }
        // sessionFactory.close();

    }

    @FXML
    private void onDbRestoreAction(ActionEvent event) throws IOException, InterruptedException, SQLException {

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        SessionFactoryImpl sfi = (SessionFactoryImpl) sessionFactory;
        Properties p = sfi.getProperties();
        String db_user = p.getProperty("hibernate.connection.username");
        String db_pass = p.getProperty("hibernate.connection.password");

        String server_v = getMysqlServerV(sessionFactory);

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.txt");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            String path = file.getAbsolutePath();
            Runtime r = Runtime.getRuntime();
            String command = System.getenv("ProgramFiles") + "\\MYSQL\\MySQL Server " + server_v + "\\bin\\mysql.exe"
                    + " --user=" + db_user
                    + " --password=" + db_pass
                    + " -e source " + path;
            Process pr = r.exec(command);
            int pComplete = pr.waitFor();
            if (pComplete == 0) {
                Alert alert_success = new Alert(Alert.AlertType.INFORMATION);
                alert_success.setTitle("Success");
                alert_success.setHeaderText("Database restored successfully !");
                alert_success.show();
            } else {
                Alert alert_error = new Alert(Alert.AlertType.ERROR);
                alert_error.setTitle("Error");
                alert_error.setHeaderText("Database restore failure !");
                alert_error.show();
            }

        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("File selection failed or invalid file !");
            alert_error.show();
        }

        // sessionFactory.close();
    }

    private String getMysqlServerV(SessionFactory sessionFactory) throws SQLException {
        Connection con = sessionFactory.
                getSessionFactoryOptions().getServiceRegistry().
                getService(ConnectionProvider.class).getConnection();

        String server_v = con.getMetaData().getDatabaseProductVersion()
                .substring(0, con.getMetaData().getDatabaseProductVersion().lastIndexOf("."));
        con.close();
        return server_v;
    }
}
