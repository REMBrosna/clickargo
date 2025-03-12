import { ApplicationType, Status, AccountTypes } from "app/c1utils/const";

export const WorkflowStage = {
    Draft: "workflow:statistics.draft",
    PendingAcknowledgement: "workflow:statistics.pAck",
    Acknowledged: "workflow:statistics.ack",
    PendingVerification: "workflow:statistics.pVer",
    PendingReVerification: "workflow:statistics.pReVer",
    PendingApproval: "workflow:statistics.pApp",
    PendingReApproval: "workflow:statistics.pReApp",
    Approved: "workflow:statistics.app",
    PendingPayment: "workflow:statistics.pPay",
    Rejected: "workflow:statistics.rej",
    Submitted: "workflow:statistics.sub",
    Paid: "workflow:statistics.paid",
    InvoiceGenerated: "workflow:statistics.pInv",
};

export const convertKeyToText = (key, appType, accnType) => {
    if (appType && appType === ApplicationType.PO.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.VER.code) {
            return WorkflowStage.PendingApproval;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        }
    } else if (appType && appType === ApplicationType.EP.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.PAY.code) {
            return WorkflowStage.PendingApproval;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        } else if (key === Status.PEN.code) {
            return WorkflowStage.PendingPayment;
        }
    }else if (appType && appType === ApplicationType.PAY.code) {
        if (key === Status.INV.code) {
            return WorkflowStage.InvoiceGenerated;
        } else if (key === Status.SUB.code) {
            return WorkflowStage.Submitted;
        }
    } else if (appType && appType === ApplicationType.SR.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.VER.code) {
            return WorkflowStage.PendingApproval;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        }
    } else if (appType && appType === ApplicationType.SSCEC.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        } else if (key === Status.PEN.code) {
            return WorkflowStage.PendingPayment;
        } else if (key === Status.PAY.code) {
            return WorkflowStage.PendingApproval;
        }
    } else if (appType && appType === ApplicationType.SSCC.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        } else if (key === Status.PEN.code) {
            return WorkflowStage.PendingPayment;
        } else if (key === Status.PAY.code) {
            return WorkflowStage.PendingApproval;
        }
    } else if (appType && appType === ApplicationType.ADSUB.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        } else if (key === Status.PEN.code) {
            return WorkflowStage.PendingPayment;
        } else if (key === Status.PAY.code) {
            return WorkflowStage.PendingApproval;
        }

        if (accnType !== AccountTypes.ACC_TYPE_QUARANTINE) {
            if (key === Status.VER.code) {
                return WorkflowStage.PendingApproval;
            }
        }

    } else if (appType && appType === ApplicationType.DDSUB.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        } else if (key === Status.PEN.code) {
            return WorkflowStage.PendingPayment;
        } else if (key === Status.PAY.code) {
            return WorkflowStage.PendingApproval;
        }

        if (accnType !== AccountTypes.ACC_TYPE_QUARANTINE) {
            if (key === Status.VER.code) {
                return WorkflowStage.PendingApproval;
            }
        }

    } else if (appType && appType === ApplicationType.PAN.code) {
        if (key === Status.SUB.code) {
            // return WorkflowStage.PendingVerification; //PORTEDI-1713 change
            return WorkflowStage.PendingAcknowledgement;
        }
    } else if(appType && appType === ApplicationType.DOS.code) {
        if (key === Status.SUB.code) {
            return WorkflowStage.PendingVerification;
        } else if (key === Status.VER.code) {
            return WorkflowStage.PendingApproval;
        } else if (key === Status.ORT.code) {
            return WorkflowStage.PendingReVerification;
        }
    } else {
        if (key === Status.SUB.code) return WorkflowStage.PendingAcknowledgement;
        else if (key === Status.ACK.code) return WorkflowStage.PendingVerification;
        else if (key === Status.VER.code) return WorkflowStage.PendingApproval;
        else if (key === Status.PEN.code) return WorkflowStage.PendingPayment;
        else if (key === Status.ORT.code) return WorkflowStage.PendingReVerification;
    }
};

