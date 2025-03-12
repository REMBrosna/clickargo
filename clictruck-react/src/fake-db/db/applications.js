export const entryPermitDB = [
    {
        appId: "EP2021030812345",
        ucrNo: 'PEDI202012123',
        submitDate: "2020-02-02T12:30:28",
        status: "S",
        expiryDate: "",

        arrivalPort: "",
        lastPortCountry: "",

        impNo: "",
        shipName: "",
        shipCountry: "",
        callSign: "",
        applicantTin: "",

        header: {
            voyageDetails: {
                voyageNo: "WWE-33455",
                voyageName: "Voyage Master",

                arrivalPort: "PPAP",
                arrivalDate: "2021-02-02",
                arrivalTime: "10:30:00",
                lastPortCountry: "SG",

                departurePort: "PPAP",
                departureDate: "2021-03-03",
                DepartureTime: "21:00:00",
                nextPortCountry: "TH",
                dutyPaidAt: "BORDER"
            },
            shipDetails: {
                impNo: "SHIP002",
                shipName: "Maersk Alabama",
                callSign: "CALL111",
                flageState: "SG",
                shipType: "TANKER",

                shipCountry: "SG",
                applicantTin: "L3342234232",
                applicantName: "CAM SHIPPING COMPANY",

            }
        }
    }
];

export const preArrivalDB = [
    {
        appId: "PA2021030812345",
        version: "",
        submitDate: "2021-03-01 12:30",
        ucrNo: "PEDI202012123",
        status: "S",
        eta: '2021-03-10 12:43',
        arrivalPort: "KHKOS",
        expiryDate: "2021-10-02",
        lastPortCountry: "",

        impNo: "",
        shipName: "",
        shipCountry: "",
        callSign: "",
        applicantTin: "",

        header: {
            type: "arrival",
            voyageDetails: {
                voyageNo: "WWE-33455",
                voyageName: "Voyage Master",

                arrivalPort: "PPAP",
                arrivalDate: "2021-02-02",
                arrivalTime: "10:30:00",
                lastPortCountry: "SG",
                lastPort: "SGPOS",

                departurePort: "PPAP",
                departureDate: "2021-03-03",
                DepartureTime: "21:00:00",
                nextPortCountry: "TH",
                nextPort: "THPHU",
                dutyPaidAt: "BORDER"
            },
            shipDetails: {
                impNo: "SHIP002",
                shipName: "Maersk Alabama",
                callSign: "CALL111",
                flageState: "SG",
                shipType: "TANKER",

                shipCountry: "SG",
                applicantTin: "L3342234232",
                applicantName: "CAM SHIPPING COMPANY",

            }
        },
        falForm: {
            fal1: {
                certificateReg: "",
                grossTonnage: "",
                netTonnage: "",
                voyageDesc: "",
                cargoDesc: "",
                remarks: "",
                facilities: ""
            },
            fal2: {
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


            },
            fal3: {
                stayPeriod: "",
                onBoardPersons: "",
                list: [{
                    locationOnBoard: "Upper Deck",
                    quantity: "20 Tn",
                    articleName: "Chemical Container",
                }, {
                    locationOnBoard: "Lower Deck",
                    quantity: "40 Tn",
                    articleName: "Petroleum Products",
                }

                ],
                ShipStoresDeclaration: {
                    articleName: "Coal",
                    quantity: "5 Tn",
                    locationOnBoard: ""
                }

            },
            fal4: {
                list: [
                    {
                        firstName: "John Doe",
                        idNumber: "MO445533",
                        ineligibleEffects: "Goods to be declared",
                        remarks: "Any Other Remarks"
                    }, {
                        firstName: "Bruce Wayne",
                        idNumber: "IC1122",
                        ineligibleEffects: "Goods to be declared",
                        remarks: "Any Other Remarks"
                    }
                ],
                AddCrew: {
                    familyName: "",
                    givenName: "",
                    nationality: "",
                    dateOfBirth: "",
                    placeOfBirth: "",
                    gender: "",
                    typeOfIdentity: "",
                    serialNumberOfIdentity: "",
                    issuingStateOfIdentity: "",
                    expiryDateOfIdentity: "",
                    portOfEmberkation: "",
                    visaNumber: "",
                    portOfDisemberkation: ""
                }

            },
            fal5: {

            },
            fal6: {
                list: [
                    {
                        familyName: "John Doe",
                        givenName: "John",
                        nationality: "JAPAN",
                        identityType: "Passport"
                    }
                ],
                AddPassenger: {
                    familyName: "",
                    givenName: "",
                    nationality: "",
                    dateOfBirth: "",
                    placeOfBirth: "",
                    gender: "",
                    typeOfIdentity: "",
                    serialNumberOfIdentity: "",
                    issuingStateOfIdentity: "",
                    expiryDateOfIdentity: "",
                    portOfEmberkation: "",
                    visaNumber: "",
                    portOfDisemberkation: "",
                    transitPassenger: ""
                }
            },
            fal7: {
                list: [
                    {
                        storagePosition: "John Doe",
                        referenceNumber: "John",
                        unNumber: "HK463"
                    },
                    {
                        storagePosition: "Bruce Wayne",
                        referenceNumber: "Bruce",
                        unNumber: "BG45665"
                    },
                ],
                AddDangerousGoods: {
                    stowagePosition: "",
                    referenceNumber: "",
                    forVIdentification: "",
                    unNumber: "",
                    properShippingName: "",
                    corSRisks: "",
                    packingGroup: "",
                    additionalInformation: "",
                    numberAndKindOfPackages: "",
                    massOrVolume: "",
                    ems: ""
                }
            }

        },
        others: {
            vaccination: {
                list: [
                    {
                        familyName: "JOHN DOE",
                        givenName: "ALEX DOE",
                        rankRating: "Rank1",
                        nationality: "VN",
                        dob: "04/20/1985",
                        signature: '',
                        effects: 'Cigars'
                    },

                ],
                AddCrew: {
                    familyName: "JOHN DOE",
                    givenName: "ALEX DOE",
                    nationality: "Vietnamese",
                    dateOfBirth: "04/20/1985",
                    placeOfBirth: "Vietnam",
                    gender: "Male",
                    rankRating: "Rank1",
                    typeOfIdentity: "Passport",
                    serialNumberOfIdentity: "1234444",
                    issuingStateOfIdentity: "VN",
                    expiryDateOfIdentity: "01/20/2030",
                    portOfEmberkation: "",
                    visaNumber: "VISA1234555",
                    portOfDisemberkation: "CAMBODIA"
                }
            },
        },
        supportingDocs: {
            docType: "",
            docRefNo: "",

        }
    }
];


