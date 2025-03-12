import React, { useCallback, useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { popupStyles } from "./commonstyles";
import { Block, Icon, Text, Checkbox, Button } from "galio-framework";
import { Ionicons } from "@expo/vector-icons";
import GliTextInput from "../../ctcomponents/GliTextInput";
import {
  Image,
  Pressable,
  ScrollView,
  ActivityIndicator,
  View,
  Linking,
} from "react-native";
import {
  getFontByFontScale,
  moderateScale,
  verticalScale,
} from "../../constants/utils";
import { MaterialIcons } from "@expo/vector-icons";
import { materialTheme } from "../../constants";
import { podTerms } from "../../constants/agreement";
import useAuth from "../../hooks/useAuth";
import { useTranslation } from "react-i18next";

export default function DropOffConfirmation({
  show,
  onClosePressed,
  onChangeText,
  signatureData,
  setSignatureData,
  handleSetViewSignature,
  confirmationData,
  handleRedoPhotoUpload,
  handleSubmitConfirmation,
  fontScale,
}) {

  const { t } = useTranslation();
  const [activity, setActivity] = useState(false);
  const { user } = useAuth();

  const styles = popupStyles(fontScale);
  const url = "https://truck.sg.clickargo.com/terms-and-conditions";
  const handleOpenTermsCondition = useCallback(async () => {
    const valid = await Linking.canOpenURL(url);
    if (valid) {
      await Linking.openURL(url);
    }
  }, [url]);

  return (
    <CtModal
      show={show}
      fontScale={fontScale}
      onClosePressed={onClosePressed}
      headerElement={
        <Text style={styles.textModalHeader}>{t("other:dropOffPopup.customerConfirmation")}</Text>
      }
      customBtnLabel={
        <Block style={{ flexDirection: "row", justifyContent: "space-evenly" }}>
          <Button
            round
            color={materialTheme.COLORS.CKYELLOWSECONDARY}
            style={[
              styles.modalButtons,
              { borderColor: materialTheme.COLORS.CKYELLOWSECONDARY },
            ]}
            onPress={handleRedoPhotoUpload}
          >
            <Text
              style={{
                color: materialTheme.COLORS.WHITE,
                fontWeight: 500,
                textAlign: "center",
                fontSize: getFontByFontScale(fontScale, 25, null),
              }}
            >
              {t("other:popup.cancel")}
            </Text>
          </Button>

          {activity ? (
            <Button
              round
              color={materialTheme.COLORS.CKPRIMARY}
              style={[styles.modalButtons]}
              onPress={() => {}}
            >
              <Text
                style={{
                  color: materialTheme.COLORS.WHITE,
                  fontWeight: 500,
                  textAlign: "center",
                  fontSize: getFontByFontScale(fontScale, 25, null),
                }}
              >
                <ActivityIndicator />
              </Text>
            </Button>
          ) : (
            <Button
              round
              color={
                confirmationData?.termsAgreed && signatureData
                  ? materialTheme.COLORS.CKPRIMARY
                  : materialTheme.COLORS.DISABLED
              }
              style={[styles.modalButtons]}
              disabled={
                confirmationData?.termsAgreed && signatureData ? false : true
              }
              onPress={() => {
                setActivity(true);
                handleSubmitConfirmation();
              }}
            >
              <Text
                style={{
                  color: materialTheme.COLORS.WHITE,
                  fontWeight: 500,
                  textAlign: "center",
                  fontSize: getFontByFontScale(fontScale, 25, null),
                }}
              >
                {t("button:modal.confirm")}
              </Text>
            </Button>
          )}
        </Block>
      }
    >
      <ScrollView>
        <GliTextInput
          label={t("other:dropOffPopup.comments")}
          labelStyle={styles.label}
          multiline={true}
          onChangeText={(e) => onChangeText(e, "tlocComment")}
          value={confirmationData.tlocComment}
        />

        <Checkbox
          color="green"
          label={(
              <>
                {t("other:dropOffPopup.agreement")}&nbsp;

                  <Text
                      p
                      onPress={handleOpenTermsCondition}
                      style={{
                        paddingTop: 5,
                        fontSize: getFontByFontScale(fontScale, 16, 10),
                        paddingLeft: 30,
                      }}
                      color="blue"
                  >
                    {t("other:dropOffPopup.termCondition")}&nbsp;
                    <Ionicons
                        name="open-outline"
                        size={getFontByFontScale(fontScale, 16, 10)}
                        style={{ paddingLeft: 10 }}
                    />
                  </Text>

              </>
          )}
          style={{ marginTop: 10, marginBottom: 10 }}
          labelStyle={[styles.label, { paddingLeft: 0 }]}
          onChange={(e) => onChangeText(e, "termsAgreed")}
          initialValue={confirmationData.termsAgreed}
        />

        <GliTextInput
            label={t("other:dropOffPopup.recipientName")}
            labelStyle={styles.label}
            onChangeText={(e) => onChangeText(e, "tlocCargoRec")}
            value={confirmationData?.tlocCargoRec}
        />
        <Text style={[styles.label, { marginBottom: -10 }]}>
          {t("other:dropOffPopup.addSignature")}
        </Text>
        <Block style={[styles.photoListContainer]}>
          {signatureData ? (
            <Block
              style={{
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <Image
                style={{
                  height: moderateScale(80),
                  width: moderateScale(160),
                  resizeMode: "contain",
                }}
                source={{ uri: signatureData }}
              />
              <Pressable
                style={popupStyles.photoListItem}
                onPress={() => setSignatureData(null)}
              >
                <MaterialIcons
                  name="delete-outline"
                  size={moderateScale(15)}
                  color="black"
                />
              </Pressable>
            </Block>
          ) : (
            <Block style={{ alignItems: "center", justifyContent: "center" }}>
              <Pressable
                style={[styles.photoListItem, { height: moderateScale(100) }]}
                onPress={() => {
                  handleSetViewSignature(true);
                }}
              >
                <Icon
                  name="plus"
                  family="font-awesome"
                  size={moderateScale(15)}
                  color={"darkgrey"}
                />
              </Pressable>
            </Block>
          )}
        </Block>
      </ScrollView>
    </CtModal>
  );
}
