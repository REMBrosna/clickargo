import Mock from "../../../mock";

const data = {
  "otherLangDesc": "",
  "coreMstLocale": null,
  "locId": "16801501037322",
  "locName": "DERIAN",
  "locAddress": " JL. PRATAMA",
  "locDtStart": 1680150103732,
  "locDtEnd": 1680150103732,
  "locRemarks": "REMARK",
  "locStatus": "I",
  "locDtCreate": 1680150103732,
  "locUidCreate": "DERIAN",
  "locDtLupd": 1680150103732,
  "locUidLupd": "DERIAN",
  "tckCtMstLocationType": null,
  "tcoreAccn": null
  }

  const status = {
    data: {
      data: {
        "otherLangDesc": "",
        "coreMstLocale": null,
        "locId": "16801501037322",
        "locName": "DERIAN",
        "locAddress": " JL. PRATAMA",
        "locDtStart": 1680150103732,
        "locDtEnd": 1680150103732,
        "locRemarks": "REMARK",
        "locStatus": "I",
        "locDtCreate": 1680150103732,
        "locUidCreate": "DERIAN",
        "locDtLupd": 1680150103732,
        "locUidLupd": "DERIAN",
        "tckCtMstLocationType": null,
        "tcoreAccn": null
        }
    }
  }

  
  Mock.onDelete(/api\/v1\/clickargo\/clictruck\/administrator\/location\/.*/).reply((config) => {
    return [200, data];
  });
  
  Mock.onPut(/api\/v1\/clickargo\/clictruck\/administrator\/location\/.*/).reply((config) => {
    return [200, data];
  });
  
  Mock.onGet(/api\/v1\/clickargo\/clictruck\/administrator\/location\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onPost("/api/v1/clickargo/clictruck/administrator/location").reply((config) => {
    return [200, data];
  });

  Mock.onPost("/api/v1/clickargo/clictruck/administrator/location/16801501037322/deactive").reply((config) => {
    return [200, status];
  });

  Mock.onPost("/api/v1/clickargo/clictruck/administrator/location/16801501037322/active").reply((config) => {
    return [200, status];
  });