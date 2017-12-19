package com.court.model;
// Generated Oct 9, 2017 7:04:51 PM by Hibernate Tools 4.3.1

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MemberLoan generated by hbm2java
 */
public class MemberLoan implements java.io.Serializable {

    private Integer id;
    private Member member;
    private String memberLoanCode;
    private Date grantedDate;
    private String guarantors;
    private Double loanAmount;
    private Double loanInterest;
    private String interestPer;
    private String interestMethod;
    private Integer loanDuration;
    private String durationPer;
    private String repaymentCycle;
    private Integer noOfRepay;
    private Double loanInstallment;
    private boolean isComplete;
    private Date paidUntil;
    private boolean status;
    private boolean hasChild;
    private boolean isChild;
    private int childId;
    private double totalPay;
    private String loanName;
    private boolean closedLoan;
    private double kotaLeft;
    private double paidSofar;
    private boolean oldLoan;
    private int lastInstall;
    private Set<LoanPayment> loanPayments = new HashSet<>();
    private List<MemberSubscription> mbrSubs = new ArrayList<>();

    private String groupPro;
    private double lSum;

    public MemberLoan() {

    }

    public MemberLoan(String memberLoanCode, Date grantedDate, Double loanAmount, String loanName) {
        this.memberLoanCode = memberLoanCode;
        this.grantedDate = grantedDate;
        this.loanAmount = loanAmount;
        this.loanName = loanName;
    }

    public MemberLoan(Member member, String memberLoanCode, boolean isComplete, boolean status, boolean hasChild, int childId) {
        this.member = member;
        this.memberLoanCode = memberLoanCode;
        this.isComplete = isComplete;
        this.status = status;
        this.hasChild = hasChild;
        this.childId = childId;
    }

    public MemberLoan(Member member, String memberLoanCode, Date grantedDate, String guarantors, Double loanAmount, Double loanInterest, String interestPer, String interestMethod, Integer loanDuration, String durationPer, String repaymentCycle, Integer noOfRepay, Double loanInstallment, boolean isComplete, boolean status, boolean hasChild, int childId, Set<LoanPayment> loanPayments) {
        this.member = member;
        this.memberLoanCode = memberLoanCode;
        this.grantedDate = grantedDate;
        this.guarantors = guarantors;
        this.loanAmount = loanAmount;
        this.loanInterest = loanInterest;
        this.interestPer = interestPer;
        this.interestMethod = interestMethod;
        this.loanDuration = loanDuration;
        this.durationPer = durationPer;
        this.repaymentCycle = repaymentCycle;
        this.noOfRepay = noOfRepay;
        this.loanInstallment = loanInstallment;
        this.isComplete = isComplete;
        this.status = status;
        this.hasChild = hasChild;
        this.childId = childId;
        this.loanPayments = loanPayments;
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

    public String getMemberLoanCode() {
        return this.memberLoanCode;
    }

    public void setMemberLoanCode(String memberLoanCode) {
        this.memberLoanCode = memberLoanCode;
    }

    public Date getGrantedDate() {
        return this.grantedDate;
    }

    public void setGrantedDate(Date grantedDate) {
        this.grantedDate = grantedDate;
    }

    public String getGuarantors() {
        return this.guarantors;
    }

    public void setGuarantors(String guarantors) {
        this.guarantors = guarantors;
    }

    public Double getLoanAmount() {
        return this.loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
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

    public String getInterestMethod() {
        return this.interestMethod;
    }

    public void setInterestMethod(String interestMethod) {
        this.interestMethod = interestMethod;
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

    public Double getLoanInstallment() {
        return this.loanInstallment;
    }

    public void setLoanInstallment(Double loanInstallment) {
        this.loanInstallment = loanInstallment;
    }

    public boolean isIsComplete() {
        return this.isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isHasChild() {
        return this.hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public int getChildId() {
        return this.childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public Set<LoanPayment> getLoanPayments() {
        return this.loanPayments;
    }

    public void setLoanPayments(Set<LoanPayment> loanPayments) {
        this.loanPayments = loanPayments;
    }

    public List<MemberSubscription> getMbrSubs() {
        return mbrSubs;
    }

    public void setMbrSubs(List<MemberSubscription> mbrSubs) {
        this.mbrSubs = mbrSubs;
    }

    public double getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(double totalPay) {
        this.totalPay = totalPay;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public Date getPaidUntil() {
        return paidUntil;
    }

    public void setPaidUntil(Date paidUntil) {
        this.paidUntil = paidUntil;
    }

    public boolean isIsChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
    }

    public boolean isClosedLoan() {
        return closedLoan;
    }

    public void setClosedLoan(boolean closedLoan) {
        this.closedLoan = closedLoan;
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

    public double getKotaLeft() {
        return kotaLeft;
    }

    public void setKotaLeft(double kotaLeft) {
        this.kotaLeft = kotaLeft;
    }

    public double getPaidSofar() {
        return paidSofar;
    }

    public void setPaidSofar(double paidSofar) {
        this.paidSofar = paidSofar;
    }

    public boolean isOldLoan() {
        return oldLoan;
    }

    public void setOldLoan(boolean oldLoan) {
        this.oldLoan = oldLoan;
    }

    public int getLastInstall() {
        return lastInstall;
    }

    public void setLastInstall(int lastInstall) {
        this.lastInstall = lastInstall;
    }

}
