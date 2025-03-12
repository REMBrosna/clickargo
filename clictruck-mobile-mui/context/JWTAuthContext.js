import React, { useEffect, useReducer } from "react";
import { createContext } from "react";
import * as SecureStore from "expo-secure-store";
import axios from "../axios.js";
import { useNavigation } from "@react-navigation/native";

const initialState = {
  isAuthenticated: false,
  isInitialised: false,
  user: null,
};

const storeUser = async (userProfile) => {
  if (userProfile) {
    await SecureStore.setItemAsync("userProfile", JSON.stringify(userProfile));
  } else {
    await SecureStore.deleteItemAsync("userProfile");
  }
};

const setSession = async (accessToken) => {
  if (accessToken) {
    await SecureStore.setItemAsync("authToken", accessToken);
    axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
  } else {
    await SecureStore.deleteItemAsync("authToken");
    delete axios.defaults.headers.common.Authorization;
  }
};

const setSessionReload = async (accessToken) => {
  console.log("setSessionReload====", accessToken);
  if (accessToken !== null) {
    console.log("Reloading token", accessToken);
    await SecureStore.deleteItemAsync("authToken");
    await SecureStore.setItemAsync("authToken", accessToken);
    axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
  } else {
    await SecureStore.setItemAsync("authToken", accessToken);
    delete axios.defaults.headers.common.Authorization;
  }
};

const AuthContext = createContext({
  ...initialState,
  method: "JWT",
  login: () => Promise.resolve(),
  logout: () => Promise.resolve(),
});

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

export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const navigation = useNavigation();

  const login = async (id, password) => {
    const targetURL = "/api/v1/clickargo/clictruck/mobile/auth/login";
    const response = await axios.post(targetURL, { id, password });
    const { user } = response.data;

    if (response.data.err) {
      return response.data;
    }

    const token = response.data.token;

    setSession(response.data.token);
    setSession(token);
    storeUser(user);

    dispatch({
      type: "LOGIN",
      payload: {
        user,
        token: token,
      },
    });

    return response.data;
  };

  const logout = () => {
    setSession(null);
    storeUser(null);
    dispatch({
      type: "LOGOUT",
      payload: {
        token: null,
        isAuthenticated: false,
        user: null,
      },
    });
  };

  const getProfile = async () => {
    try {
      const accessToken = await SecureStore.getItemAsync("authToken");
      if (accessToken) {
        setSession(accessToken);
        axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
        const response = await axios.get("/api/co/cac/profile/");
        const token = response.data.token;
        let isTokenEqual = true;

        if (accessToken !== token) {
          isTokenEqual = false;
          setSessionReload(token);
        }

        console.log("Equal token? ", isTokenEqual);

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
              token: isTokenEqual ? accessToken : token,
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
        const accessToken = await SecureStore.getItemAsync("authToken");
        // console.log("accessToken from useEffect", accessToken);
        if (accessToken) {
          const userProfile = JSON.parse(
            await SecureStore.getItemAsync("userProfile")
          );
          if (userProfile) {
            dispatch({
              type: "INIT",
              payload: {
                isAuthenticated: true,
                user: userProfile,
                token: accessToken,
              },
            });
          } else {
            dispatch({
              type: "INIT",
              payload: {
                isAuthenticated: false,
                user: null,
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
  }, []);

  return (
    <AuthContext.Provider value={{ ...state, login, logout, getProfile }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
