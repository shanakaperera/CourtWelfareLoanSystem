/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.model;

import java.util.Date;

/**
 *
 * @author Shanaka P
 */
public class SubscriptionPay implements java.io.Serializable {

    private Integer id;
    private MemberSubscriptions memberSubscriptions;
    private Double hoiFee;
    private Double aciFee;
    private Double admissionFee;
    private Double savingsFee;
    private Double membershipFee;
    private Double optional;
    private Date paymentDate;

    public SubscriptionPay() {
    }

    public SubscriptionPay(MemberSubscriptions memberSubscriptions) {
        this.memberSubscriptions = memberSubscriptions;
    }

    public SubscriptionPay(MemberSubscriptions memberSubscriptions, Double hoiFee, Double aciFee, Double admissionFee, Double savingsFee, Double membershipFee, Double optional, Date paymentDate) {
        this.memberSubscriptions = memberSubscriptions;
        this.hoiFee = hoiFee;
        this.aciFee = aciFee;
        this.admissionFee = admissionFee;
        this.savingsFee = savingsFee;
        this.membershipFee = membershipFee;
        this.optional = optional;
        this.paymentDate = paymentDate;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MemberSubscriptions getMemberSubscriptions() {
        return this.memberSubscriptions;
    }

    public void setMemberSubscriptions(MemberSubscriptions memberSubscriptions) {
        this.memberSubscriptions = memberSubscriptions;
    }

    public Double getHoiFee() {
        return this.hoiFee;
    }

    public void setHoiFee(Double hoiFee) {
        this.hoiFee = hoiFee;
    }

    public Double getAciFee() {
        return this.aciFee;
    }

    public void setAciFee(Double aciFee) {
        this.aciFee = aciFee;
    }

    public Double getAdmissionFee() {
        return this.admissionFee;
    }

    public void setAdmissionFee(Double admissionFee) {
        this.admissionFee = admissionFee;
    }

    public Double getSavingsFee() {
        return this.savingsFee;
    }

    public void setSavingsFee(Double savingsFee) {
        this.savingsFee = savingsFee;
    }

    public Double getMembershipFee() {
        return this.membershipFee;
    }

    public void setMembershipFee(Double membershipFee) {
        this.membershipFee = membershipFee;
    }

    public Double getOptional() {
        return this.optional;
    }

    public void setOptional(Double optional) {
        this.optional = optional;
    }

    public Date getPaymentDate() {
        return this.paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

}
