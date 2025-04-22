import Mock from "../mock";

export const ucrDB = {
  list: [
    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ucrNo": "PEDI202012123",
      "shipName": "Vaga Maersk",
      "imoNo": "9778545",
      "eta": "2020-12-20T12:45:30",
      "etd": "2021-01-01T12:54:29",
      "submitDate": "2020-12-12T12:43:28",
      "shippingLine": "maersk",
      "ship": "m01",
      "shipType": "CONTAINER",
      "voyageType": "Inward",
      "callSign": "OWYC1",
      "terminal": "ppap9",
      "ctyDtCreate": "2020/11/30",
      "ctyUidCreate": "SYS",
      "ctyDtLupd": "2020/11/30",
      "ctyUidLupd": "SYS",
      "port": "KHPNH",
      "country": "DK",
      "inwardVoyage": "INW-2345324",
      "outwardVoyage": "",
      "status": "A",
      "voyageName": "Voyage Master"
    },

    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ucrNo": "PEDI202013123",
      "shipName": "OOCL Asia",
      "terminal": "pas1",
      "imoNo": "9300790",
      "eta": "2020-12-21T13:30:50",
      "etd": "2021-01-02T14:40:40",
      "submitDate": "2020-12-15T12:30:45",
      "shippingLine": "OOCL",
      "ship": "o01",
      "shipType": "CARGO",
      "voyageType": "Outward",
      "callSign": "VRBQ6",
      "ctyDtCreate": "2020/11/30",
      "ctyUidCreate": "SYS",
      "ctyDtLupd": "2020/11/30",
      "ctyUidLupd": "SYS",
      "port": "KHKOS",
      "country": "CAM",
      "inwardVoyage": "",
      "outwardVoyage": "OTW-768445",
      "status": "A",
      "voyageName": "Voyage Master"
    },
    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ucrNo": "PEDI202012124",
      "shipName": "OOCL Singapore",
      "imoNo": "9628001",
      "eta": "2021-02-19T14:30:29",
      "etd": "2021-03-02T12:45:20",
      "submitDate": "2020-12-14T12:34:45",
      "shippingLine": "OOCL",
      "ship": "o02",
      "shipType": "CONTAINER",
      "voyageType": "Inward",
      "callSign": "VRMX7",
      "terminal": "ppap9",
      "ctyDtCreate": "2020/11/30",
      "ctyUidCreate": "SYS",
      "ctyDtLupd": "2020/11/30",
      "ctyUidLupd": "SYS",
      "port": "KHPNH",
      "country": "DK",
      "inwardVoyage": "INW-2345324",
      "outwardVoyage": "",
      "status": "A",
      "voyageName": "Voyage Master"
    },
    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ucrNo": "PEDI2020121221",
      "shipName": "OOCL Singapore",
      "imoNo": "9628001",
      "eta": "2021-02-19T14:30:29",
      "etd": "2021-03-02T12:45:20",
      "submitDate": "2020-12-14T12:34:45",
      "shippingLine": "OOCL",
      "ship": "o02",
      "shipType": "CONTAINER",
      "voyageType": "Inward",
      "callSign": "VRMX7",
      "terminal": "KHPAS05",
      "ctyDtCreate": "2020/11/30",
      "ctyUidCreate": "SYS",
      "ctyDtLupd": "2020/11/30",
      "ctyUidLupd": "SYS",
      "port": "KHPAS",
      "country": "DK",
      "inwardVoyage": "INW-2345324",
      "outwardVoyage": "",
      "status": "D",
      "voyageName": "Voyage Master"
    },

  ],
};


export const clearancePreArrival = {
  list: [
    {
      "appId": "CAM20210223",
      "version": "1",
      "submitDate": "20-02-2021",
      "status": "Processing",
      "borderClearance": "2",
      "portClearance": "0"


    },


  ],
};

export const clearanceDeclaration = {
  list: [
    {
      "appId": "DOS20210223",
      "version": "1",
      "submitDate": "20-02-2021",
      "status": "Approved",
      "expiryDate": "31-12-2021",



    },


  ],
};

