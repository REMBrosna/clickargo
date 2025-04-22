import React from 'react';
import { Grid } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import stampImage from "../MatxLayout/Layout2/sample_stamp.jpg";

const useStyles = makeStyles({
    chooseFile: {
        margin: "2rem"
    },
    submitButton: {
        marginTop: 20
    },
    img: {
        width: "50%",
        marginTop: -90
    }
});
const C1FileUpload = () => {
    const classes = useStyles();

    return (
        <Grid>
            <div className={clsx("relative w-full", classes.chooseFile)}>
                <input type="file" name="file" />
                <div className={classes.submitButton}>
                    <button>Submit</button>
                </div>
            </div>
            <div>
                <img src={stampImage} alt="" className={classes.img} />
            </div>
        </Grid>
    );
}

export default C1FileUpload;