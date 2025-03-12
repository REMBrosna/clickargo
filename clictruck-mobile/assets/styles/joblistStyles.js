import { StyleSheet } from 'react-native';
import { horizontalScale, verticalScale, moderateScale } from "../../constants/Metrics";

export const styles = StyleSheet.create({

    container: {
        flex: 1,
        backgroundColor: "#EFF2F1",
    },
    highlightedLabel: {
        color: "#1976d2",
        fontSize: 14,
        fontWeight: "bold",
        // backgroundColor: "#e7e7e7"
    },
    listContainer: {
        width: "100%",
        paddingHorizontal: horizontalScale(10),
        borderRadius: 14,
        minHeight: 150,
        borderWidth: 1,
        borderColor: "grey",

    },
    listTitle: {
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        borderBottomWidth: 1,
        borderColor: "grey",
        alignItems: "center",
        paddingVertical: 12,
    },
    listLocation: {
        display: "flex",
        flexDirection: "row",
        marginVertical: 15,
    },
    locationLogo: {
        flex: 0.15,
        alignItems: "center",
        justifyContent: "center",
    },
    locationDetail: {
        flex: 0.85,
    },
    listButton: {
        width: "100%",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "row",
        borderWidth: 1,
        paddingVertical: 8,
        borderRadius: 5,
        marginBottom: 10,
        zIndex: 2,
    },

    listTiming2: {
        flexDirection: "row",
        justifyContent: "space-evenly",
        marginVertical: verticalScale(15),
    },
    timingItem2: {
        flex: 0.4,
        alignItems: "center",
        flexDirection: "row",
        justifyContent: "space-between",
    },


    textListTitle: {
        fontSize: moderateScale(15),
        fontFamily: "pop-med",
    },
    textListBody: {
        fontSize: moderateScale(12),
        fontFamily: "pop-med",
    },
    textDetailBody: {
        fontSize: moderateScale(7),
        fontFamily: "pop-reg",
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
    // card content
    card: {
        width: "100%",
        paddingHorizontal: horizontalScale(10),
        borderRadius: 14,
        minHeight: 150,
        borderWidth: 1,
        backgroundColor: "#fff"
    },
    cardHeader: {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "flex-start",
        marginBottom: "-1%",
        width: "100%"
    },
    cardBody: {
        display: "flex",
        flexDirection: "row",
        marginVertical: 15,
    },
    cardIconContainer: {
        alignItems: "center",
        justifyContent: "center",
        borderWidth: 0.5,
        borderRadius: 2,
        width: moderateScale(30),
        height: moderateScale(30),
    },
    cardTimeContainer: {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "flex-start",
        marginTop: 10,
    },
    cardButton: {
        width: "100%",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "row",
        // borderWidth: 1,
        paddingVertical: 8,
        borderRadius: 8,
        marginBottom: 10,
        zIndex: 2,
        backgroundColor: "#2f95dc",
        color: "#ffffff"
    },
    //timing v1
    listTiming: {
        width: "100%",
        flexDirection: "row",
        justifyContent: "space-between",
        flexWrap: "wrap",
        marginBottom: 10,
    },
    timingItem: {
        width: "50%",
        alignItems: "center",
        flexDirection: "row",
        marginBottom: 10,
    },
    //timing v2
    cardListTiming: {
        width: "100%",
        flexDirection: "row",
        justifyContent: "space-between",
        flexWrap: "wrap",
        marginBottom: 10,
    },
    cardTimingItem: {
        width: "50%",
        alignItems: "center",
        flexDirection: "row",
        marginBottom: 10,
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
        backgroundColor: "lightgrey",
    },

})