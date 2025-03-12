import { Modal, Pressable, StyleSheet } from "react-native";
import React from "react";
import { Block, Button, Icon, Text } from "galio-framework";
import {
  getFontByFontScale,
  horizontalScale,
  moderateScale,
  verticalScale,
} from "../constants/utils";
import { materialTheme } from "../constants";
import { useTranslation } from "react-i18next";
import { ScrollView } from "react-native-gesture-handler";

const CtModal = ({
  show,
  headerElement,
  onClosePressed,
  onYesPressed,
  noLabel = "No",
  yesLabel = "Confirm",
  noBtn = false,
  customBtnLabel,
  fontScale,
  scroll = true,
  children,
}) => {
  const { t } = useTranslation();
  const styles = makeStyles(fontScale);
  yesLabel = yesLabel === "Confirm" ? t("button:modal.confirm") : yesLabel;
    noLabel = noLabel === "No" ? t("button:modal.no") : noLabel;
  return (
    <Modal animationType="slide" transparent={true} visible={show}>
      <Block card style={styles.background}>
        <Block style={styles.modalContainer}>
          <Block style={styles.modalHeader}>
            {headerElement}
            <Pressable onPress={onClosePressed}>
              <Icon
                name="times"
                family="font-awesome"
                size={moderateScale(20)}
                color={materialTheme.COLORS.CKYELLOWSECONDARY}
              />
            </Pressable>
          </Block>
          {scroll ? (
            <ScrollView nestedScrollEnabled>
              <Block style={styles.modalBody}>{children}</Block>
            </ScrollView>
          ) : (
            <Block style={styles.modalBody}>{children}</Block>
          )}

          <Block
            style={{
              flexDirection: "row",
              justifyContent: "center",
              paddingBottom: 10,
            }}
          >
            {noBtn ? null : !customBtnLabel ? (
              <>
                <Button
                  round
                  color={materialTheme.COLORS.CKYELLOWSECONDARY}
                  style={[styles.modalButtons]}
                  onPress={onClosePressed}
                >
                  <Text
                    style={{
                      color: materialTheme.COLORS.WHITE,
                      fontWeight: 500,
                      fontSize: getFontByFontScale(fontScale, 25, null),
                    }}
                  >
                    {noLabel}
                  </Text>
                </Button>
                <Button
                  round
                  color={materialTheme.COLORS.CKPRIMARY}
                  style={[styles.modalButtons]}
                  onPress={onYesPressed}
                >
                  <Text
                    style={{
                      color: materialTheme.COLORS.WHITE,
                      fontWeight: 500,
                      fontSize: getFontByFontScale(fontScale, 25, null),
                    }}
                  >
                    {yesLabel}
                  </Text>
                </Button>
              </>
            ) : (
              customBtnLabel
            )}
          </Block>
        </Block>
      </Block>
    </Modal>
  );
};

export default CtModal;

const makeStyles = (fontScale) =>
  StyleSheet.create({
    background: {
      backgroundColor: "rgba(100,100,100, 0.5)",
      alignItems: "center",
      justifyContent: "center",
      height: "100%",
      width: "100%",
      position: "absolute",
    },
    modalContainer: {
      backgroundColor: "#fff",
      width: "90%",
      maxHeight:"80%",
      minHeight: verticalScale(200),
      borderRadius: moderateScale(10),
    },
    modalHeader: {
      flexDirection: "row",
      borderTopStartRadius: 10,
      borderTopEndRadius: 10,
      paddingVertical: verticalScale(10),
      justifyContent: "space-between",
      alignItems: "center",
      borderBottomWidth: 0.25,
      backgroundColor: materialTheme.COLORS.CKSECONDARY,
      padding: 10,
    },
    modalBody: {
      paddingVertical: verticalScale(10),
      paddingHorizontal: horizontalScale(15),
    },
    modalButtons: {
      // ...ckComponentStyles.ckbuttons,
      width: "45%",
      flexDirection: "row",
      justifyContent: "center",
      alignItems: "center",
      //   borderWidth: 1,
    },
  });
