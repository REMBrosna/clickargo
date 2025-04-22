import React, { useState, useEffect } from "react";
import { Breadcrumb } from "matx";
import { orange, red } from "@material-ui/core/colors";
import Tooltip from "@material-ui/core/Tooltip";
import FaceIcon from '@material-ui/icons/Face';
import { useHistory } from "react-router-dom";
import Card from "@material-ui/core/Card";
import Grid from "@material-ui/core/Grid";
import Divider from "@material-ui/core/Divider";
import Snackbar from "@material-ui/core/Snackbar";
import { Formik, Form } from "formik";
import Typography from '@material-ui/core/Typography';
import PropTypes from 'prop-types';
import C1Alert from "./C1Alert";
import { useTranslation } from "react-i18next";
import { Badge, Chip } from "@material-ui/core";

/**
 * Form component. 
 * Actions for buttons such as form save, submit, approve or reject may be done here.
 * Snackbar (prompt for successful form save/submit is placed here so that dev need not code for 
 * each specific form panels).
 */
const C1FormDetailsPanel = ({
    breadcrumbs,
    title,
    subTitle,
    titleEx,
    formButtons,
    initialValues,
    values,
    onSubmit,
    onValidate,
    snackBarOptions,
    isLoading,
    showBreadcrumbs = true,
    allowRedirect = {
        show: true
    },
    children,
    noValidate,
}) => {


    const [snackBarState, setSnackBarState] = useState({
        open: false,
        vertical: 'top',
        horizontal: 'center',
        msg: '',
        severity: 'success'
    });

    const { t } = useTranslation(["common"]);
    const history = useHistory();

    const handleSnackBarClose = () => {
        setSnackBarState({ ...snackBarState, open: false });
        if (snackBarOptions && snackBarOptions.redirectPath && snackBarOptions.success) {
            //only redirect if it's success
            let url = snackBarOptions.redirectPath;
            snackBarOptions = null;
            const onCloseSnackbar = allowRedirect?.onCloseSnackbar;
            allowRedirect?.show ? history.push(url) : onCloseSnackbar && onCloseSnackbar(true);
        }
    };

    useEffect(() => {
        //this is for prompting if user refreshes the page
        window.onbeforeunload = function () {
            return true;
        };

        //only change snackbar state if it's has snackBarOptions is present
        if (snackBarOptions) {
            let msg = "";
            let severity = "";
            let open = false;
            if (snackBarOptions.success) {
                open = true;
                msg = snackBarOptions.successMsg || t("genericMsgs.success");
                severity = "success";
            } else if (snackBarOptions.error) {
                open = true;
                msg = snackBarOptions.errorMsg || t("genericMsgs.error");
                severity = "error";
            }

            setSnackBarState(sb => { return { ...sb, open: open, msg: msg, severity: severity } });

        }

        //clean up
        return () => {
            window.onbeforeunload = null;
        };
        // eslint-disable-next-line
    }, [snackBarOptions]);

    let snackBar = null;
    if (snackBarOptions && snackBarState && snackBarState.open) {

        const anchorOriginV = snackBarState.vertical;
        const anchorOriginH = snackBarState.horizontal;

        snackBar = <Snackbar
            anchorOrigin={{ vertical: anchorOriginV, horizontal: anchorOriginH }}
            open={snackBarState.open}
            onClose={handleSnackBarClose}
            autoHideDuration={snackBarState.severity === 'success' ? 2000 : 3000}
            key={anchorOriginV + anchorOriginH
            }>
            <C1Alert onClose={handleSnackBarClose} severity={snackBarState.severity}>
                {snackBarState.msg}
            </C1Alert>
        </Snackbar>;
    }

    return (
        <div className="m-sm-30">
            {showBreadcrumbs && (
                <div className="mb-sm-30">
                    <Breadcrumb routeSegments={breadcrumbs} />
                </div>
            )}
            {!isLoading && snackBar}
            <Formik
                initialValues={initialValues}
                onSubmit={(values, actions) => onSubmit(values, actions)}
                enableReinitialize={true}
                values={values}
                validate={onValidate}>
                {(props) => (
                    <Form noValidate={noValidate}>
                        <Card elevation={3}>
                            <Grid container spacing={0} alignItems="flex-start" justify="flex-start">
                                <Grid item lg={6} md={6} xs={12} >
                                    <Grid container item alignItems="flex-start" justify="flex-start">
                                        <div className="flex p-3">
                                            <Grid container alignItems="center">
                                                <Grid item lg={12} md={12} xs={12}>
                                                    {subTitle ?
                                                        <div>
                                                            <Typography variant="h5" style={{ marginTop: '10px' }}>
                                                                {title}
                                                            </Typography>
                                                            <Typography variant="body2" display="block" gutterBottom>
                                                                <small style={{ color: '#fff', backgroundColor: red['A400'] }}>{subTitle}</small>
                                                            </Typography>
                                                        </div>
                                                        : <Typography variant="h5" style={{ marginTop: '10px' }}>
                                                            {title}  {titleEx && <Tooltip title={t("common:common.msg.resubmitted")}>
                                                                <small style={{ color: orange[800] }}>{titleEx}</small>
                                                            </Tooltip>}
                                                        </Typography>
                                                    }
                                                </Grid>
                                            </Grid>

                                        </div>
                                    </Grid>
                                </Grid>
                                <Grid item lg={6} md={6} xs={12} >
                                    <Grid container item alignItems="flex-end" justify="flex-end">
                                        <div className="flex p-3">
                                            {formButtons}
                                        </div>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Divider className="mb-2" />
                            {children(props)}
                        </Card>
                    </Form>
                )}
            </Formik>
        </div>
    );
}

C1FormDetailsPanel.propTypes = {
    breadcrumbs: PropTypes.array,
    title: PropTypes.any,
    titleEx: PropTypes.any,
    formButtons: PropTypes.element,
    initialValues: PropTypes.object,
    values: PropTypes.object,
    onSubmit: PropTypes.func,
    onValidate: PropTypes.func,
    snackBarOptions: PropTypes.exact({
        success: PropTypes.bool,
        successMsg: PropTypes.string,
        error: PropTypes.any,
        errorMsg: PropTypes.string,
        redirectPath: PropTypes.string
    }),
    isLoading: PropTypes.bool,
    children: PropTypes.any,
    noValidate: PropTypes.bool,
}

C1FormDetailsPanel.defaultProps = {
    noValidate: false,
};

export default C1FormDetailsPanel;
