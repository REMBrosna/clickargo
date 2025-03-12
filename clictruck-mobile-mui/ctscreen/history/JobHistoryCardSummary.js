import React, { useEffect, useState } from "react";
import {
  StyleSheet,
  Platform,
  Pressable,
  Text,
  TouchableOpacity,
  Linking,
  Alert,
} from "react-native";
import { Block, Button, Icon, theme } from "galio-framework";
import { materialTheme } from "../../constants";
import { displayDate, getFontByFontScale } from "../../constants/utils";
import {
  horizontalScale,
  moderateScale,
  verticalScale,
} from "../../constants/metrics";
import { FontAwesome } from "@expo/vector-icons";
import Popover, { PopoverPlacement } from "react-native-popover-view";
import { useTranslation } from "react-i18next";

/*** Summarized job history card */
const JobHistoryCardSummary = ({
  item,
  index,
  handlePhotoDownload,
  handleEpodDownload,
  fontScale,
}) => {
  const { t, i18n } = useTranslation();

  const [jobDetails, setJobDetails] = useState({
    jobId: item?.jobId,
    jobDriverRef: item?.driverRefNo,
    jobType: item?.shipmentType,
    jobSource: item?.jobSource,
    noOfTrips: item?.trips?.length ?? 0,
    isXml: item?.jobSource?.includes("XML"),
  });

  const localStyle = makeStyle(fontScale);

  //container for the pickup details
  const [pickUpDetails, setPickUpDetails] = useState({});

  //container for the trips dropoff
  const [dropOffLocations, setDropOffLocations] = useState();
  //container for single trip dropoff
  const [dropOffDetails, setDropOffDetails] = useState({});

  const [openMultiDropOffDetails, setOpenMultiDropOffDetails] = useState(false);

  useEffect(() => {
    const trips = [...item?.trips];
    //seq no=0, the fromLocation is the pickup location.
    // the rest of the sequences, the toLocation will be the drop off, fromlocation in this case is just the assumption
    // that driver will go through the seq. of drop offs.
    let pickUpLocationDetails = trips?.filter((item, idx) => idx === 0);
    setPickUpDetails((prev) => ({ ...prev, ...pickUpLocationDetails[0] }));

    if (trips?.length > 1) {
      let dropOffLocs = trips;
      setDropOffLocations((prev) => [...dropOffLocs]);
    } else {
      //set similar to pickup locations since it has the to/destination locations anyway
      setDropOffDetails((prev) => ({ ...prev, ...pickUpLocationDetails[0] }));
    }
  }, [item]);

  return (
    <Block card flex style={localStyle.cardContainer}>
      <Block style={[localStyle.blockContainer]}>
        {/* Popover for download */}
        <Block style={localStyle.cardHeader}>
          <Block left style={{ width: "95%" }}>
            <Text
              style={{
                color: materialTheme.COLORS.CKPRIMARY,
                fontWeight: "bold",
                fontSize: getFontByFontScale(fontScale, 24, 20),
              }}
            >
              {index + 1}
            </Text>
          </Block>
          <Popover
            backgroundStyle={{ backgroundColor: "transparent" }}
            placement={PopoverPlacement.BOTTOM}
            popoverStyle={localStyle.popoverContainer}
            from={
              <Pressable>
                <Text style={{ color: "#999" }}>
                  <FontAwesome
                    name={"download"}
                    style={{ fontSize: getFontByFontScale(fontScale, 22, 22) }}
                    color={materialTheme.COLORS.CKPRIMARY}
                  />
                </Text>
              </Pressable>
            }
          >
            <Pressable
              style={[localStyle.popoverItem, { marginBottom: 10 }]}
              onPress={() => {
                jobDetails?.isXml
                  ? handleEpodDownload(jobDetails?.jobId, "job")
                  : Alert.alert(
                      "Unavailable",
                      `Epod Only available for xml job`
                    );
              }}
            >
              <Block flex style={{ flex: 0.2, flexDirection: "row" }}>
                <FontAwesome name="download" size={20} color={"#999"} />
              </Block>
              <Block
                flex
                style={{
                  flex: 0.7,
                  flexDirection: "row",
                  justifyContent: "left",
                }}
              >
                <Text
                  style={{
                    color: "#999",
                    fontWeight: "400",
                    fontSize: getFontByFontScale(fontScale, 25, null),
                  }}
                >
                  {t("jobHistory:popup.download.epod")}
                </Text>
              </Block>
            </Pressable>
            <Pressable
              style={[localStyle.popoverItem, { marginBottom: 10 }]}
              onPress={() =>
                handlePhotoDownload(jobDetails?.jobId, "job", "pickup")
              }
            >
              <Block flex style={{ flex: 0.2, flexDirection: "row" }}>
                <FontAwesome name="download" size={20} color={"#999"} />
              </Block>
              <Block
                flex
                style={{
                  flex: 0.7,
                  flexDirection: "row",
                  justifyContent: "left",
                }}
              >
                <Text
                  style={{
                    color: "#999",
                    fontWeight: "400",
                    fontSize: getFontByFontScale(fontScale, 25, null),
                  }}
                >
                  {t("jobHistory:popup.download.pickupPhoto")}
                </Text>
              </Block>
            </Pressable>
            <Pressable
              style={localStyle.popoverItem}
              onPress={() =>
                handlePhotoDownload(jobDetails?.jobId, "job", "pickup")
              }
            >
              <Block flex style={{ flex: 0.2, flexDirection: "row" }}>
                <FontAwesome name="download" size={20} color={"#999"} />
              </Block>
              <Block
                flex
                style={{
                  flex: 0.7,
                  flexDirection: "row",
                  justifyContent: "left",
                }}
              >
                <Text
                  style={{
                    color: "#999",
                    fontWeight: "400",
                    fontSize: getFontByFontScale(fontScale, 25, null),
                  }}
                >
                  {t("jobHistory:popup.download.dropPhoto")}
                </Text>
              </Block>
            </Pressable>
          </Popover>
        </Block>
        {/* Popover for download */}
      </Block>

      {/* Drop Off Locations */}
      <Block
        flex
        style={{
          borderWidth: 1,
          borderColor: "#96CEB4",
          borderRadius: 10,
          width: "100%",
        }}
      >
        <Block
          flex
          style={[localStyle.location, { backgroundColor: "#96CEB4" }]}
        >
          <Block>
            {
              dropOffLocations?.length > 1 ? (
                <Block
                  style={{
                    borderRadius: 5,
                    padding: 4,
                  }}
                >
                  <Text
                    style={
                      (localStyle.highlightedLabel, localStyle.locationLabel)
                    }
                  >
                    {dropOffLocations.length} - {t("other:jobTab.dropOffLocations")}
                  </Text>
                </Block>
              ) : null
              // (
              //   <Text
              //     style={[localStyle.highlightedLabel, localStyle.locationLabel]}
              //   >
              //     {t("jobHistory:label.dropLoaction")}
              //   </Text>
              // )
            }
          </Block>
          <Block style={{ flex: 0.85, height: "80%" }}>
            {dropOffLocations?.length > 1 ? (
              <Block flex row space="between">
                <Block
                  style={{
                    borderRadius: 5,
                    padding: 4,
                  }}
                ></Block>
                <Block style={{ padding: 4 }}>
                  <TouchableOpacity
                    onPress={() => {
                      setOpenMultiDropOffDetails(!openMultiDropOffDetails);
                    }}
                  >
                    <Text
                      style={{
                        fontSize: getFontByFontScale(fontScale, 16, 14),
                        color: materialTheme.COLORS.CKPRIMARY,
                      }}
                    >
                      {openMultiDropOffDetails ? t("other:screen.show"): t("other:screen.hide")}
                    </Text>
                  </TouchableOpacity>
                </Block>
              </Block>
            ) : (
              <Block column right flex>
                <Text style={localStyle.locationTime}>
                  <Text style={{ fontWeight: "bold" }}>Delivered: </Text>
                  {displayDate(dropOffDetails?.jobFinishTime, i18n, false)}
                </Text>
                <Text style={localStyle.locationTime}>
                  <Icon
                    name="clock-o"
                    family="font-awesome"
                    size={moderateScale(20)}
                    style={[localStyle.buttonIconLabel]}
                  />{" "}
                  {displayDate(
                    dropOffDetails?.jobFinishTime,
                    i18n,
                    false,
                    true
                  )}
                </Text>
              </Block>
            )}
          </Block>
        </Block>
        {/* Drop Off Details Body */}
        {dropOffLocations?.length > 1 ? (
          openMultiDropOffDetails ? null : (
            dropOffLocations?.map((item, idx) => (
              <MultiDropOffContainer
                locale={i18n}
                details={item}
                key={idx}
                id={idx}
                localStyle={localStyle}
                handleEpodDownload={handleEpodDownload}
                isXml={jobDetails?.isXml}
              />
            ))
          )
        ) : (
          <Block
            style={[localStyle.cardBody, { paddingTop: 10, width: "100%" }]}
          >
            <Block
              style={{
                flex: 0.2,
                alignItems: "center",
                height: "80%",
              }}
            >
              <Block style={localStyle.locationPin}>
                <Icon
                  name="location"
                  family="ionicon"
                  size={moderateScale(40)}
                  color={"#96CEB4"}
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
                <Text style={localStyle.h5Style}>
                  {dropOffDetails?.toLocName}
                </Text>
                <Text style={localStyle.subtitle}>
                  {dropOffDetails?.toLocAddr}
                </Text>
              </Block>
              <Block row space="evenly" center style={{ width: "20%" }}>
                <Block>
                  <Pressable
                    onPress={() =>
                      handlePhotoDownload(dropOffDetails?.id, "trip", "dropoff")
                    }
                  >
                    <FontAwesome
                      name="photo"
                      size={20}
                      color={materialTheme.COLORS.CKPRIMARY}
                    />
                  </Pressable>
                </Block>
              </Block>
            </Block>
          </Block>
        )}
      </Block>

      <Block style={{ marginTop: verticalScale(10) }}></Block>
    </Block>
  );
};