export const preArrSecInfoDB = [
    {
        appId: "PASI22021030812345",
        ucrNo: 'PEDI202012123',
        submitDate: "20-Nov-20 12:30",
        status: "S",
        voyageNo: "OTW-2343243",
        shipName: "Vaga Maersk",
        imoNo: "9778545",
        callSign: "c1Sign",
        cty: "SG",
        shippingLineTIN: "SL448",
        shippingLineName: "Moris Port",
        shippingLineAddress: "Line 1",
        port: "KHPNH",
        submitDate: "2021-02-02 12:30",
        noOfCrew: "5 Persons",
        typeOfShip: "STEEL CONTAINER VSL",
        yesOrno1: "YES",
        securityLevel: "Level 1",

        totalPassanger: "",
    }
];

export const arrivalDecDB = [
    {
        appId: "AD2021030812345",
        ucrNo: 'PEDI202012123',
        submitDate: "",
        status: "D",
        arrivalPort: "",
        lastPortCountry: "",

        impNo: "",
        shipName: "",
        shipCountry: "",
        callSign: "",
        applicantTin: "",

        header: {
            voyageDetails: {
                vcrNo: "PEDI202012123",
                voyageNo: "WWE-33455",
                voyageName: "Voyage Master",

                arrivalPort: "KHPNH",
                arrivalDate: "2021-02-02",
                arrivalTime: "10:30:00",
                lastPortCountry: "SG",
                lastPort: "",

                departurePort: "KHPNH",
                departureDate: "2021-03-03",
                DepartureTime: "21:00:00",
                nextPortCountry: "TH",
                nextPort: "",
                dutyPaidAt: ""
            },
            shipDetails: {
                impNo: "SHIP002",
                shipName: "Maersk Alabama",
                callSign: "CALL111",
                flageState: "SG",
                shipType: "TANKER",

                shipCountry: "SG",
                applicantTin: "L3342234232",
                applicantName: "CAM SHIPPING COMPANY",

            }
        },
        falForm: {
            fal1: {
                certificateReg: "",
                grossTonnage: "",
                netTonnage: "",
                voyageDesc: "",
                cargoDesc: "",
                remarks: "",
                facilities: ""
            },
            fal2: {
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


            },
            fal3: {
                stayPeriod: "",
                onBoardPersons: "",
                list: [{
                    locationOnBoard: "Upper Deck",
                    quantity: "20 Tn",
                    articleName: "Chemical Container",
                }, {
                    locationOnBoard: "Lower Deck",
                    quantity: "40 Tn",
                    articleName: "Petroleum Products",
                }

                ],
                ShipStoresDeclaration: {
                    articleName: "Coal",
                    quantity: "5 Tn",
                    locationOnBoard: ""
                }

            },
            fal4: {
                list: [
                    {
                        firstName: "John Doe",
                        idNumber: "MO445533",
                        ineligibleEffects: "Goods to be declared",
                        remarks: "Any Other Remarks"
                    }, {
                        firstName: "Bruce Wayne",
                        idNumber: "IC1122",
                        ineligibleEffects: "Goods to be declared",
                        remarks: "Any Other Remarks"
                    }
                ],
                AddCrew: {
                    familyName: "",
                    givenName: "",
                    nationality: "",
                    dateOfBirth: "",
                    placeOfBirth: "",
                    gender: "",
                    typeOfIdentity: "",
                    serialNumberOfIdentity: "",
                    issuingStateOfIdentity: "",
                    expiryDateOfIdentity: "",
                    portOfEmberkation: "",
                    visaNumber: "",
                    portOfDisemberkation: ""
                }

            },
            fal5: {

            },
            fal6: {
                list: [
                    {
                        familyName: "John Doe",
                        givenName: "John",
                        nationality: "JAPAN",
                        identityType: "Passport"
                    }
                ],
                AddPassenger: {
                    familyName: "",
                    givenName: "",
                    nationality: "",
                    dateOfBirth: "",
                    placeOfBirth: "",
                    gender: "",
                    typeOfIdentity: "",
                    serialNumberOfIdentity: "",
                    issuingStateOfIdentity: "",
                    expiryDateOfIdentity: "",
                    portOfEmberkation: "",
                    visaNumber: "",
                    portOfDisemberkation: "",
                    transitPassenger: ""
                }
            },
            fal7: {
                list: [
                    {
                        storagePosition: "John Doe",
                        referenceNumber: "John",
                        unNumber: "HK463"
                    },
                    {
                        storagePosition: "Bruce Wayne",
                        referenceNumber: "Bruce",
                        unNumber: "BG45665"
                    },
                ],
                AddDangerousGoods: {
                    stowagePosition: "",
                    referenceNumber: "",
                    forVIdentification: "",
                    unNumber: "",
                    properShippingName: "",
                    corSRisks: "",
                    packingGroup: "",
                    additionalInformation: "",
                    numberAndKindOfPackages: "",
                    massOrVolume: "",
                    ems: ""
                }
            }


        },
        others: {
            vaccination: {
                list: [
                    {
                        familyName: "JOHN DOE",
                        givenName: "ALEX DOE",
                        rankRating: "Rank1",
                        nationality: "Vietnamese",
                        dob: "04/20/1985",
                        signature: '',
                        effects: 'Cigars'
                    },

                ],
                AddCrew: {
                    familyName: "JOHN DOE",
                    givenName: "ALEX DOE",
                    nationality: "Vietnamese",
                    dateOfBirth: "04/20/1985",
                    placeOfBirth: "Vietnam",
                    gender: "Male",
                    rankRating: "Rank1",
                    typeOfIdentity: "Passport",
                    serialNumberOfIdentity: "1234444",
                    issuingStateOfIdentity: "VN",
                    expiryDateOfIdentity: "01/20/2030",
                    portOfEmberkation: "",
                    visaNumber: "VISA1234555",
                    portOfDisemberkation: "CAMBODIA"
                }
            },
        },
        supportingDocs: {
            docType: "",
            docRefNo: "",

        }
    }
];

