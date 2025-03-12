package com.guudint.clickargo.clictruck.admin.shell.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PriceTransactionRequestDTO {

    @JsonProperty("InvoiceStatus")
    private String invoiceStatus;

    @JsonProperty("IncludeFees")
    private boolean includeFees;

    @JsonProperty("FromDate")
    private String fromDate;

    @JsonProperty("ToDate")
    private String toDate;

    @JsonProperty("PageSize")
    private int pageSize;

    @JsonProperty("CurrentPage")
    private int currentPage;

    @JsonProperty("ColCoCode")
    private int colCoCode;

    @JsonProperty("PayerNumber")
    private String payerNumber;

    @JsonProperty("AccountNumber")
    private String accountNumber;

    @JsonProperty("RequestId")
    private String requestId;

    public PriceTransactionRequestDTO(String fromDate, String toDate, ShellCredentialConfigDto config) {
        this.invoiceStatus = "A";
        this.includeFees = true;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.pageSize = 300;
        this.currentPage = 1;
        this.colCoCode = config.getColcocode1();
        this.payerNumber = config.getPayerNumber1();
        this.accountNumber = config.getAccountNumber1();
        this.requestId = UUID.randomUUID().toString();
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public boolean isIncludeFees() {
        return includeFees;
    }

    public void setIncludeFees(boolean includeFees) {
        this.includeFees = includeFees;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getColCoCode() {
        return colCoCode;
    }

    public void setColCoCode(int colCoCode) {
        this.colCoCode = colCoCode;
    }

    public String getPayerNumber() {
        return payerNumber;
    }

    public void setPayerNumber(String payerNumber) {
        this.payerNumber = payerNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
