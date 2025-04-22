import { useEffect, useState } from "react";
import useHttp from "app/c1hooks/http";
import cacheDataService from "app/services/cacheDataService";

export function useFetchDropdownData(url, key, id, desc, isCache = true, isServer = false) {

    const [dataList, setDataList] = useState([]);
    const { isLoading, res, error, sendRequest } = useHttp();

    useEffect(() => {
        //returning empty params
        if (!url) return;
        if (!id) return;
        if (!desc) return;

        if (key && isCache) {
            let cachedData = cacheDataService.getItemData(key);
            if (cachedData) {
                let dataArr = [];
                for (let i = 0; i < cachedData.length; i++) {
                    let jsonObj = cachedData[i];

                    if (id.indexOf('.') !== -1 || desc.indexOf('.') !== -1) {
                        dataArr.push({
                            value: getValue(jsonObj, id),
                            desc: getValue(jsonObj, desc)
                        });
                    } else {
                        if (jsonObj[id] && jsonObj[desc]) {
                            dataArr.push({
                                value: jsonObj[id],
                                desc: jsonObj[desc]
                            });
                        } else {
                            //use value / desc
                            dataArr.push({
                                value: jsonObj['value'],
                                desc: jsonObj['desc']
                            });
                        }

                    }
                }

                return setDataList(dataArr);
            }
        }

        if (isServer) {
            // console.log("Retrieving from API since nothing from cache.", url);
            if (url)
                sendRequest(url, 'get');
            else {
                setDataList([]);
            }

        }

    }, [url, key, id, desc, sendRequest, isCache, isServer]);

    useEffect(() => {
        if (!id) return;
        if (!desc) return;

        if (!isLoading && res) {
            let dataArr = [];
            //did a checking below as some API returns aaData instead of data directly
            let objArr = res.data && res.data.aaData ? res.data.aaData : res.data;
            for (let i = 0; i < objArr.length; i++) {
                let jsonObj = objArr[i];

                //if id has .
                if (id.indexOf('.') !== -1 || desc.indexOf('.') !== -1) {
                    dataArr.push({
                        value: getValue(jsonObj, id),
                        desc: getValue(jsonObj, desc)
                    });
                } else {
                    dataArr.push({
                        value: jsonObj[id],
                        desc: jsonObj[desc]
                    });
                }
            }

            if (isCache) {
                cacheDataService.setItem(key, dataArr)
            }

            setDataList(dataArr);
        }
        // eslint-disable-next-line
    }, [res, isLoading, error])

    if (!url)
        return [];
    return dataList;
}

function getValue(obj, selector) {
    if (selector.indexOf('.') !== -1) {
        let sel = selector.split('.');
        let val = getValueHelper(obj, sel);

        return val;
    }
    return obj[selector];
}

function getValueHelper(obj, fieldSelector) {
    if (fieldSelector.length > 1) {
        let field = fieldSelector.shift();
        let subObj;

        try {
            subObj = getValueHelper(obj[field], fieldSelector);
        } catch {
            subObj = getValueHelper(obj[field], fieldSelector);
        }

        return subObj;
    } else {
        return obj[fieldSelector.shift()];
    }
}

// const DEF_CACHE_CLEANUP_WEEKS = 1000 * 60 * 60 * 24 * 7;
// const currentTime = () => {
//     return Date.now()
// }

// const getApiCache = (cacheKey) => {
//     console.log("Getting cache for ", cacheKey);
//     let cache = {
//         data: null,
//         nextCleanup: new Date().getTime() + DEF_CACHE_CLEANUP_WEEKS
//     }

//     try {
//         const data = localStorage.getItem(cacheKey)
//         if (data && data.length > 0) {
//             cache = JSON.parse(data)
//         }
//     }
//     catch (e) {
//         console.error(e.message)
//     }
//     return cache;
// }

// const setApiCache = (cacheKey, value) => {
//     const item = {
//         id: cacheKey,
//         expiry: new Date().getTime() + DEF_CACHE_CLEANUP_WEEKS,
//         data: value
//     }

//     try {
//         localStorage.setItem(cacheKey, JSON.stringify(item))
//     } catch (e) {
//         cleanUpStorage(cacheKey, item)
//     }
// }

// const cleanUpStorage = (cacheKey, data) => {
//     let isDeleted;
//     let oldest;
//     let oldestKey;


//     //if 14 days have been passed, it removes the cache
//     for (const key in data) {
//         const expiry = data[key].expiry
//         if (expiry && expiry <= currentTime()) {
//             delete data[key]
//             isDeleted = true
//         }

//         //finding the oldest cache in case none of them are expired
//         if (!oldest || oldest > expiry) {
//             oldest = expiry
//             oldestKey = key
//         }
//     }

//     //remove the oldest cache if there is no more space in local storage (5 MB)
//     if (!isDeleted && oldestKey) {
//         delete data[oldestKey]
//     }

//     localStorage.setItem(
//         cacheKey,
//         JSON.stringify({
//             data: data,
//             nextCleanup: currentTime() + DEF_CACHE_CLEANUP_WEEKS,
//         })
//     )
// }








