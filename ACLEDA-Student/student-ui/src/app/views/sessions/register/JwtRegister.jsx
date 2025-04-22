import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import {Box, Card, Divider, Grid, Tabs, Snackbar, Paper, Tab} from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { useTranslation } from "react-i18next";
import { MatxLoading } from "matx";
import C1Alert from "app/c1component/C1Alert";
import C1FormButtons from "app/c1component/C1FormButtons";
import mainLogo from "app/MatxLayout/Layout2/mainLogo.png";
import {commonTabs, registerTabs} from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import useHttp from "app/c1hooks/http";
import UserRegisterDetail from "./UserRegisterDetail";
import _ from "lodash";
import clsx from "clsx";
import {Form, Formik} from "formik";
import {TabsWrapper} from "../../../portedicomponent/TabsWrapper";
import Footer from "../../../MatxLayout/SharedCompoents/Footer";

const useStyles = makeStyles(({ palette, ...theme }) => ({
    cardHolder: {
        background: "#3C77D0",
        height: 979,
    },

    card: {
        width: "1200",
        borderRadius: 12,
        margin: "5rem",
    },

    formData: {
        height: "500vh"
    },

    root: {
        backgroundColor: "#3C77D0",
        borderColor: palette.divider,
        display: "table",
        height: "var(--topbar-height)",
        borderBottom: "1px solid transparent",
        paddingTop: "1rem",
        paddingBottom: "1rem",
        zIndex: 98,
        paddingLeft: "1.75rem",
        [theme.breakpoints.down("sm")]: {
            paddingLeft: "1rem",
        },
    },

    brandText: {
        color: palette.primary.contrastText,
    },

    button: {
        width: 140,
        height: 40,
        fontFamily: "sans-serif",
        fontWeight: "bolder",
        borderRadius: 12,
        backgroundColor: "white",
        color: "#3C77D0",
        margin: "10px 10px 20px 10px",
        border: "1px solid #3C77D0",
    },

    buttonBack: {
        width: 140,
        height: 40,
        fontFamily: "sans-serif",
        fontWeight: "bolder",
        borderRadius: 12,
        backgroundColor: "white",
        color: "#3C77D0",
        margin: "10px 10px 20px 10px",
        border: "1px solid #3C77D0",
        float: "right",
    },
    buttonSubmit: {
        float: "right",
    },

    title: {
        fontFamily: [
            "Roboto",
            "Helvetica",
            "Arial",
            "sans-serif",
            "Khmer OS Siemreap",
        ].join(","),
        fontWeight: "bolder",
        fontSize: "20px",
        margin: "10px 10px 20px 10px",
        float: "left",
    },
}));

