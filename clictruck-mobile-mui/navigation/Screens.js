import {
  Dimensions,
  Image,
  Platform,
  Pressable,
  View,
  useWindowDimensions,
} from "react-native";
import { Icon } from "../components/";
import CtHeader from "../ctcomponents/CtHeader";
import { materialTheme } from "../constants/";

import CustomDrawerContent from "./Menu";
import CtChangePassScreen from "../ctscreen/CtChangePass";
import CtLanguageScreen from "../ctscreen/CtLanguage";
import CtProfileScreen from "../ctscreen/CtProfile";
import CtJobHistoryScreen from "../ctscreen/CtJobHistory";

import React, { useState } from "react";
import SignIn from "../ctscreen/SignIn";
import { createDrawerNavigator } from "@react-navigation/drawer";
import { createStackNavigator } from "@react-navigation/stack";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";

import NewJobs from "../ctscreen/NewJobs";
import PausedJobs from "../ctscreen/PauseJobs";
import SignOut from "../ctscreen/SignOut";
import JobTabContext from "../context/JobTabContext";
import useAuth from "../hooks/useAuth";
import { useTranslation } from "react-i18next";
import DevScreen from "../dev-tool/DevScreen";
import useStats from "../hooks/useStats";
import OngoingJobScreen from "../ctscreen/OngoingJob";
import HeaderV2 from "../ctcomponents/HeaderV2";
import { getFontByFontScale } from "../constants/utils";

const { width } = Dimensions.get("screen");

const Stack = createStackNavigator();
const Drawer = createDrawerNavigator();
const Tab = createBottomTabNavigator();

export default function OnboardingStack(props) {
  return (
    <Stack.Navigator
      screenOptions={{
        mode: "card",
        headerShown: false,
      }}
    >
      <Stack.Screen
        name="SignIn"
        component={SignIn}
        option={{
          headerTransparent: true,
        }}
      />
      <Stack.Screen name="App" component={AppStack} />
      <Stack.Screen name="DeveloperTool" component={DevScreen} />
    </Stack.Navigator>
  );
}

