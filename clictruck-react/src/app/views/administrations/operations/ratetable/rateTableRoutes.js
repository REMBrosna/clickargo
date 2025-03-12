import React from "react";

const rateTableManagementRoutes = [
    {
        path: "/administrations/rateTable-management/list",
        component: React.lazy(() => import("../ratetable/RateTableList"))
    },
    {
        path: "/administrations/rateTable-management/:viewType/:id",
        component: React.lazy(() => import("../ratetable/RateTableFormDetails")),
    },
    {
        path: "/manage/ratetable",
        component: React.lazy(() => import("../ratetable/RateTableListSp"))
    },
    {
        path: "/manage/ratetable-details/:viewType/:id",
        component: React.lazy(() => import("./RateTableFormDetailsSp")),
    },
]

export default rateTableManagementRoutes;

