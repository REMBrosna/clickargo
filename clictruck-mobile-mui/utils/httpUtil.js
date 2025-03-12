import axios from "axios";
import axiosInstance from "../axios";
import { deleteItemAsync } from "expo-secure-store";
import * as RootNavigation from "../auth/RootNavigation";
import * as SecureStore from "expo-secure-store";

const NO_PRINCIPAL_TXT = [
    "principal is null",
    "principal is null or empty",
    "principal null"
] ;

const sessionCutOff = 'com.vcc.camelone.common.exception.ProcessingException: com.vcc.camelone.common.exception.EntityNotFoundException: java.lang.Exception: principal is null';

export async function sendRequest(url, method, body) {
    method = method ?? "get";

    const token = await SecureStore.getItemAsync("authToken");
    const config = {
        url: axiosInstance.defaults.baseURL + url, //process.env.EXPO_PUBLIC_BACKEND_URL + url,
        method: method,
        headers: { Authorization: "Bearer " + token },
        data: body,
    };

    try {
        const result = await axios.request(config);
        if (result) {
            // console.log("axios request result : ", result.data);
            return result.data;
        }
        else {
            console.error('axios request result is invalid');
            return false
        }
    }
    catch (error) {
        console.log("error:", error);
        
        const status = error?.response?.status;
        const errMsg = error?.response?.data?.err?.msg;
        if ((status === 400 && NO_PRINCIPAL_TXT.some(v => errMsg?.includes(v)))
            || (400 === error?.response?.status && error?.response?.data?.err?.msg === sessionCutOff)) {
            // console.log("need to relogin");
            await deleteItemAsync("authToken");
            RootNavigation.navigate("SignIn")
        }
        return false;
    }
}