import { useEffect, useState } from "react";
import { Animated } from "react-native";

const ExpandableBlock = ({
  expanded = false,
  defaultHeight = 100,
  toHeight = 200,
  style,
  children,
}) => {
  const [height] = useState(new Animated.Value(defaultHeight));

  useEffect(() => {
    Animated.timing(height, {
      toValue: !expanded ? defaultHeight : toHeight,
      duration: 300,
      useNativeDriver: false,
    }).start();
  }, [expanded, height]);

  return (
    <Animated.View style={[{ ...style }, { height }]}>{children}</Animated.View>
  );
};

export default ExpandableBlock;
