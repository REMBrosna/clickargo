import React, { useState } from "react";
import {
    Pressable,
    StyleSheet,
    View,
    Image,
    SafeAreaView,
    BackHandler,
    Alert,
} from "react-native";
import { useFonts } from "expo-font";
import { StatusBar } from "expo-status-bar";
import { Link, useRouter } from "expo-router";
import GliTextInput from "../components/GliTextInput";
import { horizontalScale, verticalScale, moderateScale } from "../constants/Metrics";
import ClictruckModal from "../components/modal/ClictruckModal";
import FontAwesome from "@expo/vector-icons/FontAwesome";
import axios from "axios";
import * as SecureStore from "expo-secure-store";
import { Button, Dialog, Input, Text } from "@rneui/themed";
import { GliLoading } from "../components/clictruck/GliLoading";
// import useHttp from "../hooks/http";
interface LoginState {
    id: string;
    password: string;
}

export default function Login() {
    // login page field state
    const [loginState, setLoginState] = React.useState<LoginState>({
        id: "",
        password: "",
    });
    const [forgotPassState, setForgotPassState] = React.useState({
        username: "",
        password: "",
    });

    const [backClickCount, setBackClickCount] = React.useState<number>(0);

    const [loadingVisible, setLoadingVisible] = useState<boolean>(false);

    // modal login page state
    const [showForgotModal, setShowForgotModal] = React.useState({
        firstPage: false,
        secondPage: false,
    });
    const [errorModalState, setErrorModalState] = React.useState({
        message: "",
        showErrorModal: false,
    });
    const messageSubmitSuccess =
        "We will send Whatsapp message to you the new password soon. if you do not get any notifications within 20 minutes please call the hotlines. thanks";
    const router = useRouter();

    const [fontsLoaded] = useFonts({
        "pop-black": require("../assets/fonts/poppins/Poppins-Black.ttf"),
        "pop-med": require("../assets/fonts/poppins/Poppins-Medium.ttf"),
        "pop-reg": require("../assets/fonts/poppins/Poppins-Regular.ttf"),
    });

    async function checkToken() {
        let value = await SecureStore.getItemAsync("authToken");
        if (value) {
            console.log("authtoken", value);
            router.replace("/(tabs)/new");
        }
    }

    React.useEffect(() => {
        console.log("login page useeffect");
        console.log("checking token");
        checkToken();

        const backAction = () => {
            Alert.alert("Hold on!", "Are you sure want to exit?", [
                {
                    text: "cancel",
                    onPress: () => null,
                    style: "cancel",
                },
                { text: "yes", onPress: () => BackHandler.exitApp() },
            ]);
            return true;
        };

        const backHandler = BackHandler.addEventListener("hardwareBackPress", backAction);

        return () => backHandler.remove();

    }, []);

    async function storeToken(token: string) {
        await SecureStore.setItemAsync("authToken", token);
        router.replace("/(tabs)/new");
    }

    async function storeCred(cred: object) {
        console.log("store login credential", JSON.stringify(cred));
        await SecureStore.setItemAsync("cred", JSON.stringify(cred));
    }

    const sendLogin = () => {
        const targetURL =
            process.env.EXPO_PUBLIC_BACKEND_URL + "/api/v1/clickargo/clictruck/mobile/auth/login";
        const body = loginState;
        console.log("login State: ", loginState);
        // const body = { id: loginState?.id, password: loginState?.password };
        setLoadingVisible(true);
        axios
            .post(targetURL, body)
            .then((response) => {
                console.log("response; ", response);
                if (response?.data?.loginStatus === "AUTHORIZED_LOGIN" && response?.data?.token) {
                    storeCred(loginState);
                    storeToken(response?.data?.token);
                    setLoadingVisible(false);
                } else {
                    console.log(response?.data);
                    setLoadingVisible(false);
                    setErrorModalState({ message: response?.data?.err, showErrorModal: true });
                }
            })
            .catch((error) => {
                console.error("Error: ", error);
                setLoadingVisible(false);
                setErrorModalState({
                    message: error,
                    showErrorModal: true,
                });
            });
    };



    const handleLogin = () => {
        if (loginState.id.length === 0 || loginState.password.length === 0) {
            setErrorModalState({
                ...errorModalState,
                message: "Invalid Username/Password",
                showErrorModal: true,
            });
        } else {
            sendLogin();
            // router.replace("/(tabs)");
        }
    };

    const handleForgotPassSubmit = () => {
        if (forgotPassState.username.length === 0 || forgotPassState.password.length === 0) {
            setShowForgotModal({ firstPage: false, secondPage: false });
            setErrorModalState({ showErrorModal: true, message: "Please input requirement field" });
            // setShowErrorModal(true);
        } else {
            setShowForgotModal({ secondPage: false, firstPage: false });
            setTimeout(() => {
                setShowForgotModal({ firstPage: false, secondPage: true });
            }, 500);
        }
    };

    if (!fontsLoaded) {
        return null;
    }
    return (
        <SafeAreaView style={{ backgroundColor: "#fff" }}>
            <StatusBar style="auto" />
            <View style={styles.body}>
                <View style={styles.logoContainer}>
                    <Image
                        source={require("../assets/images/Clic-Logo_Colour-300x187.png")}
                        style={{
                            height: verticalScale(80),
                            width: horizontalScale(130),
                            resizeMode: "contain",
                        }}
                    />
                </View>
                <View style={styles.loginContainer}>
                    <View style={styles.loginForm}>
                        <Input value={loginState.id}
                            autoCapitalize="characters"
                            onChangeText={(e) => setLoginState({ ...loginState, id: e })}
                            leftIcon={{ name: "person-outline", type: "ionicon", color: "#fff" }}
                            style={styles.loginInput}
                            inputContainerStyle={{ borderBottomWidth: 0 }} />
                        <Input secureTextEntry={true}
                            value={loginState.password}
                            onChangeText={(e) => setLoginState({ ...loginState, password: e })}
                            leftIcon={{ name: "key-outline", type: "ionicon", color: "#fff" }}
                            style={styles.loginInput}
                            inputContainerStyle={{ borderBottomWidth: 0 }} />

                        <Button
                            containerStyle={{
                                alignSelf: "flex-end",
                            }}
                            title="Forgot Password"
                            type="clear"
                            titleStyle={{ color: '#f9f3ee', fontSize: 16 }}
                            onPress={() =>
                                setShowForgotModal({ firstPage: true, secondPage: false })
                            }
                        />
                        <Button onPress={() => handleLogin()}
                            buttonStyle={{
                                borderRadius: 20,
                                width: "100%",
                                backgroundColor: "#13B1ED"
                            }}

                            containerStyle={{
                                width: 200,
                                marginHorizontal: 50,
                                marginVertical: 10,

                            }} title="Sign In" />
                    </View>
                </View>
            </View>
            {/* For loading upon login */}
            <GliLoading title="Please wait..." isVisible={loadingVisible}
                loadingProps={{ size: "large" }}
                titleStyle={{ textAlign: "center" }}></GliLoading>
            {/* error modal */}
            <ClictruckModal
                customContainer={styles.customErrorModalContainer}
                show={errorModalState.showErrorModal}   >
                <React.Fragment>
                    <View
                        style={[
                            styles.modalHeader,
                            {
                                backgroundColor: "red",
                                borderTopLeftRadius: moderateScale(10),
                                borderTopRightRadius: moderateScale(10),
                                paddingHorizontal: horizontalScale(10),
                            },
                        ]}
                    >
                        <Text style={[styles.textModalHeader, { color: "#fff" }]}>ERROR</Text>
                        <Pressable
                            onPress={() =>
                                setErrorModalState({ showErrorModal: false, message: "" })
                            }
                        >
                            <FontAwesome name="times" size={moderateScale(25)} color={"#fff"} />
                        </Pressable>
                    </View>
                    <View style={[styles.modalBody, { paddingHorizontal: horizontalScale(10) }]}>
                        <View>
                            <GliTextInput
                                colorLabel="black"
                                value={errorModalState.message}
                                multiline={true}
                                editable={false}
                                label="Reason "
                            />
                        </View>
                    </View>
                </React.Fragment>
            </ClictruckModal>
            {/* forgot pass modal: 1st page */}
            <ClictruckModal show={showForgotModal.firstPage}>
                <React.Fragment>
                    <View style={styles.modalHeader}>
                        <FontAwesome name="address-book" size={moderateScale(30)} />
                        <Text style={styles.textModalHeader}>Forgot Password</Text>
                        <Pressable
                            onPress={() =>
                                setShowForgotModal({ ...showForgotModal, firstPage: false })
                            }
                        >
                            <FontAwesome name="times" size={moderateScale(30)} />
                        </Pressable>
                    </View>
                    <View style={styles.modalBody}>
                        <View style={{ marginBottom: 20, paddingHorizontal: horizontalScale(10) }}>
                            <GliTextInput
                                label="Username"
                                colorLabel="black"
                                value={forgotPassState.username}
                                onChangeText={(e) => setForgotPassState({ ...forgotPassState, username: e })}
                            />
                        </View>
                        <View style={{ paddingHorizontal: horizontalScale(10) }}>
                            <GliTextInput
                                label="Password"
                                colorLabel="black"
                                secureTextEntry={true}
                                value={forgotPassState.password}
                                onChangeText={(e) =>
                                    setForgotPassState({ ...forgotPassState, password: e })}
                            />
                        </View>
                        <View
                            style={{
                                width: "100%",
                                alignItems: "flex-end",
                                paddingHorizontal: horizontalScale(10),
                            }}
                        >
                            <Pressable
                                style={styles.submitModalButton}
                                onPress={() => handleForgotPassSubmit()}
                            >
                                <Text style={styles.textModalBody}>Submit</Text>
                            </Pressable>
                        </View>
                    </View>
                </React.Fragment>
            </ClictruckModal>
            {/* forgot pass modal: 2nd page  */}
            <ClictruckModal show={showForgotModal.secondPage}>
                <React.Fragment>
                    <View style={styles.modalHeader}>
                        <FontAwesome name="address-book" size={moderateScale(30)} />
                        <Text style={styles.textModalHeader}>Forgot Password</Text>
                        <Pressable
                            onPress={() =>
                                setShowForgotModal({ ...showForgotModal, secondPage: false })
                            }
                        >
                            <FontAwesome name="times" size={moderateScale(30)} />
                        </Pressable>
                    </View>
                    <View style={styles.modalBody}>
                        <View>
                            <GliTextInput
                                label=""
                                style={styles.customTextInput}
                                editable={false}
                                multiline={true}
                                value={messageSubmitSuccess}
                            />
                        </View>
                    </View>
                </React.Fragment>
            </ClictruckModal>
        </SafeAreaView >
    );
}

