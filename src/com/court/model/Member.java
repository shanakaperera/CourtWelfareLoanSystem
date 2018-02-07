package com.court.model;
// Generated Oct 9, 2017 7:04:51 PM by Hibernate Tools 4.3.1

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Member generated by hbm2java
 */
public class Member implements java.io.Serializable {

    private Integer id;
    private Branch branch;
    private Branch payOffice;
    private String memberId;
    private String empId;
    private String fullName;
    private String nameWithIns;
    private String address;
    private String email;
    private String jobTitle;
    private String tel1;
    private String tel2;
    private String sex;
    private String maritalStatus;
    private Date dob;
    private Date appintedDate;
    private Date joinedDate;
    private String description;
    private String spouse;
    private String mother;
    private String father;
    private String memBenifits;
    private String accBenifits;
    private String imgPath;
    private boolean status;
    private String curStatus;
    private String paymentOfficer;
    private String jobStatus;
    private String nic;
    private Double overpay;
    private Set<MemberLoan> memberLoans = new HashSet<>();
    private Set<MemChild> memChilds = new HashSet<>();
    private Set<Document> documents = new HashSet<>();
    private Set<MemberSubscriptions> memberSubscriptions = new HashSet<>();
    private Set<ReceiptPay> receiptPays = new HashSet<>();

    private double totalPayment;
    private double totalSubscription;
    private boolean collected = true;

    public Member() {
    }

    public Member(String string) {
        this.fullName = string;
    }

    public Member(Branch branch, String memberId, boolean status) {
        this.branch = branch;
        this.memberId = memberId;
        this.status = status;
    }

    public Member(Branch branch, String memberId, String fullName, String nameWithIns, String address, String email, String jobTitle, String tel1, String tel2, String sex, String maritalStatus, Date dob, Date appintedDate, Date joinedDate, String description, String spouse, String mother, String father, String memBenifits, String accBenifits, String imgPath, boolean status, Set<MemberLoan> memberLoans, Set<MemChild> memChilds, Set<Document> documents, Set<MemberSubscriptions> memberSubscriptions) {
        this.branch = branch;
        this.memberId = memberId;
        this.fullName = fullName;
        this.nameWithIns = nameWithIns;
        this.address = address;
        this.email = email;
        this.jobTitle = jobTitle;
        this.tel1 = tel1;
        this.tel2 = tel2;
        this.sex = sex;
        this.maritalStatus = maritalStatus;
        this.dob = dob;
        this.appintedDate = appintedDate;
        this.joinedDate = joinedDate;
        this.description = description;
        this.spouse = spouse;
        this.mother = mother;
        this.father = father;
        this.memBenifits = memBenifits;
        this.accBenifits = accBenifits;
        this.imgPath = imgPath;
        this.status = status;
        this.memberLoans = memberLoans;
        this.memChilds = memChilds;
        this.documents = documents;
        this.memberSubscriptions = memberSubscriptions;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Branch getBranch() {
        return this.branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNameWithIns() {
        return this.nameWithIns;
    }

    public void setNameWithIns(String nameWithIns) {
        this.nameWithIns = nameWithIns;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getTel1() {
        return this.tel1;
    }

    public void setTel1(String tel1) {
        this.tel1 = tel1;
    }

    public String getTel2() {
        return this.tel2;
    }

    public void setTel2(String tel2) {
        this.tel2 = tel2;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMaritalStatus() {
        return this.maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Date getDob() {
        return this.dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Date getAppintedDate() {
        return this.appintedDate;
    }

    public void setAppintedDate(Date appintedDate) {
        this.appintedDate = appintedDate;
    }

    public Date getJoinedDate() {
        return this.joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpouse() {
        return this.spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public String getMother() {
        return this.mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getFather() {
        return this.father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMemBenifits() {
        return this.memBenifits;
    }

    public void setMemBenifits(String memBenifits) {
        this.memBenifits = memBenifits;
    }

    public String getAccBenifits() {
        return this.accBenifits;
    }

    public void setAccBenifits(String accBenifits) {
        this.accBenifits = accBenifits;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Set<MemberLoan> getMemberLoans() {
        return this.memberLoans;
    }

    public void setMemberLoans(Set<MemberLoan> memberLoans) {
        this.memberLoans = memberLoans;
    }

    public Set<MemChild> getMemChilds() {
        return this.memChilds;
    }

    public void setMemChilds(Set<MemChild> memChilds) {
        this.memChilds = memChilds;
    }

    public Set<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public Set<MemberSubscriptions> getMemberSubscriptions() {
        return this.memberSubscriptions;
    }

    public void setMemberSubscriptions(Set<MemberSubscriptions> memberSubscriptions) {
        this.memberSubscriptions = memberSubscriptions;
    }

    public String getPaymentOfficer() {
        return paymentOfficer;
    }

    public void setPaymentOfficer(String paymentOfficer) {
        this.paymentOfficer = paymentOfficer;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public double getTotalSubscription() {
        return totalSubscription;
    }

    public void setTotalSubscription(double totalSubscription) {
        this.totalSubscription = totalSubscription;
    }

    public String getEmpId() {
        return empId;
    }

    public String getCurStatus() {
        return curStatus;
    }

    public void setCurStatus(String curStatus) {
        this.curStatus = curStatus;
    }
    
    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Double getOverpay() {
        return overpay;
    }

    public void setOverpay(Double overpay) {
        this.overpay = overpay;
    }
    
    @Override
    public String toString() {
        return this.memberId != null ? (this.memberId + " - " + this.fullName) : this.fullName;
    }

    public Set<ReceiptPay> getReceiptPays() {
        return receiptPays;
    }

    public void setReceiptPays(Set<ReceiptPay> receiptPays) {
        this.receiptPays = receiptPays;
    }

    public Branch getPayOffice() {
        return payOffice;
    }

    public void setPayOffice(Branch payOffice) {
        this.payOffice = payOffice;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.memberId);
        hash = 61 * hash + Objects.hashCode(this.fullName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Member other = (Member) obj;
        return true;
    }

}
