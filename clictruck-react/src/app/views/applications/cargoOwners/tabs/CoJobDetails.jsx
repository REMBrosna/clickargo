import { Checkbox, FormControlLabel, FormGroup, Grid, IconButton, Switch, Tooltip } from "@material-ui/core";
import CalendarTodayIcon from '@material-ui/icons/CalendarTodayOutlined';
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import GetAppIcon from '@material-ui/icons/GetAppOutlined';
import NoteIcon from '@material-ui/icons/NoteOutlined';
import PersonIcon from '@material-ui/icons/PersonOutlineOutlined';
import React from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import { ATTACH_TYPE, CK_ACCOUNT_SHIPPING_LINE_ACCN_TYPE, CK_MST_SHIPMENT_TYPE, CK_SVC_AUTH_PARTIES } from "app/c1utils/const";

const CoJobDetails = ({
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
                                        value={inputData?.jobId || ''} />

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
                                        label={t("cargoowners:jobDetails.shipmentVesselNo")}
                                        value={inputData?.tckDoi?.tckDo?.doVesselNo || ''}
                                        name="tckDoi.tckDo.doVesselNo"
                                        required
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        error={errors['TCkDoi.TCkDo.doVesselNo'] !== undefined}
                                        helperText={errors['TCkDoi.TCkDo.doVesselNo'] || ''}
                                    />

                                    <C1InputField
                                        label={t("cargoowners:jobDetails.voyageNo")}
                                        value={inputData?.tckJob?.jobReference || ''}
                                        name="tckJob.jobReference"
                                        onChange={handleInputChange}
                                        disabled={isDisabled}
                                        error={errors['TCkJob.jobReference'] !== undefined}
                                        helperText={errors['TCkJob.jobReference'] || ''}
                                    />

                                    <C1SelectField
                                        name="shippingLine"
                                        label={t("cargoowners:jobDetails.shippingLine")}
                                        value={inputData?.tckDoi?.tckDo?.TCoreAccn?.accnId || 'MSC'}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        disabled={true}
                                        isServer={true}
                                        options={{
                                            url: CK_ACCOUNT_SHIPPING_LINE_ACCN_TYPE,
                                            key: "accnId",
                                            id: 'accnId',
                                            desc: 'accnName',
                                            isCache: true
                                        }}
                                    />

                                    <C1InputField
                                        multiline
                                        rows={3}
                                        label={t("cargoowners:jobDetails.remarks")}
                                        name="tckJob.jobRemarks"
                                        disabled
                                        onChange={handleInputChange}
                                        value={inputData?.tckJob?.jobRemarks || ''} />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid item lg={4} md={6} xs={12}>
                        <C1CategoryBlock icon={<PersonIcon />} title={t("cargoowners:jobDetails.partyDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("cargoowners:jobDetails.authoriserUsername")}
                                        name="authoriserUserId"
                                        value={inputData?.authoriserUserId || ''}
                                        disabled={true} />

                                    <Grid container item spacing={3} alignItems="center" xs={12}>
                                        <Grid item xs={8}>
                                            <C1SelectField
                                                isServer={true}
                                                required
                                                disabled={isDisabled}
                                                name="tcoreAccnByJobAuthorizedPartyAccn.accnId"
                                                label={t("cargoowners:jobDetails.party")}
                                                value={inputData?.tcoreAccnByJobAuthorizedPartyAccn?.accnId || ''}
                                                onChange={handleInputChange}
                                                options={{
                                                    url: CK_SVC_AUTH_PARTIES + `/${inputData?.authorized === true ? 'y' : 'n'}/${inputData?.tcoreAccnByJobAuthorizerAccn.accnId}`,
                                                    key: "account",
                                                    id: "accnId",
                                                    desc: "accnName",
                                                    isCache: false,
                                                }}
                                                error={errors['tcoreAccnByJobAuthorizedPartyAccn'] !== undefined}
                                                helperText={errors['tcoreAccnByJobAuthorizedPartyAccn'] || ''}
                                            />
                                        </Grid>
                                        <Grid item xs={4}>
                                            <FormControlLabel
                                                control={
                                                    <Checkbox
                                                        name="authorized"
                                                        size="medium"
                                                        checked={inputData?.authorized === undefined ? false : inputData?.authorized}
                                                        onChange={handleInputChange}
                                                        disabled={isDisabled}
                                                    />
                                                }
                                                label={t("cargoowners:jobDetails.authorized")}
                                                labelPlacement="start"
                                            />
                                        </Grid>
                                        {/* <Grid item xs={2}>
                                            {t("cargoowners:jobDetails.authorized")}
                                        </Grid>
                                        <Grid item xs={2} container justifyContent="center">
                                            <Grid container item spacing={5} alignItems="center" xs={12}>
                                                <Grid item xs={12}>
                                                    <Checkbox
                                                        name="authorized"
                                                        size="medium"
                                                        checked={inputData?.authorized === undefined ? false : inputData?.authorized}
                                                        onChange={handleInputChange}
                                                        disabled={isDisabled}
                                                    /> */}
                                                    {/* <Tooltip title={inputData?.authorized === undefined ? t("cargoowners:jobDetails.unauthorized")
                                                        : inputData?.authorized ? t("cargoowners:jobDetails.authorized") : t("cargoowners:jobDetails.unauthorized")}>
                                                        <FormGroup>
                                                            <FormControlLabel style={{ minWidth: 0 }} labelPlacement="start" control={<Switch
                                                                checked={inputData?.authorized === undefined ? false : inputData?.authorized}
                                                                name="authorized" disabled={isDisabled}
                                                                onChange={handleInputChange}
                                                            />} />
                                                        </FormGroup>
                                                    </Tooltip> */}
                                                {/* </Grid>
                                            </Grid>
                                        </Grid> */}
                                    </Grid>
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                        <Grid item style={{ height: '39px' }}></Grid>
                        <C1CategoryBlock icon={<CalendarTodayIcon />} title={t("cargoowners:jobDetails.jobDateDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1DateField
                                        label={t("cargoowners:jobDetails.startDate")}
                                        name="tckJob.tckRecordDate.rcdDtStart"
                                        required
                                        value={inputData?.tckJob?.tckRecordDate?.rcdDtStart || ''}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                    />

                                    <C1DateField
                                        label={t("cargoowners:jobDetails.expiryDate")}
                                        name="tckJob.tckRecordDate.rcdDtExpiry"
                                        required
                                        value={inputData?.tckJob?.tckRecordDate?.rcdDtExpiry || ''}
                                        disabled={isDisabled}
                                        onChange={handleDateChange}
                                        disablePast={true}
                                        minDate={inputData?.tckJob?.tckRecordDate?.rcdDtStart ? inputData?.tckJob?.tckRecordDate?.rcdDtStart : new Date()}
                                    />
                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                    <Grid container item lg={4} md={6} xs={12} direction="column">
                        <C1CategoryBlock icon={<NoteIcon />} title={t("cargoowners:jobDetails.blDetails")}>
                            <Grid container alignItems="center" spacing={3}>
                                <Grid item xs={12} >
                                    <C1InputField
                                        label={t("cargoowners:jobDetails.blNo")}
                                        value={inputData?.tckDoi?.doiBlNo || ''}
                                        name="tckDoi.doiBlNo"
                                        required
                                        disabled={isDisabled}
                                        onChange={handleInputChange}
                                        error={errors['TCkDoi.doiBlNo'] !== undefined}
                                        helperText={errors['TCkDoi.doiBlNo'] || ''}
                                    />

                                    <C1InputField
                                        label={t("cargoowners:jobDetails.doiContainerNo")}
                                        value={inputData?.tckDoi?.doiContainerNo || ''}
                                        name="tckDoi.doiContainerNo"
                                        required
                                        disabled={isDisabled}
                                        onChange={handleInputChange}
                                        error={errors['TCkDoi.doiContainerNo'] !== undefined}
                                        helperText={errors['TCkDoi.doiContainerNo'] || ''}
                                    />

                                    <C1InputField
                                        name="blType"
                                        label={t("cargoowners:jobDetails.blType")}
                                        value={inputData?.tckDoi?.doiBlType || ''}
                                        onChange={e => handleInputChange(e)}
                                        disabled={true}
                                    />

                                    <C1SelectField
                                        name="documentType"
                                        label={t("cargoowners:jobDetails.blDocumentType")}
                                        value={inputData?.documentType || 'BL'}
                                        onChange={e => handleInputChange(e)}
                                        required
                                        disabled={true}
                                        isServer={true}
                                        options={{
                                            url: ATTACH_TYPE,
                                            key: "mattId",
                                            id: 'mattId',
                                            desc: 'mattDesc',
                                            isCache: true
                                        }}
                                    />

                                    <Grid xs={12} container item spacing={0} alignItems="center">
                                        <Grid item xs={4}>
                                            <C1InputField
                                                name="documentFileButton"
                                                title={inputData?.jobAttach?.attName ? inputData?.jobAttach?.attName : t("cargoowners:jobDetails.noFileChosen")}
                                                type="file"
                                                disabled={isDisabled}
                                                inputProps={{
                                                    id: 'filename',
                                                    accept: "image/*;application/pdf",
                                                    style: {
                                                        color: "transparent"
                                                    }
                                                }}
                                                onChange={handleInputFileChange}
                                            />
                                        </Grid>
                                        <Grid item xs={7}>
                                            <C1InputField
                                                value={inputData?.jobAttach?.attName ? inputData?.jobAttach?.attName : t("cargoowners:jobDetails.noFileChosen")}
                                                label={t("cargoowners:jobDetails.blFile")}
                                                name="documentFile"
                                                required
                                                disabled={true}
                                                error={errors['fileUpload'] !== undefined}
                                                helperText={errors['fileUpload'] || ''}
                                            />
                                        </Grid>
                                        {inputData?.jobAttach?.attId && <Grid item xs={1}>
                                            <Tooltip title={fileUploaded ? inputData?.jobAttach?.attName : t("cargoowners:tooltip.download")} >
                                                <span>
                                                    <IconButton aria-label="Download File" type="button" disabled={!fileUploaded}
                                                        color="primary" onClick={(e) => handleViewFile(e, inputData?.jobAttach?.attId)}>
                                                        <GetAppIcon />
                                                    </IconButton>
                                                </span>
                                            </Tooltip>
                                        </Grid>}
                                    </Grid>

                                </Grid>
                            </Grid>
                        </C1CategoryBlock>
                    </Grid>

                </C1TabContainer>

            </Grid>
        </React.Fragment>
    );
};

export default CoJobDetails;