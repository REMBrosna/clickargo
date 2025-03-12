import React, { useState, useEffect } from "react";
import {
    Grid,
    Paper,
    Tabs,
    Tab,
    Divider
} from "@material-ui/core";
import { useParams, useHistory } from "react-router-dom";
import { MatxLoading } from "matx";
import { useTranslation } from "react-i18next";

import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1Propertiestab from "app/c1component/C1PropertiesTab";
import C1AuditTab from "app/c1component/C1AuditTab";

import { deepUpdateState } from "app/c1utils/stateUtils";
import { commonTabs } from "app/c1utils/const";
import useHttp from "app/c1hooks/http";

import AgencyDetails from "./AgencyDetails";

const AgencyFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, id } = useParams();

    const { t } = useTranslation(['masters', 'common']);

    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    //flag for errors in submit
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        agyCode: '',
        agyRegNo: '',
        agyDesc: '',
        agyDescOth: '',
        TCoreMinistry: {
            minCode: '',
            minDesc: ''
        }
    });

    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/ccm/entity/agency/" + id, "getAgency", "get", {});
        }
        // eslint-disable-next-line
    }, [id, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "getAgency":
                    break;
                case "saveAgency":
                case "updateAgency":
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

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleSubmit = async (values, actions) => {
        setLoading(true);

        switch (viewType) {
            case 'new':
                sendRequest("/api/co/ccm/entity/agency", "saveAgency", "post", { ...values, agyStatus: 'A' });
                break;

            case 'edit':
                sendRequest("/api/co/ccm/entity/agency/" + id, "updateAgency", "put", { ...values });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};
        return errors;
    }

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };


    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true
    }} />;

    let bcLabel = t("masters:agency.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:agency.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() }
                }} />;
                break;
            case 'new':
                bcLabel = t("masters:agency.details.breadCrumbs.sub.new");
                break;
            default: break;
        }
    }

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("masters:agency.details.breadCrumbs.main"), path: "/master/agency/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/agency/list"
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
                                    textColor="primary">
                                    {commonTabs.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={t(item.text)} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <AgencyDetails inputData={props.values}
                                    handleInputChange={handleInputChange}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={props.errors}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.agyDtCreate} usrCreated={inputData.agyUidCreate}
                                    dtLupd={inputData.agyDtLupd} usrLupd={inputData.agyUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab filterId={id} />}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(AgencyFormDetails);