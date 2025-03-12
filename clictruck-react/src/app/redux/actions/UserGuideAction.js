import axios from 'axios.js';
export const SET_USER_GUIDE = "SET_USER_GUIDE";

export function getUserGuideByServiceType(serviceType) {
    return (dispatch, getState) => {

        axios({
            method: 'get',
            url: `/api/v1/clickargo/component-guide/${serviceType}/`,
        }).then(response => {
            dispatch({
                type: SET_USER_GUIDE,
                payload: response?.data
            });

        }, (error) => {
            console.log("error", error);
            return Promise.reject(error)
        }).catch(error => {
            console.log("caught error", error);
            return Promise.reject(error)
        });


    };
}


