import { AccountTypes } from "app/c1utils/const";
import axios from 'axios.js';

import { navigations } from "../../navigations";

export const SET_USER_NAVIGATION = "SET_USER_NAVIGATION";
export const SET_USER_GUIDE = "SET_USER_GUIDE";


const getfilteredNavigations = (navList = [], role) => {
    return navList.reduce((array, nav) => {
        if (nav.auth) {
            if (nav.auth.includes(role)) {
                array.push(nav);
            }
        } else {
            if (nav.children) {
                nav.children = getfilteredNavigations(nav.children, role);
                array.push(nav);
            } else {
                array.push(nav);
            }
        }
        return array;
    }, []);
};

export function getNavigationByUser() {
    return (dispatch, getState) => {
        let { user, navigations = [] } = getState();

        let filteredNavigations = getfilteredNavigations(navigations, user.role);

        dispatch({
            type: SET_USER_NAVIGATION,
            payload: [...filteredNavigations]
        });
    };
}

/**Retrieves the menu from backend. */
export function getMenuByUser(user) {
    return (dispatch, getState) => {

        axios({
            method: 'get',
            url: `/api/v1/clickargo/auth/menu`,
        }).then(response => {
            let menu = response?.data;
            dispatch({
                type: SET_USER_NAVIGATION,
                payload: menu
            });

        }, (error) => {
            console.log("error", error);
            //return Promise.reject(error);
        }).catch(error => {
            console.log("caught error", error);
            //return Promise.reject(error);
        });


    };
}

export function resetMenu() {
    console.log("resetMenu")
    return (dispatch, getState) => {
        console.log("dispatched reset menu called", dispatch);
        dispatch({
            type: SET_USER_NAVIGATION,
            payload: []
        });
    }
}

export function getUserGuideByServiceType(serviceType) {
    return (dispatch, getState) => {

        axios({
            method: 'get',
            url: `http://localhost:8080/clicdo/api/v1/clickargo/clicdo/component-guide/${serviceType}/`,
        }).then(response => {
            dispatch({
                type: SET_USER_GUIDE,
                payload: response?.data
            });

        }, (error) => {
            console.log("error", error);
        }).catch(error => {
            console.log("caught error", error);
        });


    };
}


export function getDynamicNavigationByUser() {
    return (dispatch, getState) => {
        axios.defaults.withCredentials = false;
        //login
        const authData = {
            email: 'test3@test.com',
            password: 'password',
            returnSecureToken: true
        }

        let token = null;
        let url = 'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyBxoOvpuWs2XEEZ1bNqz8yx2Enn6Z2GCy4';
        axios.post(url, authData)
            .then(response => {
                token = response.data.idToken;
                getNavigations(dispatch, response.data.localId);
            })
            .catch(err => {
                console.log(err);

            });



    };
}

const getNavigations = (dispatch, token) => {

    let navs = navigations;

    // console.log("navigations", response.data);
    //the dispatch below is the argument from this function
    let arr = [];
    //  for (var k in response.data) {
    //arr.push(response.data[k]);
    //}

    dispatch({
        type: SET_USER_NAVIGATION,
        payload: [...navs]
    });
}
