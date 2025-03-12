import { FontAwesome, MaterialCommunityIcons } from "@expo/vector-icons";
import { useFonts } from "expo-font";
import React, { useContext, useEffect, useState } from "react";
import { FlatList, Platform, Pressable, TextInput, useColorScheme } from "react-native";
// import { Text } from "../../components/StyledText";
import { View } from "../../components/Themed";
import Colors from "../../constants/Colors";
// import { FlatList } from "react-native-gesture-handler";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { router } from "expo-router";
import { styles } from "../../assets/styles/joblistStyles";
import { StatsContextType } from "../../cktypes/clictruck";
import ClictruckModal from "../../components/modal/ClictruckModal";
import { horizontalScale, moderateScale, verticalScale } from "../../constants/Metrics";
import { newJobList, pausedJobList } from "../../constants/url";
import { displayDate, sendRequest } from "../../constants/util";
import { StatsContext } from "../../store/context/stats-context";
import Item from "../../components/Item";
import { Text } from "@rneui/themed";
import { GliLoading } from "../../components/clictruck/GliLoading";

const newJobListUrl =
    process.env.EXPO_PUBLIC_BACKEND_URL +
    newJobList +
    "?sEcho=3" +
    "&iDisplayStart=0" +
    "&iDisplayLength=10" +
    "&iSortCol_0=0" +
    "&sSortDir_0=desc" +
    "&iSortingCols=1" +
    "&mDataProp_0=jobDtCreate" +
    "&mDataProp_1=history" +
    "&sSearch_1=default" +
    "&iColumns=5";
const getPausedJobList =
    process.env.EXPO_PUBLIC_BACKEND_URL +
    pausedJobList;

const getStartConfirmUrl =
    process.env.EXPO_PUBLIC_BACKEND_URL +
    "/api/v1/clickargo/clictruck/mobile/paused/checkJobStatus";

const doStartJob =
    process.env.EXPO_PUBLIC_BACKEND_URL +
    "/api/v1/clickargo/clictruck/mobile/startJob/";



//THIS FUNCTION STORE ONGOING JOB TO LOCAL MEMORY FOR OFFLINE CAPABILITY
async function storeJob(value: any) {
    try {
        const jsonValue = JSON.stringify(value);
        await AsyncStorage.setItem('ongoingJob', jsonValue);
        console.log("Successfully store ongoingJob to async storage");
        return true;
    } catch (e) {
        console.error("AsyncStorage Error: ", e);
        return false;
    }
}




