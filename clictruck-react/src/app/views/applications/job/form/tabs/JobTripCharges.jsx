import { Checkbox, FormControlLabel, Grid, InputAdornment, Typography } from "@material-ui/core";
import { FolderOpenOutlined, GridOnOutlined } from "@material-ui/icons";
import WidgetsOutlinedIcon from '@material-ui/icons/WidgetsOutlined';
import DescriptionIcon from '@material-ui/icons/DescriptionOutlined';
import BookOutlinedIcon from '@material-ui/icons/BookOutlined';
import LocalAtmOutlinedIcon from '@material-ui/icons/LocalAtmOutlined';
import PlaceOutlinedIcon from '@material-ui/icons/PlaceOutlined';
import LocalShippingOutlinedIcon from '@material-ui/icons/LocalShippingOutlined';
import SpeedOutlinedIcon from '@material-ui/icons/SpeedOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import _ from "lodash";
import React, { useContext, useEffect, useImperativeHandle, useState } from "react";
import { forwardRef } from "react";
import { useTranslation } from "react-i18next";

import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import C1InputField from "app/c1component/C1InputField";
import C1LabeledIconButton from "app/c1component/C1LabeledIconButton";
import C1SelectField from "app/c1component/C1SelectField";
import C1TabContainer from "app/c1component/C1TabContainer";
import C1TextArea from "app/c1component/C1TextArea";
import useHttp from "app/c1hooks/http";
import { CK_CT_LOCATION, CK_MST_CONTAINER_LOAD, CK_MST_CONTAINER_TYPE, CK_MST_GOODS_TYPE, CK_MST_VEH_TYPE, Roles } from "app/c1utils/const";
import { JobStates } from "app/c1utils/const";
import { deepUpdateState } from "app/c1utils/statusUtils";
import { getValue, isArrayNotEmpty } from "app/c1utils/utility";
import LocationFormGroup from "app/clictruckcomponent/LocationGroup";
import NumFormat from "app/clictruckcomponent/NumFormat";
import useAuth from "app/hooks/useAuth";
import { MatxLoading } from "matx";

import RateTableListPopup from "../../popups/RateTableListPopup";
import JobTruckContext from "../JobTruckContext";
import Reimbursements from "./category/Reimbursements";