function HomeStack(props) {
  const { fontScale } = useWindowDimensions();

    const { newStats, pauseStats } = useStats();
    const { t } = useTranslation();

  const ActiveButton = (props) => {
    const icon = props.children.props.children[0];
    // console.log("props", props);
    return (
      <Pressable
        onPress={props.onPress}
        onLongPress={props.onLongPress}
        style={props.style}
      >
        {icon}
      </Pressable>
    );
  };

  const TabIcon = (props) => {
    const { focused, color, size, source } = props;
    return (
      <>
        <View
          style={{
            position: "absolute",
            top: 0,
            width: "100%",
            height: 2,
            backgroundColor: focused ? "#13b0eb" : "transparent",
          }}
        />
        <View>
          <Image
            source={source}
            style={{ width: size, height: size, opacity: focused ? 1 : 0.6 }}
          />
        </View>
      </>
    );
  };

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarInactiveTintColor: "gray",
        mode: "card",
        headerShown: true,
        tabBarLabelStyle: {
          fontWeight: 600,
          fontSize: getFontByFontScale(fontScale, 25, null),
        },
        tabBarBadgeStyle: {
          backgroundColor: "#fe1e1e",
          color: "white",
          textAlign: "center",
          fontSize: getFontByFontScale(fontScale, 14, 12),
          lineHeight: Platform.OS === "ios" ? 0 : 14,
        },
      })}
      initialRouteName="CtHomeScreen"
      backBehavior="history"
    >
      <Tab.Screen
        name="NewJobs"
        component={NewJobs}
        options={{
          header: ({ navigation, route, options, layout }) => (
            <HeaderV2
              // back
              options
              title={t("other:header.newJobs")}
            />
          ),
          tabBarLabel: t("other:screen.new"),
          tabBarLabelPosition: "below-icon",
          tabBarIcon: ({ focused, color, size }) => (
            <TabIcon
              focused={focused}
              color={color}
              size={size}
              source={require("../assets/images/button/newjoblist.png")}
            />
          ),
          tabBarBadge:
            newStats?.count && newStats?.count != 0 ? newStats?.count : null,
        }}
      />
      <Tab.Screen
        name="CtHomeScreen"
        headerShown={false}
        component={OngoingJobScreen}
        options={{
          header: ({ navigation, route, options, layout }) => (
            <HeaderV2
              // search
              // tabs={tabs.jobs}
              options
              title={t("other:header.activeJobs")}
              route={route}
            />
          ),
          tabBarLabel: "",
          // tabBarIcon: ({focused, color, size})=>(<Image source={require('../assets/images/truck.png')} style={{ width: size*2, height: size*2, opacity: focused? 1 : 0.5 }} />),
          tabBarIcon: ({ focused, color, size }) => (
            <TabIcon
              focused={focused}
              color={color}
              size={size * 2}
              source={require("../assets/images/button/newtruckjob.png")}
            />
          ),
          tabBarButton: ActiveButton,
        }}
      />
      <Tab.Screen
        name="PausedJobs"
        component={PausedJobs}
        options={{
          header: ({ navigation, route, options, layout }) => (
            <HeaderV2
              // back
              options
              title={t("other:header.pausedJobs")}
            />
          ),
          tabBarLabel: t("other:screen.paused"),
          // tabBarIcon: ({focused, color, size})=>(<Image source={require('../assets/images/button/pause.png')} style={{ width: size, height: size, opacity: focused? 1 : 0.6 }} />),
          tabBarIcon: ({ focused, color, size }) => (
            <TabIcon
              focused={focused}
              color={color}
              size={size}
              source={require("../assets/images/button/pause_symbol.png")}
            />
          ),
          tabBarBadge:
            pauseStats?.count && pauseStats?.count != 0
              ? pauseStats?.count
              : null,
        }}
      />
    </Tab.Navigator>
  );
}

function ProfileStack(props) {
  const { t } = useTranslation();
  return (
    <Stack.Navigator
      screenOptions={{
        mode: "card",
        headerShown: "screen",
      }}
      initialRouteName="CtProfileScreen"
    >
      <Stack.Screen
        name="CtProfileScreen"
        component={CtProfileScreen}
        options={{
          header: ({ scene }) => (
            <CtHeader
              search
              options
              // tabs={tabs.jobs}
              title={t("profile:title")}
              scene={scene}
            />
          ),
        }}
      />

      <Stack.Screen
        name="CtJobHistoryScreen"
        component={CtJobHistoryScreen}
        options={{
          header: ({ scene }) => (
            <CtHeader
              back
              // tabs={tabs.jobs}
              title={t("jobHistory:title")}
              scene={scene}
            />
          ),
        }}
      />
    </Stack.Navigator>
  );
}

function LanguageStack(props) {
  const { t } = useTranslation();

  return (
    <Stack.Navigator
      screenOptions={{
        mode: "card",
        headerShown: "screen",
      }}
      initialRouteName="CtLanguageScreen"
    >
      <Stack.Screen
        name="CtLanguageScreen"
        component={CtLanguageScreen}
        options={{
          header: ({ scene }) => (
            <CtHeader
              search
              options
              title={t("language:title")}
              scene={scene}
            />
          ),
        }}
      />
    </Stack.Navigator>
  );
}

function ChangePassStack(props) {
  const { t } = useTranslation();
  return (
    <Stack.Navigator
      screenOptions={{
        mode: "card",
        headerShown: "screen",
      }}
      initialRouteName="CtChangePassScreen"
    >
      <Stack.Screen
        name="CtChangePassScreen"
        headerShown={false}
        component={CtChangePassScreen}
        options={{
          header: ({ scene }) => (
            <CtHeader
              search
              options
              title={t("password:title")}
              scene={scene}
            />
          ),
        }}
      />
    </Stack.Navigator>
  );
}

