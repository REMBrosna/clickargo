import Mock from "../mock";

const data = [
  {
    otherLangDesc: null,
    coreMstLocale: null,
    id: {
      otherLangDesc: null,
      coreMstLocale: null,
      uocFromUom: "TNE",
      uocToUom: "KGM",
    },
    uocStatus: "A",
    uocFactor: 1000.0,
    uocOffset: 0.0,
    uocDtCreate: 1543593600000,
    uocUidCreate: "SYS",
    uocDtLupd: 1543593600000,
    uocUidLupd: "SYS",
    TMstUomByUocFromUom: {
      otherLangDesc: null,
      coreMstLocale: null,
      uomCode: "TNE",
      uomStatus: "A",
      uomDescription: "TONNE",
      uomDescriptionOth: null,
      uomDtCreate: 1543593600000,
      uomUidCreate: "SYS",
      uomDtLupd: 1543593600000,
      uomUidLupd: "SYS",
    },
    TMstUomByUocToUom: {
      otherLangDesc: null,
      coreMstLocale: null,
      uomCode: "KGM",
      uomStatus: "A",
      uomDescription: "KILOGRAM",
      uomDescriptionOth: null,
      uomDtCreate: 1543593600000,
      uomUidCreate: "SYS",
      uomDtLupd: 1543593600000,
      uomUidLupd: "SYS",
    },
  },
];

Mock.onGet("/api/co/master/entity/uomConversion").reply((config) => {
    return [200, data];
  });