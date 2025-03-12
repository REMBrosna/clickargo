import { Backdrop, CircularProgress, Grid } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import { Add, DeleteOutline, EditOutlined, VisibilityOutlined } from "@material-ui/icons";
import React from "react";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Information from "app/c1component/C1Information";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { TrackDeviceState } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: 999999,
        color: '#fff',
    },
    amountCell: {
        justifyContent: 'center',
        textAlign: 'right',
        display: 'flex',
        flex: 1
    }
}));

const OrderTermination = () => {

    const history = useHistory()
    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "payments"]);
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" }
    ]);

    // eslint-disable-next-line
    const [validationErrors, setValidationErrors] = useState({});
    // eslint-disable-next-line
    const [openWarning, setOpenWarning] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);

    // eslint-disable-next-line
    const [confirm, setConfirm] = useState({ id: null });
    const [open, setOpen] = useState(false);

    // eslint-disable-next-line
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });
    // const [inputData, setInputData] = useState({});
    // const [listCompany, setListCompany] = useState([]);
    // const [listVhicle, setListVhicle] = useState([]);
    // const [vhicleId, setVhicleId] = useState(null);
    const [dataStatus, setDataStatus] = useState("");
    const { user } = useAuth();
    const isRequester = user?.authorities?.some(item => item.authority.includes('SP_L1'));
    const isApproval = user?.authorities?.some(item => item.authority.includes('SP_FIN_HD'));

    const bdClasses = useStyles();

    const columns = [
        {
            name: "jtrId",
            label: t("listing:orderTermination.terminatId")
        },
        {
            name: "tcoreAccn.accnId",
            label: t("listing:orderTermination.accountCode")
        },
        {
            name: "tcoreAccn.accnName",
            label: t("listing:orderTermination.accountName")
        },
        {
            name: "jtrNoJobs",
            label: t("listing:orderTermination.numberOfJobs")
        },
        {
            name: "jtrJobsDnAmt",
            label: t("listing:orderTermination.totalDN")
        },
        {
            name: "jtrJobsPltfeeAmt",
            label: t("listing:orderTermination.TotalPF")
        },
        {
            name: "jtrState",
            label: t("listing:orderTermination.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(TrackDeviceState),
                    renderValue: v => {
                        return TrackDeviceState[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return TrackDeviceState[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "jtrDtCreate",
            label: t("listing:orderTermination.dateCreated"),
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
            },
        },
        {
            name: "jtrUidCreate",
            label: t("listing:orderTermination.createdBy")
        },
        {
            name: "jtrDtSubmit",
            label: t("listing:orderTermination.dateSubmitted"),
            options: {
                sort: true,
                filter: true,
                filterType: 'custom',
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
                filterOptions: {
                    display: customFilterDateDisplay
                },
            },
        },
        {
            name: "action",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const id = tableMeta.rowData[0];
                    const status = tableMeta.rowData[6]

                    return <Grid container direction="row" justifyContent="center" alignItems="center" style={{ minWidth: 120 }}>
                        <Grid container direction="row" justifyContent="flex-end" spacing={2}>
                            {
                                status === 'NEW' && (
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:edit")}
                                            label={t("buttons:edit")}
                                            action={(e) => handleEditClick(id)}
                                        >
                                            <EditOutlined />
                                        </C1LabeledIconButton>
                                    </Grid>
                                )
                            }
                            <Grid item xs={4}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={(e) => history.push(`/opadmin/order-termination/view/${id}`)}
                                >
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </Grid>
                            {
                                status === 'NEW' && (
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:delete")}
                                            label={t("buttons:delete")}
                                            action={(e) => handleConfirmDelete(id)}
                                        >
                                            <DeleteOutline />
                                        </C1LabeledIconButton>

                                    </Grid>
                                )
                            }
                        </Grid>
                    </Grid>
                }
            }
        }
    ]

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" })
    };

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

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);


    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            if (urlId === "delete") {
                setLoading(false)
                setRefresh(true);
                setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
                setSnackBarState({ ...snackBarState, open: true, msg: t("common:msg.deleteSuccess") })
            }
        }

        if (error) {
            setLoading(false);
        }

        if (validation) {
            setValidationErrors({ ...validation });
            setLoading(false);

            //if validation contains SUBMIT API CALL FAILURE, prompt message
            // console.log(validation['Submit.API.call'])
            if (validation['Submit.API.call']) {
                // alert(validation['Submit.API.call'])
                setOpenWarning(true)
                setWarningMessage(validation['Submit.API.call'])
            }
        }
    // eslint-disable-next-line
    }, [urlId, isLoading, res, error]);

    useEffect(() => {
        if (!isApproval && !isRequester) {
            history.push('/session/404')
        }
    // eslint-disable-next-line
    }, []);

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

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;
    if (snackBarState.open) {
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

    const handleEditClick = (id) => {
        history.push(`/opadmin/order-termination/edit/${id}`)

    }

    let confirmDialog = "";
    if (openSubmitConfirm.open) {
        confirmDialog = <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={openSubmitConfirm?.msg}
            title={t("common:popup.confirmation")}
            onYesClick={(e) => eventHandler(openSubmitConfirm?.action)} />
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
        } else if (action.toLowerCase() === "delete") {
            handleStoreDelete()
        }
    };

    const handleEventAdd = () => {
        history.push('/opadmin/order-termination/new/-')
    }

    const handleStoreDelete = () => {
        setLoading(true)
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clictruck/job/jobTermReq/${dataStatus?.id}`, "delete", "delete");
    }

    const handleConfirmDelete = (id) => {
        setDataStatus({ ...dataStatus, ...{ status: "delete", id } });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "DELETE", open: true, msg: t('common:msg.deleteConfirm') });
    }

    return (
        <>
            {snackBar}
            {confirmDialog}
            {confirm && confirm.id && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm.id })}
                    onYesClick={() => handleActionHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}
            <C1ListPanel
                routeSegments={[
                    { name: t("listing:orderTermination.title"), path: '/opadmin/order-termination' },
                ]}
                information={<C1Information information="manageUserListing" />}
                guideId="clicdo.truck.users.list"
                title={t("listing:orderTermination.title")}>

                <DataTable
                    url="/api/v1/clickargo/clictruck/job/jobTermReq/"
                    columns={columns}
                    title=""
                    defaultOrder="jtrDtCreate"
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
                    showActiveHistoryButton={toggleHistory}
                    showAddButton={[
                        {
                            label: t("listing:orderTermination.buttonTitle").toUpperCase(),
                            icon: <Add />,
                            action: handleEventAdd,
                            show: isRequester
                        }
                    ]}
                />

            </C1ListPanel>
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />

            <Backdrop open={loading} className={bdClasses.backdrop}> <CircularProgress color="inherit" /></Backdrop>
        </>
    )
}

export default withErrorHandler(OrderTermination)