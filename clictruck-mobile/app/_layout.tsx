import FontAwesome from "@expo/vector-icons/FontAwesome";
import { DarkTheme, DefaultTheme } from "@react-navigation/native";
import { useFonts } from "expo-font";
import { SplashScreen, Stack, Link } from "expo-router";
import { useEffect } from "react";
import { Pressable, useColorScheme, Image, SafeAreaView, Platform } from "react-native";
import { View, Text } from "../components/Themed";
import Colors from "../constants/Colors";
import i18n from "i18next";
import { useTranslation, initReactI18next } from "react-i18next";
import resources from "../assets/translation/resources.json";
import StatsProvider from "../store/context/stats-context";
import { ThemeProvider, createTheme, lightColors } from "@rneui/themed";



//i18 initialization
i18n.use(initReactI18next).init({
    resources: resources,
    lng: "en",
    fallbackLng: "en",
    interpolation: {
        escapeValue: false,
    },
    compatibilityJSON: "v3",
});

export {
    // Catch any errors thrown by the Layout component.
    ErrorBoundary,
} from "expo-router";

export const unstable_settings = {
    // Ensure that reloading on `/modal` keeps a back button present.
    initialRouteName: "(tabs)",
};

// Prevent the splash screen from auto-hiding before asset loading is complete.
SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
    const [loaded, error] = useFonts({
        SpaceMono: require("../assets/fonts/SpaceMono-Regular.ttf"),
        ...FontAwesome.font
    });

    // Expo Router uses Error Boundaries to catch errors in the navigation tree.
    useEffect(() => {
        if (error) throw error;
    }, [error]);

    useEffect(() => {
        if (loaded) {
            SplashScreen.hideAsync();
        }
    }, [loaded]);

    if (!loaded) {
        return null;
    }

    return <RootLayoutNav />;
}

function RootLayoutNav() {
    const colorScheme = useColorScheme();

    const LogoTitle = () => (
        <View style={{ flexDirection: "row", alignItems: "center" }}>
            <Image
                style={{ width: 50, height: 30 }} // Set width and height as per your logo's dimensions
                resizeMode="contain"
                source={require("../assets/images/Clickargo.png")}
            />
            <Text style={{ marginLeft: 10, fontSize: 18 }}>Clickargo</Text>
        </View>
    );

    const theme = createTheme({
        lightColors: {
            ...Platform.select({
                default: lightColors.platform.android,
                ios: lightColors.platform.ios,
            }),
        }
    });

    return (
        // <ThemeProvider value={colorScheme === "dark" ? DarkTheme : DefaultTheme}>
        <ThemeProvider theme={theme}>
            <StatsProvider>
                <Stack>
                    <Stack.Screen name="index" options={{ headerShown: false }} />
                    <Stack.Screen
                        name="(tabs)"
                        options={{
                            headerShown: false,
                            headerShadowVisible: false,
                        }}
                    />
                </Stack>
            </StatsProvider>
        </ThemeProvider>
        // </ThemeProvider>
    );
}
