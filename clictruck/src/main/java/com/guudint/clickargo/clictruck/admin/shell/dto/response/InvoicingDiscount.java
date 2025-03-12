package com.guudint.clickargo.clictruck.admin.shell.dto.response;

import java.math.BigDecimal;

public class InvoicingDiscount {

    private String accn;
    private Discount discount;

    public InvoicingDiscount() {}

    public InvoicingDiscount(String accn, Discount discount) {
        this.accn = accn;
        this.discount = discount;
    }

    public String getAccn() {
        return accn;
    }

    public void setAccn(String accn) {
        this.accn = accn;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    // Inner class for Discount
    public static class Discount {
        private BigDecimal petrol;
        private BigDecimal dissels;
        private BigDecimal lubricant;
        private BigDecimal electric;

        // Constructors
        public Discount() {}

        public Discount(BigDecimal petrol, BigDecimal dissels, BigDecimal lubricant, BigDecimal electric) {
            this.petrol = petrol;
            this.dissels = dissels;
            this.lubricant = lubricant;
            this.electric = electric;
        }

        // Getters and Setters
        public BigDecimal getPetrol() {
            return petrol;
        }

        public void setPetrol(BigDecimal petrol) {
            this.petrol = petrol;
        }

        public BigDecimal getDissels() {
            return dissels;
        }

        public void setDissels(BigDecimal dissels) {
            this.dissels = dissels;
        }

        public BigDecimal getLubricant() {
            return lubricant;
        }

        public void setLubricant(BigDecimal lubricant) {
            this.lubricant = lubricant;
        }

        public BigDecimal getElectric() {
            return electric;
        }

        public void setElectric(BigDecimal electric) {
            this.electric = electric;
        }
    }
}
