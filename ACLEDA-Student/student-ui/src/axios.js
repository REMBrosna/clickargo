import axios from "axios";
import {environment} from "./app/environment/environment";

const axiosInstance = axios.create({
  baseURL: environment.apiUrl
});

axiosInstance.defaults.withCredentials = false;
axiosInstance.interceptors.request.use((config) => {
  return config;
});

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => Promise.reject((error.response && error.response.data) || 'Something went wrong!')
);
export const sessionTimeout = "session timeout";
export const noPermission = "no permission";
export default axiosInstance;
