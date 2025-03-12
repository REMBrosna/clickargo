import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import useHttp from "app/c1hooks/http";
import { RecordStatus } from "app/c1utils/const";
import { getFormActionButton } from "app/c1utils/formActionUtils";
import { deepUpdateState } from "app/c1utils/stateUtils";
import { tabScroll } from "app/c1utils/styles";
import { isEditable } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog, MatxLoading } from "matx";
import NotificationTypeDetails from "./NotificationTypeDetails";
import C1TabInfoContainer from "../../../../c1component/C1TabInfoContainer";
import DepartmentDetails from "../../../administrations/operations/department/DepartmentDetails";

const NotificationTypeFormDetails = () => {
    const { t } = useTranslation(["administration", "common", "masters"]);

    const { viewType, id } = useParams();
    const history = useHistory();

    const tabList = [
        {
            text: t("administration:department.form.tab.details"),
            icon: <WorkOutlineOutlinedIcon />,
        },
        {
            text: t("administration:department.form.tab.audits"),
            icon: <AccessTimeOutlinedIcon />,
        },
    ];

    const [tabIndex, setTabIndex] = useState(0);
    // eslint-disable-next-line
    const [isDisabled, setDisabled] = useState(isEditable(viewType));

    /** ------------------ States ---------------------------------*/

    const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

    const defaultSnackbarValue = {
        success: false,
        successMsg: "",
        error: false,
        errorMsg: "",
        redirectPath: "",
    };
    const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);
    const [loading, setLoading] = useState(true);
    const [controls, setControls] = useState([]);
    const [openSubmitConfirm, setOpenSubmitConfirm] = useState({action: null, open: false});
    const [validationErrors, setValidationErrors] = useState({});
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("");
    const [status, setStatus] = useState("");
    const [editable, setEditable] = useState(false);
    //state for users to be selected
    const [selectedAccnUsers, setSelectedAccnUsers] = useState([]);
    //state  for  users selected
    const [selectedUsers, setSelectedUsers] = useState([]);
    //state for vehicles to be selected
    const [selectedAccnVehs, setSelectedAccnVehs] = useState([]);
    //state for vehicles selected
    const [selectedVehs, setSelectedVehs] = useState([]);
    const [inputData, setInputData] = useState({
        altName: '',
        altModule: '',
        altNotificationType: '',
        altTemplateId: '',
        altConditionType: ''
    });
    const initialButtons = {
        back: { show: true, eventHandler: () => handleExitOnClick() },
        save: {
            show: viewType === "edit",
            eventHandler: () => handleSubmitOnClick(),
        },
        delete: {
            show: false,
            eventHandler: () => handleDeleteOnClick(),
        },
    };
    //api request for the details here
    useEffect(() => {
        setLoading(false);
        if (viewType !== 'new') {
            sendRequest("/api/v2/clickargo/master/ckCtMstAlert/" + id, "getData", "get", {});
        }
        // eslint-disable-next-line
    }, [id, viewType]);

    useEffect(() => {
        if (!isLoading && !error && res && !validation) {
            switch (urlId) {
                case "createData": {
                    setLoading(false);
                    setInputData({ ...res?.data });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.saveSuccess"),
                    });
                    break;
                }
                case "getData": {
                    setInputData({
                        ...res.data,
                    });

                    setLoading(false);
                    break;
                }
                case "updateData": {
                    setInputData({ ...res.data });
                    setStatus(res.data?.drvStatus);
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.updateSuccess"),
                    });
                    break;
                }
                case "setStatus": {
                    setLoading(false);
                    setInputData({ ...inputData, drvStatus: status });
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.updateSuccess"),
                    });
                    break;
                }
                case "deleteData": {
                    setLoading(false);
                    setSnackBarOptions({
                        ...snackBarOptions,
                        success: true,
                        successMsg: t("common:msg.deleteSuccess"),
                    });
                    break;
                }
                default:
                    break;
            }
        }

        if (error) {
            setLoading(false);
        }

        //If validation has value then set to the errors
        if (validation) {
            setValidationErrors({ ...validation });
            setLoading(false);
            setSnackBarOptions(defaultSnackbarValue);
        }

        // eslint-disable-next-line
    }, [urlId, res, isLoading, error, validation]);

    /** ---------------- Event handlers ----------------- */
    const handleTabChange = (e, value) => {
        setTabIndex(value);
    };

    const handleInputChange = (e) => {
        const elName = e.target.name;
        setInputData({
            ...inputData,
            ...deepUpdateState(inputData, elName, e.target.value),
        });
    };


    const handleDateChange = (name, e) => {
        setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
    };

    const handleExitOnClick = () => {
        history.push("/master/notificationType/list");
    };

    console.log("inputData", inputData)
    const handleSaveOnClick = () => {
        setLoading(true);
        const validationErrors = validateFields();
        if (Object.keys(validationErrors).length > 0) {
            setValidationErrors(validationErrors);
            setLoading(false);
            return;
        }
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        console.log("Sending request with inputData:", inputData);
        console.log("URL:", viewType === "new" ? "/api/v2/clickargo/master/ckCtMstAlert" : "/api/v2/clickargo/master/ckCtMstAlert/" + id);
        switch (viewType) {
            case "new":
                sendRequest("/api/v2/clickargo/master/ckCtMstAlert", "createData", "post", inputData);
                break;
            case "edit":
                sendRequest(
                    "/api/v2/clickargo/master/ckCtMstAlert/" + id,
                    "updateData",
                    "put",
                    { ...inputData }
                );
                break;
            default:
                break;
        }
    };

    const validateFields = () => {
        let errors = {};

        if (!inputData.altName) {
            errors.altName = t("common:validation.required", { field: t("masters:notificationType.altName") });
        }
        if (!inputData.altModule) {
            errors.altModule = t("common:validation.required", { field: t("masters:notificationType.altModule") });
        }
        if (!inputData.altNotificationType) {
            errors.altNotificationType = t("common:validation.required", { field: t("masters:notificationType.altNotificationType") });
        }
        if (!inputData.altTemplateId) {
            errors.altTemplateId = t("common:validation.required", { field: t("masters:notificationType.altTemplateId") });
        }

        return errors;
    };

    const handleSubmitDelete = () => {
        setLoading(true);
        setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
        sendRequest(
            "/api/v2/clickargo/master/ckCtMstAlert/" + id,
            "deleteData",
            "delete",
            { ...inputData }
        );
    };

    const handleSubmitStatus = () => {
        setLoading(true);
        const action =
            status === RecordStatus.INACTIVE.code ? "deactive" : "active";
        setOpenSubmitConfirm({ ...openSubmitConfirm, action: null, open: false });
        sendRequest(
            "/api/v2/clickargo/master/ckCtMstAlert/" +
            id +
            "/" +
            action,
            "setStatus",
            "put",
            null
        );
    };

    const handleWarningAction = (e) => {
        setOpenWarning(false);
        setWarningMessage("");
    };

    const handleSubmitOnClick = () => {
        console.log("handleSubmitOnClick called");
        setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: "SAVE",
            open: true,
            msg: t("common:msg.saveConfirm"),
        });
    };

    const handleSetActiveOnClick = () => {
        setStatus(RecordStatus.ACTIVE.code);
        setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: "STATUS",
            open: true,
            msg: t("common:msg.activeConfirm"),
        });
    };

    const handleSetInActiveOnClick = () => {
        setStatus(RecordStatus.INACTIVE.code);
        setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: "STATUS",
            open: true,
            msg: t("common:msg.inActiveConfirm"),
        });
    };

    const handleDeleteOnClick = () => {
        setOpenSubmitConfirm({
            ...openSubmitConfirm,
            action: "DELETE",
            open: true,
            msg: t("common:msg.deleteConfirm"),
        });
    };

    const eventHandler = (action) => {
        if (action.toLowerCase() === "save") {
            handleSaveOnClick();
        } else if (action.toLowerCase() === "delete") {
            handleSubmitDelete();
        } else if (action.toLowerCase() === "status") {
            handleSubmitStatus();
        } else {
            setOpenSubmitConfirm({ action: action, open: true });
        }
    };

    let bcLabel =
        viewType === "edit"
            ? t("masters:notificationType.details.breadCrumbs.sub.edit")
            : t("masters:notificationType.details.breadCrumbs.sub.view");
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
                        eventHandler: handleSubmitOnClick,
                    },
                }}
            />
        );

        if (viewType) {
            switch (viewType) {
                case "edit":
                    bcLabel = t("masters:notificationType.details.breadCrumbs.sub.edit");
                    formButtons = (
                        <C1FormButtons
                            options={{
                                ...getFormActionButton(initialButtons, controls, eventHandler),
                                ...{
                                    activate: {
                                        show: inputData.drvStatus === RecordStatus.INACTIVE.code,
                                        eventHandler: () => handleSetActiveOnClick(),
                                    },
                                    deactivate: {
                                        show: inputData.drvStatus === RecordStatus.ACTIVE.code,
                                        eventHandler: () => handleSetInActiveOnClick(),
                                    },
                                },
                            }}
                        />
                    );
                    break;
                case "view":
                    formButtons = (
                        <C1FormButtons
                            options={getFormActionButton(
                                initialButtons,
                                controls,
                                eventHandler
                            )}
                        ></C1FormButtons>
                    );
                    break;
                case "new":
                    bcLabel = t("masters:notificationType.details.breadCrumbs.sub.new");
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
            <C1FormDetailsPanel
                breadcrumbs={[
                    {
                        name: t("masters:notificationType.details.breadCrumbs.main"),
                        path: "/master/notificationType/list",
                    },
                ]}
                title={bcLabel}
                formButtons={formButtons}
                initialValues={{ ...inputData }}
                values={{ ...inputData }}
                snackBarOptions={{
                    ...snackBarOptions,
                    redirectPath: "/master/notificationType/list",
                }}
                isLoading={loading}
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
                                                    style={
                                                        ind === 4 ? { backgroundColor: "#e4effa" } : {}
                                                    }
                                                    className="capitalize"
                                                    value={ind}
                                                    disabled={item.disabled}
                                                    label={
                                                        <TabLabel
                                                            viewType={viewType}
                                                            invalidTabs={inputData.invalidTabs}
                                                            tab={item}
                                                        />
                                                    }
                                                    key={ind}
                                                    icon={item.icon}
                                                    {...tabScroll(ind)}
                                                />
                                            );
                                        })}
                                </Tabs>
                                <Divider className="mb-6" />
                                {tabIndex === 0 && (
                                    <C1TabInfoContainer
                                        guideId="clictruck.administration.driver.details"
                                        title="empty"
                                        guideAlign="right"
                                        open={false}
                                    >
                                        <NotificationTypeDetails
                                            translator={t}
                                            viewType={viewType}
                                            handleInputChange={handleInputChange}
                                            handleDateChange={handleDateChange}
                                            selectedAccnUsers={selectedAccnUsers}
                                            selectedAccnVehs={selectedAccnVehs}
                                            selectedUsers={selectedUsers}
                                            selectedVehs={selectedVehs}
                                            isDisabled={isDisabled}
                                            inputData={inputData}
                                            errors={validationErrors}
                                            editable={editable}
                                        />
                                    </C1TabInfoContainer>
                                )}
                                {tabIndex === 1 && (
                                    <C1TabInfoContainer
                                        guideId="clictruck.administration.driver.audit"
                                        title="empty"
                                        guideAlign="right"
                                        open={false}
                                    >
                                        <C1AuditTab filterId={id ?? "empty"}></C1AuditTab>
                                    </C1TabInfoContainer>
                                )}
                            </Paper>
                        </Grid>
                    </Grid>
                )}
            </C1FormDetailsPanel>

            {/* For submit confirmation */}
            <ConfirmationDialog
                open={openSubmitConfirm?.open}
                onConfirmDialogClose={() =>
                    setOpenSubmitConfirm({
                        ...openSubmitConfirm,
                        action: null,
                        open: false,
                    })
                }
                text={openSubmitConfirm?.msg}
                title={t("common:popup.confirmation")}
                onYesClick={(e) => eventHandler(openSubmitConfirm?.action)}
            />

            <Dialog maxWidth="xs" open={openWarning}>
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
                            {t("cargoowners:popup.ok")}
                        </Button>
                    </div>
                </div>
            </Dialog>
        </React.Fragment>
    );
};

export default withErrorHandler(NotificationTypeFormDetails);
