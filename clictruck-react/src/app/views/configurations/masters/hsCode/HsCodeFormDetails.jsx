import React, { useState, useEffect } from "react";
import {
    Grid,
    Paper,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";

import { useParams, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";

import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";

import { commonTabs } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import useHttp from "app/c1hooks/http";

import HsCodeDetails from "./HsCodeDetails";

const HsCodeFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, hsCode } = useParams();

    const { t } = useTranslation(['masters', 'common']);

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
        hsCode: "",
        hsDescription: "",
        hsDutyType: "",
        hsStatus: "A",
        hsExciseDutyRate: 0,
        hsCustomsDutyRate: 0,
        hsImpControlled: "",
        hsExpControlled: "",
        TMstHsType: {
            hstypeCode: "",
            hstypeDescription: ""
        },
        TMstUomByHsUom: {
            uomCode: "",
            uomDescription: ""
        },
        TMstUomByHsExciseDutyUom: {
            uomCode: "",
            uomDescription: ""
        },
        TMstUomByHsCustomsDutyUom: {
            uomCode: "",
            uomDescription: ""
        }
    });

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/pedi/mst/entity/hsCode/" + hsCode, "getHsCode", "get", {});
        }
        // eslint-disable-next-line
    }, [hsCode, viewType]);


    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getHsCode":
                    break;
                case "saveHsCode":
                case "updateHsCode":
                case "deActive":
                case "active":
                    setSubmitSuccess(true);
                    break;
                default: break;
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
            case 'new':
                sendRequest("/api/co/pedi/mst/entity/hsCode", "saveHsCode", "post", { ...inputData });
                break;

            case 'edit':
                sendRequest("/api/co/pedi/mst/entity/hsCode/" + hsCode, "updateHsCode", "put", { ...inputData });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (viewType === 'new') {
            if (!inputData.hsCode)
                errors.hsCode = t("masters:contract.suppDocs.required");
        }

        if (!inputData.hsDescription) {
            errors.hsDescription = t("masters:contract.suppDocs.required")
        }

        return errors;
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };

    const handleInputChangeSwitch = (e) => {
        setInputData({ ...inputData, [e.currentTarget.name]: (e.currentTarget.checked) ? 'Y' : 'N' });
    };

    const handleActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/hsCode/" + hsCode + "/activate", "active", "put", inputData), 1000);
    }

    const handleDeActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/hsCode/" + hsCode, "deActive", "delete", {}), 1000);
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true
    }} />;

    let bcLabel = t("masters:hsCode.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:hsCode.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() },
                    activate: {
                        show: inputData.hsStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    }
                }} />;
                break;
            case 'edit':
                bcLabel = t("masters:hsCode.details.breadCrumbs.sub.edit");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() },
                    activate: {
                        show: inputData.hsStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                    deactivate: {
                        show: inputData.hsStatus === 'A',
                        eventHandler: () => handleDeActiveChange()
                    },
                    submit: true
                }} />;
                break;
            case 'new':
                bcLabel = t("masters:hsCode.details.breadCrumbs.sub.new");
                break;
            default: break;
        }
    }


    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("masters:hsCode.details.breadCrumbs.main"), path: "/master/hsCode/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/hsCode/list"
                }}
                isLoading={loading} >
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper>
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    indicatorColor="primary"
                                    textColor="primary">
                                    {commonTabs.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <HsCodeDetails inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    handleInputChangeSwitch={handleInputChangeSwitch}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={{...props.errors, ...errors}}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.hsDtCreate} usrCreated={inputData.hsUidCreate}
                                    dtLupd={inputData.hsDtLupd} usrLupd={inputData.hsUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab filterId={hsCode} />}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};

export default withErrorHandler(HsCodeFormDetails);