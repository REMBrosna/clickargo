import React, { useState } from "react";
import Icon from "react-native-ico-flags";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  useWindowDimensions,
} from "react-native";
import { Block, theme } from "galio-framework";
import { FontAwesome } from "@expo/vector-icons";
import { useTranslation } from "react-i18next";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import { getFontByFontScale } from "../constants/utils";

const languages = [
    { flag: "en", name: "English", icon: "united-kingdom", selected: true },
    { flag: "zh", name: "Mandarin", icon: "china", selected: false },
    { flag: "id", name: "Bahasa Indonesian", icon: "indonesia", selected: false },
    { flag: "ms", name: "Bahasa Melayu", icon: "malaysia", selected: false }
];

const CtLanguage = () => {
  const navigation = useNavigation();
  const { t, i18n } = useTranslation();
  const [selectedLanguages, setSelectedLanguages] = useState(languages);
  const [selectedLanguagesBefore, setSelectedLanguagesBefore] = useState(0);
  const { fontScale } = useWindowDimensions();

  const localStyle = makeStyle(fontScale);

  const handleSelectLanguage = (index) => {
    let updatedLanguages = [...selectedLanguages];
    if (selectedLanguagesBefore !== "") {
      updatedLanguages[selectedLanguagesBefore].selected =
        !updatedLanguages[selectedLanguagesBefore].selected;
    }
    updatedLanguages[index].selected = !updatedLanguages[index].selected;
    setSelectedLanguages(updatedLanguages);
    setSelectedLanguagesBefore(index);

    i18n.changeLanguage(updatedLanguages[index].flag);
  };

  const renderItem = ({ item, index }) => (
    <TouchableOpacity onPress={() => handleSelectLanguage(index)}>
      <View
        style={[
          localStyle.item,
          item.selected && {
            borderBottomWidth: 2,
            borderBottomColor: "#15b0ec",
          },
        ]}
      >
        <View style={{ flexDirection: "row" }}>
          <Icon name={item.icon} size={24} />
          <Text
            style={[
              localStyle.language,
              item.selected && localStyle.selectedTrue,
            ]}
          >
            {item.name}
          </Text>
        </View>
        <Text style={[localStyle.selected]}>
          {item.selected ? (
            <FontAwesome name="check-circle" size={18} color="#15b0ec" />
          ) : (
            ""
          )}
        </Text>
      </View>
    </TouchableOpacity>
  );

  useFocusEffect(
    React.useCallback(() => {
      navigation.closeDrawer();
    }, [])
  );

  return (
    <Block card flex style={localStyle.cardContainer}>
      <FlatList
        data={selectedLanguages}
        renderItem={renderItem}
        keyExtractor={(item) => item.name}
      />
    </Block>
  );
};

const makeStyle = (fontScale) =>
  StyleSheet.create({
    cardContainer: {
      borderRadius: 20,
      padding: 12,
      marginVertical: 8,
      elevation: 4,
      shadowColor: theme.COLORS.BLACK,
      shadowOffset: { width: 0, height: 2 },
      shadowRadius: 3,
      shadowOpacity: 0.1,
      elevation: 2,
      backgroundColor: "#fff",
      flexDirection: "column",
      borderWidth: 0.5,
      // width: "100%"
    },
    item: {
      flexDirection: "row",
      justifyContent: "space-between",
      alignItems: "center",
      padding: 10,
      borderBottomWidth: 0,
    },
    flag: {
      fontSize: 24,
    },
    language: {
      fontSize: getFontByFontScale(fontScale, 25, 16),
      marginLeft: 5,
    },
    selected: {
      right: 0,
    },
    selectedTrue: {
      color: "#15b0ec",
    },
  });

export default CtLanguage;
