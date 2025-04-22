import React, { useState, useEffect } from 'react'

import useInterval from 'app/c1hooks/useInterval'
import useHttp from "app/c1hooks/http";
import cacheDataService from "app/services/cacheDataService";
//import axios from 'axiosBackend.js';
//import useAuth from "app/hooks/useAuth";
import axios from "axios.js";


export default function C1RefreshCachedData() {

    const [delay, setDelay] = useState(1000 * 60 * 5);
    const { isLoading, res, error, sendRequest } = useHttp();

    // const axiosInstance = axios.create({
    //     baseURL: process.env.REACT_APP_BACK_END_URL,
    // });

    //http://localhost:8080/copor/api/co/common/portalCacheTimeStamp/
    const url = "/api/co/common/portalCacheTimeStamp";

    useEffect(() => {
        // init data
        //sendRequest(url, '', 'get');
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useInterval(
        () => {
            // fetch server data
            sendRequest(url, '', 'get');
        },
        // Delay in milliseconds or null to stop it
        delay,
    );

    useEffect(() => {
        if (!isLoading && res) {
            // loop module and loop service
            let masterData = res.data.master;
            for (var key of Object.keys(masterData)) {
                //ignore port as it is quite big
                if (key !== 'port' && key !== 'hsCode') {
                    let cachedDataTimeStamp = cacheDataService.getItemTimeStamp(key);
                    //console.log(key + " : " + masterData[key] + " : " + cachedDataTimeStamp);
                    if (!cachedDataTimeStamp) {
                        refreshData(key);
                    } else if (cachedDataTimeStamp && (cachedDataTimeStamp < masterData[key])) {
                        refreshData(key);
                    }
                }

            }
            // console.log("cacheDataService.getAllItem()", cacheDataService.getAllItem());
        }
        // eslint-disable-next-line
    }, [res, isLoading, error])

    const getURL = (serviceName) => {
        if (serviceName.indexOf("pedi") === 0 || ["appFeeConfig"].includes(serviceName)) {
            return "/api/co/pedi/mst/entity/" + serviceName;
        }

        return "/api/co/master/entity/" + serviceName;
    }

    const refreshData = (serviceName) => {
        let url = getURL(serviceName);
        //sendRequest(url, 'get');
        axios({
            method: 'get',
            url: url,
            data: ''
        }).then(response => {
            cacheDataService.setItem(serviceName, response ? response.data : []);
        }).catch(error => {
            cacheDataService.setItem(serviceName);
        });
    }
    /*
        useEffect(() => {
            if (!isLoadingItem && resItem) {
                // loop module and loop service
            }
        }, [resItem, isLoadingItem, errorItem])
    */
    return (
        <div>
        </div>
    )
}
