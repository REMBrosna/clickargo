import { Divider, Grid, Paper, Tabs, Dialog, Button } from "@material-ui/core";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { isEditable, previewPDF, Uint8ArrayToString } from "app/c1utils/utility";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import { Status, JobStates } from "app/c1utils/const";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";

import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import AccessTimeOutlinedIcon from '@material-ui/icons/AccessTimeOutlined';
import RentalDetails from "./tabs/RentalDetails";
import RentalAudits from "./tabs/RentalAudits";

const RentalManagementFormDetails = () => {

    const { t } = useTranslation(["administration"]);

    const { viewType, jobId } = useParams();
    const history = useHistory();
    const { user } = useAuth();

    const tabList = [
        { text: t("administration:rentalManagement.tabs.details"), icon: <WorkOutlineOutlinedIcon /> },
        { text: t("administration:rentalManagement.tabs.audits"), icon: <AccessTimeOutlinedIcon /> }
    ];

    const [tabIndex, setTabIndex] = useState(0);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    /** ------------------ States ---------------------------------*/

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [loading, setLoading] = useState(true);
    const [inputData, setInputData] = useState({});
    const [controls, setControls] = useState([]);
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [validationErrors, setValidationErrors] = useState({});
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");
    const [fileUploaded, setFileUploaded] = useState(false);

    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        save: { show: viewType === 'edit', eventHandler: () => handleSaveOnClick() },
        activate: { show: true, eventHandler: () => handleExitOnClick() },
        deactivate: { show: true, eventHandler: () => handleExitOnClick() }
    };
    /** ------------------- Update states ----------------- */
    useEffect(() => {
        if (viewType === 'new') {
            sendRequest("/api/v1/clickargo/clicdo/job/doiCo/" + jobId, "newJob", "get", {});
        } else if (viewType === 'view' || viewType === 'edit') {
            setSnackBarOptions(defaultSnackbarValue);
            setLoading(true);
            setFileUploaded(true);
            sendRequest("/api/v1/clickargo/clicdo/job/doiCo/" + jobId, "getJob", "get", null);
        }
        // eslint-disable-next-line
    }, [jobId, viewType]);


    useEffect(() => {

        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "newJob": {
                    break;
                }
                default: break;
            }
        }

        if (error) {
            //goes back to the screen
            setLoading(false);
        }

        //If validation has value then set to the errors
        if (validation) {
            setValidationErrors({ ...validation });
            setLoading(false);
            setSnackBarOptions(defaultSnackbarValue);

            //if validation contains SUBMIT API CALL FAILURE, prompt message
            // console.log(validation['Submit.API.call'])
            if (validation['Submit.API.call']) {
                // alert(validation['Submit.API.call'])
                setOpenWarning(true)
                setWarningMessage(validation['Submit.API.call'])
            }
        }

        if(inputData) {
            console.log('input Data State',inputData)
        }


        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error, inputData]);

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        if (elName === 'authorized') {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.checked) });
        } else if (elName === 'shipmentType') {
            setInputData({ ...inputData, "tckJob": { ...inputData['tckJob'], "tckMstShipmentType": { "shtId": e.target.value } } });
        } else if (elName === 'documentType') {
            setInputData({ ...inputData, "documentType": e.target.value });
        } else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
        }

    };


    const handleDateChange = (name, e) => {
        if (name === 'tckJob.tckRecordDate.rcdDtStart') {
            let startDt = moment(e).format('YYYY/MM/DD');
            let expDt = moment(inputData?.tckJob?.tckRecordDate?.rcdDtExpiry).format('YYYY/MM/DD');
            if (expDt < startDt) {
                setInputData({
                    ...inputData,
                    "tckJob": { ...inputData['tckJob'], "tckRecordDate": { "rcdDtStart": e, "rcdDtExpiry": e } }
                });
            } else {
                setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
            }
        } else if (name === 'tckJob.tckRecordDate.rcdDtExpiry') {
            setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
        }
    };

    const handleExitOnClick = () => {
        history.push("/administrations/driver-management/list");
    }

    const handleSaveOnClick = () => {
        setLoading(true);
        setValidationErrors({});
        switch (viewType) {
            case "new":
                sendRequest("/api/v1/clickargo/clicdo/job/doiCod", "createJob", "post", { ...inputData });
                break;
            case "edit":
                sendRequest("/api/v1/clickargo/clicdo/job/doiCod/" + jobId, "updateJob", "put", { ...inputData });
                break;
            default:
                break;
        }
    };

    const handleBtnSaveTruckClick = () => {
        console.log('payload save truck', inputData);
    }

    const handleConfirmAction = (e) => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });

        let errors = handleValidateFields();
        if (!errors) {
            setLoading(true);
            sendRequest("/api/v1/clickargo/clicdo/job/doiCo/" + jobId, "submitJob", "put", { ...inputData });
        } else {
            return;
        }


    };

    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    const handleSubmitOnClick = () => {
        setInputData({ ...inputData, "action": "SUBMIT" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SUBMIT", open: true });
    }

    const handleCancelOnClick = () => {
        setInputData({ ...inputData, "action": "CANCEL" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "CANCEL", open: true });
    }

    const handleDeleteOnClick = () => {
        setInputData({ ...inputData, "action": "DELETE" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "DELETE", open: true });
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSaveOnClick();
        } else if (action.toLowerCase() === "exit") {
            handleExitOnClick();
        } else if (action.toLowerCase() === "submit") {
            handleSubmitOnClick();
        } else if (action.toLowerCase() === "cancel") {
            handleCancelOnClick();
        } else if (action.toLowerCase() === "delete") {
            handleDeleteOnClick();
        } else if (action.toLowerCase() === "saveTruck") {
            handleBtnSaveTruckClick();
        } else {
            setOpenSubmitConfirm({ action: action, open: true });
        }
    };

    const handleValidateFields = () => {
        // console.log("validating");
        return null;
    }

    const onFileChangeHandler = (e) => {
        e.preventDefault();
        let file = e.target.files[0];

        if (!file) {
            // didn't select file
            return;
        }

        let errors = handleSignatureValidate(file.type);
        if (Object.keys(errors).length === 0) {
            // setFileSrc(URL.createObjectURL(e.target.files[0]));

            const fileReader = new FileReader();
            fileReader.readAsArrayBuffer(e.target.files[0]);
            fileReader.onload = e => {

                const uint8Array = new Uint8Array(e.target.result);
                if (uint8Array.byteLength === 0) {
                    return;
                }
                let imgStr = Uint8ArrayToString(uint8Array);
                // console.log("imgStr 2 ", imgStr.length, imgStr,);
                let base64Sign = btoa(imgStr);
                // setFileSrc('data:image/png;base64,' + base64Sign);

                // uploadFile = { ...uploadFile, attName: file.name, attData: base64Sign, attReferenceid: inputData?.jobId };

                setInputData({ ...inputData, "jobAttach": { ...inputData['jobAttach'], attName: file.name, attData: base64Sign } });

                //sendRequest(`/api/job/attach/${inputData?.jobId}`, "uploadBL", "put", uploadFile);
                setFileUploaded(true);
            };
        } else {
            setValidationErrors(errors);
        }
    };

    const handleSignatureValidate = (uploadFileType) => {
        const errors = {};
        // if ((uploadFileType && uploadFileType !== "image/jpg")
        //     && (uploadFileType && uploadFileType !== "image/jpeg")
        //     && (uploadFileType && uploadFileType !== "image/png")) {
        //     errors.sigUpload = "File type not allowed"
        // }
        if (uploadFileType && uploadFileType !== "application/pdf") {
            errors.fileUpload = t("cargoowners:msg.nonPDFNotAllowed")
        }
        if (uploadFileType === "") {
            errors.fileUpload = t("cargoowners:msg.noFileUploded")
        }

        // console.log("errors", errors);
        return errors;
    };

    const handleViewFile = (e, attId) => {
        // sendRequest(`/api/jobs/authLetter/download/${attId}`, "download");
        if (inputData?.jobAttach?.attData) {
            viewFile(inputData?.jobAttach?.attName, inputData?.jobAttach?.attData)
        } else {
            const url = `/api/v1/clickargo/attachments/job/${attId}`;
            sendRequest(url, "download");
        }
    };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };




    let bcLabel = t("administration:rentalManagement.form.edit")
    let formButtons;
    if (!loading) {
        formButtons = (
            <C1FormButtons
                options={{
                    back: {
                        show: true,
                        eventHandler: handleExitOnClick,
                    },
                    save: {
                        show: true,
                        eventHandler: handleSaveOnClick,
                    },
                }}
            />
        );

        if (viewType) {
            switch (viewType) {
                case "edit":
                    bcLabel = t("administration:rentalManagement.form.edit")
                    formButtons = (
                        <C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)} />
                    );
                    break;
                case "view":
                    formButtons = (<C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)}>
                    </C1FormButtons>);
                    break;
                case "new":
                    bcLabel = t("administration:rentalManagement.breadCrumbs.create");
                    break;
                default: break;
            }
        }
    }

    return loading ? <MatxLoading /> : (<React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                { name: t("administration:rentalManagement.breadCrumbs.list"), path: "/administrations/driver-management/list" },
                { name: viewType === 'new' ? (t("administration:rentalManagement.breadCrumbs.create")) : (viewType === 'view' ? (t("administration:rentalManagement.breadCrumbs.edit")) : (viewType === 'edit' ? t("administration:rentalManagement.breadCrumbs.edit") : t("administration:rentalManagement.breadCrumbs.edit"))) }
            ]}
            title={bcLabel}
            titleStatus={`New`}
            formButtons={formButtons}
            initialValues={{ ...inputData }}
            values={{ ...inputData }}
            snackBarOptions={snackBarOptions}
            isLoading={loading}>
            {(props) => (
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Paper>
                            <Tabs
                                className="mt-4"
                                value={tabIndex}
                                onChange={handleTabChange}
                                indicatorColor="primary"
                                textColor="primary"
                                variant="scrollable"
                                scrollButtons="auto">
                                {tabList &&
                                    tabList.map((item, ind) => {
                                        return (
                                            <TabsWrapper
                                                // CPEDI-193
                                                style={ind === 4 ? { backgroundColor: '#e4effa' } : {}}
                                                className="capitalize"
                                                value={ind}
                                                disabled={item.disabled}
                                                label={
                                                    <TabLabel
                                                        viewType={viewType}
                                                        invalidTabs={inputData.invalidTabs}
                                                        tab={item} />
                                                }
                                                key={ind}
                                                icon={item.icon}
                                                {...tabScroll(ind)}
                                            />
                                        );
                                    })}
                            </Tabs>
                            <Divider className="mb-6" />
                            {tabIndex === 0 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                <RentalDetails viewType={viewType}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    handleInputFileChange={onFileChangeHandler}
                                    handleViewFile={handleViewFile}
                                    isDisabled={isDisabled}
                                    inputData={inputData}
                                    errors={validationErrors}
                                    fileUploaded={fileUploaded}
                                    handleBtnSaveTruckClick={handleBtnSaveTruckClick}
                                /></C1TabInfoContainer>}

                            {tabIndex === 1 &&
                                <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.authorisation' title="empty" guideAlign="right" open={false}>
                                    <RentalAudits viewType={viewType}
                                        inputData={inputData} />
                                </C1TabInfoContainer>}

                            {tabIndex === 2 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}><C1AuditTab filterId={inputData.jobId ? inputData.jobId : 'draft'}></C1AuditTab></C1TabInfoContainer>}

                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>

        {/* For submit confirmation */}
        <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={t("cargoowners:msg.confirmation", { action: openSubmitConfirm?.action })}
            title={t("cargoowners:popup.confirmation")}
            onYesClick={(e) => handleConfirmAction(e)} />

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

export default withErrorHandler(RentalManagementFormDetails);