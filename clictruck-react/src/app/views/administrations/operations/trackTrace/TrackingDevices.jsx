import { Backdrop, CircularProgress, Grid } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import { Add, EditOutlined, LinkOffOutlined, LinkOutlined, NearMeOutlined } from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1IconButton from "app/c1component/C1IconButton";
import C1Information from "app/c1component/C1Information";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import { CK_ACCOUNT_TO_ACCN_TYPE, T_CK_CT_VEH, TrackDeviceState } from "app/c1utils/const";
import { deepUpdateState, getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import {T_CK_CT_VEH_DROPDOWN} from "../../../../c1utils/const";

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

const TrackingDevices = ({ }) => {

    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "payments"]);
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" }
    ]);
    const [validationErrors, setValidationErrors] = useState({});
    const [openWarning, setOpenWarning] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [loading, setLoading] = useState(false);
    const [confirm, setConfirm] = useState({ id: null });
    const [open, setOpen] = useState(false);
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });
    const [openPopop, setOpenPopop] = useState(false)
    const [inputData, setInputData] = useState({});
    const [listCompany, setListCompany] = useState([]);
    const [company, setCompany] = useState({});
    const [listVhicle, setListVhicle] = useState([]);
    const [vhicleId, setVhicleId] = useState(null);
    const [dataStatus, setDataStatus] = useState("");
    const [viewType, setViewType] = useState("");
    const [jobStatus, setJobStatus] = useState({});
    const [initData, setInitData] = useState({
        "otherLangDesc": null,
        "coreMstLocale": null,
        "tdId": "",
        "tdVehPlateNo": null,
        "tdGpsImei": "",
        "tdDtActivattion": null,
        "tdDtActivate": null,
        "tdDtDeactivate": null,
        "tdUidActivate": null,
        "tdUidDeactivate": null,
        "tdStatus": null,
        "tdDtCreate": null,
        "tdUidCreate": null,
        "tdDtLupd": null,
        "tdUidLupd": null,
        "tckCtMstTrackDeviceState": {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "tdsId": "NEW",
            "tdsName": "NEW",
            "tdsDesc": "NEW",
            "tdsDescOth": "NEW",
            "tdsStatus": "A",
            "tdsDtCreate": null,
            "tdsUidCreate": "sys",
            "tdsDtLupd": null,
            "tdsUidLupd": null
        },
        "tcoreAccn": {
            "accnId": ""
        },
        "tckCtVeh": {
            "vhId": ""
        },
        "tmstAccnType": null
    })

    const bdClasses = useStyles();

    const columns = [
        {
            name: "tdId",
            label: "Id",
            options: {
                filter: false,
                display: "excluded"
            }
        },
        {
            name: "tdGpsImei",
            label: t("listing:trackingDevices.imeiNo")
        },
        {
            name: "tckCtVeh.vhPlateNo",
            label: t("listing:trackingDevices.plateNo")
        },
        {
            name: "tckCtVeh.tckCtMstVehType.vhtyName",
            label: t("listing:trackingDevices.vehType"),
            options: {
                sort: true,
                filter: true,
                filterType: "dropdown",
                filterOptions: {
                    names: ["CDD", "CDD LONG", "CDE", "CONTAINER 20FT", "CONTAINER 40FT", "VAN", "WING BOX"],
                    renderValue: (v) => {
                        switch (v) {
                            case "CDD": return "CDD";
                            case "CDD LONG": return "CDD Long";
                            case "CDE": return "CDE";
                            case "CONTAINER 20FT": return "Container 20FT";
                            case "CONTAINER 40FT": return "Container 40FT";
                            case "VAN": return "Van";
                            case "WING BOX": return "Wing Box";
                            default: break
                        }
                    },
                },
                customFilterListOptions: {
                    render: (v) => {
                        switch (v) {
                            case "CDD": return "CDD";
                            case "CDD LONG": return "CDD Long";
                            case "CDE": return "CDE";
                            case "CONTAINER 20FT": return "Container 20FT";
                            case "CONTAINER 40FT": return "Container 40FT";
                            case "VAN": return "Van";
                            case "WING BOX": return "Wing Box";
                            default: break
                        }
                    },
                },
            },
        },
        {
            name: "tcoreAccn.accnName",
            label: t("listing:trackingDevices.accountName")
        },
        {
            name: "tdDtCreate",
            label: t("listing:trackingDevices.dateCreated"),
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
            name: "tdDtLupd",
            label: t("listing:trackingDevices.dateUpdated"),
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
            name: "tdDtActivattion",
            label: t("listing:trackingDevices.activationDate"),
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
            name: "tdDtDeactivate",
            label: t("listing:trackingDevices.deactivationDate"),
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
            name: "tckCtMstTrackDeviceState.tdsName",
            label: t("listing:trackingDevices.status"),
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
            name: "action",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { textAlign: 'center' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const status = tableMeta.rowData[9];
                    const id = tableMeta.rowData[0];
                    const accnId = tableMeta.rowData[11];
                    const vhId = tableMeta.rowData[12];
                    const imeiNo = tableMeta.rowData[1];

                    return <Grid container direction="row"
                        justifyContent="flex-start" alignItems="center" style={{ marginRight: "10px" }}>
                        <Grid container item justifyContent="center" spacing={3}>
                            <Grid item xs={4}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:edit")}
                                    label={t("buttons:edit")}
                                    action={(e) => handleEditClick(id, accnId, vhId, imeiNo, status)}
                                >
                                    <EditOutlined />
                                </C1LabeledIconButton>
                            </Grid>
                            {
                                (status === TrackDeviceState.ACTIVATE.code) ?
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:deactivate")}
                                            label={t("buttons:deactivate")}
                                            action={(e) => handleConfirmSetInActive(id)}
                                        >
                                            <LinkOffOutlined />
                                        </C1LabeledIconButton>
                                    </Grid>
                                    :
                                    <Grid item xs={4} >
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:activate")}
                                            label={t("buttons:activate")}
                                            action={(e) => handleConfirmSetActive(id)}
                                        >
                                            <LinkOutlined />
                                        </C1LabeledIconButton>
                                    </Grid>
                            }
                        </Grid>
                    </Grid>
                }
            }
        },
        {
            name: "tcoreAccn.accnId",
            options: {
                filter: false,
                sort: false,
                display: "excluded"
            },
        },
        {
            name: "tckCtVeh.vhId",
            options: {
                filter: false,
                sort: false,
                display: "excluded"
            },
        },
        {
            name: "tckCtVeh.vhId",
            options: {
                filter: false,
                sort: false,
                display: "excluded"
            },
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
            if (urlId === "getTruck") {
                const data = res?.data?.aaData;
                setListVhicle(data);
            } else if (urlId === "getListCompany") {
                setListCompany(res?.data?.aaData)
            } else if (urlId === "setTracking") {
                setLoading(false);
                setRefresh(true);
                setOpenPopop(false)
                setSnackBarState({ ...snackBarState, open: true, msg: t("common:msg.saveSuccess") })
            } else if (urlId === 'setStatus') {
                setRefresh(true);
                setOpenPopop(false)
                setSnackBarState({ ...snackBarState, open: true, msg: t("common:msg.updateSuccess") })
            }
        }

        if (error) {
            setLoading(false);
        }

        if (validation) {
            setValidationErrors({ ...validation });
            setLoading(false);
            if (validation['Submit.API.call']) {
                // alert(validation['Submit.API.call'])
                setOpenWarning(true)
                setWarningMessage(validation['Submit.API.call'])
            }
        }

    }, [urlId, isLoading, res, error]);


    useEffect(() => {
        sendRequest(CK_ACCOUNT_TO_ACCN_TYPE, 'getListCompany', 'GET');
    }, []);

    useEffect(() => {
        if (vhicleId && listVhicle.length > 0) {
            setInputData({
                ...inputData,
                vhId: vhicleId
            });
        }
    }, [listVhicle]);

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

    const handleOpenNewPopop = () => {
        setViewType("")
        setCompany({})
        setInputData({ ...initData })
        setValidationErrors({})
        setOpenPopop(true)
    }

    const getTruckList = (id, accnType) => {
        sendRequest(`${T_CK_CT_VEH_DROPDOWN}&mDataProp_1=TcoreAccn.accnId&sSearch_1=${id}&mDataProp_2=TCoreAccn.TMstAccnType.atypId&sSearch_2=${accnType}`, 'getTruck', 'get')
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });

        if (elName === 'tcoreAccn.accnId') {
            const company = listCompany.find(item => item?.accnId === e.target.value)
            setCompany(company)
            getTruckList(company?.accnId, company?.TMstAccnType?.atypId)
        }
    };

    const handleEditClick = (id, accnId, vhId, imeiNo, status) => {
        const company = listCompany.find(item => item.accnId === accnId);
        setVhicleId(vhId);
        setCompany(company);

        setInputData({
            ...inputData,
            ...{
                tdGpsImei: imeiNo,
                tcoreAccn: {
                    accnId
                },
                tckCtVeh: {
                    vhId
                },
            }
        });
        setJobStatus({ id, status })
        const accn = listCompany.find(item => item?.accnId === accnId)
        getTruckList(accn?.accnId, accn?.TMstAccnType?.atypId)
        setViewType('EDIT')
        setValidationErrors({})
        setOpenPopop(true)
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
        setDataStatus({ ...dataStatus, ...{ status: "ACTIVATE", id } });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.activeConfirm') });
    }

    const handleConfirmSetInActive = (id) => {
        setDataStatus({ ...dataStatus, ...{ status: "DEACTIVATE", id } });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.deactivateConfirm') });
    }

    const handleSubmitStatus = () => {
        setLoading(true);
        setRefresh(false);
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
        sendRequest("/api/v1/clickargo/clictruck/administrator/trackDevice/" + dataStatus.id + "/" + dataStatus.status, "setStatus", "put", null);
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSubmitStatus();
        }
    };

    const handleSaveButton = () => {
        setLoading(true);
        setRefresh(false);
        const payload = {
            ...initData,
            ...inputData
        }
        delete payload.vhId;
        sendRequest("/api/v1/clickargo/clictruck/administrator/trackDevice", "setTracking", "post", payload);
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
                    { name: t("listing:trackingDevices.title"), path: '/opadmin/trackingdevices' },
                ]}
                information={<C1Information information="manageUserListing" />}
                guideId="clicdo.truck.users.list"
                title={t("listing:trackingDevices.title")}>

                <DataTable
                    url="/api/v1/clickargo/clictruck/administrator/trackDevice"
                    columns={columns}
                    title=""
                    defaultOrder="tdDtCreate"
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
                    customRowsPerPage={[10, 20]}
                    showAddButton={[
                        {
                            label: t("listing:trackingDevices.newTrckDev").toUpperCase(),
                            icon: <Add />,
                            action: handleOpenNewPopop
                        }
                    ]}
                />

            </C1ListPanel>
            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />

            <C1PopUp
                maxWidth={"sm"}
                title={viewType !== 'EDIT' ? t("listing:trackingDevices.newPopupTitle") : t("listing:trackingDevices.editPopupTitle")}
                openPopUp={openPopop}
                setOpenPopUp={setOpenPopop}
                actionsEl={
                    <>
                        {
                            viewType !== 'EDIT' && <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
                                <NearMeOutlined color="primary" fontSize="large"
                                    onClick={handleSaveButton}
                                />
                            </C1IconButton>
                        }
                        {
                            (jobStatus?.status === TrackDeviceState.DEACTIVATE.code || jobStatus?.status === TrackDeviceState.NEW.code) && viewType === 'EDIT' && <C1IconButton tooltip={t("buttons:activate")} childPosition="right">
                                <LinkOutlined color="primary" fontSize="large"
                                    onClick={() => handleConfirmSetActive(jobStatus?.id)}
                                />
                            </C1IconButton>
                        }
                        {
                            jobStatus?.status === TrackDeviceState.ACTIVATE.code && viewType === 'EDIT' && <C1IconButton tooltip={t("buttons:deactivate")} childPosition="right">
                                <LinkOffOutlined color="primary" fontSize="large"
                                    onClick={() => handleConfirmSetInActive(jobStatus?.id)}
                                />
                            </C1IconButton>
                        }
                    </>
                }
            >
                <C1CategoryBlock>

                    <C1SelectAutoCompleteField
                        name="tcoreAccn.accnId"
                        label={t("listing:trackingDevices.comAccn")}
                        value={inputData?.tcoreAccn?.accnId ? inputData?.tcoreAccn?.accnId : ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        optionsMenuItemArr={listCompany.map((item, i) => {
                            return {
                                value: item.accnId,
                                desc: item.accnName
                            }
                        })}
                        disabled={viewType === 'EDIT'}
                        error={validationErrors["TCoreAccn.accnId"] !== undefined}
                        helperText={validationErrors["TCoreAccn.accnId"] || ""}
                    />

                    <C1InputField
                        label={t("listing:trackingDevices.accnType")}
                        name="companyType"
                        disabled
                        value={company?.TMstAccnType?.atypDescription}
                    />

                    <C1SelectAutoCompleteField
                        name="tckCtVeh.vhId"
                        label={t("listing:trackingDevices.plateNo")}
                        value={inputData?.tckCtVeh?.vhId ? inputData?.tckCtVeh?.vhId : ''}
                        onChange={(e, name, value) => handleInputChange({ target: { name, value: value?.value } })}
                        required
                        optionsMenuItemArr={listVhicle.map((item, i) => {
                            return {
                                value: item.vhId,
                                desc: item.vhPlateNo
                            }
                        })}
                        disabled={viewType === 'EDIT'}
                        error={validationErrors["TCkCtVeh.vhId"] !== undefined}
                        helperText={validationErrors["TCkCtVeh.vhId"] || ""}
                    />

                    <C1InputField
                        label={t("listing:trackingDevices.imeiNo")}
                        name="tdGpsImei"
                        onChange={handleInputChange}
                        disabled={viewType === 'EDIT'}
                        error={validationErrors["tdGpsImei"] !== undefined}
                        helperText={validationErrors["tdGpsImei"] || ""}
                        value={inputData?.tdGpsImei}
                    />

                </C1CategoryBlock>
            </C1PopUp>

            <Backdrop open={loading} className={bdClasses.backdrop}> <CircularProgress color="inherit" /></Backdrop>
        </>
    )
}

export default withErrorHandler(TrackingDevices)