export const isDisplayWorkflowBtn = ({ roles, transaction, isReject }) => {
    if (isRejected({ transaction })) {
        return false;
    }

    if ("PAN" === transaction.workflowName) {
        return isDisplayWorkflowBtnPAN({ roles, transaction, isReject });
    }
    if ("AD" === transaction.workflowName) {
        return isDisplayWorkflowBtnAD({ roles, transaction });
    }
    if ("DD" === transaction.workflowName) {
        return isDisplayWorkflowBtnDD({ roles, transaction });
    }
};

export const isDisplayWorkflowBtnPAN = ({ roles, transaction, isReject }) => {
    console.log("ROLES", roles);
    console.log("TRANSACTION", isReject);

    if (roles === "PORT" && WorkflowStage.PendingAcknowledgement === transaction.statusPort && !isReject) {
        return true;
    }

    /*
  if (
    roles === "CUSTOMS" &&
    WorkflowStage.PendingAcknowledgement === transaction.statusPortCu
  ) {
    return true;
  }

  if (
    roles === "IMMIGRATION" &&
    WorkflowStage.PendingAcknowledgement === transaction.statusPortIm
  ) {
    return true;
  }

  if (
    roles === "QUARANTINE" &&
    WorkflowStage.PendingAcknowledgement === transaction.statusPortQu
  ) {
    return true;
  }
*/
    return false;
};

export const isDisplayWorkflowBtnAD = ({ roles, transaction }) => {
    console.log("ROLES", roles);
    console.log(
        "TRANSACTION",
        WorkflowStage.PendingAcknowledgement === transaction.statusBodCu ||
        WorkflowStage.PendingVerification === transaction.statusBodCu ||
        WorkflowStage.PendingApproval === transaction.statusBodCu
    );

    // Border
    if (
        roles === "CUSTOMSBOD" &&
        (WorkflowStage.PendingVerification === transaction.statusBodCu ||
            WorkflowStage.PendingApproval === transaction.statusBodCu)
    ) {
        return true;
    }

    if (
        roles === "IMMIGRATIONBOD" &&
        (WorkflowStage.PendingVerification === transaction.statusBodIm ||
            WorkflowStage.PendingApproval === transaction.statusBodIm)
    ) {
        return true;
    }

    if (
        roles === "QUARANTINEBOD" &&
        (WorkflowStage.PendingVerification === transaction.statusBodQu ||
            WorkflowStage.PendingApproval === transaction.statusBodQu)
    ) {
        return true;
    }

    // border(CIQ) approved
    if (
        WorkflowStage.Approved === transaction.statusBodCu &&
        WorkflowStage.Approved === transaction.statusBodIm &&
        WorkflowStage.Approved === transaction.statusBodQu
    ) {
        if (
            roles === "CUSTOMS" &&
            (WorkflowStage.PendingVerification === transaction.statusPortCu ||
                WorkflowStage.PendingApproval === transaction.statusPortCu)
        ) {
            return true;
        }

        if (
            roles === "IMMIGRATION" &&
            (WorkflowStage.PendingVerification === transaction.statusPortIm ||
                WorkflowStage.PendingApproval === transaction.statusPortIm)
        ) {
            return true;
        }

        if (
            roles === "QUARANTINE" &&
            (WorkflowStage.PendingVerification === transaction.statusPortQu ||
                WorkflowStage.PendingApproval === transaction.statusPortQu)
        ) {
            return true;
        }
    }

    // border(CIQ) approved and port(CIQ) approved
    if (
        WorkflowStage.Approved === transaction.statusBodCu &&
        WorkflowStage.Approved === transaction.statusBodIm &&
        WorkflowStage.Approved === transaction.statusBodQu &&
        WorkflowStage.Approved === transaction.statusPortCu &&
        WorkflowStage.Approved === transaction.statusPortIm &&
        WorkflowStage.Approved === transaction.statusPortQu
    )
        if (
            roles === "PORT" &&
            (WorkflowStage.PendingVerification === transaction.statusPort ||
                WorkflowStage.PendingApproval === transaction.statusPort)
        ) {
            return true;
        }

    return false;
};

