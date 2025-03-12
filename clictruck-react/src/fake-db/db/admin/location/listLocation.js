import Mock from "../../../mock";

const data = {
  iTotalRecords: 10,
  iTotalDisplayRecords: 213,
  aaData: [
    {
      otherLangDesc: "",
      coreMstLocale: null,
      locId: "16801501037322",
      locName: "DERIAN",
      locAddress: " JL. PRATAMA",
      locDtStart: 1680150103732,
      locDtEnd: 1680150103732,
      locRemarks: "REMARK",
      locStatus: "I",
      locDtCreate: 1680150103732,
      locUidCreate: "DERIAN",
      locDtLupd: 1680150103732,
      locUidLupd: "DERIAN",
      tckCtMstLocationType: null,
      tcoreAccn: null,
    },
    {
      otherLangDesc: "",
      coreMstLocale: null,
      locId: "16801501037322",
      locName: "PRATAMA",
      locAddress: " JL. PRATAMA",
      locDtStart: 1680150103732,
      locDtEnd: 1680150103732,
      locRemarks: "REMARK",
      locStatus: "A",
      locDtCreate: 1680150103732,
      locUidCreate: "DERIAN",
      locDtLupd: 1680150103732,
      locUidLupd: "DERIAN",
      tckCtMstLocationType: null,
      tcoreAccn: null,
    },
    {
      "otherLangDesc": "",
      "coreMstLocale": null,
      "locId": "",
      "locName": "",
      "locAddress": "",
      "locDtStart": 1681201683992,
      "locDtEnd": 1681201683992,
      "locRemarks": "",
      "locStatus": "",
      "locDtCreate": 1681201683992,
      "locUidCreate": "",
      "locDtLupd": 1681201683992,
      "locUidLupd": "",
      "tckCtMstLocationType": null,
      "tcoreAccn": null
  }
  ],
};

Mock.onGet(/api\/v1\/clickargo\/clictruck\/administrator\/location\/list\/?.*/).reply(
  (config) => {
    return [200, data];
  }
);
