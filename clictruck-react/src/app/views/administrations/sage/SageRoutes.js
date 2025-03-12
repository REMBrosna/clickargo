import React from "react";

const SageRoutes = [
    {
        path: "/manage/sage/excel",
        component: React.lazy(() => import("./sageReport/SageList")),
    },
    {
        path: "/manage/sage/tax",
        // component: React.lazy(() => import("./sageReport/SageList")),
        component: React.lazy(() => import("./../operations/tax/DashboardPanel")),
    },
    {
        path: "/opadmin/sageintegrations",
        component: React.lazy(() => import("./SageIntegrations"))
    },
]

export default SageRoutes;