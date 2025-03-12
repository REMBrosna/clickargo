import Mock from "../mock";

const data = {
    "iTotalRecords": 2,
    "iTotalDisplayRecords": 2,
    "aaData": [
      {
        "rtId": "2023022390625",
        "rtNumVeh": "2",
        "rtStatus": "ACV",
        "rtName": "DERIAN",
        "rtDtStart": 1677121978000,
        "rtDtEnd": 1677121978000,
        "rtRemark": "remark",
        "rtDtCreate": 1677121978000,
        "rtDtCreateBy": "ALKNAR_U001",
        "rtDtLupd": 1677121998000,
        "rtDtUpdateBy": "ALKNAR_U001",
        "action": null,
      },
      {
        "rtId": "2023022390626",
        "rtNumVeh": "2",
        "rtStatus": "ACV",
        "rtName": "PRATAMA",
        "rtDtStart": 1677121978000,
        "rtDtEnd": 1677121978000,
        "rtRemark": "remark",
        "rtDtCreate": 1677121978000,
        "rtDtCreateBy": "ALKNAR_U001",
        "rtDtLupd": 1677121998000,
        "rtDtUpdateBy": "ALKNAR_U001",
        "action": null,
      },
    ]
  }

  Mock.onGet(/api\/v1\/clickargo\/clictruck\/rental\/ren\/list\/?.*/).reply((config) => {
    return [200, data];
  });