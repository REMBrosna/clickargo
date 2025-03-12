import React, { useEffect, useState } from "react";
import CtLoading from '../ctscreen/CtLoading';
import useAuth from "../hooks/useAuth";
import * as RootNavigation from "../auth/RootNavigation";


const AuthGuard = ({ children }) => {
    const { isAuthenticated } = useAuth();
    const [navReady, setNavReady] = useState(false);

    console.log("isAuthenticateD?", isAuthenticated);

    if (isAuthenticated) {
        let repeat = true;
        let loadingEl = <CtLoading isVisible={true} title="Please wait" />;
        while (repeat) {
            console.log("onRepeat");
            if (RootNavigation.isNavigationRead()) {
                repeat = false
            }
            repeat = false;

        }

        return RootNavigation.navigate("App");
    }
    else {
        return RootNavigation.navigate('SignIn');

    }
};

export default AuthGuard;
