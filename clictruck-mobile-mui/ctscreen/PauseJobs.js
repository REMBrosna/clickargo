import React, { useCallback, useContext, useEffect, useState } from "react";
import { FlatList, useWindowDimensions } from "react-native";
import {
  pausedJobList,
  startJobUrl,
  getResumeConfirmUrl,
} from "../constants/urls";
import useStats from "../hooks/useStats";
import { listingStyles } from "../styles/listingStyles";
import CtLoading from "./CtLoading";
import JobStartConfirmation from "./JobStartConfirmation";
import { Block, Icon, Text } from "galio-framework";
import { sendRequest } from "../utils/httpUtil";
import JobItem from "./JobItem";
import {
  getFontByFontScale,
  horizontalScale,
  storeJob,
  verticalScale,
} from "../constants/utils";
import JobTabContext from "../context/JobTabContext";
import CtLinearBackground from "../ctcomponents/CtLinearBackground";
import { useTranslation } from "react-i18next";
import ListingHeaders from "./headers/ListingHeaders";
import JobItemSummary from "./JobItemSummary";
import { useListEvents } from "../context/ListEventsContext";

const PausedJobs = (props) => {
  // const navigation = useNavigation();
  const { navigation } = props;
  const { reloadStats, isStatsReloaded, dispatch } = useStats();
  const { jobTabId, setJobTabId } = useContext(JobTabContext);
  const { t } = useTranslation();
  const { fontScale } = useWindowDimensions();

  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [selectedJob, setSelectedJob] = useState({
    jobId: null,
    jobRefNo: null,
  });
  const [activeJob, setActiveJob] = useState();
  const [inputdata, setInputData] = useState();
  const [isRefreshing, setIsRefreshing] = useState(false);

  const { showSpecialInst, showDetailed } = useListEvents();

  async function getData() {
    const result = await sendRequest(pausedJobList);
    if (result) {
      setInputData(result.aaData);
      setLoading(false);

      dispatch({
        type: "RELOAD",
        payload: {
          pauseStats: {
              "accnType": "DRIVER",
              "count": result.aaData.length,
              "dbType": "MOBILE_PAUSED",
              "id": 1,
              "image": null,
              "title": "Paused"
          },
        },
      });
    }
  }

  useEffect(() => {
    setJobTabId("paused");
    getData();
  }, []);

  useEffect(() => {
    if (!loading) setLoading(true);
    if (jobTabId === "paused" && showSpecialInst) {
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
      setJobTabId("paused");
    });

    return tabPressed;
  }, [navigation]);

  function onRefresh() {
    setIsRefreshing(true);
    try {
      //reloadStats();
      getData();
    } catch {
      console.error("onRefresh error");
    } finally {
      setIsRefreshing(false);
    }
  }

  const handleSelectJob = useCallback(async (id, jobRefNo) => {
    if (id) {
      const confirmation = await sendRequest(getResumeConfirmUrl);
      if (confirmation) {
        setShowModal(true);
        setSelectedJob({ ...selectedJob, jobId: id, jobRefNo: jobRefNo });
        setActiveJob(confirmation.data);
      }
    } else {
      console.error("invalid job id");
    }
  }, []);

  async function handleStartJob(id) {
    setLoading(true);
    const job = await sendRequest(startJobUrl + id + "?action=PAUSE", "put");
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
              return <Block style={{ marginVertical: 5 }} />;
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
                  {t("job:noNewRecords")}
                  {"\n"}
                  {t("job:pullToRefresh")}
                </Text>
              </Block>
            }
          />
          <CtLoading
            isVisible={loading}
            title="Please wait"
            onBackdropPress={() => setLoading(false)}
            onRequestClose={() => setLoading(false)}
          />
          <JobStartConfirmation
            show={showModal}
            fontScale={fontScale}
            onClosePressed={() => setShowModal(false)}
            onConfirmPressed={() => handleStartJob(selectedJob?.jobId)}
            selectedJob={selectedJob}
            activeJob={activeJob}
            isResume={true}
            locale={t}
          />
        </CtLinearBackground>
      </Block>
    </>
  );
};

export default PausedJobs;
