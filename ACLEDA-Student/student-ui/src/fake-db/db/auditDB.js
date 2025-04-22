import Mock from "../mock";

export const auditDB = {
  list: [

    {
      audtId: "a",
      audtEvent: "APP MODIFY",
      audtTimestamp: "2021-01-02 02:31:14",
      audtRemarks: "MODIFY",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "A"

    },
    {
      audtId: "a",
      audtEvent: "APP MODIFY",
      audtTimestamp: "2021-01-02 02:31:14",
      audtRemarks: "MODIFY",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "S"

    },
    {
      audtId: "a",
      audtEvent: "APP MODIFY",
      audtTimestamp: "2021-01-02 02:31:14",
      audtRemarks: "MODIFY",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Approved"

    },
    {
      audtId: "a",
      audtEvent: "APP MODIFY",
      audtTimestamp: "2021-01-02 02:31:14",
      audtRemarks: "MODIFY",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Submitted"

    },
    {
      audtId: "a",
      audtEvent: "APP MODIFY",
      audtTimestamp: "2021-01-02 02:31:14",
      audtRemarks: "MODIFY",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "S"

    },

    {
      audtId: "a",
      audtEvent: "APP CREATE",
      audtTimestamp: "2021-01-01 03:33:10",
      audtRemarks: "CREATE",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Submitted"

    },
    {
      audtId: "a",
      audtEvent: "APP CREATE",
      audtTimestamp: "2021-01-01 03:33:10",
      audtRemarks: "CREATE",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Draft"

    },
    {
      audtId: "a",
      audtEvent: "APP CREATE",
      audtTimestamp: "2021-01-01 03:33:10",
      audtRemarks: "CREATE",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "A"

    },
    {
      audtId: "a",
      audtEvent: "APP CREATE",
      audtTimestamp: "2021-01-01 03:33:10",
      audtRemarks: "CREATE",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Approved"

    },

    {
      audtId: "a",
      audtEvent: "APP SUBMITTED",
      audtTimestamp: "2021-01-03 03:33:10",
      audtRemarks: "SUBMITTED",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Submitted"

    },
    {
      audtId: "a",
      audtEvent: "APP SUBMITTED",
      audtTimestamp: "2021-01-03 03:33:10",
      audtRemarks: "SUBMITTED",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "S"

    },
    {
      audtId: "a",
      audtEvent: "APP SUBMITTED",
      audtTimestamp: "2021-01-03 03:33:10",
      audtRemarks: "SUBMITTED",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "A"

    },
    {
      audtId: "a",
      audtEvent: "APP SUBMITTED",
      audtTimestamp: "2021-01-03 03:33:10",
      audtRemarks: "SUBMITTED",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Approved"

    },

    {
      audtId: "a",
      audtEvent: "APP APPROVED",
      audtTimestamp: "2021-01-04 03:33:10",
      audtRemarks: "APPROVED",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "A"

    },
    {
      audtId: "a",
      audtEvent: "APP APPROVED",
      audtTimestamp: "2021-01-04 03:33:10",
      audtRemarks: "APPROVED",
      audtUid: "COPORTSA00",
      audtUname: "Portal Admin",
      audtReckey: "e",
      audtParam1: "w",
      status: "Approved"

    },


  ],
};

Mock.onGet("/api/auditDB/all").reply((config) => {
  return [200, auditDB.list];
});

Mock.onGet("/api/country").reply((config) => {
  const id = config.data;
  const response = auditDB.list.find((user) => user.id === id);
  return [200, response];
});

Mock.onPost("/api/docRepo/delete").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  auditDB.list.forEach((element) => {
    if (element.id === user.id) {
      return [200, auditDB.list.splice(index.i, 1)];
    }
    index.i++;
  });
  return [200, auditDB.list];
});

Mock.onPost("/api/docRepo/update").reply((config) => {
  let user = JSON.parse(config.data);
  let index = { i: 0 };
  auditDB.list.forEach((element) => {
    if (element.id === user.id) {
      auditDB.list[index.i] = user;
      return [200, auditDB.list];
    }
    index.i++;
  });
  return [200, auditDB.list];
});

Mock.onPost("/api/docRepo/add").reply((config) => {
  let user = JSON.parse(config.data);
  auditDB.list.push(user);
  return [200, auditDB.list];
});