import Mock from "../../mock";

const data = {
  iTotalRecords: 2,
  iTotalDisplayRecords: 10,
  aaData: [
    {
      id: 1,
      integrationNo: 12332112234,
      dateStart: 1680002620265,
      dateEnd: 1680002620265,
      revision: "01",
      fileType: "BOOKING",
      noRecords: 423,
      noSuccess: null,
      noFailed: null,
      status: "SUB",
    },
    {
      id: 2,
      integrationNo: 12332119873,
      dateStart: 1680002620265,
      dateEnd: 1680002620265,
      revision: "01",
      fileType: "PAYMENT IN",
      noRecords: 423,
      noSuccess: null,
      noFailed: null,
      status: "SUC",
    },
    {
      id: 3,
      integrationNo: 12332110098,
      dateStart: 1680002620265,
      dateEnd: 1680002620265,
      revision: "01",
      fileType: "PAYMENT OUT",
      noRecords: 423,
      noSuccess: null,
      noFailed: null,
      status: "PAR",
    },
  ],
};
