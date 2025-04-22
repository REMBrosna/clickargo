import { useReducer, useCallback } from 'react';
import axios from 'axios.js';

const initialState = {
    isSaving: false,
    saveError: null,
    saveResponse: null
}

const httpReducer = (curHttpState, action) => {
    switch (action.type) {
        case 'SEND':
            return {
                isSaving: true,
                saveError: null
            };
        case 'RESPONSE':
            return {
                ...curHttpState,
                isSaving: false,
                saveResponse: action.payload.saveResponse
            };
        case 'ERROR':
            return {
                isSaving: false,
                saveError: action.payload.errorMessage
            };
        case 'CLEAR':
            return { ...curHttpState, saveError: null };
        default:
            throw new Error('Should not be reached!');
    }
};

const useAutoSave = () => {
    const [httpState, dispatchHttp] = useReducer(httpReducer, initialState);

    const autoSave = useCallback(
        async (url, method, body) => {

            //set defaults post for new, otherwise put for update
            method = !method ? "post" : method;

            console.log("autoSave called", url);

            dispatchHttp({ type: 'SEND' });

            await axios({
                method: method,
                url: url,
                data: body
            }).then(response => {
                console.log("response", response);
                if (response) {
                    dispatchHttp({
                        type: 'RESPONSE',
                        payload: {
                            saveResponse: response
                        }
                    });
                } else {
                    dispatchHttp({
                        type: 'ERROR',
                        payload: {
                            errorMessage: 'Something went wrong!'
                        }
                    });
                }
            }).catch(error => {
                console.log("error", error);
                dispatchHttp({
                    type: 'ERROR',
                    payload: {
                        errorMessage: 'Something went wrong - ' + error
                    }
                });
            });

        }, []);

    return {
        isSaving: httpState.loading,
        saveResposne: httpState.res,
        saveError: httpState.error,
        autoSaveRequest: autoSave,
    };
};

export default useAutoSave;
