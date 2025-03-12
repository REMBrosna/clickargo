import { Box, Button, CircularProgress, Dialog, Grid, Snackbar, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip } from "@material-ui/core";
import grey from '@material-ui/core/colors/grey';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import { Add, NearMeOutlined, PublishOutlined } from "@material-ui/icons";
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import FileCopyOutlinedIcon from '@material-ui/icons/FileCopyOutlined';
import GetAppIcon from '@material-ui/icons/GetAppOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import GridActionButton from "app/atomics/organisms/GridActionButton";
import C1Alert from "app/c1component/C1Alert";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1IconButton from "app/c1component/C1IconButton";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import { AccountTypes, JobStates } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { customFilterDateDisplay, formatDate, generateID, isEmpty, previewPDF, Uint8ArrayToString, isArrayNotEmpty } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import AddJobAttPopup from "../../popups/AddJobAttPopup";
import AddTripAttPopup from "../../popups/AddTripAttPopup";

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: '#EFF2F5',
        color: theme.palette.common.gray,
        paddingTop: 15,
        paddingBottom: 15
    },
    body: {
        fontSize: "0.813rem",
        borderBottomWidth: 1,
        paddingTop: 15,
        paddingBottom: 15
    },
    root: {
        borderBottomWidth: 1
    }
}))(TableCell);

const useTableStyle = makeStyles({
    table: {
        minWidth: 450,
    },
    column: {
        width: 40,
    },
    // optionally added hover
    row: {
        '&:hover': {
            backgroundColor: grey[100]
        },
    },
});

