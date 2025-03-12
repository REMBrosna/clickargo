import { AppBar, Backdrop, Button, CircularProgress, Dialog, Divider, Grid, makeStyles, Paper, Tabs, Toolbar } from "@material-ui/core";
import AccessTimeOutlinedIcon from '@material-ui/icons/AccessTimeOutlined';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import DriveEtaOutlinedIcon from '@material-ui/icons/DriveEtaOutlined';
import LocalShippingOutlinedIcon from '@material-ui/icons/LocalShippingOutlined';
import NearMeOutlinedIcon from '@material-ui/icons/NearMeOutlined';
import TocOutlinedIcon from '@material-ui/icons/TocOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import _ from "lodash";
import moment from "moment";
import React, { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { useLocation } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1DialogPrompt from "app/c1component/C1DialogPrompt";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1IconButton from "app/c1component/C1IconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import C1TextArea from "app/c1component/C1TextArea";
import C1Warning from "app/c1component/C1Warning";
//connect api using axios from useHttp
import useHttp from "app/c1hooks/http";
import useQuery from "app/c1hooks/useQuery";
import { AccountTypes, Actions, JobStates, Roles, T_CK_CT_DRV, T_CK_CT_VEH } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { decryptText, encryptText, getValue, isEditable, previewPDF, Uint8ArrayToString } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from 'app/hooks/useAuth';
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

import JobTruckContext from "../applications/job/form/JobTruckContext";
import JobAuthLetters from "../applications/job/form/tabs/JobAuthLetters";
import JobDeliveryOrders from "../applications/job/form/tabs/JobDeliveryOrders";
import JobDomesticInvoice from "../applications/job/form/tabs/JobDomesticInvoice";
import JobDriverAssign from '../applications/job/form/tabs/JobDriverAssign';
import JobInvoice from "../applications/job/form/tabs/JobInvoice";
import JobNewDetails from "../applications/job/form/tabs/JobNewDetails";
import JobRejectRemarks from "../applications/job/form/tabs/JobRejectRemarks";
import JobTrack from "../applications/job/form/tabs/JobTrack"
import JobTripCharges from "../applications/job/form/tabs/JobTripCharges";
import JobTripChargesDomestic from "../applications/job/form/tabs/JobTripChargesDomestic";

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: theme.zIndex.drawer + 1,
        color: '#fff',
    },
    noSessionBg: {
        backgroundColor: "#fff"
    }
}));

/**
 * @description This is the non-session version of JobTruckFormDetails.
 */
