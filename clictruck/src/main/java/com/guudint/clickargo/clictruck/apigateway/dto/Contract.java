package com.guudint.clickargo.clictruck.apigateway.dto;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Contract {
    private String contractId;
    private String contractName;
    private Company to;
    private Company co;
    private Company ff;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Company getTo() {
        return to;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public void setTo(Company to) {
        this.to = to;
    }

    public Company getCo() {
        return co;
    }

    public void setCo(Company co) {
        this.co = co;
    }

    public Company getFf() {
        return ff;
    }

    public void setFf(Company ff) {
        this.ff = ff;
    }
}

