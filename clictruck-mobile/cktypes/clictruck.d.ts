export interface StatsType {
    id: string,
    title: string,
    count: number,
    isLoaded: boolean
}

export type StatsContextType = {
    newStats: StatsType;
    pauseStats: StatsType;
    reloadStats: (isReload: boolean) => void;
    isStatsCompleted: () => boolean;
}