export default JobHistoryCardSummary;

function MultiDropOffContainer({
  details,
  id,
  localStyle,
  locale,
  handleEpodDownload,
  handlePhotoDownload,
  isXml,
}) {
  return (
    <Block
      key={id}
      style={[localStyle.cardBody, { paddingTop: 10, width: "100%" }]}
    >
      <Block
        style={{
          flex: 0.2,
          alignItems: "center",
          height: "80%",
        }}
      >
        <Block style={localStyle.locationPin}>
          <Icon
            name="location"
            family="ionicon"
            size={moderateScale(40)}
            color={"#96CEB4"}
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
          <Text style={localStyle.h5Style}>{details?.toLocName}</Text>
          <Text style={localStyle.subtitle}>{details?.toLocAddr}</Text>
          <Text
            style={[
              localStyle.subtitle,
              { fontWeight: 300, color: materialTheme.COLORS.CKPRIMARY },
            ]}
          >
            {displayDate(details?.jobFinishTime, locale, true)}
          </Text>
        </Block>
        <Block row space="evenly" center style={{ width: "20%" }}>
          <Block>
            <Pressable
              onPress={() =>
                handlePhotoDownload(details?.id, "trip", "dropoff")
              }
            >
              <FontAwesome
                name="photo"
                size={20}
                color={materialTheme.COLORS.CKPRIMARY}
              />
            </Pressable>
          </Block>

          {isXml && (
            <Block>
              <Pressable onPress={() => handleEpodDownload(details?.id)}>
                <FontAwesome
                  name="file-pdf-o"
                  size={20}
                  color={materialTheme.COLORS.CKPRIMARY}
                />
              </Pressable>
            </Block>
          )}
        </Block>
      </Block>
    </Block>
  );
}

