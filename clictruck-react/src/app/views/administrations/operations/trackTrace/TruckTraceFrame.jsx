import React, { useEffect, useState, useRef } from "react";
import { Button, Dialog } from "@material-ui/core";
import useHttp from "app/c1hooks/http";
import { COMMON_SYSPARAM_URL } from "app/c1utils/const";
import axios from 'axios.js';

const TruckTraceFrame = ({ jsonBody, width="100%", height="95%" }) => {
    //const [ascentMapURL, setAscentMapURL] = useState("https://ext.logistics.myascents.net/customised/map?key=$2a$11$oo7.I606K/DVjUdZi88UzeFu.tLQiwTChnty4KATKbf3Dq0T8s2Ii");
    const [ascentMapURL, setAscentMapURL] = useState(sessionStorage.globalMapURL || "");
    const [openWarning, setOpenWarning] = useState(false);
    const [warningMessage, setWarningMessage] = useState("The truck has no tracking device installed on it");
    const [routeStartTime, setRouteStartTime] = useState((new Date().getTime() / 1000) - 60*60); // 1 hour

    const { sendRequest, res, urlId, isLoading, error } = useHttp();
    const loadMapTime = useRef(-1);

    const isBlankArray = (array) => {
        if (!array || array.length == 0) {
            return true;
        }
        for (let i = 0; i < array.length; i++) {
            if (array[i] && array[i].length > 2) {
                // not blank
                return false;
            }
        }
        return true;
    };


    useEffect(() => {
        console.log("sessionStorage.globalMapURL", sessionStorage.globalMapURL);

        if (!sessionStorage.globalMapURL) {
            sendRequest(
                `${COMMON_SYSPARAM_URL}/CLICKTRUCK_MAP_TRACE_URL`,
                "getSysParam",
                "get",
                {}
            );
        } else {
            setAscentMapURL(sessionStorage.globalMapURL);
            loadMapTime.current = new Date().getTime;
        }
    }, []);

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "getSysParam":
                    sessionStorage.globalMapURL = res?.data?.sysVal;
                    setAscentMapURL(res?.data?.sysVal);
                    loadMapTime.current = new Date().getTime;
                    break;
                default:
                    break;
            }
        }
        // eslint-disable-next-line
    }, [urlId, isLoading, res, error]);

    useEffect(() => {

        //console.log("ascentMapURL :", ascentMapURL);
        console.log("jsonBody outer :", jsonBody);

        //if (jsonBody && ascentMapURL?.length > 0 && jsonBody?.units?.length > 0) {
        if (jsonBody && ascentMapURL?.length > 0 ) {
            
            //let jsonBodyTruncTime = {...jsonBody, "fromTime":Math.trunc(jsonBody.fromTime), "endTime":Math.trunc(jsonBody.endTime), "radius":100}

            console.log("jsonBody in :", jsonBody);

            // if no IMEI
            if ( isBlankArray(jsonBody.units) && jsonBody.alertIfNoImei) {
            //if (false) {
                setOpenWarning(true);
            } else {
                //console.log("interval time:", (new Date().getTime - loadMapTime.current));
                //if ( (new Date().getTime - loadMapTime.current) > 2 * 1000) {
                // when render, waitMapInit is Y
                console.log("waitMapInit:", ("Y" === jsonBody.waitMapInit), new Date());
                if( "Y" === jsonBody.waitMapInit) {
                    // Tracking, Need to wait Map initial
                    setTimeout(
                        //() => iframe.contentWindow.postMessage(jsonBody, "*")
                        () => postMessage2Map(jsonBody),
                        2000
                    );
                } else {
                    //Live, historic
                    // Map already initial.
                    //iframe.contentWindow.postMessage(jsonBody, "*");
                    postMessage2Map(jsonBody);
                }
            }
        }

        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [jsonBody, ascentMapURL]);

    const postMessage2Map = (mapParam) => {

        delete mapParam['waitMapInit'];
        delete mapParam['reset'];
        if(mapParam.units && mapParam.units.length > 1) {
            // set to 0 if multi Trucks.
            mapParam = {...mapParam, "fromTime":0, "endTime":0, "radius":100}
        } else {
            // 2 hours
            // default endTime is 0
            mapParam = {...mapParam, "fromTime":Math.trunc(mapParam.fromTime || routeStartTime), "endTime":Math.trunc(mapParam.endTime || 0), "radius":100}
        } 
        const iframe = document.querySelector("iframe");
        console.log("now: ", new Date());
        if(iframe) {
            iframe.contentWindow.postMessage(mapParam, "*")
        }
    }

    return (
        // https://jira.vcargocloud.com/browse/CT2SG-132, change height to 95%
        <React.Fragment>
            <iframe
                name="traceFrame"
                title="traceFrame"
                width={width}
                height={height}
                src={ascentMapURL}
            >
                Your browser does not support inline frames.
            </iframe>

            <Dialog maxWidth="xs" open={openWarning}>
                <div className="p-8 text-center w-360 mx-auto">
                    <h4 className="capitalize m-0 mb-2">{"Warning"}</h4>
                    <p>{warningMessage}</p>
                    <div className="flex justify-center pt-2 m--2">
                        <Button
                            className="m-2 rounded hover-bg-primary px-6"
                            variant="outlined"
                            color="primary"
                            onClick={(e) => setOpenWarning(false)}
                        >
                            OK
                        </Button>
                    </div>
                </div>
            </Dialog>
        </React.Fragment>
    );
};

export default TruckTraceFrame;
