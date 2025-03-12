import Snackbar from "@material-ui/core/Snackbar";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import useHttp from "app/c1hooks/http";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import history from "../../../../../history";
import {Breadcrumb, MatxLoading} from "matx";
import Box from "@material-ui/core/Box";
import {Button, Grid, Tooltip} from "@material-ui/core";
import C1LabeledIconButton from "../../../../c1component/C1LabeledIconButton";
import VisibilityIcon from '@material-ui/icons/Visibility';
import moment from "moment";
import ShellCardInvoiceItemsPopupList from "./popups/ShellCardInvoiceItemsPopupList";
import {VisibilityOutlined} from "@material-ui/icons";

const ShellCardInvoiceList = (props) => {

    const {user} = props;

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
    }

    const [inputData, setInputData] = useState(defaultValue)
    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState(false);
    const [showPopUp, setShowPopUp] = useState(false)

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
                case "DELETE":
                    setRefresh(true)
                    setLoading(false);
                    setSuccess(true)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity:"error",
                        msg: t(`common:common.msg.deleted`),
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
            options: {
                display: false,
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
            name: "invDt",
            label: t("administration:shellCardInv.section.invoice.list.invDate"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return moment(value).format('YYYY-MM-DD HH:mm A');
                }
            },
        },
        {
            name: "invAmt",
            label: t("administration:shellCardInv.section.invoice.list.amount"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return  value ? `SGD ${value?.toLocaleString('en-US')}` : "N/A"
                },
            },
        },
        {
            name: "invPaymentDt",
            label: t("administration:shellCardInv.section.invoice.list.payDate"),
            options: {
                filter: false,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return value ? moment(value).format(' YYYY-MM-DD HH:mm A') : "N/A";
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
                    const invObj = tableMeta.tableData[tableMeta?.rowIndex];
                    return (
                        <Grid
                            container
                            direction="row"
                            justifyContent="flex-start"
                            alignItems="center" style={{ marginRight: "10px" }}
                        >
                            <Grid container item justifyContent="center" spacing={3}>
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:view")}
                                        label={t("buttons:view")}
                                        action={() => handleAction("VIEW", id, invObj)}
                                    >
                                        <VisibilityOutlined />
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
        <Box >
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
                filterBy={[{ attribute: "accnId", value: user?.coreAccn?.accnId }]}
                isShowToolbar={false}
                isShowFilter={false}
                isRefresh={isRefresh}
                isShowDownload={true}
                isShowViewColumns={true}
                defaultOrder="invPaymentDt"
                defaultOrderDirection="desc"
                customRowsPerPage={[10, 20]}
                showActiveHistoryButton={false}
                showAddButton={false}
            />
            <ShellCardInvoiceItemsPopupList
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
        </Box>
    );
};

export default withErrorHandler(ShellCardInvoiceList);
