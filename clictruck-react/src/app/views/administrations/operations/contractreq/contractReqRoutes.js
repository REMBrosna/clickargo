import React from "react";

export const contractReqRoutes = [
    {
        path: "/opadmin/contractrequest/list",
        component: React.lazy(() => import("./ContractRequestList")),
    },
    {
        path: "/opadmin/contractrequest/:viewType/:contractReqId",
        component: React.lazy(() => import("./ContractReqFormDetails"))
    },

]
