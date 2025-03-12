import { Button, Grid, Popover, Typography } from "@material-ui/core";
import { makeStyles } from '@material-ui/core/styles';
import { VisibilityOutlined } from "@material-ui/icons";
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import DataTable from "app/atomics/organisms/DataTable";
import C1Information from "app/c1component/C1Information";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { JobStates, PaymentState } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, customNumFieldDisplay, downloadFile, formatDate } from "app/c1utils/utility";
import history from "history.js";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    amountCell: {
        justifyContent: 'center',
        textAlign: 'right',
        display: 'flex',
        flex: 1
    }
}));

const InboundPaymentList = () => {
    const bdClasses = useStyles();

    const { t } = useTranslation(["buttons", "listing", "common", "status"]);

    const { sendRequest, isLoading, error, res, validation, urlId } = useHttp();
    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "downloadFile":
                    if (res?.data?.data != null) {
                        const fileName = res?.data?.filename
                        const data = res?.data?.data
                        downloadFile(fileName != null ? fileName : "preview.pdf", data);
                    } else {
                        setShowEmptyObjectPopUp(true)
                    }
                    break;
                default: break;
            }
        }
     // eslint-disable-next-line
    }, [isLoading, res, error, validation]);

    // eslint-disable-next-line
    const [loading, setLoading] = useState(false);

    const [isRefresh, setRefresh] = useState(false);

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([{ attribute: "history", value: "default" }, { attribute: "forInboundCs", value: true }]);
    const [anchorEl, setAnchorEl] = React.useState(null);
    const openPopover = Boolean(anchorEl)
    const [staticJobId, setStaticJobId] = useState(null)

    const [showEmptyObjectPopUp, setShowEmptyObjectPopUp] = useState(false)

    const columns = [
        {
            name: "checkbox",
            label: "",
            options: {
                sort: false,
                filter: false,
                display: false,
                viewColumns: false,
            }
        },
        {
            name: "jobId",
            label: t("listing:finance.invId"),
        },
        {
            name: "invoiceFromAccn.accnName",
            label: t("listing:finance.invFrom"),
            options: {
                sort: false,
                filter: false,
            }
        },
        {
            name: "tckJob.tcoreAccnByJobOwnerAccn.accnName",
            label: t("listing:finance.cargoOwner")
        },
        {
            name: "jobNoTrips",
            label: t("listing:finance.noOfTrips"),
            options: {
                sort: true,
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
                    display: customNumFieldDisplay
                },
            }
        },
        {
            name: "jobTotalCharge",
            label: t("listing:finance.charges"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className={bdClasses.amountCell}>
                            <p> {value ? value.toLocaleString("id-ID", { maximumFractionDigits: 0, style: "currency", currency: "IDR" }) : "-"}</p>
                        </div>
                    )
                },
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customNumFieldDisplay
                }
            }
        },
        {
            name: "jobTotalReimbursements",
            label: t("listing:finance.reimbursement"),
            options: {
                sort: true,
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <div className={bdClasses.amountCell}>
                            <p> {value ? value.toLocaleString("id-ID", { maximumFractionDigits: 0, style: "currency", currency: "IDR" }) : "-"}</p>
                        </div>
                    )
                },
                filterType: 'custom',
                customFilterListOptions: {
                    render: v => v.map(l => l),
                    update: (filterList, filterPos, index) => {
                        filterList[index].splice(filterPos, 1);
                        return filterList;
                    }
                },
                filterOptions: {
                    display: customNumFieldDisplay
                }
            }
        },
        {
            name: "billingDate",
            label: t("listing:finance.billingDate"),
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
            name: "paymentDueDate",
            label: t("listing:verified.paymentDueDate"),
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
            name: "tckJob.tckMstJobState.jbstId",
            label: t("listing:finance.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(JobStates),
                    renderValue: v => {
                        return JobStates[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return JobStates[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "approvedDate",
            label: t("listing:finance.approvedDate"),
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
            name: "approvedBy",
            label: t("listing:finance.approvedBy"),
            options: {
                sort: true,
                filter: true,
            }
        },
        {
            name: "jobInPaymentState",
            label: t("listing:finance.paymentState"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: Object.keys(PaymentState),
                    renderValue: v => {
                        return PaymentState[v].desc;
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        return PaymentState[v].desc;
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value);
                }
            },
        },
        {
            name: "",
            label: t("listing:common.action"),
            options: {
                filter: false,
                sort: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { justifyContent: 'center', paddingLeft: "3%" } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const id = tableMeta.rowData[1];
                    return <Grid container direction="row" justifyContent="flex-start" alignItems="center" style={{ marginRight: "10px", minWidth: "100px" }}>
                        <Popover
                            open={openPopover} anchorEl={anchorEl} onClose={handleClose} anchorOrigin={{
                                vertical: 'bottom',
                                horizontal: 'left',
                            }}>
                            <Grid container direction={"column"} alignItems={'flex-start'}>
                                <Grid item>
                                    <Button style={{ textTransform: "none" }} onClick={() => downloadFileHandler("toInvoice", staticJobId)}>
                                        <Typography>
                                            {t("listing:invoices.invJob")}
                                        </Typography>
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button style={{ textTransform: "none" }} onClick={() => downloadFileHandler("platformInvoice", staticJobId)}>
                                        <Typography>
                                            {t("listing:invoices.invPf")}
                                        </Typography>
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button style={{ textTransform: "none" }} onClick={() => downloadFileHandler("debitNote", staticJobId)}>
                                        <Typography>
                                            {t("listing:invoices.invDn")}
                                        </Typography>
                                    </Button>
                                </Grid>
                                <Grid item>
                                    <Button style={{ textTransform: "none" }} onClick={() => downloadFileHandler("taxinvoice", staticJobId)}>
                                        <Typography>
                                            {t("listing:invoices.invTax")}
                                        </Typography>
                                    </Button>
                                </Grid>
                            </Grid>
                        </Popover>
                        <Grid container item justifyContent="center" spacing={2}>
                            <Grid item sm={6} xs={6}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:download")}
                                    label={t("buttons:download")}
                                    action={(e) => handleOpenDownloadToggle(e, id)}
                                >
                                    <GetAppOutlinedIcon />
                                </C1LabeledIconButton>
                            </Grid>
                            <Grid item xs={3}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={() => history.push({
                                        pathname: `/applications/services/job/truck/view`,
                                        state: { from: "/opadmin/inboundpayments", jobId: id }
                                    })}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </Grid>
                        </Grid>
                    </Grid>
                }
            }
        },
    ];

    useEffect(() => {
        if (showHistory) {
            setFilterBy([...filterBy, { attribute: "history", value: "history" }]);
        } else {
            setFilterBy([...filterBy, { attribute: "history", value: "default" }])
        }

        // eslint-disable-next-line
    }, [showHistory]);

    const downloadFileHandler = (fileEntity, fileId) => {
        const dlApi = `/api/v1/clickargo/clictruck/attach/byJobId/${fileEntity}/${fileId}`;
        sendRequest(dlApi, "downloadFile", "get");
    };

    const handleOpenDownloadToggle = (event, id) => {
        setStaticJobId(id)
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    }

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    return (
        <React.Fragment>
            <C1ListPanel
                routeSegments={[
                    { name: "Inbound Payment List", },
                ]}
                information={<C1Information information="manageUserListing" />}
                guideId="clicdo.truck.users.list"
                title="Inbound Payment List">
                <DataTable
                    url="/api/v1/clickargo/clictruck/invoice/job/approved"
                    columns={columns}
                    title=""
                    defaultOrder="jobDtLupd"
                    defaultOrderDirection="desc"
                    isServer={true}
                    isShowViewColumns={true}
                    isShowDownload={true}
                    isShowPrint={true}
                    isShowFilter={true}
                    isRefresh={isRefresh}
                    isShowFilterChip
                    isRowSelectable
                    filterBy={filterBy}
                    customRowsPerPage={[10, 20]}
                    guideId={"clicdo.doi.ff.bl.list.table"}
                    showActiveHistoryButton={toggleHistory}
                />
            </C1ListPanel>

            {/* temporary popup to handle empty response at download file */}
            <C1PopUp
                openPopUp={showEmptyObjectPopUp}
                setOpenPopUp={setShowEmptyObjectPopUp}
                maxWidth={'sm'}>
                <Grid container alignItems='center' justifyContent='center'>
                    <Typography>Sorry, this file doesn't exist</Typography>
                </Grid>
            </C1PopUp>

        </React.Fragment>
    )
}

export default InboundPaymentList