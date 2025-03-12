import {
  Backdrop,
  Button,
  Checkbox,
  CircularProgress,
  Grid,
  Popover,
  Typography,
} from "@material-ui/core";
import Snackbar from "@material-ui/core/Snackbar";
import { makeStyles } from "@material-ui/core/styles";
import { Add, EditOutlined, VisibilityOutlined } from "@material-ui/icons";
import BusinessIcon from "@material-ui/icons/Business";
import ChatBubbleIcon from "@material-ui/icons/ChatBubble";
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import ExploreOutlinedIcon from "@material-ui/icons/ExploreOutlined";
import ListIcon from "@material-ui/icons/List";
import SettingsBackupRestoreOutlinedIcon from "@material-ui/icons/SettingsBackupRestoreOutlined";
import ZoomInIcon from "@material-ui/icons/ZoomIn";
import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import DataTable from "app/atomics/organisms/DataTable";
import C1Alert from "app/c1component/C1Alert";
import C1Button from "app/c1component/C1Button";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1FileUpload from "app/c1component/C1FileUpload";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1PopUp from "app/c1component/C1PopUp";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import C1Warning from "app/c1component/C1Warning";
import useHttp from "app/c1hooks/http";
import {
  AccountTypes,
  Actions, CK_CT_LOCATION,
  CK_MST_SHIPMENT_TYPE,
  JobStates,
  Roles,
  ShipmentTypes,
} from "app/c1utils/const";
import { getStatusDesc } from "app/c1utils/statusUtils";
import {
  customFilterDateDisplay,
  formatDate,
  getValue,
  previewPDF,
  isArrayNotEmpty,
} from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";
import { MatxLoading } from "matx";
import ConfirmationDialog from "matx/components/ConfirmationDialog";
import JobTrackPopup from "../../popups/JobTrackPopup";
import LocationDashboardPopUp from "../../popups/LocationDashboardPopUp";
import GridOnIcon from '@material-ui/icons/GridOn';
import JobUpload from "../../upload/JobUpload";
import {fetchCkAccnData} from "app/views/applications/job/upload/FetchCkAccn"
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import moment from "moment";
import useInterval from "../../../../../c1hooks/useInterval";

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: "#fff",
  },
  customIconButton: {
    "&:hover": {
      color: "#3f51b5",
    },
  },
}));

