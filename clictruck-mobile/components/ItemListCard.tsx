import { Pressable, StyleSheet, Text, View } from "react-native";
import React from "react";
import { useFonts } from "expo-font";

import { horizontalScale, verticalScale, moderateScale } from "../constants/Metrics";
import { MaterialCommunityIcons, FontAwesome } from "@expo/vector-icons";
import { GlyphMap, IconProps } from "@expo/vector-icons/build/createIconSet";

// {
//     jobId: "CKCTJ2023101900001",
//     jobType: "DOMESTIC",
//     locFrom: "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
//     locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
//     timePickUp: 1664557200000,
//     timeDropOff: 1674458200000,
//     time1: 1664557200000,
//     time2: 1664557200000,
//     time3: 1674358200000,
//     time4: 1674558200000,
//     dataCargo:{
//         type: "A",
//         weight: "1000",
//         volumetric: "1000",
//         length: "2000",
//         width: "400",
//         height: "150",
//         desc: "test test test",
//         remarks: "test test test"
//     },
//     dataRemarks:{
//         remarks: "test test test",
//         special: "test test test"
//     }
// }

export type ListOpt = "new" | "paused" | "history" | "undefined";
export interface TimeAuditObj {
    timeSectionFirst?: number | null;
    timeSectionSecond?: number | null;
    timeSectionThird?: number | null;
    timeSectionForth?: number | null;
}
export type ButtonOptProps = {
    type: ListOpt;
    label: string;
    iconName: keyof typeof FontAwesome.glyphMap;
    onPress: () => void;
};

interface Props {
    jobId: string;
    jobType: string;
    customCardHeaderContent?: JSX.Element | JSX.Element[];

    /**
     * variable time for coloring the label
     */
    variableTime: number | 0;
    pickUpTime: number | 0;
    dropOffTime: number | 0;

    pickUpLoc: string;
    dropOffLoc: string;

    /**
     * this is for type list that we're gonna put this item list card component
     */
    typeList: ButtonOptProps[];

    timeAudit?: TimeAuditObj;

    /**
     * time section 1, top left
     */
    timeSectionFirst?: number | null;

    /**
     * time section 2, bottom left
     */
    timeSectionSecond?: number | null;

    /**
     * time section 3, top right
     */
    timeSectionThird?: number | null;

    /**
     * time section 4, bottom right
     */
    timeSectionForth?: number | null;
}

/**
 * component for item at list
 *
 * if there is an update on it, please update the Props too
 */
