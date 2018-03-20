/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.db.HibernateUtil;
import com.court.model.DocSequnce;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Shanaka P
 */
public class DocSeqHandler {

    private String seq_code;

    public String getSeq_code() {
        return seq_code;
    }

    public void setSeq_code(String seq_code) {
        this.seq_code = seq_code;
    }

    private void extractSeqCode(String seq_format, int start_from) {
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(seq_format);
        while (m.find()) {
            setSeq_code(seq_format.replace(m.group(), "")
                    + String.format("%0" + m.group(1).replaceAll("\\D+", "") + "d",
                            start_from));
        }
    }

    public void reqTable(String table_name, int startFrom) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria cr = session.createCriteria(DocSequnce.class);
        cr.add(Restrictions.eq("tableName", table_name));
        DocSequnce seq = (DocSequnce) cr.uniqueResult();
        session.close();
        if (startFrom == 0) {
            extractSeqCode(seq.getFormat(), seq.getStartFrom());
        } else {
            extractSeqCode(seq.getFormat(), startFrom);
        }
    }
    
}
