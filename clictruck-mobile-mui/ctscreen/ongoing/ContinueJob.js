import React from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Block, Text } from "galio-framework";
import { popupStyles } from "./commonstyles";
import { useTranslation } from "react-i18next";

const ContinueJob = ({ show, onClosePressed, onYesPressed, fontScale }) => {

  const styles = popupStyles(fontScale);
  const { t } = useTranslation();

  return (
    <CtModal
      show={show}
      onClosePressed={onClosePressed}
      fontScale={fontScale}
      onYesPressed={onYesPressed}
      headerElement={
        <Text style={styles.textModalHeader}>{t("other:popup.startDelivery")}?</Text>
      }
    >
      <Block style={styles.modalBody}>
        <Text style={{ marginBottom: 15 }}>
          {t("other:popup.pickupPhotosSubmitted")}{" "}
        </Text>
      </Block>
    </CtModal>
  );
};

export default ContinueJob;
