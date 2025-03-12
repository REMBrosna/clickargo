import React from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Block, Text } from "galio-framework";
import { popupStyles } from "./commonstyles";
import {useTranslation} from "react-i18next";

const CargoDelivery = ({ show, onClosePressed, onYesPressed, fontScale }) => {

    const { t } = useTranslation();
    const styles = popupStyles(fontScale);

  return (
    <CtModal
      fontScale={fontScale}
      show={show}
      headerElement={
        <Text style={styles.textModalHeader}>{t("other:popup.startDelivery")}?</Text>
      }
      onClosePressed={onClosePressed}
      onYesPressed={onYesPressed}
    >
      <Block style={styles.modalBody}>
        <Text>{t("other:popup.recipientsWillReceive")}</Text>
      </Block>
    </CtModal>
  );
};

export default CargoDelivery;
