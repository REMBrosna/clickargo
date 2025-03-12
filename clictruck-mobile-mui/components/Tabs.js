import React from "react";
import {
  StyleSheet,
  Dimensions,
  FlatList,
  Animated,
  Platform,
} from "react-native";
import { Block, Text, theme } from "galio-framework";
import JobTabContext from "../context/JobTabContext";

const { width } = Dimensions.get("window");
import materialTheme from "../constants/Theme";
import { verticalScale } from "../constants/metrics";
import { getSizeByOS, horizontalScale } from "../constants/utils";

const defaultMenu = [
  { id: "popular", title: "Popular" },
  { id: "beauty", title: "Beauty" },
  { id: "cars", title: "Cars" },
  { id: "motocycles", title: "Motocycles" },
];

export default class Tabs extends React.Component {
  static defaultProps = {
    data: defaultMenu,
    initialIndex: null,
  };

  state = {
    active: null,
  };

  componentDidMount() {
    const { initialIndex } = this.props;
    initialIndex && this.selectMenu(initialIndex);
  }

  componentDidUpdate(prevProps, prevState) {
    //this is to move to the Active job screen everytime there is update from either
    if (
      prevState?.active === this.state?.active &&
      this.context?.jobTabId &&
      this.context?.jobTabId != this.state?.active
    ) {
      this.selectMenu(this.context?.jobTabId);
    }
  }

  animatedValue = new Animated.Value(1);

  animate() {
    this.animatedValue.setValue(0);

    Animated.timing(this.animatedValue, {
      toValue: 1,
      duration: 300,
      useNativeDriver: false, // color not supported
    }).start();
  }

  menuRef = React.createRef();

  onScrollToIndexFailed = () => {
    this.menuRef.current.scrollToIndex({
      index: 0,
      viewPosition: 0.5,
    });
  };

  selectMenu = (id) => {
    this.setState({ active: id });

    // this.menuRef.current.scrollToIndex({
    //     index: this.props.data.findIndex(item => item.id === id),
    //     viewPosition: 0.5
    // });

    this.animate();
    this.props.onChange && this.props.onChange(id);
  };

  renderItem = (item) => {
    const isActive = this.state.active === item.id;

    const textColor = this.animatedValue.interpolate({
      inputRange: [0, 1],
      outputRange: [
        materialTheme.COLORS.MUTED,
        isActive ? materialTheme.COLORS.CKPRIMARY : materialTheme.COLORS.MUTED,
      ],
      extrapolate: "identity",
    });

    const fontWeight = this.animatedValue.interpolate({
      inputRange: [300, 900],
      outputRange: [900, isActive ? 900 : 600],
      extrapolate: "clamp",
      easing: (value) => {
        const thousandRounded = value * 1000;
        if (thousandRounded < 300) {
          return 0;
        }

        if (thousandRounded < 600) {
          return 0.5;
        }

        return 1;
      },
    });

    const opacity = this.animatedValue.interpolate({
      inputRange: [0, 1],
      outputRange: [1, isActive ? 1 : 0.5],
      extrapolate: "clamp",
    });

    const width = this.animatedValue.interpolate({
      inputRange: [0, 1],
      outputRange: ["0%", isActive ? "100%" : "0%"],
      extrapolate: "clamp",
    });

    let count = 0;

    if (item?.id === "new") count = this.props?.stats?.newStats?.count;
    if (item?.id === "paused") count = this.props?.stats?.pauseStats?.count;

    let extendBadge = count.toString().length > 99 && { width: 25 };

    if (isActive) {
      additionalActiveStyle = { backgroundColor: "#e7e7e7" };
    }

    let centerIcon = null;
    if (item?.id === "active") {
      centerIcon = {
        height: verticalScale(70),
        width: verticalScale(70),
        borderRadius: 35,
        alignItems: "center",
        justifyContent: "center",
      };
    }

    return (
      <Block style={[styles.titleContainer]}>
        <Animated.View
          style={{
            alignItems: "center",
            justifyContent: "center",
            paddingLeft: verticalScale(getSizeByOS(15, 0)),
          }}
        >
          <Animated.Text
            style={[
              styles.menuTitle,
              {
                color: textColor,
                fontWeight: Platform.OS === "os" ? fontWeight : 900,
              },
            ]}
            onPress={() => this.selectMenu(item.id)}
          >
            <Block
              flex
              middle
              style={{
                flexDirection: "column",
                width: horizontalScale(85),
                alignItems: "center",
                justifyContent: "center",
                // borderWidth: 1,
                // borderColor: "red"
              }}
            >
              <Block style={[styles.imageTitleBlock, centerIcon]}>
                <Animated.Image
                  source={item.image}
                  style={[styles.imageTitle, { opacity: opacity }]}
                />
              </Block>

              {item?.id !== "active" && (
                <>
                  <Animated.Text
                    style={[
                      styles.menuTitle,
                      {
                        color: isActive
                          ? materialTheme.COLORS.CKPRIMARY
                          : textColor,
                      },
                    ]}
                  >
                    {item.title}
                  </Animated.Text>
                  <Block middle style={[styles.badge, extendBadge]}>
                    <Text
                      style={{
                        fontSize: 10,
                        color: materialTheme.COLORS.WHITE,
                        textAlign: "center",
                        textAlignVertical: "center",
                        alignSelf: "center",
                        right: 1,
                        fontWeight: "600",
                      }}
                    >
                      {" "}
                      {item?.id !== "active" && `${count}`}
                    </Text>
                  </Block>
                </>
              )}
            </Block>
          </Animated.Text>
        </Animated.View>
        {item?.id !== "active" && (
          <Animated.View
            style={{
              height: 2,
              width,
              backgroundColor: materialTheme.COLORS.CKPRIMARY,
            }}
          />
        )}
      </Block>
    );
  };

