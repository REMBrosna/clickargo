import React from "react";
import Button from "@material-ui/core/Button";
import { Tooltip } from "@material-ui/core";

/**
 * @param childPosition - options are left, right, center
 */
const C1IconButton = ({ tooltip, childPosition, children, ...rest }) => {

    let btn = <Button style={childPosition ? { float: childPosition } : null} {...rest}>
        {children}
    </Button>;

    if (rest?.disabled) {
        //to resolve warning
        btn = <span {...rest}>{btn}</span>
    }
    return (<Tooltip title={tooltip}>
        {btn}
    </Tooltip>)
};

export default C1IconButton;
