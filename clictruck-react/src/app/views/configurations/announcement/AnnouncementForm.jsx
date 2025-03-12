import { MatxLoading } from "../../../../matx";
import React, { useEffect, useState } from "react";
import C1FormDetailsPanel from "../../../c1component/C1FormDetailsPanel";
import { Divider, Grid, Paper, Tab, Tabs } from "@material-ui/core";
import { commonTabs } from "../../../c1utils/const";

import C1Propertiestab from "../../../c1component/C1PropertiesTab";
import C1AuditTab from "../../../c1component/C1AuditTab";
import { useHistory, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import useHttp from "../../../c1hooks/http";
import C1FormButtons from "app/c1component/C1FormButtons";
import AnnouncementDetail from "./AnnouncementDetail";


const AnnouncementForm = () => {

    let { viewType, id } = useParams();
    const { t } = useTranslation(['configuration', 'common']);
    let history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        canuId: '',
        canuDescription: '',
        TCoreApps: null,
        TMstAnnouncementType: null,
        canuDtFrom: null,
        canuDtEnd: null,
        canuStatus: 'A',
        canuDtDisplay: null,
        canuSubject: '',
        // CPEDI-107 [Announcement Setup] Request to capture Private as default value  
        canuPublic: 'N',
        canuUrl: '',
    });

    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/anncmt/entity/anncmt/" + id, "get", "get", {});
        }
        if (viewType === 'new') {
            setInputData({ ...inputData, canuId: `${new Date().getTime()}` })
        }
        // eslint-disable-next-line
    }, [id, viewType]);

    //executed when there are changes in the parameters
    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);

            switch (urlId) {
                case "get":
                    break;
                case "save":
                case "update":
                    setSubmitSuccess(true);
                    break;
                default: break;
            }
            setInputData({ ...inputData, ...res.data });

        } else if (error) {
            setLoading(false);
            setSubmitError(false);
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);


    const handleStatusChange = (action) => {
        setLoading(true);
        sendRequest("/api/co/anncmt/entity/anncmt/" + id, "update", "put", { ...inputData, canuStatus: 'A' });
    }

    const handleSubmit = () => {
        setLoading(true);
        switch (viewType) {
            case 'new':
                sendRequest("/api/co/anncmt/entity/anncmt/", "save", "post", { ...inputData });
                break;
            case 'edit':
                setLoading(true);
                sendRequest("/api/co/anncmt/entity/anncmt/" + id, "update", "put", { ...inputData });
                break;
            default: break;
        }
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        activate: {
            show: inputData.canuStatus === 'I',
            eventHandler: () => handleStatusChange("activate")
        },
        submit: true,
    }} />

    let bcLabel = t("configuration:announcement.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("configuration:announcement.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() }
                }} />;
                break;
            case 'new':
                bcLabel = t("configuration:announcement.details.breadCrumbs.sub.new");
                break;
            default: break;
        }
    }
    const handleValidate = () => {
        const errors = {};
        if (!inputData.canuDtFrom)
            errors.canuDtFrom = t("configuration:notificationPreferences.messages.required");
        if (!inputData.canuDtEnd)
            errors.canuDtEnd = t("configuration:notificationPreferences.messages.required")
        if (!inputData.canuDtDisplay)
            errors.canuDtDisplay = t("configuration:notificationPreferences.messages.required")
        if (!inputData.canuSubject)
            errors.canuSubject = t("configuration:notificationPreferences.messages.required")
        if (!inputData.canuContent)
            errors.canuContent = t("configuration:notificationPreferences.messages.required")
        return errors;
    }

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    }

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        console.log("handleInputChange =", `${e.target.name}, ${e.target.value}`);
        if (name === "appsCode") {
            setInputData({ ...inputData, TCoreApps: { [name]: value } });
        } else if (name === "anypId") {
            setInputData({ ...inputData, TMstAnnouncementType: { [name]: value } });
        } else {
            setInputData({ ...inputData, [e.target.name]: e.target.value });
        }
    }

    const handleDateChange = (name, e) => {
        console.log("handleDateChange =", name, e);
        setInputData({ ...inputData, [name]: e });
    }

    const handleChangeSwitch = (event) => {
        setInputData({ ...inputData, [event.target.name]: event.target.checked ? 'Y' : 'N' });
    };

    console.log("============", inputData)
    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("configuration:announcement.details.breadCrumbs.main"), path: "/configuration/announcement/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/configuration/announcement/list"
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

                                {tabIndex === 0 && <AnnouncementDetail
                                    handleInputChange={handleInputChange}
                                    inputData={inputData}
                                    handleChangeSwitch={handleChangeSwitch}
                                    handleDateChange={handleDateChange}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={props.errors}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.canuDtCreate} usrCreated={inputData.canuUidCreate}
                                    dtLupd={inputData.canuDtLupd} usrLupd={inputData.canuUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab filterId={inputData.canuId} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
}

export default AnnouncementForm;