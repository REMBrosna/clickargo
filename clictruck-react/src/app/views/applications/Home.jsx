import React, { useEffect } from "react";

import { AccountTypes, Roles, AdminOfficerRoles, ShippingLines } from "app/c1utils/const";
import useAuth from "app/hooks/useAuth";
import history from "history.js";

/**
 * 
 * @deprecated
 */
const Home = () => {

    const { user } = useAuth();

    useEffect(() => {
        let authorities = user.authorities;

        //check if there's only one authority for user, then check if the user is SYSTEM_ADMIN or SYSTEM_SUPPORT_OFFICER
        if (authorities.length === 1) {
            if (authorities[0].authority === Roles.SYSTEM_SUPPORT_OFFICER.code) {
                //Land on applications 
                history.push("/workbench");
            } else if (authorities[0].authority === Roles.SYSTEM_ADMIN.code) {
                //land on manage users
                history.push("/manageUsers/users/all/list");
            } else if (AdminOfficerRoles.includes(authorities[0].authority)) {
                // GUUD_PORTEDI_REG08
                history.push("/manageUsers/user/list");
            } else {
                if (ShippingLines.includes(user.coreAccn.TMstAccnType.atypId)) {
                    history.push("/applications/vesselCall/list");
                } else {
                    history.push("/workbench");
                }
            }
        } else {
            if (ShippingLines.includes(user.coreAccn.TMstAccnType.atypId)) {
                history.push("/applications/vesselCall/list");
            } else {
                if (authorities.some(el => AdminOfficerRoles.includes(el.authority))) {
                    history.push("/manageUsers/user/list");
                } else if (authorities.some(el => el.authority === Roles.SYSTEM_ADMIN.code)) {
                    history.push("/manageUsers/users/all/list");
                } else {
                    history.push("/workbench");
                }
            }
        }

    }, [user])

    return (<React.Fragment>
    </React.Fragment>

    );
};

export default Home;