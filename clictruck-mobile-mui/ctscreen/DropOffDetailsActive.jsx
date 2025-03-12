import { Block, Icon, Text } from "galio-framework";
import {
  displayDate,
  getFontByFontScale,
  moderateScale,
} from "../constants/utils";
import { FontAwesome5, Ionicons } from "@expo/vector-icons";
import OpenExternalUrl from "../ctcomponents/OpenExternalUrl";
import {Image, Pressable, View} from "react-native";
import { materialTheme } from "../constants";
import DisplayGoodsInfo from "./DisplayGoodsInfo";
import React from "react";
import ShowRemark from "../ctcomponents/ShowRemark";
import SimpleLineIcons from "react-native-vector-icons/SimpleLineIcons";

/** Component for drop off details to be used in Ongoing Job */
export default function DropOffDetailsActive({
  styles,
  item,
  fontScale,
  locale,
  idx = 0,
  handlePressRemarks,
  jobData
}) {

  return (
    <Block
      key={item?.seqNo}
      style={{
        paddingBottom: 30,
      }}
      flex
      column
    >
      {/* Date/Time/Sequence Block */}
      <Block
        style={{
          padding: 5,
          backgroundColor: "#eee",
        }}
        row
        middle
        space="between"
      >
        <Block row>
          <Text
            style={{
              fontWeight: "bold",
              fontSize: getFontByFontScale(fontScale, 24, 11),
            }}
          >
            ({idx + 1}) &nbsp;
          </Text>
          <Text
            style={{
              fontWeight: "bold",
              fontSize: getFontByFontScale(fontScale, 24, 11),
            }}
          >
            {`${displayDate(item?.estPickupTime, locale.language, false, true)} - ${displayDate(item?.estDropOffTime, locale.language, false, true)}`}
          </Text>
          {item?.status === "D" && (
            <Text
              style={{
                fontWeight: "bold",
                fontSize: getFontByFontScale(fontScale, 30, 16),
                color: materialTheme.COLORS.CKSECONDARY,
                padding: 2,
              }}
            >
              <FontAwesome5
                name="check-circle"
                size={getFontByFontScale(fontScale, 16, null)}
              />
            </Text>
          )}
        </Block>
        <Block>
          <Text
            style={{
                fontSize: getFontByFontScale(fontScale, 24, 11),
            }}
          >
            {displayDate(item?.estPickupTime, locale.language, false)}
          </Text>
        </Block>
      </Block>
      {/* Address Details */}
      <Block
        style={[
          styles.cardBody,
          {
            paddingTop: 10,
          },
        ]}
        fluid
      >
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
        {/* Right side icons */}
        <Block row space="evenly" style={{ flex: 0.75 }}>
          <Block
            style={{
              flexDirection: "column",
              width: "75%",
            }}
          >
            <Text style={{color: "#6794cb", fontSize: getFontByFontScale(fontScale, 24, 14)}}>{item?.toLocName}</Text>
            <Text style={{...styles.subtitle,color: "#7f7d7d",fontSize: getFontByFontScale(fontScale, 24, 12)}}>{item?.toLocAddr}</Text>
          </Block>

          <Block row space="evenly" center>
            <OpenExternalUrl
              url={"https://www.google.com/maps/search/" + item?.toLocAddr}
            >
              <Image
                source={require("../assets/images/button/googlemap.png")}
                style={{ width: 30, height: 30 }}
              />
            </OpenExternalUrl>
          </Block>
        </Block>
      </Block>
        {item?.toLocRemarks && item?.toLocRemarks !== '' && (
            <Block style={{paddingLeft:10}}>
                <Text style={{fontSize: getFontByFontScale(fontScale, 24, 12), fontWeight: "bold"}}>
                    <SimpleLineIcons name="speech" size={12} color={materialTheme.COLORS.CKPRIMARY} />
                    &nbsp; Remark:
                </Text>
                <ShowRemark text={item?.toLocRemarks} />
            </Block>
        )}
      {/* Goods Information */}
      <Block flex column middle>
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
          {item?.cargos &&
            item?.cargos?.map((val, idx) => {
              return (
                <DisplayGoodsInfo
                  key={idx}
                  item={val}
                  idx={idx}
                  jobData={jobData}
                  fontScale={fontScale}
                  location={item}
                />
              );
            })}
        </Block>
      </Block>
    </Block>
  );
}