const ItemListCard = (props: Props) => {
    const {
        jobId,
        jobType,
        customCardHeaderContent,
        variableTime,
        pickUpTime,
        dropOffTime,
        pickUpLoc,
        dropOffLoc,
        typeList,
        timeAudit,
        timeSectionFirst,
        timeSectionSecond,
        timeSectionThird,
        timeSectionForth,
    } = props;
    const [fontsLoaded] = useFonts({
        "pop-black": require("../assets/fonts/poppins/Poppins-Black.ttf"),
        "pop-med": require("../assets/fonts/poppins/Poppins-Medium.ttf"),
        "pop-reg": require("../assets/fonts/poppins/Poppins-Regular.ttf"),
    });
    const validateTypelist = typeList.some((item, i) => item.type === "new");

    const jobIdLabelColor = () => {
        if (pickUpTime > variableTime && dropOffTime > variableTime) {
            return "green";
        } else if (pickUpTime < variableTime && dropOffTime < variableTime) {
            return "red";
        } else {
            return "orange";
        }
    };

    const handleDateConvert = (date: any) => {
        if (typeof date === "number") {
            const convertDate = new Date(date);
            const str = convertDate.toLocaleString("en-GB", { hour12: false });

            return str.replaceAll("/", "-").slice(0, str.length - 3);
        }
    };

    const handleButtonComponent = () => {
        let res: JSX.Element;
        if (typeList.length === 1) {
            res = (
                <Pressable onPress={typeList[0].onPress} style={styles.cardButton}>
                    <FontAwesome name="play" size={moderateScale(20)} style={{ marginRight: 10 }} />
                    <Text>{typeList[0].label}</Text>
                </Pressable>
            );
        } else {
            res = (
                <View style={styles.cardButtonContainer}>
                    {typeList.map((item, i) => {
                        return (
                            <Pressable
                                key={i}
                                style={[styles.cardButton, { flex: 0.3 }]}
                                onPress={item.onPress}
                            >
                                <FontAwesome name={item.iconName ? item.iconName : "sticky-note"} />
                                <Text>{item.label}</Text>
                            </Pressable>
                        );
                    })}
                </View>
            );
        }

        return res;
    };

    if (!fontsLoaded) {
        return null;
    }
    return (
        <View style={styles.card}>
            <View style={styles.cardHeader}>
                {customCardHeaderContent ? (
                    customCardHeaderContent
                ) : (
                    <Text style={{ color: jobIdLabelColor() }}>{jobId}</Text>
                )}
                <Text>{jobType}</Text>
            </View>
            <View style={styles.cardBody}>
                <View style={{ flex: 0.15, alignItems: "center" }}>
                    <View style={styles.cardIconContainer}>
                        <MaterialCommunityIcons
                            name="map-marker-outline"
                            color={
                                pickUpTime > variableTime && dropOffTime > variableTime
                                    ? "green"
                                    : "red"
                            }
                            size={moderateScale(25)}
                        />
                    </View>
                </View>
                <View style={{ flex: 0.85 }}>
                    <Text numberOfLines={2}>{pickUpLoc}</Text>
                    <View style={styles.cardTimeContainer}>
                        <FontAwesome
                            name="clock-o"
                            size={moderateScale(20)}
                            color={
                                pickUpTime > variableTime && dropOffTime > variableTime
                                    ? "green"
                                    : "red"
                            }
                            style={{ marginRight: 10 }}
                        />
                        <Text>{handleDateConvert(pickUpTime)} (Pick Up)</Text>
                    </View>
                </View>
            </View>
            <View style={styles.cardBody}>
                <View style={{ flex: 0.15, alignItems: "center" }}>
                    <View style={styles.cardIconContainer}>
                        <MaterialCommunityIcons
                            name="map-marker-outline"
                            color={
                                pickUpTime > variableTime && dropOffTime > variableTime
                                    ? "green"
                                    : "red"
                            }
                            size={moderateScale(25)}
                        />
                    </View>
                </View>
                <View style={{ flex: 0.85 }}>
                    <Text numberOfLines={2}>{dropOffLoc}</Text>
                    <View style={styles.cardTimeContainer}>
                        <FontAwesome
                            name="clock-o"
                            color={
                                pickUpTime > variableTime && dropOffTime > variableTime
                                    ? "green"
                                    : "red"
                            }
                            size={moderateScale(20)}
                            style={{ marginRight: 10 }}
                        />
                        <Text>{handleDateConvert(dropOffTime)} (Drop Off)</Text>
                    </View>
                </View>
            </View>
            <>{handleButtonComponent()}</>
            {!validateTypelist ? (
                <View style={styles.cardListTiming}>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="play" size={20} />
                        <Text> {handleDateConvert(timeAudit?.timeSectionFirst)}</Text>
                    </View>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="arrow-right-bold-circle-outline" size={20} />
                        <Text> {handleDateConvert(timeAudit?.timeSectionThird)}</Text>
                    </View>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="upload" size={20} />
                        <Text> {handleDateConvert(timeAudit?.timeSectionThird)}</Text>
                    </View>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="download" size={20} />
                        <Text> {handleDateConvert(timeAudit?.timeSectionForth)}</Text>
                    </View>
                </View>
            ) : (
                <></>
            )}
        </View>
    );
};

export default ItemListCard;

const styles = StyleSheet.create({
    card: {
        width: "100%",
        paddingHorizontal: horizontalScale(10),
        borderRadius: 14,
        minHeight: 150,
        borderWidth: 1,
        marginVertical: verticalScale(10),
        paddingBottom: verticalScale(5),
    },
    cardHeader: {
        display: "flex",
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        borderBottomWidth: 0.5,
        paddingVertical: 12,
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
    cardButtonContainer: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
    },
    cardButton: {
        width: "100%",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "row",
        borderWidth: 1,
        paddingVertical: 8,
        borderRadius: 5,
        marginBottom: 10,
        zIndex: 2,
        gap: 2,
    },
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
});
