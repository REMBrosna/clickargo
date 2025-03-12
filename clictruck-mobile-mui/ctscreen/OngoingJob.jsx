import {
  Modal,
  Platform,
  RefreshControl,
  ScrollView,
  StyleSheet,
  useColorScheme,
  BackHandler,
  Alert,
  useWindowDimensions,
  Image,
  Pressable,
} from "react-native";
import {
  Block,
  Button,
  Icon,
  Text,
  Block as View,
  theme,
} from "galio-framework";
import AsyncStorage from "@react-native-async-storage/async-storage";
import React, { useCallback, useContext, useEffect, useState } from "react";
import { materialTheme } from "../constants";
import colors from "../constants/colors";
import {
  horizontalScale,
  moderateScale,
  verticalScale,
} from "../constants/metrics";
import { displayDate, getFontByFontScale } from "../constants/utils";
import NetworkCheckerContext from "../context/NetworkCheckerContext";
import CtLinearBackground from "../ctcomponents/CtLinearBackground";
import OpenExternalUrl from "../ctcomponents/OpenExternalUrl";
import PhotoUploadScreen from "../ctcomponents/PhotoUpload";
import SignatureScreen from "../ctcomponents/SignatureCanvas";
import { sendRequest } from "../utils/httpUtil";
import CtLoading from "./CtLoading";
import CargoDelivery from "./ongoing/CargoDelivery";
import CargoDetails from "./ongoing/CargoDetails";
import CargoRemarks from "./ongoing/CargoRemarks";
import ContinueJob from "./ongoing/ContinueJob";
import PhotoUploadPopup from "./ongoing/PhotoUploadPopup";
import DropOffConfirmation from "./ongoing/DropOffConfirmation";
import useStats from "../hooks/useStats";
import { Ionicons, FontAwesome5 } from "@expo/vector-icons";
import MKButton from "../components/Button";
import { ckComponentStyles } from "../styles/componentStyles";
import { useTranslation } from "react-i18next";
import JobTabContext from "../context/JobTabContext";
import JobAdditionalDetails from "./JobAdditionalDetails";
import OngoingJobTimingDetails from "./OngoingJobTimingDetails";
import OngoingJobSingleDropOff from "./OngoingJobSingleDropOff";
import DropOffDetailsActive from "./DropOffDetailsActive";
import MultiDropOffLocationSelectPopup from "./ongoing/MultiDropOffLocationSelectPopup";
import ShowRemark from "../ctcomponents/ShowRemark";
import SimpleLineIcons from "react-native-vector-icons/SimpleLineIcons";

const cancelDropOffUrl =
  "/api/v1/clickargo/clictruck/mobile/trip/location/attach";

