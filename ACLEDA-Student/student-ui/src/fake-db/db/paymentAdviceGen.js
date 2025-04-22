import Mock from "../mock";

export const paymentAdviceGenDB = {
  list: [
    {
      appId: "CAM20201112",
      version: "1",
      paymentRefNo: "INVCAM20201112",
      adviceRefNoNo:"20005",
      docType:"SSCEC",
      paymentBank:"",
      receiptUpload:"",
      paymentAdviceDate:"",
      remarks:"",
      paymentDate: "",
      applicationTIN:"2332434232",
      paymentAmount:"$100",
      status: "paymentPending",

    },


    {
      appId: "CAM20202022",
      version: "1",
      paymentRefNo: "INVCAM20202022",
      adviceRefNoNo:"20007",
      docType:"ENTRY PERMIT",
      paymentBank:"",
      receiptUpload:"",
      paymentAdviceDate:"20-12-2020",
      remarks:"",
      paymentDate: "",
      applicationTIN:"3434312334",
      paymentAmount:"$100",
      status: "adviceGenerated",

    },

    {
      appId: "CAM20202021",
      version: "1",
      paymentRefNo: "INVCAM20202021",
      adviceRefNoNo:"20006",
      docType:"ENTRY PERMIT",
      paymentBank:"",
      receiptUpload:"",
      paymentAdviceDate:"19-01-2021",
      remarks:"",
      paymentDate: "21-01-2021",
      applicationTIN:"3434312334",
      paymentAmount:"$100",
      status: "paid",

    },

 
  ],
};

Mock.onGet("/api/process/all").reply((config) => {
  return [200, paymentAdviceGenDB.applications];
});

Mock.onGet("/api/process").reply((config) => {
  const id = config.data;
  const response = paymentAdviceGenDB.applications.find((app) => app.appId === id);
  return [200, response];
});

Mock.onPost("/api/process/delete").reply((config) => {
  let app = JSON.parse(config.data);
  let index = { i: 0 };
  paymentAdviceGenDB.applications.forEach((element) => {
    if (element.id === app.appId) {
      return [200, paymentAdviceGenDB.applications.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, paymentAdviceGenDB.applications];
});

Mock.onPost("/api/process/update").reply((config) => {
  let app = JSON.parse(config.data);
  let index = { i: 0 };
  paymentAdviceGenDB.applications.forEach((element) => {
    if (element.id === app.appId) {
      paymentAdviceGenDB.applications[index.i] = app;
      return [200, paymentAdviceGenDB.applications];
    }
    index.i++;
  });
  return [200, paymentAdviceGenDB.applications];
});

Mock.onPost("/api/process/add").reply((config) => {
  let app = JSON.parse(config.data);
  paymentAdviceGenDB.applications.push(app);
  return [200, paymentAdviceGenDB.applications];
});
