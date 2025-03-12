import React, { useState } from 'react';
import {
    View,
    Text,
    StyleSheet,
    useWindowDimensions,
    LayoutAnimation,
    Platform,
    UIManager
} from 'react-native';
import { getFontByFontScale } from "../constants/utils";

if (
    Platform.OS === "android" &&
    UIManager.setLayoutAnimationEnabledExperimental
) {
    UIManager.setLayoutAnimationEnabledExperimental(true);
}

const ShowRemark = ({ text, style={} }) => {
    const { fontScale } = useWindowDimensions();
    const [expanded, setExpanded] = useState(false);

    const toggleExpanded = () => {
        // 1. Configure next layout change to animate
        LayoutAnimation.configureNext(LayoutAnimation.Presets.easeInEaseOut);
        // 2. Toggle the expanded state
        setExpanded(!expanded);
    };

    const isLongText = text && text.length > 100;

    return (
        <View style={{...styles.container,  ...style, backgroundColor: "#f6f8fa"}}>
            <Text
                numberOfLines={expanded ? 0 : 2}
                style={{ fontSize: getFontByFontScale(fontScale, 24, 11) }}
            >
                {text}
            </Text>

            <Text onPress={toggleExpanded} style={styles.seeMore}>
                {expanded ? (isLongText && "See less") : (isLongText && "See more")}
            </Text>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        padding: 6,
        margin: 6,
        borderRadius: 5
    },
    seeMore: {
        marginTop: 5,
        fontSize: 13,
        color: "#999999"
    }
});

export default ShowRemark;
