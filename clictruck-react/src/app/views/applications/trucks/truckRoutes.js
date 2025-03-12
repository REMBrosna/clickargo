import React from "react";

const truckRoutes = [
    {
        path: "/applications/services/truck/list",
        component: React.lazy(() => import("./TruckList"))
    },
    {
        path: "/applications/services/truck/job/:viewType/:jobId",
        component: React.lazy(() => import("./TruckFormDetails"))
    },
];

export default truckRoutes;

