import React from "react";

const creditLineRoutes = [
  {
    path: "/applications/creditline",
    component: React.lazy(() => import("./CreditHistory")),
  },
];

export default creditLineRoutes;
