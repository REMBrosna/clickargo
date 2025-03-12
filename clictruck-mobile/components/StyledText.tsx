import { Text as ThemedText, TextProps } from "./Themed";
import { moderateScale } from "../constants/Metrics";

export function MonoText(props: TextProps) {
    return <ThemedText {...props} style={[props.style, { fontFamily: "SpaceMono" }]} />;
}

export function TitleText(props: TextProps) {
    return <ThemedText {...props} style={[props.style, { fontSize: moderateScale(20) }]} />;
}

export function Text(props: TextProps) {
    return <ThemedText {...props} style={[props.style, { fontSize: moderateScale(16) }]} />;
}
