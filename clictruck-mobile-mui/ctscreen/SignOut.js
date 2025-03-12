import React, { useEffect, useState } from 'react';
import useAuth from '../hooks/useAuth';
import CtLoading from './CtLoading';
import { useNavigation } from '@react-navigation/native';


let LoginStatus = {};

const LoginStatusArray = [
    "AUTHORIZED_REG",
    "AUTHORIZED_IGNORE",
    "UNAUTHENTICATED",
    "UNAUTHORIZED",
    "AUTHORIZED_LOGIN",
    "AUTHORIZED_LOGIN_CHNG_PW_RQD",
    "ACCOUNT_SUSPENDED",
    "ACCOUNT_INACTIVE",
    "USER_SESSION_EXISTING",
    "PASSWORD_RESET_SUCCESS",
    "SESSION_EXPIRED_OR_INACTIVE",
];

LoginStatusArray.map((status) => LoginStatus = { ...LoginStatus, [status]: status });

const SignOut = (props) => {
    const navigation = useNavigation();
    const { logout, isAuthenticated } = useAuth();
    const [loading, setLoading] = useState(true);



    useEffect(() => {
        if (isAuthenticated) {
            logout();
            setLoading(false);
            navigation.navigate("SignIn")
        }

    }, []);

    return (<CtLoading isVisible={loading} title="Signing Out! Please wait"
        onBackdropPress={() => setLoading(false)}
        onRequestClose={() => setLoading(false)} />);


}



export default SignOut;


