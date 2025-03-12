import React, { useState } from "react";
import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import Signature from "react-native-signature-canvas";
import { Entypo, MaterialCommunityIcons, FontAwesome } from '@expo/vector-icons';
import {useTranslation} from "react-i18next";

export default function SignatureScreen({ setVisible, pushData })  {

  const [signature, setSign] = useState(null);
  const { t } = useTranslation();

  const handleOK = (signature) => {
    // console.log(signature);
    // setSign(signature);
    pushData(signature);
    handleClose();
  };

  const handleEmpty = () => {
    console.log("Empty");
  };

  function handleClose() {
    setVisible(false);
  }

  

  const style = `.m-signature-pad--footer
    .button {
      background-color: red;
      color: #FFF;
    }`;
  return (
    <View style={{ flex: 1 }}>
      {/* <View style={styles.preview}>
        {signature ? (
          <Image
            resizeMode={"contain"}
            style={{ width: 335, height: 114 }}
            source={{ uri: signature }}
          />
        ) : null}
      </View> */}
      <View style={styles.topButtonContainer}>
        {/* <Pressable onPress={toggleCameraType}>
          <Entypo name="cycle" size={24} color="black" />
        </Pressable> */}
        <View />
        <Pressable onPress={handleClose}>
          <Entypo name="cross" size={24} color="black" />
        </Pressable>
      </View>
      <Signature
        onOK={handleOK}
        onEmpty={handleEmpty}
        descriptionText="Sign"
        clearText={t("other:signaturePopup.clear")}
        confirmText={t("other:signaturePopup.save")}
        // webStyle={style}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  preview: {
    width: 335,
    height: 114,
    backgroundColor: "#F8F8F8",
    justifyContent: "center",
    alignItems: "center",
    marginTop: 15,
  },
  previewText: {
    color: "#FFF",
    fontSize: 14,
    height: 40,
    lineHeight: 40,
    paddingLeft: 10,
    paddingRight: 10,
    backgroundColor: "#69B2FF",
    width: 120,
    textAlign: "center",
    marginTop: 10,
  },
  topButtonContainer:{
    flexDirection: 'row',
    // position:'absolute',
    // top:0,
    width:"100%",
    backgroundColor: "transparent",
    justifyContent: 'space-between',
    padding:20,
    // minHeight:50,
  },
});