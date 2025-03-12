import {Grid} from "@material-ui/core";
import React, {useEffect, useState} from "react";
import C1DataTable from "app/c1component/C1DataTable";
import {isEmpty} from "lodash";
import {useTranslation} from "react-i18next";
import {useParams} from "react-router-dom";
import useAuth from "../../../hooks/useAuth";
import useHttp from "../../../c1hooks/http";
import {formatDate} from "../../../c1utils/utility";
import {getActiveMode, getDeActiveMode, getStatusDesc} from "../../../c1utils/statusUtils";
import C1DataTableActions from "../../../c1component/C1DataTableActions";
import Snackbar from "@material-ui/core/Snackbar";
import C1Alert from "../../../c1component/C1Alert";
import {deepUpdateState} from "../../../c1utils/stateUtils";
import C1PopUp from "../../../c1component/C1PopUp";
import C1IconButton from "../../../c1component/C1IconButton";
import SaveIcon from "@material-ui/icons/SaveOutlined";
import NotificationPopUpDetails from "./NotificationPopUpDetails";
import ConfirmationDialog from "../../../../matx/components/ConfirmationDialog";

const NotificationFormDetails = ({
      moduleName,
      isDisabled,
      commonId
  }) => {
    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: ""
    }
    const {t} = useTranslation(["administration", "common"]);
    const [openAddPopUp, setOpenAddPopUp] = useState(false)
    const [view, setView] = useState(false)
    let {viewType, id} = useParams();
    const {user} = useAuth();
    const {isLoading, isFormSubmission, res, validation, error, urlId, sendRequest} = useHttp();
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [tabIndex, setTabIndex] = useState(0);
    const [isSubmitSuccess, setSubmitSuccess] = useState(false);
    const [isSubmitError, setSubmitError] = useState(false);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState("");
    const [notificationList, setNotificationList] = useState([]);
    const [errors, setErrors] = useState({});
    const [action, setAction] = useState("");
    const [listCondition, setListCondition] = useState([]);
    const [isRefresh, setRefresh] = useState(false);
    const [isEnabledDate, setEnabledDate] = useState(false);
    const [openPopupAction, setOpenPopupAction] = useState(false);
    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: 'success',
        severity: 'success'
    });
    const [alertId, setAlertId] = useState("");
    const popupDefaultValue = {
        altConditionValue: '',
        altConditionDt: '',
        altConditionType:'',
        altReferId: '',
        altRepCon: '',
        altRemarks: '',
        coreAccn: {
            accnId: ''
        },
        ckCtMstAlert: {
            altId: "",
            altName: "",
            altNotificationType: ""
        }
    }
    const [popUpData, setPopUpData] = useState(popupDefaultValue);
    const uniqueAlerts = Array.from(
        new Map(notificationList?.map(alert => [alert?.altName, alert])).values()
    );
    const uniqueNotificationType = Array.from(
        new Map(notificationList?.map(data => [data?.altNotificationType, data])).values()
    );
    const uniqueConditionType = Array.from(
        new Map(
            notificationList
                ?.filter(data => data?.altConditionType != null) // Filter out null or undefined values
                .map(data => [data.altConditionType.toLowerCase(), data]) // Map to lowercase for uniqueness
        ).values()
    );// Function to filter and retrieve the altId based on altName and altNotificationType
    const getAltId = (altName, altNotificationType) => {
        const matchedItem = notificationList.find(
            item => item.altName === altName && item.altNotificationType === altNotificationType
        );
        return matchedItem ? matchedItem.altId : null;
    };
    const [selectedAltName, setSelectedAltName] = useState('');
    const [selectedAltNotificationType, setSelectedAltNotificationType] = useState('');
    const altId = getAltId(popUpData?.ckCtMstAlert?.altName, popUpData?.ckCtMstAlert?.altNotificationType);
    const columns = [
        {
            name: "altId",
            label: "ID",
            options: {
                display: false
            }
        },
        {
            name: "altReferId",
            options: {
                display: false
            }
        },
        {
            name: "ckCtMstAlert.altName",
            label: "Name"
        },
        {
            name: "ckCtMstAlert.altNotificationType",
            label: "Notification Type",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <>
                            {(value ? value.split(",") : []).map((row, idx) => {
                                if (idx >= 5) {
                                    return;
                                }
                                return (
                                    <>
                                        <small
                                            className="px-2 py-4px border-radius-8"
                                            style={{backgroundColor: "#e0e0e0", color: "#2c2e32"}}>
                                            {row}
                                        </small>
                                    </>
                                )
                            })}
                        </>
                    );
                }
            },
        },
        {
            name: "ckCtMstAlert.altConditionType",
            label: "Condition Type",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return (
                        <>
                            {(value ? value.split(",") : []).map((row, idx) => {
                                if (idx >= 5) {
                                    return;
                                }
                                return (
                                    <>
                                        <small
                                            className="px-2 py-4px border-radius-8"
                                            style={{backgroundColor: "#e0e0e0", color: "#2c2e32"}}>
                                            {row}
                                        </small>
                                    </>
                                )
                            })}
                        </>
                    );
                }
            },
        },
        {
            name: "altConditionValue",
            label: "Condition Value"
        },
        {
            name: "altConditionDt",
            label: "Condition Date",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "altRepCon",
            label: "Recipient"
        },
        {
            name: "altDtCreate",
            label: "Date Created",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "altDtLupd",
            label: "Date Updated",
            options: {
                filter: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return formatDate(value, true);
                }
            },
        },
        {
            name: "altStatus",
            label: "Status",
            options: {
                customBodyRender: (value, tableMeta, updateValue) => {
                    return getStatusDesc(value)
                }
            },
        },
        {
            name: "action",
            label: " ",
            options: {
                filter: false,
                display: true,
                customBodyRender: (value, tableMeta, updateValue) => {
                    return <C1DataTableActions
                        viewPopupEventHandler={() => viewClickEventHandler(tableMeta)}
                        copyPath={!isDisabled && moduleName.toLowerCase() !== 'job' ? () => duplicateClickEventHandler(tableMeta) : null}
                        editEventHandler={!isDisabled ? () => editClickEventHandler(tableMeta) : null}
                        deActiveEventHandler={getDeActiveMode(tableMeta.rowData[10]) && moduleName.toLowerCase() !== 'job' && viewType !=='view' ? () => handleDeActiveHandler(tableMeta.rowData[0]): null}
                        activeEventHandler={getActiveMode(tableMeta.rowData[10])  && moduleName.toLowerCase() !== 'job' && viewType !=='view' ? () => handleActiveHandler(tableMeta.rowData[0]): null}
                    />
                },
            },
        },
    ]
    const duplicateClickEventHandler = (rowMeta) => {
        setAlertId(rowMeta.rowData[0]);
        setErrors({});
        setOpenAddPopUp(true);
        setLoading(true)
        setErrors({})
        setView(false);
        setAction("DUPLICATE");
        sendRequest("/api/v2/clickargo/master/ckCtAlert/" + rowMeta?.rowData[0], "getAlert", "get", {});
    };

    const handleDeActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/v2/clickargo/master/ckCtAlert/"+ id, "deActive", "delete", {});
    }
    const handleActiveHandler = (id) => {
        setLoading(true);
        setRefresh(false);
        sendRequest("/api/v2/clickargo/master/ckCtAlert/" + id, "getForActive", "get", {})
    }
    const handleConfirmAction = () => {
        setLoading(true);
        setRefresh(false);
        if(action === "delete") {
            sendRequest("/api/v2/clickargo/master/ckCtAlert/"+ id, "deActive", "delete", {});
        }else if(action === "active") {
            sendRequest("/api/v2/clickargo/master/ckCtAlert/" + id, "getForActive", "get", {})
        }
        setOpenPopupAction(false);
    }
    const editClickEventHandler = (rowMeta) => {
        setAlertId(rowMeta.rowData[0]);
        setErrors({});
        setOpenAddPopUp(true);
        setLoading(true)
        setView(false);
        setAction("EDIT");
        sendRequest("/api/v2/clickargo/master/ckCtAlert/" + rowMeta?.rowData[0], "getAlert", "get", {});
    };
    const viewClickEventHandler = (rowMeta) => {
        setErrors({});
        setOpenAddPopUp(true);
        setView(true);
        setLoading(true)
        setAlertId(rowMeta.rowData[0]);
        setAction("VIEW");
        sendRequest("/api/v2/clickargo/master/ckCtAlert/" + rowMeta?.rowData[0], "getAlert", "get", {});
    };
    //api request for the details here
    useEffect(() => {
        setLoading(false);
        setSubmitSuccess(false);
        if (viewType !== 'new') {
            sendRequest(`/api/v1/dropdown/alert/${moduleName}`, "getAlertType", "get", {});
        }
        // eslint-disable-next-line
    }, [id, viewType]);
    useEffect(() => {
        if (!isLoading && !error && res) {
            if (urlId==='getForActive'){
                sendRequest("/api/v2/clickargo/master/ckCtAlert/"+ res?.data?.altId +"/activate", "active", "put", res?.data);
            }
            if (urlId==='active' || urlId==='deActive'){
                setRefresh(true);
                setLoading(false);
            }
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLoading, res, error, urlId]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            setLoading(false);
            switch (urlId) {
                case "getAlert":
                    setPopUpData({ ...popUpData, ...res?.data });
                    break;
                case "saveAlert":
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity: 'success',
                        msg: t("common:common.msg.created"),
                    });
                    setSubmitSuccess(true);
                    setOpenAddPopUp(false);
                    setLoading(true);
                    setRefresh(true);
                    break;
                case "updateAlert":
                    setSnackBarState({
                        ...snackBarState,
                        open: true,
                        success: true,
                        severity: 'success',
                        msg: t("common:common.msg.updated"),
                    });
                    setSubmitSuccess(true);
                    setOpenAddPopUp(false);
                    setLoading(true);
                    setRefresh(true);
                    break;
                case "getAlertType":
                    // Ensure res.data is an array and set it correctly
                    setListCondition(res?.data || []);
                    setNotificationList(res?.data || []);
                    break;
                default:
                    break;
            }
        }
        if (error) {
            setLoading(false);
            console.error('Error occurred:', error);
        }
        if (validation) {
            console.log("validation",validation)
            setErrors({ ...validation });
            setLoading(false);
        }
    }, [urlId, isLoading, res, validation, error]);

    const handleClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
        setSubmitSuccess(false);
        setSubmitError(false);
        setLoading(false);
        setRefresh(false)
    };

    let snackBar;
    if (isSubmitSuccess || isSubmitError) {
        const anchorOriginV = snackBarState.vertical || 'top';
        const anchorOriginH = snackBarState.horizontal || 'center';

        snackBar = (
            <Snackbar
                anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
                open={snackBarState.open}
                onClose={handleClose}
                autoHideDuration={3000}
                key={anchorOriginV + anchorOriginH}
            >
                <C1Alert onClose={handleClose} severity={snackBarState.severity}>
                    {snackBarState.msg}
                </C1Alert>
            </Snackbar>
        );
    }
    const handleInputChange = (e) => {
        const { name, value } = e.target;

        // Update altName and altNotificationType states based on input changes
        if (name === 'ckCtMstAlert.altId') {
            setSelectedAltName(value); // Update altName state
        } else if (name === 'ckCtMstAlert.altNotificationType') {
            setSelectedAltNotificationType(value); // Update altNotificationType state
        }

        // Update the state for the specific field
        const updatedPopUpData = deepUpdateState(popUpData, name, value);

        // If the updated field is altId, we need to sync altName and altNotificationType
        if (name === 'ckCtMstAlert.altId') {
            const selectedItem = uniqueAlerts.find(item => item.altId === value);
            if (selectedItem) {
                setSelectedAltName(selectedItem.altName);
                setSelectedAltNotificationType(selectedItem.altNotificationType);
            }
        }

        // Set the updated state
        setPopUpData(prevState => ({
            ...prevState,
            ...updatedPopUpData
        }));

    };
    const handleSaveOnClick = () => {
        setSnackBarOptions(defaultSnackbarValue);
        setLoading(true);
        const validationErrors = handlePopUpFieldValidate();
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            setLoading(false);
            return;
        }
        const basePopUpData = {
            altConditionValue: popUpData.altConditionValue,
            altConditionDt: popUpData.altConditionDt,
            altReferId: popUpData.altReferId,
            altRepCon: popUpData.altRepCon,
            altRemarks: popUpData.altRemarks,
            ckCtMstAlert: {
                altId,
                altName: popUpData.ckCtMstAlert?.altName,
                altNotificationType: popUpData.ckCtMstAlert?.altNotificationType,
            },
            coreAccn: {
                accnId: popUpData.coreAccn?.accnId,
            },
            altConditionType: popUpData.altConditionType,
        };

        if (action === 'EDIT') {
            const updatedData = {
                ...basePopUpData,
                altId: popUpData.altId,
            };
            sendRequest(`/api/v2/clickargo/master/ckCtAlert/${alertId}`, "updateAlert", "put", updatedData);
        } else if (action === 'DUPLICATE') {
            sendRequest("/api/v2/clickargo/master/ckCtAlert", "saveAlert", "post", basePopUpData);
        } else {
            const newPopUpData = {
                ...popUpData,
                altReferId: commonId,
                coreAccn: {
                    ...popUpData.coreAccn,
                    accnId: user?.coreAccn?.accnId,
                },
                ckCtMstAlert: {
                    ...popUpData.ckCtMstAlert,
                    altId,
                },
            };
            setPopUpData(newPopUpData);
            sendRequest("/api/v2/clickargo/master/ckCtAlert", "saveAlert", "post", newPopUpData);
        }
    };
    const popUpAddHandler = (e) => {
        setLoading(true);
        setAction("")
        if (!commonId) {
            if(!isEmpty(handlePopUpFieldValidate())){
                e.preventDefault();
                setOpenAddPopUp(false);
                setErrors({});
                setPopUpData({
                    altConditionValue: '',
                    altConditionDt: '',
                    altConditionType:'',
                    altReferId: '',
                    altRepCon: '',
                    altRemarks: '',
                    ckCtMstAlert: {
                        altId: "",
                        altName: "",
                        altNotificationType: ""
                    }
                });
            }
        } else {
            setErrors({})
            setOpenAddPopUp(true);
            setPopUpData(popupDefaultValue);
        }
    };
    const handleDateChanges = (fieldName, date) => {
        setPopUpData({...popUpData, ...deepUpdateState(popUpData, fieldName, date)});
    };
    let isCommonId = null;
    if (commonId === ''){
        isCommonId = "none";
    }else {
        isCommonId = commonId;
    }

    const filteredList = listCondition.filter(item => item.altNotificationType === popUpData?.ckCtMstAlert?.altNotificationType);
    const conditionTypeValue = filteredList[0]?.altConditionType;

    let isNullConditionType = true;
    if (conditionTypeValue !== null){
        isNullConditionType = false
    }


    console.log("errors", errors)
    const handlePopUpFieldValidate = () => {
        const errors = {};
        // Check if Condition Type is not null
        if (!isNullConditionType) {
            if (!popUpData?.altConditionType){
                errors.altConditionType = "Condition Type is mandatory";
            }
            if (!popUpData.altConditionValue) {
                errors.altConditionValue = "Condition Value is mandatory";
            }
            // Check if Recipient Contact is provided
            if (popUpData.altRepCon === '') {
                errors.altRepCon = "Recipient Contact is mandatory";
            }
            // Validate Recipient Contact based on Notification Type
            const notificationType = popUpData.ckCtMstAlert?.altNotificationType;

            if (notificationType) {
                if (notificationType === 'EMAIL') {
                    const isEmail = /\S+@\S+\.\S+/.test(popUpData.altRepCon);
                    if (!isEmail) {
                        errors.altRepCon = "Recipient Contact must be a valid email address";
                    }
                } else if (notificationType === 'WHATSAPP') {
                    const isMobileNumber = /^(\+?\d{7,15})$/.test(popUpData.altRepCon);
                    if (!isMobileNumber) {
                        errors.altRepCon = "Recipient Contact must be a valid mobile number with 7 to 15 digits, optionally starting with '+'";
                    }
                } else {
                    errors.altRepCon = "Invalid notification type provided";
                }
            } else {
                errors.ckCtMstAlert = {altNotificationType: "Notification type is mandatory"};
            }
            // Validate based on Condition Type specifics
            if (popUpData.altConditionType === 'DAYS_BEFORE') {
                if (!popUpData.altConditionValue) {
                    errors.altConditionValue = "Condition Value is mandatory";
                }
                if (!popUpData.altConditionDt) {
                    errors.altConditionDt = "Condition Date is mandatory";
                }
                const today = new Date();

                // Helper function to add days to the current date
                const addDaysToDate = (date, days) => {
                    const result = new Date(date);
                    result.setDate(result.getDate());
                    return result;
                };
                const conditionDate = new Date(popUpData?.altConditionDt);
                const conditionValueAsNumber = parseInt(popUpData?.altConditionValue, 10);

                // Ensure altConditionValue is a valid number before continuing
                if (!isNaN(conditionValueAsNumber)) {
                    console.log("conditionValueAsNumber", conditionValueAsNumber)
                    const minimumDate = addDaysToDate(today, conditionValueAsNumber);

                    // Check if Condition Date is greater than or equal to today + altConditionValue
                    if (conditionDate < minimumDate) {
                        errors.altConditionDt = `Condition Date must be at least ${conditionValueAsNumber} days from today`;
                    }
                    if (conditionValueAsNumber <=0) {
                        errors.altConditionValue = `Condition value must be greater than 0`;
                    }

                } else {
                    errors.altConditionValue = "Condition Value must be a valid number";
                }
            } else if (popUpData.altConditionType === 'DISTANCE') {
                if (!popUpData.altConditionValue) {
                    errors.altConditionValue = "Condition Value is mandatory";
                }
                // Condition Date is hidden for DISTANCE, no validation needed
            }
        }
        // Check if vehicle ID (vhId) is provided
        if (!commonId) {
            setSnackBarState({
                ...snackBarState,
                severity: 'warning',
                error: true,
                success: false,
                open: true,
                msg: `Please save the ${moduleName} detail before creating an alert`
            });
            setSubmitSuccess(true);
        }
        return errors;
    };
    return (
        <React.Fragment>
            <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                    <C1DataTable
                        url={"/api/v2/clickargo/master/ckCtAlert"}
                        isServer={true}
                        columns={columns}
                        defaultOrder="altDtCreate"
                        defaultOrderDirection="desc"
                        isShowDownload={false}
                        isShowPrint={false}
                        isShowViewColumns={false}
                        isShowFilter={false}
                        isRefresh={isRefresh}
                        filterBy={[
                            {attribute: "altReferId", value: isCommonId},
                            {attribute: "altModule", value: moduleName},
                        ]}

                        showAdd={ viewType !== 'view' &&{
                            type: "popUp",
                            popUpHandler: popUpAddHandler,
                        }}
                    />
                </Grid>
            </Grid>
            <C1PopUp
                title={`${moduleName} Alert`}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                actionsEl={viewType !== 'view' && action !=='VIEW' &&
                            <C1IconButton tooltip={t("save")} childPosition="right">
                        <SaveIcon color="primary" fontSize="large" onClick={handleSaveOnClick}></SaveIcon>
                    </C1IconButton>
                }
            >
                <NotificationPopUpDetails
                    uniqueData={uniqueAlerts}
                    uniqueNotificationType={uniqueNotificationType}
                    uniqueConditionType={uniqueConditionType}
                    isNullConditionType={isNullConditionType}
                    conditionTypeValue={conditionTypeValue}
                    moduleName={moduleName}
                    viewType={viewType}
                    isDisabled={isDisabled}
                    view={view}
                    locale={t}
                    errors={errors}
                    data={data}
                    action={action}
                    handleInputChange={handleInputChange}
                    handleDateChanges={handleDateChanges}
                    popUpData={popUpData}
                />
            </C1PopUp>
            {snackBar}
            <ConfirmationDialog
                open={openPopupAction}
                onConfirmDialogClose={() => setOpenPopupAction(false)}
                text={t("common:confirmMsgs.confirm.content")}
                title={t("common:confirmMsgs.confirm.title")}
                onYesClick={(e) => handleConfirmAction(e)} />
        </React.Fragment>
    );
}


export default NotificationFormDetails;