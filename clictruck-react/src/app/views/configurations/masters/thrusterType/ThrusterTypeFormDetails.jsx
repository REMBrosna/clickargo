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
import { commonTabs } from "app/c1utils/const";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";
import ThrusterTypeDetails from "./ThrusterTypeDetails";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";

const NumberEngineFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, ttCode } = useParams();

    const { t } = useTranslation(["masters", "common"]);

    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [errors, setErrors] = useState({});

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        ttCode: "",
        ttName: "",
        ttDesc: ""
    });

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [openPopupAction, setOpenPopupAction] = useState(false);
    // eslint-disable-next-line
    const [action, setAction] = useState("");

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        // setSubmitSuccess(false);
        if (viewType !== "new") {
            sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/" + ttCode, "getProvince", "get", {});
        }

        // eslint-disable-next-line
    }, [ttCode, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getProvince":
                    break;
                case "saveProvince":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("masters:thrusterType.msg.successCreate"),
                        redirectPath: "/master/thrusterType/list",
                    });
                    break;
                case "updateProvince":
                case "deActive":
                case "active":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("masters:thrusterType.msg.successChange"),
                        redirectPath: "/master/thrusterType/list",
                    });
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

    const handleSubmit = async () => {
        setLoading(true);
        switch (viewType) {
            case "new":
                sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType", "saveProvince", "post", {
                    ...inputData,
                    ttStatus: "A",
                });
                break;

            case "edit":
                sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/" + ttCode, "updateProvince", "put", {
                    ...inputData,
                });
                break;
            default:
                break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (viewType === "new") {
            if (!inputData.ttCode) errors.ttCode = t("masters:thrusterType.validate.required");
        }

        if (!inputData.ttName) {
            errors.ttName = t("masters:thrusterType.validate.required");
        }

        if (!inputData.ttDesc) {
            errors.ttDesc = t("masters:thrusterType.validate.required");
        }
        return errors;
    };

    const handleActiveChange = (e) => {
        setTimeout(
            () =>
                sendRequest(
                    "/api/co/pedi/mst/entity/pediMstThrusterType/" + ttCode + "/activate",
                    "active",
                    "put",
                    inputData
                ),
            1000
        );
    };

    const handleDeActiveChange = (e) => {
        setTimeout(
            () => sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/" + ttCode, "deActive", "delete", {}),
            1000
        );
    };

    const handleConfirmAction = () => {
        if (action === "delete") {
            setTimeout(
                () => sendRequest("/api/co/pedi/mst/entity/pediMstThrusterType/" + ttCode, "deActive", "delete", {}),
                1000
            );
        } else if (action === "active") {
            setTimeout(
                () =>
                    sendRequest(
                        "/api/co/pedi/mst/entity/pediMstThrusterType/" + ttCode + "/activate",
                        "active",
                        "put",
                        inputData
                    ),
                1000
            );
        }
        setOpenPopupAction(false);
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

    let bcLabel = t("masters:thrusterType.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case "view":
                bcLabel = t("masters:thrusterType.details.breadCrumbs.sub.view");
                formButtons = (
                    <C1FormButtons
                        options={{
                            back: { show: true, eventHandler: () => history.goBack() },
                            activate: {
                                show: inputData.neStatus === "I",
                                eventHandler: () => handleActiveChange(),
                            },
                        }}
                    />
                );
                break;
            case "edit":
                bcLabel = t("masters:thrusterType.details.breadCrumbs.sub.edit");
                formButtons = (
                    <C1FormButtons
                        options={{
                            back: { show: true, eventHandler: () => history.goBack() },
                            activate: {
                                show: inputData.neStatus === "I",
                                eventHandler: () => handleActiveChange(),
                            },
                            deactivate: {
                                show: inputData.neStatus === "A",
                                eventHandler: () => handleDeActiveChange(),
                            },
                            submit: true,
                        }}
                    />
                );
                break;
            case "new":
                bcLabel = t("masters:thrusterType.details.breadCrumbs.sub.new");
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
                    { name: t("masters:thrusterType.details.breadCrumbs.main"), path: "/master/pediMstThrusterType/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={snackBarOptions}
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
                                    <ThrusterTypeDetails
                                        inputData={inputData}
                                        handleInputChange={handleInputChange}
                                        viewType={viewType}
                                        isSubmitting={loading}
                                        errors={{ ...props.errors, ...errors }}
                                        locale={t}
                                    />
                                )}
                                {tabIndex === 1 && (
                                    <C1Propertiestab
                                        dtCreated={inputData.ttDtCreate}
                                        usrCreated={inputData.ttUidCreate}
                                        dtLupd={inputData.ttDtLupd}
                                        usrLupd={inputData.ttUidLupd}
                                    />
                                )}
                                {tabIndex === 2 && <C1AuditTab filterId={ttCode} />}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>

            <ConfirmationDialog
                open={openPopupAction}
                onConfirmDialogClose={() => setOpenPopupAction(false)}
                text={t("common:confirmMsgs.confirm.content")}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={(e) => handleConfirmAction(e)}
            />
        </React.Fragment>
    );
};

export default withErrorHandler(NumberEngineFormDetails);
