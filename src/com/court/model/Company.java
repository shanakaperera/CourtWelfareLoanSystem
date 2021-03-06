package com.court.model;
// Generated Oct 9, 2017 7:04:51 PM by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Company generated by hbm2java
 */
public class Company  implements java.io.Serializable {


     private Integer id;
     private String companyName;
     private String titleName;
     private Date dateUpdated;
     private String defaults;

    public Company() {
    }

    public Company(String companyName, String titleName, Date dateUpdated, String defaults) {
       this.companyName = companyName;
       this.titleName = titleName;
       this.dateUpdated = dateUpdated;
       this.defaults = defaults;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getCompanyName() {
        return this.companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getTitleName() {
        return this.titleName;
    }
    
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public Date getDateUpdated() {
        return this.dateUpdated;
    }
    
    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
    public String getDefaults() {
        return this.defaults;
    }
    
    public void setDefaults(String defaults) {
        this.defaults = defaults;
    }




}


