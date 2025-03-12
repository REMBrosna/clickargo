package com.guudint.clickargo.clictruck.apigateway.dto;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Epod {
    private String id;
    private Company to;
    private Company co;
    private Company ff;

    public Epod() {
    }

    public Epod(String id, Company to, Company co, Company ff) {
        this.id = id;
        this.to = to;
        this.co = co;
        this.ff = ff;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Company getTo() {
        return to;
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

