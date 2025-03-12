import axios from "axios.js";

export const fetchCkAccnData = async (accnId) => {

  let ckAccn;
  const url =
    "/api/v1/clickargo/admin/user/tCkAccn/" +
    (accnId != undefined ? accnId : "-");

  await axios
    .get(url)
    .then((response) => {
      console.log("response ", response);
      ckAccn = response?.data;
    })
    .catch((error) => {
      console.error(`Fail to fetch ckAccn ${error}`);
    });
  return ckAccn;
};