export const isDisplayWorkflowBtnDD = ({ roles, transaction }) => {
    console.log("ROLES", roles);
    console.log(
        "TRANSACTION",
        WorkflowStage.PendingAcknowledgement === transaction.statusBodCu ||
        WorkflowStage.PendingVerification === transaction.statusBodCu ||
        WorkflowStage.PendingApproval === transaction.statusBodCu
    );
    // Port(CIQ)
    if (
        roles === "CUSTOMS" &&
        (WorkflowStage.PendingVerification === transaction.statusPortCu ||
            WorkflowStage.PendingApproval === transaction.statusPortCu)
    ) {
        return true;
    }

    if (
        roles === "IMMIGRATION" &&
        (WorkflowStage.PendingVerification === transaction.statusPortIm ||
            WorkflowStage.PendingApproval === transaction.statusPortIm)
    ) {
        return true;
    }
    if (
        roles === "QUARANTINE" &&
        (WorkflowStage.PendingVerification === transaction.statusPortQu ||
            WorkflowStage.PendingApproval === transaction.statusPortQu)
    ) {
        return true;
    }

    // Port(CIQ) approved
    if (
        WorkflowStage.Approved === transaction.statusPortCu &&
        WorkflowStage.Approved === transaction.statusPortIm &&
        WorkflowStage.Approved === transaction.statusPortQu
    ) {
        if (
            roles === "PORT" &&
            (WorkflowStage.PendingVerification === transaction.statusPort ||
                WorkflowStage.PendingApproval === transaction.statusPort)
        ) {
            return true;
        }
    }

    // Port(CIQ) approved and port(P) approved
    if (
        WorkflowStage.Approved === transaction.statusPortCu &&
        WorkflowStage.Approved === transaction.statusPortIm &&
        WorkflowStage.Approved === transaction.statusPortQu &&
        WorkflowStage.Approved === transaction.statusPort
    ) {
        if (
            roles === "CUSTOMSBOD" &&
            (WorkflowStage.PendingAcknowledgement === transaction.statusBodCu ||
                WorkflowStage.PendingVerification === transaction.statusBodCu ||
                WorkflowStage.PendingApproval === transaction.statusBodCu)
        ) {
            return true;
        }

        if (
            roles === "IMMIGRATIONBOD" &&
            (WorkflowStage.PendingAcknowledgement === transaction.statusBodIm ||
                WorkflowStage.PendingVerification === transaction.statusBodIm ||
                WorkflowStage.PendingApproval === transaction.statusBodIm)
        ) {
            return true;
        }

        if (
            roles === "QUARANTINEBOD" &&
            (WorkflowStage.PendingAcknowledgement === transaction.statusBodQu ||
                WorkflowStage.PendingVerification === transaction.statusBodQu ||
                WorkflowStage.PendingApproval === transaction.statusBodQu)
        ) {
            return true;
        }
    }

    return false;
};

export const updateTransactionStatus = ({ roles, transaction, newStatus }) => {
    // check permission

    // update role's status
    let updatedTransaction = transaction;
    if (roles === "CUSTOMS") {
        updatedTransaction = { ...transaction, statusPortCu: newStatus };
    } else if (roles === "CUSTOMSBOD") {
        updatedTransaction = { ...transaction, statusBodCu: newStatus };
    } else if (roles === "IMMIGRATION") {
        updatedTransaction = { ...transaction, statusPortIm: newStatus };
    } else if (roles === "IMMIGRATIONBOD") {
        updatedTransaction = { ...transaction, statusBodIm: newStatus };
    } else if (roles === "QUARANTINE") {
        updatedTransaction = { ...transaction, statusPortQu: newStatus };
    } else if (roles === "QUARANTINEBOD") {
        updatedTransaction = { ...transaction, statusBodQu: newStatus };
    } else if (roles === "PORT") {
        updatedTransaction = { ...transaction, statusPort: newStatus };
    }

    // update updatedTransaction status
    updatedTransaction = {
        ...updatedTransaction,
        status: getTransactionStatus(updatedTransaction),
    };

    return updatedTransaction;
};

