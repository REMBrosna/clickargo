import { Checkbox, FormControlLabel, Grid, Tabs } from "@material-ui/core";

import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import PlaceOutlinedIcon from '@material-ui/icons/PlaceOutlined';
import ShoppingBasketOutlinedIcon from '@material-ui/icons/ShoppingBasketOutlined';
import ApartmentOutlinedIcon from '@material-ui/icons/ApartmentOutlined';
import BookOutlinedIcon from '@material-ui/icons/BookOutlined';
import LocalShippingOutlinedIcon from '@material-ui/icons/LocalShippingOutlined';
import LocalAtmOutlinedIcon from '@material-ui/icons/LocalAtmOutlined';
import SpeedOutlinedIcon from '@material-ui/icons/SpeedOutlined';
import SettingsBackupRestoreOutlinedIcon from '@material-ui/icons/SettingsBackupRestoreOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';

import React from "react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import history from "history.js";
import { tabScroll } from "app/c1utils/styles";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1DataTable from "app/c1component/C1DataTable";
import C1PopUp from "app/c1component/C1PopUp";

import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";

import LocationFormGroup from "app/clictruckcomponent/LocationGroup";


import { CCM_ACCOUNT_ALL_URL, CK_ACCOUNT_TO_ACCN_TYPE, CK_MST_SHIPMENT_TYPE, } from "app/c1utils/const";
import { CK_CT_MST_LOCATION, CK_MST_VEH_TYPE } from "app/c1utils/const";

// const JobTripFrom = ({

// }) => {
//     const { t } = useTranslation(["job"]);
//     return(
//     <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.from")}>
//         <Grid container alignItems="center" spacing={3}>
//             <Grid item xs={12} >

//                 <C1SelectField
//                     name="containerType"
//                     label={t("job:tripDetails.fromLocation")}
//                     required
//                     // disabled={viewType !== 'new'}
//                     disabled={false}
//                     isServer={true}

//                 />
//                 <C1InputField
//                     label={t("job:tripDetails.locationDetail")}
//                     name="jobId"
//                     disabled
//                     multiline
//                     rows={4}
//                 />
//                 <C1TimeField
//                     label={t("job:tripDetails.pickupTime")}
//                     required
//                 />
//                 <C1InputField
//                     label={t("job:tripDetails.remarks")} 
//                     name="tckJob.jobReference"
//                     multiline
//                     rows={4}
//                 />

//             </Grid>
//         </Grid>
//     </C1CategoryBlock>
// )}

// const JobTripTo = ({

// }) => {
//     const { t } = useTranslation(["job"]);
//     return(
//     <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.to")}>
//         <Grid container alignItems="center" spacing={3}>
//             <Grid item xs={12} >

//                 <C1SelectField
//                     name="containerType"
//                     label={t("job:tripDetails.toLocation")}
//                     required
//                     // disabled={viewType !== 'new'}
//                     disabled={false}
//                     isServer={true}

//                 />
//                 <C1InputField
//                     label={t("job:tripDetails.locationDetail")}
//                     name="jobId"
//                     disabled
//                     multiline
//                     rows={4}
//                 />
//                 <C1TimeField
//                     label={t("job:tripDetails.deliveryTime")}
//                     required
//                 />
//                 <C1InputField
//                     label={t("job:tripDetails.remarks")} 
//                     name="tckJob.jobReference"
//                     multiline
//                     rows={4}
//                 />

//             </Grid>
//         </Grid>
//     </C1CategoryBlock>
// )}

// const JobTripDepo = ({

// }) => {
//     const { t } = useTranslation(["job"]);
//     return(
//     <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.depo")}>
//         <Grid container alignItems="center" spacing={3}>
//             <Grid item xs={12} >

//                 <C1SelectField
//                     name="containerType"
//                     label={t("job:tripDetails.depoLocation")}
//                     required
//                     // disabled={viewType !== 'new'}
//                     disabled={false}
//                     isServer={true}

//                 />
//                 <C1InputField
//                     label={t("job:tripDetails.locationDetail")}
//                     name="jobId"
//                     disabled
//                     multiline
//                     rows={4}
//                 />
//                 <C1TimeField
//                     label={t("job:tripDetails.dropOffTime")}
//                     required
//                 />
//                 <C1InputField
//                     label={t("job:tripDetails.remarks")} 
//                     name="tckJob.jobReference"
//                     multiline
//                     rows={4}
//                 />

//             </Grid>
//         </Grid>
//     </C1CategoryBlock>
// )}

