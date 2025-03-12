
import React, { useEffect, useState } from "react";
import { useHistory, useParams } from "react-router-dom";

import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import TabLabel from "app/portedicomponent/TabLabel";
import { tabScroll } from "app/c1utils/styles";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import DoJobDetails from "./DoJobDetails";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";
import C1FormButtons from "app/c1component/C1FormButtons";
import { getActionButton, isEditable, Uint8ArrayToString, previewPDF } from "app/c1utils/utility";
import useAuth from 'app/hooks/useAuth';
// import JobAuthorisationLetters from "./tabs/JobAuthorisationLetters";
import C1AuditTab from "app/c1component/C1AuditTabFake";
import { ConfirmationDialog, MatxLoading } from "matx";
import moment from "moment";
import DoJobAttachments from "./DoJobAttachments";
import DoQuery from "./DoQuery";

const DoDocumentVerificationForm = () => {

    const { viewType, jobId } = useParams();
    const history = useHistory();
    const { user } = useAuth();

    const tabList = [
        { text: "Job Details" },
        { text: "Job Attachments" },
        { text: "Query" }
    ];

    const [tabIndex, setTabIndex] = useState(0);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    let uploadFile = { attType: "BILL OF LADING", attReferenceid: jobId };

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

    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        save: { show: viewType === 'edit', eventHandler: () => handleSaveOnClick() }
    };
    /** ------------------- Update states ----------------- */
    useEffect(() => {
        if (viewType === 'new') {
            sendRequest("/api/jobs/new", "newJob", "post", {});
        } else if (viewType === 'view' || viewType === 'edit') {
            setLoading(true);
            sendRequest("/api/redemptionTasks/details/" + jobId, "getJob", "get", null);
        }

    }, [jobId, viewType]);


    useEffect(() => {

        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "getJob": {
                    let data = res.data

                    setInputData({ ...data });
                    sendRequest(`/api/controls/${viewType}/${data?.status?.toLowerCase()}`, "fetchControls", "get");
                    break;
                }
                case "fetchControls": {
                    setControls([...res.data]);
                    break;
                }
                case "uploadBL": {
                    setInputData({ ...inputData, attchId: res?.data?.attId })
                    break;
                }
                case "download": {
                    viewFile(res?.data?.attName, res?.data?.attData);
                    break;
                }
                case "submit": {
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: "Submitted successfully!",
                        redirectPath: "/applications/jobs/list",
                    });
                    break;
                }
            }
        }
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        if (elName === 'authorizedParty.isAuthorized') {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.checked) });
        } else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
        }

    };


    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    };

    const handleExitOnClick = () => {
        history.push("/applications/documents/do");
    }

    const handleSaveOnClick = () => {
        setLoading(true);
        switch (viewType) {
            case "new":
                sendRequest("/api/app/vc", "createVc", "post", { ...inputData });
                break;
            case "edit":
            case "amend":
            case "view":
                sendRequest("/api/jobs/" + jobId, "update", "put", { ...inputData });
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
            sendRequest(`/api/jobs/${openSubmitConfirm?.action?.toLowerCase()}/vc`, openSubmitConfirm?.action.toLowerCase(), "put", { ...inputData, status: 'SUBMITTED', dtSubmitted: moment(new Date()) });
        } else {
            return;
        }


    };

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSaveOnClick();
        } else if (action.toLowerCase() === "exit") {
            handleExitOnClick();
        } else {
            setOpenSubmitConfirm({ action: action, open: true });
        }
    };

    const handleValidateFields = () => {
        console.log("validating");
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

                uploadFile = { ...uploadFile, attName: file.name, attData: base64Sign };

                sendRequest(`/api/job/attach/${inputData?.jobId}`, "uploadBL", "put", uploadFile);

            };
        }
    };

    const handleSignatureValidate = (uploadFileType) => {
        const errors = {};
        if ((uploadFileType && uploadFileType !== "image/jpg")
            && (uploadFileType && uploadFileType !== "image/jpeg")
            && (uploadFileType && uploadFileType !== "image/png")) {
            errors.sigUpload = "File type not allowed"
        }
        if (uploadFileType === "") {
            errors.sigUpload = "No file is uploaded"
        }
        return errors;
    };

    const handleViewFile = (e, attId) => {
        sendRequest(`/api/jobs/authLetter/download/${attId}`, "download");
    };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };




    let bcLabel = "ClicDo DO Claim Job - Document Verification"
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
                    bcLabel = "Edit Job";
                    formButtons = (
                        <C1FormButtons options={getActionButton(initialButtons, controls, eventHandler)} />
                    );
                    break;
                case "view":
                    formButtons = (<C1FormButtons options={getActionButton(initialButtons, controls, eventHandler)}>
                    </C1FormButtons>);
                    break;
                case "new":
                    bcLabel = "New Job";
                    break;
                default: break;
            }
        }
    }

    return loading ? <MatxLoading /> : (<React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                // { name: "Jobs List", path: "/applications/jobs/list" },//to do: redirect to shipping line document dashboard/workbench
                { name: "Job Details" }
            ]}
            title={bcLabel}
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
                            {tabIndex === 0 && <DoJobDetails viewType={viewType}
                                handleInputChange={handleInputChange}
                                handleDateChange={handleDateChange}
                                handleInputFileChange={onFileChangeHandler}
                                handleViewFile={handleViewFile}
                                isDisabled={isDisabled}
                                inputData={inputData} />}

                            {tabIndex === 1 && <DoJobAttachments viewType={viewType}
                                inputData={inputData} />}
                            {tabIndex === 2 && <DoQuery/>}

                            {/* {tabIndex === 2 && <C1AuditTab></C1AuditTab>} */}

                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>

        {/* For submit confirmation */}
        <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={`Are you sure you want to ${openSubmitConfirm?.action}?`}
            title="Confirmation"
            onYesClick={(e) => handleConfirmAction(e)} />

    </React.Fragment>

    );
};

export default withErrorHandler(DoDocumentVerificationForm);