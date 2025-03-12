import { Button, Divider, Grid, Paper, Tab, Tabs } from "@material-ui/core";
import { Build, CreditCard, FileCopy, PersonOutline, Schedule } from "@material-ui/icons";
import AccountBalanceIcon from '@material-ui/icons/AccountBalance';
import pako from "pako";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1Dialog from "app/c1component/C1Dialog";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1InputField from "app/c1component/C1InputField";
import useHttp from "app/c1hooks/http";
import { ALLOWED_FILE_EXTS, COMMON_ATTACH_LIST_BY_REFID_URL, isCompress, MAX_FILE_SIZE, RegistrationStatus } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/stateUtils";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { MatxLoading } from "matx";

import AccountDetailsCreditLine from "./AccounDetailsCreditLine";
import AccountDetailsProfile from "./AccountDetailsProfile";
import AccountDetailsService from "./AccountDetailsService";

const AccountOnboardingFormDetail = () => {
    //useParams hook to acces the dynamic pieaces of the URL
    let { viewType, regId } = useParams('', '');
    let history = useHistory();
    const { t } = useTranslation(["register", "common"]);

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    //useState with initial value of 0 for tabIndex
    const [tabIndex, setTabIndex] = useState(0);
    const [loading, setLoading] = useState(false);
    const [inputData, setInputData] = useState({});
    const [errors, setErrors] = useState({});
    const [regDocs, setRegDocs] = useState([]);
    const [regDocIds, setRegDocIds] = useState([]);
    const [isRemarksDialogOpen, setRemarksDialogOpen] = useState(false);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [snackBarOptions, setSnackBarOptions] = useState({
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: ""
    });

    const tabList = [
        { text: "Profile", icon: <PersonOutline /> },
        { text: "Services", icon: <Build /> },
        { text: "Credit Line", icon: <CreditCard /> },
        // { text: t("common:audits.title"), icon: <Schedule /> }];
        { text: "Bank Accounts", icon: <AccountBalanceIcon /> }];

    //api request for the details here
    useEffect(() => {
        setLoading(true);
        if (viewType !== 'new') {
            // sendRequest(`/api/co/ccm/entity/accnRegister/${regId}`, "getReg", "get", {});
            sendRequest(`/api/mockaccnRegister/details/${regId}`, "getReg", "get", {});
        }

        // eslint-disable-next-line
    }, [regId, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "getReg":
                    setInputData(res.data);
                    break;
                case "approve":
                    setInputData({ ...inputData, ...res.data });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("register:register.approved"),
                        redirectPath: "/onboarding/list"
                    });
                    break;
                case "reject":
                    setInputData({ ...inputData, ...res.data });
                    setRemarksDialogOpen(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("register:register.rejected"),
                        redirectPath: "/onboarding/list"
                    });
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
                default: break;
            }
        }

        if (error) {
            //goes back to the screen
            setLoading(false);
            setRemarksDialogOpen(false);
        }

        if (validation) {
            setLoading(false);
            let keyList = Object.keys(validation);
            if (keyList.length > 0) {
                for (let key of keyList) {
                    if (key.includes("accnrCoIntial.empty")) {
                        validation.accnrCoIntial = t("register:register.error.emptyNotAllowed");
                        break;
                    }
                    if (key.includes("accnrCoIntial.exist")) {
                        validation.accnrCoIntial = t("register:register.error.accountExist", { accnrCoIntial: inputData.accnrCoIntial });
                        break;
                    }
                }
            }
            setErrors({ ...errors, accnrCoIntial: validation.accnrCoIntial });
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error])

    const handleTabChange = (e, value) => {
        setTabIndex(value);
        // if (value === 2) {
        //     sendRequest("/api/register/suppDocs", "loadSuppDocs", "get");
        // }
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
                attReferenceid: regId,
                attSize: file.size,
                attData: base64Sign,
            };

            setLoading(true);
            sendRequest("/api/register/upload", "upload", "post", json);

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
            setSnackBarOptions(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        if (!uint8Array || uint8Array.length === 0) {
            let msg = t("common:supportingDocs.msg.fileTooSmall");
            setSubmitSuccess(true);
            setSnackBarOptions(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }
        if (uint8Array.length > MAX_FILE_SIZE * 1024 * 1024) {
            let msg = t(`common:supportingDocs.msg.fileSizeTooBig`, { size: MAX_FILE_SIZE });
            setSubmitSuccess(true);
            setSnackBarOptions(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        if (fileName.length > 128) {
            let msg = t("common:supportingDocs.msg.fileNameLen");
            setSubmitSuccess(true);
            setSnackBarOptions(sb => { return { ...sb, open: true, msg: msg, severity: "error" } });
            return false;
        }

        return true;
    }

    const handleSubmit = async (values) => {
        setLoading(true);

        switch (viewType) {
            case 'view':
                setLoading(true);
                break;
            default: break;
        }
    };

    const handleValidate = () => {
        const errors = {};
        if (inputData.accnrCoIntial) {
            error.accnrCoIntial = t("register:register.error.compInitialMaxLength");

        }
        return errors;
    }

    const handleInputChange = (e) => {
        setInputData({ ...inputData, [e.target.name]: e.target.value });
    };

    const handleAutoCompleteInput = (e, name, value) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, value?.value) });
        // setInputData({ ...inputData, [e.target.name]: e.target.value });
    }
    const handleInputAccnIdChange = (e) => {
        let elName = e.target.name;
        let temp = e.target.value.replace(/[^\w\s]/gi, "").trim();
        temp.length >= 5 ?
            setErrors({ accnrCoIntial: "Account ID allow Maximum 5 character only!" }) :
            setErrors({ accnrCoIntial: "" });
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, temp.replace("_", "").toUpperCase()) });
    };

    const handleApprove = () => {
        setLoading(true);
        const errs = {};
        if (!inputData.accnrCoIntial) {
            errs.accnrCoIntial = t("register:register.error.compInitialMaxLength");
            setErrors({ ...errors, ...errs });
            setLoading(false);
        } else {
            //reset error
            setErrors({});
            //continue to call the api
            sendRequest(`/api/register/approve/${regId}`, "approve", "put", { ...inputData });
        }
    }

    const handleReject = () => {
        setLoading(true);
        sendRequest(`/api/register/reject/${regId}`, "reject", "put", { ...inputData });
    }

    const handleCloseRemarksDialog = () => {
        setRemarksDialogOpen(false);
    }

    let bcLabel = "Account Details";

    let formButtons;
    if (!loading && res) {
        console.log("data", res.data)
        formButtons = <C1FormButtons options={{
            submit: res?.data?.accnrStatus === 'A' ? false : true,
            verify: {
                show: res?.data?.accnrStatus === 'A' ? false : true,
                eventHandler: () => null
            },
            save: {
                show: viewType !== 'view',
                eventHandler: () => null
            },
            delete: {
                show: res?.data?.accnrStatus === 'A' ? false : true,
                eventHandler: () => null
            },
            back: {
                show: true,
                eventHandler: () => history.push("/onboarding/list")
            },
            approve: {
                show: inputData.accnrStatus === RegistrationStatus.PENDING_APPROVAL.code,
                eventHandler: () => handleApprove(),
            },
            reject: {
                show: inputData.accnrStatus === RegistrationStatus.PENDING_APPROVAL.code,
                eventHandler: () => setRemarksDialogOpen(true),
            }
        }} />
    }

    let rejectDialog = <React.Fragment>
        <C1Dialog
            title={t("register:register.rejectRemarks")}
            isOpen={isRemarksDialogOpen}
            actionsEl={<Button variant="contained"
                color="primary"
                size="large"
                fullWidth
                onClick={(e) => handleReject(e)}>{t("register:register.btnReject")}</Button>}
            handleCloseEvent={handleCloseRemarksDialog} >

            <C1InputField
                required
                multiline
                rows={4}
                label=""
                name="rejectRemarks"
                onChange={handleInputChange} />
        </C1Dialog>
    </React.Fragment>

    return (
        loading ? <MatxLoading /> : <React.Fragment>
            {rejectDialog}
            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: t("register:register.list"), path: "/onboarding/list" },
                    { name: bcLabel },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                onSubmit={(values, actions) => handleSubmit(values, actions)}
                onValidate={handleValidate}
                snackBarOptions={snackBarOptions}
                isLoading={loading}>
                {(props) => (
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <Paper>
                                <Tabs
                                    className="mt-4"
                                    value={tabIndex}
                                    onChange={handleTabChange}
                                    indicatorColor="primary"
                                    textColor="primary"
                                    variant="scrollable"
                                    scrollButtons="auto">
                                    {tabList.map((item, ind) => (
                                        <Tab className="capitalize" value={ind} label={item.text} key={ind} icon={item.icon} />
                                    ))}
                                </Tabs>
                                <Divider className="mb-6" />

                                {tabIndex === 0 && <AccountDetailsProfile inputData={props.values}
                                    handleInputChange={handleInputChange}
                                    handleInputAccnIdChange={handleInputAccnIdChange}
                                    handleAutoCompleteInput={handleAutoCompleteInput}
                                    errors={errors}
                                    locale={t}
                                    viewType={viewType} />
                                }
                                {tabIndex === 1 && <AccountDetailsService inputData={props.values}
                                    handleInputChange={handleInputChange}
                                    handleInputAccnIdChange={handleInputAccnIdChange}
                                    errors={errors}
                                    locale={t}
                                    viewType={viewType} />
                                }
                                {tabIndex === 2 && <AccountDetailsCreditLine inputData={props.values}
                                    handleInputChange={handleInputChange}
                                    handleInputAccnIdChange={handleInputAccnIdChange}
                                    errors={errors}
                                    locale={t}
                                    viewType={viewType} />
                                }
                                {/* {tabIndex === 3 && <C1AuditTab filterId={regId} />} */}
                            </Paper>

                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>
        </React.Fragment>
    );
};


export default withErrorHandler(AccountOnboardingFormDetail);