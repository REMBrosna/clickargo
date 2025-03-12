import React from "react";

const generalRoutes = [
    {
        path: "/general/landing/workbench",
        component: React.lazy(() => import("./Home"))
    },
    {
        path: "/payments",
        component: React.lazy(() => import("./PaymentsList"))
    },
    {
        path: "/help",
        component: React.lazy(() => import("./Help"))
    },

];

export default generalRoutes;
