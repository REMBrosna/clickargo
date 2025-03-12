import { Pressable, SafeAreaView, StyleSheet, Text, TextInput, View } from "react-native";
import React from "react";
import { horizontalScale, moderateScale, verticalScale } from "../../constants/Metrics";

import { Link, useRouter } from "expo-router";
import { MaterialCommunityIcons, FontAwesome } from "@expo/vector-icons";
import Colors from "../../constants/Colors";
import { useFonts } from "expo-font";
import ClictruckModal from "../../components/modal/ClictruckModal";
import DropDownPicker from "react-native-dropdown-picker";
import * as SecureStore from 'expo-secure-store';
import AsyncStorage from "@react-native-async-storage/async-storage";

const langOpt = [
    { label: "Indonesia", value: "id" },
    { label: "English", value: "en" },
];

const passDef = {
    oldPass: "",
    newPass: "",
};

async function deleteToken() {
    await SecureStore.deleteItemAsync("authToken");
};

async function deleteLocalData() {
    await AsyncStorage.removeItem("newJobList");
    console.log("newJobList deleted");
    await AsyncStorage.removeItem("ongoingJob");
    console.log('ongoingJob deleted');
};


const AccountMenuProfile = () => {
    const [showDropdown, setShowDropdown] = React.useState(false);
    const [selectedLanguage, setSelectedLanguage] = React.useState("");
    const [langItems, setLangItems] = React.useState(langOpt);
    // const { activePage, setActivePage } = React.useContext(AppContext);
    // modal show state
    const [modalLangVisible, setModalLangVisible] = React.useState(false);
    const [modalPassChangeVisible, setModalPassChangeVisible] = React.useState(false);
    const [modalSignOutVisible, setModalSignOutVisible] = React.useState(false);

    const [changePassState, setChangePassState] = React.useState(passDef);

    const router = useRouter();
    const [fontsLoaded] = useFonts({
        "pop-black": require("../../assets/fonts/poppins/Poppins-Black.ttf"),
        "pop-med": require("../../assets/fonts/poppins/Poppins-Medium.ttf"),
        "pop-reg": require("../../assets/fonts/poppins/Poppins-Regular.ttf"),
    });
    if (!fontsLoaded) {
        return null;
    }
    return (
        <SafeAreaView>
            <View style={styles.container}>
                <Pressable onPress={() => setModalLangVisible(true)} style={styles.button}>
                    <FontAwesome name="language" size={moderateScale(30)} style={styles.icon} />
                    <Text style={styles.textButton}>Language</Text>
                    <FontAwesome
                        name="caret-right"
                        size={moderateScale(30)}
                        style={[styles.icon, { textAlign: "right" }]}
                    />
                </Pressable>
                <Pressable style={styles.button} onPress={() => setModalPassChangeVisible(true)}>
                    <FontAwesome name="key" size={moderateScale(30)} style={styles.icon} />
                    <Text style={styles.textButton}>Change Password</Text>
                    <FontAwesome
                        name="caret-right"
                        size={moderateScale(30)}
                        style={[styles.icon, { textAlign: "right" }]}
                    />
                </Pressable>
                <Pressable style={styles.button} onPress={() => router.replace("/(tabs)/history")}>
                    <FontAwesome name="bars" size={moderateScale(30)} style={styles.icon} />
                    <Text style={styles.textButton}>Job History</Text>
                    <FontAwesome
                        name="caret-right"
                        size={moderateScale(30)}
                        style={[styles.icon, { textAlign: "right" }]}
                    />
                </Pressable>
                <Pressable style={styles.button} onPress={() => setModalSignOutVisible(true)}>
                    <FontAwesome name="sign-out" size={moderateScale(30)} style={styles.icon} />
                    <Text style={styles.textButton}>Sign Out</Text>
                    <FontAwesome
                        name="caret-right"
                        size={moderateScale(30)}
                        style={[styles.icon, { textAlign: "right" }]}
                    />
                </Pressable>

                <Pressable style={styles.button} onPress={() => deleteLocalData()}>
                    <FontAwesome name="sign-out" size={moderateScale(30)} style={styles.icon} />
                    <Text style={styles.textButton}>Delete Job Data</Text>
                    <FontAwesome
                        name="caret-right"
                        size={moderateScale(30)}
                        style={[styles.icon, { textAlign: "right" }]}
                    />
                </Pressable>
            </View>


            {/* modal */}
            {/* modal change language */}
            <ClictruckModal show={modalLangVisible}>
                <>
                    <View style={styles.modalHeader}>
                        <FontAwesome name="language" size={moderateScale(30)} />
                        <Text style={styles.textModalHeader}>Language Preference</Text>
                        <Pressable onPress={() => setModalLangVisible(false)}>
                            <FontAwesome name="times" size={moderateScale(30)} />
                        </Pressable>
                    </View>
                    <View style={styles.modalBody}>
                        <Text style={styles.textModalBody}>Please select language:</Text>
                        <DropDownPicker
                            open={showDropdown}
                            value={selectedLanguage}
                            items={langItems}
                            setItems={setLangItems}
                            setOpen={setShowDropdown}
                            setValue={setSelectedLanguage}
                        />
                        <View style={{ alignItems: "flex-end" }}>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => setModalLangVisible(false)}
                            >
                                <FontAwesome
                                    name="check"
                                    size={moderateScale(15)}
                                    color={"green"}
                                />
                                <Text style={styles.textModalBody}>Confirm</Text>
                            </Pressable>
                        </View>
                    </View>
                </>
            </ClictruckModal>
            {/* modal change password */}
            <ClictruckModal show={modalPassChangeVisible}>
                <>
                    <View style={styles.modalHeader}>
                        <FontAwesome name="key" size={moderateScale(30)} />
                        <Text style={styles.textModalHeader}>Change Password</Text>
                        <Pressable
                            onPress={() => {
                                setModalPassChangeVisible(false);
                                setChangePassState(passDef);
                            }}
                        >
                            <FontAwesome name="times" size={moderateScale(30)} />
                        </Pressable>
                    </View>
                    <View style={styles.modalBody}>
                        <View>
                            <Text style={styles.textModalBody}>Existing Password:</Text>
                            <TextInput
                                secureTextEntry={true}
                                style={styles.modalInputTextContainer}
                                value={changePassState.oldPass}
                                onChangeText={(e) =>
                                    setChangePassState({ ...changePassState, oldPass: e })
                                }
                            />
                        </View>
                        <View>
                            <Text style={styles.textModalBody}>New Password:</Text>
                            <TextInput
                                secureTextEntry={true}
                                style={styles.modalInputTextContainer}
                                value={changePassState.newPass}
                                onChangeText={(e) =>
                                    setChangePassState({ ...changePassState, newPass: e })
                                }
                            />
                        </View>
                        <View style={{ alignItems: "flex-end" }}>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => {
                                    setModalPassChangeVisible(false);
                                    setChangePassState(passDef);
                                }}
                            >
                                <FontAwesome
                                    name="check"
                                    size={moderateScale(15)}
                                    color={"green"}
                                />
                                <Text style={styles.textModalBody}>Confirm</Text>
                            </Pressable>
                        </View>
                    </View>
                </>
            </ClictruckModal>
            {/* modal sign out app */}
            <ClictruckModal show={modalSignOutVisible}>
                <>
                    <View style={[styles.modalHeader, { justifyContent: "center" }]}>
                        {/* <FontAwesome name="sign-out" size={moderateScale(30)} /> */}
                        <Text style={styles.textModalHeader}>Sign Out</Text>
                        {/* <Pressable
                                onPress={() => {
                                    setModalSignOutVisible(false);
                                }}
                            >
                                <FontAwesome name="times" size={moderateScale(30)} />
                            </Pressable> */}
                    </View>
                    <View style={[styles.modalBody, { justifyContent: "center", flex: 1 }]}>
                        <Text style={[styles.textModalBody, { textAlign: "center" }]}>
                            Are you sure you want to sign out?
                        </Text>
                        <View style={{ flexDirection: "row", justifyContent: "space-evenly" }}>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => {
                                    setModalSignOutVisible(false);
                                }}
                            >
                                <FontAwesome name="times" size={moderateScale(15)} color={"red"} />
                                <Text style={styles.textModalBody}>No</Text>
                            </Pressable>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => {
                                    deleteToken();
                                    setModalSignOutVisible(false);
                                    setTimeout(() => {
                                        router.replace("/");
                                    }, 500);
                                }}
                            >
                                <FontAwesome
                                    name="check"
                                    size={moderateScale(15)}
                                    color={"green"}
                                />
                                <Text style={styles.textModalBody}>Confirm</Text>
                            </Pressable>
                        </View>
                    </View>
                </>
            </ClictruckModal>
        </SafeAreaView>
    );
};

export default AccountMenuProfile;

const styles = StyleSheet.create({
    // general
    confirmButton: {
        flexDirection: "row",
        justifyContent: "space-evenly",
        alignItems: "center",
        width: "35%",
        height: 30,
        borderWidth: 1,
        marginTop: 15,
        borderRadius: 5,
    },
    container: {
        height: "100%",
        width: "100%",
        paddingHorizontal: horizontalScale(20),
        paddingTop: verticalScale(80),
        backgroundColor: "#fff",
    },
    button: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        height: verticalScale(80),
        marginBottom: 10,
    },
    textButton: {
        flex: 0.8,
        fontSize: moderateScale(16),
        paddingLeft: 10,
        fontFamily: "pop-med",
    },
    icon: {
        flex: 0.1,
    },
    // modal
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
    modalInputTextContainer: {
        width: "100%",
        height: 30,
        borderWidth: 1,
        borderRadius: 5,
        paddingHorizontal: 5,
        marginVertical: 10,
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
