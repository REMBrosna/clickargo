import { Box, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Grid, Paper, Tooltip } from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import AddBoxIcon from '@material-ui/icons/AddBox';
import DeleteIcon from '@material-ui/icons/Delete';
import Visibility from '@material-ui/icons/Visibility';
import pako from "pako";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1Container from "app/c1component/C1Container";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import useHttp from "app/c1hooks/http";
import { ApplicationType, MST_DOC_CATEGORY } from "app/c1utils/const";
import { titleTab, useStyles } from "app/c1utils/styles";
import { isEmpty, previewPDF, getValue } from "app/c1utils/utility";
import { isEditable } from "app/c1utils/utility";
import { MatxLoading } from "matx";
import { GET_ATT_TYPE_BY_ID } from "app/c1utils/const";

import C1DateField from "../c1component/C1DateField";
import ConfirmationDialog from "../../matx/components/ConfirmationDialog";

const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: '#3C77D0',
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);

const useTableStyle = makeStyles({
    table: {
        minWidth: 450,
    },
    column: {
        width: 50,
    },
});

let URL = "/api/portedi/attach";

const MAX_FILE_SIZE = 10; //10M
const ALLOWED_FILE_EXTS = ['pdf', 'csv', 'xlsx', 'xls', 'doc', 'docx', 'jpeg', 'jpg', 'png', 'ppt'];
const isCompress = false; // don't compress,

