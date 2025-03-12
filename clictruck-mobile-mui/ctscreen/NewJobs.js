import React, { useCallback, useContext, useEffect, useState } from "react";
import { FlatList, Platform, useWindowDimensions } from "react-native";
import { Block, Text } from "galio-framework";

import { Icon } from "../components";

import useStats from "../hooks/useStats";
import { sendRequest } from "../utils/httpUtil";
import { getStartConfirmUrl, newJobList, startJobUrl } from "../constants/urls";
import {
  getFontByFontScale,
  horizontalScale,
  storeJob,
  verticalScale,
} from "../constants/utils";
import JobItem from "./JobItem";
import JobStartConfirmation from "./JobStartConfirmation";
import CtLoading from "./CtLoading";
import { listingStyles } from "../styles/listingStyles";
import JobTabContext from "../context/JobTabContext";
import CtLinearBackground from "../ctcomponents/CtLinearBackground";
import { useTranslation } from "react-i18next";
import ListingHeaders from "./headers/ListingHeaders";
import JobItemSummary from "./JobItemSummary";
import { useListEvents } from "../context/ListEventsContext";

const NewJobs = (props) => {
  // const navigation = useNavigation();
  const { navigation } = props;
  const { reloadStats, isStatsReloaded, dispatch } = useStats();
  const { jobTabId, setJobTabId } = useContext(JobTabContext);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedJob, setSelectedJob] = useState({
    jobId: null,
    jobRefNo: null,
  });
  const [modalErrMsg, setModalErrMsg] = useState();
  const [inputdata, setInputData] = useState();
  const [isRefreshing, setIsRefreshing] = useState(false);
  const { t } = useTranslation();
  const { fontScale } = useWindowDimensions();
  const { showSpecialInst, showDetailed } = useListEvents();

  async function getData() {
    const result = await sendRequest(newJobList);
    if (result) {
      //   if (showSpecialInst) {
      //     let filtered = result?.aaData?.filter((el, idx) => {
      //       const trips = el?.trips?.filter((tripItem) => {
      //         const trip = tripItem?.cargos?.filter(
      //           (cargo) => cargo?.specialInstructions
      //         );

      //         return trip?.length > 0;
      //       });

      //       return trips?.length > 0;
      //     });

      //     setInputData([...filtered]);
      //   } else {
      setInputData(result.aaData);
      //   }
      setLoading(false);

      dispatch({
        type: "RELOAD",
        payload: {
            newStats: {
              "accnType": "DRIVER",
              "count": result.aaData.length,
              "dbType": "MOBILE_NEW",
              "id": 0,
              "image": null,
              "title": "New"
          },
        },
      });
    }
  }

  useEffect(() => {
    setJobTabId("new");
    getData();
  }, []);

  useEffect(() => {
    if (!loading) setLoading(true);
    if (jobTabId === "new" && showSpecialInst) {
      setTimeout(() => {
        let filtered = inputdata?.filter((el, idx) => {
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
      setLoading(false);
    }
  }, [jobTabId, showSpecialInst]);

  useEffect(() => {
    const tabPressed = navigation.addListener("tabPress", (e) => {
      setLoading(true);
      //only reload data if the special inst is false
      if (!showSpecialInst) getData();
      setJobTabId("new");
    });

    return tabPressed;
  }, [navigation]);

  function onRefresh() {
    setIsRefreshing(true);
    try {
      // reloadStats();
      getData();
    } catch(err) {
      console.error("onRefresh error", err);
    } finally {
      setIsRefreshing(false);
    }
  }

  const handleSelectJob = useCallback(async (id, jobRefNo) => {
    if (id) {
      const confirmation = await sendRequest(getStartConfirmUrl);
      if (confirmation) {
        setShowModal(true);
        setSelectedJob({ ...selectedJob, jobId: id, jobRefNo: jobRefNo });
        setModalErrMsg(confirmation.data);
      }
    } else {
      console.error("invalid job id");
    }
  }, []);

  async function handleStartJob(id) {
    setLoading(true);

    const job = await sendRequest(
      startJobUrl + selectedJob?.jobId + "?action=START",
      "put"
    );
    // console.log("job start result", job);
    if (job) {
      const result = await storeJob(job.data);
      if (result) {
        reloadStats();
        //Set the jobTabId to active after confirmation
        setJobTabId("active");
      } else {
        console.error("failed to store ongoing job to local storage");
      }
    } else {
      console.error("invalid job data");
    }
    setShowModal(false);
    setLoading(false);
    navigation.navigate("CtHomeScreen");
  }

  return (
    <>
      <ListingHeaders fontScale={fontScale} />
      <Block flex center style={listingStyles.home}>
        <CtLinearBackground>
          <FlatList
            showsVerticalScrollIndicator={false}
            data={inputdata}
            onRefresh={onRefresh}
            refreshing={isRefreshing}
            contentContainerStyle={{ width: "100%" }}
            style={{
              paddingHorizontal: horizontalScale(15),
              width: "100%",
              height: "100%",
            }}
            renderItem={({ item, index }) =>
              showDetailed ? (
                <JobItem
                  {...{
                    ...item,
                    selectId: () =>
                      handleSelectJob(item?.jobId, item?.jobShipmentRef),
                  }}
                />
              ) : (
                <JobItemSummary
                  {...{
                    ...item,
                    index,
                    selectId: () =>
                      handleSelectJob(item?.jobId, item?.jobShipmentRef),
                  }}
                />
              )
            }
            keyExtractor={(item) => item.jobId}
            ItemSeparatorComponent={() => {
              return <Block style={{ marginVertical: 0 }} />;
            }}
            ListHeaderComponent={() => {
              return <Block style={{marginTop: verticalScale(20)}}/>;
            }}
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
                <Text
                  style={{
                    fontSize: getFontByFontScale(fontScale, 25, 16),
                    textAlign: "center",
                  }}
                >
                  {" "}
                  {t("job:noNewRecords")}
                  {"\n"}
                  {t("job:pullToRefresh")}
                </Text>
              </Block>
            }
          />
          <CtLoading
            isVisible={loading}
            title={t("common:loading")}
            onBackdropPress={() => setLoading(false)}
            onRequestClose={() => setLoading(false)}
          />
          <JobStartConfirmation
            fontScale={fontScale}
            show={showModal}
            onClosePressed={() => setShowModal(false)}
            onConfirmPressed={() => handleStartJob(selectedJob?.jobId)}
            selectedJob={selectedJob}
            modalErrMsg={modalErrMsg}
            locale={t}
          />
        </CtLinearBackground>
      </Block>
    </>
  );
};

export default NewJobs;
