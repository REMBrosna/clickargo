import {
    Alert,
    BackHandler,
    StyleSheet,
    View,
    FlatList,
    Platform,
    Pressable,
    useColorScheme,
    TextInput,
} from "react-native";
import React from "react";
import { horizontalScale, moderateScale, verticalScale } from "../../constants/Metrics";
import { HISTORY_DATA } from "../../constants/mockdata/historyJobList";
import { MaterialCommunityIcons, FontAwesome } from "@expo/vector-icons";
import { Text } from "../../components/StyledText";
import Colors from "../../constants/Colors";
import ClictruckModal from "../../components/modal/ClictruckModal";
import DateTimePicker, {
    DateTimePickerAndroid,
    DateTimePickerEvent,
} from "@react-native-community/datetimepicker";
import Popover, { PopoverMode, PopoverPlacement } from "react-native-popover-view";
import DropDownPicker from "react-native-dropdown-picker";
import ItemListCard, { ButtonOptProps } from "../../components/ItemListCard";

type DownloadPopoverType = "ePOD" | "pickup" | "dropoff";
interface ShowDatePickerProps {
    startDate: boolean;
    endDate: boolean;
}
interface FilterStateProps {
    startDate?: Date;
    endDate?: Date;
    orderBy: string;
}

const orderOpt = [
    { label: "Start Time", value: "start" },
    { label: "Pickup Time", value: "pickup" },
];

