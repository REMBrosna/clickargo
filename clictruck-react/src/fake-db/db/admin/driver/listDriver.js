import Mock from "../../../mock";

const data = {
    "iTotalRecords": 10,
    "iTotalDisplayRecords": 213,
    "aaData": [
      {
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
      },
      {
        "otherLangDesc": "",
        "coreMstLocale": null,
        "drvId": "93939393323",
        "drvName": "PRATAMA",
        "drvLicenseNo": "8393",
        "drvLicenseExpiry": 1680002620265,
        "drvLicensePhotoName": "PRATAMA",
        "drvLicensePhotoLoc": "/93839/93",
        "drvEmail": "PRATAMA@EMAIL.COM",
        "drvPhone": "938398",
        "drvMobileId": "3343",
        "drvMobilePassword": "3333",
        "drvStatus": "A",
        "drvDtCreate": 1680002620265,
        "drvUidCreate": "PRATAMA",
        "drvDtLupd": 1680002620265,
        "drvUidLupd": "PRATAMA",
        "tcoreAccn": null
      },
    ]
  }

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/administrator\/driver\/list\/?.*/).reply((config) => {
    return [200, data];
  });