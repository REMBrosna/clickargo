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
import PaymentTypeDetails from "./PaymentTypeDetails";
import ConfirmationDialog from "../../../../../matx/components/ConfirmationDialog";

const PaymentTypeFormDetails = () => {

    let { viewType, ptCode } = useParams();

    const { t } = useTranslation(["masters", "common"]);

    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [errors, setErrors] = useState({});

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        ptCode: "",
        ptName: "",
        ptDesc: ""
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
            sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/" + ptCode, "getPaymentType", "get", {});
        }

        // eslint-disable-next-line
    }, [ptCode, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getPaymentType":
                    break;
                case "savePaymentType":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("masters:paymentType.msg.successCreate"),
                        redirectPath: "/master/paymentType/list",
                    });
                    break;
                case "updatePaymentType":
                case "deActive":
                case "active":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("masters:paymentType.msg.successChange"),
                        redirectPath: "/master/paymentType/list",
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
                sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType", "savePaymentType", "post", {
                    ...inputData,
                    ptStatus: "A",
                });
                break;

            case "edit":
                sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/" + ptCode, "updatePaymentType", "put", {
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
            if (!inputData.ptCode) errors.ptCode = t("masters:paymentType.validate.required");
        }

        if (!inputData.ptName) {
            errors.ptName = t("masters:paymentType.validate.required");
        }

        if (!inputData.ptDesc) {
            errors.ptDesc = t("masters:paymentType.validate.required");
        }
        return errors;
    };

    const handleActiveChange = (e) => {
        setTimeout(
            () =>
                sendRequest(
                    "/api/co/pedi/mst/entity/pediMstPaymentType/" + ptCode + "/activate",
                    "active",
                    "put",
                    inputData
                ),
            1000
        );
    };

    const handleDeActiveChange = (e) => {
        setTimeout(
            () => sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/" + ptCode, "deActive", "delete", {}),
            1000
        );
    };

    const handleConfirmAction = () => {
        if (action === "delete") {
            setTimeout(
                () => sendRequest("/api/co/pedi/mst/entity/pediMstPaymentType/" + ptCode, "deActive", "delete", {}),
                1000
            );
        } else if (action === "active") {
            setTimeout(
                () =>
                    sendRequest(
                        "/api/co/pedi/mst/entity/pediMstPaymentType/" + ptCode + "/activate",
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

    let bcLabel = t("masters:paymentType.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case "view":
                bcLabel = t("masters:paymentType.details.breadCrumbs.sub.view");
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
                bcLabel = t("masters:paymentType.details.breadCrumbs.sub.edit");
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
                bcLabel = t("masters:paymentType.details.breadCrumbs.sub.new");
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
                    { name: t("masters:paymentType.details.breadCrumbs.main"), path: "/master/pediMstPaymentType/list" },
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
                                    <PaymentTypeDetails
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
                                        dtCreated={inputData.ptDtCreate}
                                        usrCreated={inputData.ptUidCreate}
                                        dtLupd={inputData.ptDtLupd}
                                        usrLupd={inputData.ptUidLupd}
                                    />
                                )}
                                {tabIndex === 2 && <C1AuditTab filterId={ptCode} />}
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

export default withErrorHandler(PaymentTypeFormDetails);
