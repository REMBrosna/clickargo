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
 * <p>Java class for Shipment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Shipment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShipmentId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dates" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}DateList" minOccurs="0"/>
 *         &lt;element name="TransportServices" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}TransportServiceList" minOccurs="0"/>
 *         &lt;element name="MonetaryAmounts" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}MonetaryAmountList" minOccurs="0"/>
 *         &lt;element name="FreeTextData" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}FreeTextList" minOccurs="0"/>
 *         &lt;element name="ControlTotals" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}ControlTotalList" minOccurs="0"/>
 *         &lt;element name="Documents" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}DocumentList" minOccurs="0"/>
 *         &lt;element name="Locations" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}LocationList" minOccurs="0"/>
 *         &lt;element name="TermsOfDelivery" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}TermsGroup" minOccurs="0"/>
 *         &lt;element name="References" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}ReferenceGroupList" minOccurs="0"/>
 *         &lt;element name="Requirements" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}RequirementList" minOccurs="0"/>
 *         &lt;element name="ChargeMethodology" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}ChargeMethodologyList" minOccurs="0"/>
 *         &lt;element name="ChargeRates" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}ChargeRatesList" minOccurs="0"/>
 *         &lt;element name="TransportStages" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}TransportStageList" minOccurs="0"/>
 *         &lt;element name="Parties" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}PartiesList" minOccurs="0"/>
 *         &lt;element name="GoodsItems" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}GoodsItemList" minOccurs="0"/>
 *         &lt;element name="EquipmentUnits" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}EquipmentUnitsList" minOccurs="0"/>
 *         &lt;element name="Orders" type="{http://edi.dsv.com/XML/Shipment_v1.xsd}OrdersList" minOccurs="0"/>
 *         &lt;element name="Milestones" type="{http://edi.dsv.com/XML/Order_v1.xsd}MilestoneList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Shipment", namespace = "http://edi.dsv.com/XML/Shipment_v1.xsd", propOrder = {
    "shipmentId",
    "dates",
    "transportServices",
    "monetaryAmounts",
    "freeTextData",
    "controlTotals",
    "documents",
    "locations",
    "termsOfDelivery",
    "references",
    "requirements",
    "chargeMethodology",
    "chargeRates",
    "transportStages",
    "parties",
    "goodsItems",
    "equipmentUnits",
    "orders",
    "milestones"
})
public class Shipment {

    @XmlElement(name = "ShipmentId")
    protected String shipmentId;
    @XmlElement(name = "Dates")
    protected DateList dates;
    @XmlElement(name = "TransportServices")
    protected TransportServiceList transportServices;
    @XmlElement(name = "MonetaryAmounts")
    protected MonetaryAmountList monetaryAmounts;
    @XmlElement(name = "FreeTextData")
    protected FreeTextList freeTextData;
    @XmlElement(name = "ControlTotals")
    protected ControlTotalList controlTotals;
    @XmlElement(name = "Documents")
    protected DocumentList documents;
    @XmlElement(name = "Locations")
    protected LocationList locations;
    @XmlElement(name = "TermsOfDelivery")
    protected TermsGroup termsOfDelivery;
    @XmlElement(name = "References")
    protected ReferenceGroupList references;
    @XmlElement(name = "Requirements")
    protected RequirementList requirements;
    @XmlElement(name = "ChargeMethodology")
    protected ChargeMethodologyList chargeMethodology;
    @XmlElement(name = "ChargeRates")
    protected ChargeRatesList chargeRates;
    @XmlElement(name = "TransportStages")
    protected TransportStageList transportStages;
    @XmlElement(name = "Parties")
    protected PartiesList parties;
    @XmlElement(name = "GoodsItems")
    protected GoodsItemList goodsItems;
    @XmlElement(name = "EquipmentUnits")
    protected EquipmentUnitsList equipmentUnits;
    @XmlElement(name = "Orders")
    protected OrdersList orders;
    @XmlElement(name = "Milestones")
    protected MilestoneList2 milestones;

    /**
     * Gets the value of the shipmentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShipmentId() {
        return shipmentId;
    }

    /**
     * Sets the value of the shipmentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShipmentId(String value) {
        this.shipmentId = value;
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
     * Gets the value of the transportServices property.
     * 
     * @return
     *     possible object is
     *     {@link TransportServiceList }
     *     
     */
    public TransportServiceList getTransportServices() {
        return transportServices;
    }

    /**
     * Sets the value of the transportServices property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportServiceList }
     *     
     */
    public void setTransportServices(TransportServiceList value) {
        this.transportServices = value;
    }

    /**
     * Gets the value of the monetaryAmounts property.
     * 
     * @return
     *     possible object is
     *     {@link MonetaryAmountList }
     *     
     */
    public MonetaryAmountList getMonetaryAmounts() {
        return monetaryAmounts;
    }

