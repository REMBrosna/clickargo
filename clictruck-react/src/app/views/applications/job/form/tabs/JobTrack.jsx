import React, { useContext, useEffect, useState } from "react";
import {
    Button,
    Grid, MenuItem,
} from "@material-ui/core";

import C1InputField from "app/c1component/C1InputField";
import { useStyles } from "app/c1utils/styles";
import { useTranslation } from "react-i18next";
import PlaceOutlinedIcon from '@material-ui/icons/PlaceOutlined';
import WorkOutlineOutlinedIcon from '@material-ui/icons/WorkOutlineOutlined';
import LocalShippingOutlinedIcon from '@material-ui/icons/LocalShippingOutlined';
import C1SelectField from "app/c1component/C1SelectField";
import C1DateTimeField from "app/c1component/C1DateTimeField";

import JobTruckContext from "../JobTruckContext";
import { JobStates } from "app/c1utils/const";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import TruckTraceFrame from "app/views/administrations/operations/trackTrace/TruckTraceFrame"
import ListIcon from "@material-ui/icons/List";
import TruckLocationHistory from "../../popups/TruckLocationHistory";
import C1PopUp from "../../../../../clictruckcomponent/JobPopUp";

const JobTrack = ({ }) => {

    const { t } = useTranslation(["job", "administration"]);

    const classes = useStyles();
    const [jsonBody, setJsonBody] = useState({});
    const [openAddPopUp, setOpenAddPopUp] = React.useState(false);

    const { inputData } = useContext(JobTruckContext);
    
    const getGpsArray = () => {
        let locArray = [];
        if (inputData?.tckCtTripList && inputData?.tckCtTripList?.length > 0) {
            /*
            if (inputData.tckCtTripList[0].tckCtTripLocationByTrTo?.tckCtLocation?.locGps) {
                locArray.push(inputData.tckCtTripList[0].tckCtTripLocationByTrTo?.tckCtLocation?.locGps)
            }
            if (inputData.tckCtTripList[0].tckCtTripLocationByTrFrom?.tckCtLocation?.locGps) {
                locArray.push(inputData.tckCtTripList[0].tckCtTripLocationByTrFrom?.tckCtLocation?.locGps)
            }
            if (inputData.tckCtTripList[0].tckCtTripLocationByTrDepot?.tckCtLocation?.locGps) {
                locArray.push(inputData.tckCtTripList[0].tckCtTripLocationByTrDepot?.tckCtLocation?.locGps)
            }*/
            if (inputData.tckCtTripList[0].tckCtTripLocationByTrTo?.tlocLocGps) {
                locArray.push(inputData.tckCtTripList[0].tckCtTripLocationByTrTo?.tlocLocGps)
            }
            if (inputData.tckCtTripList[0].tckCtTripLocationByTrFrom?.tlocLocGps) {
                locArray.push(inputData.tckCtTripList[0].tckCtTripLocationByTrFrom?.tlocLocGps)
            }
            if (inputData.tckCtTripList[0].tckCtTripLocationByTrDepot?.tlocLocGps) {
                locArray.push(inputData.tckCtTripList[0].tckCtTripLocationByTrDepot?.tlocLocGps)
            }
        }
        return locArray;
    }

    useEffect(() => {

        //const iframe = document.querySelector("iframe");

        let fromTime = (inputData?.tckJob?.tckRecordDate?.rcdDtStart || 0) / 1000;
        let endTime = (inputData?.tckJob?.tckRecordDate?.rcdDtComplete || inputData?.jobDtDelivery || 0) / 1000;

        let coordinates = "";
        let gpsArray = getGpsArray();

        if (gpsArray.length > 0) {
            coordinates = "[" + gpsArray.toString() + "]";
        }

        // sometimes vehcle doesn't have IMEI.
        let json = {
            fromTime, endTime, units: [inputData?.tckCtVeh?.vhGpsImei], coordinates, latest: 1, radius: "100", waitMapInit:"Y"
        };

        //setTimeout(() => console.log("json....", json), 2000);
        //setTimeout(() => iframe.contentWindow.postMessage(json, "*"), 2000);
        if (json.units && json.units?.length > 0 && json.units[0] && json.units[0]?.length > 0) {
            setJsonBody(json);
        }
        // deloy 1 second
        //setTimeout(() => setJsonBody(json), 1000);

        // iframe.contentWindow.postMessage(json, "*");

    }, [inputData?.tckCtVeh?.vhGpsImei]);

    // console.log("JsonBody:", jsonBody);

    return (
        <React.Fragment>
            <Grid container spacing={3} className={classes.gridContainer}>
                <Grid item lg={2} xs={12} >

                    <C1CategoryBlock
                        icon={<WorkOutlineOutlinedIcon />}
                        title={t("job:tracking.jobDetails")}>

                        <C1DateTimeField
                            label={t("job:tracking.start")}
                            name="startTime"
                            disabled={true}
                            required
                            value={inputData?.tckJob?.tckRecordDate?.rcdDtStart || ""}
                        />
                        {(inputData?.tckJob?.tckRecordDate?.rcdDtComplete || inputData?.jobDtDelivery)?<C1DateTimeField
                            label={t("job:tracking.end")}
                            name="endTime"
                            disabled={true}
                            value={inputData?.tckJob?.tckRecordDate?.rcdDtComplete || inputData?.jobDtDelivery || ""}
                        />:<C1InputField
                                label={t("job:tracking.end")}
                                name="endTime"
                                disabled={true}
                                value={"-"}
                            />}
                        {((!jsonBody.endTime) || (jsonBody.endTime == 0))}
                        <C1SelectField
                            label={t("job:tracking.status")}
                            name="status"
                            value={inputData?.tckJob?.tckMstJobState?.jbstId || ""}
                            disabled={true}
                            isServer={false}>
                            <MenuItem value={JobStates.ONGOING.code} key={JobStates.ONGOING.code}>  {JobStates.ONGOING.desc} </MenuItem>
                            <MenuItem value={JobStates.DLV.code} key={JobStates.DLV.code}>  {JobStates.DLV.desc} </MenuItem>
                            <MenuItem value={JobStates.BILLED.code} key={JobStates.BILLED.code}>  {JobStates.BILLED.desc} </MenuItem>
                            <MenuItem value={JobStates.VER.code} key={JobStates.VER.code}>  {JobStates.VER.desc} </MenuItem>
                            <MenuItem value={JobStates.APP.code} key={JobStates.APP.code}>  {JobStates.APP.desc} </MenuItem>
                        </C1SelectField>

                    </C1CategoryBlock>

                    <C1CategoryBlock
                        icon={<LocalShippingOutlinedIcon />}
                        title={t("job:tracking.drvDetails")}>
                        <C1InputField
                            label={t("job:tracking.trPlateNo")}
                            name="fullName"
                            disabled={true}
                            required
                            value={inputData?.tckCtVeh?.vhPlateNo || inputData?.jobVehOth?.vhPlateNo || ""}
                        />
                        <C1InputField
                            label={t("job:tracking.trType")}
                            name="vhtyName"
                            value={inputData?.tckCtMstVehType?.vhtyName}
                            disabled={true}
                            isServer={false}>
                        </C1InputField>
                        <C1InputField
                            label={t("job:tracking.driver")}
                            name="email"
                            disabled={true}
                            required
                            value={inputData?.tckCtDrv?.drvName || inputData?.jobDrvOth?.drvName || ""}
                        />
                        <C1InputField
                            label={t("job:tracking.phone")}
                            name="email"
                            disabled={true}
                            required
                            value={inputData?.tckCtDrv?.drvPhone || inputData?.jobDrvOth?.drvPhone || ""}
                        />

                    </C1CategoryBlock>
                </Grid>
                <Grid item lg={10} xs={12} >
                    <div style={{ marginBottom: 10 }}>
                        <C1CategoryBlock
                            icon={<PlaceOutlinedIcon />}
                            title={t("administration:liveTracking.location")}
                        />
                        <Button
                            style={{
                                position: "relative",
                                float: "right",
                                bottom: "40px"
                            }}
                            size="small"
                            variant="contained"
                            color='primary'
                            onClick={() => setOpenAddPopUp(true)}
                            startIcon={<ListIcon />}
                        >
                            Job History
                        </Button>
                    </div>
                    <TruckTraceFrame jsonBody={jsonBody} />
                </Grid>
            </Grid>
            <C1PopUp
                title={t("job:tripDetails.truckLocationHistory")}
                openPopUp={openAddPopUp}
                setOpenPopUp={setOpenAddPopUp}
                setSubmitButton={false}
                maxWidth={"lg"}
                maxHeight="500px"
            >
                <TruckLocationHistory
                    t={t}
                    jobId={inputData?.jobId}
                    trips={inputData?.tckCtTripList}
                    imei={inputData?.tckCtVeh?.vhGpsImei}
                    stat={inputData?.tckJob?.tckRecordDate?.rcdDtStart}
                    end={inputData?.tckJob?.tckRecordDate?.rcdDtComplete}
                    tripIds={inputData?.tckCtTripList?.map(val => val?.trId)}
                />
            </C1PopUp>
        </React.Fragment>
    );
};

export default JobTrack;