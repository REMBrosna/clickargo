//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.11.16 at 09:09:51 AM SGT 
//


package com.dsv.edi.xml.DSV_ShipmentMessage_v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PackageId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstructionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Marks" type="{http://edi.dsv.com/XML/SharedElements_v1.xsd}Marks" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageId", propOrder = {
    "instructionCode",
    "marks"
})
public class PackageId {

    @XmlElement(name = "InstructionCode", required = true)
    protected String instructionCode;
    @XmlElement(name = "Marks")
    protected Marks marks;

    /**
     * Gets the value of the instructionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstructionCode() {
        return instructionCode;
    }

    /**
     * Sets the value of the instructionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstructionCode(String value) {
        this.instructionCode = value;
    }

    /**
     * Gets the value of the marks property.
     * 
     * @return
     *     possible object is
     *     {@link Marks }
     *     
     */
    public Marks getMarks() {
        return marks;
    }

    /**
     * Sets the value of the marks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Marks }
     *     
     */
    public void setMarks(Marks value) {
        this.marks = value;
    }

}
