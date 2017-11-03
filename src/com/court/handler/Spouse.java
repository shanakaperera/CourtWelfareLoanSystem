/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import java.io.Serializable;

/**
 *
 * @author Shanaka P
 */
public class Spouse implements Serializable{
    
    private String spouse;
    private boolean hoi;
    private boolean aci;

    public Spouse() {
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public boolean isHoi() {
        return hoi;
    }

    public void setHoi(boolean hoi) {
        this.hoi = hoi;
    }

    public boolean isAci() {
        return aci;
    }

    public void setAci(boolean aci) {
        this.aci = aci;
    }
    
    
}
