import React, { useState } from "react";

import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { Divider, Grid, Paper, Tabs } from "@material-ui/core";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import CreditLimitContext from "./CreditLimitUpdateContext";
import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import {isEmpty} from "app/c1utils/utility"


import {
    JobStates,
    Roles,
} from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { tabScroll } from "app/c1utils/styles";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";

// tabs
import CreditLimitForm from "./tabs/CreditLimitForm";


const CreditLimitFormDetails = () => {
    const { t } = useTranslation(["job", "common", "listing", "buttons", "cargoowners"]);

    const { viewType, id } = useParams();
    const history = useHistory();
    const { user } = useAuth();

    const { res, validation, error, urlId, sendRequest } = useHttp();

    const [tabIndex, setTabIndex] = useState(0);
    const [validationErrors, setValidationErrors] = useState({});

    const defaultProvide = ["CLICTRUCK"];
    const defaultCredit = {crAmt:"-", tmstCurrency:{ccyCode:"-"}, crDtStart:"", crDtEnd:"", tcoreUsrApprove:{usrName:"-"}, crDtApprove:null};

    const isFnHDSupport = user?.authorities?.some(
        (item) => item?.authority === Roles.FINANCE_APPROVER.code
    );

    const tabList = [
        {
            id: "creditLimitDetails",
            text: "Credit Limit Update",
            icon: <WorkOutlineOutlinedIcon />,
        },
        { id: "audit", text: t("job:tabs.audit"), icon: <AccessTimeOutlinedIcon /> },
    ];

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    // for new request
    const defaultBodyReq = {
        cruId: "",
        tckMstCreditRequestState: {
            stId: "",
        },
        tcoreAccn: {
            accnId: "",
        },
        tmstCurrency: {
            ccyCode: "",
        },
        tckMstServiceType: {
            svctId: "",
        },
        cruAmt: 0.0,
        cruTxnCap: 0.0,
        cruDtStart: "",
        cruDtEnd: "",
        cruRequester: "",
        cruRemarks: "",
        cruUsrVerify: "",
        cruDtVerify: "",
        cruUsrApprove: "",
        cruDtApprove: "",
        cruUsrReject: "",
        cruDtReject: "",
        cruApproverRemarks: "",
        cruStatus: "",
        cruDtCreate: "",
        cruUidCreate: "",
        cruDtLupd: "",
        cruUidLupd: "",
        cruDtSubmitted: "",
        cruUidSubmitted: "",
    };

    // for update request

    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [controls, setControls] = useState([]);
    // eslint-disable-next-line
    const [loading, setLoading] = useState(false);

    const [inputData, setInputData] = useState(defaultBodyReq);
    const [companyList, setCompanyList] = React.useState([]);
    const [serviceList, setServiceList] = React.useState([]);
    const [servicePicked, setServicePicked] = React.useState("");

    const [creditData, setCreditData] = React.useState({});
    const [updateCreditData, setUpdateCreditData] = React.useState({});
    const [warningMessage, setWarningMessage] = useState({ open: false, msg: "" });

    const [openActionConfirm, setOpenActionConfirm] = useState({ action: null, open: false });
    const [confirmIdPicked, setConfirmIdPicked] = useState({ id: null });
    const [openConfirmation, setOpenConfirmation] = useState(false);

    React.useEffect(() => {
        if (id) {
            setConfirmIdPicked({ id: id });
        }
        sendRequest("/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=asc&iSortingCols=1&mDataProp_0=accnName&iColumns=2&mDataProp_1=TMstAccnType.atypId&sSearch_1=ACC_TYPE_CO,ACC_TYPE_FF"
        , "getCompanyList", "get");
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [viewType]);

    React.useEffect(() => {
        if (!error && res && !validation) {
            switch (urlId) {
                case "getCompanyList": {
                    const data = res?.data?.aaData;
                    setCompanyList(data);
                    if (viewType !== "new") {
                        sendRequest(
                            `/api/v1/clickargo/credit/service?companyId=${history?.location?.state?.companyId}`,
                            "getProvideOpt",
                            "get"
                        );
                    }

                    break;
                }
                case "getProvideOpt": {
                    const data = res?.data;
                    if(data && data?.length>0) {
                        setServiceList(data);
                    } else {
                        setServiceList(defaultProvide);
                    }
                    if (viewType !== "new") {
                        // setInputData({
                        //     ...inputData,
                        //     tcoreAccn:{accnId: history?.location?.state?.companyId},
                        //     tckMstServiceType:{svctId: history?.location?.state?.serviceId}
                        // })
                        sendRequest(
                            `/api/v1/clickargo/credit/detail?serviceType=${history?.location?.state?.serviceId}&accnId=${history?.location?.state?.companyId}`,
                            "getCreditDetail",
                            "get"
                        );
                    }

                    break;
                }
                case "getCreditDetail": {
                    const data = res?.data;

                    if( data && !isEmpty(data)) {
                        setCreditData(data);
                    } else {
                        setCreditData(defaultCredit);
                    }
                    setInputData({
                        ...inputData,
                        tmstCurrency: {
                            ccyCode: data?.tmstCurrency?.ccyCode || "IDR",
                        },
                    });
                    if (viewType !== "new") {
                        sendRequest(
                            `/api/v1/clickargo/creditRequest/crupdate/${id}`,
                            "getCreditRequestDetail",
                            "get"
                        );
                    }
                    break;
                }
                case "getCreditRequestDetail": {
                    const data = res?.data;
                    const state = res?.data?.tckMstCreditRequestState?.stId;
                    setInputData(data);
                    const reqBody = {
                        entityType: "CREDIT_LIMIT",
                        entityState: state,
                        page: viewType.toUpperCase(),
                    };
                    sendRequest("/api/v1/clickargo/controls/", "fetchControls", "POST", reqBody);
                    break;
                }
                case "fetchControls": {
                    // console.log(res?.data)
                    setControls(res?.data);
                    break;
                }
                case "approveReq":
                case "rejectReq":
                case "deleteReq":
                case "submitNewReq":
                    if (res?.data) {
                        setInputData({});
                        setCreditData({});
                        setCompanyList([]);
                        setServiceList([]);
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: t("cargoowners:msg.updateSuccess"),
                            redirectPath: "/opadmin/creditlimit",
                            // redirectPathState: {  }
                        });
                    }
                    break;
                case "saveNewReq": {
                    if (res?.data) {
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: t("cargoowners:msg.saveSuccess"),
                        });
                        history.replace({
                            pathname: `/opadmin/creditform/edit/${res?.data?.cruId}`,
                            state: {
                                serviceId: res?.data?.tckMstServiceType.svctId,
                                companyId: res?.data?.tcoreAccn?.accnId,
                                from: "/opadmin/creditlimit",
                            },
                        });
                    }
                    break;
                }
                case "appReq": {
                    if (res?.data) {
                        setInputData({});
                        setCreditData({});
                        setCompanyList([]);
                        setServiceList([]);
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: t("common:common.msg.approve"),
                            redirectPath: "/opadmin/creditlimit",
                        });
                    }
                    break;
                }
                case "rejReq": {
                    if (res?.data) {
                        setInputData({});
                        setCreditData({});
                        setCompanyList([]);
                        setServiceList([]);
                        setSnackBarOptions({
                            ...snackBarOptions,
                            success: true,
                            successMsg: t("common:common.msg.reject"),
                            redirectPath: "/opadmin/creditlimit",
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
        if (validation) {
            setValidationErrors({ ...validation });

            validation.hasOwnProperty("cruState")
                ? setWarningMessage({
                      open: true,
                      msg: `${validation?.cruState}`,
                  })
                : setWarningMessage({
                      open: true,
                      msg: `Some data is incorrect, please fill in correctly`,
                  });
        }
    }, [urlId, res, error]);

    const handleDetailCreditLimit = (e, name, value) => {
        if (name === "tcoreAccn.accnId") {
            setInputData({ ...defaultBodyReq, tcoreAccn: { accnId: value?.value } });
            setServicePicked("");
            setCreditData({});
            // sendRequest(`/api/v1/clickargo/credit/service?companyId=${value?.value}`, "getProvideOpt" , "get")

            sendRequest(
                `/api/v1/clickargo/credit/service?companyId=${value?.value}`,
                "getProvideOpt",
                "get"
            );
        } else if (name === "tckMstServiceType.svctId") {
            setInputData({ ...inputData, tckMstServiceType: { svctId: value?.value } });
            setServicePicked(value?.value);
            sendRequest(
                `/api/v1/clickargo/credit/detail?serviceType=${value?.value}&accnId=${inputData?.tcoreAccn?.accnId}`,
                "getCreditDetail",
                "get"
            );
        }
    };
    const handleUpdateCreditLimit = (e) => {
        const { name, value } = e.target;
        setInputData({ ...inputData, [name]: value });
    };

    const eventHandler = (action) => {
        console.log(action);
        if (action.toLowerCase() === "save") {
            viewType !== "new"
                ? sendRequest(
                      `/api/v1/clickargo/creditRequest/crupdate/${id}`,
                      "saveNewReq",
                      "put",
                      { ...inputData, tckMstCreditRequestState: { stId: "NEW" } }
                  )
                : sendRequest("/api/v1/clickargo/creditRequest/crupdate", "saveNewReq", "post", {
                      ...inputData,
                      tckMstCreditRequestState: { stId: "NEW" },
                  });
        } else if (action.toLowerCase() === "submit") {
            sendRequest(`/api/v1/clickargo/creditRequest/crupdate/${id}`, "submitNewReq", "put", {
                ...inputData,
                tckMstCreditRequestState: { stId: "SUB" },
            });
        } else if (action.toLowerCase() === "delete") {
            setOpenConfirmation(true);
            setOpenActionConfirm({ action: "delete", open: true });
        } else if (action.toLowerCase() === "approve") {
            setOpenConfirmation(true);
            setOpenActionConfirm({ action: "approve", open: true });
        } else if (action.toLowerCase() === "reject") {
            setOpenConfirmation(true);
            setOpenActionConfirm({ action: "reject", open: true });
        } else if (action.toLowerCase() === "exit") {
            setInputData({});
            setCreditData({});
            setCompanyList([]);
            setServiceList([]);
            console.log("exit");
            history.push(history?.location?.state?.from);
        } else {
            console.log("test");
        }
    };

    const handleWarningAction = (e) => {
        setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
    };

    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleDialogConfirmation = (e) => {
        if (openActionConfirm.action === "delete") {
            setOpenConfirmation(false);
            sendRequest(`/api/v1/clickargo/creditRequest/crupdate/${id}`, "deleteReq", "put", {
                ...inputData,
                tckMstCreditRequestState: { stId: "DEL" },
            });
        } else if (openActionConfirm.action === "reject") {
            setOpenConfirmation(false);
            sendRequest(`/api/v1/clickargo/creditRequest/crupdate/${id}`, "rejReq", "put", {
                ...inputData,
                tckMstCreditRequestState: { stId: "REJ" },
            });
        } else {
            setOpenConfirmation(false);
            sendRequest(`/api/v1/clickargo/creditRequest/crupdate/${id}`, "appReq", "put", {
                ...inputData,
                tckMstCreditRequestState: { stId: "APP" },
            });
        }
    };

    let bcLabel = "Credit Limit Update";
    let formButtons;
    let initialButtons = isFnHDSupport
        ? {
              back: { show: true, eventHandler: () => eventHandler("exit") },
              // approve: {show: true, eventHandler: ()=> eventHandler("approve")},
              // reject:  {show: true, eventHandler: ()=> eventHandler("reject")}
          }
        : {
              back: { show: true, eventHandler: () => eventHandler("exit") },
              save: {
                  show: viewType === "new" || viewType === "edit" ? true : false,
                  eventHandler: () => eventHandler("save"),
              },
              // submitOnClick: {show: viewType  === "edit"? true : false, eventHandler: ()=> eventHandler("submit")},
              // delete: {show: viewType === "edit"? true : false , eventHandler: ()=> eventHandler("delete")}
          };
    // console.log(controls)
    if (loading == false) {
        if (viewType) {
            switch (viewType) {
                case "new":
                    bcLabel = "New Credit Limit Update";
                    formButtons = (
                        <C1FormButtons
                            options={getFormActionButton(initialButtons, controls, eventHandler)}
                        />
                    );
                    break;

                case "edit":
                    bcLabel = "Edit Credit Limit Update";
                    formButtons = (
                        <C1FormButtons
                            options={getFormActionButton(initialButtons, controls, eventHandler)}
                        />
                    );
                    break;

                case "view":
                    bcLabel = "Credit Limit Update";
                    formButtons = (
                        <C1FormButtons
                            options={getFormActionButton(initialButtons, controls, eventHandler)}
                        />
                    );

                    break;

                default:
                    break;
            }
        }
    }

    return loading ? (
        <MatxLoading />
    ) : (
        <React.Fragment>
            {confirmIdPicked && confirmIdPicked.id && (
                <ConfirmationDialog
                    open={openConfirmation}
                    title={t("listing:coJob.popup.confirmation")}
                    text={t("listing:coJob.msg.confirmation", {
                        action: openActionConfirm?.action,
                        id: confirmIdPicked.id,
                    })}
                    onYesClick={() => handleDialogConfirmation()}
                    onConfirmDialogClose={() => setOpenConfirmation(false)}
                />
            )}

            <C1FormDetailsPanel
                breadcrumbs={[
                    { name: "Credit Limit List", path: "/opadmin/creditlimit" },
                    { name: "Credit Limit Update Form" },
                ]}
                title={bcLabel}
                titleStatus={
                    inputData?.tckMstCreditRequestState?.stId || JobStates.DRF.code.toUpperCase()
                }
                formButtons={formButtons}
                isLoading={loading}
                snackBarOptions={snackBarOptions}
            >
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
                                    scrollButtons="auto"
                                >
                                    {tabList &&
                                        tabList.map((item, ind) => {
                                            return (
                                                <TabsWrapper
                                                    className="capitalize"
                                                    value={ind}
                                                    disabled={item?.disabled}
                                                    label={
                                                        <TabLabel viewType={viewType} tab={item} />
                                                    }
                                                    key={ind}
                                                    icon={item.icon}
                                                    {...tabScroll(ind)}
                                                />
                                            );
                                        })}
                                </Tabs>
                                <Divider className="mb-6" />
                                <CreditLimitContext.Provider
                                    value={{
                                        companyList,
                                        setCompanyList,
                                        inputData,
                                        setInputData,
                                        serviceList,
                                        handleDetailCreditLimit,
                                        servicePicked,
                                        creditData,
                                        updateCreditData,
                                        handleUpdateCreditLimit,
                                        validationErrors,
                                    }}
                                >
                                    {tabIndex === 0 && (
                                        <C1TabInfoContainer
                                            guideId="clicdo.doi.co.jobs.tabs.details"
                                            title="empty"
                                            guideAlign="right"
                                            open={false}
                                        >
                                            <CreditLimitForm viewType={viewType} />
                                        </C1TabInfoContainer>
                                    )}
                                    {tabIndex === 1 && (
                                        <C1TabInfoContainer
                                            guideId="clicdo.doi.co.jobs.tabs.details"
                                            title={"empty"}
                                            guideAlign="right"
                                            open={false}
                                        >
                                            <C1AuditTab
                                                filterId={
                                                    inputData.cruId ? inputData.cruId : "draft"
                                                }
                                            />
                                        </C1TabInfoContainer>
                                    )}
                                </CreditLimitContext.Provider>
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>

            <C1Warning warningMessage={warningMessage} handleWarningAction={handleWarningAction} />
        </React.Fragment>
    );
};

export default CreditLimitFormDetails;
