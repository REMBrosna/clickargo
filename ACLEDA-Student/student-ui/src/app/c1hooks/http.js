import { useReducer, useCallback, useRef, useEffect } from 'react';
import axios from 'axios.js';

const initialState = {
    loading: false,
    formSubmission: false,
    urlId: null,
    error: null,
    res: null,
    validation: null
}

const httpReducer = (curHttpState, action) => {
    switch (action.type) {
        case 'SEND':
            return {
                loading: true,
                error: null,
                data: null,
                urlId: action.payload.urlId,
                formSubmission: action.payload.formSubmit
            };
        case 'RESPONSE':
            return {
                ...curHttpState,
                loading: false,
                res: action.payload.responseData,
                formSubmission: action.payload.formSubmit
            };
        case 'VALIDATION_FAILED':
            return {
                ...curHttpState,
                loading: false,
                res: action.payload.responseData,
                validation: action.payload.validationResponse,
                formSubmission: action.payload.formSubmit
            }
        case 'ERROR':
            return {
                loading: false,
                error: action.payload.errorMessage,
                formSubmission: action.payload.formSubmit
            };
        case 'CLEAR':
            return { ...curHttpState, error: null };
        default:
            throw new Error('Should not be reached!');
    }
};

const useHttp = () => {
    const [httpState, dispatchHttp] = useReducer(httpReducer, initialState);
    const mountedRef = useRef(true);
    const sendRequest = useCallback(
        (url, urlId, method, body) => {

            //set defaults
            method = !method ? "get" : method;
            body = !body ? null : body;
            urlId = !urlId ? null : urlId;

            //assumption: if the body is null, it's not form submission but a fetch
            let isFormSubmission = false;
            if (body) {
                isFormSubmission = true;
            }

            dispatchHttp({ type: 'SEND', payload: { formSubmit: isFormSubmission, urlId: urlId } });

            axios({
                method: method,
                url: url,
                data: body
            }).then(response => {
                if (!mountedRef.current) return null;
                if (response) {
                    dispatchHttp({
                        type: 'RESPONSE',
                        payload: {
                            responseData: response,
                            formSubmit: isFormSubmission
                        }
                    });
                } else {
                    dispatchHttp({
                        type: 'ERROR',
                        payload: {
                            errorMessage: 'Something went wrong!',
                            formSubmit: isFormSubmission
                        }
                    });
                }
            },
                (error) => {
                    if (!mountedRef.current) return null;
                    if (error.status !== 'VALIDATION_FAILED') {
                        dispatchHttp({
                            type: "ERROR",
                            payload: {
                                errorMessage: error,
                                formSubmit: isFormSubmission,
                            },
                        });
                    } else {
                        //return the body payload
                        dispatchHttp({
                            type: 'VALIDATION_FAILED',
                            payload: {
                                responseData: body,
                                validationResponse: JSON.parse(error.err.msg),
                                formSubmit: isFormSubmission
                            }
                        });
                    }

                }).catch(error => {
                    if (!mountedRef.current) return null;
                    dispatchHttp({
                        type: 'ERROR',
                        payload: {
                            errorMessage: 'Something went wrong!',
                            formSubmit: isFormSubmission
                        }
                    });
                });

        }, []);

    //To resolve warning - can't perform react state update on an unmounted component.
    useEffect(() => {
        return () => { mountedRef.current = false }
    }, [])

    return {
        isLoading: httpState.loading,
        isFormSubmission: httpState.formSubmission,
        res: httpState.res,
        validation: httpState.validation,
        error: httpState.error,
        urlId: httpState.urlId,
        sendRequest: sendRequest,
    };
};

export default useHttp;