export const shipDetails = [
  {
    ucrNo: 'PEDI202012123',
    netTonnage: '1200',
    netTonnageUom: 'TS',
    grossTonnage: '1200',
    grossTonnageUom: 'TS',
    deadWeight: '100',
    deadWeightUom: 'TS',
    length: '1000',
    lengthUom: 'MTR',
    breadth: '1200',
    breadthUom: 'CM',
    pilotStation: '1200',
    pilotStationUom: 'NPR',
    pilotOnBoard: '1200',
    pilonOnBoardUom: 'NPR',
    arrivalDraft: '1200',
    arrivalDraftUom: 'NPR'
  },
  {
    ucrNo: 'PEDI202013123',
    netTonnage: '1200',
    netTonnageUom: 'TS',
    grossTonnage: '1200',
    grossTonnageUom: 'TS',
    deadWeight: '100',
    deadWeightUom: 'TS',
    length: '1000',
    lengthUom: 'MTR',
    breadth: '1200',
    breadthUom: 'CM',
    pilotStation: '1200',
    pilotStationUom: 'NPR',
    pilotOnBoard: '1200',
    pilonOnBoardUom: 'NPR',
    arrivalDraft: '1200',
    arrivalDraftUom: 'NPR'
  },
  {
    ucrNo: 'PEDI202012124',
    netTonnage: '1200',
    netTonnageUom: 'TS',
    grossTonnage: '1200',
    grossTonnageUom: 'TS',
    deadWeight: '100',
    deadWeightUom: 'TS',
    length: '1000',
    lengthUom: 'MTR',
    breadth: '1200',
    breadthUom: 'CM',
    pilotStation: '1200',
    pilotStationUom: 'NPR',
    pilotOnBoard: '1200',
    pilonOnBoardUom: 'NPR',
    arrivalDraft: '1200',
    arrivalDraftUom: 'NPR'
  }

];


export const cargoDetails = {
  list: [
    {
      "ucrNo": "PEDI202012123",
      "cargoType": "General",
      "cargoName": "Type1",
      "quantity": "20",
    },
    {
      "ucrNo": "PEDI202012123",
      "cargoType": "Dangerous",
      "cargoName": "Type2",
      "quantity": "10",
    },
    {
      "ucrNo": "PEDI202012123",
      "cargoType": "Container",
      "cargoName": "",
      "empty": "40",
      "laden": "60",
      "quantity": "100",
    },
    {
      "ucrNo": "PEDI202012124",
      "cargoType": "General",
      "cargoName": "Type1",
      "quantity": "20",
    },
    {
      "ucrNo": "PEDI202012124",
      "cargoType": "Dangerous",
      "cargoName": "Type2",
      "quantity": "10",
    },
    {
      "ucrNo": "PEDI202012124",
      "cargoType": "Container",
      "cargoName": "",
      "empty": "40",
      "laden": "60",
      "quantity": "100",
    },
    {
      "ucrNo": "PEDI202013123",
      "cargoType": "General",
      "cargoName": "Type1",
      "quantity": "20",
    },
  ],
};

export const suppDocs = [
  {
    attUid: 'sys', attSeq: '1',
    attType: 'OTH', attUcrNo: 'PEDI202012123',
    attReferenceid: 'PEDI202012123',
    attName: 'Other Document Name.pdf',
    attDesc: 'File Description',
    attData: ""
  },
  {
    attUid: 'sys', attSeq: '1',
    attType: 'OTH', attUcrNo: 'PEDI202012124',
    attReferenceid: 'PEDI202012124',
    attName: 'Other Document Name.pdf',
    attDesc: 'File Description',
    attData: ""
  },
  {
    attUid: 'sys', attSeq: '1',
    attType: 'OTH', attUcrNo: 'PEDI202013123',
    attReferenceid: 'PEDI202013123',
    attName: 'Other Document Name.pdf',
    attDesc: 'File Description',
    attData: ""
  },
];

Mock.onGet("/api/docRepo/all").reply((config) => {
  return [200, ucrDB.list];
});

Mock.onGet("/api/docRepo").reply((config) => {
  const id = config.data;
  const response = ucrDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/docRepo/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  ucrDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, ucrDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, ucrDB.list];
});

Mock.onPost("/api/docRepo/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  ucrDB.list.forEach((element) => {
    if (element.id === user.id) {
      ucrDB.list[index.i] = user;
      return [200, ucrDB.list];
    }
    index.i++;
  });
  return [200, ucrDB.list];
});

Mock.onPost("/api/docRepo/add").reply((config) => {
  let user = JSON.parse(config.data);
  ucrDB.list.push(user);
  return [200, ucrDB.list];
});