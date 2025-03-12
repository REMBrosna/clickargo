import React from "react";

const shellCardInvRoutes = [
    {
        path: "/administrations/shellInvoicing",
        component: React.lazy(() => import("./ShellCardInvoicingPanel")),
    },
]

export default shellCardInvRoutes;