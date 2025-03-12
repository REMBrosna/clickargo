import React from "react";

const rentalManagementRoutes = [
    {
        path: "/administrations/rental-management/list",
        component: React.lazy(() => import("./RentalManagement")),
    },
    {
        path: "/administrations/rental-management/add",
        component: React.lazy(() => import("./RentalManagementFormDetails"))
    },
    {
        path: "/administrations/rental-management/:viewType/:jobId",
        component: React.lazy(() => import("./RentalManagementFormDetails"))
    },
]

export default rentalManagementRoutes;