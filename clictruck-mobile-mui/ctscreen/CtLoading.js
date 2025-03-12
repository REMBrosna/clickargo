import { ActivityIndicator, View } from "react-native";
import Dialog from "react-native-dialog";

const CtLoading = ({ title, isVisible, ...rest }) => {
    return <Dialog.Container visible={isVisible}
        {...rest}
        style={{ alignContent: "center", justifyContent: "center" }}>
        <Dialog.Title>{title}</Dialog.Title>
        <Dialog.Description style={{ alignSelf: "center" }}>
            <ActivityIndicator size="large" color="#0872ba" />
        </Dialog.Description>
    </Dialog.Container>

}

export default CtLoading;