export const pilorOrderDB = [
    {
        appId: "PO2021030812345",
        ucrNo: 'PEDI202012123',
        submitDate: "",
        status: "Pending",
        shipNameP: "Maersk Alabama",
        voyageNoP: "ARV1234",
        portOfArrivalOrDeptP: "PPAP",
        dateOfArrvP: "13/12/2020",
        portArrivedFromP: "SG",

        stowaways: 'Y',
        animalOrPlants: 'N',
        parcelPackage: 'Y',
        armsAmmunitions: 'N',
        illegalDrugs: 'N',
        passengers: 'Y',
        livestocks: 'N',

        signUpload: "",
        status: 'D',
        reqMessage: "",
    },
    {
        appId: "PO2021030812346",
        ucrNo: 'PEDI202013123',
        submitDate: "",
        status: "Pending",
        shipNameP: "Maersk Alabama",
        voyageNoP: "ARV1234",
        portOfArrivalOrDeptP: "PPAP",
        dateOfArrvP: "13/12/2020",
        portArrivedFromP: "SG",

        stowaways: 'Y',
        animalOrPlants: 'N',
        parcelPackage: 'Y',
        armsAmmunitions: 'N',
        illegalDrugs: 'N',
        passengers: 'Y',
        livestocks: 'N',

        signUpload: "",
        status: 'D',
        reqMessage: "",
    }

];

