import React, { useEffect, useRef, useState } from "react";
import {
  StyleSheet,
  Dimensions,
  KeyboardAvoidingView,
  Alert,
  Platform,
  Image,
  useWindowDimensions,
  TouchableOpacity
} from "react-native";
import { Block, Button, Input, Text, theme } from "galio-framework";

import { LinearGradient } from "expo-linear-gradient";
import { materialTheme } from "../constants/";
import {
  HeaderHeight,
  getFontByFontScale,
  horizontalScale,
  verticalScale,
} from "../constants/utils";
import Images from "../constants/Images";
import useAuth from "../hooks/useAuth";
import CtLoading from "./CtLoading";
import ErrorModal from "./ErrorModal";
import { useNavigation } from "@react-navigation/native";
import ForgotPassword from "./settings/ForgotPassword";
import SelectBackendServer from "./settings/SelectBackendServer"
import { getBackendServer } from "../config/ConfigBackendUrl";


const { height, width } = Dimensions.get("window");

let LoginStatus = {};

const LoginStatusArray = [
  "AUTHORIZED_REG",
  "AUTHORIZED_IGNORE",
  "UNAUTHENTICATED",
  "UNAUTHORIZED",
  "AUTHORIZED_LOGIN",
  "AUTHORIZED_LOGIN_CHNG_PW_RQD",
  "ACCOUNT_SUSPENDED",
  "ACCOUNT_INACTIVE",
  "USER_SESSION_EXISTING",
  "PASSWORD_RESET_SUCCESS",
  "SESSION_EXPIRED_OR_INACTIVE",
];

LoginStatusArray.map(
  (status) => (LoginStatus = { ...LoginStatus, [status]: status })
);

