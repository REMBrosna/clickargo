import { Button, Grid, Snackbar } from "@material-ui/core";
import {Add, EditOutlined, LinkOffOutlined, LinkOutlined} from "@material-ui/icons";
import PauseCircleOutlineOutlinedIcon from '@material-ui/icons/PauseCircleOutlineOutlined';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SettingsBackupRestoreOutlinedIcon from '@material-ui/icons/SettingsBackupRestoreOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Button from "app/c1component/C1Button";
import C1Information from "app/c1component/C1Information";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { AccountTypes, RecordStatus } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { encodeString, formatDate, getValue, isCustService, userRolesDescChip } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

const ManageUserList = () => {

    const { t } = useTranslation(["admin", "common"]);
    const { user } = useAuth();

    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [snackBarOptions, setSnackBarOptions] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });

  
    const [confirm, setConfirm] = useState({ id: "", open: false, action: null });

    const manageUsrUrl = `/api/v1/clickargo/clictruck/manageusr`;

    const [tempEmail, setTempEmail] = useState();
    const [openPopup, setOpenPopup] = useState(false);
    const [inputData, setInputData] = useState({});

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" },
        { attribute: "display", value: "all" }
    ]);

    // const isCs = isCustService([user.authorities]);

    useEffect(() => {
        if (showHistory) {
            setFilterBy([
                { attribute: "history", value: "history" },
                { attribute: "display", value: "all" }
            ]);
        } else {
            setFilterBy([
                { attribute: "history", value: "default" },
                { attribute: "display", value: "all" }
            ])
        }
    }, [showHistory]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId === 'getForSuspend') {
                let isUserLoggedIn = res?.data?.loggedIn;
                if (isUserLoggedIn) {
                    setConfirm({ ...confirm, id: res?.data?.coreUsr?.usrUid, open: true, action: "suspend" });
                } else {
                    sendRequest(`${manageUsrUrl}/update/suspend/` + encodeString(res?.data?.coreUsr?.usrUid), "usrSuspend", "PUT");
                }
            } else if (urlId === 'getForUnsuspend') {
                sendRequest(`${manageUsrUrl}/update/unsuspend/` + encodeString(res.data.coreUsr.usrUid), "usrUnsuspend", "PUT");
            } else if (urlId === 'getForReset') {
                setInputData({ ...inputData, ...res.data });
                setTempEmail(getValue(inputData?.usrContact?.contactEmail));
            } else if (urlId === "resetPwd") {
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    severity: "success",
                    message: t("user.msg.resetPasswordSuccess"),
                });
                setOpenPopup(false);
            }

            if (urlId === "getForDeActive") {
                let isUserLoggedIn = res?.data?.loggedIn;
                if (isUserLoggedIn) {
                    setLoading(false);
                    setConfirm({
                        ...confirm,
                        id: res?.data?.coreUsr?.usrUid,
                        open: true,
                        action: "deactivate",
                    });
                } else {
                    setLoading(false);
                    sendRequest(
                        `${manageUsrUrl}/update/deactivate/${encodeString(res?.data?.coreUsr?.usrUid)}`,
                        "deActive",
                        "PUT"
                    );
                }
            } else if (urlId === "getForActive") {
                setLoading(false);
                sendRequest(
                    `${manageUsrUrl}/update/activate/${encodeString(res.data.coreUsr.usrUid)}`,
                    "active",
                    "PUT"
                );
            }

            if (urlId === "active" || urlId === "deActive") {
                if (urlId === "deActive") {
                    setConfirm({ id: null, open: false, action: null });
                }
                setRefresh(true);
                setLoading(false);
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    severity: "success",
                    message:
                        urlId === "active"
                            ? t("admin:user.msg.activatedSuccess")
                            : t("admin:user.msg.deactivatedSuccess"),
                });
            } else if (urlId === 'usrSuspend') {
                if (urlId === 'usrSuspend') {
                    setConfirm({ id: null, open: false, action: null });
                }
                setRefresh(true);
                setLoading(false);
                setSnackBarOptions({
                    ...snackBarOptions,
                    open: true,
                    severity: "success",
                    message: urlId === "usrUnsuspend" ? t("admin:user.msg.unsuspended") : t("admin:user.msg.suspended"),
                });
            }
        }

        setLoading(false);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);


    const columns = [
        {
            name: "usrUid", // field name in the row object
            label: t("user.list.usrUid"), // column title that will be shown in table
        },
        {
          name: "usrName",
          label: t("user.list.usrName"),
        },
        {
          name: "usrContact.contactEmail",
          label: t("user.list.usrEmail"),
        },
        {
            name: "TCoreRoles",
            label: t("user.list.usrRoles"),
            options: {
                filter: false,
                sort: false,
                // setCellHeaderProps: () => { return { style: { width: '30%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    // return userRolesDesc(value);
                    return userRolesDescChip(value);
                }
            },
        },
        {
            name: "TCoreAccn.accnName",
            label: t("user.list.usrAccn"),
            options: {
                filter: true,
                display: true
            },
        },
        {
            name: "TCoreAccn.TMstAccnType.atypDescription",
            label: t("user.list.usrAccnType"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [AccountTypes.ACC_TYPE_CO.desc,
                    AccountTypes.ACC_TYPE_FF.desc,
                    AccountTypes.ACC_TYPE_TO.desc,
                    AccountTypes.ACC_TYPE_SP.desc],
                    renderValue: v => {
                        switch (v) {
                            case AccountTypes.ACC_TYPE_CO.desc: return AccountTypes.ACC_TYPE_CO.desc;
                            case AccountTypes.ACC_TYPE_FF.desc: return AccountTypes.ACC_TYPE_FF.desc;
                            case AccountTypes.ACC_TYPE_TO.desc: return AccountTypes.ACC_TYPE_TO.desc;
                            case AccountTypes.ACC_TYPE_SP.desc: return AccountTypes.ACC_TYPE_SP.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case AccountTypes.ACC_TYPE_CO.desc: return AccountTypes.ACC_TYPE_CO.desc;
                            case AccountTypes.ACC_TYPE_FF.desc: return AccountTypes.ACC_TYPE_FF.desc;
                            case AccountTypes.ACC_TYPE_TO.desc: return AccountTypes.ACC_TYPE_TO.desc;
                            case AccountTypes.ACC_TYPE_SP.desc: return AccountTypes.ACC_TYPE_SP.desc;
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value;
                }
            },
        },
        {
            name: "usrDtCreate",
            label: t("user.list.usrDtCreate"),
            options: {
                display: 'excluded',
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, false);
                }
            },
        },
        {
            name: "usrStatus",
            label: t("user.list.usrStatus"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code, RecordStatus.SUSPENDED.code, RecordStatus.DEACTIVE.code],
                    renderValue: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                            case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                            case RecordStatus.DEACTIVE.code: return RecordStatus.DEACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case RecordStatus.ACTIVE.code: return RecordStatus.ACTIVE.desc;
                            case RecordStatus.INACTIVE.code: return RecordStatus.INACTIVE.desc;
                            case RecordStatus.SUSPENDED.code: return RecordStatus.SUSPENDED.desc;
                            case RecordStatus.DEACTIVE.code: return RecordStatus.DEACTIVE.desc;
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
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
                setCellHeaderProps: () => { return { style: { width: '11%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const usrUid = tableMeta.rowData[0];
                    const usrStatus = tableMeta.rowData[7];
                    return (
                        <Grid container direction="row" justifyContent="center" alignItems="center">
                            <Grid container direction="row" justifyContent="flex-end" spacing={4}>
                                {(usrStatus === RecordStatus.ACTIVE.code || usrStatus === RecordStatus.DEACTIVE.code) &&
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:edit")}
                                            label={t("buttons:edit")}
                                            action={() => history.push(`/opadmin/user/edit/${usrUid}`)}>
                                            <EditOutlined />
                                        </C1LabeledIconButton>
                                    </Grid>
                                }
                                <Grid item xs={4}>
                                    {usrStatus === RecordStatus.ACTIVE.code && (
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:deactivate")}
                                            label={t("buttons:deactivate")}
                                            action={() => handleDeActiveHandler(tableMeta.rowData[0])}
                                        >
                                            <LinkOffOutlined />
                                        </C1LabeledIconButton>
                                    )}
                                    {usrStatus !== RecordStatus.ACTIVE.code &&
                                    usrStatus !== RecordStatus.SUSPENDED.code && (
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:activate")}
                                            label={t("buttons:activate")}
                                            action={() => handleActiveHandler(tableMeta.rowData[0])}
                                        >
                                            <LinkOutlined />
                                        </C1LabeledIconButton>
                                    )}
                                    {usrStatus === RecordStatus.SUSPENDED.code &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:unsuspend")}
                                            label={t("buttons:unsuspend")}
                                            action={() => handleUnsuspendHandler(usrUid)}>
                                            <SettingsBackupRestoreOutlinedIcon />
                                        </C1LabeledIconButton>
                                    }
                                </Grid>
                                <Grid item xs={4}>
                                    {(usrStatus === RecordStatus.ACTIVE.code || usrStatus === RecordStatus.SUSPENDED.code) &&
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:resetPwd")}
                                            label={t("buttons:reset")}
                                            action={() => handleResetPopup(usrUid)}>
                                            <RotateLeftIcon />
                                        </C1LabeledIconButton>
                                    }
                                </Grid>
                            </Grid>
                        </Grid>
                    );
                }
            },
        }
    ];

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    const handleSuspendHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest(`${manageUsrUrl}/${encodeString(id)}`, "getForSuspend", "GET", {});
    }

    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest(
            `${manageUsrUrl}/${encodeString(id)}`,
            "getForActive",
            "GET",
            {}
        );
    };

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest(
            `${manageUsrUrl}/${encodeString(id)}`,
            "getForDeActive",
            "GET",
            {}
        );
    };

    const handleUnsuspendHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest(`${manageUsrUrl}/${encodeString(id)}`, "getForUnsuspend", "GET", {});
    }

    const handleYesAction = () => {
        setLoading(true);
        setRefresh(false);
        sendRequest(
            `${manageUsrUrl}/${encodeString(confirm?.id)}`,
            "getForDeActive",
            "GET",
            {}
        );
    }

    const handleResetPopup = (id) => {
        setOpenPopup(true)
        sendRequest(`${manageUsrUrl}/${encodeString(id)}`, "getForReset", "GET", {});
    }

    const handleResetPassword = (id) => {
        var portalUser = { "coreUsr": inputData.coreUsr, "alternateEmail": tempEmail };
        sendRequest(`${manageUsrUrl}/resetPassword/` + encodeString(id), "resetPwd", "PUT", portalUser);
    }

    const handleInputChange = async (e) => {
        const elName = e.target.name;
        if (elName === 'tempEmail') {
            setTempEmail(e.target.value);
        }
    }

    const handleEventAddUser = () => {
        history.push({
            pathname: '/opadmin/user/new/0',
            state: {
                from: "manageAllUser"
            }

        })
    }

    const handleSnackBarClose = () => {
        setSnackBarOptions({ ...snackBarOptions, open: false });
    };

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

    return (
        <React.Fragment>
            {!isLoading && snackBar}
            {loading && <MatxLoading />}

            {confirm && confirm.open &&
                (
                    <ConfirmationDialog
                        title={
                            confirm.action === "deactivate"
                                ? t("admin:user.deactivate.title")
                                : ""
                        }
                    open={confirm.open}
                        text={
                            confirm.action === "deactivate"
                                ? t("admin:user.deactivate.content", { userId: confirm.id })
                                : ""
                        }
                    onYesClick={() => handleYesAction()}
                    onConfirmDialogClose={() => {
                        setLoading(false)
                        setConfirm({...confirm, open: false})
                    }} />
                )}

            <C1ListPanel
                routeSegments={[
                    { name: t("user.list.headerAll") },
                ]}
                information={<C1Information information="manageUserListing" />}
                guideId="clicdo.truck.users.list"
                title={t("user.list.headerAll")}>
                <DataTable
                    url="/api/v1/clickargo/manageusr"
                    isServer={true}
                    columns={columns}
                    defaultOrder="usrDtCreate"
                    defaultOrderDirection="desc"
                    showDownload={false}
                    showPrint={false}
                    isShowToolbar={true}
                    isRefresh={isRefresh}
                    showAddButton={
                        [
                            {
                                show: true,
                                label: t("admin:user.list.addTitle").toUpperCase(),
                                action: () => handleEventAddUser(),
                                icon: <Add />,
                            },
                        ]
                    }

                    showActiveHistoryButton={toggleHistory}
                    // filterBy={[{ attribute: "display", value: "all" }]}
                    filterBy={filterBy}
                />
            </C1ListPanel>

            <C1PopUp
                title={t("admin:user.resetPwd.title")}
                openPopUp={openPopup}
                setOpenPopUp={setOpenPopup}>
                <Grid container spacing={1} alignItems="center" >
                    <Grid container item spacing={3}>
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("admin:user.details.query.usrId")}
                                name="usrUid"
                                required
                                disabled
                                onChange={(e) => { handleInputChange(e) }}
                                value={inputData?.coreUsr?.usrUid} />
                        </Grid>
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("admin:user.details.query.email")}
                                name="tempEmail"
                                required
                                disabled={isLoading}
                                onChange={(e) => { handleInputChange(e) }}
                                value={tempEmail ? tempEmail : inputData?.coreUsr?.usrContact?.contactEmail} />
                        </Grid>
                    </Grid>
                    <Grid container item alignItems="flex-end" spacing={2} direction="row" justifyContent="flex-end">
                        <Grid item xs={6}></Grid>
                        <Grid container item xs={6} spacing={2} direction="row" justifyContent="flex-end">
                            <Grid item xs={3} >
                                <Button variant="contained"
                                    color="secondary"
                                    size="large"
                                    fullWidth
                                    onClick={(e) => setOpenPopup(false)}>{t("admin:user.details.query.btnClose")}</Button>
                            </Grid>
                            <Grid item xs={3} >
                                <C1Button variant="contained"
                                    color="primary"
                                    size="large"
                                    withLoading={isLoading}
                                    onClick={(e) => handleResetPassword(inputData?.coreUsr?.usrUid)}
                                    text={t("admin:user.details.query.btnReset")} />
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </C1PopUp>

        </React.Fragment>

    );
};

export default ManageUserList;
