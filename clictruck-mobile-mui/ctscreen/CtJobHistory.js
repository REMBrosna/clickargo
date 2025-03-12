import React, { useState, useEffect } from "react";
import {
  StyleSheet,
  View,
  FlatList,
  Platform,
  Text,
  Alert,
  useWindowDimensions,
} from "react-native";
import { horizontalScale, verticalScale } from "../constants/metrics";
import JobHistoryCard from "./history/JobHistoryCard";
import JobHistoryFilter from "./history/JobHistoryFilter";
import CargoDetails from "./history/CargoDetails";
import CargoRemarks from "./history/CargoRemarks";
import { sendRequest } from "../utils/httpUtil";
import { Block } from "galio-framework";
import { jobHistoryList } from "../constants/urls";
import { useTranslation } from "react-i18next";
import { Icon } from "../components";
import ErrorModal from "./ErrorModal";
import * as FileSystem from "expo-file-system";
import * as MediaLibrary from "expo-media-library";
import CtLoading from "./CtLoading";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import * as Sharing from "expo-sharing";
import CtLinearBackground from "../ctcomponents/CtLinearBackground";
import { materialTheme } from "../constants";
import ListingHeaders from "./headers/ListingHeaders";
import { useListEvents } from "../context/ListEventsContext";
import JobHistoryCardSummary from "./history/JobHistoryCardSummary";

