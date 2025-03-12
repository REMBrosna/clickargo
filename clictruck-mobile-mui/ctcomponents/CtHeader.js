import React, { useContext, useEffect, useRef } from "react";

import {
  StyleSheet,
  Platform,
  Dimensions,
  useWindowDimensions,
} from "react-native";
import { Button, Block, NavBar, Text, theme } from "galio-framework";
import Icon from "../components/Icon";
import materialTheme from "../constants/Theme";
import Tabs from "../components/Tabs";
import useStats from "../hooks/useStats";
import { getFontByFontScale, verticalScale } from "../constants/utils";
import JobTabContext from "../context/JobTabContext";
import { useNavigation } from "@react-navigation/native";
import { useTranslation } from "react-i18next";

const { height, width } = Dimensions.get("window");
const iPhoneX = () =>
  Platform.OS === "ios" &&
  (height === 812 || width === 812 || height === 896 || width === 896);

const CtHeader = (props) => {

    const { t } = useTranslation();
    const navigation = useNavigation();
    const { newStats, pauseStats, getStatistics } = useStats();
    const { jobTabId } = useContext(JobTabContext);
    const { fontScale } = useWindowDimensions();
    const styles = makeStyle(fontScale);

  const handleLeftPress = () => {
    const { back } = props;
    if (back) navigation.goBack();
    else {
      navigation.openDrawer();
    }

    // return (back ? navigation.goBack() : navigation.openDrawer());
  };

  useEffect(() => {
    getStatistics();
  }, []);

  const renderOptions = () => {
    const { optionLeft, optionRight } = props;

    return (
      <Block row style={styles.tabs}>
        <Button
          shadowless
          style={[styles.tab, styles.divider]}
          onPress={() =>
            navigation.navigate("ListingDrawer", { screen: "NewJobs" })
          }
        >
          <Block column middle>
            <Icon
              name="time"
              family="ionicon"
              color="#aed580"
              style={{ paddingRight: 5 }}
              size={verticalScale(20)}
            />
            <Text size={16} style={styles.tabTitle}>
              {optionLeft || t("other:header.newJobs")}
            </Text>
            <Text size={16}> ({newStats ? newStats?.count : 0})</Text>
          </Block>
        </Button>
        {/* "Listing", { screen: 'PausedJobs' } */}
        <Button
          shadowless
          style={styles.tab}
          onPress={() => navigation.navigate("Deals")}
        >
          <Block column middle>
            <Icon
              name="pause"
              family="ionicon"
              color={"#0872ba"}
              style={{ paddingRight: 5 }}
              size={verticalScale(20)}
            />
            <Text size={16} style={styles.tabTitle}>
              {optionRight || t("other:header.pausedJobs")}
            </Text>
            <Text size={16}> ({pauseStats ? pauseStats?.count : 0})</Text>
          </Block>
        </Button>
      </Block>
    );
  };

  const renderTabs = () => {
    const { tabs, tabIndex } = props;
    const defaultTab = tabs && tabs[0] && tabs[0].id;

    if (!tabs) return null;

    return (
      <Tabs
        data={tabs || []}
        initialIndex={tabIndex || defaultTab}
        onChange={(id) => navigation.setParams({ tabId: id })}
      />
    );
  };

  const renderHeader = () => {
    const { search, tabs, options } = props;
    if (search || tabs || options) {
      return (
        <Block>
          {/* {search ? this.renderSearch() : null} */}
          {/* {options ? renderOptions() : null} */}
          {tabs ? renderHeaderTabs() : null}
        </Block>
      );
    }
    return null;
  };

  const renderHeaderTabs = () => {
    const { tabs, tabIndex, route } = props;
    const defaultTab = tabs && tabs[1] && tabs[1].id;

    if (!tabs) return null;

    return (
      <Tabs
        data={tabs || []}
        stats={{ newStats, pauseStats }}
        initialIndex={jobTabId || defaultTab}
        onChange={(id) => navigation.setParams({ tabId: id })}
        fontSize={getFontByFontScale(fontScale, 25, null)}
      />
    );
  };

  const { back, title, white, transparent, scene } = props;
  // const { routeName } = navigation.state;
  // const { options } = scene.descriptor;
  // const routeName = scene.descriptor?.options.headerTitle ?? '';
  const noShadow = ["Search", "Profile"].includes(title);
  const headerStyles = [
    !noShadow ? styles.shadow : null,
    transparent ? { backgroundColor: "rgba(0,0,0,0)" } : null,
  ];

  return (
    <Block style={headerStyles}>
      <NavBar
        back={back}
        title={title}
        style={styles.navbar}
        transparent={transparent}
        rightStyle={{ alignItems: "center" }}
        leftStyle={{ paddingTop: 3, flex: 0.3 }}
        leftIconName={back ? null : "navicon"}
        // leftIconFamily="font-awesome"
        leftIconColor={white ? theme.COLORS.WHITE : theme.COLORS.ICON}
        titleStyle={[
          styles.title,
          { color: theme.COLORS[white ? "WHITE" : "ICON"] },
        ]}
        onLeftPress={handleLeftPress}
      />
      {renderHeader()}
    </Block>
  );
};

export default CtHeader;

const makeStyle = (fontScale) =>
  StyleSheet.create({
    button: {
      padding: 12,
      position: "relative",
    },
    title: {
      width: "100%",
      fontSize: getFontByFontScale(fontScale, 25, 16),
      fontWeight: "bold",
    },
    navbar: {
      paddingVertical: 0,
      paddingBottom: theme.SIZES.BASE * 1.5,
      paddingTop: iPhoneX ? theme.SIZES.BASE * 4 : theme.SIZES.BASE,
      zIndex: 5,
    },
    shadow: {
      backgroundColor: theme.COLORS.WHITE,
      shadowColor: "black",
      shadowOffset: { width: 0, height: 2 },
      shadowRadius: 6,
      shadowOpacity: 0.2,
      elevation: 3,
    },
    notify: {
      backgroundColor: materialTheme.COLORS.LABEL,
      borderRadius: 4,
      height: theme.SIZES.BASE / 2,
      width: theme.SIZES.BASE / 2,
      position: "absolute",
      top: 8,
      right: 8,
    },
    header: {
      backgroundColor: theme.COLORS.WHITE,
    },
    divider: {
      borderRightWidth: 0.3,
      borderRightColor: theme.COLORS.MUTED,
    },
    search: {
      height: 48,
      width: width - 32,
      marginHorizontal: 16,
      borderWidth: 1,
      borderRadius: 3,
    },
    tabs: {
      marginBottom: 24,
      marginTop: 10,
    },
    tab: {
      backgroundColor: theme.COLORS.TRANSPARENT,
      width: width * 0.5,
      borderRadius: 0,
      borderWidth: 0,
      height: 24,
      elevation: 0,
    },
    tabTitle: {
      lineHeight: 19,
      fontWeight: 300,
      fontSize: getFontByFontScale(fontScale, 25, null),
    },
  });
