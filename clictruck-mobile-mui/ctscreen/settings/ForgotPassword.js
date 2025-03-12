import React, { useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Text, theme, Input, Button } from "galio-framework";
import { popupStyles } from "./commonstyles";
import { useTranslation } from "react-i18next";
import { View } from "react-native";
import { materialTheme } from "../../constants";
import GliTextInput from "../../ctcomponents/GliTextInput";
import { sendRequest } from "../../utils/httpUtil";
import { getFontByFontScale } from "../../constants/utils";

const forgotPassUrl = "/api/v1/clickargo/clictruck/mobile/auth/forgotpwd";

export default function ForgotPassword({
  show,
  onClosePressed,
  forgetPassword,
  fontScale,
}) {
  const { t } = useTranslation();
  const successMsg =
    "We will Whatsapp you the new Password soon... if you do not get any notification within 20 minutes please call the hotline. Thanks";
  const [submitted, setSubmitted] = useState(false);
  const [invalid, setInvalid] = useState(false);
  const [input, setInput] = useState({drvPhone:"+65"});

  function handleInputChange(key, value) {
    setInput({ ...input, [key]: value });
    // console.log("forgot pass input",{...input, [key]:value});
  }

  async function handleSubmit() {
    console.log("submit forgot pass", input);
    try {
      const result = await sendRequest(forgotPassUrl, "POST", input);
      if (result) {
        console.log("forgot pass result: ", result);
        setSubmitted(true);
      } else {
        setInvalid(true);
      }
    } catch (error) {
      console.log("forgot pass error ", error.response.data);
    }
  }

  return (
    <CtModal
      show={show}
      onClosePressed={onClosePressed}
      headerElement={
        <Text
          style={[
            popupStyles.textModalHeader,
            { fontSize: getFontByFontScale(fontScale, 25, null) },
          ]}
        >
          {t("password:forgotPass")}
        </Text>
      }
      noBtn={true}
    >
      {submitted ? (
        <View style={[popupStyles.modalBody, { flexDirection: "column" }]}>
          <GliTextInput value={successMsg} multiline={true} readOnly={true} />
        </View>
      ) : (
        <View style={[popupStyles.modalBody, { flexDirection: "column" }]}>
          <Text>{t("password:form.id")}</Text>
          <Input
            placeholder={t("password:form.id")}
            autoCapitalize="characters"
            color={theme.COLORS.BLOCK}
            onChangeText={(text) => handleInputChange("drvMobileId", text)}
          />
          <Text>Phone Number: e.g., +6512345678</Text>
          <Input
            placeholder={t("password:form.phone")}
            type="phone-pad"
            color={theme.COLORS.BLOCK}
            value={input?.drvPhone}
            onChangeText={(text) => handleInputChange("drvPhone", text)}
          />
          {invalid && (
            <Text style={{ color: "red", fontWeight: "300" }}>
              User Id and Phone Number Not Match!
            </Text>
          )}
          <View
            style={{
              flexDirection: "row",
              flexWrap: "wrap",
              justifyContent: "space-evenly",
            }}
          >
            <Button
              round
              color="transparent"
              shadowless
              style={[
                popupStyles.modalButtons,
                { borderColor: materialTheme.COLORS.CKPRIMARY },
              ]}
              onPress={handleSubmit}
            >
              <Text
                style={{
                  color: materialTheme.COLORS.CKPRIMARY,
                  fontWeight: 500,
                  textAlign: "center",
                  fontSize: getFontByFontScale(fontScale, 25, null),
                }}
              >
                {t("password:button.submit")}
              </Text>
            </Button>
          </View>
        </View>
      )}
    </CtModal>
  );
}
