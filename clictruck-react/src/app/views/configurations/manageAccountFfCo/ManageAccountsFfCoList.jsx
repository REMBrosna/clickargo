import { Grid, Snackbar } from "@material-ui/core";
import { EditOutlined, PlayCircleOutlineOutlined } from "@material-ui/icons";
import { Add } from "@material-ui/icons";
import BlockOutlinedIcon from '@material-ui/icons/BlockOutlined';
import PauseCircleOutlineOutlinedIcon from '@material-ui/icons/PauseCircleOutlineOutlined';
import VisibilityIcon from "@material-ui/icons/VisibilityOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import ChipStatus from "app/atomics/atoms/ChipStatus";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Information from "app/c1component/C1Information";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import { AccountsProcessStates, AccountStatus, RecordStatus, AccountTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, encodeString, formatDate} from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";

import useHttp from "../../../c1hooks/http";

/** @description Listing for Account Management */
const ManageAccountsFfCoList = () => {
    const { t } = useTranslation(["admin", "common"]);

    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [tableLoading, setTableLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const { user } = useAuth();
    //const isLevel1 = isSpL1([user.authorities]);
    //const isFinanceHead = isFinanceApprover([user.authorities]);

    const [snackBarOptions, setSnackBarOptions] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });

    let showAdd = true;

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" }
    ]);

    const [accountId, setAccountId] = useState("");
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });

    useEffect(() => {
        if (showHistory) {
            setFilterBy([
                { attribute: "history", value: "history" }
            ]);
        } else {
            setFilterBy([
                { attribute: "history", value: "default" }
            ])
        }
    }, [showHistory]);

    useEffect(() => {
        if (!error && res) {

            if (urlId === 'active' || urlId === 'deActive') {
                setRefresh(true);
                setLoading(false)
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    message: urlId === "active" ? t("admin:account.msg.activatedSuccess") : t("admin:account.msg.deactivatedSuccess"),
                });
            } if (false) {
                //sendRequest(`/api/v1/clickargo/manageaccn/${accountId}/suspend`, "accnSuspend", "PUT", { accnDetails: { ...res?.data?.accnDetails }, action: "SUBMIT" })
            } if (urlId === "getAccnForSuspend") {
                setRefresh(true);
                setLoading(false)
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    message: t("admin:account.msg.accnSuspended"),
                });
            } if (false) {
                sendRequest(`/api/v1/clickargo/manageaccn/${accountId}/terminate`, "accnTerminate", "PUT", { accnDetails: { ...res?.data?.accnDetails }, action: "SUBMIT" })
            } if (urlId === "getAccnForTerminate") {
                setRefresh(true);
                setLoading(false)
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    message: t("admin:account.msg.accnTerminate"),
                });
            }
            if (urlId === "deleteAccn") {
                setRefresh(true);
                setLoading(false);
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    message: t("admin:account.msg.deleted"),
                })
                setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    const columns = [
        {
            name: "tcoreAccnByFfcoCo.accnId", // co Account id
            label: t("admin:account.list.accnId"), // column title that will be shown in table
            options: {
                sort: true,
                filter: true,
            },
        },
        {
            name: "tcoreAccnByFfcoCo.accnStatus",// co Account status
            label: "",
            options: {
                display: 'excluded',
                filter: false,
            },
        },
        {
            name: "tcoreAccnByFfcoFf.accnId",//  FF acount id;
            label: "",
            options: {
                display: 'excluded',
                filter: false,
            },
        },
        {
            name: "tcoreAccnByFfcoFf.accnName",          // FF acount Name
            label: t("admin:account.list.accnTypeFF"),
            options: {
                display: ((user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_SP.code)? true: 'excluded'),
                filter: (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_SP.code),
            },
        },
        {
            name: "tcoreAccnByFfcoCo.accnName",
            label: t("admin:account.list.accnTypeCO"),
        },
        {
            name: "tcoreAccnByFfcoCo.accnCoyRegn",
            label: t("admin:account.list.accnCoyRegn"),
        },
        {
            name: "tcoreAccnByFfcoCo.accnContact.contactTel",
            label: t("admin:account.list.accnPhone"),
        },
        {
            name: "tcoreAccnByFfcoCo.accnContact.contactEmail",
            label: t("admin:account.list.accnEmail"),
        },
        {
            name: "ffcoDtCreate",
            label: t("admin:account.list.accnDtCreate"),
            options: {
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
                    return formatDate(value, false);
                }
            },
        },
        {
            name: "tcoreAccnByFfcoCo.accnStatus",
            label: t("admin:account.list.accnStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    //names: Object.keys(AccountsProcessStates),
                    names:['A','S','T'],
                    renderValue: v => {
                        return AccountsProcessStates[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return AccountsProcessStates[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let color = "#FF2E6C";
                    switch (value) {
                        case AccountsProcessStates.R.code:
                            color = "#37B7FF";
                            break;
                        case AccountsProcessStates.P.code:
                            color = "#229881";
                            break;
                        case AccountsProcessStates.V.code:
                            color = "#D17100";
                            break;
                        case AccountsProcessStates.S.code:
                            color = "#FF2E6C";
                            break;
                        case AccountsProcessStates.T.code:
                        case AccountsProcessStates.X.code:
                            color = "#969696";
                            break;
                        case AccountsProcessStates.Q.code:
                            color = "#37B7FF";
                            break;
                        default: return getStatusDesc(value);
                    }
                    return <ChipStatus text={AccountsProcessStates[value]?.desc} color={color} />
                }
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const accnId = tableMeta.rowData[0];
                    const accnStatus = tableMeta.rowData[1];
                    const ffAccnId = tableMeta.rowData[2];
                    return (
                        <Grid container direction="row" justifyContent="flex-start" alignItems="center" style={{ marginRight: "10px", minWidth: "200px" }}>
                            <Grid container item justifyContent="center" spacing={1}>
                                <Grid item xs={3} >
                                    {([AccountsProcessStates.N.code, AccountsProcessStates.A.code].includes(accnStatus) ) &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:edit")}
                                            label={t("buttons:edit")}
                                            action={() => history.push(`/manageAccountsFfCo/edit/${encodeString(accnId)}/${encodeString(ffAccnId)}`)}>
                                            <EditOutlined />
                                        </C1LabeledIconButton>
                                    }
                                </Grid>

                                {( showHistory || [ AccountsProcessStates.A.code].includes(accnStatus) ) &&
                                    <Grid item xs={3} ><C1LabeledIconButton
                                        tooltip={t("buttons:view")}
                                        label={t("buttons:view")}
                                        action={() => history.push(`/manageAccountsFfCo/view/${encodeString(accnId)}/${encodeString(ffAccnId)}`)}>
                                        <VisibilityIcon />
                                    </C1LabeledIconButton>
                                    </Grid>}

                                <Grid item xs={3} >
                                    {accnStatus === AccountStatus.SUS_APPROVED.code &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:unsuspend")}
                                            label={t("buttons:unsuspend")}
                                            action={() => handleConfirmSetActive(accnId)}>
                                            <PlayCircleOutlineOutlined />
                                        </C1LabeledIconButton>
                                    }
                                    {accnStatus === RecordStatus.ACTIVE.code &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:suspend")}
                                            label={t("buttons:suspend")}
                                            action={() => handleConfirmSetSuspend(accnId)}>
                                            <PauseCircleOutlineOutlinedIcon />
                                        </C1LabeledIconButton>
                                    }
                                </Grid>

                                <Grid item xs={3} >
                                    {accnStatus === RecordStatus.ACTIVE.code &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:terminate")}
                                            label={t("buttons:terminate")}
                                            action={() => handleConfirmTerminate(accnId)}>
                                            <BlockOutlinedIcon />
                                        </C1LabeledIconButton>
                                    }
                                </Grid>
                            </Grid>
                        </Grid>
                    );
                },
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

    const handleSuspend = (accnId) => {
        setLoading(true)
        setRefresh(false)
        setAccountId(accnId)
        sendRequest(`/api/v1/clickargo/manageAccnFfCo/accnStatus/${accnId}/S`, "getAccnForSuspend", "PUT")
    };

    const handleTerminate = (accnId) => {
        setLoading(true)
        setRefresh(false)
        setAccountId(accnId)
        sendRequest(`/api/v1/clickargo/manageAccnFfCo/accnStatus/${accnId}/T`, "getAccnForTerminate", "PUT")
    };

    const handleActiveHandler = (accnId) => {
        setRefresh(false);
        sendRequest(`/api/v1/clickargo/manageAccnFfCo/accnStatus/${accnId}/A`, "active", "PUT", {});
    }

    const handleSnackBarClose = () => {
        setSnackBarOptions({ ...snackBarOptions, open: false });
    };

    const handleEventAddAccount = () => {
        history.push('/manageAccountsFfCo/new/-/-')
    }
    const handleAction = (action, id) => {
        setLoading(true);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        switch (action) {
            case "active":
                handleActiveHandler(id);
                break;
            case "suspend":
                handleSuspend(id);
                break;
            case "terminate":
                handleTerminate(id);
                break;
            default:
                console.log("handleAction", action);
                break;
        }
    };

    const handleConfirmSetActive = (id) => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "active", open: true, id });
    }

    const handleConfirmSetSuspend = (id) => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "suspend", open: true, id });
    }

    const handleConfirmTerminate = (id) => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "terminate", open: true, id });
    }


    let snackBar = null;
    if (snackBarOptions && snackBarOptions && snackBarOptions.open) {
        const anchorOriginV = snackBarOptions.vertical;
        const anchorOriginH = snackBarOptions.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarOptions.open}
            onClose={handleSnackBarClose}
            autoHideDuration={snackBarOptions.severity === 'success' ? 2000 : 3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleSnackBarClose} severity={snackBarOptions.severity}>
                {snackBarOptions.message}
            </C1Alert>
        </Snackbar>;
    }

    return (<React.Fragment>
        {loading && <MatxLoading />}
        {snackBar}
        <C1ListPanel
            routeSegments={[
                { name: t("CargoOwners.breadCrumbs.main"), },
            ]}
            information={<C1Information information="manageAccountListing" />}
            guideId="clicdo.truck.users.list"
            title={t("CargoOwners.breadCrumbs.main")}
        >
            <DataTable
                url="/api/v1/clickargo/clictruck/administrator/ffco"
                isServer={true}
                columns={columns}
                isRefresh={isRefresh}
                // title={t("account.list.headerAll")}
                defaultOrder="ffcoDtCreate"
                defaultOrderDirection="desc"
                showDownload={false}
                showPrint={false}
                filterBy={filterBy}
                isShowFilterChip={true}
                showAddButton={showAdd ? [{
                    label: 'NEW ACCOUNT',
                    action: () => handleEventAddAccount(),
                    icon: <Add />
                }] : null}
                showActiveHistoryButton={toggleHistory}
            />
        </C1ListPanel>
        <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={t("common:msg.confirmation", { action: openSubmitConfirm?.action })}
            title={t("common:popup.confirmation")}
            onYesClick={() => { handleAction(openSubmitConfirm?.action, openSubmitConfirm?.id) }}
        />
    </React.Fragment>);
};

export default ManageAccountsFfCoList;
