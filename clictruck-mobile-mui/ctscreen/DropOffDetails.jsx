import { Block, Icon, Text } from "galio-framework";
import {
  displayDate,
  getFontByFontScale,
  moderateScale,
} from "../constants/utils";

/** Component for drop off details to be use in JobItem for New/Paused */
export function DropOffDetails({ style, item, fontScale, t, locale, id }) {
  return (
    <Block key={id}>
      <Block style={style.body}>
        <Block style={{ flex: 0.2, alignItems: "center" }}>
          <Block>
            <Icon
              name="location"
              family="ionicon"
              size={moderateScale(40)}
              color={"#0BA247"}
            />
          </Block>
        </Block>
        <Block style={{ flex: 0.85, height: "80%" }}>
          <Text style={style.header}>{item?.toLocName}</Text>
          <Text style={style.subtitle}>{item?.toLocAddr}</Text>
          <Block style={style.timing}>
            <Icon
              name="clock-o"
              family="font-awesome"
              size={moderateScale(20)}
              color={"#ccc"}
              style={{ paddingRight: 4 }}
            />
            <Text
              style={[
                style.subtitle,
                { fontSize: getFontByFontScale(fontScale, 24, 12) },
              ]}
            >
              {t("job:estDropOff")}: {displayDate(item?.estDropOffTime, locale)}
            </Text>
          </Block>
        </Block>
      </Block>
      {/* Intentional block for separation */}
      <Block style={{ paddingTop: 1 }}></Block>
    </Block>
  );
}
