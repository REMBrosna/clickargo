import React from "react";

const truckManagementRoutes = [
    {
        path: "/administrations/truck-management/list",
        component: React.lazy(() => import("./TruckManagement")),
    },
    {
        path: "/administrations/truck-management/add",
        component: React.lazy(() => import("./TruckManagementFormDetails"))
    },
    {
        path: "/administrations/truck-management/:viewType/:truckId",
        component: React.lazy(() => import("./TruckManagementFormDetails"))
    },
]

export default truckManagementRoutes;