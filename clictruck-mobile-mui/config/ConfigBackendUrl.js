import React, { useEffect, useRef, useState } from "react";
import { Platform, StatusBar, Dimensions } from "react-native";
import * as SecureStore from "expo-secure-store";

// config.js
let backendURL = process.env.EXPO_PUBLIC_BACKEND_URL;

export const servers = [
  { name: "Production", url: "https://sg2.clickargo.com/be/clictruck" },
  { name: "UAT", url: "https://ct2-uat.clickargo.com/be/clictruck" },
  { name: "Default", url: process.env.EXPO_PUBLIC_BACKEND_URL },
];

export const setBackendUrl = (serverName) => {
  (async () => {
    let server = servers.filter((s) => serverName === s.name)[0];
    await SecureStore.setItemAsync("server", JSON.stringify(server));

    // const server1 = await SecureStore.getItemAsync("server");
    // console.log("SecureStore.getItemAsync(server) again :", server1);
  })();
};

async function initBackendUrl() {
  const server = await SecureStore.getItemAsync("server");
  // console.log("SecureStore.getItemAsync(server) 111 :", server);
  if(server) {
    const serverJson = JSON.parse(server);
      if (serverJson && serverJson.url) {
        backendURL = serverJson.url;
        // console.log("SecureStore.getItemAsync(server) 222 ", serverJson.url);
      }
  }
  return backendURL;
}

export async function getBackendUrl  ()  {
  let url = await initBackendUrl();
  console.log("ConfigBackendUrl.js backendURL 333", url);
  return url;
};

export async function getBackendServer()  {

  const server = await SecureStore.getItemAsync("server");
  if(server) {
    const serverJson = JSON.parse(server);
    return serverJson;
  }
  return undefined;
};