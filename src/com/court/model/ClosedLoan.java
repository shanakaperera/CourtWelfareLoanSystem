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
public class ClosedLoan implements java.io.Serializable {

    private Integer id;
    private Integer memberLoanId;
    private Integer closedStart;
    private Double actualinstAmt;
    private Date endedDate;
    private Double totalPayment;
    private Integer totinstClosed;

    public ClosedLoan() {
    }

    public ClosedLoan(Integer memberLoanId, Integer closedStart, Double actualinstAmt, Date endedDate, Double totalPayment, Integer totinstClosed) {
        this.memberLoanId = memberLoanId;
        this.closedStart = closedStart;
        this.actualinstAmt = actualinstAmt;
        this.endedDate = endedDate;
        this.totalPayment = totalPayment;
        this.totinstClosed = totinstClosed;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberLoanId() {
        return this.memberLoanId;
    }

    public void setMemberLoanId(Integer memberLoanId) {
        this.memberLoanId = memberLoanId;
    }

    public Integer getClosedStart() {
        return this.closedStart;
    }

    public void setClosedStart(Integer closedStart) {
        this.closedStart = closedStart;
    }

    public Double getActualinstAmt() {
        return this.actualinstAmt;
    }

    public void setActualinstAmt(Double actualinstAmt) {
        this.actualinstAmt = actualinstAmt;
    }

    public Date getEndedDate() {
        return this.endedDate;
    }

    public void setEndedDate(Date endedDate) {
        this.endedDate = endedDate;
    }

    public Double getTotalPayment() {
        return this.totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Integer getTotinstClosed() {
        return this.totinstClosed;
    }

    public void setTotinstClosed(Integer totinstClosed) {
        this.totinstClosed = totinstClosed;
    }
}
