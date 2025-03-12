import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView } from 'react-native';
import * as Device from 'expo-device';
import { useNetInfo } from "@react-native-community/netinfo";
import axios from "axios";
import axiosInstance from "../axios";


export default function DevScreen() {
  const [deviceInfo, setDeviceInfo] = useState(null);
  const [netInfo, setNetInfo] = useState(null);
  const [result, setResult] = useState({success: "", error1: "", error2:""});

  const postData = {"id":"","password":""};
  const loginUrl = "/api/v1/clickargo/clictruck/mobile/auth/login";
  async function loginTest() {
    
    try{
    const login = await axios.post(axiosInstance.defaults.baseURL + loginUrl, postData);
    if (login) {
        setResult((p) => ({success: JSON.stringify(login)}));
    } else {
        setResult((p) => ({error2: JSON.stringify(login)}));
    }
    } catch (e) {
        setResult((p) => ({error1: JSON.stringify(e)}));
    }

  }

  const netinfo= useNetInfo();

  // Fetch device information
  const fetchDeviceInfo = async () => {
    console.log("loading device info")
    const info = {
      brand: Device.brand,
      manufacturer: Device.manufacturer,
      modelName: Device.modelName,
      deviceId: Device.deviceId,
      deviceName: Device.deviceName,
      deviceType: Device.deviceType,
      deviceYear: Device.deviceYearClass,
      modelId: Device.modelId,
      osName: Device.osName,
      osVersion: Device.osVersion,
      osBuildFingerprint: Device.osBuildFingerprint,
      osBuildId: Device.osBuildId,
      osInternalBuildid: Device.osInternalBuildId,
      platform: Device.platformApiLevel,
      productName: Device.productName,
      supportedCpuArch: Device.supportedCpuArchitectures,
      totalMemory: Device.totalMemory,
      designName: Device.designName,
      isDevice: Device.isDevice,
    };

    setDeviceInfo(info);
  };

  useEffect(() => {
    

    fetchDeviceInfo();
    loginTest();
  }, []);

  return (
    <View style={{paddingHorizontal:5}}>
    <ScrollView>
      {deviceInfo ? (
        <>
          <Text>DEVICE INFO</Text>
          <Text>Is Device: {deviceInfo.isDevice ? 'Yes' : 'No'}</Text>
          <Text>Brand: {deviceInfo.brand}</Text>
          <Text>Manufacturer: {deviceInfo.manufacturer}</Text>
          <Text>Model Name: {deviceInfo.modelName}</Text>
          {/* <Text>Model Id: {deviceInfo.modelId}</Text> */}
          <Text>Device ID: {deviceInfo.deviceId}</Text>
          <Text>Device Name: {deviceInfo.deviceName}</Text>
          <Text>Device Type: {deviceInfo.deviceType}</Text>
          <Text>Device Year: {deviceInfo.deviceYear}</Text>
          <Text>Platform: {deviceInfo.platform}</Text>
          <Text>OS Name: {deviceInfo.osName}</Text>
          <Text>OS Version: {deviceInfo.osVersion}</Text>
          <Text>OS Build Fingerprint: {deviceInfo.osBuildFingerprint}</Text>
          <Text>OS Build Id: {deviceInfo.osBuildId}</Text>
          <Text>OS Internal Build Id: {deviceInfo.osInternalBuildid}</Text>
          <Text>CPU Arch: {deviceInfo.supportedCpuArch}</Text>
          <Text>Memory: {deviceInfo.totalMemory}</Text>

        </>
      ) : (
          <Text>Loading device information...</Text>
      )}
      {netinfo? (
        <>
          <Text>NETWORK INFO</Text>
          <Text>Net Info: {JSON.stringify(netinfo)}</Text>
        </>
      ):(
        <Text>Loading network information...</Text>
      )}
      {
        <>
        <Text>LOGIN TEST</Text>
        <Text>Server connection: {baseUrl}</Text>
        <Text>API URL: {loginUrl}</Text>
        <Text selectable>Test Result: {JSON.stringify(result)}</Text>
        </>
      }
    </ScrollView>
    </View>
  );
};

