import { Typography, makeStyles } from "@material-ui/core";
import { Divider, Grid, IconButton, Paper, Tooltip, InputAdornment, Button, ButtonGroup } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import GetAppIcon from "@material-ui/icons/GetApp";
import HistoryIcon from '@material-ui/icons/HistoryOutlined';
import AssignmentTurnedInOutlinedIcon from '@material-ui/icons/AssignmentTurnedInOutlined';

import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DateField from "app/c1component/C1DateField";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1InputField from "app/c1component/C1InputField";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import useAuth from "app/hooks/useAuth";
import { iconStyles } from "app/c1utils/styles";
import { customFilterDateDisplay, formatDate, getValue, isArrayNotEmpty, previewPDF } from "app/c1utils/utility";
import { MatxLoading } from "matx";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    amountCell: {
        justifyContent: 'center',
        textAlign: 'right',
        display: 'flex',
        flex: 1
    }
}));

const SageARReportDetails = ()=>{
    const { user } = useAuth();
    const { t } = useTranslation(["buttons", "payments", "common", "listing"]);
    const classes = iconStyles();
    const bdClasses = useStyles();
    const history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [isRefresh, setRefresh] = useState(false);

    const columns = [
        {
            name: "id",
            options: {
                display: "excluded"
            }
        },
        {
            name: "batch",
            label: "Batch No",
            options: {
                sort: true,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const data = tableMeta.rowData[1]
                    return batchFormat(data)

                }
            }
        },
        {
            name: "reportName",
            label: "Report Name",
            options: {
                sort: true,
                filter: false,
            }
        },
        {
            name: "records",
            label: "No. Records",
            options:{
                sort: true,
                filter: false,
            }
        },
        {
            name: "startPeriod",
            label: "Period Start",
            options:{
                sort: true,
                filter: true,
                filterType: 'custom',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "endPeriod",
            label: "Period End",
            options:{
                sort: true,
                filter: true,
                filterType: 'custom',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            }
        },
        {
            name: "status",
            label: "Status",
            options:{
                sort: true,
                filter: true,
            }
        },
        {
            name: "action",
            label: t("listing:common.action"),
            options:{
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue)=>{

                    return <Grid container direction="row" justifyContent='space-between' alignItems="center">
                         <Grid item sm={6} xs={6}>
                            <C1LabeledIconButton
                                tooltip={t("buttons:download")}
                                label={t("buttons:download")}
                                action={handleDownloadToggle}
                            >
                                <GetAppIcon color="primary" />
                            </C1LabeledIconButton>
                        </Grid>
                    </Grid>
                }
            }
        }
    ]
    const batchFormat = (value)=>{
        if(value< 10){
            return `000${value}`
        }else if(value>=10 && value < 100){
            return `00${value}`
        }else if(value>= 100){
            return `0${value}`
        }else{
            return `${value}`
        }
    }

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const handleDownloadToggle = ()=>{
        console.log("Test download")
    }

    const toggleHistory = (filter) => {
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    return loading ? <MatxLoading/> : (
        <React.Fragment>
            <C1FormDetailsPanel
            breadcrumbs={[
                {name: "AR Report"}
            ]}
            initialValues={{}}
            snackBarOptions={snackBarOptions}
            isLoading={loading}
            title={"AR Report"}
            >
                {(props)=> (

                <Grid item xs={12}>
                    <Paper>
                        <C1TabInfoContainer guideId='clicdo.payment.details.table' title="empty" guideAlign="left" open={false}>
                            <C1DataTable
                                columns={columns}
                                isServer={true}
                                defaultOrder="batch"
                                defaultOrderDirection={"desc"}
                                isShowViewColumns={true}
                                isShowDownload={true}
                                isShowPrint={true}
                                isShowFilter={true}
                                url={"api/v1/clickargo/clictruck/gli/finance/ar-report"}
                                isRefresh={isRefresh}
                                isShowToolbar={true}
                                isShowPagination={true}
                                viewTextFilter={
                                    <React.Fragment key={0}>
                                        <ButtonGroup style={{ marginRight: -24 }} color="primary" key="viewTextFilter" aria-label="outlined primary button group">
                                            <Button key="history" startIcon={<HistoryIcon />} size="small" variant={showHistory ? "contained" : null} onClick={() => toggleHistory("history")}>{t("listing:common.history")}</Button>
                                            <Button key="active" startIcon={<AssignmentTurnedInOutlinedIcon />} variant={!showHistory ? "contained" : null} size="small" onClick={() => toggleHistory("active")}>{t("listing:common.active")}</Button>
                                        </ButtonGroup>
                                    </React.Fragment>
                                }
                            />
                        </C1TabInfoContainer>
                    </Paper>
                </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    )

}

export default SageARReportDetails