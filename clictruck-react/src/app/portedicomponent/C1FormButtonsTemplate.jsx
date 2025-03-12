import React, { useState } from "react";
import PropTypes from 'prop-types'
import IconButton from '@material-ui/core/IconButton';
import Button from "@material-ui/core/Button";
import Tooltip from '@material-ui/core/Tooltip';
import GetAppIcon from '@material-ui/icons/GetApp';
import PublishIcon from '@material-ui/icons/Publish';
import { AlertTitle } from "@material-ui/lab";
import { Snackbar, CircularProgress } from "@material-ui/core";

import C1InputField from "app/c1component/C1InputField";
import C1Dialog from "app/c1component/C1Dialog";
import { Uint8ArrayToString } from "app/c1utils/utility";
import C1Alert from "app/c1component/C1Alert";

import axios from "axios.js";
import { MatxLoading } from "matx";

import { useTranslation } from "react-i18next";

/**
 * @param appType
 * @param viewType
 * @param redirect
 * @param disabled - boolean value to flag if button is disabled or not
 * @param appId - can be a vcAppId or a srfmAppnId for new application; can be an application form ID ex: pofmId, scfmId for existing application
 */
const C1FormButtonsTemplate = ({ appType, viewType, appId, disabled, snackBarCloseEvent }) => {

    const { t } = useTranslation(["buttons"]);

    const [isDialogOpen, setOpenDialog] = useState(false);
    const [fileData, setFileData] = useState({ name: "", data: "" });
    const [isUploadSuccess, setUploadSucccess] = useState(false);
    const [isUploadLoading, setUploadLoading] = useState(false);
    const [snackBarState, setSnackBarState] = useState({ open: false, vertical: 'top', horizontal: 'center', msg: '', severity: 'success' });
    const [errors, setErrors] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const handleCloseUploadDialog = () => {
        setOpenDialog(false);
        setUploadLoading(false);
        setErrors([]);
    }

    const handleOpenUploadDialog = () => {
        setOpenDialog(true);
    }

    const handleUpload = () => {
        if (fileData.data == null || fileData.data === "")
            return;

        setUploadLoading(true);
        axios.post(`/api/app/excel/upload`, {
            appType: appType,
            appId: appId, // testing only
            file: fileData.data
        }).then(result => {
            if (result) {
                setUploadSucccess(true);
                setErrors([]);
                setSnackBarState({ ...snackBarState, open: true, msg: 'File uploaded successfully!', severity: 'success' });
                setUploadLoading(false);
                setOpenDialog(false);
            }
        }).catch((error) => {
            console.log(error);
            if (error?.err?.msg) {
                const errorList = [];
                // Testing e.msg is json object if valid will map push as string to errorList
                // else mean e.msg is string we don't need to parse.
                try {
                    const errorObject = JSON.parse(error.err.msg);
                    for (const key in errorObject) {
                        errorList.push(errorObject[key])
                    }
                } catch {
                    errorList.push(error.err.msg);
                }
                setErrors(errorList);
                setUploadLoading(false);
            }
        });
    }

    const handleDownload = () => {
        //call backend for download passing the appType
        setIsLoading(true);
        axios.get("/api/app/excel/" + appType.toLowerCase() + "/" + appId, { responseType: "blob" })
            .then(({ data }) => {
                const downloadUrl = window.URL.createObjectURL(new Blob([data]));
                const link = document.createElement('a');
                link.href = downloadUrl;
                link.setAttribute('download', `${appId}.xlsx`);
                document.body.appendChild(link);
                link.click();
                link.remove();
                setIsLoading(false);
            })
            .catch((error) => {
                setIsLoading(false);
                console.log(error);
            });
    }

    const handleFileChangeHandler = (e) => {
        e.preventDefault();
        var file = e.target.files[0];
        if (!file)
            return;

        const isXlsx = file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (!isXlsx) {
            setFileData({});
            setErrors(["File uploaded must be .xlsx"])
            return;
        } else {
            setErrors([])
        }

        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = e => {
            console.log("filereader onload");
            const uint8Array = new Uint8Array(e.target.result);

            var imgStr = Uint8ArrayToString(uint8Array);
            var base64Sign = btoa(imgStr);

            setFileData({ ...fileData, name: file.name, data: base64Sign });
        };
    }

    /*const viewFile = (fileName, data) => {
        downloadFile(fileName, data);
    };*/

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
        snackBarCloseEvent();
    };

    const anchorOriginV = snackBarState.vertical;
    const anchorOriginH = snackBarState.horizontal;
    const snackBar = <Snackbar
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

    //Application template download
    let elDownloadTemplate = <Tooltip title={t("buttons:dlTemplate")}>
        <IconButton aria-label="download" type="button" color="primary" disabled={disabled}
            onClick={() => handleDownload()} target="_blank" download>
            <GetAppIcon />
        </IconButton>
    </Tooltip>;


    //Upload whole application template
    let elUploadTemplate = <Tooltip title={t("buttons:ulTemplate")}>
        <IconButton aria-label="upload" type="button" color="primary"
            disabled={disabled}
            onClick={handleOpenUploadDialog}>
            <PublishIcon />
        </IconButton>
    </Tooltip>;

    let btnUploadEl = <React.Fragment>
        <Button variant="contained"
            color={isUploadLoading ? "default" : "primary"}
            size="large"
            fullWidth
            onClick={(e) => handleUpload(e)}>{t("buttons:upload")}</Button>
        {isUploadLoading && <CircularProgress size={24} style={{
            position: 'absolute', top: '50%',
            left: '50%',
            marginTop: -12,
            marginLeft: -12
        }} />}</React.Fragment>

    return <React.Fragment>
        {isLoading && <MatxLoading />}
        {(viewType === 'edit' || viewType === 'view' || viewType === 'amend') && elDownloadTemplate}
        {(viewType === 'edit' || viewType === 'amend') && elUploadTemplate}
        {snackBar}
        <C1Dialog
            title={t("buttons:ulTemplate")}
            isOpen={isDialogOpen}
            actionsEl={btnUploadEl}
            handleCloseEvent={handleCloseUploadDialog} >

            <C1InputField
                required
                disabled={isUploadLoading}
                label=""
                name="data"
                inputProps={{
                    accept: ".xlsx"
                }}
                key={isUploadSuccess}
                onChange={handleFileChangeHandler}
                type="file"
                helperText="Must be .xlsx" />

            {errors.length > 0 && (
                <C1Alert severity="error" >
                    <AlertTitle>{t("buttons:errors")}</AlertTitle>
                    {errors.map((message, key) => <li key={key}>{message}</li>)}
                </C1Alert>
            )}
        </C1Dialog>
    </React.Fragment >
}

C1FormButtonsTemplate.propTypes = {
    appType: PropTypes.string.isRequired,
    disabled: PropTypes.bool,
}

C1FormButtonsTemplate.defaultProps = {
    disabled: false
}

export default C1FormButtonsTemplate;