import { Grid, Checkbox, Box, Button } from "@material-ui/core";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import PersonIcon from "@material-ui/icons/PersonOutlineOutlined";
import React, { useContext, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import {AccountTypes, JobStates, T_CK_CT_DRV} from "app/c1utils/const";
import { getValue } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import JobTruckContext from "../JobTruckContext";

const JobDriverAssign = ({ errors }) => {
  const {
    inputData,
    setInputData,
    isLoading,
    error,
    setSnackBarOptions,
    snackBarOptions
  } = useContext(JobTruckContext);

  const { t } = useTranslation(["cargoowners", "job"]);
  const { sendRequest, res, urlId } = useHttp();
  const [driverListArr, setDriverListArr] = useState([]);
  const [truckDetailArr, setTruckDetailArr] = useState([]);
  const [truckTypeArr, setTruckTypeArr] = useState([inputData?.tckCtMstVehType,]);
  const [truckType, setTruckType] = useState(inputData?.tckCtMstVehType?.vhtyId);
  const [truckClass, setTruckClass] = useState("");
  const [loading, setLoading] = useState(true);

  // this state to send payload to BE
  const [addDriverState, setAddDriverState] = useState({
    drvName: null,
    drvPhone: null,
    drvId: "OTHER",
  });
  const [addTruckState, setAddTruckState] = useState({
    vhPlateNo: null,
    vhId: "OTHER",
  });

  const { user } = useAuth();
  const [enableUpdateDriverTruck, setEnableUpdateDriverTruck] = useState(false);
  const [updateDriverTruck, setUpdateDriverTruck] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const userType = user?.coreAccn?.TMstAccnType?.atypId;
  const accnIdTO = inputData?.tcoreAccnByJobPartyTo?.accnId;
  const jobState = inputData?.tckJob?.tckMstJobState?.jbstId;
  const isJobMobile = false; //inputData?.jobMobileEnabled === "Y";

  // initial state for additional driver and truck at driver and truck list
  let driverOther = {
    drvName: null,
    drvPhone: null,
    drvId: "OTHER",
  };
  let truckOther = {
    vhPlateNo: null,
    vhId: "OTHER",
  };

  useEffect(() => {
    if (jobState !== JobStates.ACP.code && inputData?.jobDrvOth !== null) {
      setDriverListArr([driverOther]);
      if (jobState !== JobStates.ACP.code && inputData?.jobVehOth !== null) {
        setTruckDetailArr([{ vhPlateNo: null, vhId: "OTHER" }]);
      }
      // Fix to display plate no if tckCtVeh is present
      if (jobState !== JobStates.ACP.code && inputData?.tckCtVeh !== null) {
        setTruckDetailArr([{ ...inputData?.tckCtVeh }]);
      }
      setLoading(false);
    } 
    sendRequest(
      `${T_CK_CT_DRV}&mDataProp_1=TcoreAccn.accnId&sSearch_1=${accnIdTO}&mDataProp_2=drvStatus&sSearch_2=A`,
      "getDriver",
      "get",
        null
    );

    // now truck class only set it default to 1
    // it will need more adjustment for truck class in the future
    setTruckClass("1");
  }, []);

  const getTruckUrl = () => {
    // iDisplayLength is 1000
    let T_CK_CT_VEH_URL = `/api/v1/clickargo/clictruck/administrator/vehicle/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=vhId`;

    if ("UNDEFINE" === truckType) {
      // get All vehicle if truck type is UNDEFINE
      return `${T_CK_CT_VEH_URL}&mDataProp_1=TcoreAccn.accnId&sSearch_1=${accnIdTO}&mDataProp_2=vhIsMaintenance&sSearch_2=N&mDataProp_3=department&sSearch_3=Y`;
    } else {
      return `${T_CK_CT_VEH_URL}&mDataProp_1=TcoreAccn.accnId&sSearch_1=${accnIdTO}&mDataProp_2=vhIsMaintenance&sSearch_2=N&mDataProp_3=department&sSearch_3=Y&mDataProp_4=TCkCtMstVehType.vhtyId&sSearch_4=${truckType}`;
    }
  };

  useEffect(() => {
    if (!isLoading && res && !error) {
      switch (urlId) {
        case "getDriver":
          {
            setLoading(false);

            const driverListAll = res?.data?.aaData?.filter(
              (e) => !["MAINTENANCE"].includes(e?.drvState)
            );

            setDriverListArr([...driverListAll]);

            sendRequest(getTruckUrl(), "getTruck", "get", null);
          }
          break;
        case "getTruck":
          {
            const truckListAccepted = res?.data?.aaData;
            if(userType === AccountTypes.ACC_TYPE_TO.code) {
              // TO
              setTruckDetailArr([...truckListAccepted]);
            } else {
              if(inputData?.tckCtVeh) {
                setTruckDetailArr([inputData?.tckCtVeh]);
              }
            }

            // Moved call to mobileTruckListUrl
            if (isJobMobile) {
              if (inputData?.tckCtDrv?.drvId) {
                const mobileTruckListUrl = `/api/v1/clickargo/clictruck/administrator/associated-vehicle/list?drvId=${inputData?.tckCtDrv?.drvId}&vehType=${truckType}`;
                sendRequest(mobileTruckListUrl, "getTruckMobile", "GET", null);
              }
            }
          }
          break;
        case "getTruckMobile":
          setTruckDetailArr(res?.data?.aaData);
          break;
        case "getCurrentTruck":
          setTruckDetailArr(res?.data?.aaData);
          break;
        case "updateDriverTruck":
          setSnackBarOptions({...snackBarOptions,
            success: true,
            successMsg: "Updated driver and truck successful."})
          break;
        default:
          break;
      }
      setLoading(false);
    }
  }, [urlId, isLoading, error, res]);

  const handleDriverChanges = (e) => {
    const { name, value } = e.target;
    if (isJobMobile) {
      const mobileTruckListUrl = `/api/v1/clickargo/clictruck/administrator/associated-vehicle/list?drvId=${value}&vehType=${truckType}`;
      sendRequest(mobileTruckListUrl, "getTruckMobile", "GET", null);
    }
    if (value === "OTHER") {
      setInputData({
        ...inputData,
        tckCtDrv: null,
        jobDrvOth: { ...addDriverState },
        // reset vehicle
        // tckCtVeh: null,
        // jobVehOth: null,
      });
    } else {
      const tckCtDrv = driverListArr.find((e) => e.drvId === value);
      setInputData({
        ...inputData,
        tckCtDrv: { ...tckCtDrv },
        jobDrvOth: null,
        // reset vehicle
        //tckCtVeh: null,
        //jobVehOth: null,
      });
    }
  };
  const handleAdditionalDriverChange = (e) => {
    const { name, value } = e.target;
    if (name === "jobDrvOth.drvName") {
      setInputData({
        ...inputData,
        jobDrvOth: {
          ...inputData.jobDrvOth,
          drvName: value ? value : null,
        },
      });
    } else if (name === "jobDrvOth.drvPhone") {
      setInputData({
        ...inputData,
        jobDrvOth: {
          ...inputData.jobDrvOth,
          drvPhone: value ? value : null,
        },
      });
    }
  };
    const handleAdditionalTruckChange = (e) => {
      const { name, value } = e.target;
      if (value === "OTHER") {
        setInputData({
          ...inputData,
          tckCtVeh: null,
          jobVehOth: {
            ...addTruckState,
          },
        });
      } else {
        const tckCtVeh = truckDetailArr.find((e) => e?.vhId === value);
        setInputData({
          ...inputData,
          jobVehOth: null,
          tckCtVeh: { ...tckCtVeh },
        });
      }
      if (name === "vhPlateNo") {
        setInputData((prevInputData) => ({
          ...prevInputData,
          jobVehOth: {
            ...prevInputData.jobVehOth,
            vhPlateNo: value ? value : null,
          },
        }));
      }
    };
  /////////////////////////
  useEffect( () => {
    // TO officer
    // Assigned and ON-going status.
    if(userType === AccountTypes.ACC_TYPE_TO.code
      && [JobStates.ASG.code, JobStates.ONGOING.code, JobStates.PAUSED.code].includes(jobState)) {
      setEnableUpdateDriverTruck(true);
    }
    let isDisabled = !(jobState === JobStates.ACP.code && userType === AccountTypes.ACC_TYPE_TO.code);
    setDisabled(isDisabled);

  }, []);
  

  const onClickUpdateDriverTruckCheckbox = (e) => {
    const checked = e.target.checked;
    updateDriverTruckVal(checked);
  };


  const updateDriverTruckVal = (checked) => {
    setUpdateDriverTruck(checked);
    setDisabled(!checked);
  };

  const handleClickUpdateDriverTruckBtn = (e) => {
    const mobileTruckListUrl = `/api/v1/clickargo/clictruck/job/truck/updateDriverTruck`;
    sendRequest(mobileTruckListUrl, "updateDriverTruck", "POST", inputData);
    updateDriverTruckVal(false);
  }

  return loading ? (
    <MatxLoading />
  ) : (
    <React.Fragment>
      <Grid item xs={12}>
        <C1TabContainer>
          <Grid item lg={6} md={6} xs={12}>
            <C1CategoryBlock
              icon={<PersonIcon />}
              title={t("job:driverTruck.driverDetails")}
            >
              <C1SelectAutoCompleteField
                required={true}
                name={
                  inputData?.jobDrvOth != null
                    ? "jobDrvOth.drvId"
                    : "tckCtDrv.drvId"
                }
                label={t("job:driverTruck.pickDriver")}
                value={
                  inputData?.jobDrvOth != null
                    ? inputData?.jobDrvOth?.drvId
                    : inputData?.tckCtDrv?.drvId
                }
                onChange={(e, name, value) =>
                  handleDriverChanges({ target: { name, value: value?.value } })
                }
                disabled={disabled}
                optionsMenuItemArr={driverListArr?.map((item, i) => {
                  return {
                    value: item?.drvId,
                    desc: item?.drvId === "OTHER" ? "OTHER" : item?.drvName,
                  };
                })}
                error={
                  errors["drvName"] !== undefined &&
                  inputData?.jobDrvOth == null
                }
                helperText={
                  inputData?.jobDrvOth == null ? errors["drvName"] : ""
                }
              />
              {inputData?.jobDrvOth?.drvId === "OTHER" ? (
                <React.Fragment>
                  <C1InputField
                    label={t("job:driverTruck.driverName")}
                    onChange={handleAdditionalDriverChange}
                    value={getValue(inputData?.jobDrvOth?.drvName)}
                    name="jobDrvOth.drvName"
                    required
                    disabled={disabled}
                    error={errors["drvName"] !== undefined}
                    helperText={errors["drvName"] || ""}
                  />
                  <C1InputField
                    label={t("job:driverTruck.driverPhone")}
                    onChange={handleAdditionalDriverChange}
                    value={getValue(inputData?.jobDrvOth?.drvPhone)}
                    name="jobDrvOth.drvPhone"
                    required
                    disabled={disabled}
                    error={errors["drvPhone"] !== undefined}
                    helperText={errors["drvPhone"] || ""}
                  />
                </React.Fragment>
              ) : (
                <C1InputField
                  label={t("job:driverTruck.driverPhone")}
                  value={getValue(inputData?.tckCtDrv?.drvPhone)}
                  name="tckCtDrv.drvPhone"
                  disabled
                  error={errors["drvPhone"] !== undefined}
                  helperText={errors["drvPhone"] || ""}
                />
              )}
              <Box m={3}>
                { enableUpdateDriverTruck && 
                    <FormControlLabel
                    control={
                      <Checkbox
                        checked={updateDriverTruck}
                        onChange={(e) => onClickUpdateDriverTruckCheckbox(e)}
                        style={{ padding: "2px" }}
                      />
                    }
                    label={"Update driver and truck"}
                  />
                }
                {
                  (enableUpdateDriverTruck && updateDriverTruck)
                    && <Button
                    size="small"
                    variant="contained"
                    color='primary'
                    onClick={(e) => handleClickUpdateDriverTruckBtn(e)}
                    >
                    Save
                    </Button>
                }
              </Box>
              <Box m={3}>
              </Box>
            </C1CategoryBlock>
          </Grid>
          <Grid item lg={6} md={6} xs={12}>
            <C1CategoryBlock
              icon={<LocalShippingOutlinedIcon />}
              title={t("job:driverTruck.truckDetails")}
            >
              <C1SelectAutoCompleteField
                required={true}
                value={
                  inputData?.jobVehOth != null
                    ? inputData?.jobVehOth?.vhId
                    : inputData?.tckCtVeh?.vhId != null
                    ? inputData?.tckCtVeh?.vhId
                    : null
                }
                name={
                  inputData?.jobVehOth != null
                    ? "jobVehOth.vhId"
                    : "tckCtVeh.vhId"
                }
                label={t("job:driverTruck.pickTruck")}
                onChange={(e, name, value) =>
                    handleAdditionalTruckChange({
                    name,
                    target: { name, value: value?.value },
                  })
                }
                disabled={disabled}
                error={errors["vehicle"] !== undefined}
                helperText={errors["vehicle"] || ""}
                optionsMenuItemArr={truckDetailArr.map((item, i) => {
                  return {
                    value: item.vhId,
                    desc: item.vhId === "OTHER" ? "OTHER" : item.vhPlateNo,
                  };
                })}
              />
              {inputData?.jobVehOth?.vhId === "OTHER" ? (
                <React.Fragment>
                  <C1InputField
                    label={t("job:driverTruck.plateNo")}
                    onChange={handleAdditionalTruckChange}
                    value={getValue(inputData?.jobVehOth?.vhPlateNo)}
                    name="jobVehOth.vhPlateNo"
                    error={errors["vhPlateNo"] !== undefined}
                    helperText={errors["vhPlateNo"] || ""}
                    required
                    disabled={disabled}
                  />
                </React.Fragment>
              ) : (
                <>
                  <C1InputField
                      name="tckCtMstVehType.vhtyName"
                      label={t("job:driverTruck.type")}
                      disabled
                      error={errors["vhType"] !== undefined}
                      helperText={errors["vhType"] || ""}
                      value={getValue(
                          inputData?.tckCtVeh?.tckCtMstVehType?.vhtyName
                      )}
                  />
                  {/*<C1InputField*/}
                  {/*  label={t("job:driverTruck.class")}*/}
                  {/*  name="vhClass"*/}
                  {/*  disabled*/}
                  {/*  error={errors["vhClass"] !== undefined}*/}
                  {/*  helperText={errors["vhClass"] || ""}*/}
                  {/*  value={getValue(inputData?.tckCtVeh?.vhClass)}*/}
                  {/*/>*/}
                </>
              )}
            </C1CategoryBlock>
          </Grid>
        </C1TabContainer>
      </Grid>
    </React.Fragment>
  );
};

export default JobDriverAssign;
