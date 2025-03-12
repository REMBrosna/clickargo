import React from "react";

const shellPaymentsRoutes = [
    {
        path: "/shell/payments",
        component: React.lazy(() => import("./ShellInvoicePaymentsList")),
    },
]

export default shellPaymentsRoutes;