  renderMenu = () => {
    const { data, ...props } = this.props;

    return (
      <FlatList
        {...props}
        data={data}
        horizontal={true}
        ref={this.menuRef}
        extraData={this.state}
        keyExtractor={(item) => item.id}
        scrollEnabled={false}
        automaticallyAdjustContentInsets
        centerContent
        // ListHeaderComponentStyle={{ alignContent: 'center' }}
        showsHorizontalScrollIndicator={false}
        onScrollToIndexFailed={this.onScrollToIndexFailed}
        renderItem={({ item }) => this.renderItem(item)}
        contentContainerStyle={styles.menu}
      />
    );
  };

  render() {
    return <Block style={styles.container}>{this.renderMenu()}</Block>;
  }
}

Tabs.contextType = JobTabContext;
const styles = StyleSheet.create({
  container: {
    width: width,
    backgroundColor: theme.COLORS.WHITE,
    zIndex: 2,
    alignItems: "center",
    justifyContent: "center",
  },
  shadow: {
    shadowColor: theme.COLORS.BLACK,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 8,
    shadowOpacity: 0.2,
    elevation: 4,
  },
  menu: {
    paddingTop: horizontalScale(8),
    width: width,
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  titleContainer: {
    alignItems: "center",
    justifyContent: "center",
    height: verticalScale(75) + 3,
    padding: 5,
    paddingBottom: 0,
    borderRadius: getSizeByOS(10, 10),
    // width: getSizeByOS(horizontalScale(100), horizontalScale(120)),
    width: horizontalScale(120),
  },
  menuTitle: {
    fontWeight: 500,
    fontSize: getSizeByOS(10, 10),
    lineHeight: 28,
    paddingHorizontal: 12,
  },
  imageTitleBlock: {
    borderRadius: 20,
    height: verticalScale(50),
    width: verticalScale(50),
    // borderWidth: 1,
    backgroundColor: "rgba(255, 255, 255, 0.95)",
    elevation: 10,
    shadowColor: "#000000",
    shadowOpacity: 0.2,
    shadowRadius: 2,
    shadowOffset: {
      height: 1,
      width: 1,
    },
    alignItems: "center",
    justifyContent: "center",
  },
  imageTitle: {
    height: verticalScale(40),
    width: horizontalScale(40),
    alignItems: "center",
    justifyContent: "center",
    // borderWidth: 1,
  },
  badge: {
    backgroundColor: materialTheme.COLORS.CKSECONDARY,
    borderRadius: 20,
    alignSelf: "center",
    alignContent: "center",
    justifyContent: "center",
    height: 20,
    width: 20,
    position: "absolute",
    top: 0,
    right: 15,
  },
});
