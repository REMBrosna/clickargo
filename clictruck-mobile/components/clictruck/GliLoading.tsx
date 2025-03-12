import { Dialog } from "@rneui/themed"
import { ActivityIndicatorProps } from "react-native"

interface GliLoadingProps {
    title: string,
    loadingProps: ActivityIndicatorProps,
    titleStyle: {},
    isVisible: boolean
}

export const GliLoading: React.FC<GliLoadingProps> = ({ title, titleStyle, loadingProps, isVisible }) => {
    return <Dialog isVisible={isVisible}>
        <Dialog.Title title={title} titleStyle={titleStyle} />
        <Dialog.Loading loadingProps={loadingProps} />
    </Dialog>
}