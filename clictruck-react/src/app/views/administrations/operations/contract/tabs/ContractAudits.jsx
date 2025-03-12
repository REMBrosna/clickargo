import { Grid, IconButton, Tooltip, Snackbar, Dialog, Button } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import useHttp from "app/c1hooks/http";
import { previewPDF, formatDate, customFilterDateDisplay, generateID, isEmpty, Uint8ArrayToString } from "app/c1utils/utility";
import { deepUpdateState } from "app/c1utils/stateUtils";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import { useTranslation } from "react-i18next";
import GetAppIcon from '@material-ui/icons/GetAppOutlined';
import PublishIcon from '@material-ui/icons/PublishOutlined';
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import { JobStates } from "app/c1utils/const";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1Alert from "app/c1component/C1Alert";
import C1PopUp from "app/c1component/C1PopUp";
import C1IconButton from "app/c1component/C1IconButton";
import AddJobAttPopup from "../popups/AddJobAttPopup";

const ContractAudits = ({
    inputData,
    viewType
}) => {

    const { t } = useTranslation(["buttons", "listing", "common", "cargoowners"]);

    const [isRefresh, setRefresh] = useState(false);
    const [jobId, setJobId] = useState(inputData?.tckJob?.jobId ? inputData?.tckJob?.jobId : "empty");

    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");

    const [view, setView] = useState(false);
    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const [popUpFieldError, setPopUpFieldError] = useState({});
    const popupDefaultValue = {
        attId: generateID("CKJA"),
        tmstAttType: {
            mattId: "",
        },
        attData: null,
        attName: "",
        tckJob: {
            jobId: jobId
        }
    }
    const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
    const ALLOWED_FILE_EXTS = ['pdf', 'doc', 'docx', 'jpeg', 'jpg', 'png'];
    const [confirm, setConfirm] = useState({ attId: null });
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [alreadyAssignedErrorOpen, setAlreadyAssignedErrorOpen] = useState({ msg: null, open: false });
    const [openDeleteConfirm, setOpenDeleteConfirm] = useState({ action: null, open: false });
    const [success, setSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: "",
        severity: 'success'
    });

    /** ---------------- Declare states ------------------- */
    const { isLoading, res, urlId, sendRequest } = useHttp();

    /** --------------- Update states -------------------- */

    useEffect(() => {
        if (!isLoading && res) {
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
               case "download": {
                    viewFile(res?.data?.attName, res?.data?.attData);
                    break;
                }
                case "delete": {
                    setLoading(false);
                    setSuccess(true)
                    setRefresh(true)
                    setSnackBarState({ ...snackBarState, open: true, msg: t("cargoowners:msg.deleteAttSuccess") });
                    break;
                }
                default: break;
            }

        }
    }, [isLoading, res, urlId]);


    /** ---------------- Event handlers ------------------- */
    const handleViewFile = (e, attId) => {
        const url = `/api/v1/clickargo/attachments/job/${attId}`;
        sendRequest(url, "download");
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

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    const popUpAddHandler = () => {
        if(jobId === "empty"){
            setOpenWarning(true)
            setWarningMessage(t("cargoowners:msg.jobIdNotAvailable"))
            return;
        }
        setView(false);
        setOpenAddPopUp(true);
        setPopUpFieldError({});
        setPopUpDetails(popupDefaultValue);
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

        return errors;
    }

    const uploadAttachment = (e) => {

        if (!isEmpty(handlePopUpFieldValidate())) {
            setPopUpFieldError(handlePopUpFieldValidate());
        } else {
            setOpenAddPopUp(false);
            setPopUpFieldError({});
            setLoading(true);
            setSuccess(false)
            setRefresh(false);
            sendRequest(`/api/v1/clickargo/attachments/attach`, "createAttachment", "POST", { ...popUpDetails })
        }
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setPopUpDetails({ ...popUpDetails, ...deepUpdateState(popUpDetails, elName, e.target.value) });
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

    const columns = [
        {
            name: "audtEvent",
            label: t("listing:audits.event")
        },
        {
            name: "audtTimestamp",
            label: t("listing:audits.dateTime")
        },
        {
            name: "audtRemarks",
            label: t("listing:audits.remarks")
        },
        {
            name: "audtUid",
            label: t("listing:audits.userId")
        },
        {
            name: "audtUname",
            label: t("listing:audits.userName")
        },
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

    return (
        <React.Fragment>
            {loading && <MatxLoading />}
            {snackBar}
            {confirm && confirm.attId && (
                <ConfirmationDialog
                    open={open}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", { action: openDeleteConfirm?.action, id: confirm.attId })}
                    onYesClick={() => handleDeleteHandler()}
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
            <C1DataTable
                url={"/api/common/entity/auditLog"}
                isServer={true}
                columns={columns}
                defaultOrder="attDtCreate"
                defaultOrderDirection="desc"
                filterBy={
                    [
                        { attribute: "TCkJob.jobId", value: jobId },
                        { attribute: "mattStatus", value: 'A'}
                    ]
                }
                isRefresh={isRefresh}
                isShowViewColumns
                isShowFilter
                isShowToolbar
                isShowFilterChip
                isShowDownload
                // handleBuildBody={handleDownloadBuildBody}
                isShowPrint
                guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
            />
            <C1PopUp
                title={t("listing:attachments.title")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                actionsEl={
                    <C1IconButton tooltip={t("buttons:upload")} childPosition="right">
                        <PublishIcon color="primary" fontSize="large" onClick={() => uploadAttachment()}></PublishIcon>
                    </C1IconButton>}>
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
        </React.Fragment >
    );
};

export default ContractAudits;