const makeStyle = (fontScale) =>
  StyleSheet.create({
    cardButtonContainer: {
      flexDirection: "row",
      justifyContent: "space-between",
      alignItems: "center",
      borderColor: "#EFF2F1",
      padding: 5,
    },
    cardContainer: {
      borderRadius: 20,
      padding: 12,
      marginVertical: 8,
      elevation: 3,
      shadowColor: "#999999",
      shadowOffset: { width: 2, height: 2 },
      shadowRadius: 3,
      shadowOpacity: 1,
      backgroundColor: theme.COLORS.WHITE,
      alignItems: "center",
      flexDirection: "column",
      borderWidth: 1,
    },
    cardHeader: {
      display: "flex",
      flexDirection: "row",
      alignItems: "flex-start",
      justifyContent: "flex-start",
      borderBottomWidth: 0.5,
      borderColor: "#ccc",
      width: "100%",
      paddingRight: 10,
      paddingLeft: 10,
    },
    cardBody: {
      display: "flex",
      flexDirection: "row",
      paddingBottom: 10,
    },
    highlightedLabel: {
      color: "#1976d2",
      fontSize: getFontByFontScale(fontScale, 25, 14),
      fontWeight: "bold",
    },
    location: {
      justifyContent: "space-between",
      flexDirection: "row",
      flex: 1,
      width: "100%",
      paddingBottom: 10,
      borderRadius: 5,
      padding: 6,
    },
    locationLabel: {
      fontWeight: "600",
      color: "white",
      fontSize: getFontByFontScale(fontScale, 25, 16),
    },
    locationTime: {
      color: "white",
      fontWeight: "400",
      fontSize: getFontByFontScale(fontScale, 25, null),
    },
    locationPin: {
      alignItems: "center",
      justifyContent: "center",
      borderRadius: 12,
      backgroundColor: "#EFF2F1",
      width: moderateScale(50),
      height: moderateScale(60),
    },
    h4Style: {
      fontSize: getFontByFontScale(fontScale, 25, 14),
      paddingLeft: 2,
      fontWeight: "500",
    },
    h5Style: {
      fontSize: 16,
      fontWeight: "600",
    },
    subtitle: {
      color: materialTheme.COLORS.BLACK,
      fontWeight: "200",
      fontSize: 13,
    },
    buttonItem: {
      width: 100, //change to 30%
      //   height: 50,
      minWidth: horizontalScale(50),
      minHeight: verticalScale(30),
      alignItems: "center",
      justifyContent: "center",
      borderColor: "rgba(21,176,236, 0.6)",
      borderWidth: 1,
      borderRadius: 8,
      margin: 5,
    },
    buttonIconLabel: {
      // backgroundColor: "rgba(255, 255, 255, 0.9)",
      padding: 3,
      // borderRadius: 8,
      //   fontSize: getFontByFontScale(fontScale, 25, 15),
      borderColor: "#EFF2F1",
    },
    blockContainer: {
      width: Platform.OS === "ios" ? "100%" : "97%",
      borderRadius: 10,
      backgroundColor: "transparent",
      overflow: "hidden",
      alignItems: "center",
      marginBottom: 15,
    },
    timingContainer: {
      width: "100%",
      backgroundColor: "rgba(21,176,236, 0.6)",
      paddingTop: 10,
      paddingBottom: 10,
      flexDirection: "column",
      justifyContent: "flex-start",
      alignItems: "flex-start",
      borderWidth: 1,
      borderRadius: 10,
      borderColor: "rgba(21,176,236, 0.6)",
    },
    timingItem: {
      // width: '50%',
      // paddingLeft: 10,
      // marginBottom: 10,
      backgroundColor: "rgba(52, 52, 52, alpha)",
      flexDirection: "column",
      alignItems: "flex-start",
      justifyContent: "flex-start",
      // borderWidth: 1
    },
    timingItemText: {
      color: materialTheme.COLORS.BLACK,
      fontSize: getFontByFontScale(fontScale, 25, 14),
      fontWeight: "400",
    },
    // popover
    popoverContainer: {
      width: horizontalScale(180),
      paddingHorizontal: 5,
      paddingVertical: 10,
      borderRadius: moderateScale(15),
      //new style
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
      marginTop: -10,
      marginLeft: -10,
      borderWidth: 1.5,
      borderColor: "#999",
    },
    popoverItem: {
      flexDirection: "row",
      alignItems: "left",
      //   borderBottomWidth: 0.5,
      borderColor: "#999",
      width: "100%",
      // gap: 10,
    },
  });
