import {
  Backdrop,
  Checkbox,
  CircularProgress,
  Grid,
  IconButton,
  InputAdornment,
  makeStyles,
  Typography,
} from "@material-ui/core";
import ChatBubbleOutlineIcon from "@material-ui/icons/ChatBubbleOutline";
import DeleteIcon from "@material-ui/icons/Delete";
import DescriptionIcon from "@material-ui/icons/DescriptionOutlined";
import EventNoteOutlinedIcon from "@material-ui/icons/EventNoteOutlined";
import GetAppIcon from "@material-ui/icons/GetApp";
import PlaceOutlinedIcon from "@material-ui/icons/PlaceOutlined";
import PublishIcon from "@material-ui/icons/Publish";
import SpeedOutlinedIcon from "@material-ui/icons/SpeedOutlined";
import React, {
  forwardRef,
  useContext,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DateField from "app/c1component/C1DateField";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import useHttp from "app/c1hooks/http";
import { CCM_ACCOUNT_ALL_URL, JobStates, Roles } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/statusUtils";
import {
  formatCurrency,
  getValue,
  previewPDF,
  Uint8ArrayToString,
} from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import JobTruckContext from "../JobTruckContext";
import Reimbursements from "./category/Reimbursements";

/**
 *
 * @description Invoice component for first mile.
 */

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
}));

