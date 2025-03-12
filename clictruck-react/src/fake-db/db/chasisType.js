import Mock from "../mock";

const data = {
    "timestamp": "2023-03-28T17:56:39.075+0700",
    "status": 200,
    "error": null,
    "success": "Data Master class com.guudint.clickargo.clictruck.master.service.impl.CkCtMstChassisTypeServiceImpl",
    "data": [
        {
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
        {
            "otherLangDesc": null,
            "coreMstLocale": null,
            "chtyId": "40FT",
            "chtyName": "40FT CHASSIS",
            "chtyDesc": "40FT CHASSIS",
            "chtyDescOth": "40FT CHASSIS",
            "chtyStatus": "A",
            "chtyDtCreate": 1601485200000,
            "chtyUidCreate": "SYS",
            "chtyDtLupd": 1601485200000,
            "chtyUidLupd": null,
            "tckCtVehs": []
        }
    ]
}

Mock.onGet('/api/v1/clickargo/clictruck/master/chassis-type').reply((config) => {
    return [200, data];
  });