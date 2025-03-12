import Mock from "../mock";

const data = [];

Mock.onGet("/api/co/master/entity/currencyFx").reply((config) => {
    return [200, data];
  });