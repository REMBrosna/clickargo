import React from "react";

const locationManagementRoutes = [
    {
        path: "/administrations/location-management/list",
        component: React.lazy(() => import("./LocationManagement")),
    },
    {
        path: "/administrations/location-management/add",
        component: React.lazy(() => import("./LocationManagementFormDetails"))
    },
    {
        path: "/administrations/location-management/:viewType/:locId",
        component: React.lazy(() => import("./LocationManagementFormDetails"))
    },
]

export default locationManagementRoutes;