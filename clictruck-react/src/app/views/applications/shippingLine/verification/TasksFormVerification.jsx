import { Divider, Grid, Paper, Tabs, Typography } from "@material-ui/core";
import { Snackbar } from "@material-ui/core";
import AssignmentReturnOutlinedIcon from '@material-ui/icons/AssignmentReturnOutlined';
import LibraryAddCheckOutlinedIcon from '@material-ui/icons/LibraryAddCheckOutlined';
import LiveHelpOutlinedIcon from '@material-ui/icons/LiveHelpOutlined';
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

import C1Alert from "app/c1component/C1Alert";
import C1Dialog from "app/c1component/C1Dialog";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import { JobStates, Roles, Status } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { tabScroll } from "app/c1utils/styles";
import { formatDate, isArrayNotEmpty, previewPDF } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import DoQuery from "../doClaimJobs/doDocumentVerification/DoQuery";
import TasksFormDocReturned from "./tabs/TasksFormDocReturned";
import TasksFormDocVerification from "./tabs/TasksFormDocVerification";

// import TasksFormPayVerification from "./tabs/TasksFormPayVerification";

const TasksFormVerification = () => {
    const { t } = useTranslation(["verification"]);

    const tabList = [
        { text: t("verification:tabs.docsverification"), icon: <LibraryAddCheckOutlinedIcon /> },
        { text: t("verification:tabs.docsreturned"), icon: <AssignmentReturnOutlinedIcon /> },
        // { text: "DO Payments", icon: <PaymentOutlinedIcon /> },
        { text: t("verification:tabs.docsquery"), icon: <LiveHelpOutlinedIcon /> }
    ];
    const history = useHistory();
    const { user } = useAuth();

    const [tabIndex, setTabIndex] = useState(0);


    /** ------------------ States ---------------------------------*/

    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();

    const [loading, setLoading] = useState(false);
    const [filter, setFilter] = useState({ confirmedDate: new Date(), ffJobId: "" });
    const [retDocFilter, setRetDocFilter] = useState({ ffJobId: "", doiFfId: "", bl: "" });
    const [docsRecords, setDocsRecords] = useState({ list: [] });
    const [retDocsRecords, setRetDocsRecords] = useState({ list: [] });
    const [jobsToDate, setJobstoDate] = useState([]);
    const [readOnlyData, setReadOnlyData] = useState({ selectedJobState: "", selectedFfName: "", verifiedJobs: "" });
    //holder for the selected job to be used in verification
    const [selectedFfJob, setSelectedFfJob] = useState({});
    const [selectedFfRetJob, setSelectedFfRetJob] = useState({});
    const [selectedFfQryJob, setSelectedFfQryJob] = useState({});
    const [controls, setControls] = useState([]);
    const [selectedRowIds, setSelectedRowIds] = useState([]);
    const [hasPendingReturn, setHasPendingReturn] = useState(false);

    const [verifyErrorOpen, setVerifyErrorOpen] = useState({ msg: null, open: false });
    const [verifySubmitConfirm, setVerifySubmitConfirm] = useState({ action: "", open: false });
    const [verifyReturnConfirm, setVerifyReturnConfirm] = useState({ action: "", open: false });
    const [payVerifySubmitConfirm, setPayVerifySubmitConfirm] = useState({ action: "", open: false });

    const [qryFilter, setQryFilter] = useState({ confirmedDate: new Date(), queryJobId: "" });
    const [qryRecords, setQryRecords] = useState({ list: [] });
    const [qryJobsToDate, setQryJobsToDate] = useState([]);
    const [qryReadOnlyData, setQryReadOnlyData] = useState({
        selectedJobState: "", selectedFfName: "", selectedJob: "", parentJob: ""
    });
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);

    const [isShowVerifyIcon, setShowVerifyIcon] = useState(true);

    const [blNo, setBlNo] = useState("");

    const initialButtons = {
        back: { show: false, eventHandler: () => handleExitOnClick() },
        verify: { show: false, eventHandler: null },
        reverify: { show: false, eventHandler: null }
    };

    const [verifyDocSuccess, setVerifyDocSuccess] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        susuccessMsg: t("verification:msg.docsretverified"),
        severity: 'success'
    });

    //initialize both tabs not allowed to access first until all rendered
    const [allowedAccess, setAllowedAccess] = useState({ docsTab: false, retDocsTab: false, queryTab: false });
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
            docsTab: user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code),
            retDocsTab: user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code),
            // payTab: user?.authorities.some(el => el?.authority === Roles.FINANCE.code),
            // Do not show query tab on payment verification
            queryTab: user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code),
        });
        setTabIndex(user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code) ? 0 : 1);
        if (user?.authorities.some(el => [Roles.FINANCE.code].includes(el.authority))
            && !user?.authorities.some(el => [Roles.OPERATIONS.code].includes(el.authority))) {
            history.push('/applications/payments/do');
        }
    }, []);


    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(isLoading);
            switch (urlId) {
                case "fetchControls": {
                    setControls([...res.data]);
                    // reset jobsToDate to empty to not included previously selected jobsToDate
                    // setJobstoDate([])
                    reloadJobsSelection(filter?.confirmedDate);
                    break;
                }

                case "getRecords": {
                    let jobAttachList = [...res?.data?.jobAttchList];
                    setDocsRecords({ list: jobAttachList });
                    setSelectedRowIds([]);
                    break;
                }

                case "getRecordsByBlNo": {
                    console.log(" getRecordsByBlNo ", res?.data)
                    let retRecords = [];
                    let respAaData = [...res?.data];

                    let test = respAaData?.map(atEl => {
                        if (isArrayNotEmpty(retRecords) && retRecords?.find(a => a.attId === atEl.attId)) {
                            //do not add in retRecords
                        } else {
                            if (isArrayNotEmpty(retRecords)
                                && retRecords?.find(a => a.tmstAttType.mattId === atEl.tmstAttType.mattId
                                    && a.attBlNo === atEl.attBlNo)) {
                            } else {
                                if (atEl?.attReturnFlag === 'Y' && atEl?.attDtReturned === null && atEl?.attBlNo.includes(blNo)) {
                                    setRetDocFilter({ ffJobId: atEl?.tckJob?.jobId, doiFfId: atEl?.relatedDoiFfId, bl: blNo })
                                    // setSelectedFfRetJob(atEl?.relatedDoiFfId);
                                    return retRecords.push(atEl);
                                }
                            }
                        }
                    });


                    setRetDocsRecords({ list: [...retRecords] });
                    break;
                }
                case "getQryRecords": {
                    setQryRecords({ list: [...res?.data] })
                    break;
                }

                case "deleteQuery": {
                    setRefresh(true);
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        successMsg: t("verification:msg.qryDeleted")
                    });
                    setSubmitSuccess(true);
                    sendRequest(`/api/v1/clickargo/clicdo/job/doiFf/${qryReadOnlyData?.selectedJob}`, "getQueries", "get");
                    break;
                }

                case "getFfDetails": {
                    setReadOnlyData({
                        ...readOnlyData,
                        selectedJobState: res?.data?.tckJob?.tckMstJobState?.jbstDesc,
                        selectedFfName: res?.data?.tcoreAccn?.accnName
                    });

                    setSelectedFfJob({ ...selectedFfJob, ...res?.data });
                    //reload db?
                    if (filter?.ffJobId) {
                        //didn't add jobstate as it is filtering by ffjobid and the selection is already filtered
                        let url = `/api/v1/clickargo/clicdo/job/doiFf/${filter?.ffJobId}`
                        sendRequest(url, "getRecords", "get");
                    }
                    break;
                }

                case "getQueries": {
                    setQryReadOnlyData({
                        ...qryReadOnlyData,
                        selectedJobState: res?.data?.tckJob?.tckMstJobState?.jbstDesc,
                        selectedFfName: res?.data?.tcoreAccn?.accnName,
                        selectedJob: res?.data?.jobId,
                        parentJob: res?.data?.tckJob?.jobId
                    });
                    setSelectedFfQryJob({ ...selectedFfQryJob, ...res?.data });
                    //reload db?
                    if (qryFilter?.queryJobId) {
                        //didn't add jobstate as it is filtering by ffjobid and the selection is already filtered
                        let url = "/api/v1/clickargo/query/job/list/" + res?.data?.tckJob?.jobId;
                        sendRequest(url, "getQryRecords", "get");
                    }
                    break;
                }

                case "getJobsToDate": {
                    if (res?.data?.jobsToDateList) {
                        setJobstoDate([...jobsToDate, ...res?.data?.jobsToDateList]);
                        // setQryJobsToDate([...qryJobsToDate, ...res?.data?.jobsToDateList])
                    }

                    //update readonly data
                    setReadOnlyData({ ...readOnlyData, verifiedJobs: res?.data?.ctrVerified + "/" + res?.data?.totalJobs })
                    break;
                }

                case "getQryJobsToDate": {
                    if (res?.data?.jobsToDateList) {
                        setQryJobsToDate([...qryJobsToDate, ...res?.data?.jobsToDateList])
                    }
                    //update readonly data
                    setQryReadOnlyData({ ...qryReadOnlyData })
                    break;
                }

                case "verifyDocs": {
                    setVerifySubmitConfirm({ ...verifySubmitConfirm, open: false });

                    // Reset filters after verify docs
                    setFilter({ ...filter, ffJobId: "" });
                    setQryFilter({ ...qryFilter, queryJobId: "" });
                    setJobstoDate([]);
                    reloadJobsSelection(filter?.confirmedDate);
                    // setQryJobsToDate([]);
                    // reloadQryJobsSelection(qryFilter?.confirmedDate);
                    setVerifyDocSuccess(true);
                    setSnackBarState({ ...snackBarState, open: true, success: true, successMsg: t("verification:msg.docsverified") });
                    setRetDocFilter({ ffJobId: "", doiFfId: "", bl: "" });
                    setBlNo("");
                    setDocsRecords({ ...docsRecords.list, list: [] });
                    setQryRecords({ ...qryRecords.list, list: [] })
                    setRetDocsRecords({ ...retDocsRecords.list, list: [] });
                    setHasPendingReturn(false);
                    break;
                }
                case "getFfRetJob": {
                    let doiFfJob = res?.data;
                    setSelectedFfRetJob(doiFfJob);
                    sendRequest(`/api/v1/clickargo/clicdo/job/doiFf/${doiFfJob?.tckJob?.jobId}`, "verifyReturnDocs", "put", { ...doiFfJob, requiredReturnList: selectedRowIds, blNumber: blNo, action: "VERIFY_RETURNED_DOCS" });
                    break;
                }
                case "verifyReturnDocs": {
                    setVerifyReturnConfirm({ ...verifyReturnConfirm, open: false });
                    // Reset filters after verify docs
                    setFilter({ ...filter, ffJobId: "" });
                    setQryFilter({ ...qryFilter, queryJobId: "" });
                    setJobstoDate([]);
                    reloadJobsSelection(filter?.confirmedDate);
                    // setQryJobsToDate([]);
                    // reloadQryJobsSelection(qryFilter?.confirmedDate);
                    setVerifyDocSuccess(true);
                    setSnackBarState({ ...snackBarState, open: true, success: true, successMsg: t("verification:msg.docsretverified"), });
                    setRetDocFilter({ ffJobId: "", doiFfId: "", bl: "" });
                    setBlNo("");
                    setDocsRecords({ ...docsRecords.list, list: [] });
                    setQryRecords({ ...qryRecords.list, list: [] })
                    setRetDocsRecords({ ...retDocsRecords.list, list: [] });
                    break;
                }
                case "downloadFile": {
                    viewFile(res?.data?.attName, res?.data?.attData);
                    setLoading(false)
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
                        successMsg: "Payments Verified!"
                    });
                    setSubmitSuccess(true);
                    break;
                }
                default: break;
            }


        }
    }, [urlId, isLoading, isFormSubmission, res, error]);

    /** ---------------- Event handlers ----------------- */
    const reloadJobsSelection = (date) => {
        if (date) {
            let url = "/api/v1/clickargo/clicdo/vrf/doiffdo/ffjobs?confirmed_date=" + moment(date).format('YYYY/MM/DD');
            sendRequest(url, "getJobsToDate", "get");
        }
    }

    const reloadQryJobsSelection = (date) => {
        if (date) {
            let url = "/api/v1/clickargo/clicdo/vrf/doiffdo/ffjobs?confirmed_date=" + moment(date).format('YYYY/MM/DD');
            sendRequest(url, "getQryJobsToDate", "get");
        }
    }

    const handleTabChange = (e, value) => {
        // temporary, hide icons on query tab
        if (value === 2) {
            setShowVerifyIcon(false)
            setQryJobsToDate([])
            setTimeout(() => reloadQryJobsSelection(qryFilter?.confirmedDate), 500)
        } else {
            setShowVerifyIcon(true);
        }
        setTabIndex(value);
    };

    const handleInputChange = (e) => {
        let val = e?.target?.value;
        setFilter({ ...filter, [e?.target?.name]: val });

        if (e?.target?.name === "ffJobId" && val) {
            //call doiffdo to get some details
            sendRequest(`/api/v1/clickargo/clicdo/job/doiFf/${e?.target?.value}`, "getFfDetails", "get");
        } else if (!val) {
            //reset readonly data
            setReadOnlyData({ ...readOnlyData, selectedJobState: "", selectedFfName: "" });
            //reset db
            setDocsRecords({ ...docsRecords.list, list: [] });
        }
    }

    const handleSearchChange = (e) => {
        let val = e?.target?.value;
        setBlNo(val);
    };

    const handleDocSearchByBl = (e) => {
        if (blNo) {
            let url = `/api/v1/clickargo/clicdo/vrf/doiffdo/docs/bl/${blNo}`;
            sendRequest(url, "getRecordsByBlNo", "get");
        } else {
            // setRetDocsRecords({ ...retDocsRecords.list, list: [] });
            setQryJobsToDate([])
        }
    }

    const handleDateChange = (name, e) => {
        setFilter({ ...filter, [name]: e, ffJobId: "" });
        setReadOnlyData({ selectedJobState: "", selectedFfName: "" });
        // setDocsRecords({ ...docsRecords.list, list: [] });
        setDocsRecords({ list: [] });
        // reset jobsToDate to empty to not included previously selected jobsToDate
        setJobstoDate([]);
        // setQryJobsToDate([]);
        reloadJobsSelection(e);
        setHasPendingReturn(false);
    };

    const handleQueryDateChange = (name, e) => {
        setQryFilter({ ...qryFilter, [name]: e, queryJobId: "" });
        setQryReadOnlyData({ selectedJobState: "", selectedFfName: "", selectedJob: "", parentJob: "" });
        setQryRecords({ list: [] });
        // reset jobsToDate to empty to not included previously selected jobsToDate
        // setJobstoDate([]);
        setQryJobsToDate([]);
        reloadQryJobsSelection(e);
    };

    const handleQueryInputChange = (e) => {
        let val = e?.target?.value;
        setQryFilter({ ...qryFilter, [e?.target?.name]: val });

        if (e?.target?.name === "queryJobId" && val) {
            sendRequest(`/api/v1/clickargo/clicdo/job/doiFf/${e?.target?.value}`, "getQueries", "get");
        } else if (!val) {
            //reset readonly data
            setQryReadOnlyData({ ...qryReadOnlyData, selectedJobState: "", selectedFfName: "", selectedJob: "", parentJob: "" });
            //reset db
            setQryRecords({ list: [] });
        }
    }

    let queryUrl = "/api/v1/clickargo/query/job";
    const handleDeleteQuery = (qryId) => {
        setRefresh(false);
        sendRequest(`${queryUrl}/${qryId}`, "deleteQuery", "DELETE");
    }

    const handleExitOnClick = () => {
        console.log("no  exit for now..");
        //history.push("/applications/documents/do");
    }

    const handleVerifyConfirmAction = (e) => {
        e.preventDefault();
        setVerifySubmitConfirm({ ...verifySubmitConfirm, action: null, open: false })
        setRefresh(false)
        //TODO call api to verify ff
        sendRequest(`/api/v1/clickargo/clicdo/job/doiFf/${filter?.ffJobId}`, "verifyDocs", "put", { ...selectedFfJob, requiredReturnList: selectedRowIds, action: "VERIFY_DOCS" });
    }

    const handleVerifyReturnedDocsAction = (e) => {
        e.preventDefault();
        setVerifyReturnConfirm({ ...verifyReturnConfirm, action: null, open: false })
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clicdo/job/doiFf/${retDocFilter?.doiFfId}`, "getFfRetJob", "get", {});
    }

    const handlePayVerifyConfirmAction = (e) => {
        e.preventDefault();
        setPayVerifySubmitConfirm({ ...payVerifySubmitConfirm, action: null, open: false })
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clicdo/payment-verification/verify/${inputs?.query?.date}`, "paymentVerification", "put", null);
    }

    const handleVerifyOnClick = (action) => {
        if (action === "verify_docs") {
            console.log("!selectedFfJob?.jobDtDocVerified", selectedFfJob);
            if (!filter?.ffJobId) {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: t("verification:msg.noselected"), open: true });
            } else if (selectedFfJob?.jobDtDocVerified) {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: t("verification:msg.jobalreadyverified"), open: true });
            } else if (selectedFfJob?.jobDocVerifiedState === 'Pending Return') {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: t("verification:msg.nodocstoverify"), open: true });
            } else {
                setVerifySubmitConfirm({ ...verifySubmitConfirm, action: "verify", open: true });
            }
            // } else if (action === "verify_returned_docs" && tabIndex === 0) {
            //     if (!filter?.ffJobId) {
            //         setVerifyErrorOpen({ ...verifyErrorOpen, msg: "Please select a job to verify!", open: true });
            //     } else if (selectedFfJob?.jobDtDocVerified) {
            //         setVerifyErrorOpen({ ...verifyErrorOpen, msg: "Job is already verified!", open: true });
            //     } else {
            //         setVerifyReturnConfirm({ ...verifyReturnConfirm, action: "verify", open: true });
            //     }
        } else if (action === "verify_returned_docs" && tabIndex === 1) {
            if (!retDocFilter?.ffJobId || !retDocFilter?.bl || retDocsRecords?.list?.length === 0) {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: t("verification:msg.noretdocstoverify"), open: true });
            } else if (selectedFfRetJob?.jobDtDocVerified) {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: t("verification:msg.jobalreadyverified"), open: true });
            } else {
                setVerifyReturnConfirm({ ...verifyReturnConfirm, action: "verify", open: true });
            }
        } else if (action === "verify_payment") {
            if (inputs?.info?.totalDo === 0) {
                setVerifyErrorOpen({ ...verifyErrorOpen, msg: "No Payment to verify!", open: true });
            } else if (inputs?.info?.totalDo && inputs?.info?.totalDo > 0) {
                setPayVerifySubmitConfirm({ ...payVerifySubmitConfirm, action: "verify", open: true });
            }
        }
    }

    const eventHandler = (action) => {
        if (action.toLowerCase() === "verify") {
            if (tabIndex === 0) { handleVerifyOnClick("verify_docs") }
            if (tabIndex === 1) { handleVerifyOnClick("verify_returned_docs") }
        } else if (action.toLowerCase() === "exit") {
            handleExitOnClick();
        }
    };


    const handleViewFile = (e, attId) => {
        setLoading(true)
        sendRequest(`/api/v1/clickargo/attachments/job/${attId}`, "downloadFile");
    };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
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

    let bcLabel = user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code) ? t("verification:form.bctitledocsver") : t("verification:form.bctitlepayver");
    let breadcrumb = user?.authorities.some(el => el?.authority === Roles.OPERATIONS.code) ? t("verification:breadcrumb.documents") : t("verification:breadcrumb.payments");
    let formButtons;
    if (!loading && isShowVerifyIcon) {
        formButtons = (
            <C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)} />
        );
    }
    if (tabIndex === 0) {
        formButtons = <C1FormButtons options={{
            ...initialButtons,
            verify: {
                show:
                    selectedFfJob?.jobDocVerifiedState !== 'Pending Return' &&
                    // if removed, will show the icon, but will show error message 
                    docsRecords?.list?.length > 0 && !selectedFfJob?.jobDtDocVerified,
                eventHandler: () => handleVerifyOnClick("verify_docs")
            },
            reverify: { show: false, eventHandler: null }
        }} />
    }
    if (tabIndex === 1) {
        formButtons = <C1FormButtons options={{
            ...initialButtons,
            verify: { show: false, eventHandler: null },
            reverify: {
                show: hasPendingReturn === true
                    // if removed, will show the icon, but will show error message 
                    && retDocsRecords?.list?.length > 0,
                eventHandler: () => handleVerifyOnClick("verify_returned_docs")
            }
        }} />
    }

    const handleCloseSnackBar = () => {
        setSnackBarState({ ...snackBarState, open: false });
    };

    let snackBar = null;
    if (verifyDocSuccess) {
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
                                        //if docs and docsTab is true
                                        if (ind === 0 && !allowedAccess?.docsTab)
                                            return null;
                                        if (ind === 1 && !allowedAccess?.docsTab)
                                            return null;
                                        // Do not show query tab on payment verification
                                        if (ind === 2 && !allowedAccess?.queryTab)
                                            return null;
                                        return (
                                            <TabsWrapper
                                                style={ind === 4 ? { backgroundColor: '#e4effa' } : {}}
                                                className="capitalize"
                                                value={ind}
                                                // disabled={ind === 2 ? true : false}
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
                            {tabIndex === 0 && allowedAccess?.docsTab && <C1TabInfoContainer guideId="clicdo.doi.sl.doc.verify.list">
                                <TasksFormDocVerification filter={filter}
                                    handleInputChange={handleInputChange}
                                    handleDateChange={handleDateChange}
                                    records={docsRecords}
                                    setDocsRecords={setDocsRecords}
                                    jobListToDate={jobsToDate}
                                    readOnlyData={readOnlyData}
                                    isDocsVerified={selectedFfJob?.jobDtDocVerified}
                                    isDocsPendingReturn={selectedFfJob?.jobDocVerifiedState === 'Pending Return'}
                                    loading={loading}
                                    handleViewFile={handleViewFile}
                                    selectedRowIds={selectedRowIds}
                                    setSelectedRowIds={setSelectedRowIds}
                                    setHasPendingReturn={setHasPendingReturn}
                                    selectedFfJob={selectedFfJob} /></C1TabInfoContainer>}

                            {tabIndex === 1 && allowedAccess?.retDocsTab && <C1TabInfoContainer guideId="clicdo.doi.sl.doc.verify.list">
                                <TasksFormDocReturned filter={retDocFilter}
                                    handleInputChange={handleInputChange}
                                    handleSearchChange={handleSearchChange}
                                    handleDocSearchByBl={handleDocSearchByBl}
                                    blNo={blNo}
                                    handleDateChange={handleDateChange}
                                    records={retDocsRecords}
                                    jobListToDate={jobsToDate}
                                    readOnlyData={readOnlyData}
                                    isDocsVerified={selectedFfJob?.jobDtDocVerified}
                                    loading={loading}
                                    handleViewFile={handleViewFile}
                                    setHasPendingReturn={setHasPendingReturn} /></C1TabInfoContainer>}

                            {/* {tabIndex === 2 && allowedAccess?.payTab && <C1TabInfoContainer guideId="clicdo.doi.sl.pay.verify.list">
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
                            </C1TabInfoContainer>} */}

                            {tabIndex === 2 && <C1TabInfoContainer guideId="clicdo.doi.sl.claim.jobs.tabs.query">
                                <DoQuery filter={qryFilter}
                                    qryJobListToDate={qryJobsToDate}
                                    qryReadOnlyData={qryReadOnlyData}
                                    qryRecords={qryRecords}
                                    setQryRecords={setQryRecords}
                                    selectedFfJob={selectedFfQryJob}
                                    handleDeleteQuery={handleDeleteQuery}
                                    handleQueryDateChange={handleQueryDateChange}
                                    handleQueryInputChange={handleQueryInputChange}
                                /></C1TabInfoContainer>}

                        </Paper>
                    </Grid>
                </Grid>
            )}
        </C1FormDetailsPanel>
        {/** Confirmation for verify button */}
        {snackBar}
        <ConfirmationDialog
            open={verifySubmitConfirm?.open}
            onConfirmDialogClose={() => setVerifySubmitConfirm({ ...verifySubmitConfirm, action: null, open: false })}
            text={t("verification:msg.confirmverify", { action: "VERIFY", jobId: filter?.ffJobId })}
            title={t("verification:msg.confirmTitle")}
            disabled={isLoading}
            onYesClick={(e) => handleVerifyConfirmAction(e)} />
        <ConfirmationDialog
            open={verifyReturnConfirm?.open}
            onConfirmDialogClose={() => setVerifyReturnConfirm({ ...verifyReturnConfirm, action: null, open: false })}
            text={t("verification:msg.confirmretverify", { action: "VERIFY", blNo: retDocFilter?.bl })}
            title={t("verification:msg.confirmTitle")}
            disabled={isLoading}
            onYesClick={(e) => handleVerifyReturnedDocsAction(e)} />
        <ConfirmationDialog
            open={payVerifySubmitConfirm?.open}
            onConfirmDialogClose={() => setPayVerifySubmitConfirm({ ...payVerifySubmitConfirm, action: null, open: false })}
            text={`Are you sure you want to ${payVerifySubmitConfirm?.action} Payments on this date ${formatDate(inputs.query.date)}?`}
            title="Confirmation"
            disabled={isLoading}
            onYesClick={(e) => handlePayVerifyConfirmAction(e)} />

        {/** Dialog popup for validation upon form button clicks*/}
        <C1DialogPrompt
            confirmationObj={{
                openConfirmPopUp: verifyErrorOpen?.open,
                onConfirmationDialogClose: () => setVerifyErrorOpen({ ...verifyErrorOpen, open: false }),
                text: verifyErrorOpen?.msg,
                title: "Error",
                onYesClick: () => setVerifyErrorOpen({ ...verifyErrorOpen, open: false }),
                yesBtnText: "Ok",
            }} />
    </React.Fragment>

    );
};

export default withErrorHandler(TasksFormVerification);