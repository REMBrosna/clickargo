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

const withErrorHandler = (WrappedComponent) => {


    return class extends Component {

        state = {
            error: null,
            open: false
        }

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
            if (this.state.error && this.state.error.err && sessionTimeout === this.state.error.err.msg) {
                return <Redirect to='/session/timeout' />;
            }
            if (this.state.error && this.state.error.err && noPermission === this.state.error.err.msg) {
                return <Redirect to='/session/nopermission' />;
            }
            return (
                <AuxDiv>
                    <Modal
                        aria-labelledby="transition-modal-title"
                        aria-describedby="transition-modal-description"
                        open={this.state.open}
                        onClose={this.handleClose}
                        BackdropComponent={Backdrop}
                        BackdropProps={{
                            timeout: 500,
                        }}
                        centered="true"
                        style={{
                            position: 'absolute',
                            top: '30%',
                            margin: 'auto',
                            width: 600,
                        }}  >
                        <Fade in={this.state.open}>
                            <div className={classes.ModalPaper}>
                                <h2 id="transition-modal-title" style={{ backgroundColor: '#FF0000', color: '#fff', padding: '4px' }}>Error</h2>
                                <Grid item lg={12} md={12} xs={12} style={{ padding: '6px' }}>
                                    <Grid container alignItems="center" className={classes.gridContainer}>
                                        <Grid item xs={12} >
                                            <C1InputField label="Code" disabled
                                                value={this.state.error && this.state.error.err ? this.state.error.err.code : '-1'} />
                                        </Grid>
                                        <Grid item xs={12} >
                                            <C1TextArea label="Description" disabled
                                                value={this.state.error && this.state.error.err ? this.state.error.err.msg : 'Something went wrong!'} />
                                        </Grid>
                                    </Grid>

                                </Grid>
                                <Typography variant="body2" style={{ padding: '6px' }}>Please try again and contact PortEDI support if problem persist.</Typography>
                            </div>
                        </Fade>
                    </Modal>
                    <WrappedComponent {...this.props} />
                </AuxDiv>

            );
        }

    }
}

export default withErrorHandler;