/** Application specific implementation of supporting documents. */
const SupportingDocs = ({
    isRequireExpireDate,
    isRefNoRequired,
    isExpDtRequired,
    isEndWorkflow,
    viewType,
    isCatSelect,
    appType,
    isDisabled,
    appId
}) => {

    const title = titleTab();
    const tableCls = useTableStyle();
    const fieldClass = useStyles();
    const { t } = useTranslation(["buttons", "common"]);

    const [rows, setRows] = useState([]);
    const uploadFileExtAttr = { "docType": "", "refNo": "", "expireDate": null };
    const defaultSupport = { fileType: "SUP", catCode: "", name: "", data: null, appType, appId, extAttr: uploadFileExtAttr };
    const [uploadFile, setUploadFile] = useState(defaultSupport);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isConfirmOpen, setConfirmOpen] = useState(false);
    const [docsAttId, setDocsAttId] = useState({});

    const [errors, setErrors] = useState({});

    const [isExpiryRequired, setExpiryRequired] = useState(isExpDtRequired || isRequireExpireDate);
    const [isRefRequired, setRefNoRequired] = useState(isRefNoRequired);
    const [openDocsNotAvailWarn, setOpenDocsNoAvailWarn] = useState(false);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });
    const [refreshList, setRefreshList] = useState(0);
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();


    let selectedFileType = "application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint,text/plain";
    selectedFileType += ", application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    selectedFileType += ", application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    selectedFileType += ", application/pdf, image/*";

    const handleInputChange = (e) => {
        setUploadFile({ ...uploadFile, [e.target.name]: e.target.value, "extAttr": { ...uploadFile.extAttr, [e.target.name]: e.target.value } });
    };

    const handleAutoCompleteInput = (e, name, value) => {

        if (name !== 'catCode') {
            if (value?.value !== undefined) {
                sendRequest(`${GET_ATT_TYPE_BY_ID}${value?.value}`, "getAppTypeDetails", "get");
            } else {
                //reset
                setExpiryRequired(isExpDtRequired || isRequireExpireDate);
                setRefNoRequired(isRefNoRequired)
            }
        }

        setUploadFile({ ...uploadFile, [name]: value?.value, "extAttr": { ...uploadFile.extAttr, [name]: value?.value } });
    }

    const handleDateChange = (name, e) => {
        if (isRequireExpireDate) {
            setUploadFile({ ...uploadFile, "extAttr": { ...uploadFile.extAttr, [name]: e } });
        }
    };

    const onLocalFileChangeHandler = (e) => {
        e.preventDefault();

        let file = e.target.files[0];
        if (!file) {
            return;
        }
        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(e.target.files[0]);
        fileReader.onload = e => {
            let uint8Array = new Uint8Array(e.target.result);

            if (!validateFile(uint8Array, file.name)) {
                return;
            }
            if (isCompress) {
                uint8Array = pako.deflate(uint8Array);
            }

            let buff = new Buffer.from(uint8Array);
            let base64data = buff.toString('base64');

            setUploadFile({ ...uploadFile, "compress": isCompress, "fileName": file.name, "data": base64data });
        };

    };

    const validateFile = (uint8Array, fileName) => {

        let ext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length) || fileName;
        if (!ALLOWED_FILE_EXTS.includes(ext.toLowerCase())) {
            let msg = t("common:supportingDocs.msg.extsNotAllowed");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        if (!uint8Array || uint8Array.length === 0) {
            let msg = t("common:supportingDocs.msg.fileTooSmall");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }
        if (uint8Array.length > MAX_FILE_SIZE * 1024 * 1024) {
            let msg = t(`common:supportingDocs.msg.fileSizeTooBig`, { size: MAX_FILE_SIZE });
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }
        //
        let filterByFileName = rows.filter(file => file.fileName === fileName);
        if (fileName.length > 128) {
            let msg = t("common:supportingDocs.msg.fileNameLen");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }
        if (filterByFileName.length > 0) {
            let msg = t("common:supportingDocs.msg.fileDuplicate");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }
        return true;
    }


    const handleBtnAddFile = (e) => {
        e.preventDefault();
        if (!isEmpty(handleFormValidate())) {
            return;
        }

        sendRequest(`${URL}`, "Upload", "Post", uploadFile);
        setUploadFile(defaultSupport);
        document.getElementById('uploadFile').value = '';
    }

    const handleDeleteFile = (e, value) => {
        e.preventDefault();
        setDocsAttId(value);
        setConfirmOpen(true);
    }

    const handleViewFile = (e, attId) => {
        sendRequest(`${URL}/${attId}`, "Download");
    };

    const viewFile = (fileName, data) => {
        if (!data || data === undefined) {
            setOpenDocsNoAvailWarn(true);
        } else {
            previewPDF(fileName, data);
        }

    };

    /////////////////
    const handleFormValidate = () => {
        const errors = {};

        if ([ApplicationType.AD.code, ApplicationType.DD.code, ApplicationType.PAN.code].includes(uploadFile.appType) && !uploadFile.catCode) {
            errors.catCode = t("common:common.dataTable.required");
        }

        if (!uploadFile.extAttr.docType) {
            errors.docType = t("common:common.dataTable.required");
        }
        if (!uploadFile.extAttr.refNo && isRefRequired) {
            errors.refNo = t("common:common.dataTable.required");
        }

        if (uploadFile?.extAttr?.refNo.length > 256) {
            errors.refNo = t("common:common.dataTable.docRefNoExceed");
        }

        if (isRequireExpireDate && !uploadFile.extAttr.expireDate && isExpiryRequired) {
            errors.expireDate = t("common:common.dataTable.required");
        }
        if (!uploadFile.data) {
            errors.data = t("common:common.dataTable.required");
        }
        setErrors(errors);

        return errors;
    }

    useEffect(() => {
        if (viewType !== 'new' && appId !== undefined) {
            sendRequest(`${URL}/${appType}/${appId}/SUP`, "List");
        }
    }, [sendRequest, appType, appId, refreshList]);

    useEffect(() => {
        let msg = "";
        let severity = "success";

        if ("Upload" === urlId) {
            msg = t("common:supportingDocs.msg.uploadSuccess");
        } else if ("Delete" === urlId) {
            msg = t("common:supportingDocs.msg.deleteSuccess");
        }

        if (!isLoading && !error && res) {
            if ("List" === urlId) {
                setRows(res.data);

            } else if ("Download" === urlId) {
                viewFile(res.data.fileName, res.data.data);
            } else if ("getAppTypeDetails" === urlId) {
                setExpiryRequired(res?.data?.mattExpiry === undefined ? false : res.data.mattExpiry === 'Y' ? true : false);
                setRefNoRequired(res?.data?.mattRefNo === undefined ? false : res.data.mattRefNo === 'Y' ? true : false);
            } else {
                setRefreshList(refreshList + 1);
            }
        } else if (error) {
            msg = t("common:uploadSignature.msg.fetchError");
            severity = "error";
        }

        if ((("Upload" === urlId) || ("Delete" === urlId)) && !isLoading) {
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: severity } });
            //reset
            setExpiryRequired(isExpDtRequired || isRequireExpireDate);
            setRefNoRequired(isRefNoRequired)
        }
    }, [isLoading, error, res, urlId, isFormSubmission]);

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const handleDocsNotAvailWarnClose = () => {
        setOpenDocsNoAvailWarn(false);
    }

    // convert expire date to date format
    const stringToDate = (dateString) => {
        if (null !== dateString && dateString !== undefined && dateString !== '') {
            const date = dateString.split('-');
            const day = date[2].split('T');
            return day[0].concat("-").concat(date[1]).concat("-").concat(date[0]);
        }
    };

    let snackBar = null;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleClose}
            autoHideDuration={3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleClose} severity={snackBarState.severity}>
                {snackBarState.msg}
            </C1Alert>
        </Snackbar>;
    }

    return (
        <React.Fragment>
            {isLoading && <MatxLoading />}
            {snackBar}
            <ConfirmationDialog
                open={isConfirmOpen}
                onConfirmDialogClose={() => setConfirmOpen(false)}
                text={t("common:confirmMsgs.delete.content", { appnId: docsAttId.docTitle })}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={(e) => {
                    sendRequest(`${URL}/${docsAttId.docId}`, "Delete", "Delete")
                    setConfirmOpen(false)
                }}
            />
            <C1Container rendered={!isDisabled && !isEndWorkflow}>
                <Grid container alignItems="flex-start" spacing={3} className={fieldClass.gridContainer}>
                    <Grid item xs={4}>
                        <Grid item className={fieldClass.gridContainer} xs={12}>
                            <Box className={title.root}>{t("common:supportingDocs.documentDetails")}</Box>
                        </Grid>
                        {isCatSelect && <C1SelectAutoCompleteField
                            label={t("common:supportingDocs.fields.category")}
                            name="catCode"
                            required
                            onChange={handleAutoCompleteInput}
                            value={getValue(uploadFile?.catCode)}
                            disabled={isDisabled}
                            isServer={true}
                            isShowCode={true}
                            options={{
                                url: viewType !== 'new' && MST_DOC_CATEGORY,
                                key: "pediMstDocCategory",
                                id: "mdcCode",
                                desc: "mdcDesc",
                                isCache: true,
                            }}
                            error={(errors && errors.catCode ? true : false)}
                            helperText={(errors && errors.catCode)}
                        />}

                        <C1SelectAutoCompleteField
                            label={t("common:supportingDocs.fields.docType")}
                            name="docType"
                            required
                            onChange={handleAutoCompleteInput}
                            value={getValue(uploadFile?.extAttr?.docType)}
                            disabled={isDisabled}
                            isServer={true}
                            options={{
                                url: viewType !== 'new' ? `/api/portedi/suppAsocDocs/${appType}/${appId}` : null,
                                key: "attchment",
                                id: "mstAttType.mattId",
                                desc: "mstAttType.mattName",
                                isCache: false,
                            }}
                            error={(errors && errors.docType ? true : false)}
                            helperText={(errors && errors.docType)}
                        />

                        <C1InputField
                            required={isRefRequired}
                            label={t("common:supportingDocs.fields.docRef")}
                            name="refNo"
                            type="input"
                            disabled={isDisabled}
                            onChange={handleInputChange}
                            value={uploadFile.extAttr.refNo}
                            error={(errors && errors.refNo ? true : false)}
                            helperText={(errors && errors.refNo)}
                            inputProps={{ maxLength: 256 }}
                        />
                    </Grid>
                    <Grid item xs={4}>
                        <Grid item className={fieldClass.gridContainer} xs={12}>
                            <Box className={title.root}>{t("common:supportingDocs.upload")}</Box>
                        </Grid>
                        {
                            isRequireExpireDate ? <div><C1DateField
                                label={t("common:supportingDocs.fields.expiryDate")}
                                name="expireDate"
                                disabled={isDisabled}
                                type="date"
                                disablePast
                                required={isExpiryRequired}
                                onChange={handleDateChange}
                                value={uploadFile.extAttr.expireDate}
                                error={(errors && errors.expireDate ? true : false)}
                                helperText={(errors && errors.expireDate)} />

                                <C1InputField
                                    required
                                    disabled={isDisabled}
                                    label={t("common:supportingDocs.fields.uploadFile")}
                                    name="data"
                                    inputProps={{ id: 'uploadFile', "accept": selectedFileType }}
                                    onChange={onLocalFileChangeHandler}
                                    type="file"
                                    error={(errors && errors.data ? true : false)}
                                    helperText={(errors && errors.data)} /></div> : <C1InputField
                                required
                                disabled={isDisabled}
                                label={t("common:supportingDocs.fields.uploadFile")}
                                name="data"
                                inputProps={{ id: 'uploadFile', "accept": selectedFileType }}
                                onChange={onLocalFileChangeHandler}
                                type="file"
                                error={(errors && errors.data ? true : false)}
                                helperText={((errors && errors.data) || t("common:supportingDocs.msg.allowedExts"))} />
                        }
                    </Grid>

                    <Grid item xs={4}>
                        <Grid item className={fieldClass.gridContainer} xs={12}>
                            <Box mt={2}><br /></Box>
                        </Grid>
                        <Box mt={2}>
                            <Tooltip title={t("buttons:add")} aria-label="add">
                                <Button type="submit" disabled={isDisabled} color="primary" variant="contained"
                                    size="large" onClick={handleBtnAddFile}>
                                    <AddBoxIcon viewBox="1 -1 30 30"></AddBoxIcon>{t("common:supportingDocs.add")}
                                </Button>
                            </Tooltip>
                        </Box>
                    </Grid>
                </Grid>

            </C1Container>
            <Box m={1}>
                <TableContainer component={Paper}>
                    <Table className={tableCls.table} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.no")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.docType")}</StyledTableCell>
                                {isCatSelect && <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.category")}</StyledTableCell>}
                                <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.mandatory")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.docName")}</StyledTableCell>
                                <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.docRefNo")}</StyledTableCell>

                                {isRequireExpireDate && <StyledTableCell align="center">{t("common:supportingDocs.tableHdrs.expiredDate")}</StyledTableCell>}
                                <StyledTableCell align="center"></StyledTableCell>

                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.map((el, idx) => {
                                let row = el.row;
                                let parentElement = <TableRow key={row.id}>
                                    <TableCell align="center">{row.seq}</TableCell>
                                    <TableCell align="center">{row.desc ? row.desc : row.extAttr ? row.extAttr.docType : ""}</TableCell>
                                    {isCatSelect && <TableCell align="center">{row.catCode ? row.catCode : ""}</TableCell>}
                                    <TableCell align="center">{row.mandatory}</TableCell>
                                    <TableCell align="center">{row.fileName}</TableCell>
                                    <TableCell align="center">{row.extAttr ? row.extAttr.refNo : ""}</TableCell>
                                    {isRequireExpireDate && <TableCell align="center">{row.extAttr ? stringToDate(row.extAttr.expireDate) : ""}</TableCell>}
                                    <TableCell align="center">
                                        {row.fileName &&
                                            <Tooltip title="View">
                                                <IconButton aria-label="View" type="button"
                                                    color="primary" onClick={(e) => handleViewFile(e, row.id)}>
                                                    <Visibility />
                                                </IconButton>
                                            </Tooltip>}
                                        {row.fileName && !isDisabled && <IconButton aria-label="Delete" type="button"
                                            color="primary" onClick={(e) => handleDeleteFile(e, {docId: row.id, docTitle: row?.fileName})}>
                                            <DeleteIcon />
                                        </IconButton>}

                                    </TableCell>
                                </TableRow>

                                let subElement = [];
                                if (el.subRow && el.subRow.length >= 1) {
                                    el.subRow.map((sr, i) => {
                                        subElement.push(
                                            (<TableRow key={sr.id}>
                                                <TableCell colSpan={2}></TableCell>
                                                <TableCell align="center">{sr.catCode ? sr.catCode : ""}</TableCell>
                                                {isCatSelect && <TableCell align="center">{sr.mandatory}</TableCell>}
                                                <TableCell align="center">{sr.fileName}</TableCell>
                                                <TableCell align="center">{sr.extAttr ? sr.extAttr.refNo : ""}</TableCell>
                                                {isRequireExpireDate && <TableCell align="center">{sr.extAttr ? stringToDate(sr.extAttr.expireDate) : ""}</TableCell>}
                                                <TableCell align="center">
                                                    <Tooltip title="View">
                                                        <IconButton aria-label="View" type="button"
                                                            color="primary" onClick={(e) => handleViewFile(e, sr.id)}>
                                                            <Visibility />
                                                        </IconButton>
                                                    </Tooltip>
                                                    {!isDisabled && <IconButton aria-label="Delete" type="button"
                                                        color="primary" onClick={(e) =>  handleDeleteFile(e, {docId: sr.id, docTitle: sr?.fileName})}>
                                                        <DeleteIcon />
                                                    </IconButton>}

                                                </TableCell>
                                            </TableRow>));
                                    });
                                }


                                return <React.Fragment key={idx}>
                                    {parentElement}
                                    {subElement.map(i => i)}
                                </React.Fragment>;
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>

            <Dialog
                open={openDocsNotAvailWarn}
                onClose={handleDocsNotAvailWarnClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description">
                <DialogTitle id="alert-dialog-title">{t("common:validationAlerts.title")}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description" color="inherit">
                        File cannot be retrieved from server. Please try again and contact ACLEDA support if problem persist.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleDocsNotAvailWarnClose} color="primary" autoFocus>
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
        </React.Fragment >
    );
};

export default SupportingDocs;