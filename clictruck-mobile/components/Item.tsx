import { StyleSheet, View, useColorScheme } from "react-native";
import Colors from "../constants/Colors";
import { styles } from "../assets/styles/joblistStyles";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { moderateScale } from "../constants/Metrics";
import { displayDate } from "../constants/util";
import { Text } from "@rneui/themed";
import { Button, Card, Icon } from "@rneui/themed";

const Item = (props: any) => {
    const colorScheme = useColorScheme();
    let color = Colors[colorScheme ?? "light"];

    const jobId = props?.jobId;
    const jobType = props?.tckJob?.tckMstShipmentType?.shtName;
    const locFrom =
        props?.tckCtTripList?.[0].tckCtTripLocationByTrFrom?.tckCtLocation?.locName;
    const locFromAddress = props?.tckCtTripList?.[0].tckCtTripLocationByTrFrom?.tckCtLocation?.locAddress;
    const locTo =
        props?.tckCtTripList?.[0].tckCtTripLocationByTrTo?.tckCtLocation?.locName;
    const locToAddress = props?.tckCtTripList?.[0].tckCtTripLocationByTrTo?.tckCtLocation?.locAddress;
    const timePickUp = props?.tckCtTripList?.[0].tckCtTripLocationByTrFrom?.tlocDtLoc;
    const timeDropOff = props?.tckCtTripList?.[0].tckCtTripLocationByTrTo?.tlocDtLoc;



    return (
        <View style={styles.container}>
            <Card containerStyle={{
                borderWidth: 0.5, borderRadius: 10, elevation: 10,
                shadowColor: "#000", shadowOffset: { width: 1, height: 2 }, shadowOpacity: 0.10
            }}>
                <Card.Title >
                    <View style={styles.cardHeader}>
                        <View style={{ backgroundColor: "#EFF2F1", borderRadius: 5, padding: 4, }}>
                            <Text style={styles.highlightedLabel}>{jobType}</Text></View>
                        <Text style={{ padding: 5, fontWeight: "200" }}>|</Text>
                        <Text h4
                            h4Style={localStyle.h4Style}>{jobId}</Text>
                    </View >
                </Card.Title>
                <Card.Divider />
                <View style={styles.cardBody}>
                    <View style={{ flex: 0.20, alignItems: "center", height: "80%" }}>
                        <View style={localStyle.locationPin}>
                            <Icon name="location-outline" type="ionicon" color={"#FE3727"} />
                        </View>
                    </View>
                    <View style={{ flex: 0.85, height: "80%" }}>
                        <Text h4 h4Style={localStyle.h5Style}>{locFrom}</Text>
                        <Text style={localStyle.subtitle}>{locFromAddress}</Text>
                        <View style={styles.cardTimeContainer}>
                            <Icon name="clock-o" type="font-awesome" size={moderateScale(20)} color={"#999"} />
                            <Text>{displayDate(timePickUp)} (Pick Up)</Text>
                        </View>
                    </View>
                </View>
                <View style={styles.cardBody}>
                    <View style={{ flex: 0.20, alignItems: "center" }}>
                        <View style={localStyle.locationPin}>
                            <Icon name="location-outline" type="ionicon" color={"#0BA247"} />
                        </View>
                    </View>
                    <View style={{ flex: 0.85, height: "80%" }}>
                        <Text h4 h4Style={localStyle.h5Style}>{locTo}</Text>
                        <Text style={localStyle.subtitle}>{locToAddress}</Text>
                        <View style={styles.cardTimeContainer}>
                            <Icon name="clock-o" type="font-awesome" size={moderateScale(20)} color={"#999"} />
                            <Text>{displayDate(timeDropOff)} (Drop Off)</Text>
                        </View>
                    </View>
                </View>
                <View style={{ alignItems: "center", borderTopWidth: 0.5, width: "100%" }}>
                    <Button onPress={props.selectId}
                        buttonStyle={{
                            borderRadius: 20,
                            width: "100%"
                        }}
                        icon={{
                            name: 'play',
                            type: 'font-awesome',
                            size: 15,
                            color: 'white',
                        }}
                        containerStyle={{
                            width: 200,
                            marginHorizontal: 50,
                            marginVertical: 10,
                        }}>Start</Button>
                </View>
                <View style={styles.cardListTiming}>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="play" size={20} />
                        <Text>2023-10-02 09.15</Text>
                        {/* <Text> {displayDate(timeAudit?.timeSectionFirst)}</Text> */}
                    </View>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="arrow-right-bold-circle-outline" size={20} />
                        <Text>2023-10-02 09.15</Text>
                        {/* <Text> {displayDate(timeAudit?.timeSectionThird)}</Text> */}
                    </View>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="upload" size={20} />
                        <Text>2023-10-02 09.15</Text>
                        {/* <Text> {displayDate(timeAudit?.timeSectionThird)}</Text> */}
                    </View>
                    <View style={styles.cardTimingItem}>
                        <MaterialCommunityIcons name="download" size={20} />
                        <Text>2023-10-02 09.15</Text>
                        {/* <Text> {displayDate(timeAudit?.timeSectionForth)}</Text> */}
                    </View>
                </View>
            </Card >
        </View >
    )
}

const localStyle = StyleSheet.create({
    h4Style: {
        fontSize: 16,
        paddingLeft: 2,
        fontWeight: "300"
    },
    h5Style: {
        fontSize: 16,
        fontWeight: "600"
    },
    subtitle: {
        color: "#999"
    },
    muted: {
        color: "#999"
    },
    locationPin: {
        alignItems: "center",
        justifyContent: "center",
        borderWidth: 0,
        borderRadius: 12,
        backgroundColor: "#EFF2F1",
        width: moderateScale(50),
        height: moderateScale(60),
    }
});

export default Item;