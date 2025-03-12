import { Camera, CameraType, FlashMode } from "expo-camera";
import { useState } from "react";
import {
  Button,
  StyleSheet,
  Text,
  View,
  Pressable,
  ImageBackground,
} from "react-native";
import {
  Entypo,
  MaterialCommunityIcons,
  FontAwesome,
} from "@expo/vector-icons";
import { manipulateAsync } from "expo-image-manipulator";
import * as FileSystem from "expo-file-system";
import * as ImagePicker from "expo-image-picker";

export default function PhotoUploadScreen({ setVisible, pushData }) {
  let cameraRef;

  const [permission, requestPermission] = Camera.useCameraPermissions();
  const [type, setType] = useState(CameraType.back);
  const [flash, setFlash] = useState(FlashMode.auto);
  const [ratio, setRatio] = useState("16:9");
  const [capturedImage, setCapturedImage] = useState(null);
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
        <Text style={{ textAlign: "center" }}>
          We need your permission to show the camera
        </Text>
        <Button onPress={requestPermission} title="grant permission" />
      </View>
    );
  }

  function handleClose() {
    // router.replace({pathname: '/(tabs)', params: {paramOng: "test"} })
    setVisible(false);
  }

  function toggleCameraType() {
    setType((current) =>
      current === CameraType.back ? CameraType.front : CameraType.back
    );
  }

  function toggleCameraFlash() {
    flash === FlashMode.auto
      ? setFlash(FlashMode.on)
      : flash === FlashMode.on
        ? setFlash(FlashMode.torch)
        : flash === FlashMode.torch
          ? setFlash(FlashMode.off)
          : setFlash(FlashMode.auto);
  }

  async function takePicture() {
    if (!cameraRef) return;
    const photo = await cameraRef.takePictureAsync({
      base64: true,
      quality: 0,
    });

    const { size } = await FileSystem.getInfoAsync(photo?.uri);
    console.log(">>>>>>>> BEFORE: ", size);

    if (size / 1000 > 500) {
      const manipulateResult = await manipulateAsync(photo.uri, [], {
        compress: 0.25,
        base64: true,
      });
      const result = await FileSystem.getInfoAsync(manipulateResult?.uri);
      console.log("<<<<<<<<< AFTER: ", result?.size);
      setCapturedImage(manipulateResult);
    } else {
      setCapturedImage(photo);
    }

    setPreviewVisible(true);
  }

  function rejectPhoto() {
    setPreviewVisible(false);
  }

  function acceptPhoto(imgValue) {
    pushData(capturedImage || imgValue);
    handleClose();
  }

  const pickImageFromGallery = async () => {
    const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!permissionResult.granted) {
      alert("Permission to access gallery is required!");
      return;
    }

    const pickerResult = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      base64: true,
    });

    if (pickerResult.canceled) {
      return;
    }

    const image = pickerResult.assets ? pickerResult.assets[0] : pickerResult;
    const imageUri = image.uri;

    // Get file info
    const { size } = await FileSystem.getInfoAsync(imageUri);

    // Compress if necessary
    if (size / 1000 > 500) {
      const compressedImage = await manipulateAsync(imageUri, [], {
        compress: 0.25,
        base64: true,
      });
      setCapturedImage(compressedImage);
      acceptPhoto(compressedImage)
    } else {
      setCapturedImage(image);
      acceptPhoto(image)
    }
    setPreviewVisible(true);
  };

  const CameraPreview = (photo) => {
    return (
      <View
        style={{
          backgroundColor: "transparent",
          flex: 1,
          width: "100%",
          height: "100%",
        }}
      >
        <ImageBackground
          source={{ uri: photo.photo.uri }}
          style={{
            flex: 1,
            justifyContent: "flex-end",
          }}
        >
          <View style={styles.buttonContainer}>
            <View />
            <Pressable onPress={acceptPhoto}>
              <FontAwesome name="check" size={30} color="white" />
            </Pressable>
            <Pressable onPress={rejectPhoto}>
              <FontAwesome name="remove" size={24} color="white" />
            </Pressable>
          </View>
        </ImageBackground>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      {previewVisible && capturedImage ? (
        <CameraPreview photo={capturedImage} />
      ) : (
        <Camera
          style={styles.camera}
          type={type}
          flashMode={flash}
          ratio={ratio}
          ref={(r) => {
            cameraRef = r;
          }}
        >
          <View style={styles.buttonContainer}>
            <Pressable onPress={toggleCameraFlash}>
              {flash === FlashMode.auto ? (
                <MaterialCommunityIcons
                  name="flash-auto"
                  size={24}
                  color="white"
                />
              ) : flash === FlashMode.on ? (
                <MaterialCommunityIcons name="flash" size={24} color="white" />
              ) : flash === FlashMode.torch ? (
                <MaterialCommunityIcons
                  name="flash-alert"
                  size={24}
                  color="white"
                />
              ) : (
                <MaterialCommunityIcons
                  name="flash-off"
                  size={24}
                  color="white"
                />
              )}
            </Pressable>
            <Pressable onPress={takePicture}>
              <Entypo name="circle" size={40} color="white" />
            </Pressable>
            <Pressable onPress={pickImageFromGallery}>
              <Entypo name="image" size={35} color="white" />
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
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
  },
  camera: {
    flex: 1,
    justifyContent: "flex-end",
  },
  buttonContainer: {
    // flex: 1,
    flexDirection: "row",
    backgroundColor: "transparent",
    marginBottom: "20%",
    justifyContent: "space-evenly",
    alignItems: "center",
    // margin: 64,
  },
  topButtonContainer: {
    flexDirection: "row",
    position: "absolute",
    top: 30,
    width: "100%",
    // backgroundColor: "pink",
    justifyContent: "space-between",
    padding: 20,
  },
  button: {
    // flex: 1,
    alignSelf: "flex-end",
    alignItems: "center",
    // backgroundColor: 'pink',
  },
  text: {
    fontSize: 24,
    fontWeight: "bold",
    color: "white",
  },
});
