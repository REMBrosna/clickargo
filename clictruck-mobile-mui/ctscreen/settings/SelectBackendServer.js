import React, { useEffect, useState } from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Text, theme, Input, Button } from "galio-framework";
import { popupStyles } from "./commonstyles";
import { useTranslation } from "react-i18next";
import { View } from "react-native";
import { materialTheme } from "../../constants";
import GliTextInput from "../../ctcomponents/GliTextInput";
import { sendRequest } from "../../utils/httpUtil";
import { getFontByFontScale } from "../../constants/utils";
import {servers, setBackendUrl, getBackendServer } from "../../config/ConfigBackendUrl";
import DropDownPicker from "react-native-dropdown-picker";
import axiosInstance, {updateAxiosBaseURL} from "../../axios";
import axios from "axios";

export default function SelectBackendServer({
                                                show,
                                                onClosePressed,
                                                fontScale,
                                            }) {
    const { t } = useTranslation();

    const [selectedServerVal, setSelectedServerVal] = useState();
    const [showDropdown, setShowDropdown] = useState(false);
    /*
      const serverOpt = [
          { label: 'Production', value: 'https://sg2.clickargo.com/be/clictruck' },
          { label: 'UAT', value: 'https://ct2-uat.clickargo.com/be/clictruck' },
      ]; */
    const serverOpt = servers.map( server => {
        return {label: server.name, value: server.name}
    });

    const [serverItem, setServerItem] = useState(serverOpt);
    const [connectionStatus, setConnectionStatus] = useState("");

  function handleSubmit() {
    console.log("selectedServerVal", selectedServerVal);
    setBackendUrl(selectedServerVal);

    let server = servers.filter((s) => selectedServerVal === s.name)[0];
    updateAxiosBaseURL(server?.url)
    onClosePressed();
  }

    const handleTestConnection = async () => {
        const server = servers.find((s) => selectedServerVal === s.name);
        await updateAxiosBaseURL(server?.url)
        const response = await axios.request({
            url: `${axiosInstance.defaults.baseURL}/admin/cache/refreshSysParam`,
            method: "get",
        });
        if (response?.data){
            setConnectionStatus(`✅ Connection Successful => SERVER: ${server?.name}(${server?.url.split('.')[0]})`);
            console.log(`✅ Connection Successful:`, server?.url)
        }else {
            setConnectionStatus(`❌ Connection Failed: ${error}, SERVER: ${server?.name}(${server?.url.split('.')[0]})`);
            console.log(`❌ Connection Failed: ${error}, SERVER: ${server?.url}`, )
        }
    };

    useEffect( () => {
        (
            async () => {
                let serverName = await getBackendServer();
                console.log("serverName: ", serverName);
                setSelectedServerVal(serverName?.name);
            }
        )()

  }, []);

  return (
    <CtModal
      show={show}
      onClosePressed={onClosePressed}
      headerElement={
        <Text
          style={[
            popupStyles.textModalHeader,
            { fontSize: getFontByFontScale(fontScale, 25, null) },
          ]}
        >
          Select Server
        </Text>
      }
      noBtn={true}
      scroll={false}
    >
      {(
        <View style={[popupStyles.modalBody, { flexDirection: "column" }]}>

          <DropDownPicker
          style={{
            borderWidth: 1,
            borderColor: "#EAEAEA",
          }}
          textStyle={{
            color: "gray",
            fontSize: getFontByFontScale(fontScale, 25, null),
          }}
          dropDownContainerStyle={{
            borderColor: "#EAEAEA",
          }}
          tickIconStyle={{ tintColor: "#c1c1c1" }}
          arrowIconStyle={{ tintColor: "#c1c1c1" }}
          open={showDropdown}
          value={selectedServerVal}
          items={serverItem}

          setItems={setServerItem}
          setOpen={setShowDropdown}
          setValue={setSelectedServerVal}
          placeholder={"Select Server"}
        />

            <View
                style={{
                    flexDirection: "row",
                    // flexWrap: "wrap",
                    justifyContent: "space-evenly",
                    marginTop: "20px"
                }}
            >
                <Button
                    round
                    color="transparent"
                    shadowless
                    style={[
                        popupStyles.modalButtons,
                        { borderColor: materialTheme.COLORS.CKPRIMARY },
                    ]}
                    onPress={handleSubmit}
                >
                    <Text
                        style={{
                            color: materialTheme.COLORS.CKPRIMARY,
                            fontWeight: 500,
                            textAlign: "center",
                            fontSize: getFontByFontScale(fontScale, 25, 12),
                        }}
                    >
                        {"Confirm"}
                    </Text>
                </Button>
                <Button
                    round
                    color="transparent"
                    shadowless
                    style={[
                        popupStyles.modalButtons,
                        { borderColor: materialTheme.COLORS.CKPRIMARY },
                    ]}
                    onPress={handleTestConnection}
                >
                    <Text
                        style={{
                            color: materialTheme.COLORS.CKPRIMARY,
                            fontWeight: 500,
                            textAlign: "center",
                            fontSize: getFontByFontScale(fontScale, 25, 12),
                        }}
                    >
                        Test Connection
                    </Text>
                </Button>
            </View>
            <Text
                style={{
                    color: materialTheme.COLORS.CKPRIMARY,
                    fontWeight: 500,
                    textAlign: "center",
                    fontSize: getFontByFontScale(fontScale, 25, 8),
                }}
            >
                {connectionStatus}
            </Text>
            </View>
            )}
        </CtModal>
    );
}
