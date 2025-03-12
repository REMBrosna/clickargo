import { Box, Card, Divider, Grid, IconButton, Paper, Tooltip } from "@material-ui/core";
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
import GetAppIcon from '@material-ui/icons/GetApp';
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
import useHttp from "app/c1hooks/http";
import { MST_ACCN_TYPE_URL, MST_CTRY_URL, RegistrationStatus } from "app/c1utils/const";
import { BillingTypes, FeeTypes, PaymentTypes } from "app/c1utils/const";
import { isEditable } from "app/c1utils/utility";
import { getValue } from "app/c1utils/utility";

import { authLettersDB } from "../../../../../../fake-db/db/clickDOAuthorisations";

const DoJobAttachments = ({
    inputData,
    handleInputChange,
    handleInputAccnIdChange,
    errors, locale, viewType }) => {

    const classes = useStyles();
    const title = titleTab();
    const [rows, setRows] = useState([]);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    const cols = [
        {
            name: "attId", // field name in the row object
            label: "", // column title that will be shown in table
            options: {
                display: false,
                filter: false
            },
        },
        // 0
        {
            name: "taskId", // field name in the row object
            label: "Task ID", // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // 1
        {
            name: "attType",
            label: "Document Type",
            options: {
                filter: true,
            },
        },
        // 2
        {
            name: "authoriser",
            label: "Authoriser",
            options: {
                filter: true,
            },
        },
        // 3
        {
            name: "blNo",
            label: "Reference No.",
            options: {
                filter: true,
            },
        },
        // 4
        {
            name: "tckDo.doNo", // Updated to get from tckDo (manual add for now)
            label: "DO No.",
            options: {
                filter: true,
            },
        },
        // 5
        {
            name: "createDate",
            label: "Created At",
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
                    // return <C1DataTableActions
                    //     downloadPath={handleViewFile(`DOC123`)}
                    // />
                    return <C1DataTableActions>
                        <Tooltip title="Download">
                            <IconButton aria-label="View" type="button"
                                color="primary" onClick={(e) => console.log("Downloading")}>
                                <GetAppIcon />
                            </IconButton>
                        </Tooltip>
                    </C1DataTableActions>
                }
            },
        }
    ]

    let documentsSubTitle = <Typography variant="body2" className="m-0 mt-1 text-primary font-medium"> </Typography>;

    useEffect(() => {
        // sendRequest(`${COMMON_ATTACH_LIST_BY_REFID_URL}${refId}`, "list", "get");

        setRows(authLettersDB);
        // eslint-disable-next-line
    }, []);

    return (
        <React.Fragment>
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
                <Grid item lg={12} md={12} xs={12}>
                    {/* <C1OutlinedDiv label="Authorization Letters"> */}
                    <C1DataTable url="/api/process/all"
                        columns={cols}
                        title={documentsSubTitle}
                        defaultOrder="appId"
                        dbName={authLettersDB}
                        showAdd={!isDisabled}
                        isServer={false}
                        isShowViewColumns={false}
                        isShowDownload={false}
                        isShowPrint={false}
                        isShowFilter={false}
                    />
                    {/* </C1OutlinedDiv> */}
                </Grid>
                <Grid className={classes.gridContainer} item xs={12}>
                    {"*Click on the DOWNLOAD icon to download the respective documents. " +
                        "Power Of Authority, Letter of Assignment and Container Load Application documents " +
                        "are automatically generated by the system."}
                </Grid>
                <Grid item lg={12} md={12} xs={12}>
                    <C1Information information="claimJobAttachments" />
                </Grid>

            </C1TabContainer>
        </React.Fragment>
    );
};
export default DoJobAttachments;