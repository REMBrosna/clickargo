import Snackbar from "@material-ui/core/Snackbar";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import history from "../../../../../history";
import {Breadcrumb, MatxLoading} from "matx";
import DescriptionIcon from '@material-ui/icons/Description';
import Box from "@material-ui/core/Box";
import {Grid} from "@material-ui/core";
import C1LabeledIconButton from "../../../../c1component/C1LabeledIconButton";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";
import ChipStatus from "../../../../atomics/atoms/ChipStatus";
import {Add} from "@material-ui/icons";
import CardAssignPopupForm from "./popups/CardAssignPopupForm";
import moment from "moment";
import ClearIcon from '@material-ui/icons/Clear';
import C1ListPanel from "../../../../c1component/C1ListPanel";
import LinkIcon from "@material-ui/icons/LinkOutlined";

const ShellManagmentList = () => {

    const { t } = useTranslation([
        "job",
        "common",
        "status",
        "buttons",
        "listing",
        "ffclaims",
        "administration"
    ]);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const defaultValue = {
        ctId: "",
        tcoreAccn: {
            accnId: "",
            accnName: ""
        },
        tckCtShellCard: {
            scId: ""
        },
        tckCtVeh:{
            vhId : ""
        }
    }
    const [inputData, setInputData] = useState(defaultValue)
    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [showPopUp, setShowPopUp] = useState(false)
    const [warningMessage, setWarningMessage] = useState({
        open: false,
        msg: "",
    });
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }]);
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: t("common:common.msg.deleted"),
        severity: "success",
    });
    const [open, setOpen] = useState(false);
    const [confirm, setConfirm] = useState({ id: null, accnName: null, status: null});

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "ACTIVE":
                case "TERMINATE":
                    setRefresh(true)
                    setLoading(false);
                    setSuccess(true)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity:openActionConfirm?.action === "Terminate" ? "error" : "success",
                        msg: t("common:common.msg.generalAction", { action: openActionConfirm?.action }),
                    });
                    break;
                case "CREATE":
                    setRefresh(true)
                    setLoading(false);
                    setSuccess(true)
                    setShowPopUp(false)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity: "success",
                        msg: t(`common:common.msg.assignSuccess`),
                    });
                    setInputData(defaultValue)
                    setErrors({})
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, error, res, urlId]);

    useEffect(() => {
        const value = showHistory ? "history" : "default";
        setFilterBy([{ attribute: "history", value }]);
    }, [showHistory]);

    const columns = [
        {
            name: "ctId",
            options: {
                display: false,
            },
        },
        {
            name: "tcoreAccn",
            label: t("administration:shell.list.accId"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return `${value?.accnId}-${value?.accnName}`;
                },
            },
        },
        {
            name: "tckCtShellCard",
            label: t("administration:shell.list.cardNo"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <Box style={{display: "grid", width: "130px"}}>
                            <span>
                                {value?.scId}
                            </span>
                            <span style={{fontSize: "x-small"}}>
                                {`Expire on ${moment(value?.scDtExpiry).format('YYYY-MM-DD')}`}
                            </span>
                        </Box>
                    );
                },
            },
        },
        {
            name: "tckCtVeh",
            label: t("administration:shell.list.truckPlateNo"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return `${value?.vhPlateNo}(${value?.tckCtMstVehType?.vhtyName})`;
                },
            }
        },
        {
            name: "ctDtCreate",
            label: t("administration:shell.list.dateAssign"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return moment(value).format('YYYY-MM-DD HH:mm a');
                }
            },
        },
        {
            name: "ctUidCreate",
            label: t("administration:shell.list.assignBy"),
            options: {
                filter: false,
            }
        },
        {
            name: "ctStatus",
            label: t("administration:shell.list.status"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const getStatusColorAndText = (va) => {
                        let statusColor, statusText;
                        switch (va) {
                            case "A":
                                statusColor = "#00D16D";
                                statusText = "Active";
                                break;
                            case "E":
                                statusColor = "#FF2E6C";
                                statusText = "Expired";
                                break;
                            case "I":
                                statusColor = "#9c3422";
                                statusText = "Inactive";
                                break;
                            default:
                                break;
                        }

                        return { statusColor, statusText };
                    };
                    const { statusColor, statusText } = getStatusColorAndText(value);
                    return <ChipStatus text={statusText} color={statusColor} />;
                },
            },
        },
        {
            name: "action",
            label: t("administration:shell.list.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { textAlign: 'center' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const id = tableMeta.rowData[0];
                    const status = tableMeta.rowData[6];
                    const ojb = tableMeta.rowData[1];
                    return (
                        <Grid
                            container
                            direction="row"
                            justifyContent="flex-start"
                            alignItems="center" style={{ marginRight: "10px" }}
                        >
                            <Grid container item justifyContent="center" spacing={3}>
                                {(status === "A") ? (
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:terminate")}
                                            label={t("buttons:terminate")}
                                            action={() => handleAction("Terminate", id, ojb, "T")}
                                        >
                                            <ClearIcon />
                                        </C1LabeledIconButton>
                                    </Grid>
                                ) : (status === "I") &&
                                (
                                    <Grid item xs={4}>
                                        <C1LabeledIconButton
                                            tooltip={t("buttons:activate")}
                                            label={t("buttons:activate")}
                                            action={() => handleAction("Active", id, ojb, "A")}
                                        >
                                            <LinkIcon />
                                        </C1LabeledIconButton>
                                    </Grid>

                                    )}
                            </Grid>
                        </Grid>
                    )
                }
            }
        },
    ];

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history');
        setTimeout(() => {
            setRefresh(true);
            setLoading(false);
        }, 500);
    };

    const handleAction = (action, id, value, status) => {
        setRefresh(false);
        const accnName =  value?.accnName
        setConfirm({id,accnName,status});
        setOpenActionConfirm({action})
        setOpen(true)
        setSuccess(false)
    };

    const handleActionConfirm = () => {
        setLoading(true);
        setOpen(false)
        switch (openActionConfirm?.action.toUpperCase()) {
            case "ASSIGN":
                sendRequest("/api/v1/clickargo/clictruck/administrator/shellCardTruck", "CREATE", "POST", inputData);
                break;
            case "TERMINATE":
                sendRequest(`/api/v1/clickargo/clictruck/administrator/shellCardTruck/${confirm?.id}/I`, "TERMINATE", "PUT", inputData);
                break;
            case "ACTIVE":
                sendRequest(`/api/v1/clickargo/clictruck/administrator/shellCardTruck/${confirm?.id}/A`, "ACTIVE", "PUT", inputData);
                break;
            default:
                break;
        }
    };

    const handleOnAdd = () => {
        setShowPopUp(true);
    }

    let snackBar = null;
    if (success) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;
        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleCloseSnackBar}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert
                    onClose={handleCloseSnackBar}
                    severity={snackBarState.severity}
                >
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }

    return (
       <>
            {loading && <MatxLoading />}
            {snackBar}
            <C1ListPanel
                routeSegments={[{ name: t("administration:shell.breadCrumbs.list") }]}
                guideId="null"
                title={t("administration:shell.breadCrumbs.list")}
            >
                <DataTable
                    title=""
                    guideId={""}
                    url="/api/v1/clickargo/clictruck/administrator/shellCardTruck"
                    isServer={true}
                    isShowFilterChip
                    columns={columns}
                    isShowPrint={true}
                    filterBy={filterBy}
                    isShowToolbar={true}
                    isShowFilter={false}
                    isRefresh={isRefresh}
                    isShowDownload={true}
                    isShowViewColumns={true}
                    defaultOrder="ctDtCreate"
                    defaultOrderDirection="desc"
                    customRowsPerPage={[10, 20]}
                    showActiveHistoryButton={toggleHistory}
                    showAddButton={[{
                        label: t("administration:shell.form.button.newAssign").toUpperCase(),
                        action: handleOnAdd,
                        icon: <Add />
                    }]}
                />
            </C1ListPanel>
            <C1Warning
                warningMessage={warningMessage}
                handleWarningAction={handleWarningAction}
            />
            <ConfirmationDialog
                open={open}
                title={t("listing:coJob.popup.confirmation")}
                text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm?.tcoreAccn?.accnName })}
                onYesClick={() => handleActionConfirm()}
                onConfirmDialogClose={() => setOpen(false)}
            />
            <CardAssignPopupForm
                translate={t}
                popUp={showPopUp}
                setOpen={setOpen}
                success={success}
                errors={errors}
                setErrors={setErrors}
                setPopUp={setShowPopUp}
                setSuccess={setSuccess}
                setConfirm={setConfirm}
                setLoading={setLoading}
                inputData={inputData}
                setInputData={setInputData}
                setOpenActionConfirm={setOpenActionConfirm}
            />
       </>
    );
};

export default withErrorHandler(ShellManagmentList);