export default function OngoingJobScreen(props) {
  const { navigation } = props;
  const { isOnline, checkConnection } = useContext(NetworkCheckerContext);
  const { jobTabId } = useContext(JobTabContext);
  const { fontScale } = useWindowDimensions();

  const styles = makeStyles(fontScale);

  const [loading, setLoading] = useState(true);
  const { newStats, pauseStats, reloadStats } = useStats();
  const [onGoingJobData, setonGoingJobData] = useState();
  const { t, i18n } = useTranslation();

  const colorScheme = useColorScheme();
  let color = colors[colorScheme ?? "light"];
  const ctColor = theme.COLORS;

  const [noData, setNoData] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [modalPhotoUpload, setModalPhotoUpload] = useState({
    open: false,
    tripId: null,
  }); //pickup button & dropOff button
  const [modalContinueJob, setModalContinueJob] = useState(false); //pickup->continue
  const [modalRemark, setModalRemark] = useState({
    open: false,
    content: null,
  }); //reamrks button
  const [modalCargoDetail, setModalCargoDetail] = useState(false); //cargo button
  const [modalStartDelivery, setModalStartDelivery] = useState(false); //Deliver Cargo button (action confirmation)
  const [modalCustomerConfirmation, setModalCustomerConfirmation] =
    useState(false); //dropoff->continue
  const [cameraScreenVisible, setCameraScreenVisible] = useState(false);
  const [photoComment, setPhotoComment] = useState();
  const [signatureScreenVisible, setSignatureScreenVisible] = useState(false);
  const [activityIndicator, setActivityIndicator] = useState(false);
  const [cargoData, setCargoData] = useState();

  const [dropOffPhotoData, setDropOffPhotoData] = useState([]);

  const [photoData, setPhotoData] = useState([]);
  const [signatureData, setSignatureData] = useState("");

  const [additionalDtls, setAdditionalDtls] = useState("");

  //state container for the pickup details
  const [pickUpDetails, setPickUpDetails] = useState({});
  //state container for the trips with multiple dropoffs
  const [dropOffLocations, setDropOffLocations] = useState();
  //state container if job is multidropoff
  const [isMultiDrop, setIsMultiDrop] = useState(false);
  //state container for single trip dropoff
  const [dropOffDetails, setDropOffDetails] = useState({});
  //state container for determining the button to display
  const [btnActionState, setBtnActionState] = useState({
    tripId: null,
    action: "pickup", //default to pickup (pickup | deliver | dropoff)
    dropoffSize: 1, //default to 1 for single
    dropoffCtr: 0, //this is for the dropoff of multiple dropoffs, this is defaulted to 1 for single trip
  });

  const [openMultiDropSelect, setOpenMultiDropSelect] = useState(false);

  //state container to reload after update of some status specially during multi-drop
  const [reload, setReload] = useState(false);

  //   const [isExpanded, setIsExpanded] = useState(false);

  const custConfirmData = {
    discrepanciesEnabled: false,
    termsAgreed: false,
    tlocComment: "",
    tlocDeviationComment: "",
    tlocName: "",
    tlocCargoRec: "",
  };

  const [confirmationData, setConfirmationData] = useState(custConfirmData);

  async function fetchData() {
    try {
      if (isOnline) {
        //connected to network, retrieve the record from backend
        getOngoing();
      } else {
        const storedData = await AsyncStorage.getItem("ongoingJob");
        if (storedData !== null) {
          setonGoingJobData(JSON.parse(storedData));
          setNoData(false);
        } else {
          //   console.log("Data is null");
          setNoData(true);
          //clear storage
          await AsyncStorage.removeItem("ongoingJob");
        }
      }
    } catch (error) {
      console.error("Error fetching ongoing data:", error);
    } finally {
      setLoading(false);
    }
  }

  async function getOngoing() {
    try {
      const response = await sendRequest(
        "/api/v1/clickargo/clictruck/mobile/trip/ongoing"
      );
      if (response) {
        if (response.data !== null && response.data.jobId !== null) {
          //   console.log("ON GOING DATA-----?", response?.data);
          setonGoingJobData(response.data);
          setNoData(false);

          //remove first then set again?
          await AsyncStorage.removeItem("ongoingJob");
          setTimeout(
            () =>
              AsyncStorage.setItem("ongoingJob", JSON.stringify(response.data)),
            2000
          );
        } else {
          setNoData(true);
          //clear storage
          await AsyncStorage.removeItem("ongoingJob");
        }
      }
    } catch (error) {
      console.log("error in getOngoing", error);
      const storedData = await AsyncStorage.getItem("ongoingJob");
      if (storedData !== null) {
        setonGoingJobData(JSON.parse(storedData));
        setNoData(false);
      } else {
        setNoData(true);
      }
    } finally {
      reloadStats();
      setLoading(false);
    }
  }

  //monitor noData, then clear the cache
  useEffect(() => {
    if (noData) {
      async () => {
        await AsyncStorage.removeItem("ongoingJob");
      };
    }
  }, [noData]);

  useEffect(() => {
    if (!isOnline) {
      checkConnection();
    }

    //giving time to update the network status
    setTimeout(() => {
      fetchData();
    }, 1000);

    //add confirmation before closing app
    const backAction = () => {
      Alert.alert("Exit App!", "Are you sure want to quit?", [
        {
          text: "Cancel",
          onPress: () => null,
          style: "cancel",
        },
        { text: "YES", onPress: () => BackHandler.exitApp() },
      ]);
      return true;
    };

    const backHandler = BackHandler.addEventListener(
      "hardwareBackPress",
      backAction
    );

    return () => backHandler.remove();
  }, []);

  //monitor navigation to add listener for tabPress
  useEffect(() => {
    const tabPressed = navigation.addListener("tabPress", (e) => {
      setLoading(true);
      if (!isOnline) {
        checkConnection();
      }

      //giving time to update the network status
      setTimeout(() => {
        fetchData();
      }, 1000);
    });

    return tabPressed;
  }, [navigation]);

  //monitor changes in jobTabId if this is set somewhere to show Active tab
  useEffect(() => {
    if (jobTabId === "active") {
      setLoading(true);
      setTimeout(() => {
        fetchData();
      }, 2000);
    }
  }, [jobTabId]);

  //monitor changes to onGoingJobData
  useEffect(() => {
    //Get the first index fromLocation to be set as the Pickup location
    let pickUpLocationDetails = onGoingJobData?.trips?.filter(
      (item, idx) => idx === 0
    );

    if (pickUpLocationDetails?.length > 0) {
      setPickUpDetails((prev) => ({
        ...prev,
        ...pickUpLocationDetails[0],
      }));
    }

    let dropOffLocsList = null;
    let multiDrop = false;
    //stores the tripid for single trip
    let singleTripId = null;

    if (onGoingJobData?.trips?.length > 1) {
      multiDrop = true;
      //Get the rest of  the elements and put as dropoff locations
      dropOffLocsList = onGoingJobData?.trips;
      setDropOffLocations((prev) => [...dropOffLocsList]);
    } else {
      multiDrop = false;

      //set similar to pickup locations since it has the to/destination locations anyway
      let dropOffLocationDetail = onGoingJobData?.trips?.filter(
        (item, idx) => idx === 0
      );

      dropOffLocsList = dropOffLocationDetail;

      if (dropOffLocationDetail?.length > 0) {
        singleTripId = dropOffLocationDetail[0]?.id;
        setDropOffDetails((prev) => ({ ...prev, ...dropOffLocationDetail[0] }));
        //reset to empty array or null maybe since it only has one dropoff and it will be set in dropOffDetails
        setConfirmationData({...confirmationData, tlocCargoRec: dropOffLocationDetail[0]?.cargoRecipient})
        setDropOffLocations(dropOffLocsList);
      }
    }

    setIsMultiDrop(multiDrop);
    //check if all the trips are all still active then display the pickup button
    let areAllActive = onGoingJobData?.trips?.every(
      (itm, idx) => itm?.status === "A"
    );
    // console.log("ARE ALL TRIP STILL ACTIVE: ", areAllActive);

    //check if all are P (pending for delivery, which all should be after Pickup)
    let areAllPendingforDelivery = onGoingJobData?.trips?.every(
      (itm, idx) => itm?.status === "P"
    );

    //check if some of the trips is still R, that means dropoff button will still be visible
    const areSomeDelivering = dropOffLocsList?.some(itm => itm?.status === 'R');

    if (areAllActive) {
      setBtnActionState({
        ...btnActionState,
        tripId: pickUpLocationDetails[0]?.id, //use the index 0 one pick up location only
        action: "pickup",
      });
    } else if (areAllPendingforDelivery) {
      setBtnActionState({ ...btnActionState, action: "deliver" });
    } else {
      //not all are active, not all are for pending, so start the dropoff
      if (areSomeDelivering) {
        const pendingDropOffCnt = dropOffLocsList?.filter(
          (el) => el?.status === "R"
        );
        setBtnActionState({
          ...btnActionState,
          action: "dropoff",
          tripId: !multiDrop ? singleTripId : null,
          dropoffSize: dropOffLocsList?.length,
          dropoffCtr: pendingDropOffCnt?.length,
        });
      }
    }
  }, [onGoingJobData]);

  useEffect(() => {
    if (reload) {
      getOngoing();
      setReload(false);
    }
  }, [reload]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    getOngoing();
    // fetchData();
    setTimeout(() => {
      setRefreshing(false);
    }, 2000);
  }, []);

  const jobId = onGoingJobData?.jobId;

  //Trip Data Mapping
  const trip = onGoingJobData?.trip;

  const tripStatus = trip?.status;
  const isDelivering = trip?.status === "R";

  const jobColor = onGoingJobData?.isRed
    ? "#d7170a"
    : onGoingJobData?.isOrange
      ? "#ffaf38"
      : "#3ad16d";

  function handleSetViewCamera(v) {
    // console.log("handle camera visibility");
    if (v) {
      setModalPhotoUpload({ ...modalPhotoUpload, open: !v });
      setCameraScreenVisible(v);
    } else {
      setCameraScreenVisible(v);
      setModalPhotoUpload({ ...modalPhotoUpload, open: !v });
    }
  }

  function handlePhotoComment(e) {
    setPhotoComment(e);
  }

  function handleSetViewSignature(v) {
    // console.log("handle signature screen visibility");
    if (v) {
      setModalCustomerConfirmation(!v);
      setSignatureScreenVisible(v);
    } else {
      setSignatureScreenVisible(v);
      setModalCustomerConfirmation(!v);
    }
  }

  function handlePushPictureData(o) {
    setPhotoData((prevPhotoData) => [o, ...(prevPhotoData ?? [])]);
  }

  function handlePhotoItemPress(i) {
    i ?? 0;
    // console.log("photo item pressed", i);

    // Create a copy of the photoData array
    const updatedPhotoData = [...photoData];

    // Remove the selected photo at the given index
    updatedPhotoData.splice(i, 1);

    // Update the state with the new photoData array
    setPhotoData(updatedPhotoData);
  }

  function handlePushSignatureData(o) {
    // setSignatureData((prevSignatureData) => [o, ...prevSignatureData ?? []])
    setSignatureData(o);
  }

  function handleConfirmationOnChange(v, n) {
    setConfirmationData({ ...confirmationData, [n]: v });
  }

  function handleConfirmDropOffSelect(e) {
    setBtnActionState({
      ...btnActionState,
      tripId: e,
      action: "dropoff",
    });

    setOpenMultiDropSelect(false);
    setModalPhotoUpload({ ...modalPhotoUpload, open: true, action: "dropoff" });
  }

  /** Submit handle for upload photo for PICKUP Aand DROPOFF */
  async function handleSubmitPhoto(action, tripId) {
    // console.log("handlePhotosubmit: ", action, tripId);
    ///FOR PICKUP
    let isDropOff = action === "dropoff" ? true : false;

    let prepData = [];

    // console.log("PHOTO DATA: ", photoData, photoComment);
    if (photoData) {
      for (const item of photoData) {
        const filename = item.uri.split("/").pop();
        const base64data = item.base64;

        prepData.push({ name: filename, data: base64data });
      }
    }

    if (isDropOff) {
      //for dropoff, save the data first before uploading because there will be a next modal
      //for confirmation
      setDropOffPhotoData([...dropOffPhotoData, ...prepData]);
      setModalPhotoUpload({ ...modalPhotoUpload, open: false, tripId: tripId });
      setModalCustomerConfirmation(true);
      // setPhotoData([]);
    } else {
      const submitData = {
        action: "UPLOAD",
        typeData: "PHOTO_PICKUP",
        truckJobId: onGoingJobData?.jobId,
        listData: prepData,
        tripId: tripId,
      };

      const uploadResult = await sendRequest(
        "/api/v1/clickargo/clictruck/mobile/trip/location/attach",
        "post",
        submitData
      );

      //after photo is uploaded, update the photo comment to the trip
      if (uploadResult && uploadResult.status === "SUCCESS") {
        //// IF PHOTO UPLOAD SUCCESS THEN UPDATE JOBTRUCK

        const updateData = {
          action: "MPICKUP",
          truckJobId: onGoingJobData?.jobId,
          tripId: tripId,
          photoComment: photoComment,
          multiDropOff: isMultiDrop,
          dropOffTrips: dropOffLocations?.map((el) => el?.id),
        };

        const pickupResult = await sendRequest(
          "/api/v1/clickargo/clictruck/mobile/trip",
          "put",
          updateData
        );

        if (pickupResult) {
          setonGoingJobData(pickupResult.data);

          await sendRequest(
              "/api/v1/clickargo/clictruck/mobile/job/trip/cargos/checklist",
              "PUT",
              dropOffLocations?.flatMap(val => val?.cargos || [])
          );

          setPhotoData([]);

          //reset photomodal upload state
          setModalPhotoUpload({
            ...modalPhotoUpload,
            open: false,
            tripId: null,
          });

          //set the  button to deliver to show the redo upload and deliver button
          //retain the  tripID
          setBtnActionState({
            ...btnActionState,
            action: "delivery",
          });
          setModalContinueJob(true);
        }
      } else {
        setModalPhotoUpload({ ...modalPhotoUpload, open: false, tripId: null });
      }
    }
  }

  async function handleRedoPhotoUpload() {
    //if state is still in P then it still can be reverted to pickup
    let isDropOff = btnActionState?.action === "deliver" ? false : true;
    const cancelDropOffData = {
      action: "CANCEL",
      typeData: "PHOTO_DROPOFF",
      ckJobTruck: onGoingJobData,
    };

    if (isDropOff) {
      const redoDropOff = await sendRequest(
        cancelDropOffUrl,
        "post",
        cancelDropOffData
      );
      if (redoDropOff) {
        setModalCustomerConfirmation(false);
        setModalPhotoUpload({
          ...modalPhotoUpload,
          open: true,
          tripId: btnActionState?.tripId,
        });
      }
    } else {
      const redoData = {
        action: "MREDO",
        tripId: pickUpDetails?.id,
        truckJobId: onGoingJobData?.jobId,
        multiDropOff: isMultiDrop,
        dropOffTrips: dropOffLocations?.map((el) => el?.id),
      };

      const redoResult = await sendRequest(
        "/api/v1/clickargo/clictruck/mobile/trip",
        "put",
        redoData
      );

      if (redoResult) {
        setonGoingJobData({ ...onGoingJobData, ...redoResult.data });
        setModalContinueJob(false);
        setModalPhotoUpload({
          ...modalPhotoUpload,
          open: true,
          tripId: pickUpDetails?.id, //set the tripid to the pickup location id
        });
      }
    }
  }

  async function handleStartDeliver() {
    const deliverData = {
      action: "MDELIVER",
      tripId: pickUpDetails?.id,
      truckJobId: onGoingJobData?.jobId,
      multiDropOff: isMultiDrop,
      dropOffTrips: dropOffLocations?.map((el) => el?.id),
    };
    setLoading(true)
    const deliverResult = await sendRequest(
      "/api/v1/clickargo/clictruck/mobile/trip",
      "put",
      deliverData
    );
    if (deliverResult) {
      setonGoingJobData({ ...onGoingJobData, ...deliverResult.data });
      //reset the tripId to be used later for multiple drop off, otherwise set to the dropoff details id
      setBtnActionState({
        ...btnActionState,
        action: "dropoff",
        tripId: isMultiDrop ? null : dropOffDetails?.id,
      });
      setLoading(false)
      setModalContinueJob(false);
      setModalStartDelivery(false);
    }
  }

  /** For dropping off cargos */
  async function handleSubmitDropOffConfirmation() {
    // console.log("isMultiDrop?", !isMultiDrop);
    let dropOffConfirmRequest = {
      // typeData: "SIGNATURE",
      truckJobId: onGoingJobData?.jobId,
      tripId: btnActionState?.tripId,
      updateTruckJob: !isMultiDrop || btnActionState?.dropoffCtr === 1, //update the truck job state if it's not multidrop
      multiDrop: isMultiDrop,
      imageData: {
        SIGNATURE: [
          {
            name: "signature.png",
            data: signatureData.substring("data:image/png;base64,".length),
          },
        ],
        PHOTO_DROPOFF: [...dropOffPhotoData],
      },
      comment: confirmationData?.tlocComment,
      tlocCargoRec: confirmationData?.tlocCargoRec,
    };

    const dropOffResult = await sendRequest(
      "/api/v1/clickargo/clictruck/mobile/trip/confirmation",
      "post",
      dropOffConfirmRequest
    );

    if (dropOffResult) {
      //if current onGoingData is multi-drop, update counter
      let dropOffCtr = dropOffLocations?.filter(
        (el) => el?.status === "D"
      )?.length;

      if (isMultiDrop) {
        console.log("still have pending ");
        setBtnActionState({
          ...btnActionState,
          action: "dropoff",
          dropoffCtr: dropOffCtr,
        });
        //reload the data
        setonGoingJobData({ ...onGoingJobData, ...dropOffResult });
        setReload(true);
      } else {
        //reset multidrop
        setIsMultiDrop(false);
        setNoData(true);
        setonGoingJobData();
      }

      setModalCustomerConfirmation(false);
      setDropOffPhotoData([]);
      setPhotoData([]);
      setConfirmationData({ ...confirmationData, ...custConfirmData });
      setSignatureData("");
      await sendRequest(
          "/api/v1/clickargo/clictruck/mobile/job/trip/cargos/checklist",
          "PUT",
          dropOffLocations?.flatMap(val => val?.cargos || [])
      );
    }
  }

  const handlePressRemarks = (remarks) => {
    setModalRemark({ ...modalRemark, open: true, content: remarks });
  };
  if (noData) {
    return (
      <CtLinearBackground>
        <ScrollView
          style={{
            backgroundColor: "transparent",
            paddingHorizontal: horizontalScale(15),
            height: "100%",
          }}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
        >
          <Block
            style={{
              flex: 1,
              alignItems: "center",
              justifyContent: "center",
              borderRadius: 10,
              padding: 10,
              marginTop: 20,
            }}
          >
            <Icon
              name="alert-circle-outline"
              family="ionicon"
              color="#FE3727"
              size={40}
            />
            {newStats.count === 0 && pauseStats.count === 0 && (
              <Text
                style={{
                  fontSize: getFontByFontScale(fontScale, 25, 16),
                  textAlign: "center",
                }}
              >
                There is no ongoing job.{"\n"}
              </Text>
            )}
            {newStats.count > 0 && pauseStats.count > 0 && (
              <Text
                style={{
                  fontSize: getFontByFontScale(fontScale, 25, 16),
                  textAlign: "center",
                }}
              >
                {t("other:jobTab.ongoingJob")}{"\n"}{t("other:jobTab.plsStartOrResume")}{"\n"}
              </Text>
            )}
            {newStats.count > 0 && pauseStats.count === 0 && (
              <Text
                style={{
                  fontSize: getFontByFontScale(fontScale, 25, 16),
                  textAlign: "center",
                }}
              >
                {t("other:jobTab.ongoingJob")}{"\n"}{t("other:jobTab.plsStart")}{"\n"}
              </Text>
            )}
            {newStats.count === 0 && pauseStats.count > 0 && (
              <Text
                style={{
                  fontSize: getFontByFontScale(fontScale, 25, 16),
                  textAlign: "center",
                }}
              >
                {t("other:jobTab.ongoingJob")}{"\n"}{t("other:jobTab.plsResume")}{"\n"}
              </Text>
            )}
          </Block>
        </ScrollView>
      </CtLinearBackground>
    );
  }

  return (
    <>
      <CtLoading
        isVisible={loading}
        title={t("common:loading")}
        onBackdropPress={() => setLoading(false)}
        onRequestClose={() => setLoading(false)}
      />

      <CtLinearBackground>
        <ScrollView
          style={{ width: "100%", height: "100%" }}
          contentContainerStyle={{
            width: "100%",
            alignItems: "center",
            paddingHorizontal: 10,
          }}
          showsVerticalScrollIndicator={false}
          automaticallyAdjustContentInsets
          refreshControl={
            <RefreshControl
              scrollEnabled
              refreshing={refreshing}
              onRefresh={onRefresh}
            />
          }
        >
          <Block card flex style={styles.cardContainer}>
            {
              // begin header
            }
            <Block style={[styles.blockContainer]}>
              <Block style={styles.cardHeader}>
                <View
                  style={{
                    backgroundColor: "#ffb74d",
                    borderRadius: 5,
                    padding: 4,
                  }}
                >
                  <Text style={[styles.highlightedLabel]}>
                    {onGoingJobData?.shipmentType}
                  </Text>
                </View>
                <Button
                  shadowless
                  style={{
                    backgroundColor: theme.COLORS.TRANSPARENT,
                    borderRadius: 0,
                    width: 0,
                    borderRightWidth: 1,
                    borderRightColor: theme.COLORS.MUTED,
                  }}
                ></Button>
                <Text
                  style={[
                    {
                      fontSize: getFontByFontScale(fontScale, 25, 14),
                      paddingLeft: 2,
                      fontWeight: "400",
                      color: jobColor,
                    },
                  ]}
                >
                  {onGoingJobData?.driverRefNo}
                </Text>
              </Block>
            </Block>
            {
              // end header
              // begin Pickup
            }
            <Block
              flex
              space="evenly"
              style={{
                borderWidth: 1,
                borderColor: "rgba(247,28,5, 0.6)",
                borderRadius: 10,
                width: "100%",
              }}
            >
              <Block flex row style={[styles.pickupHeader]}>
                <Block>
                  <Text style={[styles.h5Style]}>
                    {t("job:ongoing.pickUpLoc")}
                  </Text>
                </Block>
                <Block column right flex>
                  <Text
                    style={{
                      color: "#fff",
                      fontWeight: "400",
                      fontSize: getFontByFontScale(fontScale, 16, null),
                    }}
                  >
                    {displayDate(
                      pickUpDetails?.estPickupTime,
                      i18n.language,
                      false
                    )}
                  </Text>
                  <Text
                    style={{
                      color: "#fff",
                      fontWeight: "400",
                      fontSize: getFontByFontScale(fontScale, 16, null),
                    }}
                  >
                    <FontAwesome5
                      name="clock"
                      size={getFontByFontScale(fontScale, 16, null)}
                    />{" "}
                    {displayDate(
                      pickUpDetails?.estPickupTime,
                      i18n.language,
                      false,
                      true
                    )}
                  </Text>
                </Block>
              </Block>
              <Block
                style={[styles.cardBody, { paddingTop: 10, width: "100%" }]}
              >
                <Block style={{ flex: 0.2, alignItems: "center" }}>
                  <Block style={styles.locationPin}>
                    <Icon
                      name="location"
                      family="ionicon"
                      size={moderateScale(40)}
                      color={"rgba(247,28,5, 0.6)"}
                    />
                  </Block>
                </Block>
                <Block row space="evenly" style={{ flex: 0.75 }}>
                  <Block
                    style={{
                      flexDirection: "column",
                      width: "75%",
                    }}
                  >
                    <Text style={[styles.h5Style, { color: "#000" }]}>
                      {pickUpDetails?.fromLocName}
                    </Text>
                    <Text style={styles.subtitle}>
                      {pickUpDetails?.fromLocAddr}
                    </Text>
                  </Block>
                  <Block row space="evenly" center>
                    <OpenExternalUrl
                      url={
                        "https://www.google.com/maps/search/" +
                        pickUpDetails?.fromLocAddr
                      }
                    >
                      <Image
                        source={require("../assets/images/button/googlemap.png")}
                        style={{ width: 30, height: 30 }}
                      />
                    </OpenExternalUrl>
                  </Block>
                </Block>
              </Block>
              {pickUpDetails?.fromLocRemarks && pickUpDetails?.fromLocRemarks !== '' && (
                  <Block style={{paddingLeft:10}}>
                    <Text style={{fontSize: getFontByFontScale(fontScale, 24, 12), fontWeight: "bold"}}>
                      <SimpleLineIcons name="speech" size={12} color={materialTheme.COLORS.CKPRIMARY} />
                      {' '}Remark:
                    </Text>
                    <ShowRemark text={pickUpDetails?.fromLocRemarks} />
                  </Block>
              )}
            </Block>
            {
              // end Pckup
              // begin DropOff
            }
            {/* Intentional block for division */}
            <Block style={{marginTop: verticalScale(10)}}/>

            {/* Drop off locations */}
            {dropOffLocations?.length > 1 ? (
              <Block
                flex
                style={{
                  borderWidth: 1,
                  borderColor: "rgba(11,162,71, 0.6)",
                  borderRadius: 10,
                  width: "100%",
                }}
              >
                <Block flex row style={styles.dropOffHeader}>
                  <Block flex row space="between">
                    <Block
                      style={{
                        borderRadius: 5,
                        padding: 4,
                      }}
                    >
                      <Text style={styles.h5Style}>
                        {dropOffLocations.length} - {t("other:jobTab.dropOffLocations")}
                      </Text>
                    </Block>
                  </Block>
                </Block>
                {dropOffLocations &&
                  dropOffLocations?.map((item, idx) => {
                    return (
                      <DropOffDetailsActive
                        styles={styles}
                        item={item}
                        key={idx}
                        idx={idx}
                        fontScale={fontScale}
                        t={t}
                        locale={i18n.language}
                        location={dropOffLocations}
                        jobData = {onGoingJobData}
                        handlePressRemarks={handlePressRemarks}
                      />
                    );
                  })}
              </Block>
            ) : (
              <OngoingJobSingleDropOff
                t={t}
                dropOffDetails={dropOffDetails}
                jobData = {onGoingJobData}
                styles={styles}
                fontScale={fontScale}
                handlePressRemarks={handlePressRemarks}
              />
            )}

            {additionalDtls && (
              <>
                <Block style={{marginTop: verticalScale(10)}}/>
                <JobAdditionalDetails
                  details={additionalDtls}
                  fontScale={fontScale}
                />
              </>
            )}
            <Block style={{marginTop: verticalScale(10)}}/>
            {/* Timing Details */}
            {dropOffLocations?.length === 1 && (
              <OngoingJobTimingDetails
                locale={i18n.language}
                timingDetails={pickUpDetails}
                fontScale={fontScale}
              />
            )}
          </Block>
          <Block marginBottom={50} />
        </ScrollView>

        {/* For pickup button if all the trip states are Active (A) */}
        {/* {console.log("BTN ACTION STATE:", btnActionState)} */}
        {btnActionState && btnActionState?.action === "pickup" && (
          <Block style={[styles.fab]}>
            <View style={styles.fabView}>
              <MKButton
                gradient
                size="large"
                style={ckComponentStyles.ckbuttons}
                onPress={() =>
                  setModalPhotoUpload({
                    ...modalPhotoUpload,
                    open: true,
                    tripId: btnActionState?.tripId,
                  })
                }
                fontSize={getFontByFontScale(fontScale, 25, null)}
              >
                {t("button:pickup")}{" "}
              </MKButton>
            </View>
          </Block>
        )}

        {/* For Redo Upload and Deliver Button */}
        {btnActionState && btnActionState?.action === "deliver" && (
          <Block style={[styles.buttonItem, styles.fab]}>
            <Block
              style={[
                styles.fabView,
                {
                  flex: 1,
                  flexDirection: "row",
                  justifyContent: "space-evenly",
                },
              ]}
            >
              <MKButton
                gradient
                size="small"
                style={{
                  width: "45%",
                  flexDirection: "row",
                  justifyContent: "center",
                  borderRadius: 10,
                  alignItems: "center",
                }}
                onPress={() => handleRedoPhotoUpload()}
                fontSize={getFontByFontScale(fontScale, 18, null)}
              >
                {t("button:redoUpload")}{" "}
              </MKButton>
              <MKButton
                gradient
                size="small"
                style={{
                  width: "45%",
                  flexDirection: "row",
                  justifyContent: "center",
                  borderRadius: 10,
                  alignItems: "center",
                }}
                onPress={() => setModalStartDelivery(true)}
                fontSize={getFontByFontScale(fontScale, 18, null)}
              >
                {t("button:deliver")}{" "}
              </MKButton>
            </Block>
          </Block>
        )}

        {btnActionState && btnActionState?.action === "dropoff" && (
          <Block style={[styles.buttonItem, styles.fab]}>
            <View style={styles.fabView}>
              <MKButton
                gradient
                size="large"
                style={ckComponentStyles.ckbuttons}
                onPress={() => {
                  dropOffLocations?.length > 1
                    ? setOpenMultiDropSelect(true)
                    : setModalPhotoUpload({ ...modalPhotoUpload, open: true });
                }}
                fontSize={getFontByFontScale(fontScale, 25, null)}
              >
                {t("button:dropOff")}
                {isMultiDrop
                  ? ` (${btnActionState?.dropoffCtr} / ${btnActionState?.dropoffSize})`
                  : null}
              </MKButton>
            </View>
          </Block>
        )}

        {/*  modal for drop off location selection for multiple drop offs*/}
        {openMultiDropSelect && (
          <MultiDropOffLocationSelectPopup
            show={openMultiDropSelect}
            onClosePressed={() => setOpenMultiDropSelect(false)}
            list={dropOffLocations?.filter((el) => ["A", "R"].includes(el?.status))}
            handleConfirm={(e) => handleConfirmDropOffSelect(e)}
            fontScale={fontScale}
          />
        )}
        {/* modal photos upload use in pickup and dropoff */}
        {/* {console.log("modalPhotoUpload", modalPhotoUpload)} */}
        {modalPhotoUpload && modalPhotoUpload?.open && (
          <PhotoUploadPopup
            show={modalPhotoUpload?.open}
            fontScale={fontScale}
            onClosePressed={() =>
              setModalPhotoUpload({
                ...modalPhotoUpload,
                open: false,
                tripId: null,
              })
            }
            action={btnActionState?.action}
            tripId={btnActionState?.tripId}
            handleSubmitPhoto={handleSubmitPhoto}
            photoData={photoData}
            handlePhotoItemPress={handlePhotoItemPress}
            handleSetViewCamera={handleSetViewCamera}
            onChangeText={handlePhotoComment}
            dropOffLocations={dropOffLocations}
          />
        )}

        {/* modal for continue job */}
        {modalContinueJob && (
          <ContinueJob
            show={modalContinueJob}
            fontScale={fontScale}
            onClosePressed={() => {
              setModalContinueJob(false);
              setBtnActionState({ ...btnActionState, action: "deliver" });
            }}
            onYesPressed={() => handleStartDeliver()}
            navigation={navigation}
          />
        )}

        {/* modal for deliver */}
        {modalStartDelivery && (
          <CargoDelivery
            show={modalStartDelivery}
            fontScale={fontScale}
            onClosePressed={() => setModalStartDelivery(false)}
            onYesPressed={() => handleStartDeliver()}
          />
        )}

        {/* modal for remark */}
        {modalRemark && modalRemark?.open && (
          <CargoRemarks
            show={modalRemark?.open}
            onClosePressed={() =>
              setModalRemark({ ...modalRemark, open: false, content: null })
            }
            remarks={modalRemark?.content}
          />
        )}

        {/* modal for cargo detail */}
        {modalCargoDetail && (
          <CargoDetails
            show={modalCargoDetail}
            onClosePressed={() => setModalCargoDetail(false)}
            data={cargoData}
          />
        )}

        {/* modal for confirmation detail on drop off */}
        {modalCustomerConfirmation && (
          <DropOffConfirmation
            show={modalCustomerConfirmation}
            fontScale={fontScale}
            onClosePressed={() => setModalCustomerConfirmation(false)}
            signatureData={signatureData}
            setSignatureData={setSignatureData}
            handleSetViewSignature={handleSetViewSignature}
            confirmationData={confirmationData}
            handleRedoPhotoUpload={() => handleRedoPhotoUpload(isDelivering)}
            handleSubmitConfirmation={() => handleSubmitDropOffConfirmation()}
            onChangeText={handleConfirmationOnChange}
          />
        )}

        {cameraScreenVisible && (
          <Modal visible={cameraScreenVisible}>
            <PhotoUploadScreen
              setVisible={handleSetViewCamera}
              pushData={handlePushPictureData}
            />
          </Modal>
        )}
        {signatureScreenVisible && (
          <Modal visible={signatureScreenVisible}>
            <SignatureScreen
              setVisible={handleSetViewSignature}
              pushData={handlePushSignatureData}
            />
          </Modal>
        )}
      </CtLinearBackground>
    </>
  );
}

