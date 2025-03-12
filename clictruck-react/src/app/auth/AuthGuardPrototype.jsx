import React, {
  useContext,
  useEffect,
  useState
} from "react";
import { Redirect, useLocation } from "react-router-dom";
import AppContext from "app/appContext";
import useAuth from "app/hooks/useAuth";

//NOTE: Use this for prototype purpose only
const getUserRoleAuthStatus = (pathname, user, routes) => {
  const matched = routes.find((r) => r.path === pathname);

  const authenticated =
    matched && matched.auth && matched.auth.length
      ? matched.auth.includes(user.role)
      : true;
  return authenticated;
};

const AuthGuard = ({ children }) => {
  const {
    isAuthenticated,
    user
  } = useAuth();

  const [previouseRoute, setPreviousRoute] = useState(null);
  const { pathname } = useLocation();

  const { routes } = useContext(AppContext);
  const isUserRoleAuthenticated = getUserRoleAuthStatus(pathname, user, routes);
  let authenticated = isAuthenticated && isUserRoleAuthenticated;

  // **** IF YOU NEED ROLE BASED AUTHENTICATION,
  // UNCOMMENT ABOVE TWO LINES, getUserRoleAuthStatus METHOD AND user VARIABLE
  // COMMENT OUT BELOW LINE 
  //
  // UNCOMMENT BELOW LINE IF ROLE AUTHENTICATION IS DONE BACKEND ****
  //let authenticated = isAuthenticated;

  useEffect(() => {
    if (previouseRoute !== null) setPreviousRoute(pathname);
  }, [pathname, previouseRoute]);

  if (authenticated) return <>{children}</>;
  else {
    return (
      <Redirect
        to={{
          pathname: "/session/signin",
          state: { redirectUrl: previouseRoute },
        }}
      />
    );
  }
};

export default AuthGuard;
