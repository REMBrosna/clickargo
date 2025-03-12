import Mock from "../mock";

const data = {
  master: {
    container: 1676940736447,
    hsCodeType: 1676940736447,
    country: 1676940736447,
    attType: 1676940736447,
    userClass: 1676940736447,
    bankbr: 1676940736447,
    contactType: 1676940736447,
    holiday: 1676940736447,
    bank: 1676940736447,
    uom: 1676940736447,
    currencyFx: 1676940736447,
    signType: 1676940736447,
    currency: 1676940736447,
    addrType: 1676940736447,
    incoTerms: 1676940736447,
    package: 1676940736447,
    accnType: 1676940736447,
    borderPost: 1676940736447,
    uomConversion: 1676940736447,
    currencyFxConfig: 1676940736447,
    portType: 1676940736447,
    hsCode: 1676940736447,
    ckMstShipmentType: 1676940741293,
    port: 1676940736447,
    region: 1676940736447,
  },
};

Mock.onGet("/api/co/common/portalCacheTimeStamp").reply((config) => {
    return [200, data];
  });