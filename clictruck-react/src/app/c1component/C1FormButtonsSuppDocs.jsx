import React, { useEffect, useState } from "react";
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import AttachmentIcon from '@material-ui/icons/Attachment';
import useHttp from "app/c1hooks/http";
import { downloadFile } from "app/c1utils/utility";
import Snackbar from "@material-ui/core/Snackbar";
import { MatxLoading } from "matx";
import C1Alert from "app/c1component/C1Alert";
import { useTranslation } from "react-i18next";

const C1FormButtonsSuppDocs = ({ appType, appId, rendered }) => {

    const { t } = useTranslation(["buttons"]);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const { isLoading, isFormSubmission, res, error, sendRequest } = useHttp();

    const handleDownload = (e) => {
        //call backend for download passing the appType
        console.log("handleDownload");
        sendRequest(`/api/app/downloadSupportDocs/${appType}/${appId}`);
    }

    const viewFile = (fileName, contents) => {
        console.log("fileName", fileName, contents);
        if (contents && contents.length > 0) {
            downloadFile(fileName, contents);
        }
    };
    /////////////////
    useEffect(() => {
        let msg = "";
        let severity = "success";

        if (!isLoading && !error && res) {
            if (!res.data.contents || res.data.contents.length === 0) {
                msg = "No file to download";
                severity = "error";
                setSubmitSuccess(true);
                setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: severity } });
            } else {
                setSubmitSuccess(false);
                viewFile(res.data.fileName, res.data.contents);
            }
        } else if (error) {
            msg = "Error encountered whilte trying to fetch data!";
            severity = "error";
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: severity } });
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, error, res, isFormSubmission]);
    /////////////////

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
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

    let elDownloadTemplate = <Tooltip title={t("buttons:dlSuppDocs")}>
        <IconButton aria-label="Download Support Documents" type="button" color="primary"
            onClick={(e) => { handleDownload(e); }} target="_blank" >
            <AttachmentIcon />
        </IconButton>
    </Tooltip>;

    return <React.Fragment>
        {isLoading && <MatxLoading />}
        {snackBar}
        {rendered ? elDownloadTemplate : ""}
    </React.Fragment>
}


C1FormButtonsSuppDocs.defaultProps = {
    rendered: true
}
export default C1FormButtonsSuppDocs;