import React from "react";
import CtModal from "../../ctcomponents/CtModal";
import { popupStyles } from "./commonstyles";
import { Block, Text } from "galio-framework";
import GliTextInput from "../../ctcomponents/GliTextInput";
import { useTranslation } from "react-i18next";

const CargoRemarks = ({ show, onClosePressed, remarks, fontScale }) => {

    const { t } = useTranslation();

  return (
    <CtModal
      show={show}
      fontScale={fontScale}
      headerElement={
        <Text style={popupStyles.textModalHeader}>
          {t("jobHistory:popup.remarks.title")}
        </Text>
      }
      onClosePressed={onClosePressed}
      noBtn={true}
    >
      <Block style={popupStyles.modalBody}>
        <Text>{t("jobHistory:popup.remarks.remarks").toLowerCase() ?? "N/A"}</Text>
      </Block>
    </CtModal>
  );
};

export default CargoRemarks;
