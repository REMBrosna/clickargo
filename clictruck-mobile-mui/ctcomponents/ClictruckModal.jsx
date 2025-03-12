import { Modal, StyleSheet, Text, View } from "react-native";
import React from "react";
import { horizontalScale, verticalScale, moderateScale } from "../constants/metrics";


const ClictruckModal = ({ show, children, customBackground, customContainer }) => {
    return (
        <Modal animationType="slide" transparent={true} visible={show}>
            <View style={customBackground ? customBackground : styles.background}>
                <View style={customContainer ? customContainer : styles.modalContainer}>
                    {children}
                </View>
            </View>
        </Modal>
    );
};

export default ClictruckModal;

const styles = StyleSheet.create({
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
        minHeight: verticalScale(200),
        paddingVertical: verticalScale(10),
        paddingHorizontal: horizontalScale(15),
        borderRadius: moderateScale(10),
    },
});
