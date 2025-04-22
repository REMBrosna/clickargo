import React, { useState, useEffect } from "react";
import { Grid, Paper, Tooltip, Button, Divider, Box } from "@material-ui/core";
import { withStyles } from "@material-ui/core/styles";
import Snackbar from "@material-ui/core/Snackbar";
import { MatxLoading } from "matx";
import C1Alert from "app/c1component/C1Alert";
import C1Container from "app/c1component/C1Container";

import { useStyles, titleTab } from "app/c1component/C1Styles";
import C1SelectField from "app/c1component/C1SelectField";
import C1InputField from "app/c1component/C1InputField";
import MenuItem from "@material-ui/core/MenuItem";
import AddBoxIcon from "@material-ui/icons/AddBox";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import { Icon, IconButton } from "@material-ui/core";
import C1PopUp from "app/c1component/C1PopUp";
import { isEmpty } from "app/c1utils/utility";
import useHttp from "app/c1hooks/http";
import { Roles } from "app/c1utils/const";
import moment from "moment";
import { FiberManualRecord } from '@material-ui/icons';
import useAuth from "app/hooks/useAuth";
import { isShipSide } from "app/c1utils/utility";
import { useTranslation } from "react-i18next";
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


const C1Remark = ({ isEndWorkflow, appId, sectionDb, props, handleValidate, isRetOrRej, getUpdateRead, refresh = 0, lock }) => {
    const { t } = useTranslation(["buttons", "common"]);
    const { user } = useAuth();
    let map = new Set(user.authorities.map((el) => el.authority));

    let isDisabled = map.has(Roles.SHIP_LINE_USER.code) || map.has(Roles.SHIP_AGENT_USER.code) || isEndWorkflow;
    let isShipSideUser = isShipSide(user.coreAccn.TMstAccnType.atypId);

    const [inputData, setInputData] = useState({});
    const [subSectionArray, setSubSectionArray] = useState([]);
    const [rows, setRows] = useState([]);
    const gridClass = useStyles();
    const titleTabClasses = titleTab();

    const [openPopUp, setOpenPopUp] = useState(false);
    const [selectedRemark, setSelectedRemark] = useState({});
    const [errors, setErrors] = useState({});

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isFormLoading, setFormLoading] = useState(false);
    const [readSuccess, setReadSuccess] = useState("");

    const [openRemarkConfirmationDialog, setOpenRemarkConfirmationDialog] = useState(false);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
    });
    const [refreshRmkList, setRefreshRmkList] = useState(0);

    const [operation, setOperation] = useState("");
    const { isLoading, isFormSubmission, res, error, sendRequest } = useHttp();

    const [deletePopup, setDeletePopup] = useState(false);
    const [rmkId, setRmkId] = useState("");

    const handleDelete = () => {
        setFormLoading(true);
        setOperation("Delete");
        sendRequest(`/api/co/pedi/entity/remark/${rmkId}`, "id", "Delete");
    };

    const confirmDelete = (rmkId) => {
        setDeletePopup(true);
        setRmkId(rmkId);
    };

    //fetch remark list;
    useEffect(() => {
        setOperation("List");
        refresh && sendRequest(`/api/portedi/remark/list/${sectionDb.moduleName}/${appId}`);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [sendRequest, sectionDb.moduleName, appId, refreshRmkList, refresh]);

    useEffect(() => {
        setOperation("List");
        sendRequest(`/api/portedi/remark/list/${sectionDb.moduleName}/${appId}`);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [sendRequest, sectionDb.moduleName, appId, refreshRmkList]);

    useEffect(() => {
        let msg = "";
        let severity = "success";

        if ("Add" === operation) {
            msg = t("common:common.msg.createSuccess", { item: t("common:remarks.altTitle") });
        } else if ("Delete" === operation) {
            msg = t("common:common.msg.deleteSuccess", { item: t("common:remarks.altTitle") });
        }

        if (!isLoading && !error && res) {
            setFormLoading(false);
            if ("List" === operation) {
                setRows(res.data);
            } else {
                setRefreshRmkList(refreshRmkList + 1);
            }
        } else if (error) {
            msg = t("common:common.msg.fetchFail", { item: t("common:remarks.altTitle") });
            severity = "error";
        }
        if (("Add" === operation || "Delete" === operation) && !isLoading) {
            setDeletePopup(false);
            setSubmitSuccess(true);
            setSnackBarState((sb) => {
                return { ...sb, open: true, msg: msg, severity: severity };
            });
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, error, res, isFormSubmission]);

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });

        for (let i = 0; i < sectionDb.sections.length; i++) {
            if (e.target.value in sectionDb.sections[i]) {
                setSubSectionArray(sectionDb.sections[i][e.target.value]);
            }
        }
    };
    const handleBtnAdd = (e) => {
        e.preventDefault();

        if (!isEmpty(handleFormValidate())) {
            return;
        }

        setOpenRemarkConfirmationDialog(true);
    };

    const handleConfirmedRemark = () => {
        inputData.rmkApp = sectionDb.moduleName;
        inputData.rmkAppId = appId;

        setOperation("Add");
        sendRequest("/api/co/pedi/entity/remark", "id", "post", { ...inputData });

        setInputData({ rmkSection: "", rmkSubSection: "", rmkMessage: "" });
        setOpenRemarkConfirmationDialog(false);
    }
    //
    let sectionArray = [];
    for (var i = 0, len = sectionDb.sections.length; i < len; i++) {
        for (var key in sectionDb.sections[i]) {
            sectionArray[i] = key;
        }
    }

    const handleOpenDialog = (rmkId) => {
        const idx = rows.findIndex((row) => row.rmkId === rmkId);
        setSelectedRemark(rows[idx]);
        setOpenPopUp(true);
        setOperation("viewed");
        //only marked as read if it's shipside/shipagent
        if (isShipSideUser) {
            getUpdateRead(rmkId)
        }

    };

    const handleFormValidate = () => {
        const errors = {};

        if (!inputData.rmkSection) {
            errors.rmkSection = t("common:validationMsgs.required");
        }
        if (sectionDb.isDisplaySubSection) {
            if (!inputData.rmkSubSection) {
                errors.rmkSubSection = t("common:validationMsgs.required");
            }
        }
        if (!inputData.rmkMessage || /^\s*$/.test(inputData.rmkMessage)) {
            errors.rmkMessage = t("common:validationMsgs.required");
        }

        if (inputData?.rmkMessage?.length > 10000) {
            errors.rmkMessage = t("common:validationMsgs.overLength");
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
                                label={t("common:remarks.fields.section")}
                                name="rmkSection"
                                required
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={inputData.rmkSection || ""}
                                disabled={isDisabled}
                                optionsMenuItemArr={sectionArray.map((item, ind) => (
                                    <MenuItem value={item} key={ind}>
                                        {item}
                                    </MenuItem>
                                ))}
                                error={errors && errors.rmkSection}
                                helperText={errors && errors.rmkSection}
                                lock={lock}
                            />
                        </Grid>
                        {sectionDb.isDisplaySubSection && (
                            <Grid item xs={3}>
                                <C1SelectField
                                    label={t("common:remarks.fields.subSection")}
                                    name="rmkSubSection"
                                    required
                                    onChange={(e) => {
                                        handleInputChange(e);
                                    }}
                                    value={inputData.rmkSubSection || ""}
                                    disabled={isDisabled}
                                    optionsMenuItemArr={subSectionArray.map((item, ind) => (
                                        <MenuItem value={item} key={ind}>
                                            {item}
                                        </MenuItem>
                                    ))}
                                    error={errors && errors.rmkSubSection}
                                    helperText={errors && errors.rmkSubSection}
                                    lock={lock} />
                            </Grid>
                        )}
                        {sectionDb.isDisplaySubSection && <Grid item xs={6}></Grid>}
                        <Grid item xs={6}>
                            <C1InputField
                                multiline
                                label={t("common:remarks.fields.remark")}
                                name="rmkMessage"
                                rows={3}
                                type="input"
                                required
                                disabled={isDisabled}
                                onChange={(e) => {
                                    handleInputChange(e);
                                }}
                                value={inputData.rmkMessage}
                                error={errors && errors.rmkMessage}
                                helperText={errors && errors.rmkMessage}
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
                            {t("common:remarks.subGroup.remarkHistory")}
                        </Grid>
                    </Grid>

                    <Divider className="mb-6" />
                </C1Container>

                <TableContainer component={Paper}>
                    <Table aria-label="simple table" style={{ tableLayout: "auto" }}>
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="left">{t("common:remarks.list.section")}</StyledTableCell>
                                {sectionDb.isDisplaySubSection && (
                                    <StyledTableCell align="left">{t("common:remarks.list.subSection")}</StyledTableCell>
                                )}
                                <StyledTableCell align="left">{t("common:remarks.list.agency")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:remarks.list.userName")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:remarks.list.remark")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:remarks.list.dateTime")}</StyledTableCell>
                                <StyledTableCell align="left">{t("common:remarks.list.action")}</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows &&
                                rows.map((row, ind) => (
                                    <TableRow key={row.rmkId}>
                                        <StyledTableCell align="left">{row.rmkSection}</StyledTableCell>
                                        {sectionDb.isDisplaySubSection && (
                                            <StyledTableCell align="left">{row.rmkSubSection}</StyledTableCell>
                                        )}
                                        <StyledTableCell align="left">{row.TCoreAccn.accnName}</StyledTableCell>
                                        <StyledTableCell align="left">{row.TCoreUsr.usrName}</StyledTableCell>
                                        <StyledTableCell align="left">{row.rmkMessage}</StyledTableCell>
                                        <StyledTableCell>
                                            {moment(row.rmkDtCreate).format("DD/MM/YYYY hh:mm:ss")}
                                        </StyledTableCell>
                                        <StyledTableCell>
                                            <Tooltip title={t("buttons:view")}>
                                                <IconButton
                                                    type="button"
                                                    color="primary"
                                                    onClick={() => handleOpenDialog(row.rmkId)}
                                                >
                                                    <Icon>visibility</Icon>
                                                    {user?.id !== row?.rmkUidCreate && row?.rmkStatus === "A" &&
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
                                            {!isDisabled && user?.id === row?.rmkUidCreate && !isRetOrRej && (
                                                <Tooltip title={t("buttons:delete")}>
                                                    <IconButton
                                                        type="button"
                                                        color="primary"
                                                        onClick={() => confirmDelete(row.rmkId)}
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
                                    <StyledTableCell colSpan="7">
                                        <p style={{ textAlign: "center" }}>{t("common:genericMsgs.emptyTable")}</p>
                                    </StyledTableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>

            <C1PopUp title={t("common:remarks.altTitle")} openPopUp={openPopUp} setOpenPopUp={setOpenPopUp}>
                <C1QueryRemarkDialog remark={selectedRemark} setOpenPopUp={setOpenPopUp} sectionDb={sectionDb}></C1QueryRemarkDialog>
            </C1PopUp>

            <ConfirmationDialog
                open={deletePopup}
                onConfirmDialogClose={() => setDeletePopup(false)}
                text={t("common:confirmMsgs.delete.content", { appnId: rmkId })}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={(e) => handleDelete(e)}
            />

            <ConfirmationDialog
                open={openRemarkConfirmationDialog}
                onConfirmDialogClose={() => setOpenRemarkConfirmationDialog(false)}
                text={t("common:remarks.msgs.confirm")}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={handleConfirmedRemark} />

        </React.Fragment>
    );
};

export default C1Remark;

const C1QueryRemarkDialog = ({ remark, setOpenPopUp, sectionDb }) => {
    const { t } = useTranslation(["common"]);
    return (
        <div>
            <Grid container spacing={1} alignItems="center">
                <Grid item xs={6}>
                    <C1InputField
                        label={t("common:remarks.fields.section")}
                        name="section"
                        required
                        value={remark.rmkSection || ""}
                        disabled={true}
                    />
                </Grid>
                {sectionDb.isDisplaySubSection && (
                    <Grid item xs={6}>
                        <C1InputField
                            label={t("common:remarks.fields.subSection")}
                            name="subSection"
                            required
                            value={remark.rmkSubSection || ""}
                            disabled={true}
                        />
                    </Grid>
                )}
                <Grid item xs={6}>
                    <C1InputField
                        label={t("common:remarks.fields.agency")}
                        name="account"
                        required
                        value={remark.TCoreAccn.accnName || ""}
                        disabled={true}
                    />
                </Grid>
                <Grid item xs={6}>
                    <C1InputField
                        label={t("common:remarks.fields.userName")}
                        name="userName"
                        required
                        value={remark.TCoreUsr.usrName || ""}
                        disabled={true}
                    />
                </Grid>
                <Grid item xs={6}>
                    <C1InputField
                        label={t("common:remarks.fields.dateTime")}
                        name="date time"
                        required
                        value={moment(remark.rmkDtCreate).format("DD/MM/YYYY hh:mm:ss") || ""}
                        disabled={true}
                    />
                </Grid>
                <Grid item xs={12}>
                    <C1InputField
                        multiline
                        label={t("common:remarks.fields.remark")}
                        name="message"
                        rows={4}
                        type="input"
                        disabled={true}
                        value={remark.rmkMessage}
                    />
                </Grid>
                {/*
                <Grid item xs={9}>
                </Grid>
                <Grid item xs={3}>
                    <Button variant="contained"
                        color="secondary"
                        size="large"
                        fullWidth
                        onClick={(e) => setOpenPopUp(false)}>Close</Button>
                </Grid>
                */}
            </Grid>
        </div>
    );
};
