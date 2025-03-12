import React, { useState, useEffect } from "react";
import { Grid, Paper, Tabs, Tab, Divider } from "@material-ui/core";

import { useParams, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";

import { commonTabs, Applications } from "app/c1utils/const";
import { CAN_NOTIFICATION_TEMPLATE_URL } from "app/c1utils/const";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";

import NotificationTemplateDetails from "./NotificationTemplateDetails";

const NotificationTemplateFormDetails = () => {
    let { viewType, ntplId } = useParams();

    const { t } = useTranslation(["masters", "common"]);

    let history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);

    const [inputData, setInputData] = useState({
        id: { ntplId: "", ntplAppscode: Applications.CPEDI.code },
        ntplSeq: 0,
        ntplDesc: "",
        ntplSubject: "",
        TCoreApps: {
            appsCode: Applications.CPEDI.code,
            appsDesc: Applications.CPEDI.desc,
        },
        TCoreNotificationChannelType: {
            nchntypeId: "",
            nchntypeDesc: "",
        },
        TCoreNotificationContentType: {
            ncnttypeId: "",
            ncnttypeDesc: "",
        },
        ntplTempalte: "",
    });

    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== "new") {
            sendRequest(
                `${CAN_NOTIFICATION_TEMPLATE_URL}/${ntplId}:${Applications.CPEDI.code}`,
                "getNotifTemplate",
                "get",
                {}
            );
        }
     // eslint-disable-next-line
    }, [ntplId, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "getNotifTemplate":
                    break;
                case "saveNotifTemplate":
                case "updateNotifTemplate":
                    setSubmitSuccess(true);
                    break;
                default:
                    break;
            }

            setInputData({ ...inputData, ...res.data });

            if (validation) {
                console.log("validation in useEffect....", validation);
                //setValidationErrors({ ...validation });
            }
        } else if (error) {
            setLoading(false);
            setSubmitError(false);
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case "new":
                sendRequest(`${CAN_NOTIFICATION_TEMPLATE_URL}/`, "saveNotifTemplate", "post", {
                    ...inputData,
                    ntplStatus: "A",
                });
                break;

            case "edit":
                sendRequest(
                    `${CAN_NOTIFICATION_TEMPLATE_URL}/${ntplId}:${Applications.CPEDI.code}`,
                    "updateNotifTemplate",
                    "put",
                    {
                        ...inputData,
                    }
                );
                break;
            default:
                break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (viewType === "new") {
            if (!inputData.id.ntplId) errors.ntplId = t("masters:contract.suppDocs.required");
        }

        if (!inputData.ntplSeq) {
            errors.TCoreApps = t("masters:contract.suppDocs.required");
        }

        if (!inputData.TCoreNotificationChannelType.nchntypeId) {
            errors.TCoreNotificationChannelType = t("masters:contract.suppDocs.required");
        }

        if (!inputData.TCoreNotificationContentType.ncnttypeId) {
            errors.TCoreNotificationContentType = t("masters:contract.suppDocs.required");
        }

        if (!inputData.ntplDesc) {
            errors.ntplDesc = t("masters:contract.suppDocs.required");
        }

        if (!inputData.ntplSubject) {
            errors.ntplSubject = t("masters:contract.suppDocs.required");
        }

        if (!inputData.ntplTempalte) {
            errors.ntplTempalte = t("masters:contract.suppDocs.required");
        }

        return errors;
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };

    let formButtons = (
        <C1FormButtons
            options={{
                back: {
                    show: true,
                    eventHandler: () => history.goBack(),
                },
                submit: true,
            }}
        />
    );

    let bcLabel = t("masters:notificationTemplate.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case "view":
                bcLabel = t("masters:notificationTemplate.details.breadCrumbs.sub.view");
                formButtons = (
                    <C1FormButtons
                        options={{
                            back: { show: true, eventHandler: () => history.goBack() },
                        }}
                    />
                );
                break;
            case "new":
                bcLabel = t("masters:notificationTemplate.details.breadCrumbs.sub.new");
                break;
            default:
                break;
        }
    }

    return loading ? (
        <MatxLoading />
    ) : (
        <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    {
                        name: t("masters:notificationTemplate.details.breadCrumbs.main"),
                        path: "/notification/templates/list",
                    },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess,
                    error: isSubmitError,
                    redirectPath: "/notification/templates/list",
                }}
                isLoading={loading}
            >
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper className="p-3">
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    indicatorColor="primary"
                                    textColor="primary"
                                >
                                    {commonTabs.map((item, ind) => (
                                        <Tab
                                            className="capitalize"
                                            value={ind}
                                            label={t(item.text)}
                                            key={ind}
                                            icon={item.icon}
                                        />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && (
                                    <NotificationTemplateDetails
                                        inputData={inputData}
                                        handleInputChange={handleInputChange}
                                        viewType={viewType}
                                        isSubmitting={loading}
                                        errors={props.errors}
                                        locale={t}
                                    />
                                )}
                                {tabIndex === 1 && (
                                    <C1Propertiestab
                                        dtCreated={inputData.ntplDtCreate}
                                        usrCreated={inputData.ntplUidCreate}
                                        dtLupd={inputData.ntplDtLupd}
                                        usrLupd={inputData.ntplUidLupd}
                                    />
                                )}
                                {tabIndex === 2 && <C1AuditTab filterId={ntplId} />}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};

export default withErrorHandler(NotificationTemplateFormDetails);
