import { Block, Button, Icon, Text, theme } from "galio-framework";
import {
  StyleSheet,
  TouchableOpacity,
  View,
  useWindowDimensions,
} from "react-native";
import MKButton from "../components/Button";
import {
  displayDate,
  getFontByFontScale,
  moderateScale,
} from "../constants/utils";
import { ckComponentStyles } from "../styles/componentStyles";
import { styles } from "../styles/customStyles";
import { materialTheme } from "../constants";
import { useTranslation } from "react-i18next";
import React, { useEffect, useState } from "react";
import MultiDropOffLocationsPopup from "./MultiDropOffLocationsPopup";
import DisplayGoodsInfo from "./DisplayGoodsInfo";
import SimpleLineIcons from "react-native-vector-icons/SimpleLineIcons";
import ShowRemark from "../ctcomponents/ShowRemark";

/** Job Item card used for Assigned and Paused */
const JobItem = (props) => {
  const jobDriverRef = props?.driverRefNo;
  const jobType = props?.shipmentType;
  const isPaused = props?.jobState === "PAUSED";

  const { t, i18n } = useTranslation();

  const { fontScale } = useWindowDimensions();
  let fontSize = getFontByFontScale(fontScale, 18, 14);

  const localStyle = makeLocalStyle(fontScale);

  //for the job details
  const [jobDetails, setJobDetails] = useState({
    jobDriverRef: props?.driverRefNo,
    jobType: props?.shipmentType,
    isPaused: props?.jobState === "PAUSED",
    noOfTrips: props?.trips?.length ?? 0,
  });

  //container for the pickup details
  const [pickUpDetails, setPickUpDetails] = useState({});

  //container for the trips dropoff
  const [dropOffLocations, setDropOffLocations] = useState();
  //container for single trip dropoff
  const [dropOffDetails, setDropOffDetails] = useState({});

  //container for additional attributes
  //   const [addtlFields, setAddtlFields] = useState([...props?.addAttrDto]);

  const [openMultiDropOffDetails, setOpenMultiDropOffDetails] = useState(false);

  //container

  useEffect(() => {
    const trips = [...props?.trips];

    // console.log("trips in job item: ", trips);

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

    //TODO for the additional trip
  }, [props]);

  return (
    <>
      <Block card flex style={localStyle.cardContainer}>
        <Block style={localStyle.cardHeader}>
          <View
            style={{ backgroundColor: "#EFF2F1", borderRadius: 5, padding: 4 }}
          >
            <Text style={localStyle.highlightedLabel}>
              {jobDetails?.jobType}
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
            />
          <Text style={localStyle.h4Style}>{jobDetails?.jobDriverRef}</Text>
        </Block>

        <Block style={localStyle.cardBody}>
          <Block style={{ flex: 0.15, alignItems: "center", height: "80%" }}>
            <Block style={localStyle.locationPin}>
              <Icon
                name="location"
                family="ionicon"
                size={moderateScale(30)}
                color={"#FE3727"}
              />
            </Block>
          </Block>
          {/** Start Pick up Location */}
          <Block style={{ flex: 0.85, height: "80%" }}>
            <Text style={localStyle.h5Style}>{pickUpDetails?.fromLocName}</Text>
            <Text style={localStyle.subtitle}>
              {pickUpDetails?.fromLocAddr}
            </Text>
            <Block style={styles.cardTimeContainer}>
              <Icon
                name="clock-o"
                family="font-awesome"
                size={moderateScale(20)}
                color={"#ccc"}
                style={{ paddingRight: 4 }}
              />
              <Text
                style={[
                  localStyle.subtitle,
                  { fontSize: getFontByFontScale(fontScale, 24, 11) },
                ]}
              >
                {t("job:estPickup")}:{" "}
                {displayDate(pickUpDetails?.estPickupTime, i18n.language)}
              </Text>
            </Block>
          </Block>
          {/** End Pick up Location */}
        </Block>
        {pickUpDetails?.fromLocRemarks && pickUpDetails?.fromLocRemarks !== '' &&(
            <Block style={{marginTop: -10,borderRadius: 8, borderColor: "#f8f6f6", backgroundColor: "rgba(239,242,241, 0.5)", width: "100%"}}>
              <Text style={{paddingLeft:4,fontSize: getFontByFontScale(fontScale, 24, 12), fontWeight: "bold"}}>
                <SimpleLineIcons name="speech" size={12} color={materialTheme.COLORS.CKPRIMARY} />
                &nbsp; Remark:
              </Text>
              <ShowRemark text={pickUpDetails?.fromLocRemarks} />
            </Block>
        )}
        {/** Start Drop Off Location */}
        <Block style={localStyle.cardBody}>
          <Block style={{ flex: 0.15, alignItems: "center" }}>
            <Block style={localStyle.locationPin}>
              <Icon
                name="location"
                family="ionicon"
                size={moderateScale(30)}
                color={"#0BA247"}
              />
            </Block>
          </Block>
          <Block style={{ flex: 0.85, height: "80%" }}>
            {dropOffLocations?.length > 1 ? (
              <Block flex row space="between">
                <Block
                  style={{
                    borderRadius: 5,
                    padding: 4,
                  }}
                >
                  <Text style={localStyle.h5Style}>
                    {dropOffLocations.length} - {t("other:jobTab.dropOffLocations")}
                  </Text>
                </Block>
                <Block style={{ paddingRight: 5 }}>
                  <TouchableOpacity
                    onPress={() => {
                      setOpenMultiDropOffDetails(!openMultiDropOffDetails);
                    }}
                  >
                    <Text
                      style={{
                        fontSize: getFontByFontScale(fontScale, 16, 14),
                      }}
                      color={materialTheme.COLORS.CKPRIMARY}
                    >
                        {t("other:screen.show")}
                    </Text>
                  </TouchableOpacity>
                </Block>
              </Block>
            ) : (
              <>
                <Text style={localStyle.h5Style}>
                  {dropOffDetails?.toLocName}
                </Text>
                <Text style={localStyle.subtitle}>
                  {dropOffDetails?.toLocAddr}
                </Text>
                <Block style={styles.cardTimeContainer}>
                  <Icon
                    name="clock-o"
                    family="font-awesome"
                    size={moderateScale(20)}
                    color={"#ccc"}
                    style={{ paddingRight: 4 }}
                  />
                  <Text
                    style={[
                      localStyle.subtitle,
                      { fontSize: getFontByFontScale(fontScale, 24, 11) },
                    ]}
                  >
                    {t("job:estDropOff")}:{" "}
                    {displayDate(dropOffDetails?.estDropOffTime, i18n.language)}{" "}
                  </Text>
                </Block>
              </>
            )}
          </Block>
        </Block>
        {/* End Drop Off Location */}

        {/* Additional Details */}
        {/* Goods Information */}
        <Block flex column middle style={{ paddingBottom: 10, width: "100%" }}>
          <Block
            column
            style={{
              //   flex: 0.75,
              // paddingLeft: 2,
              paddingBottom: 10,
              backgroundColor: "#ffffff",
              borderRadius: 2,
              // borderWidth: 0.5,
              // borderColor: "#fff7f7",
              width: "100%",
            }}
          >
            {
              // https://jira.vcargocloud.com/browse/CT2SG-138
              // [Driver App] Goods Information header missing after 1st section
            }
            {false && <Text
              style={[
                {
                  paddingTop: 5,
                  paddingBottom: 5,
                  fontWeight: "bold",
                  fontSize: getFontByFontScale(fontScale, 24, 12),
                },
              ]}
            >
                {t("other:cargo.goodsInfo")}:
            </Text> }

            {dropOffLocations?.length > 1
              ? dropOffLocations?.map((dropOff, idx) => {
                  return dropOff?.toLocRemarks && dropOff?.toLocRemarks !== '' && (
                    <Block style={{margin:4,paddingLeft:2,marginTop: 2, borderRadius: 8, borderWidth: 2, borderColor: "#f8f6f6", backgroundColor: "#fcfcfc"}}>
                        {dropOff?.toLocRemarks && dropOff?.toLocRemarks !== '' && (
                            <>
                                <Text style={{paddingTop:4,paddingLeft:4,fontSize: getFontByFontScale(fontScale, 24, 12), fontWeight: "bold"}}>
                                    <SimpleLineIcons name="speech" size={12} color={materialTheme.COLORS.CKPRIMARY} />
                                    &nbsp; Remark:
                                </Text>
                                <ShowRemark text={dropOff?.toLocRemarks} />
                            </>
                        )}
                      {dropOff?.cargos?.map((doCargo, doCargoIdx) => {
                          return (
                              <Block style={{padding:2,margin: 2}}>
                                  <DisplayGoodsInfo
                                      key={doCargoIdx}
                                      item={doCargo}
                                      idx={doCargoIdx}
                                      fontScale={fontScale}
                                      jobData={props}
                                      location={dropOff}
                                  />
                              </Block>
                          );
                      })}
                    </Block>
                  )
                })
              : ( <Block style={{margin:4,paddingLeft:2,marginTop: 2, borderRadius: 8, borderWidth: 2, borderColor: "#f8f6f6", backgroundColor: "#fcfcfc"}}>
                    {dropOffDetails?.toLocRemarks && dropOffDetails?.toLocRemarks !== '' && (
                        <>
                            <Text style={{paddingTop:4,paddingLeft:4,fontSize: getFontByFontScale(fontScale, 24, 12), fontWeight: "bold"}}>
                                <SimpleLineIcons name="speech" size={12} color={materialTheme.COLORS.CKPRIMARY} />
                                &nbsp; Remark:
                            </Text>
                            <ShowRemark text={dropOffDetails?.toLocRemarks} />
                        </>
                    )}
                  {dropOffDetails?.cargos && dropOffDetails?.cargos?.map((item, idx) => {
                    return (
                        <DisplayGoodsInfo
                            key={idx}
                            item={item}
                            idx={idx}
                            jobData={props}
                            fontScale={fontScale}
                            location={dropOffDetails}
                        />
                    );
                  })}
                </Block>
                )}
          </Block>
        </Block>

        <Block style={localStyle.buttonContainer}>
          <MKButton
            gradient
            size="large"
            shadowless
            // color={materialTheme.COLORS.BUTTON_COLOR}
            style={ckComponentStyles.ckbuttons}
            onPress={props.selectId}
            fontSize={getFontByFontScale(fontScale, 25, null)}
          >
            {isPaused ? t("button:resume") : t("button:start")}
          </MKButton>
        </Block>
      </Block>

      {/* For the popup of the location details for multi-dropoff */}
      {openMultiDropOffDetails && (
        <MultiDropOffLocationsPopup
          show={openMultiDropOffDetails}
          onClosePressed={() => setOpenMultiDropOffDetails(false)}
          locations={dropOffLocations}
        />
      )}
    </>
  );
};



