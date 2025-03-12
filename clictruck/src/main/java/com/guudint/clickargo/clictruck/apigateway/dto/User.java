package com.guudint.clickargo.clictruck.apigateway.dto;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class User {
    private String id;
    private String name;
    private String email;
    private Token token;
    private Company company;

    public User() {
    }

    public User(String id, String name, String email, Token token, Company company) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
