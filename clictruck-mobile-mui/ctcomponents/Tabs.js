import { Text } from "galio-framework";
import React from "react";
import { View, StyleSheet } from "react-native";
import { FontAwesome,MaterialCommunityIcons } from "@expo/vector-icons";
import { horizontalScale, moderateScale, verticalScale } from "../constants/metrics";

const Tabs = ({icon}) => {

    return (
      <View style={styles.container}>
        <View style={styles.container2}>
          <View style={styles.header1}>
            <Text style={[styles.h5Style,{fontWeight:'bold'}]}>New</Text>
            <Text style={styles.h5Style}>0</Text>
          </View>
          <View style={styles.header2}>
            <Text style={[styles.h5Style,{fontWeight:'bold'}]}>Paused</Text>
            <Text style={styles.h5Style}>0</Text>
          </View>
        </View>
        <View style={styles.circle} >
          <MaterialCommunityIcons name={icon} size={moderateScale(40)} style={{color:'#75CCF5'}}/>
        </View>
      </View>
    );
}


export default Tabs;

const styles = StyleSheet.create({
  container: {
    flex: 0.13,
    backgroundColor: 'white',
    alignItems: 'center',
    paddingBottom: 5
  },
  container2: {
    flexDirection: 'row',
    height: 70, 
    backgroundColor: 'red',
  },
  header1: {
    flex: 1,
    backgroundColor: '#75CCF5',
    borderWidth: 2,
    borderTopColor: '#75CCF5',
    borderBottomColor: '#EFF2F1',
    borderLeftColor: '#75CCF5',
    borderRightColor: '#EFF2F1',
    justifyContent: 'center',
  },
  header2: {
    flex: 1,
    backgroundColor: '#75CCF5',
    borderWidth: 2,
    borderTopColor: '#75CCF5',
    borderBottomColor: '#EFF2F1',
    borderLeftColor: '#75CCF5',
    borderRightColor: '#75CCF5',
    justifyContent: 'center',
  },
  circle: {
    height: 70,
    width: 70,
    borderRadius: 50,
    position: 'absolute',
    top: 15,
    backgroundColor: 'white',
    borderWidth: 2,
    borderColor: '#EFF2F1',
    justifyContent: 'center',
    alignItems: 'center',
  },
  h5Style: {
    alignSelf: 'center',
    fontSize: 16,
    fontWeight: "600",
    color: "white"
  },
  subtitle: {
    color: "#999",
  },
});