import React, { useEffect, useState } from "react";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import { Grid, Paper, Typography, makeStyles } from "@material-ui/core";
import LocalShippingOutlinedIcon from "@material-ui/icons/LocalShippingOutlined";
import { useTranslation } from "react-i18next";
import C1CategoryBlock from "app/c1component/C1CategoryBlock";
import useHttp from "app/c1hooks/http";
import { JobStates, ShipmentTypes } from "app/c1utils/const";
import { encryptText, formatDate } from "app/c1utils/utility";
import useAuth from "app/hooks/useAuth";
import { useStyles } from "app/c1utils/styles";
import WorkOutlineOutlinedIcon from "@material-ui/icons/WorkOutlineOutlined";
import PlaceOutlinedIcon from "@material-ui/icons/PlaceOutlined";
import TruckTraceFrame from "app/views/administrations/operations/trackTrace/TruckTraceFrame";
import PlayCircleFilledWhiteOutlinedIcon from "@material-ui/icons/PlayCircleFilledWhiteOutlined";
import PinDropOutlinedIcon from "@material-ui/icons/PinDropOutlined";
import HomeWorkOutlinedIcon from "@material-ui/icons/HomeWorkOutlined";
import MoreHorizOutlinedIcon from '@material-ui/icons/MoreHorizOutlined';
import AccountTreeOutlinedIcon from '@material-ui/icons/AccountTreeOutlined';
import {
  Timeline,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineItem,
  TimelineOppositeContent,
  TimelineSeparator,
} from "@material-ui/lab";
import StatusTrain from "../../../../clictruckcomponent/StatusTrain";
/**
 *
 * this popup for popup multidrop location,
 * preparing this component if all the location remove from dashboard
 */
