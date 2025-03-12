import {Divider, Grid, Tabs, Tooltip,} from "@material-ui/core";
import {DeleteOutline, EditOutlined, CheckCircleOutline, VisibilityOutlined} from "@material-ui/icons";
import PublicOutlinedIcon from "@material-ui/icons/PublicOutlined";
import SettingsBackupRestoreOutlinedIcon from "@material-ui/icons/SettingsBackupRestoreOutlined";
import EditLocationOutlinedIcon from "@material-ui/icons/EditLocationOutlined";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import React, {
  useContext,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from "react";
import { forwardRef } from "react";
import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1DataTable from "app/c1component/C1DataTable";
import C1DataTableActions from "app/c1component/C1DataTableActions";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TabInfoContainer from "app/c1component/C1TabInfoContainer";
import useHttp from "app/c1hooks/http";
import {
  CK_CT_LOCATION,
  CK_MST_CARGO_TYPES,
  CK_MST_VEH_TYPE,
  AccountTypes,
  Roles,
} from "app/c1utils/const";
import { JobStates, TripType } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/statusUtils";
import { tabScroll } from "app/c1utils/styles";
import {idrCurrency, isArrayNotEmpty} from "app/c1utils/utility";
import C1PopUp from "app/clictruckcomponent/JobPopUp";
import useAuth from "app/hooks/useAuth";
import TabLabel from "app/portedicomponent/TabLabel";
import { TabsWrapper } from "app/portedicomponent/TabsWrapper";
import { ConfirmationDialog } from "matx";
import JobTruckContext from "../JobTruckContext";
import Reimbursements from "./category/Reimbursements";
import JobTripLocationDomestic from "./JobTripLocationDomestic";
import JobTripCargoDomestic from "./JobTripCargoDomestic";

const JobTripChargesDomestic = forwardRef(({
   errors
 }, ref
) => {
  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();
  const [cargoData, setCargoData] = useState({});
  const [cargoTypes, setCargoTypes] = useState([]);
  const { user } = useAuth();
  const reimbursementRef = useRef();
  const [openAddPopUp, setOpenAddPopUp] = React.useState(false);
  const [totalTripAmt, setTotalTripAmt] = React.useState(0);
  const [formAction, setFormAction] = React.useState("");

  const {
    inputData,
    setInputData,
    viewType,
    tcrData,
    setTcrData,
    isDisabled,
    acceptedStatus = false,
    ableToSaveModifiesTrip = false,
  } = useContext(JobTruckContext);

  const [tabIndex, setTabIndex] = React.useState(0);
  const { t } = useTranslation(["job"]);
  const isToFinance = user?.authorities.some(
    (item) => item.authority === Roles?.FF_FINANCE?.code
  );
  const isDelivered =
    inputData?.tckJob?.tckMstJobState?.jbstId === JobStates?.DLV?.code;
  const isTruckingOperator =
    user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code && (user?.role?.includes(Roles?.OFFICER.code)
          || user?.role?.includes(Roles?.OP_OFFICER.code));
  let isEditableTruckType = true;
  if(inputData?.tckJob?.tckMstJobState?.jbstId === JobStates.ACP.code && user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code){
      isEditableTruckType = false;
  }

  ////////// /////// /////////////////////////////////////////////////////////////////
  //const [tcrData, setTcrData] = useState({ location: {}, cargos: [], reimbursements: [] });
  const [jobTripList, setJobTripList] = useState(
    inputData?.tckCtTripList || []
  );
  const [locationArr, setLocationArr] = useState([]);
  const [isDisalbeTrip, setIsDisalbeTrip] = useState(true);
  const [selectedTripId, setSelectedTripId] = useState();
  const [openSubmitConfirm, setOpenSubmitConfirm] = useState({
    action: null,
    open: false,
  });
  const [deleteId, setDeleteId] = useState("");
  const [isFieldHidden, setIsFieldHidden] = useState(false);
  const [
    autoCloseDialogAfterSelectMultiDropRate,
    setAutoCloseDialogAfterSelectMultiDropRate,
  ] = useState(false);

  useImperativeHandle(ref, () => ({
    getTripList: () => {
      //return _.merge(jobTripDetails, { tckCtTripCargoFmList: [cargoTripDetails] });
      return jobTripList;
    },
  }));
  const handleInsertTcrList = () => {
    if (tcrData?.tCkCtTripRate) {
      // Insert
      if (TripType.S.code === tcrData?.tCkCtTripRate?.trType) {
        handleInsertTcrListSub(tcrData);
      } else if (TripType.M.code === tcrData?.tCkCtTripRate?.trType) {
        if (tcrData.tcrDataChild && tcrData.tcrDataChild?.length > 0) {
          if (!tcrData.tcrDataChild[0].trId) {
            // insert
            let jobTripListChild = [];
            tcrData.tcrDataChild.forEach((tcrDataChildren, index) => {
              let trmpTrId = Math.ceil(Math.random() * 10000);
              tcrDataChildren.trId = trmpTrId;
              tcrDataChildren.trSeq = index;
              jobTripListChild.push(tcrDataChildren);
            });
            setJobTripList([...jobTripList, ...jobTripListChild]);
          }
        }
      } else if (TripType.C.code === tcrData?.tCkCtTripRate?.trType) {
        handleInsertTcrListSub(tcrData);
      }
    } else {
      // Update
      handleInsertTcrListSub(tcrData);
    }
  };

  const handleInsertTcrListSub = (tcrDataChildren) => {
    if (!tcrDataChildren.trId) {
      let trmpTrId = Math.ceil(Math.random() * 10000);
      tcrDataChildren.trId = trmpTrId;
      if (acceptedStatus){
        tcrDataChildren.trStatus = 'N';
      }
      setJobTripList([...jobTripList, ...[tcrDataChildren]]);
    } else {
      // update
      //let trip = jobTripList?.filter(e => e.trId === tcrData.trId)[0];
      let idx = jobTripList.findIndex(
        (item) => item.trId === tcrDataChildren.trId
      );
      jobTripList[idx] = tcrDataChildren;
      updateSubSequenceTripFromFieldForMultiDrop(idx, jobTripList);
    }
  };

  const updateSubSequenceTripFromFieldForMultiDrop = (idx, jobTripList) => {
    if (jobTripList.length > 1) {
      jobTripList.forEach((val, idn) => {
        if (idn !== 0) {
          Object.assign(jobTripList[idn]?.tckCtTripLocationByTrFrom, jobTripList[0]?.tckCtTripLocationByTrFrom);
        }
      });
    }
  };

  const handleInputChange = (e) => {
    const elName = e.target.name;
    if (elName === "shipmentType") {
      setInputData({
        ...inputData,
        tckJob: {
          ...inputData["tckJob"],
          tckMstShipmentType: { shtId: e.target.value },
        },
      });
    } else if (elName === "documentType") {
      setInputData({ ...inputData, documentType: e.target.value });
    } else if (elName === "tckCtDrv.drvName") {
      //sendRequest(`${T_CK_CT_DRV}&mDataProp_1=drvLicenseNo&sSearch_1=${e.target.value}`, "getDriver")
    } else if (elName === "tckCtVeh.vhPlateNo") {
      // setInputData({ ...inputData, ...deepUpdateState(inputData, elName, e.target.value) });
      //sendRequest(`${T_CK_CT_VEH}&mDataProp_1=vhPlateNo&sSearch_1=${e.target.value}`, "getTruck")
    } if (elName === "tckCtMstVehType.vhtyId") {
      if (inputData?.tckCtMstVehType?.vhtyId !== e.target.value) {
        // Reset tckCtVeh to null only when the value of vhtyId changes
        setInputData({ ...inputData, tckCtVeh: null, tckCtMstVehType: { ...inputData.tckCtMstVehType, vhtyId: e.target.value } });
      }
    } else {
      setInputData({
        ...inputData,
        ...deepUpdateState(inputData, elName, e.target.value),
      });
    }
  };

  const handleTabChange = (e, value) => {
    setTabIndex(value);
  };

  const handleSubmitButton = () => {
    setOpenAddPopUp(false);
    setFormAction("");
    handleInsertTcrList();
  };

  useEffect(() => {
    if (autoCloseDialogAfterSelectMultiDropRate && tcrData?.tCkCtTripRate) {
      handleSubmitButton();
    }
  }, [autoCloseDialogAfterSelectMultiDropRate, tcrData?.tCkCtTripRate]);

  const handleShowTcrPopup = (action) => {
    setFormAction(action)
    setOpenAddPopUp(true);
    setTcrData({});
    //sendRequest(`/api/v1/clickargo/clictruck/truck/cargoMm/-`, 'getCargoMm', 'GET')
  };

  useEffect(() => {
    if (
      isArrayNotEmpty(inputData?.hiddenFields) &&
      inputData?.hiddenFields.includes("tripcharges")
    ) {
      setIsFieldHidden(true);
    }
  }, []);

  //After render
  useEffect(() => {
    //call the api to load the locations
    //should filter based on the selected TO from Details tab
    sendRequest(
      `${CK_CT_LOCATION}&mDataProp_2=TCoreAccn.accnId&sSearch_2=${inputData?.tcoreAccnByJobPartyTo?.accnId}`,
      "fetchLocationsByTo",
      "get"
    );
  }, []);

  const handleEditViewTrip = (e, tripId, isDisable) => {
    isDisable ? setFormAction("VIEW") : setFormAction("EDIT")
    setOpenAddPopUp(true);
    let trip = jobTripList?.filter((e) => e.trId === tripId)[0];
    setTcrData(trip);
    setIsDisalbeTrip(isDisable);
    setSelectedTripId(tripId);
  };
  // only delete 1 trip
  // eslint-disable-next-line no-unused-vars
  const handleDeleteConfirmFor1Trip = (e, tripId) => {
    let idx = jobTripList.findIndex((item) => item.trId === tripId);
    let tmp = [...jobTripList];
    tmp.splice(idx, 1);
    setJobTripList(tmp);

    setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
    setDeleteId("");
  };
  const handleDeleteConfirm = (e, tripId) => {
    // clear all trip list
    setJobTripList(jobTripList?.filter(val => val?.trId !== tripId));
    setOpenSubmitConfirm({ ...openSubmitConfirm, action: "", open: false });
    setDeleteId("");
  };
  /*
        const handleViewTrip = (e, tripId) => {
            setOpenAddPopUp(true);
            let trip = jobTripList?.filter(e => e.trId === tripId)[0];
            setTcrData(trip);
            setIsDisalbeTrip(true)
        }
    */

  const isEditable = ["new", "edit"].includes(viewType) && [JobStates.NEW.code, JobStates.DRF.code, null].includes(inputData?.tckJob?.tckMstJobState?.jbstId);

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      // setLoading(isLoading);
      switch (urlId) {
        case "getCargoMm": {
          setCargoData(res.data);
          // setTcrData({...tcrData, cargos: res.data})

          break;
        }
        case "getCargoTypes": {
          setCargoTypes(res.data?.data);
          break;
        }
        case "fetchLocationsByTo": {
          setLocationArr([...res?.data?.aaData]);
          // one by one do request
          sendRequest(`${CK_MST_CARGO_TYPES}`, "getCargoTypes", "GET");
          break;
        }
        default:
          break;
      }
    }

    if (error) {
      //goes back to the screen
      // setLoading(false);
    }
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  const computeTotalCharge = () => {
    let tmpTotalTripAmt = 0;
    let tmpTotalReimbursementAmt = 0;

    for (const trip of jobTripList) {
      tmpTotalTripAmt =
        tmpTotalTripAmt + parseInt(trip?.tckCtTripCharge?.tcPrice || 0);
    }
    for (const trip of jobTripList) {
      tmpTotalReimbursementAmt =
        tmpTotalReimbursementAmt +
        parseInt(trip?.totalReimbursementCharge || 0);
    }
    //setTotalReimbursementAmt(tmpToalReimbursementAmt);
    inputData.jobTotalReimbursements = tmpTotalReimbursementAmt;
    setTotalTripAmt(tmpTotalTripAmt);
    return;
  };

  useEffect(() => {
    // when close popup window.
    if (!openAddPopUp) {
      // get updated total reimbursemnt from Reimbursements.jsx
      let idx = -1;
      if (reimbursementRef?.current?.getTotalReimbursementAmt) {
        let updatedReimbursementAmt =
          reimbursementRef?.current?.getTotalReimbursementAmt();
        idx = jobTripList.findIndex((item) => item.trId === selectedTripId);
        if (idx > -1) {
          jobTripList[idx].totalReimbursementCharge = updatedReimbursementAmt;
        }
      }

      computeTotalCharge();
      inputData.tckCtTripList = jobTripList;
      // refresh page.
      setInputData({
        ...inputData,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [openAddPopUp, jobTripList]);

  // ----tablist ---------------------------------------------------------------------------------------------
  const tabList = [
    {
      id: "location",
      text: t("job:tripDetails.domestic.location"),
      icon: <PublicOutlinedIcon />,
    },
    {
      id: "cargo",
      text: t("job:tripDetails.domestic.cargo"),
      icon: <LocalShippingOutlinedIcon />,
    },
    {
      id: "reimbursement",
      text: t("job:tripDetails.domestic.reimbursement"),
      icon: <SettingsBackupRestoreOutlinedIcon />,
    },
  ];
  // -----end tablist ----------------------------------------------------------------------------------------
  const columnsDomesticJob = [
    {
      name: "trId",
      label: "Id",
      options: {
        sort: false,
        filter: false,
        display: false,
      },
    },
    {
      name: "",
      label: "No.",
      options: {
        sort: false,
        filter: false,
        customBodyRender: (value, tableMeta) => {
          return tableMeta.rowIndex + 1;
        },
      },
    },
    {
      name: "tckCtTripLocationByTrFrom.tlocLocAddress",
      label: t("job:tripDetails.domestic.from"),
      options: {
        sort: false,
        filter: false,
      },
    },
    {
      name: "tckCtTripLocationByTrTo.tlocLocAddress",
      label: t("job:tripDetails.domestic.to"),
      options: {
        sort: false,
        filter: false,
        customBodyRender: (value, tableMeta) => {
          const tripStatus = tableMeta.rowData[7];
          return (
              <span style={{display: "flex"}}>
                {["D"].includes(tripStatus) && (
                    <Tooltip title={t("common:tooltip.droppedOff")}>
                      <CheckCircleOutline style={{color: "#38a0ad", fontSize: "17px"}}/>
                    </Tooltip>
                )}&nbsp;{value}
              </span>
          )
        },
      },
    },
    {
      name: "tckCtTripCharge.tcPrice",
      label: t("job:tripDetails.domestic.tripCharge"),
      options: {
        sort: false,
        filter: false,
        customBodyRender: (value) => {
          return idrCurrency(value || 0);
        },
        display:
          isArrayNotEmpty(inputData?.hiddenFields) &&
          inputData?.hiddenFields.includes("tripcharges")
            ? "excluded"
            : true,
      },
    },
    {
      name: "totalReimbursementCharge",
      label: t("job:tripDetails.domestic.reimbursementCharge"),
      options: {
        sort: false,
        filter: false,
        customBodyRender: (value) => {
          return idrCurrency(value || 0);
        },
        display:
          isArrayNotEmpty(inputData?.hiddenFields) &&
          inputData?.hiddenFields.includes("tripcharges")
            ? "excluded"
            : true,
      },
    },
    {
      name: "marksNo",
      label: t("job:tripDetails.domestic.action"),
      options: {
        filter: false,
        display: true,
        viewColumns: false,
        sort: false,
        setCellHeaderProps: () => {
          return { style: { textAlign: "center", width: "12.5%" } };
        },
        customBodyRender: (value, tableMeta, updateValue) => {
          const id = tableMeta.rowData[0];
          const tripStatus = tableMeta.rowData[7];
          return (
            <C1DataTableActions>
              <Grid container alignItems="flex-start" justifyContent="center">
                {isDisabled ? (
                    <>
                        <Grid item xs={4}>
                            <C1LabeledIconButton
                              tooltip={t("buttons:view")}
                              label={t("buttons:view")}
                              action={(e) => handleEditViewTrip(e, id, true)}
                            >
                                <VisibilityOutlined />
                            </C1LabeledIconButton>
                        </Grid>
                        {(ableToSaveModifiesTrip && ['A','R','P'].includes(tripStatus)) && (
                            <>
                                <span style={{minWidth: "10px"}}/>
                                <Grid item xs={4}>
                                    <C1LabeledIconButton
                                        tooltip={t("buttons:edit")}
                                        label={t("buttons:edit")}
                                        action={(e) => handleEditViewTrip(e, id, false)}
                                    >
                                        <EditOutlined />
                                    </C1LabeledIconButton>
                                </Grid>
                            </>
                        )}
                      {ableToSaveModifiesTrip && ['N'].includes(tripStatus) && (
                          <>
                            <span style={{minWidth: "10px"}}/>
                            <Grid item xs={4}>
                              <C1LabeledIconButton
                                  tooltip={t("buttons:edit")}
                                  label={t("buttons:edit")}
                                  action={(e) => handleEditViewTrip(e, id, false)}
                              >
                                <EditOutlined />
                              </C1LabeledIconButton>
                            </Grid>
                          </>
                      )}
                    </>
                ) : (
                  <>
                    <Grid item xs={4}>
                      <C1LabeledIconButton
                        tooltip={t("buttons:edit")}
                        label={t("buttons:edit")}
                        action={(e) => handleEditViewTrip(e, id, false)}
                      >
                        <EditOutlined />
                      </C1LabeledIconButton>
                    </Grid>
                    <span style={{ minWidth: "10px" }}></span>
                    <Grid item xs={4}>
                      {isEditable && (
                        <C1LabeledIconButton
                          tooltip={t("buttons:delete")}
                          label={t("buttons:delete")}
                          action={(e) => handlePopupDeleteConfirm(e, id)}
                        >
                          <DeleteOutline />
                        </C1LabeledIconButton>
                      )}
                    </Grid>
                  </>
                )}
              </Grid>
            </C1DataTableActions>
          );
        },
      },
    },
    {
      name: "trStatus",
      options: {
        sort: false,
        filter: false,
        display: false,
      },
    },
  ];

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
        onYesClick={(e) => handleDeleteConfirm(e, deleteId)}
      />
    );
  }

  const handlePopupDeleteConfirm = (e, id) => {
    setDeleteId(id);
    setOpenSubmitConfirm({
      ...openSubmitConfirm,
      action: "DELETE",
      open: true,
      msg: t("common:msg.deleteConfirm"),
    });
  };

  const notEditable = ["VIEW"].includes(formAction);
  const isButtonNotDisabled = !isDisabled;
  const isTripModifiable = ableToSaveModifiesTrip && !notEditable;
  const shouldEnableSubmitButton = isButtonNotDisabled || isTripModifiable;

  return (
    <React.Fragment>
      {confirmDialog}
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={4} md={6} xs={12}>
            <C1CategoryBlock
              icon={<LocalShippingOutlinedIcon />}
              title={t("job:tripDetails.trucksDetails")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1SelectField
                    name="tckCtMstVehType.vhtyId"
                    label={t("job:tripDetails.truckType")}
                    value={inputData?.tckCtMstVehType?.vhtyId}
                    onChange={(e) => handleInputChange(e)}
                    required
                    isServer={true}
                    disabled={isDisabled && isEditableTruckType}
                    options={
                      inputData?.tcoreAccnByJobPartyTo?.accnId
                        ? {
                            url: `/api/v1/clickargo/clictruck/vehicle/veh-type/${inputData?.tcoreAccnByJobPartyTo?.accnId}`,
                            key: "vhtyId",
                            id: "vhtyId",
                            desc: "vhtyName",
                          }
                        : {
                            url: CK_MST_VEH_TYPE,
                            key: "vhtyId",
                            id: "vhtyId",
                            desc: "vhtyDesc",
                            isCache: true,
                          }
                    }
                    error={errors["tckCtMstVehType.vhtyId"]}
                    helperText={errors["tckCtMstVehType.vhtyId"] || ""}
                  />
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>
          <Grid item lg={8} md={12}>
            <C1CategoryBlock
              icon={<EditLocationOutlinedIcon />}
              title={t("job:tripDetails.tripCargoReimbursement")}
            >
              <Grid container alignItems="center" spacing={3}>
                <Grid item xs={12}>
                  <C1DataTable
                    dbName={{ list: jobTripList }}
                    // url={"/api/v1/clickargo/attachments/job"}
                    isServer={false}
                    columns={columnsDomesticJob}
                    defaultOrder="attDtCreate"
                    defaultOrderDirection="desc"
                    isRefresh={false}
                    isShowDownload={false}
                    isShowToolbar={acceptedStatus && viewType === "view" || viewType !== "view"}
                    isShowPrint={false}
                    isShowViewColumns={false}
                    isShowFilter={false}
                    showAdd={isEditable || (acceptedStatus && isTruckingOperator && viewType === "view") ?
                        {
                            type: "popUp",
                            popUpHandler: () => handleShowTcrPopup("ADD"),
                        }
                        : null
                    }
                  />
                  {isArrayNotEmpty(inputData?.hiddenFields) &&
                  inputData?.hiddenFields.includes("tripcharges") ? null : (
                    <Grid container alignItems="flex-end" spacing={3}>
                      <Grid item xs={6}></Grid>
                      <Grid item xs={6} alignItems="flex-end">
                        <C1InputField
                          label={t("job:tripDetails.domestic.totalTripCharge")}
                          value={idrCurrency(totalTripAmt)}
                          disabled
                        />
                        <C1InputField
                          label={t(
                            "job:tripDetails.domestic.totalReimbursement"
                          )}
                          value={idrCurrency(inputData?.jobTotalReimbursements)}
                          disabled
                        />
                        <C1InputField
                          label={t("job:tripDetails.domestic.totalJobCharge")}
                          disabled
                          value={idrCurrency(
                            totalTripAmt + inputData?.jobTotalReimbursements
                          )}
                        />
                      </Grid>
                    </Grid>
                  )}
                </Grid>
              </Grid>
            </C1CategoryBlock>
          </Grid>
        </C1TabContainer>
      </Grid>

      <C1PopUp
        title={t("job:tripDetails.tripCargoReimbursement")}
        openPopUp={openAddPopUp}
        setOpenPopUp={setOpenAddPopUp}
        setSubmitButton={shouldEnableSubmitButton ? handleSubmitButton : null}
        maxWidth="lg"
        maxHeight="500px"
        overflowY="auto"
        customStyles={{
          backgroundColor: "rgba(244,244,244,0.11)",
          borderRadius: "12px",
          boxShadow: "0 4px 10px rgba(0, 0, 0, 0.2)",
          bottom: "100px",
        }}
      >
        <Tabs
          onChange={handleTabChange}
          value={tabIndex}
          className="mt-4"
          indicatorColor="primary"
          textColor="primary"
          variant="scrollable"
          scrollButtons="auto"
        >
          {tabList &&
            tabList.map((item, i) => {
              if (item?.id === "reimbursement" && isFieldHidden) return null;
              return (
                <TabsWrapper
                  style={i === tabIndex ? { backgroundColor: "#e4effa" } : {}}
                  className="capitalize"
                  value={i}
                  disabled={item.disabled}
                  label={
                    <TabLabel
                      viewType={viewType}
                      invalidTabs={inputData.invalidTabs}
                      tab={item}
                    />
                  }
                  key={i}
                  icon={item.icon}
                  {...tabScroll(i)}
                />
              );
            })}
        </Tabs>
        <Divider className="mb-6" />
        {/* ------ tab component -------------------------------------------------------------------------------------------------------------- */}
        {tabIndex === 0 && (
          <C1TabInfoContainer title="empty" guideAlign="right" open={false}>
            <JobTripLocationDomestic
              formAction={formAction}
              notEditable={notEditable}
              locationArr={locationArr}
              isFieldHidden={isFieldHidden}
              setAutoCloseDialogAfterSelectMultiDropRate={
                setAutoCloseDialogAfterSelectMultiDropRate
              } // auto close Popup dialog
              jobTrips={jobTripList}
              ableToSaveModifiesTrip={ableToSaveModifiesTrip}
            />
          </C1TabInfoContainer>
        )}
        {tabIndex === 1 && (
          <C1TabInfoContainer title="empty" guideAlign="right" open={false}>
            <JobTripCargoDomestic
                cargoTypes={cargoTypes}
                acceptedStatus={acceptedStatus}
            />
          </C1TabInfoContainer>
        )}
        {!isFieldHidden && tabIndex === 2 && (
          <C1TabInfoContainer title="empty" guideAlign="right" open={false}>
            <Reimbursements
              tripId={tcrData.trId || 0}
              viewType={viewType}
              showAddButton={isToFinance && isDelivered}
              showEditButton={isToFinance && isDelivered}
              showDeleteButton={isToFinance && isDelivered}
              ref={reimbursementRef}
            />
          </C1TabInfoContainer>
        )}
      </C1PopUp>
    </React.Fragment>
  );
});

export default JobTripChargesDomestic;
