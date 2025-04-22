import React, { useEffect, useState } from "react";
import { Redirect, useLocation } from "react-router-dom";

import useAuth from "app/hooks/useAuth";

const AuthGuard = ({ children }) => {
  const {
    isAuthenticated,
     user
  } = useAuth();

  const [previouseRoute, setPreviousRoute] = useState(null);
  const { pathname } = useLocation();

  // IF YOU NEED ROLE BASED AUTHENTICATION,
  // UNCOMMENT ABOVE TWO LINES, getUserRoleAuthStatus METHOD AND user VARIABLE
  // COMMENT OUT BELOW LINE
  let authenticated = isAuthenticated;

  useEffect(() => {
    if (previouseRoute !== null) setPreviousRoute(pathname);
  }, [pathname, previouseRoute]);


  if (authenticated) return <>{children}</>;
  else {
    return (

      <Redirect
        to={{
          pathname: "/session/signin",
          state: { redirectUrl: previouseRoute ? previouseRoute : pathname },
        }}
      />
    );
  }
};

export default AuthGuard;