const JobAuthLetters = ({
    inputData,
    viewType,
    showDocList = true,
    //rows,
    //reloadTable
}) => {

    const tableCls = useTableStyle();
    const { t } = useTranslation(["buttons", "listing", "common", "cargoowners"]);
    const { user } = useAuth();

    const [isRefresh, setRefresh] = useState(false);
    const [jobId, setJobId] = useState(inputData?.tckJob?.jobId ? inputData?.tckJob?.jobId : "empty");

    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");

    const [view, setView] = useState(false);
    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const [popUpFieldError, setPopUpFieldError] = useState({});
    const popupDefaultValue = {
        attId: generateID("CKJA"),
        tmstAttType: { mattId: "", },
        attData: null,
        attName: "",
        attRefNo: "",
        tckJob: { jobId: jobId }
    }
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
    const [openTripAttPopUp, setOpenTripAttPopUp] = useState(false);

    const tripDtlsDefaultValue = {
        atId: generateID("CKCTTA"),
        tckCtMstTripAttachType: { atypId: "", },
        atLocData: null,
        atName: "",
        atComment: "",
        // If there's only one trip, auto select
        tckCtTrip: { trId: inputData?.tckCtTripList?.length === 1 ? inputData?.tckCtTripList[0]?.trId : "" }
    }
    const [tripAttDetails, setTripAttDetails] = useState(tripDtlsDefaultValue);

    const ALLOWED_FILE_EXTS = ['pdf', 'doc', 'docx', 'jpeg', 'jpg', 'png'];
    const [confirm, setConfirm] = useState({ attId: null });
    const [confirmTrAt, setConfirmTrAt] = useState({ atId: null });
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [alreadyAssignedErrorOpen, setAlreadyAssignedErrorOpen] = useState({ msg: null, open: false });
    const [openDeleteConfirm, setOpenDeleteConfirm] = useState({ action: null, open: false });
    const [openDeleteConfirmTrAt, setOpenDeleteConfirmTrAt] = useState({ action: null, open: false });
    const [success, setSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: "",
        severity: 'success'
    });

    const arrJobStateDisable = [JobStates?.SUB?.code, JobStates?.ACP?.code, JobStates?.NEW?.code, null];
    const isToAccn = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code;
    const tripDocEditable = arrJobStateDisable.includes(inputData?.tckJob?.tckMstJobState?.jbstId);
    const isOngoing = [JobStates.ASG.code, JobStates.ONGOING.code, JobStates.PAUSED.code].includes(inputData?.tckJob?.tckMstJobState?.jbstId)

    const [validationErrors, setValidationErrors] = useState({});
    const [rows, setRows] = useState([]);

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

    const reloadTable = () => {
        if (
            inputData?.tckCtTripList &&
            isArrayNotEmpty(inputData?.tckCtTripList) &&
            viewType !== "new"
        )
        sendRequest(
            `/api/v1/clickargo/clictruck/job/trip/${inputData.jobId}/tripList`,
            "getTripList"
        );
    };
    
    useEffect(() => {
        reloadTable();
    }, []);
    // ///////

    /** --------------- Update states -------------------- */

    useEffect(() => {
        if (!isLoading && res && !error && !validation) {
            switch (urlId) {
                case "createAttachment": {
                    if (res?.data?.duplicate === true) {
                        setAlreadyAssignedErrorOpen({
                            ...alreadyAssignedErrorOpen,
                            msg: t("cargoowners:msg.duplicate"),
                            open: true
                        });
                        setLoading(false);
                    } else {
                        setLoading(false);
                        setSuccess(true)
                        setRefresh(true)
                        setSnackBarState({ ...snackBarState, open: true, msg: t("cargoowners:msg.createAttSuccess") });
                    }
                    break;
                }
                case "createTripAttachment": {
                    // Delay setLoading to prevent concurrent upload
                    setTimeout(() => setLoading(false), 500);
                    setSuccess(true)
                    setRefresh(true)
                    setOpenTripAttPopUp(false);
                    setSnackBarState({ ...snackBarState, open: true, msg: t("cargoowners:msg.createAttSuccess") });
                    reloadTable();
                    break;
                }
                case "download": {
                    setLoading(false);
                    viewFile(res?.data?.attName, res?.data?.attData);
                    break;
                }
                case "downloadTripAtt": {
                    setLoading(false);
                    viewFile(res?.data?.atName, res?.data?.atLocData);
                    break;
                }
                case "delete": {
                    setLoading(false);
                    setSuccess(true)
                    setRefresh(true)
                    setSnackBarState({ ...snackBarState, open: true, msg: t("cargoowners:msg.deleteAttSuccess") });
                    reloadTable();
                    break;
                }
                case "getTripList":
                  setRows(res?.data);
                  break;
                default: break;
            }

        }
        if (error) {
            //goes back to the screen
            setLoading(false);
        }
        if (validation) {
            setValidationErrors({ ...validation });
            setOpenTripAttPopUp(true)
            setLoading(false);
        }
    }, [isLoading, res, validation, error, urlId]);


    /** ---------------- Event handlers ------------------- */
    const handleViewFile = (e, attId) => {
        const url = `/api/v1/clickargo/attachments/job/${attId}`;
        setLoading(true)
        sendRequest(url, "download");
    };

    const viewTripAttach = (e, atId) => {
        const url = `/api/v1/clickargo/clictruck/tripdo/tripAttach/getTripAtt?id=${atId}`;
        setLoading(true)
        sendRequest(url, "downloadTripAtt");
    };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };

    const handleDeleteConfirm = (e, attId) => {
        e.preventDefault();
        setConfirm({ ...confirm, attId });
        setOpen(true);
        setOpenDeleteConfirm({ ...openDeleteConfirm, action: "DELETE", open: true });
    }

    const handleDeleteHandler = (e) => {
        if (confirm && !confirm.attId)
            return;

        setLoading(true);
        setSuccess(false)
        setRefresh(false)
        sendRequest("/api/v1/clickargo/attachments/job/" + confirm.attId, "delete", "delete", {});
        setOpen(false);
    }

    const handleDeleteTripAtt = (e, atId) => {
        e.preventDefault();
        setConfirmTrAt({ ...confirmTrAt, atId });
        setOpen(true);
        setOpenDeleteConfirmTrAt({ ...openDeleteConfirmTrAt, action: "DELETE", open: true });
    }

    const deleteTripAttach = (e) => {
        if (confirmTrAt && !confirmTrAt.atId)
            return;

        setLoading(true);
        setSuccess(false)
        setRefresh(false)
        sendRequest("/api/v1/clickargo/clictruck/tripdo/tripAttach/deleteTripAtt?id=" + confirmTrAt.atId, "delete", "delete", {});
        setOpen(false);
    }

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    const popUpAddHandler = () => {
        if (jobId === "empty") {
            setOpenWarning(true)
            setWarningMessage(t("cargoowners:msg.jobIdNotAvailable"))
            return;
        }
        setView(false);
        setOpenAddPopUp(true);
        setPopUpFieldError({});
        setPopUpDetails(popupDefaultValue);
    };

    const popUpAddTripAttHandler = () => {
        setView(false);
        setOpenTripAttPopUp(true);
        setValidationErrors({})
        setTripAttDetails(tripDtlsDefaultValue)
    };

    const handlePopUpFieldValidate = () => {
        let errors = {};

        if (popUpDetails?.tmstAttType?.mattId === '') {
            errors.mattId = t("common:validationMsgs.required");
        }

        let ext = popUpDetails?.attName.substring(popUpDetails?.attName.lastIndexOf('.') + 1, popUpDetails?.attName.length) || popUpDetails?.attName;
        if (!ALLOWED_FILE_EXTS.includes(ext.toLowerCase())) {
            errors.attName = t("cargoowners:msg.allowedFileExtensions");
        }

        if (popUpDetails.attName === '') {
            errors.attName = t("common:validationMsgs.required");
        }

        if (popUpDetails?.attRefNo === '') {
            errors.attRefNo = t("common:validationMsgs.required");
        }

        return errors;
    }

    const uploadAttachment = (e) => {
        if (!isEmpty(handlePopUpFieldValidate())) {
            setPopUpFieldError(handlePopUpFieldValidate());
        } else {
            setLoading(true);
            setPopUpFieldError({});
            setSuccess(false)
            setRefresh(false);
            setOpenAddPopUp(false);
            sendRequest(`/api/v1/clickargo/attachments/attach`, "createAttachment", "POST", { ...popUpDetails })
        }
    }

    const uploadTripAttachment = (e) => {
        setLoading(true);
        setSuccess(false)
        setRefresh(false);
        setValidationErrors({})
        tripAttDetails.tckCtTrip = inputData?.tckCtTripList?.find(val => tripAttDetails?.tckCtTrip?.trId == val?.trId)
        sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripAttach/createTripAtt`, "createTripAttachment", "POST", { ...tripAttDetails })
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        switch (elName) {
            case "tmstAttType.mattId":
            case "attRefNo":
                setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, elName, e.target.value) });
                break;
            case "tckCtMstTripAttachType.atypId":{
                setValidationErrors({});
                setTripAttDetails({ ...tripAttDetails, ...deepUpdateState(tripAttDetails, elName, e.target.value) });
            }
            break;
            case "atComment":
            case "tckCtTrip.trId":
                setTripAttDetails({ ...tripAttDetails, ...deepUpdateState(tripAttDetails, elName, e.target.value) });
                break;
            default:
                break;
        }
    };

    const handleInputFileChange = (e) => {
        e.preventDefault();
        var file = e.target.files[0];
        if (!file)
            return;

        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = e => {
            const uint8Array = new Uint8Array(e.target.result);
            var imgStr = Uint8ArrayToString(uint8Array);
            var base64Sign = btoa(imgStr);
            setPopUpDetails({ ...popUpDetails, attName: file.name, attData: base64Sign });
        };
    }

    const handleInputFileChangeTripAtt = (e) => {
        e.preventDefault();
        var file = e.target.files[0];
        if (!file)
            return;

        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = e => {
            const uint8Array = new Uint8Array(e.target.result);
            var imgStr = Uint8ArrayToString(uint8Array);
            var base64Sign = btoa(imgStr);
            setTripAttDetails({ ...tripAttDetails, atName: file.name, atLocData: base64Sign });
        };
    }

    // convert expire date to date format
    const stringToDate = (dateString) => {
        if (null !== dateString && dateString !== undefined && dateString !== '') {
            const date = dateString.split('-');
            const day = date[2].split('T');
            return day[0].concat("-").concat(date[1]).concat("-").concat(date[0]);
        }
    };

    const columns = [
        {
            name: "attId",
            label: t("listing:attachments.docId"),
            options: {
                display: 'excluded',
                filter: false,
            }
        },
        {
            name: "tmstAttType.mattId",
            label: " ",
            options: {
                display: 'excluded',
                filter: false
            }
        },
        {
            name: "tmstAttType.mattName",
            label: t("listing:attachments.docType"),
            options: {
                filter: true,
                filterType: 'dropdown',
                filterOptions: {
                    names: ["OTHERS"],
                    renderValue: v => {
                        switch (v) {
                            case "OTHERS": return "OTHERS";
                            default: break;
                        }
                    }
                },
            },
        },
        {
            name: "attName",
            label: t("listing:attachments.docName")
        },
        {
            name: "attRefNo",
            label: t("listing:attachments.docNo")
        },
        {
            name: "attDtCreate",
            label: t("listing:attachments.dtCreate"),
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
        {
            name: "action",
            label: t("listing:attachments.action"),
            options: {
                filter: false,
                display: true,
                sort: false,
                viewColumns: false,
                customHeadLabelRender: (columnMeta) => {
                    return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>
                },
                customBodyRender: (value, tableMeta, updateValue) => {
                    let attId = tableMeta.rowData[0];
                    let attType = tableMeta.rowData[1];
                    return <C1DataTableActions>
                        <Grid container alignItems="flex-start" justifyContent="center">
                            <span style={{ minWidth: '48px' }}>
                                <C1LabeledIconButton
                                    tooltip={t("buttons:download")}
                                    label={t("buttons:download")}
                                    action={(e) => handleViewFile(e, attId)}>
                                    <GetAppIcon />
                                </C1LabeledIconButton>
                            </span>
                            {((inputData?.tckJob?.tckMstJobState?.jbstId === JobStates.DRF.code
                                || inputData?.tckJob?.tckMstJobState?.jbstId === JobStates.NEW.code)
                                && viewType !== "view") &&
                                <span style={{ minWidth: '48px' }}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:delete")}
                                        label={t("buttons:delete")}
                                        action={(e) => handleDeleteConfirm(e, attId)}>
                                        <DeleteOutlinedIcon />
                                    </C1LabeledIconButton>
                                </span>
                            }
                        </Grid>
                    </C1DataTableActions>
                }
            }
        }
    ]

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

    let actionElJobAtt =
        <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            {loading ?
                <CircularProgress color="inherit" size={30} />
                :
                <NearMeOutlined color="primary" fontSize="large" onClick={() => uploadAttachment()} />
            }
        </C1IconButton>

    let actionElTripAtt =
        <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
            {loading ?
                <CircularProgress color="inherit" size={30} />
                :
                <NearMeOutlined color="primary" fontSize="large" onClick={() => uploadTripAttachment()} />
            }
        </C1IconButton>

    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}
            {confirm && confirm.attId && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.deleteConfirm", { action: openDeleteConfirm?.action })}
                    onYesClick={() => handleDeleteHandler()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}
            {confirmTrAt && confirmTrAt.atId && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.deleteConfirm", { action: openDeleteConfirmTrAt?.action })}
                    onYesClick={() => deleteTripAttach()}
                    onConfirmDialogClose={() => setOpen(false)}
                />
            )}
            <C1DialogPrompt confirmationObj={{
                openConfirmPopUp: alreadyAssignedErrorOpen?.open,
                onConfirmationDialogClose: () => { },
                text: alreadyAssignedErrorOpen?.msg,
                title: t("cargoowners:msg.error"),
                onYesClick: () => setAlreadyAssignedErrorOpen({ ...alreadyAssignedErrorOpen, open: false }),
                yesBtnText: "buttons:ok"
            }} />
            {showDocList && (
                <C1TabContainer>
                    <Grid item xs={12}>
                        <C1CategoryBlock icon={<FileCopyOutlinedIcon />} title={t("listing:attachments.titleSub")}>
                            <C1DataTable
                                url={"/api/v1/clickargo/attachments/job"}
                                isServer={true}
                                columns={columns}
                                defaultOrder="attDtCreate"
                                defaultOrderDirection="desc"
                                filterBy={
                                    [
                                        { attribute: "TCkJob.jobId", value: jobId },
                                        { attribute: "mattStatus", value: 'A' }
                                    ]
                                }
                                isRefresh={isRefresh}
                                isShowDownload={true}
                                isShowToolbar={(viewType !== "view")}
                                isShowPrint={true}
                                isShowViewColumns={true}
                                isShowFilter={true}
                                showAdd={(viewType !== "view") ? {
                                    type: "popUp",
                                    popUpHandler: popUpAddHandler,
                                } : null}
                                guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
                            />
                        </C1CategoryBlock>
                    </Grid>
                </C1TabContainer>
            )}
            <C1PopUp
                title={t("listing:attachments.title")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                actionsEl={actionElJobAtt}>
                <AddJobAttPopup
                    view={view}
                    inputData={popUpDetails}
                    viewType={"view"}
                    handleInputChange={handleInputChange}
                    handleInputFileChange={handleInputFileChange}
                    locale={t}
                    errors={popUpFieldError}
                />
            </C1PopUp>
            <Dialog maxWidth="xs" open={openWarning} >
                <div className="p-8 text-center w-360 mx-auto">
                    <h4 className="capitalize m-0 mb-2">{"Warning"}</h4>
                    <p>{warningMessage}</p>
                    <div className="flex justify-center pt-2 m--2">
                        <Button
                            className="m-2 rounded hover-bg-primary px-6"
                            variant="outlined"
                            color="primary"
                            onClick={(e) => handleWarningAction(e)}
                        >
                            {t("cargoowners:popup.ok")}
                        </Button>
                    </div>
                </div>
            </Dialog>

            <C1TabContainer>
                <Grid item xs={12}>
                    <Box component={`div`} sx={{ opacity: !tripDocEditable ? 1 : 0.3, pointerEvents: !tripDocEditable ? '' : 'none' }}>
                        <C1CategoryBlock
                            icon={<FileCopyOutlinedIcon />} title={t("listing:attachments.tripSub")}>
                        </C1CategoryBlock>

                        {isOngoing ?
                            <GridActionButton
                                showAddButton={isToAccn && [{
                                    label: t("listing:attachments.newTripAtt").toUpperCase(),
                                    action: () => popUpAddTripAttHandler(),
                                    icon: <Add />
                                }]}
                            />
                            : <div style={{ height: 10 }} />}
                        <TableContainer /** component={Paper} **/ >
                            <Table className={tableCls.table} aria-label="simple table">
                                <TableHead>
                                    <TableRow>
                                        <StyledTableCell align="left" style={{ paddingLeft: 20 }}>{t("common:supportingDocs.tableHdrs.trip")}</StyledTableCell>
                                        <StyledTableCell align="left">{t("common:supportingDocs.tableHdrs.atType")}</StyledTableCell>
                                        <StyledTableCell align="left">{t("common:supportingDocs.tableHdrs.atName")}</StyledTableCell>
                                        <StyledTableCell align="left">{t("common:supportingDocs.tableHdrs.dtCreate")}</StyledTableCell>
                                        <StyledTableCell align="center" style={{ width: '12.5%' }}>{t("common:supportingDocs.tableHdrs.action")}</StyledTableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {rows?.map((el, idx) => {
                                        let row = el.row;
                                        let parentElement = <TableRow key={row.id} className={tableCls.row}>
                                            <StyledTableCell align="left" style={{ paddingLeft: 20 }}>{(row.locFrom != null ? row.locFrom : "") + " - " + (row.locTo != null ? row.locTo : "")}</StyledTableCell>
                                            <StyledTableCell align="left">{row.fileType ? row.fileType : "-"}</StyledTableCell>
                                            <StyledTableCell align="left">{row.fileName ? row.fileName : "-"}</StyledTableCell>
                                            <StyledTableCell align="left">{row.createdAt ? formatDate(row.createdAt, true) : "-"}</StyledTableCell>
                                            <StyledTableCell align="center">
                                                {row.fileName &&
                                                    <Grid container alignItems="flex-start" justifyContent="center">
                                                        <span style={{ minWidth: '48px' }}>
                                                            <C1LabeledIconButton
                                                                tooltip={t("buttons:download")}
                                                                label={t("buttons:download")}
                                                                action={(e) => viewTripAttach(e, row.id)}>
                                                                <GetAppIcon />
                                                            </C1LabeledIconButton>
                                                        </span>
                                                        {isToAccn && isOngoing &&
                                                            <span style={{ minWidth: '48px' }}>
                                                                <C1LabeledIconButton
                                                                    tooltip={t("buttons:delete")}
                                                                    label={t("buttons:delete")}
                                                                    action={(e) => handleDeleteTripAtt(e, row.id)}>
                                                                    <DeleteOutlinedIcon />
                                                                </C1LabeledIconButton>
                                                            </span>
                                                        }
                                                    </Grid>
                                                }
                                                {/** This block is just a workaround to render equal spacing for empty records. 
                                             * Can also use css to adjust row height specific to this table.
                                             */}
                                                {!row.fileName &&
                                                    <Grid container alignItems="flex-start" justifyContent="center">
                                                        <span style={{ minWidth: '48px', visibility: "hidden" }}>
                                                            <C1LabeledIconButton
                                                                tooltip={t("buttons:upload")}
                                                                label={t("buttons:upload")}
                                                                action={null} >
                                                                <PublishOutlined />
                                                            </C1LabeledIconButton>
                                                        </span>
                                                    </Grid>
                                                }
                                            </StyledTableCell>
                                        </TableRow>

                                        let subElement = [];
                                        if (el.subRow && el.subRow.length >= 1) {
                                            el.subRow.map((sr, i) => {
                                                subElement.push(
                                                    (<TableRow key={sr.id} className={tableCls.row}>
                                                        <StyledTableCell colSpan={1}></StyledTableCell>
                                                        <StyledTableCell align="left">{sr.fileType ? sr.fileType : "-"}</StyledTableCell>
                                                        <StyledTableCell align="left">{sr.fileName ? sr.fileName : "-"}</StyledTableCell>
                                                        <StyledTableCell align="left">{sr.createdAt ? formatDate(sr.createdAt, true) : "-"}</StyledTableCell>
                                                        <StyledTableCell align="center">
                                                            <Grid container alignItems="flex-start" justifyContent="center">
                                                                <span style={{ minWidth: '48px' }}>
                                                                    <C1LabeledIconButton
                                                                        tooltip={t("buttons:download")}
                                                                        label={t("buttons:download")}
                                                                        action={(e) => viewTripAttach(e, sr.id)}>
                                                                        <GetAppIcon />
                                                                    </C1LabeledIconButton>
                                                                </span>
                                                                {isToAccn && isOngoing &&
                                                                    <span style={{ minWidth: '48px' }}>
                                                                        <C1LabeledIconButton
                                                                            tooltip={t("buttons:delete")}
                                                                            label={t("buttons:delete")}
                                                                            action={(e) => handleDeleteTripAtt(e, sr.id)}>
                                                                            <DeleteOutlinedIcon />
                                                                        </C1LabeledIconButton>
                                                                    </span>
                                                                }
                                                            </Grid>
                                                        </StyledTableCell>
                                                    </TableRow>));
                                            });
                                        }

                                        return <React.Fragment key={idx}>
                                            {parentElement}
                                            {subElement.map(i => i)}
                                        </React.Fragment>;
                                    })}

                                    {
                                        (!rows || rows.length === 0) && (
                                            <TableRow key={1}>
                                                <StyledTableCell colSpan="5">
                                                    <div style={{ textAlign: "center" }}>{t("common:genericMsgs.emptyTable")}</div>
                                                </StyledTableCell>
                                            </TableRow>
                                        )
                                    }
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Box>
                </Grid>
            </C1TabContainer>

            <C1PopUp
                title={t("listing:attachments.tripAddDoc")}
                openPopUp={openTripAttPopUp}
                setOpenPopUp={setOpenTripAttPopUp}
                actionsEl={actionElTripAtt}>
                <AddTripAttPopup
                    view={view}
                    inputData={tripAttDetails}
                    handleInputChange={handleInputChange}
                    handleInputFileChange={handleInputFileChangeTripAtt}
                    locale={t}
                    errors={validationErrors}
                    tripList={inputData?.tckCtTripList}
                />
            </C1PopUp>

        </React.Fragment >
    );
};

export default JobAuthLetters;