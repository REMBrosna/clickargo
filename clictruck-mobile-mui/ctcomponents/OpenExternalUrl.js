import { Block, Button } from "galio-framework";
import React, { useCallback } from "react";
import { Alert, Linking, Pressable } from "react-native";


const OpenExternalUrl = ({ url, children }) => {

    const handlePress = useCallback(async () => {

        // Checking if the link is supported for links with custom URL scheme.
        const supported = await Linking.canOpenURL(url);
        if (supported) {
            // Opening the link with some app, if the URL scheme is "http" the web link should be opened
            // by some browser in the mobile
            await Linking.openURL(url);
        } else {
            Alert.alert(`Don't know how to open this URL: ${url}`);
        }
    }, [url]);

    return <Block>
        {/* <Button onPress={handlePress} color="transparent" style={{ borderWidth: 0 }}> */}
        <Pressable onPress={handlePress} style={{padding:5}}>
            {children}
        </Pressable>
        {/* </Button> */}

    </Block>

}

export default OpenExternalUrl;