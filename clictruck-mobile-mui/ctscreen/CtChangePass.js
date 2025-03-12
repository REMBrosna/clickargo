import React, { useState } from "react";
import { Block, theme, Input } from "galio-framework";
import { StyleSheet, Alert, useWindowDimensions } from "react-native";
import { ckComponentStyles } from "../styles/componentStyles";
import MKButton from "../components/Button";
import CtLoading from "./CtLoading";
import ErrorModal from "./ErrorModal";
import useAuth from "../hooks/useAuth";
import { sendRequest } from "../utils/http";
import { useTranslation } from "react-i18next";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import { ScrollView } from "react-native-gesture-handler";
import { getFontByFontScale } from "../constants/utils";

const changePwd = "/api/v1/clickargo/clictruck/mobile/auth/changePwd";

const CtChangePass = (props) => {
  const navigation = useNavigation();
  const { t } = useTranslation();
  const { user } = useAuth();
  const { fontScale } = useWindowDimensions();

  const defaultInputData = {
    oldPass: "",
    newPass: "",
  };

  const [errorModalState, setErrorModalState] = useState({
    message: "",
    showErrorModal: false,
  });

  const [loading, setLoading] = useState(false);
  const [inputData, setInputData] = useState(defaultInputData);
  const [confirmPassword, setConfirmPassword] = useState();

  const isValidPassword = (password) => {
    let reg = /^(?=.*[0-9])(?=.*[@#$%^&+=!])(?=.*[a-z])(?=.*[A-Z]).{6,999}$/;
    return reg.test(password);
  };

  async function changePassword() {
    console.log("inputData", inputData);

    try {
      setLoading(true);
      if (
        confirmPassword === "" ||
        inputData?.newPass === "" ||
        inputData?.oldPass === ""
      ) {
        setLoading(false);
        Alert.alert("Warning", "Password must be entry", [
          { text: "OK", onPress: () => console.log("OK") },
        ]);
      } else if (confirmPassword === inputData?.newPass) {
        if (isValidPassword(inputData?.newPass)) {
          const res = await sendRequest(changePwd, "put", inputData);
          console.log("data", res);

          if (res.status === "SUCCESS") {
            Alert.alert("Success", "Your password already changed", [
              { text: "OK", onPress: () => console.log("OK") },
            ]);
            setLoading(false);
            setInputData(defaultInputData);
          } else {
            setLoading(false);
            setErrorModalState({
              ...errorModalState,
              message: res?.response.data.err.msg,
              showErrorModal: true,
            });
          }
        } else {
          setLoading(false);
          Alert.alert(
            "Warning",
            "Password must be at least 6 characters, containing uppercase letter, lowercase letter, number and special character.",
            [{ text: "OK", onPress: () => console.log("OK") }]
          );
        }
      } else {
        setLoading(false);
        Alert.alert(
          "Warning",
          "Confirm Password must be same with new Password",
          [{ text: "OK", onPress: () => console.log("OK") }]
        );
      }
    } catch (error) {
      console.log("error", error);

      setLoading(false);
      setErrorModalState({
        ...errorModalState,
        message: error?.err?.msg,
        showErrorModal: true,
      });
    }
  }

  const handleInputChange = (name, value) => {
    setInputData({ ...inputData, [name]: value });
  };

  const handleModalClose = () => {
    setErrorModalState({ ...errorModalState, showErrorModal: false });
  };

  useFocusEffect(
    React.useCallback(() => {
      navigation.closeDrawer();
    }, [])
  );

  return (
    <ScrollView
      style={{ width: "100%", height: "100%" }}
      contentContainerStyle={{
        width: "100%",
        alignItems: "center",
        paddingHorizontal: 10,
      }}
      showsVerticalScrollIndicator={false}
      automaticallyAdjustContentInsets
    >
      <Block card flex style={localStyle.cardContainer}>
        <Input
          label={t("password:form.currentPassword")}
          placeholder={t("password:form.currentPassword")}
          password
          viewPass
          color={theme.COLORS.BLOCK}
          onChangeText={(e) => handleInputChange("oldPass", e)}
        />
        <Input
          label={t("password:form.newPassword")}
          placeholder={t("password:form.newPassword")}
          password
          viewPass
          color={theme.COLORS.BLOCK}
          onChangeText={(e) => handleInputChange("newPass", e)}
        />
        <Input
          label={t("password:form.confirmPassword")}
          placeholder={t("password:form.confirmPassword")}
          password
          viewPass
          color={theme.COLORS.BLOCK}
          onChangeText={(e) => setConfirmPassword(e)}
        />
        <Block style={localStyle.buttonContainer}>
          <MKButton
            gradient
            size="large"
            shadowless
            // color={materialTheme.COLORS.BUTTON_COLOR}
            style={ckComponentStyles.ckbuttons}
            onPress={() => changePassword()}
            fontSize={getFontByFontScale(fontScale, 25, null)}
          >
            {t("password:button.changePassword")}
          </MKButton>
        </Block>
        <CtLoading
          isVisible={loading}
          title="Please wait"
          onBackdropPress={() => setLoading(false)}
          onRequestClose={() => setLoading(false)}
        />
        <ErrorModal
          show={errorModalState?.showErrorModal}
          errorMsg={errorModalState?.message}
          onClosePressed={() => handleModalClose()}
        />
      </Block>
    </ScrollView>
  );
};

const localStyle = StyleSheet.create({
  cardContainer: {
    borderRadius: 20,
    padding: 12,
    marginVertical: 8,
    elevation: 4,
    shadowColor: theme.COLORS.BLACK,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 3,
    shadowOpacity: 0.1,
    elevation: 2,
    backgroundColor: "#fff",
    alignItems: "center",
    flexDirection: "column",
    borderWidth: 0.5,
    // width: "100%"
  },
  formContainer: {
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start",
    marginBottom: "-1%",
    borderColor: "#ccc",
    // width: "100%",
    // borderWidth: 1,
    // borderColor: "green"
  },
  buttonContainer: {
    alignItems: "center",
    width: "100%",
    borderColor: "#ccc",
    paddingTop: 10,
    paddingBottom: 10,
  },
});

export default CtChangePass;
