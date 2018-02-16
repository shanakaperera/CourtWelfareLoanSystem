/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.main;

import com.court.handler.FxUtilsHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author Shanaka P
 */
public class NewClass {

    public static void main(String[] args) {
        Date d = new Date(Long.MAX_VALUE);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(df.format(d));
    }

}
