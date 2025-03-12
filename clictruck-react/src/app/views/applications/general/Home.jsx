import React, { useEffect } from "react";

import { AccountTypes, Roles } from "app/c1utils/const";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";

/** Determines the landing page after login. */
const Home = () => {
    const { user } = useAuth();

    useEffect(() => {
        let authorities = user?.authorities;

        if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_CO.code
            || user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code) {
            //removed the checking if finance, all will fall in this route
            history.push("/applications/services/job/coff/truck")
        } else if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_SP.code) {
            if (authorities.some(el => [Roles.FINANCE_VERIFIER.code, Roles.FINANCE_APPROVER.code].includes(el.authority))) {
                history.push("/applications/services/gli/dashboard");
            } else if (authorities.some(el => [Roles.SP_L1.code].includes(el.authority))) {
                history.push("/opadmin/docverification");
            }
        } else if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code) {
            // truck operator
            history.push("/applications/services/job/to/truck")
        } else if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF_CO.code) {
            // truck operator
            history.push("/applications/services/job/ffco/truck")
        }else if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO_WJ.code) {
            // truck operator
            history.push("/applications/service/truckTracking")
        }
    }, [user])

    return (<React.Fragment>

    </React.Fragment>

    );
};

export default withErrorHandler(Home);