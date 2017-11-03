/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.db.HibernateUtil;
import com.court.model.LoanPayment;
import com.court.model.Member;
import com.court.model.MemberSubscriptions;
import eu.hansolo.tilesfx.Tile;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shanaka P
 */
public class FxUtilsHandler {

    /**
     * This method use to change the border color of the given parent pane all
     * children controls when needed * @param parent_pane
     *
     * @param required_status
     * @param parent_panes
     */
    public static void activeDeactiveChildrenControls(boolean required_status, Pane... parent_panes) {
        ObservableList<Node> childrenNodes = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            childrenNodes.addAll(parent_pane.getChildren());
        }
        for (Node childrenNode : childrenNodes) {
            if (childrenNode instanceof TextField) {
                ((TextField) childrenNode).setStyle(required_status ? "" : "-fx-border-color:#d9534f");
            }
            if (childrenNode instanceof TextArea) {
                ((TextArea) childrenNode).setStyle(required_status ? "" : "-fx-border-color:#d9534f");
            }
            if (childrenNode instanceof ComboBox) {
                ((ComboBox) childrenNode).setStyle(required_status ? "" : "-fx-border-color:#d9534f");
            }
            if (childrenNode instanceof DatePicker) {
                ((DatePicker) childrenNode).setStyle(required_status ? "" : "-fx-border-color:#d9534f");
            }
        }
    }

    /**
     * This method use to clear all the inputs and set to default.
     *
     * @param parent_panes
     *
     */
    public static void clearFields(Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            if (c instanceof TextField) {
                if (((TextField) c).getText() != null) {
                    if (((TextField) c).getText().contains("Rs")) {
                        ((TextField) c).setText(TextFormatHandler.CURRENCY_DECIMAL_FORMAT.format(0));
                    } else if (((TextField) c).getText().contains("%")) {
                        ((TextField) c).setText(TextFormatHandler.PRECENTAGE_DECIMAL_FORMAT.format(0));
                    } else {
                        ((TextField) c).setText(null);
                    }
                }

            }
            if (c instanceof ComboBox) {
                ((ComboBox) c).getSelectionModel().select(null);
            }
            if (c instanceof TextArea) {
                ((TextArea) c).setText("");
            }
            if (c instanceof DatePicker) {
                ((DatePicker) c).setValue(null);
            }
            if (c instanceof CheckBox) {
                ((CheckBox) c).setSelected(false);
            }

        }
    }

    /**
     * This method use to disable all the inputs and set to default.
     *
     * @param disable
     * @param parent_panes
     *
     */
    public static void disableFields(boolean disable, Pane... parent_panes) {
        ObservableList<Node> children = FXCollections.observableArrayList();
        for (Pane parent_pane : parent_panes) {
            children.addAll(parent_pane.getChildren());
        }
        for (Node c : children) {
            if (c instanceof TextField) {
                ((TextField) c).setDisable(disable);
            }
            if (c instanceof ComboBox) {
                ((ComboBox) c).setDisable(disable);
            }
            if (c instanceof TextArea) {
                ((TextArea) c).setDisable(disable);
            }
            if (c instanceof DatePicker) {
                ((DatePicker) c).setDisable(disable);
            }

        }
    }

    /**
     * This method use to change the visible date format of the javafx
     * datepickers
     *
     * @param datePickers
     */
    public static void setDatePickerTimeFormat(DatePicker... datePickers) {
        for (DatePicker datePicker : datePickers) {
            datePicker.setConverter(new StringConverter<LocalDate>() {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                @Override
                public String toString(LocalDate date) {
                    return date != null ? dateTimeFormatter.format(date) : "";
                }

                @Override
                public LocalDate fromString(String dateString) {
                    return dateString == null || dateString.trim().isEmpty() ? null : LocalDate.parse(dateString, dateTimeFormatter);
                }
            });
        }

    }

    /**
     * This method use to convert java.util.Date to java.time.LocalDate and that
     * is how we fetch db date into datepicker
     *
     *
     * @param date
     * @return
     */
    public static LocalDate getLocalDateFrom(Date date) {
        String date_format = "yyyy-MM-dd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(date_format);
        return LocalDate.parse(new SimpleDateFormat(date_format).format(date), dateTimeFormatter);
    }

    /**
     * Convert LocalDate to java.util.Date
     *
     * @param localDate
     * @return java.util.Date
     *
     */
    public static Date getDateFrom(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * This method use to change the deactivate button text as needed once you
     * fetch member from db
     *
     *
     * @param btn
     * @param status
     * @param isCaps
     */
    public static void activeBtnAppearanceChange(Button btn, boolean status, boolean isCaps) {
        String string_active = isCaps ? "ACTIVE" : "Active";
        String string_deactive = isCaps ? "DEACTIVATE" : "Deactivate";
        btn.setText(status ? string_deactive : string_active);
    }

    /**
     * This method use to round required number for n decimal places
     *
     * @param value
     * @param numberOfDigitsAfterDecimalPoint
     * @return double value
     */
    public static double roundNumber(double value, int numberOfDigitsAfterDecimalPoint) {
        if (value > 0) {
            BigDecimal bigDecimal = new BigDecimal(value);
            bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                    BigDecimal.ROUND_HALF_UP);
            return bigDecimal.doubleValue();
        } else {
            return 0;
        }

    }

    /**
     * This method use to filter a collection using the Stream API by checking
     * the distinctness of a property of each object
     *
     * @param <T>
     * @param keyExtractor
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * This method use to create show and hide password field
     *
     * @param passwordField
     *
     * @param container
     */
    public static void showHidePasswordField(PasswordField passwordField, VBox container) {
        TextField textField = new TextField();
        CheckBox checkBox = new CheckBox("Show/Hide password");
        // Set initial state
        textField.setManaged(false);
        textField.setVisible(false);

        textField.managedProperty().bind(checkBox.selectedProperty());
        textField.visibleProperty().bind(checkBox.selectedProperty());

        passwordField.managedProperty().bind(checkBox.selectedProperty().not());
        passwordField.visibleProperty().bind(checkBox.selectedProperty().not());

        textField.textProperty().bindBidirectional(passwordField.textProperty());
        container.getChildren().addAll(textField, checkBox);
    }

    /**
     * This method use to live update time of a time tile
     *
     * @param timeTile
     *
     */
    public static void startTimeOf(Tile timeTile) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        actionEvent -> timeTile.setTime(ZonedDateTime
                                .ofInstant(new Date().toInstant(), ZoneId.systemDefault()))
                ),
                new KeyFrame(
                        Duration.seconds(60)
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static List<LoanPayment> previousInstallments(int memeberId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<LoanPayment> list = session.createCriteria(LoanPayment.class)
                .createAlias("memberLoan", "ml")
                .createAlias("ml.member", "m")
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("m.id", memeberId))).list();
        session.close();
        System.out.println("MId - " + memeberId + " : Size - " + list.size());
        return list;
    }

    public static JRBeanCollectionDataSource getMemberSubscriptions(Member m) {
        List<MemberSubscriptions> mbrSubs = new CopyOnWriteArrayList<>(m.getMemberSubscriptions());
        boolean flag = FxUtilsHandler.previousInstallments(m.getId()).isEmpty();

        for (MemberSubscriptions ms : mbrSubs) {
            if (flag) {
                if (ms.getRepaymentType().equalsIgnoreCase("Once")) {
                    ms.setAmount(0.0);
                }
            }
        }
        return new JRBeanCollectionDataSource(mbrSubs);
    }
}