export default function PausedJobsListingScreen() {

    const { reloadStats, isStatsCompleted } = useContext(StatsContext) as StatsContextType;
    console.log("PausedJobScreen render");



    const [showModal, setShowModal] = useState(false);
    const [selectedJobId, setSelectedJobId] = useState<string | undefined>();
    const [selectedItem, setSelectedItem] = useState<any | undefined>();
    const [modalErrMsg, setModalErrMsg] = useState<string | undefined>();

    const [inputdata, setInputData] = useState<any | undefined>()
    const [isRefreshing, setIsRefreshing] = useState(false);

    const [fontsLoaded] = useFonts({
        "pop-med": require("../../assets/fonts/poppins/Poppins-Medium.ttf"),
        "pop-reg": require("../../assets/fonts/poppins/Poppins-Regular.ttf"),
    });

    async function getData() {
        const result = await sendRequest(getPausedJobList);
        if (result) {
            setInputData(result.aaData)
        }
    }

    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        getData();
        reloadStats(true);
        if (isStatsCompleted()) {
            setTimeout(() => setLoading(false), 2000);
        }
    }, [])

    if (!fontsLoaded) {
        return null;
    }

    function onRefresh() {
        setIsRefreshing(true);
        try {
            getData();
            reloadStats(true);
        } catch {
            console.error("onRefresh error");
        } finally {
            setIsRefreshing(false);
        }
    }

    function handleSelectedItem(id: string) {
        console.log("select job", id);
        let data = inputdata;
        const selected = data?.find((item: { jobId: string; }) => {
            return item?.jobId === id;
        });
        console.log("selected", selected);
        if (selected !== undefined) {
            setSelectedItem(selected);
            setShowModal(true);
        }
    };

    async function handleSelectJob(id: string) {
        if (id) {
            console.log("select job", id);
            const confirmation = await sendRequest(getStartConfirmUrl);
            setModalErrMsg(confirmation.data);
            setSelectedJobId(id);
            setShowModal(true);
        } else {
            console.error("invalid job id");
        }
    };

    async function handleStartJob(id: string | undefined) {
        console.log("starting job", id, selectedJobId);
        const job = await sendRequest(doStartJob + selectedJobId + "?action=PAUSE", "put");
        // console.log("job start result", job);
        if (job) {
            const result = await storeJob(job.data);
            if (result) {
                reloadStats(true);
                router.push("/(tabs)");
            } else {
                console.error("failed to store ongoing job to local storage");
            }
        } else {
            console.error("invalid job data");
        }
        setShowModal(false);
    }



    return (
        <React.Fragment>
            <View style={[styles.container]}>
                <FlatList
                    showsVerticalScrollIndicator={false}
                    data={inputdata}
                    onRefresh={onRefresh}
                    refreshing={isRefreshing}
                    style={{ paddingHorizontal: horizontalScale(15) }}
                    renderItem={({ item }) => (
                        <Item {...{ ...item, selectId: () => handleSelectJob(item.jobId) }} />
                    )}
                    keyExtractor={(item) => item.jobId}
                    ItemSeparatorComponent={() => {
                        return <View style={{ marginVertical: 10 }} />;
                    }}
                    ListHeaderComponent={() => {
                        return <View style={{ marginTop: verticalScale(80) }}></View>;
                    }}
                    // ListFooterComponent={() => {
                    //     return (
                    //         <View
                    //             style={{
                    //                 height:
                    //                     Platform.OS === "android"
                    //                         ? verticalScale(130)
                    //                         : verticalScale(80),
                    //                 backgroundColor: "#eeeeee",
                    //             }}
                    //         ></View>
                    //     );
                    // }}
                    ListEmptyComponent={
                        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', borderRadius: 10, paddingVertical: 10 }}>
                            {/* <View style={{flexDirection:'column'}}> */}
                            <FontAwesome name="warning" size={moderateScale(20)} />
                            <Text>No paused job availaible.{"\n"}Try pull to refresh.</Text>
                            {/* </View> */}
                        </View>
                    }
                />
            </View>
            <GliLoading title="Please wait..." isVisible={loading}
                loadingProps={{ size: "large" }}
                titleStyle={{ textAlign: "center" }}></GliLoading>
            <ClictruckModal show={showModal}>
                <React.Fragment>
                    <View style={styles.modalHeader}>
                        <FontAwesome name="play" size={moderateScale(20)} />
                        <Text style={styles.textModalHeader}>Start Job</Text>
                        <Pressable onPress={() => setShowModal(false)}>
                            <FontAwesome name="times" size={moderateScale(20)} />
                        </Pressable>
                    </View>
                    <View style={styles.modalBody}>
                        <View>
                            <Text style={styles.textModalBody}>
                                Confirm to start the following job?
                            </Text>
                            <TextInput
                                style={[
                                    styles.modalInputTextContainer,
                                    // { backgroundColor: "lightgrey" },
                                ]}
                                editable={false}
                                value={selectedJobId}
                            />
                        </View>
                        <View style={{ flexDirection: "row", justifyContent: "space-evenly" }}>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => {
                                    setShowModal(false);
                                }}
                            >
                                <FontAwesome name="times" size={moderateScale(15)} color={"red"} />
                                <Text style={styles.textModalBody}>No</Text>
                            </Pressable>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => handleStartJob(selectedJobId)}
                            >
                                <FontAwesome
                                    name="check"
                                    size={moderateScale(15)}
                                    color={"green"}
                                />
                                <Text style={styles.textModalBody}>Confirm</Text>
                            </Pressable>
                        </View>
                        {modalErrMsg &&
                            <View
                                style={{
                                    flexDirection: "row",
                                    alignItems: "center",
                                    marginVertical: verticalScale(10),
                                    paddingHorizontal: horizontalScale(10),
                                }}
                            >
                                <FontAwesome
                                    name="exclamation-triangle"
                                    size={moderateScale(20)}
                                    style={{
                                        flex: 0.2,
                                        alignItems: "center",
                                        justifyContent: "center",
                                    }}
                                />
                                <Text
                                    style={[
                                        styles.textModalBody,
                                        { color: "red", flex: 0.7, textAlign: "justify" },
                                    ]}
                                >
                                    {modalErrMsg}
                                </Text>
                            </View>
                        }
                    </View>
                </React.Fragment>
            </ClictruckModal>
        </React.Fragment>
    );
}
