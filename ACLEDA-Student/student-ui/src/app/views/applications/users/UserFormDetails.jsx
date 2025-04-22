import { Grid, Tabs } from "@material-ui/core";
import { Icon } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import { tabScroll } from "app/c1utils/styles";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";
import { TabsWrapper } from "../../../portedicomponent/TabsWrapper";
import C1AuditTab from "../../../c1component/C1AuditTab";
import useAuth from "../../../hooks/useAuth";
import _ from "lodash";
import UserDetail from "./UserDetail";

const useStyles = makeStyles((theme) => ({

    tabText: {
        marginLeft: '16px',
        marginTop: '-1px',
    },
    tabIcon: {
        position: 'absolute',
        marginLeft: '-5px',
    },
    valid: {
        color: '#F0DB72'
    },
    error: {
        color: 'red'
    }
}));

const UserFormDetails = () => {
    const { user } = useAuth();
    const classes = useStyles();
    const { t } = useTranslation(["register", "common","user"]);
    let { viewType, id } = useParams();
    const history = useHistory();
    const [loading, setLoading] = useState(false);
    const [isRefresh, setRefresh] = useState(false);
    const [inputData, setInputData] = useState({});
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [tabIndex, setTabIndex] = useState(0);
    const [errors, setErrors] = useState({});

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: ""
    }
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, [name]: e });
    }

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    //*************
    //capacityAndMachine
    const tabList = [
        { text: t("Student Details"), icon: <Icon>schedule</Icon>},
        { text: t("Audits"), icon: <Icon>schedule</Icon>, disabled: false},
    ];
    useEffect(() => {
        if(viewType !=='new'){
            sendRequest(`/api/v1/users/${id}`, 'doFetch', 'GET', null);
        }
        // eslint-disable-next-line
    }, [id, viewType]);
    useEffect(() => {
        setSnackBarOptions(defaultSnackbarValue);
        if (!isLoading && !error && res && !validation) {
            setLoading(false);
            switch (urlId) {
                case "doFetch":{
                    setInputData(res.data?.data);
                    break;
                }
                case "doCreate": {
                    setInputData(res.data);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: `Application Student has been created.`,
                        redirectPath: `/student/applicationStudent/list`
                    });
                    break;
                }
                case "doUpdate": {
                    setInputData(res.data);
                    if (res.data) {
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: `Application Student has been save!`,
                            redirectPath: `/student/applicationStudent/list/`
                        })
                    }
                    break;
                }
                default: break;
            }
            setRefresh(true);
        } else if (error) {
            console.log("UseEffect Error come.....")
            setErrors({...error})
        }
        if (validation) {
            setErrors(validation);
            setLoading(false);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, isFormSubmission, validation, error]);
    const handleSave = async (e) => {
        if (_.isEmpty(handleValidate())){
            setLoading(true);
            setErrors({});
            switch (viewType) {
                case 'new':
                    sendRequest(`api/v1/users`, "doCreate", "post", { ...inputData });
                    break;
                case 'edit':
                    sendRequest(`api/v1/users/${id}`, "doUpdate", "put", { ...inputData, id: id });
                    break;
                default: break;
            }
        }else {
            setErrors(handleValidate())
        }

    }
    const handleValidate = () => {
        const errors = {};
        console.log("errors", errors)

        if (!inputData.username || inputData.username.trim() === "") {
            errors.username = t("register:register.validation.usernameRequired");
        } else if (inputData.username.length < 3) {
            errors.username = t("register.validation.usernameTooShort");
        }

        if (!inputData.email || inputData.email.trim() === "") {
            errors.email = t("register:register.validation.emailRequired");
        } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(inputData.email)) {
            errors.email = t("register.validation.emailInvalid");
        }

        if (!inputData.firstname) {
            errors.firstname = t("register:register.validation.firstnameRequired");
        }

        if (!inputData.lastname) {
            errors.lastname = t("register:register.validation.lastnameRequired");
        }

        if (!inputData.gender) {
            errors.gender = t("register:register.validation.genderRequired");
        }

        if (!inputData.dtOfBirth) {
            errors.dtOfBirth = t("register:register.validation.dobRequired");
        }

        if (!/^[0-9]{9,15}$/.test(inputData.conNumber) && inputData.conNumber != '') {
            errors.conNumber = t("register:register.validation.contactInvalid");
        }

        if (!inputData.address) {
            errors.address = t("register:register.validation.addressRequired");
        }

        setErrors(errors);
        return errors;
    };

    const handleInputChange = (e, key) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });
    };

    let bcLabel = 'Application Student';
    if (viewType) {
        switch (viewType) {
            case 'view':
                break;
            case 'new':
                bcLabel = 'New ' + bcLabel;
                break;
            default: break;
        }
    }
    console.log("tabIndex", tabIndex)
    let formButtons = <C1FormButtons options={{
        save: { show: viewType === 'new' || viewType === 'edit', eventHandler: (e) => handleSave(e) },
        // submitOnClick: { show:  viewType === 'edit',  eventHandler: handleSubmit},
        back: { show: true, eventHandler: () => history.push("/student/applicationStudent/list") }
    }}>



    </C1FormButtons>
    return (
        loading ? <MatxLoading /> :
            <React.Fragment>
                <C1FormDetailsPanel
                    //isForm="true"
                    breadcrumbs={[
                        { name: "Application Student ", path: "/student/applicationStudent/list" },
                        { name: bcLabel },
                    ]}
                    title={bcLabel}
                    formButtons={formButtons}
                    snackBarOptions={snackBarOptions}
                    // onSubmit={(values, actions) => handleSubmit(values, actions)}
                    formInitialValues={{ ...inputData }}
                    formValues={{ ...inputData }}
                    onValidate={handleValidate}
                    isLoading={loading}
                >
                    {() => (
                        <Grid container spacing={3}>
                            <Grid item xs={2}>
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    orientation="vertical"
                                    variant="scrollable"
                                    scrollButtons="auto"
                                    indicatorColor="primary"
                                    textColor="primary">
                                    {tabList.map((item, ind) => (
                                        <TabsWrapper className="capitalize" value={ind} disabled={item.disabled} label={item.text} key={ind} icon={item.icon} {...tabScroll(ind)} />
                                    ))}
                                </Tabs>
                            </Grid>
                            <Grid item xs={10}>
                                {tabIndex === 0 && <UserDetail
                                    errors={errors}
                                    viewType={viewType}
                                    locale={t}
                                    inputData={inputData}
                                    handleDateChange={handleDateChange}
                                    handleInputChange={handleInputChange}
                                />}
                                {tabIndex === 1 && <C1AuditTab filterId={user?.username} />}
                            </Grid>
                        </Grid>
                    )}
                </C1FormDetailsPanel >
            </React.Fragment>
    );
};
export default withErrorHandler(UserFormDetails);