import Mock from "../mock";

const data = {
    "timestamp": "2023-03-28T17:56:50.717+0700",
    "status": 200,
    "error": null,
    "success": "Data Master class com.guudint.clickargo.clictruck.master.service.impl.CkCtMstVehTypeServiceImpl",
    "data": [
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "vhtyId": "CDD",
            "vhtyName": "CDD",
            "vhtyDesc": "CDD",
            "vhtyDescOth": "CDD",
            "vhtyStatus": "A",
            "vhtyDtCreate": 1601485200000,
            "vhtyUidCreate": "SYS",
            "vhtyDtLupd": 1601485200000,
            "vhtyUidLupd": "SYS",
            "ckCtTripRates": [],
            "ckCtRentalVehs": [],
            "ckCtVehs": []
        },
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "vhtyId": "CDD LONG",
            "vhtyName": "CDD LONG",
            "vhtyDesc": "CDD LONG",
            "vhtyDescOth": "CDD LONG",
            "vhtyStatus": "A",
            "vhtyDtCreate": 1601485200000,
            "vhtyUidCreate": "SYS",
            "vhtyDtLupd": 1601485200000,
            "vhtyUidLupd": "SYS",
            "ckCtTripRates": [],
            "ckCtRentalVehs": [],
            "ckCtVehs": []
        },
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "vhtyId": "CDE",
            "vhtyName": "CDE",
            "vhtyDesc": "CDE",
            "vhtyDescOth": "CDE",
            "vhtyStatus": "A",
            "vhtyDtCreate": 1601485200000,
            "vhtyUidCreate": "SYS",
            "vhtyDtLupd": 1601485200000,
            "vhtyUidLupd": "SYS",
            "ckCtTripRates": [],
            "ckCtRentalVehs": [],
            "ckCtVehs": []
        },
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "vhtyId": "CONTAINER 20FT",
            "vhtyName": "CONTAINER 20FT",
            "vhtyDesc": "CONTAINER 20FT",
            "vhtyDescOth": "CONTAINER 20FT",
            "vhtyStatus": "A",
            "vhtyDtCreate": 1601485200000,
            "vhtyUidCreate": "SYS",
            "vhtyDtLupd": 1601485200000,
            "vhtyUidLupd": "SYS",
            "ckCtTripRates": [],
            "ckCtRentalVehs": [],
            "ckCtVehs": []
        },
        {
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
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "vhtyId": "VAN",
            "vhtyName": "VAN",
            "vhtyDesc": "VAN",
            "vhtyDescOth": "VAN",
            "vhtyStatus": "A",
            "vhtyDtCreate": 1601485200000,
            "vhtyUidCreate": "SYS",
            "vhtyDtLupd": 1601485200000,
            "vhtyUidLupd": "SYS",
            "ckCtTripRates": [],
            "ckCtRentalVehs": [],
            "ckCtVehs": []
        },
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "vhtyId": "WING BOX",
            "vhtyName": "WING BOX",
            "vhtyDesc": "WING BOX",
            "vhtyDescOth": "WING BOX",
            "vhtyStatus": "A",
            "vhtyDtCreate": 1601485200000,
            "vhtyUidCreate": "SYS",
            "vhtyDtLupd": 1601485200000,
            "vhtyUidLupd": "SYS",
            "ckCtTripRates": [],
            "ckCtRentalVehs": [],
            "ckCtVehs": []
        }
    ]
}

Mock.onGet('/api/v1/clickargo/clictruck/master/veh-type').reply((config) => {
    return [200, data];
  });