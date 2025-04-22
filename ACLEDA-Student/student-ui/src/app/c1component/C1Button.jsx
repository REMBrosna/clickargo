import React from "react";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";

const C1Button = ({ text, color, onClick, disabled, withLoading, props }) => {

    let btnEl = <Button variant="contained"
        color={color}
        size="large"
        fullWidth
        onClick={onClick}
        disabled={withLoading || disabled}
        {...props}> {text}</Button >;

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