const makeLocalStyle = (fontScale) =>
  StyleSheet.create({
    shadow: {
      backgroundColor: theme.COLORS.WHITE,
      shadowColor: theme.COLORS.BLACK,
      shadowOffset: { width: 0, height: 2 },
      shadowRadius: 3,
      shadowOpacity: 0.1,
      elevation: 2,
    },
    highlightedLabel: {
      color: "#1976d2",
      fontSize: getFontByFontScale(fontScale, 25, 14),
      fontWeight: "bold",

      // backgroundColor: "#e7e7e7"
    },
    cardContainer: {
      borderRadius: 20,
      padding: 10,
      marginVertical: 8,
      shadowColor: theme.COLORS.BLACK,
      shadowOffset: { width: 0, height: 2 },
      shadowRadius: 3,
      shadowOpacity: 0.1,
      elevation: 2,
      backgroundColor: "#fff",
      alignItems: "center",
      flexDirection: "column",
      borderWidth: 0.5,
      // width: "100%"
    },
    cardBody: {
      display: "flex",
      flexDirection: "row",
      marginVertical: 10,
      backgroundColor: "rgba(239,242,241, 0.5)",
      borderColor: "#d0d0d8",
      padding: 5,
      borderRadius: 10,
    },
    cardHeader: {
      display: "flex",
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "flex-start",
      marginBottom: "-1%",
      borderBottomWidth: 0.5,
      borderColor: "#ccc",
      // paddingBottom: 10,
      width: "100%",
      // borderWidth: 1,
      // borderColor: "green"
    },
    cardTimeContainer: {
      display: "flex",
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "flex-start",
      marginTop: 10,
    },
    cardListTiming: {
      width: "100%",
      flexDirection: "row",
      justifyContent: "space-between",
      flexWrap: "wrap",
      // borderWidth: 1,
      marginBottom: 10,
    },
    cardTimingItem: {
      width: "50%",
      alignItems: "center",
      flexDirection: "row",
      marginBottom: 10,
    },

    h4Style: {
      fontSize: getFontByFontScale(fontScale, 25, 14),
      paddingLeft: 2,
      fontWeight: "500",
    },
    h5Style: {
      fontSize: getFontByFontScale(fontScale, 30, 14),
      fontWeight: "600",
    },
    subtitle: {
      color: materialTheme.COLORS.BLACK,
      fontWeight: "200",
      fontSize: getFontByFontScale(fontScale, 30, 14),
    },
    muted: {
      color: "#999",
    },
    locationPin: {
      alignItems: "center",
      justifyContent: "center",
      borderWidth: 0,
      borderRadius: 12,
      //   backgroundColor: "#EFF2F1",
      width: moderateScale(50),
      height: moderateScale(60),
    },
    buttonContainer: {
      alignItems: "center",
      borderTopWidth: 0.5,
      width: "100%",
      borderColor: "#ccc",
      paddingTop: 10,
      paddingBottom: 10,
    },
  });

export default JobItem;
