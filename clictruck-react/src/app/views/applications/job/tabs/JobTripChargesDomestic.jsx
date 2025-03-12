import { Grid, Tabs, Divider } from "@material-ui/core";

import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import PublicOutlinedIcon from '@material-ui/icons/PublicOutlined';
import EditLocationOutlinedIcon from '@material-ui/icons/EditLocationOutlined';
import PlaceOutlinedIcon from '@material-ui/icons/PlaceOutlined';
import SettingsBackupRestoreOutlinedIcon from '@material-ui/icons/SettingsBackupRestoreOutlined';
import WidgetsOutlinedIcon from '@material-ui/icons/WidgetsOutlined';

import React from "react";
import { useTranslation } from "react-i18next";
import history from "history.js";
import { tabScroll } from "app/c1utils/styles";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TimeField from "app/c1component/C1TimeField";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";


import C1TabContainer from "app/c1component/C1TabContainer";
import C1DataTable from "app/c1component/C1DataTable";
import C1PopUp from "app/clictruckcomponent/JobPopUp";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import TabLabel from "app/portedicomponent/TabLabel";

import C1FileUpload from "app/c1component/C1FileUpload";

import { CK_MST_VEH_TYPE } from "app/c1utils/const";

const JobTripLocation = ({

}) => {
    const { t } = useTranslation(["job"]);

    return (
        <React.Fragment>
            <C1CategoryBlock icon={<PlaceOutlinedIcon />} title={t("job:tripDetails.locationDetail")}>
                <Grid container alignItems="center" spacing={3}>
                    <Grid item xs={12} >
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item md={6} xs={12}>
                                <C1SelectField
                                    name="containerType"
                                    label={t("job:tripDetails.fromLocation")}
                                    required
                                    // disabled={viewType !== 'new'}
                                    disabled={false}
                                    isServer={true}

                                />
                                <C1InputField
                                    label={t("job:tripDetails.locationDetail")}
                                    name="jobId"
                                    disabled
                                    multiline
                                    rows={4}
                                />
                                <C1TimeField
                                    label={t("job:tripDetails.pickupTime")}
                                    required
                                />
                                <C1InputField
                                    label={t("job:tripDetails.remarks")}
                                    name="tckJob.jobReference"
                                    multiline
                                    rows={4}
                                />

                            </Grid>


                            <Grid item md={6} xs={12}>
                                <C1SelectField
                                    name="containerType"
                                    label={t("job:tripDetails.toLocation")}
                                    required
                                    // disabled={viewType !== 'new'}
                                    disabled={false}
                                    isServer={true}

                                />
                                <C1InputField
                                    label={t("job:tripDetails.locationDetail")}
                                    name="jobId"
                                    disabled
                                    multiline
                                    rows={4}
                                />
                                <C1TimeField
                                    label={t("job:tripDetails.deliveryTime")}
                                    required
                                />
                                <C1InputField
                                    label={t("job:tripDetails.remarks")}
                                    name="tckJob.jobReference"
                                    multiline
                                    rows={4}
                                />

                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </C1CategoryBlock>
            <C1CategoryBlock icon={<LocalShippingOutlinedIcon />} title={t("job:tripDetails.trucksDetails")}>
                <Grid container alignItems="center" spacing={3}>
                    <Grid item md={6} xs={12} >

                        <C1SelectField
                            name="containerType"
                            label={t("job:tripDetails.truckType")}

                            required
                            // disabled={viewType !== 'new'}
                            disabled={false}
                            isServer={true}

                        />

                    </Grid>
                </Grid>
            </C1CategoryBlock>
        </React.Fragment>
    )
}

