import { FontAwesome, MaterialCommunityIcons } from "@expo/vector-icons";
import { useFonts } from "expo-font";
import React, { useContext, useEffect, useState } from "react";
import { FlatList, Platform, Pressable, TextInput, useColorScheme } from "react-native";
// import { Text } from "../../components/StyledText";
import { Text } from "@rneui/themed";
import { View } from "../../components/Themed";
import Colors from "../../constants/Colors";
// import { FlatList } from "react-native-gesture-handler";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { router } from "expo-router";
import { styles } from "../../assets/styles/joblistStyles";
import { StatsContextType } from "../../cktypes/clictruck";
import ClictruckModal from "../../components/modal/ClictruckModal";
import { horizontalScale, moderateScale, verticalScale } from "../../constants/Metrics";
import { newJobList } from "../../constants/url";
import { displayDate, sendRequest } from "../../constants/util";
import { StatsContext } from "../../store/context/stats-context";
import Item from "../../components/Item";
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

const getStartConfirmUrl =
    process.env.EXPO_PUBLIC_BACKEND_URL +
    "/api/v1/clickargo/clictruck/mobile/assigned/checkJobStatus";

const doStartJob =
    process.env.EXPO_PUBLIC_BACKEND_URL +
    "/api/v1/clickargo/clictruck/mobile/startJob/";


// async function sendRequest(url:string, method?:string, body?:string|undefined) {
//     method = method ?? "get";

//     const token = await getItemAsync("authToken");
//     const config = { 
//         url: url,
//         method: method,
//         headers:{ Authorization: "Bearer " + token }, 
//         data: body,
//     };

//     try 
//     {
//         const result = await axios.request(config);
//         if (result){
//             console.log("axios request result : ", result.data);
//             return result.data;
//         }
//         else
//         {
//             console.error('axios request result is invalid');
//             return false
//         }
//     }
//     catch (e)
//     {
//         console.error('Axios payload: ', config);
//         console.error('Axios Error:', e);
//         return false;
//     }

// }

// obsolete
// async function sendRequest () {
//     // Define the URL 
//     const apiUrl = newJobListUrl;

//     const apiToken = await getItemAsync("authToken");
//     const apiConfig = { headers:{ Authorization: "Bearer " + apiToken } };

//     console.log("getNewJob", apiUrl);
//     // Make the GET request
//     try {
//         const response = await axios.get(apiUrl, apiConfig);
//         if(response?.data?.aaData){
//             //THIS IS FOR OFFLINE MODE
//             // storeData(response?.data?.aaData);
//             return true;

//         } else {
//             console.error("aaData from axios is invalid");
//             return false;
//         }
//     } catch (e) {
//         console.error('Axios payload: ', apiUrl, apiConfig);
//         console.error('Axios Error:', e);
//         return false;
//     }
// };

//THIS FUNCTION STORE TO LOCAL MEMORY FOR OFFLINE CAPABILITY
async function storeData(value: any) {
    try {
        const jsonValue = JSON.stringify(value);
        await AsyncStorage.setItem('newJobList', jsonValue);
        console.log("Successfully store data to async storage");
    } catch (e) {
        console.error("AsyncStorage Error: ", e);
    }
};

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


// function Item(props: any) {
//     const colorScheme = useColorScheme();
//     let color = Colors[colorScheme ?? "light"];

//     const jobId = props?.jobId;
//     const jobType = props?.tckJob?.tckMstShipmentType?.shtName;
//     const locFrom =
//         props?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locName + "\n" +
//         props?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locAddress;
//     const locTo =
//         props?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tckCtLocation?.locName + "\n" +
//         props?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tckCtLocation?.locAddress;
//     const timePickUp = props?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tlocDtLoc;
//     const timeDropOff = props?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tlocDtLoc;



//     return (
//         <View style={styles.card}>
//             <View style={styles.cardHeader}>

//                 <Text>{jobId}</Text>
//                 <Text style={{ color: "green" }}>{jobType}</Text>
//             </View>
//             <View style={styles.cardBody}>
//                 <View style={{ flex: 0.15, alignItems: "center" }}>
//                     <View style={styles.cardIconContainer}>
//                         <MaterialCommunityIcons
//                             name="map-marker-outline"
//                             color={color.text}
//                             size={moderateScale(25)}
//                         />
//                     </View>
//                 </View>
//                 <View style={{ flex: 0.85 }}>
//                     <Text>{locFrom}</Text>
//                     <View style={styles.cardTimeContainer}>
//                         <FontAwesome
//                             name="clock-o"
//                             size={moderateScale(20)}
//                             style={{ marginRight: 10 }}
//                         />
//                         <Text>{displayDate(timePickUp)} (Pick Up)</Text>
//                     </View>
//                 </View>
//             </View>
//             <View style={styles.cardBody}>
//                 <View style={{ flex: 0.15, alignItems: "center" }}>
//                     <View style={styles.cardIconContainer}>
//                         <MaterialCommunityIcons
//                             name="map-marker-outline"
//                             color={color.text}
//                             size={moderateScale(25)}
//                         />
//                     </View>
//                 </View>
//                 <View style={{ flex: 0.85 }}>
//                     <Text>{locTo}</Text>
//                     <View style={styles.cardTimeContainer}>
//                         <FontAwesome
//                             name="clock-o"
//                             size={moderateScale(20)}
//                             style={{ marginRight: 10 }}
//                         />
//                         <Text>{displayDate(timeDropOff)} (Drop Off)</Text>
//                     </View>
//                 </View>
//             </View>
//             <Pressable
//                 onPress={props.selectId}
//                 style={styles.cardButton}
//             >
//                 <FontAwesome
//                     name="play"
//                     size={moderateScale(20)}
//                     style={{ marginRight: 10 }}
//                     color="white"
//                 />
//                 <Text style={{ color: "white" }}>Start</Text>
//             </Pressable>
//         </View>
//     )
// }

export default function NewJobListingScreen() {
    console.log("NewJobListingScreen render");
    const { reloadStats, isStatsCompleted } = useContext(StatsContext) as StatsContextType;


    const [loading, setLoading] = useState<boolean>(true);



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
        const result = await sendRequest(newJobListUrl);
        if (result) {
            console.log("loading is still true");
            setInputData(result.aaData);
        }
    }

    useEffect(() => {
        reloadStats(true);
        getData();
        if (isStatsCompleted()) {
            setTimeout(() => setLoading(false), 1000);
        }


    }, [])

    if (!fontsLoaded) {
        return null;
    }



    function onRefresh() {
        setIsRefreshing(true);
        try {
            reloadStats(true);
            getData();
        } catch {
            console.error("onRefresh error");
        } finally {
            setIsRefreshing(false);
        }
    }


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
        const job = await sendRequest(doStartJob + selectedJobId + "?action=START", "put");
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
                    ListFooterComponent={() => {
                        return (
                            <View
                                style={{
                                    height:
                                        Platform.OS === "android"
                                            ? verticalScale(130)
                                            : verticalScale(80),
                                    backgroundColor: "#eeeeee",
                                }}
                            ></View>
                        );
                    }}
                    ListEmptyComponent={
                        <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', borderRadius: 10, paddingVertical: 10 }}>
                            {/* <View style={{flexDirection:'column'}}> */}
                            <FontAwesome name="warning" size={moderateScale(20)} />
                            <Text>No new job availaible.{"\n"}Try pull to refresh.</Text>
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
