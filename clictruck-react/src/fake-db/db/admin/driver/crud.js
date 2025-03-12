import Mock from "../../../mock";

const data = {
  "otherLangDesc": "",
  "coreMstLocale": null,
  "drvId": "93939393",
  "drvName": "DERIAN",
  "drvLicenseNo": "8393",
  "drvLicenseExpiry": 1680002620265,
  "drvLicensePhotoName": "DERIAN",
  "drvLicensePhotoLoc": "/93839/93",
  "drvEmail": "DERIAN@EMAIL.COM",
  "drvPhone": "938398",
  "drvMobileId": "3343",
  "drvMobilePassword": "3333",
  "drvStatus": "A",
  "drvDtCreate": 1680002620265,
  "drvUidCreate": "DERIAN",
  "drvDtLupd": 1680002620265,
  "drvUidLupd": "DERIAN",
  "tcoreAccn": null
  }

  Mock.onPut(/api\/v1\/clickargo\/clictruck\/administrator\/driver\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onDelete(/api\/v1\/clickargo\/clictruck\/administrator\/driver\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/administrator\/driver\/.*/).reply((config) => {
    return [200, data];
  });

  Mock.onPost("/api/v1/clickargo/clictruck/admin/driver").reply((config) => {
    return [200, data];
  });