const JobTripCharges = ({
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

    const [openAddPopUp, setOpenAddPopUp] = React.useState(false)
    const [tabIndex, setTabIndex] = React.useState(0);
    const { t } = useTranslation(["job"]);

    const shippingType = history.location.state.newJobState;

    //enable open price trip price input when checkbox is checked
    const [isChecked, setIsChecked] = useState(false);
    const [isInputEnabled, setIsInputEnabled] = useState(false);
    function enableOpenPrice(event) {
        setIsChecked(event.target.checked);
        setIsInputEnabled(event.target.checked);
    }

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    const tabList = [
        {
            text: "test1",
            icon: <WorkOutlineOutlinedIcon />
        },
        {
            text: "test2",
            icon: <WorkOutlineOutlinedIcon />
        }
    ]

    const columns = [
        {
            name: "attId",
            label: "S/No"
        },
        {
            name: "tmstAttType.mattDesc",
            label: "Type"
        },
        {
            name: "attName",
            label: "Description"
        },
        {
            name: "attDtCreate",
            label: "Price (IDR)"
        },
        {
            name: "action",
            label: "Tax Amount"

        },
        {
            name: "tmstAttType.mattId",
            label: "Action",

        },
    ]

    const jobtype = () => {
        if (shippingType === "EXPORT") {
            return (
                <React.Fragment>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup locationType="depo" />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup locationType="from" />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup locationType="to" />
                    </Grid>
                </React.Fragment>)
        }
        else {
            return (
                <React.Fragment>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup locationType="from" />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup locationType="to" />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup locationType="depo" />
                    </Grid>
                </React.Fragment>)
        }
    }

    // t("job:tripDetails.")
    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>
                    <Grid item xs={12} >
                        <C1CategoryBlock icon={<PlaceOutlinedIcon />} title={t("job:tripDetails.locationDetails")}>
                        </C1CategoryBlock>
                    </Grid>

                    {/* from to depo ---------------------------------------------------------------------------------------
                <Grid item lg={4} md={6} xs={12} >
                    <JobTripFrom />        
                </Grid> 
                <Grid item lg={4} md={6} xs={12} >
                    <JobTripTo />
                </Grid>

                <Grid item lg={4} md={6} xs={12} >
                    <JobTripDepo />
                </Grid>

// {/* depo from to --------------------------------------------------------------------------------------- 
// <Grid item lg={4} md={6} xs={12} >
//                     <JobTripDepo />        
//                 </Grid> 
//                 <Grid item lg={4} md={6} xs={12} >
//                     <JobTripFrom />
//                 </Grid>

//                 <Grid item lg={4} md={6} xs={12} >
//                     <JobTripTo />
//                 </Grid> */}

                    {jobtype()}

                    {/* end of location block -------------------------------------   */}
                    <Grid item xs={12} >
                        <C1CategoryBlock icon={<ShoppingBasketOutlinedIcon />} title={t("job:tripDetails.cargoAndTruckDetails")}>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<ApartmentOutlinedIcon />} title={t("job:tripDetails.containerDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >

                                    <C1SelectField
                                        name="containerType"
                                        label={t("job:tripDetails.containerType")}
                                        value={inputData?.tckJob?.tckMstShipmentType?.shtId || 'IMPORT'}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        // disabled={viewType !== 'new'}
                                        disabled={false}
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
                                        label={t("job:tripDetails.containerNumber")}
                                        name="jobId"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.jobId || '-'} />

                                    <C1InputField
                                        label={t("job:tripDetails.sealNo")}
                                        value={inputData?.tckJob?.jobReference || ''}
                                        name="tckJob.jobReference"
                                        onChange={handleInputChange}
                                        error={errors['TCkJob.jobReference'] !== undefined}
                                        helperText={errors['TCkJob.jobReference'] || ''}
                                        required
                                    />

                                    <C1SelectField
                                        name="containerType"
                                        label={t("job:tripDetails.containerLoad")}
                                        value={inputData?.tckJob?.tckMstShipmentType?.shtId || 'IMPORT'}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        // disabled={viewType !== 'new'}
                                        disabled={false}
                                        isServer={true}
                                        options={{
                                            url: CK_MST_SHIPMENT_TYPE,
                                            key: "shtId",
                                            id: 'shtId',
                                            desc: 'shtDesc',
                                            isCache: true
                                        }}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<BookOutlinedIcon />} title={t("job:tripDetails.goodsDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >

                                    <C1SelectField
                                        name="containerType"
                                        label={t("job:tripDetails.goodsType")}
                                        value={inputData?.tckJob?.tckMstShipmentType?.shtId || 'IMPORT'}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        // disabled={viewType !== 'new'}
                                        disabled={false}
                                        isServer={true}

                                    />
                                    <C1InputField
                                        label={t("job:tripDetails.description")}
                                        name="jobId"
                                        required
                                        multiline
                                        rows={4}
                                    />


                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<LocalShippingOutlinedIcon />} title={t("job:tripDetails.trucksDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >

                                    {/* <C1SelectField
                                        name="containerType"
                                        label={t("job:tripDetails.truckType")}
                                        value={inputData?.tckCtMstVehType?.vhtyId}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        // disabled={viewType !== 'new'}
                                        disabled={false}
                                        isServer={true}
                                        options={{
                                            url: CK_MST_VEH_TYPE,
                                            key: "vhtyId",
                                            id: 'vhtyId',
                                            desc: 'vhtyDesc',
                                            isCache: true
                                        }}
                                        
                                    /> */}
                                    <C1SelectField
                                        name="tckCtMstVehType.vhtyId"
                                        label={t("job:tripDetails.truckType")}
                                        value={inputData?.tckCtMstVehType?.vhtyId}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        isServer={true}
                                        options={{
                                            url: CK_MST_VEH_TYPE,
                                            key: "vhtyId",
                                            id: 'vhtyId',
                                            desc: 'vhtyDesc',
                                            isCache: true
                                        }}
                                        error={errors['TCkCtMstVehType.vhtyId'] !== undefined}
                                        helperText={errors['TCkCtMstVehType.vhtyId'] || ''}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.commentsAndInstruction")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("job:tripDetails.specialInstruction")}
                                        name="jobId"
                                        multiline
                                        rows={4}
                                        onChange={handleInputChange}
                                        value={inputData?.jobId || '-'}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>


                    <Grid item xs={12} >
                        <C1CategoryBlock icon={<LocalAtmOutlinedIcon />} title={t("job:tripDetails.chargesReimbursements")}>
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<SpeedOutlinedIcon />} title={t("job:tripDetails.tripCharges")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("job:tripDetails.amount")}
                                        name="jobId"
                                        disabled={!isInputEnabled}
                                        onChange={handleInputChange}
                                        value={inputData?.jobId || '-'} />
                                    <FormControlLabel
                                        control={
                                            <Checkbox
                                                checked={isChecked}
                                                onChange={enableOpenPrice}
                                            />}
                                        label={t("job:tripDetails.openPrice")}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>

                    </Grid>
                    <Grid item lg={8} md={12} >
                        <C1CategoryBlock icon={<SettingsBackupRestoreOutlinedIcon />} title={t("job:tripDetails.reimbursement")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DataTable
                                        url={"/api/v1/clickargo/attachments/job"}
                                        isServer={true}
                                        columns={columns}
                                        defaultOrder="attDtCreate"
                                        defaultOrderDirection="desc"

                                        isRefresh={false}
                                        isShowDownload={false}
                                        isShowToolbar={(viewType !== "view")}
                                        isShowPrint={false}
                                        isShowViewColumns={false}
                                        isShowFilter={false}
                                        showAdd={(viewType !== "view") ? {
                                            type: "popUp",
                                            popUpHandler: setOpenAddPopUp,
                                        } : null}

                                    // guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                                    />
                                    <Grid container alignItems="flex-end" spacing={3}>
                                        <Grid item xs={6}>

                                        </Grid>
                                        <Grid item xs={6} alignItems="flex-end">

                                            <C1InputField
                                                label="Total Trip Charge"
                                                inputProps={{ readOnly: true }}
                                            />
                                            <C1InputField
                                                label="Total Reimbursement"
                                                inputProps={{ readOnly: true }}
                                            />
                                            <C1InputField
                                                label="Total Trip Amount"
                                                inputProps={{ readOnly: true }}
                                            />

                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>

                    </Grid>


                </C1TabContainer>
            </Grid>

            <C1PopUp
                title={t("listing:attachments.title")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}

            >
                <Tabs
                    onChange={handleTabChange}
                    value={tabIndex}
                    className="mt-4"
                    indicatorColor="primary"
                    textColor="primary"
                    variant="scrollable"
                    scrollButtons="auto"
                >
                    {tabList
                        && tabList.map((item, i) => {
                            return (
                                <TabsWrapper
                                    style={i === tabIndex ? { backgroundColor: '#e4effa' } : {}}
                                    className="capitalize"
                                    value={i}
                                    disabled={item.disabled}
                                    label={
                                        <TabLabel
                                            viewType={viewType}
                                            invalidTabs={inputData.invalidTabs}
                                            tab={item} />
                                    }
                                    key={i}
                                    icon={item.icon}
                                    {...tabScroll(i)}
                                />
                            )
                        })}

                </Tabs>
            </C1PopUp>
        </React.Fragment>
    );
};

export default JobTripCharges;