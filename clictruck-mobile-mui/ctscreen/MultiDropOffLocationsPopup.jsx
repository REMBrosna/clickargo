import React from "react";
import { Modal, StyleSheet } from "react-native";
import CtModal from "../ctcomponents/CtModal";
import { Block, Icon, Text } from "galio-framework";
import { makePopupStyle } from "./history/commonstyles";
import { materialTheme } from "../constants";
import {
  displayDate,
  getFontByFontScale,
  moderateScale,
} from "../constants/utils";
import { useTranslation } from "react-i18next";
import { DropOffDetails } from "./DropOffDetails";

const MultiDropOffLocationsPopup = ({
  show,
  locations,
  onClosePressed,
  fontScale,
}) => {
  const popupStyles = makePopupStyle(fontScale);
  const style = makeLocalStyle(fontScale);
  const { t, i18n } = useTranslation();
  return (
    <CtModal
      show={show}
      onClosePressed={onClosePressed}
      headerElement={
        <Text style={popupStyles.textModalHeader}>{t("other:jobTab.dropOffLocations")}</Text>
      }
      noBtn={true}
    >
      {locations &&
        locations?.map((item, idx) => {
          return (
            <DropOffDetails
              item={item}
              t={t}
              fontScale={fontScale}
              style={style}
              id={idx}
              key={idx}
            />
          );
        })}
    </CtModal>
  );
};

const makeLocalStyle = (fontScale) =>
  StyleSheet.create({
    body: {
      display: "flex",
      flexDirection: "row",
      marginVertical: 15,
      backgroundColor: "rgba(239,242,241, 0.5)",
      padding: 3,
      borderRadius: 10,
    },
    header: {
      fontSize: getFontByFontScale(fontScale, 30, 16),
      fontWeight: "600",
    },
    subtitle: {
      color: materialTheme.COLORS.BLACK,
      fontWeight: "200",
      fontSize: getFontByFontScale(fontScale, 30, 16),
    },
    timing: {
      display: "flex",
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "flex-start",
      marginTop: 10,
      color: materialTheme.COLORS.BLACK,
      fontWeight: "200",
    },
  });

export default MultiDropOffLocationsPopup;
