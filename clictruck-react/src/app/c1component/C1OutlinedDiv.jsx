import React from "react";
import ReactDOM from "react-dom";
import InputLabel from "@material-ui/core/InputLabel";
import NotchedOutline from "@material-ui/core/OutlinedInput/NotchedOutline";
import { withStyles } from "@material-ui/core/styles";
import clsx from "clsx";


const styles = {
    root: {
        "& $notchedOutline": {
            borderColor: "rgba(0, 0, 0, 0.12)"
        },
        position: "relative",
        margin: "20px 0 20px 0",
        color: "rgba(0, 0, 0, .5)"
    },
    contentWrapper: {
        position: "relative",
    },
    content: {
        padding: "18.5px 14px"
    },
    inputLabel: {
        position: "absolute",
        left: 0,
        top: 0,
        // slight alteration to spec spacing to match visual spec result
        transform: "translate(0, 24px) scale(1)",
        fontWeight: "bold"
    },
    notchedOutline: {}
};

const C1OutlinedDiv = ({ classes, id, label, status = null, children, className }) => {
    const [labelWidth, setLabelWidth] = React.useState(0);
    const labelRef = React.useRef(null);
    React.useEffect(() => {
        const labelNode = ReactDOM.findDOMNode(labelRef.current);
        setLabelWidth(labelNode != null ? labelNode.offsetWidth : 0);
    }, [label]);

    return (
        <div className={clsx(className, classes.root)}>
            <InputLabel
                ref={labelRef}
                htmlFor={id}
                variant="outlined"
                className={classes.inputLabel}
                shrink>
                {label}
            </InputLabel>
            <div className={classes.contentWrapper}>
                <div id={id} className={classes.content}>
                    {children}
                    <NotchedOutline
                        className={classes.notchedOutline}
                        notched
                        labelWidth={labelWidth}
                    />
                </div>
            </div>
        </div>
    );
};
export default withStyles(styles)(C1OutlinedDiv);
