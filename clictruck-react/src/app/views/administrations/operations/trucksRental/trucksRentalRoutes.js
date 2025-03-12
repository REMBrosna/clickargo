import React from "react";

const trucksRentalRoutes = [
    {
        path: "/administrations/truckrental",
        component: React.lazy(() => import("./TrucksRentalPanel"))
    },
    {
        path: "/administrations/truckrental/:viewType/:id",
        component: React.lazy(() => import("./TrucksRentalPanel"))
    },
]

export default trucksRentalRoutes;