const SignIn = (props) => {
  const navigation = useNavigation();
  const { fontScale } = useWindowDimensions();

  const { login, isAuthenticated } = useAuth();
  const [loginState, setLoginState] = useState({
    id: "",
    password: "",
    active: { id: false, password: false },
  });
  const [loading, setLoading] = useState(false);
  const [errorModalState, setErrorModalState] = useState({
    message: "",
    showErrorModal: false,
  });

  const [modalForgotPassVisible, setModalForgotPassVisible] = useState(false);
  const [serverName, setServerName] = useState("");

  const ref_inputPass = useRef(null);

  const handleChange = (name, value) => {
    setLoginState({ ...loginState, [name]: value });
  };

  const toggleActive = (name) => {
    const { active } = loginState;
    active[name] = !active[name];

    setLoginState({ ...loginState, active });
  };

  const handleModalClose = () => {
    setErrorModalState({ ...errorModalState, showErrorModal: false });
  };

  const handleLogin = () => {
    if (loginState.id.length === 0 || loginState.password.length === 0) {
      setErrorModalState({
        ...errorModalState,
        message: "Invalid Username/Password",
        showErrorModal: true,
      });
    } else {
      setLoading(true);
      // handleSubmit();
    }
  };

  const handleSubmit = async (event) => {
    try {
      let data = await login(loginState.id, loginState.password);

      if (data.loginStatus) {
        setLoading(false);
        switch (data.loginStatus) {
          case LoginStatus.AUTHORIZED_LOGIN: {
            navigation.navigate("App");
            break;
          }
          // case LoginStatus.AUTHORIZED_LOGIN_CHNG_PW_RQD:
          //     history.push("/session/force-change-password", userInfo.email);
          //     break;
          default:
            setTimeout(() => {
              setErrorModalState({
                ...errorModalState,
                message: data.err,
                showErrorModal: true,
              });
            }, 1000);

            break;
        }
      } else {
        setTimeout(() => {
          setErrorModalState({
            ...errorModalState,
            message: "Something went wrong",
            showErrorModal: true,
          });
        }, 1000);
      }
    } catch (e) {
      console.log("error", e);
      setLoading(false);
      let msg = e.err.msg;
      setTimeout(() => {
        setErrorModalState({
          ...errorModalState,
          message: msg,
          showErrorModal: true,
        });
      }, 1000);
    }
  };

  useEffect(() => {
    if (isAuthenticated) navigation.navigate("App");
  }, [isAuthenticated]);

  useEffect(() => {
    if (loading) {
      handleSubmit();
    }
  }, [loading]);

  // Begin handle clic image
  const TIME_LIMIT = 3000; 
  const CLICK_TIMES_TO_CHANGE_SERVERS = 9; 
  const [modalSelectServerVisible, setModalSelectServerVisible] = useState(false);

  const [clickCount, setClickCount] = useState(0);
  const [startTime, setStartTime] = useState(null);

  const handleImageClick = () => {
    const currentTime = new Date().getTime();

    if (startTime === null || currentTime - startTime <= TIME_LIMIT) {
      setClickCount(prevCount => {
        const newCount = prevCount + 1;

        if (newCount === CLICK_TIMES_TO_CHANGE_SERVERS) {

          setClickCount(0); 
          setStartTime(null); 
          setModalSelectServerVisible(true);
        }

        return newCount;
      });
    } else {
      setClickCount(1);
      setStartTime(currentTime);
    }
    
    if (startTime === null) {
      setStartTime(currentTime);
    }
  };

  useEffect(() => {
    (
      async () => {
        let server = await getBackendServer();
        // console.log("SignIn.js server ", server);
        setServerName(server?.name);
      }
    )()
  }, [modalSelectServerVisible]);

  const getServerName = () => {
    // console.log("SignIn.js  serverName ", serverName);
    if( serverName && "Production" != serverName  && "Default" != serverName) {
        return "(" + serverName + ") ";
    }
  }

  // End handle clic image

  return (
    <LinearGradient
      start={{ x: 0, y: 0 }}
      end={{ x: 0.25, y: 1.1 }}
      locations={[0.2, 1]}
      colors={["#fff", "#fff"]}
      style={[
        styles.signin,
        { flex: 1, paddingTop: theme.SIZES.BASE * 10, position: "relative" },
      ]}
    >
      <Block flex center>
        <KeyboardAvoidingView
          behavior="padding"
          enabled={Platform.OS === "android" ? true : false}
        >
          <Block
            center
            style={{ paddingTop: 0, paddingVertical: 0, paddingBottom: 17 }}
          >
          <TouchableOpacity onPress={() => handleImageClick()}>
                <Image
                  source={require("../assets/images/Clic-Logo_Colour-300x187.png")}
                  style={{
                    height: verticalScale(80),
                    width: horizontalScale(130),
                    resizeMode: "contain",
                  }}
                />
          </TouchableOpacity>
          </Block>
          <Image
            source={require("../assets/images/transport.png")}
            style={{
              // position: "absolute",
              height: verticalScale(350),
              width: horizontalScale(350),
              resizeMode: "contain",
            }}
          />
        </KeyboardAvoidingView>
      </Block>

      <KeyboardAvoidingView
        behavior="position"
        enabled={Platform.OS === "ios" ? true : false}
      >
        <Block
          flex
          style={{
            position: "absolute",
            bottom: 0,
            justifyContent: "center",
            alignItems: "center",
            width: "100%",
            backgroundColor: "#0772BA",
            borderWidth: 0,
            borderTopLeftRadius: 20,
            borderTopRightRadius: 20,
          }}
        >
          <LinearGradient
            start={{ x: 0.3, y: -0.3 }}
            end={{ x: 0.2, y: 3.1 }}
            locations={[0.1, 0.4, 0.8]}
            colors={["#fff", "#13b1ed", "#13b1ed"]}
            style={{ width: "100%" }}
          >
            <Block center style={{ marginTop: 40 }}>
              <Input
                color={"#13b1ed"}
                placeholder="Id"
                type="default"
                autoCapitalize="characters"
                bgColor="white"
                onBlur={() => toggleActive("id")}
                onFocus={() => toggleActive("id")}
                placeholderTextColor={"#13b1ed"}
                onChangeText={(text) => handleChange("id", text)}
                style={[
                  styles.input,
                  loginState.active.id ? styles.inputActive : null,
                ]}
                returnKeyType="next"
                onSubmitEditing={() => ref_inputPass.current.focus()}
                blurOnSubmit={false}
              />
              <Input
                password
                viewPass
                color={"#13b1ed"}
                iconColor={"#13b1ed"}
                placeholder="Password"
                bgColor="white"
                onBlur={() => toggleActive("password")}
                onFocus={() => toggleActive("password")}
                placeholderTextColor={"#13b1ed"}
                onChangeText={(text) => handleChange("password", text)}
                style={[
                  styles.input,
                  loginState.active.password ? styles.inputActive : null,
                ]}
                onRef={(ref) => (ref_inputPass.current = ref)}
                onSubmitEditing={handleLogin}
              />
              <Text
                color={theme.COLORS.WHITE}
                size={theme.SIZES.FONT * 0.75}
                onPress={() => setModalForgotPassVisible(true)}
                style={{
                  alignSelf: "flex-end",
                  lineHeight: theme.SIZES.FONT * 2,
                  fontWeight: "500",
                  fontSize: getFontByFontScale(fontScale, 25, null),
                }}
              >
                {getServerName()}
                Forgot your password?
              </Text>

            </Block>
            <Block
              center
              flex
              style={{ marginBottom: horizontalScale(70), marginTop: 20 }}
            >
              <Button
                size="large"
                shadowless
                color={"#13b1ed"}
                style={{ height: 48, borderWidth: 0, borderRadius: 20 }}
                textStyle={{
                  fontWeight: "800",
                  fontSize: getFontByFontScale(fontScale, 25, null),
                }}
                onPress={() => handleLogin()}
              >
                Sign In
              </Button>
              {/* TO BE REMOVED IN PRODUCTION !! ----------------------------------------- !! */}
              {/* <Button
                size="large"
                shadowless
                color={"transparent"}
                style={{ height: 48, borderWidth: 0, borderRadius: 20 }}
                textStyle={{ fontWeight: "800" }}
                onPress={() => {
                  navigation.navigate("DeveloperTool");
                }}
              >
                Developer Tool
              </Button> */}
              {/* ----------------------------- !! ----------------------------- */}
            </Block>
          </LinearGradient>
        </Block>
      </KeyboardAvoidingView>

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
      {modalForgotPassVisible && (
        <ForgotPassword
          fontScale={fontScale}
          show={modalForgotPassVisible}
          onClosePressed={() => {
            setModalForgotPassVisible(false);
          }}
        />
      )}
      {modalSelectServerVisible && (
        <SelectBackendServer
          fontScale={fontScale}
          show={modalSelectServerVisible}
          onClosePressed={() => {
            setModalSelectServerVisible(false);
          }}
        />
      )}
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  signin: {
    marginTop: Platform.OS === "android" ? -HeaderHeight : 0,
  },
  social: {
    width: theme.SIZES.BASE * 3.5,
    height: theme.SIZES.BASE * 3.5,
    borderRadius: theme.SIZES.BASE * 1.75,
    justifyContent: "center",
    shadowColor: "rgba(0, 0, 0, 0.3)",
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowRadius: 8,
    shadowOpacity: 1,
  },
  input: {
    width: width * 0.9,
    borderWidth: 0,
    borderRadius: 10,
    borderBottomWidth: 1,
    borderBottomColor: materialTheme.COLORS.PLACEHOLDER,
  },
  inputActive: {
    borderBottomColor: "#13b1ed",
  },
});

export default SignIn;
