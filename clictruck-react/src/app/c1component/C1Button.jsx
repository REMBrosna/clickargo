import React from "react";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import { Tooltip } from "@material-ui/core";

const C1Button = ({ type = "button", size = "small", text, color, onClick, disabled, withLoading, tipTitle, props }) => {

    let btnEl = <Button variant="contained"
        color={color}
        type={type}
        size={size}
        fullWidth
        onClick={onClick}
        disabled={withLoading || disabled}
        {...props}> {text}</Button >;

    if (tipTitle) {
        btnEl = <Tooltip title={tipTitle}><span>{btnEl}</span></Tooltip>
    }

    let btnElWithLoading = null;
    if (withLoading) {
        btnElWithLoading = <div className="relative">
            {btnEl}
            {(<CircularProgress size={24} style={{
                position: "absolute",
                top: "50%",
                left: "50%",
                marginTop: -12,
                marginLeft: -12
            }} />)}
        </div>;
    }
    return withLoading ? btnElWithLoading : btnEl;
};

export default C1Button;
