import { StyleSheet } from "react-native";
import {
  getFontByFontScale,
  moderateScale,
  verticalScale,
} from "../constants/utils";
import CtModal from "../ctcomponents/CtModal";
import { Block, Text } from "galio-framework";
import Theme from "../constants/Theme";
import { ckComponentStyles } from "../styles/componentStyles";
import { materialTheme } from "../constants";

const JobStartConfirmation = ({
  show,
  onClosePressed,
  onConfirmPressed,
  selectedJob,
  activeJob,
  isResume,
  locale,
  fontScale,
}) => {
  const styles = makeStyle(fontScale);
  return (
    <CtModal
      show={show}
      fontScale={fontScale}
      headerElement={
        <>
          {/* <Icon name="play" family="font-awesome" size={moderateScale(20)} /> */}
          <Text style={styles.textModalHeader}>
            {isResume
              ? locale("job:resumeStart.header.resume")
              : locale("job:resumeStart.header.start")}
          </Text>
        </>
      }
      onClosePressed={onClosePressed}
      onYesPressed={onConfirmPressed}
    >
      <Block>
        <Block style={styles.modalBody}>
          <Block>
            <Text style={styles.textModalBody}>
              {isResume ? (
                activeJob ? (
                  <Text>
                    {locale("job:resumeStart.resume")}{" "}
                    <Text bold color={materialTheme.COLORS.CKPRIMARY}>
                      {selectedJob?.jobRefNo}
                    </Text>{" "}
                    {locale("job:resumeStart.resumeWithActive")}{" "}
                    <Text color={Theme.COLORS.ERROR}>{activeJob}</Text>
                  </Text>
                ) : (
                  <Text>
                    {locale("job:resumeStart.resume")}{" "}
                    <Text bold color={materialTheme.COLORS.CKPRIMARY}>
                      {selectedJob?.jobRefNo}
                    </Text>{" "}
                  </Text>
                )
              ) : (
                <Text>
                  {locale("job:resumeStart.start")}{" "}
                  <Text bold color={materialTheme.COLORS.CKPRIMARY}>
                    {selectedJob?.jobRefNo}
                  </Text>
                </Text>
              )}
              .
            </Text>
          </Block>
        </Block>
      </Block>
    </CtModal>
  );
};

export default JobStartConfirmation;

const makeStyle = (fontScale) =>
  StyleSheet.create({
    modalHeader: {
      flexDirection: "row",
      paddingVertical: verticalScale(10),
      justifyContent: "space-between",
      alignItems: "center",
    },
    modalBody: {
      paddingVertical: verticalScale(10),
    },
    modalInputTextContainer: {
      width: "100%",
      borderWidth: 1,
      borderRadius: 5,
      paddingHorizontal: 5,
      marginVertical: 10,
      backgroundColor: "#eee",
    },
    textModalBody: {
      fontSize: getFontByFontScale(fontScale, 20, moderateScale(14)),
    },
    confirmButton: {
      flexDirection: "row",
      justifyContent: "space-evenly",
      alignItems: "center",
      width: "35%",
      height: 30,
      borderWidth: 1,
      marginTop: 15,
      borderRadius: 5,
    },
    textModalHeader: {
      fontSize: getFontByFontScale(fontScale, 25, moderateScale(16)),
      textAlign: "center",
      fontWeight: 500,
    },
    modalButtons: {
      ...ckComponentStyles.ckbuttons,
      color: "blue",
      width: "45%",
      flexDirection: "row",
      justifyContent: "space-evenly",
      alignItems: "center",
    },
  });
