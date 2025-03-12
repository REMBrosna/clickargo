import { Block, Text } from "galio-framework";
import { Image, StyleSheet } from "react-native";
import { displayDate, getFontByFontScale } from "../constants/utils";
import { materialTheme } from "../constants";

export default function ActiveJobTimingDetails({
  fontScale,
  timingDetails,
  locale,
}) {
  return (
    <Block flex style={[styles.timingContainer]}>
      <Block flex style={[styles.timingItem, { width: "100%", padding: 5 }]}>
        <Block flex row>
          {/* <MaterialCommunityIcons
          name="timer"
          color={materialTheme.COLORS.LIGHT_BRIGHT_BLUE}
          size={moderateScale(16)}
        /> */}
          <Image
            source={require("../assets/images/button/start.png")}
            style={{
              width: 20,
              height: 20,
            }}
          />
          <Text
            style={[
              styles.timingLabel,
              { fontSize: getFontByFontScale(fontScale, 24, null) },
            ]}
          >
            {displayDate(timingDetails?.jobStartTime, locale)}
          </Text>
        </Block>
        <Block flex style={{ flexDirection: "row" }}>
          <Image
            source={require("../assets/images/button/pickup_cargo.png")}
            style={{
              width: 20,
              height: 20,
            }}
          />
          <Text
            style={[
              styles.timingLabel,
              { fontSize: getFontByFontScale(fontScale, 25, null) },
            ]}
          >
            {" "}
            {displayDate(timingDetails?.pickedUpTime, locale)}
          </Text>
        </Block>
        <Block flex style={{ flexDirection: "row" }}>
          <Image
            source={require("../assets/images/button/delivery.png")}
            style={{
              width: 20,
              height: 20,
            }}
          />
          <Text
            style={[
              styles.timingLabel,
              { fontSize: getFontByFontScale(fontScale, 25, null) },
            ]}
          >
            {" "}
            {displayDate(timingDetails?.deliverStartTime, locale)}
          </Text>
        </Block>
        <Block flex style={{ flexDirection: "row" }}>
          <Image
            source={require("../assets/images/button/delivered.png")}
            style={{
              width: 20,
              height: 20,
            }}
          />
          <Text
            style={[
              styles.timingLabel,
              { fontSize: getFontByFontScale(fontScale, 25, null) },
            ]}
          >
            {" "}
            {displayDate(timingDetails?.jobFinishTime, locale)}
          </Text>
        </Block>
      </Block>
    </Block>
  );
}

const styles = StyleSheet.create({
  timingContainer: {
    width: "100%",
    backgroundColor: "rgba(239,242,241, 0.6)",
    paddingTop: 10,
    paddingBottom: 10,
    flexDirection: "column",
    justifyContent: "flex-start",
    alignItems: "flex-start",
    borderRadius: 10,
    borderColor: "rgba(239,242,241, 0.6)",
    borderWidth: 1,
  },
  timingItem: {
    // width: '50%',
    // paddingLeft: 10,
    marginBottom: 10,
    backgroundColor: "rgba(52, 52, 52, alpha)",
    flexDirection: "column",
    // alignItems: "center",
    //justifyContent: "center",
    // borderWidth: 1
  },
  timingLabel: {
    color: materialTheme.COLORS.BLACK,
  },
});
