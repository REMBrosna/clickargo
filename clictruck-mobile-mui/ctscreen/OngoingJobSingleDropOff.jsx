import { Block, Icon, Text } from "galio-framework";
import {
  displayDate,
  getFontByFontScale,
  moderateScale,
} from "../constants/utils";
import { Ionicons, FontAwesome5 } from "@expo/vector-icons";
import OpenExternalUrl from "../ctcomponents/OpenExternalUrl";
import { Image, Pressable } from "react-native";
import { materialTheme } from "../constants";
import DisplayGoodsInfo from "./DisplayGoodsInfo";
import React from "react";
import SimpleLineIcons from "react-native-vector-icons/SimpleLineIcons";
import ShowRemark from "../ctcomponents/ShowRemark";

export default function OngoingJobSingleDropOff({
  styles,
  t,
  locale,
  fontScale,
  dropOffDetails,
  handlePressRemarks,
  jobData
}) {
  return (
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
        <Block>
          <Text style={[styles.h5Style, { fontWeight: 500 }]}>
            {t("job:ongoing.dropOffLoc")}
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
            {displayDate(dropOffDetails?.estDropOffTime, locale, false)}
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
            {displayDate(dropOffDetails?.estDropOffTime, locale, false, true)}
          </Text>
        </Block>
      </Block>
      <Block style={[styles.cardBody, { paddingTop: 10, width: "100%" }]}>
        <Block
          style={{
            flex: 0.2,
            alignItems: "center",
            height: "80%",
          }}
        >
          <Block style={styles.locationPin}>
            <Icon
              name="location"
              family="ionicon"
              size={moderateScale(40)}
              color={"rgba(11,162,71, 0.6)"}
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
              {dropOffDetails?.toLocName}
            </Text>
            <Text style={styles.subtitle}>{dropOffDetails?.toLocAddr}</Text>
          </Block>
          <Block row space="evenly" center>
            <OpenExternalUrl
              url={
                "https://www.google.com/maps/search/" +
                dropOffDetails?.toLocAddr
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
        {dropOffDetails?.toLocRemarks && dropOffDetails?.toLocRemarks !== '' && (
            <Block style={{paddingLeft:10}}>
                <Text style={{fontSize: getFontByFontScale(fontScale, 24, 12), fontWeight: "bold"}}>
                    <SimpleLineIcons name="speech" size={12} color={materialTheme.COLORS.CKPRIMARY} />
                    &nbsp; Remark:
                </Text>
                <ShowRemark text={dropOffDetails?.toLocRemarks} />
            </Block>
        )}
      {/* Goods Information */}
      <Block flex column middle style={{ paddingBottom: 10 }}>
        <Block
          column
          style={{
            flex: 0.75,
            paddingLeft: 10,
            paddingBottom: 5,
            backgroundColor: "#e7f4fd",
            borderRadius: 5,
            borderWidth: 0.5,
            borderColor: "#9bc9ff",
            width: "95%",
          }}
        >
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

          {dropOffDetails?.cargos &&
            dropOffDetails?.cargos?.map((item, idx) => {
              return (
                <DisplayGoodsInfo
                  key={idx}
                  item={item}
                  idx={idx}
                  jobData={jobData}
                  fontScale={fontScale}
                  location={dropOffDetails}
                />
              );
            })}
        </Block>
      </Block>
    </Block>
  );
}

function DisplayGoodsInfo1111({ item, idx, fontScale }) {
  return (
    <Block
      column
      key={idx}
      fluid
      space="evenly"
      style={{
        paddingBottom: 5,
      }}
    >
      {item?.goodsType || item?.goodsDesc ? (
        <Text
          style={{
            paddingLeft: 2,
            fontSize: getFontByFontScale(fontScale, 24, 12),
          }}
        >
          {idx + 1}. {item?.goodsType} | {item?.goodsDesc}
        </Text>
      ) : null}
      {item?.specialInstructions && (
        <>
          <Text
            style={{
              paddingTop: 10,
              fontWeight: "bold",
              fontSize: getFontByFontScale(fontScale, 24, 12),
              paddingLeft: 2,
            }}
          >
              {t("other:header.specialInstructions")}:
          </Text>
          <Text
            style={{
              paddingLeft: 10,
              fontSize: getFontByFontScale(fontScale, 24, 12),
            }}
          >
            {item?.specialInstructions}
          </Text>
        </>
      )}
    </Block>
  );
}