const JobInvoice = forwardRef(({ tripId, idx = 0, isDomestic }, ref) => {
  const { t } = useTranslation(["cargoowners", "job"]);

  const { inputData, errors, isLoading } = useContext(JobTruckContext);

  const { sendRequest, res, error, validation, urlId } = useHttp();
  const { user } = useAuth();
  const [invoiceData, setInvoiceData] = useState(
    inputData?.toInvoiceList == null ? null : inputData?.toInvoiceList[idx]
  );

  const isToFinance = user?.authorities.some(
    (item) => item.authority === Roles?.FF_FINANCE?.code
  );
  const isCOFFFinance =
    (user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_CO" ||
      user?.coreAccn?.TMstAccnType?.atypId === "ACC_TYPE_FF") &&
    user?.authorities.some(
      (item) => item.authority === Roles?.FF_FINANCE?.code
    );
  // data for disbursement component
  //const tripId = inputData?.tckCtTripList?.length > 0 && inputData?.tckCtTripList[0]?.trId ? inputData?.tckCtTripList[0].trId : 0;

  const deliveredJob = JobStates.DLV.code;
  // const [tripId, setTripId] = useState(trId);

  // eslint-disable-next-line
  const [isRefresh, setRefresh] = useState(false);
  const doNo = inputData?.tckCtTripList[0]?.tckCtTripDo?.doNo;
  const bdClasses = useStyles();
  const [dlOpen, setDlOpen] = useState(false);
  const [isInvoiceChange, setInvoiceChange] = useState(false);

  const jobState = inputData?.tckJob?.tckMstJobState?.jbstId;
  const enabledUlDl = [JobStates.DLV.code].includes(jobState);

  const [documentType, setDocumentType] = useState("unsigned");

  const [validationErrors, setValidationErrors] = useState({});

  useImperativeHandle(ref, () => ({
    getInvoiceData: () => {
      // return _.merge(jobTripDetails, { tckCtTripCargoFmList: [cargoTripDetails] });
      return invoiceData;
    },
  }));

  useEffect(() => {
    //Retrieving the corresponding invoice of the trip
    if (tripId && !invoiceData?.tripDoDetail) {
      sendRequest(
        `/api/v1/clickargo/clictruck/job/truck/details/invoice/${tripId}`,
        "getInvoice",
        "get"
      );
    }
    // eslint-disable-next-line
  }, [tripId]);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      if (urlId === "getInvoice") {
        setDlOpen(false);
        setRefresh(true);
        setInvoiceData({ ...res?.data });
      } else if (urlId === "previewDo") {
        setDlOpen(false);
        // CT-106 - [TO-Import]Unable to Download For Proof of delivery for Billed Jon record.
        previewPDF(res?.data?.doaName, res?.data?.doaData);
      } else if (urlId === "getInvoiceFile") {
        setInvoiceData({
          ...invoiceData,
          base64File: res?.data,
        });
        previewPDF(invoiceData?.invName, res?.data);
      } else if (urlId === "uploadFile") {
        setDlOpen(true);
        sendRequest(
          `/api/v1/clickargo/clictruck/job/truck/details/invoice/${tripId}`,
          "getDoData",
          "get"
        );
      } else if (urlId === "deleteAttachment") {
        setDlOpen(true);
        sendRequest(
          `/api/v1/clickargo/clictruck/job/truck/details/invoice/${tripId}`,
          "getDoData",
          "get"
        );
      } else if (urlId === "getDoData") {
        setDlOpen(false);
        setRefresh(true);
        setValidationErrors({});
        setInvoiceData({
          ...invoiceData,
          tripDoDetail: res?.data?.tripDoDetail,
        });
      } else if (urlId === "getTripForDownload") {
        let doaId =
          documentType === "unsigned"
            ? res?.data?.tckCtTripDo?.doUnsigned
            : res?.data?.tckCtTripDo?.doSigned;
        if (doaId)
          sendRequest(
            `/api/v1/clickargo/clictruck/tripdo/tripDoAttach/fileData?id=${doaId}&type=doAttach`,
            "previewDo"
          );
        else console.log("No Document found for download.");
      } else if (urlId === "getTripForDelete") {
        let doaId =
          documentType === "unsigned"
            ? res?.data?.tckCtTripDo?.doUnsigned
            : res?.data?.tckCtTripDo?.doSigned;
        if (doaId)
          sendRequest(
            `/api/v1/clickargo/clictruck/tripdo/tripDoAttach/deleteDoAttach?id=${doaId}&type=${documentType}`,
            "deleteAttachment",
            "DELETE",
            {}
          );
        else console.log("No Document found for delete.");
      }
    }
    if (error) {
      //goes back to the screen
      setDlOpen(false);
    }
    if (validation) {
      setValidationErrors({ ...validation });
      setDlOpen(false);
    }
    // eslint-disable-next-line
  }, [urlId, isLoading, error, res]);

  // const handleDownloadDisburse = (data) => {
  //     // previewPDF('disburse.pdf', data)
  //     sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDoAttach/fileData?id=${data}&type=doAttach`, "previewDo");
  // }

  const handleInvoiceDate = (e, date) => {
    setInvoiceData({
      ...invoiceData,
      ...deepUpdateState(invoiceData, e, date.getTime()),
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e?.target;
    setInvoiceData({ ...invoiceData, [name]: value });
  };

  const handleUploadFileChange = (e) => {
    let file = e.target.files[0];
    if (!file) {
      return;
    }

    let errors = validateFileType(file.type);
    if (Object.keys(errors).length === 0) {
      const fileReader = new FileReader();
      fileReader.readAsArrayBuffer(e.target.files[0]);
      fileReader.onload = (e) => {
        const uint8Array = new Uint8Array(e.target.result);
        if (uint8Array.byteLength === 0) {
          return;
        }
        let imgStr = Uint8ArrayToString(uint8Array);
        let base64Sign = btoa(imgStr);
        setInvoiceData({
          ...invoiceData,
          base64File: base64Sign,
          invName: file.name,
        });
        setValidationErrors({
          ...validation,
          "invalid-invoice-file-format": "",
        });
        setInvoiceChange(true);
      };
    } else {
      setInvoiceData({
        ...invoiceData,
        base64File: "",
        invName: "",
        invLoc: "",
      });
      setValidationErrors({
        ...validation,
        "invalid-invoice-file-format": errors.invoice_file_format,
      });
      setInvoiceChange(false);
    }
  };

  const uploadDeliveryOrder = (e, tripId, doNo, type) => {
    e.preventDefault();
    var file = e.target.files[0];
    if (!file) return;

    const fileReader = new FileReader();
    fileReader.readAsArrayBuffer(file);
    fileReader.onload = (e) => {
      const uint8Array = new Uint8Array(e.target.result);
      var imgStr = Uint8ArrayToString(uint8Array);
      var base64Sign = btoa(imgStr);
      const popUpDoDetails = {
        ...popUpDoDetails,
        doaName: file.name,
        doaData: base64Sign,
        tckCtTrip: { trId: tripId },
        ckCtTripDo: { doNo: doNo },
      };
      setRefresh(false);
      setDlOpen(true);
      sendRequest(
        `/api/v1/clickargo/clictruck/tripdo/doattach?type=${type}`,
        "uploadFile",
        "POST",
        popUpDoDetails
      );
    };
  };

  // const downloadDOHandler = (id) => {
  //     sendRequest(`/api/v1/clickargo/clictruck/tripdo/tripDoAttach/fileData?id=${id}&type=doAttach`, "previewDo");
  // };

  const downloadDocument = (type) => {
    setDlOpen(true);
    setDocumentType(type);
    sendRequest(
      `/api/v1/clickargo/clictruck/job/trip/${tripId}`,
      "getTripForDownload",
      "GET"
    );
  };

  const deleteDOHandler = (type) => {
    setRefresh(false);
    setDocumentType(type);
    sendRequest(
      `/api/v1/clickargo/clictruck/job/trip/${tripId}`,
      "getTripForDelete",
      "GET"
    );
  };

  const downloadFileHandler = () => {
    const invId = invoiceData?.invId;
    if (invId && !isInvoiceChange) {
      const dlApi = `/api/v1/clickargo/clictruck/attach/invoice/${invId}`;
      sendRequest(dlApi, "getInvoiceFile", "GET");
    } else {
      previewPDF(invoiceData?.invName, invoiceData?.base64File);
    }
  };

  const validateFileType = (uploadFileType) => {
    const errors = {};
    if (
      uploadFileType &&
      !/(image\/(png|jpeg|jpg)|application\/pdf)/.test(uploadFileType)
    ) {
      errors.invoice_file_format =
        "Invoice File format not allowed, must be *.jpg, *.jpeg, *.png or *.pdf";
    }
    return errors;
  };

  return (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          {!isDomestic && (
            <Grid item lg={12}>
              <C1CategoryBlock
                icon={<PlaceOutlinedIcon />}
                title={t("job:invoice.tripChargesDODetails")}
              />
            </Grid>
          )}
          <Grid item lg={4} xs={12}>
            <C1CategoryBlock
              icon={<SpeedOutlinedIcon />}
              title={t("job:invoice.tripCharges")}
            >
              <C1InputField
                label={t("job:invoice.amount")}
                value={
                  invoiceData?.tripCharges?.amount
                    ? invoiceData?.tripCharges?.amount?.toLocaleString("id-ID")
                    : 0
                }
                name="tripCharges.amount"
                disabled
                inputProps={{ style: { textAlign: "right" } }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment
                      position="start"
                      style={{ paddingRight: "8px" }}
                    >
                      Rp
                    </InputAdornment>
                  ),
                }}
              />
              <Grid container alignItems="center" direction="row">
                <Checkbox
                  disabled={true}
                  checked={invoiceData?.tripCharges?.openPrice === "Y"}
                />
                <Typography
                  style={{
                    color: "gray",
                  }}
                >
                  {t("job:invoice.openPrice")}
                </Typography>
              </Grid>
            </C1CategoryBlock>
            <Grid item style={{ height: "39px" }}></Grid>
            <C1CategoryBlock
              icon={<DescriptionIcon />}
              title={t("job:invoice.tripDODetails")}
            >
              <C1InputField
                label={t("job:invoice.DONumber")}
                value={invoiceData?.tripDoDetail?.doNumber}
                name="tripDoDetail.doNumber"
                disabled={true}
              />
              <Grid
                container
                item
                xs={12}
                alignItems="center"
                direction="row"
                style={{
                  paddingTop: 20,
                  // paddingBottom: 20,
                }}
              >
                <Grid item xs={6}>
                  <Typography variant="h6">
                    {t("job:invoice.DONumber")}
                  </Typography>
                </Grid>
                {!invoiceData?.tripDoDetail?.doDocument && (
                  <>
                    {isToFinance === true && enabledUlDl ? (
                      <Grid item xs={6}>
                        <C1FileUpload
                          value={null}
                          inputProps={{
                            placeholder: t("listing:attachments.nofilechosen"),
                          }}
                          fileChangeHandler={(e) =>
                            uploadDeliveryOrder(e, tripId, doNo, "unsigned")
                          }
                          label={<PublishIcon />}
                          disabled={![JobStates.DLV.code].includes(jobState)}
                          inputLabel={t("job:invoice.unsignedFile")}
                          errors={
                            validationErrors[`invalid-do-file-format`] !==
                            undefined
                          }
                          helperText={
                            validationErrors[`invalid-do-file-format`] || ""
                          }
                        />
                      </Grid>
                    ) : (
                      <>
                        {" "}
                        <Grid item xs={2}></Grid>
                        <Grid
                          item
                          xs={2}
                          style={{ paddingTop: 10, paddingBottom: 10 }}
                        >
                          <IconButton disabled>
                            <GetAppIcon fontSize="large" />
                          </IconButton>
                        </Grid>
                        <Grid
                          item
                          xs={2}
                          style={{ paddingTop: 10, paddingBottom: 10 }}
                        >
                          <IconButton disabled>
                            <DeleteIcon fontSize="large" />
                          </IconButton>
                        </Grid>
                      </>
                    )}
                  </>
                )}
                <Grid item xs={2}></Grid>
                {invoiceData?.tripDoDetail?.doDocument && (
                  <>
                    <Grid
                      item
                      xs={2}
                      style={{ paddingTop: 10, paddingBottom: 10 }}
                    >
                      <IconButton onClick={() => downloadDocument("unsigned")}>
                        <GetAppIcon fontSize="large" />
                      </IconButton>
                    </Grid>
                    <Grid
                      item
                      xs={2}
                      style={{ paddingTop: 10, paddingBottom: 10 }}
                    >
                      <IconButton
                        disabled={
                          isToFinance === true && enabledUlDl ? false : true
                        }
                        onClick={() => deleteDOHandler("unsigned")}
                      >
                        <DeleteIcon fontSize="large" />
                      </IconButton>
                    </Grid>
                  </>
                )}
              </Grid>
              <Grid container item xs={12} alignItems="center" direction="row">
                <Grid item xs={6}>
                  <Typography variant="h6">{t("job:invoice.POD")}</Typography>
                </Grid>
                {!invoiceData?.tripDoDetail?.pod && (
                  <>
                    {isToFinance === true && enabledUlDl ? (
                      <Grid item xs={6}>
                        <C1FileUpload
                          inputProps={{
                            placeholder: t("listing:attachments.nofilechosen"),
                          }}
                          required={true}
                          fileChangeHandler={(e) =>
                            uploadDeliveryOrder(e, tripId, doNo, "signed")
                          }
                          disabled={![JobStates.DLV.code].includes(jobState)}
                          value={getValue(invoiceData?.tripDoDetail?.pod)}
                          name={"pod"}
                          label={<PublishIcon />}
                          inputLabel={t("job:invoice.signedFile")}
                          errors={
                            errors[`pod`] ||
                            validationErrors[`invalid-pod-file-format`] !==
                              undefined
                          }
                          helperText={
                            validationErrors[`invalid-pod-file-format`]
                              ? validationErrors[`invalid-pod-file-format`]
                              : errors[`pod`]
                              ? errors[`pod`]
                              : ""
                          }
                        />
                      </Grid>
                    ) : (
                      <>
                        {" "}
                        <Grid item xs={2}></Grid>
                        <Grid
                          item
                          xs={2}
                          style={{ paddingTop: 10, paddingBottom: 10 }}
                        >
                          <IconButton disabled>
                            <GetAppIcon fontSize="large" />
                          </IconButton>
                        </Grid>
                        <Grid
                          item
                          xs={2}
                          style={{ paddingTop: 10, paddingBottom: 10 }}
                        >
                          <IconButton disabled>
                            <DeleteIcon fontSize="large" />
                          </IconButton>
                        </Grid>
                      </>
                    )}
                  </>
                )}
                <Grid item xs={2}></Grid>
                {invoiceData?.tripDoDetail?.pod && (
                  <>
                    <Grid
                      item
                      xs={2}
                      style={{ paddingTop: 10, paddingBottom: 10 }}
                    >
                      <IconButton onClick={() => downloadDocument("signed")}>
                        <GetAppIcon fontSize="large" />
                      </IconButton>
                    </Grid>
                    <Grid
                      item
                      xs={2}
                      style={{ paddingTop: 10, paddingBottom: 10 }}
                    >
                      <IconButton
                        disabled={
                          isToFinance === true && enabledUlDl ? false : true
                        }
                        onClick={() => deleteDOHandler("signed")}
                      >
                        <DeleteIcon fontSize="large" />
                      </IconButton>
                    </Grid>
                  </>
                )}
              </Grid>
            </C1CategoryBlock>
          </Grid>
          <Grid item lg={8} xs={12}>
            <C1CategoryBlock
              icon={<EventNoteOutlinedIcon />}
              title={t("job:invoice.invoiceDetails")}
            >
              <Grid style={{ paddingTop: 20 }} container spacing={2}>
                <Grid item lg={6} xs={12}>
                  <C1CategoryBlock
                    icon={<DescriptionIcon />}
                    title={t("job:invoice.generalDetails")}
                  >
                    <C1InputField
                      label={t("job:invoice.invNo")}
                      name="invNo"
                      value={getValue(invoiceData?.invNo)}
                      onChange={handleInputChange}
                      disabled={
                        isToFinance === true &&
                        inputData?.tckJob?.tckMstJobState?.jbstId ===
                          deliveredJob
                          ? false
                          : true
                      }
                      required={true}
                      error={errors[`invNo`] !== undefined}
                      helperText={errors[`invNo`] || ""}
                    />
                    <C1DateField
                      label={t("job:invoice.invDate")}
                      name="invDtIssue"
                      required={true}
                      disablePast
                      value={getValue(invoiceData?.invDtIssue)}
                      onChange={handleInvoiceDate}
                      disabled={
                        isToFinance === true &&
                        inputData?.tckJob?.tckMstJobState?.jbstId ===
                          deliveredJob
                          ? false
                          : true
                      }
                      error={errors[`invDtIssue`] !== undefined}
                      helperText={errors[`invDtIssue`] || ""}
                    />
                    <C1InputField
                      label={t("job:invoice.invFrom")}
                      value={invoiceData?.tcoreAccnByInvFrom?.accnName}
                      name="tcoreAccnByInvFrom.accnName"
                      disabled={true}
                      required={true}
                    />
                    <C1SelectField
                      isServer={true}
                      required
                      disabled={true}
                      name="tcoreAccnByInvTo.accnName"
                      label={t("job:invoice.invTo")}
                      value={getValue(invoiceData?.tcoreAccnByInvTo?.accnId)}
                      options={{
                        url: CCM_ACCOUNT_ALL_URL,
                        key: "account",
                        id: "accnId",
                        desc: "accnName",
                        isCache: false,
                      }}
                    />
                    <Grid
                      container
                      direction="row"
                      justifyContent="center"
                      alignItems="center"
                      spacing="1"
                    >
                      <Grid item xs={invoiceData?.invName ? 10 : 12}>
                        <C1FileUpload
                          inputProps={{
                            placeholder: t("listing:attachments.nofilechosen"),
                          }}
                          required={true}
                          fileChangeHandler={handleUploadFileChange}
                          disabled={
                            isToFinance === true &&
                            inputData?.tckJob?.tckMstJobState?.jbstId ===
                              deliveredJob
                              ? false
                              : true
                          }
                          value={getValue(invoiceData?.invName)}
                          name={"invName"}
                          label={t("job:invoice.browse")}
                          inputLabel={t("job:invoice.invFile")}
                          errors={
                            errors[`invName`] !== undefined ||
                            validationErrors[`invalid-invoice-file-format`]
                          }
                          helperText={
                            validationErrors[`invalid-invoice-file-format`]
                              ? validationErrors[`invalid-invoice-file-format`]
                              : errors[`invName`]
                              ? errors[`invName`]
                              : ""
                          }
                        />
                      </Grid>
                      {invoiceData?.invName && (
                        <Grid item xs={2}>
                          <IconButton
                            onClick={downloadFileHandler}
                            style={{ marginTop: 10 }}
                          >
                            <GetAppIcon fontSize="large" />
                          </IconButton>
                        </Grid>
                      )}
                    </Grid>
                  </C1CategoryBlock>
                </Grid>
                <Grid item lg={6} xs={12}>
                  <C1CategoryBlock
                    icon={<ChatBubbleOutlineIcon />}
                    title={t("job:invoice.CnR")}
                  >
                    <C1TextArea
                      label={t("job:invoice.invComments")}
                      name={"invInvocierComment"}
                      value={getValue(invoiceData?.invInvocierComment)}
                      onChange={handleInputChange}
                      multiline
                      textLimit={1024}
                      disabled={
                        isToFinance === true &&
                        inputData?.tckJob?.tckMstJobState?.jbstId ===
                          deliveredJob
                          ? false
                          : true
                      }
                    />
                    <C1TextArea
                      label={t("job:invoice.invRemarks")}
                      name={"invInvocieeRemarks"}
                      value={getValue(invoiceData?.invInvocieeRemarks)}
                      disabled={isCOFFFinance ? false : true}
                      onChange={handleInputChange}
                      multiline
                      textLimit={1024}
                    />
                  </C1CategoryBlock>
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>
          <Grid direction="row" container>
            <Grid item lg={8} />

            <Grid
              item
              container
              direction="row"
              justifyContent="space-between"
              lg={4}
              xs={12}
            >
              <Typography variant="h6">
                {t("job:invoice.tripCharges")}
              </Typography>
              <Typography variant="h6">
                {formatCurrency(
                  invoiceData?.tripCharges?.amount
                    ? invoiceData?.tripCharges?.amount
                    : 0,
                  "IDR"
                )}
              </Typography>
            </Grid>
          </Grid>
          <Grid item lg={12}>
            {inputData?.jobId && (
              <Reimbursements jobId={inputData?.jobId} tripId={tripId} />
            )}
          </Grid>
        </C1TabContainer>
      </Grid>

      <Backdrop open={dlOpen} className={bdClasses.backdrop}>
        {" "}
        <CircularProgress color="inherit" />
      </Backdrop>
    </React.Fragment>
  );
});

export default JobInvoice;
