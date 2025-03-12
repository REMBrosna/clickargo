import React from "react";

const co2xroutes = [
  {
    path: "/co2x",
    component: React.lazy(() => {
      return import("./ThirdPartyList");
    }),
  },
  {
    path: "/co2xRedirect",
    component: React.lazy(() => {
      return import("./CO2xRedirect");
    }),
  },
];

export default co2xroutes;
