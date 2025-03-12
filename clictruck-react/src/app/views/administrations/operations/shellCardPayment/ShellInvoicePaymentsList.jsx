import Snackbar from "@material-ui/core/Snackbar";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import {Breadcrumb, MatxLoading} from "matx";
import Box from "@material-ui/core/Box";
import ChipStatus from "../../../../atomics/atoms/ChipStatus";
import moment from "moment";
import {Grid} from "@material-ui/core";
import C1LabeledIconButton from "../../../../c1component/C1LabeledIconButton";
import LocalAtmOutlinedIcon from '@material-ui/icons/LocalAtmOutlined';
import UpdatePaymentDetails from "./popups/UpdatePaymentDetails";
import C1Warning from "../../../../c1component/C1Warning";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";
import PersonOutlineOutlinedIcon from '@material-ui/icons/PersonOutlineOutlined';

const ShellInvoicePaymentsList = () => {

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
        invAmt: null,
        invBalanceAmt: null,
        invDt: "",
        invId:"",
        invNo: "",
        invPaymentAmt: "",
        invPaymentDt: "",
        invUidLupd: "",
        invStatus: "",
    }

    const [inputData, setInputData] = useState(defaultValue)
    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState(false);
    const [showPopUp, setShowPopUp] = useState(false);
    const [warningMessage, setWarningMessage] = useState({
        open: false,
        msg: "",
    });

    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: t("common:common.msg.deleted"),
        severity: "success",
    });
    const [open, setOpen] = useState(false);
    const [confirm, setConfirm] = useState({ id: null, invNo: null, status: null});

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "UPDATE_INV":
                    setRefresh(true)
                    setLoading(false);
                    setSuccess(true)
                    setOpen(false)
                    setShowPopUp(false)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity:"success",
                        msg: t(`common:common.msg.updated`),
                    });
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, error, res, urlId]);

    const columns = [
        {
            name: "invId",
            label: t("administration:shellCardInv.section.invoice.list.select"),
            options: {
                display: false,
                filter: false,
                sort: false,
                viewColumns: false,

            },
        },
        {
            name: "tcoreAccn",
            label: t("administration:shellCardInv.section.invoice.list.companyName"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => value?.accnName
            },
        },
        {
            name: "invNo",
            label: t("administration:shellCardInv.section.invoice.list.invNo"),
            options: {
                filter: false,
            },
        },
        {
            name: "invAmt",
            label: t("administration:shellCardInv.section.invoice.list.invAmount"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return  value ? `SGD ${value?.toLocaleString('en-US')}` : "N/A"
                },
            },
        },
        {
            name: "invStatus",
            label: t("administration:shellCardInv.section.invoice.list.status"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    const getStatusColorAndText = (va) => {
                        let statusColor, statusText;
                        switch (va) {
                            case "A":
                                statusColor = "#1976d2";
                                statusText = "ACTIVE";
                                break;
                            case "I":
                                statusColor = "#ea0c0c";
                                statusText = "INACTIVE";
                                break;
                            case "U":
                                statusColor = "#f8ca00";
                                statusText = "UNPAID";
                                break;
                            case "P":
                                statusColor = "#00D16D";
                                statusText = "PAID";
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
            name: "invPaymentDt",
            label: t("administration:shellCardInv.section.invoice.list.payDate"),
            options: {
                display: true,
                filter: false,
                sort: false,
                viewColumns: false,
                customBodyRender: (value, tableMeta, updateValue) => value ? moment(value).format(' YYYY-MM-DD HH:mm A') : "N/A",
            },
        },
        {
            name: "invUidLupd",
            label: t("administration:shellCardInv.section.invoice.list.payInfo"),
            options: {
                display: true,
                filter: false,
                sort: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                   return <span>
                        <PersonOutlineOutlinedIcon style={{margin: "-2px", fontSize: "14px"}}/>&nbsp;&nbsp;{value||"N/A"}
                    </span>
                }
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
                    const status = tableMeta.rowData[4];
                    const invObj = tableMeta.tableData[tableMeta?.rowIndex];
                    return status === "U" && (
                        <Grid
                            container
                            direction="row"
                            justifyContent="flex-start"
                            alignItems="center" style={{ marginRight: "10px" }}
                        >
                            <Grid container item justifyContent="center" spacing={3}>
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:updatePayment")}
                                        label={t("buttons:updatePayment")}
                                        action={() => handleAction("VIEW", id, invObj)}
                                    >
                                        <LocalAtmOutlinedIcon />
                                    </C1LabeledIconButton>
                                </Grid>
                            </Grid>
                        </Grid>
                    )
                }
            }
        },
    ];

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const handleAction = (action, id, value) => {
        setShowPopUp(true)
        setSuccess(false)
        setInputData(pre => value)
    };

    const handleActionConfirm = () => {
        setLoading(true);
        setOpen(false)
        setRefresh(false)
        switch (openActionConfirm?.action) {
            case "UPDATE":
                inputData.invStatus = 'P';
                sendRequest(`/api/v1/clickargo/clictruck/administrator/shellCardInvoice/${inputData?.invId}`, "UPDATE_INV", "PUT", inputData);
                break;
            default:
                break;
        }
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
        <Box style={{ padding: "30px 50px 30px 50px" }}>
            <div className="mb-sm-30">
                <Breadcrumb
                    routeSegments={[{
                        name: t("administration:shellCardInv.breadCrumbs.titleInv"),
                    }]}
                />
            </div>
            {loading && <MatxLoading />}
            {snackBar}
            <DataTable
                title={""}
                guideId={""}
                url="/api/v1/clickargo/clictruck/administrator/shellCardInvoice"
                isServer={true}
                isShowFilterChip
                columns={columns}
                isShowPrint={true}
                showAddButton={false}
                isShowToolbar={false}
                isShowFilter={false}
                isRefresh={isRefresh}
                isShowDownload={true}
                isShowViewColumns={true}
                isRowSelectable={"undefined"}
                defaultOrder="invPaymentDt"
                defaultOrderDirection="desc"
                customRowsPerPage={[10, 20]}
                showActiveHistoryButton={false}
            />
            <UpdatePaymentDetails
                translate={t}
                popUp={showPopUp}
                setOpen={setOpen}
                success={success}
                loading={loading}
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
            <C1Warning
                warningMessage={warningMessage}
                handleWarningAction={handleWarningAction}
            />
            <ConfirmationDialog
                open={open}
                title={t("listing:coJob.popup.confirmation")}
                text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm?.invNo })}
                onYesClick={() => handleActionConfirm()}
                onConfirmDialogClose={() => setOpen(false)}
            />
        </Box>
    );
};

export default withErrorHandler(ShellInvoicePaymentsList);
