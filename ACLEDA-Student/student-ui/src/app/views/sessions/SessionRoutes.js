import React from "react";
import JWTLogin from "./login/JwtLogin";
import NotFound from "./NotFound";
import ForgotPassword from "./ForgotPassword";
import ResetPassword from "./ResetPassword";
import ForceChangePassword from "./ForceChangePassword";
import JWTRegister from "./register/JwtRegister";
import SessionTimeout from "./SessionTimeout";
import NoPermission from "./NoPermission";

const sessionRoutes = [
  {
    path: "/session/signup",
    component: JWTRegister,
  },
  {
    path: "/session/register/:regId",
    component: JWTRegister,
  },
  {
    path: "/session/signin",
    component: JWTLogin,
  },

  {
    path: "/session/forgot-password",
    component: ForgotPassword,
  },
  {
    path: "/session/reset-password",
    component: ResetPassword,
  },
  {
    path: "/session/force-change-password",
    component: ForceChangePassword,
  },
  {
    path: "/session/404",
    component: NotFound,
  },
  {
    path: "/session/timeout",
    component: SessionTimeout,
  },
  {
    path: "/session/nopermission",
    component: NoPermission,
  },
  {
    path: "/activate/:token",
    component: React.lazy(() =>
      import("./register/RegActivate")
    )
  },

];

export default sessionRoutes;
