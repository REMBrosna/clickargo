const actions = ["CANCEL", "ACCEPT", "DELETE", "EXIT", 'NEW', 'PAY', "REJECT", "SAVE", "SUBMIT",
    "VERIFY", "REVERIFY", "QUERY", "ASSIGN", "START", "BILLJOB", "STOP",
    "APPROVE", "WITHDRAW", "CLONE", "VERIFY_BILL", "APPROVE_BILL", "REJECT_BILL",
    "ACKNOWLEDGE_BILL", "DEACTIVATE", "ACTIVATE", "SUSPEND", "TERMINATE", "RESUMPTION", "SPLIT"];

export function getFormActionButton(initialButtons, controls, eventHandler, fal) {

    controls.map(ctr => {
        const ctrAction = ctr?.ctrlAction;
        const filter = actions.filter(a => a === ctrAction);

        if (filter.length > 0) {
            let act = filter[0];
            if (act === "SUBMIT") {
                act = "submitOnClick";
            } else if (act === "CANCEL") {
                act = "cancel";
            } else if (act === "EXIT") {
                act = "back";
            } else if (act === "VERIFY") {
                act = "verifyPayment";
            } else if (act === "APPROVE") {
                act = "approvePayment";
            } else if (act === "CLONE") {
                act = "duplicate";
            }

            Object.assign(initialButtons, {
                [act !== "submitOnClick" ? act?.toLowerCase() : act]: {
                    show: true,
                    eventHandler: () => eventHandler(ctrAction)
                },
            });
         }

         return ctr;
    });

    return initialButtons;
}