import jwtDecode from "jwt-decode";
import React, { createContext, useEffect, useReducer } from "react";
import { useLocation } from "react-router-dom"

import axios from "axios.js";
import { MatxLoading } from "matx";
import { resetMenu } from "app/redux/actions/NavigationAction";
import { useDispatch } from "react-redux";

const initialState = {
    isAuthenticated: false,
    isInitialised: false,
    user: null,
};

export const isValidToken = (accessToken) => {
    if (!accessToken) {
        return false;
    }

    const decodedToken = jwtDecode(accessToken);
    const currentTime = Date.now() / 1000;
    return decodedToken.exp > currentTime;
};

const setSession = (accessToken) => {
    if (accessToken) {
        localStorage.setItem("accessToken", accessToken);
        axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
    } else {
        localStorage.removeItem("accessToken");
        delete axios.defaults.headers.common.Authorization;
    }
};

const setSessionReload = (accessToken) => {
    if (accessToken) {
        console.log("Reloading token", accessToken);
        localStorage.removeItem("accessToken");
        localStorage.setItem("accessToken", accessToken);
        axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
    } else {
        localStorage.removeItem("accessToken");
        delete axios.defaults.headers.common.Authorization;
    }
};

const reducer = (state, action) => {
    switch (action.type) {
        case "INIT": {
            const { isAuthenticated, user } = action.payload;

            return {
                ...state,
                isAuthenticated,
                isInitialised: true,
                user,
            };
        }
        case "LOGIN": {
            const { user } = action.payload;

            return {
                ...state,
                isAuthenticated: true,
                user,
            };
        }
        case "LOGOUT": {
            return {
                ...state,
                isAuthenticated: false,
                user: null,
            };
        }
        case "REGISTER": {
            const { user } = action.payload;

            return {
                ...state,
                isAuthenticated: true,
                user,
            };
        }
        default: {
            return { ...state };
        }
    }
};

const AuthContext = createContext({
    ...initialState,
    method: "JWT",
    login: () => Promise.resolve(),
    logout: () => { },
    register: () => Promise.resolve(),
});

export const AuthProvider = ({ children }) => {

    const location = useLocation()
    const dispatchMenu = useDispatch();

    const [state, dispatch] = useReducer(reducer, initialState);

    const login = async (email, password) => {
        const response = await axios.post("/api/v1/clickargo/clictruck/auth/login", { id: email, password: password });
        const { user } = response.data;

        if (response.data.err) {
            return response.data;
        }

        const token = response.data.token;

        setSession(response.data.token);
        setSession(token);

        dispatch({
            type: "LOGIN",
            payload: {
                user,
                token: token
            },
        });

        return response.data;
    };

    const register = async (email, username, password) => {
        const response = await axios.post("/api/auth/register", {
            email,
            username,
            password,
        });

        const { accessToken, user } = response.data;

        setSession(accessToken);

        dispatch({
            type: "REGISTER",
            payload: {
                user,
            },
        });
    };

    const logout = () => {
        setSession(null);
        dispatch({
            type: "LOGOUT",
            payload: {
                token: null,
                isAuthenticated: false,
                user: null,
            },
        });

        //reset menu navigation so that next logged in user will not see the previous user's menu as 
        //it is taking time to load the new ones.
        dispatchMenu(resetMenu());
    };

    const getProfile = async () => {

        try {
            const accessToken = window.localStorage.getItem("accessToken");

            if (accessToken && isValidToken(accessToken)) {
                setSession(accessToken);
                const response = await axios.get("/api/co/cac/profile/");
                const token = response.data.token;
                let isTokenEqual = true;

                if (accessToken !== token) {
                    isTokenEqual = false;
                    setSessionReload(token);
                }

                console.log("Equal token? ", isTokenEqual)

                if (response && response.err) {
                    dispatch({
                        type: "INIT",
                        payload: {
                            isAuthenticated: false,
                        },
                    });
                } else {
                    const { user } = response.data;

                    dispatch({
                        type: "PROFILE",
                        payload: {
                            profile: user,
                            isAuthenticated: true,
                            token: isTokenEqual ? accessToken : token

                        },
                    });

                }

            } else {
                dispatch({
                    type: "INIT",
                    payload: {
                        isAuthenticated: false,
                        user: null,
                    },
                });
            }
        } catch (err) {
            console.error(err);
            dispatch({
                type: "INIT",
                payload: {
                    isAuthenticated: false,
                    user: null,
                },
            });
        }
    };

    useEffect(() => {
        (async () => {
            try {

                const accessToken = window.localStorage.getItem("accessToken");

                if (accessToken && isValidToken(accessToken)) {

                    setSession(accessToken);
                    const response = await axios.get("/api/co/cac/profile/");

                    if (response && response.err) {
                        dispatch({
                            type: "INIT",
                            payload: {
                                isAuthenticated: false,
                            },
                        });
                    } else {
                        const { user, token } = response.data;

                        let isTokenEqual = true;

                        if (accessToken !== token) {
                            isTokenEqual = false;
                            setSessionReload(token);
                        }

                        dispatch({
                            type: "INIT",
                            payload: {
                                isAuthenticated: true,
                                user,
                                token: isTokenEqual ? accessToken : token
                            },
                        });
                    }

                } else {
                    dispatch({
                        type: "INIT",
                        payload: {
                            isAuthenticated: false,
                            user: null,
                        },
                    });
                }

            } catch (err) {
                console.error("error: ", err);
                dispatch({
                    type: "INIT",
                    payload: {
                        isAuthenticated: false,
                        user: null,
                    },
                });
            }
        })();
    }, [location]);

    if (!state.isInitialised) {
        return <MatxLoading />;
    }

    return (
        <AuthContext.Provider
            value={{
                ...state,
                method: "JWT",
                login,
                logout,
                register,
                getProfile,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
