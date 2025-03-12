import Mock from "../../mock";

const data = {
    "status": "SUCCESS",
    "data": "9100230824135048",
    "err": null
}

Mock.onGet("/api/v1/clickargo/clictruck/va/staticVA/generateVA").reply((config) => {
    return [200, data];
});
