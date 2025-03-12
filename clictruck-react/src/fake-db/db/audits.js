import Mock from "../mock";
const data = {
    "iTotalRecords": 4,
    "iTotalDisplayRecords": 4,
    "aaData": [
      {
        "otherLangDesc": null,
        "coreMstLocale": null,
        "audtId": "1677569676138",
        "audtEvent": "JOB SUBMIT EVENT",
        "audtTimestamp": 1677569676000,
        "audtAccnid": "ALKNAR",
        "audtUid": "ALKNAR_U002",
        "audtUname": "Ivan Sitorus",
        "audtRemoteIp": "-",
        "audtReckey": "CKDOJ2023022827725",
        "audtParam1": "-",
        "audtParam2": "-",
        "audtParam3": "-",
        "audtRemarks": "-"
      },
      {
        "otherLangDesc": null,
        "coreMstLocale": null,
        "audtId": "3651229450452966255802",
        "audtEvent": "DOICOJOB MODIFY",
        "audtTimestamp": 1677569676000,
        "audtAccnid": null,
        "audtUid": "ALKNAR_U002",
        "audtUname": "Ivan Sitorus",
        "audtRemoteIp": "192.168.2.1",
        "audtReckey": "CKDOJ2023022827725",
        "audtParam1": "TCKJOBDOICO",
        "audtParam2": null,
        "audtParam3": null,
        "audtRemarks": "MODIFY"
      },
      {
        "otherLangDesc": null,
        "coreMstLocale": null,
        "audtId": "1677569671642",
        "audtEvent": "JOB CREATE EVENT",
        "audtTimestamp": 1677569672000,
        "audtAccnid": "ALKNAR",
        "audtUid": "ALKNAR_U002",
        "audtUname": "Ivan Sitorus",
        "audtRemoteIp": "-",
        "audtReckey": "CKDOJ2023022827725",
        "audtParam1": "-",
        "audtParam2": "-",
        "audtParam3": "-",
        "audtRemarks": "-"
      },
      {
        "otherLangDesc": null,
        "coreMstLocale": null,
        "audtId": "3651224951806368817169",
        "audtEvent": "DOICOJOB CREATE",
        "audtTimestamp": 1677569672000,
        "audtAccnid": null,
        "audtUid": "ALKNAR_U002",
        "audtUname": "Ivan Sitorus",
        "audtRemoteIp": "192.168.2.1",
        "audtReckey": "CKDOJ2023022827725",
        "audtParam1": "TCKJOBDOICO",
        "audtParam2": null,
        "audtParam3": null,
        "audtRemarks": "CREATE"
      }
    ]
  }

  Mock.onGet(/api\/common\/entity\/auditLog\/list\/?.*/).reply((config) => {
    return [200, data];
  });