const TruckFormNoSessionDetails = () => {

    let { jobId, accnId, role, validDate } = useParams();
    let viewType = "view";

    const { t } = useTranslation(["job", "common", "cargoowners", "listing", "buttons"]);

    const bdClasses = useStyles();
    const history = useHistory();
    const { user } = useAuth();

    let location = useLocation();


    const shipmentType = history?.location?.state?.shipmentType;
    const query = useQuery()
    const isTruckingOperator = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code;
    const isSuspended = user?.coreAccn?.accnStatus === "S";

    //tablist ----------------------------------------------------------------------------------------------------------------------------*/
    const tabList = [
        { id: "jobDetails", text: t("job:tabs.jobDetails"), icon: <WorkOutlineOutlinedIcon /> },
        { id: "fmTrip", text: t("job:tabs.tripCargos"), icon: <LocalShippingOutlinedIcon /> },
        { id: "mmTrip", text: t("job:tabs.tripCargos"), icon: <LocalShippingOutlinedIcon /> },
        { id: "documents", text: t("job:tabs.documents"), icon: <DescriptionOutlinedIcon /> },
        { id: "driver", text: t("job:tabs.driver"), icon: <LocalShippingOutlinedIcon /> },
        { id: "deliveryOrders", text: t("job:tabs.deliveryOrders"), icon: <TocOutlinedIcon /> },
        { id: "invoice", text: "Invoice", icon: <DescriptionOutlinedIcon /> },
        { id: "midMileInvoice", text: "Invoice", icon: <DescriptionOutlinedIcon /> },
        { id: "rejectRemarks", text: t("job:tabs.remarks"), icon: <DescriptionOutlinedIcon /> },
        { id: "audit", text: t("job:tabs.audit"), icon: <AccessTimeOutlinedIcon /> },
        { id: "tracking", text: t("job:tabs.tracking"), icon: <DriveEtaOutlinedIcon /> }
    ];

    const [tabIndex, setTabIndex] = useState(0);
    const [isDisabled, setDisabled] = useState(isEditable(viewType));
    const [jobState, setJobState] = useState(JobStates.NEW.code);
    //initialize to not show fmtrip and mmtrip first
    const [showTabs, setShowTabs] = useState({ driver: false, invoice: false, fmTrip: false, mmTrip: false, midMileInvoice: false, deliveryOrders: false, tracking: false });

    const jobTripChargesRef = useRef();
    const invoiceRef = useRef();


    const [start, setStart] = useState(false);
    const [stop, setStop] = useState(false);
    const [startErrorOpen, setStartErrorOpen] = useState({ msg: null, open: false });
    const [stopErrorOpen, setStopErrorOpen] = useState({ msg: null, open: false });
    const [duplicateErrorOpen, setDuplicateErrorOpen] = useState({ msg: null, open: false });
    const [customErrorOpen, setCustomErrorOpen] = useState({ title: "Error", msg: null, open: false });

    /** ------------------ States ---------------------------------*/

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
    const [controls, setControls] = useState([]);
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({ action: null, open: false });
    const [validationErrors, setValidationErrors] = useState({});
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");
    const [fileUploaded, setFileUploaded] = useState(false);
    const [tcrData, setTcrData] = useState({ tripCargoMmList: [], reimbursements: [] });
    const [tcrList, setTcrList] = useState({ list: [] });
    const [totalTripCharges, setTotalTripCharges] = useState({})
    const [invoiceData, setInvoiceData] = useState({})

    const [isRefresh, setRefresh] = useState(false);
    const [doId, setDoId] = useState("");
    const [dlOpen, setDlOpen] = useState(false);
    const [openAddPopUp, setOpenAddPopUp] = useState(false);
    const [rejectRemarks, setRejectRemarks] = useState({ open: false, msg: null });
    const [jobRemarks, setJobRemarks] = useState({ open: false, msg: null, remarksType: 'V' });

    const [popUpFieldError, setPopUpFieldError] = useState({});
    const popupDoDefaultValue = {
        doNo: "",
        tckCtTrip: {
            trId: ""
        },
    }
    const [popUpDoDetails, setPopUpDoDetails] = useState(popupDoDefaultValue);
    const [domestic, setDomestc] = useState();
    const isToFinance = user?.authorities.some(item => item.authority === Roles?.FF_FINANCE?.code);
    const [warningProps, setWarningProps] = useState({ open: false, msg: "" })

    const popupAttDefaultValue = {
        doId: "",
        tckCtTrip: {
            trId: ""
        },
        doaName: "",
        doaData: null
    }
    const [popUpAttDetails, setPopUpAttDetails] = useState(popupAttDefaultValue);

    //initial button handler --------------------------------------------------------------------------------------------------
    const initialButtons = {
        // back: { show: true, eventHandler: () => handleExitOnClick() },
        // save: { show: viewType === 'edit' || viewType === 'new', eventHandler: () => handleSaveOnClick() }
    };

    const truckUrlApi = `/api/v1/clickargo/clictruck/job/truck/`;

    /** ------------------- Update states ----------------- */
    useEffect(() => {

        setSnackBarOptions(defaultSnackbarValue);
        setLoading(true);
        setFileUploaded(true);
        sendRequest(`/api/v1/clickargo/clictruck/job/ns/truck/view/${jobId}/${validDate}`, "getJob", "GET", null);

        // eslint-disable-next-line
    }, [jobId, viewType]);


    useEffect(() => {

        if (!isLoading && !error && res && !validation) {
            setLoading(isLoading);
            switch (urlId) {
                case "getJob": {
                    let data = res.data;
                    let shipmentTypeData = data?.tckJob?.tckMstShipmentType?.shtId;
                    setInputData({ ...data, shipmentType: shipmentType ? shipmentType : shipmentTypeData });

                    let jobState = data?.tckJob?.tckMstJobState?.jbstId;
                    let shipmentTypeState = data?.tckJob?.tckMstShipmentType?.shtName

                    if (shipmentTypeState === "DOMESTIC") {
                        setShowTabs({
                            ...showTabs, driver: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.CAN.code].includes(jobState),
                            // invoice: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.ACP.code, JobStates.ASG.code, JobStates.ONGOING.code].includes(jobState),
                            midMileInvoice: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.ACP.code, JobStates.ASG.code, JobStates.ONGOING.code, JobStates.CAN.code].includes(jobState),
                            fmTrip: !data?.domestic, mmTrip: data?.domestic,
                            deliveryOrders: [JobStates.ASG.code, JobStates.ONGOING.code, JobStates.PAUSED.code, JobStates.DLV.code].includes(jobState),
                            tracking: [JobStates.ONGOING.code, JobStates.DLV.code, JobStates.BILLED.code, JobStates.VER.code, JobStates.APP.code].includes(jobState)
                        });
                    } else {
                        setShowTabs({
                            ...showTabs, driver: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.CAN.code].includes(jobState),
                            invoice: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.ACP.code, JobStates.ASG.code, JobStates.ONGOING.code, JobStates.CAN.code].includes(jobState),
                            // midMileInvoice: ![JobStates.NEW.code, JobStates.SUB.code, JobStates.ACP.code, JobStates.ASG.code, JobStates.ONGOING.code].includes(jobState),
                            fmTrip: !data?.domestic, mmTrip: data?.domestic,
                            deliveryOrders: [JobStates.ASG.code, JobStates.ONGOING.code, JobStates.PAUSED.code, JobStates.DLV.code].includes(jobState),
                            tracking: [JobStates.ONGOING.code, JobStates.DLV.code, JobStates.BILLED.code, JobStates.VER.code, JobStates.APP.code].includes(jobState)
                        });
                    }
                    setJobState({ ...jobState, ...data?.tckJob?.tckMstJobState?.jbstId });
                    let crntJobState = data?.tckJob?.tckMstJobState?.jbstId === "INVVER" ? "VER" : data?.tckJob?.tckMstJobState?.jbstId;

                    let jobIdStr = data?.jobId;

                    const reqBody = {
                        entityType: "JOB_TRUCK",
                        entityState: crntJobState,
                        page: viewType.toUpperCase(),
                    };
                    if (query && query.get("tabIndex")) {
                        setTabIndex((parseInt(query.get("tabIndex"))))
                    }

                    setDomestc(data?.domestic);
                    sendRequest(`/api/v1/clickargo/controls/${jobIdStr}/${accnId}/${role}`, "fetchControls", "POST", reqBody);
                    break;
                }
                case "updateJob": {
                    let data = res.data
                    setInputData({ ...data });
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("cargoowners:msg.updateSuccess"),
                    });
                    break;
                }
                case "fetchControls": {
                    let tmp = removeSessionButtons(res.data);
                    console.log("fetchControls", tmp);
                    setControls(tmp);
                    break;
                }
                case "download": {
                    viewFile(res?.data?.attName, res?.data?.attData);
                    break;
                }

                case "reject_bill":
                case "verify_bill":
                case "acknowledge_bill": {
                    let msg = t("common:common.msg.generalAction", { action: Actions[openSubmitConfirm?.action]?.result });
                    //do not redirect
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: msg
                    });
                    window.location.reload(true);
                    break;
                }

                case "getDriver": {
                    const data = res.data.aaData[0]
                    setInputData({
                        ...inputData,
                        ...inputData.tckCtDrv,
                        tckCtDrv: { ...inputData.tckCtDrv, ...data }
                    })
                    break
                }
                case "getTruck": {
                    const data = res.data.aaData[0]
                    setInputData({
                        ...inputData,
                        ...inputData.tckCtVeh,
                        tckCtVeh: { ...inputData.tckCtVeh, ...data }
                    })
                    break;
                }

                case 'previewDo': {
                    console.log(res?.data)
                    setDlOpen(false);
                    if (res?.data) {
                        previewPDF(res?.data?.doaName, res?.data?.doaData)
                    }
                    break;
                }
                case 'previewUnsigned': {
                    setDlOpen(false);
                    if (res?.data) {
                        previewPDF(res?.data?.doUnsignedName, res?.data?.doUnsignedData)
                    }
                    break;
                }
                case 'previewSigned': {
                    setDlOpen(false);
                    if (res?.data) {
                        previewPDF(res?.data?.doSignedName, res?.data?.doSignedData)
                    }
                    break;
                }
                default: break;
            }
        }

        if (error) {
            //goes back to the screen
            setLoading(false);
        }

        // setOpenWarning(true)
        // setWarningMessage("TEST")
        //If validation has value then set to the errors
        if (validation) {

            setValidationErrors({ ...validation });
            setLoading(false);
            setSnackBarOptions(defaultSnackbarValue);
            let keyList = Object.keys(validation);
            for (let key of keyList) {
                setOpenWarning(true)
                setWarningMessage(validation[key]);
            }
        }

        // eslint-disable-next-line
    }, [urlId, isLoading, isFormSubmission, res, validation, error]);
    /** ---------------- Event handlers ----------------- */

    const removeSessionButtons = (prameterControls) => {
        console.log(prameterControls)
        if (prameterControls) {
            prameterControls = prameterControls.filter(c => !["EXIT", "CLONE"].includes(c.ctrlAction));
        }
        return prameterControls;
    }

    const handleTabChange = (e, value) => {
        setTabIndex(value);
        if (value !== 1) {
            //merge if the tabIndex is not 1, when user move from one tab to the other
            if (jobTripChargesRef?.current?.getTripDetails) {

                //trigger a merge to input data so that trip/cargoes state won't; only when there is change
                setInputData({ ...inputData, tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()] });
            }

        }

        // CT-124 [CO Operations-Trucking Jobs-Domestic] Added Trip Details Goes Away when We Switch Tab
        if (value !== 2) {
            if (jobTripChargesRef?.current?.getTripList) {
                setInputData({ ...inputData, tckCtTripList: jobTripChargesRef?.current?.getTripList(), });
            }
        }

        if (value !== 6) {
            //merge the invoice 
            if (invoiceRef?.current?.getInvoiceData()) {
                setInputData({ ...inputData, toInvoiceList: [invoiceRef?.current?.getInvoiceData()] });
            }
        }
    };


    const handleInputChange = (e) => {
        const elName = e.target.name;
        if (elName === 'shipmentType') {
            setInputData({ ...inputData, "tckJob": { ...inputData['tckJob'], "tckMstShipmentType": { "shtId": e.target.value } } });
        } else if (elName === 'documentType') {
            setInputData({ ...inputData, "documentType": e.target.value });
        } else if (elName === "tckCtDrv.drvName") {
            sendRequest(`${T_CK_CT_DRV}&mDataProp_1=drvLicenseNo&sSearch_1=${e.target.value}`, "getDriver")
        } else if (elName === "tckCtVeh.vhPlateNo") {
            sendRequest(`${T_CK_CT_VEH}&mDataProp_1=vhPlateNo&sSearch_1=${e.target.value}`, "getTruck")
        }

        else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
        }

    };

    const handlePopupInputChange = (e) => {
        const elName = e.target.name;
        if (elName === 'doNo') {
            setPopUpDoDetails({ ...popUpDoDetails, ...deepUpdateState(popUpDoDetails, elName, e.target.value.trim()) });
        }
    }

    const saveTripDo = (doNo, tripId) => {
        setDlOpen(true)
        const popUpDoDetails = { ...popUpDoDetails, tckCtTrip: { trId: tripId }, doNo: doNo }
        if (!doNo) {
            setDlOpen(false);
            return;
        }
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clictruck/tripdo/doCreate`, "createDo", "POST", popUpDoDetails);
    }
    const editTripDo = (doaId, doNo) => {
        setRefresh(false)
        sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDo/doDelete?doaId=${doaId}&doNo=${doNo}`, "deleteTripDo", "DELETE", {});
    }

    const handleDateChange = (name, e) => {
        if (name === 'tckJob.tckRecordDate.rcdDtStart') {
            let startDt = moment(e).format('YYYY/MM/DD');
            let expDt = moment(inputData?.tckJob?.tckRecordDate?.rcdDtExpiry).format('YYYY/MM/DD');
            if (expDt < startDt) {
                setInputData({
                    ...inputData,
                    "tckJob": { ...inputData['tckJob'], "tckRecordDate": { "rcdDtStart": e, "rcdDtExpiry": e } }
                });
            } else {
                setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
            }
        } else if (name === 'tckJob.tckRecordDate.rcdDtExpiry') {
            setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
        } else {
            setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) })
        }
    };

    const handleExitOnClick = () => {
        let prevUrl = history?.location?.state?.from;
        if (prevUrl) {
            history.push(prevUrl);
        } else {
            if (isTruckingOperator)
                history.push("/applications/services/job/to/truck");
            else
                history.push("/applications/services/job/coff/truck");
        }

    }

    const handleSaveOnClick = () => {
        setLoading(true);
        setValidationErrors({});
        switch (viewType) {
            case 'view':
                let data = {};
                if (!domestic) {
                    //reset the action in case from action user decided to click Save instead.
                    data = {
                        ...inputData, ...jobTripChargesRef?.current?.getTripDetails() == null ? null
                            : { tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()] },
                        ...invoiceRef?.current?.getInvoiceData() == null ? null :
                            { toInvoiceList: [invoiceRef?.current?.getInvoiceData()] },
                        action: null
                    }
                    sendRequest(`${truckUrlApi}` + jobId, "updateJob", "PUT", data);

                } else {
                    data = {
                        ...inputData, ...jobTripChargesRef?.current?.getTripList() == null ? null
                            : { tckCtTripList: jobTripChargesRef?.current?.getTripList() },
                        //CT-68 - reset the action in case from action user decided to click Save instead.
                        action: null
                    }
                    sendRequest(`${truckUrlApi}` + jobId, "updateJob", "PUT", data);

                }
                break;
            default:
                break;
        }
    };

    const handleConfirmAction = (e) => {

        if (["VERIFY_BILL", "REJECT_BILL", "ACKNOWLEDGE_BILL", "APPROVE_BILL", "REJECT"].includes(openSubmitConfirm?.action)
            && !jobRemarks?.msg) {
            setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });

            if (["VERIFY_BILL", "ACKNOWLEDGE_BILL", "APPROVE_BILL"].includes(openSubmitConfirm?.action) && jobRemarks?.open === true) {
                setJobRemarks({
                    ...jobRemarks, open: false,
                    remarksType: (openSubmitConfirm?.action === "VERIFY_BILL" ? "V"
                        : (openSubmitConfirm?.action === "APPROVE_BILL" || openSubmitConfirm?.action === "ACKNOWLEDGE_BILL") ? "A"
                            : openSubmitConfirm?.action === "REJECT") ? "J" : "R"
                });
            } else {
                setJobRemarks({
                    ...jobRemarks, open: true,
                    remarksType: (openSubmitConfirm?.action === "VERIFY_BILL" ? "V"
                        : (openSubmitConfirm?.action === "APPROVE_BILL" || openSubmitConfirm?.action === "ACKNOWLEDGE_BILL") ? "A"
                            : openSubmitConfirm?.action === "REJECT") ? "J" : "R"
                });
                return;
            }


        }

        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        setOpenAddPopUp(false)

        let errors = handleValidateFields();
        if (!errors) {
            setLoading(true);

            let reqBody = {};
            if (!domestic) {
                //setting input data in case validation exception is  thrown
                setInputData({
                    ...inputData, action: openSubmitConfirm?.action, ...jobTripChargesRef?.current?.getTripDetails() == null ? null
                        : { tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()] },
                    ...invoiceRef?.current?.getInvoiceData() == null ? null : { toInvoiceList: [invoiceRef?.current?.getInvoiceData()] },
                });
                reqBody = {
                    ...inputData, action: openSubmitConfirm?.action, ...jobTripChargesRef?.current?.getTripDetails() == null ? null
                        : { tckCtTripList: [jobTripChargesRef?.current?.getTripDetails()] },
                    ...invoiceRef?.current?.getInvoiceData() == null ? null : { toInvoiceList: [invoiceRef?.current?.getInvoiceData()] },
                    ...jobRemarks?.msg ? { jobRemarks: jobRemarks?.msg } : null
                }
            } else {
                const domesticsTrips = jobTripChargesRef?.current?.getTripList();
                reqBody = {
                    ...inputData, action: openSubmitConfirm?.action,
                    ...Object.keys(invoiceData).length > 0 ? { tckCtToInvoice: invoiceData } : null,
                    ...jobRemarks?.msg ? { jobRemarks: jobRemarks?.msg } : null,
                    tckCtTripList: domesticsTrips ? domesticsTrips : inputData?.tckCtTripList
                }
                setInputData({ ...inputData, ...reqBody });
            }

            sendRequest(`/api/v1/clickargo/clictruck/job/ns/truck/${inputData?.jobId}/${accnId}/${role}`, openSubmitConfirm.action.toLowerCase(), "PUT", reqBody);
        } else {
            return;
        }
    };

    const handleAction = (e) => {
        handleConfirmAction(e)

    };

    // const downloadDOHandlerUnsigned = () => {
    //     setDlOpen(true);
    //     sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDo/doFileData?id=${doId}&type=unsigned`, "previewUnsigned");
    // };

    // const downloadDOHandlerSigned = () => {
    //     setDlOpen(true);
    //     sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDo/doFileData?id=${doId}&type=signed`, "previewSigned");
    // };

    const downloadDOHandler = (id) => {
        setDlOpen(true);
        sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDoAttach/fileData?id=${id}&type=doAttach`, "previewDo");
    };

    const deleteDOHandler = (id, type) => {
        setRefresh(false)
        setDlOpen(true);
        sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDoAttach/deleteDoAttach?id=${id}&type=${type}`, "deleteAttachment", "DELETE", {});
    }

    const uploadDeliveryOrder = (e, tripId, doNo, type) => {
        e.preventDefault();
        var file = e.target.files[0];
        if (!file)
            return;

        const fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = e => {
            const uint8Array = new Uint8Array(e.target.result);
            var imgStr = Uint8ArrayToString(uint8Array);
            var base64Sign = btoa(imgStr);
            const popUpDoDetails = { ...popUpDoDetails, doaName: file.name, doaData: base64Sign, tckCtTrip: { trId: tripId }, ckCtTripDo: { doNo: doNo } }
            console.log("uploadDeliveryOrder ", uploadDeliveryOrder)
            setRefresh(false)
            setDlOpen(true);
            sendRequest(`/api/v1/clickargo/clictruck/tripdo/doattach?type=${type}`, "uploadFile", "POST", popUpDoDetails);
        };
    }



    const handleWarningAction = (e) => {
        setOpenWarning(false)
        setWarningMessage("")
    };

    const handleWarningPopup = () => {
        setWarningProps({ open: false, msg: "" });
    }

    // const handleSubmitOnClick = () => {
    //     setInputData({ ...inputData, "action": "SUBMIT" });
    //     setOpenSubmitConfirm({ ...openSubmitConfirm, action: "SUBMIT", open: true });
    // }

    // const handleActionOnClick = (actionUp) => {
    //     setInputData({ ...inputData, "action": actionUp });
    //     setOpenSubmitConfirm({ ...openSubmitConfirm, action: actionUp, open: true });
    // }


    const eventHandler = (action) => {
        if ((action.toLowerCase() === "verify_bill")) {
            // if (!isTruckingOperator && isSuspended) { //disable  verify if account suspended
            //     setCustomErrorOpen({ ...customErrorOpen, title: t("job:msg.verifyError"), msg: t("job:msg.accnSuspnd"), open: true });
            // } else {
            setOpenSubmitConfirm({ action: action, open: true });
            // }
        } else {
            setOpenSubmitConfirm({ action: action, open: true });
        }
    };

    const handleValidateFields = () => {
        // console.log("validating");
        return null;
    }

    // const onFileChangeHandler = (e) => {
    //     e.preventDefault();
    //     let file = e.target.files[0];

    //     if (!file) {
    //         // didn't select file
    //         return;
    //     }

    //     let errors = handleSignatureValidate(file.type);
    //     if (Object.keys(errors).length === 0) {
    //         // setFileSrc(URL.createObjectURL(e.target.files[0]));

    //         const fileReader = new FileReader();
    //         fileReader.readAsArrayBuffer(e.target.files[0]);
    //         fileReader.onload = e => {

    //             const uint8Array = new Uint8Array(e.target.result);
    //             if (uint8Array.byteLength === 0) {
    //                 return;
    //             }
    //             let imgStr = Uint8ArrayToString(uint8Array);
    //             // console.log("imgStr 2 ", imgStr.length, imgStr,);
    //             let base64Sign = btoa(imgStr);
    //             // setFileSrc('data:image/png;base64,' + base64Sign);

    //             // uploadFile = { ...uploadFile, attName: file.name, attData: base64Sign, attReferenceid: inputData?.jobId };

    //             setInputData({ ...inputData, "jobAttach": { ...inputData['jobAttach'], attName: file.name, attData: base64Sign } });

    //             //sendRequest(`/api/job/attach/${inputData?.jobId}`, "uploadBL", "put", uploadFile);
    //             setFileUploaded(true);
    //         };
    //     } else {
    //         setValidationErrors(errors);
    //     }
    // };

    // const handleSignatureValidate = (uploadFileType) => {
    //     const errors = {};

    //     if (uploadFileType && uploadFileType !== "application/pdf") {
    //         errors.fileUpload = t("cargoowners:msg.nonPDFNotAllowed")
    //     }
    //     if (uploadFileType === "") {
    //         errors.fileUpload = t("cargoowners:msg.noFileUploded")
    //     }

    //     return errors;
    // };

    // const handleViewFile = (e, attId) => {
    //     if (inputData?.jobAttach?.attData) {
    //         viewFile(inputData?.jobAttach?.attName, inputData?.jobAttach?.attData)
    //     } else {
    //         const url = `/api/v1/clickargo/attachments/job/${attId}`;
    //         sendRequest(url, "download");
    //     }
    // };

    const viewFile = (fileName, data) => {
        previewPDF(fileName, data);
    };




    let bcLabel = t("cargoowners:form.viewJob")

    //--------------------form button ---------------------------------------------------------------------------------------------------------------------
    let formButtons;
    if (!loading) {
        formButtons = (
            <C1FormButtons
                options={{
                    back: {
                        show: true,
                        eventHandler: handleExitOnClick,
                    },
                    save: {
                        show: true,
                        eventHandler: handleSaveOnClick,
                    },
                }}
            />
        );

        if (viewType) {
            switch (viewType) {
                case "view":
                    formButtons = (<C1FormButtons options={getFormActionButton(initialButtons, controls, eventHandler)} />);
                    break;
                default: break;
            }
        }
    }

    return loading ? <MatxLoading /> :
        (<React.Fragment>
            <div style={{ backgroundColor: "#fff", border: "1px solid #fff" }}>
                <C1FormDetailsPanel
                    title={bcLabel}
                    titleStatus={inputData?.tckJob?.tckMstJobState?.jbstId || JobStates.DRF.code.toUpperCase()}
                    initialValues={{ ...inputData }}
                    values={{ ...inputData }}
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
                                        {tabList &&
                                            tabList.map((item, ind) => {
                                                if (item?.id === 'driver' && !showTabs?.driver) 
                                                    return null;
                                                else if (item?.id == "deliveryOrders" && !showTabs?.deliveryOrders)
                                                    return null;
                                                else if (item?.id === 'invoice' && true) // not display invoice tab
                                                    return null;
                                                else if (item?.id === 'midMileInvoice' && true) // not display invoice tab
                                                    return null;
                                                else if (item?.id == 'fmTrip' && !showTabs?.fmTrip)
                                                    return null;
                                                else if (item?.id == 'mmTrip' && !showTabs?.mmTrip)
                                                    return null;
                                                else if (item?.id == 'tracking' && !showTabs?.tracking)
                                                    return null;
                                                else if (item?.id == "rejectRemarks" && !inputData?.hasRemarks)
                                                    return null;
                                                else return (
                                                    <TabsWrapper
                                                        className="capitalize"
                                                        value={ind}
                                                        disabled={item.disabled}
                                                        label={
                                                            <TabLabel
                                                                viewType={viewType}
                                                                tab={item}
                                                                errors={validationErrors} />
                                                        }
                                                        key={ind}
                                                        icon={item.icon}
                                                        {...tabScroll(ind)}
                                                    />
                                                );
                                            })}
                                    </Tabs>
                                    <Divider className="mb-6" />
                                    <JobTruckContext.Provider
                                        value={{
                                            inputData, setInputData, viewType, handleInputChange,
                                            handleDateChange, locale: { t },
                                            isDisabled,
                                            errors: validationErrors,
                                            jobState,
                                            shipmentType,
                                            tcrData,
                                            setTcrData,
                                            //handleInsertTcrList,
                                            tcrList,
                                            totalTripCharges,
                                            invoiceData,
                                            setInvoiceData,
                                            setOpenWarning,
                                            setWarningMessage,
                                            isWithSession: false
                                        }}>
                                        {tabIndex === 0 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                            <JobNewDetails shipmentType={shipmentType} errors={validationErrors} /></C1TabInfoContainer>}

                                        {showTabs?.fmTrip && tabIndex === 1 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                            <JobTripCharges ref={jobTripChargesRef} errors={validationErrors} /></C1TabInfoContainer>}

                                        {showTabs?.mmTrip && tabIndex === 2 && <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.details' title="empty" guideAlign="right" open={false}>
                                            {<JobTripChargesDomestic
                                                ref={jobTripChargesRef}
                                                errors={validationErrors}
                                            />}</C1TabInfoContainer>}
                                        {tabIndex === 3 &&
                                            <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.authorisation' title="empty" guideAlign="right" open={false}>
                                                <JobAuthLetters viewType={"view"}
                                                    inputData={inputData} />
                                            </C1TabInfoContainer>}
                                        {showTabs?.driver && tabIndex === 4 &&
                                            <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                                <JobDriverAssign
                                                    errors={validationErrors}
                                                    inputData={inputData}
                                                    handleInputChange={handleInputChange}
                                                />
                                            </C1TabInfoContainer>
                                        }
                                        {/** Delivery Orders tab */}
                                        {showTabs?.deliveryOrders && tabIndex === 5 &&
                                            <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.do' title="empty" guideAlign="right" open={false}>
                                                <JobDeliveryOrders
                                                    inputData={popUpDoDetails}
                                                    data={popUpAttDetails}
                                                    state={inputData?.tckJob?.tckMstJobState}
                                                    truckJobId={inputData?.jobId}
                                                    doId={doId}
                                                    setDoId={setDoId}
                                                    setStart={setStart}
                                                    setStop={setStop}
                                                    isRefresh={isRefresh}
                                                    viewType={"view"}
                                                    handleInputChange={handleInputChange}
                                                    handlePopupInputChange={handlePopupInputChange}
                                                    // handleInputFileChange={handleInputFileChange}
                                                    uploadDeliveryOrder={uploadDeliveryOrder}
                                                    // handleInputFileChangeSigned={handleInputFileChangeSigned}
                                                    // downloadDOHandlerUnsigned={downloadDOHandlerUnsigned}
                                                    // downloadDOHandlerSigned={downloadDOHandlerSigned}
                                                    downloadDOHandler={downloadDOHandler}
                                                    deleteDOHandler={deleteDOHandler}
                                                    locale={t}
                                                    errors={popUpFieldError}
                                                    saveTripDo={saveTripDo}
                                                    editTripDo={editTripDo}
                                                />
                                            </C1TabInfoContainer>
                                        }
                                        {inputData?.hasRemarks && tabIndex == 8 &&
                                            <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                                <JobRejectRemarks parentJobId={inputData?.tckJob?.jobId} />
                                            </C1TabInfoContainer>
                                        }
                                        {tabIndex === 9 &&
                                            <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                                <C1AuditTab filterId={inputData.jobId ? inputData.jobId : 'draft'}></C1AuditTab>
                                            </C1TabInfoContainer>
                                        }
                                        {showTabs?.tracking && tabIndex === 10 &&
                                            <C1TabInfoContainer guideId='clicdo.doi.co.jobs.tabs.audit' title="empty" guideAlign="right" open={false}>
                                                <JobTrack />
                                            </C1TabInfoContainer>
                                        }

                                    </JobTruckContext.Provider>

                                </Paper>
                            </Grid>
                        </Grid>
                    )}
                </C1FormDetailsPanel>
            </div>
            {/* For submit confirmation */}
            <ConfirmationDialog
                open={openSubmitConfirm?.open}
                onConfirmDialogClose={() => setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false })}
                text={t("job:msg.confirmation", { action: Actions[openSubmitConfirm?.action]?.text })}
                title={t("job:popup.confirmation")}
                onYesClick={(e) => handleAction(e)} />



            {/* For rejection remarks - TO BE REMOVED */}
            <C1PopUp
                maxWidth={"md"}
                title={`Rejection Remarks`}
                openPopUp={rejectRemarks?.open}
                setOpenPopUp={setRejectRemarks}
                actionsEl={<C1IconButton disabled={!rejectRemarks?.msg} tooltip={t("buttons:submit")} childPosition="right">
                    <NearMeOutlinedIcon color="primary" fontSize="large"
                        onClick={(e) => { setRejectRemarks({ ...rejectRemarks, open: false }); return handleConfirmAction(e) }}></NearMeOutlinedIcon>
                </C1IconButton>}>
                <C1TextArea textLimit={256} required name="rejectRemarks.msg" value={getValue(rejectRemarks?.msg)} onChange={(e) => setRejectRemarks({ ...rejectRemarks, msg: e?.target?.value })} />
            </C1PopUp>

            {/* For Billing Action Remarks */}
            <C1PopUp
                maxWidth={"md"}
                title={`Remarks`}
                openPopUp={jobRemarks?.open}
                setOpenPopUp={setJobRemarks}
                actionsEl={<C1IconButton disabled={(["REJECT", "REJECT_BILL"].includes(openSubmitConfirm?.action) && !jobRemarks?.msg) ? true : false} tooltip={t("buttons:submit")} childPosition="right">
                    <NearMeOutlinedIcon color="primary" fontSize="large"
                        onClick={(e) => { setJobRemarks({ ...jobRemarks, open: false }); return handleConfirmAction(e) }}></NearMeOutlinedIcon>
                </C1IconButton>}>
                <C1TextArea textLimit={256} required={["REJECT", "REJECT_BILL"].includes(openSubmitConfirm?.action)}
                    name="jobRemarks.msg" value={getValue(jobRemarks?.msg)} onChange={(e) => setJobRemarks({ ...jobRemarks, msg: e?.target?.value })} />
            </C1PopUp>


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
                            {t("job:popup.ok")}
                        </Button>
                    </div>
                </div>
            </Dialog>

            {/** Error prompt for start job */}
            <C1DialogPrompt
                confirmationObj={{
                    openConfirmPopUp: startErrorOpen?.open,
                    onConfirmationDialogClose: () => setStartErrorOpen({ ...startErrorOpen, open: false }),
                    text: startErrorOpen?.msg,
                    title: t("job:msg.startErrorTitle"),
                    onYesClick: () => setStartErrorOpen({ ...startErrorOpen, open: false }),
                    yesBtnText: "Ok",
                }} />
            {/** Error prompt for stop job */}
            <C1DialogPrompt
                confirmationObj={{
                    openConfirmPopUp: stopErrorOpen?.open,
                    onConfirmationDialogClose: () => setStopErrorOpen({ ...stopErrorOpen, open: false }),
                    text: stopErrorOpen?.msg,
                    title: t("job:msg.stopErrorTitle"),
                    onYesClick: () => setStopErrorOpen({ ...stopErrorOpen, open: false }),
                    yesBtnText: "Ok",
                }} />
            {/** Error prompt for duplicate Do No */}
            <C1DialogPrompt
                confirmationObj={{
                    openConfirmPopUp: duplicateErrorOpen?.open,
                    onConfirmationDialogClose: () => setDuplicateErrorOpen({ ...duplicateErrorOpen, open: false }),
                    text: duplicateErrorOpen?.msg,
                    title: t("job:msg.error"),
                    onYesClick: () => setDuplicateErrorOpen({ ...duplicateErrorOpen, open: false }),
                    yesBtnText: "Ok",
                }} />
            {/** Custom Error prompt */}
            <C1DialogPrompt
                confirmationObj={{
                    openConfirmPopUp: customErrorOpen?.open,
                    onConfirmationDialogClose: () => setCustomErrorOpen({ ...customErrorOpen, open: false }),
                    text: customErrorOpen?.msg,
                    title: customErrorOpen?.title,
                    onYesClick: () => setCustomErrorOpen({ ...customErrorOpen, open: false }),
                    yesBtnText: "Ok",
                }} />

            <Backdrop open={dlOpen} className={bdClasses.backdrop}> <CircularProgress color="inherit" /></Backdrop>

            <C1Warning warningMessage={warningProps} handleWarningAction={handleWarningPopup} />

        </React.Fragment >

        );
};

export default withErrorHandler(TruckFormNoSessionDetails);