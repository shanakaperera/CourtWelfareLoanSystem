/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Shanaka P
 */
public class MemberSubscriptions implements java.io.Serializable {

    private Integer id;
    private Member member;
    private MemberSubscription memberSubscription;
    private String repaymentType;
    private Double amount;
    private Set<SubscriptionPay> subscriptionPays = new HashSet<>();

    public MemberSubscriptions() {
    }

    public MemberSubscriptions(Member member, MemberSubscription memberSubscription) {
        this.member = member;
        this.memberSubscription = memberSubscription;
    }

    public MemberSubscriptions(Member member, MemberSubscription memberSubscription, Double amount) {
        this.member = member;
        this.memberSubscription = memberSubscription;
        this.amount = amount;
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

    public MemberSubscription getMemberSubscription() {
        return this.memberSubscription;
    }

    public void setMemberSubscription(MemberSubscription memberSubscription) {
        this.memberSubscription = memberSubscription;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRepaymentType() {
        return this.repaymentType;
    }

    public void setRepaymentType(String repaymentType) {
        this.repaymentType = repaymentType;
    }

    public Set<SubscriptionPay> getSubscriptionPays() {
        return subscriptionPays;
    }

    public void setSubscriptionPays(Set<SubscriptionPay> subscriptionPays) {
        this.subscriptionPays = subscriptionPays;
    }

}
