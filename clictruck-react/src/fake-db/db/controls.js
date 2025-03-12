import Mock from "../mock";

const data = [
    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ctrlId": "1",
      "ctrlEntityState": "DRF",
      "ctrlEntityType": "JOB_DOI_CO",
      "ctrlAction": "SAVE",
      "ctrlViewType": "EDIT",
      "ctrlAccn": "",
      "ctrlAccnType": "ACC_TYPE_CO",
      "ctrlUsrRole": "OFFICER",
      "ctrlFormActionMenu": null,
      "ctrlStatus": "A",
      "ctrlDtCreate": 1668474655000,
      "ctrlUidCreate": "SYS",
      "ctrlDtLupd": 1668474655000,
      "ctrlUidLupd": "SYS"
    },
    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ctrlId": "1667813002371",
      "ctrlEntityState": "DRF",
      "ctrlEntityType": "JOB_DOI_CO",
      "ctrlAction": "SAVE",
      "ctrlViewType": "EDIT",
      "ctrlAccn": "",
      "ctrlAccnType": "ACC_TYPE_CO",
      "ctrlUsrRole": "OFFICER",
      "ctrlFormActionMenu": null,
      "ctrlStatus": "A",
      "ctrlDtCreate": 1664557200000,
      "ctrlUidCreate": "SYS",
      "ctrlDtLupd": 1664557200000,
      "ctrlUidLupd": "SYS"
    },
    {
      "otherLangDesc": null,
      "coreMstLocale": null,
      "ctrlId": "2",
      "ctrlEntityState": "DRF",
      "ctrlEntityType": "JOB_DOI_CO",
      "ctrlAction": "EXIT",
      "ctrlViewType": "EDIT",
      "ctrlAccn": "",
      "ctrlAccnType": "ACC_TYPE_CO",
      "ctrlUsrRole": "OFFICER",
      "ctrlFormActionMenu": null,
      "ctrlStatus": "A",
      "ctrlDtCreate": 1668474655000,
      "ctrlUidCreate": "SYS",
      "ctrlDtLupd": 1668474655000,
      "ctrlUidLupd": "SYS"
    }
  ];

  Mock.onPost("/api/v1/clickargo/controls/").reply(config => {
    const response = data;
    return [200, response];
  });