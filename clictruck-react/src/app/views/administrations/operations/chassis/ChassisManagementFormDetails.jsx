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
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";

import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import AccessTimeOutlinedIcon from '@material-ui/icons/AccessTimeOutlined';
import ChassisDetails from "./tabs/ChassisDetails";
import { RecordStatus } from "app/c1utils/const";

const ChassisManagementFormDetails = () => {

    const { t } = useTranslation(["administration", "common"]);

    const { viewType, chsId } = useParams();
    const history = useHistory();

    const tabList = [
        { text: t("administration:chassisManagement.tabs.details"), icon: <WorkOutlineOutlinedIcon /> },
        { text: t("administration:chassisManagement.tabs.audits"), icon: <AccessTimeOutlinedIcon /> }
    ];

    const [tabIndex, setTabIndex] = useState(0);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    /** ------------------ States ---------------------------------*/

    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

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
    const [status, setStatus] = useState("");

    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        save: { show: viewType === 'edit', eventHandler: () => handleSubmitOnClick() },
        delete: { show: true, eventHandler: () => handleDeleteOnClick() }
    };
    /** ------------------- Update states ----------------- */
    useEffect(() => {
        if (viewType === 'new') {
            sendRequest("/api/v1/clickargo/clictruck/administrator/chassis/-", "getData", "GET", null);
        } else if (viewType === 'view' || viewType === 'edit') {
            sendRequest("/api/v1/clickargo/clictruck/administrator/chassis/" + chsId, "getData", "GET", null);
        }
    }, [chsId, viewType]);


    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "createData": {
                    setLoading(false)
                    setInputData({ ...res?.data });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.saveSuccess"),
                    });
                    break;
                }
                case "getData": {
                    setInputData({ ...res.data });
                    setStatus(res.data?.chsStatus)
                    setLoading(false)
                    break;
                }
                case "updateData": {
                    setInputData({ ...res.data });
                    setStatus(res.data?.chsStatus)
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.updateSuccess"),
                    });
                    break;
                }
                case "setStatus": {
                    setLoading(false)
                    setInputData({ ...inputData, 'chsStatus': status })
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.updateSuccess"),
                    });
                    break;
                }
                case "deleteData": {
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.deleteSuccess"),
                    });
                    break;
                }
                default: break;
            }
        }

        if (error) {
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

        if (inputData) {
            console.log('input Data State', inputData)
        }


        // eslint-disable-next-line
    }, [urlId, res, isLoading, error, validation]);

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        if (elName === 'drvPhone') {
            const re = /^\+?[0-9]*$/;
            if (e.target.value === "" || re.test(e.target.value)) {
                setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
            }
        } else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
        }
    };


    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
    };

    const handleExitOnClick = () => {
        history.push("/administrations/chassis-management/list");
    }

    const handleSaveOnClick = () => {
        setLoading(true);
        setValidationErrors({});
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        switch (viewType) {
            case "new":
                sendRequest("/api/v1/clickargo/clictruck/administrator/chassis", "createData", "POST", { ...inputData });
                break;
            case "edit":
                sendRequest("/api/v1/clickargo/clictruck/administrator/chassis/" + chsId, "updateData", "PUT", { ...inputData });
                break;
            default:
                break;
        }
    };

    const handleSubmitDelete = () => {
        setLoading(true);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        sendRequest("/api/v1/clickargo/clictruck/administrator/chassis/" + chsId, "deleteData", "DELETE", { ...inputData });
    }

    const handleSubmitStatus = () => {
        setLoading(true)
        const action = status === RecordStatus.INACTIVE.code ? 'deactive' : 'active';
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })
        sendRequest("/api/v1/clickargo/clictruck/administrator/chassis/" + chsId + "/" + action, "setStatus", "PUT", null);
    }

    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    const handleSubmitOnClick = () => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.saveConfirm') });
    }

    const handleSetActiveOnClick = () => {
        setStatus(RecordStatus.ACTIVE.code)
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "STATUS", open: true, msg: t('common:msg.activeConfirm') });
    }

    const handleSetInActiveOnClick = () => {
        setStatus(RecordStatus.INACTIVE.code)
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "STATUS", open: true, msg: t('common:msg.inActiveConfirm') });
    }

    const handleDeleteOnClick = () => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "DELETE", open: true, msg: t('common:msg.deleteConfirm') });
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSaveOnClick();
        } else if (action.toLowerCase() === "delete") {
            handleSubmitDelete();
        } else if (action.toLowerCase() === "status") {
            handleSubmitStatus();
        } else {
            setOpenSubmitConfirm({ action: action, open: true });
        }
    };

    const onFileChangeHandler = (e) => {
        e.preventDefault();
        let file = e.target.files[0];

        if (!file) {
            // didn't select file
            return;
        }

        let errors = handleSignatureValidate(file.type);
        if (Object.keys(errors).length === 0) {
            const fileReader = new FileReader();
            fileReader.readAsArrayBuffer(e.target.files[0]);
            fileReader.onload = e => {
                const uint8Array = new Uint8Array(e.target.result);
                if (uint8Array.byteLength === 0) {
                    return;
                }
                let imgStr = Uint8ArrayToString(uint8Array);
                let base64Sign = btoa(imgStr);
                setInputData({ ...inputData, ...{ base64File: base64Sign, drvLicensePhotoName: file.name } });
                setFileUploaded(true);
            };
        } else {
            setValidationErrors(errors);
        }
    };

    const handleSignatureValidate = (uploadFileType) => {
        const errors = {};
        const images = ["image/png", "image/jpeg"];
        if (uploadFileType && !images.includes(uploadFileType)) {
            errors.fhotoFileButton = t("common:common.msg.nonImageNotAllowed")
        }
        if (uploadFileType === "") {
            errors.fhotoFileButton = t("common:common.msg.noFileUploded")
        }
        return errors;
    };

    const handleViewFile = (fileName, data) => {
        viewFile(fileName, data)
    };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };

    let bcLabel = viewType === 'edit' ? t("administration:chassisManagement.form.edit") : t("administration:driverManagement.form.view")
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
                        eventHandler: handleSubmitOnClick,
                    },
                }}
            />
        );

        if (viewType) {
            switch (viewType) {
                case "edit":
                    bcLabel = t("administration:chassisManagement.form.edit")
                    formButtons = (
                        <C1FormButtons options={{
                            ...getFormActionButton(initialButtons, controls, eventHandler),
                            ...{
                                activate: { show: inputData.chsStatus === RecordStatus.INACTIVE.code, eventHandler: () => handleSetActiveOnClick() },
                                deactivate: { show: inputData.chsStatus === RecordStatus.ACTIVE.code, eventHandler: () => handleSetInActiveOnClick() },
                            }
                        }} />
                    );
                    break;
                case "view":
                    formButtons = (<C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)}>
                    </C1FormButtons>);
                    break;
                case "new":
                    bcLabel = t("administration:chassisManagement.breadCrumbs.create");
                    break;
                default: break;
            }
        }
    }

    return loading ? <MatxLoading /> : (<React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                { name: t("administration:chassisManagement.breadCrumbs.list"), path: "/administrations/driver-management/list" },
                { name: viewType === 'new' ? (t("administration:chassisManagement.breadCrumbs.create")) : (viewType === 'view' ? (t("administration:chassisManagement.breadCrumbs.view")) : (viewType === 'edit' ? t("administration:driverManagement.breadCrumbs.edit") : t("administration:driverManagement.breadCrumbs.edit"))) }
            ]}
            title={bcLabel}
            titleStatus={inputData.chsStatus}
            formButtons={formButtons}
            initialValues={{ ...inputData }}
            values={{ ...inputData }}
            snackBarOptions={{ ...snackBarOptions, redirectPath: "/administrations/chassis-management/list" }}
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
                                <ChassisDetails viewType={viewType}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    handleInputFileChange={onFileChangeHandler}
                                    handleViewFile={handleViewFile}
                                    isDisabled={isDisabled}
                                    inputData={inputData}
                                    errors={validationErrors}
                                    fileUploaded={fileUploaded}
                                /></C1TabInfoContainer>}
                            {tabIndex === 1 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}><C1AuditTab filterId={inputData?.chsId ? inputData?.chsId : 'empty'}></C1AuditTab></C1TabInfoContainer>}

                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>

        {/* For submit confirmation */}
        <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={openSubmitConfirm?.msg}
            title={t("common:popup.confirmation")}
            onYesClick={(e) => eventHandler(openSubmitConfirm?.action)} />

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

export default withErrorHandler(ChassisManagementFormDetails);