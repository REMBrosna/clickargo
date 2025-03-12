import React from "react";
import { StyleSheet, TextInput, Pressable } from "react-native";
import { View, Text } from "../Themed";
import { Stack } from "expo-router";

export default function ForgotPass() {
    return (
        <View style={styles.container}>
            <Stack.Screen options={{ title: "Forget Password" }} />
            <Text style={{ alignSelf: "center", marginVertical: 20 }}>Reset Password</Text>

            <Text style={[styles.label]}>User Id</Text>
            <TextInput style={[styles.input]} />

            <Text style={[styles.label]}>Phone Number</Text>
            <TextInput style={[styles.input]} />

            <Pressable style={styles.button}>
                <Text>Submit</Text>
            </Pressable>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        // flexDirection: 'column',
        alignSelf: "center",
        width: "85%",
        // backgroundColor:'pink',
    },
    label: {
        marginLeft: 5,
        marginBottom: 5,
    },
    input: {
        height: 40,
        width: "100%",
        borderWidth: 1,
        borderColor: "#ccc",
        borderRadius: 8,
        paddingLeft: 10,
        marginBottom: 20,
    },
    button: {
        backgroundColor: "pink",
        width: 100,
        height: 40,
        alignItems: "center",
        justifyContent: "center",
        alignSelf: "flex-end",
    },
});
