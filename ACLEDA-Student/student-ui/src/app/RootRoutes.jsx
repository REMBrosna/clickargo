import React from "react";
import { Redirect } from "react-router-dom";
import landingRoutes from "./views/landing/LandingRoutes";
import userRoutes from "./views/applications/userRoutes";

const redirectRoute = [
  {
    path: "/",
    exact: true,
    component: () => <Redirect to="/student/applicationStudent/list" />
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
  ...userRoutes,
  ...errorRoute,
];

export default routes;
