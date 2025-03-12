import React, { useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Block, Button, Icon, Text } from "galio-framework";
import { ActivityIndicator, ImageBackground, Pressable } from "react-native";
import GliTextInput from "../../ctcomponents/GliTextInput";
import { ScrollView } from "react-native-gesture-handler";
import { materialTheme } from "../../constants";
import { getFontByFontScale, moderateScale } from "../../constants/utils";
import { popupStyles } from "./commonstyles";
import {useTranslation} from "react-i18next";
import CheckListComponent from '../../components/CheckListComponent';

const PhotoUploadPopup = ({
  show,
  onClosePressed,
  tripId,
  action,
  handleSubmitPhoto,
  photoData,
  handlePhotoItemPress,
  handleSetViewCamera,
  onChangeText,
  fontScale,
 dropOffLocations,
}) => {

    const { t } = useTranslation();
    const styles = popupStyles(fontScale);
    const [isLoading, setIsLoading] = useState(false);
    const [checkCount,setCheckCount] = useState({checkCount: 0, totalCount: 0})
    const isDropOff = action === "dropoff";

  return (
    <CtModal
      show={show}
      headerElement={
        <Text style={styles.textModalHeader}>
          {isDropOff
            ? t("other:pickupPopup.dropOffPhotos")
            : t("other:pickupPopup.pickupPhotos")
          }
        </Text>
      }
      onClosePressed={onClosePressed}
      customBtnLabel={
        isLoading ? (
          <Button
            round
            color={materialTheme.COLORS.CKPRIMARY}
            style={[styles.modalButtons]}
            onPress={() => {}}
          >
            <ActivityIndicator />
          </Button>
        ) : (
          <Button
            round
            color={
                photoData.length === 0
                ? materialTheme.COLORS.DISABLED
                : materialTheme.COLORS.CKPRIMARY
            }
            style={[styles.modalButtons]}
            disabled={photoData.length === 0}
            onPress={() => {
              setIsLoading(true);
              handleSubmitPhoto(action, tripId);
            }}
          >
            <Text
              style={{
                color: materialTheme.COLORS.WHITE,
                fontWeight: 500,
                fontSize: getFontByFontScale(fontScale, 25, null),
              }}
            >
              {isDropOff ? t("other:pickupPopup.next") : t("other:pickupPopup.submit")}
            </Text>
          </Button>
        )
      }
    >
      <Block style={styles.modalBody}>
        <Text style={[styles.label, { marginBottom: -10 }]}>{t("other:pickupPopup.photos")}</Text>
        <ScrollView
          style={styles.photoListContainer}
          contentContainerStyle={styles.photoListContainerContent}
          horizontal={true}
        >
          {photoData &&
            photoData.map((photo, index) => (
              <Pressable
                key={index}
                onLongPress={() => handlePhotoItemPress(index)}
              >
                <ImageBackground
                  key={index}
                  source={{ uri: photo?.uri }}
                  resizeMode="cover"
                  style={styles.photoListItem}
                />
              </Pressable>
            ))}
          <Pressable
            style={styles.photoListItem}
            onPress={() => {
              handleSetViewCamera(true);
            }}
          >
            <Icon
              name="camera"
              family="font-awesome"
              size={moderateScale(15)}
              color={"darkgrey"}
            />
          </Pressable>
        </ScrollView>
        {photoData && photoData.length != 0 && (
          <Text style={[styles.label, { marginTop: -10, marginRight: 5 }]}>
              {t("other:pickupPopup.longPress")}
          </Text>
        )}

        {isDropOff ? null : (
          <GliTextInput
            label={t("other:pickupPopup.comments")}
            multiline={true}
            labelStyle={styles.label}
            onChangeText={onChangeText}
          />
        )}
        <Text style={[styles.label, { marginBottom: 10, marginTop: 10, marginRight: 5, fontSize: 15 }]}>
            {isDropOff ? t("job:checkList.dropOfCargo") : t("job:checkList.pickupCargo")}
        </Text>
        <CheckListComponent
            tripId={tripId}
            isDropOff={isDropOff}
            checkCount={checkCount}
            setCheckCount={setCheckCount}
            itemsList={dropOffLocations}
        />
      </Block>
    </CtModal>
  );
};

export default PhotoUploadPopup;
