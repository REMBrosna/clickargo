//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.11.16 at 09:09:51 AM SGT 
//


package com.dsv.edi.xml.DSV_ShipmentMessage_v1;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EquipmentUnit complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EquipmentUnit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EquipmentType" type="{http://edi.dsv.com/XML/SharedElements_v1.xsd}EquipmentType" minOccurs="0"/>
 *         &lt;element name="NumberOfUnits" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="Measurements" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}MeasurementsList" minOccurs="0"/>
 *         &lt;element name="Dimensions" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}DimensionsList" minOccurs="0"/>
 *         &lt;element name="SealIds" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}SealList" minOccurs="0"/>
 *         &lt;element name="FreeTextData" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}FreeTextList" minOccurs="0"/>
 *         &lt;element name="References" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}ReferenceGroupList" minOccurs="0"/>
 *         &lt;element name="Dates" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}DateList" minOccurs="0"/>
 *         &lt;element name="TransportMovement" type="{http://edi.dsv.com/XML/SharedElements_v1.xsd}TransportMovement" minOccurs="0"/>
 *         &lt;element name="TemperatureControl" type="{http://edi.dsv.com/XML/SharedElements_v1.xsd}TemperatureControl" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EquipmentUnit", namespace = "http://edi.dsv.com/XML/Shipment_v1.xsd", propOrder = {
    "equipmentType",
    "numberOfUnits",
    "measurements",
    "dimensions",
    "sealIds",
    "freeTextData",
    "references",
    "dates",
    "transportMovement",
    "temperatureControl"
})
public class EquipmentUnit {

    @XmlElement(name = "EquipmentType")
    protected EquipmentType equipmentType;
    @XmlElement(name = "NumberOfUnits")
    protected BigDecimal numberOfUnits;
    @XmlElement(name = "Measurements")
    protected MeasurementsList measurements;
    @XmlElement(name = "Dimensions")
    protected DimensionsList dimensions;
    @XmlElement(name = "SealIds")
    protected SealList sealIds;
    @XmlElement(name = "FreeTextData")
    protected FreeTextList freeTextData;
    @XmlElement(name = "References")
    protected ReferenceGroupList references;
    @XmlElement(name = "Dates")
    protected DateList dates;
    @XmlElement(name = "TransportMovement")
    protected TransportMovement transportMovement;
    @XmlElement(name = "TemperatureControl")
    protected TemperatureControl temperatureControl;

    /**
     * Gets the value of the equipmentType property.
     * 
     * @return
     *     possible object is
     *     {@link EquipmentType }
     *     
     */
    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    /**
     * Sets the value of the equipmentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquipmentType }
     *     
     */
    public void setEquipmentType(EquipmentType value) {
        this.equipmentType = value;
    }

    /**
     * Gets the value of the numberOfUnits property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberOfUnits() {
        return numberOfUnits;
    }

    /**
     * Sets the value of the numberOfUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberOfUnits(BigDecimal value) {
        this.numberOfUnits = value;
    }

    /**
     * Gets the value of the measurements property.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementsList }
     *     
     */
    public MeasurementsList getMeasurements() {
        return measurements;
    }

    /**
     * Sets the value of the measurements property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementsList }
     *     
     */
    public void setMeasurements(MeasurementsList value) {
        this.measurements = value;
    }

    /**
     * Gets the value of the dimensions property.
     * 
     * @return
     *     possible object is
     *     {@link DimensionsList }
     *     
     */
    public DimensionsList getDimensions() {
        return dimensions;
    }

    /**
     * Sets the value of the dimensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link DimensionsList }
     *     
     */
    public void setDimensions(DimensionsList value) {
        this.dimensions = value;
    }

    /**
     * Gets the value of the sealIds property.
     * 
     * @return
     *     possible object is
     *     {@link SealList }
     *     
     */
    public SealList getSealIds() {
        return sealIds;
    }

    /**
     * Sets the value of the sealIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link SealList }
     *     
     */
    public void setSealIds(SealList value) {
        this.sealIds = value;
    }

    /**
     * Gets the value of the freeTextData property.
     * 
     * @return
     *     possible object is
     *     {@link FreeTextList }
     *     
     */
    public FreeTextList getFreeTextData() {
        return freeTextData;
    }

    /**
     * Sets the value of the freeTextData property.
     * 
     * @param value
     *     allowed object is
     *     {@link FreeTextList }
     *     
     */
    public void setFreeTextData(FreeTextList value) {
        this.freeTextData = value;
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

    /**
     * Gets the value of the dates property.
     * 
     * @return
     *     possible object is
     *     {@link DateList }
     *     
     */
    public DateList getDates() {
        return dates;
    }

    /**
     * Sets the value of the dates property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateList }
     *     
     */
    public void setDates(DateList value) {
        this.dates = value;
    }

    /**
     * Gets the value of the transportMovement property.
     * 
     * @return
     *     possible object is
     *     {@link TransportMovement }
     *     
     */
    public TransportMovement getTransportMovement() {
        return transportMovement;
    }

    /**
     * Sets the value of the transportMovement property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportMovement }
     *     
     */
    public void setTransportMovement(TransportMovement value) {
        this.transportMovement = value;
    }

    /**
     * Gets the value of the temperatureControl property.
     * 
     * @return
     *     possible object is
     *     {@link TemperatureControl }
     *     
     */
    public TemperatureControl getTemperatureControl() {
        return temperatureControl;
    }

    /**
     * Sets the value of the temperatureControl property.
     * 
     * @param value
     *     allowed object is
     *     {@link TemperatureControl }
     *     
     */
    public void setTemperatureControl(TemperatureControl value) {
        this.temperatureControl = value;
    }

}
