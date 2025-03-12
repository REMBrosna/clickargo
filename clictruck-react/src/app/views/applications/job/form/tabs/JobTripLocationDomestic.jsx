import {
  Checkbox,
  FormControlLabel,
  FormGroup,
  Grid,
  InputAdornment,
} from "@material-ui/core";
import {
  FolderOpenOutlined,
  GridOnOutlined,
  SpeedOutlined,
} from "@material-ui/icons";
import PlaceOutlinedIcon from "@material-ui/icons/PlaceOutlined";
import _, { isEmpty } from "lodash";
import React, { useContext, useEffect, useState } from "react";

import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import useHttp from "app/c1hooks/http";
import {JobStates, Roles, TripType} from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/statusUtils";
import { getValue } from "app/c1utils/utility";
import LocationFormGroup from "app/clictruckcomponent/LocationGroupTripChargesDomestic";
import NumFormat from "app/clictruckcomponent/NumFormat";

import RateTableListPopup from "../../popups/RateTableListPopup";
import JobTruckContext from "../JobTruckContext";
import C1TextArea from "../../../../../c1component/C1TextArea";

const JobTripLocationDomestic = ({
   formAction,
   locationArr,
   isFieldHidden,
   isEnableForToAccept,
   setAutoCloseDialogAfterSelectMultiDropRate,
   jobTrips = [],
   ableToSaveModifiesTrip,
   notEditable = false,
 }) => {

  const { t } = useTranslation(["job", "common"]);

  const {
    tcrData,
    setTcrData,
    setTestTmpDataWrap,
    viewType,
    isDisabled,
    inputData,
    errors,
    setOpenWarning,
    setWarningMessage,
  } = useContext(JobTruckContext);

  //enable open price trip price input when checkbox is checked
  // const [isOpenPrice, setIsOpenPrice] = React.useState(false);
  const [isInputEnabled, setIsInputEnabled] = React.useState(false);

  const {
    isLoading,
    isFormSubmission,
    res,
    validation,
    error,
    urlId,
    sendRequest,
  } = useHttp();

  const [jobTripList, setJobTripList] = useState(
    inputData?.tckCtTripList || []
  );
  const [errorItems, setErrorItems] = useState([]);
  const [isMultiTrip, setIsMultiTrip] = useState(
    inputData?.tckCtTripList?.length > 1
  );

  const isMobileEnabled = inputData?.jobMobileEnabled === "Y";

  useEffect(() => {
    let idx = jobTripList.findIndex((item) => item.trId === tcrData.trId);
    if (idx > -1) {
      let prefix = "[" + idx + "]";

      const errorIds = Object.keys(errors).filter(
        (key) => key.indexOf(prefix) == 0
      );
      let errJson = {};

      for (let errKey of errorIds) {
        let keyWithoutPrefix = errKey.substring(prefix.length);
        errJson = Object.assign(errJson, {
          [keyWithoutPrefix]: errors[errKey],
        });
      }
      setErrorItems(errJson);
    }
  }, [errors]);

  const handleInputChange = (e, fieldAttribute) => {
    const { name, value } = e.target;
    //since we have the locationArr, we can get the rest of the details from here
    let details = locationArr?.filter((e) => e?.locId === value)[0];
    let isAddrFieldEnabled = false;
    if (details?.tckCtMstLocationType?.lctyId === "REGION") {
      isAddrFieldEnabled = true;
    }

    if (name.includes("tckCtLocation")) {
      if (name.includes("locAddress")) {
        setTcrData({
          ...tcrData,
          [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
          ...deepUpdateState(tcrData, name, value),
        });
      } else {
        //this is to populate the address details
        setTcrData({
          ...tcrData,
          [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
          [fieldAttribute]: {
            ...tcrData[fieldAttribute],
            tckCtLocation: details,
            tlocLocAddress: details?.locAddress,
          },
        });
      }
    } else if (name.includes("tlocLocAddress")) {
      setTcrData({
        ...tcrData,
        [fieldAttribute]: {
          ...tcrData[fieldAttribute],
          tlocLocAddress: value,
        },
      });
    } else {
      //this is remarks
      setTcrData({
        ...tcrData,
        [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
        ...deepUpdateState(tcrData, name, value),
      });
    }
  };
  const handleInputDeepChange = (e) => {
    const elName = e.target.name;
    let elValue = e.target.value;
    if (elName.includes("tcPrice")) {
      elValue = elValue.replace(/[^0-9]/g, "");
    }
    setTcrData({ ...tcrData, ...deepUpdateState(tcrData, elName, elValue) });
  };

  const handleTimeChange = (name, date) => {
    setTcrData({
      ...tcrData,
      ...deepUpdateState(tcrData, name, date),
    });
  };

  useEffect(() => {
    if (!isLoading && !error && res && !validation) {
      switch (urlId) {
        case "getLocationFromById": {
          const data = res?.data?.aaData[0];
          // setLocationData({...locationData, ...{fromLocationDetail: data.locAddress}})
          setTcrData({
            ...tcrData,
            ...deepUpdateState(
              tcrData,
              "location.fromLocationDetail",
              data.locAddress
            ),
          });
          break;
        }
        case "getLocationToById": {
          const data = res?.data?.aaData[0];
          // setLocationData({...locationData, ...{toLocationDetail: data.locAddress}})
          setTcrData({
            ...tcrData,
            ...deepUpdateState(
              tcrData,
              "location.toLocationDetail",
              data.locAddress
            ),
          });
          break;
        }
        case "getTripPrices": {
          setTcrData({
            ...tcrData,
            ...deepUpdateState(
              tcrData,
              "tckCtTripCharge.tcPrice",
              res?.data?.data?.trCharge
            ),
          });
          break;
        }
        default: {
        }
      }
    }
  }, [urlId, isLoading, isFormSubmission, res, validation, error]);

  // state for rateLocation
  useEffect(() => {
    if (tcrData) {
      const from = tcrData?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId;
      const to = tcrData?.tckCtTripLocationByTrTo?.tckCtLocation?.locId;
      let isRegionFrom = false;
      let isRegionTo = false;
      if (from) {
        const locFrom = locationArr.find((e) => e.locId === from);
        if (locFrom?.tckCtMstLocationType?.lctyId === "REGION") {
          isRegionFrom = true;
        }
      }

      if (to) {
        const locTo = locationArr.find((e) => e.locId === to);
        if (locTo?.tckCtMstLocationType?.lctyId === "REGION") {
          isRegionTo = true;
        }
      }
      if (isFieldHidden) {
        const newValue = {
          ...tcrData,
          tckCtTripLocationByTrFromDisable: !isRegionFrom,
          tckCtTripLocationByTrToDisable: !isRegionTo,
          trChargeOpen: "Y",
        };
        if(jobTrips && jobTrips.length >= 1) {
          newValue.tckCtTripLocationByTrFrom = jobTrips?.[0]?.tckCtTripLocationByTrFrom
        }
        setTcrData(newValue);
      } else {
        setTcrData({
          ...tcrData,
          tckCtTripLocationByTrFromDisable: !isRegionFrom,
          tckCtTripLocationByTrToDisable: !isRegionTo,
        });
      }
    } else {
      setTcrData({
        ...tcrData,
        ...{
          tckCtTripLocationByTrFromDisable: true,
          tckCtTripLocationByTrToDisable: true,
        },
      });
    }
  }, [jobTrips]);

  const [isOpenRatePopup, setIsOpenRatePopup] = useState(false);
  const isOpenPrice = tcrData?.trChargeOpen === "Y";
  const company = {
    to: inputData?.tcoreAccnByJobPartyTo?.accnId,
    coff: inputData?.tcoreAccnByJobPartyCoFf?.accnId,
  };

  const handleOpenRateTable = () => {
    if (!company?.to) {
      // haven't select TO
      setOpenWarning(true);
      setWarningMessage(t("common:common.msg.noToInRateTable"));
    } else if (
      inputData?.tckJob?.tckMstShipmentType?.shtId === "DOMESTIC" &&
      !inputData?.tckCtMstVehType?.vhtyId
    ) {
      // haven't select truck type
      setOpenWarning(true);
      setWarningMessage(t("common:common.msg.truckTypeRequired"));
    } else {
      setIsOpenRatePopup(true);
    }
  };

  const handleOpenPrice = () => {
    setTcrData({
      ...tcrData,
      tckCtTripLocationByTrFrom: null,
      tckCtTripLocationByTrTo: null,
      tckCtTripCharge: {
        tcPrice: 0,
      },
      trChargeOpen: "Y",
      tckCtTripLocationByTrFromDisable: true,
      tckCtTripLocationByTrToDisable: true,
    });
  };

  const handleSelectedRate = (item) => {
    let tripData = convert2TcrData(item);
    let multiTrip = false;
    if (
      TripType.M.code === item.trType &&
      item.tckCtTripRates &&
      item.tckCtTripRates.length > 0
    ) {
      const tripDataChildList = [];
      item.tckCtTripRates
        .sort((a, b) => a.trSeq - b.trSeq)
        .forEach((itemChild) => {
          const tripDataChild = convert2TcrData(itemChild);
          tripDataChildList.push(tripDataChild);
        });
      tripData = { ...tripData, tcrDataChild: tripDataChildList };
      multiTrip = true;
    } else {
      multiTrip = false;
    }
    setIsMultiTrip(multiTrip);

    setTcrData({ ...tcrData, ...tripData });
    setIsOpenRatePopup(false);
    if (multiTrip && setAutoCloseDialogAfterSelectMultiDropRate) {
      setAutoCloseDialogAfterSelectMultiDropRate(true);
      //setTimeout(afterSelectedRate,5000); //
      // setTestTmpDataWrap("aaaa");
      // await new Promise(() => setTimeout(afterSelectedRate, 5000));
    }
  };

  const convert2TcrData = (item) => {
    let tcrData = {};
    tcrData = {
      ...tcrData,
      tckCtTripLocationByTrFrom: {
        ...tcrData?.tckCtTripLocationByTrFrom,
        tckCtLocation: {
          ...tcrData?.tckCtTripLocationByTrFrom?.tckCtLocation,
          locId: item?.tckCtLocationByTrLocFrom?.locId,
          locAddress: item?.tckCtLocationByTrLocFrom?.locAddress,
        },
        tlocLocAddress: item?.tckCtLocationByTrLocFrom?.locAddress,
      },
      tckCtTripLocationByTrTo: {
        ...tcrData?.tckCtTripLocationByTrTo,
        tckCtLocation: {
          ...tcrData?.tckCtTripLocationByTrTo?.tckCtLocation,
          locId: item?.tckCtLocationByTrLocTo?.locId,
          locAddress: item?.tckCtLocationByTrLocTo?.locAddress,
        },
        tlocLocAddress: item?.tckCtLocationByTrLocTo?.locAddress,
      },
      tckCtTripCharge: {
        ...tcrData?.tckCtTripCharge,
        tcPrice: item?.trCharge,
      },
      trChargeOpen: "N",
      tckCtTripLocationByTrFromDisable: true,
      tckCtTripLocationByTrToDisable: true,
      tCkCtTripRate: item,
    };
    return tcrData;
  };

  const isFirstTrip = () => {
    if (isMultiTrip) {
      let idx = inputData?.tckCtTripList?.findIndex(
        (item) => item.trId === tcrData.trId
      );
      return idx === 0;
    }
    return true;
  };

  const locationAction =
    !isDisabled && !isMultiTrip && !isFieldHidden ? (
      <Grid container justifyContent="flex-end">
        <Grid item>
          <C1LabeledIconButton
            tooltip={t("buttons:open")}
            label={t("buttons:open")}
            action={handleOpenPrice}
          >
            <FolderOpenOutlined color="primary" />
          </C1LabeledIconButton>
        </Grid>
        <Grid item>
          <C1LabeledIconButton
            tooltip={t("buttons:rateTable")}
            label={t("buttons:rateTable")}
            action={handleOpenRateTable}
          >
            <GridOnOutlined color="primary" />
          </C1LabeledIconButton>
        </Grid>
      </Grid>
    ) : (
      false
    );

  const tripIndex = jobTrips.findIndex((item) => item.trId === tcrData.trId);
  const restrictSinglePickup = jobTrips && jobTrips.length >= 1 && (formAction === "ADD" || (formAction === "EDIT" && tripIndex !== 0));
  const toLocs = jobTrips?.map(val => {
      if(tcrData?.tckCtTripLocationByTrTo?.tckCtLocation?.locId !== val?.tckCtTripLocationByTrTo?.tckCtLocation?.locId){
          return val?.tckCtTripLocationByTrTo?.tckCtLocation?.locId;
      }
  }).filter(value => value !== undefined)

  const fromLocs = jobTrips?.map(val => {
      return val?.tckCtTripLocationByTrTo?.tckCtLocation?.locId;
  });

  const showDriverComment = [JobStates.ONGOING.code, JobStates.DLV.code, JobStates.PAUSED.code].includes(inputData?.tckJob?.tckMstJobState?.jbstId);

  return (
    <React.Fragment>
      <C1CategoryBlock
        icon={<PlaceOutlinedIcon />}
        title={t("job:tripDetails.locationDetail")}
        actionEl={locationAction}
      >
        <Grid container alignItems="center" spacing={3}>
          <Grid item xs={12}>
            <Grid container alignItems="center" spacing={3}>
              <Grid item md={6} xs={12}>
                <LocationFormGroup
                  type={{
                    name: "tckCtTripLocationByTrFrom.tckCtLocation.locId",
                    value: getValue(
                      tcrData?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId
                    ),
                    label: t("job:tripDetails.from"),
                    disabled: isDisabled || !isOpenPrice || restrictSinglePickup,
                    exclude: [...fromLocs]
                  }}
                  details={{
                    name: !tcrData.tckCtTripLocationByTrFromDisable
                      ? "tckCtTripLocationByTrFrom.tlocLocAddress"
                      : "tckCtTripLocationByTrFrom.tckCtLocation.locAddress",
                    value: !tcrData.tckCtTripLocationByTrFromDisable
                      ? getValue(
                          tcrData?.tckCtTripLocationByTrFrom?.tlocLocAddress
                        )
                      : getValue(
                          tcrData?.tckCtTripLocationByTrFrom?.tckCtLocation
                            ?.locAddress
                        ),
                    disabled: isDisabled || !isOpenPrice || restrictSinglePickup
                      ? true
                      : tcrData?.tckCtTripLocationByTrFromDisable,
                      required: !tcrData?.tckCtTripLocationByTrFromDisable
                  }}
                  time={{
                    name: "tckCtTripLocationByTrFrom.tlocDtLoc",
                    isMandatory: isMobileEnabled,
                    value: tcrData?.tckCtTripLocationByTrFrom?.tlocDtLoc
                      ? getValue(tcrData?.tckCtTripLocationByTrFrom?.tlocDtLoc)
                      : null,
                      disabled:
                          notEditable
                          // || restrictSinglePickup
                  }}
                  remarks={{
                    name: "tckCtTripLocationByTrFrom.tlocRemarks",
                    value: getValue(
                      tcrData?.tckCtTripLocationByTrFrom?.tlocRemarks
                    ),
                      disabled: notEditable
                          // || restrictSinglePickup
                  }}
                  mobile={{
                    name: "tckCtTripLocationByTrFrom.tlocMobileNo",
                    value: getValue(
                      tcrData?.tckCtTripLocationByTrFrom?.tlocMobileNo
                    ),
                    isMandatory: false,
                    disabled:
                        // isDisabled ||
                        !isOpenPrice ||
                        // restrictSinglePickup ||
                        notEditable
                  }}
                  isMobileEnabled={true}
                  isDisabled={isDisabled}
                  isDisableForMultiDrop={isMultiTrip && !isFirstTrip()} //
                  isEnableForToAccept={isEnableForToAccept}
                  locationArr={locationArr}
                  handleInputChange={(e) =>
                    handleInputChange(e, "tckCtTripLocationByTrFrom")
                  }
                  handleTimeChange={handleTimeChange}
                  errors={errorItems}
                />
                  {showDriverComment && (
                      <C1TextArea
                          name="atComment"
                          label={t("listing:attachments.pickUpComment")}
                          multiline
                          disabled
                          textLimit={1024}
                          value={getValue(tcrData?.tckCtTripLocationByTrFrom?.tlocComment)}
                          isabled={false}
                      />
                  )}
              </Grid>

              <Grid item md={6} xs={12}>
                <LocationFormGroup
                  type={{
                    name: "tckCtTripLocationByTrTo.tckCtLocation.locId",
                    value: getValue(
                      tcrData?.tckCtTripLocationByTrTo?.tckCtLocation?.locId
                    ),
                    label: t("job:tripDetails.to"),
                    disabled: (ableToSaveModifiesTrip && tcrData?.trStatus === "A") || notEditable || !isOpenPrice,
                    exclude: [...toLocs, jobTrips?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId || tcrData?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId]
                  }}
                  details={{
                    name: !tcrData.tckCtTripLocationByTrToDisable
                      ? "tckCtTripLocationByTrTo.tlocLocAddress"
                      : "tckCtTripLocationByTrTo.tckCtLocation.locAddress",
                    value: !tcrData.tckCtTripLocationByTrToDisable
                      ? getValue(
                          tcrData?.tckCtTripLocationByTrTo?.tlocLocAddress
                        )
                      : getValue(
                          tcrData?.tckCtTripLocationByTrTo?.tckCtLocation
                            ?.locAddress
                        ),
                      disabled: (ableToSaveModifiesTrip && tcrData?.trStatus === "A") || notEditable || tcrData?.tckCtTripLocationByTrToDisable,
                      required: !tcrData?.tckCtTripLocationByTrToDisable
                  }}
                  time={{
                    name: "tckCtTripLocationByTrTo.tlocDtLoc",
                    isMandatory: isMobileEnabled,
                    value: tcrData?.tckCtTripLocationByTrTo?.tlocDtLoc
                      ? getValue(tcrData?.tckCtTripLocationByTrTo?.tlocDtLoc)
                      : null,
                      disabled: notEditable
                  }}
                  remarks={{
                    name: "tckCtTripLocationByTrTo.tlocRemarks",
                    value: getValue(
                      tcrData?.tckCtTripLocationByTrTo?.tlocRemarks
                    ),
                      disabled: notEditable
                  }}
                  cargoRec={{
                      name: "tckCtTripLocationByTrTo.tlocCargoRec",
                      value: getValue(
                          tcrData?.tckCtTripLocationByTrTo?.tlocCargoRec
                      ),
                      isMandatory: false,
                      disabled: notEditable
                  }}
                  mobile={{
                    name: "tckCtTripLocationByTrTo.tlocMobileNo",
                    value: getValue(
                      tcrData?.tckCtTripLocationByTrTo?.tlocMobileNo
                    ),
                    isMandatory: true,
                      disabled: notEditable
                  }}
                  isMobileEnabled={true}
                  isCargoOwnerEnabled={true}
                  isDisabled={isDisabled}
                  isEnableForToAccept={isEnableForToAccept}
                  locationArr={locationArr}
                  handleInputChange={(e) =>
                    handleInputChange(e, "tckCtTripLocationByTrTo")
                  }
                  handleTimeChange={handleTimeChange}
                  errors={errorItems}
                />
                  {showDriverComment && (
                      <C1TextArea
                          name="tlocDeviationComment"
                          label={t("listing:attachments.dropOffComment")}
                          multiline
                          disabled
                          textLimit={1024}
                          value={getValue(tcrData?.tckCtTripLocationByTrTo?.tlocComment)}
                          isabled={false}
                      />
                  )}
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </C1CategoryBlock>
      {!isFieldHidden && (
        <C1CategoryBlock
          icon={<SpeedOutlined />}
          title={t("job:tripDetails.tripCharges")}
        >
          <Grid container justifyContent="flex-end">
            <Grid item xs={12} sm={4}>
              <C1InputField
                name="tckCtTripCharge.tcPrice"
                label={`Amount (IDR)`}
                disabled={!isOpenPrice || isDisabled}
                value={tcrData?.tckCtTripCharge?.tcPrice}
                onChange={(e) => handleInputDeepChange(e)}
                //set align to right
                inputProps={{ style: { textAlign: "right" } }}
                InputProps={{
                  //add thousand separator mask to input number
                  inputComponent: NumFormat,
                  //add Rp label infront of number
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
              <FormGroup>
                <FormControlLabel
                  control={
                    <Checkbox checked={isOpenPrice} disabled={isDisabled} />
                  }
                  label={t("job:tripDetails.openPrice")}
                />
              </FormGroup>
            </Grid>
          </Grid>
        </C1CategoryBlock>
      )}

      {isOpenRatePopup && (
        <RateTableListPopup
          open={isOpenRatePopup}
          handleClose={() => setIsOpenRatePopup(false)}
          handleSelected={handleSelectedRate}
          company={company}
          isDomestic
          truckTypeData={inputData?.tckCtMstVehType?.vhtyId}
          trTypeFilter={"S,M"} // Single and multi-drop trap type
        />
      )}
    </React.Fragment>
  );
};

export default JobTripLocationDomestic;
