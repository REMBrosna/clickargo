//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.11.16 at 09:09:51 AM SGT 
//


package com.dsv.edi.xml.DSV_ShipmentMessage_v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Party complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Party">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PartyDetails" type="{http://edi.dsv.com/XML/SharedElements_v1.xsd}PartyDetails" minOccurs="0"/>
 *         &lt;element name="Locations" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}LocationList" minOccurs="0"/>
 *         &lt;element name="Contact" type="{http://edi.dsv.com/XML/SharedElements_v1.xsd}Contact" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="References" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}ReferenceGroupList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Party", namespace = "http://edi.dsv.com/XML/Shipment_v1.xsd", propOrder = {
    "partyDetails",
    "locations",
    "contact",
    "references"
})
public class Party {

    @XmlElement(name = "PartyDetails")
    protected PartyDetails partyDetails;
    @XmlElement(name = "Locations")
    protected LocationList locations;
    @XmlElement(name = "Contact")
    protected List<Contact> contact;
    @XmlElement(name = "References")
    protected ReferenceGroupList references;

    /**
     * Gets the value of the partyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PartyDetails }
     *     
     */
    public PartyDetails getPartyDetails() {
        return partyDetails;
    }

    /**
     * Sets the value of the partyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyDetails }
     *     
     */
    public void setPartyDetails(PartyDetails value) {
        this.partyDetails = value;
    }

    /**
     * Gets the value of the locations property.
     * 
     * @return
     *     possible object is
     *     {@link LocationList }
     *     
     */
    public LocationList getLocations() {
        return locations;
    }

    /**
     * Sets the value of the locations property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationList }
     *     
     */
    public void setLocations(LocationList value) {
        this.locations = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Contact }
     * 
     * 
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the references property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceGroupList }
     *     
     */
    public ReferenceGroupList getReferences() {
        return references;
    }

    /**
     * Sets the value of the references property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceGroupList }
     *     
     */
    public void setReferences(ReferenceGroupList value) {
        this.references = value;
    }

}
