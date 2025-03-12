
export const manifestDB = {
  list: [{
    blNo: "123",
    marksNo: "ER345",
    kindPck: "300 Tn",
    grossWeight: "400",
    measurement: "100",
}, {
    blNo: "4544",
    marksNo: "YT455",
    kindPck: "100 Tn",
    grossWeight: "200",
    measurement: "320",
}, {
    blNo: "5766",
    marksNo: "HG5645",
    kindPck: "300 Tn",
    grossWeight: "200",
    measurement: "444",
},

],
cargoDeclaration: {
    billInfo: {
        billNo: "",
        billType: "",
        billNature: "",
        billMasterNo: "",

        placeLoading: "",
        placeUnloading: "",
        portLoading: "",
        portUnloading: ""
    },
    traderInfo: {
        exporterName: "",
        exporterAddress: "",
        consigneeName: "",
        consigneeAddress: "",
        consigneeCode: "",
        notifyName: "",
        notifyAddress: "",
        notifyCode: ""
    },
    goodsInfo: {
        goodsDescription: "",
        numberOfContainers: "",
        numberOfPackages: "",
        packageType: "",
        grossMass: "",
        volumeInCubicMeters: "",
        numberOfSeals: "",
        sealPartyCode: "",
        sealsMarks: "",
        shippingMarks: "",
        information: ""
    },
    containerInfo: {
        list: [
            {
                containerNo: "",
                contanerType: "",
                emptyOrFull: "",
                nameCode: "",

            },
            {
                containerNo: "",
                contanerType: "",
                emptyOrFull: "",
                nameCode: "",

            }
        ],
        containerDetails: {
            containerNo: "",
            contanerType: "",
            emptyOrFull: "",
            nameCode: "",
            noOfPackages: "",
            goodsDescription: "",
            grossMass: "",


            sealingParty: "",
            marks1: "",
            marks2: "",
            marks3: "",
            minTemp: "",
            maxTemp: "",
            humidity: "",
            dangerousGoods: ""
        }
    }
}
};

