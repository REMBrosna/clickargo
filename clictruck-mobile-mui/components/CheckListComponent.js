import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, Dimensions } from 'react-native';
import { Checkbox, Icon } from 'galio-framework';
import { materialTheme } from "../constants";
import { moderateScale } from "../constants/utils";
import { useTranslation } from "react-i18next";

const { height } = Dimensions.get("window");

const CheckListComponent = ({ tripId, itemsList, isDropOff, checkCount, setCheckCount }) => {
    const { t } = useTranslation();
    const [checkListData, setCheckListData] = useState(() =>
        itemsList?.filter(item => !isDropOff || item?.id === tripId)
    );

    useEffect(() => {
        updateCheckCount(checkListData);
    }, []);

    const onCheckBoxChange = (isChecked, itemIndex, cargoIndex) => {
        const updatedItemsList = [...checkListData];
        const cargo = updatedItemsList[itemIndex]?.cargos?.[cargoIndex];

        if (cargo) {
            const statusKey = isDropOff ? 'cgDropOffStatus' : 'cgPickupStatus';
            cargo[statusKey] = isChecked ? 'C' : 'U';

            if (!isDropOff) {
                cargo.cgDropOffStatus = 'U';
            }

            setCheckListData(updatedItemsList);
            updateCheckCount(updatedItemsList);
        }
    };

    const updateCheckCount = (updatedItemsList) => {
        let totalChecked = 0;
        let totalItems = 0;
        const statusKey = isDropOff ? "cgDropOffStatus" : "cgPickupStatus";

        updatedItemsList.forEach(({ cargos }) => {
            if (cargos?.length) {
                totalItems += cargos.length;
                totalChecked += cargos.filter(cargo => cargo[statusKey] === 'C').length;
            }
        });
        setCheckCount({ checkCount: totalChecked, totalCount: totalItems });
    };

    return (
        <ScrollView contentContainerStyle={styles.scrollContainer}>
            <View style={styles.container}>
                {/* Header Row */}
                <View style={[styles.tableRowHeader, styles.centerRow]}>
                    <Text style={[styles.tableCell, styles.header, { flex: 0.5 }]}>{t("job:checkList.location")}</Text>
                    <Text style={[styles.tableCell, styles.header]}>{t("job:checkList.cargoType")}</Text>
                    <Text style={[styles.tableCell, styles.header]}>{t("job:checkList.cargoDesc")}</Text>
                    <Text style={[styles.tableCell, styles.header]}>{t("other:header.specialInstructions")}</Text>
                </View>
                <ScrollView style={styles.innerScroll} nestedScrollEnabled={true}>
                    {checkListData?.map((value, ind) => (
                        <View key={ind}>
                            <View style={styles.locationRow}>
                                <Icon
                                    name="location"
                                    family="ionicon"
                                    size={moderateScale(15)}
                                    color="#596c90"
                                    style={styles.locationIcon}
                                />
                                <Text style={styles.subHeader}>
                                    {value?.toLocAddr}
                                </Text>
                            </View>
                            {value?.cargos?.map((item, index) => (
                                <View
                                    style={[styles.tableRow, item.checked ? styles.selectedRow : null]}
                                    key={index}
                                >
                                    <View style={[styles.checkboxContainer, { flex: 0.5 }]}>
                                        <Checkbox
                                            label=""
                                            initialValue={isDropOff ? item.cgDropOffStatus === 'C' : item.cgPickupStatus === 'C'}
                                            onChange={(isChecked) => onCheckBoxChange(isChecked, ind, index)}
                                            color="success"
                                        />
                                    </View>
                                    <Text style={styles.tableCell}>{item?.goodsType}</Text>
                                    <Text style={styles.tableCell}>{item?.goodsDesc}</Text>
                                    <Text style={styles.tableCell}>{item?.specialInstructions}</Text>
                                </View>
                            ))}
                        </View>
                    ))}
                </ScrollView>
                <View style={styles.totalRow}>
                    <Text style={styles.tableCell} />
                    <Text style={[styles.tableCell, styles.totalLabel]}>{t("job:checkList.totalCargo")}</Text>
                    <Text style={[styles.tableCell, styles.totalEarnings]}>
                        {checkCount.checkCount}/{checkCount.totalCount} {t("job:checkList.checked")}
                    </Text>
                </View>
            </View>
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    scrollContainer: {
        flexGrow: 1,
    },
    container: {
        backgroundColor: '#f7f7f7',
        borderRadius: 10,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 5,
        elevation: 3,
        width: "100%",
        overflow: 'hidden',
        minHeight: height * 0.8,
    },
    tableRow: {
        flexDirection: 'row',
        paddingVertical: 6,
        paddingHorizontal: 8,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
        alignItems: 'center',
    },
    tableRowHeader: {
        flexDirection: 'row',
        paddingVertical: 0,
        paddingHorizontal: 8,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
        alignItems: 'center',
    },
    locationRow: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingVertical: 5,
    },
    tableCell: {
        flex: 1,
        textAlign: 'center',
        fontSize: 8,
        paddingVertical: 8,
    },
    header: {
        backgroundColor: materialTheme.COLORS.CKSECONDARY,
        color: 'white',
        fontWeight: 'bold',
        paddingVertical: 8,
        fontSize: 7,
    },
    subHeader: {
        fontWeight: 'bold',
        marginVertical: 1,
        marginHorizontal: 5,
        fontSize: 8
    },
    totalLabel: {
        fontWeight: 'bold',
        fontSize: 15,
        color: '#7f7f7f',
    },
    totalEarnings: {
        fontWeight: 'bold',
        fontSize: 18,
        color: '#424141',
    },
    checkboxContainer: {
        justifyContent: 'center',
        alignItems: 'center',
    },
    centerRow: {
        justifyContent: 'center',
    },
    selectedRow: {
        backgroundColor: '#e7e7e7',
    },
    locationIcon: {
        marginHorizontal: 10,
    },
    totalRow: {
        flexDirection: 'row',
        paddingVertical: 12,
        alignItems: 'center',
    },
    innerScroll: {
        maxHeight: height * 0.5,
    },
});

export default CheckListComponent;