const JobTripCharges = forwardRef(({ }, ref) => {

    const { t } = useTranslation(["job", "common"]);
    const { inputData, shipmentType, setInputData, viewType, handleDateChange, isDisabled, errors, setOpenWarning, setWarningMessage } = useContext(JobTruckContext);
    const { isLoading, urlId, res, error, sendRequest } = useHttp();
    const { user } = useAuth();


    console.log("inputData from context:", inputData?.hiddenFields?.includes("tripcharges"));
    //set to true to give  time to load the locations array before rendering
    const [loading, setLoading] = useState(true);

    //initialize to empty array since tckCtTripList from BE will not be initialized unless there is existing record
    const jobTripDetailsInit = inputData?.tckCtTripList == null ? [] : { ...inputData?.tckCtTripList[0] };
    const [jobTripDetails, setJobTripDetails] = useState({
        ...jobTripDetailsInit,
        tckCtTripLocationByTrDepotDisable: true,
        tckCtTripLocationByTrFromDisable: true, 
        tckCtTripLocationByTrToDisable: true
    });

    console.log("jobTripDetails", jobTripDetails);
    

    //initialize to empty array if tckCtTripList.tckCtTripCargoFmList is null since it will not be initialzed from BE unless
    //there is existing record
    let cargoInit = inputData?.tckCtTripList == null || inputData?.tckCtTripList[0]?.tckCtTripCargoFmList == null
        ? [] : inputData?.tckCtTripList[0]?.tckCtTripCargoFmList[0];
    const [cargoTripDetails, setCargoTripDetails] = useState({ ...cargoInit });

    const [locationArr, setLocationArr] = useState([]);

    // data for disbursement component
    const tripId = inputData?.tckCtTripList?.length > 0 && inputData?.tckCtTripList[0]?.trId ? inputData?.tckCtTripList[0].trId : 0;
    const isToFinance = user?.authorities.some(item => item.authority === Roles?.FF_FINANCE?.code);
    const isDelivered = inputData?.tckJob?.tckMstJobState?.jbstId === JobStates?.DLV?.code;
    const arrJobStateDisable = [JobStates?.ASG?.code, JobStates?.SUB?.code, JobStates?.ACP?.code, JobStates?.ONGOING?.code, JobStates?.NEW?.code, null];
    const isReimbursementDisabled = arrJobStateDisable.includes(inputData?.tckJob?.tckMstJobState?.jbstId);

    // state for rateLocation
    const [isOpenRatePopup, setIsOpenRatePopup] = useState(false);
    const [isOpenPrice, setIsOpenPrice] = useState(false);
    //const isOpenPrice = jobTripDetails?.trChargeOpen === 'Y'
    const company = {
        to: inputData?.tcoreAccnByJobPartyTo?.accnId,
        coff: inputData?.tcoreAccnByJobPartyCoFf?.accnId
    }

    const isMobileEnabled = inputData?.jobMobileEnabled === 'Y';

    useImperativeHandle(ref, () => ({
        getTripDetails: () => {
            return _.merge(jobTripDetails, { tckCtTripCargoFmList: [cargoTripDetails] });
        }
    }));

    //After render
    useEffect(() => {

        if(inputData?.hiddenFields?.includes("tripcharges")){
            setIsOpenPrice(true);
        }

        //call the api to load the locations
        //should filter based on the selected TO from Details tab
        sendRequest(`${CK_CT_LOCATION}&mDataProp_2=TCoreAccn.accnId&sSearch_2=${inputData?.tcoreAccnByJobPartyTo?.accnId}`, 'fetchLocationsByTo', 'get');       
    }, []);

    //Track changes of urlId,isLoading from useHttp changes
    useEffect(() => {
        if (!isLoading && res && !error) {
            if (urlId === "fetchLocationsByTo") {
                setLoading(isLoading);

                setLocationArr([...res?.data?.aaData]);
            } else if (urlId === "getTripPrices") {
                setJobTripDetails({
                    ...jobTripDetails,
                    ...deepUpdateState(jobTripDetails, "tckCtTripCharge.tcPrice", res?.data?.data?.trCharge)
                });
            }

        }
    }, [urlId, isLoading, error, res]);

    // state for rateLocation
    useEffect(() => {
       console.log("Useffect for locationArr.length",jobTripDetails);
        if (jobTripDetails) {
            const from = jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId;
            const to = jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locId;
            const depot = jobTripDetails?.tckCtTripLocationByTrDepot?.tckCtLocation?.locId;

            let isRegionFrom = false;
            let isRegionTo = false;
            let isRegionDepot = false;

            if (from) {
                const locFrom = locationArr.find((e) => e.locId === from)
                if (locFrom?.tckCtMstLocationType?.lctyId === "REGION") {
                    isRegionFrom = true
                }
            }

            if (to) {
                const locTo = locationArr.find((e) => e.locId === to)
                if (locTo?.tckCtMstLocationType?.lctyId === "REGION") {
                    isRegionTo = true
                }
            }

            if (depot) {
                const locDeop = locationArr.find((e) => e.locId === depot)
                if (locDeop?.tckCtMstLocationType?.lctyId === "REGION") {
                    isRegionDepot = true
                }
            }

            setJobTripDetails({
                ...jobTripDetails,
                tckCtTripLocationByTrFromDisable: !isRegionFrom,
                tckCtTripLocationByTrToDisable: !isRegionTo,
                tckCtTripLocationByTrDepotDisable: !isRegionDepot
            });

        } else {
           
            setJobTripDetails({
                ...jobTripDetails, ...{
                    tckCtTripLocationByTrFromDisable: true,
                    tckCtTripLocationByTrToDisable: true,
                    tckCtTripLocationByTrDepotDisable: true
                }
            })
        }

        if(inputData?.hiddenFields?.includes("tripcharges")){
            console.log("setting open price true");
            setIsOpenPrice(true);
        }
           
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [locationArr.length])


    const handleInputChange = (e, fieldAttribute) => {
        const { name, value } = e.target;
        //since we have the locationArr, we can get the rest of the details from here
        let details = locationArr?.find(e => e?.locId === value);
        let isAddrFieldEnabled = false;
        // console.log("handleInputChange jobtripcharge", name, value);
        if (details?.tckCtMstLocationType?.lctyId === "REGION") {
            isAddrFieldEnabled = true;
        }

        if (name.includes("tckCtLocation")) {
            if (name.includes("locAddress")) {
                setJobTripDetails({
                    ...jobTripDetails, [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
                    ...deepUpdateState(jobTripDetails, name, value)
                });
            } else {
                //this is to populate the address details
                setJobTripDetails({
                    ...jobTripDetails, [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
                    [fieldAttribute]: {
                        ...jobTripDetails[fieldAttribute],
                        tckCtLocation: details,
                        tlocLocAddress: details?.locAddress
                    }
                });
                // calculateTripCharge();
            }

        } else if (name.includes("tcPrice")) { //clean tcprice from non numerical input including negative sign

            const cleanedValue = e.target.value.replace(/[^0-9]/g, '');
            console.log("cleaned value", cleanedValue);
            setJobTripDetails({
                ...jobTripDetails, [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
                ...deepUpdateState(jobTripDetails, name, cleanedValue)
            });
        } else if (name.includes("tlocLocAddress")) {
            setJobTripDetails({
                ...jobTripDetails,
                [fieldAttribute]: {
                    ...jobTripDetails[fieldAttribute],
                    tlocLocAddress: value
                }
            });
        } else {
            //this is remarks
            setJobTripDetails({
                ...jobTripDetails, [`${fieldAttribute}Disable`]: !isAddrFieldEnabled,
                ...deepUpdateState(jobTripDetails, name, value)
            });
        }

    }

    //set location pick/drop time
    let timeVal = null; // timeVal is to trigger value update
    const handleTimeChange = (name, date) => {

        setJobTripDetails({
            ...deepUpdateState(jobTripDetails, name, date)
        });

        timeVal = getValue(jobTripDetails?.[name]);
    };

    const handleCargoInputChange = (e) => {
        const { name, value } = e.target;
        if (name.includes("tckCtMstVehType")) {
            setInputData({ ...inputData, ...deepUpdateState(inputData, name, value) });
            // call calculate trip to get trip charge value:
            // calculateTripCharge();
        } else {
            setCargoTripDetails({ ...cargoTripDetails, ...deepUpdateState(cargoTripDetails, name, value) });
        }
    }

    let sumTotalCharge = 0;

    const calculateTripCharge = () => {
        //console.log("tripcharge calculate called!");
        const valReimbursement = parseInt(getValue(jobTripDetails?.totalReimbursement));
        const valTcPrice = parseInt(getValue(jobTripDetails?.tckCtTripCharge?.tcPrice));
        sumTotalCharge = valReimbursement + valTcPrice;
        if ((inputData?.tcoreAccnByJobPartyTo?.accnId != null) &&
            (inputData?.tcoreAccnByJobPartyCoFf?.accnId != null) &&
            (jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId != null) &&
            (jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locId != null) &&
            (inputData?.tckCtMstVehType?.vhtyId != null)
        ) {
            const requestBody = {
                "toAccn": inputData?.tcoreAccnByJobPartyTo?.accnId,
                "coFfAccn": inputData?.tcoreAccnByJobPartyCoFf?.accnId,
                "locFrom": jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId,
                "locTo": jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locId,
                "vehType": inputData?.tckCtMstVehType?.vhtyId
            }
            //console.log("requestBody: ", requestBody)
            sendRequest(`/api/v1/clickargo/clictruck/tripcharges/calculation`, 'getTripPrices', 'post', requestBody);
        }
        //else {console.log("trip charge data not fullfilled");}
        // inputData?.tckCtTripList[0]?.totalTripPrice = jobTripDetails?.tckCtTripCharge?.tcPrice;

        // setInputData({ ...inputData, ...deepUpdateState(inputData, "tckCtTripList.0.totalTripPrice", jobTripDetails?.tckCtTripCharge?.tcPrice) });

        //console.log("inputData: ", inputData);
    }

   
    let isRequired = false;
    if (inputData?.jobMobileEnabled === "Y") {
        isRequired = true;
    }

    const renderLocationEl = () => {
        if ((shipmentType === "EXPORT") || (inputData?.tckJob?.tckMstShipmentType?.shtId === "EXPORT")) {
            return (
                <React.Fragment>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup
                            type={{
                                name: "tckCtTripLocationByTrDepot.tckCtLocation.locId",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tckCtLocation?.locId),
                                label: t("job:tripDetails.depo"),
                                disable: isDisabled 
                            }}
                            details={{
                                name: (!jobTripDetails?.tckCtTripLocationByTrDepotDisable) ? "tckCtTripLocationByTrDepot.tlocLocAddress"
                                    : "tckCtTripLocationByTrDepot.tckCtLocation.locAddress",
                                value: (!jobTripDetails?.tckCtTripLocationByTrDepotDisable) ? getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocLocAddress) : getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tckCtLocation?.locAddress),
                                disable: jobTripDetails?.tckCtTripLocationByTrDepotDisable
                            }}
                            time={{
                                name: "tckCtTripLocationByTrDepot.tlocDtLoc",
                                isMandatory: false,
                                value: jobTripDetails?.tckCtTripLocationByTrDepot?.tlocDtLoc ? getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocDtLoc) : null
                            }}
                            remarks={{
                                name: "tckCtTripLocationByTrDepot.tlocRemarks",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocRemarks)
                            }}
                            mobile={{
                                name: "tckCtTripLocationByTrDepot.tlocMobileNo",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocMobileNo),
                                isMandatory: false,
                            }}
                            isMobileEnabled={isMobileEnabled}
                            isDisabled={isDisabled}
                            locationArr={locationArr}
                            handleInputChange={(e) => handleInputChange(e, "tckCtTripLocationByTrDepot")}
                            handleTimeChange={handleTimeChange}
                            errors={errors}
                        />

                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup type={{
                            name: "tckCtTripLocationByTrFrom.tckCtLocation.locId",
                            value: getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId),
                            label: t("job:tripDetails.from"),
                            disable: isDisabled || !isOpenPrice
                        }}
                            details={{
                                name: (!jobTripDetails.tckCtTripLocationByTrFromDisable) ? "tckCtTripLocationByTrFrom.tlocLocAddress"
                                    : "tckCtTripLocationByTrFrom.tckCtLocation.locAddress",
                                value: (!jobTripDetails.tckCtTripLocationByTrFromDisable) ? getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocLocAddress) : getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locAddress),
                                disable: jobTripDetails?.tckCtTripLocationByTrFromDisable
                            }}
                            time={{
                                name: "tckCtTripLocationByTrFrom.tlocDtLoc",
                                isMandatory: isMobileEnabled,
                                value: jobTripDetails?.tckCtTripLocationByTrFrom?.tlocDtLoc ? getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocDtLoc) : null
                            }}
                            remarks={{
                                name: "tckCtTripLocationByTrFrom.tlocRemarks",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocRemarks)
                            }}
                            mobile={{
                                name: "tckCtTripLocationByTrFrom.tlocMobileNo",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocMobileNo),
                                isMandatory: false,
                            }}
                            isMobileEnabled={isMobileEnabled}
                            isDisabled={isDisabled}
                            locationArr={locationArr}
                            handleInputChange={(e) => handleInputChange(e, "tckCtTripLocationByTrFrom")}
                            handleTimeChange={handleTimeChange}
                            errors={errors}
                        />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup type={{
                            name: "tckCtTripLocationByTrTo.tckCtLocation.locId",
                            value: getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locId),
                            label: t("job:tripDetails.to"),
                            disable: isDisabled || !isOpenPrice
                        }}
                            details={{
                                name: (!jobTripDetails.tckCtTripLocationByTrToDisable) ? "tckCtTripLocationByTrTo.tlocLocAddress"
                                    : "tckCtTripLocationByTrTo.tckCtLocation.locAddress",
                                value: (!jobTripDetails.tckCtTripLocationByTrToDisable) ? getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocLocAddress) : getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locAddress),
                                disable: jobTripDetails?.tckCtTripLocationByTrToDisable
                            }}
                            time={{
                                name: "tckCtTripLocationByTrTo.tlocDtLoc",
                                isMandatory: isMobileEnabled,
                                value: jobTripDetails?.tckCtTripLocationByTrTo?.tlocDtLoc ? getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocDtLoc) : null
                            }}
                            remarks={{
                                name: "tckCtTripLocationByTrTo.tlocRemarks",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocRemarks)
                            }}
                            mobile={{
                                name: "tckCtTripLocationByTrTo.tlocMobileNo",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocMobileNo),
                                isMandatory: true,
                            }}
                            isMobileEnabled={isMobileEnabled}
                            isDisabled={isDisabled}
                            locationArr={locationArr}
                            handleInputChange={(e) => handleInputChange(e, "tckCtTripLocationByTrTo")}
                            handleTimeChange={handleTimeChange}
                            errors={errors}
                        />
                    </Grid>

                </React.Fragment>)
        }
        else {
            return (
                <React.Fragment>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup type={{
                            name: "tckCtTripLocationByTrFrom.tckCtLocation.locId",
                            value: getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locId),
                            label: t("job:tripDetails.from"),
                            disable: isDisabled || !isOpenPrice
                        }}
                            details={{
                                name: (!jobTripDetails.tckCtTripLocationByTrFromDisable) ? "tckCtTripLocationByTrFrom.tlocLocAddress"
                                    : "tckCtTripLocationByTrFrom.tckCtLocation.locAddress",
                                value: (!jobTripDetails.tckCtTripLocationByTrFromDisable) ? getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocLocAddress) : getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation?.locAddress),
                                disable: jobTripDetails?.tckCtTripLocationByTrFromDisable
                            }}
                            time={{
                                name: "tckCtTripLocationByTrFrom.tlocDtLoc",
                                value: jobTripDetails?.tckCtTripLocationByTrFrom?.tlocDtLoc ? getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocDtLoc) : null,
                                isMandatory: isMobileEnabled,
                            }}
                            remarks={{
                                name: "tckCtTripLocationByTrFrom.tlocRemarks",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocRemarks)
                            }}
                            mobile={{
                                name: "tckCtTripLocationByTrFrom.tlocMobileNo",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrFrom?.tlocMobileNo),
                                isMandatory: false,
                            }}
                            isMobileEnabled={isMobileEnabled}
                            isDisabled={isDisabled}
                            locationArr={locationArr}
                            handleInputChange={(e) => handleInputChange(e, "tckCtTripLocationByTrFrom")}
                            handleTimeChange={handleTimeChange}
                            errors={errors}
                        />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup type={{
                            name: "tckCtTripLocationByTrTo.tckCtLocation.locId",
                            value: getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locId),
                            label: t("job:tripDetails.to"),
                            disable: isDisabled || !isOpenPrice
                        }}
                            details={{
                                name: (!jobTripDetails.tckCtTripLocationByTrToDisable) ? "tckCtTripLocationByTrTo.tlocLocAddress"
                                    : "tckCtTripLocationByTrTo.tckCtLocation.locAddress",
                                value: (!jobTripDetails.tckCtTripLocationByTrToDisable) ? getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocLocAddress) : getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation?.locAddress),
                                disable: jobTripDetails?.tckCtTripLocationByTrToDisable
                            }}
                            time={{
                                name: "tckCtTripLocationByTrTo.tlocDtLoc",
                                value: jobTripDetails?.tckCtTripLocationByTrTo?.tlocDtLoc ? getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocDtLoc) : null,
                                isMandatory: isMobileEnabled,
                            }}
                            remarks={{
                                name: "tckCtTripLocationByTrTo.tlocRemarks",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocRemarks)
                            }}
                            mobile={{
                                name: "tckCtTripLocationByTrTo.tlocMobileNo",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrTo?.tlocMobileNo),
                                isMandatory: true,
                            }}
                            isMobileEnabled={isMobileEnabled}
                            isDisabled={isDisabled}
                            locationArr={locationArr}
                            handleInputChange={(e) => handleInputChange(e, "tckCtTripLocationByTrTo")}
                            handleTimeChange={handleTimeChange}
                            errors={errors}
                        />
                    </Grid>
                    <Grid item lg={4} md={6} xs={12} >
                        <LocationFormGroup
                            type={{
                                name: "tckCtTripLocationByTrDepot.tckCtLocation.locId",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tckCtLocation?.locId),
                                label: t("job:tripDetails.depo"),
                                disable: isDisabled
                            }}
                            details={{
                                name: (!jobTripDetails?.tckCtTripLocationByTrDepotDisable) ? "tckCtTripLocationByTrDepot.tlocLocAddress"
                                    : "tckCtTripLocationByTrDepot.tckCtLocation.locAddress",
                                value: (!jobTripDetails?.tckCtTripLocationByTrDepotDisable) ? getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocLocAddress) : getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tckCtLocation?.locAddress),
                                disable: jobTripDetails?.tckCtTripLocationByTrDepotDisable
                            }}
                            time={{
                                name: "tckCtTripLocationByTrDepot.tlocDtLoc",
                                value: jobTripDetails?.tckCtTripLocationByTrDepot?.tlocDtLoc ? getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocDtLoc) : null,
                                isMandatory: false,
                            }}
                            remarks={{
                                name: "tckCtTripLocationByTrDepot.tlocRemarks",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocRemarks)
                            }}
                            mobile={{
                                name: "tckCtTripLocationByTrDepot.tlocMobileNo",
                                value: getValue(jobTripDetails?.tckCtTripLocationByTrDepot?.tlocMobileNo),
                                isMandatory: false,
                            }}
                            isMobileEnabled={isMobileEnabled}
                            isDisabled={isDisabled}
                            locationArr={locationArr}
                            handleInputChange={(e) => handleInputChange(e, "tckCtTripLocationByTrDepot")}
                            handleTimeChange={handleTimeChange}
                            errors={errors}
                        />
                    </Grid>
                </React.Fragment>)
        }
    }

    // console.log("toAccn", inputData?.tcoreAccnByJobPartyTo?.accnId);

    const handleOpenRateTable = () => {
        if (!company?.to) {
            setOpenWarning(true)
            setWarningMessage(t("common:common.msg.noToInRateTable"))
        } else {
            setIsOpenRatePopup(true)
        }
    }

    const handleOpenPrice = () => {
        setJobTripDetails({
            ...jobTripDetails,
            tckCtTripLocationByTrFrom: null,
            tckCtTripLocationByTrTo: null,
            tckCtTripCharge: {
                tcPrice: 0
            },
            trChargeOpen: 'Y',
            tckCtTripLocationByTrFromDisable: true,
            tckCtTripLocationByTrToDisable: true
        });
        setInputData({
            ...inputData,
            tckCtMstVehType: null
        });
        setIsOpenPrice(true);
    }

    const handleSelectedRate = (item) => {
        const tripData = {
            ...jobTripDetails,
            tckCtTripLocationByTrFrom: {
                ...jobTripDetails?.tckCtTripLocationByTrFrom,
                tckCtLocation: {
                    ...jobTripDetails?.tckCtTripLocationByTrFrom?.tckCtLocation,
                    locId: item?.tckCtLocationByTrLocFrom?.locId,
                    locAddress: item?.tckCtLocationByTrLocFrom?.locAddress,
                }
            },
            tckCtTripLocationByTrTo: {
                ...jobTripDetails?.tckCtTripLocationByTrTo,
                tckCtLocation: {
                    ...jobTripDetails?.tckCtTripLocationByTrTo?.tckCtLocation,
                    locId: item?.tckCtLocationByTrLocTo?.locId,
                    locAddress: item?.tckCtLocationByTrLocTo?.locAddress,
                }
            },
            tckCtTripCharge: {
                ...jobTripDetails?.tckCtTripCharge,
                tcPrice: item?.trCharge
            },
            trChargeOpen: 'N',
            tckCtTripLocationByTrFromDisable: true,
            tckCtTripLocationByTrToDisable: true
        }

        setJobTripDetails({ ...jobTripDetails, ...tripData })
        setInputData({
            ...inputData,
            tckCtMstVehType: item?.tckCtMstVehType
        })

        setIsOpenRatePopup(false);
        setIsOpenPrice(false);
    }

    const locationAction = !isDisabled ? inputData?.hiddenFields?.includes("tripcharges") ? null : (
        <Grid container justifyContent="flex-end">
            <Grid item>
                <C1LabeledIconButton
                    tooltip={t("buttons:open")}
                    label={t("buttons:open")}
                    action={handleOpenPrice}>
                    <FolderOpenOutlined color="primary" />
                </C1LabeledIconButton>
            </Grid>
            <Grid item>
                <C1LabeledIconButton
                    tooltip={t("buttons:rateTable")}
                    label={t("buttons:rateTable")}
                    action={handleOpenRateTable}>
                    <GridOnOutlined color="primary" />
                </C1LabeledIconButton>
            </Grid>
        </Grid>
    )
        : null

    return (loading ? <MatxLoading /> : <React.Fragment>
        <Grid item xs={12}>
            <C1TabContainer>
                <Grid item xs={12} >
                    <C1CategoryBlock icon={<PlaceOutlinedIcon />} title={t("job:tripDetails.locationDetails")} actionEl={locationAction}>
                    </C1CategoryBlock>
                </Grid>
                {/* <Grid item xs={12}><Typography variant="subtitle2">Choose Truck Operator in the Job Details to load the locations.</Typography></Grid> */}
                <Grid item xs={12}><Typography variant="subtitle2">{t("job:tripDetails.loadLocation")}</Typography></Grid>

                {renderLocationEl()}

                {/* end of location block -------------------------------------   */}
                <Grid item xs={12} >
                    <C1CategoryBlock icon={<LocalShippingOutlinedIcon />} title={t("job:tripDetails.cargoAndTruckDetails")}>
                    </C1CategoryBlock>
                </Grid>

                <Grid item lg={4} md={6} xs={12} >
                    <C1CategoryBlock icon={<WidgetsOutlinedIcon />} title={t("job:tripDetails.containerDetails")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <C1SelectField
                                    name="tckMstCntType.cnttId"
                                    label={t("job:tripDetails.containerType")}
                                    value={getValue(cargoTripDetails?.tckMstCntType?.cnttId)}
                                    onChange={handleCargoInputChange}
                                    disabled={isDisabled}
                                    isServer={true}
                                    options={{
                                        url: CK_MST_CONTAINER_TYPE,
                                        id: "cnttId",
                                        desc: "cnttName",
                                        isCache: true
                                    }}
                                    error={errors['tckMstCntType.cnttId'] !== undefined}
                                    helperText={errors['tckMstCntType.cnttId'] || ''}
                                />
                                <C1InputField
                                    label={t("job:tripDetails.containerNumber")}
                                    name="cgCntNo"
                                    onChange={handleCargoInputChange}
                                    value={getValue(cargoTripDetails?.cgCntNo)}
                                    disabled={isDisabled}
                                    error={errors['cgCntNo'] !== undefined}
                                    helperText={errors['cgCntNo'] || ''}
                                />

                                <C1InputField
                                    label={t("job:tripDetails.sealNo")}
                                    value={getValue(cargoTripDetails?.cgCntSealNo)}
                                    name="cgCntSealNo"
                                    onChange={handleCargoInputChange}
                                    error={errors['cgCntSealNo'] !== undefined}
                                    helperText={errors['cgCntSealNo'] || ''}
                                    disabled={isDisabled}
                                />

                                <C1SelectField
                                    name="cgCntFullLoad"
                                    label={t("job:tripDetails.containerLoad")}
                                    value={getValue(cargoTripDetails?.cgCntFullLoad)}
                                    onChange={handleCargoInputChange}
                                    disabled={isDisabled}
                                    isServer={true}
                                    options={{
                                        url: CK_MST_CONTAINER_LOAD,
                                        key: "containerLoad",
                                        id: 'key',
                                        desc: 'value',
                                        isCache: true
                                    }}
                                    error={errors['cgCntFullLoad'] !== undefined}
                                    helperText={errors['cgCntFullLoad'] || ''}
                                />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <C1CategoryBlock icon={<BookOutlinedIcon />} title={t("job:tripDetails.goodsDetails")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >

                                <C1SelectField
                                    name="tckCtMstCargoType.crtypId"
                                    label={t("job:tripDetails.goodsType")}
                                    value={getValue(cargoTripDetails?.tckCtMstCargoType?.crtypId)}
                                    onChange={handleCargoInputChange}
                                    disabled={isDisabled}
                                    isServer={true}
                                    options={{
                                        url: CK_MST_GOODS_TYPE,
                                        key: "goodsType",
                                        id: 'crtypId',
                                        desc: 'crtypName',
                                        isCache: true
                                    }}
                                    error={errors['tckCtMstCargoType.crtypId'] !== undefined}
                                    helperText={errors['tckCtMstCargoType.crtypId'] || ''}
                                />
                                <C1TextArea
                                    label={t("job:tripDetails.description")}
                                    name="cgCargoDesc"
                                    disabled={isDisabled}
                                    value={getValue(cargoTripDetails?.cgCargoDesc)}
                                    multiline
                                    textLimit={1024}
                                    onChange={handleCargoInputChange}
                                    error={errors['cgCargoDesc'] !== undefined}
                                    helperText={errors['cgCargoDesc'] || ''}
                                />

                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <C1CategoryBlock icon={<LocalShippingOutlinedIcon />} title={t("job:tripDetails.trucksDetails")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >

                                <C1SelectField
                                    name="tckCtMstVehType.vhtyId"
                                    label={t("job:tripDetails.truckType")}
                                    value={getValue(inputData?.tckCtMstVehType?.vhtyId)}
                                    onChange={handleCargoInputChange}
                                    required
                                    disabled={!isOpenPrice || isDisabled}
                                    isServer={true}
                                    options={
                                        inputData?.tcoreAccnByJobPartyTo?.accnId ?
                                            {
                                                url: `/api/v1/clickargo/clictruck/vehicle/veh-type/${inputData?.tcoreAccnByJobPartyTo?.accnId}`,
                                                key: 'vhtyId',
                                                id: 'vhtyId',
                                                desc: 'vhtyName',
                                            } :
                                            {
                                                url: CK_MST_VEH_TYPE,
                                                key: "vhtyId",
                                                id: 'vhtyId',
                                                desc: 'vhtyDesc',
                                                isCache: true
                                            }
                                    }
                                    error={errors['tckCtMstVehType.vhtyId'] !== undefined}
                                    helperText={errors['tckCtMstVehType.vhtyId'] || ''}
                                />

                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                    <Grid item style={{ height: '39px' }}></Grid>
                    <C1CategoryBlock icon={<DescriptionIcon />} title={t("job:tripDetails.commentsAndInstruction")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <C1TextArea
                                    label={t("job:tripDetails.specialInstruction")}
                                    name="cgCargoSpecialInstn"
                                    multiline
                                    textLimit={1024}
                                    onChange={handleCargoInputChange}
                                    disabled={isDisabled}
                                    value={getValue(cargoTripDetails?.cgCargoSpecialInstn)}
                                    error={errors['cgCargoSpecialInstn'] !== undefined}
                                    helperText={errors['cgCargoSpecialInstn'] || ''}
                                />
                            </Grid>
                        </Grid>
                    </C1CategoryBlock>
                </Grid>


                {isArrayNotEmpty(inputData?.hiddenFields ) && inputData?.hiddenFields.includes("tripcharges") ? null : <><Grid item xs={12} >
                    <C1CategoryBlock icon={<LocalAtmOutlinedIcon />} title={t("job:tripDetails.chargesReimbursements")}>
                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={4} md={6} xs={12} >
                    <C1CategoryBlock icon={<SpeedOutlinedIcon />} title={t("job:tripDetails.tripCharges")}>
                        <Grid container alignItems="center" spacing={3}>
                            <Grid item xs={12} >
                                <C1InputField
                                    label={t("job:tripDetails.amount")}
                                    name="tckCtTripCharge.tcPrice"
                                    disabled={!isOpenPrice || isDisabled}
                                    onChange={handleInputChange}
                                    value={
                                        jobTripDetails?.tckCtTripCharge?.tcPrice ?
                                            jobTripDetails?.tckCtTripCharge?.tcPrice
                                            : 0
                                    }
                                    inputProps={{ style: { textAlign: 'right' } }}
                                    InputProps={{
                                        inputComponent: NumFormat,
                                        startAdornment:
                                            <InputAdornment position="start" style={{ paddingRight: "8px" }}>
                                                Rp
                                            </InputAdornment>
                                    }}
                                />
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            checked={isOpenPrice}
                                            disabled={isDisabled}
                                        />}
                                    label={t("job:tripDetails.openPrice")}
                                />

                            </Grid>
                        </Grid>
                    </C1CategoryBlock>

                </Grid>
                <Grid item lg={8} md={12} >
                    <Reimbursements
                        disabled={isReimbursementDisabled}
                        tripId={tripId}
                        viewType={viewType}
                        showAddButton={isToFinance && isDelivered}
                        showEditButton={isToFinance && isDelivered}
                        showDeleteButton={isToFinance && isDelivered}
                        chargeAmount={jobTripDetails?.tckCtTripCharge?.tcPrice}
                    />
                </Grid></>}


            </C1TabContainer>
        </Grid>
        { isOpenRatePopup && <RateTableListPopup
            open={isOpenRatePopup}
            handleClose={() => setIsOpenRatePopup(false)}
            handleSelected={handleSelectedRate}
            company={company}
            trTypeFilter={"S"} //Single trip type
        /> }
    </React.Fragment>
    );
});

export default JobTripCharges;