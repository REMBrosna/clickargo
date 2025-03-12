import React from "react";

const driverManagementRoutes = [
    {
        path: "/administrations/driver-management/list",
        component: React.lazy(() => import("./DriverManagement")),
    },
    {
        path: "/administrations/driver-management/add",
        component: React.lazy(() => import("./DriverManagementFormDetails"))
    },
    {
        path: "/administrations/driver-management/:viewType/:driverId",
        component: React.lazy(() => import("./DriverManagementFormDetails"))
    },
]

export default driverManagementRoutes;