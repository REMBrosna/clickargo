package com.guudint.clickargo.clictruck.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Schema {
    @JsonProperty("ocean_bl")
    private String oceanBl;
    private String hbl;
    private String hawb;
    private String eta;
    private String etd;
    private String vessel;
    private String voyage;
    private String pod;
    private String pol;
    @JsonProperty("eta_sin")
    private String etaSin;
    @JsonProperty("eta_pod")
    private String etaPod;
    @JsonProperty("closing_datetime")
    private String closingDatetime;
    @JsonProperty("carrier_ref")
    private String carrierRef;
    private String carrier;
    @JsonProperty("booking_no")
    private String bookingNo;
    private String tailgate;
    private String labour;
    private String disposal;
    private String uncrating;
    @JsonProperty("prepared_by")
    private String preparedBy;
    private String attn;
    private String phone;
    private String fax;
    private List<Container> containers;

    public Schema() {
    }

    public Schema(String oceanBl, String hbl, String hawb, String eta, String etd, String vessel, String voyage, String pod, String pol, String etaSin, String etaPod, String closingDatetime, String carrierRef, String carrier, String bookingNo, String tailgate, String labour, String disposal, String uncrating, String preparedBy, String attn, String phone, String fax, List<Container> containers) {
        this.oceanBl = oceanBl;
        this.hbl = hbl;
        this.hawb = hawb;
        this.eta = eta;
        this.etd = etd;
        this.vessel = vessel;
        this.voyage = voyage;
        this.pod = pod;
        this.pol = pol;
        this.etaSin = etaSin;
        this.etaPod = etaPod;
        this.closingDatetime = closingDatetime;
        this.carrierRef = carrierRef;
        this.carrier = carrier;
        this.bookingNo = bookingNo;
        this.tailgate = tailgate;
        this.labour = labour;
        this.disposal = disposal;
        this.uncrating = uncrating;
        this.preparedBy = preparedBy;
        this.attn = attn;
        this.phone = phone;
        this.fax = fax;
        this.containers = containers;
    }

    public String getOceanBl() {
        return oceanBl;
    }

    public void setOceanBl(String oceanBl) {
        this.oceanBl = oceanBl;
    }

    public String getHbl() {
        return hbl;
    }

    public void setHbl(String hbl) {
        this.hbl = hbl;
    }

    public String getHawb() {
        return hawb;
    }

    public void setHawb(String hawb) {
        this.hawb = hawb;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getVoyage() {
        return voyage;
    }

    public void setVoyage(String voyage) {
        this.voyage = voyage;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public String getPol() {
        return pol;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public String getEtaSin() {
        return etaSin;
    }

    public void setEtaSin(String etaSin) {
        this.etaSin = etaSin;
    }

    public String getEtaPod() {
        return etaPod;
    }

    public void setEtaPod(String etaPod) {
        this.etaPod = etaPod;
    }

    public String getClosingDatetime() {
        return closingDatetime;
    }

    public void setClosingDatetime(String closingDatetime) {
        this.closingDatetime = closingDatetime;
    }

    public String getCarrierRef() {
        return carrierRef;
    }

    public void setCarrierRef(String carrierRef) {
        this.carrierRef = carrierRef;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getTailgate() {
        return tailgate;
    }

    public void setTailgate(String tailgate) {
        this.tailgate = tailgate;
    }

    public String getLabour() {
        return labour;
    }

    public void setLabour(String labour) {
        this.labour = labour;
    }

    public String getDisposal() {
        return disposal;
    }

    public void setDisposal(String disposal) {
        this.disposal = disposal;
    }

    public String getUncrating() {
        return uncrating;
    }

    public void setUncrating(String uncrating) {
        this.uncrating = uncrating;
    }

    public String getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
    }

    public String getAttn() {
        return attn;
    }

    public void setAttn(String attn) {
        this.attn = attn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }
}
