import { StyleSheet } from "react-native";
import { moderateScale, verticalScale } from "../../constants/utils";

export const popupStyles = StyleSheet.create({
    modalBody: {
        paddingVertical: verticalScale(10),
    },
    modalButtons: {
        width: "45%",
        flexDirection: "row",
        justifyContent: "center",
        alignItems: "center",
        borderWidth: 1,
    },
    textModalHeader: {
        fontSize: moderateScale(16),
        textAlign: "center",
        fontWeight: 500,
    },
    textModalBody: {
        fontSize: moderateScale(14),
    },
    modalHalf: {
        width: "50%",
        paddingHorizontal: 5,
    },
    label: {
        fontSize: 10
    },
    confirmButton: {
        flexDirection: "row",
        justifyContent: "space-evenly",
        alignItems: "center",
        width: "35%",
        minHeight: 30,
        borderWidth: 1,
        marginTop: 15,
        borderRadius: 5,
    },
    photoListContainer: {
        minHeight: verticalScale(80),
        borderWidth: 1,
        borderColor: "#ccc",
        marginVertical: 10,
        borderRadius: moderateScale(8),
        // flexDirection:'row',
        // alignItems:'center',
    },
    photoListContainerContent: {
        alignItems: 'center',
    },
    photoListItem: {
        height: moderateScale(50),
        width: moderateScale(50),
        backgroundColor: '#eee',
        alignItems: 'center',
        justifyContent: 'center',
        margin: 5,
        borderRadius: moderateScale(8),
    }

});