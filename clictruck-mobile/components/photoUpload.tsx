import { Camera, CameraType, FlashMode } from 'expo-camera';
import { useState } from 'react';
import { Button, StyleSheet, Text, TouchableOpacity, View, Pressable, ImageBackground } from 'react-native';
import { Entypo, MaterialCommunityIcons, FontAwesome } from '@expo/vector-icons';
import { router } from 'expo-router';

interface CamScreenProps {
  setVisible: (visible: boolean) => void;
  pushData: (d: object) => void;
}

export default function PhotoUploadScreen({ setVisible, pushData }: CamScreenProps) {
  console.log("photo upload screen rendered")

  

  let cameraRef: any;
  
  const [permission, requestPermission] = Camera.useCameraPermissions();
  const [type, setType] = useState(CameraType.back);
  const [flash, setFlash] = useState(FlashMode.auto);
  const [ratio, setRatio] = useState<string>("16:9");
  const [capturedImage, setCapturedImage] = useState<any>(null);
  // const [camOptions, setCamOptions] = useState({base64:true})
  const [previewVisible, setPreviewVisible] = useState(false);

  if (!permission) {
    // Camera permissions are still loading
    return <View />;
  }

  if (!permission.granted) {
    // Camera permissions are not granted yet
    return (
      <View style={styles.container}>
        <Text style={{ textAlign: 'center' }}>We need your permission to show the camera</Text>
        <Button onPress={requestPermission} title="grant permission" />
      </View>
    );
  }
  
  function handleClose() {
    // router.replace({pathname: '/(tabs)', params: {paramOng: "test"} })
    setVisible(false);
  }

  function toggleCameraType() {
    setType(current => (current === CameraType.back ? CameraType.front : CameraType.back));
  }

  function toggleCameraFlash() {
    flash === FlashMode.auto ? setFlash(FlashMode.on) :
    flash === FlashMode.on ? setFlash(FlashMode.torch) :
    flash === FlashMode.torch ? setFlash(FlashMode.off) :
    setFlash(FlashMode.auto)
  }

  async function takePicture() {
    if (!Camera) return
    // const photo = await cameraRef.takePictureAsync();
    const photo = await cameraRef.takePictureAsync({base64:true, quality:0});
    // console.log(photo)
    setCapturedImage(photo)
    setPreviewVisible(true)
  }

  function rejectPhoto(){
    setPreviewVisible(false);
  }

  function acceptPhoto(){
    // console.log("accept photo")
    pushData(capturedImage);
    handleClose();
  }

  function addPhoto(){

  }

  const  CameraPreview=(photo:any)=>{
    // console.log('sdsfds', photo)
    // console.log('photo uri', photo.uri)
    return (
      <View
        style={{
          backgroundColor: 'transparent',
          flex: 1,
          width: '100%',
          height: '100%',
        }}
      >
        <ImageBackground
          source={{uri: photo.photo.uri}}
          style={{
            flex: 1,
            justifyContent:'flex-end',
          }}
        >
          <View style={styles.buttonContainer}>
            {/* <Pressable onPress={addPhoto}>
              <FontAwesome name="plus" size={24} color="white" />
            </Pressable> */}
            <View/>
            <Pressable onPress={acceptPhoto}>
              <FontAwesome name="check" size={30} color="white" />
            </Pressable>
            <Pressable onPress={rejectPhoto}>
              <FontAwesome name="remove" size={24} color="white" />
            </Pressable>
          </View>
        </ImageBackground>  
      </View>
    )
  }

  return (
    <View style={styles.container}>
      {previewVisible && capturedImage ?
      <CameraPreview photo={capturedImage} />
      :
      <Camera 
        style={styles.camera} 
        type={type} 
        flashMode={flash} 
        ratio={ratio} 
        ref={(r) => {cameraRef = r}}
      >
        <View style={styles.buttonContainer}>
          <Pressable onPress={toggleCameraFlash}>
            {
              flash === FlashMode.auto  ? <MaterialCommunityIcons name="flash-auto" size={24} color="white" /> :
              flash === FlashMode.on    ? <MaterialCommunityIcons name="flash" size={24} color="white" /> :
              flash === FlashMode.torch ? <MaterialCommunityIcons name="flash-alert" size={24} color="white" /> :
                                          <MaterialCommunityIcons name="flash-off" size={24} color="white" />
            }
          </Pressable>
          <Pressable onPress={takePicture}>
            <Entypo name="circle" size={40} color="white" />
          </Pressable>
          <Pressable onPress={toggleCameraType}>
            <Entypo name="cycle" size={24} color="white" />
          </Pressable>
        </View>
        <View style={styles.topButtonContainer}>
          <Pressable onPress={toggleCameraType}>
            <Entypo name="cycle" size={24} color="white" />
          </Pressable>
          <Pressable onPress={handleClose}>
            <Entypo name="cross" size={24} color="white" />
          </Pressable>
        </View>
      </Camera>
      }
    </View>
  );
}



const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  camera: {
    flex: 1,
    justifyContent:'flex-end',
  },
  buttonContainer: {
    // flex: 1,
    flexDirection: 'row',
    backgroundColor: 'transparent',
    marginBottom: '20%',
    justifyContent: 'space-evenly',
    alignItems: 'center',
    // margin: 64,
  },
  topButtonContainer:{
    flexDirection: 'row',
    position:'absolute',
    top:0,
    width:"100%",
    // backgroundColor: "pink",
    justifyContent: 'space-between',
    padding:20,
  },
  button: {
    // flex: 1,
    alignSelf: 'flex-end',
    alignItems: 'center',
    // backgroundColor: 'pink',
  },
  text: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
  },
});