const HistoryJobScreen = () => {
    const colorScheme = useColorScheme();
    let color = Colors[colorScheme ?? "light"];

    const currentDate = new Date();
    // 7 weeks gap in ms
    // const weeksGap = 4233600000;
    const [showFilterModal, setShowFilterModal] = React.useState<boolean>(false);

    const [showDropdown, setShowDropdown] = React.useState<boolean>(false);
    const [selectedOrderBy, setSelectedOrderBy] = React.useState("");
    const [orderByItems, setOrderByItems] = React.useState(orderOpt);

    const [filterState, setFilterState] = React.useState<FilterStateProps>({
        startDate: currentDate,
        endDate: currentDate,
        orderBy: "startTime",
    });
    const [showDatePicker, setShowDatePicker] = React.useState<ShowDatePickerProps>({
        startDate: false,
        endDate: false,
    });

    React.useEffect(() => {
        const backAction = () => {
            Alert.alert("Hold on!", "Are you sure want to exit?", [
                {
                    text: "cancel",
                    onPress: () => null,
                    style: "cancel",
                },
                { text: "yes", onPress: () => BackHandler.exitApp() },
            ]);
            return true;
        };

        const backHandler = BackHandler.addEventListener("hardwareBackPress", backAction);

        return () => backHandler.remove();
    }, []);

    /**
     * function for convert into date from ms
     * @example
     * 23-10-2023
     */
    const handleDateConvert = (date: any) => {
        const convertDate = new Date(date);
        const str = convertDate.toLocaleString("en-GB", { hour12: false });

        return str.replaceAll("/", "-").slice(0, str.length - 3);
    };

    const handleStartDateChange = (e: DateTimePickerEvent, selectedDate?: Date) => {
        setFilterState({ ...filterState, startDate: selectedDate });
        setShowDatePicker({ ...showDatePicker, startDate: false });
    };

    const handleEndDateChange = (e: DateTimePickerEvent, selectedDate?: Date) => {
        setFilterState({ ...filterState, endDate: selectedDate });
        setShowDatePicker({ ...showDatePicker, endDate: false });
    };

    /**
     * this function is for android date picker because android have different behavior compare to iOS,
     * it will update the minimumDate and maximumDate props later
     */
    const handleAndroidShowDatePicker = (type?: string) => {
        if (type === "start") {
            DateTimePickerAndroid.open({
                mode: "date",
                value: filterState.startDate ? filterState.startDate : currentDate,
                onChange: handleStartDateChange,
                maximumDate: currentDate,
                // minimumDate:
            });
        } else {
            DateTimePickerAndroid.open({
                mode: "date",
                value: filterState.endDate ? filterState.endDate : currentDate,
                onChange: handleEndDateChange,
                maximumDate: currentDate,
                // minimumDate:
            });
        }
    };

    /**
     * function for download at popover,
     * will update later
     */
    const handlePopoverDownload = (jobId: string, type: DownloadPopoverType) => {
        if (type === "ePOD") {
            console.log("ePOD", jobId);
        } else if (type === "pickup") {
            console.log("pickup", jobId);
        } else if (type === "dropoff") {
            console.log("dropoff", jobId);
        } else {
            console.log("error");
        }
    };

    /**
     * array for button at card item list
     *
     * will update later for onPress and probably label
     */
    const buttonOpt: ButtonOptProps[] = [
        {
            type: "history",
            label: "Cargo",
            iconName: "archive",
            onPress: () => console.log("test cargo"),
        },
        {
            type: "history",
            label: "Remarks",
            iconName: "commenting",
            onPress: () => console.log("test remarks"),
        },
        {
            type: "history",
            label: "ePOD",
            iconName: "sticky-note",
            onPress: () => console.log("test ePOD"),
        },
    ];

    return (
        <React.Fragment>
            <View style={{ backgroundColor: "#fff" }}>
                <View style={styles.filterContainer}>
                    <Pressable onPress={() => setShowFilterModal(true)}>
                        <FontAwesome name="bars" size={moderateScale(20)} />
                    </Pressable>
                </View>
                <FlatList
                    data={HISTORY_DATA}
                    style={{ paddingHorizontal: horizontalScale(15) }}
                    ListFooterComponent={() => {
                        return (
                            <View
                                style={{
                                    height:
                                        Platform.OS === "android"
                                            ? verticalScale(170)
                                            : verticalScale(120),
                                }}
                            />
                        );
                    }}
                    showsVerticalScrollIndicator={false}
                    keyExtractor={(item, i) => item.jobId}
                    renderItem={({ item }) => {
                        return (
                            <ItemListCard
                                jobId={item.jobId}
                                jobType={item.jobType}
                                variableTime={currentDate.getTime()}
                                customCardHeaderContent={
                                    <View
                                        style={{
                                            flexDirection: "row",
                                            alignItems: "center",
                                            gap: 5,
                                        }}
                                    >
                                        <Popover
                                            placement={PopoverPlacement.RIGHT}
                                            popoverStyle={styles.popoverContainer}
                                            from={
                                                <Pressable>
                                                    <FontAwesome
                                                        name="paperclip"
                                                        size={moderateScale(20)}
                                                    />
                                                </Pressable>
                                            }
                                        >
                                            <>
                                                <Pressable
                                                    style={styles.popoverItem}
                                                    onPress={() =>
                                                        handlePopoverDownload(item.jobId, "ePOD")
                                                    }
                                                >
                                                    <MaterialCommunityIcons
                                                        name="download"
                                                        color={color.text}
                                                        size={20}
                                                    />
                                                    <Text>ePOD</Text>
                                                </Pressable>
                                                <Pressable
                                                    style={[
                                                        styles.popoverItem,
                                                        { marginVertical: 5 },
                                                    ]}
                                                    onPress={() =>
                                                        handlePopoverDownload(item.jobId, "pickup")
                                                    }
                                                >
                                                    <MaterialCommunityIcons
                                                        name="download"
                                                        color={color.text}
                                                        size={20}
                                                    />
                                                    <Text>Pickup Photos</Text>
                                                </Pressable>
                                                <Pressable
                                                    style={styles.popoverItem}
                                                    onPress={() =>
                                                        handlePopoverDownload(item.jobId, "dropoff")
                                                    }
                                                >
                                                    <MaterialCommunityIcons
                                                        name="download"
                                                        color={color.text}
                                                        size={20}
                                                    />
                                                    <Text>DropOff Photos</Text>
                                                </Pressable>
                                            </>
                                        </Popover>
                                        <Text>{item.jobId}</Text>
                                    </View>
                                }
                                pickUpTime={item.timePickUp}
                                dropOffTime={item.timeDropOff}
                                pickUpLoc={item.locFrom}
                                dropOffLoc={item.locTo}
                                typeList={buttonOpt}
                                timeAudit={{
                                    timeSectionFirst: item.time1,
                                    timeSectionSecond: item.time2,
                                    timeSectionThird: item.time3,
                                    timeSectionForth: item.time4,
                                }}
                            />
                        );
                    }}
                />
            </View>
            {/* modal for filter */}
            <ClictruckModal show={showFilterModal}>
                <React.Fragment>
                    <View style={styles.modalHeader}>
                        <FontAwesome name="filter" size={moderateScale(20)} />
                        <Text>Filter Criteria</Text>
                        <Pressable onPress={() => setShowFilterModal(false)}>
                            <FontAwesome name="times" size={moderateScale(20)} />
                        </Pressable>
                    </View>
                    <View style={styles.modalBody}>
                        <View>
                            {Platform.OS === "android" ? (
                                <>
                                    <Text>Start Date</Text>
                                    <View style={styles.modalDate}>
                                        <Text style={styles.textDateModalAndroid}>
                                            {filterState.startDate
                                                ? handleDateConvert(filterState.startDate)
                                                : ""}
                                        </Text>

                                        <Pressable
                                            style={{ flex: 0.2, alignItems: "center" }}
                                            onPress={() => handleAndroidShowDatePicker("start")}
                                        >
                                            <FontAwesome name="calendar" size={moderateScale(20)} />
                                        </Pressable>
                                    </View>
                                    <Text>End Date</Text>
                                    <View style={styles.modalDate}>
                                        <Text style={styles.textDateModalAndroid}>
                                            {filterState.endDate
                                                ? handleDateConvert(filterState.endDate)
                                                : ""}
                                        </Text>

                                        <Pressable
                                            style={{ flex: 0.2, alignItems: "center" }}
                                            onPress={() => handleAndroidShowDatePicker("end")}
                                        >
                                            <FontAwesome name="calendar" size={moderateScale(20)} />
                                        </Pressable>
                                    </View>
                                </>
                            ) : (
                                <>
                                    <Text>Start Date</Text>
                                    <View style={styles.modalDate}>
                                        <View
                                            style={{
                                                flex: 0.8,
                                                alignItems: "flex-start",
                                                marginVertical: 5,
                                            }}
                                        >
                                            <DateTimePicker
                                                mode="date"
                                                maximumDate={currentDate}
                                                value={
                                                    filterState.startDate
                                                        ? filterState.startDate
                                                        : currentDate
                                                }
                                                style={{ backgroundColor: "#fff" }}
                                                onChange={handleStartDateChange}
                                            />
                                        </View>
                                        <Pressable style={{ flex: 0.2, alignItems: "center" }}>
                                            <FontAwesome name="calendar" size={moderateScale(20)} />
                                        </Pressable>
                                    </View>
                                    <Text>End Date</Text>
                                    <View style={styles.modalDate}>
                                        <View
                                            style={{
                                                flex: 0.8,
                                                alignItems: "flex-start",
                                                marginVertical: 5,
                                            }}
                                        >
                                            <DateTimePicker
                                                mode="date"
                                                maximumDate={currentDate}
                                                value={
                                                    filterState.endDate
                                                        ? filterState.endDate
                                                        : currentDate
                                                }
                                                style={{ backgroundColor: "#fff" }}
                                                onChange={handleEndDateChange}
                                            />
                                        </View>
                                        <Pressable style={{ flex: 0.2, alignItems: "center" }}>
                                            <FontAwesome name="calendar" size={moderateScale(20)} />
                                        </Pressable>
                                    </View>
                                </>
                            )}
                        </View>
                        <View>
                            <Text>Order By</Text>
                            <DropDownPicker
                                style={{ marginVertical: 5 }}
                                open={showDropdown}
                                value={selectedOrderBy}
                                items={orderByItems}
                                setItems={setOrderByItems}
                                setOpen={setShowDropdown}
                                setValue={setSelectedOrderBy}
                            />
                        </View>
                        <View style={{ alignItems: "flex-end" }}>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => setShowFilterModal(false)}
                            >
                                <FontAwesome
                                    name="check"
                                    size={moderateScale(15)}
                                    color={"green"}
                                />
                                <Text>Confirm</Text>
                            </Pressable>
                        </View>
                    </View>
                </React.Fragment>
            </ClictruckModal>
        </React.Fragment>
    );
};

export default HistoryJobScreen;

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
    filterContainer: {
        paddingHorizontal: horizontalScale(20),
        marginTop: verticalScale(80),
        alignItems: "flex-end",
        marginBottom: 15,
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
    modalDate: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
    },
    textDateModalAndroid: {
        backgroundColor: "#fff",
        flex: 0.8,
        textAlign: "left",
        paddingTop: 5,
        paddingHorizontal: horizontalScale(10),
        height: 30,
        justifyContent: "center",
        alignItems: "center",
        borderRadius: 5,
        borderWidth: 1,
        marginVertical: 5,
    },
    // popover
    popoverContainer: {
        width: horizontalScale(180),
        paddingHorizontal: 5,
        paddingVertical: 10,
        borderRadius: moderateScale(15),
    },
    popoverItem: {
        flexDirection: "row",
        alignItems: "center",
        gap: 10,
    },
});
