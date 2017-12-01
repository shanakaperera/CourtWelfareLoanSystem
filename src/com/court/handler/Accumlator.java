/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.model.SubscriptionPay;

/**
 *
 * @author Shanaka P
 */
public class Accumlator {

    private double hoi = 0d;
    private double aci = 0d;
    private double savings = 0d;
    private double admission = 0d;
    private double membership = 0d;
    private double optional = 0d;

    public void accumlate(SubscriptionPay sp) {
        this.hoi += sp.getHoiFee();
        this.aci += sp.getAciFee();
        this.savings += sp.getSavingsFee();
        this.admission += sp.getSavingsFee();
        this.membership += sp.getMembershipFee();
        this.optional += sp.getOptional();
    }

    public Accumlator combine(Accumlator ac) {
        this.hoi += ac.hoi;
        this.aci += ac.aci;
        this.savings += ac.savings;
        this.admission += ac.admission;
        this.membership += ac.membership;
        this.optional += ac.optional;
        return this;
    }

    public double getHoi() {
        return hoi;
    }

    public void setHoi(double hoi) {
        this.hoi = hoi;
    }

    public double getAci() {
        return aci;
    }

    public void setAci(double aci) {
        this.aci = aci;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public double getAdmission() {
        return admission;
    }

    public void setAdmission(double admission) {
        this.admission = admission;
    }

    public double getMembership() {
        return membership;
    }

    public void setMembership(double membership) {
        this.membership = membership;
    }

    public double getOptional() {
        return optional;
    }

    public void setOptional(double optional) {
        this.optional = optional;
    }

}