/***Truck Job List for CO/FF */
const TruckJobList = ({
  roleId,
  filterStatus,
  onFilterChipClose,
  onFilterChange,
}) => {
  const { t } = useTranslation([
    "buttons",
    "listing",
    "ffclaims",
    "common",
    "status",
    "job",
  ]);
  const shipmentTypeDomestic = "DOMESTIC";
  const { isLoading, res, error, urlId, sendRequest } = useHttp();

  const [confirm, setConfirm] = useState({ id: null });
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const [isRefresh, setRefresh] = useState(false);

  const [success, setSuccess] = useState(false);
  const [openActionConfirm, setOpenActionConfirm] = useState({
    action: null,
    open: false,
  });

  // const [shipmentType, setShipmentType] = useState();
  const [showUploadTemplatePopUp, setShowUploadTemplatePopUp] = useState(false);

  const [showHistory, setShowHistory] = useState(false);
  const [filterBy, setFilterBy] = useState([
    { attribute: "history", value: "default" },
  ]);

  //for jobstate filter
  const [stateFilter, setStateFilter] = useState([]);

  const [openShipmentDialog, setOpenShipmentDialog] = useState(false);

  const [tripListData, setTripListData] = useState([]);
  const [showLocationPopUp, setShowLocationPopUp] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(new Date());
  const buttonRef = useRef(null);
  const [trackingPopup, setTrackingPopup] = useState({
    jobId: null,
    open: false,
    trips: [],
    tripIds: []
  });
  // const popupDefaultValue = { jobRemarks: "", };

  const { user } = useAuth();
  const isCargoOwner =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code;
  const isForwarder =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code;
  const isSuspended = user?.coreAccn?.accnStatus === "S";

  let rowData = [];
  let statusData = [];
  const [selectedRowIds, setSelectedRowIds] = useState([]);
  const [warningMessage, setWarningMessage] = useState({
    open: false,
    msg: "",
  });
  const [elementPick, setElementPick] = useState(null);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const openPopover = Boolean(anchorEl);
  const [showMultiSelectActionPopup, setShowMultiSelectActionPopup] =
    useState(false);

  const [multiOptions, setMultiOptions] = useState([]);
  const [multiSelectResponseData, setMultiSelectResponseData] = useState(null);

  const [openJobUploadPopUp, setOpenJobUploadPopUp ] = useState(false);
  const jobUploadRef = useRef(null);
  const [isEnableUpload, setIsEnableUpload] = useState(false);


  const popupColumns = [
    {
      name: "tckCtTripLocationByTrFrom",
      label: t("job:popup.from"),
      options: {
        customBodyRender: (value, tableMeta, updateValue) => value?.tlocLocAddress || value?.locName,
      }
    },
    {
      name: "tckCtTripLocationByTrTo",
      label: t("job:popup.to"),
      options:{
        customBodyRender: (value, tableMeta, updateValue) => value?.tlocLocAddress || value?.locName,
      }
    },
  ];

  const truckCols = [
    {
      name: "checkbox",
      label: "",
      options: {
        sort: false,
        filter: false,
        display: !showHistory,
        viewColumns: false,
        customHeadLabelRender: (columnMeta, handleToggleColumn, sortOrder) => {
          // console.log(statusData)
          // let uniqueStatusData = [...new Set(statusData)]
          return (
            <Checkbox
              // disabled={uniqueStatusData.length> 1 ? true:false}
              disableRipple={true}
              checked={
                selectedRowIds.length > 0 &&
                selectedRowIds.length === rowData.length
              }
              onChange={({ target: { checked } }) => {
                try {
                  if (checked === true) {
                    setSelectedRowIds(rowData);
                  } else {
                    setSelectedRowIds([]);
                  }
                } catch (e) {
                  console.log(e);
                }
              }}
            />
          );
        },
        customBodyRender: (emptyStr, tableMeta, updateValue) => {
          rowData = tableMeta.tableData.map((data) => data[1]);
          statusData = tableMeta.tableData.map((data) => data[9]);
          const id = tableMeta.rowData[1];
          // const jobStatus = tableMeta.rowData[9];
          return (
            <React.Fragment>
              <Checkbox
                disableRipple={true}
                checked={selectedRowIds.includes(id)}
                onChange={({ target: { checked } }) => {
                  try {
                    if (checked === true) {
                      setSelectedRowIds(
                        selectedRowIds
                          .filter((rowId) => rowId !== id)
                          .concat(id)
                      );
                    } else {
                      if (selectedRowIds.length === 1) {
                        setSelectedRowIds(
                          selectedRowIds.filter((rowId) => rowId !== id)
                        );
                      } else {
                        setSelectedRowIds(
                          selectedRowIds.filter((rowId) => rowId !== id)
                        );
                      }
                    }
                  } catch (e) {
                    console.log(e);
                  }
                }}
              />
            </React.Fragment>
          );
        },
      },
    },
    // 1 DO NOT REMOVE, USED FOR FILTER ETC tableMeta.rowData[0]
    {
      name: "jobId",
      label: t("listing:trucklist.jobid"),
      options: {
        //display: "excluded",
      },
    },
    {
      name: "tckJob.tckMstShipmentType.shtId",
      label: t("listing:trucklist.type"),
      options: {
        filterType: "dropdown",
        filterOptions: {
          names: Object.keys(ShipmentTypes),
          renderValue: (v) => {
            return ShipmentTypes[v].desc;
          },
        },
      },
    },
    {
      name: "tcoreAccnByJobPartyTo.accnName",
      label: t("listing:trucklist.truckoperator"),
    },
    {
      name: "tckJob.tcoreAccnByJobSlAccn.accnName",
      label: t("listing:trucklist.cargoOwner"),
      options: {
        filter: isForwarder,
        display: isForwarder,
      },
    },
    {
      name: "jobDtDelivery",
      label: t("listing:trucklist.dtdelivery"),
      options: {
        filter: showHistory,
        filterType: "custom",
        display: showHistory ? true : "excluded",
        customFilterListOptions: {
          render: (v) => v.map((l) => l),
          update: (filterList, filterPos, index) => {
            filterList[index].splice(filterPos, 1);
            return filterList;
          },
        },
        filterOptions: {
          display: customFilterDateDisplay,
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
      },
    },
    {
      name: "jobShipmentRef",
      label: t("listing:trucklist.shipref"),
    },
    {
      name: "pickUp",
      label: t("listing:trucklist.pickup"),
      options: {
        sort: false,
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => value?.tlocLocAddress || "",
      },
    },
    {
      name: "lastDrop.tckCtLocation.locName",
      label: t("listing:trucklist.lastdrop"),
      options: {
        sort: false,
        filter: true,
        customBodyRender: (value, tableMeta, updateValue) => {
          const jobId = tableMeta.rowData[1];
          const jobType = tableMeta.rowData[2];
          const jobList = tableMeta?.rowData[13]?.length > 1;

          return (
            <React.Fragment>
              {jobType === "DOMESTIC" && jobList ? (
                <C1LabeledIconButton
                  tooltip={t("buttons:viewLoc")}
                  label={t("buttons:view")}
                  action={() => handleLocationPopUpShow(jobId)}
                >
                  <ZoomInIcon />
                </C1LabeledIconButton>
              ) : (
                <span>{value}</span>
              )}
            </React.Fragment>
          );
        },
      },
    },
    {
      name: "jobDtCreate",
      label: t("listing:trucklist.dtCreate"),
      options: {
        filter: isCargoOwner,
        filterType: "custom",
        display: isCargoOwner ? true : "excluded",
        customFilterListOptions: {
          render: (v) => v.map((l) => l),
          update: (filterList, filterPos, index) => {
            filterList[index].splice(filterPos, 1);
            return filterList;
          },
        },
        filterOptions: {
          display: customFilterDateDisplay,
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          return formatDate(value, true);
        },
      },
    },
    {
      name: "tckJob.tckMstJobState.jbstId",
      label: t("listing:trucklist.status"),
      options: {
        filter: true,
        filterType: "dropdown",
        filterOptions: {
          names: isArrayNotEmpty(stateFilter)
            ? stateFilter
            : Object.keys(JobStates),
          renderValue: (v) => {
            return JobStates[v].desc;
          },
        },
        customFilterListOptions: {
          render: (v) => {
            return JobStates[v].desc;
          },
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const hasRemark  = tableMeta?.rowData?.[12];
          if(value === JobStates.NEW.code && hasRemark) {
            return getStatusDesc(JobStates.REJ.code);
          } else {
            return getStatusDesc(value);
          }
        },
      },
    },
    {
      name: "action",
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
          const jobId = tableMeta.rowData[1];
          const status = tableMeta.rowData[10];
          const trips = tableMeta?.rowData[13];

          return (
            <Grid
              container
              direction="row"
              justifyContent="center"
              alignItems="center"
              style={{ minWidth: "150px" }}
            >
              <Grid
                container
                direction="row"
                justifyContent="flex-end"
                spacing={2}
              >
                <Grid item xs={4}>
                  {(status === JobStates.DRF.code ||
                    status === JobStates.NEW.code) && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:edit")}
                      label={t("buttons:edit")}
                      action={() =>
                        history.push({
                          pathname: `/applications/services/job/truck/edit`,
                          state: { jobId },
                        })
                      }
                    >
                      <EditOutlined />
                    </C1LabeledIconButton>
                  )}
                </Grid>
                <Grid item xs={4}>
                  <C1LabeledIconButton
                    tooltip={t("buttons:view")}
                    label={t("buttons:view")}
                    action={() =>
                      history.push({
                        pathname: `/applications/services/job/truck/view`,
                        state: { jobId },
                      })
                    }
                  >
                    <VisibilityOutlined />
                  </C1LabeledIconButton>
                </Grid>
                <Grid item xs={4}>
                  {(status === JobStates.DRF.code ||
                    status === JobStates.NEW.code) && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:delete")}
                      label={t("buttons:delete")}
                      action={(e) => handleCancelConfirm(e, jobId)}
                    >
                      <DeleteOutlinedIcon />
                    </C1LabeledIconButton>
                  )}
                  {status === JobStates.SUB.code && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:withdraw")}
                      label={t("buttons:withdraw")}
                      action={(e) => handleWithdrawConfirm(e, jobId)}
                    >
                      <SettingsBackupRestoreOutlinedIcon />
                    </C1LabeledIconButton>
                  )}
                  {/* Shortcut  to Tracking tab */}
                  {[
                    JobStates.ONGOING.code,
                    JobStates.DLV.code,
                    JobStates.BILLED.code,
                    JobStates.VER.code,
                    JobStates.APP.code,
                  ].includes(status) && (
                    <C1LabeledIconButton
                      tooltip={t("buttons:track")}
                      label={t("buttons:track")}
                      action={(e) => handleTrackingPopup(e, jobId, trips)}
                    >
                      <ExploreOutlinedIcon />
                    </C1LabeledIconButton>
                  )}
                </Grid>
              </Grid>
            </Grid>
          );
        },
      },
    },
    {
      name: "hasRemarks",
      label: "",
      options: {
        display: false,
        viewColumns: false,
        filter: false
      }
    },
    {
      name: "tckCtTripList",
      label: "",
      options: {
        display: "excluded",
        filter: false
      },
    },
    // ADDED BELOW TO IMPROVE SPACING AFTER THE ICON BUTTONS
    // {
    //     name: '', label: '',
    //     options: {
    //         filter: false,
    //         viewColumns: false,
    //         // display: (showHistory && !loading) || (!showHistory && loading),
    //         display: true,
    //         sort: false,
    //     }
    // },
  ];

  // <====== multi select, pop over & warning message ======>

  const handleMultiSelectedJob = (type) => {
    setAnchorEl(null);
    setLoading(true);
    let reqBody = {
      action: type,
      accType: AccountTypes.ACC_TYPE_CO.code,
      role: Roles.OFFICER.code,
      id: selectedRowIds,
    };
    sendRequest(
      "/api/v1/clickargo/clictruck/jobs",
      "multiSelect",
      "post",
      reqBody
    );
  };

  const handleOpenPopover = (e) => {
    let query = selectedRowIds.join(";");
    setElementPick(e.currentTarget);
    if (selectedRowIds.length === 0) {
      setWarningMessage({
        open: true,
        msg: t("listing:payments.errorNoSelectTitle"),
      });
    } else {
      sendRequest(
        `api/v1/clickargo/clictruck/jobs/action?accnType=${AccountTypes.ACC_TYPE_CO.code}&role=${Roles.OFFICER.code}&jobId=${query}`,
        "getMultiOptions",
        "get"
      );
    }
  };

  const handleWarningAction = (e) => {
    setWarningMessage({ open: false, msg: "", hlMsg: "", subMsg: "" });
  };

  const handleClose = () => {
    setAnchorEl(null);
    setSelectedRowIds([]);
    setMultiOptions([]);
  };

  const handleMultiSelectActionPopup = () => {
    if (showMultiSelectActionPopup === true) {
      setAnchorEl(null);
      setRefresh(false);
      setSelectedRowIds([]);
      setShowMultiSelectActionPopup(false);
    } else {
      setShowMultiSelectActionPopup(true);
    }
  };
  const messagePopup = React.useMemo(() => {
    let data = multiSelectResponseData?.failed;
    const keyMappings = {
      "jobDtPlan": "Plan Date",
      "jobShipmentRef": "Shipment Ref",
      "tcoreAccnByJobPartyTo.accnId": "Party Details",
      "tckCtTripLocationByTrDepot.tckCtLocation.locId": "Depot",
      "tckCtTripLocationByTrFrom.tckCtLocation.locId": "From",
      "tckCtTripLocationByTrTo.tckCtLocation.locId": "To",
      "tckCtTripLocationByTrFrom.tlocDtLoc": "Schedule Details",
      "tckCtTripLocationByTrTo.tlocDtLoc": "Schedule Details",
      "tckCtTripLocationByTrTo.tlocMobileNo": "Mobile Number",
      "tckCtMstVehType.vhtyId": "Truck Type",
      "[0]tckCtTripLocationByTrFrom.tckCtLocation.locId": "From",
      "[0]tckCtTripLocationByTrTo.tckCtLocation.locId": "To",
      "[0]tckCtTripLocationByTrFrom.tlocDtLoc": "Schedule Details",
      "[0]tckCtTripLocationByTrTo.tlocDtLoc": "Schedule Details",
      "[0]tckCtTripLocationByTrTo.tlocMobileNo": "Mobile Number",
    };

    if (data?.length > 0) {
      return data.map((item) => {
        let reasonText = item.reason;
        try {
          const parsedReason = JSON.parse(item.reason);
          reasonText = Object.entries(parsedReason).map(([key, value]) => {
            if (key === "invalidTabs.jobDetails" || key === "invalidTabs.fmTrip" || key === "invalidTabs.mmTrip") {
              return null;  // Skip the key you don't want to render
            }
            const displayKey = keyMappings[key] || key;
            return (
                <div key={key}>
                  <strong>{displayKey}</strong>: <span style={{ color: "red" }}>{value}</span>
                </div>
            );
          }).filter(Boolean); // Filter out null/empty values
        } catch (e) {
          reasonText = item.reason; // Fall back to string if parsing fails
        }

        return (
            <div key={item.id}>
              <strong>Job ID</strong>: {item.id}
              <br />
              <strong>Job Details</strong>:
              <div>{reasonText}</div>
              <br />
            </div>
        );
      });
    } else {
      if (multiSelectResponseData) {
        return <div style={{ color: "green" }}>All processes are successful!</div>;
      } else {
        return null;
      }
    }
  }, [multiSelectResponseData]);
  // <====== multi select, pop over & warning message ======>

  const handleCancelConfirm = (e, id) => {
    e.preventDefault();
    setLoading(false);
    setRefresh(false);
    setConfirm({ ...confirm, id });
    setOpen(true);
    setOpenActionConfirm({
      ...openActionConfirm,
      action: "DELETE",
      open: true,
    });
  };

  // TODO: No status REJECTED, not used at this time
  // const handleRejectConfirm = (e, id) => {
  //     e.preventDefault();
  //     setLoading(false);
  //     setRefresh(false);
  //     setConfirm({ ...confirm, id });
  //     setOpen(true);
  //     setOpenActionConfirm({ ...openActionConfirm, action: "REJECT", open: true });
  // }

  // CT-129 - [CO Operation] [Trucking Jobs] Cannot withdraw job

  const handleWithdrawConfirm = (e, id) => {
    e.preventDefault();
    setLoading(false);
    setRefresh(false);
    setConfirm({ ...confirm, id });
    setOpen(true);
    setOpenActionConfirm({
      ...openActionConfirm,
      action: "WITHDRAW",
      open: true,
    });
  };

  const handleTrackingPopup = (e, id, trips) => {
    e.preventDefault();
    const tripIds = trips?.map(val => val?.trId);
    setTrackingPopup({ ...trackingPopup, open: true, jobId: id, trips: trips, tripIds: tripIds });
  };

  const handleActionHandler = (e) => {
    if (confirm && !confirm.id) return;

    setLoading(true);
    if (openActionConfirm && openActionConfirm.action === "DELETE") {
      setOpen(false);
      console.log("action delete");
      // const url = `api/v1/clickargo/clictruck/job/truck/${confirm.id}`;
      // const urlId = "deleteJob";
      // const method = "GET";
      // console.log("sendrequest", url ,urlId,method);
      // sendRequest(url,urlId,method);
      sendRequest(
        "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
        "deleteJob",
        "get",
        null
      );
    } else if (openActionConfirm && openActionConfirm.action === "CANCEL") {
      setOpen(false);
      sendRequest(
        "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
        "cancelJob",
        "get",
        null
      );
      // CT-129 - [CO Operation] [Trucking Jobs] Cannot withdraw job
    } else if (openActionConfirm && openActionConfirm.action === "WITHDRAW") {
      setOpen(false);
      sendRequest(
        "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
        "withdrawJob",
        "get",
        null
      );
    } else if (openActionConfirm && openActionConfirm.action === "REJECT") {
      setOpen(false);
    }
  };

  const toggleHistory = (filter) => {
    setLoading(true);
    setRefresh(false);
    setShowHistory(filter === "history" ? true : false);
    setTimeout(() => setRefresh(true), 500);
    setTimeout(() => setLoading(false), 500);
  };

  const [delay, setDelay] = useState(1000 * 60 * 5);

  const callback = () => {
    console.log(`[${new Date().toISOString()}] Refresh triggered: Fetching server data`);
    setLastUpdated(new Date());
    sendRequest("/api/v1/clickargo/clictruck/job/truck/list?sEcho=3&iDisplayStart=0&iDisplayLength=10&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=jobDtLupd&mDataProp_2=TcoreAccnByJobPartyTo.accnId&sSearch_2=CK0015&iColumns=3", '', 'get', null);
  };

  useInterval(callback, delay);

  // useEffect(() => {
  //
  //   const intervalId = setInterval(() => {
  //     setRefresh(prev => !prev);
  //     setLastUpdated(new Date());
  //   }, 300000);
  //
  //   return () => clearInterval(intervalId);
  //
  // }, []);

  useEffect(() => {
    if (showHistory) {
      setFilterBy([{ attribute: "history", value: "history" }]);
    } else {
      setFilterBy([{ attribute: "history", value: "default" }]);
    }

    //fetch the jobtruckfilter
    sendRequest(
      "/api/v1/clickargo/clictruck/accnconfig/truckstatefilter",
      "getStateFilter",
      "get"
    );
    // eslint-disable-next-line
  }, [showHistory]);

  useEffect(() => {
    setTimeout(() => setSnackBarOptions(defaultSnackbarValue), 100);
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "cancelJob":
          console.log("cancelJob");
          sendRequest(
            "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
            "cancelled",
            "put",
            { ...res.data, action: "CANCEL" }
          );
          break;
        case "cancelled":
          setRefresh(true);
          setFilterBy([{ attribute: "history", value: "default" }]);
          setSuccess(true);
          setSnackBarState({
            ...snackBarState,
            msg: t("listing:coJob.msg.cancelSuccess"),
            open: true,
          });
          setLoading(false);
          break;
        case "deleted":
          setRefresh(true);
          setFilterBy([{ attribute: "history", value: "default" }]);
          setSuccess(true);
          setSnackBarState({
            ...snackBarState,
            msg: t("listing:coJob.msg.deleteSuccess"),
            open: true,
          });
          setLoading(false);
          break;
        // CT-129 - [CO Operation] [Trucking Jobs] Cannot withdraw job
        case "withdrawJob":
          console.log("withdrawJob");
          sendRequest(
            "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
            "withdrawn",
            "put",
            { ...res.data, action: "WITHDRAW" }
          );
          break;
        case "withdrawn":
          setRefresh(true);
          setFilterBy([{ attribute: "history", value: "default" }]);
          setSuccess(true);
          setSnackBarState({
            ...snackBarState,
            msg: t("common:msg.withdrawSuccess"),
            open: true,
          });
          setLoading(false);
          break;
        case "getJob": {
          setLoading(false);
          break;
        }
        case "getTripList": {
          if (res?.data?.tckCtTripList != null) {
            let tripLIstSort = res?.data?.tckCtTripList;
            tripLIstSort.sort((a, b) => a.trSeq - b.trSeq);
            setTripListData(tripLIstSort);
          } else {
            setTripListData([]);
          }
          break;
        }
        // CT-51 - [CO Operations-Import Job] System goes in processing after Click of Delete button
        case "deleteJob":
          sendRequest(
            "/api/v1/clickargo/clictruck/job/truck/" + confirm.id,
            "deleted",
            "put",
            { ...res.data, action: "DELETE" }
          );
          break;
        case "deletedJobFinal": {
          setRefresh(true);
          setFilterBy([{ attribute: "history", value: "default" }]);
          setSuccess(true);
          setLoading(false);
          setSnackBarState({
            ...snackBarState,
            msg: t("listing:coJob.msg.cancelSuccess"),
            open: true,
          });
          break;
        }
        case "getMultiOptions": {
          let dataOpt = res?.data;
          let identifierTest = dataOpt?.actions[0];
          if (dataOpt?.actions?.length > 0) {
            setMultiOptions(dataOpt?.actions);
            setAnchorEl(elementPick);
            console.log(Actions[identifierTest]?.text);
          } else {
            setElementPick(null);
            setAnchorEl(null);
            setSelectedRowIds([]);
            setWarningMessage({
              open: true,
              msg: t("listing:payments.errorThereIsNoOption"),
            });
          }
          break;
        }
        case "multiSelect": {
          if (res?.data) {
            if (res?.data?.suspended === true) {
              setWarningMessage({
                open: true,
                msg: t("listing:payments.errorYourAccountIsSuspended"),
              });
              setLoading(false);
            } else {
              setMultiSelectResponseData(res?.data);
              setRefresh(true);
              setSuccess(true);
              setLoading(false);
              setTimeout(() => setShowMultiSelectActionPopup(true), 500);
            }
          }
          break;
        }
        case "getStateFilter": {
          setStateFilter([...res?.data]);
          break;
        }
        case "downloadExcelTempate": {
          //console.log("resData downloadExcelTempate size :", res?.data?.length);
          if (res?.data) {
            previewPDF(user?.coreAccn.accnId + "_Template.xlsx", res?.data);
          }
          break;
        }
        default:
          break;
      }
    }
    // eslint-disable-next-line
  }, [isLoading, res, error, urlId]);

  const [snackBarState, setSnackBarState] = useState({
    open: false,
    vertical: "top",
    horizontal: "center",
    msg: t("common:common.msg.deleted"),
    severity: "success",
  });

  const defaultSnackbarValue = {
    success: false,
    successMsg: "",
    error: false,
    errorMsg: "",
    redirectPath: "",
  };
  // eslint-disable-next-line
  const [snackBarOptions, setSnackBarOptions] = useState(defaultSnackbarValue);

  const handleCloseSnackBar = () => {
    setSnackBarState({ ...snackBarState, open: false });
  };

  // const popUpAddHandler = () => {
  //   setShipmentType("");
  //   setOpenShipmentDialog(true);
  // };

  // const handleInputChange = (e) => {
  //   setShipmentType(e?.target?.value);
  // };
  const handleLocationPopUpShow = (id) => {
    sendRequest(
      `/api/v1/clickargo/clictruck/job/truck/${id}`,
      "getTripList",
      "get"
    );
    setShowLocationPopUp(true);
  };

  const setOpenJobUploadPopUpWrap = (isOpenJobUploadPopup) => {

      setOpenJobUploadPopUp(isOpenJobUploadPopup);

      // set Refresh to false when popup dialog
      // set Refresh to true when close dialog
      console.log("isOpenJobUploadPopup " , isRefresh, isOpenJobUploadPopup);
      setRefresh(!isOpenJobUploadPopup); 

      console.log("jobUploadRef?.current?.uploadRst: " , jobUploadRef?.current?.uploadRst);
  }

  const downloadExcelTempate = () => {

    sendRequest(
      `/api/v1/clickargo/clictruck/jobUpload/downloadExcelTemplate`,
      "downloadExcelTempate"
    );
  }
