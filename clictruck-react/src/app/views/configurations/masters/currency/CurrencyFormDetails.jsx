import React, { useState, useEffect } from "react";
import {Grid, Paper, Tabs, Tab, Divider} from "@material-ui/core";
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
import CurrencyDetails from "./CurrencyDetails";

const CurrencyFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, ccyCode } = useParams();

    const { t } = useTranslation(['masters', 'common']);

    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});

    const [inputData, setInputData] = useState({
        ccyCode: '',
        ccyDescription: '',
        ccyStatus: 'A',
        ccyDtCreate: '',
        ccyUidCreate: '',
        ccyDtLupd: '',
        ccyUidLupd: ''
    });


    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/pedi/mst/entity/currency/" + ccyCode, "getCcy", "get", {});
        }

        // eslint-disable-next-line
    }, [ccyCode, viewType]);

    //executed when there are changes in the parameters
    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getCcy":
                    break;
                case "saveCcy":
                case "updateCcy":
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
                sendRequest("/api/co/pedi/mst/entity/currency", "saveCcy", "post", { ...inputData });
                break;

            case 'edit':
                setLoading(true);
                sendRequest("/api/co/pedi/mst/entity/currency/" + ccyCode, "updateCcy", "put", { ...inputData });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (viewType === 'new') {
            if (!inputData.ccyCode)
                errors.ccyCode = t("common:validationMsgs.required");
        }

        if (!inputData.ccyDescription) {
            errors.ccyDescription = t("common:validationMsgs.required");
        }

        return errors;
    }

    const handleActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/currency/"+ ccyCode +"/activate", "active", "put", inputData), 1000);
    }

    const handleDeActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/currency/"+ ccyCode, "deActive", "delete", {}), 1000);
    }

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.currentTarget.name]: e.currentTarget.value });
    };


    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true
    }} />;

    let bcLabel = t("masters:currency.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:currency.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true, eventHandler: () => history.goBack()
                    },
                    activate: {
                        show: inputData.ccyStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                }} />;
                break;
            case 'edit':
                bcLabel = t("masters:currency.details.breadCrumbs.sub.edit");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true, eventHandler: () => history.goBack()
                    },
                    activate: {
                        show: inputData.ccyStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                    deactivate: {
                        show: inputData.ccyStatus === 'A',
                        eventHandler: () => handleDeActiveChange()
                    },
                    submit: true
                }} />;
            case 'new':
                bcLabel = t("masters:currency.details.breadCrumbs.sub.new");
                break;
            default: break;
        }


    }

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("masters:currency.details.breadCrumbs.main"), path: "/master/currency/list" },
                    { name: bcLabel },
                ]}

                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/currency/list"
                }}
                isLoading={loading} >
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
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <CurrencyDetails
                                    data={inputData}
                                    handleInputChange={handleInputChange}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={{...props.errors, ...errors}}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.ccyDtCreate} usrCreated={inputData.ccyUidCreate}
                                    dtLupd={inputData.ccyDtLupd} usrLupd={inputData.ccyUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab
                                    filterId={inputData.ccyCode} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(CurrencyFormDetails);