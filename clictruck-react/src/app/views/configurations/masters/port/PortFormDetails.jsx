import React, { useState, useEffect } from "react";
import { Grid, Paper, Tabs, Tab, Divider } from "@material-ui/core";

import { useParams, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import PortDetails from "./PortDetails";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";

import useHttp from "app/c1hooks/http";
import { commonTabs } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";

const PortFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, portCode } = useParams();

    const { t } = useTranslation(["masters", "common"]);

    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    //flag for errors in submit
    const [isSubmitError, setSubmitError] = useState(false);
    const [errors, setErrors] = useState({});

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        portCode: "",
        portDescription: "",
        portDescriptionOth: "",
        TMstCountry: {
            ctyCode: "",
            ctyDescription: "",
        },
        TMstPortType: {
            porttCode: "",
            porttDescription: "",
        },
    });

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== "new") {
            sendRequest("/api/co/pedi/mst/entity/port/" + portCode, "getPort", "get", {});
        }

        // eslint-disable-next-line
    }, [portCode, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getPort":
                    break;
                case "savePort":
                case "updatePort":
                case "deActive":
                case "active":
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
            //set loading to false to display back to the screen if error is encountered
            setLoading(false);
            //even though there is error, setting this to false to not display the snackbar
            setSubmitError(false);
        }

        if (validation) {
            setErrors({ ...validation });
            setLoading(false);
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
                sendRequest("/api/co/pedi/mst/entity/port", "savePort", "post", { ...inputData, portStatus: "A" });
                break;

            case "edit":
                sendRequest("/api/co/pedi/mst/entity/port/" + portCode, "updatePort", "put", { ...inputData });
                break;
            default:
                break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (viewType === "new") {
            if (!inputData.portCode) errors.portCode = t("common:validationMsgs.required");
            if (inputData.portCode && inputData.portCode.length > 5)
                errors.portCode = t("common:validationMsgs.lengthValidation", { length: 5 });
        }

        if (!inputData.portDescription) {
            errors.portDescription = t("common:validationMsgs.required");
        }

        if (!inputData.TMstCountry.ctyCode) {
            errors.TMstCountry = t("common:validationMsgs.required");
        }

        if (!inputData.TMstPortType.porttCode) {
            errors.TMstPortType = t("common:validationMsgs.required");
        }

        return errors;
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };

    const handleActiveChange = (e) => {
        setTimeout(
            () => sendRequest("/api/co/pedi/mst/entity/port/" + portCode + "/activate", "active", "put", inputData),
            1000
        );
    };

    const handleDeActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/port/" + portCode, "deActive", "delete", {}), 1000);
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

    let bcLabel = t("masters:ports.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case "view":
                bcLabel = t("masters:ports.details.breadCrumbs.sub.view");
                formButtons = (
                    <C1FormButtons
                        options={{
                            back: { show: true, eventHandler: () => history.goBack() },
                            activate: {
                                show: inputData.portStatus === "I",
                                eventHandler: () => handleActiveChange(),
                            },
                        }}
                    />
                );
                break;
            case "edit":
                bcLabel = t("masters:ports.details.breadCrumbs.sub.edit");
                formButtons = (
                    <C1FormButtons
                        options={{
                            back: { show: true, eventHandler: () => history.goBack() },
                            activate: {
                                show: inputData.portStatus === "I",
                                eventHandler: () => handleActiveChange(),
                            },
                            deactivate: {
                                show: inputData.portStatus === "A",
                                eventHandler: () => handleDeActiveChange(),
                            },
                            submit: true,
                        }}
                    />
                );
                break;
            case "new":
                bcLabel = t("masters:ports.details.breadCrumbs.sub.new");
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
                    { name: t("masters:ports.details.breadCrumbs.main"), path: "/master/port/list" },
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
                    redirectPath: "/master/port/list",
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
                                    <PortDetails
                                        inputData={inputData}
                                        handleInputChange={handleInputChange}
                                        viewType={viewType}
                                        isSubmitting={loading}
                                        errors={{...props.errors, ...errors}}
                                        locale={t}
                                    />
                                )}
                                {tabIndex === 1 && (
                                    <C1Propertiestab
                                        dtCreated={inputData.portDtCreate}
                                        usrCreated={inputData.portUidCreate}
                                        dtLupd={inputData.portDtLupd}
                                        usrLupd={inputData.portUidLupd}
                                    />
                                )}
                                {tabIndex === 2 && <C1AuditTab filterId={portCode} />}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};

export default withErrorHandler(PortFormDetails);
