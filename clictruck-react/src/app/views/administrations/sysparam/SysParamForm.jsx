import React, { useState, useEffect } from "react";
import { Grid, Paper, Tabs, Tab, Divider } from "@material-ui/core";

import { useParams, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ConfirmationDialog, MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";

import { commonTabs } from "app/c1utils/const";
import { COMMON_SYSPARAM_URL } from "app/c1utils/const";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";

import SysParamDetails from "./SysParamDetails";

const SysParamForm = () => {
    let { viewType, sysKey } = useParams();

    const { t } = useTranslation(["administration"]);

    let history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [tabIndex, setTabIndex] = useState(0);

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    
    // eslint-disable-next-line
    const [isSubmitError, setSubmitError] = useState(false);
    const [validationError, setValidationError] = useState();

    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });

    const [loading, setLoading] = useState(false);

    const [inputData, setInputData] = useState({
        sysKey: "",
        sysVal: "",
        sysDesc: "",
    });

    useEffect(() => {
        setLoading(false);
       
        if (viewType !== "new") {
            sendRequest(`${COMMON_SYSPARAM_URL}/${sysKey}`, "getSysParam", "get", {});
        }
        console.log("Form page inputData:", inputData);
    // eslint-disable-next-line
    }, [sysKey, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "getSysParam":
                    break;
                case "saveSysParam":
                case "updateSysParam":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.saveSuccess"),
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
            setLoading(false);
            setSubmitError(false);
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSave();
        } else {
            setOpenSubmitConfirm({ action: action, open: true });
        }
    };

    const handleSaveOnClick = () => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.saveConfirm') });
    }

    const handleSave = async (values) => {
        setLoading(true);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        let errors = handleValidate();
        if(errors){
            setLoading(false);
            setSnackBarOptions(defaultSnackbarValue);
            return;
        }
            

        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        switch (viewType) {
            case "new":
                sendRequest(`${COMMON_SYSPARAM_URL}/`, "saveSysParam", "post", {
                    ...inputData,
                    sysStatus: "A",
                });
                break;
            case "edit":
                sendRequest(`${COMMON_SYSPARAM_URL}/${sysKey}`, "updateSysParam", "put", {
                    ...inputData,
                });
                break;
            default:
                break;
        }
       
    };

    const handleValidate = () => {

        const errors = {};

        if (!inputData.sysVal) {
            errors.sysVal = "Required";
        }

        if (!inputData.sysDesc) {
            errors.sysDesc = "Required";
        }

        if(Object.keys(errors).length > 0){
            setValidationError(errors);
            return errors;
        } else{
            setValidationError(null);
            return null;
        }
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
                save: {
                    show: true,
                    eventHandler: handleSaveOnClick
                }
               
            }}
        />
    );

    let bcLabel = t("administration:sysParam.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case "view":
                bcLabel = t("administration:sysParam.details.breadCrumbs.sub.view");
                formButtons = (
                    <C1FormButtons
                        options={{
                            back: { show: true, eventHandler: () => history.goBack() },
                        }}
                    />
                );
                break;
            default:
                break;
        }
    }

    return loading ? <MatxLoading />
     :  <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    {
                        name: t("administration:sysParam.details.breadCrumbs.main"),
                        path: "/administrations/sysparam/list",
                    },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                snackBarOptions={{ ...snackBarOptions, redirectPath: "/administrations/sysparam/list" }}
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
                                    <SysParamDetails
                                        inputData={inputData}
                                        handleInputChange={handleInputChange}
                                        viewType={viewType}
                                        isSubmitting={loading}
                                        errors={validationError}
                                        locale={t}
                                    />
                                )}
                                {tabIndex === 1 && (
                                    <C1Propertiestab
                                        dtCreated={inputData.sysDtCreate}
                                        usrCreated={inputData.sysUidCreate}
                                        dtLupd={inputData.sysDtLupd}
                                        usrLupd={inputData.sysUidLupd}
                                    />
                                )}
                                {tabIndex === 2 && <C1AuditTab filterId={sysKey} />}
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
        </React.Fragment>;
};

export default withErrorHandler(SysParamForm);
