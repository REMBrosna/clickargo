import React from "react";

const orderTerminationRoutes = [
    {
        path: "/opadmin/order-termination/list",
        component: React.lazy(() => import("./OrderTermination")),
    },
    {
        path: "/opadmin/order-termination/:viewType/:terminationId",
        component: React.lazy(() => import("./OrderTerminationFormDetails")),
    },
]

export default orderTerminationRoutes;