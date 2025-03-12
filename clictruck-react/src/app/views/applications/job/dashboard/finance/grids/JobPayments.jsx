import { Backdrop, Button, ButtonGroup, Checkbox, CircularProgress, Grid, IconButton, Link, Tooltip } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import { Close, CloudDownloadOutlined, DoneAll, EditOutlined, Visibility, VisibilityOutlined } from "@material-ui/icons";
import AssignmentTurnedInOutlinedIcon from '@material-ui/icons/AssignmentTurnedInOutlined';
import HistoryIcon from '@material-ui/icons/HistoryOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';
import DeleteOutlineOutlinedIcon from '@material-ui/icons/DeleteOutlineOutlined';
import C1Alert from "app/c1component/C1Alert";
import C1DataTable from 'app/c1component/C1DataTable';
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { dialogStyles, iconStyles } from "app/c1utils/styles";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import { ConfirmationDialog, MatxLoading } from "matx";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { customFilterDateDisplay, downloadFile, formatDate } from "app/c1utils/utility";
import history from "history.js";
import DataTable from "app/atomics/organisms/DataTable";
import C1Warning from "app/c1component/C1Warning";

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

/**
 * @description This is for listing the payment transactions. One transaction can contain multiple jobs.
 * Job payments for CO/FF inbound payment listing. This should only have one active record, as static VA can only be binded one at a time.
 */
