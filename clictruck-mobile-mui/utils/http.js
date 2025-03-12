import axios from "axios";
import axiosInstance from "../axios";
import { deleteItemAsync } from "expo-secure-store";
import * as RootNavigation from "../auth/RootNavigation";
import * as SecureStore from "expo-secure-store";

export async function sendRequest(url, method, body) {
    method = method ?? "get";

    const token = await SecureStore.getItemAsync("authToken");
    console.log("token: ", SecureStore);
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
        const status = error?.response?.status;
        // if (status == 400) {
        //     // console.log("need to relogin");
        //     await deleteItemAsync("authToken");
        //     RootNavigation.navigate("App")
        // }
        const jsonObject = error;
        const rawData = jsonObject.config.data;

        console.error('Axios payload: ', config);
        console.error('Axios Error:', error);
        return error;
    }
}