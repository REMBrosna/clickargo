import axios from "axios";
import * as SecureStore from "expo-secure-store";
import  {servers, setBackendUrl, getBackendUrl } from "./config/ConfigBackendUrl";

//Use this to connect to backend API

const axiosInstance = axios.create({
  baseURL: process.env.EXPO_PUBLIC_BACKEND_URL,
});

// //
// Fetch the URL asynchronously and update axiosInstance's baseURL
export async function updateAxiosBaseURL( url ) {

  if( !url ) {
    // if url is undefind
    url = await getBackendUrl();
  }
  // console.log("updateAxiosBaseURL: ", url);
  if (url) {
    axiosInstance.defaults.baseURL = url; // Update the baseURL dynamically
  } 
}
// Immediately invoke the function to update baseURL
updateAxiosBaseURL();
// //

// Use this to use Mock api
// const axiosInstance = axios.create();
// axios.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
axiosInstance.defaults.withCredentials = true;
axiosInstance.interceptors.request.use(async (config) => {
  let token = await SecureStore.getItemAsync("authToken");
  config.headers.Authorization = token;
  return config;
});

// ADD NO SESSION MESSAGE HERE, as some api don't seem to throw same exception message
const NO_PRINCIPAL_TXT = [
  "principal is null",
  "principal is null or empty",
  "principal null",
];

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    console.log("error", error);

    if (error.toJSON().message === "Network Error") {
      return Promise.reject({ err: { msg: NetworkError } });
    } else if (408 === error.response.status) {
      return Promise.reject({ err: { msg: sessionTimeout } });
    } else if (403 === error.response.status) {
      return Promise.reject({ err: { msg: noPermission } });
    } else if (502 === error.response.status) {
      //for validation
      return Promise.reject(error.response && error.response.data);
    } else if (406 === error.response.status) {
      return Promise.reject(error.response && error.response.data);
    } else {
      if (
        (400 === error.response.status &&
          NO_PRINCIPAL_TXT.some((v) =>
            error?.response?.data?.err?.msg?.includes(v)
          )) ||
        (400 === error.response.status &&
          error?.response?.data?.err?.msg === sessionCutOff)
      ) {
        return Promise.reject({ err: { msg: sessionTimeout } });
      } else {
        if (error?.response?.data?.err?.msg.includes("attachment")) {
          return Promise.reject({
            err: { msg: error?.response?.data?.err?.msg },
          });
        } else {
          //Nina removed displaying of stacktrace for security purposes
          // return Promise.reject("Something went wrong!");
          return Promise.reject({
            err: {
              code: error?.response?.data?.err?.code,
              msg: error?.response?.data?.err?.msg,
            },
          });
        }
      }
    }
  }
);

export const sessionTimeout = "session timeout";
export const noPermission = "no permission";
// export const NetworkError = "Please check your internet connection!";
export const NetworkError =
  "Unable to reach " + axiosInstance.defaults.baseURL ; //process.env.EXPO_PUBLIC_BACKEND_URL;
export const sessionCutOff =
  "com.vcc.camelone.common.exception.ProcessingException: com.vcc.camelone.common.exception.EntityNotFoundException: java.lang.Exception: principal is null";

export default axiosInstance;
