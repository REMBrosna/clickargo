import React from "react";
import {
  TouchableWithoutFeedback,
  ScrollView,
  StyleSheet,
  Dimensions,
  Image,
  useWindowDimensions,
} from "react-native";
import { Block, Text, theme } from "galio-framework";
import { useSafeAreaInsets } from "react-native-safe-area-context";

import { Drawer as DrawerCustomItem } from "../components/";
import { Images, materialTheme } from "../constants/";
import Theme from "../constants/Theme";
import { LinearGradient } from "expo-linear-gradient";

const { width } = Dimensions.get("screen");
import { useTranslation } from "react-i18next";
import { useNavigation } from "@react-navigation/native";
import { getFontByFontScale } from "../constants/utils";

function CustomDrawerContent({
  drawerPosition,

  profile,
  focused,
  state,
  ...rest
}) {
  const navigation = useNavigation();
  const insets = useSafeAreaInsets();
  const { t } = useTranslation();
  const { fontScale } = useWindowDimensions();

  const styles = makeStyle(fontScale);

  const screens = [
    { title: t("menu:home"), navigateTo: "HomeDrawer" },
    { title: t("menu:language"), navigateTo: "LanguageDrawer" },
    { title: t("menu:changePassword"), navigateTo: "ChangePassDrawer" },
    { title: t("menu:jobHistory"), navigateTo: "JobHistoryDrawer" },
  ];

  return (
    <Block
      style={styles.container}
      forceInset={{ top: "always", horizontal: "never" }}
    >
      <LinearGradient
        colors={[Theme.COLORS.CKPRIMARY, Theme.COLORS.CKSECONDARY]}
        start={{ x: 0, y: 0 }}
        end={{ x: 0, y: 1 }}
        locations={[0.2, 1]}
        flflex={0.23}
      >
        <Block style={[styles.header]}>
          <TouchableWithoutFeedback
            onPress={() => navigation.navigate("ProfileDrawer")}
          >
            <Block style={styles.profile}>
              <Image source={Images.TruckDriver} style={styles.avatar} />
              <Text
                size={getFontByFontScale(fontScale, 20, 20)}
                bold
                color={"white"}
              >
                {profile?.name}
              </Text>
            </Block>
          </TouchableWithoutFeedback>
          <Block row>
            <Text
              size={getFontByFontScale(fontScale, 25, 16)}
              style={styles.account}
            >
              {profile?.coreAccn?.accnName}
            </Text>
          </Block>
        </Block>
      </LinearGradient>
      <Block flex style={{ paddingTop: 7, paddingLeft: 7, paddingRight: 14 }}>
        <ScrollView
          contentContainerStyle={[
            {
              paddingTop: insets.top * 0.4,
              paddingLeft: drawerPosition === "left" ? insets.left : 0,
              paddingRight: drawerPosition === "right" ? insets.right : 0,
            },
          ]}
          showsVerticalScrollIndicator={false}
        >
          {screens.map((item, index) => {
            return (
              <DrawerCustomItem
                title={item?.title}
                navigateTo={item?.navigateTo}
                key={index}
                fontSize={getFontByFontScale(fontScale, 20, null)}
                navigation={navigation}
                focused={state.index === index ? true : false}
              />
            );
          })}
        </ScrollView>
      </Block>
      <Block flex={0.25} style={{ paddingLeft: 7, paddingRight: 14 }}>
        <DrawerCustomItem
          title={t("menu:signOut")}
          navigation={navigation}
          navigateTo="Sign Out"
          fontSize={getFontByFontScale(fontScale, 20, null)}
          focused={state.index === 8 ? true : false}
        />
        {/* <DrawerCustomItem
                    title="Sign Up"
                    navigateTo="Sign Up"
                    navigation={navigation}
                    focused={state.index === 9 ? true : false}
                /> */}
      </Block>
    </Block>
  );
}

const makeStyle = (fontScale) =>
  StyleSheet.create({
    container: {
      flex: 1,
    },
    header: {
      // backgroundColor: Theme.COLORS.CKSECONDARY,
      paddingHorizontal: 20,
      paddingBottom: theme.SIZES.BASE * 2,
      marginTop: theme.SIZES.BASE * 2,
      paddingTop: theme.SIZES.BASE * 2,
      justifyContent: "center",
    },
    footer: {
      paddingHorizontal: 28,
      justifyContent: "flex-end",
    },
    profile: {
      marginBottom: theme.SIZES.BASE / 2,
    },
    avatar: {
      height: 40,
      width: 40,
      borderRadius: 20,
      marginBottom: theme.SIZES.BASE,
    },
    pro: {
      backgroundColor: materialTheme.COLORS.LABEL,
      paddingHorizontal: 6,
      marginRight: 8,
      borderRadius: 4,
      height: 19,
      width: 38,
    },
    account: {
      marginRight: 16,
    },
  });

export default CustomDrawerContent;
