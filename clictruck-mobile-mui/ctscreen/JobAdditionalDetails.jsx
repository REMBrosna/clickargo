import React, { useEffect, useState } from "react";
import { Block, Text } from "galio-framework";
import { MaterialIcons } from "@expo/vector-icons";
import { materialTheme } from "../constants";
import { getFontByFontScale, moderateScale } from "../constants/utils";
import { StyleSheet, TouchableOpacity } from "react-native";
import RenderHtml, {
  HTMLContentModel,
  HTMLElementModel,
} from "react-native-render-html";
import { useWindowDimensions } from "react-native";
import { useTranslation } from "react-i18next";

const JobAdditionalDetails = ({ details, fontScale }) => {
  const { width } = useWindowDimensions();
  const [isExpanded, setIsExpanded] = useState(false);
  const [minHeight, setMinHeight] = useState(120);
  const [showMore, setShowMore] = useState(false);
  const [content, setContent] = useState("");
  const [fullDetails, setFullDetails] = useState("");
  const { t } = useTranslation();

  const styles = createLocalStyles("100%");
  useEffect(() => {
    constructAdditionalDetails(details);
  }, [details]);

  const constructAdditionalDetails = (details) => {
    if (details?.length > minHeight) {
      setShowMore(true);
      setFullDetails(details);
      let modifiedContent = getSubstring(details, 0, minHeight).concat("...");
      setContent(modifiedContent);
    } else {
      setShowMore(false);
      setContent(details);
    }
  };

  function getSubstring(paragraph, startIndex, length) {
    // Ensure startIndex is within bounds
    if (startIndex < 0) {
      startIndex = 0;
    } else if (startIndex >= paragraph.length) {
      return "";
    }

    let remainingLength = length;
    let substring = "";
    const words = paragraph.split(" ");
    for (const word of words) {
      if (remainingLength === 0) {
        break;
      }

      const wordLength = word.length;

      if (startIndex < wordLength) {
        substring += word.substr(startIndex, remainingLength) + " ";
        remainingLength -= wordLength - startIndex;
        startIndex = 0;
      } else {
        startIndex -= wordLength;
      }
    }

    return substring.trim();
  }

  const MemoizedRenderHtml = React.memo(RenderHtml);

  const customHTMLElementModels = {
    p: HTMLElementModel.fromCustomModel({
      tagName: "p",
      mixedUAStyles: {
        width: "100%",
        alignSelf: "justify",
        // borderWidth: 1,
        padding: 5,
      },
      contentModel: HTMLContentModel.block,
    }),
  };

  return (
    <>
      <Block style={styles.detailsContainer}>
        <Block
          row
          right
          fluid
          space="between"
          style={{
            borderBottomWidth: 1,
            width: "100%",
            borderColor: "rgba(15,176,232, 1)",
            marginBottom: 3,
          }}
        >
          <Text
            p
            style={{
              color: materialTheme.COLORS.BLACK,
              fontSize: getFontByFontScale(fontScale, 24, 14),
              paddingLeft: 5,
              fontWeight: 500,
            }}
          >
            {t("other:cargo.goodsInfo")}:
          </Text>

          {showMore && (
            <Block right style={{ paddingLeft: 5 }}>
              <TouchableOpacity
                onPress={() => {
                  setIsExpanded(!isExpanded);
                }}
              >
                <Text
                  style={{
                    fontSize: getFontByFontScale(fontScale, 14, 10),
                  }}
                  color={materialTheme.COLORS.CKPRIMARY}
                >
                  {!isExpanded ? t("other:screen.showMore") : t("other:screen.showLess")}
                  <MaterialIcons
                    name={!isExpanded ? "expand-more" : "expand-less"}
                    size={getFontByFontScale(
                      fontScale,
                      moderateScale(24),
                      moderateScale(12)
                    )}
                    color={materialTheme.COLORS.BLACK}
                  />
                </Text>
              </TouchableOpacity>
            </Block>
          )}
        </Block>
        <Block style={{ paddingBottom: fontScale }}>
          {/* <Text style={[styles.mutedSubLabel, { padding: 4 }]}>
          {!isExpanded ? content : fullDetails}
        </Text> */}
          <MemoizedRenderHtml
            contentWidth={width}
            source={{ html: !isExpanded ? content : fullDetails }}
            customHTMLElementModels={customHTMLElementModels}
          ></MemoizedRenderHtml>
        </Block>
      </Block>
    </>
  );
};

const createLocalStyles = (width) =>
  StyleSheet.create({
    detailsContainer: {
      // width: "85%",
      width: width,
      backgroundColor: "rgba(231,244,253, 1)",
      paddingTop: 10,
      // paddingBottom: 10,
      flexDirection: "column",
      justifyContent: "flex-start",
      alignItems: "flex-start",
      borderRadius: 10,
      borderColor: "rgba(15,176,232, 1)",
      borderWidth: 1,
    },
    mutedLabel: {
      color: materialTheme.COLORS.BLACK,
      fontWeight: 500,
      fontSize: moderateScale(12),
      paddingBottom: 5,
    },
    mutedSubLabel: {
      color: materialTheme.COLORS.BLACK,
      fontWeight: 400,
      fontSize: moderateScale(12),
      paddingBottom: 5,
    },
    mutedIcon: {
      size: moderateScale(14),
    },
  });

export default JobAdditionalDetails;