const JobPayments = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    const bdClasses = useStyles();
    const { t } = useTranslation(["buttons", "listing", "common", "status", "payments"]);

    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();


    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });


    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("common:common.msg.deleted"),
        severity: 'success'
    });

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);


    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);

    const [success, setSuccess] = useState(false);

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "", hlMsg: "", subMsg: "" });

    const paymentHistoryTableCols = [
        {
            name: "ptxId", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.paymentId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        {
            name: "ptxDtCreate", // field name in the row object
            label: t("payments:paymentDetails.label.billingDate"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "ptxAmount", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.amount"), // column title that will be shown in table
            options: {
                sort: true,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let ccy = tableMeta.rowData[3];
                    return (
                        <div className={bdClasses.amountCell}>
                            <p>{value ? value.toLocaleString(ccy == "IDR" ? "in-ID" : "en-US", { maximumFractionDigits: 0, style: "currency", currency: ccy }) : 0}</p>
                        </div>
                    )
                }
            },
        },
        {
            name: "tmstCurrency.ccyCode", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.currency"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "ptxDtCreate", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.paymentDate"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "ptxDtPaid", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.paidDate"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "ptxPaymentState", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.status"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                filterOptions: {
                    names: ['NEW', 'SUCCESS', 'PARTIAL_FAIL', 'FAIL'],
                    // renderValue: v => v
                    renderValue: v => {
                        switch (v) {
                            case "NEW": return "New";
                            case "SUCCESS":
                            case "COMPLETED": return "Success";
                            case "PARTIAL_FAIL": return "Partial Fail";
                            case "FAIL": return "Fail";
                            default: break;
                        }
                    }
                },
                filterType: 'dropdown',
                customBodyRender: (status, tableMeta, updateValue) => {
                    switch (status) {
                        case 'NEW':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                New
                            </small>)
                        case 'PAID':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                Paid
                            </small>)
                        case 'SUCCESS':
                            return (<small className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#00B050', color: '#ffffff' }}>
                                Success
                            </small>)
                        case 'PAYING':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#D96B67', color: '#ffffff' }}>
                                Paying
                            </small>)
                        case 'FAIL':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#D96B67', color: '#ffffff' }}>
                                Fail
                            </small>)
                        case 'CANCELLED':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#D96B67', color: '#ffffff' }}>
                                Cancelled
                            </small>)

                        default: break;
                    }
                }
            },
        },
        {
            name: "", // field name in the row object
            label: "", // column title that will be shown in table
            options: {
                sort: false,
                filter: false,
                customBodyRender: (noValue, tableMeta, updateValue) => {
                    const ptxId = tableMeta.rowData[0];
                    const details = tableMeta.rowData[8];
                    const paymentState = tableMeta.rowData[6];
                    let jobsList = [];

                    if (details !== undefined && details !== null) {
                        jobsList = details
                            .map((item) => item.jobId)
                            .filter((value, index, self) => self.indexOf(value) === index)
                    }
                    return <Grid container direction="row" alignItems="center" justifyContent="flex-start">
                        <Grid container item justifyContent="center" spacing={3}>
                            <Grid item xs={4} >
                                <C1LabeledIconButton tooltip="Download" label={t("buttons:download")}
                                    action={() => handleActionDownload(ptxId)}>
                                    <CloudDownloadOutlined />
                                </C1LabeledIconButton>
                            </Grid>
                            <Grid item xs={4}>
                                <C1LabeledIconButton tooltip="Payment Details" label={t("buttons:view")}
                                    action={() => history.push({
                                        pathname: `/applications/finance/payments/transactions/details/${ptxId}`,
                                        state: { from: "/applications/services/job/coff/truck" }
                                    })}>
                                    <VisibilityOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>
                            {(paymentState === 'NEW' || paymentState === 'PAYING') && <Grid item xs={4}>
                                <C1LabeledIconButton tooltip="Cancel Payment" label={t("buttons:cancel")}
                                    action={() => handleCancelConfirm(ptxId)}>
                                    <DeleteOutlineOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>}
                        </Grid>
                    </Grid >
                }
            },
        },

    ];


    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    const handleCancelConfirm = (id) => {
        setOpenActionConfirm({ ...openActionConfirm, action: "CANCEL", open: true, rowId: id });
    }

    const handleYesAction = () => {
        setLoading(true);
        let txnId = openActionConfirm?.rowId;
        console.log("txnId", txnId);
        setOpenActionConfirm({ ...openActionConfirm, open: false });
        sendRequest(`/api/v1/clickargo/clictruck/payment/txn/INBOUND/${txnId}`, "cancelpayment", "delete");

    }

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" })
    };


    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);

    useEffect(() => {
        setTimeout(() => setSnackBarOptions(defaultSnackbarValue), 100);
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "cancelpayment":
                    setShowHistory(true);
                    break;
                case "downloadFile": {
                    downloadFile(res?.data?.filename, res?.data?.data)
                    break;
                }
                default:
                    break;
            }
        }


        if (validation) {
            setLoading(false);
            let keyList = Object.keys(validation);
            if (keyList.length > 0) {
                for (let key of keyList) {
                    setWarningMessage({ open: true, msg: validation[key], hlMsg: null, subMsg: null });

                }
            }
        }
        // eslint-disable-next-line
    }, [isLoading, res, error, urlId]);

    const handleActionDownload = (id) => {
        setLoading(true)
        sendRequest(`/api/v1/clickargo/clictruck/attach/byParam/ctPayment/${id}`, "downloadFile", "get");
    }

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;
    if (success) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleCloseSnackBar}
            autoHideDuration={3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleCloseSnackBar} severity={snackBarState.severity}>
                {snackBarState.msg}
            </C1Alert>
        </Snackbar>;
    }


    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}

            <ConfirmationDialog
                open={openActionConfirm?.open}
                onConfirmDialogClose={() => setOpenActionConfirm({ ...openActionConfirm, action: null, open: false })}
                text={t("listing:finance.confirmation.msg", { action: openActionConfirm?.action })}
                title={t("listing:finance.confirmation.title")}
                onYesClick={(e) => handleYesAction(e)} />

            <DataTable
                url="/api/v1/clickargo/payment/paymentTxn"
                columns={paymentHistoryTableCols}
                defaultOrder="ptxDtCreate"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={filterBy}
                isRowSelectable={false}
                isShowToolbar={true}
                isShowDownloadData={false}
                filterBy={filterBy}
                showActiveHistoryButton={toggleHistory}
            />

            {/* For downloading of BL */}
            <Backdrop open={loading && openActionConfirm?.action === 'CANCEL'} className={bdClasses.backdrop}> <CircularProgress color="inherit" /></Backdrop>

            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />

        </React.Fragment >
    );

};

export default withErrorHandler(JobPayments);