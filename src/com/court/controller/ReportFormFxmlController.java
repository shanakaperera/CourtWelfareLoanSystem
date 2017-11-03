/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.controller;

import com.court.db.HibernateUtil;
import com.court.handler.ReportHandler;
import com.court.model.MemberLoan;
import com.court.model.Member;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;

/**
 * FXML Controller class
 *
 * @author Shanaka P
 */
public class ReportFormFxmlController implements Initializable {

    @FXML
    private Button mbr_loans_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onLoanAction(ActionEvent event) {
        String reportPath = "com/court/reports/LoansReport.jasper";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class);
        List<MemberLoan> list = (List<MemberLoan>) c.list();
        JRBeanCollectionDataSource memberLoanBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Welfare Loan List");
        ReportHandler rh = new ReportHandler(reportPath, map, memberLoanBeanCollection);
        rh.genarateReport();
        rh.viewReport();
        session.close();
    }

    @FXML
    private void onMemberAction(ActionEvent event) {
        String reportPath = "com/court/reports/MemberReport.jasper";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        List<Member> list = (List<Member>) c.list();
        JRBeanCollectionDataSource memberBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Welfare Member List");
        ReportHandler rh = new ReportHandler(reportPath, map, memberBeanCollection);
        rh.genarateReport();
        rh.viewReport();
        session.close();
    }

    @FXML
    private void onMemberWiseLoanAction(ActionEvent event) throws JRException {
        String reportPath = "com/court/reports/MemberWiseLoansReport.jasper";
        String subReportPath = "com/court/reports/MemberWiseLoansSubreport.jasper";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(Member.class);
        List<Member> list = (List<Member>) c.list();
        JRBeanCollectionDataSource memberLoansBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Member wise loans List");
        JasperReport subReport = (JasperReport) JRLoader.loadObject(
                ClassLoader.getSystemResourceAsStream(subReportPath));
        map.put("SUBREPORT", subReport);
        ReportHandler rh = new ReportHandler(reportPath, map, memberLoansBeanCollection);
        rh.genarateReport();
        rh.viewReport();
        session.close();
    }

    @FXML
    private void onBranchCollectionAction(ActionEvent event) throws JRException {
        String reportPath = "com/court/reports/BranchWiseCollection.jasper";
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(MemberLoan.class, "ml");
        ProjectionList pList = Projections.projectionList();
        ClassMetadata lpMeta = session.getSessionFactory().getClassMetadata(MemberLoan.class);
        pList.add(Projections.property(lpMeta.getIdentifierPropertyName()));

        String branchCode = "BR00001";
        for (String prop : lpMeta.getPropertyNames()) {
            pList.add(Projections.property(prop), prop);
        }
        c.createAlias("ml.member", "m")
                .createAlias("m.branch", "b")
                .addOrder(Order.asc("ml.id"))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("ml.isComplete", false))
                        .add(Restrictions.eq("b.branchCode", branchCode))
                ).setProjection(pList.add(Projections.groupProperty("ml.memberLoanCode")))
                .setResultTransformer(Transformers.aliasToBean(MemberLoan.class));
        List<MemberLoan> list = c.list();

        JRBeanCollectionDataSource branchCollBeanCollection = new JRBeanCollectionDataSource(list);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", ReportHandler.COMPANY_NAME);
        map.put("companyAddress", ReportHandler.ADDRESS);
        map.put("reportTitle", "Branch Wise Collection");
        ReportHandler rh = new ReportHandler(reportPath, map, branchCollBeanCollection);
        rh.genarateReport();
        rh.viewReport();
        session.close();
    }

}
