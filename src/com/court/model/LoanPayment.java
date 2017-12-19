package com.court.model;
// Generated Oct 9, 2017 7:04:51 PM by Hibernate Tools 4.3.1

import java.util.Date;

/**
 * LoanPayment generated by hbm2java
 */
public class LoanPayment implements java.io.Serializable {

    private Integer id;
    private MemberLoan memberLoan;
    private Integer installmentNo;
    private String chequeNo;
    private Integer installmentDue;
    private Date paymentDate;
    private Date installmentDate;
    private boolean isLast;
    private Double paidAmt;
    private Double listedPay;
    private Integer payOffice;
    private Integer workOffice;

    private String groupPro;
    private double lSum;

    public LoanPayment() {
    }

    public LoanPayment(MemberLoan memberLoan, boolean isLast) {
        this.memberLoan = memberLoan;
        this.isLast = isLast;
    }

    public LoanPayment(MemberLoan memberLoan, Integer installmentNo, String chequeNo, Integer installmentDue, Date paymentDate, boolean isLast) {
        this.memberLoan = memberLoan;
        this.installmentNo = installmentNo;
        this.chequeNo = chequeNo;
        this.installmentDue = installmentDue;
        this.paymentDate = paymentDate;
        this.isLast = isLast;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MemberLoan getMemberLoan() {
        return this.memberLoan;
    }

    public void setMemberLoan(MemberLoan memberLoan) {
        this.memberLoan = memberLoan;
    }

    public Integer getInstallmentNo() {
        return this.installmentNo;
    }

    public void setInstallmentNo(Integer installmentNo) {
        this.installmentNo = installmentNo;
    }

    public String getChequeNo() {
        return this.chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public Integer getInstallmentDue() {
        return this.installmentDue;
    }

    public void setInstallmentDue(Integer installmentDue) {
        this.installmentDue = installmentDue;
    }

    public Date getPaymentDate() {
        return this.paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean isIsLast() {
        return this.isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public Date getInstallmentDate() {
        return installmentDate;
    }

    public void setInstallmentDate(Date installmentDate) {
        this.installmentDate = installmentDate;
    }

    public Double getPaidAmt() {
        return paidAmt;
    }

    public void setPaidAmt(Double paidAmt) {
        this.paidAmt = paidAmt;
    }

    public String getGroupPro() {
        return groupPro;
    }

    public void setGroupPro(String groupPro) {
        this.groupPro = groupPro;
    }

    public double getlSum() {
        return lSum;
    }

    public void setlSum(double lSum) {
        this.lSum = lSum;
    }

    public Double getListedPay() {
        return listedPay;
    }

    public void setListedPay(Double listedPay) {
        this.listedPay = listedPay;
    }

    public Integer getPayOffice() {
        return payOffice;
    }

    public void setPayOffice(Integer payOffice) {
        this.payOffice = payOffice;
    }

    public Integer getWorkOffice() {
        return workOffice;
    }

    public void setWorkOffice(Integer workOffice) {
        this.workOffice = workOffice;
    }
    
    
}
