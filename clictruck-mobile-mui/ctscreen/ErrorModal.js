import { Modal, Pressable, StyleSheet, ScrollView } from "react-native";
import React from "react";
import { horizontalScale, moderateScale, verticalScale } from "../constants/utils";
import { Block, Icon, Input, Text } from "galio-framework";



const ErrorModal = ({ show, errorMsg, onClosePressed }) => {
    return (
        <Modal animationType="slide" transparent={true} visible={show}>
            <Block style={styles.background}>
                <Block style={styles.customErrorModalContainer}>
                    <Block
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
                        <Pressable onPress={onClosePressed} >
                            <Icon name="times" size={moderateScale(25)} color={"#fff"} family="font-awesome" />
                        </Pressable>
                    </Block>
                    <Block style={[styles.modalBody, { paddingHorizontal: horizontalScale(10),flex:1, paddingTop: horizontalScale(10) }]}>
                        {/* <Input
                            style={styles.inputMultiline} value={errorMsg}></Input> */}
                        <Block style={[styles.box,{ paddingHorizontal: horizontalScale(5), paddingVertical: 1}]}>
                        <ScrollView style={{flex:1}}>
                            <Text selectable={true} style={{fontWeight:'400'}}>{errorMsg}</Text>
                        </ScrollView>
                        </Block>
                    </Block>
                </Block>
            </Block>
        </Modal>
    );
};


export default ErrorModal;

const styles = StyleSheet.create({
    background: {
        backgroundColor: "rgba(100,100,100, 0.5)",
        alignItems: "center",
        justifyContent: "center",
        height: "100%",
        width: "100%",
        position: "absolute",
    },
    textModalHeader: {
        fontSize: moderateScale(16),
        textAlign: "left",
        fontWeight: "600"
    },
    modalHeader: {
        flexDirection: "row",
        borderBottomWidth: 1,
        paddingVertical: verticalScale(10),
        justifyContent: "space-between",
        alignItems: "center",
    },
    modalContainer: {
        backgroundColor: "#fff",
        width: "90%",
        minHeight: verticalScale(200),
        paddingVertical: verticalScale(10),
        paddingHorizontal: horizontalScale(15),
        borderRadius: moderateScale(10),
    },
    customErrorModalContainer: {
        backgroundColor: "#fff",
        width: "90%",
        minHeight: verticalScale(200),
        // paddingVertical: verticalScale(10),
        // paddingHorizontal: horizontalScale(15),
        borderRadius: moderateScale(10),
        overflow: "hidden"
    },
    box:{
        height: verticalScale(80),
        width: "100%",
        borderWidth: 1,
        borderColor: "#ccc",
        borderRadius: moderateScale(8)
    },
    inputMultiline: {
        height: verticalScale(80),
        width: "100%",
        borderWidth: 1,
        borderColor: "#ccc",
        borderRadius: moderateScale(8),
        paddingLeft: horizontalScale(10),
        backgroundColor: "white",
        textAlignVertical: 'top',
        paddingTop: 3,
    },
});
