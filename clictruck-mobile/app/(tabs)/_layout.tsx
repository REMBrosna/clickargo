import React, { useContext, useState } from "react";
import FontAwesome from "@expo/vector-icons/FontAwesome";
import { Tabs, router, usePathname } from "expo-router";
import {
    Pressable,
    useColorScheme,
    StyleSheet,
    View,
    Image,
    SafeAreaView,
    Dimensions,
    StatusBar,
    Platform,
    Alert,
    BackHandler,
} from "react-native";
import { moderateScale, horizontalScale } from "../../constants/Metrics";
import { useFonts } from "expo-font";
import Colors from "../../constants/Colors";
import Header from "../../components/Header";
import { StatsContext } from "../../store/context/stats-context";
import { StatsContextType, StatsType } from "../../cktypes/clictruck";
import { Text } from "../../components/Themed";


/**
 * You can explore the built-in icon families and icons on the web at https://icons.expo.fyi/
 */
function TabBarIcon(props: {
    name: React.ComponentProps<typeof FontAwesome>["name"];
    color: string;
    size: number;
}) {
    return <FontAwesome style={{ marginBottom: 0 }} {...props} />;
}

export default function TabLayout() {
    const colorScheme = useColorScheme();

    const { width, height } = Dimensions.get("screen");

    const [fontsLoaded] = useFonts({
        "pop-black": require("../../assets/fonts/poppins/Poppins-Black.ttf"),
        "pop-med": require("../../assets/fonts/poppins/Poppins-Medium.ttf"),
        "pop-reg": require("../../assets/fonts/poppins/Poppins-Regular.ttf"),
    });


    const { newStats, pauseStats } = useContext(StatsContext) as StatsContextType;

    const pathname = usePathname();

    React.useEffect(() => {
        const backAction = () => {
            Alert.alert("Hold on!", "Are you sure want to exit?", [
                {
                    text: "cancel",
                    onPress: () => null,
                    style: "cancel",
                },
                { text: "yes", onPress: () => BackHandler.exitApp() },
            ]);
            return true;
        };

        const backHandler = BackHandler.addEventListener("hardwareBackPress", backAction);

        return () => backHandler.remove();


    }, []);


    const handleAccountPress = () => {
        router.push("/(tabs)/menu");


    };

    if (!fontsLoaded) {
        return null;
    }

    const CustomButton = (props: any) => {
        const isFocused = props.accessibilityState.selected;
        return (
            <Pressable
                style={{
                    width: 70,
                    height: 70,
                    borderRadius: 80,
                    alignItems: "center",
                    justifyContent: "center",
                    borderColor: isFocused? "#2f95dc" : "grey",
                    borderWidth: isFocused? 3 : 1,
                    zIndex: 2,
                    backgroundColor: Colors[colorScheme ?? "light"].background,
                    position: "absolute",
                    left: width / 2 - 35,
                }}
                onPress={() => {
                    router.push("/(tabs)/");
                }}
            >
                {props.children}
            </Pressable>
        );
    };

    return (
        <SafeAreaView style={{ backgroundColor: "#fff" }}>
            <StatusBar
                barStyle={Platform.OS === "android" ? "light-content" : "dark-content"}
                backgroundColor={"black"}
                animated={true}
                showHideTransition={"fade"} />
            <View
                style={{
                    height: "100%",
                    width: "100%",
                    paddingTop: Platform.OS === "android" ? StatusBar.currentHeight : 0,
                }} >
                <Tabs
                    initialRouteName="index"
                    screenOptions={{
                        header: () => {
                            return (
                                <Header ActivePage={pathname} OnPressAccount={handleAccountPress} />
                            );
                        },
                        tabBarActiveBackgroundColor: "#1976d2",
                        tabBarActiveTintColor: Colors[colorScheme ?? "light"].tint,
                        tabBarStyle: {
                            position: "absolute",
                            top: 70,
                            height: 50,
                        },
                        tabBarLabelStyle: { justifyContent: "center", alignItems: "center" },
                        tabBarItemStyle: {
                            height: 50,
                            justifyContent: "center",
                        },
                    }} >
                    <Tabs.Screen
                        name="new"
                        options={{
                            title: "New",
                            tabBarIconStyle: { display: "none" },
                            tabBarLabel: (e) => {
                                return (
                                    <>
                                        <Text
                                            style={[
                                                styles.textTabLabel,
                                                { color: e.focused ? "#fff" : "black" },
                                            ]} >
                                            {newStats && newStats?.title}
                                        </Text>
                                        <Text
                                            style={[
                                                styles.textTabLabel,
                                                { color: e.focused ? "#fff" : "black" },
                                            ]} >
                                            {newStats && newStats?.count}
                                        </Text>
                                    </>
                                );
                            },
                        }}
                    />
                    <Tabs.Screen
                        name="index"
                        options={{
                            tabBarIcon: ({ focused, color, size }) => {
                                if (pathname === "/history") {
                                    return(
                                        <TabBarIcon
                                        name="hourglass-end"
                                        color="#2f95dc"
                                        size={size * 1.5}
                                        />
                                    )
                                } else if(focused) {
                                    return(
                                        <Image source={require('../../assets/images/button/3d-truck.png')} style={{width:50, height:50}} />
                                    )
                                } else {
                                    return (
                                        <TabBarIcon
                                            name={pathname === "/history" ? "hourglass-end" : "truck"}
                                            color={pathname === "/history" ? "#2f95dc" : color}
                                            size={size * 1.5}
                                        />
                                    );
                                }
                            },
                            tabBarItemStyle: { display: "none" },
                            tabBarLabelStyle: { display: "none" },
                            tabBarButton: (props) => <CustomButton {...props} />,
                        }}
                    />
                    <Tabs.Screen
                        name="paused"
                        options={{
                            title: "Paused",
                            tabBarIcon: ({ color, size }) => (
                                <TabBarIcon name="file-o" color={color} size={size * 0.7} />
                            ),
                            tabBarIconStyle: { display: "none" },
                            tabBarLabel: (e) => {
                                return (
                                    <>
                                        <Text
                                            style={[
                                                styles.textTabLabel,
                                                { color: e.focused ? "#fff" : "black" },
                                            ]}   >
                                            {pauseStats && pauseStats?.title}
                                        </Text>
                                        <Text
                                            style={[
                                                styles.textTabLabel,
                                                { color: e.focused ? "#fff" : "black" },
                                            ]}  >
                                            {pauseStats && pauseStats?.count}
                                        </Text>
                                    </>
                                );
                            },
                        }}
                    />
                    <Tabs.Screen
                        name="menu"
                        options={{
                            tabBarItemStyle: { display: "none" },
                        }}
                    />
                    <Tabs.Screen
                        name="history"
                        options={{
                            tabBarItemStyle: { display: "none" },
                        }}
                    />
                </Tabs>
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    tabContainer: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        height: 70,
        zIndex: 2,
    },
    tab: {
        width: "50%",
        height: 70,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#95bcf3",
        borderWidth: 1,
    },
    midTabIcon: {
        position: "absolute",
        alignItems: "center",
        top: "20%",
        left: "50%",
        right: "50%",
    },
    tabIcon: {
        height: 80,
        width: 80,
        borderRadius: 40,
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "lightgrey",
        borderWidth: 2,
    },
    textTabLabel: {
        fontSize: moderateScale(14),
        fontFamily: "pop-med",
    },
    // card list
    card: {
        width: "100%",
        paddingHorizontal: horizontalScale(10),
        borderRadius: 14,
        minHeight: 150,
        borderWidth: 1,
    },
    cardHeader: {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        borderBottomWidth: 0.5,
        paddingVertical: 12,
    },
    cardBody: {
        display: "flex",
        flexDirection: "row",
        marginVertical: 15,
    },
    cardIconContainer: {
        alignItems: "center",
        justifyContent: "center",
        borderWidth: 0.5,
        borderRadius: 2,
        width: moderateScale(30),
        height: moderateScale(30),
    },
    cardTimeContainer: {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "flex-start",
        marginTop: 10,
    },
    cardButton: {
        width: "100%",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "row",
        borderWidth: 1,
        paddingVertical: 8,
        borderRadius: 5,
        marginBottom: 10,
        zIndex: 2,
    },
    textCardHeader: {
        fontSize: moderateScale(15),
        // fontWeight: "400",
        fontFamily: "pop-med",
    },
    textCardBodyHeader: {
        fontSize: moderateScale(12),
        fontFamily: "pop-med",
    },
});
