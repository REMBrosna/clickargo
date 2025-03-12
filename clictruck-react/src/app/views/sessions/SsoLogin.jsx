import React from 'react';
import { isValidToken } from "app/contexts/JWTAuthContext"
import { useLocation } from "react-router-dom"
import { useHistory } from 'react-router-dom';

const SsoLogin = () => {

    const history = useHistory();
    const location = useLocation()

    const params = new URLSearchParams(location.search)
    const tokenParam = params.get("token")


    if (tokenParam && isValidToken(tokenParam)) {

        localStorage.setItem("accessToken", tokenParam);
        history.push("/");
    } else {
        history.push("/session/signin");
    }

    return (
        <div></div>
    )

};

export default SsoLogin;