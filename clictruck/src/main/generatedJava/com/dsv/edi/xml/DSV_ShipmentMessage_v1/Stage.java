//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.11.16 at 09:09:51 AM SGT 
//


package com.dsv.edi.xml.DSV_ShipmentMessage_v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Stage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Stage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ModeOfTransport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransportId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CarrierId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CarrierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransportType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransportMeansName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransportMeansNationality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stage", propOrder = {
    "modeOfTransport",
    "transportId",
    "carrierId",
    "carrierName",
    "transportType",
    "transportMeansName",
    "transportMeansNationality"
})
public class Stage {

    @XmlElement(name = "ModeOfTransport")
    protected String modeOfTransport;
    @XmlElement(name = "TransportId")
    protected String transportId;
    @XmlElement(name = "CarrierId")
    protected String carrierId;
    @XmlElement(name = "CarrierName")
    protected String carrierName;
    @XmlElement(name = "TransportType")
    protected String transportType;
    @XmlElement(name = "TransportMeansName")
    protected String transportMeansName;
    @XmlElement(name = "TransportMeansNationality")
    protected String transportMeansNationality;
    @XmlAttribute(name = "Type", required = true)
    protected String type;

    /**
     * Gets the value of the modeOfTransport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModeOfTransport() {
        return modeOfTransport;
    }

    /**
     * Sets the value of the modeOfTransport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModeOfTransport(String value) {
        this.modeOfTransport = value;
    }

    /**
     * Gets the value of the transportId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportId() {
        return transportId;
    }

    /**
     * Sets the value of the transportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportId(String value) {
        this.transportId = value;
    }

    /**
     * Gets the value of the carrierId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrierId() {
        return carrierId;
    }

    /**
     * Sets the value of the carrierId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrierId(String value) {
        this.carrierId = value;
    }

    /**
     * Gets the value of the carrierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrierName() {
        return carrierName;
    }

    /**
     * Sets the value of the carrierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrierName(String value) {
        this.carrierName = value;
    }

    /**
     * Gets the value of the transportType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportType() {
        return transportType;
    }

    /**
     * Sets the value of the transportType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportType(String value) {
        this.transportType = value;
    }

    /**
     * Gets the value of the transportMeansName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportMeansName() {
        return transportMeansName;
    }

    /**
     * Sets the value of the transportMeansName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportMeansName(String value) {
        this.transportMeansName = value;
    }

    /**
     * Gets the value of the transportMeansNationality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportMeansNationality() {
        return transportMeansNationality;
    }

    /**
     * Sets the value of the transportMeansNationality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportMeansNationality(String value) {
        this.transportMeansNationality = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
