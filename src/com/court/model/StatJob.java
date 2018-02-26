/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Shanaka P
 */
public class StatJob implements Serializable {

    private Integer id;
    private Member member;
    private User user;
    private String jobStatus;
    private Date dateUpdated;

    public StatJob() {
    }

    public StatJob(Member member, User user) {
        this.member = member;
        this.user = user;
    }

    public StatJob(Member member, User user, String jobStatus, Date dateUpdated) {
        this.member = member;
        this.user = user;
        this.jobStatus = jobStatus;
        this.dateUpdated = dateUpdated;
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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJobStatus() {
        return this.jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Date getDateUpdated() {
        return this.dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
