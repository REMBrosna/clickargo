import React, { useState, useEffect } from "react";
import { Prompt } from "react-router";
import { Grid, Paper, Divider } from "@material-ui/core";

import { useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import {
    Applications,
    PEDI_NOTIF_PREF_URL,
    PEDI_NOTIF_PREF_APPTYPES_URL,
    CAN_NOTIFICATION_TEMPLATE_URL,
} from "app/c1utils/const";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";

import NotificationPreferenceDetails from "./NotificationPreferenceDetails";

const NotificationPreferenceForm = () => {
    const { t } = useTranslation(["configuration", "common"]);

    let history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const defaultSnackbarValue = {
        success: false,
        successMsg: null,
        error: false,
        errorMsg: null,
        redirectPath: null,
    };

    // eslint-disable-next-line
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    // const [errors, setErrors] = useState({});
    // eslint-disable-next-line
    const [isSubmitError, setSubmitError] = useState(false);
    const [loading, setLoading] = useState(false);
    const [isSelected, setSelected] = useState(false);
    const [isTemplateDataChanged, setTemplateDataChanged] = useState(false);
    const [snackbarOptions, setSnackbarOptions] = useState(defaultSnackbarValue);
    const [openPopupAction, setOpenPopupAction] = useState(false);

    const defaultValues = {
        sypfAppType: "",
        sypfAction: "",
        emailTemplate: {
            ntplSubject: "",
            ntplTempalte: "",
        },
        smsTemplate: {
            ntplSubject: "",
            ntplTempalte: "",
        },
        sypfTlgmReq: "",
        telegramTemplate: {
            ntplSubject: "",
            ntplTempalte: "",
        },
    };

    const [inputData, setInputData] = useState({
        sypfId: "",
        sypfAppType: "",
        sypfAction: "",
        sypfEmailReq: "",
        emailTemplate: {
            id: { ntplId: "", ntplAppscode: Applications.CPEDI.code },
            ntplSubject: "",
            ntplTempalte: "",
            ntplSeq: 0,
            ntplDesc: "",
            ntplStatus: "",
        },
        sypfSmsReq: "",
        smsTemplate: {
            id: { ntplId: "", ntplAppscode: Applications.CPEDI.code },
            ntplSubject: "",
            ntplTempalte: "",
            ntplSeq: 0,
            ntplDesc: "",
            ntplStatus: "",
        },
        sypfTlgmReq: "",
        telegramTemplate: {
            id: { ntplId: "", ntplAppscode: Applications.CPEDI.code },
            ntplSubject: "",
            ntplTempalte: "",
            ntplSeq: 0,
            ntplDesc: "",
            ntplStatus: "",
        },
    });

    useEffect(() => {
        setLoading(false);
        setSelected(false);
        setSubmitSuccess(false);
        setTemplateDataChanged(false);
        sendRequest(`${PEDI_NOTIF_PREF_APPTYPES_URL}`, "getNotifAppTypes", "get", {});
    // eslint-disable-next-line
    }, []);

    useEffect(() => {
        setSubmitSuccess(false);
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "getNotifPrefs":
                    setInputData({ ...inputData, ...res.data });
                    setSelected(true);
                    setTemplateDataChanged(false);
                    setSnackbarOptions({
                        success: true,
                        successMsg: t("configuration:notificationPreferences.messages.selectSuccess"),
                    });
                    break;
                case "updateNotifTemplate":
                    setSubmitSuccess(true);
                    setSelected(false);
                    setTemplateDataChanged(false);
                    setSnackbarOptions({
                        success: true,
                        successMsg: t("configuration:notificationPreferences.messages.saveSuccess"),
                    });
                    break;
                default:
                    break;
            }

            if (error) {
                //goes back to the screen
                setLoading(false);
                setSubmitError(false);
            }

            if (validation) {
                setLoading(false);
            }
        }
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const fetchTemplateSettings = () => {
        if (inputData.sypfAction && inputData.sypfAppType) {
            if (!isLoading && !error && res) {
                setLoading(false);
                setSubmitSuccess(false);
                console.log(`URL:   ${PEDI_NOTIF_PREF_URL}/${inputData.sypfAppType}/${inputData.sypfAction}`);
                sendRequest(
                    `${PEDI_NOTIF_PREF_URL}/${inputData.sypfAppType}/${inputData.sypfAction}`,
                    "getNotifPrefs",
                    "get",
                    {}
                );
            }
        } else {
            setLoading(false);
            setSnackbarOptions({
                error: true,
                errorMsg: t("configuration:notificationPreferences.messages.noAppTypeAction"),
            });
            setSelected(false);
            setInputData({ ...inputData, ...defaultValues });
            return;
        }
    };

    const handleSubmit = async () => {
        if (!isSelected) {
            setLoading(false);
            setSnackbarOptions({
                error: true,
                errorMsg: t("configuration:notificationPreferences.messages.noSelectedError"),
            });
            setInputData({ ...inputData, ...defaultValues });
            return;
        }

        if (inputData.sypfEmailReq === "Y") {
            sendRequest(
                `${CAN_NOTIFICATION_TEMPLATE_URL}/${inputData.emailTemplate.id.ntplId}:${Applications.CPEDI.code}`,
                "updateNotifTemplate",
                "put",
                {
                    ...inputData.emailTemplate,
                }
            );
        }

        if (inputData.sypfSmsReq === "Y") {
            sendRequest(
                `${CAN_NOTIFICATION_TEMPLATE_URL}/${inputData.smsTemplate.id.ntplId}:${Applications.CPEDI.code}`,
                "updateNotifTemplate",
                "put",
                {
                    ...inputData.smsTemplate,
                }
            );
        }

        if (inputData.sypfTlgmReq === "Y") {
            sendRequest(
                `${CAN_NOTIFICATION_TEMPLATE_URL}/${inputData.telegramTemplate.id.ntplId}:${Applications.CPEDI.code}`,
                "updateNotifTemplate",
                "put",
                {
                    ...inputData.telegramTemplate,
                }
            );
        }
        setInputData({ ...inputData, ...defaultValues });
        setSelected(false);
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };

    const handleTemplateDataChange = (e) => {
        handleInputChange(e);
        setTemplateDataChanged(true);
    };

    const initBeforeUnLoad = (isTemplateDataChanged) => {
        window.onbeforeunload = (event) => {
            // Show prompt based on state
            if (isTemplateDataChanged) {
                const e = event || window.event;
                e.preventDefault();
                if (e) {
                    e.returnValue = "";
                }
                return "";
            }
        };
    };

    const exitOnClick = () => {
        setTemplateDataChanged(false);
        setTimeout(function () {
            console.log("Executed after 1 second");
        }, 1000);
        setOpenPopupAction(false);
        history.push("/");
    };

    const handleConfirmAction = (e) => {
        if (isTemplateDataChanged) {
            setOpenPopupAction(true);
        } else {
            exitOnClick();
        }
    };

    let bcLabel = t("configuration:notificationPreferences.details.breadCrumbs.sub.view");
    let formButtons;
    if (!loading) {
        formButtons = (
            <C1FormButtons
                options={{
                    back: {
                        show: true,
                        eventHandler: () => handleConfirmAction(),
                    },
                    /*save: {
                        show: true,
                        eventHandler: () => handleSubmit(),
                    },*/
                }}
            />
        );
    }

    return loading ? (
        <MatxLoading />
    ) : (
        <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[{ name: bcLabel }]}
                title={t("configuration:notificationPreferences.details.breadCrumbs.main")}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                snackBarOptions={snackbarOptions}
                isLoading={loading}
            >
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className="p-3">
                                <Divider className="mb-6" />
                                <NotificationPreferenceDetails
                                    inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleTemplateDataChange={handleTemplateDataChange}
                                    fetchTemplateSettings={fetchTemplateSettings}
                                    errors={props.errors}
                                    locale={t}
                                />
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>

            <ConfirmationDialog
                open={openPopupAction}
                title={t("common:confirmMsgs.confirm.title")}
                text={t("configuration:notificationPreferences.messages.confirmOnExit")}
                onYesClick={(e) => exitOnClick(e)}
                onConfirmDialogClose={() => setOpenPopupAction(false)}
            />

            <Prompt
                when={isTemplateDataChanged}
                message={t("configuration:notificationPreferences.messages.confirmOnExit")}
            />
        </React.Fragment>
    );
};

export default withErrorHandler(NotificationPreferenceForm);