    /**
     * Sets the value of the monetaryAmounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonetaryAmountList }
     *     
     */
    public void setMonetaryAmounts(MonetaryAmountList value) {
        this.monetaryAmounts = value;
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
     * Gets the value of the controlTotals property.
     * 
     * @return
     *     possible object is
     *     {@link ControlTotalList }
     *     
     */
    public ControlTotalList getControlTotals() {
        return controlTotals;
    }

    /**
     * Sets the value of the controlTotals property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlTotalList }
     *     
     */
    public void setControlTotals(ControlTotalList value) {
        this.controlTotals = value;
    }

    /**
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentList }
     *     
     */
    public DocumentList getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentList }
     *     
     */
    public void setDocuments(DocumentList value) {
        this.documents = value;
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
     * Gets the value of the termsOfDelivery property.
     * 
     * @return
     *     possible object is
     *     {@link TermsGroup }
     *     
     */
    public TermsGroup getTermsOfDelivery() {
        return termsOfDelivery;
    }

    /**
     * Sets the value of the termsOfDelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link TermsGroup }
     *     
     */
    public void setTermsOfDelivery(TermsGroup value) {
        this.termsOfDelivery = value;
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
     * Gets the value of the requirements property.
     * 
     * @return
     *     possible object is
     *     {@link RequirementList }
     *     
     */
    public RequirementList getRequirements() {
        return requirements;
    }

    /**
     * Sets the value of the requirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequirementList }
     *     
     */
    public void setRequirements(RequirementList value) {
        this.requirements = value;
    }

    /**
     * Gets the value of the chargeMethodology property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeMethodologyList }
     *     
     */
    public ChargeMethodologyList getChargeMethodology() {
        return chargeMethodology;
    }

    /**
     * Sets the value of the chargeMethodology property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeMethodologyList }
     *     
     */
    public void setChargeMethodology(ChargeMethodologyList value) {
        this.chargeMethodology = value;
    }

    /**
     * Gets the value of the chargeRates property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeRatesList }
     *     
     */
    public ChargeRatesList getChargeRates() {
        return chargeRates;
    }

    /**
     * Sets the value of the chargeRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeRatesList }
     *     
     */
    public void setChargeRates(ChargeRatesList value) {
        this.chargeRates = value;
    }

    /**
     * Gets the value of the transportStages property.
     * 
     * @return
     *     possible object is
     *     {@link TransportStageList }
     *     
     */
    public TransportStageList getTransportStages() {
        return transportStages;
    }

    /**
     * Sets the value of the transportStages property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportStageList }
     *     
     */
    public void setTransportStages(TransportStageList value) {
        this.transportStages = value;
    }

    /**
     * Gets the value of the parties property.
     * 
     * @return
     *     possible object is
     *     {@link PartiesList }
     *     
     */
    public PartiesList getParties() {
        return parties;
    }

    /**
     * Sets the value of the parties property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartiesList }
     *     
     */
    public void setParties(PartiesList value) {
        this.parties = value;
    }

    /**
     * Gets the value of the goodsItems property.
     * 
     * @return
     *     possible object is
     *     {@link GoodsItemList }
     *     
     */
    public GoodsItemList getGoodsItems() {
        return goodsItems;
    }

    /**
     * Sets the value of the goodsItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link GoodsItemList }
     *     
     */
    public void setGoodsItems(GoodsItemList value) {
        this.goodsItems = value;
    }

    /**
     * Gets the value of the equipmentUnits property.
     * 
     * @return
     *     possible object is
     *     {@link EquipmentUnitsList }
     *     
     */
    public EquipmentUnitsList getEquipmentUnits() {
        return equipmentUnits;
    }

    /**
     * Sets the value of the equipmentUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link EquipmentUnitsList }
     *     
     */
    public void setEquipmentUnits(EquipmentUnitsList value) {
        this.equipmentUnits = value;
    }

    /**
     * Gets the value of the orders property.
     * 
     * @return
     *     possible object is
     *     {@link OrdersList }
     *     
     */
    public OrdersList getOrders() {
        return orders;
    }

    /**
     * Sets the value of the orders property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrdersList }
     *     
     */
    public void setOrders(OrdersList value) {
        this.orders = value;
    }

    /**
     * Gets the value of the milestones property.
     * 
     * @return
     *     possible object is
     *     {@link MilestoneList2 }
     *     
     */
    public MilestoneList2 getMilestones() {
        return milestones;
    }

    /**
     * Sets the value of the milestones property.
     * 
     * @param value
     *     allowed object is
     *     {@link MilestoneList2 }
     *     
     */
    public void setMilestones(MilestoneList2 value) {
        this.milestones = value;
    }

}
