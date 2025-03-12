//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.11.21 at 09:24:47 AM SGT 
//


package com.dsv.edi.xml.DSV_StatusMessage_v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransportService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransportService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackagingState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PriorityCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CargoType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportService", propOrder = {
    "serviceCode",
    "packagingState",
    "priorityCode",
    "cargoType"
})
public class TransportService {

    @XmlElement(name = "ServiceCode")
    protected String serviceCode;
    @XmlElement(name = "PackagingState")
    protected String packagingState;
    @XmlElement(name = "PriorityCode")
    protected String priorityCode;
    @XmlElement(name = "CargoType")
    protected String cargoType;

    /**
     * Gets the value of the serviceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * Sets the value of the serviceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCode(String value) {
        this.serviceCode = value;
    }

    /**
     * Gets the value of the packagingState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackagingState() {
        return packagingState;
    }

    /**
     * Sets the value of the packagingState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackagingState(String value) {
        this.packagingState = value;
    }

    /**
     * Gets the value of the priorityCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriorityCode() {
        return priorityCode;
    }

    /**
     * Sets the value of the priorityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorityCode(String value) {
        this.priorityCode = value;
    }

    /**
     * Gets the value of the cargoType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCargoType() {
        return cargoType;
    }

    /**
     * Sets the value of the cargoType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCargoType(String value) {
        this.cargoType = value;
    }

}