export const getStatusByRole = ({ roles, transaction }) => {
    if (roles === "CUSTOMS") {
        return transaction.statusPortCu;
    } else if (roles === "CUSTOMSBOD") {
        return transaction.statusBodCu;
    } else if (roles === "IMMIGRATION") {
        return transaction.statusPortIm;
    } else if (roles === "IMMIGRATIONBOD") {
        return transaction.statusBodIm;
    } else if (roles === "QUARANTINE") {
        return transaction.statusPortQu;
    } else if (roles === "QUARANTINEBOD") {
        return transaction.statusBodQu;
    } else if (roles === "PORT") {
        return transaction.statusPort;
    }
};

export const getTransactionStatus = (transaction) => {
    if (isRejected({ transaction })) {
        return WorkflowStage.Rejected;
    }

    if ("PAN" === transaction.workflowName) {
        if (transaction.statusPort === transaction.statusPort) {
            return transaction.statusPort;
        }
        return WorkflowStage.PendingAcknowledgement;
    } else {
        if (
            transaction.statusPort === transaction.statusBodCu &&
            transaction.statusPort === transaction.statusBodIm &&
            transaction.statusPort === transaction.statusBodQu &&
            transaction.statusPort === transaction.statusPortCu &&
            transaction.statusPort === transaction.statusPortIm &&
            transaction.statusPort === transaction.statusPortQu &&
            transaction.statusPort === transaction.statusPort
        ) {
            // 7 roles' stages are same;
            return transaction.statusPort;
        }
        return WorkflowStage.PendingVerification;
    }
};

export const getTotalStatusCountByRole = ({ roles, tranList, status, workflowName }) => {
    if ("PAN" === workflowName) {
        if (roles === "CUSTOMSBOD" || roles === "IMMIGRATIONBOD" || roles === "QUARANTINEBOD") {
            return -1;
        }
    }

    if (roles === "CUSTOMS") {
        return tranList.filter((el) => el.statusPortCu === status).length;
    } else if (roles === "CUSTOMSBOD") {
        return tranList.filter((el) => el.statusBodCu === status).length;
    } else if (roles === "IMMIGRATION") {
        return tranList.filter((el) => el.statusPortIm === status).length;
    } else if (roles === "IMMIGRATIONBOD") {
        return tranList.filter((el) => el.statusBodIm === status).length;
    } else if (roles === "QUARANTINE") {
        return tranList.filter((el) => el.statusPortQu === status).length;
    } else if (roles === "QUARANTINEBOD") {
        return tranList.filter((el) => el.statusBodQu === status).length;
    } else if (roles === "PORT") {
        return tranList.filter((el) => el.statusPort === status).length;
    }
    return 0;
};

export const isRejected = ({ transaction }) => {
    if (
        WorkflowStage.Rejected === transaction.statusBodCu ||
        WorkflowStage.Rejected === transaction.statusBodIm ||
        WorkflowStage.Rejected === transaction.statusBodQu ||
        WorkflowStage.Rejected === transaction.statusPortCu ||
        WorkflowStage.Rejected === transaction.statusPortIm ||
        WorkflowStage.Rejected === transaction.statusPortQu ||
        WorkflowStage.Rejected === transaction.statusPort
    ) {
        return true;
    }
};

export const isEndWorkflow = ({ transaction }) => {
    console.log("isEndWorkflow", transaction.status);

    if (
        WorkflowStage.Rejected === transaction.status ||
        WorkflowStage.Approved === transaction.status ||
        WorkflowStage.Acknowledged === transaction.status
    ) {
        return true;
    }
    return false;
};
