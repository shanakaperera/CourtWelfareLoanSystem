/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Shanaka P
 */
public class LoanCalculationHandler {

    private static final int ROUND_DECIMAL_PLACES = 2;

    /**
     * This method use to calculate loan method reducing balance equal
     * installment
     *
     * @param principal_amt
     * @param interest_rate
     * @param no_of_ins
     * @param interestPer
     * @param tenureIn
     * @param repay_cycle
     * @return
     */
    public static ObservableList<LoanSchedule> reducingBalanceEqInstallmentsCalculator(double principal_amt,
            double interest_rate, int no_of_ins, int interestPer, int tenureIn, int repay_cycle) {

        double P = principal_amt;
        double R = interestPer == 0 ? interest_rate / 100 : (interest_rate / 100) / 12;
        double N = tenureIn == 0 ? no_of_ins : no_of_ins * 12;
        double EMI = (P * R * (Math.pow((1 + R), N)) / ((Math.pow((1 + R), N)) - 1));

        double totalInt = Math.round((EMI * N) - P);
        double totalAmt = Math.round((EMI * N));

        ObservableList<LoanSchedule> lst = FXCollections.observableArrayList();

        double intPerInstallment;
        for (int i = 1; i <= N; i++) {
            intPerInstallment = (P * R);
            P = ((P) - ((EMI) - (intPerInstallment)));
            LoanSchedule ls = new LoanSchedule(i,
                    FxUtilsHandler.roundNumber(((EMI) - intPerInstallment), ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(intPerInstallment, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(P, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(totalInt, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(totalAmt, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(EMI, ROUND_DECIMAL_PLACES));
            lst.add(ls);

        }
        return lst;
    }

    /**
     * This method use to calculate loan method compound interest installment
     *
     * @param principal_amt
     * @param interest_rate
     * @param no_of_ins
     * @param interestPer
     * @param tenureIn
     * @param repay_cycle
     * @return
     */
    public static ObservableList<LoanSchedule> compoundInterestCalculator(double principal_amt,
            double interest_rate, int no_of_ins, int interestPer, int tenureIn, int repay_cycle) {

        double P = principal_amt;
        double R = interestPer == 0 ? interest_rate / 100 : (interest_rate / 100) / 12;
        double N = tenureIn == 0 ? no_of_ins : no_of_ins * 12;
        double T = (principal_amt * Math.pow((1 + (R)), N));

        double installment = T / N;
        double intPerInstallment = (T - P) / N;

        ObservableList<LoanSchedule> lst = FXCollections.observableArrayList();
        double due = P;
        for (int i = 1; i <= N; i++) {
            due = due - FxUtilsHandler.roundNumber((installment - intPerInstallment), ROUND_DECIMAL_PLACES);
            LoanSchedule ls = new LoanSchedule(i,
                    FxUtilsHandler.roundNumber((installment - intPerInstallment), ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(intPerInstallment, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(due, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber((T - P), ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(T, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(installment, ROUND_DECIMAL_PLACES));
            lst.add(ls);
        }
        return lst;
    }

    /**
     * This method use to calculate loan method flat rate installment
     *
     * @param principal_amt
     * @param interest_rate
     * @param no_of_ins
     * @param interestPer
     * @param tenureIn
     * @param repay_cycle
     * @return
     */
    public static ObservableList<LoanSchedule> flatRateCalculator(double principal_amt,
            double interest_rate, int no_of_ins, int interestPer, int tenureIn, int repay_cycle) {

        double P = principal_amt;
        double R = interestPer == 0 ? interest_rate / 100 : (interest_rate / 100) / 12;
        double N = tenureIn == 0 ? no_of_ins : no_of_ins * 12;

        double total_interest = P * R * N;
        double installment = (P + total_interest) / N;
        double intPerInstallment = total_interest / N;

        ObservableList<LoanSchedule> lst = FXCollections.observableArrayList();
        double due = P;
        for (int i = 1; i <= N; i++) {
            due = due - FxUtilsHandler.roundNumber((installment - intPerInstallment), ROUND_DECIMAL_PLACES);
            LoanSchedule ls = new LoanSchedule(i,
                    FxUtilsHandler.roundNumber((installment - intPerInstallment), ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(intPerInstallment, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(due, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(total_interest, ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber((P + total_interest), ROUND_DECIMAL_PLACES),
                    FxUtilsHandler.roundNumber(installment, ROUND_DECIMAL_PLACES));
            lst.add(ls);
        }
        return lst;
    }

    public static class LoanSchedule {

        private int inst_no;
        private double principal;
        private double interest;
        private double due_amt;
        private double total_interest;
        private double total_payment;
        private double installment_amount;

        public LoanSchedule() {
        }

        public LoanSchedule(int inst_no, double principal, double interest, double due_amt, double total_interest, double total_payment, double installment_amount) {
            this.inst_no = inst_no;
            this.principal = principal;
            this.interest = interest;
            this.due_amt = due_amt;
            this.total_interest = total_interest;
            this.total_payment = total_payment;
            this.installment_amount = installment_amount;
        }

        public int getInst_no() {
            return inst_no;
        }

        public void setInst_no(int inst_no) {
            this.inst_no = inst_no;
        }

        public double getPrincipal() {
            return principal;
        }

        public void setPrincipal(double principal) {
            this.principal = principal;
        }

        public double getInterest() {
            return interest;
        }

        public void setInterest(double interest) {
            this.interest = interest;
        }

        public double getDue_amt() {
            return due_amt;
        }

        public void setDue_amt(double due_amt) {
            this.due_amt = due_amt;
        }

        public double getTotal_interest() {
            return total_interest;
        }

        public void setTotal_interest(double total_interest) {
            this.total_interest = total_interest;
        }

        public double getTotal_payment() {
            return total_payment;
        }

        public void setTotal_payment(double total_payment) {
            this.total_payment = total_payment;
        }

        public double getInstallment_amount() {
            return installment_amount;
        }

        public void setInstallment_amount(double installment_amount) {
            this.installment_amount = installment_amount;
        }

    }
}
