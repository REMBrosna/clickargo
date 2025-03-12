import React from "react";
import { Box, FormControlLabel, FormGroup, Grid, IconButton, MenuItem, Switch, Tooltip } from "@material-ui/core";
import C1OutlinedDiv from "app/c1component/C1OutlinedDiv";
import C1TabContainer from "app/c1component/C1TabContainer";
import { useStyles } from "app/c1utils/styles";
import C1InputField from "app/c1component/C1InputField";
import { MOCK_SHIPMENT_TYPE, MOCK_DOC_TYPE } from "fake-db/db/jobs"
import { FORWARDER_ACCOUNTS } from "fake-db/db/accounts";
import C1SelectField from "app/c1component/C1SelectField";
import C1DateField from "app/c1component/C1DateField";
import C1Information from "app/c1component/C1Information";
import GetAppIcon from '@material-ui/icons/GetApp';
import BLList from "../BLList";

const DoJobDetails = ({
    inputData,
    handleInputChange,
    handleDateChange,
    handleInputFileChange,
    handleViewFile,
    viewType,
    isDisabled
}) => {

    const classes = useStyles();

    return (
        <React.Fragment>
            <Grid container className={classes.gridContainer} justifyContent="center">
                <Grid item xs={12}>
                    {/* <C1OutlinedDiv label="Job Details"> */}
                        <C1TabContainer>
                            <Grid item lg={12} md={12} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer} justifyContent="flex-start" direction="row-reverse" >
                                    <Grid item xs={2}>
                                        <C1InputField label="Status"
                                            value={inputData?.status || ''}
                                            name="status"
                                            onChange={handleInputChange}
                                            disabled={true} />
                                    </Grid>
                                </Grid>

                            </Grid>
                            <Grid item lg={4} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label="ID"
                                            name="jobId"
                                            disabled
                                            onChange={handleInputChange}
                                            value={inputData?.jobId || ''} />

                                        <C1SelectField
                                            name="shipmentType"
                                            label="Shipment Type"
                                            value={inputData?.shipmentType || ''}
                                            onChange={e => handleInputChange(e)}
                                            disabled
                                            isServer={false}
                                            optionsMenuItemArr={Object.values(MOCK_SHIPMENT_TYPE).map((type, idx) => {
                                                return <MenuItem value={type.value} key={idx}>{type.desc}</MenuItem>

                                            })} />
                                        {/* <C1InputField label="Shipment Vessel No."
                                            value={inputData?.vesselNo || ''}
                                            name="vesselNo"
                                            required
                                            onChange={handleInputChange}
                                            disabled={isDisabled} />

                                        <C1InputField label="Reference No."
                                            value={inputData?.refNo || ''}
                                            name="refNo"
                                            onChange={handleInputChange}
                                            disabled={isDisabled} /> */}
                                    </Grid>

                                </Grid>
                            </Grid>

                            <Grid item lg={4} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        {/* <C1InputField label="Authoriser User ID"
                                            name="authoriserUserId"
                                            value={inputData?.authoriserUserId || ''}
                                            disabled={true} />

                                        <Grid container item spacing={1} alignItems="center">
                                            <Grid item xs={10}>
                                                <C1SelectField
                                                    isServer={true}
                                                    required
                                                    disabled={isDisabled}
                                                    name="authorizedParty.accnId"
                                                    label="Authorized Party"
                                                    value={inputData?.authorizedParty?.accnId || ''}
                                                    onChange={handleInputChange}
                                                    options={{
                                                        url: `/api/accounts/authorised/${inputData?.authorizedParty?.isAuthorized === true ? 'y' : 'n'}`,
                                                        key: "account",
                                                        id: "accnId",
                                                        desc: "accnName",
                                                        isCache: false,
                                                    }} />
                                            </Grid>
                                            <Grid item xs={2}>
                                                <Tooltip title="Authorized">
                                                    <FormGroup>
                                                        <FormControlLabel style={{ minWidth: 0 }} labelPlacement="start" control={<Switch
                                                            checked={inputData?.authorizedParty?.isAuthorized === undefined ? false : inputData?.authorizedParty?.isAuthorized}
                                                            name="authorizedParty.isAuthorized" disabled={isDisabled}
                                                            onChange={handleInputChange}
                                                        />} />
                                                    </FormGroup>
                                                </Tooltip>
                                            </Grid>
                                        </Grid>


                                        <C1DateField
                                            label="Start Date"
                                            name="startDate"
                                            required
                                            value={inputData?.startDate || ''}
                                            disabled={isDisabled}
                                            onChange={handleDateChange} />

                                        <C1DateField
                                            label="Expiry Date"
                                            name="expiryDate"
                                            required
                                            value={inputData?.expiryDate || ''}
                                            disabled={isDisabled}
                                            onChange={handleDateChange} /> */}
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={4} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        {/* <C1InputField label="Document No."
                                            value={inputData?.docNo || ''}
                                            name="docNo"
                                            required
                                            disabled={isDisabled}
                                            onChange={handleInputChange} />

                                        <C1SelectField
                                            name="documentType"
                                            label="Document Type"
                                            value={inputData?.documentType || ''}
                                            onChange={handleInputChange}
                                            disabled
                                            isServer={false}
                                            optionsMenuItemArr={Object.values(MOCK_DOC_TYPE).map((type, idx) => {
                                                return <MenuItem value={type.value} key={idx}>{type.desc}</MenuItem>

                                            })} />

                                        <Grid container item spacing={1} alignItems="center">
                                            <Grid item xs={inputData?.attchId ? 10 : 12}>
                                                <C1InputField
                                                    label="Document File"
                                                    name="documentFile"
                                                    value={inputData?.documentFile || ''}
                                                    type="file"
                                                    required
                                                    disabled={isDisabled}
                                                    inputProps={{ id: 'filename', accept: "image/*;application/pdf" }}
                                                    onChange={handleInputFileChange} />
                                            </Grid>
                                            {inputData?.attchId && <Grid item xs={2}>
                                                <Tooltip title="View">
                                                    <IconButton aria-label="Download File" type="button"
                                                        color="primary" onClick={(e) => handleViewFile(e, inputData?.attchId)}>
                                                        <GetAppIcon />
                                                    </IconButton>
                                                </Tooltip>
                                            </Grid>}
                                        </Grid> */}
                                        <C1DateField
                                            label="Start Date"
                                            name="startDate"
                                            required
                                            value={inputData?.startDate || ''}
                                            disabled={isDisabled}
                                            onChange={handleDateChange} />

                                        <C1DateField
                                            label="Expiry Date"
                                            name="expiryDate"
                                            required
                                            value={inputData?.expiryDate || ''}
                                            disabled={isDisabled}
                                            onChange={handleDateChange} />

                                    </Grid>
                                </Grid>
                            </Grid>

                        </C1TabContainer>
                    {/* </C1OutlinedDiv> */}
                </Grid>
                <Grid item xs={12}>
                    <BLList />
                </Grid>
                <Grid className={classes.gridContainer} item xs={12}>
                    {"*Click on the EYE icon to view the BL document. " +
                        "BL documents cannot be removed because the job is confirmed."}
                </Grid>
                <Grid item xs={12}>
                    <C1Information information="shiplineDoClaimJobs" />
                </Grid>
            </Grid>


        </React.Fragment>
    );
};

export default DoJobDetails;