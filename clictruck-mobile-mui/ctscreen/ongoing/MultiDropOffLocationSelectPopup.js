import React, { useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Block, Text } from "galio-framework";
import { ScrollView } from "react-native-gesture-handler";
import { popupStyles } from "./commonstyles";
import RadioGroup from "react-native-radio-buttons-group";
import { useTranslation } from "react-i18next";

const MultiDropOffLocationSelectPopup = ({
  show,
  onClosePressed,
  list,
  handleConfirm,
  fontScale,
}) => {

  const { t } = useTranslation();
  const styles = popupStyles(fontScale);
  const [selectedId, setSelectedId] = useState();

  //build the selection from the list
  const radioButtons = list?.map((itm, ind) => {
    return {
      id: itm?.id,
      // label: `(${itm?.seqNo + 1}) ${itm?.toLocName}`,
      label: `(${ind + 1}) ${itm?.toLocName}`,
      value: itm?.id,
    };
  });

  return (
    <CtModal
      show={show}
      headerElement={
        <Text style={styles.textModalHeader}>{t("other:popup.selectDropOffLocation")}</Text>
      }
      onClosePressed={onClosePressed}
      noLabel={t("other:popup.cancel")}
      onYesPressed={() => handleConfirm(selectedId)}
    >
      <Block style={styles.modalBody}>
        <ScrollView horizontal={true}>
          {radioButtons && (
            <RadioGroup
              radioButtons={radioButtons}
              layout="column"
              containerStyle={{
                flex: 1,
                flexDirection: "column",
                alignContent: "flex-start",
                justifyContent: "flex-start",
                alignItems: "flex-start",
              }}
              onPress={(e) => setSelectedId(e)}
              selectedId={selectedId}
            />
          )}
        </ScrollView>
      </Block>
    </CtModal>
  );
};

export default MultiDropOffLocationSelectPopup;
