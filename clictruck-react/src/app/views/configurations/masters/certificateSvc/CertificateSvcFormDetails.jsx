import {
    Divider, Grid,
    Paper, Tab, Tabs
} from "@material-ui/core";
import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import useHttp from "app/c1hooks/http";
import { commonTabs } from "app/c1utils/const";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import CertificateSvcDetails from "./CertificateSvcDetails";

const CertificateSvcFormDetails = () => {
    //useParams hook to acces the dynamic pieces of the URL
    let { viewType, id } = useParams();

    const { t } = useTranslation(['masters', 'common']);
    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        certsId: '',
        certsVal: '',
        certsModuleService: '',
        certsDecs: '',
    });


    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            // sendRequest("/api/co/master/entity/attType/" + id, "getService", "get", {});
            sendRequest("/api/co/pedi/mst/entity/pediMstCertificateSvc/" + id, "getService", "get", {});

        }
        // eslint-disable-next-line
    }, [id, viewType]);

    //executed when there are changes in the parameters
    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "getService":
                    break;
                case "saveService":
                case "updateService":
                    setSubmitSuccess(true);
                    break;
                default: break;
            }

            setInputData({ ...inputData, ...res.data });

            if (validation) {
                //setValidationErrors({ ...validation });
            }
        } else if (error) {
            //set loading to false to display back to the screen if error is encountered
            setLoading(false);
            //even though there is error, setting this to false to not display the snackbar
            setSubmitError(false);
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);


    //api request for the details here
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case 'new':
                sendRequest("/api/co/pedi/mst/entity/pediMstCertificateSvc/", "saveService", "post", { ...inputData });
                break;

            case 'edit':
                setLoading(true);
                sendRequest("/api/co/pedi/mst/entity/pediMstCertificateSvc/" + id, "updateService", "put", { ...inputData });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};

        if (!inputData.certsId)
            errors.certsId = t("masters:contract.suppDocs.required");

        if (!inputData.certsVal) {
            errors.certsVal = t("masters:contract.suppDocs.required")
        }
        if (!inputData.certsModuleService) {
            errors.certsModuleService = t("masters:contract.suppDocs.required")
        }
        return errors;
    }

    const handleInputChange = (e) => {
        console.log(`${e.currentTarget.name}, ${e.currentTarget.value}`);
        setInputData({ ...inputData, [e.currentTarget.name]: e.currentTarget.value });
    };

    const handleStatusChange = (action) => {
        setLoading(true);
        // sendRequest("/api/co/master/entity/attType/" + id, "updateAttach", "put", { ...inputData, mattStatus: 'A' });
        sendRequest("/api/co/pedi/mst/entity/pediMstCertificateSvc/" + id, "updateService", "put", { ...inputData, mattStatus: 'A' });
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        // submitOnClick: {
        //     show: true,
        //     eventHandler: () => handleSubmit()
        // },
        // activate: {
        //     show: inputData.mattStatus === 'I',
        //     eventHandler: () => handleStatusChange("activate")
        // },
        submit: true,
    }} />;

    let bcLabel = t("masters:certificateSvc.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:certificateSvc.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() }
                }} />;
                break;
            case 'new':
                bcLabel = t("masters:attachType.details.breadCrumbs.sub.new");
                break;
            default: break;
        }


    }

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("masters:certificateSvc.details.breadCrumbs.main"), path: "/master/certificateSvc/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/certificateSvc/list"
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

                                {tabIndex === 0 && <CertificateSvcDetails
                                    handleInputChange={handleInputChange}
                                    inputData={inputData}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={props.errors}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.certsDtCreate} usrCreated={inputData.certsUidCreate}
                                    dtLupd={inputData.certsDtUpdate} usrLupd={inputData.certsUidUpdate} />}
                                {tabIndex === 2 && <C1AuditTab filterId={inputData.certsId} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(CertificateSvcFormDetails);