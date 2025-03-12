import { Checkbox, FormControlLabel, FormGroup, Grid, IconButton, Switch, Tooltip } from "@material-ui/core";
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import GetAppIcon from '@material-ui/icons/GetAppOutlined';
import BorderColorOutlinedIcon from '@material-ui/icons/BorderColorOutlined';
import React, { useState } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1SelectField from "app/c1component/C1SelectField";
import { CK_MST_SHIPMENT_TYPE } from "app/c1utils/const";
import C1DataTable from "app/c1component/C1DataTable";
import C1IconButton from "app/c1component/C1IconButton";
import C1PopUp from "app/c1component/C1PopUp";
import AddTruckRentalPopup from "../popups/AddTruckRentalPopup";
import SaveIcon from '@material-ui/icons/SaveOutlined';

const RentalDetails = ({
    inputData,
    handleInputChange,
    handleDateChange,
    isDisabled,
    errors,
    handleBtnSaveTruckClick
}) => {

    const { t } = useTranslation(["administration", "listing"]);

    const [openAddPopUp, setOpenAddPopUp] = useState(false)
    const [view, setView] = useState(false)

    const popUpAddHandler = () => {
        setView(false);
        setOpenAddPopUp(true);
        // setPopUpFieldError({});
        // setPopUpDetails({});
    };

    const columns = [
        {
            name: "jobId",
            label: t("listing:common.id")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:rental.truckType")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:rental.rentalValue")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:rental.rentalRate")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.dateCreated")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.dateUpdated")
        },
        {
            name: "tckDoi.doiBlNo",
            label: t("listing:common.status")
        },
    ]

    return (
        <React.Fragment>
            <Grid item xs={12}>
                <C1TabContainer>

                    <Grid item lg={4} md={6} xs={12} >
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:rentalManagement.rentalDetails.generalDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:rentalManagement.rentalDetails.id")}
                                        name="id"
                                        disabled
                                        onChange={handleInputChange}
                                        value={inputData?.id || ''} />
                                    <C1InputField
                                        label={t("administration:rentalManagement.rentalDetails.name")}
                                        name="name"
                                        required
                                        onChange={handleInputChange}
                                        value={inputData?.name || ''} />
                                    <C1InputField
                                        label={t("administration:rentalManagement.rentalDetails.desc")}
                                        name="desc"
                                        required
                                        multiline
                                        rows={3}
                                        onChange={handleInputChange}
                                        value={inputData?.desc || ''} />
                                    <C1SelectField
                                        name="currency"
                                        label={t("administration:rentalManagement.rentalDetails.currency")}
                                        value={inputData?.currency}
                                        onChange={e => handleInputChange(e)}
                                        required
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

                    <Grid item lg={4} md={6} xs={12}>
                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:rentalManagement.rentalDetails.partiesDetail")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1SelectField
                                        name="truckOperator"
                                        label={t("administration:rentalManagement.rentalDetails.truckOperator")}
                                        value={inputData?.currency}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        isServer={true}
                                        options={{
                                            url: CK_MST_SHIPMENT_TYPE,
                                            key: "shtId",
                                            id: 'shtId',
                                            desc: 'shtDesc',
                                            isCache: true
                                        }}
                                    />

                                    <C1SelectField
                                        name="coff"
                                        label={t("administration:rentalManagement.rentalDetails.coff")}
                                        value={inputData?.currency}
                                        onChange={e => handleInputChange(e)}
                                        required
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

                        <C1CategoryBlock icon={<DescriptionIcon />} title={t("administration:rentalManagement.rentalDetails.valilidilityDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DateField
                                        label={t("administration:rentalManagement.rentalDetails.startDate")}
                                        name="startDate"
                                        required
                                        value={inputData?.startDate}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                    <C1DateField
                                        label={t("administration:rentalManagement.rentalDetails.expiredDate")}
                                        name="expiredDate"
                                        required
                                        value={inputData?.expiredDate}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid container item lg={4} md={6} xs={12} direction="column">
                        <C1CategoryBlock icon={<BorderColorOutlinedIcon />} title={t("administration:rentalManagement.rentalDetails.properties")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("administration:rentalManagement.rentalDetails.createdBy")}
                                        value={inputData?.createdBy}
                                        name="createdBy"
                                        required
                                        disabled={isDisabled}
                                        onChange={handleInputChange}
                                        error={errors['TCkDoi.doiBlNo'] !== undefined}
                                        helperText={errors['TCkDoi.doiBlNo'] || ''}
                                    />

                                    <C1DateField
                                        label={t("administration:rentalManagement.rentalDetails.createdDate")}
                                        name="createdDate"
                                        required
                                        value={inputData?.createdDate}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                    <C1InputField
                                        label={t("administration:rentalManagement.rentalDetails.updatedBy")}
                                        value={inputData?.updatedBy}
                                        name="updatedBy"
                                        required
                                        disabled={isDisabled}
                                        onChange={handleInputChange}
                                        error={errors['TCkDoi.doiContainerNo'] !== undefined}
                                        helperText={errors['TCkDoi.doiContainerNo'] || ''}
                                    />

                                    <C1DateField
                                        label={t("administration:rentalManagement.rentalDetails.updatedDate")}
                                        name="updatedDate"
                                        required
                                        value={inputData?.updatedDate}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={12} md={12} xs={12} >

                        <C1CategoryBlock title={t("administration:rentalManagement.rentalDetails.rentalList")}>
                            <C1DataTable
                                url={`/api/v1/clickargo/clictruck/administration/truck-management/list`}
                                id="selected Bls"
                                columns={columns}
                                // dbName={jobState === Status.DRF.code ? addedBls : null}
                                defaultOrder="jdoId"
                                isShowToolbar={true}
                                defaultOrderDirection="desc"
                                // isServer={jobState === Status.DRF.code ? false : true}
                                // isRefresh={availableBls}
                                isShowViewColumns={true}
                                isShowDownload={false}
                                isShowPrint={false}
                                isShowFilter={true}
                                showAdd={{
                                    type: "popUp",
                                    popUpHandler: popUpAddHandler,
                                }}
                            // showManualAdd={!isDisabled && inputData?.tckJob?.tckMstJobState?.jbstId === JobStates.DRF.code ? {
                            //     type: "popUp",
                            //     popUpHandler: popUpManualAddHandler,
                            // } : null}
                            // filterBy={[
                            //     { attribute: "tckJobDoiFf.jobId", value: inputData?.jobId },
                            //     { attribute: "jdoStatus", value: RecordStatus.ACTIVE.code }]}
                            // guideId={infoGuide?.table}
                            />
                        </C1CategoryBlock>
                    </Grid>

                </C1TabContainer>
            </Grid>

            <C1PopUp
                title={t("administration:rentalManagement.popupTitle")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                actionsEl={
                    <C1IconButton tooltip={t("ffclaims:tooltip.add")} childPosition="right">
                        <SaveIcon color="primary" fontSize="large" onClick={handleBtnSaveTruckClick}></SaveIcon>
                    </C1IconButton>}>
                <AddTruckRentalPopup
                    view={view}
                    viewType={"view"}
                    locale={t}
                    handleInputChange={handleInputChange}
                    inputData={inputData}
                />
            </C1PopUp>

        </React.Fragment>
    );
};

export default RentalDetails;