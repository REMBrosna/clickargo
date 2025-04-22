import { Button, Grid, Icon } from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import useHttp from "app/c1hooks/http";
import { Uint8ArrayToString } from "app/c1utils/utility";
import { MatxLoading } from "matx";

const UploadSignature = ({
    isReplaceable,
    viewType,
    isSubmitting,
    appType,
    appId,
    props }) => {

    // console.log(`UserRolesDetails= ${data}, ${JSON.stringify(inputData)}`);

    //const [fileSrc, setFileSrc] = useState('data:image/png;base64,' + (signatureData ? signatureData.attData : ""));
    const [fileSrc, setFileSrc] = useState("");
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isFormLoading, setFormLoading] = useState(false);
    const { isLoading, isFormSubmission, res, error, urlId, sendRequest } = useHttp();
    const { t } = useTranslation(["common"]);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });

    let isDisabled = true;
    if (viewType === 'new')
        isDisabled = false;
    else if (viewType === 'edit')
        isDisabled = false;
    else if (viewType === 'view')
        isDisabled = true;

    if (isSubmitting)
        isDisabled = true;

    //////
    var imgRef = React.createRef();

    let uploadFile = { fileType: "SIG", appType, appId, extAttr: {} };

    useEffect(() => {
        sendRequest(`/api/portedi/attach/${appType}/${appId}/signature`, "getSignature");
    }, [sendRequest, appType, appId]);

    /////////////////
    useEffect(() => {
        let msg = "";
        let severity = "success";

        if (!isLoading && !error && res) {
            setFormLoading(false);
            if ("getSignature" === urlId) {
                setFileSrc('data:image/png;base64,' + res.data.data);
            } else if ("Upload" === urlId) {
                msg = t("uploadSignature.msg.uploadSuccess");
            }

        } else if (error) {
            msg = t("uploadSignature.msg.fetchError");
            severity = "error";
        }
        if ((("Upload" === urlId)) && !isLoading) {
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: severity } });
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, error, res, isFormSubmission]);


    const onFileChangeHandler = (e) => {
        e.preventDefault();
        let file = e.target.files[0];

        if (!file) {
            // didn't select file
            return;
        }

        let errors = handleSignatureValidate(file.type);
        if (Object.keys(errors).length === 0) {
            setFileSrc(URL.createObjectURL(e.target.files[0]));

            const fileReader = new FileReader();
            fileReader.readAsArrayBuffer(e.target.files[0]);
            fileReader.onload = e => {
                console.log("e.target.result", e.target.result);

                const uint8Array = new Uint8Array(e.target.result);
                if (uint8Array.byteLength === 0) {
                    return;
                }
                let imgStr = Uint8ArrayToString(uint8Array);
                // console.log("imgStr 2 ", imgStr.length, imgStr,);
                let base64Sign = btoa(imgStr);
                setFileSrc('data:image/png;base64,' + base64Sign);

                uploadFile = { ...uploadFile, "fileName": file.name, "data": base64Sign, isSignature: true };

                if (isReplaceable) {
                    sendRequest(`/api/portedi/attach`, "id", "put", uploadFile);
                } else {
                    sendRequest(`/api/portedi/attach`, "id", "post", uploadFile);
                }
            };
        } else {
            setSubmitSuccess(true);
            setFormLoading(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: errors.sigUpload, severity: "error" } });
        }
    };

    const handleSignatureValidate = (uploadFileType) => {
        const errors = {};
        if ((uploadFileType && uploadFileType !== "image/jpg")
            && (uploadFileType && uploadFileType !== "image/jpeg")
            && (uploadFileType && uploadFileType !== "image/png")) {
            errors.sigUpload = t("common:validationMsgs.uploadTypeNotAllowed");
        }
        if (uploadFileType === "") {
            errors.sigUpload = t("common:validationMsgs.uploadTypeNotAllowed");
        }
        return errors;
    };

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
    return (

        <React.Fragment>
            {isLoading && <MatxLoading />}
            {snackBar}
            <Grid container spacing={3} alignItems="flex-start">
                <Grid item lg={6} md={6} sm={6} xs={12} >
                    <div>
                        <label htmlFor="upload-multiple-file">
                            <Button
                                className="capitalize"
                                color="primary"
                                component="span"
                                variant="contained"
                                disabled={isDisabled}
                            >
                                <div className="flex items-center">
                                    <Icon className="pr-8">cloud_upload</Icon>
                                    <span>{t("uploadSignature.fields.uploadSignature")} </span>
                                </div>
                            </Button>
                        </label>
                        <input
                            className="hidden"
                            onChange={onFileChangeHandler}
                            id="upload-multiple-file"
                            disabled={isDisabled}
                            type="file"
                            single="true"
                        />
                    </div>
                    <div>
                        <p> {t("uploadSignature.fields.photoSpec")} <br />
                            {t("uploadSignature.fields.photoSpecDesc1")} <br />
                            {t("uploadSignature.fields.photoSpecDesc2")} </p>
                    </div>
                </Grid>
                <Grid item lg={6} md={6} sm={6} xs={12} >
                    <div>{t("uploadSignature.fields.previewSignature")}</div>
                    {fileSrc.includes('undefined') ?
                        <div style={{ color: 'red' }}></div>
                        : <img alt="signature" ref={imgRef} src={fileSrc} width="300px"></img>
                    }
                </Grid>
            </Grid>
        </React.Fragment>
    );
};

export default UploadSignature;