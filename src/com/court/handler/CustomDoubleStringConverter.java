/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import javafx.util.StringConverter;

/**
 *
 * @author Shanaka P
 */
public class CustomDoubleStringConverter extends StringConverter<Double> {

    @Override
    public Double fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        if (value.matches("\\D+")) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        if (Math.signum(Double.valueOf(value)) <= 0) {
            return null;
        }

        return Double.valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(Double value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return Double.toString(value.doubleValue());
    }
}
