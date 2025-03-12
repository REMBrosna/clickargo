package com.guudint.clickargo.clictruck.admin.shell.dto.response;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionDetailsResponseDto {

    private String txnId;
    private String txnCardPan;
    private String txnProductName;
    private Date txnDateTime;
    private BigDecimal txnQty;
    private BigDecimal txnInvNetAmount;
    private BigDecimal txnInvGrossAmount;
    private BigDecimal txnCustomerRetailValueTotalNet;
    private BigDecimal txnCustomerRetailValueTotalGross;

    public TransactionDetailsResponseDto(String txnId, String txnCardPan, String txnProductName, Date txnDateTime, BigDecimal txnQty, BigDecimal txnInvNetAmount, BigDecimal txnInvGrossAmount, BigDecimal txnCustomerRetailValueTotalNet, BigDecimal txnCustomerRetailValueTotalGross) {
        this.txnId = txnId;
        this.txnCardPan = txnCardPan;
        this.txnProductName = txnProductName;
        this.txnDateTime = txnDateTime;
        this.txnQty = txnQty;
        this.txnInvNetAmount = txnInvNetAmount;
        this.txnInvGrossAmount = txnInvGrossAmount;
        this.txnCustomerRetailValueTotalNet = txnCustomerRetailValueTotalNet;
        this.txnCustomerRetailValueTotalGross = txnCustomerRetailValueTotalGross;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnCardPan() {
        return txnCardPan;
    }

    public void setTxnCardPan(String txnCardPan) {
        this.txnCardPan = txnCardPan;
    }

    public String getTxnProductName() {
        return txnProductName;
    }

    public void setTxnProductName(String txnProductName) {
        this.txnProductName = txnProductName;
    }

    public Date getTxnDateTime() {
        return txnDateTime;
    }

    public void setTxnDateTime(Date txnDateTime) {
        this.txnDateTime = txnDateTime;
    }

    public BigDecimal getTxnQty() {
        return txnQty;
    }

    public void setTxnQty(BigDecimal txnQty) {
        this.txnQty = txnQty;
    }

    public BigDecimal getTxnInvNetAmount() {
        return txnInvNetAmount;
    }

    public void setTxnInvNetAmount(BigDecimal txnInvNetAmount) {
        this.txnInvNetAmount = txnInvNetAmount;
    }

    public BigDecimal getTxnInvGrossAmount() {
        return txnInvGrossAmount;
    }

    public void setTxnInvGrossAmount(BigDecimal txnInvGrossAmount) {
        this.txnInvGrossAmount = txnInvGrossAmount;
    }

    public BigDecimal getTxnCustomerRetailValueTotalNet() {
        return txnCustomerRetailValueTotalNet;
    }

    public void setTxnCustomerRetailValueTotalNet(BigDecimal txnCustomerRetailValueTotalNet) {
        this.txnCustomerRetailValueTotalNet = txnCustomerRetailValueTotalNet;
    }

    public BigDecimal getTxnCustomerRetailValueTotalGross() {
        return txnCustomerRetailValueTotalGross;
    }

    public void setTxnCustomerRetailValueTotalGross(BigDecimal txnCustomerRetailValueTotalGross) {
        this.txnCustomerRetailValueTotalGross = txnCustomerRetailValueTotalGross;
    }
}