const HistoryJobScreen = () => {
  const navigation = useNavigation();

  const { t } = useTranslation();
  const { fontScale } = useWindowDimensions();

  const orderOpt = [
    {
      label: t("jobHistory:popup.filter.startTime"),
      value: "tckJob.tckRecordDate.rcdDtStart",
    },
    {
      label: t("jobHistory:popup.filter.completedTime"),
      value: "tckJob.tckRecordDate.rcdDtComplete",
    },
  ];

  const [modalFilterVisible, setModalFilterVisible] = useState(false);

  const [showDropdown, setShowDropdown] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState("");
  const [orderItem, setOrderItem] = useState(orderOpt);
  const [inputData, setInputData] = useState();
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [cargoData, setCargoData] = useState();
  const [remarkData, setRemarkData] = useState();

  const [modalCargoDetail, setModalCargoDetail] = useState(false);
  const [modalRemark, setModalRemark] = useState(false);
  const [loading, setLoading] = useState(false);

  const [filterState, setFilterState] = useState({
    startDate: "",
    endDate: "",
    orderBy: "",
  });

  const [errorModalState, setErrorModalState] = useState({
    message: "",
    showErrorModal: false,
  });

  const { showSpecialInst, showDetailed } = useListEvents();

  async function getData() {
    const result = await sendRequest(jobHistoryList);
    if (result) {
      setInputData(result.aaData);
      setLoading(false);
    }
  }

  async function getDataFilter(selectedOrder, timestampStart, timestampEnd) {
    try {
      //update jobHistoryList url
      let idx = 1;
      let url = jobHistoryList;
      if (selectedOrder) {
        url += url.replace(`jobDtLupd`, `${selectedOrder}`);
      }

      if (timestampStart) {
        const formattedTlocDtStart = new Date(
          timestampStart.toISOString().split("T")[0]
        ).toLocaleDateString();

        url += `&mDataProp_${idx}=tckJob.tckRecordDate.rcdDtStart`;
        url += `&sSearch_1=${formattedTlocDtStart}`;
        idx++;
      }

      if (timestampEnd) {
        const formattedTlocDtEnd = new Date(
          timestampEnd.toISOString().split("T")[0]
        ).toLocaleDateString();

        url += `&mDataProp_${idx}=tckJob.tckRecordDate.rcdDtComplete`;
        url += `&sSearch_1=${formattedTlocDtEnd}`;
        idx++;
      }

      const result = await sendRequest(url);

      if (result) {
        setInputData(result.aaData);
        setModalFilterVisible(false);
        setLoading(false);
      } else {
        setLoading(false);
        setErrorModalState({
          ...errorModalState,
          message: "Filter Failed",
          showErrorModal: true,
        });
      }
    } catch (error) {
      setLoading(false);
      setErrorModalState({
        ...errorModalState,
        message: error?.err?.msg,
        showErrorModal: true,
      });
    }
  }

  useEffect(() => {
    setLoading(true);
    getData();
  }, []);

  useEffect(() => {
    if (!loading) setLoading(true);
    if (showSpecialInst) {
      setTimeout(() => {
        let filtered = inputData?.filter((el, idx) => {
          const trips = el?.trips?.filter((tripItem) => {
            const trip = tripItem?.cargos?.filter(
              (cargo) => cargo?.specialInstructions
            );

            return trip?.length > 0;
          });

          return trips?.length > 0;
        });

        setInputData(filtered ? [...filtered] : []);
        setLoading(false);
      }, 2000);
    } else {
      getData();
    }
  }, [showSpecialInst]);

  function onRefresh() {
    setIsRefreshing(true);
    try {
      // reloadStats();
      getData();
    } catch {
      console.error("onRefresh error");
    } finally {
      setIsRefreshing(false);
      setShowDropdown(false);
      setSelectedOrder("");
      setFilterState({
        startDate: "",
        endDate: "",
        orderBy: "",
      });
    }
  }

  const handleSubmitFilter = () => {
    let filter = "";
    if (!selectedOrder && !filterState?.startDate && !filterState?.endDate) {
      Alert.alert("Warning", "Please enter at least one filter criteria.", [
        { text: "OK", onPress: null },
      ]);
    } else {
      getDataFilter(
        selectedOrder,
        filterState?.startDate,
        filterState?.endDate
      );
    }
  };

  const handleResetFilter = () => {
    setFilterState({ ...filterState, startDate: "", endDate: "", orderBy: "" });
    setSelectedOrder("");
    getData();
  };

  async function handleJobViewCargo(jobId) {
    setModalCargoDetail(true);

    let foundJob = inputData?.find((item) => item?.jobId === jobId);

    if (foundJob) {
      setCargoData(foundJob);
    }
  }

  async function handleJobViewRemark(jobId) {
    setModalRemark(true);
    let foundJob = inputData?.find((item) => item?.jobId === jobId);

    if (foundJob) {
      setRemarkData(foundJob);
    }
  }

  async function handleEpodDownload(id, by) {
    // fetch data from server
    setLoading(true);

    try {
      //default to trip epodz
      let url = "/api/v1/clickargo/clictruck/mobile/downloadDsv/" + id;
      if (by === "job")
        url = "/api/v1/clickargo/clictruck/mobile/downloadJobEpod/" + id;

      const result = await sendRequest(url);
      if (result) {
        const filestring = result.data?.[0].doaData;
        await saveFile(filestring, "EPOD_" + id);
      } else {
        Alert.alert("Error", `File not exist`);
      }
    } catch (error) {
      Alert.alert("Error", `Could not Download file, ${error.message}`);
    } finally {
      setLoading(false);
    }
  }

  async function handlePhotoDownload(id, by, type) {
    setLoading(true);
    let url =
      "/api/v1/clickargo/clictruck/mobile/trip/location/attach?tripId=" + id;

    if (by === "job") {
      url =
        "/api/v1/clickargo/clictruck/mobile/job/trip/location/attach?jobId=" +
        id;
    }

    if (type === "pickup") {
      url = url + "&type=pickup";
    }

    try {
      const result = await sendRequest(url);

      if (result) {
        if (by === "job") {
          Alert.alert("Note", "One or more images will be saved", [
            {
              text: "OK",
              onPress: async () => {
                for (const element of result.data) {
                  const file = await saveImage(
                    element.atLocData,
                    element.atName,
                    id
                  );
                  if (file) {
                    saveToGallery(file, id);
                  }
                }
              },
            },
          ]);
        } else {
          for (const element of result.data) {
            const file = await saveImage(element.atLocData, element.atName, id);
            if (file) {
              saveToGallery(file, id);
            }
          }
        }
      }
    } catch (error) {
      Alert.alert("Error", `Could not Download file, ${error.message}`);
    } finally {
      setLoading(false);
    }
  }

  const saveFile = async (data, fileName = "File") => {
    let localPath = `${FileSystem.documentDirectory}${fileName}.pdf`;

    try {
      await FileSystem.writeAsStringAsync(localPath, data, {
        encoding: FileSystem.EncodingType.Base64,
      });

      Sharing.shareAsync(localPath);
    } catch (error) {
      Alert.alert("INFO", JSON.stringify(error));
    }
  };

  async function checkDirectory(dirLoc) {
    const directoryInfo = await FileSystem.getInfoAsync(dirLoc);
    if (!directoryInfo.exists) {
      const createDir = await FileSystem.makeDirectoryAsync(dirLoc, {
        intermediates: true,
      });
      if (createDir) {
        return true;
      }
    } else {
      return true;
    }
  }

  async function saveImage(base64, filename, tripId) {
    let filedir = FileSystem.documentDirectory + "media/";
    if (tripId) {
      filedir = filedir + tripId + "/";
    }
    let filepath = filedir + filename;
    await checkDirectory(filedir);
    try {
      await FileSystem.writeAsStringAsync(filepath, base64, {
        encoding: FileSystem.EncodingType.Base64,
      });
      return `file://${filepath}`;
    } catch (e) {
      console.error("save image fail: ", e);
      return e;
    }
  }

  async function saveToGallery(imageUri, tripId) {
    if (imageUri) {
      try {
        await MediaLibrary.requestPermissionsAsync();
        const asset = await MediaLibrary.createAssetAsync(imageUri);

        await MediaLibrary.createAlbumAsync(
          tripId ?? "ClictruckImage",
          asset,
          false
        );
        alert("Image saved to gallery!");
      } catch (e) {
        console.error("Fail to Save to Gallery ", e);
      }
    } else {
      alert("Image not loaded yet. Please wait.");
    }
  }

  const handleModalClose = () => {
    setErrorModalState({ ...errorModalState, showErrorModal: false });
  };

  useFocusEffect(
    React.useCallback(() => {
      navigation.closeDrawer();
    }, [])
  );

  return (
    <>
      {/* <Tabs icon="timer-sand-empty"/> */}
      <Block flex>
        <View style={{ backgroundColor: "#fff" }}>
          {/* <View style={styles.filterContainer}>
            <Pressable onPress={() => setModalFilterVisible(true)}>
              <Text style={styles.subtitle}>
                <Ionicons
                  name="filter-outline"
                  size={getFontByFontScale(fontScale, 24, 24)}
                  color="black"
                />
              </Text>
            </Pressable>
          </View> */}
          <ListingHeaders
            fontScale={fontScale}
            showFilter={{ onPress: () => setModalFilterVisible(true) }}
          />
          <CtLinearBackground>
            <FlatList
              data={inputData}
              onRefresh={onRefresh}
              refreshing={isRefreshing}
              style={{ paddingHorizontal: horizontalScale(15), height: "100%" }}
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
              keyExtractor={(item, i) => item?.jobId}
              renderItem={({ item, index }) =>
                showDetailed ? (
                  <JobHistoryCard
                    item={item}
                    fontScale={fontScale}
                    handleJobViewCargo={handleJobViewCargo}
                    handleJobViewRemark={handleJobViewRemark}
                    handleEpodDownload={handleEpodDownload}
                    handlePhotoDownload={handlePhotoDownload}
                  />
                ) : (
                  <JobHistoryCardSummary
                    item={item}
                    index={index}
                    fontScale={fontScale}
                    handleEpodDownload={handleEpodDownload}
                    handlePhotoDownload={handlePhotoDownload}
                  />
                )
              }
              ListEmptyComponent={
                <Block
                  style={{
                    flex: 1,
                    alignItems: "center",
                    justifyContent: "center",
                    borderRadius: 10,
                    padding: 10,
                  }}
                >
                  <Icon
                    name="alert-circle-outline"
                    family="ionicon"
                    color={"#FE3727"}
                    size={40}
                  />
                  <Text style={{ fontSize: 16, textAlign: "center" }}>
                    {t("other:jobHistory.noJob")}{"\n"}{t("job:pullToRefresh")}
                  </Text>
                </Block>
              }
            />
          </CtLinearBackground>
        </View>
      </Block>
      {/* modal for filter */}
      {modalFilterVisible && (
        <JobHistoryFilter
          show={modalFilterVisible}
          fontScale={fontScale}
          onClosePressed={() => {
            setModalFilterVisible(false);
            setShowDropdown(false);
            setSelectedOrder("");
            setFilterState({
              startDate: "",
              endDate: "",
              orderBy: "",
            });
          }}
          showDropdown={showDropdown}
          selectedOrder={selectedOrder}
          orderItem={orderOpt}
          setOrderItem={setOrderItem}
          setShowDropdown={setShowDropdown}
          setSelectedOrder={setSelectedOrder}
          handleSubmitFilter={handleSubmitFilter}
          handleResetFilter={handleResetFilter}
          filterState={filterState}
          setFilterState={setFilterState}
        />
      )}

      {/* modal for cargo detail */}
      {modalCargoDetail && (
        <CargoDetails
          show={modalCargoDetail}
          onClosePressed={() => setModalCargoDetail(false)}
          data={cargoData}
          fontScale={fontScale}
        />
      )}

      {/* modal for remark */}
      {modalRemark && (
        <CargoRemarks
          show={modalRemark}
          onClosePressed={() => setModalRemark(false)}
          jobData={remarkData}
          remarkValue={remarkData?.tlocRemarks}
          remarkInstr={remarkData?.tlocSpecialInstn}
          fontScale={fontScale}
        />
      )}
      <CtLoading
        isVisible={loading}
        title="Please wait"
        onBackdropPress={() => setLoading(false)}
        onRequestClose={() => setLoading(false)}
      />
      <ErrorModal
        show={errorModalState?.showErrorModal}
        errorMsg={errorModalState?.message}
        onClosePressed={() => handleModalClose()}
      />
    </>
  );
};

export default HistoryJobScreen;

const styles = StyleSheet.create({
  filterContainer: {
    paddingHorizontal: horizontalScale(20),
    marginTop: verticalScale(7),
    alignItems: "flex-end",
    marginBottom: 10,
  },
  subtitle: {
    color: materialTheme.COLORS.BLACK,
    fontWeight: "200",
  },
});
