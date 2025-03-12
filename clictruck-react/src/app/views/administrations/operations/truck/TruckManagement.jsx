import { Button, ButtonGroup, Dialog, Grid, Snackbar } from "@material-ui/core";
import { green, red } from "@material-ui/core/colors";
import {
  EditOutlined,
  LinkOffOutlined,
  LinkOutlined,
  VisibilityOutlined,
} from "@material-ui/icons";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { RecordStatus, Status, VehicleTypes } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  formatDate,
  Uint8ArrayToString,
  previewPDF,
} from "app/c1utils/utility";
import UploadTemplatePopup from "../common/UploadTemplatePopup";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";

/*** Administration > Truck Management Listing component. */
const TruckMangement = () => {
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
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const [truckData, setTruckData] = useState("");
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [loading, setLoading] = useState(true);
  const [isRefresh, setRefresh] = useState(false);
  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("common:msg.deleteSuccess"),
    severity: "success",
  });
  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");

  const [templateObj, setTemplateObj] = useState({
    open: false,
    fileName: "",
    fileData: null,
    errors: null,
    loading: false,
  });

  const { user } = useAuth();

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

  const handleConfirmSetActive = (id) => {
    console.log("id", id);
    setTruckData({ ...truckData, ...{ status: "active", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleConfirmSetInActive = (id) => {
    console.log("id", id);
    setTruckData({ ...truckData, ...{ status: "deactive", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.inActiveConfirm"),
    });
  };

  const handleSubmitStatus = () => {
    setLoading(true);
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
    sendRequest(
      "/api/v1/clickargo/clictruck/administrator/vehicle/" +
        truckData.id +
        "/" +
        truckData.status,
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
      name: "vhId",
      label: t("listing:common.id"),
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "vhPlateNo",
      label: t("listing:truck.platNo"),
    },
    {
      name: "tckCtMstVehType.vhtyName",
      label: t("listing:truck.type"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(VehicleTypes),
          renderValue: (v) => VehicleTypes[v]?.desc || v,
        },
        customFilterListOptions: {
          render: (v) => VehicleTypes[v]?.code || v,
        },
        customBodyRender: (value) => value,
      },
    },
    {
      name: "vhClass",
      label: t("listing:truck.class"),
      options: {
        sort: true,
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: ["1", "2", "3"],
          renderValue: (v) => {
            switch (v) {
              case "1":
                return "1";
              case "2":
                return "2";
              case "3":
                return "3";
              default:
                break;
            }
          },
        },
      },
    },
    {
      name: "vhVolume",
      label: t("listing:truck.volume"),
    },
    {
      name: "vhWeight",
      label: t("listing:truck.maxWeight"),
    },
    {
      name: "vhDtCreate",
      label: t("listing:common.dateCreated"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, false);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "vhDtLupd",
      label: t("listing:common.dateUpdated"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, false);
        },
        filterType: "custom",
        filterOptions: {
          display: customFilterDateDisplay,
        },
      },
    },
    {
      name: "vhStatus",
      label: t("listing:common.status"),
      options: {
        filter: true,
        display: true,
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
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
        },
      },
    },
    {
      name: "tckDoi.vhId",
      label: t("listing:common.action"),
      options: {
        filter: false,
        sort: false,
        display: true,
        viewColumns: false,
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const status = tableMeta.rowData[8];
          const id = tableMeta.rowData[0];
          return (
            <C1DataTableActions>
              {/* do note that this beats the purpose of just passing prop values into C1DataTableActions*/}
              <Grid
                container
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                style={{ minWidth: 180 }}
              >
                <Grid container direction="row" justifyContent="space-between">
                  <Grid container item alignItems="center">
                    <Grid item xs={4}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:edit")}
                        label={t("buttons:edit")}
                        action={() =>
                          history.push(
                            `/administrations/truck-management/edit/${id}`
                          )
                        }
                      >
                        <EditOutlined />
                      </C1LabeledIconButton>
                    </Grid>
                    <Grid item xs={4}>
                      {status === Status.ACV.code && (
                        <C1LabeledIconButton
                          tooltip={t("buttons:deactivate")}
                          label={t("buttons:deactivate")}
                          action={() => handleConfirmSetInActive(id)}
                        >
                          <LinkOffOutlined />
                        </C1LabeledIconButton>
                      )}

                      {status !== Status.ACV.code && (
                        <C1LabeledIconButton
                          tooltip={t("buttons:activate")}
                          label={t("buttons:activate")}
                          action={() => handleConfirmSetActive(id)}
                        >
                          <LinkOutlined />
                        </C1LabeledIconButton>
                      )}
                    </Grid>
                    <Grid item xs={4}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:view")}
                        label={t("buttons:view")}
                        action={() =>
                          history.push(
                            `/administrations/truck-management/view/${id}`
                          )
                        }
                      >
                        <VisibilityOutlined />
                      </C1LabeledIconButton>
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
            </C1DataTableActions>
          );
          // </Grid>
        },
      },
    },
  ];

  const handleEventUploadTemplete = (action) => {
    console.log("handle event download", action);
    if (action === "download") {
      sendRequest(
        `/api/v1/clickargo/clictruck/template/download/vehicle`,
        "downloadTemplate"
      );
    } else {
      //upload
      setTemplateObj((prev) => ({ ...prev, open: true }));
    }
  };

  const handleTemplateResetPopupEventHandler = () => {
    setTemplateObj((prev) => ({
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
      "/api/v1/clickargo/clictruck/template/upload/vehicle",
      "uploadTemplate",
      "post",
      { data: templateObj.fileData }
    );
  };

  const handleEventAddTruck = () => {
    history.push("/administrations/truck-management/new/-");
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const handleDownloadBuildBody = (values) => {
    return (
      values?.length > 0 &&
      values.map((value) => {
        return {
          data: value?.data?.map((v, idx) => {
            if (idx === 7) {
              switch (v) {
                case RecordStatus.INACTIVE.code:
                  v = RecordStatus.INACTIVE.desc;
                  break;
                case RecordStatus.ACTIVE.code:
                  v = RecordStatus.ACTIVE.desc;
                  break;
                default:
                  break;
              }
            }

            return v;
          }),
        };
      })
    );
  };

  useEffect(() => {
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "delete":
          setRefresh(true);
          setFilterHistory([{ attribute: "history", value: "default" }]);
          setSnackBarState({ ...snackBarState, open: true });
          setLoading(false);
          break;
        case "activate":
        case "deactivate": {
          setRefresh(true);
          break;
        }
        case "setStatus": {
          setLoading(false);
          setRefresh(true);
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("common:msg.updateSuccess"),
          });
          break;
        }
        case "downloadTemplate":
          previewPDF("Vehicle_Template.xlsx", res?.data?.data);
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
        default:
          break;
      }
    }
  }, [isLoading, res, error, urlId]);

  return (
    <React.Fragment>
      {isLoading && <MatxLoading />}
      {confirmDialog}
      {snackBar}
      <C1ListPanel
        routeSegments={[
          { name: t("administration:truckManagement.breadCrumbs.truckList") },
        ]}
        guideId="clictruck.administration.truck.list"
        title={t("administration:truckManagement.breadCrumbs.truckList")}
      >
        <C1DataTable
          url={"/api/v1/clickargo/clictruck/administrator/vehicle"}
          isServer={true}
          columns={columns}
          defaultOrder="vhDtCreate"
          defaultOrderDirection="desc"
          isRefresh={isRefresh}
          isShowToolbar
          isShowFilterChip
          isShowDownload={true}
          isShowPrint={true}
          isRowSelectable={false}
          guideId="clicdo.doi.co.jobs.list.table"
          csvFileName="TruckList"
          handleBuildBody={handleDownloadBuildBody}
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
                    eventHandler: () => {
                      handleEventUploadTemplete("download");
                    },
                  },
                  add: {
                    show: true,
                    eventHandler: handleEventAddTruck,
                  },
                }}
              />
            </ButtonGroup>
          }
        />
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

export default TruckMangement;
