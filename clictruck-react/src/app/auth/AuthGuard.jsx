import React, { useEffect, useState } from "react";
import { Redirect, useLocation } from "react-router-dom";

// import AppContext from "app/appContext";
import useAuth from "app/hooks/useAuth";

// const getUserRoleAuthStatus = (pathname, user, routes) => {
//   const matched = routes.find((r) => r.path === pathname);

//   const authenticated =
//     matched && matched.auth && matched.auth.length
//       ? matched.auth.includes(user.role)
//       : true;
//   console.log(matched, user);
//   return authenticated;
// };

const AuthGuard = ({ children }) => {
    const {
        isAuthenticated,
        // user
    } = useAuth();

    const [previouseRoute, setPreviousRoute] = useState(null);
    const { pathname } = useLocation();

    // const { routes } = useContext(AppContext);
    // const isUserRoleAuthenticated = getUserRoleAuthStatus(pathname, user, routes);
    // let authenticated = isAuthenticated && isUserRoleAuthenticated;

    // IF YOU NEED ROLE BASED AUTHENTICATION,
    // UNCOMMENT ABOVE TWO LINES, getUserRoleAuthStatus METHOD AND user VARIABLE
    // COMMENT OUT BELOW LINE
    let authenticated = isAuthenticated;

    useEffect(() => {
        if (previouseRoute !== null) setPreviousRoute(pathname);
    }, [pathname, previouseRoute]);

    let loginPath = window.localStorage.getItem("loginPath");
    let nLoginPath = loginPath ? loginPath.substring(process.env.REACT_APP_CONTEXT_NAME.length, loginPath.length) : null;

    if (authenticated) return <>{children}</>;
    else {
        return (
            <Redirect
                to={{
                    pathname: nLoginPath ? nLoginPath : "/session/signin",
                    // state: { redirectUrl: previouseRoute },
                    state: { redirectUrl: previouseRoute ? previouseRoute : pathname },
                }}
            />
        );
    }
};

export default AuthGuard;
