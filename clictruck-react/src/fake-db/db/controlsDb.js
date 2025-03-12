import Mock from "../mock";

export const controls = [
    {
        ctrlAccnType: "CARGO_OWNER",
        ctrlCurrentState: "NEW",
        ctrlPage: "EDIT",
        ctrlAction: "SAVE",
        ctrlStatus: "A"
    },
    {
        ctrlAccnType: "CARGO_OWNER",
        ctrlCurrentState: "NEW",
        ctrlPage: "EDIT",
        ctrlAction: "SUBMIT",
        ctrlStatus: "A"
    },
    {
        ctrlAccnType: "CARGO_OWNER",
        ctrlCurrentState: "NEW",
        ctrlPage: "VIEW",
        ctrlAction: "EXIT",
        ctrlStatus: "A"
    },
];

////\/api\/controls\/view\/w+/
Mock.onGet(/\api\/controls\/\w+\/\w+/).reply((config) => {
    const params = config.url.split("/");
    const view = params[3];
    const status = params[4];
    const response = controls.filter((c) => {
        return c.ctrlCurrentState.toLowerCase() === status
            && c.ctrlPage.toLowerCase() === view.toLowerCase();
    });
    return [200, response];
});