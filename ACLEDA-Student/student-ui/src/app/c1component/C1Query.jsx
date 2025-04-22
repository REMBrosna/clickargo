import { Box, Button, Divider, Grid, Paper, TextField, Tooltip } from "@material-ui/core";
import { Icon, IconButton } from "@material-ui/core";
import MenuItem from "@material-ui/core/MenuItem";
import Snackbar from "@material-ui/core/Snackbar";
import { withStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import { FiberManualRecord } from "@material-ui/icons";
import AddBoxIcon from "@material-ui/icons/AddBox";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1Container from "app/c1component/C1Container";
import C1InputField from "app/c1component/C1InputField";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import { titleTab, useStyles } from "app/c1component/C1Styles";
import useHttp from "app/c1hooks/http";
import { Roles, Status } from "app/c1utils/const";
import { isEmpty } from "app/c1utils/utility";
import { isShipSide } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";

import ConfirmationDialog from "../../matx/components/ConfirmationDialog";


const StyledTableCell = withStyles((theme) => ({
    head: {
        backgroundColor: "#3C77D0",
        color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);


const C1Query = ({ isEndWorkflow, appId, appStatus, sectionDb, props, handleValidate, getRefreshQuery, lock }) => {

    const { t } = useTranslation(["buttons", "common"]);
    const { user } = useAuth();
    let map = new Set(user.authorities.map((el) => el.authority));
    let isDisabled = ((map.has(Roles.SHIP_LINE_USER.code) || map.has(Roles.SHIP_AGENT_USER.code))
        && (appStatus && appStatus !== Status.RET.code && appStatus !== Status.AMN.code)) ||
        isEndWorkflow;
    let isAdminSupport = map.has(Roles.SYSTEM_ADMIN.code) || map.has(Roles.SYSTEM_SUPPORT_OFFICER.code);
    let isAdminSupportOnly = (!map.has("APPROVER_OFFICER") && !map.has("VERIFIER_OFFICER")) && map.has("SYSTEM_SUPPORT_OFFICER")

    const qryViewer = user.coreAccn.TMstAccnType.atypId;

    const [inputData, setInputData] = useState({});
    const [subSectionArray, setSubSectionArray] = useState([]);
    const [rows, setRows] = useState([]);

    const [openPopUp, setOpenPopUp] = useState(false);
    const [selectedRow, setSelectedRow] = useState("");
    const [replyMsg, setReplyMsg] = useState("");
    const [errors, setErrors] = useState({});

    const gridClass = useStyles();
    const titleTabClasses = titleTab();

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isFormLoading, setFormLoading] = useState(false);

    const [refreshQuery, setRefreshQuery] = useState(0);

    const [openQueryConfirmationDialog, setOpenQueryConfirmationDialog] = useState(false);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
    });

    const [operation, setOperation] = useState("");
    const [refreshList, setRefreshList] = useState(0);
    const { isLoading, isFormSubmission, res, error, sendRequest } = useHttp();
    const [deletePopup, setDeletePopup] = useState(false);
    const [qurId, setQurId] = useState("");

    const [isReplyDisabled, setReplyDisabled] = useState(false);

    const confirmDelete = (id) => {
        setDeletePopup(true);
        setQurId(id);
    };

    const handleDelete = () => {
        setOperation("Delete");
        sendRequest(`/api/co/pedi/entity/query/${qurId}`, "id", "Delete");
    };

    //fetch  list;
    useEffect(() => {
        setOperation("List");
        sendRequest(`/api/portedi/query/list/${sectionDb.moduleName}/${appId}`);
    }, [sendRequest, sectionDb.moduleName, appId, refreshList]);

    useEffect(() => {
        let msg = "";
        let severity = "success";

        if ("Add" === operation) {
            msg = t("common:common.msg.createSuccess", { item: t("common:queries.altTitle") });
        } else if ("Delete" === operation) {
            msg = t("common:common.msg.deleteSuccess", { item: t("common:queries.altTitle") });
        } else if ("Reply" === operation) {
            msg = t("common:queries.msgs.replySuccess");
        }

        if (!isLoading && !error && res) {
            setFormLoading(false);
            if ("List" === operation) {
                setRows(res.data);
                console.log("res ", res.data);
            } else {
                setRefreshList(refreshList + 1);
            }
        } else if (error) {
            msg = t("common:common.msg.fetchFail", { item: t("common:queries.altTitle") });
            severity = "error";
        }

        if (("Add" === operation || "Delete" === operation || "Reply" === operation) && !isLoading) {
            setDeletePopup(false);
            setSubmitSuccess(true);
            setOpenPopUp(false);
            setReplyMsg("");
            setSnackBarState((sb) => {
                return { ...sb, open: true, msg: msg, severity: severity };
            });
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, error, res, isFormSubmission]);
    //////////////////////////

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });

        for (let i = 0; i < sectionDb.sections.length; i++) {
            if (e.target.value in sectionDb.sections[i]) {
                setSubSectionArray(sectionDb.sections[i][e.target.value]);
            }
        }
    };
    const handleInputChangeReplyMsg = (e) => {
        setReplyMsg(e.target.value);
    };
    const handleBtnAdd = (e) => {
        e.preventDefault();

        if (!isEmpty(handleFormValidate())) {
            return;
        }

        setOpenQueryConfirmationDialog(true);
    };

    const handleConfirmedQuery = () => {
        inputData.qurApp = sectionDb.moduleName;
        inputData.qurAppId = appId;

        setOperation("Add");
        sendRequest("/api/co/pedi/entity/query", "id", "post", { ...inputData });

        setInputData({ qurSection: "" });
        setInputData({ qurSubSection: "" });
        setInputData({ qurMessage: "" });
        setOpenQueryConfirmationDialog(false);
    }

    const handleBtnReply = (e) => {
        e.preventDefault();
        setFormLoading(true);
        setOperation("Reply");
        selectedRow.qurRpyMessage = replyMsg;
        const errors = {};
        if (!replyMsg) {//Added to handle throw new Exception("Reply message is null.") from Back End
            errors.replyMsg = t("common:validationMsgs.required");
            setErrors(errors);
            return errors;
        }
        sendRequest(`/api/portedi/query/reply/${selectedRow.qurId}`, "viewed", "put", { ...selectedRow });
        getRefreshQuery(res);
    };

    const handlePopUpReplyWindow = (qurId, qryOwner) => {
        if (appId.startsWith('EP')) {
            setReplyDisabled(isAdminSupportOnly)
        } else {
            setReplyDisabled(qryOwner === qryViewer || isAdminSupport);
        }
        setSelectedRowWrap(qurId);
        setOpenPopUp(true);
    };

    const setSelectedRowWrap = (qurId) => {
        const rowFilter = rows.filter((qry) => qry.qurId === qurId);

        setSelectedRow(rowFilter[0]);
    };

    //
    let sectionArray = [];
    for (var i = 0, len = sectionDb.sections.length; i < len; i++) {
        for (var key in sectionDb.sections[i]) {
            sectionArray[i] = key;
        }
    }

    const handleFormValidate = () => {
        const errors = {};

        if (!inputData.qurSection) {
            errors.qurSection = t("common:validationMsgs.required");
        }
        if (sectionDb.isDisplaySubSection) {
            if (!inputData.qurSubSection) {
                errors.qurSubSection = t("common:validationMsgs.required");
            }
        }

        if (!inputData.qurMessage || /^\s*$/.test(inputData.qurMessage)) {
            errors.qurMessage = t("common:validationMsgs.required");
        }

        if (inputData?.qurMessage?.length > 10000) {
            errors.qurMessage = t("common:validationMsgs.overLength");
        }

        setErrors(errors);
        return errors;
    };

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleClose}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert onClose={handleClose} severity={snackBarState.severity}>
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }

    return (
        <React.Fragment>
            {isLoading && <MatxLoading />}
            {snackBar}
            <Box className="p-3">
                <C1Container rendered={!isDisabled && !isEndWorkflow}>
                    <Grid container spacing={1} direction="row" justify="flex-start" alignItems="flex-start">
                        <Grid item xs={3}>
                            <C1SelectField
                                label={t("common:queries.fields.section")}
                                name="qurSection"
                                required
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={inputData.qurSection || ""}
                                disabled={isDisabled}
                                optionsMenuItemArr={sectionArray.map((item, ind) => (
                                    <MenuItem value={item} key={ind}>
                                        {item}
                                    </MenuItem>
                                ))}
                                error={errors && errors.qurSection}
                                helperText={errors && errors.qurSection}
                                lock={lock} />
                        </Grid>
                        {sectionDb.isDisplaySubSection && (
                            <Grid item xs={3}>
                                <C1SelectField
                                    label={t("common:queries.fields.subSection")}
                                    name="qurSubSection"
                                    required
                                    onChange={(e) => {
                                        handleInputChange(e);
                                    }}
                                    value={inputData.qurSubSection || ""}
                                    disabled={isDisabled}
                                    optionsMenuItemArr={subSectionArray.map((item, ind) => (
                                        <MenuItem value={item} key={ind}>
                                            {item}
                                        </MenuItem>
                                    ))}
                                    error={errors && errors.qurSubSection}
                                    helperText={errors && errors.qurSubSection}
                                    lock={lock}
                                />
                            </Grid>
                        )}
                        {sectionDb.isDisplaySubSection && <Grid item xs={6}></Grid>}
                        <Grid item xs={6}>
                            <C1InputField
                                multiline
                                label={t("common:queries.fields.query")}
                                name="qurMessage"
                                rows={"3"}
                                required
                                type="input"
                                disabled={isDisabled}
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={inputData.qurMessage}
                                error={errors && errors.qurMessage}
                                helperText={errors && errors.qurMessage}
                                lock={lock}
                            />
                        </Grid>
                        <Grid item xs={2}>
                            <Box m={2}>
                                <Tooltip title={t("buttons:add")} aria-label="add">
                                    <Button
                                        type="button"
                                        disabled={lock ? true : isDisabled}
                                        color="primary"
                                        variant="contained"
                                        size="large"
                                        m={2}
                                        onClick={handleBtnAdd}
                                    >
                                        <AddBoxIcon viewBox="1 -1 30 30"></AddBoxIcon>
                                        {t("buttons:add")}
                                    </Button>
                                </Tooltip>
                            </Box>
                        </Grid>
                    </Grid>

                    <Grid container spacing={3} alignItems="center" className={gridClass.gridContainer}>
                        <Grid item xs={12} className={titleTabClasses.root}>
                            {t("common:queries.subGroup.queryHistory")}
                        </Grid>
                    </Grid>
                    <Divider className="mb-6" />
                </C1Container>

                <TableContainer component={Paper}>
                    <Table aria-label="simple table" style={{ tableLayout: "auto" }}>
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="left">{t("common:queries.list.section")}</StyledTableCell>
                                {sectionDb.isDisplaySubSection && (
                                    <StyledTableCell align="left">
                                        {t("common:queries.list.subSection")}
                                    </StyledTableCell>
                                )}
                                <StyledTableCell align="left">{t("common:queries.list.agency")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:queries.list.userName")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:queries.list.query")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:queries.list.queryDate")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:queries.list.replyMsg")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:queries.list.responseDate")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:queries.list.action")}</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows &&
                                rows.map((row, ind) => (
                                    <TableRow key={row.qurId}>
                                        <StyledTableCell align="left">{row.qurSection}</StyledTableCell>
                                        {sectionDb.isDisplaySubSection && (
                                            <StyledTableCell align="left">{row.qurSubSection}</StyledTableCell>
                                        )}
                                        <StyledTableCell align="left">
                                            {row.TCoreAccnByQurAcctId.accnName}
                                        </StyledTableCell>
                                        <StyledTableCell align="left">{row.TCoreUsrByQurUsrId.usrName}</StyledTableCell>
                                        <StyledTableCell align="left">{row.qurMessage}</StyledTableCell>
                                        <StyledTableCell>
                                            {moment(row.qurDtIssue).format("DD/MM/YYYY hh:mm:ss")}
                                        </StyledTableCell>
                                        <StyledTableCell>{row.qurRpyMessage ? row.qurRpyMessage : ""}</StyledTableCell>
                                        <StyledTableCell>
                                            {row.qurDtReply ? moment(row.qurDtReply).format("DD/MM/YYYY hh:mm:ss") : ""}
                                        </StyledTableCell>
                                        <StyledTableCell>
                                            <Tooltip title={t("buttons:view")}>
                                                <IconButton
                                                    type="button"
                                                    color="primary"
                                                    onClick={() => handlePopUpReplyWindow(row.qurId, row?.TCoreAccnByQurAcctId?.TMstAccnType?.atypId)}
                                                >
                                                    <Icon>visibility</Icon>
                                                    {user?.id !== row?.qurUidCreate && row?.qurRpyMessage === null &&
                                                        <FiberManualRecord style={{
                                                            color: "#ff3d57",
                                                            fontSize: "8px",
                                                            position: "absolute",
                                                            top: "15px",
                                                            left: "32px"
                                                        }}
                                                        />
                                                    }
                                                </IconButton>
                                            </Tooltip>
                                            {!isDisabled && !row.qurDtReply && user?.id === row?.qurUidCreate && (
                                                <Tooltip title={t("buttons:delete")}>
                                                    <IconButton
                                                        type="button"
                                                        color="primary"
                                                        onClick={() => confirmDelete(row.qurId)}
                                                    >
                                                        <Icon>delete</Icon>
                                                    </IconButton>
                                                </Tooltip>
                                            )}
                                        </StyledTableCell>
                                    </TableRow>
                                ))}
                            {(!rows || rows.length === 0) && (
                                <TableRow key={1}>
                                    <StyledTableCell colSpan="8">
                                        <p style={{ textAlign: "center" }}>{t("common:genericMsgs.emptyTable")}</p>
                                    </StyledTableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>

                <ConfirmationDialog
                    open={deletePopup}
                    onConfirmDialogClose={() => setDeletePopup(false)}
                    text={t("common:confirmMsgs.delete.content", { appnId: qurId })}
                    title={t("common:confirmMsgs.confirm.title")}
                    onYesClick={(e) => handleDelete(e)}
                />

                <C1PopUp title={t("common:queries.replyTitle")} openPopUp={openPopUp} setOpenPopUp={setOpenPopUp}>
                    <Grid container spacing={1} alignItems="center">
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("common:queries.fields.section")}
                                name="qurSection"
                                required
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={selectedRow.qurSection || ""}
                                disabled={true}
                            />
                        </Grid>
                        {sectionDb.isDisplaySubSection && (
                            <Grid item xs={6}>
                                <C1InputField
                                    label={t("common:queries.fields.subSection")}
                                    name="qurSubSection"
                                    required
                                    onChange={(e) => {
                                        handleInputChange(e);
                                    }}
                                    value={selectedRow.qurSubSection || ""}
                                    disabled={true}
                                />
                            </Grid>
                        )}
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("common:queries.fields.agency")}
                                name="account"
                                required
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={
                                    selectedRow.TCoreAccnByQurAcctId ? selectedRow.TCoreAccnByQurAcctId.accnName : ""
                                }
                                disabled={true}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("common:queries.fields.userName")}
                                name="userName"
                                required
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={selectedRow.TCoreUsrByQurUsrId ? selectedRow.TCoreUsrByQurUsrId.usrName : ""}
                                disabled={true}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("common:queries.fields.dateTime")}
                                name="date time"
                                required
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={moment(selectedRow.qurDtIssue).format("DD/MM/YYYY hh:mm:ss") || ""}
                                disabled={true}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <C1InputField
                                label={t("common:queries.fields.replyDateTime")}
                                name="date time"
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={
                                    selectedRow.qurDtReply
                                        ? moment(selectedRow.qurDtReply).format("DD/MM/YYYY hh:mm:ss")
                                        : ""
                                }
                                disabled={true}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <C1InputField
                                multiline
                                label={t("common:queries.fields.query")}
                                name="qurMessage"
                                rows={"3"}
                                type="input"
                                disabled={true}
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={selectedRow.qurMessage}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            {selectedRow.qurRpyMessage && (
                                <C1InputField
                                    multiline
                                    label={t("common:queries.fields.replyQuery")}
                                    name="message"
                                    rows={3}
                                    type="input"
                                    disabled={true}
                                    onChange={(e) => {
                                        handleInputChange(e);
                                    }}
                                    value={selectedRow.qurRpyMessage}
                                />
                            )}

                            {!selectedRow.qurRpyMessage && (
                                <TextField
                                    label={t("common:queries.fields.replyQuery")}
                                    name="message"
                                    disabled={isReplyDisabled}
                                    multiline
                                    fullWidth
                                    type="input"
                                    variant="outlined"
                                    onChange={(e) => {
                                        handleInputChangeReplyMsg(e);
                                    }}
                                    value={replyMsg}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    rows={4}
                                    error={errors && errors.replyMsg}
                                    helperText={errors && errors.replyMsg}
                                />
                            )}
                        </Grid>
                        <Grid item xs={9}></Grid>
                        <Grid item xs={3}>
                            {!selectedRow.qurRpyMessage && (
                                <Button
                                    variant="contained"
                                    color="primary"
                                    size="large"
                                    disabled={isReplyDisabled}
                                    fullWidth
                                    onClick={(e) => handleBtnReply(e)}
                                >
                                    {t("common:queries.buttons.reply")}
                                </Button>
                            )}
                            {false && (
                                <Button
                                    variant="contained"
                                    color="secondary"
                                    size="large"
                                    fullWidth
                                    onClick={(e) => setOpenPopUp(false)}
                                >
                                    {t("common:queries.buttons.close")}
                                </Button>
                            )}
                        </Grid>
                    </Grid>
                </C1PopUp>
            </Box>
            <ConfirmationDialog
                open={openQueryConfirmationDialog}
                onConfirmDialogClose={() => setOpenQueryConfirmationDialog(false)}
                text={t("common:queries.msgs.confirm")}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={handleConfirmedQuery} />

        </React.Fragment>
    );
};

export default C1Query;
