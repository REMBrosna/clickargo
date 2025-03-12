package com.guudint.clickargo.clictruck.finacing.dto;

public class TripDoDetail {
    
    public TripDoDetail(String doNumber, String doDocument, String pod) {
        this.doNumber = doNumber;
        this.doDocument = doDocument;
        this.pod = pod;
    }

    private String doNumber, doDocument, pod;

    public TripDoDetail() {
    }

    public String getDoNumber() {
        return doNumber;
    }

    public void setDoNumber(String doNumber) {
        this.doNumber = doNumber;
    }

    public String getDoDocument() {
        return doDocument;
    }

    public void setDoDocument(String doDocument) {
        this.doDocument = doDocument;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }
}