function JobHistoryStack(props) {
  const { t } = useTranslation();
  return (
    <Stack.Navigator
      screenOptions={{
        mode: "card",
        headerShown: "screen",
      }}
      initialRouteName="CtJobHistoryScreen"
    >
      <Stack.Screen
        name="CtJobHistoryScreen"
        headerShown={false}
        component={CtJobHistoryScreen}
        options={{
          header: ({ scene }) => (
            <CtHeader
              search
              options
              title={t("menu:jobHistory")}
              scene={scene}
            />
          ),
        }}
      />
    </Stack.Navigator>
  );
}

function AppStack(props) {
  const [jobTabId, setJobTabId] = useState(null);
  const { user } = useAuth();

  return (
    <JobTabContext.Provider value={{ jobTabId, setJobTabId }}>
      <Drawer.Navigator
        style={{ flex: 1 }}
        drawerContent={(props) => (
          <CustomDrawerContent {...props} profile={user} />
        )}
        drawerStyle={{
          backgroundColor: "white",
          width: width * 0.8,
        }}
        screenOptions={{
          activeTintColor: "white",
          inactiveTintColor: "#000",
          activeBackgroundColor: materialTheme.COLORS.ACTIVE,
          inactiveBackgroundColor: "transparent",
          itemStyle: {
            width: width * 0.74,
            paddingHorizontal: 12,
            // paddingVertical: 4,
            justifyContent: "center",
            alignContent: "center",
            // alignItems: 'center',
            overflow: "hidden",
          },
          labelStyle: {
            fontSize: 18,
            fontWeight: "normal",
          },
        }}
        initialRouteName="HomeDrawer"
      >
        <Drawer.Screen
          name="HomeDrawer"
          component={HomeStack}
          options={{
            headerShown: false,
            drawerIcon: ({ focused }) => (
              <Icon
                size={16}
                name="shop"
                family="GalioExtra"
                color={focused ? "white" : materialTheme.COLORS.MUTED}
              />
            ),
          }}
        />
        <Drawer.Screen
          name="LanguageDrawer"
          component={LanguageStack}
          options={{
            headerShown: false,
            drawerIcon: ({ focused }) => (
              <Icon
                size={16}
                name="md-woman"
                family="ionicon"
                color={focused ? "white" : materialTheme.COLORS.MUTED}
                style={{ marginLeft: 4, marginRight: 4 }}
              />
            ),
          }}
        />
        <Drawer.Screen
          name="ChangePassDrawer"
          component={ChangePassStack}
          options={{
            headerShown: false,
            drawerIcon: ({ focused }) => (
              <Icon
                size={16}
                name="md-woman"
                family="ionicon"
                color={focused ? "white" : materialTheme.COLORS.MUTED}
                style={{ marginLeft: 4, marginRight: 4 }}
              />
            ),
          }}
        />
        <Drawer.Screen
          name="JobHistoryDrawer"
          component={JobHistoryStack}
          options={{
            headerShown: false,
            drawerIcon: ({ focused }) => (
              <Icon
                size={16}
                name="gears"
                family="font-awesome"
                color={focused ? "white" : materialTheme.COLORS.MUTED}
                style={{ marginRight: -3 }}
              />
            ),
          }}
        />
        <Drawer.Screen
          name="ProfileDrawer"
          component={ProfileStack}
          options={{
            headerShown: false,
            drawerIcon: ({ focused }) => (
              <Icon
                size={16}
                name="circle-10"
                family="GalioExtra"
                color={focused ? "white" : materialTheme.COLORS.MUTED}
              />
            ),
          }}
        />
        <Drawer.Screen
          name="Sign Out"
          component={SignOut}
          options={{
            headerShown: false,
            drawerIcon: ({ focused }) => (
              <Icon
                size={16}
                name="md-person-add"
                family="ionicon"
                color={focused ? "white" : materialTheme.COLORS.MUTED}
              />
            ),
          }}
        />
      </Drawer.Navigator>
    </JobTabContext.Provider>
  );
}
