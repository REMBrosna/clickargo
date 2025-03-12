import React, {useState, useEffect, useCallback, useRef} from "react";
import { Platform, Image, useColorScheme, StatusBar, Alert, Linking } from "react-native";
import { Asset } from "expo-asset";
import { Block, GalioProvider, theme } from "galio-framework";
import { NavigationContainer } from "@react-navigation/native";
import * as SplashScreen from "expo-splash-screen";
import { enableScreens } from "react-native-screens";
enableScreens();

import Screens from "./navigation/Screens";
import { Images, materialTheme } from "./constants/";
import { AuthProvider } from "./context/JWTAuthContext";
import { navigationRef } from "./auth/RootNavigation";
import { StatsProvider } from "./context/StatsContext";
import { SafeAreaView } from "react-native-safe-area-context";
import colors from "./constants/colors";
import NetworkCheckerContext from "./context/NetworkCheckerContext";
import { useNetInfo } from "@react-native-community/netinfo";
import "./i18n";
import { ListEventProvider } from "./context/ListEventsContext";


const assetImages = [Images.Profile, Images.Avatar, Images.ClictruckLogo];

// cache product images
// products.map(product => assetImages.push(product.image));

// cache categories images
// Object.keys(categories).map(key => {
//   categories[key].map(category => assetImages.push(category.image));
// });

function cacheImages(images) {
  return images.map((image) => {
    if (typeof image === "string") {
      return Image.prefetch(image);
    } else {
      return Asset.fromModule(image).downloadAsync();
    }
  });
}

export default function App() {
  const [appIsReady, setAppIsReady] = useState(false);
  const colorScheme = useColorScheme();

  //assume that network is online
  const [isOnline, setIsOnline] = useState(true);
  const netInfo = useNetInfo();

  const checkConnection = () => {
    // console.log("NET INFO: ", netInfo);
    if (netInfo.isConnected && netInfo.isInternetReachable) {
      setIsOnline(true);
    } else {
      setIsOnline(false);
    }
  };

  useEffect(() => {
    async function prepare() {
      try {
        //Load Resources
        await _loadResourcesAsync();
        // await checkForUpdates();
      } catch (e) {
        console.warn(e);
      } finally {
        // Tell the application to render
        setAppIsReady(true);
        checkConnection();
      }
    }
    prepare();
  }, []);

  const _loadResourcesAsync = async () => {
    return Promise.all([...cacheImages(assetImages)]);
  };

  // const checkForUpdates = async () => {
  //   try {
  //     if (__DEV__) {
  //       console.log("Running in development mode, skipping version check.");
  //       return;
  //     }
  //
  //     const currentVersion = await VersionCheck.getCurrentVersion();
  //     const latestVersion = await VersionCheck.getLatestVersion();
  //
  //     if (VersionCheck.needUpdate({currentVersion, latestVersion})) {
  //       Alert.alert(
  //           "Update Available",
  //           "A newer version of the app is available. Please update to the latest version.",
  //           [
  //             {
  //               text: "Update Now",
  //               onPress: async () => {
  //                 try {
  //                   const storeUrl = Platform.OS === 'ios'
  //                       ? await VersionCheck.getAppStoreUrl()
  //                       : await VersionCheck.getPlayStoreUrl();
  //
  //                   if (storeUrl) {
  //                     await Linking.openURL(storeUrl);
  //                   } else {
  //                     Alert.alert("Error", "Unable to find the store URL.");
  //                   }
  //                 } catch (error) {
  //                   console.error("Failed to open store URL:", error);
  //                   Alert.alert("Error", "An issue occurred while trying to open the store.");
  //                 }
  //               },
  //             },
  //           ]
  //       );
  //     }
  //   } catch (error) {
  //     console.error("Error checking for app update:", error);
  //   }
  // };

  const onLayoutRootView = useCallback(async () => {
    if (appIsReady) {
      await SplashScreen.hideAsync();
    }
  }, [appIsReady]);

  if (!appIsReady) {
    return null;
  }

  return (
      <SafeAreaView style={{ flex: 1 }}>
        <NavigationContainer onReady={onLayoutRootView} ref={navigationRef}>
          <GalioProvider theme={materialTheme}>
            <NetworkCheckerContext.Provider value={{ isOnline, checkConnection }}>
              <AuthProvider>
                <StatsProvider>
                  <ListEventProvider>
                    <Block flex>
                      {Platform.OS === "ios" && (
                          <StatusBar barStyle="dark-content" />
                      )}
                      <Screens />
                    </Block>
                  </ListEventProvider>
                </StatsProvider>
              </AuthProvider>
            </NetworkCheckerContext.Provider>
          </GalioProvider>
        </NavigationContainer>
      </SafeAreaView>
  );
}
