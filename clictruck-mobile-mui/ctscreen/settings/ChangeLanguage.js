import React from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Text, theme, Input, Button } from "galio-framework";
import { popupStyles } from "./commonstyles";
import { useTranslation } from 'react-i18next';
import { View } from "react-native";
import { materialTheme } from "../../constants";
import DropDownPicker from "react-native-dropdown-picker";


const ChangeLanguage = ({ show, onClosePressed, showDropdown, selectedLanguage, langItems, setLangItems, setShowDropdown, setSelectedLanguage, handleSelectLanguage}) => {

    const { t } = useTranslation();
    
    console.log('langItems',langItems);
    return <CtModal show={show}
        onClosePressed={onClosePressed}
        headerElement={<Text style={popupStyles.textModalHeader}>{t('profile:settings.language')}</Text>}
        noBtn={true}>

        <View style={[popupStyles.modalBody, { flexDirection: 'column'}]}>
            {/* <Text>{t('profile:label.changeLanguage')}:</Text> */}
            <DropDownPicker
              open={showDropdown}
              value={selectedLanguage}
              items={langItems}
              setItems={setLangItems}
              setOpen={setShowDropdown}
              setValue={setSelectedLanguage}
              placeholder={t('profile:label.changeLanguage')}
            />
            <View style={{ flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-evenly', padding:7 }}>
                <Button
                        round color="transparent" shadowless
                        style={[popupStyles.modalButtons, { borderColor: materialTheme.COLORS.CKPRIMARY }]}
                        onPress={ () => handleSelectLanguage()} >
                        <Text style={{ color: materialTheme.COLORS.CKPRIMARY, fontWeight: 500, textAlign: 'center' }}>{t('profile:button.confirm')}</Text>
                    </Button>
                </View>
            </View>
    </CtModal>
}


export default ChangeLanguage;