const JobTripCargo = ({
    viewType
}) => {
    const { t } = useTranslation(["job"]);
    const columnCargo = [
        {
            name: "number",
            label: "S/No"
        },
        {
            name: "cargoType",
            label: "Cargo Type"
        },
        {
            name: "quantity",
            label: "Quantity"
        },
        {
            name: "marks",
            label: "Marks and Number"
        },
        {
            name: "action",
            label: "Action"
        }

    ]
    const [openCargoPopUp, setOpenCargoPopUp] = React.useState(false)
    return (
        <React.Fragment>
            <C1CategoryBlock icon={<WidgetsOutlinedIcon />} title={t("job:tripDetails.cargoDetails")}>
                <Grid container alignItems="center" spacing={3}>
                    <Grid item xs={12} >

                        <C1DataTable
                            url={"/api/v1/clickargo/attachments/job"}
                            isServer={true}
                            columns={columnCargo}
                            defaultOrder="attDtCreate"
                            defaultOrderDirection="desc"
                            // filterBy={
                            //     [
                            //         { attribute: "TCkJob.jobId", value: jobId },
                            //         { attribute: "mattStatus", value: 'A' }
                            //     ]
                            // }
                            // isRefresh={isRefresh}
                            isShowDownload={false}
                            // isShowToolbar={(viewType !== "view")}
                            isShowPrint={false}
                            isShowViewColumns={false}
                            isShowFilter={false}
                            showAdd={(viewType !== "view") ? {
                                type: "popUp",
                                popUpHandler: setOpenCargoPopUp,
                            } : null}
                            guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                        />

                    </Grid>
                </Grid>
            </C1CategoryBlock>
            <C1PopUp
                title={t("job:tripDetails.cargoDetails")}
                openPopUp={openCargoPopUp}
                setOpenPopUp={setOpenCargoPopUp}
            >

                <Grid container alignItems="center" spacing={3}>
                    <Grid item md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.generalDetails")}>
                            <C1SelectField
                                name="containerType"
                                label={t("job:tripDetails.cargoType")}
                                required
                                // disabled={viewType !== 'new'}
                                disabled={false}
                                isServer={true}

                            />
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.quantity")}
                                type="number"
                            />
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.marksNo")}
                            />
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item md={6} xs={12}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.weightDimension")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={4}>
                                    <C1InputField
                                        name=""
                                        label={t("job:tripDetails.length")}
                                        type="number"
                                    />
                                </Grid>
                                <Grid item xs={4}>
                                    <C1InputField
                                        name=""
                                        label={t("job:tripDetails.width")}
                                        type="number"
                                    />
                                </Grid>
                                <Grid item xs={4}>
                                    <C1InputField
                                        name=""
                                        label={t("job:tripDetails.height")}
                                        type="number"
                                    />
                                </Grid>
                            </Grid>
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.weight")}
                                type="number"
                            />
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.volume")}
                                type="number"
                            />
                        </C1CategoryBlock>
                    </Grid>
                </Grid>
                <Grid container alignItems="center" spacing={3}>
                    <Grid item xs={12}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.descriptionInstruction")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item md={6} xs={12}>
                                    <C1InputField
                                        name=""
                                        label={t("job:tripDetails.description")}
                                        multiline
                                        rows={4}
                                    />
                                </Grid>
                                <Grid item md={6} xs={12}>
                                    <C1InputField
                                        name=""
                                        label={t("job:tripDetails.specialInstruction")}
                                        multiline
                                        rows={4}
                                    />
                                </Grid>
                            </Grid>

                        </C1CategoryBlock>
                    </Grid>
                </Grid>

            </C1PopUp>
        </ React.Fragment>
    )
}

