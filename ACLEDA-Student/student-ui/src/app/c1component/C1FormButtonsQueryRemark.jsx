import React, { useState, useEffect } from "react";

import { useTranslation } from "react-i18next";
import C1QueryRemarkDialog from "app/c1component/C1QueryRemarkDialog";
import sessionStorageService, { SUBSECTION } from "app/services/sessionStorageService";

import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
import CommentOutlinedIcon from "@material-ui/icons/CommentOutlined";
import useHttp from "app/c1hooks/http";
import Snackbar from "@material-ui/core/Snackbar";
import C1Alert from "app/c1component/C1Alert";
import { Status } from "app/c1utils/const";
import useAuth from "../hooks/useAuth";
import { isShipSide } from "app/c1utils/utility";

const C1FormButtonsQueryRemark = (props) => {
    const [displayBtn, setDisplayBtn] = useState(false);
    const { t } = useTranslation(["common"]);

    const [openQueryRemarkDialog, setOpenQueryRemarkDialog] = useState(false);
    const [queryRemarkTitle, setQueryRemarkTitle] = useState("title");
    const { user } = useAuth();

    const handleCloseQueryRemarkDialog = () => {
        setOpenQueryRemarkDialog(false);
    };

    const handleQuery = () => {
        setQueryRemarkTitle(t("common:queries.altTitle"));
        setOpenQueryRemarkDialog(true);
    };

    const handleRemark = () => {
        setQueryRemarkTitle(t("common:remarks.altTitle"));
        setOpenQueryRemarkDialog(true);
    };

    useEffect(() => {
        const isDisplayBtn = (status, section) => {
            // end workflow;
            if (isEndWorkflow(status)) {
                return false;
            }
            // section is Query or Remark;
            if (
                section.indexOf(t("common:queries.altTitle")) > -1 ||
                section.indexOf(t("common:queries.title")) > -1 ||
                section.indexOf(t("common:remarks.altTitle")) > -1 ||
                section.indexOf(t("common:remarks.title")) > -1
            ) {
                return false;
            }
            if (isShipSide(user.coreAccn.TMstAccnType.atypId)) {
                return false;
            }
            return true;
        };

        const isEndWorkflow = (status) => {
            return (
                Status.REJ.code === status ||
                Status.APP.code === status ||
                Status.ACK.code === status ||
                Status.EXP.code === status
            );
        };

        if (!(props.queryRemark && props.queryRemark.status && props.queryRemark.section)) {
            return; // miss properties,
        }
        setDisplayBtn(isDisplayBtn(props.queryRemark.status, props.queryRemark.section));
        // console.log("status, section", props.queryRemark.status, props.queryRemark.section, displayBtn);
        // eslint-disable-next-line
    }, [displayBtn, props.queryRemark]);

    const handleSaveQueryRemarkDialog = ({ message }) => {
        setOpenQueryRemarkDialog(false);

        let queryRemarkProps = props.queryRemark;

        if (t("common:queries.altTitle") === queryRemarkTitle) {
            let inputData = {
                qurApp: queryRemarkProps.sectionDb.moduleName,
                qurAppId: queryRemarkProps.appId,
                qurSection: queryRemarkProps.section,
                qurSubSection: sessionStorageService.getItem(SUBSECTION),
                qurMessage: message,
            };

            sendRequest("/api/co/pedi/entity/query", "id", "post", { ...inputData });
        } else {
            let inputData = {
                rmkApp: queryRemarkProps.sectionDb.moduleName,
                rmkAppId: queryRemarkProps.appId,
                rmkSection: queryRemarkProps.section,
                rmkSubSection: sessionStorageService.getItem(SUBSECTION),
                rmkMessage: message,
            };

            sendRequest("/api/co/pedi/entity/remark", "id", "post", { ...inputData });
        }
    };

    let queryBtn = (
        <Tooltip title={t("common:queries.altTitle")}>
            <IconButton
                aria-label={t("common:queries.altTitle")}
                type="button"
                color="primary"
                onClick={(e) => {
                    handleQuery(e);
                }}
            >
                <HelpOutlineIcon />
            </IconButton>
        </Tooltip>
    );

    let remarkBtn = (
        <Tooltip title={t("common:remarks.altTitle")}>
            <IconButton
                aria-label={t("common:remarks.altTitle")}
                type="button"
                color="primary"
                onClick={(e) => {
                    handleRemark(e);
                }}
            >
                <CommentOutlinedIcon />
            </IconButton>
        </Tooltip>
    );

    ///////////////////////////////////////////////////
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const { isLoading, isFormSubmission, res, error, sendRequest } = useHttp();

    useEffect(() => {
        let msg = t("common:common.msg.createSuccess", { item: queryRemarkTitle });
        let severity = "success";
        if (!isLoading && !error && res) {
            setSubmitSuccess(true);
            setSnackBarState((sb) => {
                return { ...sb, open: true, msg: msg, severity: severity };
            });
        } else if (error) {
            msg = t("common:common.msg.fetchFail", { item: queryRemarkTitle });
            severity = "error";
        }

        if (isFormSubmission && !isLoading) {
        }
        // eslint-disable-next-line
    }, [isLoading, error, res, isFormSubmission]);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
    });

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
    ///////////////////////////////////////////////////

    return (
        <div className="flex items-center">
            {isLoading}
            {snackBar}

            {displayBtn ? (
                <>
                    {queryBtn}
                    {remarkBtn}
                </>
            ) : (
                <></>
            )}

            <div className="flex items-center">
                <C1QueryRemarkDialog
                    isOpen={openQueryRemarkDialog}
                    title={queryRemarkTitle}
                    handleClose={handleCloseQueryRemarkDialog}
                    handleSave={handleSaveQueryRemarkDialog}
                />
            </div>
        </div>
    );
};

export default C1FormButtonsQueryRemark;
