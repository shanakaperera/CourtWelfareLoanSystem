package com.court.model;
// Generated Oct 9, 2017 7:04:51 PM by Hibernate Tools 4.3.1



/**
 * Loan generated by hbm2java
 */
public class Loan  implements java.io.Serializable {


     private Integer id;
     private String loanId;
     private String loanName;
     private String interestMethod;
     private Double loanInterest;
     private String interestPer;
     private Integer loanDuration;
     private String durationPer;
     private String repaymentCycle;
     private Integer noOfRepay;
     private boolean status;

    public Loan() {
    }

	
    public Loan(String loanId, boolean status) {
        this.loanId = loanId;
        this.status = status;
    }
    public Loan(String loanId, String loanName, String interestMethod, Double loanInterest, String interestPer, Integer loanDuration, String durationPer, String repaymentCycle, Integer noOfRepay, boolean status) {
       this.loanId = loanId;
       this.loanName = loanName;
       this.interestMethod = interestMethod;
       this.loanInterest = loanInterest;
       this.interestPer = interestPer;
       this.loanDuration = loanDuration;
       this.durationPer = durationPer;
       this.repaymentCycle = repaymentCycle;
       this.noOfRepay = noOfRepay;
       this.status = status;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLoanId() {
        return this.loanId;
    }
    
    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }
    public String getLoanName() {
        return this.loanName;
    }
    
    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }
    public String getInterestMethod() {
        return this.interestMethod;
    }
    
    public void setInterestMethod(String interestMethod) {
        this.interestMethod = interestMethod;
    }
    public Double getLoanInterest() {
        return this.loanInterest;
    }
    
    public void setLoanInterest(Double loanInterest) {
        this.loanInterest = loanInterest;
    }
    public String getInterestPer() {
        return this.interestPer;
    }
    
    public void setInterestPer(String interestPer) {
        this.interestPer = interestPer;
    }
    public Integer getLoanDuration() {
        return this.loanDuration;
    }
    
    public void setLoanDuration(Integer loanDuration) {
        this.loanDuration = loanDuration;
    }
    public String getDurationPer() {
        return this.durationPer;
    }
    
    public void setDurationPer(String durationPer) {
        this.durationPer = durationPer;
    }
    public String getRepaymentCycle() {
        return this.repaymentCycle;
    }
    
    public void setRepaymentCycle(String repaymentCycle) {
        this.repaymentCycle = repaymentCycle;
    }
    public Integer getNoOfRepay() {
        return this.noOfRepay;
    }
    
    public void setNoOfRepay(Integer noOfRepay) {
        this.noOfRepay = noOfRepay;
    }
    public boolean isStatus() {
        return this.status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }




}


