import Mock from "../mock";

/**{ value: 'm01', desc: 'Vaga Maersk', shipLineId: 'maersk', imoNo: '9778545', shipType: 'CONTAINER', callSign: 'OWYC1', country: 'DK' },
    { value: 'm02', desc: 'Vilnia Maersk', shipLineId: 'maersk', imoNo: '9778533', shipType: 'CONTAINER', callSign: 'OWYC2', country: 'DK' },
    { value: 'o01', desc: 'OOCL Asia', shipLineId: 'oocl', imoNo: '9300790', shipType: 'CARGO', callSign: 'VRBQ6', country: 'HK' },
    { value: 'o02', desc: 'OOCL Singapore', shipLineId: 'oocl', imoNo: '9628001', shipType: 'CARGO', callSign: 'VRMX7', country: 'HK' } */
export const shipFleetDB = {
  list: [
    {
      regNo: "SR18983876",
      regPort: "PPAP",
      imoNo: "9778545",
      vesselID: "INT1234",
      vesselType: "CONTAINER",
      vesselName: "Vaga Maersk",
      vesselCountry: "DK",
      createdBy: "TRAD1",
      shipType: "CONTAINER",
      originCountry: "KH",
      flag: "SG",
      portOfRegistry: "KHKKO",
      createdDate: "2021-02-21T12:30:45",
      expiryDate: "2021-08-21T12:30:45",
      status: "S",
      profileStatus: 'I',
      qrc: "QRC",
      callsign: "OWYC1",
      mmsi: "MMSI",
      offNo: "13456",
      dnvlglreg: "DN-GL-REG01",
      builtYear: "2020",
      builtAddr1: "Addr1",
      builtAddr2: "Addr2",
      telephone: "912345667",
      fax: "234522",
      email: "abc@gmail.com",

      containers: "2194",
      count: "1",
      safeEquCount: "27",
      cargoCraneDesc: "SWL 450kN @ 4,0 - 29.5 m",
      cargocranes: "3",
      refersockets: "490",
      bollardpull: "Panama 614 kN",
      
      airDraughtCranes: "airDraughtCranes",
      airdraught: "airdraught",
      depthmoulded: "4",
      draughtmoulded: "4",
      bredthmoulded: "4",
      lengthbwperpendicular: "2",
      lengthoverall: "2",
      netTonnage: "10",
      grossTonnage: "10",
      addr1: "Addr1",
      addr2: "Addr2",
      name: "Michael",
      regno: "Reg No1",

      lightVessel: "10056,7",
      summerDisplacement: "35339,4t",
      sdwt: "25293,7t",
      sDraught: "9,474m",
      sFreeboard: "7,026m",
      fwAllowance: "0,198 m",
      fwFreeboard: "7,215m",
      depthmoulded: "9,824m",
      airdraught: "24899,8t",
      wDraught: "9,252m",
      mainEngine: "Hu Dong, MAN 16669CE-02",
      bowThruster: "CPP",
      thrusterCapacity: "1300KW",
      rudderType: "Ordinary",
      propeller: "Fix, 5 bladed"

    },
    {
      regNo: "SR8473358",
      regPort: "PAS",
      imoNo: "9778533",
      vesselID: "INT3334",
      vesselType: "CONTAINER",
      vesselName: "Vilnia Maersk",
      vesselCountry: "DK",
      createdBy: "DEV1",
      shipType: "CONTAINER",
      originCountry: "KH",
      flag: "SG",
      portOfRegistry: "KHKKO",
      createdDate: "2021-02-21T12:30:45",
      expiryDate: "2021-08-21T12:30:45",
      status: "S",
      profileStatus: 'I',
      qrc: "QRC",
      callsign: "OWYC2",
      mmsi: "MMSI",
      offNo: "13456",
      dnvlglreg: "DN-GL-REG01",
      builtYear: "2020",
      builtAddr1: "Addr1",
      builtAddr2: "Addr2",
      telephone: "912345667",
      fax: "234522",
      email: "abc@gmail.com",
      containers: "2194",
      count: "1",
      safeEquCount: "27",
      cargoCraneDesc: "SWL 450kN @ 4,0 - 29.5 m",
      cargocranes: "3",
      refersockets: "490",
      bollardpull: "Panama 614 kN",
      airDraughtCranes: "airDraughtCranes",
      airdraught: "airdraught",
      depthmoulded: "4",
      draughtmoulded: "4",
      bredthmoulded: "4",
      lengthbwperpendicular: "2",
      lengthoverall: "2",
      netTonnage: "10",
      grossTonnage: "10",
      addr1: "Addr1",
      addr2: "Addr2",
      name: "Michael",
      regno: "Reg No1",

      lightVessel: "10056,7",
      summerDisplacement: "35339,4t",
      sdwt: "25293,7t",
      sDraught: "9,474m",
      sFreeboard: "7,026m",
      fwAllowance: "0,198 m",
      fwFreeboard: "7,215m",
      depthmoulded: "9,824m",
      airdraught: "24899,8t",
      wDraught: "9,252m",
      mainEngine: "Hu Dong, MAN 16669CE-02",
      bowThruster: "CPP",
      thrusterCapacity: "1300KW",
      rudderType: "Ordinary",
      propeller: "Fix, 5 bladed"
    },
    {
      regNo: "SR15491594",
      regPort: "PPAP",
      imoNo: "9300790",
      vesselID: "INT3411",
      vesselType: "CARGO",
      vesselName: "OOCL Asia",
      vesselCountry: "HK",
      createdBy: "TRAD2",
      shipType: "CARGO",
      originCountry: "KH",
      flag: "SG",
      portOfRegistry: "KHKKO",
      createdDate: "2021-02-22T02:30:45",
      expiryDate: "2021-08-22T02:30:45",
      status: "S",
      profileStatus: 'A',
      qrc: "QRC",
      callsign: "VRBQ6",
      mmsi: "MMSI",
      offNo: "13456",
      dnvlglreg: "DN-GL-REG01",
      builtYear: "2020",
      builtAddr1: "Addr1",
      builtAddr2: "Addr2",
      telephone: "912345667",
      fax: "234522",
      email: "abc@gmail.com",
      containers: "2194",
      count: "1",
      safeEquCount: "27",
      cargoCraneDesc: "SWL 450kN @ 4,0 - 29.5 m",
      cargocranes: "3",
      refersockets: "490",
      bollardpull: "Panama 614 kN",
      airDraughtCranes: "airDraughtCranes",
      airdraught: "airdraught",
      depthmoulded: "4",
      draughtmoulded: "4",
      bredthmoulded: "4",
      lengthbwperpendicular: "2",
      lengthoverall: "2",
      netTonnage: "10",
      grossTonnage: "10",
      addr1: "Addr1",
      addr2: "Addr2",
      name: "Michael",
      regno: "Reg No1",

      lightVessel: "10056,7",
      summerDisplacement: "35339,4t",
      sdwt: "25293,7t",
      sDraught: "9,474m",
      sFreeboard: "7,026m",
      fwAllowance: "0,198 m",
      fwFreeboard: "7,215m",
      depthmoulded: "9,824m",
      airdraught: "24899,8t",
      wDraught: "9,252m",
      mainEngine: "Hu Dong, MAN 16669CE-02",
      bowThruster: "CPP",
      thrusterCapacity: "1300KW",
      rudderType: "Ordinary",
      propeller: "Fix, 5 bladed"
    },
    {
      regNo: "SR18548291",
      regPort: "PAS",
      imoNo: "9628001",
      vesselID: "INT3364",
      vesselType: "CARGO",
      vesselName: "OOCL Singapore",
      vesselCountry: "HK",
      originCountry: "KH",
      flag: "SG",
      portOfRegistry: "KHKKO",
      createdBy: "DEV2",
      shipType: "CARGO",
      createdDate: "2021-02-21T12:30:45",
      expiryDate: "2021-08-21T12:30:45",
      status: "A",
      profileStatus: 'A',
      qrc: "QRC",
      callsign: "VRMX7",
      mmsi: "MMSI",
      offNo: "13456",
      dnvlglreg: "DN-GL-REG01",
      builtYear: "2020",
      builtAddr1: "Addr1",
      builtAddr2: "Addr2",
      telephone: "912345667",
      fax: "234522",
      email: "abc@gmail.com",
      containers: "2194",
      count: "1",
      safeEquCount: "27",
      cargoCraneDesc: "SWL 450kN @ 4,0 - 29.5 m",
      cargocranes: "3",
      refersockets: "490",
      bollardpull: "Panama 614 kN",
      airDraughtCranes: "airDraughtCranes",
      airdraught: "airdraught",
      depthmoulded: "4",
      draughtmoulded: "4",
      bredthmoulded: "4",
      lengthbwperpendicular: "2",
      lengthoverall: "2",
      netTonnage: "10",
      grossTonnage: "10",
      addr1: "Addr1",
      addr2: "Addr2",
      name: "Michael",
      regno: "Reg No1",

      lightVessel: "10056,7",
      summerDisplacement: "35339,4t",
      sdwt: "25293,7t",
      sDraught: "9,474m",
      sFreeboard: "7,026m",
      fwAllowance: "0,198 m",
      fwFreeboard: "7,215m",
      depthmoulded: "9,824m",
      airdraught: "24899,8t",
      wDraught: "9,252m",
      mainEngine: "Hu Dong, MAN 16669CE-02",
      bowThruster: "CPP",
      thrusterCapacity: "1300KW",
      rudderType: "Ordinary",
      propeller: "Fix, 5 bladed"
    },

  ],
};

Mock.onGet("/api/user/all").reply((config) => {
  return [200, shipFleetDB.list];
});

Mock.onGet("/api/user").reply((config) => {
  const id = config.data;
  const response = shipFleetDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/user/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  shipFleetDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, shipFleetDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, shipFleetDB.list];
});

Mock.onPost("/api/user/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  shipFleetDB.list.forEach((element) => {
    if (element.id === user.id) {
      shipFleetDB.list[index.i] = user;
      return [200, shipFleetDB.list];
    }
    index.i++;
  });
  return [200, shipFleetDB.list];
});

Mock.onPost("/api/user/add").reply((config) => {
  let user = JSON.parse(config.data);
  shipFleetDB.list.push(user);
  return [200, shipFleetDB.list];
});
