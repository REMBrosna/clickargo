import {MatxLoading} from "../../../../matx";
import React, {useEffect, useState} from "react";
import C1FormDetailsPanel from "../../../c1component/C1FormDetailsPanel";
import {Divider, Grid, Paper, Tab, Tabs} from "@material-ui/core";
import {commonTabs} from "../../../c1utils/const";
import C1Propertiestab from "../../../c1component/C1PropertiesTab";
import C1AuditTab from "../../../c1component/C1AuditTab";
import {useHistory, useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import useHttp from "../../../c1hooks/http";
import C1FormButtons from "app/c1component/C1FormButtons";
import AnnouncementTypeDetail from "./AnnouncementTypeDetail";

const AnnouncementTypeForm = () => {

    let { viewType, id } = useParams();
    const { t } = useTranslation(['configuration', 'common']);
    let history = useHistory();

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        anypDescription: '',
        anypIconName: '',
        anypStatus: 'A'
    });

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: ""
    }
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/anncmt/entity/anncmtType/"+ id, "get", "get", {});
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
                case "deActive":
                case "active":
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("configuration:announcementType.messages.updateSuccess"),
                        redirectPath: "/configuration/announcementType/list"
                    });
                    break;
                default: break;
            }
            setInputData({ ...inputData, ...res.data });

        } else if (error) {
            setLoading(false);
            setSubmitError(false);
            setSnackBarOptions({
                ...snackBarOptions,
                success: false,
                error: true,
                errorMsg: t("configuration:announcementType.messages.failedSubmit")
            });
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    const handleActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/anncmt/entity/anncmtType/" + id, "active", "put", { ...inputData, anypStatus: 'A' }), 1000);
    }

    const handleDeActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/anncmt/entity/anncmtType/" + id, "deActive", "put", { ...inputData, anypStatus: 'I' }), 1000);
    }

    const handleSubmit = () => {
        setLoading(true);
        switch (viewType) {
            case 'new':
                sendRequest("/api/co/anncmt/entity/anncmtType/", "save", "post", { ...inputData });
                break;
            case 'edit':
                setLoading(true);
                sendRequest("/api/co/anncmt/entity/anncmtType/" + id, "update", "put", { ...inputData });
                break;
            default: break;
        }
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true
    }}/>

    let bcLabel = t("configuration:announcementType.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("configuration:announcementType.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() },
                    activate: {
                        show: inputData.anypStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                }} />;
                break;
            case 'edit':
                bcLabel = t("configuration:announcementType.details.breadCrumbs.sub.edit");
                formButtons = <C1FormButtons options={{
                    back: { show: true, eventHandler: () => history.goBack() },
                    activate: {
                        show: inputData.anypStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                    deactivate: {
                        show: inputData.anypStatus === 'A',
                        eventHandler: () => handleDeActiveChange()
                    },
                    submit: true
                }} />;
                break;
            case 'new':
                bcLabel = t("configuration:announcementType.details.breadCrumbs.sub.new");
                break;
            default: break;
        }
    }
    const handleValidate = () => {
        return {};
    }

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    }

    const handleInputChange = (e) => {
        console.log(`${e.currentTarget.name}, ${e.currentTarget.value}`);
        setInputData({ ...inputData, [e.currentTarget.name]: e.currentTarget.value });
    }

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("configuration:announcementType.details.breadCrumbs.main"), path: "/configuration/announcementType/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={snackBarOptions}
                // snackBarOptions={{
                //     success: isSubmitSuccess, error: isSubmitError, redirectPath: "/configuration/announcementType/list"
                // }}
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

                                {tabIndex === 0 && <AnnouncementTypeDetail
                                    handleInputChange={handleInputChange}
                                    inputData={inputData}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={props.errors}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.anypDtCreate} usrCreated={inputData.anypUidCreate}
                                                                    dtLupd={inputData.anypDtLupd} usrLupd={inputData.anypUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab filterId={inputData.anypId} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
}

export default AnnouncementTypeForm;