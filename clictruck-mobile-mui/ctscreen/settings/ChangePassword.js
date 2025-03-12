import React from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Text, theme, Input, Button } from "galio-framework";
import { popupStyles } from "./commonstyles";
import { useTranslation } from 'react-i18next';
import { View } from "react-native";
import { materialTheme } from "../../constants";


const ChangePassword = ({ show, onClosePressed, handleInputChange, setConfirmPassword, changePassword}) => {

    const { t } = useTranslation();
    

    return <CtModal show={show}
        onClosePressed={onClosePressed}
        headerElement={<Text style={popupStyles.textModalHeader}>{t('profile:settings.changePassword')}</Text>}
        noBtn={true}>

        <View style={[popupStyles.modalBody, { flexDirection: 'column'}]}>
            <Text>{t("password:form.currentPassword")}</Text>
            <Input
              placeholder={t("password:form.currentPassword")}
              password
              viewPass
              color={theme.COLORS.BLOCK}
              onChangeText={(e) => handleInputChange("oldPass", e)}
            />
            <Text>{t("password:form.newPassword")}</Text>
            <Input
              placeholder={t("password:form.newPassword")}
              password
              viewPass
              color={theme.COLORS.BLOCK}
              onChangeText={(e) => handleInputChange("newPass", e)}
            />
            <Text>{t("password:form.confirmPassword")}</Text>
            <Input
              placeholder={t("password:form.confirmPassword")}
              password
              viewPass
              color={theme.COLORS.BLOCK}
              onChangeText={(e) => setConfirmPassword(e)}
            />
            <View style={{ flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-evenly' }}>
            <Button
                    round color="transparent" shadowless
                    style={[popupStyles.modalButtons, { borderColor: materialTheme.COLORS.CKPRIMARY }]}
                    onPress={ () => changePassword()} >
                    <Text style={{ color: materialTheme.COLORS.CKPRIMARY, fontWeight: 500, textAlign: 'center' }}>{t('profile:settings.changePassword')}</Text>
                 </Button>
            </View>
        </View>
    </CtModal>
}


export default ChangePassword;