const JwtRegister = (props) => {

    const isRegisterForm = false;
    const { t } = useTranslation(["register", "common", "user"]);
    const history = useHistory();
    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const classes = useStyles();
    const [errors, setErrors] = useState({});
    const [enableTab, setEnableTab] = useState(false);
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
        redirectUrl: ''
    });
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [inputData, setInputData] = useState({
        email: '',
        username: '',
        firstname: '',
        lastname: '',
        password: '',
        gender: '',
        dtOfBirth: '',
        address: '',
        conNumber: ''
    });

    const [validationErrors, setValidationErrors] = useState([]);
    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "CREATE":
                    setLoading(false);
                    setInputData({ ...inputData, ...res.data });
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        msg: t("register.save"),
                        severity: "success",
                        redirectUrl: `/session/signin`,
                    });
                    break;
                default:
                    break;
            }
        if (validation) {
            console.log("validation in useEffect....", validation);
        }
    }  else if (error) {
        console.log("error in useEffect....", error);
        setValidationErrors({ ...error });
        setLoading(false);
    }
    if (validation) {
        console.log("validation in useEffect....", validation);
        setValidationErrors({ ...validation });
        setLoading(false);
    }

    // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);


    const handleSnackbarClose = () => {
        setSnackBarState({ ...snackBarState, open: false });

        if (snackBarState && snackBarState.redirectUrl && snackBarState.severity === 'success') {
            //only redirect if it's success
            let url = snackBarState.redirectUrl;
            history.push(url);
        }
    };

    console.log("validationErrors", validationErrors)
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };
    // const handleSave = async () => {
    //     setLoading(true);
    //     sendRequest("/signup", "CREATE", "post", { ...inputData });
    // };
    const handleSave = () => {
        if (_.isEmpty(handleValidate())){
                setLoading(true);
                sendRequest("/signup", "CREATE", "post", { ...inputData });
        }else {
            setValidationErrors(handleValidate())
        }
    };
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

        if (!inputData.password) {
            errors.password = t("register:register.validation.passwordRequired");
        } else if (inputData.password.length < 6) {
            errors.password = t("register:register.validation.passwordTooShort");
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

        if (!inputData.conNumber) {
            errors.conNumber = t("register:register.validation.contactRequired");
        } else if (!/^[0-9]{9,15}$/.test(inputData.conNumber)) {
            errors.conNumber = t("register:register.validation.contactInvalid");
        }

        if (!inputData.address) {
            errors.address = t("register:register.validation.addressRequired");
        }

        setValidationErrors(errors);
        return errors;
    };


    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
    };
    const handleAutoComplete = (e, name, value, reason) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, value?.value) });
    };

    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, [name]: e });
    };
    const handleSubmit = async (values) => {
        setLoading(true);
        sendRequest("api/v1/library/mst/entity/province", "saveProvince", "post", {
            ...inputData,
            recStatus: "A",
        });
    };
    let snackBar;
    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleSnackbarClose}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert onClose={handleSnackbarClose} severity={snackBarState.severity}>
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }

    let formButtons = (
        <C1FormButtons
            options={{
                back: {
                    show: true,
                    eventHandler: () => history.push("/signin"),
                },
                save: {
                    show : true,
                    eventHandler: () => handleSave()
                }
            }}
        />
    );

    return (
        <div className="h-full-screen flex-column flex-grow">
            {snackBar}
            {loading && <MatxLoading />}
            <div className={clsx("relative w-full", classes.root)}>
                <div className="flex justify-between items-center h-full">
                    <div className="flex items-center h-full">
                        <img className="h-32" src={mainLogo} alt="" />
                        <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>Student Registration</span>
                    </div>
                </div>
            </div>
            <div className={classes.formData}>
            <Formik
                className={classes.formData}
                initialValues={inputData}
                onSubmit={(values, actions) => handleSave(values, actions, "submit")}
                enableReinitialize={true}
                values={inputData}
                validate={handleValidate}>
                {(props) => (
                    <Form noValidate={true}>
                        <Card className={classes.card}>
                            <div style={{ width: "100%", height: "70px" }}>
                                <Box display="flex" p={1}>
                                    <Box p={1} flexGrow={1}>
                                        <div className={classes.title}>{t("register.header")} </div>
                                    </Box>
                                    <Box p={1}>{formButtons}</Box>
                                </Box>
                            </div>
                            <Grid container>
                                <Grid item xs={12}>
                                    <Tabs
                                        className="mt-4"
                                        value={tabIndex}
                                        onChange={handleTabChange}
                                        indicatorColor="primary"
                                        textColor="primary" >

                                        {registerTabs.map((item, ind) => {
                                            return <TabsWrapper
                                                className="capitalize"
                                                value={ind}
                                                label={t(item.text)}
                                                key={ind}
                                                disabled={ind === 0 ? false : !enableTab}
                                                icon={item.icon}
                                            />
                                        })}
                                    </Tabs>
                                    <Divider className="mb-6" />
                                    {tabIndex === 0 && (
                                        <UserRegisterDetail
                                            inputData={inputData}
                                            isRegisterForm={isRegisterForm}
                                            errors={validationErrors}
                                            handleDateChange={handleDateChange}
                                            handleInputChange={handleInputChange}
                                            handleAutoComplete={handleAutoComplete}
                                            locale={t}
                                        />
                                    )}
                                </Grid>
                            </Grid>
                        </Card>
                    </Form>
                )}
            </Formik>
            </div>
            <Footer />
        </div>
    );
};

export default JwtRegister;
