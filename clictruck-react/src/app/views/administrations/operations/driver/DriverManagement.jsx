import { Button, ButtonGroup, Dialog, Grid, Snackbar } from "@material-ui/core";
import {
  EditOutlined,
  LinkOffOutlined,
  LinkOutlined,
  VisibilityOutlined,
} from "@material-ui/icons";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1DataTable from "app/c1component/C1DataTable";
import C1FormButtons from "app/c1component/C1FormButtons";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1ListPanel from "app/c1component/C1ListPanel";
import useHttp from "app/c1hooks/http";
import { RecordStatus, Status } from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import { customFilterDateDisplay, formatDate, Uint8ArrayToString, previewPDF } from "app/c1utils/utility";
import UploadTemplatePopup from "../common/UploadTemplatePopup";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { ConfirmationDialog, MatxLoading } from "matx";

const DriverMangement = () => {
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
  const [dataStatus, setDataStatus] = useState("");
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [loading, setLoading] = useState(true);
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

  const handleEventUploadTemplete = (action) => {
    console.log("handle event download", action);
    if (action === "download") {
      sendRequest(
        `/api/v1/clickargo/clictruck/template/download/driver`,
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
      "/api/v1/clickargo/clictruck/template/upload/driver",
      "uploadTemplate",
      "post",
      { data: templateObj.fileData }
    );
  };
  const handleConfirmSetActive = (id) => {
    console.log("id", id);
    setDataStatus({ ...dataStatus, ...{ status: "active", id } });
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "SAVE",
      open: true,
      msg: t("common:msg.activeConfirm"),
    });
  };

  const handleConfirmSetInActive = (id) => {
    console.log("id", id);
    setDataStatus({ ...dataStatus, ...{ status: "deactive", id } });
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
      "/api/v1/clickargo/clictruck/administrator/driver/" +
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
      name: "drvId",
      label: t("listing:common.id"),
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "drvName",
      label: t("listing:common.name"),
    },
    {
      name: "drvLicenseNo",
      label: t("listing:driver.lisenseNo"),
    },
    {
      name: "drvMobileId",
      label: t("listing:common.userId"),
    },
    {
      name: "drvEmail",
      label: t("listing:common.email"),
    },
    {
      name: "drvPhone",
      label: t("listing:common.phone"),
    },
    {
      name: "drvDtCreate",
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
      name: "drvDtLupd",
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
      name: "drvStatus",
      label: t("listing:common.status"),
      options: {
        filter: true,
        display: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          return getStatusDesc(value);
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
      name: "drvId",
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
                          `/administrations/driver-management/edit/${id}`
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
                          `/administrations/driver-management/view/${id}`
                        )
                      }
                    >
                      <VisibilityOutlined />
                    </C1LabeledIconButton>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          );
        },
      },
    },
  ];

  const buttonStyle = {
    marginLeft: "-20px",
    marginButton: "-20px",
  };

  const handleEventDownloadTemplete = () => {
    console.log("download");
  };

  const handleEventAddTruck = () => {
    history.push("/administrations/driver-management/new/-");
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  //   const handleDownloadBuildBody = (values) => {
  //     return (
  //       values?.length > 0 &&
  //       values.map((value) => {
  //         value.data[6] = formatDate(value?.data[6], false);
  //         value.data[7] = formatDate(value?.data[7], false);
  //         return value;
  //       })
  //     );
  //   };

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
        case "downloadTemplate":
          previewPDF("Driver_Template.xlsx", res?.data?.data);
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
        case "setStatus": {
          setLoading(false);
          setRefresh(true);
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("common:msg.updateSuccess"),
          });
        }
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
          { name: t("administration:driverManagement.breadCrumbs.list") },
        ]}
        guideId="clictruck.administration.driver.list"
        title={t("administration:driverManagement.breadCrumbs.list")}
      >
        <C1DataTable
          url={"/api/v1/clickargo/clictruck/administrator/driver"}
          isServer={true}
          columns={columns}
          defaultOrder={['drvId', "drvDtCreate"]}
          defaultOrderDirection="desc"
          isRefresh={isRefresh}
          isShowToolbar
          isShowFilterChip
          isShowDownload={true}
          isShowPrint={true}
          isRowSelectable={false}
          //   handleBuildBody={handleDownloadBuildBody}
          csvFileName="DriverManagement"
          filterBy={[
            { attribute: "TcoreAccn.accnId", value: user?.coreAccn?.accnId },
          ]}
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

export default DriverMangement;
