import C1PopUp from "app/c1component/C1PopUp";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";

import { Grid, MenuItem } from "@material-ui/core";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import PersonIcon from "@material-ui/icons/PersonOutlineOutlined";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1SelectAutoCompleteField from "app/c1component/C1SelectAutoCompleteField";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import useHttp from "app/c1hooks/http";
import {T_CK_CT_DRV} from "app/c1utils/const";
import { getValue } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";

import NearMeOutlinedIcon from "@material-ui/icons/NearMeOutlined";
import C1IconButton from "app/c1component/C1IconButton";

/**
 *
 * this popup for popup multidrop location,
 * preparing this component if all the location remove from dashboard
 */
const AssignDriverPopup = ({
  openPopUp,
  setOpenPopUp,
  title,
  handleAssignDriver,
  inputData,
  setInputData,
  errors:initErrors = {},
}) => {
  const { t } = useTranslation(["cargoowners", "job", "buttons"]);
  const { sendRequest, res, urlId, isLoading, error } = useHttp();

  const [driverListArr, setDriverListArr] = useState([]);
  const [truckDetailArr, setTruckDetailArr] = useState([]);
  const [truckTypeArr, setTruckTypeArr] = useState([]);

  const [truckClass, setTruckClass] = useState("");

//   const [loading, setLoading] = useState(true);

  // this state to send payload to BE
  // eslint-disable-next-line
  const [addDriverState, setAddDriverState] = useState({
    drvName: null,
    drvPhone: null,
    drvId: "OTHER",
  });
  // eslint-disable-next-line
  const [addTruckState, setAddTruckState] = useState({
    vhPlateNo: null,
    vhId: "OTHER",
  });

  const [errors, setErrors] = useState(initErrors);
  

  // initial state for additional driver and truck at driver and truck list
//   let driverOther = {
//     drvName: null,
//     drvPhone: null,
//     drvId: "OTHER",
//   };
//   let truckOther = {
//     vhPlateNo: null,
//     vhId: "OTHER",
//   };

  const { user } = useAuth();
  
  const accnIdTO = user?.coreAccn?.accnId;

  useEffect(() => {
    sendRequest(
      `${T_CK_CT_DRV}&mDataProp_1=TcoreAccn.accnId&sSearch_1=${accnIdTO}&mDataProp_2=drvStatus&sSearch_2=A`,
      "getDriver",
      "get"
    );

    // now truck class only set it default to 1
    // it will need more adjustment for truck class in the future
    setTruckClass("1");
// eslint-disable-next-line
  }, []);

  const getTruckUrl = () => {
    // iDisplayLength is 1000
    let T_CK_CT_VEH_URL = `/api/v1/clickargo/clictruck/administrator/vehicle/list?sEcho=3&iDisplayStart=0&iDisplayLength=1000&iSortCol_0=0&sSortDir_0=desc&iSortingCols=1&mDataProp_0=vhId`;

    // get All vehicle 
    return `${T_CK_CT_VEH_URL}&mDataProp_1=TcoreAccn.accnId&sSearch_1=${accnIdTO}&mDataProp_2=vhIsMaintenance&sSearch_2=N`;
    
  };

  useEffect(() => {
    if (!isLoading && res && !error) {
      if (urlId === "getDriver") {
        // setLoading(false);

        const driverListAccepted = res?.data?.aaData?.filter(
          //(e) => !["ASSIGNED", "MAINTENANCE"].includes(e?.drvState)
          (e) => ![ "MAINTENANCE"].includes(e?.drvState)
        );
        // setDriverListArr([...driverListAccepted, driverOther]);
        setDriverListArr([...driverListAccepted]);

        sendRequest(getTruckUrl(), "getTruck", "get");
      } else if (urlId === "getTruck") {
        const truckListAccepted = res?.data?.aaData?.filter(
          //(e) => e?.tckCtMstVehState?.vhstId === "UNASSIGNED"
          (e) => true
        );
        // setTruckDetailArr([...truckListAccepted, truckOther]);
        setTruckDetailArr([...truckListAccepted]);
      }
    }
// eslint-disable-next-line
  }, [urlId, isLoading, error, res]);

  const handleTruckChanges = (e) => {
    const { value } = e.target;
    if (value === "OTHER") {
      setInputData({
        ...inputData,
        ckCtVeh: null,
        jobVehOth: {
          ...addTruckState,
        },
      });
    } else {
      const ckCtVeh = truckDetailArr.find((e) => e?.vhId === value);

      setTruckTypeArr([
        {
          vhtyId: ckCtVeh?.tckCtMstVehType?.vhtyId,
          vhtyName: ckCtVeh?.tckCtMstVehType?.vhtyName,
        },
      ]);

      setInputData({
        ...inputData,
        // jobVehOth: null,
        ckCtVeh: { ...ckCtVeh },
      });
    }
  };

  const handleDriverChanges = (e) => {
    const { value } = e.target;
    if (value === "OTHER") {
      setInputData({
        ...inputData,
        ckCtDrv: null,
        jobDrvOth: { ...addDriverState },
      });
    } else {
      const ckCtDrv = driverListArr.find((e) => e.drvId === value);
      setInputData({
        ...inputData,
        ckCtDrv: { ...ckCtDrv },
        // jobDrvOth: null,
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
    const { value } = e.target;
    setInputData({
      ...inputData,
      jobVehOth: {
        ...inputData.jobVehOth,
        vhPlateNo: value ? value : null,
      },
    });
  };

  const handleAssignDriverWrap = (e) => {
    if( validateDrvVeh() ) {
      handleAssignDriver(e);
    }
  }

  const validateDrvVeh = () => {
    let rst = true;
    errors["drvName"] = undefined;
    errors["vehicle"] = undefined;

    if(!inputData?.ckCtDrv?.drvId) {
      errors["drvName"] = "Driver Name cannot be empty."
      rst = false;
    }
    if(!inputData?.ckCtVeh?.vhId) {
      errors["vehicle"] = "Vehicle Name cannot be empty."
      rst = false;
    }
    if(!rst) {
      console.log(errors);
      setErrors({...errors})
    }
    console.log(rst, errors);
    return rst;
  }

  return (
    <C1PopUp
      title={`${title}`}
      openPopUp={openPopUp}
      setOpenPopUp={setOpenPopUp}
      actionsEl={
        <C1IconButton
          disabled={false}
          tooltip={t("buttons:submit")}
          childPosition="right"
        >
          <NearMeOutlinedIcon
            color="primary"
            fontSize="large"
            onClick={(e) => {
              handleAssignDriverWrap(e);
            }}
          ></NearMeOutlinedIcon>
        </C1IconButton>
      }
    >
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
                    : "ckCtDrv.drvId"
                }
                label={t("job:driverTruck.pickDriver")}
                value={
                  inputData?.jobDrvOth != null
                    ? inputData?.jobDrvOth?.drvId
                    : inputData?.ckCtDrv?.drvId
                }
                onChange={(e, name, value) =>
                  handleDriverChanges({ target: { name, value: value?.value } })
                }
                disabled={false}
                optionsMenuItemArr={driverListArr.map((item, i) => {
                  return {
                    value: item.drvId,
                    desc: item.drvId === "OTHER" ? "OTHER" : item.drvName,
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
                    disabled={false}
                    error={errors["drvName"] !== undefined}
                    helperText={errors["drvName"] || ""}
                  />
                  <C1InputField
                    label={t("job:driverTruck.driverPhone")}
                    onChange={handleAdditionalDriverChange}
                    value={getValue(inputData?.jobDrvOth?.drvPhone)}
                    name="jobDrvOth.drvPhone"
                    required
                    disabled={false}
                    error={errors["drvPhone"] !== undefined}
                    helperText={errors["drvPhone"] || ""}
                  />
                </React.Fragment>
              ) : (
                <C1InputField
                  label={t("job:driverTruck.driverPhone")}
                  value={getValue(inputData?.ckCtDrv?.drvPhone)}
                  name="ckCtDrv.drvPhone"
                  disabled
                  error={errors["drvPhone"] !== undefined}
                  helperText={errors["drvPhone"] || ""}
                />
              )}
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
                    : inputData?.ckCtVeh?.vhId
                }
                name={
                  inputData?.jobVehOth != null
                    ? "jobVehOth.vhId"
                    : "ckCtVeh.vhId"
                }
                label={t("job:driverTruck.pickTruck")}
                onChange={(e, name, value) =>
                  handleTruckChanges({
                    name,
                    target: { name, value: value?.value },
                  })
                }
                disabled={false}
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
                    disabled={false}
                  />
                </React.Fragment>
              ) : (
                <>
                  <C1SelectField
                    isServer={false}
                    name="vhType"
                    label={t("job:driverTruck.type")}
                    disabled={true}
                    error={errors["vhType"] !== undefined}
                    helperText={errors["vhType"] || ""}
                    value={truckTypeArr[0]?.vhtyId}
                    optionsMenuItemArr={truckTypeArr.map((item, i) => {
                      return (
                        <MenuItem value={item.vhtyId} key={i}>
                          {item.vhtyName}
                        </MenuItem>
                      );
                    })}
                  />
                  {/*<C1InputField*/}
                  {/*  label={t("job:driverTruck.class")}*/}
                  {/*  name="vhClass"*/}
                  {/*  disabled*/}
                  {/*  error={errors["vhClass"] !== undefined}*/}
                  {/*  helperText={errors["vhClass"] || ""}*/}
                  {/*  // value={getValue(inputData?.ckCtVeh?.vhClass)}*/}
                  {/*  value={inputData?.ckCtVeh?.vhId ? truckClass : null}*/}
                  {/*/>*/}
                </>
              )}
            </C1CategoryBlock>
          </Grid>
        </C1TabContainer>
      </Grid>
    </C1PopUp>
  );
};

export default withErrorHandler(AssignDriverPopup);
