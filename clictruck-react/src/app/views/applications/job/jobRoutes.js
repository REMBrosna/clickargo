import React from "react";

const jobRoutes = [
    {
        path: "/applications/services/job/coff/truck",
        component: React.lazy(() => import("./dashboard/DashboardPanel")),
    },
    {
        path: "/applications/services/job/ffco/truck",
        component: React.lazy(() => import("./dashboardFfCo/DashboardPanel")),
    },
    {
        path: "/applications/services/job/truck/:viewType/:jobId",
        component: React.lazy(() => import("./form/JobTruckFormDetails")),
    },
    {
        path: "/applications/services/job/truck/:viewType",
        component: React.lazy(() => import("./form/JobTruckFormDetails")),
    },
    {
        path: "/applications/services/job/to/truck",
        component: React.lazy(() => import("./dashboardTruckJob/DashboardPanel")),
    },
    {
        path: "/applications/services/job/gli",
        component: React.lazy(()=> import("./dashboardGli/DashboardPanel"))
    },
    {
        path: "/applications/services/job/upload",
        component: React.lazy(()=> import("./upload/JobUpload"))
    },
    {
        path: "/applications/services/job/uploadList",
        component: React.lazy(()=> import("./upload/JobUploadList"))
    }
];

export default jobRoutes;
