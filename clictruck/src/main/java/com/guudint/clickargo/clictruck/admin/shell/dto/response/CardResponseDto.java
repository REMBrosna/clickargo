package com.guudint.clickargo.clictruck.admin.shell.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardResponseDto {

    @JsonProperty("AccountId")
    private long accountId;

    @JsonProperty("AccountName")
    private String accountName;

    @JsonProperty("AccountNumber")
    private String accountNumber;

    @JsonProperty("AccountShortName")
    private String accountShortName;

    @JsonProperty("BundleId")
    private Long bundleId; // Nullable

    @JsonProperty("CardBlockSchedules")
    private Object cardBlockSchedules;

    @JsonProperty("CardGroupId")
    private Long cardGroupId;

    @JsonProperty("CardGroupName")
    private String cardGroupName;

    @JsonProperty("CardId")
    private long cardId;

    @JsonProperty("CardTypeCode")
    private String cardTypeCode;

    @JsonProperty("CardTypeId")
    private int cardTypeId;

    @JsonProperty("CardTypeName")
    private String cardTypeName;

    @JsonProperty("ColCoCountryCode")
    private String colCoCountryCode;

    @JsonProperty("CreationDate")
    private String creationDate;

    @JsonProperty("DriverName")
    private String driverName;

    @JsonProperty("EffectiveDate")
    private String effectiveDate;

    @JsonProperty("ExpiryDate")
    private String expiryDate;

    @JsonProperty("FleetIdInput")
    private boolean fleetIdInput;

    @JsonProperty("IsCRT")
    private boolean isCRT;

    @JsonProperty("IsFleet")
    private boolean isFleet;

    @JsonProperty("IsInternational")
    private boolean isInternational;

    @JsonProperty("IsNational")
    private boolean isNational;

    @JsonProperty("IsPartnerSitesIncluded")
    private boolean isPartnerSitesIncluded;

    @JsonProperty("IsShellSitesOnly")
    private boolean isShellSitesOnly;

    @JsonProperty("IssueDate")
    private String issueDate;

    @JsonProperty("IsSuperseded")
    private boolean isSuperseded;

    @JsonProperty("IsVirtualCard")
    private boolean isVirtualCard;

    @JsonProperty("LastModifiedDate")
    private String lastModifiedDate;

    @JsonProperty("LastUsedDate")
    private String lastUsedDate; // Nullable

    @JsonProperty("LocalCurrencyCode")
    private String localCurrencyCode;

    @JsonProperty("LocalCurrencySymbol")
    private String localCurrencySymbol;

    @JsonProperty("OdometerInput")
    private boolean odometerInput;

    @JsonProperty("PAN")
    private String pan;

    @JsonProperty("MaskedPAN")
    private String maskedPAN; // Nullable

    @JsonProperty("PANID")
    private long panId;

    @JsonProperty("PurchaseCategoryCode")
    private String purchaseCategoryCode;

    @JsonProperty("PurchaseCategoryId")
    private int purchaseCategoryId;

    @JsonProperty("PurchaseCategoryName")
    private String purchaseCategoryName;

    @JsonProperty("Reason")
    private String reason;

    @JsonProperty("ReissueSetting")
    private String reissueSetting;

    @JsonProperty("StatusDescription")
    private String statusDescription;

    @JsonProperty("StatusId")
    private int statusId;

    @JsonProperty("TokenTypeID")
    private int tokenTypeID;

    @JsonProperty("TokenTypeName")
    private String tokenTypeName;

    @JsonProperty("VRN")
    private String vrn;

    @JsonProperty("ClientReferenceId")
    private String clientReferenceId; // Nullable

    @JsonProperty("IsEMVContact")
    private boolean isEMVContact;

    @JsonProperty("IsEMVContactless")
    private boolean isEMVContactless;

    @JsonProperty("IsRFID")
    private boolean isRFID;

    @JsonProperty("RFIDUID")
    private String rfiduid;

    @JsonProperty("EMAID")
    private String emaid;

    @JsonProperty("EVPrintedNumber")
    private String evPrintedNumber;

    @JsonProperty("CardMediaCode")
    private String cardMediaCode;

    @JsonProperty("MediumTypeID")
    private int mediumTypeID;

    @JsonProperty("MediumType")
    private String mediumType;

    public CardResponseDto() {
    }

    public CardResponseDto(long accountId, String accountName, String accountNumber, String accountShortName, Long bundleId, Object cardBlockSchedules, Long cardGroupId, String cardGroupName, long cardId, String cardTypeCode, int cardTypeId, String cardTypeName, String colCoCountryCode, String creationDate, String driverName, String effectiveDate, String expiryDate, boolean fleetIdInput, boolean isCRT, boolean isFleet, boolean isInternational, boolean isNational, boolean isPartnerSitesIncluded, boolean isShellSitesOnly, String issueDate, boolean isSuperseded, boolean isVirtualCard, String lastModifiedDate, String lastUsedDate, String localCurrencyCode, String localCurrencySymbol, boolean odometerInput, String pan, String maskedPAN, long panId, String purchaseCategoryCode, int purchaseCategoryId, String purchaseCategoryName, String reason, String reissueSetting, String statusDescription, int statusId, int tokenTypeID, String tokenTypeName, String vrn, String clientReferenceId, boolean isEMVContact, boolean isEMVContactless, boolean isRFID, String rfiduid, String emaid, String evPrintedNumber, String cardMediaCode, int mediumTypeID, String mediumType) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.accountShortName = accountShortName;
        this.bundleId = bundleId;
        this.cardBlockSchedules = cardBlockSchedules;
        this.cardGroupId = cardGroupId;
        this.cardGroupName = cardGroupName;
        this.cardId = cardId;
        this.cardTypeCode = cardTypeCode;
        this.cardTypeId = cardTypeId;
        this.cardTypeName = cardTypeName;
        this.colCoCountryCode = colCoCountryCode;
        this.creationDate = creationDate;
        this.driverName = driverName;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.fleetIdInput = fleetIdInput;
        this.isCRT = isCRT;
        this.isFleet = isFleet;
        this.isInternational = isInternational;
        this.isNational = isNational;
        this.isPartnerSitesIncluded = isPartnerSitesIncluded;
        this.isShellSitesOnly = isShellSitesOnly;
        this.issueDate = issueDate;
        this.isSuperseded = isSuperseded;
        this.isVirtualCard = isVirtualCard;
        this.lastModifiedDate = lastModifiedDate;
        this.lastUsedDate = lastUsedDate;
        this.localCurrencyCode = localCurrencyCode;
        this.localCurrencySymbol = localCurrencySymbol;
        this.odometerInput = odometerInput;
        this.pan = pan;
        this.maskedPAN = maskedPAN;
        this.panId = panId;
        this.purchaseCategoryCode = purchaseCategoryCode;
        this.purchaseCategoryId = purchaseCategoryId;
        this.purchaseCategoryName = purchaseCategoryName;
        this.reason = reason;
        this.reissueSetting = reissueSetting;
        this.statusDescription = statusDescription;
        this.statusId = statusId;
        this.tokenTypeID = tokenTypeID;
        this.tokenTypeName = tokenTypeName;
        this.vrn = vrn;
        this.clientReferenceId = clientReferenceId;
        this.isEMVContact = isEMVContact;
        this.isEMVContactless = isEMVContactless;
        this.isRFID = isRFID;
        this.rfiduid = rfiduid;
        this.emaid = emaid;
        this.evPrintedNumber = evPrintedNumber;
        this.cardMediaCode = cardMediaCode;
        this.mediumTypeID = mediumTypeID;
        this.mediumType = mediumType;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountShortName() {
        return accountShortName;
    }

    public void setAccountShortName(String accountShortName) {
        this.accountShortName = accountShortName;
    }

    public Long getBundleId() {
        return bundleId;
    }

    public void setBundleId(Long bundleId) {
        this.bundleId = bundleId;
    }

    public Object getCardBlockSchedules() {
        return cardBlockSchedules;
    }

    public void setCardBlockSchedules(Object cardBlockSchedules) {
        this.cardBlockSchedules = cardBlockSchedules;
    }

    public Long getCardGroupId() {
        return cardGroupId;
    }

    public void setCardGroupId(Long cardGroupId) {
        this.cardGroupId = cardGroupId;
    }

    public String getCardGroupName() {
        return cardGroupName;
    }

    public void setCardGroupName(String cardGroupName) {
        this.cardGroupName = cardGroupName;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getCardTypeCode() {
        return cardTypeCode;
    }

    public void setCardTypeCode(String cardTypeCode) {
        this.cardTypeCode = cardTypeCode;
    }

    public int getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(int cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getColCoCountryCode() {
        return colCoCountryCode;
    }

    public void setColCoCountryCode(String colCoCountryCode) {
        this.colCoCountryCode = colCoCountryCode;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isFleetIdInput() {
        return fleetIdInput;
    }

    public void setFleetIdInput(boolean fleetIdInput) {
        this.fleetIdInput = fleetIdInput;
    }

    public boolean isCRT() {
        return isCRT;
    }

    public void setCRT(boolean CRT) {
        isCRT = CRT;
    }

    public boolean isFleet() {
        return isFleet;
    }

    public void setFleet(boolean fleet) {
        isFleet = fleet;
    }

    public boolean isInternational() {
        return isInternational;
    }

    public void setInternational(boolean international) {
        isInternational = international;
    }

    public boolean isNational() {
        return isNational;
    }

    public void setNational(boolean national) {
        isNational = national;
    }

    public boolean isPartnerSitesIncluded() {
        return isPartnerSitesIncluded;
    }

    public void setPartnerSitesIncluded(boolean partnerSitesIncluded) {
        isPartnerSitesIncluded = partnerSitesIncluded;
    }

    public boolean isShellSitesOnly() {
        return isShellSitesOnly;
    }

    public void setShellSitesOnly(boolean shellSitesOnly) {
        isShellSitesOnly = shellSitesOnly;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public boolean isSuperseded() {
        return isSuperseded;
    }

    public void setSuperseded(boolean superseded) {
        isSuperseded = superseded;
    }

    public boolean isVirtualCard() {
        return isVirtualCard;
    }

    public void setVirtualCard(boolean virtualCard) {
        isVirtualCard = virtualCard;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(String lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public String getLocalCurrencyCode() {
        return localCurrencyCode;
    }

    public void setLocalCurrencyCode(String localCurrencyCode) {
        this.localCurrencyCode = localCurrencyCode;
    }

    public String getLocalCurrencySymbol() {
        return localCurrencySymbol;
    }

    public void setLocalCurrencySymbol(String localCurrencySymbol) {
        this.localCurrencySymbol = localCurrencySymbol;
    }

    public boolean isOdometerInput() {
        return odometerInput;
    }

    public void setOdometerInput(boolean odometerInput) {
        this.odometerInput = odometerInput;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getMaskedPAN() {
        return maskedPAN;
    }

    public void setMaskedPAN(String maskedPAN) {
        this.maskedPAN = maskedPAN;
    }

    public long getPanId() {
        return panId;
    }

    public void setPanId(long panId) {
        this.panId = panId;
    }

    public String getPurchaseCategoryCode() {
        return purchaseCategoryCode;
    }

    public void setPurchaseCategoryCode(String purchaseCategoryCode) {
        this.purchaseCategoryCode = purchaseCategoryCode;
    }

    public int getPurchaseCategoryId() {
        return purchaseCategoryId;
    }

    public void setPurchaseCategoryId(int purchaseCategoryId) {
        this.purchaseCategoryId = purchaseCategoryId;
    }

    public String getPurchaseCategoryName() {
        return purchaseCategoryName;
    }

    public void setPurchaseCategoryName(String purchaseCategoryName) {
        this.purchaseCategoryName = purchaseCategoryName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReissueSetting() {
        return reissueSetting;
    }

    public void setReissueSetting(String reissueSetting) {
        this.reissueSetting = reissueSetting;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getTokenTypeID() {
        return tokenTypeID;
    }

    public void setTokenTypeID(int tokenTypeID) {
        this.tokenTypeID = tokenTypeID;
    }

    public String getTokenTypeName() {
        return tokenTypeName;
    }

    public void setTokenTypeName(String tokenTypeName) {
        this.tokenTypeName = tokenTypeName;
    }

    public String getVrn() {
        return vrn;
    }

    public void setVrn(String vrn) {
        this.vrn = vrn;
    }

    public String getClientReferenceId() {
        return clientReferenceId;
    }

    public void setClientReferenceId(String clientReferenceId) {
        this.clientReferenceId = clientReferenceId;
    }

    public boolean isEMVContact() {
        return isEMVContact;
    }

    public void setEMVContact(boolean EMVContact) {
        isEMVContact = EMVContact;
    }

    public boolean isEMVContactless() {
        return isEMVContactless;
    }

    public void setEMVContactless(boolean EMVContactless) {
        isEMVContactless = EMVContactless;
    }

    public boolean isRFID() {
        return isRFID;
    }

    public void setRFID(boolean RFID) {
        isRFID = RFID;
    }

    public String getRfiduid() {
        return rfiduid;
    }

    public void setRfiduid(String rfiduid) {
        this.rfiduid = rfiduid;
    }

    public String getEmaid() {
        return emaid;
    }

    public void setEmaid(String emaid) {
        this.emaid = emaid;
    }

    public String getEvPrintedNumber() {
        return evPrintedNumber;
    }

    public void setEvPrintedNumber(String evPrintedNumber) {
        this.evPrintedNumber = evPrintedNumber;
    }

    public String getCardMediaCode() {
        return cardMediaCode;
    }

    public void setCardMediaCode(String cardMediaCode) {
        this.cardMediaCode = cardMediaCode;
    }

    public int getMediumTypeID() {
        return mediumTypeID;
    }

    public void setMediumTypeID(int mediumTypeID) {
        this.mediumTypeID = mediumTypeID;
    }

    public String getMediumType() {
        return mediumType;
    }

    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    @Override
    public String toString() {
        return "CardResponse{" +
                "accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountShortName='" + accountShortName + '\'' +
                ", bundleId=" + bundleId +
                ", cardBlockSchedules=" + cardBlockSchedules +
                ", cardGroupId=" + cardGroupId +
                ", cardGroupName='" + cardGroupName + '\'' +
                ", cardId=" + cardId +
                ", cardTypeCode='" + cardTypeCode + '\'' +
                ", cardTypeId=" + cardTypeId +
                ", cardTypeName='" + cardTypeName + '\'' +
                ", colCoCountryCode='" + colCoCountryCode + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", driverName='" + driverName + '\'' +
                ", effectiveDate='" + effectiveDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", fleetIdInput=" + fleetIdInput +
                ", isCRT=" + isCRT +
                ", isFleet=" + isFleet +
                ", isInternational=" + isInternational +
                ", isNational=" + isNational +
                ", isPartnerSitesIncluded=" + isPartnerSitesIncluded +
                ", isShellSitesOnly=" + isShellSitesOnly +
                ", issueDate='" + issueDate + '\'' +
                ", isSuperseded=" + isSuperseded +
                ", isVirtualCard=" + isVirtualCard +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", lastUsedDate='" + lastUsedDate + '\'' +
                ", localCurrencyCode='" + localCurrencyCode + '\'' +
                ", localCurrencySymbol='" + localCurrencySymbol + '\'' +
                ", odometerInput=" + odometerInput +
                ", pan='" + pan + '\'' +
                ", maskedPAN='" + maskedPAN + '\'' +
                ", panId=" + panId +
                ", purchaseCategoryCode='" + purchaseCategoryCode + '\'' +
                ", purchaseCategoryId=" + purchaseCategoryId +
                ", purchaseCategoryName='" + purchaseCategoryName + '\'' +
                ", reason='" + reason + '\'' +
                ", reissueSetting='" + reissueSetting + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", statusId=" + statusId +
                ", tokenTypeID=" + tokenTypeID +
                ", tokenTypeName='" + tokenTypeName + '\'' +
                ", vrn='" + vrn + '\'' +
                ", clientReferenceId='" + clientReferenceId + '\'' +
                ", isEMVContact=" + isEMVContact +
                ", isEMVContactless=" + isEMVContactless +
                ", isRFID=" + isRFID +
                ", rfiduid='" + rfiduid + '\'' +
                ", emaid='" + emaid + '\'' +
                ", evPrintedNumber='" + evPrintedNumber + '\'' +
                ", cardMediaCode='" + cardMediaCode + '\'' +
                ", mediumTypeID=" + mediumTypeID +
                ", mediumType='" + mediumType + '\'' +
                '}';
    }

}