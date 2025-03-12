import { Button, Dialog, Grid, Snackbar } from "@material-ui/core";
import { Add, EditOutlined, VisibilityOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import ChipStatus from "app/atomics/atoms/ChipStatus";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { ContractRequestStates, Roles, Status } from "app/c1utils/const";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";

const ContractRequestList = () => {

    const { t } = useTranslation(["buttons", "listing", "administration", "common"]);
    const { user } = useAuth();

    let showAdd = false;
    if (user?.authorities.some(el => [Roles.SP_L1.code].includes(el.authority))) {
        showAdd = true;
    }

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [dataStatus, setDataStatus] = useState("");
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [loading, setLoading] = useState(true);
    const [isRefresh, setRefresh] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("common:msg.deleteSuccess"),
        severity: 'success'
    });
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");

    const handleCloseSnackBar = () => {
        setRefresh(false);
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

    let confirmDialog = "";
    if (openSubmitConfirm.open) {
        confirmDialog = <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={openSubmitConfirm?.msg}
            title={t("common:popup.confirmation")}
            onYesClick={(e) => eventHandler(openSubmitConfirm?.action)} />
    }

    const handleConfirmSetActive = (id) => {
        console.log('id', id)
        setDataStatus({ ...dataStatus, ...{ status: "active", id } });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.activeConfirm') });
    }

    const handleConfirmSetInActive = (id) => {
        console.log('id', id)
        setDataStatus({ ...dataStatus, ...{ status: "deactive", id } });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.inActiveConfirm') });
    }

    const handleSubmitStatus = () => {
        setLoading(true);
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
        sendRequest("/api/v1/clickargo/clictruck/administrator/contract/" + dataStatus.id + "/" + dataStatus.status, "setStatus", "put", null);
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSubmitStatus();
        }
    };

    const columns = [
        {
            name: "crId",
            label: t("listing:common.id"),
            options: {
                display: "excluded",
                filter: false,
                sort: false
            }
        },
        {
            name: "crName",
            label: t("listing:contract.contractName"),
            options: {
                filter: true,
                sort: true
            }
        },
        {
            name: "tcoreAccnByCrTo.accnName",
            label: t("listing:contract.truckOperator")
        },
        {
            name: "tcoreAccnByCrCoFf.accnName",
            label: t("listing:contract.cargoOwner")
        },
        {
            name: "crDtStart",
            label: t("listing:common.startDate"),
            options: {
                filter: true,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
            }
        },
        {
            name: "crDtEnd",
            label: t("listing:common.expiryDate"),
            options: {
                filter: true,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
            }
        },
        {
            name: "crDtCreate",
            label: t("listing:common.dateCreated"),
            options: {
                filter: true,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
            }
        },
        {
            name: "crDtLupd",
            label: t("listing:common.dateUpdated"),
            options: {
                filter: false,
                display: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true)
                },
                filterType: 'custom',
                filterOptions: {
                    display: customFilterDateDisplay
                },
            }
        },
        {
            name: "tckCtMstContractReqState.stId",
            label: t("listing:common.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(ContractRequestStates),
                    renderValue: v => {
                        return ContractRequestStates[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return ContractRequestStates[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let color = "#FFC633";
                    switch (value) {
                        case ContractRequestStates.DELETED.code:
                        case ContractRequestStates.UPDATE_REJECTED.code:
                        case ContractRequestStates.NEW_REJECTED.code:
                        case ContractRequestStates.RENEWAL_REJECTED.code:
                        case ContractRequestStates.EXPIRED.code:
                        case ContractRequestStates.EXPORTED.code:
                            color = "#FF2E6C"; break;
                        case ContractRequestStates.NEW_REQ.code:
                        case ContractRequestStates.NEW_UPDATE.code:
                        case ContractRequestStates.RENEWAL_REQ.code:
                            color = "#229881"; break;
                        case ContractRequestStates.NEW_SUBMITTED.code:
                        case ContractRequestStates.UPDATE_SUBMITTED.code:
                        case ContractRequestStates.RENEWAL_SUBMITTED.code:
                            color = "#37B7FF"; break;
                        case ContractRequestStates.NEW_APPROVED.code:
                        case ContractRequestStates.UPDATE_APPROVED.code:
                        case ContractRequestStates.RENEWAL_APPROVED.code:
                            color = "#00D16D"; break;

                        default: break;
                    }

                    return <ChipStatus text={ContractRequestStates[value]?.desc} color={color} style={{width:'180px'}}/>

                }
            }
        },

        {
            name: "crId",
            label: t("listing:common.action"),
            options: {
                filter: false,
                display: true,
                sort: false,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const id = tableMeta.rowData[0];
                    const status = tableMeta.rowData[8];
                    return <Grid container direction="row"
                        justifyContent="flex-start" alignItems="center" style={{ minWidth: 120 }}>
                        <Grid container item justifyContent="center" spacing={2}>
                            <Grid item xs={4}>
                                {[ContractRequestStates.NEW_REQ.code, ContractRequestStates.NEW_UPDATE.code].includes(status) &&
                                    <C1LabeledIconButton tooltip={t("buttons:edit")}
                                        label={t("buttons:edit")}
                                        action={() => history.push(`/opadmin/contractrequest/edit/${id}`)}>
                                        <EditOutlined />
                                    </C1LabeledIconButton>
                                }
                            </Grid>
                            <Grid item xs={4}>
                                <C1LabeledIconButton tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={() => history.push(`/opadmin/contractrequest/view/${id}`)}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </Grid>
                        </Grid>
                    </Grid>
                }
            }
        },
    ];


    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }])
        }
    }, [showHistory]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "delete":
                    setRefresh(true);
                    setFilterBy([{ attribute: "history", value: "default" }])
                    setSnackBarState({ ...snackBarState, open: true });
                    setLoading(false);
                    break;
                case "activate":
                case "deactivate": {
                    setRefresh(true);
                    break;
                }
                case "setStatus": {
                    setLoading(false);
                    setRefresh(true);
                    setSnackBarState({ ...snackBarState, open: true, msg: t("common:msg.updateSuccess") })
                }
                default:
                    break;
            }
        }
    }, [isLoading, res, error, urlId]);

    return (
        <React.Fragment>
            {isLoading && <MatxLoading />}
            {confirmDialog}
            {snackBar}

            <C1ListPanel
                routeSegments={[
                    { name: t("administration:contractManagement.breadCrumbs.requestList") }
                ]} guideId="clicdo.doi.co.jobs.list"
                title={t("administration:contractManagement.breadCrumbs.requestList")}
            >
                <DataTable
                    url="/api/v1/clickargo/clictruck/administrator/contractReq"
                    columns={columns}
                    title=""
                    defaultOrder="crDtCreate"
                    defaultOrderDirection="desc"
                    isServer={true}
                    isShowViewColumns={true}
                    isShowDownload={true}
                    isShowPrint={true}
                    isShowFilter={true}
                    isRefresh={isRefresh}
                    isShowToolbar={true}
                    isShowFilterChip
                    filterBy={filterBy}
                    guideId={""}
                    showActiveHistoryButton={toggleHistory}
                    customRowsPerPage={[10, 20]}
                    showAddButton={showAdd ? [{
                        label: t("listing:contract.newContract").toUpperCase(),
                        action: () => history.push('/opadmin/contractrequest/new/-'),
                        icon: <Add />
                    }] : null}

                />
            </C1ListPanel>

            <Dialog maxWidth="xs" open={openWarning} >
                <div className="p-8 text-center w-360 mx-auto">
                    <h4 className="capitalize m-0 mb-2">{"Information"}</h4>
                    <p>{warningMessage}</p>
                    <div className="flex justify-center pt-2 m--2">
                        <Button
                            className="m-2 rounded hover-bg-primary px-6"
                            variant="outlined"
                            color="primary"
                            onClick={(e) => handleWarningAction(e)}
                        >
                            {t("cargoowners:popup.ok")}
                        </Button>
                    </div>
                </div>
            </Dialog>

        </React.Fragment>
    )
}

export default ContractRequestList;