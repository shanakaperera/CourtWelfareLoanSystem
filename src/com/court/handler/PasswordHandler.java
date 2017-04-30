/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 *
 * @author Shanaka P
 */
public class PasswordHandler {

    private static final BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();

    /**
     * This method use to encrypt the plain password given by the user
     *
     * @param password plain String
     * @return encrypted string
     */
    public static String encryptPassword(String password) {
        return encryptor.encryptPassword(password);
    }

    /**
     * This method use to check if the given password matches with the saved
     * password in db
     * @param typedPass
     * @param encryptedPass
     * @return boolean value
     */
    public static boolean isValidPass(String typedPass, String encryptedPass) {
        return encryptor.checkPassword(typedPass, encryptedPass);
    }
}
