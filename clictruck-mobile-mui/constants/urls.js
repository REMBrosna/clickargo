export const newJobList =
    "/api/v1/clickargo/clictruck/mobile/assigned" + 
    "?sEcho=3" +
    "&iDisplayStart=0" +
    "&iDisplayLength=100" +
    "&iSortCol_0=0" +
    "&sSortDir_0=desc" +
    "&iSortingCols=1" +
    "&mDataProp_0=jobDtCreate" +
    "&mDataProp_1=history" +
    "&sSearch_1=default" +
    "&iColumns=5";

export const pausedJobList =
    "/api/v1/clickargo/clictruck/mobile/paused" +
    "?sEcho=3" +
    "&iDisplayStart=0" +
    "&iDisplayLength=100" +
    "&iSortCol_0=0" +
    "&sSortDir_0=desc" +
    "&iSortingCols=1" +
    "&mDataProp_0=jobDtCreate" +
    "&mDataProp_1=history" +
    "&sSearch_1=default" +
    "&iColumns=5";

export const jobHistoryList =
    "/api/v1/clickargo/clictruck/mobile/mHistory" + 
    "?sEcho=3" + 
    "&iDisplayStart=0" + 
    "&iDisplayLength=100" + 
    "&iSortCol_0=0" + 
    "&sSortDir_0=desc" + 
    "&iSortingCols=1" + 
    "&mDataProp_0=jobDtLupd" + 
    "&iColumns=5";

export const getStartConfirmUrl = 
    "/api/v1/clickargo/clictruck/mobile/assigned/checkJobStatus";

export const getResumeConfirmUrl =
    "/api/v1/clickargo/clictruck/mobile/paused/checkJobStatus";

export const startJobUrl = 
    "/api/v1/clickargo/clictruck/mobile/startJob/";