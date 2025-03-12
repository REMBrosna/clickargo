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
import C1ListPanel from "app/c1component/C1ListPanel";
import C1OutlinedDiv from "app/c1component/C1OutlinedDiv";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import { titleTab, useStyles } from "app/c1component/C1Styles";
import C1TabContainer from "app/c1component/C1TabContainer";
import { MST_ACCN_TYPE_URL, MST_CTRY_URL, RegistrationStatus } from "app/c1utils/const";
import { BillingTypes, FeeTypes, PaymentTypes } from "app/c1utils/const";
import { isEditable } from "app/c1utils/utility";
import { getValue } from "app/c1utils/utility";

import { servicesDB, suppDocsDB } from "../../../../../fake-db/db/accountOnboarding";

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

const AccountDetailsService = ({
    inputData,
    handleInputChange,
    handleInputAccnIdChange,
    errors, locale, viewType }) => {

    const classes = useStyles();
    const title = titleTab();
    const tableCls = useTableStyle();
    const [rows, setRows] = useState([]);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    const columns = [
        // 1
        {
            name: "serviceId", // field name in the row object
            label: "Service ID", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 2
        {
            name: "serviceType",
            label: "Type",
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "startDate",
            label: "Start Date",
            options: {
                filter: true,
            },
        },
        // 4
        {
            name: "expiryDate",
            label: "Validity Date",
            options: {
                filter: true,
            },
        },
        // 5
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
                    else if (value === 'New')
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: blue[200], color: blue[800] }}>
                            New
                        </small>;
                    else if (value === 'Draft')
                        return <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: blue[200], color: blue[800] }}>
                            Draft
                        </small>;
                }
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
                        editPath={isDisabled ? null : "" + tableMeta.rowData[0]}
                        removeEventHandler={isDisabled ? null : (e) => handleDeleteConfirm(e, tableMeta.rowData[0])}
                    />
                }
            },
        }
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

    let servicesSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Services</Typography>;
    let documentsSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium">Documents</Typography>;

    useEffect(() => {
        // sendRequest(`${COMMON_ATTACH_LIST_BY_REFID_URL}${refId}`, "list", "get");

        setRows(suppDocsDB);
        // eslint-disable-next-line
    }, []);

    return (
        <React.Fragment>
            <C1TabContainer>
                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"Service Subscription"}</Box>
                </Grid> */}

                <Grid item lg={12} md={12} xs={12}>
                    <C1OutlinedDiv label="Service Subscription">
                        <C1DataTable url="/api/process/all"
                            columns={columns}
                            title={servicesSubTitle}
                            defaultOrder="appId"
                            dbName={servicesDB}
                            showAdd={!isDisabled}
                            isServer={false}
                            isShowViewColumns={true}
                            isShowDownload={false}
                            isShowPrint={false}
                            isShowFilter={false}
                        />
                    </C1OutlinedDiv>
                </Grid>

                {/* <Grid className={classes.gridContainer} item xs={12}>
                    <Box className={title.root}>{"Service Details"}</Box>
                </Grid> */}

                <Grid className={classes.gridContainer} item xs={12}>
                    <C1OutlinedDiv label="Service Details">
                        <Grid container spacing={3}>
                            <Grid item lg={3} md={6} xs={12}>
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12}>
                                        <C1InputField
                                            label="Service ID"
                                            name="usrName"
                                            disabled={true}
                                            inputProps={{
                                                maxLength: 35
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.usrName)}
                                            error={errors.coreUsr && errors.coreUsr['usrName'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrName']) || ''} />

                                        <C1SelectField
                                            label="Service Type"
                                            name="usrPassNid"
                                            disabled={true}
                                            required
                                            inputProps={{
                                                maxLength: 20
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.usrPassNid)}
                                            error={errors.coreUsr && errors.coreUsr['usrPassNid'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrPassNid']) || ''} />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12}>
                                        <C1DateField
                                            label={"Start Date"}
                                            name="usrContact.contactTel"
                                            disabled={false}
                                            required
                                            inputProps={{
                                                maxLength: 1024,
                                                placeholder: locale("common:common.placeHolder.contactTel")
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.usrContact?.contactTel)}
                                            error={errors.coreUsr && errors.coreUsr['usrContact.contactTel'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrContact.contactTel']) || ''} />

                                        <C1DateField
                                            label={"Validity Date"}
                                            name="usrContact.contactEmail"
                                            disabled={false}
                                            required
                                            inputProps={{
                                                maxLength: 128
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.usrContact?.contactEmail)}
                                            error={errors.coreUsr && errors.coreUsr['usrContact.contactEmail'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrContact.contactEmail']) || ''} />

                                        <FormGroup>
                                            <FormControlLabel control={<Switch defaultChecked="true"
                                                disabled={false}
                                                name=""
                                                onChange={""}
                                            />} label="Auto Renewed"
                                                labelPlacement="start" />
                                        </FormGroup>

                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item lg={3} md={6} xs={12} >
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1SelectField
                                            name=""
                                            label="Fee Type"
                                            value={""}
                                            onChange={handleInputChange}
                                            disabled={false}
                                            isServer={true}
                                            optionsMenuItemArr={Object.values(FeeTypes).map((type) => {
                                                return <MenuItem value={type.code} key={type.code}>{type.desc}</MenuItem>
                                            })}
                                        />
                                        <C1InputField
                                            label="Fee Amount (IDR)"
                                            name="usrAddr.addrLn2"
                                            disabled={false}
                                            required={false}
                                            inputProps={{
                                                maxLength: 64
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.usrAddr?.addrLn2)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn2'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn2']) || ''} />
                                        <C1InputField
                                            label="Subscription Fee (IDR)"
                                            name="usrAddr.addrLn3"
                                            disabled={false}
                                            required={false}
                                            inputProps={{
                                                maxLength: 64
                                            }}
                                            onChange={handleInputChange}
                                            value={getValue(inputData?.usrAddr?.addrLn3)}
                                            error={errors.coreUsr && errors.coreUsr['usrAddr.addrLn3'] !== undefined}
                                            helperText={(errors.coreUsr && errors.coreUsr['usrAddr.addrLn3']) || ''} />
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item lg={3} md={6} xs={12}>
                                <Grid container alignItems="center" spacing={3} className={classes.gridContainer}>
                                    <Grid item xs={12} >
                                        <C1SelectField
                                            name=""
                                            label="Payment Type"
                                            value={""}
                                            onChange={handleInputChange}
                                            disabled={false}
                                            isServer={true}
                                            optionsMenuItemArr={Object.values(PaymentTypes).map((type) => {
                                                return <MenuItem value={type.code} key={type.code}>{type.desc}</MenuItem>
                                            })}
                                        />
                                        <C1SelectField
                                            name=""
                                            label="Billing"
                                            value={""}
                                            onChange={handleInputChange}
                                            disabled={false}
                                            isServer={true}
                                            optionsMenuItemArr={Object.values(BillingTypes).map((type) => {
                                                return <MenuItem value={type.code} key={type.code}>{type.desc}</MenuItem>
                                            })}
                                        />
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item lg={12} md={12} xs={12}>
                                <Divider />
                            </Grid>
                            <Grid item lg={12} md={12} xs={12}>
                                <C1DataTable url="/api/process/all"
                                    columns={cols}
                                    title={documentsSubTitle}
                                    defaultOrder="appId"
                                    dbName={suppDocsDB}
                                    showAdd={!isDisabled}
                                    isServer={false}
                                    isShowViewColumns={true}
                                    isShowDownload={false}
                                    isShowPrint={false}
                                    isShowFilter={false}
                                />
                            </Grid>
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
export default AccountDetailsService;