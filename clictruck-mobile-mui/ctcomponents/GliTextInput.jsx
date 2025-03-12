import React from "react";
import { View, Text, TextInput, StyleSheet, TextInputProps } from "react-native";
import { moderateScale, horizontalScale, verticalScale } from "../constants/metrics";


const GliTextInput = ({ label, labelStyle, style, multiline, ...rest }) => {
    return (
        <View style={styles.container}>
            {label &&
                <Text style={labelStyle ?? styles.label}>{label}</Text>
            }
            <TextInput
                multiline={multiline}
                style={style ?? multiline ? styles.inputMultiline : styles.input}
                {...rest} // Include standard TextInputProps here
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        width: "100%",
        marginVertical: verticalScale(10),
    },
    label: {
        marginLeft: horizontalScale(5),
        marginBottom: 0,
    },
    input: {
        height: verticalScale(40),
        width: "100%",
        borderWidth: 1,
        borderColor: "#ccc",
        borderRadius: moderateScale(8),
        paddingLeft: horizontalScale(10),
        backgroundColor: "white",
    },
    inputMultiline: {
        height: verticalScale(80),
        width: "99.9%",
        borderWidth: 1,
        borderColor: "#ccc",
        borderRadius: moderateScale(8),
        paddingHorizontal: horizontalScale(5),
        backgroundColor: "white",
        textAlignVertical: 'top',
        paddingTop: 3,
    },
});

export default GliTextInput;
