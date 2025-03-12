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
import AttachTypeDetails from "./AttachTypeDetails";
import { hasWhiteSpace } from "app/c1utils/utility";

const AttachTypeFormDetails = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, id } = useParams();

    const { t } = useTranslation(['masters', 'common']);
    let history = useHistory();

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);
    const [errors, setErrors] = useState({});

    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({
        mattId: '',
        mattName: '',
        mattDesc: '',
        mattDescOth: '',
        mattStatus: 'A',
    });


    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest("/api/co/pedi/mst/entity/attType/" + id, "getAttach", "get", {});
        }
        // eslint-disable-next-line
    }, [id, viewType]);

    //executed when there are changes in the parameters
    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);

            switch (urlId) {
                case "getAttach":
                    break;
                case "saveAttach":
                case "updateAttach":
                case "deActive":
                case "active":
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

        if (validation) {
            setErrors({ ...validation });
            setLoading(false);
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
                sendRequest("/api/co/pedi/mst/entity/attType", "saveAttach", "post", { ...inputData });
                break;

            case 'edit':
                setLoading(true);
                sendRequest("/api/co/pedi/mst/entity/attType/" + id, "updateAttach", "put", { ...inputData });
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const { mattId, mattName, mattDesc, mattDescOth } = inputData;
        const errors = {};

        if (!mattId || mattId === "") {
            errors.mattId = t("masters:attachType.field.required");
        } else if (hasWhiteSpace(mattId)) {
            errors.mattId = t("masters:attachType.field.hasWhitespace");
        } else if (mattId.length > 35) {
            errors.mattId = t("masters:attachType.field.overLength", { max: 35 });
        }

        if (!mattName || mattName === "") {
            errors.mattName = t("masters:attachType.field.required");
        } else if (mattName.length > 128) {
            errors.mattName = t("masters:attachType.field.overLength", { max: 128 });
        }

        if (!mattDesc || mattDesc === "") {
            errors.mattDesc = t("masters:attachType.field.required");
        } else if (mattDesc.length > 255) {
            errors.mattDesc = t("masters:attachType.field.overLength", { max: 255 });
        }

        if (mattDescOth?.length > 512) {
            errors.mattDescOth = t("masters:attachType.field.overLength", { max: 512 });
        }

        return errors;
    }

    const handleInputChange = (e) => {
        console.log("target", e);
        if (e.target.name === 'mattExpiry' || e.target.name === 'mattRefNo') {
            setInputData({ ...inputData, [e.target.name]: e.target.checked ? 'Y' : 'N' });
        } else {
            setInputData({ ...inputData, [e.currentTarget.name]: e.currentTarget.value });
        }

    };

    const handleActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/attType/" + id + "/activate", "active", "put", inputData), 1000);
    }

    const handleDeActiveChange = (e) => {
        setTimeout(() => sendRequest("/api/co/pedi/mst/entity/attType/" + id, "deActive", "delete", {}), 1000);
    }

    let formButtons = <C1FormButtons options={{
        back: {
            show: true,
            eventHandler: () => history.goBack()
        },
        submit: true,
    }} />;

    let bcLabel = t("masters:attachType.details.breadCrumbs.sub.edit");
    if (viewType) {
        switch (viewType) {
            case 'view':
                bcLabel = t("masters:attachType.details.breadCrumbs.sub.view");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true, eventHandler: () => history.push("/master/attachType/list")
                    },
                    activate: {
                        show: inputData.mattStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                }} />;
                break;
            case 'edit':
                bcLabel = t("masters:attachType.details.breadCrumbs.sub.edit");
                formButtons = <C1FormButtons options={{
                    back: {
                        show: true, eventHandler: () => history.push("/master/attachType/list")
                    },
                    activate: {
                        show: inputData.mattStatus === 'I',
                        eventHandler: () => handleActiveChange()
                    },
                    deactivate: {
                        show: inputData.mattStatus === 'A',
                        eventHandler: () => handleDeActiveChange()
                    },
                    submit: true
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
                noValidate
                breadcrumbs={[
                    { name: t("masters:attachType.details.breadCrumbs.main"), path: "/master/attachType/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={{
                    success: isSubmitSuccess, error: isSubmitError, redirectPath: "/master/attachType/list"
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

                                {tabIndex === 0 && <AttachTypeDetails
                                    handleInputChange={handleInputChange}
                                    inputData={inputData}
                                    viewType={viewType}
                                    isSubmitting={loading}
                                    errors={{ ...props.errors, ...errors }}
                                    locale={t} />}
                                {tabIndex === 1 && <C1Propertiestab dtCreated={inputData.mattDtCreate} usrCreated={inputData.mattUidCreate}
                                    dtLupd={inputData.mattDtLupd} usrLupd={inputData.mattUidLupd} />}
                                {tabIndex === 2 && <C1AuditTab filterId={inputData.mattId} />}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(AttachTypeFormDetails);