import React from "react";

const coRoutes = [
    {
        path: "/applications/services/co/list",
        component: React.lazy(() => import("./CoJobsList"))
    },
    {
        path: "/applications/services/co/job/:viewType/:jobId",
        component: React.lazy(() => import("./CoJobFormDetails.jsx"))
    },

];

export default coRoutes;

