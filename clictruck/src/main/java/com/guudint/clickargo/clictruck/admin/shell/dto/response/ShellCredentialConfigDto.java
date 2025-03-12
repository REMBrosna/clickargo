package com.guudint.clickargo.clictruck.admin.shell.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ShellCredentialConfigDto implements Serializable {

    private static final long serialVersionUID = -1846466223896088998L;

    @JsonProperty("url")
    private String url;

    @JsonProperty("secondaryUrl")
    private String secondaryUrl;

    @JsonProperty("apikey")
    private String apikey;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("apikeyT1")
    private String apikeyT1;

    @JsonProperty("secretT1")
    private String secretT1;

    @JsonProperty("colcocode")
    private int colcocode;

    @JsonProperty("colcoid")
    private int colcoid;

    @JsonProperty("colcocode1")
    private int colcocode1;

    @JsonProperty("colcoid1")
    private int colcoid1;

    @JsonProperty("payer_number")
    private String payerNumber;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("payer_number1")
    private String payerNumber1;

    @JsonProperty("payer_number2")
    private String payerNumber2;

    @JsonProperty("account_number1")
    private String accountNumber1;

    public ShellCredentialConfigDto() {
    }

    public ShellCredentialConfigDto(String url, String secondaryUrl, String apikey, String secret, String apikeyT1, String secretT1, int colcocode, int colcoid, int colcocode1, int colcoid1, String payerNumber, String accountNumber, String payerNumber1, String payerNumber2, String accountNumber1) {
        this.url = url;
        this.secondaryUrl = secondaryUrl;
        this.apikey = apikey;
        this.secret = secret;
        this.apikeyT1 = apikeyT1;
        this.secretT1 = secretT1;
        this.colcocode = colcocode;
        this.colcoid = colcoid;
        this.colcocode1 = colcocode1;
        this.colcoid1 = colcoid1;
        this.payerNumber = payerNumber;
        this.accountNumber = accountNumber;
        this.payerNumber1 = payerNumber1;
        this.payerNumber2 = payerNumber2;
        this.accountNumber1 = accountNumber1;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getColcocode() {
        return colcocode;
    }

    public void setColcocode(int colcocode) {
        this.colcocode = colcocode;
    }

    public int getColcoid() {
        return colcoid;
    }

    public void setColcoid(int colcoid) {
        this.colcoid = colcoid;
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

    public String getSecondaryUrl() {
        return secondaryUrl;
    }

    public void setSecondaryUrl(String secondaryUrl) {
        this.secondaryUrl = secondaryUrl;
    }

    public String getApikeyT1() {
        return apikeyT1;
    }

    public void setApikeyT1(String apikeyT1) {
        this.apikeyT1 = apikeyT1;
    }

    public String getSecretT1() {
        return secretT1;
    }

    public void setSecretT1(String secretT1) {
        this.secretT1 = secretT1;
    }

    public int getColcocode1() {
        return colcocode1;
    }

    public void setColcocode1(int colcocode1) {
        this.colcocode1 = colcocode1;
    }

    public int getColcoid1() {
        return colcoid1;
    }

    public void setColcoid1(int colcoid1) {
        this.colcoid1 = colcoid1;
    }

    public String getPayerNumber1() {
        return payerNumber1;
    }

    public void setPayerNumber1(String payerNumber1) {
        this.payerNumber1 = payerNumber1;
    }

    public String getPayerNumber2() {
        return payerNumber2;
    }

    public void setPayerNumber2(String payerNumber2) {
        this.payerNumber2 = payerNumber2;
    }

    public String getAccountNumber1() {
        return accountNumber1;
    }

    public void setAccountNumber1(String accountNumber1) {
        this.accountNumber1 = accountNumber1;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
