import React from "react";

const contractManagementRoutes = [
    {
        path: "/administrations/contract-management/list",
        component: React.lazy(() => import("./ContractManagement")),
    },
    {
        path: "/administrations/contract-management/:viewType/:contractId",
        component: React.lazy(() => import("./ContractManagementFormDetails"))
    },
]

export default contractManagementRoutes;