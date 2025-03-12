import React, {useEffect, useState} from "react";
import {
    Timeline,
    TimelineConnector, TimelineContent,
    TimelineDot,
    TimelineItem,
    TimelineOppositeContent,
    TimelineSeparator
} from "@material-ui/lab";
import {Paper, Typography} from "@material-ui/core";
import {encryptText, formatDate} from "../c1utils/utility";
import AssignmentTurnedInIcon from '@material-ui/icons/AssignmentTurnedIn';
import EmojiTransportationIcon from '@material-ui/icons/EmojiTransportation';
import PlayCircleOutlineIcon from '@material-ui/icons/PlayCircleOutline';
import TimerIcon from '@material-ui/icons/Timer';
import LocalShippingIcon from '@material-ui/icons/LocalShipping';
import PinDropIcon from '@material-ui/icons/PinDrop';
import DoneAllIcon from '@material-ui/icons/DoneAll';
import useHttp from "../c1hooks/http";

const StatusTrain = (props) => {

    const { jobId, tripIds, trips } = props;
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [auditList, setAuditList] = useState([]);

    useEffect(() => {
        sendRequest(
            `/api/v1/clickargo/clictruck/job/truck/job/audit/${jobId}?params=${encodeURIComponent(JSON.stringify(tripIds))}`,
            "GET_AUDIT_LOGS",
            "GET",
            null
        );
    }, []);

    useEffect(() => {
        if (!isLoading && !error && res) {
           switch (urlId) {
               case "GET_AUDIT_LOGS":
                   setAuditList(res?.data)
                   break
               default:
                   break
           }
        }
    }, [urlId, isLoading, res, error]);

    const renderStatus = (value, ind, isLast) => {

        let Icon = AssignmentTurnedInIcon ;
        let status = "";
        let color = "";
        let location = "";

        switch (value?.audtEvent) {
            case "JOB ASSIGNED":
                color = "#2874a6"
                status = "Job Assigned"
                break
            case "JOB STARTED":
                color = "#2ecc71"
                Icon = PlayCircleOutlineIcon
                status = "Started"
                break
            case "SUBMITTED PICK UP CARGO": //pickup
                color = "#2ecc71"
                Icon = EmojiTransportationIcon
                status = "Cargo Pickup"
                location = trips[0]?.tckCtTripLocationByTrFrom?.tlocLocAddress
                break
            case "PAUSE":
                Icon = TimerIcon
                color = "#e74c3c"
                status = "Paused"
                break
            case "REDO PICK UP CARGO":// resume
                color = "#3498db"
                Icon = EmojiTransportationIcon
                status = "Resumed"
                break
            case "DELIVER CARGO":
                color = "#3498db"
                Icon = LocalShippingIcon;
                status = "Ongoing"
                break
            case "CONFIRMED DROP OFF CARGO":
                const trip = trips?.find(val => val?.trId === value?.audtReckey)
                color = "#3498db"
                Icon = PinDropIcon
                status = "Cargo Dropped off"
                location = trip?.tckCtTripLocationByTrTo?.tlocLocAddress
                break
            case "JOB DELIVERED":
                color = "#2ecc71"
                Icon = DoneAllIcon
                status = "Delivered (Completed)"
                break
            default:
                status = "Job Assigned"
                Icon = AssignmentTurnedInIcon
                break
        }

        return (
            <>
                <TimelineItem key={ind}>
                    <TimelineOppositeContent>
                        <Typography variant="body2" color="textSecondary">
                            {formatDate(value?.audtTimestamp, true)}
                        </Typography>
                    </TimelineOppositeContent>
                    <TimelineSeparator>
                        <TimelineDot color="inherit">
                            <Icon style={{fontSize: "1.2rem", color: color}}/>
                        </TimelineDot>
                        {isLast && (<TimelineConnector />)}
                </TimelineSeparator>
                    <TimelineContent>
                        <Paper elevation={3} style={{ padding: "8px" }}>
                            <Typography>
                                <Typography component="span" style={{ fontWeight: "bold" }}>
                                    {status}
                                </Typography>
                                <br />
                                {location}
                            </Typography>
                        </Paper>
                    </TimelineContent>
                </TimelineItem>
            </>
        )
    }

    const sortedTrips = [...auditList].sort((a, b) => {
        if (a.audtEvent === "JOB DELIVERED") return 1;
        if (b.audtEvent === "JOB DELIVERED") return -1;
        return 0;
    });

    return (
        <Timeline align="alternate">
            {sortedTrips?.map((value, ind) =>
                ["JOB ASSIGNED", "JOB STARTED", "SUBMITTED PICK UP CARGO", "PAUSE", "REDO PICK UP CARGO", "DELIVER CARGO","CONFIRMED DROP OFF CARGO","JOB DELIVERED"].includes(value?.audtEvent) &&
                renderStatus(value, ind,  ind !== auditList.length - 1))}
        </Timeline>
    )
}

export default StatusTrain;