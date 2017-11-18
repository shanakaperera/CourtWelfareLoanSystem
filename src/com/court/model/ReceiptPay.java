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
public class ReceiptPay implements java.io.Serializable {

    private Integer id;
    private Member member;
    private Date payDate;
    private Double amount;
    private String payIds;
    private String paymentType;

    public ReceiptPay() {
    }

    public ReceiptPay(Member member, String paymentType) {
        this.member = member;
        this.paymentType = paymentType;
    }

    public ReceiptPay(Member member, Date payDate, Double amount, String payIds, String paymentType) {
        this.member = member;
        this.payDate = payDate;
        this.amount = amount;
        this.payIds = payIds;
        this.paymentType = paymentType;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Member getMember() {
        return this.member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Date getPayDate() {
        return this.payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPayIds() {
        return this.payIds;
    }

    public void setPayIds(String payIds) {
        this.payIds = payIds;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
