import React from "react";

const trucksLeasingRoutes = [
    {
        path: "/opadmin/truckleasing/list",
        component: React.lazy(() => import("./LeasingApplicationList"))
    },
]

export default trucksLeasingRoutes;