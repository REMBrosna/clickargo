import { Platform, StatusBar, Dimensions } from "react-native";
import { theme } from "galio-framework";
import AsyncStorage from "@react-native-async-storage/async-storage";

const width = Dimensions.get("window").width;
const height = Dimensions.get("window").height;

export const StatusHeight = StatusBar.currentHeight;
export const HeaderHeight = theme.SIZES.BASE * 4 + StatusHeight;
export const iPhoneX = () =>
  Platform.OS === "ios" && (height === 812 || width === 812);

export const getSizeByOS = (iosSize, androidSize) =>
  Platform.OS === "ios" ? iosSize : androidSize;

const guidelineBaseWidth = 375;
const guidelineBaseHeight = 812;

export const horizontalScale = (size) => (width / guidelineBaseWidth) * size;
export const verticalScale = (size) => (height / guidelineBaseHeight) * size;
export const moderateScale = (size, factor = 0.5) =>
  size + (horizontalScale(size) - size) * factor;

export const getFontByFontScale = (fontScale, sizeByFontScale, defaultSize) => {
  if (Platform.OS === "ios") {
    if (fontScale > 3) return sizeByFontScale / fontScale;
    return defaultSize;
  } else {
    if (fontScale >= 2) return (sizeByFontScale - 2) / fontScale;
    return defaultSize;
  }
};

export function displayDate(
  unixTime,
  locale = "en",
  withTime = true,
  onlyTime = false
) {
  if (unixTime) {
    // Convert Unix timestamp to JavaScript Date object
    const date = new Date(unixTime);

    const year = new Intl.DateTimeFormat(locale, { year: "numeric" }).format(
      date
    );
    const month = new Intl.DateTimeFormat(locale, {
      month: "2-digit",
    }).format(date);
    const day = new Intl.DateTimeFormat(locale, { day: "2-digit" }).format(
      date
    );
    const weekday = new Intl.DateTimeFormat(locale, {
      weekday: "short",
    }).format(date);

    const hours = new Intl.DateTimeFormat(locale, {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    }).format(date);

    // Return formatted date
    let formattedDate = `${weekday}, ${year}-${month}-${day}`;
    if (withTime) formattedDate += ` ${hours}`;
    if (onlyTime) formattedDate = hours;

    return formattedDate;
  } else {
    return unixTime;
  }
}

export const convertToString = (variable) => {
  if (typeof variable === "number") {
    return variable.toString();
  } else if (typeof variable === "string") {
    return variable;
  }
};

//THIS FUNCTION STORE ONGOING JOB TO LOCAL MEMORY FOR OFFLINE CAPABILITY
export async function storeJob(value) {
  try {
    const jsonValue = JSON.stringify(value);
    await AsyncStorage.setItem("ongoingJob", jsonValue);
    console.log("Successfully store ongoingJob to async storage");
    return true;
  } catch (e) {
    console.error("AsyncStorage Error: ", e);
    return false;
  }
}
