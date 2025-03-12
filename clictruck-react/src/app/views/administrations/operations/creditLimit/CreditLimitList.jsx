import { makeStyles } from "@material-ui/core";
import { Grid} from "@material-ui/core";
import { Add, CancelOutlined, EditOutlined, VisibilityOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import DataTable from "app/atomics/organisms/DataTable";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { CreditLimitUpdateStates, Roles } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { iconStyles } from "app/c1utils/styles";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { ConfirmationDialog } from "matx";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: "#fff",
    },
    amountCell: {
        justifyContent: "center",
        textAlign: "right",
        // display: 'flex',
        // flex: 1
    },
}));

const CreditLimitList = () => {
    const { user } = useAuth();
    const { t } = useTranslation(["buttons", "payments", "common", "listing"]);
    const classes = iconStyles();
    const bdClasses = useStyles();
    const history = useHistory();
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });

    const isFnHDSupport = user?.authorities?.some(
        (item) => item?.authority === Roles.FINANCE_APPROVER.code
    );
    const isL1Support = user?.authorities?.some((item) => item?.authority === Roles.SP_L1.code);

    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [confirmIdPicked, setConfirmIdPicked] = useState({ id: null });
    const [openConfirmation, setOpenConfirmation] = useState(false);
    const [creditData, setCreditData] = useState({})

    const [showHistory, setShowHistory] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);

    const columns = [
        {
            name: "cruId",
            label: t("listing:creditLimit.id"),
        },
        {
            name: "tcoreAccn.accnId",
            label: t("listing:creditLimit.code"),
        },
        {
            name: "tcoreAccn.accnName",
            label: t("listing:creditLimit.name"),
        },
        {
            name: "tckMstServiceType.svctName",
            label: t("listing:creditLimit.type"),
        },
        {
            name: "creditLimit",
            label: t("listing:creditLimit.limit"),
            options: {
                sort: false,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let ccy = tableMeta.rowData[5];
                    return (
                        <div className={bdClasses.amountCell}>
                            <p>
                                {value
                                    ? value.toLocaleString(ccy == "IDR" ? "in-ID" : "en-US", {
                                        maximumFractionDigits: 0,
                                        style: "currency",
                                        currency: ccy,
                                    })
                                    : 0}
                            </p>
                        </div>
                    );
                },
            },
        },
        {
            name: "tmstCurrency.ccyCode",
            label: t("listing:creditLimit.ccy"),
        },
        {
            name: "cruAmt",
            label: t("listing:creditLimit.newLimit"),
            options: {
                sort: false,
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    let ccy = tableMeta.rowData[5];
                    return (
                        <div className={bdClasses.amountCell}>
                            <p>
                                {value
                                    ? value.toLocaleString(ccy == "IDR" ? "in-ID" : "en-US", {
                                        maximumFractionDigits: 0,
                                        style: "currency",
                                        currency: ccy,
                                    })
                                    : 0}
                            </p>
                        </div>
                    );
                },
            },
        },
        {
            name: "tckMstCreditRequestState.stId",
            label: t("listing:trucklist.status"),
            options: {
                filter: true,
                filterType: "dropdown",
                filterOptions: {
                    names: Object.keys(CreditLimitUpdateStates),
                    renderValue: (v) => {
                        return CreditLimitUpdateStates[v].desc;
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        return CreditLimitUpdateStates[v].desc;
                    },
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                },
            },
        },
        {
            name: "cruDtCreate",
            label: t("listing:staticVAList.dateCreate"),
            options: {
                filter: true,
                filterType: "custom",
                customFilterListOptions: {
                    render: (v) => v.map((l) => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    },
                },
                filterOptions: {
                    display: customFilterDateDisplay,
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "cruUidCreate",
            label: t("listing:creditLimit.cruUidCreate"),
        },
        {
            name: "cruDtSubmitted",
            label: t("listing:creditLimit.cruDtSubmitted"),
            options: {
                filter: true,
                filterType: "custom",
                customFilterListOptions: {
                    render: (v) => v.map((l) => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    },
                },
                filterOptions: {
                    display: customFilterDateDisplay,
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "cruUidSubmitted",
            label: t("listing:creditLimit.cruUidSubmitted"),
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
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const creditId = tableMeta.rowData[0];
                    const companyId = tableMeta.rowData[1];
                    const serviceId = tableMeta.rowData[13];
                    const isSubmitted = tableMeta.rowData[10];
                    const status = tableMeta.rowData[7];

                    return (
                        <Grid
                            container
                            direction="row"
                            justifyContent="flex-start"
                            alignItems="center"
                            style={{ marginRight: "10px" }}
                        >
                            <Grid container item justifyContent="center" spacing={3}>
                                {isFnHDSupport === false ? (
                                    <Grid item xs={4}>
                                        {status == "NEW" && showHistory === false ? (
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:edit")}
                                                label={t("buttons:edit")}
                                                action={() =>
                                                    history.push({
                                                        pathname: `/opadmin/creditform/edit/${creditId}`,
                                                        state: {
                                                            companyId: companyId,
                                                            serviceId: serviceId,
                                                            from: "/opadmin/creditlimit",
                                                        },
                                                    })
                                                }
                                            >
                                                <EditOutlined />
                                            </C1LabeledIconButton>
                                        ) : (
                                            <></>
                                        )}
                                    </Grid>
                                ) : (
                                    <Grid item xs={4}></Grid>
                                )}
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:view")}
                                        label={t("buttons:view")}
                                        action={() =>
                                            history.push({
                                                pathname: `/opadmin/creditform/view/${creditId}`,
                                                state: {
                                                    companyId: companyId,
                                                    serviceId: serviceId,
                                                    status: status,
                                                    from: "/opadmin/creditlimit",
                                                },
                                            })
                                        }
                                    >
                                        <VisibilityOutlined />
                                    </C1LabeledIconButton>
                                </Grid>

                                {isFnHDSupport === false ? (
                                    <Grid item xs={4}>
                                        {status == "NEW" && showHistory === false ? (
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:delete")}
                                                label={t("buttons:delete")}
                                                action={() => {
                                                    setOpenConfirmation(true)
                                                    setConfirmIdPicked({ id: creditId })
                                                    setOpenActionConfirm({ action: "delete", open: true })
                                                    sendRequest(
                                                        `/api/v1/clickargo/creditRequest/crupdate/${creditId}`,
                                                        "getDetail",
                                                        "get"
                                                    )
                                                }
                                                }
                                            >
                                                <CancelOutlined />
                                            </C1LabeledIconButton>
                                        ) : (
                                            <></>
                                        )}
                                    </Grid>
                                ) : (
                                    <Grid item xs={4}></Grid>
                                )}
                            </Grid>
                        </Grid>
                    );
                },
            },
        },
        {
            name: "tckMstServiceType.svctId",
            label: "",
            options: {
                display: "excluded",
                filter: false,
            },
        },
    ];

    useEffect(() => {
        if (!error && res) {
            switch (urlId) {
                case "getDetail": {
                    const data = res?.data;
                    setCreditData(data)

                    break;
                }
                case "deleteReq": {
                    setRefresh(true);
                    setTimeout(() => {
                        setRefresh(false);
                    }, 500);
                    break;
                }
                default:
                    break;
            }
        }
    }, [urlId, res, error]);

    useEffect(() => {
        if (showHistory) {
            setFilterBy([{ attribute: "history", value: "history" }]);
        } else {
            setFilterBy([{ attribute: "history", value: "default" }]);
        }
    }, [showHistory]);

    const toggleHistory = (filter) => {
        setRefresh(false);
        setShowHistory(filter === "history" ? true : false);
        setTimeout(() => setRefresh(true), 500);
        // setTimeout(() => setLoading(false), 500);
    };

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleDialogConfirmation = (e) => {
        if (openActionConfirm.action === "delete") {
            setOpenConfirmation(false)
            sendRequest(
                `/api/v1/clickargo/creditRequest/crupdate/${creditData?.cruId}`,
                "deleteReq",
                "put",
                { ...creditData, tckMstCreditRequestState: { stId: "DEL" } }
            );
        }
    }

    return (
        <React.Fragment>

            {/* Confirmation Popup */}
            {confirmIdPicked && confirmIdPicked.id && (
                <ConfirmationDialog
                    open={openConfirmation}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirmIdPicked.id })}
                    onYesClick={() => handleDialogConfirmation()}
                    onConfirmDialogClose={() => setOpenConfirmation(false)}
                />
            )}

            <C1ListPanel
                routeSegments={[{ name: t("listing:creditLimit.title") }]}
                guideId="clicdo.truck.users.list"
                title={t("listing:creditLimit.title")}
            >
                <DataTable
                    url="/api/v1/clickargo/creditRequest/crupdate"
                    columns={columns}
                    title=""
                    defaultOrder="cruDtCreate"
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
                    showAddButton={[
                        {
                            show: isL1Support === true && showHistory === false ? true : false,
                            label: t("listing:creditLimit.updateLimit").toUpperCase(),
                            action: () =>
                                history.push({
                                    pathname: `/opadmin/creditform/new/0`,
                                    state: { from: "/opadmin/creditlimit" },
                                }),
                            icon: <Add />,
                        },
                    ]}
                />
            </C1ListPanel>

            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />
        </React.Fragment>
    );
};

export default CreditLimitList;