const styles = StyleSheet.create({
    // container
    body: {
        backgroundColor: "#fff",
        alignItems: "center",
        justifyContent: "flex-start",
        padding: 5,
        flexDirection: "column",
        height: "100%"
    },
    loginInput: {
        backgroundColor: "#fff",
        borderRadius: 5,
        padding: 5,
        borderBottomWidth: 0
    },
    logoContainer: {
        height: "20%",
        justifyContent: "center",
    },
    loginContainer: {
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#0772BA",
        height: "50%",
        width: "100%",
        overflow: "hidden",
        borderRadius: 30,
        color: "#fff",
    },
    loginForm: {
        alignItems: "center",
        width: "100%",
        paddingHorizontal: horizontalScale(20),
        color: "white",
    },
    loginButton: {
        width: "70%",
        height: 35,
        borderRadius: 8,
        backgroundColor: "#13B1ED",
        alignItems: "center",
        justifyContent: "center",
        alignContent: "center",
        marginTop: 20,
    },
    customTextInput: {
        width: "100%",
        height: 100,
        borderWidth: 1,
        borderRadius: 5,
        paddingHorizontal: 5,
        marginVertical: 10,
    },
    // modal
    customErrorModalContainer: {
        backgroundColor: "#fff",
        width: "90%",
        minHeight: verticalScale(200),
        // paddingVertical: verticalScale(10),
        // paddingHorizontal: horizontalScale(15),
        borderRadius: moderateScale(10),
    },
    modalHeader: {
        flexDirection: "row",
        borderBottomWidth: 1,
        paddingVertical: verticalScale(10),
        justifyContent: "space-between",
        alignItems: "center",
    },
    modalBody: {
        paddingVertical: verticalScale(10),
    },
    submitModalButton: {
        alignItems: "center",
        justifyContent: "center",
        width: "35%",
        height: verticalScale(30),
        borderWidth: 1,
        marginTop: 15,
        borderRadius: 5,
    },
    textModalHeader: {
        fontSize: moderateScale(16),
        fontFamily: "pop-med",
        textAlign: "left",
    },
    textModalBody: {
        fontSize: moderateScale(14),
        fontFamily: "pop-reg",
    },
});
