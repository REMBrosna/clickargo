import { Block, Switch, Text } from "galio-framework";
import React from "react";
import {
  getFontByFontScale,
  horizontalScale,
  verticalScale,
} from "../../constants/utils";
import { materialTheme } from "../../constants";
import { useListEvents } from "../../context/ListEventsContext";
import { Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useTranslation } from "react-i18next";

const ListingHeaders = ({ showFilter = null, fontScale }) => {

    const { t } = useTranslation();
    const { showSpecialInst, toggleSpecialIns, showDetailed, toggleDetailed } = useListEvents();

  return (
    <Block
      space="around"
      style={{
        flexDirection: "row",
        paddingHorizontal: horizontalScale(20),
        marginTop: verticalScale(7),
        alignItems: "flex-end",
        alignContent: "flex-end",
        alignSelf: "flex-end",
        marginBottom: 5,
        borderWidth: 0,
      }}
    >
      <Block column space="evenly" style={{ borderWidth: 0 }}>
        <Text
          style={{
            fontSize: getFontByFontScale(fontScale, 24, 10),
            paddingRight: 5,
          }}
        >
            {t("other:header.specialInstructions")}
        </Text>
        {/* <Pressable onPress={() => setModalFilterVisible(true)}> */}
        <Switch
          value={showSpecialInst}
          color={materialTheme.COLORS.CKPRIMARY}
          onChange={toggleSpecialIns}
          trackColor={{ true: materialTheme.COLORS.CKSECONDARY }}
          size={getFontByFontScale(fontScale, 24, 12)}
          style={{ transform: [{ scaleX: 0.9 }, { scaleY: 0.9 }] }}
        />
        {/* </Pressable> */}
        {/* <Checkbox
          initialValue={false}
          color={materialTheme.COLORS.CKPRIMARY}
          label="Special Instructions"
          labelStyle={{
            fontSize: getFontByFontScale(fontScale, 24, 10),
          }}
          value={isSpecialIns}
          onChange={() => handleSpecialIns(isSpecialIns)}
          flexDirection="column-reverse"
        /> */}
      </Block>
      <Block column space="evenly" style={{ borderWidth: 0 }}>
        <Text
          style={{
            fontSize: getFontByFontScale(fontScale, 24, 10),
            padding: 0,
          }}
        >
          {t("other:header.detailedView")}
        </Text>
        {/* <Pressable onPress={() => setModalFilterVisible(true)}> */}
        <Switch
          value={showDetailed}
          color={materialTheme.COLORS.CKPRIMARY}
          onChange={toggleDetailed}
          trackColor={{ true: materialTheme.COLORS.CKSECONDARY }}
          size={getFontByFontScale(fontScale, 24, 12)}
          style={{ transform: [{ scaleX: 0.9 }, { scaleY: 0.9 }] }}
        />
        {/* </Pressable> */}
      </Block>
      {showFilter && (
        <Block
          style={{
            paddingHorizontal: horizontalScale(20),
            marginTop: verticalScale(7),
            alignItems: "flex-end",
            marginBottom: 10,
          }}
        >
          <Pressable onPress={() => showFilter?.onPress()}>
            <Text
              style={{ color: materialTheme.COLORS.BLACK, fontWeight: "200" }}
            >
              <Ionicons
                name="filter-outline"
                size={getFontByFontScale(fontScale, 24, 24)}
                color="black"
              />
            </Text>
          </Pressable>
        </Block>
      )}
    </Block>
  );
};

export default ListingHeaders;
