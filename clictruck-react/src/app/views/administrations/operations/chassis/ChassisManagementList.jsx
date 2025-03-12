import {
  Button,
  ButtonGroup,
  CircularProgress,
  Dialog,
  Grid,
  Snackbar,
} from "@material-ui/core";
import {
  LinkOffOutlined,
  LinkOutlined,
  VisibilityOutlined,
} from "@material-ui/icons";
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import NearMeOutlined from "@material-ui/icons/NearMeOutlined";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1IconButton from "app/c1component/C1IconButton";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import useHttp from "app/c1hooks/http";
import {
  ChassisTypes,
  CK_MST_CHASSIS_TYPE,
  RecordStatus,
  Status,
} from "app/c1utils/const";
import { deepUpdateState, getStatusDesc} from "app/c1utils/statusUtils";
import UploadTemplatePopup from "../common/UploadTemplatePopup";
import { customFilterDateDisplay, Uint8ArrayToString, previewPDF } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { ConfirmationDialog, MatxLoading } from "matx";

const ChassisManagementList = () => {
  const { t } = useTranslation([
    "buttons",
    "listing",
    "administration",
    "common",
    "cargoowners",
  ]);
  const [filterHistory, setFilterHistory] = useState([
    { attribute: "history", value: "default" },
  ]);
  const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();
  const [dataStatus, setDataStatus] = useState("");
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [loading, setLoading] = useState(false);
  const [isRefresh, setRefresh] = useState(false);
  const { user } = useAuth();
  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("common:msg.deleteSuccess"),
    severity: "success",
  });
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");

  const [inputData, setInputData] = useState({});
  const [openPopup, setOpenPopup] = useState(false);
  const [viewType, setViewType] = useState("");
  const [validationErrors, setValidationErrors] = useState({});
  const [chassisTypes, setChassisTypes] = useState([]);

  const handleCloseSnackBar = () => {
    setRefresh(false);
    setSnackBarState({ ...snackBarState, open: false });
  };

  let snackBar = null;
  if (snackBarState.open) {
    const anchorOriginV = snackBarState.vertical;
    const anchorOriginH = snackBarState.horizontal;
    snackBar = (
      <Snackbar
        anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
        open={snackBarState.open}
        onClose={handleCloseSnackBar}
        autoHideDuration={3000}
        key={anchorOriginV + anchorOriginH}
      >
        <C1Alert
          onClose={handleCloseSnackBar}
          severity={snackBarState.severity}
        >
          {snackBarState.msg}
        </C1Alert>
      </Snackbar>
    );
  }

  let confirmDialog = "";
  if (openSubmitConfirm.open) {
    confirmDialog = (
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
    );
  }

  const [templateObj, setTemplateObj] = useState({
    open: false,
    fileName: "",
    fileData: null,
    errors: null,
    loading: false,
  });

  const handleOpenPopup = (id, viewType) => {
    setLoading(true);
    setInputData({});
    setViewType(viewType);
    setValidationErrors({});
    setOpenPopup(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/administrator/chassis/${id}`,
      "getChassis",
      "GET"
    );
  };

  const handleSaveButton = () => {
    setLoading(true);
    setRefresh(false);
    if (viewType === "NEW")
      sendRequest(
        `/api/v1/clickargo/clictruck/administrator/chassis`,
        "createData",
        "POST",
        { ...inputData }
      );
    else
      sendRequest(
        `/api/v1/clickargo/clictruck/administrator/chassis/${inputData?.chsId}`,
        "updateData",
        "PUT",
        { ...inputData }
      );
  };

  const handleConfirmSetActive = (id) => {
    setDataStatus({ ...dataStatus, ...{ status: "active", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleConfirmSetInActive = (id) => {
    setDataStatus({ ...dataStatus, ...{ status: "deactive", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };

  const handleSubmitStatus = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/chassis/" +
        dataStatus.id +
        "/" +
        dataStatus.status,
      "setStatus",
      "put",
      null
    );
  };

  const eventHandler = (action) => {
    if (action.toLowerCase() === "save") {
      handleSubmitStatus();
    }
  };

  const columns = [
    {
      name: "chsId",
      label: "chsId",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "tckCtMstChassisType.chtyName",
      label: t("listing:chassis.chsSize"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(ChassisTypes),
          renderValue: (v) => {
            return ChassisTypes[v].desc;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            return ChassisTypes[v].desc;
          },
        },
      },
    },
    {
      name: "chsNo",
      label: t("listing:chassis.chsNo"),
    },
    {
      name: "chsDtCreate",
      label: t("listing:chassis.chsDtCreate"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return moment(value).format("DD/MM/YYYY");
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "chsDtLupd",
      label: t("listing:chassis.chsDtLupd"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return moment(value).format("DD/MM/YYYY");
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "chsStatus",
      label: t("listing:chassis.chsStatus"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
          // switch (value) {
          //     case Status.ACV.code:
          //         return <small className="px-3 py-6px border-radius-4" style={{ backgroundColor: green[500], color: green[50] }}>
          //             {Status.ACV.desc}
          //         </small>;
          //     case Status.NCV.code:
          //         return <small className="px-3 py-6px border-radius-4" style={{ backgroundColor: red[500], color: red[50] }}>
          //             {Status.NCV.desc}
          //         </small>;
          //     default: return <small className="px-3 py-6px border-radius-4" style={{ backgroundColor: red[500], color: red[50] }}>
          //         {Status.NCV.desc}
          //     </small>;
          // }
        },
        filterType: "dropdown",
        filterOptions: {
          names: [RecordStatus.ACTIVE.code, RecordStatus.INACTIVE.code],
          renderValue: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              default:
                break;
            }
          },
        },
        customFilterListOptions: {
          render: (v) => {
            switch (v) {
              case RecordStatus.ACTIVE.code:
                return RecordStatus.ACTIVE.desc;
              case RecordStatus.INACTIVE.code:
                return RecordStatus.INACTIVE.desc;
              default:
                break;
            }
          },
        },
      },
    },
    {
      name: "",
      label: t("listing:chassis.chsAction"),
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const status = tableMeta.rowData[5];
          const id = tableMeta.rowData[0];
          return (
            <Grid
              container
              direction="row"
              justifyContent="center"
              alignItems="center"
            >
              <Grid item xs={4}>
                <C1LabeledIconButton
                  tooltip={t("buttons:view")}
                  label={t("buttons:view")}
                  action={() => handleOpenPopup(id, "VIEW")}
                >
                  <VisibilityOutlined />
                </C1LabeledIconButton>
              </Grid>
              <Grid item xs={4}>
                {status === Status.ACV.code && (
                  <C1LabeledIconButton
                    tooltip={t("buttons:delete")}
                    label={t("buttons:delete")}
                    action={() => handleConfirmSetInActive(id)}
                  >
                    <DeleteOutlinedIcon />
                  </C1LabeledIconButton>
                )}
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

  const handleEventUploadTemplete = (action) => {
    console.log("handle event download", action);
    if (action === "download") {
      sendRequest(
        `/api/v1/clickargo/clictruck/template/download/chassis`,
        "downloadTemplate"
      );
    } else {
      //upload
      setTemplateObj((prev)=>({ ...prev, open: true }));
    }
  };

  const handleTemplateResetPopupEventHandler = () => {
    setTemplateObj((prev)=>({
      ...prev,
      open: false,
      fileData: null,
      fileName: null,
      errors: null,
      loading: false,
    }));
  };

  const handleFileInputChange = (e) => {
    e.preventDefault();
    var file = e.target.files[0];
    if (!file) return;

    if (
      [
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      ].includes(file.type)
    ) {
      const fileReader = new FileReader();
      fileReader.readAsArrayBuffer(file);
      fileReader.onload = (e) => {
        const uint8Array = new Uint8Array(e.target.result);
        var imgStr = Uint8ArrayToString(uint8Array);
        var base64Sign = btoa(imgStr);

        setTemplateObj({
          ...templateObj,
          fileName: file?.name,
          fileData: base64Sign,
          errors: null,
        });
      };
    } else {
      setTemplateObj({
        ...templateObj,
        fileName: file?.name,
        fileData: null,
        errors: {
          msg: "File type not allowed",
        },
      });
    }
  };

  const handleTemplateUpload = () => {
    setTemplateObj({ ...templateObj, loading: true });
    sendRequest(
      "/api/v1/clickargo/clictruck/template/upload/chassis",
      "uploadTemplate",
      "post",
      { data: templateObj.fileData }
    );
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    setInputData({
      ...inputData,
      ...deepUpdateState(inputData, elName, e.target.value),
    });
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  useEffect(() => {
    sendRequest(CK_MST_CHASSIS_TYPE, "fetchChassisTypes", "GET");
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      switch (urlId) {
        case "fetchChassisTypes":
          setChassisTypes(res?.data?.data);
          break;
        case "getChassis":
          setLoading(false);
          setInputData(res?.data);
          break;
        case "createData":
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("common:msg.saveSuccess"),
          });
          setOpenPopup(false);
          setTimeout(() => setLoading(false), 500);
          setRefresh(true);
          break;
        case "updateData":
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("common:msg.updateSuccess"),
          });
          setOpenPopup(false);
          setLoading(false);
          setRefresh(true);
          break;
        case "delete":
          setRefresh(true);
          setFilterHistory([{ attribute: "history", value: "default" }]);
          setSnackBarState({ ...snackBarState, open: true });
          setLoading(false);
          break;
        case "activate":
        case "deactivate":
          setRefresh(true);
          break;
        case "downloadTemplate":
          previewPDF("Chassis_Template.xlsx", res?.data?.data);
          break;
        case "uploadTemplate":
          setTemplateObj({
            ...templateObj,
            open: false,
            loading: false,
            fileData: null,
            fileName: null,
            errors: null,
          });

          setSnackBarState({
            ...snackBarState,
            msg: "Template uploaded successfully.",
            open: true,
          });
          setRefresh(true);
          break;
        case "setStatus":
          setLoading(false);
          setRefresh(true);
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("common:msg.deleteSuccess"),
          });
          setOpenPopup(false);
        default:
          break;
      }
    }
    if (error) setLoading(false);

    if (validation) {
      setValidationErrors({ ...validation });
      setLoading(false);
    }
  }, [isLoading, res, error, urlId]);

  return (
    <React.Fragment>
      {isLoading && <MatxLoading />}
      {confirmDialog}
      {snackBar}
      <C1ListPanel
        routeSegments={[
          { name: t("administration:chassisManagement.breadCrumbs.list") },
        ]}
        guideId="clictruck.administration.chassis.list"
        title={t("administration:chassisManagement.breadCrumbs.list")}
      >
        <C1DataTable
          url={"/api/v1/clickargo/clictruck/administrator/chassis"}
          isServer={true}
          columns={columns}
          defaultOrder="chsDtCreate"
          defaultOrderDirection="desc"
          isRefresh={isRefresh}
          isShowToolbar
          isShowFilterChip
          isShowDownload={true}
          isShowPrint={true}
          isRowSelectable={false}
          guideId="clicdo.doi.co.jobs.list.table"
          csvFileName="ChassisList"
          viewTextFilter={
            <ButtonGroup
              color="primary"
              key="viewTextFilter"
              aria-label="outlined primary button group"
            >
              <C1FormButtons
                options={{
                  uploadTemplate: {
                    show: true,
                    eventHandler: handleEventUploadTemplete,
                  },
                  downloadTemplate: {
                    show: true,
                    eventHandler: ()=>{handleEventUploadTemplete("download")},
                  },
                  add: {
                    show: true,
                    eventHandler: () => handleOpenPopup("-", "NEW"),
                  },
                }}
              />
            </ButtonGroup>
          }
        />

        <C1PopUp
          maxWidth={"sm"}
          title={
            viewType === "NEW"
              ? t("listing:chassis.newPopupTitle")
              : t("listing:chassis.chsDetails")
          }
          openPopUp={openPopup}
          setOpenPopUp={setOpenPopup}
          actionsEl={
            <>
              {viewType === "NEW" && (
                <C1IconButton
                  tooltip={t("buttons:submit")}
                  childPosition="right"
                >
                  {loading ? (
                    <CircularProgress color="inherit" size={30} />
                  ) : (
                    <NearMeOutlined
                      color="primary"
                      fontSize="large"
                      onClick={handleSaveButton}
                    />
                  )}
                </C1IconButton>
              )}
              {inputData?.chsStatus === RecordStatus.INACTIVE.code &&
                viewType === "EDIT" && (
                  <C1IconButton
                    tooltip={t("buttons:activate")}
                    childPosition="right"
                  >
                    {loading ? (
                      <CircularProgress color="inherit" size={30} />
                    ) : (
                      <LinkOutlined
                        color="primary"
                        fontSize="large"
                        onClick={() => handleConfirmSetActive(inputData?.chsId)}
                      />
                    )}
                  </C1IconButton>
                )}
              {inputData?.chsStatus === RecordStatus.ACTIVE.code &&
                viewType === "EDIT" && (
                  <C1IconButton
                    tooltip={t("buttons:deactivate")}
                    childPosition="right"
                  >
                    {loading ? (
                      <CircularProgress color="inherit" size={30} />
                    ) : (
                      <LinkOffOutlined
                        color="primary"
                        fontSize="large"
                        onClick={() =>
                          handleConfirmSetInActive(inputData?.chsId)
                        }
                      />
                    )}
                  </C1IconButton>
                )}
            </>
          }
        >
          <C1CategoryBlock>
            <C1SelectAutoCompleteField
              name="tcoreAccn.accnId"
              label={t("listing:chassis.chsCompany")}
              options={{
                url: `/api/co/ccm/entity/accn/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=accnId&iColumns=2&mDataProp_1=accnId&sSearch_1=${user?.coreAccn?.accnId}`,
                key: "accnId",
                id: "accnId",
                desc: "accnName",
              }}
              isServer={true}
              value={
                inputData?.tcoreAccn?.accnId ? inputData?.tcoreAccn?.accnId : ""
              }
              disabled={true}
            />
            <C1SelectAutoCompleteField
              label={t("listing:chassis.chsAccnType")}
              name="tcoreAccn.TMstAccnType.atypId"
              value={
                inputData?.tcoreAccn?.TMstAccnType?.atypId
                  ? inputData?.tcoreAccn?.TMstAccnType?.atypId
                  : ""
              }
              options={{
                url: `/api/co/master/entity/accnType/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=atypId&iColumns=2&mDataProp_1=atypId&sSearch_1=${user?.coreAccn?.TMstAccnType?.atypId}`,
                key: "atypId",
                id: "atypId",
                desc: "atypDescription",
              }}
              isServer={true}
              disabled={true}
            />

            <C1SelectAutoCompleteField
              name="tckCtMstChassisType.chtyId"
              label={t("listing:chassis.chsSize")}
              required
              value={
                inputData?.tckCtMstChassisType?.chtyId
                  ? inputData?.tckCtMstChassisType?.chtyId
                  : ""
              }
              onChange={(e, name, value) =>
                handleInputChange({ target: { name, value: value?.value } })
              }
              optionsMenuItemArr={
                chassisTypes &&
                chassisTypes.map((item, i) => {
                  return {
                    value: item.chtyId,
                    desc: item.chtyName,
                  };
                })
              }
              disabled={viewType !== "NEW" || loading}
              error={
                validationErrors["TCkCtMstChassisType.chtyId"] !== undefined
              }
              helperText={validationErrors["TCkCtMstChassisType.chtyId"] || ""}
            />

            <C1InputField
              name="chsNo"
              label={t("listing:chassis.chsNo")}
              required
              onChange={handleInputChange}
              disabled={viewType !== "NEW" || loading}
              value={inputData?.chsNo}
              error={validationErrors["chsNo"] !== undefined}
              helperText={validationErrors["chsNo"] || ""}
            />
          </C1CategoryBlock>
        </C1PopUp>
      </C1ListPanel>

      <Dialog maxWidth="xs" open={openWarning}>
        <div className="p-8 text-center w-360 mx-auto">
          <h4 className="capitalize m-0 mb-2">{"Information"}</h4>
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
      <UploadTemplatePopup 
        popupObj={templateObj}
        handleFileInputChange={handleFileInputChange}
        uploadEventHandler={handleTemplateUpload}
        handlePopupEventHandler={handleTemplateResetPopupEventHandler}
      />
    </React.Fragment>
  );
};

export default ChassisManagementList;
