import React from "react";

const shippingLineRoutes = [
    {
        path: "/applications/services/do",
        component: React.lazy(() => import("./verification/TasksFormVerification"))
    },
    {
        path: "/applications/payments/do",
        component: React.lazy(() => import("./doClaimJobs/doPaymentVerification/TasksFormVerification"))
    },
];

export default shippingLineRoutes;

