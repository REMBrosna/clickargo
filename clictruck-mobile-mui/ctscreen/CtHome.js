import React, { useEffect } from 'react';
import { StyleSheet, Dimensions, ScrollView } from 'react-native';
import { Button, Block, Text, Input, theme } from 'galio-framework';
import { Icon, Product, Tabs } from '../components';
import homeImages from '../constants/images/home';
import NewJobs from './NewJobs';
import PausedJobs from './PauseJobs';
import OngoingJob from './OngoingJob'
import { useFocusEffect, useNavigation } from '@react-navigation/native';
const { width } = Dimensions.get('screen');




const CtHomeScreen = (props) => {
    const navigation = useNavigation();
    const { route } = props;

    const tabId = route.params?.tabId;
    const tabContents = {
        new: <NewJobs {...props} />,
        active: <OngoingJob {...props}/>,
        paused: <PausedJobs {...props} />
    }

    const content = tabId ? tabContents[tabId] : tabContents.active;

    // const renderSearch = () => {
    //     const { navigation } = props;
    //     const iconContent = <Icon size={16} color={theme.COLORS.MUTED} name="zoom-in" family="material" />

    //     return (
    //         <Input
    //             right
    //             color="black"
    //             style={styles.search}
    //             iconContent={iconContent}
    //             placeholder="What are you looking for?"
    //             onFocus={() => navigation.navigate('Search')}
    //         />
    //     )
    // }

    const renderTabs = () => {
        console.log("CT Home Screen render Tabs", props);
        const { tabs, tabIndex } = props;
        const defaultTab = tabs && tabs[0] && tabs[0].id;

        if (!tabs) return null;

        return (
            <Tabs
                data={tabs || []}
                initialIndex={tabIndex || defaultTab}
                onChange={id => navigation.setParams({ tabId: id })}
            />
        )
    }

    const renderContents = () => {
        return (
            <>
                {content}
            </>

            // <ScrollView
            //     showsVerticalScrollIndicator={false}
            //     contentContainerStyle={styles.products}>
            //     <Block flex>
            //         <Product product={homeImages[0]} horizontal />
            //         <Block flex row>
            //             <Product product={homeImages[1]} style={{ marginRight: theme.SIZES.BASE }} />
            //             <Product product={homeImages[2]} />
            //         </Block>
            //         <Product product={homeImages[3]} horizontal />
            //         <Product product={homeImages[4]} full />
            //     </Block>
            // </ScrollView>
        )
    }

    // useEffect(() => {
    //     if (route?.params?.tabId)
    //         navigation.navigate(route?.params?.tabId);
    // }, [route])

    useFocusEffect(
        React.useCallback(() => {
            navigation.closeDrawer();
        }, [])
      );

    return (
        <Block flex center style={styles.home}>
            {renderContents()}
            {/* {renderProducts()} */}
            {/* {renderTabs()} */}
        </Block>
    );

}

const styles = StyleSheet.create({
    home: {
        width: width,
    },
    search: {
        height: 48,
        width: width - 32,
        marginHorizontal: 16,
        borderWidth: 1,
        borderRadius: 3,
    },
    header: {
        backgroundColor: theme.COLORS.WHITE,
        shadowColor: theme.COLORS.BLACK,
        shadowOffset: {
            width: 0,
            height: 2
        },
        shadowRadius: 8,
        shadowOpacity: 0.2,
        elevation: 4,
        zIndex: 2,
    },
    tabs: {
        marginBottom: 24,
        marginTop: 10,
        elevation: 4,
    },
    tab: {
        backgroundColor: theme.COLORS.TRANSPARENT,
        width: width * 0.50,
        borderRadius: 0,
        borderWidth: 0,
        height: 24,
        elevation: 0,
    },
    tabTitle: {
        lineHeight: 19,
        fontWeight: 300
    },
    divider: {
        borderRightWidth: 0.3,
        borderRightColor: theme.COLORS.MUTED,
    },
    products: {
        width: width - theme.SIZES.BASE * 2,
        paddingVertical: theme.SIZES.BASE * 2,
    },
});

export default CtHomeScreen;