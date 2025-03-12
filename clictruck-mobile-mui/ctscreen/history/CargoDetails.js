import React, { useEffect, useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Block, Text } from "galio-framework";
import { makePopupStyle } from "./commonstyles";
import { convertToString } from "../../constants/utils";
import { useTranslation } from "react-i18next";
import { materialTheme } from "../../constants";
import { ScrollView, StyleSheet } from "react-native";

const CargoDetails = ({ show, onClosePressed, data, fontScale }) => {
  const { t } = useTranslation();

  const [cargoDetails, setCargoDetails] = useState([]);

  const popupStyles = makePopupStyle(fontScale);

  useEffect(() => {
    let arrDetails = data?.trips?.map((item, idx) => {
      return {
        seqNo: item?.seqNo,
        cargos: item?.cargos?.map((cItm, cIdx) => {
          return {
            contNo: cItm?.cntNo,
            sealNo: cItm?.cntSealNo,
            load: cItm?.cntLoad,
            type: cItm?.goodsType,
            desc: cItm?.goodsDesc,
            specialIns: cItm?.specialInstructions,
          };
        }),
      };
    });

    setCargoDetails([...arrDetails]);
  }, [data]);
  return (
    <CtModal
      show={show}
      onClosePressed={onClosePressed}
      headerElement={
        <Text style={popupStyles.textModalHeader}>
          {t("jobHistory:popup.cargo.title")}
        </Text>
      }
      noBtn={true}
    >
      <ScrollView style={{ flex: 1 }}>
        <Block
          style={[
            popupStyles.modalBody,
            { flexDirection: "row", flexWrap: "wrap" },
          ]}
        >
          {cargoDetails ? (
            cargoDetails?.map((item, idx) => {
              return (
                <React.Fragment key={idx}>
                  <Block left row style={styles.blockDetails}>
                    <Text style={{ fontWeight: "bold" }}>
                      ({item?.seqNo + 1})
                    </Text>
                  </Block>
                  {item?.cargos?.map((cItm, cIdx) => {
                    return (
                      <Block key={cIdx} style={styles.blockSubDetails}>
                        <Block left row>
                          <Text p style={styles.label}>
                            {t("jobHistory:popup.cargo.type")}:
                          </Text>
                          <Text style={styles.dataLabel}>{cItm?.type}</Text>
                        </Block>
                        <Block
                          safe
                          left
                          row
                          style={[styles.blockDetails, { marginBottom: 5 }]}
                        >
                          <Text p style={styles.label}>
                            {t("jobHistory:popup.cargo.description")}:
                          </Text>
                          <Text style={styles.dataLabel}>{cItm?.desc}</Text>
                        </Block>

                        <Block safe left row style={styles.blockDetails}>
                          <Text p style={styles.label}>
                            {t("jobHistory:popup.cargo.remarks")}:
                          </Text>
                        </Block>
                        <Block safe left row style={styles.blockDetails}>
                          <Text style={styles.dataLabel}>
                            {cItm?.specialIns}
                          </Text>
                        </Block>
                      </Block>
                    );
                  })}
                </React.Fragment>
              );
            })
          ) : (
            <Text>N/A</Text>
          )}
        </Block>
      </ScrollView>
    </CtModal>
  );
};

const styles = StyleSheet.create({
  blockDetails: {
    width: "100%",
    paddingBottom: 5,
  },
  blockSubDetails: {
    width: "100%",
    paddingBottom: 5,
    paddingLeft: 5,
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
  },
});

export default CargoDetails;
