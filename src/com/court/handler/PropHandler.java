/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Shanaka P
 */
public class PropHandler {

    private static final String STRING_PROPERTY = "string.properties";
    private static final String REGEX_PROPERTY = "regexz.properties";

    private static final Properties PROP = new Properties();

    public static String getStringProperty(String property_name) throws IOException {
        PROP.load(ClassLoader.getSystemResourceAsStream(STRING_PROPERTY));
        return PROP.getProperty(property_name);
    }

    public static String getRegexProperty(String property_name) throws IOException {
        PROP.load(ClassLoader.getSystemResourceAsStream(REGEX_PROPERTY));
        return PROP.getProperty(property_name);
    }
}
