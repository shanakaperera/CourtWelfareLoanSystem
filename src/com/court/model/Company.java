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
public class Company implements java.io.Serializable {

    private Integer id;
    private String companyName;
    private String titleName;
    private Date dateUpdated;
    private String defaults;

    public Company() {
    }

    public Company(Integer id, String companyName, String titleName, Date dateUpdated, String defaults) {
        this.id = id;
        this.companyName = companyName;
        this.titleName = titleName;
        this.dateUpdated = dateUpdated;
        this.defaults = defaults;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getDefaults() {
        return defaults;
    }

    public void setDefaults(String defaults) {
        this.defaults = defaults;
    }

}
