import React, { useEffect } from "react";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";
import useAuth from "app/hooks/useAuth";
import history from "history.js";

const AccountWorkbench =()=>{
    const {user} = useAuth()
    
    // const admin = Roles.ADMIN.code
    // const isToAccn = user?.coreAccn?.TMstAccnType?.atypId === AccountTypes.ACC_TYPE_TO.code

    useEffect(()=>{
        let auth =user?.authorities;
        

        // if(auth?.some((item,i)=> item.authority === admin) === true){
            history.push(`/detail/account/edit/my`, {
                state: { from: `/`}
            })
        // }
    // eslint-disable-next-line
    },[])

    return (
        <React.Fragment></React.Fragment>
    )
}

export default withErrorHandler(AccountWorkbench)