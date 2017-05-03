/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.DocSeqHandler;
import com.court.handler.FxUtilsHandler;
import com.court.handler.LoggedSessionHandler;
import com.court.handler.PropHandler;
import com.court.model.Branch;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class BranchFxmlController implements Initializable {

    private static final String TABLE_NAME = "branch";
    private ValidationSupport validationSupport;
    @FXML
    private ComboBox<String> branch_type_combo;
    @FXML
    private TextField br_id_txt;
    @FXML
    private TextField branch_name_txt;
    @FXML
    private TextField branch_adrs_txt;
    @FXML
    private TextField branch_tel_txt;
    @FXML
    private TextArea branch_des_txt;
    @FXML
    private TableView<Branch> branch_table;
    @FXML
    private TableColumn<Branch, String> branch_id_col;
    @FXML
    private TableColumn<Branch, String> branch_name_col;
    @FXML
    private TableColumn<Branch, String> address_col;
    @FXML
    private TableColumn<Branch, String> contact_no_col;
    @FXML
    private TableColumn<Branch, Boolean> status_col;
    @FXML
    private TextField branch_search_id;
    @FXML
    private TextField branch_search_name;
    @FXML
    private GridPane branch_grid_pane;
    @FXML
    private Button branch_actv_deactv_btn;
    @FXML
    private GridPane branch_search_grid_pane;
    @FXML
    private Button sav_brnch_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport = new ValidationSupport();
        fillBranchCodeTxt(br_id_txt);

        ObservableList<Branch> allBranches = getAllBranches();
        initBranchTable(allBranches);
        List<String> branchCodes = allBranches.stream()
                .map(Branch::getBranchCode).collect(Collectors.toList());
        List<String> branchNames = allBranches.stream()
                .map(Branch::getBranchName).collect(Collectors.toList());
        TextFields.bindAutoCompletion(branch_search_id, branchCodes);
        TextFields.bindAutoCompletion(branch_search_name, branchNames);

        disableButtonWithLoggingPrv(DashBoardFxmlController.controller.loggedSession());
        bindValidationOnPaneControlFocus(branch_grid_pane);
    }

    @FXML
    private void onBranchSaveBtnAction(ActionEvent event) throws IOException {
        if (isValidationEmpty()) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Branch branch;
            int id = getIdByBranchCode(session, br_id_txt.getText().trim());
            if (id != 0) {
                branch = (Branch) session.load(Branch.class, id);
            } else {
                branch = new Branch();
            }

            branch.setBranchCode(br_id_txt.getText().trim());
            branch.setBranchName(branch_name_txt.getText());
            branch.setAddress(branch_adrs_txt.getText());
            branch.setBranchType(branch_type_combo.getSelectionModel().getSelectedItem());
            branch.setContactNo(branch_tel_txt.getText());
            branch.setDescription(branch_des_txt.getText().isEmpty() ? "No Description" : branch_des_txt.getText());
            branch.setStatus(true);
            session.saveOrUpdate(branch);
            session.getTransaction().commit();
            session.close();

            Alert alert_info = new Alert(Alert.AlertType.INFORMATION);
            alert_info.setTitle("Information");
            alert_info.setHeaderText("Successfully Saved !");
            alert_info.setContentText("You have successfully saved the \"" + branch_name_txt.getText() + "\" \n "
                    + "branch.");
            Optional<ButtonType> result = alert_info.showAndWait();
            if (result.get() == ButtonType.OK) {
                FxUtilsHandler.clearFields(branch_grid_pane);
                fillBranchCodeTxt(br_id_txt);

                ObservableList<Branch> allBranches = getAllBranches();
                initBranchTable(allBranches);
                List<String> branchCodes = allBranches.stream()
                        .map(Branch::getBranchCode).collect(Collectors.toList());
                List<String> branchNames = allBranches.stream()
                        .map(Branch::getBranchName).collect(Collectors.toList());
                TextFields.bindAutoCompletion(branch_search_id, branchCodes);
                TextFields.bindAutoCompletion(branch_search_name, branchNames);
            }

        } else {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Missing Fields !");
            alert_error.setContentText("You have some missing fields left. Move the cursor to the red \"X\""
                    + " sign and find the error.");
            alert_error.show();
        }

    }

    @FXML
    private void onBranchNewBtnAction(ActionEvent event) {
        FxUtilsHandler.clearFields(branch_grid_pane);
        fillBranchCodeTxt(br_id_txt);
        FxUtilsHandler.activeDeactiveChildrenControls(true, branch_grid_pane);
        FxUtilsHandler.activeBtnAppearanceChange(branch_actv_deactv_btn, true, false);
    }

    @FXML
    private void onBranchDeactiveBtnAction(ActionEvent event) throws IOException {
        if (isValidationEmpty()) {
            Alert alert_error = new Alert(Alert.AlertType.ERROR);
            alert_error.setTitle("Error");
            alert_error.setHeaderText("Empty Fields !");
            alert_error.setContentText(PropHandler.getStringProperty("empty_fields"));
            alert_error.show();
            return;
        }
        if (validationSupport.validationResultProperty().get().getErrors().isEmpty()) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Branch prfm_action = (Branch) session.load(Branch.class,
                    getIdByBranchCode(session, br_id_txt.getText()));
            boolean set_status = !prfm_action.isStatus();
            prfm_action.setStatus(set_status);
            session.update(prfm_action);
            session.getTransaction().commit();
            session.close();

            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            String what_happened = set_status ? "Activated" : "Deactivated";
            alert_inf.setHeaderText("Successfully " + what_happened + "!");
            alert_inf.setContentText("You have successfully " + what_happened + " the "
                    + branch_name_txt.getText() + " branch !");
            Optional<ButtonType> result = alert_inf.showAndWait();
            if (result.get() == ButtonType.OK) {
                //deactivation proccess----------
                ObservableList<Branch> allBranches = getAllBranches();
                initBranchTable(allBranches);
                FxUtilsHandler.activeDeactiveChildrenControls(set_status, branch_grid_pane);
                FxUtilsHandler.activeBtnAppearanceChange(branch_actv_deactv_btn, set_status, false);
            }
        } else {
            Alert alert_inf = new Alert(Alert.AlertType.INFORMATION);
            alert_inf.setTitle("Information");
            alert_inf.setHeaderText("Nothing to deactive !");
            alert_inf.setContentText("You have not selected a valid branch to deactivate."
                    + " Select a branch and try again.");
            alert_inf.show();
        }
    }

    @FXML
    private void onBranchSearchBtnAction(ActionEvent event) {

        FilteredList<Branch> filteredBranches = new FilteredList<>(getAllBranches(), p -> true);
        String newValue_id = branch_search_id.getText();
        String newValue_name = branch_search_name.getText();
        searchThroughTableByCode(newValue_id, filteredBranches);
        searchThroughTableByName(newValue_name, filteredBranches);
    }

    @FXML
    private void onClearBtnAction(ActionEvent event) {
        initBranchTable(getAllBranches());
        FxUtilsHandler.clearFields(branch_grid_pane, branch_search_grid_pane);
        fillBranchCodeTxt(br_id_txt);
        FxUtilsHandler.activeDeactiveChildrenControls(true, branch_grid_pane);
        FxUtilsHandler.activeBtnAppearanceChange(branch_actv_deactv_btn, true, false);
    }

    private void fillBranchCodeTxt(TextField branchCodeField) {
        DocSeqHandler seqHandler = new DocSeqHandler();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Branch.class);
        c.addOrder(Order.desc("id"));
        c.setMaxResults(1);
        Branch br = (Branch) c.uniqueResult();
        session.close();
        if (br != null) {
            seqHandler.reqTable(TABLE_NAME, Integer.parseInt(br.getBranchCode().replaceAll("\\D+", "")) + 1);
            branchCodeField.setText(seqHandler.getSeq_code());
        } else {
            seqHandler.reqTable(TABLE_NAME, 0);
            branchCodeField.setText(seqHandler.getSeq_code());
        }

    }

    private void initBranchTable(ObservableList<Branch> branches) {
        branch_id_col.setCellValueFactory(new PropertyValueFactory<>("branchCode"));
        branch_name_col.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        address_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        contact_no_col.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        status_col.setCellFactory((column) -> {
            return new TableCell<Branch, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Boolean> currentRow = getTableRow();
                    if (!isEmpty()) {
                        setText(item ? "Active" : "Deactive");
                        currentRow.setStyle(item ? "" : "-fx-background-color:#d9534f");
                    }
                }

            };
        });
        status_col.setCellValueFactory(new PropertyValueFactory<>("status"));

        branch_table.setItems(branches);

        branch_table.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (branch_table.getSelectionModel().getSelectedItem() != null) {
                        Branch selectedBranch = branch_table.getSelectionModel().getSelectedItem();
                        getBranchByCode(selectedBranch.getBranchCode());
                    }
                });

    }

    private ObservableList<Branch> getAllBranches() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Branch.class);
        List<Branch> bList = c.list();
        ObservableList<Branch> branches = FXCollections.observableArrayList(bList);
        session.close();
        return branches;
    }

    private void getBranchByCode(String bCode) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Branch.class);
        c.add(Restrictions.eq("branchCode", bCode));
        Branch filteredBranch = (Branch) c.uniqueResult();
        session.close();
        br_id_txt.setText(filteredBranch.getBranchCode());
        branch_name_txt.setText(filteredBranch.getBranchName());
        branch_adrs_txt.setText(filteredBranch.getAddress());
        branch_tel_txt.setText(filteredBranch.getContactNo());
        branch_des_txt.setText(filteredBranch.getDescription());
        branch_type_combo.getSelectionModel().select(filteredBranch.getBranchType());
        FxUtilsHandler.activeDeactiveChildrenControls(filteredBranch.isStatus(), branch_grid_pane);
        FxUtilsHandler.activeBtnAppearanceChange(branch_actv_deactv_btn, filteredBranch.isStatus(), false);
    }

    private void registerInputValidation() {
        if (!validationSupport.getRegisteredControls().isEmpty()) {
            return;
        }
        validationSupport.registerValidator(branch_name_txt,
                Validator.createEmptyValidator("This field is not optional."));
        validationSupport.registerValidator(branch_adrs_txt,
                Validator.createEmptyValidator("This field is not optional."));
        validationSupport.registerValidator(branch_tel_txt,
                Validator.combine(Validator.createEmptyValidator("This field is not optional."),
                        Validator.createRegexValidator("Should be a telephone number with 10 digits.", "\\d{10}", Severity.ERROR)));
        validationSupport.registerValidator(branch_type_combo,
                Validator.createEmptyValidator("Branch Type Selection required"));

    }

    private void searchThroughTableByCode(String searchText, FilteredList<Branch> filteredBranches) {

        FilteredList<Branch> filtered = filteredBranches.filtered(branch -> {
            if (searchText == null || searchText.isEmpty()) {
                return false;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            if (branch.getBranchCode().toLowerCase().equals(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
        if (!filtered.isEmpty()) {
            initBranchTable(filtered);
        }
    }

    private void searchThroughTableByName(String searchText, FilteredList<Branch> filteredBranches) {

        FilteredList<Branch> filtered = filteredBranches.filtered(branch -> {
            if (searchText == null || searchText.isEmpty()) {
                return false;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            if (branch.getBranchName().toLowerCase().equals(lowerCaseFilter)) {
                return true;
            }
            return false;
        });
        if (!filtered.isEmpty()) {
            initBranchTable(filtered);
        }
    }

    private Integer getIdByBranchCode(Session session, String bCode) {
        Criteria c = session.createCriteria(Branch.class);
        c.add(Restrictions.eq("branchCode", bCode));
        Branch filteredBranch = (Branch) c.uniqueResult();
        return filteredBranch != null ? filteredBranch.getId() : 0;
    }

    private void disableButtonWithLoggingPrv(LoggedSessionHandler ls) {
        sav_brnch_btn.setDisable(!ls.checkPrivilegeExist(10402));
        branch_actv_deactv_btn.setDisable(!ls.checkPrivilegeExist(10403));
    }

    private void bindValidationOnPaneControlFocus(Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            c.focusedProperty().addListener(e -> {
                registerInputValidation();
            });

        }
    }

    private boolean isValidationEmpty() {
        return validationSupport.validationResultProperty().get() == null;
    }

}
