import React from "react";
import { Redirect } from "react-router-dom";

import administrationRoutes from "./views/administrations/administrationRoutes";
import landingRoutes from "./views/applications/general/generalRoutes";
import truckRoutes from "./views/applications/trucks/truckRoutes";
import configurationRoutes from "./views/configurations/configurationRoutes";
import dashboardRoutes from "./views/dashboard/DashboardRoutes";
import workBenchRoutes from "./views/workbench/workbenchRoutes";
import jobRoutes from "./views/applications/job/jobRoutes";
import financeRoutes from "./views/applications/job/finance/financeRoutes";
import coffFinanceRoutes from "./views/applications/job/dashboard/finance/coffFinanceRoutes";
import gliRoutes from "./views/applications/job/dashboardGli/gliRoutes";
import creditLineRoutes from "./views/applications/creditLine/creditLineRoutes";
import co2xroutes from "./views/applications/co2x/co2xroute";
import truckTrackingRoutes from "./views/administrations/operations/truckTracking/truckTrackingRoutes";

const redirectRoute = [
  {
    path: "/",
    exact: true,
    component: () => <Redirect to="/general/landing/workbench" />,
  },
];
const errorRoute = [
  {
    component: () => <Redirect to="/session/404" />,
  },
];

const routes = [
  ...redirectRoute,
  ...landingRoutes,
  ...truckRoutes,
  ...dashboardRoutes,
  ...configurationRoutes,
  ...workBenchRoutes,
  ...administrationRoutes,
  ...jobRoutes,
  ...financeRoutes,
  ...coffFinanceRoutes,
  ...gliRoutes,
  ...creditLineRoutes,
  ...co2xroutes,
  ...truckTrackingRoutes,
  ...errorRoute,
];

export default routes;
