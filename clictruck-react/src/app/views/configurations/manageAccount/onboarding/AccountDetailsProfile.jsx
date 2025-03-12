import { Box, Grid, Paper } from "@material-ui/core";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Typography from '@material-ui/core/Typography';
import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1Information from "app/c1component/C1Information";
import C1InputField from "app/c1component/C1InputField";
import C1OutlinedDiv from "app/c1component/C1OutlinedDiv";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import { titleTab, useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { MST_ACCN_TYPE_URL, MST_CTRY_URL, RegistrationStatus } from "app/c1utils/const";
import { getValue } from "app/c1utils/utility";
import { isEditable } from "app/c1utils/utility";

import { genSuppDocsDB } from "../../../../../fake-db/db/accountOnboarding";

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: '#3C77D0',
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);

const useTableStyle = makeStyles({
    table: {
        minWidth: 450,
    },
    column: {
        width: 50,
    },
});


const AccountDetailsProfile = ({
    inputData,
    handleInputChange,
    handleInputAccnIdChange,
    handleAutoCompleteInput,
    errors, locale }) => {

    const { viewType } = useParams();
    const classes = useStyles();
    const title = titleTab();
    const tableCls = useTableStyle();
    const [rows, setRows] = useState([]);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    const cols = [
        // 1
        {
            name: "attId", // field name in the row object
            label: "Document ID", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 2
        {
            name: "tmstAttType.mattName",
            label: "Type",
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "aattName",
            label: "Document Name",
            options: {
                filter: true,
            },
        },
        // 4
        {
            name: "mandatory",
            label: "Mandatory",
            options: {
                filter: true,
            },
        },
        // 5
        {
            name: "expiryDate",
            label: "Validity Date",
            options: {
                filter: true,
            },
        },
        // 6
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return <C1DataTableActions
                        viewPath={"" + tableMeta.rowData[0]}
                        removeEventHandler={isDisabled ? null : (e) => console.log("Remove document")}
                    />
                }
            },
        }
    ]

    let documentsSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Documents</Typography>;
    useEffect(() => {
        // sendRequest(`${COMMON_ATTACH_LIST_BY_REFID_URL}${refId}`, "list", "get");

        setRows(genSuppDocsDB);
        // eslint-disable-next-line
    }, []);

    return (
        <React.Fragment>
            <C1TabContainer>
                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"Company Details"}</Box>
                </Grid> */}
                <Grid className={classes.gridContainer} item xs={12}>
                    <C1OutlinedDiv label="Company Details">
                        <Grid container spacing={3}>
                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={locale("companyDetails.accnrCoIntial")}
                                            name="accnrCoIntial"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 5
                                            }}
                                            onChange={handleInputAccnIdChange}
                                            value={inputData.accnrCoIntial || ""}
                                            error={errors && errors.accnrCoIntial ? true : false}
                                            helperText={errors && errors.accnrCoIntial ? errors && errors.accnrCoIntial : null} />

                                        <C1SelectAutoCompleteField
                                            label={locale("companyDetails.atypId")}
                                            name="accnDetails.TMstAccnType.atypId"
                                            disabled={true}
                                            onChange={handleAutoCompleteInput}
                                            value={getValue(inputData?.TMstAccnType?.atypId)}
                                            isServer={true}
                                            options={{
                                                // url: MST_ACCN_TYPE_URL,
                                                url: `api/accountTypes/all`,
                                                key: "accnType",
                                                id: 'atypId',
                                                desc: 'atypDescription',
                                                isCache: false
                                            }}
                                        />

                                        <C1InputField
                                            label={locale("companyDetails.accnName")}
                                            name="accnrCompName"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrCompName || ""} />

                                        <C1InputField
                                            label="Tax Registration No."
                                            name="accnrCompReg"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrCompReg || ""} />

                                        <C1InputField
                                            label="Channel"
                                            name="accnrChannel"
                                            disabled={true}
                                            onChange={handleInputChange}
                                            value={inputData.accnrChannel || ""} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label="Company Phone"
                                            name="accnrTel"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrTel || ""}
                                            inputProps={{
                                                placeholder: locale("common:common.placeHolder.contactTel")
                                            }} />

                                        <C1InputField
                                            label="Company Fax"
                                            name="accnrFax"
                                            disabled={isDisabled}
                                            onChange={handleInputChange}
                                            value={inputData.accnrFax || ""}
                                            inputProps={{
                                                placeholder: locale("common:common.placeHolder.contactTel")
                                            }} />

                                        <C1InputField
                                            label="Company Email"
                                            name="accnrEmail"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrEmail || ""} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={locale("companyDetails.addrLn1")}
                                            name="accnrAddressLine1"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrAddressLine1 || ""} />

                                        <C1InputField
                                            label={locale("companyDetails.addrLn2")}
                                            name="accnrAddressLine2"
                                            disabled={isDisabled}
                                            required={false}
                                            onChange={handleInputChange}
                                            value={inputData.accnrAddressLine2 || ""} />

                                        <C1InputField
                                            label={locale("companyDetails.addrLn3")}
                                            name="accnrAddressLine3"
                                            disabled={isDisabled}
                                            required={false}
                                            onChange={handleInputChange}
                                            value={inputData.accnrAddressLine3 || ""} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={locale("companyDetails.addrProv")}
                                            name="accnrProv"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrProv || ""} />

                                        <C1InputField
                                            label="City/District"
                                            name="accnrCity"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrCity || ""} />

                                        <C1InputField
                                            label={locale("companyDetails.addrPcode")}
                                            name="accnrPcode"
                                            disabled={isDisabled}
                                            required
                                            onChange={handleInputChange}
                                            value={inputData.accnrPcode || ""} />

                                        <C1SelectAutoCompleteField
                                            label={locale("companyDetails.ctyCode")}
                                            name="TMstCountry.ctyCode"
                                            disabled={isDisabled}
                                            onChange={handleAutoCompleteInput}
                                            value={getValue(inputData?.TMstCountry?.ctyCode)}
                                            isServer={true}
                                            isShowCode={true}
                                            options={{
                                                url: `/api/country/all`,
                                                key: "country",
                                                id: 'ctyCode',
                                                desc: 'ctyDescription',
                                                isCache: true
                                            }} />
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1OutlinedDiv>
                </Grid>

                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"Administrator Details"}</Box>
                </Grid> */}
                <Grid className={classes.gridContainer} item xs={12}>
                    <C1OutlinedDiv label="Administrator Details">
                        <Grid container spacing={3}>
                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={locale("userDetails.usrName")}
                                            name="accnrAplName"
                                            disabled={isDisabled}
                                            inputProps={{
                                                maxLength: 35
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplName)}
                                            error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />

                                        <C1InputField
                                            label={locale("userDetails.usrPassNid")}
                                            name="accnrAplPassNid"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 20
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplPassNid)}
                                            error={errors.coreUsr && errors.coreUsr['accnrAplPassNid'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['accnrAplPassNid']) || ''} />

                                        <C1InputField
                                            label={locale("userDetails.usrTitle")}
                                            name="accnrAplTitle"
                                            disabled={isDisabled}
                                            inputProps={{
                                                maxLength: 35
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplTitle)}
                                            error={errors.coreUsr && errors.coreUsr['accnrAplTitle'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['accnrAplTitle']) || ''} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={"Contact Number"}
                                            name="accnrAplTel"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 1024,
                                                placeholder: locale("common:common.placeHolder.contactTel")
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplTel)}
                                            error={errors.coreUsr && errors.coreUsr['usrContact.contactTel'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrContact.contactTel']) || ''} />

                                        <C1InputField
                                            label={"Email"}
                                            name="accnrAplEmail"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 128
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplEmail)}
                                            error={errors.coreUsr && errors.coreUsr['usrContact.contactEmail'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrContact.contactEmail']) || ''} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={locale("companyDetails.addrLn1")}
                                            name="accnrAplAddr1"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 64
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplAddr1)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn1'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn1']) || ''} />
                                        <C1InputField
                                            label={locale("companyDetails.addrLn2")}
                                            name="accnrAplAddr2"
                                            disabled={isDisabled}
                                            required={false}
                                            inputProps={{
                                                maxLength: 64
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplAddr2)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn2'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn2']) || ''} />
                                        <C1InputField
                                            label={locale("companyDetails.addrLn3")}
                                            // name="addrLn3"
                                            name="accnrAplAddr3"
                                            disabled={isDisabled}
                                            required={false}
                                            inputProps={{
                                                maxLength: 64
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplAddr3)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn3'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn3']) || ''} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12}>
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1InputField
                                            label={locale("userDetails.addrProv")}
                                            // name="addrProv"
                                            name="accnrAplProv"
                                            disabled={isDisabled}
                                            inputProps={{
                                                maxLength: 15
                                            }}
                                            required
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplProv)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrProv'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrProv']) || ''} />

                                        <C1InputField
                                            label={locale("userDetails.addrCity")}
                                            // name="usrAddr.addrCity"
                                            name="accnrAplCity"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 15
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.accnrAplCity)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrProv'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrProv']) || ''} />

                                        <C1InputField
                                            label={locale("userDetails.addrPcode")}
                                            // name="userPostalCode"
                                            name="accnrAplPcode"
                                            disabled={isDisabled}
                                            required
                                            inputProps={{
                                                maxLength: 10
                                            }}
                                            onChange={handleInputChange}
                                            // value={getValue(inputData?.usrAddr?.addrPcode)}
                                            value={getValue(inputData?.accnrAplPcode)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrPcode'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrPcode']) || ''} />

                                        <C1SelectAutoCompleteField
                                            isServer={true}
                                            label={locale("companyDetails.ctyCode")}
                                            name="TMstCountry.ctyCode"
                                            disabled={isDisabled}
                                            onChange={handleAutoCompleteInput}
                                            value={getValue(inputData?.TMstCountry?.ctyCode)}
                                            isShowCode={true}
                                            options={{
                                                url: `/api/country/all`,
                                                key: "country",
                                                id: 'ctyCode',
                                                desc: 'ctyDescription',
                                                isCache: true
                                            }} />
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1OutlinedDiv>
                </Grid>

                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"Supporting Documents"}</Box>
                </Grid> */}

                <Grid item lg={12} md={12} xs={12}>
                    <C1OutlinedDiv label="Supporting Documents">

                        <Grid item lg={12} md={12} xs={12}>
                            <C1DataTable url="/api/process/all"
                                columns={cols}
                                title={documentsSubTitle}
                                defaultOrder="appId"
                                dbName={genSuppDocsDB}
                                showAdd={!isDisabled}
                                isServer={false}
                                isShowViewColumns={true}
                                isShowDownload={false}
                                isShowPrint={false}
                                isShowFilter={false}
                            />
                        </Grid>
                    </C1OutlinedDiv>
                </Grid>

                <Grid item lg={12} md={12} xs={12}>
                    <C1Information information="accnDetailsProfile" />
                </Grid>

            </C1TabContainer>
        </React.Fragment>
    );
};
export default AccountDetailsProfile;