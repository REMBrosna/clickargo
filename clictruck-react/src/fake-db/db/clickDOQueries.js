import Mock from "../mock";

//T_CK_JOB_QUERY
export const queriesDB = {
    list: [
        {
            queryId: "CKJQ2256746",
            qryJob: "CKDOJ2022081051312", //REFERENCES `T_CK_JOB` (`JOB_ID`)
            requestor: "MSC_MARY",
            query: "Letter of authorization is not clear please upload another copy for the claim process",
            queryDate: "27/08/2022",
            responder: "",
            response: "",
            responseDate: "",
            qryStatus: null,
            qryDtCreate: null,
            qryDtLupd: null,
            qryUidLupd: null,
        },
        {
            queryId: "CKJQ21212126",
            qryJob: "CKDOJ2022081051312", //REFERENCES `T_CK_JOB` (`JOB_ID`)
            requestor: "MSC_PETER",
            query: "Document is not clear...",
            queryDate: "26/08/2022",
            responder: "GAD_MARY",
            response: "New Document uploaded...",
            responseDate: "31/08/2022",
            qryStatus: null,
            qryDtCreate: null,
            qryDtLupd: null,
            qryUidLupd: null,
        }
    ],
};

Mock.onPost("/api/job/query/add").reply((config) => {
    let query = JSON.parse(config.data);
    queriesDB.list.push(query);
    return [200, queriesDB.list];
});
  