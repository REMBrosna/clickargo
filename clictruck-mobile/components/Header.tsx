import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import React from "react";
import { horizontalScale, moderateScale } from "../constants/Metrics";
import FontAwesome from "@expo/vector-icons/FontAwesome";
interface HeaderProps {
    OnPressAccount?: () => void;
    ActivePage?: string;
}

const Header = ({ ActivePage, OnPressAccount }: HeaderProps) => {
    return (
        <View style={styles.headerContainer}>
            <View style={{ flexDirection: "row", alignItems: "center" }}>
                <Image
                    style={{
                        width: 50,
                        height: 50,
                        backgroundColor: "#fff",
                        resizeMode: "contain",
                    }}
                    source={require("../assets/images/Clickargo.png")}
                />
                <Text style={{ marginLeft: 10, fontSize: 18 }}>Clickargo</Text>
            </View>
            <Pressable onPress={OnPressAccount}>
                <FontAwesome
                    name="user"
                    size={moderateScale(35)}
                    color={ActivePage === "/menu" ? "#2f95dc" : "#8E8E8F"}
                />
            </Pressable>
        </View>
    );
};

export default Header;

const styles = StyleSheet.create({
    headerContainer: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        paddingHorizontal: horizontalScale(20),
        // backgroundColor: "#0972bb",
        backgroundColor: "#fff",
        height: 70,
    },
});
