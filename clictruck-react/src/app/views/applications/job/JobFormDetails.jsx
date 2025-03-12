import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from '@material-ui/icons/AccessTimeOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import LocalShippingOutlinedIcon from '@material-ui/icons/LocalShippingOutlined';

import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";

//connect api using axios from useHttp
import useHttp from "app/c1hooks/http";

import { JobStates, Status } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { isEditable, previewPDF, Uint8ArrayToString } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

// import tabs component for render ----------------------------------------------------------------------------------------------------
// import TruckJobAuthLetters from "./tabs/TruckJobAuthLetters";
// import TruckJobDetails from "./tabs/TruckJobDetails";

import JobAuthLetters from "./tabs/JobAuthLetters";
import JobNewDetails from "./tabs/JobNewDetails";
import JobTripCharges from "./tabs/JobTripCharges";
import JobTripChargesDomestic from "./tabs/JobTripChargesDomestic";
import JobDriverAssign from './tabs/JobDriverAssign'
import JobInvoice from "./tabs/JobInvoice";

const JobFormDetails = () => {

    const { t } = useTranslation(["job"]);

    //const shippingType = history.location.state.newJobState;

    const { viewType, jobId } = useParams();
    const history = useHistory();
    const { user } = useAuth();
    //tablist ----------------------------------------------------------------------------------------------------------------------------*/
    const tabList = [
        { name: "jobDetails", text: t("job:tabs.jobDetails"), icon: <WorkOutlineOutlinedIcon /> },
        { name: "fmTripCharges", text: t("job:tabs.tripCharges"), icon: <LocalShippingOutlinedIcon /> },
        { name: "domesticTrips", text: "Domestic Trip", icon: <LocalShippingOutlinedIcon /> },
        { name: "documents", text: t("job:tabs.documents"), icon: <DescriptionOutlinedIcon /> },
        { text: t("job:tabs.audit"), icon: <AccessTimeOutlinedIcon /> },
        { name: "driverTrucks", text: "Driver/Truck", icon: <LocalShippingOutlinedIcon /> },
        { name: "invoice", text: "Invoice", icon: <DescriptionOutlinedIcon /> },
        // { text: t("job:tabs.tracking"), icon: <PublicOutlinedIcon /> }
    ];

    const [tabIndex, setTabIndex] = useState(0);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    // let uploadFile = { attType: "BILL OF LADING", attReferenceid: jobId };

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


    //initial button handler --------------------------------------------------------------------------------------------------
    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        delete: { show: true },
        submit: { show: true },
        duplicate: { show: true },
        save: { show: viewType === 'edit', eventHandler: () => handleSaveOnClick() }
    };

    const truckUrlApi = `/api/v1/clickargo/clictruck/job/truck/`;

    /** ------------------- Update states ----------------- */
    useEffect(() => {
        if (viewType === 'new') {
            sendRequest(`${truckUrlApi}` + jobId, "newJob", "GET", {});
        } else if (viewType === 'view' || viewType === 'edit') {
            setSnackBarOptions(defaultSnackbarValue);
            setLoading(true);
            setFileUploaded(true);
            sendRequest(`${truckUrlApi}` + jobId, "getJob", "GET", null);
        }
        // eslint-disable-next-line
    }, [jobId, viewType]);


    useEffect(() => {

        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "newJob": {
                    setInputData({ ...res?.data });
                    // TODO: CREATE CONTROLS FOR JOB_TRUCK
                    const reqBody = {
                        entityType: "JOB_TRUCK",
                        entityState: Status.DRF.code,
                        page: "EDIT",
                    };
                    sendRequest("/api/v1/clickargo/controls/", "fetchControls", "POST", reqBody);

                    break;
                }
                case "createJob": {
                    setInputData({ ...res?.data });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("cargoowners:msg.saveSuccess"),
                        redirectPath: "/applications/services/truck/job/edit/" + res?.data?.jobId,
                    });
                    break;
                }
                case "getJob": {
                    let data = res.data
                    setInputData({ ...data });
                    // sendRequest(`/api/v1/clickargo/clicSvc/ckSvcAuth/authParty/${data?.tcoreAccnByJobAuthorizerAccn.accnId}/${data?.tcoreAccnByJobAuthorizedPartyAccn.accnId}`, "isSelectedPartyAuthorized", "get");
                    break;
                }
                case "updateJob": {
                    let data = res.data
                    console.log("updateJob ", data)
                    setInputData({ ...data });
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("cargoowners:msg.updateSuccess"),
                    });
                    break;
                }
                case "fetchControls": {
                    setControls([...res.data]);
                    break;
                }
                case "download": {
                    viewFile(res?.data?.attName, res?.data?.attData);
                    break;
                }
                case "submitJob": {
                    let msg = t("cargoowners:msg.submitSuccess");
                    if (openSubmitConfirm && openSubmitConfirm.action === "CANCEL") {
                        msg = t("cargoowners:msg.cancelSuccess");
                    } else if (openSubmitConfirm && openSubmitConfirm.action === "DELETE") {
                        msg = t("cargoowners:msg.deleteSuccess");
                    }
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: msg,
                        redirectPath: "/applications/services/truck/list",
                    });
                    break;
                }
                default: break;
            }
        }

        if (error) {
            console.log("error", error);
            //goes back to the screen
            setLoading(false);
        }

        // setOpenWarning(true)
        // setWarningMessage("TEST")

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

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    // const handleInputChange = (e) => {
    //     const elName = e.target.name;
    //     if (elName === 'shipmentType') {
    //         setInputData({ ...inputData, "tckJob": { ...inputData['tckJob'], "tckMstShipmentType": { "shtId": e.target.value } } });
    //     } else if (elName === 'documentType') {
    //         setInputData({ ...inputData, "documentType": e.target.value });
    //     } else {
    //         setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    //     }

    // };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
        console.log('target name', e.target.name)
        console.log('target value', e.target.value)
        console.log('inputData state', inputData);

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
        } else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
        }
    };

    const handleExitOnClick = () => {
        history.push("/applications/services/job/coff/truck");
    }

    const handleSaveOnClick = () => {
        setLoading(true);
        setValidationErrors({});
        switch (viewType) {
            case "new":
                sendRequest(`${truckUrlApi}`, "createJob", "POST", { ...inputData });
                break;
            case "edit":
                sendRequest(`${truckUrlApi}` + jobId, "updateJob", "PUT", { ...inputData });
                break;
            default:
                break;
        }
    };

    const handleConfirmAction = (e) => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });

        let errors = handleValidateFields();
        if (!errors) {
            setLoading(true);
            sendRequest(`${truckUrlApi}` + jobId, "submitJob", "PUT", { ...inputData });
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

        if (uploadFileType && uploadFileType !== "application/pdf") {
            errors.fileUpload = t("cargoowners:msg.nonPDFNotAllowed")
        }
        if (uploadFileType === "") {
            errors.fileUpload = t("cargoowners:msg.noFileUploded")
        }

        return errors;
    };

    const handleViewFile = (e, attId) => {
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

    let bcLabel = t("cargoowners:form.viewJob")

    // if (shippingType === "EXPORT")
    // {bcLabel = "New Job Export";} 
    // else {bcLabel = t("cargoowners:form.viewJob");}
    //--------------------form button ---------------------------------------------------------------------------------------------------------------------
    let formButtons;
    if (!loading) {
        formButtons = (
            <C1FormButtons
                options={{
                    back: {
                        show: true,
                        eventHandler: handleExitOnClick,
                    },
                    delete: { show: true },
                    submit: { show: true },
                    duplicate: { show: true },
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
                    bcLabel = t("cargoowners:form.editJob")
                    formButtons = (
                        <C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)} />
                    );
                    break;
                case "view":
                    formButtons = (<C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)}>
                    </C1FormButtons>);
                    break;
                case "new":
                    bcLabel = t("cargoowners:form.newJob");
                    break;
                default: break;
            }
        }
    }

    return loading ? <MatxLoading /> : (<React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                { name: "ClicTruck Job List", path: "/applications/services/truck/list" },
                { name: viewType === 'new' ? (t("cargoowners:form.newJobDetails")) : (viewType === 'view' ? (t("cargoowners:form.viewJobDetails")) : (viewType === 'edit' ? t("cargoowners:form.editJobDetails") : t("cargoowners:form.jobDetails"))) }
            ]}
            title={bcLabel}
            titleStatus={inputData?.tckJob?.tckMstJobState?.jbstId || JobStates.DRF.code.toUpperCase()}
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
                                                style={{}}
                                                className="capitalize"
                                                value={ind}
                                                disabled={item.disabled}
                                                label={
                                                    <TabLabel
                                                        viewType={viewType}
                                                        invalidTabs={inputData.invalidTabs}
                                                        errors={validationErrors}
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
                                <JobNewDetails viewType={viewType}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    handleInputFileChange={onFileChangeHandler}
                                    handleViewFile={handleViewFile}
                                    isDisabled={isDisabled}
                                    inputData={inputData}
                                    errors={validationErrors}
                                    fileUploaded={fileUploaded}
                                /></C1TabInfoContainer>}
                            {tabIndex === 1 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                <JobTripCharges viewType={viewType}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    handleInputFileChange={onFileChangeHandler}
                                    handleViewFile={handleViewFile}
                                    isDisabled={isDisabled}
                                    inputData={inputData}
                                    errors={validationErrors}
                                    fileUploaded={fileUploaded}
                                /></C1TabInfoContainer>}
                            {tabIndex === 2 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                <JobTripChargesDomestic viewType={viewType}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    handleInputFileChange={onFileChangeHandler}
                                    handleViewFile={handleViewFile}
                                    isDisabled={isDisabled}
                                    //isDisabled={false}
                                    inputData={inputData}
                                    errors={validationErrors}
                                    fileUploaded={fileUploaded}
                                /></C1TabInfoContainer>}
                            {tabIndex === 3 &&
                                <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.authorisation' title="empty" guideAlign="right" open={false}>
                                    <JobAuthLetters viewType={viewType}
                                        inputData={inputData} />
                                </C1TabInfoContainer>}

                            {tabIndex === 4 &&
                                <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                    <C1AuditTab filterId={inputData.jobId ? inputData.jobId : 'draft'}></C1AuditTab>
                                </C1TabInfoContainer>}
                            {tabIndex === 5 &&
                                <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                    <JobDriverAssign />
                                </C1TabInfoContainer>
                            }
                            {tabIndex === 6 &&
                                <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                    <JobInvoice />
                                </C1TabInfoContainer>
                            }
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

export default withErrorHandler(JobFormDetails);