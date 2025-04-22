import React, { createContext, useEffect, useReducer } from "react";
import jwtDecode from "jwt-decode";
import axios from "axios.js";
import { MatxLoading } from "matx";
import { encodeString } from "app/c1utils/utility";

const initialState = {
  isAuthenticated: true,
  isInitialised: false,
  user: null,
  token: null,
  profile: null
};

const isValidToken = (accessToken) => {
  if (!accessToken) {
    return false;
  }

  const decodedToken = jwtDecode(accessToken);
  // console.log(decodedToken.exp, Date.now(), (decodedToken.exp < Date.now()));
  return decodedToken.exp > Date.now();
};

const setSession = (accessToken,username, refreshToken, expiredAt) => {

  if (accessToken) {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("username", username);
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('expiredAt', expiredAt);
    axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
  } else {
    localStorage.removeItem("accessToken");
    localStorage.setItem("username", username);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('expiredAt');
    delete axios.defaults.headers.common.Authorization;
  }
};

const setProfile = (profile) => {
  if (profile) {
    localStorage.setItem("profile", JSON.stringify(profile));
  }
}

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
      const { userId } = parseJwt(action.payload.token);

      //check if userId and user.username is the same
      //if (userId === user.username) {
      if (user !== null) {
        return {
          ...state,
          isAuthenticated: true,
          user,
          token: action.payload.token

        };
      }

      return {
        ...state,
        isAuthenticated: false,
        user: null,
        token: null
      }

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
    case "PROFILE": {
      const { profile } = action.payload;

      return {
        ...state,
        profile,
        isAuthenticated: true
      };
    }
    default: {
      return { ...state };
    }
  }
};

const parseJwt = (token) => {
  try {
    return JSON.parse(window.atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
};

const AuthContext = createContext({
  ...initialState,
  method: "JWT",
  login: () => Promise.resolve(),
  logout: () => { },
  register: () => Promise.resolve(),
  getProfile: () => Promise.resolve(),
});

export const AuthProvider = ({ children }) => {

  const [state, dispatch] = useReducer(reducer, initialState);

  const login = async (email, password, isRememberMe) => {

    let encodePassword = encodeString(password);

    let FormData = require('form-data');
    let data = new FormData();
    data.append('username', email);
    data.append('password', password);

    let response = await axios.post("http://localhost:8080/oauth/token", data);

    const { user } = response.data;

    if (response.data.err) {
      return response.data;
    }

    const token = response.data.accessToken;
    console.log("response.data", response.data)

    setSession(token, user.username, response.data.refreshToken, response.data.expiredAt);

    dispatch({
      type: "LOGIN",
      payload: {
        ...state,
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
    axios.post("/api/logout")
      .then(res => {
        setSession(null);
        dispatch({
          type: "LOGOUT",
          payload: {
            token: null,
            isAuthenticated: false,
            user: null,
          },
        });
      })
      .catch(err => {
        console.log(err);
      });

  };

  const getProfile = async () => {

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
          const { user } = response.data;

          dispatch({
            type: "PROFILE",
            payload: {
              profile: user,
              isAuthenticated: true
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
        const username = window.localStorage.getItem("username");

        if (accessToken) {
          setSession(accessToken,username);
          const response = await axios.get(`/user/${username}`);

          if (response && response.err) {
            dispatch({
              type: "INIT",
              payload: {
                isAuthenticated: false,
              },
            });
          } else {
            dispatch({
              type: "INIT",
              payload: {
                isAuthenticated: true,
                user: response.data.data,
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
    })();
  }, []);

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
