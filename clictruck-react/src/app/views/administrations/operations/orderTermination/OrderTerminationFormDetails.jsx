import { Backdrop, Button, CircularProgress, Dialog, Divider, Grid, Paper, Tabs, makeStyles } from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog } from "matx";

import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import AccessTimeOutlinedIcon from '@material-ui/icons/AccessTimeOutlined';
import Details from "./tabs/Details";
import { CK_CT_TERMINATION_JOB_LIST, CK_CT_TERMINATION_JOB_TERM_LIST } from "app/c1utils/const";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: 99999,
        color: '#fff',
    },
}));

const OrderTerminationFormDetails = () => {

    const { t } = useTranslation(["administration", "common", 'listing']);
    const classes = useStyles();
    const { viewType, terminationId } = useParams();
    const history = useHistory();
    const { user } = useAuth();
    const tabList = [
        { text: t("listing:orderTermination.tabs.details"), icon: <DescriptionOutlinedIcon /> },
        { text: t("listing:orderTermination.tabs.audits"), icon: <AccessTimeOutlinedIcon /> }
    ];
    const [tabIndex, setTabIndex] = useState(0);
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [loading, setLoading] = useState(true);
    const [inputData, setInputData] = useState({});
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [validationErrors, setValidationErrors] = useState({});
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");
    // eslint-disable-next-line
    const [fileUploaded, setFileUploaded] = useState(false);
    const [invoiceDetails, setInvoiceDetails] = useState([]);
    const [creditDetails, setCreditDetails] = useState([]);
    const [truckJobs, setTruckJobs] = useState([]);
    const isApproval = user?.authorities?.some(item => item.authority.includes('SP_FIN_HD'));
    const isRequester = user?.authorities?.some(item => item.authority.includes('SP_L1'));
    const [refreshTable, setRefreshTable] = useState(true);
    const [termDataList, setTermDataList] = useState([]);

    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        save: { show: viewType === 'edit' || viewType === 'new', eventHandler: () => handleSaveOnClick() },
    }
    /** ------------------- Update states ----------------- */
    useEffect(() => {
        if (viewType === 'new') {
            sendRequest("/api/v1/clickargo/clictruck/job/jobTermReq/-", "getData", "get", null);
        } else if (viewType === 'view' || viewType === 'edit') {
            setSnackBarOptions(defaultSnackbarValue);
            setLoading(true);
            setFileUploaded(true);
            sendRequest("/api/v1/clickargo/clictruck/job/jobTermReq/" + terminationId, "getData", "get", null);
        }
    // eslint-disable-next-line
    }, [terminationId, viewType]);


    useEffect(() => {

        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "createData": {
                    setInputData({ ...res?.data });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.saveSuccess"),
                        redirectPath: `/opadmin/order-termination/edit/${res?.data?.jtrId}`
                    });
                    break;
                }
                case "getData": {
                    setInputData({ ...res.data });
                    setLoading(false)
                    if (res?.data?.tcoreAccn?.accnId) {
                        getCreditBalance(res?.data?.tcoreAccn?.accnId)
                    }
                    break;
                }
                case "updateData": {
                    setInputData({ ...res.data });
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.updateSuccess"),
                    });
                    break;
                }
                case "submit": {
                    let msg = t("common:msg.submitSuccess");
                    if (openSubmitConfirm && openSubmitConfirm.action === "DELETE") {
                        msg = t("common:msg.deleteSuccess");
                    } else if (openSubmitConfirm && openSubmitConfirm.action === "APPROVE") {
                        msg = t("common:msg.approveSuccess");
                    } else if (openSubmitConfirm && openSubmitConfirm.action === "REJECT") {
                        msg = t("common:msg.rejectSuccess");
                    }

                    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: msg,
                        redirectPath: "/opadmin/order-termination/list",
                    });
                    break;
                }
                case "deleteData": {
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.deleteSuccess"),
                        redirectPath: '/opadmin/order-termination/list'
                    });
                    break;
                }
                case "getCreditData": {
                    setCreditDetails(res?.data)
                    getInvoiceDetails()
                    break;
                }
                case "getInvoiceDetails": {
                    setInvoiceDetails(res?.data)
                    getTermData();
                    break;
                }
                case "getTruckJobs": {
                    setLoading(false)
                    setTruckJobs(res?.data?.aaData)
                    break;
                }
                case "storeJob": {
                    getTermData()
                    setRefreshTable(true)
                    break;
                }
                case "deleteJob": {
                    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
                    getTermData()
                    setRefreshTable(true)
                    break;
                }
                case "getTermData": {
                    setTermDataList(res?.data?.aaData);
                    break;
                }
                default: break;
            }
        }

        if (error) {
            //goes back to the screen
            setLoading(false);
        }

        //If validation has value then set to the errors
        if (validation) {
            setValidationErrors({ ...validation });
            setLoading(false);
            setSnackBarOptions(defaultSnackbarValue);
            setOpenSubmitConfirm({ ...openSubmitConfirm, open: false })

            if (validation['TCkCtJobTerms']) {
                setOpenWarning(true)
                setWarningMessage(validation['TCkCtJobTerms'])
            }

            //if validation contains SUBMIT API CALL FAILURE, prompt message
            // console.log(validation['Submit.API.call'])
            if (validation['Submit.API.call']) {
                // alert(validation['Submit.API.call'])
                setOpenWarning(true)
                setWarningMessage(validation['Submit.API.call'])
            }
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);

    useEffect(() => {
        if (!isApproval && !isRequester) {
            history.push('/session/404')
        }
    // eslint-disable-next-line
    }, []);

    const getTruckJobs = () => {
        setLoading(true)
        sendRequest(`${CK_CT_TERMINATION_JOB_LIST}${inputData?.tcoreAccn?.accnId}`, "getTruckJobs", "get");
    }

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });

        if (elName === 'tcoreAccn.accnId') {
            getCreditBalance(e.target.value)
        }
    };

    const handleExitOnClick = () => {
        history.push("/opadmin/order-termination/list");
    }

    const handleSave = () => {
        setLoading(true);
        setValidationErrors({});
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        const data = inputData;
        delete data.tckJobTruck;
        switch (viewType) {
            case "new":
                sendRequest("/api/v1/clickargo/clictruck/job/jobTermReq", "createData", "post", data);
                break;
            case "edit":
                sendRequest("/api/v1/clickargo/clictruck/job/jobTermReq/" + terminationId, "updateData", "put", { ...inputData });
                setLoading(false);
                break;
            default:
                break;
        }
    };

    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    const handleSubmitOnClick = () => {
        setInputData({ ...inputData, "action": "SUBMIT", jtrDtApproveReject: "" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SUBMIT", open: true, msg: t('common:msg.submitConfirm') });
    }

    const handleSaveOnClick = () => {
        setInputData({ ...inputData, "action": null, jtrDtApproveReject: "", jtrDtSubmit: "" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SAVE", open: true, msg: t('common:msg.saveConfirm') });
    }

    const handleDeleteOnClick = () => {
        setInputData({ ...inputData, "action": "DELETE" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "DELETE", open: true, msg: t('common:msg.deleteConfirm') });
    }

    const handleApproveOnClick = () => {
        setInputData({ ...inputData, "action": "APPROVE" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "APPROVE", open: true, msg: t('common:msg.approveConfirm') });
    }

    const handleRejectOnClick = () => {
        setInputData({ ...inputData, "action": "REJECT" });
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "REJECT", open: true, msg: t('common:msg.rejectConfirm') });
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSave();
        } else if (['submit', 'approve', 'reject'].includes(action.toLowerCase())) {
            setLoading(true)
            const data = inputData;
            delete data.tckJobTruck;
            sendRequest(`/api/v1/clickargo/clictruck/job/jobTermReq/${terminationId}`, "submit", "put", data);
        } else if (action.toLowerCase() === "delete") {
            setLoading(true)
            sendRequest(`/api/v1/clickargo/clictruck/job/jobTermReq/${terminationId}`, "submit", "delete");
        } else if (action.toLowerCase() === "delete_job") {
            setLoading(true)
            setRefreshTable(false)
            sendRequest(`/api/v1/clickargo/clictruck/job/jobTerm/${openSubmitConfirm?.id}`, 'deleteJob', 'delete');
        }
    };

    const getCreditBalance = (accnId) => {
        setLoading(true)
        const payload = {
            tckMstServiceType: {
                svctId: "CLICTRUCK",
            },
            tcoreAccn: {
                accnId: accnId
            },
            tmstCurrency: {
                ccyCode: "IDR"
            }
        }
        sendRequest(`/api/v1/clickargo/credit/fetch`, 'getCreditData', 'POST', payload);
    }

    const getInvoiceDetails = () => {
        sendRequest(`/api/v1/clickargo/clictruck/invoice/statistic/${inputData?.tcoreAccn?.accnId}?invStatus=NEW,PAYING,PENDING`, 'getInvoiceDetails', 'get');

    }

    const handleAddTruckJob = async () => {
        setLoading(true)
        setRefreshTable(false)

        const payload = await inputData?.tckJobTruck?.jobId.map(item => {
            return {
                "otherLangDesc": null,
                "coreMstLocale": null,
                "jtId": "",
                "jtJobDn": 0.0,
                "jtJobPltfeeAmtCoff": 0.0,
                "jtJobPltfeeAmtTo": 0.0,
                "jtStatus": "A",
                "jtDtCreate": null,
                "jtUidCreate": null,
                "jtDtLupd": null,
                "jtUidLupd": null,
                "tckJobTruck": {
                    "jobId": item
                },
                "tckCtJobTermReq": {
                    "jtrId": terminationId
                }
            }
        });

        sendRequest(`/api/v1/clickargo/clictruck/job/truck/jobTerms`, "storeJob", "post", payload);

    }

    const handleDeleteJobOnClick = id => {
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: "DELETE_JOB", open: true, msg: t('common:msg.deleteConfirm'), id });
    }

    const getTermData = () => {
        sendRequest(`${CK_CT_TERMINATION_JOB_TERM_LIST}${terminationId}`, 'getTermData', 'get');

    }

    let bcLabel = viewType === 'edit' ? t("listing:orderTermination.breadCrumbs.edit") : t("listing:orderTermination.headerTitle")
    let formButtons;
    if (!loading) {
        if (viewType) {
            switch (viewType) {
                case "edit":
                    bcLabel = t("listing:orderTermination.breadCrumbs.edit")
                    formButtons = (
                        <C1FormButtons options={{
                            ...initialButtons,
                            ...{
                                submitOnClick: { show: true, eventHandler: () => handleSubmitOnClick() },
                                delete: { show: inputData?.jtrState === 'NEW', eventHandler: () => handleDeleteOnClick() }
                            }
                        }
                        } />
                    );
                    break;
                case "view":
                    formButtons = (
                        <C1FormButtons options={{
                            ...initialButtons,
                            ...{
                                approve: { show: isApproval && inputData?.jtrState === 'SUB', eventHandler: () => handleApproveOnClick() },
                                reject: { show: isApproval && inputData?.jtrState === 'SUB', eventHandler: () => handleRejectOnClick() }
                            }
                        }
                        } />
                    );
                    break;
                case "new":
                    bcLabel = t("listing:orderTermination.breadCrumbs.create");
                    formButtons = (
                        <C1FormButtons options={{
                            ...initialButtons
                        }
                        } />
                    );
                    break;
                default: break;
            }
        }
    }

    return <React.Fragment>
        <C1FormDetailsPanel
            breadcrumbs={[
                { name: t("listing:orderTermination.headerTitle"), path: "/opadmin/order-termination/list" },
                { name: viewType === 'new' ? (t("listing:orderTermination.breadCrumbs.create")) : (viewType === 'view' ? (t("listing:orderTermination.breadCrumbs.view")) : (viewType === 'edit' ? t("listing:orderTermination.breadCrumbs.edit") : t("administration:truckManagement.breadCrumbs.edit"))) }
            ]}
            titleStatus={inputData?.jtrState}
            title={bcLabel}
            formButtons={formButtons}
            initialValues={{ ...inputData }}
            values={{ ...inputData }}
            snackBarOptions={{ ...snackBarOptions }}
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
                                        return (
                                            <TabsWrapper
                                                // CPEDI-193
                                                style={ind === 4 ? { backgroundColor: '#e4effa' } : {}}
                                                className="capitalize"
                                                value={ind}
                                                disabled={item.disabled}
                                                label={
                                                    <TabLabel
                                                        viewType={viewType}
                                                        invalidTabs={inputData.invalidTabs}
                                                        tab={item} />
                                                }
                                                key={ind}
                                                icon={item.icon}
                                                {...tabScroll(ind)}
                                            />
                                        );
                                    })}
                            </Tabs>
                            <Divider className="mb-6" />
                            {tabIndex === 0 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                <Details viewType={viewType}
                                    inputData={inputData}
                                    handleInputChange={handleInputChange}
                                    errors={validationErrors}
                                    invoiceDetails={invoiceDetails}
                                    creditDetails={creditDetails}
                                    truckJobs={truckJobs}
                                    getTruckJobs={getTruckJobs}
                                    handleAddTruckJob={handleAddTruckJob}
                                    isApproval={isApproval}
                                    terminationId={terminationId}
                                    refreshTable={refreshTable}
                                    handleDeleteJob={handleDeleteJobOnClick}
                                    termDataList={termDataList}
                                /></C1TabInfoContainer>}

                            {tabIndex === 1 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                <C1AuditTab filterId={inputData?.jtrId ? inputData?.jtrId : 'empty'}></C1AuditTab>
                            </C1TabInfoContainer>}
                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>

        {/* For submit confirmation */}
        <ConfirmationDialog
            open={openSubmitConfirm?.open}
            onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
            text={openSubmitConfirm?.msg}
            title={t("common:popup.confirmation")}
            onYesClick={(e) => eventHandler(openSubmitConfirm?.action)} />

        <Dialog maxWidth="xs" open={openWarning} >
            <div className="p-8 text-center w-360 mx-auto">
                <h4 className="capitalize m-0 mb-2">{"Warning"}</h4>
                <p>{warningMessage}</p>
                <div className="flex justify-center pt-2 m--2">
                    <Button
                        className="m-2 rounded hover-bg-primary px-6"
                        variant="outlined"
                        color="primary"
                        onClick={(e) => handleWarningAction(e)}
                    >
                        {t("common:popup.ok")}
                    </Button>
                </div>
            </div>
        </Dialog>

        <Backdrop className={classes.backdrop} open={loading}>
            <CircularProgress color="inherit" />
        </Backdrop>

    </React.Fragment >
};

export default withErrorHandler(OrderTerminationFormDetails);