const JobTripReimbursment = ({
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
    const { t } = useTranslation(["job"]);

    const columnReimbursement = [
        {
            name: "number",
            label: "S/No"
        },
        {
            name: "description",
            label: "Description"
        },
        {
            name: "price",
            label: "Price (IDR)"
        },
        {
            name: "tax",
            label: "Tax Amount (IDR)"
        },
        {
            name: "action",
            label: "Action"
        }

    ]
    const [openAddPopUp2, setOpenAddPopUp2] = React.useState(false)
    return (
        <React.Fragment>
            <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.reimbursement")}>
                <Grid container alignItems="center" spacing={3}>
                    <Grid item xs={12} >

                        <C1DataTable
                            url={"/api/v1/clickargo/attachments/job"}
                            isServer={true}
                            columns={columnReimbursement}
                            defaultOrder="attDtCreate"
                            defaultOrderDirection="desc"
                            // filterBy={
                            //     [
                            //         { attribute: "TCkJob.jobId", value: jobId },
                            //         { attribute: "mattStatus", value: 'A' }
                            //     ]
                            // }
                            // isRefresh={isRefresh}
                            isShowDownload={false}
                            // isShowToolbar={(viewType !== "view")}
                            isShowPrint={false}
                            isShowViewColumns={false}
                            isShowFilter={false}
                            showAdd={
                                (viewType !== "view") ? {
                                    type: "popUp",
                                    popUpHandler: setOpenAddPopUp2,
                                } :
                                    null}
                            guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                        />

                    </Grid>
                </Grid>
            </C1CategoryBlock>
            <C1PopUp
                title={t("job:tripDetails.addReimbursement")}
                openPopUp={openAddPopUp2}
                setOpenPopUp={setOpenAddPopUp2}
            >
                <Grid container alignItems="center" spacing={3}>
                    <Grid item md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.generalDetails")}>
                            <C1SelectField
                                name=""
                                label={t("job:tripDetails.type")}
                                required
                            />
                            <C1FileUpload
                                name="receiptFileButton"
                                label={"Browse"}
                                required
                                fileChangeHandler={handleInputFileChange}
                                inputLabel={t("job:tripDetails.receipt")}
                                value={inputData?.vhPhotoName ? inputData?.vhPhotoName : t("administration:truckManagement.truckDetails.noFileChosen")}
                                inputProps={{
                                    id: 'filename',
                                    accept: "image/*;application/pdf",
                                    style: {
                                        color: "transparent"
                                    }
                                }}
                            />
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.description")}
                            />

                        </C1CategoryBlock>
                    </Grid>
                    <Grid item md={6} xs={12}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.reimbursementCost")}>
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.price")}
                                type="number"
                            />
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.tax")}
                                type="number"
                            />
                            <C1InputField
                                name=""
                                label={t("job:tripDetails.total")}
                                type="number"
                                inputProps={{ readOnly: true }}
                            />
                        </C1CategoryBlock>
                    </Grid>
                </Grid>
            </C1PopUp>
        </React.Fragment>
    )
}

const JobTripChargesDomestic = ({
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
    const [openAddPopUp2, setOpenAddPopUp2] = React.useState(false)

    const [tabIndex, setTabIndex] = React.useState(0);
    const { t } = useTranslation(["job"]);

    const data = history.location.state.newJobState;

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    // ----tablist ---------------------------------------------------------------------------------------------
    const tabList = [
        {
            text: "location",
            icon: <PublicOutlinedIcon />
        },
        {
            text: "Cargo",
            icon: <LocalShippingOutlined />
        },
        {
            text: "Reimbursment",
            icon: <SettingsBackupRestoreOutlinedIcon />
        }
    ]
    // -----end tablist ----------------------------------------------------------------------------------------
    const columnsDomesticJob = [
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


    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>
                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<SpeedOutlinedIcon />} title={t("job:tripDetails.trucksDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
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

                    </Grid>
                    <Grid item lg={8} md={12} >
                        <C1CategoryBlock icon={<EditLocationOutlinedIcon />} title={t("job:tripDetails.tripCargoReimbursement")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DataTable
                                        url={"/api/v1/clickargo/attachments/job"}
                                        isServer={true}
                                        columns={columnsDomesticJob}
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
                title={t("job:tripDetails.tripCargoReimbursement")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                maxHeight="80%"
                overflowY="auto"
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
                <Divider className="mb-6" />
                {/* ------ tab component -------------------------------------------------------------------------------------------------------------- */}
                {tabIndex === 0 && <C1TabInfoContainer title="empty" guideAlign="right" open={false}>
                    <JobTripLocation />
                </C1TabInfoContainer>}
                {tabIndex === 1 && <C1TabInfoContainer title="empty" guideAlign="right" open={false}>
                    <JobTripCargo />
                </C1TabInfoContainer>}
                {tabIndex === 2 && <C1TabInfoContainer title="empty" guideAlign="right" open={false}>
                    <JobTripReimbursment />
                </C1TabInfoContainer>}
            </C1PopUp>

        </React.Fragment>
    );
};

export default JobTripChargesDomestic;