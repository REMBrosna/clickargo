import { Box, Card, Divider, Grid, Snackbar, Tab, Tabs } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import { Form, Formik } from "formik";
import pako from "pako";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";

import C1Alert from "app/c1component/C1Alert";
import C1FormButtons from "app/c1component/C1FormButtons";
import useHttp from "app/c1hooks/http";
import { COMMON_ATTACH_LIST_BY_REFID_URL, registerTabs } from "app/c1utils/const";
import { ALLOWED_FILE_EXTS, isCompress, MAX_FILE_SIZE, RegistrationStatus } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { getExtension, isEmpty } from "app/c1utils/utility";
import cliclogo from "app/MatxLayout/Layout2/cliclogo.png";
import Footer from "app/MatxLayout/SharedCompoents/Footer";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { MatxLoading } from "matx";

import CompanyDetails from "./CompanyDetails";
import RegisterSuppDocs from "./RegisterSuppDocs";
import Services from "./Services";
import UserDetail from "./UserDetail";

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
            "Poppins",
        ].join(","),
        fontWeight: "bolder",
        fontSize: "20px",
        margin: "10px 10px 20px 10px",
        float: "left",
    },
}));

const JwtRegister = (props) => {

    let { regId } = useParams();
    const { t } = useTranslation(["register", "common"]);
    const history = useHistory();
    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const classes = useStyles();

    const [enableTab, setEnableTab] = useState(true);

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: "top",
        horizontal: "center",
        msg: "",
        severity: "success",
        redirectUrl: ''
    });

    //for server calls use sendRequest and responses will be stored in the corresponding deconstructed variables
    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

    const [accnData, setAccnData] = useState();
    const [usrData, setUsrData] = useState();

    const [regDocs, setRegDocs] = useState([]);
    const [regDocIds, setRegDocIds] = useState([]);

    const [inputData, setInputData] = useState({ usrData: usrData, accnData: accnData });

    const [validationErrors, setValidationErrors] = useState([]);


    //for loading the supporting documents
    useEffect(() => {

        if (regId) {
            setEnableTab(true);
            sendRequest(`/api/register/${regId}`, "fetch", "get");
        } else {
            // sendRequest("/api/register/new", "newSignUp", "get");
            sendRequest("/api/mockregister/new", "newSignUp", "get");
            // sendRequest("/api/register/suppDocs", "loadSuppDocs", "get");
        }
        // eslint-disable-next-line
    }, [regId]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {

            switch (urlId) {
                case "newSignUp":
                    setAccnData({ ...res?.data?.portalAccn });
                    setUsrData({ ...res?.data?.coreUsr });
                    break;
                case "save":
                    setLoading(false);
                    setInputData({ ...inputData, ...res.data });
                    setSnackBarState({
                        ...snackBarState,
                        open: true, msg: t("register.save"),
                        severity: "success", redirectUrl: `/session/register/${res.data.data.accnrId}`
                    });
                    setSubmitSuccess(true);
                    setValidationErrors({});
                    break;
                case "submit":
                    setLoading(false);
                    setSnackBarState({ ...snackBarState, open: true, msg: t("register.submit"), severity: "success", redirectUrl: "/session/signin" });
                    setSubmitSuccess(true);
                    break;
                case "fetch":
                    //Company Detail
                    setAccnData({ ...res?.data?.portalAccn });
                    setUsrData({ ...res?.data?.coreUsr });
                    setEnableTab(res?.data?.adminTabEnabled || res?.data?.supDocsEnabled);
                    sendRequest("/api/register/suppDocs", "loadSuppDocs", "get");
                    break;
                case "loadSuppDocs":
                    const newSuppDocs = res?.data?.map((value) => value);
                    setRegDocs([...newSuppDocs]);
                    sendRequest(`${COMMON_ATTACH_LIST_BY_REFID_URL}${regId}`, "loadExistingSuppDocs", "get");
                    break;
                case "upload":
                    setLoading(isLoading);
                    let { attId } = res.data;
                    let { attType } = res.data;

                    let tempDocArr = [...regDocs];
                    tempDocArr = tempDocArr.map((doc) => {
                        if (doc.attType === attType) {
                            return { ...doc, attId: attId, attReferenceid: 433333 };
                        }
                        return doc;
                    });
                    setRegDocs(tempDocArr);
                    setRegDocIds([...regDocIds, attId]);
                    break;
                case "loadExistingSuppDocs":
                    setLoading(isLoading);
                    const tempExistingDocArr = [...regDocs].map((value) => {
                        const newValue = { ...value }
                        const newExistingRegDocs = res?.data?.aaData?.find((doc) => doc?.attType === value?.attType);
                        newValue.attId = newExistingRegDocs?.attId
                        newValue.attName = newExistingRegDocs?.attName
                        newValue.attData = newExistingRegDocs?.attData || ""
                        return newValue;
                    });
                    setRegDocs(tempExistingDocArr);
                    break;
                default:
                    break;
            }
        }

        if (validation) {
            //currently only tin is validated backend. TODO will have to change this to implement properly.
            const errorValidation = { ...validation };
            if (validation.hasOwnProperty("accnDetails")) {
                setTabIndex(0)
                errorValidation.accnDetails.accnCoyRegn && (
                    validation.accnDetails.accnCoyRegn = t("validations.accnDetails.accnCoyRegn")
                );
            }

            else if (validation.hasOwnProperty("coreUsr")) {
                if (urlId === 'submit')
                    setTabIndex(1);
                //ENABLE THE TAB
                setEnableTab(true);

            }
            else if (validation.hasOwnProperty("regDocs")) {
                if (urlId === 'submit') {
                    setTabIndex(2);
                    sendRequest("/api/register/suppDocs", "loadSuppDocs", "get");
                }

                //ENABLE THE TAB
                setEnableTab(true);
            }
            setValidationErrors({ ...validation });
            setLoading(false);
        }

        if (error) {
            setLoading(false);
        }
        // eslint-disable-next-line
    }, [isLoading, res, error, urlId, history]);

    const handleSnackbarClose = () => {
        setSnackBarState({ ...snackBarState, open: false });

        if (snackBarState && snackBarState.redirectUrl && snackBarState.severity === 'success') {
            //only redirect if it's success
            let url = snackBarState.redirectUrl;
            history.push(url);
        }
    };

    const handleTabChange = (e, value) => {
        setTabIndex(value);
        if (value === 2) {
            sendRequest("/api/register/suppDocs", "loadSuppDocs", "get");
        }
    };


    const handleSave = async (values, action, actionType) => {
        setLoading(true);
        const newBody = {
            portalAccn: accnData,
            coreUsr: usrData,
            regDocIds: regDocIds,
            regDocs: regDocs,
            requestAction: actionType,
            adminTabEnabled: regId === undefined ? actionType === 'submit' ? true : false : true,
            supDocsEnabled: regId === undefined ? actionType === 'submit' ? true : false : true
        }

        sendRequest("/api/register/", actionType, "post", { ...newBody });
    };

    const handleValidate = () => {
        const errors = {
            regDocs: {},
        };

        //reset the error to proceed to submit
        if (isEmpty(errors.regDocs)) {
            return {};
        }

        return errors;
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        if (elName.includes("accnDetails")) {
            setAccnData({ ...accnData, ...deepUpdateState(accnData, elName, e.target.value) });
        } else {
            setUsrData({ ...usrData, ...deepUpdateState(usrData, elName, e.target.value) });
        }

    };

    const handleAutoComplete = (e, name, value, reason) => {
        if (name.includes("accnDetails")) {
            setAccnData({ ...accnData, ...deepUpdateState(accnData, name, value?.value) });
        } else {
            setUsrData({ ...usrData, ...deepUpdateState(usrData, name, value?.value) });
        }
    };



    const handleFileChange = (e, index) => {
        e.preventDefault();

        let file = e.target.files[0];
        if (!file) {
            return;
        }
        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(e.target.files[0]);

        let type = e.target.name;
        fileReader.onload = (e) => {
            let uint8Array = new Uint8Array(e.target.result);

            if (!validateFile(uint8Array, file.name)) {
                return;
            }

            if (isCompress) {
                // let originalLength = uint8Array.length;
                uint8Array = pako.deflate(uint8Array);
                // console.log("length:", uint8Array.length - originalLength, originalLength);
            }
            // let imgStr = Uint8ArrayToString(uint8Array);
            // let base64Sign = btoa(imgStr);
            let buff = new Buffer.from(uint8Array);
            let base64Sign = buff.toString('base64');

            //upload file to server
            let json = {
                attName: file.name,
                attType: type,
                attReferenceid: accnData?.accnDetails?.accnId,
                attSize: file.size,
                attData: base64Sign,
            };

            setLoading(true);
            sendRequest("/api/register/attch/upload", "upload", "post", json);

            //for onclick view of the file
            setRegDocs((d) =>
                d.map((el) => {
                    if (el.seq === index) {
                        el.attData = base64Sign;
                        el.attName = file.name;
                        el.attSize = file.size;
                    }

                    return el;
                })
            );
        };
    };

    const validateFile = (uint8Array, fileName) => {

        let ext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length) || fileName;
        if (!ALLOWED_FILE_EXTS.includes(ext.toLowerCase())) {
            let msg = t("common:supportingDocs.msg.extsNotAllowed");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        if (!uint8Array || uint8Array.length === 0) {
            let msg = t("common:supportingDocs.msg.fileTooSmall");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }
        if (uint8Array.length > MAX_FILE_SIZE * 1024 * 1024) {
            let msg = t(`common:supportingDocs.msg.fileSizeTooBig`, { size: MAX_FILE_SIZE });
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        if (fileName.length > 128) {
            let msg = t("common:supportingDocs.msg.fileNameLen");
            setSubmitSuccess(true);
            setSnackBarState(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        return true;
    }

    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, [name]: e });
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
                submit: (['P', 'C', 'V'].includes(accnData?.accnDetails?.accnStatus)) ? false : true,
                // save: {
                //     show: (['P', 'C', 'V'].includes(accnData?.accnDetails?.accnStatus)) ? false : true,
                //     eventHandler: () => handleSave(null, null, "save")
                // }
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
                        <img className="h-32" src={cliclogo} alt="" />
                        <span className={clsx("font-medium text-24 mx-4", classes.brandText)}>PortEDI</span>
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
                                            <CompanyDetails
                                                inputData={accnData}
                                                errors={validationErrors}
                                                handleInputChange={handleInputChange}
                                                handleAutoComplete={handleAutoComplete}
                                                isSubmitting={loading || (['P', 'C', 'V'].includes(accnData?.accnDetails?.accnStatus))}
                                                locale={t}
                                            />
                                        )}

                                        {tabIndex === 1 && (
                                            <UserDetail
                                                inputData={usrData}
                                                errors={validationErrors}
                                                handleDateChange={handleDateChange}
                                                handleInputChange={handleInputChange}
                                                handleAutoComplete={handleAutoComplete}
                                                isSubmitting={loading || (['P', 'C', 'V'].includes(accnData?.accnDetails?.accnStatus))}
                                                locale={t}
                                            />
                                        )}

                                        {tabIndex === 2 && (
                                            <Services
                                                inputData={regDocs}
                                                handleFileChange={handleFileChange}
                                                // errors={props.errors}
                                                errors={validationErrors}
                                                isSubmitting={loading || (['P', 'C', 'V'].includes(accnData?.accnDetails?.accnStatus))}
                                                locale={t}
                                            />
                                        )}
                                        {tabIndex === 3 && (
                                            <RegisterSuppDocs
                                                inputData={regDocs}
                                                handleFileChange={handleFileChange}
                                                // errors={props.errors}
                                                errors={validationErrors}
                                                isSubmitting={loading || (['P', 'C', 'V'].includes(accnData?.accnDetails?.accnStatus))}
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
