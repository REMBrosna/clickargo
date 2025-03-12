import React from "react";

const financeRoutes = [
    {
        path: "/applications/finance/payments/details",
        component: React.lazy(() => import("./details/PaymentDetails"))
    },
    // {
    //     path: "/applications/finance/verification/dashboard",
    //     component: React.lazy(() => import("./verification/dashboard/DashboardPanel")),
    // },

    {
        path: "/applications/finance/payments/transactions/:viewType/:txnId",
        component: React.lazy(() => import("./details/PaymentDetails")),
    },

];

export default financeRoutes;
