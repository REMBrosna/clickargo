import Mock from "../mock";

export const processApplicationDB = {
  list: [
    {
      appId: "EP2021030812345",
      version: "1",
      submitDate: "2020-12-21",
      Status: "Submitted",
      ETA: "2020-12-24",
      ETD: "2021-01-15",
      ecrNO: "ECR20201201",
      arrivalPort: "PAS",
      lastPortCountry: "Thailand",

      imoNo: "ERT3344",
      shipName: "Moon Light",
      shipCountry: "US",
      callSign: "TR1234",
      applicantTin: "2332434232",

      subTable: {
        quarrantine: {
          appId: "QRN20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        immegration: {
          appId: "IMG20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        customs: {
          appId: "CUS20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
      },

      header: {
        voyageDetails: {
          voyageNo: "VE123765",
          vcrNo: "PEDI202012123",
          voyageName: "2000918",
          voyageUOM: "LT",
          arrivalPort: "KHPNH",
          arrivalDate: "2021-01-12",
          arrivalTime: "",
          lastPortCountry: "AR",
          dwt: "45",
          nt: "8016",
          gt: "25145",
          callSign: "CallSign",
          imoNo: "ERT3344",
          shipName: "Moon Light",
          shipOwner: "John",
          shipCountry: "US",
          callSign: "TR1234",
          flageState: "FL1234",
          applicantTin: "2332434232",
          departurePort: "KHPNH",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "AR",
          address: "3 Cross SG",
        },
        shipDetails: {
          impNo: "ERT3344",
          shipName: "Moon Light",
          shipCountry: "US",
          callSign: "TR1234",
          flageState: "FL1234",
          applicantTin: "2332434232",

          importedGoods: "Petroleum",
          qtyGoods: "1000",
          uom: "LT",
          noPassangers: "100",
        },
      },
      falForm: {
        fal1: {
          certificateReg: "",
          grossTonnage: "",
          netTonnage: "",
          voyageDesc: "",
          cargoDesc: "",
          remarks: "",
          facilities: "",
        },
        fal2: {
          list: [
            {
              blNo: "123",
              marksNo: "ER345",
              kindPck: "300 Tn",
              grossWeight: "400",
              measurement: "100",
            },
            {
              blNo: "4544",
              marksNo: "YT455",
              kindPck: "100 Tn",
              grossWeight: "200",
              measurement: "320",
            },
            {
              blNo: "5766",
              marksNo: "HG5645",
              kindPck: "300 Tn",
              grossWeight: "200",
              measurement: "444",
            },
          ],
          cargoDeclaration: {
            billInfo: {
              billNo: "111",
              billType: "Bill",
              billNature: "NT345",
              billMasterNo: "MS234",

              placeLoading: "",
              placeUnloading: "",
              portLoading: "",
              portUnloading: "",
            },
            traderInfo: {
              exporterName: "",
              exporterAddress: "",
              consigneeName: "",
              consigneeAddress: "",
              consigneeCode: "",
              notifyName: "",
              notifyAddress: "",
              notifyCode: "",
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
              information: "",
            },
            containerInfo: {
              list: [
                {
                  containerNo: "1234",
                  contanerType: "ER123",
                  emptyOrFull: "Chemicals",
                  nameCode: "300Tn",
                },
                {
                  containerNo: "4567",
                  contanerType: "RV123",
                  emptyOrFull: "Petrol",
                  nameCode: "100Tn",
                },
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
                dangerousGoods: "",
              },
            },
          },
        },

        fal3: {
          stayPeriod: "4",
          onBoardPersons: "215",
          list: [
            {
              locationOnBoard: "Chemical Container",
              quantity: "20 Tn",
              articleName: "Upper Deck",
            },
            {
              locationOnBoard: "Petroleum Products",
              quantity: "40 Tn",
              articleName: "Lower Deck",
            },
          ],
          ShipStoresDeclaration: {
            articleName: "Coal",
            quantity: "5 Tn",
            locationOnBoard: "asd",
          },
        },
        fal4: {
          list: [
            {
              firstName: "John Doe",
              idNumber: "MO445533",
              ineligibleEffects: "Goods to be declared",
              remarks: "Any Other Remarks",
            },
            {
              firstName: "Bruce Wayne",
              idNumber: "IC1122",
              ineligibleEffects: "Goods to be declared",
              remarks: "Any Other Remarks",
            },
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
            portOfDisemberkation: "",
          },
        },
        fal5: {},
        fal6: {
          list: [
            {
              familyName: "John Doe",
              givenName: "John",
              nationality: "JAPAN",
              identityType: "Passport",
            },
            {
              familyName: "Bruce Wayne",
              givenName: "Bruce",
              nationality: "MALAYSIA",
              identityType: "IKAD",
            },
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
            transitPassenger: "",
          },
        },
        fal7: {
          list: [
            {
              storagePosition: "John Doe",
              referenceNumber: "John",
              unNumber: "HK463",
            },
            {
              storagePosition: "Bruce Wayne",
              referenceNumber: "Bruce",
              unNumber: "BG45665",
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
            ems: "",
          },
        },
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
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: {
        docType: "ABC",
        docRefNo: "AA334",
      },
    },
    {
      appId: "EPPPAP20201231",
      version: "1",
      arrivalPort: "PAP",
      impNo: "ERT33244",
      shipName: "Sea Star",
      shipCountry: "US",
      lastPortCountry: "Thailand",
      applicantTin: "2332434232",
      submitDate: "2020-12-11",
      Status: "Payment Pending",
      callSign: "TR5667",
      ETA: "2020-12-12",
      ETD: "2020-12-15",
      ecrNO: "ECR20201214",
      header: {
        voyageDetails: {
          voyageNo: "",
          voyageName: "",
          voyageUOM: "LT",
          arrivalPort: "PAS",
          arrivalDate: "",
          arrivalTime: "",
          lastPortCountry: "Thailand",

          departurePort: "",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "",
        },
        shipDetails: {
          impNo: "ERT3344",
          shipName: "Moon Light",
          shipCountry: "US",
          callSign: "TR1234",
          applicantTin: "2332434232",
        },
      },
      flaForm: [],
      others: {
        vaccination: {
          list: [
            {
              familyName: "JOHN DOE",
              givenName: "ALEX DOE",
              rankRating: "Rank1",
              nationality: "Vietnamese",
              dob: "04/20/1985",
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: [],
    },
    {
      appId: "EPPPAP20201254",
      version: "1",
      arrivalPort: "PAS",
      submitDate: "2020-12-11",
      ETA: "2020-12-21",
      ETD: "2021-01-13",
      ecrNO: "ECR20201214",

      impNo: "ERT3390",
      shipName: "Maersk Alabama",
      shipCountry: "US",
      lastPortCountry: "Thailand",
      applicantTin: "2332434232",
      Status: "Paid",
      callSign: "TR1876",

      subTable: {
        quarrantine: {
          appId: "QRN20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        immegration: {
          appId: "IMG20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        customs: {
          appId: "CUS20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
      },
      header: {
        voyageDetails: {
          voyageNo: "",
          voyageName: "",
          voyageUOM: "LT",
          arrivalPort: "PAS",
          arrivalDate: "",
          arrivalTime: "",
          lastPortCountry: "Thailand",

          departurePort: "",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "",
        },
        shipDetails: {
          impNo: "ERT3344",
          shipName: "Moon Light",
          shipCountry: "US",
          callSign: "TR1234",
          applicantTin: "2332434232",
        },
      },
      flaForm: [],
      others: {
        vaccination: {
          list: [
            {
              familyName: "JOHN DOE",
              givenName: "ALEX DOE",
              rankRating: "Rank1",
              nationality: "Vietnamese",
              dob: "04/20/1985",
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: [],
    },
    {
      appId: "EPPPAP20201284",
      version: "1",
      arrivalPort: "PAS",
      submitDate: "2020-12-31",
      ETA: "2021-01-01",
      ETD: "2021-01-13",
      ecrNO: "ECR20201244",

      impNo: "ERT3378",
      shipName: "Alabama",
      shipCountry: "US",
      lastPortCountry: "Thailand",
      applicantTin: "2332434232",
      Status: "Submitted",
      callSign: "TR9874",
      subTable: {
        quarrantine: {
          appId: "QRN20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        immegration: {
          appId: "IMG20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        customs: {
          appId: "CUS20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
      },
      header: {
        voyageDetails: {
          voyageNo: "",
          voyageName: "",

          arrivalPort: "PAS",
          arrivalDate: "",
          arrivalTime: "",
          lastPortCountry: "Thailand",

          departurePort: "",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "",
        },
        shipDetails: {
          impNo: "ERT3344",
          shipName: "Moon Light",
          shipCountry: "US",
          callSign: "TR1234",
          applicantTin: "2332434232",
        },
      },
      flaForm: [],
      others: {
        vaccination: {
          list: [
            {
              familyName: "JOHN DOE",
              givenName: "ALEX DOE",
              rankRating: "Rank1",
              nationality: "Vietnamese",
              dob: "04/20/1985",
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: [],
    },
    {
      appId: "EPPPAP20201234",
      version: "1",
      arrivalPort: "PAS",
      submitDate: "2021-01-01",
      ETA: "2021-01-11",
      ETD: "2021-01-23",
      ecrNO: "ECR20201264",

      impNo: "ERT3323",
      shipName: "Sea Master",
      shipCountry: "US",
      lastPortCountry: "Denmark",
      applicantTin: "2332434232",
      Status: "Rejected",
      callSign: "TR1267",
      subTable: {
        quarrantine: {
          appId: "QRN20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        immegration: {
          appId: "IMG20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        customs: {
          appId: "CUS20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
      },
      header: {
        voyageDetails: {
          voyageNo: "",
          voyageName: "",
          voyageUOM: "LT",
          arrivalPort: "PAS",
          arrivalDate: "",
          arrivalTime: "",
          lastPortCountry: "Thailand",

          departurePort: "",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "",
        },
        shipDetails: {
          impNo: "ERT3344",
          shipName: "Moon Light",
          shipCountry: "US",
          callSign: "TR1234",
          applicantTin: "2332434232",
        },
      },
      flaForm: [],
      others: {
        vaccination: {
          list: [
            {
              familyName: "JOHN DOE",
              givenName: "ALEX DOE",
              rankRating: "Rank1",
              nationality: "Vietnamese",
              dob: "04/20/1985",
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: [],
    },
    {
      appId: "EPPPAP20201288",
      version: "1",
      arrivalPort: "PAS",
      submitDate: "2021-01-10",
      ETA: "2021-01-11",
      ETD: "2021-01-23",
      ecrNO: "ECR20201274",

      impNo: "ERT33456",
      shipName: "Maersk Alabama",
      shipCountry: "US",
      lastPortCountry: "Moracco",
      applicantTin: "2332434232",
      Status: "Submitted",
      callSign: "TR098234",
      subTable: {
        quarrantine: {
          appId: "QRN20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        immegration: {
          appId: "IMG20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        customs: {
          appId: "CUS20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
      },
      header: {
        voyageDetails: {
          voyageNo: "",
          voyageName: "",
          voyageUOM: "LT",
          arrivalPort: "PAS",
          arrivalDate: "",
          arrivalTime: "",
          lastPortCountry: "Moracco",

          departurePort: "",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "",
        },
        shipDetails: {
          impNo: "ERT33456",
          shipName: "Maersk Alabama",
          shipCountry: "US",
          callSign: "TR098234",
          applicantTin: "2332434232",
        },
      },
      flaForm: [],
      others: {
        vaccination: {
          list: [
            {
              familyName: "JOHN DOE",
              givenName: "ALEX DOE",
              rankRating: "Rank1",
              nationality: "Vietnamese",
              dob: "04/20/1985",
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: [],
    },
    {
      appId: "EPPPAP20201233",
      version: "1",
      arrivalPort: "PAP",
      submitDate: "2021-01-11",
      ETA: "2021-01-21",
      ETD: "2021-01-23",
      ecrNO: "ECR20210123",

      impNo: "ERT3347",
      shipName: "Ship Cap",
      shipCountry: "US",
      lastPortCountry: "Togo",
      applicantTin: "2332434232",
      submitDate: "20-Nov-20",
      Status: "Approved",
      callSign: "RT1234",
      subTable: {
        quarrantine: {
          appId: "QRN20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        immegration: {
          appId: "IMG20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
        customs: {
          appId: "CUS20201112",
          version: "1",
          submitDate: "20-Nov-20",
          Status: "Approved",
        },
      },
      header: {
        voyageDetails: {
          voyageNo: "",
          voyageName: "",
          voyageUOM: "LT",
          arrivalPort: "PAS",
          arrivalDate: "",
          arrivalTime: "",
          lastPortCountry: "Togo",

          departurePort: "",
          departureDate: "",
          DepartureTime: "",
          nextPortCountry: "",
        },
        shipDetails: {
          impNo: "ERT3347",
          shipName: "Ship Cap",
          shipCountry: "US",
          callSign: "RT1234",
          applicantTin: "2332434232",
        },
      },
      flaForm: [],
      others: {
        vaccination: {
          list: [
            {
              familyName: "JOHN DOE",
              givenName: "ALEX DOE",
              rankRating: "Rank1",
              nationality: "Vietnamese",
              dob: "04/20/1985",
              signature: "",
              effects: "Cigars",
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
            portOfDisemberkation: "CAMBODIA",
          },
        },
      },
      supportingDocs: [],
    },
  ],
};

Mock.onGet("/api/process/all").reply((config) => {
  return [200, processApplicationDB.applications];
});

Mock.onGet("/api/process").reply((config) => {
  const id = config.data;
  const response = processApplicationDB.applications.find(
    (app) => app.appId === id
  );
  return [200, response];
});

Mock.onPost("/api/process/delete").reply((config) => {
  let app = JSON.parse(config.data);
  let index = { i: 0 };
  processApplicationDB.applications.forEach((element) => {
    if (element.id === app.appId) {
      return [200, processApplicationDB.applications.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, processApplicationDB.applications];
});

Mock.onPost("/api/process/update").reply((config) => {
  let app = JSON.parse(config.data);
  let index = { i: 0 };
  processApplicationDB.applications.forEach((element) => {
    if (element.id === app.appId) {
      processApplicationDB.applications[index.i] = app;
      return [200, processApplicationDB.applications];
    }
    index.i++;
  });
  return [200, processApplicationDB.applications];
});

Mock.onPost("/api/process/add").reply((config) => {
  let app = JSON.parse(config.data);
  processApplicationDB.applications.push(app);
  return [200, processApplicationDB.applications];
});
