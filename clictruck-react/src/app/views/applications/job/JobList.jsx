import { Button, ButtonGroup, Grid, IconButton, Tooltip } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import AssignmentTurnedInOutlinedIcon from '@material-ui/icons/AssignmentTurnedInOutlined';
import Block from '@material-ui/icons/BlockOutlined';
import Delete from '@material-ui/icons/DeleteOutlined';
import Edit from '@material-ui/icons/EditOutlined';
import HistoryIcon from '@material-ui/icons/HistoryOutlined';
import SpeakerNotesOutlinedIcon from '@material-ui/icons/SpeakerNotesOutlined';
import Visibility from '@material-ui/icons/VisibilityOutlined';
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { Link } from "react-router-dom";

import C1Alert from "app/c1component/C1Alert";
import C1Button from "app/c1component/C1Button";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import { CK_MST_SHIPMENT_TYPE, JobStates, Status } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate } from "app/c1utils/utility";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import useHttp from "../../../c1hooks/http";
import BlRemarkPopUp from "../fforwarder/doClaim/popups/BlRemarkPopup";

const JobList = () => {

    const { t } = useTranslation(["buttons", "listing"]);

    const history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [confirm, setConfirm] = useState({ jobId: null });
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);

    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const [deleteSuccess, setDeleteSuccess] = useState(false);
    const [openDeleteConfirm, setOpenDeleteConfirm] = useState({ action: null, open: false });
    const [filterHistory, setFilterHistory] = useState([{ attribute: "history", value: "default" }]);
    const [showHistory, setShowHistory] = useState(false);

    //Remarks
    const popupDefaultValue = { jobRemarks: "", };
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
    const [openRemarkDialog, setOpenRemarkDialog] = useState(false);



    const columns = [
        // 0
        {
            name: "jobId",
            label: t("listing:trucklist.jobid")
        },
        // 1
        {
            name: "tckJob.tckMstShipmentType.shtId",
            label: t("listing:trucklist.type"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["IMPORT", "EXPORT", "DOMESTIC"],
                    renderValue: v => {
                        switch (v) {
                            case "IMPORT": return "IMPORT";
                            case "EXPORT": return "EXPORT";
                            case "DOMESTIC": return "DOMESTIC"
                            default: break;
                        }
                    }
                },
            },
        },
        // 2
        {
            name: "tcoreAccnByJobPartyTo.accnName",
            label: t("listing:trucklist.truckoperator"),
        },
        // 3
        {
            name: "jobDtDelivery",
            label: t("listing:trucklist.dtdelivery"),
            options: {
                filter: true,
                filterType: 'custom',
                display: true,
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, false);

                }
            }
        },
        // 4
        {
            name: "tckJob.jobReference",
            label: t("listing:trucklist.shipref"),
        },
        // 5
        {
            name: "",
            label: t("listing:trucklist.cusref"),
        },
        // 6
        {
            name: "",
            label: t("listing:trucklist.pickup"),
        },
        // 7
        {
            name: "",
            label: t("listing:trucklist.lastdrop"),
        },
        // 8
        {
            name: "jobDtCreate",
            label: t("listing:trucklist.dtCreate"),
            options: {
                filter: true,
                filterType: 'custom',
                display: true,
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);

                }
            }
        },
        // 8
        {
            name: "jobDtLupd",
            label: t("listing:trucklist.dtLupd"),
            options: {
                filter: true,
                filterType: 'custom',
                display: true,
                filterOptions: {
                    display: customFilterDateDisplay
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);

                }
            }
        },
        // 5
        {
            name: "tckJob.tckMstJobState.jbstId",
            label: t("listing:trucklist.status"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: [JobStates.SUB.code, JobStates.COM.code, JobStates.NEW.code,
                    JobStates.PAID.code, JobStates.PMV.code, JobStates.PROG.code, JobStates.DEL.code, JobStates.CAN.code,
                    JobStates.REJ.code, JobStates.ASG.code,],
                    renderValue: v => {
                        switch (v) {
                            case JobStates.SUB.code: return JobStates.SUB.desc;
                            case JobStates.COM.code: return JobStates.COM.desc;
                            case JobStates.NEW.code: return JobStates.NEW.desc;
                            case JobStates.PAID.code: return JobStates.PAID.desc;
                            case JobStates.PMV.code: return JobStates.PMV.desc;
                            // case JobStates.PROG.code: return JobStates.PROG.desc;
                            case JobStates.DEL.code: return JobStates.DEL.desc;
                            case JobStates.CAN.code: return JobStates.CAN.desc;
                            case JobStates.REJ.code: return JobStates.REJ.desc;
                            // case JobStates.ASG.code: return JobStates.ASG.desc;
                            case JobStates.ASG.code: return JobStates.PROG.desc;//change request to display In Progress instead of Assigned
                            default: break;
                        }
                    }
                },
                customFilterListOptions: {
                    render: v => {
                        switch (v) {
                            case JobStates.SUB.code: return JobStates.SUB.desc;
                            case JobStates.COM.code: return JobStates.COM.desc;
                            case JobStates.NEW.code: return JobStates.NEW.desc;
                            case JobStates.PAID.code: return JobStates.PAID.desc;
                            case JobStates.PMV.code: return JobStates.PMV.desc;
                            // case JobStates.PROG.code: return JobStates.PROG.desc;
                            case JobStates.DEL.code: return JobStates.DEL.desc;
                            case JobStates.CAN.code: return JobStates.CAN.desc;
                            case JobStates.REJ.code: return JobStates.REJ.desc;
                            // case JobStates.ASG.code: return JobStates.ASG.desc;
                            case JobStates.ASG.code: return JobStates.PROG.desc;//change request to display In Progress instead of Assigned
                            default: break;
                        }
                    }
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    //change request to display In Progress instead of Assigned
                    return getStatusDesc(value)
                }
            },
        },
        // 6
        {
            name: "action",
            label: t("listing:trucklist.action"),
            options: {
                filter: false,
                display: true,
                viewColumns: false,
                setCellHeaderProps: () => { return { style: { paddingLeft: '5%' } } },
                customBodyRender: (value, tableMeta, updateValue) => {
                    const status = tableMeta.rowData[10];
                    const jobId = tableMeta.rowData[0];

                    return <C1DataTableActions>
                        {/* do note that this beats the purpose of just passing prop values into C1DataTableActions*/}
                        <Grid item xs={12}>
                            <Grid container alignItems="flex-start">
                                <Grid container item alignItems="center">
                                    <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            {status === Status.NEW.code && <Link to={`/applications/services/truck/job/edit/${jobId}`}>
                                                <Tooltip title={t("buttons:edit")}>
                                                    <IconButton>
                                                        <Edit color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            </Link>}
                                        </span>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            <Link to={`/applications/services/truck/job/view/${jobId}`}>
                                                <Tooltip title={t("buttons:view")}>
                                                    <IconButton>
                                                        <Visibility color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            </Link>
                                        </span>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <span style={{ minWidth: '48px' }}>
                                            {(status === JobStates.SUB.code) &&
                                                <Tooltip title={t("buttons:cancel")}>
                                                    <IconButton onClick={(e) => handleCancelConfirm(e, jobId)} >
                                                        <Block color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            }
                                            {(status === JobStates.NEW.code) &&
                                                <Tooltip title={t("buttons:delete")}>
                                                    <IconButton onClick={(e) => handleDeleteConfirm(e, jobId)} >
                                                        <Delete color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            }
                                            {(status === JobStates.REJ.code) &&
                                                <Tooltip title={t("common:tooltip.rmkView")}>
                                                    <IconButton onClick={(e) => viewRemarksPopupHandler(jobId)} >
                                                        <SpeakerNotesOutlinedIcon color="primary" />
                                                    </IconButton>
                                                </Tooltip>
                                            }
                                        </span>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </C1DataTableActions>
                }
            }
        },
    ]

    const popUpAddHandler = () => {
        setOpenAddPopUp(true);
    };

    const redirectAddHandler = () => {
        setOpenAddPopUp(false);
        history.push("/applications/services/truck/job/new/-");
    };

    const viewRemarksPopupHandler = (jobId) => {
        setLoading(true);
        setPopUpDetails(popupDefaultValue);
        setOpenRemarkDialog(true)
        sendRequest("/api/v1/clickargo/clicdo/job/doiCo/" + jobId, "getJob", "get", null);
    };

    const handleDeleteConfirm = (e, jobId) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        setConfirm({ ...confirm, jobId });
        setOpen(true);
        setOpenDeleteConfirm({ ...openDeleteConfirm, action: "DELETE", open: true });
    }

    const handleDeleteHandler = (e) => {
        if (confirm && !confirm.jobId)
            return;

        setLoading(true);
        if (openDeleteConfirm && openDeleteConfirm.action === "DELETE") {
            setOpen(false);
            sendRequest("/api/v1/clickargo/clictruck/job/truck/" + confirm.jobId, "delete", "delete", {});
        } else if (openDeleteConfirm && openDeleteConfirm.action === "CANCEL") {
            setOpen(false);
            sendRequest("/api/v1/clickargo/clictruck/job/truck/" + confirm.jobId, "cancelJob", "get", null);
        }
    }

    const handleCancelConfirm = (e, jobId) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        setConfirm({ ...confirm, jobId });
        setOpen(true);
        setOpenDeleteConfirm({ ...openDeleteConfirm, action: "CANCEL", open: true });

    }

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(true);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setLoading(false), 500)
    }

    const handleDownloadBuildBody = (values) => {
        return values?.length > 0 && values.map(value => {

            if (value.data[5])
                value.data[5] = moment(value?.data[5]).format('YYYY-MM-DD HH:mm').toString();

            if (value.data[6]) {
                switch (value.data[6]) {
                    case JobStates.ASG.code:
                        value.data[6] = JobStates.PROG.desc;
                        break;
                    case JobStates.SUB.code:
                        if (value.data[8] === "UNRDY") {
                            value.data[6] = "Submitting";
                        } else {
                            value.data[6] = JobStates.SUB.desc;
                        }
                        break;
                    case JobStates.COM.code:
                        value.data[6] = JobStates.COM.desc;
                        break;
                    case JobStates.NEW.code:
                        value.data[6] = JobStates.NEW.desc;
                        break;
                    case JobStates.PAID.code:
                        value.data[6] = JobStates.PAID.desc;
                        break;
                    case JobStates.PMV.code:
                        value.data[6] = JobStates.PMV.desc;
                        break;
                    case JobStates.DEL.code:
                        value.data[6] = JobStates.DEL.desc;
                        break;
                    case JobStates.CAN.code:
                        value.data[6] = JobStates.CAN.desc;
                        break;
                    case JobStates.REJ.code:
                        value.data[6] = JobStates.REJ.desc;
                        break;
                    default: break;
                }
            }

            if (value.data[8])
                value.data[8] = "";//to exclude value in the CSV file

            return value;
        });
    }

    useEffect(() => {
        console.log("History", showHistory);
        if (showHistory) {
            setFilterHistory([{ attribute: "history", value: "history" }]);
        } else {
            setFilterHistory([{ attribute: "history", value: "default" }])

        }
    }, [showHistory]);


    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "delete":
                    setRefresh(true);
                    setFilterHistory([{ attribute: "history", value: "default" }])
                    setDeleteSuccess(true);
                    setSnackBarState({ ...snackBarState, open: true });
                    setLoading(false);
                    break;
                case "cancelJob":
                    sendRequest("/api/v1/clickargo/clicdo/job/doiCo/" + confirm.jobId, "cancelled", "put", { ...res.data, "action": "CANCEL" });
                    break;
                case "cancelled":
                    setRefresh(true);
                    setFilterHistory([{ attribute: "history", value: "default" }])
                    setDeleteSuccess(true);
                    setSnackBarState({ ...snackBarState, msg: t("listing:coJob.msg.cancelSuccess"), open: true });
                    setLoading(false);
                    break;
                case "getJob": {
                    setPopUpDetails({ jobRemarks: res?.data?.tckJob?.jobRemarks });
                    setLoading(false);
                    break;
                }
                default:
                    break;
            }
        }
        // eslint-disable-next-line
    }, [isLoading, res, error, urlId]);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("listing:coJob.msg.deleteSuccess"),
        severity: 'success'
    });
    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };
    let snackBar = null;
    if (deleteSuccess) {
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

    let elAddAction = <Button style={{
        backgroundColor: "#13B1ED",
        color: "#fff", padding: '10px 20px 10px 20px',
        fontWeight: "bold"
    }} onClick={() => popUpAddHandler()}>{t("listing:coJob.button.newJob")}</Button>

    return (<React.Fragment>
        {loading && <MatxLoading />}
        {snackBar}
        {confirm && confirm.jobId && (
            <ConfirmationDialog
                open={open}
                title={t("listing:coJob.popup.confirmation")}
                text={t("listing:coJob.msg.confirmation", { action: openDeleteConfirm?.action, id: confirm.jobId })}
                onYesClick={() => handleDeleteHandler()}
                onConfirmDialogClose={() => setOpen(false)}
            />
        )}

        <C1ListPanel
            routeSegments={[
                { name: "ClicTruck Job List" }
            ]} guideId="clicdo.doi.co.jobs.list"
            title={!showHistory ? t("listing:coJob.activeJobsTitle") : t("listing:coJob.jobsHistoryTitle")}
            elAction={elAddAction}>

            <C1DataTable
                // url={"/api/v1/clickargo/clicdo/job/doiCo"}
                url={"/api/v1/clickargo/clictruck/job/truck"}
                isServer={true}
                columns={columns}
                defaultOrder="jobDtLupd"
                defaultOrderDirection="desc"
                isRefresh={filterHistory}
                viewTextFilter={
                    <ButtonGroup color="primary" key="viewTextFilter" aria-label="outlined primary button group">
                        <Button key="history" startIcon={<HistoryIcon />} size="small" variant={showHistory ? "contained" : null} onClick={() => toggleHistory("history")}>{t("listing:coJob.button.history")}</Button>
                        <Button key="active" startIcon={<AssignmentTurnedInOutlinedIcon />} variant={!showHistory ? "contained" : null} size="small" onClick={() => toggleHistory("active")}>{t("listing:coJob.button.active")}</Button>
                    </ButtonGroup>
                }
                // viewHistory={{
                //     title: !showHistory ? t("listing:coJob.icon.showHistoryIcon") : t("listing:coJob.icon.showActiveIcon"),
                //     handler: () => toggleHistory(),
                //     icon: !showHistory ? <HistoryIcon /> : <AssignmentTurnedInOutlinedIcon />
                // }}
                filterBy={filterHistory}
                isShowToolbar
                isShowFilterChip
                isShowDownload={true}
                handleBuildBody={handleDownloadBuildBody}
                isShowPrint={true}
                isRowSelectable={false}
                guideId="clicdo.doi.co.jobs.list.table"
            />
        </C1ListPanel >

        <C1PopUp
            title={t("common:remarks.title")}
            openPopUp={openRemarkDialog}
            setOpenPopUp={setOpenRemarkDialog}
            actionsEl={null}>
            <BlRemarkPopUp
                inputData={popUpDetails}
                handlePopupInputChange={null}
                isDisabled={true}
                handleBtnAddClick={null}
                locale={t}
            />
        </C1PopUp>

        <C1PopUp
            title={t("listing:coJob.popup.newJob")}
            openPopUp={openAddPopUp}
            setOpenPopUp={setOpenAddPopUp}
            maxWidth={"sm"}
        >
            <Grid container spacing={3} alignItems="center">
                <Grid container item lg={6} md={6} xs={6}>
                    <C1SelectField
                        name="shipmentType"
                        label={t("listing:coJob.type")}
                        value={'IMPORT'}
                        required
                        // disabled={true}
                        isServer={true}
                        options={{
                            url: CK_MST_SHIPMENT_TYPE,
                            key: "shtId",
                            id: 'shtId',
                            desc: 'shtDesc',
                            isCache: true
                        }}
                    />
                </Grid>
                <Grid container item lg={6} md={6} xs={6}>
                    <C1Button text={t("listing:coJob.button.create")}
                        color="primary"
                        type="submit"
                        onClick={redirectAddHandler}
                    />
                </Grid>
            </Grid>
        </C1PopUp>

    </React.Fragment >);
};

export default JobList;