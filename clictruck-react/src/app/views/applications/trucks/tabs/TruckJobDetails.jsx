import { Checkbox, FormControlLabel, FormGroup, Grid, IconButton, Switch, Tooltip } from "@material-ui/core";
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import CalendarTodayIcon from '@material-ui/icons/CalendarTodayOutlined';
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import PersonIcon from '@material-ui/icons/PersonOutlineOutlined';
import PhoneInTalkOutlinedIcon from '@material-ui/icons/PhoneInTalkOutlined';
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { CCM_ACCOUNT_ALL_URL, CK_ACCOUNT_TO_ACCN_TYPE, CK_MST_SHIPMENT_TYPE } from "app/c1utils/const";

const TruckJobDetails = ({
    inputData,
    handleInputChange,
    handleDateChange,
    handleInputFileChange,
    handleViewFile,
    viewType,
    isDisabled,
    errors,
    fileUploaded
}) => {

    const { t } = useTranslation(["cargoowners"]);

    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>

                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("cargoowners:jobDetails.generalDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("cargoowners:jobDetails.jobId")}
                                        name="jobId"
                                        disabled
                                        onChange={handleInputChange}
                                        value={inputData?.jobId || '-'} />
                                    <C1SelectField
                                        name="shipmentType"
                                        label={t("cargoowners:jobDetails.shipmentType")}
                                        value={inputData?.tckJob?.tckMstShipmentType?.shtId || 'IMPORT'}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        // disabled={viewType !== 'new'}
                                        disabled={true}
                                        isServer={true}
                                        options={{
                                            url: CK_MST_SHIPMENT_TYPE,
                                            key: "shtId",
                                            id: 'shtId',
                                            desc: 'shtDesc',
                                            isCache: true
                                        }}
                                    />
                                    <C1InputField
                                        label={"Shipment Ref"}
                                        value={inputData?.jobShipmentRef || ''}
                                        name="jobShipmentRef"
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        required
                                        error={errors['jobShipmentRef'] !== undefined}
                                        helperText={errors['jobShipmentRef'] || ''}
                                    />
                                    <C1InputField
                                        label={"Customer Ref"}
                                        name="jobCustomerRef"
                                        value={inputData?.jobCustomerRef || ''}
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        error={errors['jobCustomerRef'] !== undefined}
                                        helperText={errors['jobCustomerRef'] || ''}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <Grid item style={{ height: '39px' }}></Grid>
                        <C1CategoryBlock icon={<CalendarTodayIcon />} title={"Job Dates"}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DateField
                                        label={"Booking Date"}
                                        name="jobDtBooking"
                                        required
                                        value={inputData?.jobDtBooking || ''}
                                        disabled={true}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />
                                    <C1DateField
                                        label={"Plan Date"}
                                        name="jobDtPlan"
                                        required
                                        value={inputData?.jobDtPlan || ''}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={isDisabled}
                                        error={errors['jobDtPlan'] !== undefined}
                                        helperText={errors['jobDtPlan'] || ''}
                                    />
                                    <C1DateField
                                        label={"Delivered Date"}
                                        name="jobDtDelivery"
                                        required
                                        value={inputData?.jobDtDelivery || ''}
                                        disabled={true}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    // minDate={inputData?.tckJob?.tckRecordDate?.rcdDtStart ? inputData?.tckJob?.tckRecordDate?.rcdDtStart : new Date()}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<PersonIcon />} title={"Parties Details"}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    {/* Trucking Operator*/}
                                    <C1SelectField
                                        isServer={true}
                                        required
                                        disabled={isDisabled}
                                        name="tcoreAccnByJobPartyTo.accnId"
                                        label={"Truck Operator"}
                                        value={inputData?.tcoreAccnByJobPartyTo?.accnId || ''}
                                        onChange={handleInputChange}
                                        options={{
                                            url: CK_ACCOUNT_TO_ACCN_TYPE,
                                            key: "account",
                                            id: "accnId",
                                            desc: "accnName",
                                            isCache: false,
                                        }}
                                        error={errors['TCoreAccnByJobPartyTo.accnId'] !== undefined}
                                        helperText={errors['TCoreAccnByJobPartyTo.accnId'] || ''}
                                    />
                                    {/* Freight Forwarder / Cargo Owner Selection */}
                                    <C1SelectField
                                        isServer={true}
                                        required
                                        disabled={true}
                                        name="tcoreAccnByJobPartyCoFf.accnId"
                                        label={"CO / FF"}
                                        value={inputData?.tcoreAccnByJobPartyCoFf?.accnId}
                                        onChange={handleInputChange}
                                        options={{
                                            url: CCM_ACCOUNT_ALL_URL,
                                            key: "account",
                                            id: "accnId",
                                            desc: "accnName",
                                            isCache: false,
                                        }}
                                        error={errors['tcoreAccnByJobPartyCoFf'] !== undefined}
                                        helperText={errors['tcoreAccnByJobPartyCoFf'] || ''}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <Grid item style={{ height: '39px' }}></Grid>
                        <C1CategoryBlock icon={<PhoneInTalkOutlinedIcon />} title={"Contact Details (CO/FF)"}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={"Contact Person"}
                                        name="tckCtContactDetailByJobContactCoFf.cdName"
                                        value={inputData?.tckCtContactDetailByJobContactCoFf?.cdName || ''}
                                        onChange={handleInputChange}
                                        disabled={true} />
                                    <C1InputField
                                        label={"Phone Number"}
                                        placeholder={t("job:tripDetails.mobileFormat")}
                                        name="tckCtContactDetailByJobContactCoFf.cdPhone"
                                        value={inputData?.tckCtContactDetailByJobContactCoFf?.cdPhone || ''}
                                        onChange={handleInputChange}
                                        disabled={true} />
                                    <C1InputField
                                        label={"Email"}
                                        name="tckCtContactDetailByJobContactCoFf.cdEmail"
                                        value={inputData?.tckCtContactDetailByJobContactCoFf?.cdEmail || ''}
                                        onChange={handleInputChange}
                                        disabled={true} />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <Grid item style={{ height: '39px' }}></Grid>
                        <C1CategoryBlock icon={<PhoneInTalkOutlinedIcon />} title={"Contact Details (TO)"}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={"Contact Person"}
                                        name="tckCtContactDetailByJobContactTo.cdName"
                                        value={inputData?.tckCtContactDetailByJobContactTo?.cdName || ''}
                                        onChange={handleInputChange}
                                        disabled={true} />
                                    <C1InputField
                                        label={"Phone Number"}
                                        placeholder={t("job:tripDetails.mobileFormat")}
                                        name="tckCtContactDetailByJobContactTo.cdPhone"
                                        value={inputData?.tckCtContactDetailByJobContactTo?.cdPhone || ''}
                                        onChange={handleInputChange}
                                        disabled={true} />
                                    <C1InputField
                                        label={"Email"}
                                        name="tckCtContactDetailByJobContactTo.cdEmail"
                                        value={inputData?.tckCtContactDetailByJobContactTo?.cdEmail || ''}
                                        onChange={handleInputChange}
                                        disabled={true} />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <C1CategoryBlock icon={<BorderColorOutlinedIcon />} title={"Properties"}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={"Created By"}
                                        value={inputData?.jobUidCreate || ''}
                                        name="jobUidCreate"
                                        required
                                        disabled={true}
                                        onChange={handleInputChange}
                                        error={errors['jobUidCreate'] !== undefined}
                                        helperText={errors['jobUidCreate'] || ''}
                                    />
                                    <C1DateField
                                        label={"Created Date"}
                                        name="jobDtCreate"
                                        required
                                        value={inputData?.jobDtCreate || ''}
                                        disabled={true}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                        minDate={inputData?.jobDtCreate ? inputData?.jobDtCreate : new Date()}
                                    />
                                    <C1InputField
                                        label={"Updated By"}
                                        value={inputData?.jobUidLupd || ''}
                                        name="jobUidLupd"
                                        required
                                        disabled={true}
                                        onChange={handleInputChange}
                                        error={errors['jobUidLupd'] !== undefined}
                                        helperText={errors['jobUidLupd'] || ''}
                                    />
                                    <C1DateField
                                        label={"Updated Date"}
                                        name="rtDtLupd"
                                        required
                                        value={inputData?.rtDtLupd || ''}
                                        disabled={true}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                        minDate={inputData?.rtDtLupd ? inputData?.rtDtLupd : new Date()}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>
            </Grid>
        </React.Fragment>
    );
};

export default TruckJobDetails;