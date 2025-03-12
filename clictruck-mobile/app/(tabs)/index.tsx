import { FontAwesome, MaterialCommunityIcons } from '@expo/vector-icons';
import { Pressable,Text as RText, RefreshControl, ScrollView, StyleSheet, useColorScheme, Modal, ImageBackground, Image } from 'react-native';
import { ExternalLink } from '../../components/ExternalLink';
import { Text, TitleText } from '../../components/StyledText';
import { View } from '../../components/Themed';
// import { useColorScheme } from 'react-native/Libraries/Utilities/Appearance';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useBottomTabBarHeight } from '@react-navigation/bottom-tabs';
import { router } from 'expo-router';
import React, { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import GliTextInput from '../../components/GliTextInput';
import ClictruckModal from '../../components/modal/ClictruckModal';
import Colors from '../../constants/Colors';
import { horizontalScale, moderateScale, verticalScale } from '../../constants/Metrics';
import { convertToString, displayDate, sendRequest } from '../../constants/util';
import PhotoUploadScreen from '../../components/photoUpload';



export default function OngoingJobScreen() {
  console.log("ongoing screen render");

  async function fetchData () {
    // console.log("fetch data from storage");
    try {
      const storedData = await AsyncStorage.getItem('ongoingJob');
      if (storedData !== null) {
        setOngoingJobData(JSON.parse(storedData));
        setNoData(false);
        // console.log("Fetch Data Successful");
        // console.log(storedData);
      } else {
        console.log('Error fetching ongoing data: Data is null');
        setNoData(true);
      }
    } catch (error) {
      console.error('Error fetching ongoing data:', error);
    }
  };

async function getOngoing() { 
  try {
      const response = await sendRequest(process.env.EXPO_PUBLIC_BACKEND_URL + "/api/v1/clickargo/clictruck/mobile/trip/ongoing");
      if (response.data !== null) {
        const storedData = response.data;
        // console.log("ongoingjob",storedData);
        setOngoingJobData(storedData);
        setNoData(false);

        await AsyncStorage.setItem('ongoingJob', JSON.stringify(storedData));
      }
  } catch (error) {
    const storedData = await AsyncStorage.getItem('ongoingJob');
    if (storedData !== null) {
      setOngoingJobData(JSON.parse(storedData));
      setNoData(false);
    } else {
      setNoData(true);
    }
  }
};

  useEffect(() => {
    console.log("useEffect Ongoing Job Screen");
    fetchData();
  }, [])
  
  // useFocusEffect(()=>{
  //   console.log("use focus effect at ongoing job screen")
  //   fetchData();
  // });
  

  
  const colorScheme = useColorScheme();
  let color = Colors[colorScheme ?? 'light'];

  const { t } = useTranslation();

  const [noData, setNoData] = useState<boolean>(false);
  const [refreshing, setRefreshing] = useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [modalPhotoUpload, setModalPhotoUpload] = useState<boolean>(false); //pickup button & dropOff button
  const [modalContinueJob, setModalContinueJob] = useState<boolean>(false); //pickup->continue
  const [modalRemark, setModalRemark] = useState<boolean>(false); //reamrks button
  const [modalCargoDetail, setModalCargoDetail] = useState<boolean>(false); //cargo button
  const [modalStartDelivery, setModalStartDelivery] = useState<boolean>(false); //Deliver Cargo button (action confirmation)
  const [modalCustomerConfirmation, setModalCustomerConfirmation] = useState<boolean>(false); //dropoff->continue
  const [cameraScreenVisible, setCameraScreenVisible] = useState<boolean>(false);
  const [photoComment, setPhotoComment] = useState<string | undefined>();


  const onRefresh = useCallback( () => {
    setRefreshing(true);
    getOngoing();
    // fetchData();
    setTimeout(() => {
      setRefreshing(false);
      }, 2000);
    }, [])
  

  const [ongoingJobData, setOngoingJobData] = useState<any | undefined>();
  
  const jobId = ongoingJobData?.jobId;
  const jobType = ongoingJobData?.tckJob?.tckMstShipmentType?.shtName;
  const locFrom = 
      ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locName + "\n" +
      ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locAddress;
  const locTo = 
      ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tckCtLocation?.locName + "\n" +
      ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tckCtLocation?.locAddress;
  const timePickUp = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tlocDtLoc;
  const timeDropOff = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tlocDtLoc;
  const timeStart = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locDtStart;
  const timePickedup = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tckCtLocation?.locDtEnd;
  const timeDeliver = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tckCtLocation?.locDtStart;
  const timeJobDone = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrTo?.tckCtLocation?.locDtEnd;
  const tripId = ongoingJobData?.tckCtTripList?.[0]?.trId;
  const tLocID = ongoingJobData?.tckCtTripList?.[0]?.tckCtTripLocationByTrFrom?.tlocId;


  const [cargoData, setCargoData] = useState<any | undefined>();

  const cargoType= cargoData?.[0]?.tckCtTripList?.[0]?.tckCtTripCargoFmList?.[0]?.tckCtMstCargoType?.crtypName;
  const cargoLength = convertToString(cargoData?.[0]?.tckCtVeh?.vhLength);
  const cargoWeight = convertToString(cargoData?.[0]?.tckCtVeh?.vhWeight);
  const cargoWidth = convertToString(cargoData?.[0]?.tckCtVeh?.vhWidth);
  const cargoVolume = convertToString(cargoData?.[0]?.tckCtVeh?.vhVolume);
  const cargoHeight = convertToString(cargoData?.[0]?.tckCtVeh?.vhHeight);
  const cargoDesc= cargoData?.[0]?.tckCtTripList?.[0]?.tckCtTripCargoFmList?.[0]?.cgCargoDesc;
  const cargoInst= cargoData?.[0]?.tckCtTripList?.[0]?.tckCtTripCargoFmList?.[0]?.cgCargoSpecialInstn;

  const [remarkData, setRemarkData] = useState<any | undefined> ();
  const remarkValue= remarkData?.tlocRemarks;
  const remarkInstr= remarkData?.tlocSpecialInstn;

  async function handleViewCargo(trid:string) {
    console.log(trid);
    setModalCargoDetail(true);
    const url=process.env.EXPO_PUBLIC_BACKEND_URL+"/api/v1/clickargo/clictruck/mobile/trip/cargo?tripId="+trid;
    const detail = await sendRequest(url);
    if (detail){
      setCargoData(detail.data)
      // console.log(detail.data)
      // console.log("cargotype",detail.data);
    }
  };

  async function handleViewRemark(tlocId:string) {
    console.log(tlocId);
    setModalRemark(true);
    const url=process.env.EXPO_PUBLIC_BACKEND_URL+"/api/v1/clickargo/clictruck/mobile/trip/location?tLocId="+tlocId;
    const detail = await sendRequest(url);
    if (detail) {
      setRemarkData(detail.data);
      // console.log("remark data", detail.data);
    }
  };

  const [photoData, setPhotoData] = useState<any[] | undefined>([]);
  const imageSource = {uri: "file:///data/user/0/host.exp.exponent/cache/ExperienceData/%2540anonymous%252Fclictruck-mobile-c2fa2cab-04f2-4853-8bd6-42eba5424937/Camera/3408790e-85dc-4c60-8e82-51b30ecf354a.jpg"}
  // const imagePath = photoData.[0].uri;
  // const imageSource = {uri: imagePath};

  function handleSetViewCamera(v:boolean){
    console.log("handle camera visibility");
    setCameraScreenVisible(v);
  };

  function handlePushPictureData(o: any){
    setPhotoData((prevPhotoData) => [o, ...prevPhotoData ?? []]);
    // console.log("pushdata",o?.uri);
    // console.log("expected result",[o, ...photoData ?? []]);
  }

  function onChangeComment(v:string){
    
  }

  async function handleSubmitPhoto(){
    ///FOR PICKUP
    let prepData = [];

    if (photoData){
      for (const item of photoData){
        const filename = item.uri.split('/').pop();
        const base64data = item.base64;

        prepData.push({name: filename, data: base64data});
      }
    }
    
    const submitData = 
      {
        action: "UPLOAD",
        typeData: "PHOTO_PICKUP",
        ckJobTruck: ongoingJobData,
        listData: prepData,
      }
      // console.log("tckjob?", ongoingJobData);
    // console.log("Submit this",submitData);

    let newCkJobTruck = { ...ongoingJobData};
    newCkJobTruck.tckCtTripList[0].tckCtTripLocationByTrFrom = 
      {
        ...newCkJobTruck.tckCtTripList[0].tckCtTripLocationByTrFrom,
        tlocComment: photoComment ?? ""
      };
    
    const updateData =
      {
        action: "MPICKUP",
        ckJobTruck: newCkJobTruck
      };

    // console.log("job put data",newCkJobTruck.tckCtTripList[0].tckCtTripLocationByTrFrom);

    const submitPhotoUrl = process.env.EXPO_PUBLIC_BACKEND_URL + '/api/v1/clickargo/clictruck/mobile/trip/location/attach';
    const submitPickUpJobUrl = process.env.EXPO_PUBLIC_BACKEND_URL + '/api/v1/clickargo/clictruck/mobile/trip';
    
    const uploadResult = await sendRequest(submitPhotoUrl,"post",submitData);
    if (uploadResult && uploadResult.status === "SUCCESS"){
      //// IF PHOTO UPLOAD SUCCESS THEN UPDATE JOBTRUCK
      
        const pickupResult = await sendRequest(submitPickUpJobUrl,"put",updateData);
        if (pickupResult){
          setOngoingJobData(pickupResult.data);
          // console.log(pickupResult);
          // console.log("pik ap sukses");
          setPhotoData([]);
          setModalPhotoUpload(false);
          setModalContinueJob(true);
        }
    }
  }


  if(noData){
    return(
      <ScrollView 
        style={{marginTop:100, backgroundColor: "#eeeeee", paddingHorizontal: horizontalScale(15)}}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        >
      <View style={{flex:1, alignItems:'center',justifyContent:'center',borderRadius: 10, paddingVertical:10}}>
          <FontAwesome name="warning" size={moderateScale(20)} />
          <Text>There is no ongoingJob.{"\n"}Please start a new job{"\n"}or resume paused job.</Text>
      </View>
      </ScrollView>
    )
  }

  const backImg:string = "../../assets/images/map-of-the-city-center-gps-map-navigator-concept-vector-illustration-eps-10-2M80MGG.jpg"

  return (
  <>
    <ImageBackground style={[styles.container, {marginTop: useBottomTabBarHeight()}]} source={require(backImg)} resizeMode='stretch' blurRadius={5}>
      <View style={styles.buttonBar}>
      <ScrollView horizontal={true} >
      
          <Pressable style={styles.buttonItem} onPress={() => handleViewCargo(tripId)}>
            <View style={styles.buttonItemIcon}>
            <Image source={require("../../assets/images/button/delivery-box.png")} resizeMode='contain' style={styles.buttonItemIconSize} />
            </View>
            <Text style={styles.buttonIconLabel}>Cargo</Text>
          </Pressable>
          <Pressable style={styles.buttonItem} onPress={() => handleViewRemark(tLocID)}>
            <View style={styles.buttonItemIcon}>
            <Image source={require("../../assets/images/button/post-it.png")} resizeMode='contain' style={styles.buttonItemIconSize} />
            </View>
            <Text style={styles.buttonIconLabel}>Remark</Text>
          </Pressable>
          <Pressable style={styles.buttonItem} onPress={() => setModalPhotoUpload(true)}>
            <View style={styles.buttonItemIcon}>
            <Image source={require("../../assets/images/button/forklift.png")} resizeMode='contain' style={styles.buttonItemIconSize} />
            </View>
            <Text style={styles.buttonIconLabel}>Pickup</Text>
          </Pressable>
          <Pressable style={styles.buttonItem} onPress={() => setModalStartDelivery(true)}>
            <View style={styles.buttonItemIcon}>
            <Image source={require("../../assets/images/button/fast-delivery.png")} resizeMode='contain' style={styles.buttonItemIconSize} />
            </View>
            <Text style={styles.buttonIconLabel}>Deliver</Text>
          </Pressable>
          <Pressable style={styles.buttonItem} onPress={() => setModalPhotoUpload(true)}>
            <View style={styles.buttonItemIcon}>
            <Image source={require("../../assets/images/button/container.png")} resizeMode='contain' style={styles.buttonItemIconSize} />
            </View>
            <Text style={styles.buttonIconLabel}>Drop Off</Text>
          </Pressable>
      </ScrollView>
      </View>
      
      <ScrollView 
          style={{width:'100%'}} 
          contentContainerStyle={{alignItems:'center'}} 
          showsVerticalScrollIndicator={false}
          refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
          >
      <View style={styles.listContainer}>
        
        <View style={styles.listTitle}>
          <View>
              <TitleText>{jobId}</TitleText>
          </View>
          <View style={{right:0}}>
              <Text>{jobType}</Text>
          </View>
        </View>
        <View style={{marginBottom: 10}}>
          <Text>{displayDate(timePickUp)} (PickUp)</Text>
          {/* <Text>{displayDate(timeDropOff)} (DropOff)</Text> */}
        </View>
      </View>
      <View style={styles.listContainer}>
        <View style={styles.locTitle}>
          <Text>Pickup Location</Text>
        </View>
        <View style={styles.locDetailContainer}>
          <View style={styles.locDetailLogo}><MaterialCommunityIcons name="map-marker-outline" color={color.text} size={24}/></View>
          <View style={styles.locDetailContent}><Text>{locFrom}</Text></View>
          <View style={styles.locDetailButton}><ExternalLink
              // style={styles.helpLink}
              href={"https://www.google.com/maps/search/" + locFrom}
            >
              <MaterialCommunityIcons name="map-outline" size={24} color={color.text} />
          </ExternalLink></View>
        </View>
      </View>
      <View style={styles.listContainer}>
        <View style={styles.locTitle}>
          <Text>{t("other:jobTab.dropOffLocation")}</Text>
        </View>
        <View style={styles.locDetailContainer}>
        <View style={styles.locDetailLogo}><MaterialCommunityIcons name="map-marker-outline" color={color.text} size={24}/></View>
          <View style={styles.locDetailContent}><Text>{locTo}</Text></View>
          <View style={styles.locDetailButton}>
            
           
            <ExternalLink
              // style={styles.helpLink}
              href={"https://www.google.com/maps/search/" + locTo}
            >
              <MaterialCommunityIcons name="map-outline" size={24} color={color.text} />
          </ExternalLink>
          </View>
        </View>
      </View>
      <View style={[styles.timingContainer, {backgroundColor: color.lightBlue}]}>
            <View style={styles.timingItem}>
                <MaterialCommunityIcons name="play" color={color.text} size={20}/>
                <Text> {displayDate(timeStart)}</Text>
            </View>
            <View style={styles.timingItem}>
                <MaterialCommunityIcons name="arrow-right-bold-circle-outline" color={color.text} size={20}/>
                <Text> {displayDate(timeDeliver)}</Text>
            </View>
            <View style={styles.timingItem}>
                <MaterialCommunityIcons name="upload" color={color.text} size={20}/>
                <Text> {displayDate(timePickedup)}</Text>
            </View>
            <View style={styles.timingItem}>
                <MaterialCommunityIcons name="download" color={color.text} size={20}/> 
                <Text> {displayDate(timeJobDone)}</Text>
            </View>
      </View>
      </ScrollView>
      
      {/* modal photos upload */}
      <ClictruckModal show={modalPhotoUpload}>
            <View style={styles.modalHeader}>
                <FontAwesome name="picture-o" size={moderateScale(30)} />
                <Text style={styles.textModalHeader}>Photos Upload</Text>
                <Pressable
                    onPress={() => {
                        setModalPhotoUpload(false);
                        // setChangePassState(passDef);
                    }}
                >
                    <FontAwesome name="times" size={moderateScale(30)} />
                </Pressable>
            </View>
            <View style={styles.modalBody}>
                {/* <View style={{flexDirection: 'row', alignItems: 'center'}}>
                    <Text style={styles.textModalBody}>Source : </Text>
                    <Pressable
                        style={[styles.confirmButton,{marginTop:0}]}
                        onPress={() => {
                            setModalVisible(false);
                            router.push("/jobs/photoUpload");
                        }}
                    >
                        <FontAwesome
                            name="camera"
                            size={moderateScale(15)}
                            color={color.text}
                        />
                        <Text style={styles.textModalBody}>Camera</Text>
                    </Pressable>
                </View> */}
                <RText style={{marginBottom:-10}}>Photos</RText>
                <ScrollView style={styles.photoListContainer} contentContainerStyle={styles.photoListContainerContent} horizontal={true}>   
                    {/* <View style={styles.photoListItem}>

                    </View> */}
                    {/* <ImageBackground source={imageSource} resizeMode='cover' style={styles.photoListItem}/> */}
                    {photoData && photoData.map((photo, index) => (
                      <ImageBackground
                        key={index} // Make sure to use a unique key for each element
                        source={{ uri: photo.uri }}
                        resizeMode='cover'
                        style={styles.photoListItem}
                      />
                    ))}
                    <Pressable 
                      style={styles.photoListItem} 
                      onPress={() => {
                        // setModalVisible(false);
                        // router.push("/jobs/photoUpload");
                        setCameraScreenVisible(true);
                      }}
                    >
                    <FontAwesome
                            name="camera"
                            size={moderateScale(15)}
                            color={"darkgrey"}
                        />
                    </Pressable>
                </ScrollView>

                {/* <View><Text>Comments (Optional) : </Text></View>
                <View style={{minHeight:50,borderWidth:1,borderColor:color.text}}> */}
                <GliTextInput
                  label="Comments (Optional)"
                  multiline={true}
                  onChangeText={(t)=>setPhotoComment(t)}
                />

                {/* </View> */}
                <View style={{ alignItems: "flex-end" }}>
                    <Pressable
                        style={styles.confirmButton}
                        onPress={
                          // () => {
                          // setModalContinueJob(true);
                          // setModalVisible(false);
                          // }
                          handleSubmitPhoto
                        }
                    >
                        <FontAwesome
                            name="cloud-upload"
                            size={moderateScale(15)}
                            color={"blue"}
                        />
                        <Text style={styles.textModalBody}>Submit</Text>
                    </Pressable>
                </View> 
            </View>
        </ClictruckModal>

        {/* modal for continue job */}
        <ClictruckModal show={modalContinueJob}>
            <View style={styles.modalHeader}>
                <FontAwesome name="picture-o" size={moderateScale(30)} />
                <Text style={styles.textModalHeader}>Photos Upload</Text>
                <Pressable
                    onPress={() => {
                        setModalContinueJob(false);
                        // setChangePassState(passDef);
                    }}
                >
                    <FontAwesome name="times" size={moderateScale(30)} />
                </Pressable>
            </View>
            <View style={styles.modalBody}>
                <Text>Photos for the pickup has been uploaded. Please proceed with the following options.</Text>
                <View style={{flexDirection:'row', flexWrap:'wrap', justifyContent:'space-evenly'}}>
                  <Pressable
                        style={[styles.confirmButton]}
                        onPress={() => {
                          setModalContinueJob(true);
                          setModalPhotoUpload(false);
                        }}
                    >
                        <FontAwesome
                            name="cloud-upload"
                            size={moderateScale(15)}
                            color={"blue"}
                        />
                        <Text style={[styles.textModalBody,{flexWrap:'wrap'}]}>Redo{'\n'}Upload</Text>
                  </Pressable>
                  <Pressable
                        style={styles.confirmButton}
                        onPress={() => {
                          setModalContinueJob(true);
                          setModalPhotoUpload(false);
                        }}
                    >
                        <FontAwesome
                            name="arrow-circle-o-right"
                            size={moderateScale(15)}
                            color={"blue"}
                        />
                        <Text style={styles.textModalBody}>Deliver{'\n'}Cargo</Text>
                  </Pressable>
                  <Pressable
                        style={styles.confirmButton}
                        onPress={() => {
                          setModalContinueJob(true);
                          setModalPhotoUpload(false);
                        }}
                    >
                        <FontAwesome
                            name="list-alt"
                            size={moderateScale(15)}
                            color={"blue"}
                        />
                        <Text style={styles.textModalBody}>New{'\n'}Job</Text>
                  </Pressable>
                  <Pressable
                        style={styles.confirmButton}
                        onPress={() => {
                          setModalContinueJob(true);
                          setModalPhotoUpload(false);
                        }}
                    >
                        <FontAwesome
                            name="list-alt"
                            size={moderateScale(15)}
                            color={"blue"}
                        />
                        <Text style={styles.textModalBody}>Pause{'\n'}Job</Text>
                  </Pressable>
                </View>
            </View>

        </ClictruckModal>
        
        {/* modal for deliver */}
        <ClictruckModal show={modalStartDelivery}>
            <View style={styles.modalHeader}>
                <MaterialCommunityIcons name="arrow-right-bold-circle-outline" size={moderateScale(30)} />
                <Text style={styles.textModalHeader}>Start Cargo Delivery</Text>
                <Text></Text>
            </View>
            <View style={styles.modalBody}>
              <Text>
                Please press yes to start cargo delivery.{"\n"}
                Note that recipient will receive notification for this job.
              </Text>
            </View>
            <View style={styles.modalBody}>
              <Text>Confirm Start</Text>
              <View style={{ flexDirection: "row", justifyContent: "space-evenly" }}>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={() => {
                                  setModalStartDelivery(false);
                                }}
                            >
                                <FontAwesome name="times" size={moderateScale(15)} color={"red"} />
                                <Text style={styles.textModalBody}>No</Text>
                            </Pressable>
                            <Pressable
                                style={styles.confirmButton}
                                onPress={()=>{}}
                            >
                                <FontAwesome
                                    name="check"
                                    size={moderateScale(15)}
                                    color={"green"}
                                />
                                <Text style={styles.textModalBody}>Confirm</Text>
                            </Pressable>
                        </View>
            </View>
        </ClictruckModal>

        {/* modal for remark */}
        <ClictruckModal show={modalRemark}>
            <View style={styles.modalHeader}>
                <FontAwesome name="picture-o" size={moderateScale(30)} />
                <Text style={styles.textModalHeader}>Remarks</Text>
                <Pressable
                    onPress={() => {
                        setModalRemark(false);
                        // setChangePassState(passDef);
                    }}
                >
                    <FontAwesome name="times" size={moderateScale(30)} />
                </Pressable>
            </View>
            <View style={styles.modalBody}>
              <GliTextInput 
                label="Remarks" 
                colorLabel={color.text} 
                multiline={true}
                editable={false}
                value={remarkValue}
              />
              <GliTextInput 
                label={t("other:header.specialInstructions")}
                colorLabel={color.text} 
                multiline={true}
                editable={false}
                value={remarkInstr}
              />
            </View>
        </ClictruckModal>

        {/* modal for cargo detail */}
        <ClictruckModal show={modalCargoDetail}>
            <View style={styles.modalHeader}>
                <FontAwesome name="picture-o" size={moderateScale(30)} />
                <Text style={styles.textModalHeader}>Cargo Details</Text>
                <Pressable
                    onPress={() => {
                        setModalCargoDetail(false);
                    }}
                >
                    <FontAwesome name="times" size={moderateScale(30)} />
                </Pressable>
            </View>
            <View style={[styles.modalBody,{flexDirection:'row', flexWrap:'wrap'}]}>
              <View style={styles.modalHalf}>
                <GliTextInput 
                label="Type"
                value={cargoType}
                editable={false}
                />
              </View>
              <View style={styles.modalHalf}>
              <GliTextInput 
                label="Length"
                editable={false}
                value={cargoLength}
                />
              </View>
              <View style={styles.modalHalf}>
              <GliTextInput 
                label="Weight"
                editable={false}
                value={cargoWeight}
                />
              </View>
              <View style={styles.modalHalf}>
              <GliTextInput 
                label="Width"
                editable={false}
                value={cargoWidth}
                />
              </View>
              <View style={styles.modalHalf}>
              <GliTextInput 
                label="Volumetric"
                editable={false}
                value={cargoVolume}
                />
              </View>
              <View style={styles.modalHalf}>
              <GliTextInput 
                label="Height"
                editable={false}
                value={cargoHeight}
                />
              </View>
              <GliTextInput 
                label="Description"
                editable={false}
                multiline={true}
                value={cargoDesc}
              />
              <GliTextInput
                label={t("jobHistory:popup.cargo.remarks")}
                editable={false}
                multiline={true}
                value={cargoInst}
              />
            </View>
        </ClictruckModal>
    </ImageBackground>
    {cameraScreenVisible &&
    <Modal visible={cameraScreenVisible}>
      
      <PhotoUploadScreen setVisible={handleSetViewCamera} pushData={handlePushPictureData} />
      
    </Modal>
    }
  </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'flex-start',
    // backgroundColor: '#eeeeee',
    // marginTop:100,
  },
  buttonBar: {
    width: '97%',
    // flexDirection: 'row',
    // justifyContent: 'space-between',
    // overflowX:'scroll',
    marginTop: 30,
    marginBottom: 10,
    backgroundColor: "transparent",
    alignItems: 'center',
  },
  buttonItem:{
    // width: 120, //change to 30%
    // height: 50,
    minWidth: horizontalScale(80),
    minHeight: verticalScale(70),
    alignItems: 'center',
    justifyContent: 'center',
    // borderColor: 'grey',
    // borderWidth: 1,
    // borderRadius: 8,
    
  },
  buttonItemIcon:{
    // height:moderateScale(50),
    // width:moderateScale(50),
    padding:10,
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    borderRadius: 50,
    //android shadow
    elevation:10,
    //ios shadow
    shadowColor: "#000000",
    shadowOpacity: 0.8,
    shadowRadius: 2,
    shadowOffset: {
      height: 1,
      width: 1,
    },
    marginBottom:5,
  },
  buttonItemIconSize:{
    height:moderateScale(40),
    width:moderateScale(40),
  },
  buttonIconLabel:{
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    padding: 3,
    borderRadius: 8,
  },
  listContainer:{
    width: '97%',
    borderColor:"#ffffff",
    borderWidth: 1,
    borderRadius: 10,
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    overflow: "hidden",
    alignItems: 'center',
    marginBottom: 15,
    //android shadow
    elevation:10,
    //ios shadow
    shadowColor: "#000000",
    shadowOpacity: 0.8,
    shadowRadius: 2,
    shadowOffset: {
      height: 1,
      width: 1,
    }
    // opacity:0.5
  },
  listTitle:{
    width: '95%',
    padding: 5,
    flexDirection:'row',
    justifyContent: 'space-between',
    // borderBottomWidth: 1,
    // borderColor: 'grey',
    marginBottom:10,
    backgroundColor: "transparent",
  },
  locTitle:{
    width: '95%',
    padding: 5,
    // flexDirection: 'row',
    // alignItems:'flex-start',
    backgroundColor: "transparent",
  },
  locDetailContainer:{
    width: '95%',
    flexDirection: 'row',
    // backgroundColor: 'pink',
    marginBottom: 10,
    backgroundColor: "transparent",
  },
  locDetailLogo:{
    width: '20%',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: "transparent",
  },
  locDetailContent:{
    width: '60%',
    backgroundColor: "transparent",
  },
  locDetailButton:{
    width: '20%',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: "transparent",
  },
  timingContainer:{
    width:'100%',
    // backgroundColor: 'pink',
    padding:5,
    paddingTop:10,
    flexDirection:'row',
    justifyContent: 'space-between',
    // alignItems: 'center',
    flexWrap:'wrap',
    marginBottom: 10,
  },
  timingItem:{
    width:'50%',
    paddingLeft:10,
    marginBottom:10,
    backgroundColor: 'rgba(52, 52, 52, alpha)',
    flexDirection: 'row',
    alignItems: 'center',
  },
  // modal
  confirmButton: {
    flexDirection: "row",
    justifyContent: "space-evenly",
    alignItems: "center",
    width: "35%",
    minHeight: 30,
    borderWidth: 1,
    marginTop: 15,
    borderRadius: 5,
  },
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
      fontFamily: "pop-med",
      textAlign: "left",
  },
  textModalBody: {
      fontSize: moderateScale(14),
      fontFamily: "pop-reg",
  },
  modalHalf:{
    width:"50%",
    paddingHorizontal:5,
  },
  photoListContainer:{
    minHeight:verticalScale(80),
    borderWidth:1,
    borderColor:"#ccc", 
    marginVertical:10,
    borderRadius: moderateScale(8),
    // flexDirection:'row',
    // alignItems:'center',
  },
  photoListContainerContent:{
    alignItems:'center',
  },
  photoListItem:{
    height:moderateScale(50),
    width:moderateScale(50),
    backgroundColor:'#eee',
    alignItems:'center',
    justifyContent:'center',
    margin:5,
    borderRadius: moderateScale(8),
  }

});
