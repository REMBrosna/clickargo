import React from "react";
import Grid from "@material-ui/core/Grid";
import {useTranslation} from "react-i18next";
import C1DateField from "../../../c1component/C1DateField";
import C1CategoryBlock from "../../../c1component/C1CategoryBlock";
import C1InputField from "../../../c1component/C1InputField";
import C1TextArea from "../../../c1component/C1TextArea";
import C1SelectAutoCompleteField from "../../../c1component/C1SelectAutoCompleteField";
import C1TabContainer from "../../../c1component/C1TabContainer";

const NotificationPopUpDetails = ({
     errors,
     isDisabled,
     popUpData,
     uniqueData,
     uniqueNotificationType,
     handleDateChanges,
     handleInputChange,
     isNullConditionType,
     uniqueConditionType,
     action,
     moduleName,
     viewType
 }) => {
    console.log("popUpData", popUpData)
    const { t } = useTranslation(["administration", "common"]);
    const getConditionValueProps = () => {
        switch (popUpData?.altConditionType) {
            case "DISTANCE":
                return {
                    label: "Condition Value (KM)",
                };
            case "DAYS_BEFORE":
                return {
                    label: "Condition Value (Days)",
                };
            default:
                return {
                    label: "Condition Value",
                };
        }
    };
    const getRecipientProps = () => {
        switch (popUpData?.ckCtMstAlert?.altNotificationType) {
            case "EMAIL":
                return {
                    label: "Email",
                };
            case "WHATSAPP":
                return {
                    label: "Recipient Contact",
                };
            default:
                return {
                    label: "Recipient Contact",
                };
        }
    };
    const { label: conditionValueLabel, helperText: conditionValueHelperText } = getConditionValueProps();
    const { label: recipientLabel } = getRecipientProps();
    let conditionType = false;
    if (popUpData?.altConditionType === undefined || popUpData?.altConditionType === ""){
        conditionType = true;
    }
    let alertType = false;
    if (popUpData?.ckCtMstAlert?.altId === undefined || popUpData?.ckCtMstAlert?.altId === ""){
        alertType = true;
    }
    let notificationType = false;
    if (popUpData?.ckCtMstAlert?.altNotificationType !== undefined && popUpData?.ckCtMstAlert?.altNotificationType===""){
        notificationType = true;
    }
    const today = new Date(); // Get today's date
    const altConditionValue = parseInt(popUpData?.altConditionValue, 10) || 0;

    // Calculate the minimum date
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + altConditionValue);

    // Format the minDate for your component, if needed (e.g., using toISOString() for yyyy-mm-dd format)
    const formattedMinDate = minDate.toISOString().split('T')[0];
    moduleName = moduleName.toLowerCase();
    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>
                    {/* Alert Section */}
                    <Grid item xs={8}>
                        <C1CategoryBlock title={"Alert"}>
                            <Grid container spacing={3}>
                                <Grid item xs={12}>
                                    <Grid container spacing={1}>
                                        {/* Alert Type and Notification Type */}
                                        <Grid item xs={6}>
                                            <C1SelectAutoCompleteField
                                                label="Alert Type"
                                                name="ckCtMstAlert.altId"
                                                value={popUpData?.ckCtMstAlert?.altName || ""}
                                                onChange={(e, name, value) => {
                                                    const selectedItem = uniqueData.find(item => item.altName === value?.value);
                                                    const selectedAltId = selectedItem ? selectedItem.altId : null;
                                                    handleInputChange({ target: { name: 'ckCtMstAlert.altId', value: selectedAltId } });
                                                    handleInputChange({ target: { name: 'ckCtMstAlert.altName', value: value?.value } });
                                                }}
                                                required
                                                disabled={isDisabled || action === 'VIEW'}
                                                isServer={false}
                                                optionsMenuItemArr={
                                                    uniqueData
                                                        .filter((item, index, self) =>
                                                            index === self.findIndex((t) => t.altName === item.altName)
                                                        ) // Ensure uniqueness based on altName
                                                        .map((item) => ({
                                                            value: item.altName, // Use altName for display value
                                                            desc: item.altName,  // Description for display
                                                        }))
                                                }
                                                error={!!errors.ckCtMstAlert?.altId}
                                                helperText={errors.ckCtMstAlert?.altId ?? null}
                                            />
                                        {moduleName !== "job" && (
                                            <C1SelectAutoCompleteField
                                                label="Notification Type"
                                                name="ckCtMstAlert.altNotificationType"
                                                value={popUpData?.ckCtMstAlert?.altNotificationType || ""}
                                                onChange={(e, name, value) =>
                                                    handleInputChange({ target: { name, value: value?.value } })
                                                }
                                                required
                                                disabled={isDisabled || alertType || action === 'VIEW'}
                                                isServer={false}
                                                optionsMenuItemArr={
                                                    uniqueNotificationType
                                                        .filter((item, index, self) =>
                                                                index === self.findIndex(
                                                                    (t) => t.altNotificationType === item.altNotificationType
                                                                )
                                                        ) // Ensure uniqueness based on altNotificationType
                                                        .map((item) => ({
                                                            value: item.altNotificationType, // Set altNotificationType as the value
                                                            desc: item.altNotificationType, // Display altNotificationType
                                                        }))
                                                }
                                                error={errors?.ckCtMstAlert?.altNotificationType !== undefined}
                                                helperText={errors?.ckCtMstAlert?.altNotificationType || ""}
                                            />
                                        )}
                                        </Grid>
                                        {moduleName === "job" && (
                                            <Grid item xs={6}>
                                                <C1SelectAutoCompleteField
                                                    label="Notification Type"
                                                    name="ckCtMstAlert.altNotificationType"
                                                    value={popUpData?.ckCtMstAlert?.altNotificationType || ""}
                                                    onChange={(e, name, value) =>
                                                        handleInputChange({ target: { name, value: value?.value } })
                                                    }
                                                    required
                                                    disabled={isDisabled || alertType || action === 'VIEW'}
                                                    isServer={false}
                                                    optionsMenuItemArr={
                                                        uniqueNotificationType
                                                            .filter((item, index, self) =>
                                                                    index === self.findIndex(
                                                                        (t) => t.altNotificationType === item.altNotificationType
                                                                    )
                                                            ) // Ensure uniqueness based on altNotificationType
                                                            .map((item) => ({
                                                                value: item.altNotificationType, // Set altNotificationType as the value
                                                                desc: item.altNotificationType, // Display altNotificationType
                                                            }))
                                                    }
                                                    error={errors?.ckCtMstAlert?.altNotificationType !== undefined}
                                                    helperText={errors?.ckCtMstAlert?.altNotificationType || ""}
                                                />
                                            </Grid>
                                        )}
                                        {/* Condition Type and Monitor Value */}
                                        {!isNullConditionType && moduleName !== "job" && (
                                            <>
                                                <Grid item xs={6}>
                                                    <C1SelectAutoCompleteField
                                                        label="Condition Type"
                                                        name="altConditionType"
                                                        value={popUpData?.altConditionType || ""}
                                                        onChange={(e, name, value) =>
                                                            handleInputChange({ target: { name, value: value?.value } })
                                                        }
                                                        required
                                                        disabled={isDisabled || isNullConditionType || notificationType || alertType || action === 'VIEW'}
                                                        isServer={false}
                                                        optionsMenuItemArr={
                                                            uniqueConditionType
                                                                .filter((item, index, self) =>
                                                                    index === self.findIndex(t => t?.altConditionType === item?.altConditionType)
                                                                ) // Ensure uniqueness based on altConditionType
                                                                .map((item) => ({
                                                                    value: item?.altConditionType, // Set altConditionType as the value
                                                                    desc: item?.altConditionType, // Display altConditionType
                                                                }))
                                                        }
                                                        error={errors?.altConditionType !== undefined}
                                                        helperText={errors?.altConditionType || ""} // Use helperText property
                                                    />

                                                    <C1InputField
                                                        required={true}
                                                        name="altConditionValue"
                                                        type="number"
                                                        label={conditionValueLabel}
                                                        disabled={isNullConditionType || alertType || isDisabled || action === 'VIEW'}
                                                        onChange={(e) => handleInputChange(e, "vextMonitorValue")}
                                                        value={popUpData?.altConditionValue || ""}
                                                        inputProps={{min: 1}}
                                                        error={errors.altConditionValue !== undefined}
                                                        helperText={errors.altConditionValue || ""}
                                                    />
                                                </Grid>

                                                <Grid item xs={6}>
                                                    <C1InputField
                                                        required={true}
                                                        name="altRepCon"
                                                        type="text"
                                                        label={recipientLabel}
                                                        // label="Recipient Contact"
                                                        disabled={isDisabled || alertType || notificationType || isNullConditionType || action === 'VIEW'}
                                                        onChange={(e) => handleInputChange(e, "recipientContact")}
                                                        value={popUpData?.altRepCon || ""}
                                                        error={errors.altRepCon !== undefined}
                                                        helperText={errors.altRepCon || ""}
                                                    />
                                                </Grid>
                                            </>
                                        )}

                                        {popUpData?.altConditionType !== 'DISTANCE' && moduleName !== "job" &&  (
                                            <Grid item xs={6}>
                                                <C1DateField
                                                    label={"Date"}
                                                    name="altConditionDt"
                                                    type="date"
                                                    disabled={isDisabled || alertType || action === 'VIEW'}
                                                    required
                                                    onChange={handleDateChanges}
                                                    value={popUpData?.altConditionDt}
                                                    disablePast
                                                    minDate={formattedMinDate || ""}
                                                    error={errors.altConditionDt !== undefined}
                                                    helperText={errors.altConditionDt || ""}
                                                />
                                            </Grid>
                                        )}
                                        {/* Remark */}
                                        <Grid item xs={12}>
                                            <C1TextArea
                                                label={"Remark"}
                                                name="altRemarks"
                                                type="input"
                                                disabled={isDisabled || action === 'VIEW'}
                                                value={popUpData?.altRemarks}
                                                onChange={(e) => handleInputChange(e, "remarks")}
                                                textLimit={1024}
                                                rows={2}
                                                error={Boolean(errors?.altRemarks)}
                                                helperText={errors?.altRemarks || ""}
                                            />
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    {/* Driver Details Section */}
                        <Grid item xs={4}>
                            <C1CategoryBlock title={t("administration:driverManagement.driverDetails.properties")}>
                                <Grid item xs={12}>
                                    {/* Null values for DUPLICATE action */}
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.createdBy")}
                                        value={action === 'DUPLICATE' ? null : popUpData?.altUidCreate}
                                        name="drvUidCreate"
                                        required
                                        disabled
                                        onChange={handleInputChange}
                                    />
                                    <C1DateField
                                        label={t("administration:driverManagement.driverDetails.createdDate")}
                                        name="drvDtCreate"
                                        required
                                        value={action === 'DUPLICATE' ? null : popUpData?.altDtCreate}
                                        disabled
                                        onChange={handleDateChanges}
                                        disablePast
                                    />
                                    <C1InputField
                                        label={t("administration:driverManagement.driverDetails.updatedBy")}
                                        value={action === 'DUPLICATE' ? null : popUpData?.altUidLupd}
                                        name="drvUidLupd"
                                        required
                                        disabled
                                        onChange={handleInputChange}
                                    />
                                    <C1DateField
                                        label={t("administration:driverManagement.driverDetails.updatedDate")}
                                        name="drvDtLupd"
                                        required
                                        value={action === 'DUPLICATE' ? null : popUpData?.altDtLupd}
                                        disabled
                                        onChange={handleDateChanges}
                                    />
                                </Grid>
                            </C1CategoryBlock>
                        </Grid>
                </C1TabContainer>
            </Grid>
        </React.Fragment>
    );
};

export default NotificationPopUpDetails;