const JobTrackPopup = ({ jobId, tripIds=[], trips=[] }) => {
  const { t } = useTranslation(["job", "administration"]);
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const { user } = useAuth();
  const classes = useStyles();
  const localClasses = localStyles();

  const [jsonBody, setJsonBody] = useState({});

  const [inputData, setInputData] = useState({});
  const [tripList, setTripList] = useState([]);

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
        locArray.push(
          inputData.tckCtTripList[0].tckCtTripLocationByTrTo?.tlocLocGps
        );
      }
      if (inputData.tckCtTripList[0].tckCtTripLocationByTrFrom?.tlocLocGps) {
        locArray.push(
          inputData.tckCtTripList[0].tckCtTripLocationByTrFrom?.tlocLocGps
        );
      }
      if (inputData.tckCtTripList[0].tckCtTripLocationByTrDepot?.tlocLocGps) {
        locArray.push(
          inputData.tckCtTripList[0].tckCtTripLocationByTrDepot?.tlocLocGps
        );
      }
    }
    return locArray;
  };

  useEffect(() => {
    let encryptedJobId = encryptText(jobId, user?.coreAccn?.accnId, user?.id);
    sendRequest(
      `/api/v1/clickargo/clictruck/job/truck/` + encryptedJobId,
      "getJob",
      "GET",
      null
    );
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res) {
      if (urlId === "getJob") {
        console.log("resd?data", res?.data);
        setInputData({ ...inputData, ...res?.data });
        setTripList([...tripList, ...res?.data?.tckCtTripList]);
      }
    }
  }, [urlId, isLoading, res, error]);

  useEffect(() => {
    //const iframe = document.querySelector("iframe");

    let fromTime = (inputData?.tckJob?.tckRecordDate?.rcdDtStart || 0) / 1000;

    let coordinates = "";
    let gpsArray = getGpsArray();

    if (gpsArray.length > 0) {
      coordinates = "[" + gpsArray.toString() + "]";
    }

    // sometimes vehcle doesn't have IMEI.
    let json = {
      fromTime,
      endTime: "0",
      units: [inputData?.tckCtVeh?.vhGpsImei],
      coordinates,
      latest: 1,
      radius: "100",
      waitMapInit: "Y"
    };
    console.log("json....", json);

    //setTimeout(() => console.log("json....", json), 2000);
    //setTimeout(() => iframe.contentWindow.postMessage(json, "*"), 2000);
    if (
      json.units &&
      json.units?.length > 0 &&
      json.units[0] &&
      json.units[0]?.length > 0
    ) {
      setJsonBody(json);
    }
    // deloy 1 second
    //setTimeout(() => setJsonBody(json), 1000);

    // iframe.contentWindow.postMessage(json, "*");
  }, [inputData?.tckCtVeh?.vhGpsImei]);

  const generateTimeline = (el) => {
    let shipmentType = inputData?.tckJob?.tckMstShipmentType?.shtId;

    return (
      <Timeline align="alternate">
        <TimelineItem key={el.trId}>
          <TimelineOppositeContent>
            <Typography variant="body2" color="textSecondary">
              {formatDate(inputData?.tckJob?.tckRecordDate?.rcdDtStart, true)}
            </Typography>
          </TimelineOppositeContent>
          <TimelineSeparator>
            <TimelineDot color="primary">
              {shipmentType === ShipmentTypes.EXPORT.code ?  <HomeWorkOutlinedIcon />  : <PlayCircleFilledWhiteOutlinedIcon />}
            </TimelineDot>
            <TimelineConnector />
          </TimelineSeparator>
          <TimelineContent>
            <Paper elevation={3} className={localClasses.paper}>
              <Typography>
                {shipmentType === ShipmentTypes.EXPORT.code 
                    ? el?.tckCtTripLocationByTrDepot?.tlocLocAddress 
                    : el?.tckCtTripLocationByTrFrom?.tlocLocAddress
                }
              </Typography>
            </Paper>
          </TimelineContent>
        </TimelineItem>
        <TimelineItem>
          <TimelineSeparator>
            <TimelineDot style={{backgroundColor: "#3ad16d"}}>
                {shipmentType !== ShipmentTypes.DOMESTIC.code ? <LocalShippingOutlinedIcon /> :<PinDropOutlinedIcon />} 
            </TimelineDot>
            {shipmentType !== ShipmentTypes.DOMESTIC.code && <TimelineConnector />}
          </TimelineSeparator>
          <TimelineContent>
            <Paper elevation={3} className={localClasses.paper}>
              <Typography>
                {shipmentType === ShipmentTypes.EXPORT.code 
                    ? el?.tckCtTripLocationByTrFrom?.tlocLocAddress 
                    : shipmentType === ShipmentTypes.IMPORT.code  ? el?.tckCtTripLocationByTrTo?.tlocLocAddress : el?.tckCtTripLocationByTrFrom?.tlocLocAddress
                }
              </Typography>
            </Paper>
          </TimelineContent>
        </TimelineItem>
        {shipmentType === ShipmentTypes.EXPORT.code || shipmentType === ShipmentTypes.IMPORT.code ? <TimelineItem>
          <TimelineOppositeContent>
            <Typography variant="body2" color="textSecondary">
              {formatDate(
                inputData?.tckJob?.tckRecordDate?.rcdDtComplete,
                true
              )}
            </Typography>
          </TimelineOppositeContent>
          <TimelineSeparator>
            <TimelineDot color="secondary">
            {shipmentType === ShipmentTypes.IMPORT.code ?  <HomeWorkOutlinedIcon />  : <PinDropOutlinedIcon />}
            </TimelineDot>
          </TimelineSeparator>
          <TimelineContent>
            <TimelineContent>
              <Paper elevation={3} className={localClasses.paper}>
                <Typography>
                    {shipmentType === ShipmentTypes.EXPORT.code 
                        ? el?.tckCtTripLocationByTrTo?.tlocLocAddress 
                        : el?.tckCtTripLocationByTrDepot?.tlocLocAddress
                    }
                </Typography>
              </Paper>
            </TimelineContent>
          </TimelineContent>
        </TimelineItem> : null}
      </Timeline>
    );
  };

  return (
    <Grid container spacing={3} className={classes.gridContainer}>
      <Grid item lg={5} xs={12}>
        <C1CategoryBlock
          icon={<WorkOutlineOutlinedIcon />}
          title={t("job:tracking.jobDetails")}
        >
          <Grid
            container
            direction="column"
            style={{
              backgroundColor: "#e7f4fd",
              marginTop: "10px",
              marginLeft: "5px",
              borderRadius: "5px",
              paddingTop: 5,
              width: "100%",
              height: "100%",
              overflow: "auto"
            }}
            justifyContent="flex-start"
            alignItems="flex-start"
            spacing={4}
          >
            <Grid container item>
              <Grid item xs={12}>
                <Typography variant="body1" style={{ fontWeight: 800 }}>
                  {inputData?.tckCtVeh?.vhPlateNo ||
                    inputData?.jobVehOth?.vhPlateNo ||
                    ""}
                </Typography>
                <p>
                  <b>Job No.: </b>
                  {jobId}
                </p>
                <p>
                  <b>Status: </b>
                  {JobStates[inputData?.tckJob?.tckMstJobState?.jbstId]?.desc}
                </p>
                <p>
                  <b>Shipment Ref: </b>
                  {inputData?.jobShipmentRef || ""}
                </p>
                <p>
                  <b>Customer Ref: </b>
                  {inputData?.jobCustomerRef || ""}
                </p>
              </Grid>
            </Grid>

            <Grid container item>
              <Grid item xs={12}>
                {tripList && tripList?.length > 0
                  ? tripList?.map((el) => {
                      return generateTimeline(el);
                    })
                  : null} 
              </Grid>
            </Grid>
          </Grid>
        </C1CategoryBlock>
     </Grid>
      <Grid item lg={7} xs={12}>
        <C1CategoryBlock
          icon={<PlaceOutlinedIcon />}
          title={t("administration:liveTracking.location")}
        />
        <div style={{ marginBottom: 10 }}></div>
        <TruckTraceFrame jsonBody={{waitMapInit: "Y", ...jsonBody}} width="100%" height="92%" />
      </Grid>
      <Grid item xs={12}>
          <C1CategoryBlock
              icon={<AccountTreeOutlinedIcon />}
              title={t("job:tracking.jobStatusTrain")}
          >
            <Grid
                container
                direction="column"
                style={{
                  backgroundColor: "#e7f4fd",
                  marginTop: "10px",
                  marginLeft: "5px",
                  borderRadius: "5px",
                  paddingTop: 5,
                  width: "100%",
                  height: "100%",
                  overflow: "auto"
                }}
                justifyContent="flex-start"
                alignItems="flex-start"
                spacing={4}
            >
              <Grid container item>
                <Grid item xs={12}>
                  <StatusTrain
                      trips={trips}
                      jobId={jobId}
                      tripIds={tripIds}
                  />
                </Grid>
              </Grid>
            </Grid>
          </C1CategoryBlock>
        </Grid>
    </Grid>
  );
};

export default withErrorHandler(JobTrackPopup);

const localStyles = makeStyles((theme) => ({
  paper: {
    padding: "6px 6px",
  },
  secondaryTail: {
    backgroundColor: theme.palette.secondary.main,
  },
}));
