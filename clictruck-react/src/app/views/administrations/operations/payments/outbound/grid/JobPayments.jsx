import { Button, ButtonGroup, Grid, Checkbox, Popover, Typography, Tooltip, } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import ChatBubbleIcon from "@material-ui/icons/ChatBubble";
import BusinessIcon from "@material-ui/icons/Business";
import DeleteOutlineOutlinedIcon from '@material-ui/icons/DeleteOutlineOutlined';
import ListIcon from "@material-ui/icons/List";
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1DataTable from 'app/c1component/C1DataTable';
import C1PopUp from "app/c1component/C1PopUp";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import C1Warning from "app/c1component/C1Warning";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import { AccountTypes, JobStates, PaymentState, Roles } from "app/c1utils/const";

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
 * @description This is the listing for outbound payments subject for GLI FINANCE VERIFICATION and FINANCE APPROVAL.
 */
const JobPayments = ({ }) => {

    const bdClasses = useStyles();
    const { t } = useTranslation(["buttons", "listing", "common", "status", "payments"]);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const { user } = useAuth();

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" },
        { attribute: "invoiceFromAccn.accnName", value: null }
    ]);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);

    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });

    let rowData = [];
    let statusData = [];
    const [selectedRowIds, setSelectedRowIds] = useState([]);
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });
    const [elementPick, setElementPick] = useState(null)
    const [anchorEl, setAnchorEl] = React.useState(null);
    const openPopover = Boolean(anchorEl);
    const [showMultiSelectActionPopup, setShowMultiSelectActionPopup] = useState(false);

    const isGLIVer = user?.authorities?.some((item) => item.authority === Roles.FINANCE_VERIFIER.code)

    const [multiOptions, setMultiOptions] = useState([])
    const [multiSelectResponseData, setMultiSelectResponseData] = useState(null)

    const paymentHistoryTableCols = [
        {
            name: "checkbox",
            label: "",
            options: {
                display: false,
                sort: false,
                filter: false,
                // display: !showHistory,
                viewColumns: false,
                customHeadLabelRender: (columnMeta, handleToggleColumn, sortOrder) => {
                    return (
                        <Checkbox
                            disableRipple={true}
                            checked={
                                selectedRowIds.length > 0 &&
                                selectedRowIds.length === rowData.length
                            }
                            onChange={({ target: { checked } }) => {
                                try {
                                    if (checked === true) {
                                        setSelectedRowIds(rowData)
                                    } else {
                                        setSelectedRowIds([])
                                    }
                                } catch (e) {
                                    console.log(e)
                                }
                            }
                            }
                        />
                    );
                },
                customBodyRender: (emptyStr, tableMeta, updateValue) => {
                    rowData = tableMeta.tableData.map((data) => data[1]);
                    // statusData = tableMeta.tableData.map((data)=> data[7])

                    const id = tableMeta.rowData[1];
                    const paymentStatus = tableMeta.rowData[7]

                    return (
                        <React.Fragment>
                            <Checkbox
                                disableRipple={true}
                                checked={
                                    selectedRowIds.includes(id)
                                }
                                onChange={({ target: { checked } }) => {
                                    try {
                                        if (checked === true) {
                                            setSelectedRowIds(
                                                selectedRowIds
                                                    .filter((rowId) => rowId !== id)
                                                    .concat(id)
                                            )
                                        } else {
                                            if (selectedRowIds.length === 1) {
                                                setSelectedRowIds(
                                                    selectedRowIds.filter((rowId) => rowId !== id)
                                                );
                                            } else {
                                                setSelectedRowIds(
                                                    selectedRowIds.filter((rowId) => rowId !== id)
                                                );
                                            }
                                        }
                                    } catch (e) {
                                        console.log(e);
                                    }
                                }}
                            />
                        </React.Fragment>
                    );
                },
            },
        },
        {
            name: "ptxId", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.paymentId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true
            },
        },
        // {
        //     name: "ptxDtCreate", // field name in the row object
        //     label: t("payments:paymentDetails.label.billingDate"), // column title that will be shown in table
        //     options: {
        //         sort: true,
        //         filter: true,
        //         filterType: 'custom',
        //         customFilterListOptions: {
        //             render: v => v.map(l => l),
        //             update: (filterList, filterPos, index) => {
        //                 filterList[index].splice(filterPos, 1);
        //                 return filterList;
        //             }
        //         },
        //         filterOptions: {
        //             display: customFilterDateDisplay
        //         },
        //         customBodyRender: (value, tableMeta, updateValue) => {
        //             return formatDate(value, true);
        //         }
        //     },
        // },
        {
            name: "ptxAmount", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.amount"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let ccy = tableMeta.rowData[3];
                    return <span className={bdClasses.amountCell}>{value ? value.toLocaleString(ccy == "IDR" ? "in-ID" : "en-US", { maximumFractionDigits: 0, style: "currency", currency: ccy }) : 0}</span>
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
            name: "ptxDtDue", // field name in the row object
            label: t("payments:paymentDetails.list.table.headers.dueDate"), // column title that will be shown in table
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
            label: t("payments:paymentDetails.list.table.headers.txnStatus"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
                filterOptions: {
                    names: ['NEW', 'SUCCESS', 'PARTIAL_FAIL', 'FAILED', 'VER', 'APP'],
                    // renderValue: v => v
                    renderValue: v => {
                        switch (v) {
                            case "NEW": return "New";
                            case "VER": return "Verified";
                            case "APP": return "Approved";
                            case "SUCCESS":
                            case "COMPLETED": return "Success";
                            case "PARTIAL_FAIL": return "Partial Fail";
                            case "FAILED": return "Failed";
                            case "CANCELLED": return "Cancelled";
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
                                {t("status:new")}
                            </small>);
                        case 'VERIFIED':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                {t("status:verified")}
                            </small>)
                        case 'VER_BILL':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                {t("status:verifiedBill")}
                            </small>)
                        case 'APPROVED':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                {t("status:approved")}
                            </small>);
                        case 'APP_BILL':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                {t("status:approvedBill")}
                            </small>)
                        case 'PAID':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#92D050', color: '#ffffff' }}>
                                {t("status:paid")}
                            </small>)
                        case 'SUCCESS':
                            return (<small className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#00B050', color: '#ffffff' }}>
                                {t("status:success")}
                            </small>)
                        case 'PAYING':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#D96B67', color: '#ffffff' }}>
                                {t("status:paying")}
                            </small>)
                        case 'FAIL':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#D96B67', color: '#ffffff' }}>
                                {t("status:fail")}
                            </small>)
                        case 'CANCELLED':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#D96B67', color: '#ffffff' }}>
                                {t("status:cancelled")}
                            </small>)
                        case 'FAILED':
                            return (<small
                                className="px-3 py-6px border-radius-4"
                                style={{ backgroundColor: '#5C131E', color: '#ffffff' }}>
                                {t("status:failed")}
                            </small>)


                        default: break;
                    }
                }
            },
        },
        {
            name: "", // field name in the row object
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { justifyContent: 'center', paddingLeft: "3%" } } },
                customBodyRender: (noValue, tableMeta, updateValue) => {

                    const ptxId = tableMeta.rowData[1];
                    const details = tableMeta.rowData[8];
                    const paymentState = tableMeta.rowData[7];

                    let jobsList = [];

                    if (details !== undefined && details !== null) {
                        jobsList = details
                            .map((item) => item.jobId)
                            .filter((value, index, self) => self.indexOf(value) === index)
                    }

                    let isAllowDelete = (['NEW', 'PAYING', 'FAIL'].includes(paymentState));
                    return <Grid container direction="row" justifyContent="flex-start" alignItems="center">
                        <Grid container item justifyContent="center" spacing={3}>
                            <Grid item xs={4} >
                                <C1LabeledIconButton tooltip="Payment Details" label={t("buttons:view")}
                                    action={() => history.push({
                                        pathname: `/applications/finance/payments/transactions/view/${ptxId}`,
                                        state: { from: "/opadmin/outboundpayments" }
                                    })}>
                                    <VisibilityOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>
                            {/* <Grid item xs={4}>
                                {isAllowDelete && <C1LabeledIconButton tooltip="Cancel Payment" label={t("buttons:cancel")}
                                    action={() => handleCancelConfirm(ptxId)}>
                                    <DeleteOutlineOutlinedIcon />
                                </C1LabeledIconButton>}
                            </Grid> */}
                        </Grid>
                    </Grid >
                }
            },
        },
    ];

    // <====== multi select, pop over & warning message ======>

    const handleMultiSelectedTransaction = (type) => {
        setAnchorEl(null);
        setLoading(true)
        let reqBody = {
            action: type,
            accType: AccountTypes.ACC_TYPE_SP.code,
            role: isGLIVer ? Roles.FINANCE_VERIFIER.code : Roles.FINANCE_APPROVER.code,
            id: selectedRowIds,
        };
        sendRequest("api/v1/clickargo/clictruck/payments", "multiSelect", "post", reqBody)

    }

    const handleOpenPopover = (e) => {
        let query = selectedRowIds.join(';')
        let gliVer = isGLIVer ? Roles.FINANCE_VERIFIER.code : Roles.FINANCE_APPROVER.code
        setElementPick(e.currentTarget)
        if (selectedRowIds.length === 0) {
            setWarningMessage({ open: true, msg: t("listing:payments.errorNoTransactionSelectTitle") });
        } else {
            sendRequest(`api/v1/clickargo/clictruck/payments/action?accnType=${AccountTypes.ACC_TYPE_SP.code}&role=${gliVer}&paymentId=${query}`, "getMultiOptions", "get")
        }
    }

    const handleClose = () => {
        setAnchorEl(null);
        setSelectedRowIds([])
        setMultiOptions([])
    };

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleMultiSelectActionPopup = () => {
        if (showMultiSelectActionPopup === true) {
            setAnchorEl(null);
            setSelectedRowIds([]);
            setShowMultiSelectActionPopup(false);
        } else {
            setShowMultiSelectActionPopup(true);
        }
    };
    const messagePopup = React.useMemo(() => {
        let str = ""
        let data = multiSelectResponseData?.failed

        if (data?.length > 0) {
            for (let i = 0; i < data?.length; i++) {
                str += `paymentId: ${data[i].id}\nexception: ${data[i].reason}\n\n\n`
            }
        } else {
            if (multiSelectResponseData !== undefined && multiSelectResponseData !== null) {
                str = "All process is success!"
            } else {
                str = ""
            }
        }

        return str
    }, [multiSelectResponseData]);

    // <====== multi select, pop over & warning message ======>

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
                    setLoading(false);
                    break;
                case "getMultiOptions": {
                    let dataOpt = res?.data
                    if (dataOpt?.actions?.length > 0) {
                        setMultiOptions(dataOpt?.actions)
                        setAnchorEl(elementPick)
                    } else {
                        setElementPick(null)
                        setAnchorEl(null)
                        setSelectedRowIds([])
                        setWarningMessage({ open: true, msg: t("listing:payments.errorThereIsNoOption") });
                    }
                    break
                }
                case "multiSelect":
                    if (res?.data) {
                        if (res?.data?.suspended === true) {
                            setWarningMessage({ open: true, msg: t("listing:payments.errorYourAccountIsSuspended") });
                            setLoading(false)
                        } else {
                            setMultiSelectResponseData(res?.data)
                            setRefresh(true);
                            setSuccess(true);
                            setLoading(false)
                            setTimeout(() => setShowMultiSelectActionPopup(true), 500);
                        }
                    }
                    break
                default:
                    break;
            }
        }

        // eslint-disable-next-line
    }, [isLoading, res, error, urlId]);

    const handleYesAction = () => {
        setLoading(true);
        let txnId = openActionConfirm?.rowId;
        console.log("txnId", txnId);
        setOpenActionConfirm({ ...openActionConfirm, open: false });
        sendRequest(`/api/v1/clickargo/clictruck/payment/txn/OUTBOUND/${txnId}`, "cancelpayment", "delete");

    }

    const handleCancelConfirm = (id) => {
        setOpenActionConfirm({ ...openActionConfirm, action: "CANCEL", open: true, rowId: id });
    }

    // usable function
    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

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
                onYesClick={(e) => handleYesAction(e)}
            />
            <Popover
                open={openPopover}
                anchorEl={anchorEl}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: "bottom",
                    horizontal: "left",
                }}
            >
                <Grid
                    container
                    direction={"column"}
                    // alignItems={"flex-start"}
                    style={{ minWidth: "100px", padding: "10px" }}
                >
                    {multiOptions.map((item, i) => {
                        return (
                            <Grid item key={i}>
                                <Button
                                    style={{ textTransform: "none" }}
                                    onClick={() => handleMultiSelectedTransaction(item)}
                                >
                                    <Typography>{item}</Typography>
                                </Button>
                            </Grid>
                        )
                    })}
                </Grid>
            </Popover>

            <C1PopUp
                title={`Multi-records Request: ${isGLIVer ? "Verified" : "Approve"}`}
                openPopUp={showMultiSelectActionPopup}
                setOpenPopUp={handleMultiSelectActionPopup}
                maxWidth={"md"}
            >
                <C1TabContainer>
                    <Grid item lg={6} md={6} xs={12}>
                        <C1CategoryBlock icon={<BusinessIcon />} title={"Request Details"}>
                            <C1InputField disabled value={multiSelectResponseData?.id?.length} label="No. Records" />
                            <C1InputField disabled value={multiSelectResponseData?.success?.length} label="No. Success" />
                            <C1InputField disabled value={multiSelectResponseData?.failed?.length} label="No. Failed" />
                        </C1CategoryBlock>
                    </Grid>
                    <Grid item lg={6} md={6} xs={12}>
                        <C1CategoryBlock icon={<ChatBubbleIcon />} title={"Exceptions"}>
                            <C1TextArea
                                inputProps={{
                                    readOnly: true,
                                }}
                                multiline={true}
                                label=""
                                // disabled={true}
                                rows={10}
                                rowsMax={10}
                                value={`${messagePopup}`}
                            />
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>
            </C1PopUp>

            <DataTable
                url="/api/v1/clickargo/payment/paymentTxn"
                columns={paymentHistoryTableCols}
                title=""
                defaultOrder="ptxDtCreate"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowFilterChip
                filterBy={filterBy}
                guideId={""}
                customRowsPerPage={[10, 20]}
                showActiveHistoryButton={toggleHistory}
            // showMultiSelectActionButton={[
            //     {
            //         show: true,
            //         label: "ACTION",
            //         action: handleOpenPopover,
            //         icon: <ListIcon />,
            //     },
            // ]}
            />

            {/* Warning message when there is no selected transaction */}
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />

        </React.Fragment>
    )
}

export default withErrorHandler(JobPayments)