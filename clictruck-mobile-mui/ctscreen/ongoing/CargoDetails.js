import React from "react";
import CtModal from "../../ctcomponents/CtModal";
import { Block, Text } from "galio-framework";
import { popupStyles } from "./commonstyles";
import GliTextInput from "../../ctcomponents/GliTextInput";
import { convertToString } from "../../constants/utils";


const CargoDetails = ({ show, onClosePressed, data }) => {

    const jobType = data?.[0]?.tckJob?.tckMstShipmentType?.shtId;
    // console.log("cargo jobtype ", jobType);

    const isDomestic = "DOMESTIC" == jobType;
    // console.log("cargo isDomestic job ", isDomestic);

    let cargoType = data?.[0]?.tckCtTripList?.[0]?.tckCtTripCargoFmList?.[0]?.tckCtMstCargoType?.crtypName;
    let cargoDesc = data?.[0]?.tckCtTripList?.[0]?.tckCtTripCargoFmList?.[0]?.cgCargoDesc;
    let cargoInst = data?.[0]?.tckCtTripList?.[0]?.tckCtTripCargoFmList?.[0]?.cgCargoSpecialInstn;
    let cargoLength = convertToString(data?.[0]?.tckCtVeh?.vhLength);
    let cargoWeight = convertToString(data?.[0]?.tckCtVeh?.vhWeight);
    let cargoWidth = convertToString(data?.[0]?.tckCtVeh?.vhWidth);
    let cargoVolume = convertToString(data?.[0]?.tckCtVeh?.vhVolume);
    let cargoHeight = convertToString(data?.[0]?.tckCtVeh?.vhHeight);

    if (isDomestic){
        const dom = data?.[0]?.tckCtTripList?.[0]?.tripCargoMmList;
        cargoType = dom?.[0]?.tckCtMstCargoType?.crtypName;
        cargoLength = convertToString(dom?.[0]?.cgCargoLength);
        cargoWidth = convertToString(dom?.[0]?.cgCargoWidth);
        cargoHeight = convertToString(dom?.[0]?.cgCargoHeight);
        cargoWeight = convertToString(dom?.[0]?.cgCargoWeight);
        cargoVolume = convertToString(dom?.[0]?.cgCargoVolume);
        cargoDesc = dom?.[0]?.cgCargoDesc;
        cargoInst = dom?.[0]?.cgCargoSpecialInstn;
    }

    return <CtModal show={show}
        onClosePressed={onClosePressed}
        headerElement={<Text style={popupStyles.textModalHeader}>Cargo Details</Text>}
        noBtn={true}>

        <Block style={[popupStyles.modalBody, { flexDirection: 'row', flexWrap: 'wrap' }]}>
            <Block style={popupStyles.modalHalf}>
                <GliTextInput
                    label="Type"
                    labelStyle={popupStyles.label}
                    value={cargoType}
                    editable={false}
                />
            </Block>
            <Block style={popupStyles.modalHalf}>
                <GliTextInput
                    label="Length"
                    labelStyle={popupStyles.label}
                    editable={false}
                    value={cargoLength}
                />
            </Block>
            <Block style={popupStyles.modalHalf}>
                <GliTextInput
                    label="Weight"
                    labelStyle={popupStyles.label}
                    editable={false}
                    value={cargoWeight}
                />
            </Block>
            <Block style={popupStyles.modalHalf}>
                <GliTextInput
                    label="Width"
                    labelStyle={popupStyles.label}
                    editable={false}
                    value={cargoWidth}
                />
            </Block>
            <Block style={popupStyles.modalHalf}>
                <GliTextInput
                    label="Volumetric"
                    labelStyle={popupStyles.label}
                    editable={false}
                    value={cargoVolume}
                />
            </Block>
            <Block style={popupStyles.modalHalf}>
                <GliTextInput
                    label="Height"
                    labelStyle={popupStyles.label}
                    editable={false}
                    value={cargoHeight}
                />
            </Block>
            <GliTextInput
                label="Description"
                labelStyle={popupStyles.label}
                editable={false}
                multiline={true}
                value={cargoDesc}
            />
            <GliTextInput
                label={t("jobHistory:popup.cargo.remarks")}
                labelStyle={popupStyles.label}
                editable={false}
                multiline={true}
                value={cargoInst}
            />
        </Block>
    </CtModal>
}


export default CargoDetails;