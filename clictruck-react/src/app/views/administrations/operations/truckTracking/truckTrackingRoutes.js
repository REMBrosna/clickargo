import React from "react";

const truckTrackingRoutes = [
    {
        path: "/applications/service/truckTracking",
        component: React.lazy(()=> import("./TrucksTrackingPanel"))
    }
];

export default truckTrackingRoutes;
