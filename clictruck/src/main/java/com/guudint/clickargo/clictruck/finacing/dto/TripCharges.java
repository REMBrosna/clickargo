package com.guudint.clickargo.clictruck.finacing.dto;

import java.math.BigDecimal;

public class TripCharges {

    private BigDecimal amount;
    private char openPrice;

    public TripCharges() {
    }

    public TripCharges(BigDecimal amount, char openPrice) {
        this.amount = amount;
        this.openPrice = openPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public char getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(char openPrice) {
        this.openPrice = openPrice;
    }
}
