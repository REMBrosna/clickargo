import { Backdrop, Button, CircularProgress, Grid, Popover, Typography } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from '@material-ui/core/styles';
import { CancelOutlined, EditOutlined, GetAppOutlined, VisibilityOutlined } from "@material-ui/icons";
import AddCircleIcon from '@material-ui/icons/AddCircleOutlineOutlined';
import BlockIcon from '@material-ui/icons/Block';
import ZoomInIcon from '@material-ui/icons/ZoomIn';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Button from "app/c1component/C1Button";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1IconButton from "app/c1component/C1IconButton";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import useHttp from "app/c1hooks/http";
import { AccountTypes, CK_MST_SHIPMENT_TYPE, JobStates, ShipmentTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate, isEmpty, previewPDF } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import LocationDashboardPopUp from "../../popups/LocationDashboardPopUp";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
}));

const JobsToBill = ({ roleId, filterStatus, onFilterChipClose, onFilterChange }) => {

    const bdClasses = useStyles();

    const { t } = useTranslation(["buttons", "listing", "ffclaims", "common", "status", "job"]);

    const { isLoading, res, error, urlId, sendRequest } = useHttp();

    const [confirm, setConfirm] = useState({ id: null });
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    
    const [isRefresh, setRefresh] = useState(false);

    const [success, setSuccess] = useState(false);
    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });

    const [shipmentType, setShipmentType] = useState();
    const [showUploadTemplatePopUp, setShowUploadTemplatePopUp] = useState(false)

    const [showHistory, setShowHistory] = useState(false);
    const [filterBy, setFilterBy] = useState([
        { attribute: "history", value: "default" },
        { attribute: "jobForBilling", value: true }]);

    const [openRemarkDialog, setOpenRemarkDialog] = useState(false);
    const [openBlDialog, setOpenBlDialog] = useState(false);
    
    const [staticJobId, setStaticJobId] = useState(null)
    const [anchorEl, setAnchorEl] = React.useState(null);
    const openPopover = Boolean(anchorEl)
    const [showEmptyObjectPopUp, setShowEmptyObjectPopUp] = useState(false)

    const popupDefaultValue = { jobRemarks: "", };

    // eslint-disable-next-line
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
    // eslint-disable-next-line
    const [validationError, setValidationError] = useState({});
    // eslint-disable-next-line
    const [isDisabled, setDisabled] = useState(false);

    const [fileUploaded, setFileUploaded] = useState(false);

    const [inputData, setInputData] = useState({});
    const [tripListData, setTripListData] = useState([])
    const [showLocationPopUp, setShowLocationPopUp] = useState(false)

    const { user } = useAuth();
    const isCargoOwner = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code;

    const popupColumns = [
        {
            name: "tckCtTripLocationByTrFrom.tckCtLocation.locAddress",
            label: "From"
        },
        {
            name: "tckCtTripLocationByTrTo.tckCtLocation.locAddress",
            label: "To"
        },
    ]

    const downloadFileHandler = (fileEntity, fileId) => {
        console.log('fileEntity', fileEntity)
        console.log('filed', fileId)
        const dlApi = `/api/v1/clickargo/clictruck/attach/byJobId/${fileEntity}/${fileId}`;
        sendRequest(dlApi, "downloadFile", "get");
    };

    const truckCols =
        [
            // 0 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[0]
            {
                name: "jobId",
                label: t("listing:trucklist.jobid")
            },
            {
                name: "tckJob.tckMstShipmentType.shtId",
                label: t("listing:trucklist.type"),
                options: {
                    filterType: 'dropdown',
                    filterOptions: {
                        names: Object.keys(ShipmentTypes),
                        renderValue: v => {
                            return ShipmentTypes[v].desc;
                        }
                    },
                }
            },
            {
                name: "tcoreAccnByJobPartyCoFf.accnName",
                label: t("listing:trucklist.coff"),
                options: {
                    filter: true,
                }
            },
            {
                name: "jobDtDelivery",
                label: t("listing:trucklist.dtdelivery"),
                options: {
                    filter: true,
                    filterType: 'custom',
                    display: true,
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "jobShipmentRef",
                label: t("listing:trucklist.shipref")
            },
            {
                name: "pickUp.tckCtLocation.locAddress",
                label: t("listing:trucklist.pickup"),
                options: {
                    sort: false,
                    filter: false
                }
            },
            {
                name: "lastDrop.tckCtLocation.locName",
                label: t("listing:trucklist.lastdrop"),
                options: {
                    sort: false,
                    filter: false,
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const jobId = tableMeta.rowData[0]
                        const jobType = tableMeta.rowData[1]

                        return (
                            <React.Fragment>
                                {jobType === "DOMESTIC" ?
                                    (<C1LabeledIconButton
                                        tooltip={t("buttons:viewLoc")}
                                        label={t("buttons:view")}
                                        action={() => handleLocationPopUpShow(jobId)}
                                    >
                                        <ZoomInIcon />
                                    </C1LabeledIconButton>)
                                    :
                                    (<span>{value}</span>)
                                }
                            </React.Fragment>
                        )
                    }
                }
            },
            {
                name: "jobDtCreate",
                label: t("listing:trucklist.dtCreate"),
                options: {
                    filter: isCargoOwner,
                    filterType: 'custom',
                    display: isCargoOwner ? true : 'excluded',
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "jobDtLupd",
                label: t("listing:trucklist.dtLupd"),
                options: {
                    filter: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code,
                    filterType: 'custom',
                    display: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code
                        ? true : 'excluded',
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "jobInPaymentDtDue",
                label: t("listing:trucklist.dtDueIn"),
                options: {
                    filter: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code && showHistory,
                    filterType: 'custom',
                    display: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code && showHistory
                        ? true : 'excluded',
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },
            {
                name: "jobOutPaymentDtDue",
                label: t("listing:trucklist.dtDueOut"),
                options: {
                    filter: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code && showHistory,
                    filterType: 'custom',
                    display: user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code && showHistory
                        ? true : 'excluded',
                    customFilterListOptions: {
                        render: v => v.map(l => l),
                        update: (filterList, filterPos, index) => {
                            filterList[index].splice(filterPos, 1);
                            return filterList;
                        }
                    },
                    filterOptions: {
                        display: customFilterDateDisplay
                    },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        return formatDate(value, true);
                    }
                },
            },

            {
                name: "tckJob.tckMstJobState.jbstId",
                label: t("listing:trucklist.status"),
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
                name: "action",
                label: t("listing:common.action"),
                options: {
                    filter: false,
                    sort: false,
                    display: true,
                    viewColumns: false,
                    setCellHeaderProps: () => { return { style: { textAlign: "center" } } },
                    customBodyRender: (value, tableMeta, updateValue) => {
                        const jobId = tableMeta.rowData[0];
                        const status = tableMeta.rowData[10];
                       

                        return <Grid container justifyContent="center" alignItems="center" spacing={1} style={{ minWidth: 80 }} wrap="nowrap" >
                            <Grid item>
                                {(status === JobStates.DRF.code || status === JobStates.NEW.code) &&
                                    <C1LabeledIconButton tooltip={t("buttons:edit")}
                                        label={t("buttons:edit")}
                                        action={() => history.push({ pathname: `/applications/services/job/truck/edit`, state: { jobId } })}>
                                        <EditOutlined />
                                    </C1LabeledIconButton>
                                }
                            </Grid>
                            {
                                showHistory &&
                                <Grid item>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:download")}
                                        label={t("buttons:download")}
                                        action={(e) => handleOpenDownloadToggle(e, jobId)}
                                    >
                                        <GetAppOutlined />
                                    </C1LabeledIconButton>
                                </Grid>
                            }
                            <Grid item>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:view")}
                                    label={t("buttons:view")}
                                    action={() => history.push({ pathname: `/applications/services/job/truck/view`, state: { jobId } })}>
                                    <VisibilityOutlined />
                                </C1LabeledIconButton>
                            </Grid>
                            <Grid item>
                                {(status === JobStates.DRF.code || status === JobStates.NEW.code) &&
                                    <C1LabeledIconButton tooltip={t("buttons:cancel")}
                                        label={t("buttons:cancel")}
                                        action={(e) => handleCancelConfirm(e, jobId)}>
                                        <CancelOutlined />
                                    </C1LabeledIconButton>
                                }
                                {(status === JobStates.SUB.code) &&
                                    <C1LabeledIconButton
                                        tooltip={t("listing:billOfLading.action.rejectBL")}
                                        label={t("listing:billOfLading.action.reject")}
                                        action={(e) => handleRejectConfirm(e, jobId)}>
                                        <BlockIcon />
                                    </C1LabeledIconButton>
                                }
                            </Grid>
                        </Grid>
                    },
                },
            },
        ];

    const handleOpenDownloadToggle = (event, id) => {
        setStaticJobId(id)
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    }

    // const handleDeleteConfirm = (e, id) => {
    //     e.preventDefault();
    //     setLoading(false);
    //     setRefresh(false);
    //     setConfirm({ ...confirm, id });
    //     setOpen(true);
    //     setOpenActionConfirm({ ...openActionConfirm, action: "DELETE", open: true });
    // }

    const handleCancelConfirm = (e, id) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        setConfirm({ ...confirm, id });
        setOpen(true);
        setOpenActionConfirm({ ...openActionConfirm, action: "CANCEL", open: true });
    }

    // TODO: No status REJECTED, not used at this time
    const handleRejectConfirm = (e, id) => {
        e.preventDefault();
        setLoading(false);
        setRefresh(false);
        setConfirm({ ...confirm, id });
        setOpen(true);
        setOpenActionConfirm({ ...openActionConfirm, action: "REJECT", open: true });
    }

    const handleActionHandler = (e) => {
        if (confirm && !confirm.id)
            return;

        setLoading(true);
        if (openActionConfirm && openActionConfirm.action === "DELETE") {
            setOpen(false);

        } else if (openActionConfirm && openActionConfirm.action === "CANCEL") {
            setOpen(false);
            sendRequest("/api/v1/clickargo/clictruck/job/truck/" + confirm.id, "cancelJob", "get", null);
        } else if (openActionConfirm && openActionConfirm.action === "REJECT") {
            setOpen(false);

        }
    }

    const toggleHistory = (filter) => {
        setLoading(true);
        setRefresh(false);
        setShowHistory(filter === 'history' ? true : false);
        setTimeout(() => setRefresh(true), 500);
        setTimeout(() => setLoading(false), 500);
    }

    const handlePopUpBtnAddClick = (e) => {
        let validateErr = validateFields();
        setValidationError(validateErr);
        if (isEmpty(validateErr)) {
            setOpenRemarkDialog(false);
            setRefresh(false);
            if (openActionConfirm && openActionConfirm.action === "REJECT") {
                sendRequest("/api/v1/clickargo/clicdo/job/doiCo/" + confirm.id, "rejectJob", "get", null);
            }
        }
    }

    // const downloadBLHandler = blId => {
    //     if (fileUploaded) {
    //         viewFile(inputData?.blFileName, inputData?.blFileData)
    //     } else if (blId) {
    //         setBlLoadDlOpen(true)
    //         sendRequest(`api/ck/doi/getBlFileData?blId=${blId}&type=bl`, "previewBL");
    //     } else {
    //         viewFile(inputData?.blFileName, inputData?.blFileData)
    //     }
    //     setFileUploaded(false);
    // };

    // const viewFile = (fileName, data) => {
    //     previewPDF(fileName, data);
    // };

    // validate required field used for remark popup
    const validateFields = () => {
        let errors = {};
        if (!popUpDetails?.jobRemarks) {
            errors.jobRemarks = t("common:validationMsgs.required")
        }
        return errors;
    }

    // const popupViewHandler = (id, viewOnly, action) => {
    //     setLoading(true)
    //     setInputData({});
    //     setOpenBlDialog(true)
    //     setValidationError({})
    //     sendRequest(`/api/ck/doi/ckDoBl/${id}`, "getBl", "GET", null);
    // };

    // const [confirmationObj, setConfirmationObj] = useState({})

    useEffect(() => {
        if (showHistory) {
            setFilterBy([...filterBy, { attribute: "history", value: "history" }]);
        } else {
            setFilterBy([...filterBy, { attribute: "history", value: "default" }])
        }

    // eslint-disable-next-line
    }, [showHistory]);

    useEffect(() => {
        setTimeout(() => setSnackBarOptions(defaultSnackbarValue), 100);
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "cancelJob":
                    console.log("cancelJob")
                    sendRequest("/api/v1/clickargo/clictruck/job/truck/" + confirm.id, "cancelled", "put", { ...res.data, "action": "CANCEL" });
                    break;
                case "cancelled":
                    setRefresh(true);
                    setFilterBy([{ attribute: "history", value: "default" }])
                    setSuccess(true);
                    setSnackBarState({ ...snackBarState, msg: t("listing:coJob.msg.cancelSuccess"), open: true });
                    setLoading(false);
                    break;
                case "getJob": {

                    setLoading(false);
                    break;
                }
                case "getTripList": {
                    if (res?.data?.tckCtTripList != null) {
                        setTripListData(res?.data?.tckCtTripList)
                    } else {
                        setTripListData([])
                    }

                    break
                }
                case "downloadFile": {
                    if (res.data.data != null) {
                        const fileName = res?.data?.filename
                        console.log("filename ", fileName)
                        previewPDF(fileName != null ? fileName : "preview.pdf", res?.data?.data);
                    } else {
                        setShowEmptyObjectPopUp(true)
                    }

                    break
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
        msg: t("common:common.msg.deleted"),
        severity: 'success'
    });

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const popUpAddHandler = () => {
        setInputData({});
        setOpenBlDialog(true);
        setValidationError({});
    };

    const handleLocationPopUpShow = (id) => {
        sendRequest(`/api/v1/clickargo/clictruck/job/truck/${id}`, "getTripList", 'get')
        setShowLocationPopUp(true)
    }

    let snackBar = null;
    if (success) {
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

    let elAction = <C1IconButton tooltip={t("common:tooltip.rmkAdd")} childPosition="right">
        <AddCircleIcon color="primary" fontSize="large" onClick={(e) => handlePopUpBtnAddClick(e)}></AddCircleIcon>
    </C1IconButton>



    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}

            {/* Confirmation Popup */}
            {confirm && confirm.id && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", { action: openActionConfirm?.action, id: confirm.id })}
                    onYesClick={() => handleActionHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}

            <LocationDashboardPopUp
                openPopUp={showLocationPopUp}
                setOpenPopUp={setShowLocationPopUp}
                tripListData={tripListData}
                title={t("job:tripDetails.domestic.location")}
                columns={popupColumns}
            />

            <C1PopUp
                title={t("common:remarks.title")}
                openPopUp={openRemarkDialog}
                setOpenPopUp={setOpenRemarkDialog}
                actionsEl={isDisabled ? null : elAction}>
            </C1PopUp>

            <C1PopUp
                title={"Upload Template"}
                openPopUp={showUploadTemplatePopUp}
                setOpenPopUp={setShowUploadTemplatePopUp}
                maxWidth={'sm'}>
                <React.Fragment>
                    <Grid container spacing={3} >
                        <Grid container item xs={12}>
                            <C1FileUpload
                                name={"UploadTemplate"}
                                label={"Upload Template"}
                                inputLabel={"Template Picked"}
                            />
                        </Grid>
                    </Grid>
                    <Grid container alignItems="center" item xs={12}>
                        <C1Button text={t("listing:coJob.button.create")}
                            color="primary"
                            type="submit"
                            onClick={() => setShowUploadTemplatePopUp(false)}
                        />
                    </Grid>
                </React.Fragment>
            </C1PopUp>

            {/* Popup for New Job button  */}
            <C1PopUp
                title={"New Job"}
                openPopUp={openBlDialog}
                setOpenPopUp={setOpenBlDialog}
                maxWidth={'sm'} >
                <Grid container spacing={3} alignItems="center">
                    <Grid container item >
                        <C1SelectField
                            name="shipmentType"
                            label={t("listing:coJob.type")}
                            value={shipmentType}
                            required
                            onChange={e => setShipmentType(e.target.value)}
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
                    <Grid container item>
                        <C1Button text={t("listing:coJob.button.create")}
                            color="primary"
                            type="submit"
                            disabled={shipmentType ? false : true}
                            onClick={() => history.push("/applications/services/job/truck/new/-", {
                                shipmentType: shipmentType
                            })}
                        />
                    </Grid>
                </Grid>
            </C1PopUp>

            <DataTable
                url="/api/v1/clickargo/clictruck/job/truck"
                columns={truckCols}
                title=""
                defaultOrder="jobDtCreate"
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
            />
            
            <Popover
                open={openPopover} anchorEl={anchorEl} onClose={handleClose} anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                }}>
                <Grid container direction={"column"} alignItems={'flex-start'}>
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

            <C1PopUp
                openPopUp={showEmptyObjectPopUp}
                setOpenPopUp={setShowEmptyObjectPopUp}
                maxWidth={'sm'}
            >
                <Grid container alignItems='center' justifyContent='center'>
                    <Typography>Sorry, this file doesn't exist</Typography>
                </Grid>
            </C1PopUp>


        </React.Fragment >
    );

};

export default withErrorHandler(JobsToBill);