import { Backdrop, CircularProgress, Grid } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import { VisibilityOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import C1Alert from "app/c1component/C1Alert";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import { JobStates, PaymentState, Roles, ShipmentTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, customNumFieldDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import DataTable from "app/atomics/organisms/DataTable";

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

const InvoiceApprovals = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    // const classes = iconStyles();
    // const dialogClasses = dialogStyles();
    const bdClasses = useStyles();

    const { t } = useTranslation(["buttons", "listing", "common", "status"]);

    // const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    // const { viewType } = useParams()

    // eslint-disable-next-line
    const [confirm, setConfirm] = useState({ id: null });
    // eslint-disable-next-line
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);

    const [success, setSuccess] = useState(false);
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });

    // const dispatch = useDispatch();

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);


    const { user } = useAuth();

    const isFinance = user?.authorities.some(el => el?.authority === Roles.FF_FINANCE.code)
    const isOfficer = user?.authorities.some(el => el?.authority === Roles.OFFICER.code)

    const columns = [
        {
            name: "jobId",
            label: t("listing:trucklist.jobid")
        },
        {
            name: "tckJob.tckMstShipmentType.shtId",
            label: t("listing:trucklist.type"),
            options: {
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(ShipmentTypes),
                    renderValue: v => {
                        return ShipmentTypes[v].desc;
                    }
                },
            }
        },
        {
            name: "invoiceFromAccn.accnName",
            label: t("listing:finance.invFrom"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "jobNoTrips",
            label: t("listing:finance.noOfTrips"),
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
                    display: customNumFieldDisplay
                },
            }
        },
        {
            name: "jobTotalCharge",
            label: t("listing:finance.charges"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className={bdClasses.amountCell}>
                            <p> {value ? value.toLocaleString("id-ID", { maximumFractionDigits: 0, style: "currency", currency: "IDR" }) : "-"}</p>
                        </div>
                    )
                },
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customNumFieldDisplay
                }
            }
        },
        {
            name: "jobTotalReimbursements",
            label: t("listing:finance.reimbursement"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className={bdClasses.amountCell}>
                            <p> {value ? value.toLocaleString("id-ID", { maximumFractionDigits: 0, style: "currency", currency: "IDR" }) : "-"}</p>
                        </div>
                    )
                },
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customNumFieldDisplay
                }
            }
        },
        {
            name: "billingDate",
            label: t("listing:finance.billingDate"),
            options: {
                filter: true,
                display: true,
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
            }
        },
        {
            name: "tckJob.tckMstJobState.jbstId",
            label: t("listing:finance.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(JobStates),
                    renderValue: v => {
                        return JobStates[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return JobStates[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "verifiedDate",
            label: t("listing:finance.verifiedDate"),
            options: {
                filter: true,
                display: true,
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
            }
        },
        {
            name: "verifiedBy",
            label: t("listing:finance.verifiedBy"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "jobInPaymentState",
            label: t("listing:finance.paymentState"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(PaymentState),
                    renderValue: v => {
                        return PaymentState[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return PaymentState[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "",
            label: t("listing:common.action"),
            options: {
                filter: false,
                display: true,
                sort: false,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { textAlign: "center" } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const jobId = tableMeta.rowData[0];

                    return <C1DataTableActions>
                        <Grid container direction="row" justifyContent="center" alignItems="center">
                            <Grid item xs={4}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={() => history.push({
                                        pathname: `/applications/services/job/truck/view`,
                                        state: { from: "/applications/services/job/coff/truck", jobId }
                                    })}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </Grid>

                        </Grid>
                    </C1DataTableActions>
                }
            }
        },
    ];

    const handleActionHandler = (e) => {
        if (confirm && !confirm.id)
            return;
        setLoading(true);
        if (openActionConfirm && openActionConfirm.action === "DELETE") {
            setOpen(false);
        } else if (openActionConfirm && openActionConfirm.action === "CANCEL") {
            setOpen(false);
        } else if (openActionConfirm && openActionConfirm.action === "REJECT") {
            setOpen(false);
        }
    }

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);


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
            {confirm && confirm.id && (
                <ConfirmationDialog
                    open={openActionConfirm.open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm.id })}
                    onYesClick={() => handleActionHandler()}
                    onConfirmDialogClose={() => setOpenActionConfirm({ ...openActionConfirm, open: false })}
                />
            )}

            <DataTable
                url="/api/v1/clickargo/clictruck/invoice/job/verified"
                columns={columns}
                title=""
                defaultOrder="verifiedDate"
                defaultOrderDirection="desc"
                isServer={true}
                isShowViewColumns={true}
                isShowDownload={true}
                isShowPrint={true}
                isShowFilter={true}
                isRefresh={isRefresh}
                isShowFilterChip
                filterBy={filterBy}
                guideId={"clicdo.doi.ff.bl.list.table"}
                showActiveHistoryButton={toggleHistory}
            />

        </React.Fragment >
    );

};

export default withErrorHandler(InvoiceApprovals);