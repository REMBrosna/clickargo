import { Box, Card, Divider, Grid, Paper } from "@material-ui/core";
import { blue, cyan, green, orange, purple, red } from '@material-ui/core/colors';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormGroup from '@material-ui/core/FormGroup';
import MenuItem from "@material-ui/core/MenuItem";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Switch from '@material-ui/core/Switch';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Typography from '@material-ui/core/Typography';
import React, { useEffect, useState } from "react";

import C1DataTable from 'app/c1component/C1DataTable';
import C1DataTableActions from 'app/c1component/C1DataTableActions';
import C1DateField from "app/c1component/C1DateField";
import C1Information from "app/c1component/C1Information";
import C1InputField from "app/c1component/C1InputField";
import C1OutlinedDiv from "app/c1component/C1OutlinedDiv";
import { titleTab, useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { isEditable } from "app/c1utils/utility";
import SupportingDocs from "app/portedicomponent/SupportingDocs";

import { clicDeclareDB, clicGatePassDB, suppDocsQuotationDB } from "../../../../../fake-db/db/accountOnboarding";

const useTableStyle = makeStyles({
    table: {
        minWidth: 450,
    },
    column: {
        width: 50,
    },
});

const AccounDetailsCreditLine = ({
    inputData,
    handleInputChange,
    handleInputAccnIdChange,
    errors, locale,
    viewType }) => {

    const classes = useStyles();
    const title = titleTab();
    const tableCls = useTableStyle();
    const [rows, setRows] = useState([]);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    const columns = [
        // 1
        {
            name: "transactionId", // field name in the row object
            label: "Service ID", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 2
        {
            name: "type",
            label: "Type",
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "date",
            label: "Start Date",
            options: {
                filter: true,
            },
        },
        // 4
        {
            name: "reference",
            label: "Reference",
            options: {
                filter: true,
            },
        },
        // 5
        {
            name: "adjustment",
            label: "Adjustment",
            options: {
                filter: true,
            },
        },
        // 6
        {
            name: "line",
            label: "Line",
            options: {
                filter: true,
            },
        },
        // 7
        {
            name: "status",
            label: "Status",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    if (value === 'PEN')
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: cyan[200], color: cyan[800] }}>
                            Pending Approval
                        </small>;
                    else if (value === 'CPT')
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: green[200], color: green[800] }}>
                            Completed
                        </small>;
                    else if (value === 'Draft')
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: blue[200], color: blue[800] }}>
                            Draft
                        </small>;
                }
            },
        },
        // 8
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
                        downloadPath={"" + tableMeta.rowData[0]}
                    />
                }
            },
        },
    ]

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
            name: "attType",
            label: "Type",
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "attName",
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
                        removeEventHandler={isDisabled ? null : (e) => handleDeleteConfirm(e, tableMeta.rowData[0])}
                    />
                }
            },
        }
    ]

    const handleDeleteConfirm = (e) => {
        return;
    }

    let servicesSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Credit Line Transactions</Typography>;
    let documentsSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Documents</Typography>;

    useEffect(() => {
        // sendRequest(`${COMMON_ATTACH_LIST_BY_REFID_URL}${refId}`, "list", "get");

        setRows(suppDocsQuotationDB);
        // eslint-disable-next-line
    }, []);

    return (
        <React.Fragment>
            <C1TabContainer>
                <Grid className={classes.gridContainer} item xs={12}>
                    <C1OutlinedDiv label="ClickDeclare">
                        {/* <Box className={title.root}>{"ClicDeclare"}</Box> */}

                        <Grid item lg={12} md={12} xs={12} >
                            <Grid container alignItems="center" spacing={1} className={classes.gridContainer}>
                                <Grid item xs={2} >
                                    <C1InputField
                                        label="Credit Limit (IDR)"
                                        name="usrName"
                                        disabled={false}
                                        inputProps={{
                                            maxLength: 35
                                        }}
                                        onChange={handleInputChange}
                                        value={""}
                                        error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                        helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />
                                </Grid>
                                <Grid item xs={6} >
                                </Grid>
                                <Grid item xs={2} >
                                    <C1DateField
                                        label="Start Date"
                                        name="usrName"
                                        disabled={true}
                                        inputProps={{
                                            maxLength: 35
                                        }}
                                        onChange={handleInputChange}
                                        value={""}
                                        error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                        helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />
                                </Grid>
                                <Grid item xs={2} >
                                    <C1DateField
                                        label="Validity Date"
                                        name="usrName"
                                        disabled={true}
                                        inputProps={{
                                            maxLength: 35
                                        }}
                                        onChange={handleInputChange}
                                        value={""}
                                        error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                        helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />
                                </Grid>
                            </Grid>
                        </Grid>

                        <C1DataTable url="/api/process/all"
                            columns={columns}
                            title={servicesSubTitle}
                            defaultOrder="transactionId"
                            dbName={clicDeclareDB}
                            showAdd={!isDisabled}
                            isServer={false}
                            isShowViewColumns={false}
                            isShowDownload={false}
                            isShowPrint={false}
                            isShowFilter={false}
                            filterBy={[{ attribute: "serviceType", value: "ClicDeclare" }]}
                        />

                        <C1DataTable url="/api/process/all"
                            columns={cols}
                            title={documentsSubTitle}
                            defaultOrder="appId"
                            dbName={suppDocsQuotationDB}
                            showAdd={!isDisabled}
                            isServer={false}
                            isShowViewColumns={false}
                            isShowDownload={false}
                            isShowPrint={false}
                            isShowFilter={false}
                        />

                    </C1OutlinedDiv>
                </Grid>

                <Grid className={classes.gridContainer} item xs={12}>
                    <C1OutlinedDiv label="ClickDeclare">
                        <Grid className={classes.gridContainer} item xs={12}>
                            {/* <Box className={title.root}>{"ClicGatePass"}</Box> */}
                            <Grid item lg={12} md={12} xs={12} >
                                <Grid container alignItems="center" spacing={1} className={classes.gridContainer}>
                                    <Grid item xs={2} >
                                        <C1InputField
                                            label="Credit Limit (IDR)"
                                            name="usrName"
                                            disabled={false}
                                            inputProps={{
                                                maxLength: 35
                                            }}
                                            onChange={handleInputChange}
                                            value={""}
                                            error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />
                                    </Grid>
                                    <Grid item xs={6} >
                                    </Grid>
                                    <Grid item xs={2} >
                                        <C1DateField
                                            label="Start Date"
                                            name="usrName"
                                            disabled={true}
                                            inputProps={{
                                                maxLength: 35
                                            }}
                                            onChange={handleInputChange}
                                            value={""}
                                            error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />
                                    </Grid>
                                    <Grid item xs={2} >
                                        <C1DateField
                                            label="Validity Date"
                                            name="usrName"
                                            disabled={true}
                                            inputProps={{
                                                maxLength: 35
                                            }}
                                            onChange={handleInputChange}
                                            value={""}
                                            error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>

                        <C1DataTable url="/api/process/all"
                            columns={columns}
                            title={servicesSubTitle}
                            defaultOrder="transactionId"
                            dbName={clicGatePassDB}
                            showAdd={!isDisabled}
                            isServer={false}
                            isShowViewColumns={false}
                            isShowDownload={false}
                            isShowPrint={false}
                            isShowFilter={false}
                            filterBy={[{ attribute: "serviceType", value: "ClicGatePass" }]}
                        />

                        <C1DataTable url="/api/process/all"
                            columns={cols}
                            title={documentsSubTitle}
                            defaultOrder="appId"
                            dbName={suppDocsQuotationDB}
                            showAdd={!isDisabled}
                            isServer={false}
                            isShowViewColumns={false}
                            isShowDownload={false}
                            isShowPrint={false}
                            isShowFilter={false}
                        />
                        {/* <SupportingDocs /> */}
                    </C1OutlinedDiv>
                </Grid>

                <Grid item lg={12} md={12} xs={12}>
                    <C1Information information="accnDetailsProfile" />
                </Grid>

            </C1TabContainer>
        </React.Fragment>
    );
};
export default AccounDetailsCreditLine;