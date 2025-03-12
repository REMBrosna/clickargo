import Mock from "../../../mock";

const data = {
    "companyId":"ALKNAR",
    "state": ["Clicktruck", "ClicDO"]
  }

  Mock.onGet("/api/v1/clickargo/credit/service?companyId=").reply((config) => {
    return [200, data];
  });