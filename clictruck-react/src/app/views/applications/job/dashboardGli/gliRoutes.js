import React from "react";

const gliRoutes = [
    {
        path: "/applications/services/gli/dashboard",
        component: React.lazy(() => import("./DashboardPanel")),
    },
    {
        path: "/opadmin/docverification",
        component: React.lazy(() => import("./DashboardDocVerify"))
    },
]

export default gliRoutes