import React, {useState, useEffect} from 'react';
import {Pressable, View, Text, TextInput, FlatList, TouchableOpacity, StyleSheet, Alert, Image, ImageBackground} from 'react-native';
import { Block, theme, Input } from "galio-framework";
import { Icon as IconG } from "galio-framework";
import { FontAwesome } from '@expo/vector-icons';
import { useTranslation } from 'react-i18next';
import useAuth from "../hooks/useAuth";
import useStats from '../hooks/useStats';
import { horizontalScale, moderateScale, verticalScale } from "../constants/metrics";
import { sendRequest } from '../utils/http';
import { useFocusEffect, useNavigation } from '@react-navigation/native';
import CtLoading from './CtLoading';
import ErrorModal from './ErrorModal';
import ChangePassword from './settings/ChangePassword';
import ChangeLanguage from './settings/ChangeLanguage';
import { Images, materialTheme } from "../constants/";
import { LinearGradient } from 'expo-linear-gradient';

const changePwd = '/api/v1/clickargo/clictruck/mobile/auth/changePwd';

const langOpt = [
  { label: "Bahasa", value: "id" },
  { label: "English", value: "en" },
];

const CtProfile = () => {
  
  const { newStats, pauseStats } = useStats();
  const { user } = useAuth();
  const { t, i18n } = useTranslation();
  const navigation = useNavigation();

  const [showDropdown, setShowDropdown] = useState(false);
  const [selectedLanguage, setSelectedLanguage] = useState("");
  const [langItems, setLangItems] = useState(langOpt);
  
  const [errorModalState, setErrorModalState] = useState({
    message: "",
    showErrorModal: false,
  });

  const [loading, setLoading] = useState(false);
  const [inputData, setInputData] = useState(defaultInputData);
  const [confirmPassword, setConfirmPassword] = useState();

  const [modalLangVisible, setModalLangVisible] = useState(false);
  const [modalPassChangeVisible, setModalPassChangeVisible] = useState(false);

  
  const settings = [
    {name: t('profile:settings.language'), icon: 'language', setting: 'language', background: '#e3f4fd', colorIcon: '#5da3c4'},
    {name: t('profile:settings.changePassword'), icon: 'edit', setting: 'password', background: '#fae7dc', colorIcon: '#bb6c42'},
    {name: t('profile:settings.jobHistory'), icon: 'history', setting: 'job', background: '#def1e0', colorIcon: '#75a779'},
    {name: t('profile:settings.signOut'), icon: 'sign-out', setting: 'signout', background: '#fcd9d6', colorIcon: '#c3352b'},
   ];

  const defaultInputData = {
    oldPass: "",
    newPass: "",
  };

  const setSetting = (setting) => {
    if(setting === 'language'){
      setModalLangVisible(true);
    }else if(setting === 'password'){
      setModalPassChangeVisible(true);
    }
  }
 
  const renderItem = ({item, index}) => (
    <TouchableOpacity onPress={() => {
      if (item.setting === 'job') {
        navigation.navigate('CtJobHistoryScreen');
      } else if (item.setting === 'signout') {
        navigation.navigate('Sign Out');
      } else {
        setSetting(item.setting);
      }
    }}>
      <View style={[localStyle.item]}>
        <View style={{ flexDirection: 'row' }}>
          <View style={{borderColor:item.background,borderWidth:1,padding:10, borderRadius:10,backgroundColor: item.background}}>
          <FontAwesome name={item.icon} size={15} color={item.colorIcon}/>
          </View>
          <Text style={[localStyle.settings]}>{item.name}</Text>
          
        </View>
        <View style={{ flexDirection: 'row' }}>
          <IconG name="angle-right" family="font-awesome" style={{ paddingRight: 5, color:'#6B6B6B' }} />
        </View>
      </View>
    </TouchableOpacity>
  );
  
  const handleSelectLanguage = () => {
    setModalLangVisible(false);
    console.log(selectedLanguage);
    i18n.changeLanguage(selectedLanguage);
    setSelectedLanguage("");
  };

  const isValidPassword = (password) => {
    let reg = /^(?=.*[0-9])(?=.*[@#$%^&+=!])(?=.*[a-z])(?=.*[A-Z]).{6,999}$/;
    return reg.test(password);
  }

  async function changePassword() {

    try {
        setLoading(true);
        if(confirmPassword === '' || inputData?.newPass === '' || inputData?.oldPass === ''){
          setLoading(false);
          Alert.alert('Warning', 'Password must be entry', [
            {text: 'OK', onPress: () => console.log('OK')},
          ]);
        }else if(confirmPassword === inputData?.newPass){

          if(isValidPassword(inputData?.newPass)){
            const res = await sendRequest(changePwd, "put", inputData);
            console.log('data',res)

            if (res.status === 'SUCCESS') {
                setLoading(false);
                Alert.alert('Success', 'Your password already changed', [
                  {text: 'OK', onPress: () => console.log('OK')},
                ]);
                setModalPassChangeVisible(false);
                setInputData(defaultInputData);
                
            }else{
              setLoading(false);
              setErrorModalState({
                  ...errorModalState,
                  message: res?.response.data.err.msg,
                  showErrorModal: true,
              });
            }
          }else{
            setLoading(false);
            Alert.alert('Warning', 'Password must be at least 6 characters, containing uppercase letter, lowercase letter, number and special character.', [
              {text: 'OK', onPress: () => console.log('OK')},
            ]);
          }
        }else{
          setLoading(false);
          Alert.alert('Warning', 'Confirm Password must be same with new Password', [
            {text: 'OK', onPress: () => console.log('OK')},
          ]);
        }
    } catch (error) {
        console.log('error',error)
        
        setLoading(false);
        setErrorModalState({
            ...errorModalState,
            message: error?.err?.msg,
            showErrorModal: true,
        });
    }
};

  const handleInputChange = (name, value) => {
    setInputData({ ...inputData, [name]: value });
  };

  const handleModalClose = () => {
    setErrorModalState({ ...errorModalState, showErrorModal: false });
  }

  useFocusEffect(
    React.useCallback(() => {
      navigation.closeDrawer();
    }, [])
  );

  return (
    <>
      <LinearGradient
            start={{x: 0.0, y: 0.25}} end={{x: 1, y: 3.0}}
            colors={['#69d4ff','#69d4ff']}
            style={[styles.signin, { width:'100%', height:'100%' }]}>
            <Block center style={{height:200, flexDirection:'column', justifyContent:'space-evenly'}}>
              <View middle style={[localStyle.circle]}>
                <Image resizeMode="cover" source={Images.TruckDriver} style={[localStyle.avatar,{alignSelf:'center'}]} />
              </View>
              <View>
                <Text style={{alignSelf:'center',color:'white',fontWeight:'bold','fontSize':20}}>{user?.name}</Text>
                <Text style={{alignSelf:'center',color:'white',fontWeight:'normal','fontSize':12}}>{user?.coreAccn?.accnName}</Text>
              </View>
            </Block>
            
            <Block flex style={localStyle.cardContainer}>
              <FlatList
                data={settings}
                renderItem={renderItem}
                keyExtractor={(item) => item.name}
              />
            </Block>
      </LinearGradient>

      {modalLangVisible && (
          <ChangeLanguage
            show={modalLangVisible}
            onClosePressed={() => {
              setModalLangVisible(false);
              setShowDropdown(false);
              setSelectedLanguage("");
            }}
            showDropdown={showDropdown}
            selectedLanguage={selectedLanguage}
            langItems={langItems}
            setLangItems={setLangItems}
            setShowDropdown={setShowDropdown}
            setSelectedLanguage={setSelectedLanguage}
            handleSelectLanguage={handleSelectLanguage}
          />
      )}

      {modalPassChangeVisible && (
          <ChangePassword
            show={modalPassChangeVisible}
            onClosePressed={() => {
              setModalPassChangeVisible(false);
              setInputData(defaultInputData);
            }}
            setConfirmPassword={setConfirmPassword}
            handleInputChange={handleInputChange}
            changePassword={changePassword}
          />
        )}

      <CtLoading isVisible={loading} title="Please wait" onBackdropPress={() => setLoading(false)} onRequestClose={() => setLoading(false)} />
      <ErrorModal show={errorModalState?.showErrorModal} errorMsg={errorModalState?.message} onClosePressed={() => handleModalClose()} />
    </>
  );
 };

 const localStyle = StyleSheet.create({
  cardContainer: {
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    // padding: 12,
    // marginVertical: 8,
    // elevation: 4,
    // shadowColor: theme.COLORS.BLACK,
    // shadowOffset: { width: 0, height: 2 },
    // shadowRadius: 3,
    // shadowOpacity: 0.1,
    // elevation: 2,
    backgroundColor: "#fff",
    flexDirection: "column",
    // borderWidth: 0.5,
    // width: "100%"
  },
  cardProfil: {
    borderRadius: 20,
    padding: 12,
    marginVertical: 8,
    elevation: 4,
    shadowRadius: 3,
    shadowOpacity: 0.1,
    elevation: 2,
    backgroundColor: "#fff",
    flexDirection: "column",
    // borderWidth: 0.5,
    width: "100%"
  },
  item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 10,
    borderBottomWidth: 0.5, 
    borderBottomColor: '#E5E5E5'
  },
  flag: {
     fontSize: 24,
  },
  settings: {
     fontSize: 16,
     marginLeft: 5,
     color: '#6B6B6B',
     alignSelf: 'center'
  },
  selected: {
    fontSize: 13,
     right: 0,
  },
  selectedTrue: {
     color: '#15b0ec',
  },
  header: {
    paddingHorizontal: 20,
    justifyContent: "center",
  },
    profile: {
      marginBottom: theme.SIZES.BASE / 2,

  },
  avatar: {
      height: 60,
      width: 60,
      borderRadius: 20,
      marginBottom: theme.SIZES.BASE
  },
  account: {
    marginRight: 16,
  },  
  circle: {
    height: 100,
    width: 100,
    borderRadius: 70,
    borderWidth:2,
    borderColor: '#83b8dc',
    top: 15,
    backgroundColor: 'white',
    justifyContent: 'center',
    alignItems: 'center',
    padding:15
  },
  avatar: {
    height: '100%',
    width: '100%',
    borderRadius: 20,
  },
 });

 const styles = StyleSheet.create({
  // general
  confirmButton: {
      flexDirection: "row",
      justifyContent: "space-evenly",
      alignItems: "center",
      width: "35%",
      height: 30,
      borderWidth: 1,
      marginTop: 15,
      borderRadius: 5,
  },
  container: {
      height: "100%",
      width: "100%",
      paddingHorizontal: horizontalScale(20),
      paddingTop: verticalScale(80),
      backgroundColor: "#fff",
  },
  button: {
      flexDirection: "row",
      justifyContent: "space-between",
      alignItems: "center",
      height: verticalScale(80),
      marginBottom: 10,
  },
  textButton: {
      flex: 0.8,
      fontSize: moderateScale(16),
      paddingLeft: 10,
  },
  icon: {
      flex: 0.1,
  },
  // modal
  modalHeader: {
      flexDirection: "row",
      borderBottomWidth: 1,
      paddingVertical: verticalScale(10),
      justifyContent: "space-between",
      alignItems: "center",
  },
  modalBody: {
      paddingVertical: verticalScale(10),
  },
  modalInputTextContainer: {
      width: "100%",
      height: 30,
      borderWidth: 1,
      borderRadius: 5,
      paddingHorizontal: 5,
      marginVertical: 10,
  },
  textModalHeader: {
      fontSize: moderateScale(16),
      textAlign: "left",
  },
  textModalBody: {
      fontSize: moderateScale(14),
  },
});

export default CtProfile;
