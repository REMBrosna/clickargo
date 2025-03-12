import React from "react";

const taxRoutes = [
    {
        path: "/administrations/tax-management/list",
        component: React.lazy(() => import("./DashboardPanel")),
    },
]

export default taxRoutes;