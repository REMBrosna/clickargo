import React, { useEffect, useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { makePopupStyle } from "./commonstyles";
import { Block, Text } from "galio-framework";
import { useTranslation } from "react-i18next";
import { StyleSheet } from "react-native";
import { materialTheme } from "../../constants";

const CargoRemarks = ({ show, onClosePressed, jobData, fontScale }) => {
  const [remarks, setRemarks] = useState([]);

  useEffect(() => {
    let arrRemarks = jobData?.trips?.map((item, idx) => {
      return {
        seqNo: item?.seqNo,
        from: item?.fromLocRemarks,
        to: item?.toLocRemarks,
      };
    });

    setRemarks([...arrRemarks]);
  }, [jobData]);

  const { t } = useTranslation();
  const popupStyles = makePopupStyle(fontScale);

  return (
    <CtModal
      show={show}
      headerElement={
        <Text style={popupStyles.textModalHeader}>
          {t("jobHistory:popup.remarks.title")}
        </Text>
      }
      onClosePressed={onClosePressed}
      noBtn={true}
    >
      <Block style={popupStyles.modalBody}>
        <Block safe left row style={styles.blockDetails}>
          <Text p style={styles.label}>
            {t("jobHistory:popup.remarks.remarks")}:
          </Text>
        </Block>
        {remarks &&
          remarks?.map((item, idx) => {
            return (
              <Block key={idx} safe left row style={styles.blockDetails}>
                <Text style={styles.dataLabel}>
                  {item?.seqNo + 1}: {item?.from} - {item?.to}
                </Text>
              </Block>
            );
          })}
      </Block>
    </CtModal>
  );
};

const styles = StyleSheet.create({
  blockDetails: {
    width: "100%",
    paddingBottom: 5,
  },
  label: {
    color: materialTheme.COLORS.BLACK,
    fontSize: 12,
    paddingLeft: 5,
    fontWeight: 600,
  },
  dataLabel: {
    paddingLeft: 5,
    fontSize: 12,
    color: materialTheme.COLORS.BLACK,
  },
});

export default CargoRemarks;
