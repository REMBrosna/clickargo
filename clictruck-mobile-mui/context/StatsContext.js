import React, { useEffect, useReducer } from "react";
import { createContext } from "react";
import useAuth from "../hooks/useAuth.js";
import { sendRequest } from "../utils/httpUtil.js";


const initialState = {
    newStats: {
        id: "",
        title: "",
        count: 0,
        isLoaded: false
    },
    pauseStats: {
        id: "",
        title: "",
        count: 0,
        isLoaded: false
    }
}


const StatsContext = createContext({
    ...initialState,
    method: "JWT",
    isStatsReloaded: false,
    getStatistics: () => Promise.resolve(),
    reloadStats: () => Promise.resolve(),

});

const reducer = (state, action) => {
    switch (action.type) {
        case "INIT": {
            const { newStats, pauseStats } = action.payload;

            return {
                ...state,
                newStats,
                pauseStats,
                isLoaded: true,
            };
        }
        case "RELOAD": {
            const { newStats, pauseStats } = action.payload;

            return {
                ...state,
                ...(newStats ? { newStats } : {}), 
                ...(pauseStats ? { pauseStats } : {}), 
                isStatsReloaded: true,
            };
        }
        default: {
            return { ...state };
        }
    }
};


export const StatsProvider = ({ children }) => {

    const [state, dispatch] = useReducer(reducer, initialState);
    const { isAuthenticated } = useAuth();

    const getStatistics = async () => {
        await fetchStats(false);
    }

    const reloadStats = async () => {
        await fetchStats(true);

    };

    useEffect(() => {
        if (isAuthenticated)
            fetchStats(false);
    }, [isAuthenticated]);



    const fetchStats = async (isReload) => {

        const result = await sendRequest(`/api/v1/clickargo/clictruck/dashboardMobile`);
        // console.log("getStatistics", result);
        if (result) {
            //set for new stats
            let newStats = {};
            let pauseStats = {};
            result?.map((el) => {
                if (el?.dbType === "MOBILE_NEW") {
                    newStats = {
                        id: el?.dbType,
                        title: el?.title,
                        count: el?.count,
                        isLoaded: true
                    };
                } else if (el?.dbType === "MOBILE_PAUSED") {
                    pauseStats = {
                        id: el?.dbType,
                        title: el?.title,
                        count: el?.count,
                        isLoaded: true
                    };
                }
            });

            dispatch({
                type: isReload ? "RELOAD" : "INIT",
                payload: {
                    newStats,
                    pauseStats
                },
            });
        }


    };

    return (
        <StatsContext.Provider value={{ ...state, getStatistics, reloadStats, dispatch }}>
            {children}
        </StatsContext.Provider>
    );

};

export default StatsContext;
