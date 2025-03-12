import React from "react";

const chassisManagementRoutes = [
    {
        path: "/administrations/chassis-management/list",
        component: React.lazy(() => import("./ChassisManagementList")),
    },
    {
        path: "/administrations/chassis-management/:viewType/:chsId",
        component: React.lazy(() => import("./ChassisManagementFormDetails"))
    },
]

export default chassisManagementRoutes;