export const handleDateConvert = (date:number) => {
    const convertDate = new Date(date);
    const str = convertDate.toLocaleString("id-ID", { hour12: false });

    return str.slice(0, str.length - 3);
};

export const convertToString = (variable: number | string) => {
  if (typeof variable === 'number') {
    return variable.toString();
  } else if (typeof variable === 'string') {
    return variable;
  }
};

export function displayDate (unixTime: string | number | Date | undefined, locale?: number ) :any{
  if(unixTime){
    // Convert Unix timestamp to JavaScript Date object
    const date = new Date(unixTime);

    // adjust to locale time
    if (locale){
      date.setHours(date.getHours() + locale);
    }

    // Format the date and time
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    // Return formatted date
    const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}`;
    return formattedDate;
  } else {
    return unixTime;
  }
};

import axios from "axios";
import { router } from "expo-router";
import { deleteItemAsync, getItemAsync } from "expo-secure-store";
export async function sendRequest(url:string, method?:string, body?:any) {
  method = method ?? "get";

  const token = await getItemAsync("authToken");
  const config = { 
      url: url,
      method: method,
      headers:{ Authorization: "Bearer " + token }, 
      data: body,
  };

  try 
  {
      const result = await axios.request(config);
      if (result){
          console.log("axios request result : ", result.data);
          return result.data;
      }
      else
      {
          console.error('axios request result is invalid');
          return false
      }
  }
  catch (error:any)
  {
    const status = error?.response?.status;
    if(status == 400){
      // console.log("need to relogin");
      await deleteItemAsync("authToken");
      router.push("/");
    }
      console.error('Axios payload: ', config);
      console.error('Axios Error:', error);
      return false;
  }
}