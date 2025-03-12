import Mock from "../../../mock";

const data = {
  "otherLangDesc": "",
  "coreMstLocale": null,
  "vhId": "1680146508917",
  "vhPlateNo": "F 93893",
  "vhClass": "1",
  "vhPhotoName": "DERIAN",
  "vhPhotoLoc": "/393/3938",
  "vhLength": "1",
  "vhWidth": "2",
  "vhHeight": "3",
  "vhWeight": 11,
  "vhVolume": 22,
  "vhChassisNo": "9383837",
  "vhIsMaintenance": "Y",
  "vhRemarks": "Remark",
  "vhGpsImei": "987383",
  "vhStatus": "I",
  "vhDtCreate": 1680146508917,
  "vhUidCreate": "DERIAN",
  "vhDtLupd": 1680146508917,
  "vhUidLupd": "DERIAN",
  "tckCtMstVehState": {
      "otherLangDesc": "",
      "coreMstLocale": null,
      "vhstId": "",
      "vhstName": "",
      "vhstDesc": "",
      "vhstDescOth": "",
      "vhstStatus": "",
      "vhstDtCreate": 1680146508917,
      "vhstUidCreate": "",
      "vhstDtLupd": 1680146508917,
      "vhstUidLupd": "",
      "tckCtVehs": []
  },
  "tckCtMstChassisType": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "chtyId": "20FT",
      "chtyName": "20FT CHASSIS",
      "chtyDesc": "20FT CHASSIS",
      "chtyDescOth": "20FT CHASSIS",
      "chtyStatus": "A",
      "chtyDtCreate": 1601485200000,
      "chtyUidCreate": "SYS",
      "chtyDtLupd": 1601485200000,
      "chtyUidLupd": "SYS",
      "tckCtVehs": []
  },
  "tckCtMstVehType": {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "vhtyId": "CONTAINER 40FT",
      "vhtyName": "CONTAINER 40FT",
      "vhtyDesc": "CONTAINER 40FT",
      "vhtyDescOth": "CONTAINER 40FT",
      "vhtyStatus": "A",
      "vhtyDtCreate": 1601485200000,
      "vhtyUidCreate": "SYS",
      "vhtyDtLupd": 1601485200000,
      "vhtyUidLupd": "SYS",
      "ckCtTripRates": [],
      "ckCtRentalVehs": [],
      "ckCtVehs": []
  },
  "tcoreAccn": null
}

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/administrator\/vehicle\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onPut(/api\/v1\/clickargo\/clictruck\/administrator\/vehicle\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onDelete(/api\/v1\/clickargo\/clictruck\/administrator\/vehicle\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onPost("/api/v1/clickargo/clictruck/admin/vehicle").reply((config) => {
    return [200, data];
  });

  const erorVal = {
    "status": "VALIDATION_FAILED",
    "data": "{TCkDoi.doiContainerNo:Container No. cannot be empty.,TCkDoi.TCkDo.doVesselNo:Vessel No. cannot be empty.,tcoreAccnByJobAuthorizedPartyAccn:Authorized Party cannot be empty.,fileUpload:BL Document file cannot be empty.,TCkDoi.doiBlNo:BL No. cannot be empty.}",
    "err": {
      "code": -500,
      "msg": "{vhPlateNo:Plate No. cannot be empty.}",
      "stack": null
    }
  }

  Mock.onPost('/api/v1/clickargo/clictruck/admin/vehicle/error').reply((config) => {
    return [500, erorVal];
  });