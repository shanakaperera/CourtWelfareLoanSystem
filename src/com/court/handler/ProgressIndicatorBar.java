/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Shanaka P
 */
public class ProgressIndicatorBar extends StackPane {

    final private ReadOnlyDoubleProperty workDone;
    final private double totalWork;

    final private ProgressBar bar = new ProgressBar();
    final private Text text = new Text();
    final private String WORK_DONE_LABEL_FORMAT = "%.0f";

    final private static int DEFAULT_LABEL_PADDING = 5;

    public ProgressIndicatorBar(final ReadOnlyDoubleProperty workDone, final double totalWork) {
        this.workDone = workDone;
        this.totalWork = totalWork;

        syncProgress();

        workDone.addListener((ObservableValue<? extends Number> observableValue, Number number, Number number2) -> {
            syncProgress();
        });

        bar.setMaxWidth(Double.MAX_VALUE); // allows the progress bar to expand to fill available horizontal space.
        getChildren().setAll(bar, text);
    }

    // synchronizes the progress indicated with the work done.
    private void syncProgress() {
        if (workDone == null || totalWork == 0) {
            text.setText("");
            bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        } else {
            text.setText(String.format(WORK_DONE_LABEL_FORMAT, Math.ceil(workDone.get())) + "%");
            bar.setProgress(workDone.get() / totalWork);
        }

        bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
        bar.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
    }

    public void createProgressIndicatorBar(VBox vBox, ReadOnlyDoubleWrapper workDone) {
        int max_work = 100;
        Timeline countDown = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(workDone, 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(workDone, max_work - totalWork))
        );
        countDown.play();
        if (!vBox.getChildren().isEmpty()) {
            vBox.getChildren().clear();
        }
        vBox.getChildren().addAll(this);
    }
}
