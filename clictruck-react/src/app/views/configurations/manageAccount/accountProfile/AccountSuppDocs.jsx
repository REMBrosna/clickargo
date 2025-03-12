import { Box, Grid, Snackbar, TableContainer } from "@material-ui/core";
import grey from '@material-ui/core/colors/grey';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import { Add, NearMeOutlined } from "@material-ui/icons";
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import PublishOutlinedIcon from '@material-ui/icons/PublishOutlined';
import VisibilityOutlinedIcon from '@material-ui/icons/VisibilityOutlined';
import React, { useEffect, useState } from "react";

import GridActionButton from "app/atomics/organisms/GridActionButton";
import C1Alert from "app/c1component/C1Alert";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1IconButton from "app/c1component/C1IconButton";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { AccountStatus } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { useStyles } from "app/c1utils/styles";
import { formatDate, generateID, getValue, isEmpty, previewPDF, Uint8ArrayToString } from "app/c1utils/utility";
import { ConfirmationDialog, MatxLoading } from "matx";

import AddAccnAttPopup from "./AddAccnAttPopup";

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

const AccountSuppDocs = ({ docs, reloadTable, locale, inputData, viewType, displayEmpty }) => {

    const { isLoading, isFormSubmission, res, urlId, error, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);

    const tableCls = useTableStyle();
    const classes = useStyles();

    const [success, setSuccess] = useState(false);
    // eslint-disable-next-line
    const [refresh, setRefresh] = useState(false);

    const popupDefaultValue = {
        aatId: generateID("GENID"),
        tckMstAccnAttType: {
            id: {
                atId: "OTHER",
                atWorkflow: ""
            }
        },
        aatLocData: null,
        aatName: "",
        aatNo: "",
        atDtValidility: null,
        tcoreAccn: {
            accnId: inputData?.accnDetails?.accnId
        }
    }
    
    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const [popUpFieldError, setPopUpFieldError] = useState({});
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
    const ALLOWED_FILE_EXTS = ['pdf', 'doc', 'docx', 'jpeg', 'jpg', 'png'];
    

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: "",
        severity: 'success'
    });

    const [openDeleteConfirm, setOpenDeleteConfirm] = useState({ action: null, open: false });
    const [confirm, setConfirm] = useState({ aatId: null });
    const [open, setOpen] = useState(false);

    const [duplicateErrorOpen, setDuplicateErrorOpen] = useState({ msg: null, open: false });
    const [enableDd, setEnableDd] = useState(false);

    const accnSuspended = inputData?.accnDetails?.accnStatus === AccountStatus.SUS_APPROVED.code;
    const accnId = inputData?.accnDetails?.accnId;

    const handleViewFile = (e, attId) => {
        setLoading(true)
        sendRequest(`/api/v1/clickargo/attachments/accnAtt/${attId}`, "download", 'get');
    }

    const viewFile = (fileName, data) => {
        // downloadFile(fileName, data);
        previewPDF(fileName, data);
    };

    const uploadAttachment = (e) => {
        if (!isEmpty(handlePopUpFieldValidate())) {
            setPopUpFieldError(handlePopUpFieldValidate());
        } else {
            setOpenAddPopUp(false);
            setPopUpFieldError({});
            setLoading(true);
            setSuccess(false)
            setRefresh(false);
            sendRequest(`/api/v1/clickargo/attachments/accnAttach`, "createAttachment", "POST", { ...popUpDetails })
        }
    }

    const handlePopUpFieldValidate = () => {
        console.log("popUpDetails ", popUpDetails)
        let errors = {};

        if (getValue(popUpDetails?.tckMstAccnAttType?.id?.atWorkflow) === "") {
            errors.atId = locale("common:validationMsgs.required");
        }

        let ext = popUpDetails?.aatName.substring(popUpDetails?.aatName.lastIndexOf('.') + 1, popUpDetails?.aatName.length) || popUpDetails?.aatName;
        if (!ALLOWED_FILE_EXTS.includes(ext.toLowerCase())) {
            errors.aatName = locale("cargoowners:msg.allowedFileExtensions");
        }

        if (popUpDetails.aatName === '') {
            errors.aatName = locale("common:validationMsgs.required");
        }
        if (popUpDetails.aatNo === '') {
            errors.aatNo = locale("common:validationMsgs.required");
        }

        if (popUpDetails.atDtValidility === null) {
            errors.atDtValidility = locale("common:validationMsgs.required");
        }

        return errors;
    }

    const popUpAddHandler = (type) => {
        undefined === type ? setEnableDd(true) : setEnableDd(false)
        setOpenAddPopUp(true);
        setPopUpFieldError({});
        // setPopUpDetails(popupDefaultValue);
        setPopUpDetails({
            ...popupDefaultValue, tckMstAccnAttType: {
                id: {
                    atId: "OTHER",
                    atWorkflow: type
                }
            },
        });
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, elName, e.target.value) });
    };

    const handleDateChange = (name, e) => {
        setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, name, e) })
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
            setPopUpDetails({ ...popUpDetails, aatName: file.name, aatLocData: base64Sign });
        };
    }

    const handleDeleteConfirm = (e, aatId) => {
        e.preventDefault();
        setConfirm({ ...confirm, aatId });
        setOpen(true);
        setOpenDeleteConfirm({ ...openDeleteConfirm, action: "DELETE", open: true });
    }

    const handleDeleteHandler = (e) => {
        if (confirm && !confirm.aatId)
            return;

        setLoading(true);
        setSuccess(false)
        setRefresh(false)
        sendRequest("/api/v1/clickargo/attachments/accnAtt/" + confirm.aatId, "deleteAttachment", "DELETE", {});
        setOpen(false);
    }

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "download":
                    setLoading(false)
                    viewFile(res.data.aatName, res.data.aatLocData);
                    break;
                case "createAttachment":
                    if (res?.data.duplicate === true) {
                        setRefresh(true)
                        setLoading(false)
                        setDuplicateErrorOpen({ ...duplicateErrorOpen, msg: locale("common:msg.duplicate"), open: true });
                    } else {
                        setLoading(false);
                        setSuccess(true)
                        setRefresh(true)
                        setSnackBarState({ ...snackBarState, open: true, msg: locale("cargoowners:msg.createAttSuccess") });
                        reloadTable();
                    }
                    break;
                case "deleteAttachment":
                    setLoading(false);
                    setSuccess(true)
                    setRefresh(true)
                    setSnackBarState({ ...snackBarState, open: true, msg: locale("cargoowners:msg.deleteAttSuccess") });
                    reloadTable();
                    break;
                default:
                    break;
            }
        }
        // eslint-disable-next-line
    }, [isLoading, isFormSubmission, res, error]);

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

    return <React.Fragment>
        {loading && docs && <MatxLoading />}
        {snackBar}
        {confirm && confirm.aatId && (
            <ConfirmationDialog
                open={open}
                title={locale("listing:coJob.popup.confirmation")}
                text={locale("listing:coJob.msg.deleteConfirm", { action: openDeleteConfirm?.action })}
                onYesClick={() => handleDeleteHandler()}
                onConfirmDialogClose={() => setOpen(false)}
            />
        )}

        <Grid container alignItems="flex-start" spacing={1} className={classes.gridContainer}>

            <Box sx={{ marginBottom: 50 }} m={1}>
                {/** Do not show Add Document button if account is suspended before resumption */}
                <GridActionButton
                    showAddButton={viewType !== 'view' && accnId && accnSuspended === false ? [{
                        label: locale("register:suppDocs.newDoc").toUpperCase(),
                        action: () => popUpAddHandler(),
                        icon: <Add />
                    }] : null}
                />
                <TableContainer /**component={Paper}**/ >
                    <Table className={tableCls.table} aria-label="simple table">
                        <TableHead>
                            <TableRow className={tableCls.row}>
                                <StyledTableCell align="center" width="20%">{locale("register:suppDocs.type")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("register:suppDocs.docName")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("register:suppDocs.mandatory")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("register:suppDocs.dtValid")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("register:suppDocs.uidLupd")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("register:suppDocs.dtLupd")}</StyledTableCell>
                                <StyledTableCell align="center">{locale("register:suppDocs.actions")}</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {docs && docs.map((row, index) => (
                                // add conditional if it is, not mandatory item and file is not exist, then dont display
                                (row.mandatory || row.aatName) &&

                                <TableRow key={index} className={tableCls.row}>
                                    <StyledTableCell align="center">{row.wfTypeDesc}</StyledTableCell>
                                    <StyledTableCell align="center">{row.aatName ? row.aatName : "-"}</StyledTableCell>
                                    <StyledTableCell align="center">{row.mandatory === true ? locale("register:suppDocs.yes") : locale("register:suppDocs.not")}</StyledTableCell>
                                    <StyledTableCell align="center">{row.atDtValidility ? formatDate(row.atDtValidility, true) : "-"}</StyledTableCell>
                                    <StyledTableCell align="center">{row.atUidLupd ? row.atUidLupd : "-"}</StyledTableCell>
                                    <StyledTableCell align="center">{row.atDtLupd ? formatDate(row.atDtLupd, true) : "-"}</StyledTableCell>
                                    <StyledTableCell align="center">
                                        {viewType !== "new" &&
                                            <>
                                                <Grid container alignItems="flex-start" justifyContent="center">
                                                    <span style={{ minWidth: '48px' }}>
                                                        {/** Do not show Upload icon if account is suspended before resumption */}
                                                        {!row.aatId && viewType !== 'view' && accnSuspended === false &&
                                                            <C1LabeledIconButton
                                                                tooltip={locale("buttons:upload")}
                                                                label={locale("buttons:upload")}
                                                                action={() => popUpAddHandler(row.wfTypeId)}>
                                                                <PublishOutlinedIcon color="primary" />
                                                            </C1LabeledIconButton>
                                                        }
                                                    </span>
                                                    <span style={{ minWidth: '48px' }}>
                                                        {row.aatId &&
                                                            <C1LabeledIconButton
                                                                tooltip={locale("buttons:view")}
                                                                label={locale("buttons:view")}
                                                                action={(e) => handleViewFile(e, row.aatId)}>
                                                                <VisibilityOutlinedIcon color="primary" />
                                                            </C1LabeledIconButton>
                                                        }
                                                    </span>
                                                    <span style={{ minWidth: '48px' }}>
                                                        {/** Do not show Delete icon if account is suspended before resumption */}
                                                        {row.aatId && viewType !== 'view' && accnSuspended === false &&
                                                            <C1LabeledIconButton
                                                                tooltip={locale("buttons:delete")}
                                                                label={locale("buttons:delete")}
                                                                action={(e) => handleDeleteConfirm(e, row.aatId)}>
                                                                <DeleteOutlinedIcon color="primary" />
                                                            </C1LabeledIconButton>
                                                        }
                                                    </span>
                                                </Grid>
                                            </>
                                        }
                                    </StyledTableCell>
                                </TableRow>
                            ))}
                            {(!docs || docs.length === 0 || displayEmpty) && (
                                <TableRow key={1} className={tableCls.row}>
                                    <StyledTableCell colSpan="7">
                                        <div style={{ textAlign: "center" }}>{locale("common:genericMsgs.emptyTable")}</div>
                                    </StyledTableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
        </Grid>
        <C1PopUp
            title={locale("listing:attachments.titleAdd")}
            openPopUp={openAddPopUp}
            setOpenPopUp={setOpenAddPopUp}
            actionsEl={
                <C1IconButton tooltip={locale("buttons:submit")} childPosition="right">
                    <NearMeOutlined color="primary" fontSize="large"
                        onClick={() => uploadAttachment()}>
                    </NearMeOutlined>
                </C1IconButton>}>
            <AddAccnAttPopup
                enableDd={enableDd}
                inputData={popUpDetails}
                handleInputChange={handleInputChange}
                handleInputFileChange={handleInputFileChange}
                handleDateChange={handleDateChange}
                locale={locale}
                errors={popUpFieldError}
            />
        </C1PopUp>
        <C1DialogPrompt
            confirmationObj={{
                openConfirmPopUp: duplicateErrorOpen?.open,
                onConfirmationDialogClose: () => setDuplicateErrorOpen({ ...duplicateErrorOpen, open: false }),
                text: duplicateErrorOpen?.msg,
                title: locale("common:msg.error"),
                onYesClick: () => setDuplicateErrorOpen({ ...duplicateErrorOpen, open: false }),
                yesBtnText: "Ok",
            }} />
    </React.Fragment >

};

export default AccountSuppDocs;