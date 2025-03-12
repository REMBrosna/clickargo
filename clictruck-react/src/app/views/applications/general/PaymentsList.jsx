import React, { useEffect } from "react";

import { AccountTypes, Roles } from "app/c1utils/const";
import useAuth from 'app/hooks/useAuth';
import history from "history.js";

const PaymentsList = () => {
    const { user } = useAuth();

    useEffect(() => {
        //This is done, with the assumption that payment menu may be available in Shipping Line
        let authorities = user.authorities;
        if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_FF.code) {
            if (authorities.some(el => [Roles.FF_FINANCE.code].includes(el.authority)))
                history.push('/applications/payments/payFfDoiJobs');
        }
        if (user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_SL.code) {
            if (authorities.some(el => [Roles.FINANCE.code].includes(el.authority)))
                history.push('/applications/payments/do');
        }


    }, [user])


    return (<React.Fragment>
        Payments List Page
    </React.Fragment>

    );
};

export default PaymentsList;