/*
  useEffect(()=> {

    let ckAccn;
    const fetchData = async () => {
      ckAccn = await fetchCkAccnData();
      console.log("ckAccn", ckAccn);
      
      if ( ckAccn?.caccnExcelTemplate &&
        ckAccn?.caccnExcelTemplate?.length > 0) {
        setIsEnableUpload(true);
      }
    };
    fetchData();

  }, [])
*/
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

  // let elAddAction = (
  //   <Button
  //     style={{
  //       backgroundColor: "#13B1ED",
  //       color: "#fff",
  //       padding: "10px 20px 10px 20px",
  //       fontWeight: "bold",
  //     }}
  //     onClick={() => popUpAddHandler()}
  //   >
  //     {t("listing:coJob.button.newJob")}
  //   </Button>
  // );

  const lastUpdate = <h6 style={{position:"absolute",color: "rgba(0, 0, 0, 0.54)", paddingTop: "10px"}}>Last Updated: {moment(lastUpdated).format("DD/MM/YYYY HH:MM:SS")}</h6>;

  return (
    <React.Fragment>
      {loading && <MatxLoading />}
      {snackBar}

      {/* Confirmation Popup */}
      {confirm && confirm.id && (
        <ConfirmationDialog
          open={open}
          title={t("listing:coJob.popup.confirmation")}
          text={t("listing:coJob.msg.confirmation", {
            action: openActionConfirm?.action,
            id: confirm.id,
          })}
          onYesClick={() => handleActionHandler()}
          onConfirmDialogClose={() => setOpen(false)}
        />
      )}

      <C1PopUp
        title={"Upload Template"}
        openPopUp={showUploadTemplatePopUp}
        setOpenPopUp={setShowUploadTemplatePopUp}
        maxWidth={"sm"}
      >
        <React.Fragment>
          <Grid container spacing={3}>
            <Grid container item xs={12}>
              <C1FileUpload
                name={"UploadTemplate"}
                label={"Upload Template"}
                inputLabel={"Template Picked"}
              />
            </Grid>
          </Grid>
          <Grid container alignItems="center" item xs={12}>
            <C1Button
              text={t("listing:coJob.button.create")}
              color="primary"
              type="submit"
              onClick={() => setShowUploadTemplatePopUp(false)}
            />
          </Grid>
        </React.Fragment>
      </C1PopUp>

      {/* Popup for New Job button  */}
      {/*<C1PopUp*/}
      {/*  title={t("job:form.newJob")}*/}
      {/*  openPopUp={openShipmentDialog}*/}
      {/*  setOpenPopUp={setOpenShipmentDialog}*/}
      {/*  maxWidth={"sm"}*/}
      {/*>*/}
      {/*  <Grid container spacing={3} alignItems="center">*/}
      {/*    <Grid container item>*/}
      {/*      <C1SelectField*/}
      {/*        name="shipmentType"*/}
      {/*        label={t("listing:coJob.type")}*/}
      {/*        value={getValue(shipmentType)}*/}
      {/*        required*/}
      {/*        onChange={handleInputChange}*/}
      {/*        isServer={true}*/}
      {/*        options={{*/}
      {/*          url: CK_MST_SHIPMENT_TYPE,*/}
      {/*          key: "shtId",*/}
      {/*          id: "shtId",*/}
      {/*          desc: "shtDesc",*/}
      {/*          isCache: true,*/}
      {/*        }}*/}
      {/*        disabled={isSuspended ? true : false}*/}
      {/*        helperText={isSuspended ? t("job:msg.accnSuspnd") : null}*/}
      {/*      />*/}
      {/*    </Grid>*/}
      {/*    <Grid container item>*/}
      {/*      <C1Button*/}
      {/*        text={t("listing:coJob.button.create")}*/}
      {/*        color="primary"*/}
      {/*        type="submit"*/}
      {/*        disabled={shipmentType ? false : true}*/}
      {/*        onClick={() =>*/}
      {/*          history.push("/applications/services/job/truck/new/-", {*/}
      {/*            shipmentType: shipmentType,*/}
      {/*          })*/}
      {/*        }*/}
      {/*      />*/}
      {/*    </Grid>*/}
      {/*  </Grid>*/}
      {/*</C1PopUp>*/}

      <LocationDashboardPopUp
        openPopUp={showLocationPopUp}
        setOpenPopUp={setShowLocationPopUp}
        tripListData={tripListData}
        title={t("job:tripDetails.domestic.location")}
        columns={popupColumns}
      />
      <Popover
        open={openPopover}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <Grid
          container
          direction={"column"}
          // alignItems={"flex-start"}
          style={{ minWidth: "100px", padding: "10px" }}
        >
          {multiOptions.map((item, i) => {
            return (
              <Grid item key={i}>
                <Button
                  style={{ textTransform: "none" }}
                  onClick={() => handleMultiSelectedJob(item)}
                >
                  <Typography>{Actions[item].text}</Typography>
                </Button>
              </Grid>
            );
          })}
        </Grid>
      </Popover>

      <C1PopUp
        title={`Multi-records Request: ${
          multiSelectResponseData?.action ? multiSelectResponseData?.action : ""
        }`}
        openPopUp={showMultiSelectActionPopup}
        setOpenPopUp={handleMultiSelectActionPopup}
        maxWidth={"md"}
      >
        <C1TabContainer>
          <Grid item lg={6} md={6} xs={12}>
            <C1CategoryBlock icon={<BusinessIcon />} title={"Request Details"}>
              <C1InputField
                disabled
                value={multiSelectResponseData?.id?.length}
                label="No. Records"
              />
              <C1InputField
                disabled
                value={multiSelectResponseData?.success?.length}
                label="No. Success"
              />
              <C1InputField
                disabled
                value={multiSelectResponseData?.failed?.length}
                label="No. Failed"
              />
            </C1CategoryBlock>
          </Grid>
          <Grid item lg={6} md={6} xs={12}>
            <C1CategoryBlock icon={<ChatBubbleIcon />} title={"Exceptions"}>
              <div style={{ whiteSpace: 'pre-wrap', paddingTop: '10px' }}>
                {messagePopup}
              </div>
            </C1CategoryBlock>
          </Grid>

        </C1TabContainer>
      </C1PopUp>
 
      <C1PopUp
          maxWidth={"lg"}
          title={"Upload Excel File"}
          openPopUp={openJobUploadPopUp}
          setOpenPopUp={setOpenJobUploadPopUpWrap} >
          <JobUpload ref={jobUploadRef}/>
      </C1PopUp>

      <DataTable
        url="/api/v1/clickargo/clictruck/job/truck"
        columns={truckCols}
        title=""
        defaultOrder="jobDtCreate"
        defaultOrderDirection="desc"
        isServer={true}
        showSubTile={!showHistory}
        subTile={lastUpdate}
        isShowViewColumns={true}
        isShowDownload={true}
        isShowPrint={true}
        isShowFilter={true}
        isRefresh={isRefresh}
        isShowToolbar={true}
        isShowFilterChip
        filterBy={filterBy}
        guideId={""}
        showActiveHistoryButton={toggleHistory}
        customRowsPerPage={[10, 20]}
        showAddButton={[
          {
            label: t("job:form.newJob").toUpperCase(),
            action: () => history.push("/applications/services/job/truck/new/-",
                {shipmentType: shipmentTypeDomestic}
            ),
            icon: <Add />,
          },
        ]}
        showMultiSelectActionButton={[
          {
              show: true,
              label: t("listing:common.uploadJob").toUpperCase(),
              action: () => setOpenJobUploadPopUpWrap(true),
              icon: <GridOnIcon />,
          },
          {
              show: true,
              label: t("listing:common.downloadExcelTempate").toUpperCase(),
              action: () => downloadExcelTempate(true),
              icon: <DescriptionOutlinedIcon />,
          },
          {
            show: true,
            label: t("listing:common.action").toUpperCase(),
            action: handleOpenPopover,
            icon: <ListIcon />,
          },
        ]}
      />

      {trackingPopup?.open && (
        <C1PopUp
          title={`Track Details`}
          openPopUp={trackingPopup?.open}
          setOpenPopUp={() =>
            setTrackingPopup({ ...trackingPopup, open: false, jobId: null })
          }
          maxWidth={"lg"}
        >
          <JobTrackPopup
              jobId={trackingPopup?.jobId}
              trips={trackingPopup?.trips}
              tripIds={trackingPopup?.tripIds}
          />
        </C1PopUp>
      )}

      {/* Warning message when there is no selected job */}
      <C1Warning
        warningMessage={warningMessage}
        handleWarningAction={handleWarningAction}
      />
    </React.Fragment>
  );
};

export default withErrorHandler(TruckJobList);
