/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Shanaka P
 */
public class NewClass {

    public static void main(String[] args) {
        DateTimeZone zone = DateTimeZone.forID("Asia/Colombo");
        DateTime dateTime = new DateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), zone);
        // DateTime now = DateTime.parse("25-11-2016", DateTimeFormat.forPattern("dd-MM-yyyy"));
        DateTime now = DateTime.now(zone);
        if ((dateTime.getMonthOfYear() == now.getMonthOfYear()) && (dateTime.getYear() == now.getYear())) {
            System.out.println("Working");
        } else {
            System.out.println("Not in rage");
        }
    }

}