const makeStyles = (fontScale) =>
  StyleSheet.create({
    cardContainer: {
      borderRadius: 20,
      padding: 12,
      marginVertical: 8,
      shadowColor: "#000",
      shadowOffset: { width: 0, height: 2 },
      shadowRadius: 3,
      shadowOpacity: 0.1,
      elevation: 2,
      backgroundColor: theme.COLORS.WHITE,
      alignItems: "center",
      flexDirection: "column",
      borderWidth: 1,
      width: Platform.OS === "ios" ? "95%" : "97%",
    },
    cardHeader: {
      display: "flex",
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "flex-start",
      borderBottomWidth: 0.5,
      borderColor: "#ccc",
      width: "100%",
      paddingLeft: 5,
    },
    cardBody: {
      display: "flex",
      flexDirection: "row",
      paddingBottom: 10,
    },
    highlightedLabel: {
      color: "#fff",
      fontSize: getFontByFontScale(fontScale, 25, 14),
      fontWeight: "bold",

      // backgroundColor: "#e7e7e7"
    },
    pickupHeader: {
      flex: 1,
      paddingBottom: 10,
      backgroundColor: "rgba(247,28,5, 0.6)",
      justifyContent: "space-between",
      width: "100%",
      borderRadius: 5,
      padding: 6,
    },
    dropOffHeader: {
      flex: 1,
      width: "100%",
      paddingBottom: 10,
      backgroundColor: "rgba(11,162,71, 0.6)",
      justifyContent: "space-between",
      flexDirection: "row",
      borderRadius: 5,
      padding: 6,
    },
    locationPin: {
      alignItems: "center",
      justifyContent: "center",
      borderRadius: 12,
      backgroundColor: "#EFF2F1",
      width: moderateScale(50),
      height: moderateScale(60),
    },
    h5Style: {
      fontSize: getFontByFontScale(fontScale, 24, 16),
      fontWeight: "600",
      color: "#fff",
    },
    subtitle: {
      color: materialTheme.COLORS.BLACK,
      fontWeight: "200",
      fontSize: getFontByFontScale(fontScale, 24, 16),
    },
    container: {
      flex: 1,
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "#eeeeee",
      width: "100%",
    },
    buttonBar: {
      width: "97%",
      marginTop: 10,
      marginBottom: 10,
      backgroundColor: "transparent",
      alignItems: "center",
    },
    buttonItem: {
      width: 100, //change to 30%
      minWidth: horizontalScale(80),
      minHeight: verticalScale(70),
      alignItems: "center",
      justifyContent: "center",
    },
    buttonItemIcon: {
      padding: 10,
      backgroundColor: "rgba(255, 255, 255, 1)",
      borderRadius: 50,
      //android shadow
      elevation: 10,
      //ios shadow
      shadowColor: "#000000",
      shadowOpacity: 0.5,
      shadowRadius: 2,
      shadowOffset: {
        height: 1,
        width: 1,
      },
      marginBottom: 5,
    },
    buttonItemIconSize: {
      height: moderateScale(20),
      width: moderateScale(20),
    },
    buttonIconLabel: {
      padding: 3,
      fontSize: 10,
      fontWeight: "600",
    },
    blockContainer: {
      width: Platform.OS === "ios" ? "100%" : "97%",
      borderRadius: 10,
      backgroundColor: "transparent",
      overflow: "hidden",
      alignItems: "center",
      marginBottom: 15,
    },
    locationContainer: {
      width: Platform.OS === "ios" ? "100%" : "97%",
      borderColor: "#000",
      borderWidth: 1,
      borderRadius: 10,
      backgroundColor: "transparent",
      overflow: "hidden",
      alignItems: "center",
      marginBottom: 15,
    },
    listTitle: {
      width: "95%",
      padding: 5,
      flexDirection: "row",
      justifyContent: "space-between",
      marginBottom: 10,
      backgroundColor: "transparent",
    },
    locTitle: {
      width: "95%",
      padding: 5,
      backgroundColor: "transparent",
    },
    locDetailContainer: {
      width: "95%",
      flexDirection: "row",
      marginBottom: 10,
      backgroundColor: "transparent",
    },
    locDetailLogo: {
      width: "20%",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "transparent",
    },
    locDetailContent: {
      width: "60%",
      backgroundColor: "transparent",
    },
    locDetailButton: {
      width: "20%",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "transparent",
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
    },
    textModalHeader: {
      fontSize: moderateScale(16),
      textAlign: "left",
    },
    textModalBody: {
      fontSize: moderateScale(14),
    },
    modalHalf: {
      width: "50%",
      paddingHorizontal: 5,
    },
    photoListContainer: {
      minHeight: verticalScale(80),
      borderWidth: 1,
      borderColor: "#ccc",
      marginVertical: 10,
      borderRadius: moderateScale(8),
    },
    photoListContainerContent: {
      alignItems: "center",
    },
    photoListItem: {
      height: moderateScale(50),
      width: moderateScale(50),
      backgroundColor: "#eee",
      alignItems: "center",
      justifyContent: "center",
      margin: 5,
      borderRadius: moderateScale(8),
    },
    fab: {
      position: "absolute",
      width: "100%",
      alignItems: "center",
      justifyContent: "center",
      right: 0,
      bottom: 8,
      backgroundColor: "transparent",
      elevation: 10,
    },
    fabIcon: {
      height: moderateScale(40),
      width: moderateScale(40),
    },
    fabView: {
      paddingHorizontal: 8,
      backgroundColor: "transparent",
      borderRadius: 10,
      //android shadow
      elevation: 10,
      //ios shadow
      shadowColor: "#000000",
      shadowOpacity: 0.5,
      shadowOffset: {
        height: 1,
        width: 1,
      },
      width: Platform.OS === "ios" ? "95%" : "97%",
    },
    mutedLabel: {
      color: materialTheme.COLORS.MUTED,
      fontWeight: 500,
      fontSize: moderateScale(14),
      paddingBottom: 5,
    },
    mutedSubLabel: {
      color: materialTheme.COLORS.MUTED,
      fontWeight: 400,
      fontSize: moderateScale(14),
      paddingBottom: 5,
    },
    mutedIcon: {
      size: moderateScale(14),
    }
  });
