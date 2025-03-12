import Snackbar from "@material-ui/core/Snackbar";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import {
    formatDate,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "../../../../../history";
import {Breadcrumb, MatxLoading} from "matx";
import DescriptionIcon from '@material-ui/icons/Description';
import Box from "@material-ui/core/Box";
import {Grid} from "@material-ui/core";
import C1LabeledIconButton from "../../../../c1component/C1LabeledIconButton";
import BlockIcon from "@material-ui/icons/Block";
import CheckIcon from "@material-ui/icons/CheckOutlined";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";
import ChipStatus from "../../../../atomics/atoms/ChipStatus";
import C1ListPanel from "../../../../c1component/C1ListPanel";

const LeasingApplicationList = (props) => {

    const { t } = useTranslation([
        "job",
        "common",
        "status",
        "buttons",
        "listing",
        "ffclaims",
        "administration"
    ]);

    const { user } = useAuth();
    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [success, setSuccess] = useState(false);
    const [showHistory, setShowHistory] = useState(false);
    const [warningMessage, setWarningMessage] = useState({
        open: false,
        msg: "",
    });
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" },   { attribute: "accnId", value: user?.coreAccn?.accnId },]);
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: t("common:common.msg.deleted"),
        severity: "success",
    });
    const [open, setOpen] = useState(false);
    const [confirm, setConfirm] = useState({ id: null, trucksName: null, status: null });

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "UPDATE_STATUS":
                    setRefresh(true)
                    setLoading(false);
                    setSuccess(true)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity: openActionConfirm?.action === "APPROVE" ? "success" : "error",
                        msg: t(`common:common.msg.${openActionConfirm?.action.toLowerCase()}`),
                    });
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, error, res, urlId]);

    useEffect(() => {
        const value = showHistory ? "history" : "default";
        setFilterBy([{ attribute: "history", value }, { attribute: "accnId", value: user?.coreAccn?.accnId },]);
    }, [showHistory]);


    const columns = [
        {
            name: "vrId",
            label: t("administration:trucksRental.rentals.accId"),
            options: {
                display: false,
            },
        },
        {
            name: "accn",
            label: t("administration:trucksRental.rentals.accId"),
            options: {
                filter: false,
            },
        },
        {
            name: "name",
            label: t("administration:trucksRental.rentals.name"),
            options: {
                filter: false,
            },
        },
        {
            name: "truck",
            label: t("administration:trucksRental.rentals.trucks"),
            options: {
                filter: false,
            },
        },
        {
            name: "provider",
            label: t("administration:trucksRental.rentals.provider"),
            options: {
                filter: false,
            },
        },
        {
            name: "lease",
            label: t("administration:trucksRental.rentals.leasePlans"),
            options: {
                filter: false,
            },
        },
        {
            name: "price",
            label: t("administration:trucksRental.rentals.price"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value && new Intl.NumberFormat('en-SG', { style: 'currency', currency: 'SGD' }).format(value)
                },
            },
        },
        {
            name: "quantity",
            label: t("administration:trucksRental.rentals.numberTrucks"),
            options: {
                filter: false,
            },
        },
        {
            name: "vrDtCreate",
            label: t("administration:trucksRental.rentals.dateSubmit"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                },
            },
        },
        {
            name: "vrStatus",
            label: t("administration:trucksRental.rentals.status"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const getStatusColorAndText = (va) => {
                        let statusColor, statusText;
                        switch (va) {
                            case "S":
                                statusColor = "#00D16D";
                                statusText = "Approved";
                                break;
                            case "R":
                                statusColor = "#FF2E6C";
                                statusText = "Rejected";
                                break;
                            case "N":
                                statusColor = "#37B7FF";
                                statusText = "New";
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
            label: t("administration:trucksRental.form.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { textAlign: 'center' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const id = tableMeta.rowData[0];
                    const status = tableMeta.rowData[9];
                    const trucksName = tableMeta.rowData[3];
                    return (
                        <Grid
                            container
                            direction="row"
                            justifyContent="flex-start"
                            alignItems="center" style={{ marginRight: "10px" }}
                        >
                            <Grid container item justifyContent="center" spacing={3}>
                                {(status === "N") && (
                                    <>
                                        <Grid item xs={4}>
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:approve")}
                                                label={t("buttons:approve")}
                                                action={() => handleAction("APPROVE", id, trucksName, "S")}
                                            >
                                                <CheckIcon color="primary" />
                                            </C1LabeledIconButton>
                                        </Grid>
                                        <Grid item xs={4}>
                                            <C1LabeledIconButton
                                                tooltip={t("buttons:reject")}
                                                label={t("buttons:reject")}
                                                action={() => handleAction("REJECT", id, trucksName, "R")}
                                            >
                                                <BlockIcon color="primary" />
                                            </C1LabeledIconButton>
                                        </Grid>
                                    </>
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

    const handleAction = (action, id, trucksName, status) => {
        setRefresh(false);
        setConfirm({id, trucksName, status});
        setOpenActionConfirm({action})
        setOpen(true)
        setSuccess(false)
    };

    const handleActionConfirm = (e) => {
        if (confirm && !confirm.id) return;
        setLoading(true);
        sendRequest(`/api/v1/clickargo/clictruck/administrator/rentalApp/${confirm?.id}/${confirm?.status}`, "UPDATE_STATUS", "PUT", null);
        setOpen(false)
    };

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
                routeSegments={[{ name: t("administration:trucksRental.form.leasingList") }]}
                guideId="null"
                title={t("administration:trucksRental.form.leasingList")}
            >
                <DataTable
                    title=""
                    guideId={""}
                    url="/api/v1/clickargo/clictruck/administrator/rentalApp"
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
                    defaultOrder="vrDtCreate"
                    defaultOrderDirection="desc"
                    customRowsPerPage={[10, 20]}
                    showActiveHistoryButton={toggleHistory}
                />
            </C1ListPanel>
            <C1Warning
                warningMessage={warningMessage}
                handleWarningAction={handleWarningAction}
            />
            {confirm && confirm.id && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm.trucksName })}
                    onYesClick={() => handleActionConfirm()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}
        </>
    );
};

export default withErrorHandler(LeasingApplicationList);
