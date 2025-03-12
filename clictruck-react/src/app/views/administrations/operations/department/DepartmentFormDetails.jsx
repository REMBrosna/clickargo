import { Button, Dialog, Divider, Grid, Paper, Tabs } from "@material-ui/core";
import AccessTimeOutlinedIcon from "@material-ui/icons/AccessTimeOutlined";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";

import C1AuditTab from "app/c1component/C1AuditTab";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1FormDetailsPanel from "app/c1component/C1FormDetailsPanel";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
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
import DepartmentDetails from "./DepartmentDetails";

const DepartmentFormDetails = () => {
  const { t } = useTranslation(["administration", "common"]);

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
  const [inputData, setInputData] = useState({});
  // eslint-disable-next-line
  const [controls, setControls] = useState([]);
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
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

  const initialButtons = {
    back: { show: true, eventHandler: () => handleExitOnClick() },
    save: {
      show: viewType === "edit",
      eventHandler: () => handleSubmitOnClick(),
    },
    delete: {
      show: inputData?.deptStatus !== "D" ? true : false,
      eventHandler: () => handleDeleteOnClick(),
    },
  };
  /** ------------------- Update states ----------------- */
  useEffect(() => {
    if (viewType === "new") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/department/-",
        "getData",
        "get",
        null
      );
    } else if (viewType === "view" || viewType === "edit") {
      sendRequest(
        "/api/v1/clickargo/clictruck/administrator/department/" + id,
        "getData",
        "get",
        null
      );
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
      console.log("error", error)
      console.log("validation", validation)
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

  const handleChangeMultiple = (e) => {
    const { name, options } = e.target;

    if (name === "accnUsers" || name === "deptUsers") {
      const users = [];
      for (let i = 0, l = options?.length; i < l; i += 1) {
        if (options[i].selected) {
          users.push(options[i].value);
        }
      }
      if (name === "accnUsers") setSelectedAccnUsers(users);
      else if (name === "deptUsers") setSelectedUsers(users);
    } else if (name === "accnVehs" || name === "deptVehs") {
      const vehs = [];
      for (let i = 0, l = options?.length; i < l; i += 1) {
        if (options[i].selected) {
          vehs.push(options[i].value);
        }
      }
      if (name === "accnVehs") setSelectedAccnVehs(vehs);
      else if (name === "deptVehs") setSelectedVehs(vehs);
    }
  };

  /** Handler for the forward button */
  const handleAvailToSelected = (e, field) => {
    if (field === "users") {
      //filter accnUsers  to get the selected user object
      let selUsersFromAccnUsers = inputData?.accnUsers?.filter((el) =>
        selectedAccnUsers?.includes(el?.usrUid)
      );

      //merge to deptUsers
      let users = [...inputData?.deptUsers, ...selUsersFromAccnUsers];
      setSelectedUsers(users);

      //filter accnUsers t o get the non-selected user object
      let modAccnUsers = inputData?.accnUsers?.filter(
        (el) => !selectedAccnUsers?.includes(el?.usrUid)
      );

      //update inputdata
      setInputData({
        ...inputData,
        accnUsers: [...modAccnUsers],
        deptUsers: [...users],
      });
    } else if (field === "vehs") {
      //filter accnVehs to get the selected user object
      let selvehsFromAccnVehs = inputData?.accnVehs?.filter((el) =>
        selectedAccnVehs?.includes(el?.vhId)
      );
      //merge to deptvehs
      let vehs = [...inputData?.deptVehs, ...selvehsFromAccnVehs];
      setSelectedVehs(vehs);

      //filter accnVehs to get the non-selected veh object
      let modAccnVehs = inputData?.accnVehs?.filter(
        (el) => !selectedAccnVehs?.includes(el?.vhId)
      );

      //update inputData
      setInputData({
        ...inputData,
        accnVehs: [...modAccnVehs],
        deptVehs: [...vehs],
      });
    }
  };

  /** Handler for the back button */
  const handleSelectedToAvail = (e, field) => {
    if (field === "users") {
      //filter accnUsers  to get the selected user object
      let selUsersFromDeptUsers = inputData?.deptUsers?.filter((el) =>
        selectedUsers?.includes(el?.usrUid)
      );

      //merge to deptUsers
      let users = [...inputData?.accnUsers, ...selUsersFromDeptUsers];
      setSelectedAccnUsers(users);

      //filter accnUsers t o get the non-selected user object
      let modDeptUsers = inputData?.deptUsers?.filter(
        (el) => !selectedUsers?.includes(el?.usrUid)
      );

      //update inputdata
      setInputData({
        ...inputData,
        accnUsers: [...users],
        deptUsers: [...modDeptUsers],
      });
    } else if (field === "vehs") {
      //filter accnVehs to get the selected user object
      let selvehsFromDeptVehs = inputData?.deptVehs?.filter((el) =>
        selectedVehs?.includes(el?.vhId)
      );
      //merge to deptvehs
      let vehs = [...inputData?.accnVehs, ...selvehsFromDeptVehs];
      setSelectedAccnVehs(vehs);

      //filter accnVehs to get the non-selected veh object
      let modDeptVehs = inputData?.deptVehs?.filter(
        (el) => !selectedVehs?.includes(el?.vhId)
      );

      //update inputData
      setInputData({
        ...inputData,
        accnVehs: [...vehs],
        deptVehs: [...modDeptVehs],
      });
    }
  };

  const handleDateChange = (name, e) => {
    setInputData({ ...inputData, ...deepUpdateState(inputData, name, e) });
  };

  const handleExitOnClick = () => {
    history.push("/administrations/department/list");
  };

  const handleSaveOnClick = () => {
    setLoading(true);
    setValidationErrors({});
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    switch (viewType) {
      case "new":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/department",
          "createData",
          "post",
          { ...inputData }
        );
        break;
      case "edit":
        sendRequest(
          "/api/v1/clickargo/clictruck/administrator/department/" + id,
          "updateData",
          "put",
          { ...inputData }
        );
        break;
      default:
        break;
    }
  };

  const handleSubmitDelete = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/department/" + id,
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
      "/api/v1/clickargo/clictruck/administrator/department/" +
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

  // ///////
  useEffect(() => {
    if(inputData?.accnUsers) {
      let accnUsers = inputData?.accnUsers?.sort((a,b) => (a.usrName||"").localeCompare((b.usrName||"")));
      //console.log("accnUsers",accnUsers);
      setInputData({...inputData, accnUsers});
    }
    if(inputData?.deptUsers) {
      let deptUsers = inputData?.deptUsers?.sort((a,b) => (a.usrName||"").localeCompare((b.usrName||"")));
      setInputData({...inputData, deptUsers});
    }
    // eslint-disable-next-line
  }, [inputData?.accnUsers?.length, inputData?.deptUsers?.length]);


  useEffect(() => {
    if(inputData?.accnVehs) {
      let accnVehs = inputData?.accnVehs?.sort((a,b) => (a.vhPlateNo||"").localeCompare((b.vhPlateNo||"")));
      setInputData({...inputData, accnVehs});
    }
    if(inputData?.deptVehs) {
      let deptVehs = inputData?.deptVehs?.sort((a,b) => (a.vhPlateNo||"").localeCompare((b.vhPlateNo||"")));
      setInputData({...inputData, deptVehs});
    }
    // eslint-disable-next-line
  }, [inputData?.accnVehs?.length, inputData?.deptVehs?.length]);

  // ///////

  let bcLabel =
    viewType === "edit"
      ? t("administration:department.form.edit")
      : t("administration:department.form.view");
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
          bcLabel = t("administration:department.form.edit");
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
          bcLabel = t("administration:department.form.new");
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
            name: t("administration:department.listing.title"),
            path: "/administrations/department/list",
          },
        ]}
        title={bcLabel}
        formButtons={formButtons}
        initialValues={{ ...inputData }}
        values={{ ...inputData }}
        snackBarOptions={{
          ...snackBarOptions,
          redirectPath: "/administrations/department/list",
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
                    <DepartmentDetails
                      viewType={viewType}
                      handleInputChange={handleInputChange}
                      handleDateChange={handleDateChange}
                      handleChangeMultiple={handleChangeMultiple}
                      selectedAccnUsers={selectedAccnUsers}
                      selectedAccnVehs={selectedAccnVehs}
                      selectedUsers={selectedUsers}
                      selectedVehs={selectedVehs}
                      handleSelectedToAvail={handleSelectedToAvail}
                      handleAvailToSelected={handleAvailToSelected}
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

export default withErrorHandler(DepartmentFormDetails);
