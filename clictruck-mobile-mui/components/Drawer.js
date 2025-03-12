import React from "react";
import { StyleSheet, TouchableOpacity } from "react-native";
import { Block, Text, theme } from "galio-framework";

import Icon from "./Icon";
import materialTheme from "../constants/Theme";

class DrawerItem extends React.Component {
  renderIcon = () => {
    const { title, focused } = this.props;

    switch (title) {
      case "Laman Utama":
      case "主页":
      case "Home":
        return (
          <Icon
            size={14}
            name="home"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Beranda":
        return (
          <Icon
            size={14}
            name="home"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "语言":
      case "Language":
        return (
          <Icon
            size={16}
            name="globe"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Bahasa":
        return (
          <Icon
            size={16}
            name="globe"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Tukar Kata Laluan":
      case "更改密码":
      case "Change Password":
        return (
          <Icon
            size={15}
            name="unlock"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Ubah Password":
        return (
          <Icon
            size={15}
            name="unlock"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Sejarah Pekerjaan":
      case "历史订单列表":
      case "Job History":
        return (
          <Icon
            size={15}
            name="history"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Riwayat Job":
        return (
          <Icon
            size={15}
            name="history"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Delete Job Data":
        return (
          <Icon
            size={15}
            name="trash"
            family="ionicon"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Profile":
        return (
          <Icon
            size={15}
            name="circle-10"
            family="GalioExtra"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Settings":
        return (
          <Icon
            size={15}
            name="gears"
            family="font-awesome"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Components":
        return (
          <Icon
            size={17}
            name="md-triangle"
            family="ionicon"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Log Keluar":
      case "退出":
      case "Sign Out":
        return (
          <Icon
            size={15}
            name="log-out"
            family="ionicon"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Keluar":
        return (
          <Icon
            size={15}
            name="log-out"
            family="ionicon"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      case "Sign Up":
        return (
          <Icon
            size={15}
            name="md-person-add"
            family="ionicon"
            color={
              focused
                ? materialTheme.COLORS.CKPRIMARY
                : materialTheme.COLORS.MUTED
            }
          />
        );
      default:
        return null;
    }
  };
  render() {
    const { title, focused, navigation, navigateTo, fontSize } = this.props;
    return (
      <TouchableOpacity
        style={{ height: 55 }}
        onPress={() => navigation.navigate(navigateTo)}
      >
        <Block
          flex
          row
          style={[
            styles.defaultStyle,
            focused ? [styles.activeStyle, styles.shadow] : null,
            {
              paddingVertical: 10,
            },
          ]}
        >
          <Block middle flex={0.1} style={{ marginRight: 28 }}>
            {this.renderIcon()}
          </Block>
          <Block flex={0.9}>
            <Text
              size={fontSize ?? 15}
              color={
                focused
                  ? materialTheme.COLORS.CKPRIMARY
                  : materialTheme.COLORS.BLACK
              }
            >
              {title}
            </Text>
          </Block>
        </Block>
      </TouchableOpacity>
    );
  }
}

export default DrawerItem;

const styles = StyleSheet.create({
  defaultStyle: {
    paddingVertical: 16,
    paddingHorizontal: 16,
    marginBottom: 6,
  },
  activeStyle: {
    backgroundColor: materialTheme.COLORS.CKSECONDARY,
    borderRadius: 4,
  },
  shadow: {
    shadowColor: theme.COLORS.BLACK,
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowRadius: 8,
    shadowOpacity: 0.2,
    backgroundColor: theme.COLORS.WHITE,
  },
});
