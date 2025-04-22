var cacheDataService = (function () {
  let cachedDataMap = new Map();

  let getItem = (serviceName) => {
    return cachedDataMap.get(serviceName);
  };

  let getItemData = (serviceName) => {
    if (cachedDataMap.get(serviceName)) {
      return cachedDataMap.get(serviceName).data;
    }
  };

  let getItemTimeStamp = (serviceName) => {
    if (cachedDataMap.get(serviceName)) {
      return cachedDataMap.get(serviceName).timeStamp;
    }
  };

  let getAllItem = () => {
    return cachedDataMap;
  };

  let setItem = (serviceName, data) => {
    if (!data) {
      cachedDataMap.delete(serviceName);
      return;
    }

    cachedDataMap.set(serviceName, {
      data: data,
      timeStamp: new Date().getTime(),
    });
  };

  return {
    getAllItem: getAllItem,
    getItem: getItem,
    getItemData: getItemData,
    getItemTimeStamp: getItemTimeStamp,
    setItem: setItem,
  };
})();

export default cacheDataService;
