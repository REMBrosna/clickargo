import { Divider, Grid, Paper, Tab, Tabs } from "@material-ui/core";
import FileCopy from '@material-ui/icons/FileCopyOutlined';
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import { tabScroll } from "app/c1utils/styles";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import PersonOutline from '@material-ui/icons/PersonOutline';
import { AccnProcessTypes, AccountsProcessStates, Actions, COMMON_ATTACH_LIST_BY_ACCNID_URL } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { decodeString, encodeString } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import {  MatxLoading } from "matx";


import CompanyDetails from "./tabs/CompanyDetails";
import UserDetail from "./tabs/UserDetail";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { isEditable  } from "app/c1utils/utility";

/**@description Manage Account form details. */
const AccountProfileFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, id, ffAccnId } = useParams();

    let isFromProfile = (id === 'my');

    let history = useHistory();
    const { t } = useTranslation(["register", "common", "admin", "buttons", "listing", "cargoowners", "opadmin"]);
    const { user } = useAuth();
    const [controls, setControls] = useState([]);

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({});
    const [validationErrors, setValidationErrors] = useState({});
    const [accountId, setAccountId] = useState("");
    const [decodedId, setDecodedId] = useState({});
    const [hideSave, setHideSave] = useState(false);

    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [accnProcessType, setAccnProcessType] = useState("ACCN_REGISTRATION");
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    const tabList = [
        { name: "cmpnyDtls", text: t("register:companyDetails.title"), icon: <FileCopy /> },
        { name: "adminDtls", text: t("register:adminDetail.title"), icon: <PersonOutline /> },
        //{ text: t("common:audits.title"), icon: <Schedule /> },
    ];

    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        save: { show: (viewType == 'new' && !hideSave) || viewType == 'edit', eventHandler: () => handleSaveOnClick() },
    };

    const [regDocs, setRegDocs] = useState([]);

    const [openRemarksPopup, setOpenRemarksPopup] = useState(false);
    const [popUpFieldError, setPopUpFieldError] = useState({});

    const remarksDefaultValue = {
        arRemark: "",
        atDtCreate: new Date(),
        atUidCreate: user?.id,
        tckMstRemarkType: {
            rtId: ""
        },
        tckMstWorkflowType: {
            wktId: ""
        }
    }

    const [terminableError, setTerminableError] = useState({ msg: null, open: false });
    const [isDisabled, setDisabled] = useState(isEditable(viewType));


    useEffect(() => {
        setLoading(true);
        if (viewType !== "new") {
            setAccountId(id);
            setDecodedId(decodeString(id));
            sendRequest(`/api/v1/clickargo/manageAccnFfCo/${id}`, "getAccn", "get", {});
        } else {
            sendRequest(`/api/v1/clickargo/manageAccnFfCo/-`, "newAccn", "get", {});
        }

        // eslint-disable-next-line
    }, [id]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "newAccn": {
                    setInputData({ ...inputData, ...res?.data });
                    break;
                }
                case "getAccn":
                    setHideSave(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: false,
                    });
                    setInputData({ ...inputData, ...res?.data, ...{ffAccnId:decodeString(ffAccnId)} });
                    break;
                case "create":
                    setInputData({ ...res?.data });
                    setLoading(false)
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.saveSuccess"),
                        redirectPath: `/manageAccountsFfCo/edit/${encodeString(res?.data?.coreAccn?.accnId)}/-`,
                    });
                    break;
                case "update":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.updateSuccess"),
                    });
                    break;
                case "submit":
                case "approve":
                case "reject":
                case "suspend":
                case "terminate":
                case "resumption":
                case "delete":
                    if (res?.data.accnTerminable === true) {
                        setLoading(false)
                        setTerminableError({ ...terminableError, msg: t("common:msg.nonTerminableMsg"), open: true });
                    } else {
                        let msg = t("common:common.msg.generalAction", { action: Actions[openSubmitConfirm?.action]?.result });
                        setLoading(false)
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: msg,
                            redirectPath: "/manageAccounts/all/list",
                        });
                    }
                    break;
                case "accnRegisterDelete":
                    setLoading(false)
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.deleteSuccess"),
                        redirectPath: "/manageAccounts/all/list"
                    });
                    break;
                case "deActive":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("admin:account.msg.deactivatedSuccess"),
                        redirectPath: "/manageAccounts/all/list"
                    });
                    break;
                case "activate":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("admin:account.msg.activatedSuccess"),
                        redirectPath: "/manageAccounts/all/list"
                    });
                    break;
                default:
                    break;
            }
        }


        if (error) {
            //goes back to the screen
            setLoading(false);
            setHideSave(false);
        }

        if (validation) {
            // set errors and remark to default
            setPopUpFieldError({})
            setValidationErrors({ ...validation });
            setLoading(false);
            setHideSave(false);

            let keyList = Object.keys(validation);
            if (keyList.length > 0) {
                for (let key of keyList) {
                    if (key.includes("invalidTabs.cmpnyDtls")) {
                        setTabIndex(0);
                        break;
                    } else if (key.includes("invalidTabs.userDtls")) {
                        setTabIndex(1);
                        break;
                    }
                }
            }
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleTabChange = (e, value) => {
        //setSubmitSuccess(false);
        setTabIndex(value);
    };

    const handleInputChange2 = (e, nameA, valueA) => {
        const elName = e.target.name;
        if (nameA && valueA) {
            //this is for autocompleteselect handler
            setInputData({ ...inputData, ...deepUpdateState(inputData, nameA, valueA?.value) });
        } else {
            //this is default handler
            const { name, value } = e.target;
            setInputData({ ...inputData, ...deepUpdateState(inputData, name, value) });
        }
    }


    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, [name]: e });
    }

    const handleExitOnClick = () => {
        isFromProfile ? history.goBack() : history.push("/manageAccountsFfCo/all/list")
    }

    const handleActionOnClick = (actionUp) => {
        setInputData({ ...inputData, "action": actionUp });
        // CT-172 - This is removed so that the icon will not redirect to suspend/terminate route
        // if (actionUp === "SUSPEND" && viewType === 'edit') {
        //     history.push(`/manageAccount/suspend/${encodeString(inputData?.accnId)}`)
        //     setControls(controls.filter(c => c.ctrlAction === actionUp));
        // } else if (actionUp === "TERMINATE" && viewType === 'edit') {
        //     history.push(`/manageAccount/terminate/${encodeString(inputData?.accnId)}`)
        //     setControls(controls.filter(c => c.ctrlAction === actionUp));
        // } else {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: actionUp, open: true });
        // }
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "exit") {
            handleExitOnClick();
        } else if (action.toLowerCase() === "save") {
            handleSaveOnClick();
        } else {
            handleActionOnClick(action);
        }
    };

    const handleSaveOnClick = () => {
        setLoading(true);
        setSnackBarOptions(defaultSnackbarValue)
        setHideSave(true);
        setValidationErrors({});
        switch (viewType) {
            case "new":
                sendRequest(`/api/v1/clickargo/manageAccnFfCo/create`, "create", "POST", inputData);
                break;
            case "edit":
                sendRequest(`/api/v1/clickargo/manageAccnFfCo/${accountId}`, "update", "PUT", inputData);
                break;
            default:
                break;
        }
    };


    const handleConfirmAction = (e) => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        //do not present remarks as Delete can only be done by the non-submitted record
        if (openSubmitConfirm?.action !== "DELETE") {
            let remarksType = "";
            switch (accnProcessType) {
                case AccnProcessTypes.ACCN_REGISTRATION.code:
                    remarksType = (openSubmitConfirm?.action === "APPROVE") ? "REG_APPROVE" : openSubmitConfirm?.action == "REJECT" ? "REG_REJECT" : "REG_REQ";
                    break;
                case AccnProcessTypes.ACCN_SUSPENSION.code:
                    remarksType = (openSubmitConfirm?.action === "APPROVE") ? "SUS_APPROVE" : openSubmitConfirm?.action == "REJECT" ? "SUS_REJECT" : "SUS_REQ";
                    break;
                case AccnProcessTypes.ACCN_TERMINATION.code:
                    remarksType = (openSubmitConfirm?.action === "APPROVE") ? "TERM_APPROVE" : openSubmitConfirm?.action == "REJECT" ? "TERM_REJECT" : "TERM_REQ";
                    break;
                case AccnProcessTypes.ACCN_RESUMPTION.code:
                    remarksType = (openSubmitConfirm?.action === "APPROVE") ? "RESUMPT_APPROVE" : openSubmitConfirm?.action == "REJECT" ? "RESUMPT_REJECT" : "RESUMPT_REQ";
                default: break;
            }
            // CT-172 [START]
            if (openSubmitConfirm?.action === "SUSPEND") {
                setAccnProcessType(AccnProcessTypes.ACCN_SUSPENSION.code)
                remarksType = "SUS_REQ"
            }
            if (openSubmitConfirm?.action === "TERMINATE") {
                setAccnProcessType(AccnProcessTypes.ACCN_TERMINATION.code)
                remarksType = "TERM_REQ"
            }
            if (openSubmitConfirm?.action === "RESUMPTION") {
                setAccnProcessType(AccnProcessTypes.ACCN_RESUMPTION.code)
                remarksType = "RESUMPT_REQ"
            }
            setOpenRemarksPopup(true);
        } else {
            sendRequest(`/api/v1/clickargo/manageaccn/${id}`,
                openSubmitConfirm.action.toLowerCase(), "PUT", {
                accnDetails: { ...inputData?.accnDetails }, action: openSubmitConfirm.action,
                sageAccpacId: inputData?.sageAccpacId,
                accnProcessType: accnProcessType
            });
        }


    };

    // console.log("loading: ", loading, snackBarOptions );

    let bcLabel = "Cargo Owner Account Profile";
    let formButtons;
    if (!loading) {
        formButtons = <C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)} />;
    }

    return loading && inputData ? (
        <MatxLoading />
    ) : (
        <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[{ name: bcLabel }]}
                title={bcLabel}
                titleStatus={inputData?.accnStatus}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                snackBarOptions={snackBarOptions}
                isLoading={loading}            >
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
                                    scrollButtons="auto" >
                                    {tabList && tabList.map((item, ind) => {
                                        return (
                                            // <Tab
                                            //     className="capitalize"
                                            //     value={ind}
                                            //     label={item.text}
                                            //     key={ind}
                                            //     icon={item.icon} />
                                            <TabsWrapper
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

                                {tabIndex === 0 && (
                                    <CompanyDetails
                                        inputData={inputData}
                                        handleInputChange={handleInputChange2}
                                        handleDateChange={handleDateChange}
                                        viewType={viewType}
                                        isDisabled={isDisabled}
                                        errors={!validationErrors ? props.errors : validationErrors}
                                        locale={t}
                                    />
                                )}

                                {tabIndex === 1 && (
                                            <UserDetail
                                                inputData={inputData}
                                                errors={validationErrors}
                                                handleDateChange={handleDateChange}
                                                handleInputChange={handleInputChange2}
                                                viewType={viewType}
                                                //handleAutoComplete={handleAutoComplete}
                                                locale={t}
                                            />
                                )}

                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>

        </React.Fragment>
    );
};

export default withErrorHandler(AccountProfileFormDetails);
