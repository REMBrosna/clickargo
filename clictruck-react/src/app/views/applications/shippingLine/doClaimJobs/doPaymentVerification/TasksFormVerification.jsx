import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import { Snackbar } from "@material-ui/core";
import PaymentOutlinedIcon from '@material-ui/icons/PaymentOutlined';
import React, { useEffect, useState } from "react";

import C1Alert from "app/c1component/C1Alert";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { JobStates, Roles, Status } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { tabScroll } from "app/c1utils/styles";
import { formatDate } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";
import { useTranslation } from "react-i18next";

import TasksFormPayVerification from "./TasksFormPayVerification";

const TasksFormVerification = () => {

    const { t } = useTranslation(["payments"]);

    const tabList = [
        { text: t("payments:paymentVerification.tabs.doPayments"), icon: <PaymentOutlinedIcon /> },
    ];
    const { user } = useAuth();

    const [tabIndex, setTabIndex] = useState(0);


    /** ------------------ States ---------------------------------*/

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(false);
    const [controls, setControls] = useState([]);

    const [verifyErrorOpen, setVerifyErrorOpen] = useState({ msg: null, open: false });
    // const [verifySubmitConfirm, setVerifySubmitConfirm] = useState({ action: "", open: false });
    const [payVerifySubmitConfirm, setPayVerifySubmitConfirm] = useState({ action: "", open: false });

    const [isSubmitSuccess, setSubmitSuccess] = useState(false);

    const [isShowVerifyIcon, setShowVerifyIcon] = useState(true);

    const initialButtons = {
        back: { show: false, eventHandler: () => handleExitOnClick() },
        verify: { show: false, eventHandler: () => console.log("initializing") }
    };

    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: t("payments:paymentVerification.msg.paymentsVerified"),
        severity: 'success'
    });

    //initialize both tabs not allowed to access first until all rendered
    const [allowedAccess, setAllowedAccess] = useState({ payTab: false });
    /** ------------------- Update states ----------------- */
    useEffect(() => {
        //call for controls
        const reqBody = {
            entityType: "JOB_DOI_FF",
            entityState: Status.SUB.code,
            page: "VIEW",
        };
        sendRequest("/api/v1/clickargo/controls/", "fetchControls", "post", reqBody);

        //check if user is authorized to access
        setAllowedAccess({
            payTab: user?.authorities.some(el => el?.authority === Roles.FINANCE.code),
        });
    }, []);


    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "fetchControls": {
                    setControls([...res.data]);
                    break;
                }
                case "getPaymentVerificationInfo": {
                    if (res?.data) {
                        inputs.info = res.data
                        setInputs(Object.assign({}, inputs))
                    }
                    setRefresh(true)
                    break;
                }
                case "paymentVerification": {
                    if (res?.data) {
                        inputs.info = res.data
                        setInputs(Object.assign({}, inputs))
                    }
                    setRefresh(true)
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        successMsg: t("payments:paymentVerification.msg.paymentsVerified")
                    });
                    setSubmitSuccess(true);
                    break;
                }
                default: break;
            }


        }
    }, [urlId, isLoading, isFormSubmission, res, error]);

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleExitOnClick = () => {
        console.log("no  exit for now..");
    }

    const handlePayVerifyConfirmAction = (e) => {
        e.preventDefault();
        setPayVerifySubmitConfirm({ ...payVerifySubmitConfirm, action: null, open: false })
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clicdo/payment-verification/verify/${inputs?.query?.date}`, "paymentVerification", "put", null);
    }

    const handleVerifyOnClick = (action) => {
        if (action === "verify_payment") {
            if (inputs?.info?.totalDo === 0) {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: t("payments:paymentVerification.msg.noPaymentToVerify"), open: true });
            } else if (inputs?.info?.totalDo && inputs?.info?.totalDo > 0) {
                setPayVerifySubmitConfirm({ ...payVerifySubmitConfirm, action: "verify", open: true });
            }
        }
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "verify") {
            handleVerifyOnClick("verify_payment")
        } else if (action.toLowerCase() === "exit") {
            handleExitOnClick();
        }
    };

    //PAYMENT VERIFICATION RELATED PROPS START
    const getPaymentVerificationInfo = () => {
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clicdo/payment-verification/${inputs?.query?.date}`, "getPaymentVerificationInfo");
    }
    const now = new Date();
    const [inputs, setInputs] = useState({
        info: {
            totalDo: 0,
            totalCo: 0,
            totalCharges: 0,
            authorizerOptions: []
        },
        query: {
            date: now,
            authorizer: '',
            region: '',
        }
    })
    const [filterBy, setFilterBy] = useState([{ attribute: 'forPaymentVerification', value: 'true' },
    { attribute: 'jobDatePaid', value: formatDate(now) },
    { attribute: 'ffDoJobState', value: JobStates.PAID.code },
    { attribute: 'coJobStatus', value: 'A' }])
    const [isRefresh, setRefresh] = useState(true);
    const [selectedTotalCharges, setSelectedTotalCharges] = useState(0);
    const [selectedTotalChargesUsd, setSelectedTotalChargesUsd] = useState(0);
    const handlePayInputChange = (e, type) => {
        setSelectedTotalCharges(0);
        setSelectedTotalChargesUsd(0);
        if (type === 'date') {
            let newValue = formatDate(e)
            setRefresh(false)
            if (newValue) {
                inputs.query.date = e;
                setInputs(Object.assign({}, inputs))
                setFilterBy([...filterBy.filter(param => param.attribute !== 'jobDatePaid'), { attribute: 'jobDatePaid', value: newValue }])
            }
            else {
                inputs.query.date = newValue;
                setInputs(Object.assign({}, inputs, { date: '' }))
                setFilterBy(filterBy.filter(param => param.attribute !== 'date'))
            }
            getPaymentVerificationInfo();
            setTimeout(() => setRefresh(true), 150)
        } else if (type === 'region') {
            let newValue = e.target.value
            setRefresh(false)
            if (newValue) {
                inputs.query.region = newValue
                setInputs(Object.assign({}, inputs))
                setFilterBy([...filterBy.filter(param => param.attribute !== 'region'), { attribute: 'region', value: newValue }])
            } else {
                inputs.query.region = ''
                setInputs(Object.assign({}, inputs))
                setFilterBy(filterBy.filter(param => param.attribute !== 'region'))
            }
            setTimeout(() => setRefresh(true), 150)
        } else if (type === 'authorizer') {
            let newValue = e.target.value
            setRefresh(false)
            if (newValue) {
                inputs.query.authorizer = newValue
                setInputs(Object.assign({}, inputs))
                setFilterBy([...filterBy.filter(param => param.attribute !== 'authorizer'), { attribute: 'authorizer', value: newValue }])
            } else {
                inputs.query.authorizer = ''
                setInputs(Object.assign({}, inputs))
                setFilterBy(filterBy.filter(param => param.attribute !== 'authorizer'))
            }
            setTimeout(() => setRefresh(true), 150)
        }
    }
    //PAYMENT VERIFICATION RELATED PROPS END

    let bcLabel = t("payments:paymentVerification.label.clicDoPaymentVerification");
    let breadcrumb = t("payments:paymentVerification.label.payments");
    let formButtons;
    if (!loading && isShowVerifyIcon) {
        let payControls = controls?.filter(e => {
            if(e.ctrlAction !== "REVERIFY")
                return e;
        });
        formButtons = (
            <C1FormButtons options={getFormActionButton(initialButtons, payControls, eventHandler)} />
        );
    }

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;

    if (isSubmitSuccess) {
        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleCloseSnackBar}
            autoHideDuration={3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleCloseSnackBar} severity={snackBarState.severity}>
                {snackBarState.successMsg}
            </C1Alert>
        </Snackbar>;
    }

    return loading ? <MatxLoading /> : (<React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                { name: breadcrumb }
            ]}
            title={bcLabel}
            formButtons={formButtons}
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
                                {tabList &&
                                    tabList.map((item, ind) => {
                                        if (ind === 0 && !allowedAccess?.payTab)
                                            return null;
                                        return (
                                            <TabsWrapper
                                                style={ind === 4 ? { backgroundColor: '#e4effa' } : {}}
                                                className="capitalize"
                                                value={ind}
                                                label={
                                                    <TabLabel tab={item} />
                                                }
                                                key={ind}
                                                icon={item.icon}
                                                {...tabScroll(ind)}
                                            />
                                        );
                                    })}
                            </Tabs>
                            <Divider className="mb-6" />

                            {tabIndex === 0 && allowedAccess?.payTab && <C1TabInfoContainer guideId="clicdo.doi.sl.pay.verify.list">
                                <TasksFormPayVerification
                                    inputs={inputs}
                                    handlePayInputChange={handlePayInputChange}
                                    filterBy={filterBy}
                                    isRefresh={isRefresh}
                                    selectedTotalCharges={selectedTotalCharges}
                                    setSelectedTotalCharges={setSelectedTotalCharges}
                                    selectedTotalChargesUsd={selectedTotalChargesUsd}
                                    setSelectedTotalChargesUsd={setSelectedTotalChargesUsd}
                                >
                                </TasksFormPayVerification>
                            </C1TabInfoContainer>}

                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>
        {/** Confirmation for verify button */}
        {snackBar}
        <ConfirmationDialog
            open={payVerifySubmitConfirm?.open}
            onConfirmDialogClose={() => setPayVerifySubmitConfirm({ ...payVerifySubmitConfirm, action: null, open: false })}
            text={t("payments:paymentVerification.msg.confirmation", { action: payVerifySubmitConfirm?.action, date: formatDate(inputs.query.date) })}
            title={t("payments:paymentVerification.label.confirmation")}
            disabled={isLoading}
            onYesClick={(e) => handlePayVerifyConfirmAction(e)} />

        {/** Dialog popup for validation upon form button clicks*/}
        <C1DialogPrompt
            confirmationObj={{
                openConfirmPopUp: verifyErrorOpen?.open,
                onConfirmationDialogClose: () => setVerifyErrorOpen({ ...verifyErrorOpen, open: false }),
                text: verifyErrorOpen?.msg,
                title: t("payments:paymentVerification.label.error"),
                onYesClick: () => setVerifyErrorOpen({ ...verifyErrorOpen, open: false }),
                yesBtnText: t("payments:paymentVerification.label.ok"),
            }} />
    </React.Fragment>

    );
};

export default withErrorHandler(TasksFormVerification);