export const dosDB = [
    {
        appId: "DOS2021030812345",
        ucrNo: 'PEDI202012123',
        submitDate: "2021-02-02 12:30",
        status: "Pending",
        shipName: "Maersk Alabama",
        imoNo: "IM0123",
        callSign: "c1Sign",
        cty: "SG",
        shippingLineTIN: "SL448",
        shippingLineName: "Moris Port",
        shippingLineAddress: "Line 1",
        port: "Mories",
        status: 'S'
    }
];

export const deptDecDB = [
    {
        appId: "DD2021030812345",
        ucrNo: "PEDI202013123",
        version: "",
        submitDate: "",
        status: "D",

        arrivalPort: "",
        lastPortCountry: "",

        impNo: "",
        shipName: "",
        shipCountry: "",
        callSign: "",
        applicantTin: "",

        header: {
            voyageDetails: {
                voyageNo: "WWE-33455",
                voyageName: "Voyage Master",

                arrivalPort: "PPAP",
                arrivalDate: "2021-02-02",
                arrivalTime: "10:30:00",
                lastPortCountry: "SG",

                departurePort: "PPAP",
                departureDate: "2021-03-03",
                DepartureTime: "21:00:00",
                nextPortCountry: "TH",
                dutyPaidAt: "BORDER"
            },
            shipDetails: {
                impNo: "SHIP002",
                shipName: "Maersk Alabama",
                callSign: "CALL111",
                flageState: "SG",
                shipType: "TANKER",

                shipCountry: "SG",
                applicantTin: "L3342234232",
                applicantName: "CAM SHIPPING COMPANY",

            }
        },
        falForm: {
            fal1: {
                certificateReg: "",
                grossTonnage: "",
                netTonnage: "",
                voyageDesc: "",
                cargoDesc: "",
                remarks: "",
                facilities: ""
            },
            fal2: {
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


            },
            fal3: {
                stayPeriod: "",
                onBoardPersons: "",
                list: [{
                    locationOnBoard: "Upper Deck",
                    quantity: "20 Tn",
                    articleName: "Chemical Container",
                }, {
                    locationOnBoard: "Lower Deck",
                    quantity: "40 Tn",
                    articleName: "Petroleum Products",
                }


                ],
                ShipStoresDeclaration: {
                    articleName: "Coal",
                    quantity: "5 Tn",
                    locationOnBoard: ""
                }

            },
            fal4: {
                list: [
                    {
                        firstName: "John Doe",
                        idNumber: "MO445533",
                        ineligibleEffects: "Goods to be declared",
                        remarks: "Any Other Remarks"
                    }, {
                        firstName: "Bruce Wayne",
                        idNumber: "IC1122",
                        ineligibleEffects: "Goods to be declared",
                        remarks: "Any Other Remarks"
                    }
                ],
                AddCrew: {
                    familyName: "",
                    givenName: "",
                    nationality: "",
                    dateOfBirth: "",
                    placeOfBirth: "",
                    gender: "",
                    typeOfIdentity: "",
                    serialNumberOfIdentity: "",
                    issuingStateOfIdentity: "",
                    expiryDateOfIdentity: "",
                    portOfEmberkation: "",
                    visaNumber: "",
                    portOfDisemberkation: ""
                }

            },
            fal5: {

            },
            fal6: {
                list: [
                    {
                        familyName: "John Doe",
                        givenName: "John",
                        nationality: "JAPAN",
                        identityType: "Passport"
                    }
                ],
                AddPassenger: {
                    familyName: "",
                    givenName: "",
                    nationality: "",
                    dateOfBirth: "",
                    placeOfBirth: "",
                    gender: "",
                    typeOfIdentity: "",
                    serialNumberOfIdentity: "",
                    issuingStateOfIdentity: "",
                    expiryDateOfIdentity: "",
                    portOfEmberkation: "",
                    visaNumber: "",
                    portOfDisemberkation: "",
                    transitPassenger: ""
                }
            },
            fal7: {
                list: [
                    {
                        storagePosition: "John Doe",
                        referenceNumber: "John",
                        unNumber: "HK463"
                    },
                    {
                        storagePosition: "Bruce Wayne",
                        referenceNumber: "Bruce",
                        unNumber: "BG45665"
                    },
                ],
                AddDangerousGoods: {
                    stowagePosition: "",
                    referenceNumber: "",
                    forVIdentification: "",
                    unNumber: "",
                    properShippingName: "",
                    corSRisks: "",
                    packingGroup: "",
                    additionalInformation: "",
                    numberAndKindOfPackages: "",
                    massOrVolume: "",
                    ems: ""
                }
            }


        },
        others: {
            vaccination: {
                list: [
                    {
                        familyName: "JOHN DOE",
                        givenName: "ALEX DOE",
                        rankRating: "Rank1",
                        nationality: "Vietnamese",
                        dob: "04/20/1985",
                        signature: '',
                        effects: 'Cigars'
                    },

                ],
                AddCrew: {
                    familyName: "JOHN DOE",
                    givenName: "ALEX DOE",
                    nationality: "Vietnamese",
                    dateOfBirth: "04/20/1985",
                    placeOfBirth: "Vietnam",
                    gender: "Male",
                    rankRating: "Rank1",
                    typeOfIdentity: "Passport",
                    serialNumberOfIdentity: "1234444",
                    issuingStateOfIdentity: "VN",
                    expiryDateOfIdentity: "01/20/2030",
                    portOfEmberkation: "",
                    visaNumber: "VISA1234555",
                    portOfDisemberkation: "CAMBODIA"
                }
            },
        },
        supportingDocs: {
            docType: "",
            docRefNo: "",

        }
    }
];
export const suppDocs = [
    {
        attUid: 'sys', attSeq: '1',
        attType: 'OTH', attUcrNo: 'PO2021030812345',
        attReferenceid: 'PO2021030812345',
        attName: 'Other Document Name.pdf',
        attDesc: 'File Description',
        attData: ""
    }
];
export const suppDocsDOS = [
    {
        attUid: 'sys', attSeq: '1',
        attType: 'OTH', attUcrNo: 'DOS2021030812345',
        attReferenceid: 'DOS2021030812345',
        attName: 'Other Document Name.pdf',
        attDesc: 'File Description',
        attData: ""
    }
];

