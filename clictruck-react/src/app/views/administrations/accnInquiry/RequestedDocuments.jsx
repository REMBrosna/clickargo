import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  Grid,
  Snackbar,
  TableCell,
} from "@material-ui/core";
import grey from "@material-ui/core/colors/grey";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import { NearMeOutlined } from "@material-ui/icons";
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import GetAppIcon from "@material-ui/icons/GetAppOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1Alert from "app/c1component/C1Alert";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1IconButton from "app/c1component/C1IconButton";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import useHttp from "app/c1hooks/http";
import { deepUpdateState } from "app/c1utils/stateUtils";
import {
  customFilterDateDisplay,
  formatDate,
  isEmpty,
  previewPDF,
  Uint8ArrayToString,
} from "app/c1utils/utility";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";

import { RequestState } from "./AccnInquiryList";
import UploadPopup from "./UploadPopup";

const useTableStyle = makeStyles({
  table: {
    minWidth: 450,
  },
  column: {
    width: 40,
  },
  // optionally added hover
  row: {
    "&:hover": {
      backgroundColor: grey[100],
    },
  },
});

const RequestedDocuments = ({ inputData, disableAdd }) => {
  const [isRefresh, setRefresh] = useState(false);
  const [accnReqId, setAccnReqId] = useState(inputData?.airId ?? "");
  const { t } = useTranslation(["listing", "common", "opadmin"]);

  const [openWarning, setOpenWarning] = useState(false);
  const [warningMessage, setWarningMessage] = useState("");

  const [view, setView] = useState(false);
  const [openAddPopUp, setOpenAddPopUp] = useState(false);
  const [popUpFieldError, setPopUpFieldError] = useState({});

  const popupDefaultValue = {
    tmstAttType: { mattId: "" },
    data: null,
    airdFilename: "",
    tckCtAccnInqReq: { airId: accnReqId },
  };
  const [popUpDetails, setPopUpDetails] = useState(popupDefaultValue);
  const [openTripAttPopUp, setOpenTripAttPopUp] = useState(false);

  const ALLOWED_FILE_EXTS = ["pdf", "doc", "docx", "jpeg", "jpg", "png"];
  const [confirm, setConfirm] = useState({ airdId: null });

  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const [openDeleteConfirm, setOpenDeleteConfirm] = useState({
    action: null,
    open: false,
  });
  const [openDeleteConfirmTrAt, setOpenDeleteConfirmTrAt] = useState({
    action: null,
    open: false,
  });
  const [success, setSuccess] = useState(false);
  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: "",
    severity: "success",
  });

  const [validationErrors, setValidationErrors] = useState({});

  /** ---------------- Declare states ------------------- */
  const { isLoading, res, validation, error, urlId, sendRequest } = useHttp();

  /** --------------- Update states -------------------- */

  useEffect(() => {
    if (!isLoading && res && !error && !validation) {
      setLoading(isLoading);
      switch (urlId) {
        case "createDocs": {
          setSuccess(true);
          setRefresh(true);
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("opadmin:accnInq.docsUploadSuccess"),
          });

          break;
        }

        case "download": {
          previewPDF(res?.data?.airdFilename, res?.data?.data);
          break;
        }

        case "delete": {
          setSuccess(true);
          setRefresh(true);
          setConfirm({ ...confirm, airdId: null });
          setSnackBarState({
            ...snackBarState,
            open: true,
            msg: t("opadmin:accnInq.docsDeleteSuccess"),
          });

          break;
        }
        default:
          break;
      }
    }
    if (error) {
      //goes back to the screen
    }

    if (validation) {
      setValidationErrors({ ...validation });
      setOpenTripAttPopUp(true);
    }
  }, [isLoading, res, validation, error, urlId]);

  /** ---------------- Event handlers ------------------- */
  const handleViewFile = (e, airdId) => {
    setLoading(true);
    sendRequest(
      `/api/v1/clickargo/clictruck/inquiry/docs/${airdId}`,
      "download"
    );
  };

  const handleDeleteConfirm = (e, airdId) => {
    e.preventDefault();
    setConfirm({ ...confirm, airdId: airdId });
    setOpen(true);
    setOpenDeleteConfirm({
      ...openDeleteConfirm,
      action: "DELETE",
      open: true,
    });
  };

  const handleDeleteHandler = (e) => {
    if (confirm && !confirm.airdId) return;

    setLoading(true);
    setSuccess(false);
    setRefresh(false);
    setOpen(false);
    sendRequest(
      "/api/v1/clickargo/clictruck/inquiry/docs/" + confirm.airdId,
      "delete",
      "delete",
      {}
    );
  };

  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
  };

  const handleWarningAction = (e) => {
    setOpenWarning(false);
    setWarningMessage("");
  };

  const popUpAddHandler = () => {
    if (accnReqId === "empty") {
      setOpenWarning(true);
      setWarningMessage("Account Inquiry Request ID not available.");
      return;
    }
    setView(false);
    setOpenAddPopUp(true);
    setPopUpFieldError({});
    setPopUpDetails(popupDefaultValue);
  };

  const handlePopUpFieldValidate = () => {
    let errors = {};

    if (popUpDetails?.tmstAttType?.mattId === "") {
      errors.mattId = t("common:validationMsgs.required");
    }

    let ext =
      popUpDetails?.airdFilename.substring(
        popUpDetails?.airdFilename.lastIndexOf(".") + 1,
        popUpDetails?.airdFilename.length
      ) || popUpDetails?.airdFilename;
    if (!ALLOWED_FILE_EXTS.includes(ext.toLowerCase())) {
      errors.airdFilename =
        "Allowed file types are pdf, doc, docx, jpeg, jpg, png";
    }

    if (popUpDetails.airdFilename === "") {
      errors.attName = t("common:validationMsgs.required");
    }

    return errors;
  };

  const uploadAttachment = (e) => {
    if (!isEmpty(handlePopUpFieldValidate())) {
      setPopUpFieldError(handlePopUpFieldValidate());
    } else {
      setLoading(true);
      setPopUpFieldError({});
      setSuccess(false);
      setRefresh(false);
      setOpenAddPopUp(false);
      sendRequest(
        `/api/v1/clickargo/clictruck/inquiry/docs`,
        "createDocs",
        "POST",
        { ...popUpDetails }
      );
    }
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    setPopUpDetails({
      ...popUpDetails,
      ...deepUpdateState(popUpDetails, elName, e.target.value),
    });
  };

  const handleInputFileChange = (e) => {
    e.preventDefault();
    var file = e.target.files[0];
    if (!file) return;

    const fileReader = new FileReader();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = (e) => {
      const uint8Array = new Uint8Array(e.target.result);
      var imgStr = Uint8ArrayToString(uint8Array);
      var base64Sign = btoa(imgStr);
      setPopUpDetails({
        ...popUpDetails,
        airdFilename: file.name,
        data: base64Sign,
      });
    };
  };

  const columns = [
    {
      name: "airdId",
      label: "",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "tmstAttType.mattId",
      label: " ",
      options: {
        display: "excluded",
        filter: false,
      },
    },
    {
      name: "tmstAttType.mattName",
      label: t("listing:attachments.docType"),
      options: {
        filter: false,
        filterType: "dropdown",
        filterOptions: {
          names: ["OTHERS"],
          renderValue: (v) => {
            switch (v) {
              case "OTHERS":
                return "OTHERS";
              default:
                break;
            }
          },
        },
      },
    },
    {
      name: "airdFilename",
      label: t("listing:attachments.docName"),
    },
    {
      name: "airdDtCreate",
      label: t("listing:attachments.dtCreate"),
      options: {
        filter: true,
        filterType: "custom",
        display: true,
        filterOptions: {
          display: customFilterDateDisplay,
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
      },
    },
    {
      name: "action",
      label: t("listing:attachments.action"),
      options: {
        filter: false,
        display: true,
        sort: false,
        viewColumns: false,
        customHeadLabelRender: (columnMeta) => {
          return <div style={{ textAlign: "center" }}>{columnMeta.label}</div>;
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          let airdId = tableMeta.rowData[0];

          return (
            <C1DataTableActions>
              <Grid container alignItems="flex-start" justifyContent="center">
                <span style={{ minWidth: "48px" }}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:download")}
                    label={t("buttons:download")}
                    action={(e) => handleViewFile(e, airdId)}
                  >
                    <GetAppIcon />
                  </C1LabeledIconButton>
                </span>
                {(inputData?.airReqState === RequestState.PENDING.code ||
                  inputData?.airReqState === RequestState.INPROGRESS.code) && (
                  <span style={{ minWidth: "48px" }}>
                    <C1LabeledIconButton
                      tooltip={t("buttons:delete")}
                      label={t("buttons:delete")}
                      action={(e) => handleDeleteConfirm(e, airdId)}
                    >
                      <DeleteOutlinedIcon />
                    </C1LabeledIconButton>
                  </span>
                )}
              </Grid>
            </C1DataTableActions>
          );
        },
      },
    },
  ];

  let snackBar = null;
  if (success) {
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

  let actionElJobAtt = (
    <C1IconButton tooltip={t("buttons:submit")} childPosition="right">
      {loading ? (
        <CircularProgress color="inherit" size={30} />
      ) : (
        <NearMeOutlined
          color="primary"
          fontSize="large"
          onClick={() => uploadAttachment()}
        />
      )}
    </C1IconButton>
  );

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <Grid item xs={12}>
        <C1DataTable
          url={"/api/v1/clickargo/clictruck/inquiry/docs"}
          isServer={true}
          columns={columns}
          defaultOrder="airdStatus"
          defaultOrderDirection="desc"
          filterBy={[
            { attribute: "TCkCtAccnInqReq.airId", value: accnReqId },
            { attribute: "airdStatus", value: "A" },
          ]}
          isRefresh={isRefresh}
          isShowViewColumns={true}
          isShowFilter={true}
          showAdd={
            !disableAdd
              ? {
                  type: "popUp",
                  popUpHandler: popUpAddHandler,
                }
              : null
          }
          guideId="clicdo.doi.co.jobs.tabs.authorisation.table"
        />
      </Grid>

      {snackBar}
      {open && (
        <ConfirmationDialog
          open={openDeleteConfirm?.open}
          title={t("listing:coJob.popup.confirmation")}
          text={t("listing:coJob.msg.deleteConfirm", {
            action: openDeleteConfirm?.action,
          })}
          onYesClick={() => handleDeleteHandler()}
          onConfirmDialogClose={() => setOpen(false)}
        />
      )}
      <C1PopUp
        title={t("opadmin:accnInq.uploadDoc")}
        openPopUp={openAddPopUp}
        setOpenPopUp={setOpenAddPopUp}
        actionsEl={actionElJobAtt}
      >
        <UploadPopup
          view={view}
          inputData={popUpDetails}
          viewType={"view"}
          handleInputChange={handleInputChange}
          handleInputFileChange={handleInputFileChange}
          locale={t}
          errors={popUpFieldError}
        />
      </C1PopUp>
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

export default RequestedDocuments;
