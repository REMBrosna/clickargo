import React, { Component } from 'react';
import { Redirect } from "react-router-dom";

import Modal from '@material-ui/core/Modal';
import Backdrop from '@material-ui/core/Backdrop';
import Grid from '@material-ui/core/Grid';
import Fade from '@material-ui/core/Fade';
import AuxDiv from "../AuxDiv/AuxDiv";
import classes from "./withErrorHandler.module.css";
import axios, { sessionTimeout, noPermission } from "axios.js";
import C1InputField from 'app/c1component/C1InputField';
import C1TextArea from 'app/c1component/C1TextArea';
import { Typography } from '@material-ui/core';
import {Alert, AlertTitle} from "@material-ui/lab";

const withErrorHandler = (WrappedComponent) => {

// Utility function to clean up error messages
    const cleanErrorMessage = (message) => {
        // Use a regular expression to remove fully qualified class names
        return message.replace(/^com\.vcc\.camelone\.common\.exception\.\w+: /, '');
    };
    return class extends Component {

        state = {error: null, open: false}
        handleOpen = () => {
            this.setState({ open: true });
        }

        handleClose = () => {
            this.setState({ open: false });
        }

        constructor(props) {
            super(props);
            this.reqInterceptor = axios.interceptors.request.use(req => {
                //this is to not close the modal if some fields (e.g. select) rerenders and request http
                if (!this.state.error) {
                    this.setState({ error: null, open: false });
                }

                return req;
            });
            this.resInterceptor = axios.interceptors.response.use(res => res, error => {
                if (error.status !== 'VALIDATION_FAILED' && error?.code !== 406) {
                    this.setState({ error: error, open: true });
                } else if (error.status === 'VALIDATION_FAILED') {
                    return Promise.reject(error);
                } else {
                    //Nina removed displaying of stacktrace for security purposes
                    return Promise.reject(error);
                }

            });
        }

        //This will remove the interceptors, cleaning up so that if we reuse this in other parts in the application
        //it will not create more interceptors
        componentWillUnmount() {
            axios.interceptors.request.eject(this.reqInterceptor);
            axios.interceptors.response.eject(this.resInterceptor);
        }

        errorConfirmedHandler = () => {
            this.setState({ error: null });
        }



        render() {
            if (this.state.error && this.state.error.err &&
                (sessionTimeout === this.state.error.err.msg || this.state.error.err.msg.includes("jwtUser null"))) {
                return <Redirect to='/session/timeout' />;
            }
            if (this.state.error && this.state.error.err && noPermission === this.state.error.err.msg) {
                return <Redirect to='/session/nopermission' />;
            }

            // Clean up the error message before displaying it
            const errorMessage = this.state.error && this.state.error.err
                ? cleanErrorMessage(this.state.error.err.msg)
                : "Something went wrong!";

            return (
                <AuxDiv>
                    <Modal
                        aria-labelledby="transition-modal-title"
                        aria-describedby="transition-modal-description"
                        open={this.state.open}
                        onClose={this.handleClose}
                        BackdropComponent={Backdrop}
                        BackdropProps={{
                            timeout: 150,
                        }}
                        centered="true"
                        style={{
                            position: 'absolute',
                            top: '5%',
                            margin: 'auto',
                            width: 600,
                        }}  >
                        <Fade in={this.state.open}>
                            <Alert severity="warning">
                                <AlertTitle>Warning</AlertTitle>
                                {errorMessage}
                            </Alert>
                        </Fade>
                    </Modal>
                    <WrappedComponent {...this.props} />
                </AuxDiv>
            );
        }

    }
}

export default withErrorHandler;