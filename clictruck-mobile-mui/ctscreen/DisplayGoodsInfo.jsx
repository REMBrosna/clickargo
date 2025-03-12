import { Block, Icon, Text } from "galio-framework";
import {
  displayDate,
  getFontByFontScale,
  moderateScale,
} from "../constants/utils";
import { materialTheme } from "../constants";
import { useTranslation } from "react-i18next";
import React from "react";
import Feather from "react-native-vector-icons/Feather";
import Entypo from "react-native-vector-icons/Entypo";

export default function DisplayGoodsInfo({ item, idx, fontScale, location, jobData }) {

    const { t } = useTranslation();

    return (
      <Block
        key={idx}
        fluid
        space="evenly"
        style={{
          paddingBottom: 5,
        }}
      >
        {
          ((!idx)||(idx===0)) ?          
          <Text
            style={[
              {
                paddingTop: 5,
                paddingBottom: 5,
                fontWeight: "bold",
                fontSize: getFontByFontScale(fontScale, 24, 13),
              },
            ]}
          >
              {t("other:cargo.jobId")}: {jobData?.jobId}
          </Text> :<></>
        }

        {location ? (
          <Text
            style={{
              fontSize: getFontByFontScale(fontScale, 24, 13),
              fontWeight: "bold",
              color: materialTheme.COLORS.CKPRIMARY,
            }}
          >
              <Entypo name="location" size={15} color={materialTheme.COLORS.CKPRIMARY} /> {`${location?.toLocName} ${location?.toLocAddr}` || location || ""}
          </Text>
        ) : null}
  
          <Text
            style={[
              {
                paddingTop: 5,
                paddingBottom: 5,
                fontWeight: "bold",
                fontSize: getFontByFontScale(fontScale, 24, 13),
              },
            ]}
          >
              {t("other:cargo.goodsInfo")}:
          </Text> 
  
        {item?.goodsType || item?.goodsDesc ? (
          <Block>
            <Text
              style={{
                paddingLeft: 2,
                fontSize: getFontByFontScale(fontScale, 24, 12),
              }}
            >
              {idx + 1}. {item?.goodsType}{" "}
              {item?.goodsDesc ? `| ${item?.goodsDesc}` : null}
            </Text>
            <Text
              style={{
                  paddingLeft: 2,
                  marginVertical: 3 ,
                  marginHorizontal: 1 ,
                  fontSize: getFontByFontScale(fontScale, 24, 11),
              }}
            >
                <Feather
                    name="user"
                    color={materialTheme.COLORS.CKPRIMARY}
                    size={moderateScale(14)}
                />
                {t("other:dropOffPopup.recipientName")}: {location?.cargoRecipient || "-"}
            </Text>
          </Block>
        ) : null}
        {item?.specialInstructions && (
          <Block>
            <Text
              style={{
                paddingTop: 10,
                fontWeight: "bold",
                fontSize: getFontByFontScale(fontScale, 24, 13),
                paddingLeft: 2,
              }}
            >
                {t("other:header.specialInstructions")}:
            </Text>
  
            <Text
              style={{
                fontSize: getFontByFontScale(fontScale, 24, 15),
              }}
            >
              {item?.specialInstructions}
            </Text>
          </Block>
        )}
      </Block>
    );
  }