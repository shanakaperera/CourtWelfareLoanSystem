package com.court.model;
// Generated Apr 18, 2017 4:12:00 PM by Hibernate Tools 4.3.1



/**
 * DocSequnce generated by hbm2java
 */
public class DocSequnce  implements java.io.Serializable {


     private Integer id;
     private String seqName;
     private String tableName;
     private String format;
     private String defaultFormat;
     private Integer startFrom;

    public DocSequnce() {
    }

    public DocSequnce(String seqName, String tableName, String format, Integer startFrom) {
       this.seqName = seqName;
       this.tableName = tableName;
       this.format = format;
       this.startFrom = startFrom;
    }

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getSeqName() {
        return this.seqName;
    }
    
    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    public Integer getStartFrom() {
        return this.startFrom;
    }
    
    public void setStartFrom(Integer startFrom) {
        this.startFrom = startFrom;
    }




}


