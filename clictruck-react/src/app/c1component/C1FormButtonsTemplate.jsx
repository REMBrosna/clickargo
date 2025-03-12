import React, { useEffect, useState } from "react";
import PropTypes from 'prop-types'
import IconButton from '@material-ui/core/IconButton';
import Button from "@material-ui/core/Button";
import Tooltip from '@material-ui/core/Tooltip';
import GetAppIcon from '@material-ui/icons/GetApp';
import PublishIcon from '@material-ui/icons/Publish';
import { AlertTitle } from "@material-ui/lab";
import { CircularProgress, Snackbar } from "@material-ui/core";
import axios from 'axios.js';

import C1InputField from "app/c1component/C1InputField";
import C1Dialog from "app/c1component/C1Dialog";
import { Uint8ArrayToString } from "app/c1utils/utility";
import { useStyles } from "app/c1utils/styles";
import C1Alert from "./C1Alert";

import { useTranslation } from "react-i18next";

/**
 * @param appType - appType for upload/download in UPPERCASE. For example: VP, VC, DOS
 * @param disabled - boolean value to flag if button is disabled or not
 */
const C1FormButtonsTemplate = ({ appType, disabled }) => {

    const { t } = useTranslation(["common"]);

    const [isDialogOpen, setOpenDialog] = useState(false);
    const [fileData, setFileData] = useState({ name: "", data: "" });
    const [isUploadSuccess, setUploadSucccess] = useState(false);
    const [isUploadLoading, setUploadLoading] = useState(false);
    const [snackBarState, setSnackBarState] = useState({ open: false, vertical: 'top', horizontal: 'center', msg: '', severity: 'success' });
    const [errors, setErrors] = useState([]);

    const classes = useStyles();

    const handleCloseUploadDialog = () => {
        setOpenDialog(false);
        setErrors([]);
    }

    const handleOpenUploadDialog = () => {
        setOpenDialog(true);
    }

    const handleUpload = () => {
        if (fileData.data == null || fileData.data === "")
            return;

        setUploadLoading(true);

        axios.post(`/api/app/file/uploadExcel`, {
            appType,
            appId: 20210718616420494133, // testing only
            file: fileData.data
        })
            .then(result => {
                if (result) {
                    setUploadSucccess(true);
                    setOpenDialog(false);
                    setErrors([]);
                    setSnackBarState({ ...snackBarState, open: true, msg: 'File uploaded successfully!', severity: 'success' });
                    console.log(result)
                }
            })
            .catch((error) => {
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
                }
            });
        setUploadLoading(false);
    }

    const handleDownload = () => {
        //call backend for download passing the appType
        axios.get(`/api/app/file/download/${appType}`, { responseType: "blob" })
            .then(({ data }) => {
                const downloadUrl = window.URL.createObjectURL(new Blob([data]));
                const link = document.createElement('a');
                link.href = downloadUrl;
                link.setAttribute('download', `${appType}-template.xlsx`);
                document.body.appendChild(link);
                link.click();
                link.remove();
            })
            .catch((error) => {
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

    useEffect(() => {
        //if no appType is specified
        if (!(appType && disabled))
            return;
    }, [appType]);

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
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
    let elDownloadTemplate = <Tooltip title="Download">
        <IconButton aria-label="download" type="button" color="primary" disabled={disabled}
            onClick={() => handleDownload()} target="_blank" download>
            <GetAppIcon />
        </IconButton>
    </Tooltip>;


    //Upload whole application template
    let elUploadTemplate = <Tooltip title={t("buttons:upload")}>
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
            onClick={(e) => handleUpload(e)}>Upload</Button>
        {isUploadLoading && <CircularProgress size={24} className={classes.buttonProgress} />}</React.Fragment>

    return <React.Fragment>
        {elDownloadTemplate}
        {elUploadTemplate}
        {snackBar}
        <C1Dialog
            title="Upload Template"
            isOpen={isDialogOpen}
            actionsEl={<Button variant="contained"
                color={isUploadLoading ? "default" : "primary"}
                size="large"
                fullWidth
                onClick={(e) => handleUpload(e)}>Upload</Button>}
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
                    <AlertTitle>Error(s)</AlertTitle>
                    {errors.map((message, key) => <li key={key}>{message}</li>)}
                </C1Alert>
            )}
        </C1Dialog>
    </React.Fragment>
}

C1FormButtonsTemplate.propTypes = {
    appType: PropTypes.string.isRequired,
    disabled: PropTypes.bool,
}

C1FormButtonsTemplate.defaultProps = {
    disabled: false
}

export default C1FormButtonsTemplate;