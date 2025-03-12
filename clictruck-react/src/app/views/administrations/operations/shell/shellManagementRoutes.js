import React from "react";

const shellManagementRoutes = [
    {
        path: "/opadmin/shellcard",
        component: React.lazy(() => import("./ShellManagmentList")),
    },
]

export default shellManagementRoutes;