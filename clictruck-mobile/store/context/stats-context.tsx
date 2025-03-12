import React, { useEffect, useState } from "react";
import { createContext } from "react";
import { StatsContextType, StatsType } from "../../cktypes/clictruck";
import { sendRequest } from "../../constants/util";


export const StatsContext = createContext<StatsContextType | null>(null);

interface Props {
    children: React.ReactNode;
}

const StatsProvider: React.FC<Props> = ({ children }) => {

    const [newStats, setNewStats] = useState<StatsType>({ id: "", title: "New", count: 0, isLoaded: false });
    const [pauseStats, setPauseStats] = useState<StatsType>({ id: "", title: "Paused", count: 0, isLoaded: false });

    async function getStatistics() {
        const result = await sendRequest(`${process.env.EXPO_PUBLIC_BACKEND_URL}/api/v1/clickargo/clictruck/dashboardMobile`);
        console.log("getStatistics", result);
        if (result) {
            //set for new stats
            result?.map((el: { dbType: string; title: string; count: number; }) => {
                if (el?.dbType === "MOBILE_NEW") {
                    setNewStats({
                        ...newStats,
                        id: el?.dbType,
                        title: el?.title,
                        count: el?.count,
                        isLoaded: true
                    });
                } else if (el?.dbType === "MOBILE_PAUSED") {
                    setPauseStats({
                        ...pauseStats,
                        id: el?.dbType,
                        title: el?.title,
                        count: el?.count,
                        isLoaded: true
                    })
                }
            });
        }
    }

    useEffect(() => {
        getStatistics();
    }, []);

    const reloadStats = (isReload: boolean) => {
        console.log("reloading statistics...");
        if (isReload)
            getStatistics();
    }

    const isStatsCompleted = () => {
        console.log("new & pause LoadeD? " + newStats?.isLoaded, pauseStats?.isLoaded)
        if (newStats?.isLoaded && pauseStats?.isLoaded)
            return true;
        return false;
    }


    return (
        <StatsContext.Provider value={{ newStats, pauseStats, reloadStats, isStatsCompleted }}>
            {children}
        </StatsContext.Provider>
    );

};

export default StatsProvider;