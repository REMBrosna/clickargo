import React from "react";

const coffFinanceRoutes = [
    {
        path: "/applications/services/coff/finance/dashboard",
        component: React.lazy(() => import("./DashboardPanel")),
    },
];

export default coffFinanceRoutes;