export const suppDocsSSCEC = [
    {
        attUid: 'sys', 
        attSeq: '1',
        attType: 'SSCEC/SSCC', 
        attUcrNo: 'SSCC20201213435',
        attReferenceid: 'SSCC20201213435',
        attName: 'Ship Certificate SSCEC 2020.pdf',
        attDesc: 'File Description',
        attData: ""
    },
    {
        attUid: 'sys', 
        attSeq: '2',
        attType: 'OTH', 
        attUcrNo: 'SSCC20201213435',
        attReferenceid: 'SSCC20201213435',
        attName: 'Other Document Name.pdf',
        attDesc: 'File Description',
        attData: ""
    },
    {
        attUid: 'sys', 
        attSeq: '1',
        attType: 'SSCEC/SSCC', 
        attUcrNo: 'SSCEC20201220123',
        attReferenceid: 'SSCEC20201220123',
        attName: 'Ship Certificate SSCEC 2020.pdf',
        attDesc: 'File Description',
        attData: ""
    },
    {
        attUid: 'sys', 
        attSeq: '2',
        attType: 'SSCEC/SSCC', 
        attUcrNo: 'SSCEC20201220123',
        attReferenceid: 'SSCEC20201220123',
        attName: 'Ship Certificate SSCEC 2019.pdf',
        attDesc: 'File Description',
        attData: ""
    },
    {
        attUid: 'sys', 
        attSeq: '3',
        attType: 'OTH', 
        attUcrNo: 'SSCEC20201220123',
        attReferenceid: 'SSCEC20201220123',
        attName: 'Other Document Name.pdf',
        attDesc: 'File Description',
        attData: ""
    },
    {
        attUid: 'sys', 
        attSeq: '1',
        attType: 'SSCEC/SSCC', 
        attUcrNo: 'SSCEC20200120875',
        attReferenceid: 'SSCEC20200120875',
        attName: 'Ship Certificate SSCEC 2020.pdf',
        attDesc: 'File Description',
        attData: ""
    },
];
export const suppDocsPAS = [
    {
        attUid: 'sys', attSeq: '1',
        attType: 'OTH', attUcrNo: 'PASI22021030812345',
        attReferenceid: 'PASI22021030812345',
        attName: 'Other Document Name.pdf',
        attDesc: 'File Description',
        attData: ""
    }
];
