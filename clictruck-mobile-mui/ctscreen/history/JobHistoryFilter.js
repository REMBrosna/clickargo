import React, { useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Text, theme, Input, Button } from "galio-framework";
import { makePopupStyle, popupStyles } from "./commonstyles";
import { useTranslation } from "react-i18next";
import { Pressable, View } from "react-native";
import { materialTheme } from "../../constants";
import DropDownPicker from "react-native-dropdown-picker";
import DateTimePickerModal from "react-native-modal-datetime-picker";

import { getFontByFontScale } from "../../constants/utils";

const JobHistoryFilter = ({
  show,
  onClosePressed,
  showDropdown,
  setShowDropdown,
  selectedOrder,
  setSelectedOrder,
  orderItem,
  setOrderItem,
  handleSubmitFilter,
  handleResetFilter,
  filterState,
  setFilterState,
  fontScale,
}) => {
  const { t } = useTranslation();
  const currentDate = new Date();
  const popupStyles = makePopupStyle(fontScale);

  const [datePickerStartVisible, setDatePickerStartVisible] = useState(false);
  const [datePickerEndVisible, setDatePickerEndVisible] = useState(false);

  const showDatePickerStart = () => {
    setDatePickerStartVisible(true);
  };

  const showDatePickerEnd = () => {
    setDatePickerEndVisible(true);
  };

  const hideDatePicker = () => {
    setDatePickerStartVisible(false);
    setDatePickerEndVisible(false);
  };

  const handleConfirmStartDate = (date) => {
    console.log("handleConfirmStartDate:", date);
    hideDatePicker();
    setFilterState({ ...filterState, startDate: date });
  };

  const handleConfirmEndDate = (date) => {
    console.log("handleConfirmEndDate:", date);
    hideDatePicker();
    setFilterState({ ...filterState, endDate: date });
  };

  return (
    <CtModal
      show={show}
      fontScale={fontScale}
      onClosePressed={onClosePressed}
      headerElement={
        <Text style={popupStyles.textModalHeader}>
          {t("jobHistory:popup.filter.title")}
        </Text>
      }
      noBtn={true}
      scroll={false}
    >
      <View style={[popupStyles.modalBody, { flexDirection: "column" }]}>
        <Text>{t("jobHistory:popup.filter.startDate")}</Text>
        <Pressable onPress={() => showDatePickerStart()}>
          <Input
            placeholder={t("jobHistory:popup.filter.startDate")}
            right
            icon="calendar"
            family="antdesign"
            iconSize={14}
            iconColor="#ccc"
            color={theme.COLORS.BLACK}
            value={
              filterState.startDate !== ""
                ? new Date(filterState.startDate).toLocaleDateString()
                : filterState.startDate
            }
          />
        </Pressable>
        <Text>{t("jobHistory:popup.filter.endDate")}</Text>
        <Pressable onPress={() => showDatePickerEnd()}>
          <Input
            placeholder={t("jobHistory:popup.filter.endDate")}
            right
            icon="calendar"
            family="antdesign"
            iconSize={14}
            iconColor="#ccc"
            color={theme.COLORS.BLACK}
            value={
              filterState.endDate !== ""
                ? new Date(filterState.endDate).toLocaleDateString()
                : filterState.endDate
            }
          />
        </Pressable>
        <Text>{t("jobHistory:popup.filter.orderBy")}</Text>

        <DropDownPicker
          style={{
            borderWidth: 1,
            borderColor: "#EAEAEA",
          }}
          textStyle={{
            color: "gray",
            fontSize: getFontByFontScale(fontScale, 25, null),
          }}
          dropDownContainerStyle={{
            borderColor: "#EAEAEA",
          }}
          tickIconStyle={{ tintColor: "#c1c1c1" }}
          arrowIconStyle={{ tintColor: "#c1c1c1" }}
          open={showDropdown}
          value={selectedOrder}
          items={orderItem}
          setItems={setOrderItem}
          setOpen={setShowDropdown}
          setValue={setSelectedOrder}
          placeholder={t("jobHistory:popup.filter.orderBy")}
        />
        <View
          style={{
            flexDirection: "row",
            justifyContent: "center",
            paddingTop: 10,
          }}
        >
          <Button
            round
            color={materialTheme.COLORS.CKYELLOWSECONDARY}
            style={{
              width: "45%",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
            }}
            onPress={() => handleResetFilter()}
          >
            <Text
              style={{
                color: materialTheme.COLORS.WHITE,
                fontWeight: 500,
                textAlign: "center",
                fontSize: getFontByFontScale(fontScale, 25, null),
              }}
            >
              {t("button:reset")}
            </Text>
          </Button>
          <Button
            round
            color={materialTheme.COLORS.CKPRIMARY}
            style={{
              width: "45%",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
            }}
            onPress={() => handleSubmitFilter()}
          >
            <Text
              style={{
                color: materialTheme.COLORS.WHITE,
                fontWeight: 500,
                textAlign: "center",
                fontSize: getFontByFontScale(fontScale, 25, null),
              }}
            >
              {t("button:confirm")}
            </Text>
          </Button>
        </View>
      </View>

      <DateTimePickerModal
        date={filterState.startDate ? filterState.startDate : currentDate}
        isVisible={datePickerStartVisible}
        mode="date"
        onConfirm={handleConfirmStartDate}
        onCancel={hideDatePicker}
      />
      <DateTimePickerModal
        date={filterState.endDate ? filterState.endDate : currentDate}
        isVisible={datePickerEndVisible}
        mode="date"
        onConfirm={handleConfirmEndDate}
        onCancel={hideDatePicker}
      />
    </CtModal>
  );
};

export default JobHistoryFilter;
