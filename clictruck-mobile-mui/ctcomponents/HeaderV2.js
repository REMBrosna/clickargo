import React from "react";

import {
  StyleSheet,
  Platform,
  Dimensions,
  useWindowDimensions,
} from "react-native";
import { Block, NavBar, theme } from "galio-framework";

import { useNavigation } from "@react-navigation/native";
import { getFontByFontScale } from "../constants/utils";

const { height, width } = Dimensions.get("window");
// console.log("height-width:  ", height, width);
// const iPhoneX = () =>
//   Platform.OS === "ios" &&
//   (height === 812 || width === 812 || height === 896 || width === 896);

const HeaderV2 = (props) => {
  const navigation = useNavigation();
  const { fontScale } = useWindowDimensions();
  const styles = makeStyles(fontScale);

  const handleLeftPress = () => {
    const { back } = props;
    if (back) navigation.goBack();
    else {
      navigation.openDrawer();
    }

    // return (back ? navigation.goBack() : navigation.openDrawer());
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
        transparent={true}
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
    </Block>
  );
};

export default HeaderV2;

const makeStyles = (fontScale) =>
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
      paddingTop:
        Platform.OS === "ios" ? theme.SIZES.BASE * 4 : theme.SIZES.BASE,
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
    // notify: {
    //   backgroundColor: materialTheme.COLORS.LABEL,
    //   borderRadius: 4,
    //   height: theme.SIZES.BASE / 2,
    //   width: theme.SIZES.BASE / 2,
    //   position: "absolute",
    //   top: 8,
    //   right: 8,
    // },
    header: {
      backgroundColor: theme.COLORS.WHITE,
    },
    divider: {
      borderRightWidth: 0.3,
      borderRightColor: theme.COLORS.MUTED,
    },
    // search: {
    //   height: 48,
    //   width: width - 32,
    //   marginHorizontal: 16,
    //   borderWidth: 1,
    //   borderRadius: 3,
    // },
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
    },
  });
