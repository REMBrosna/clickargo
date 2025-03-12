import React from 'react';
import { View, TouchableOpacity, Text, StyleSheet } from 'react-native';

interface Route {
  key: string;
  name: string;
}

interface Descriptor {
  options: { tabBarLabel?: string; title?: string };
}

interface CustomTabBarProps {
  state: { routes: Route[] };
  descriptors: { [key: string]: Descriptor };
  navigation: any; // Replace with the appropriate type of your navigation object
}

const CustomTabBar: React.FC<CustomTabBarProps> = ({ state, descriptors, navigation }) => {
  return (
    <View style={styles.container}>
      {state.routes.map((route, index) => {
        const { options } = descriptors[route.key];
        const label =
          options.tabBarLabel !== undefined
            ? options.tabBarLabel
            : options.title !== undefined
            ? options.title
            : route.name;

        const isMiddle = index === 1; // Assuming middle tab is the second one (index 1)

        return (
          <TouchableOpacity
            key={route.key}
            onPress={() => navigation.navigate(route.name)}
            style={[
              styles.tabButton,
              isMiddle && styles.middleTabButton,
            ]}
          >
            {isMiddle ? (
              <View style={styles.middleButton}>
                <Text style={styles.middleButtonText}>{label}</Text>
              </View>
            ) : (
              <Text>{label}</Text>
            )}
          </TouchableOpacity>
        );
      })}
    </View>
  );
};

const styles = StyleSheet.create({
    container: {
      flexDirection: 'row',
      justifyContent: 'space-around',
      width: '100%',
      backgroundColor: '#fff', // Customize background color if needed
    },
    tabButton: {
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: 10,
    },
    middleTabButton: {
      flex: 0,
    },
    middleButton: {
      width: 50,
      height: 50,
      borderRadius: 25,
      backgroundColor: 'blue', // Customize circle color
      alignItems: 'center',
      justifyContent: 'center',
    },
    middleButtonText: {
      color: '#fff',
      fontSize: 16,
    },
  });
  
export default CustomTabBar;
