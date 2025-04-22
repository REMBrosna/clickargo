import Popper from '@material-ui/core/Popper';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Tooltip from "@material-ui/core/Tooltip";
import InfoIcon from "@material-ui/icons/Info";
import React from "react";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles((theme) => ({
    paper: {
        border: '1px solid',
        padding: theme.spacing(1),
        backgroundColor: "#faf7d4",
        overflowY: 'auto',
        overflowX: 'auto',
        borderRadius: '4px 4px 4px 4px',
        scrollBehavior: 'auto',
        zIndex: 100
    },
    table: {
        maxWidth: '350px',
    },
}));



const C1Version = ({ changes }) => {
    const classes = useStyles();
    const [anchorEl, setAnchorEl] = React.useState(null);
    const { t } = useTranslation(["common"]);

    const handleClick = (event) => {
        event.stopPropagation();
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const open = Boolean(anchorEl);
    const id = open ? 'simple-popper' : undefined;

    return <React.Fragment>
        <Tooltip title={t("tooltip.viewChanges")}>
            <InfoIcon color="primary" fontSize="small" onClick={handleClick} cursor="pointer" />
        </Tooltip>
        <Popper id={id} open={open} anchorEl={anchorEl}
            placement="right-end"
            disablePortal={true}
            modifiers={{
                flip: {
                    enabled: true,
                },
                preventOverflow: {
                    enabled: true,
                    boundariesElement: 'scrollParent',
                },
                arrow: {
                    enabled: true
                },
                offset: {
                    options: {
                        offset: [20, 0]
                    }

                }
            }} style={{ zIndex: 100 }}>

            {/* <div className={classes.paper}>{longText}</div> */}
            <div className={classes.paper}>
                <Table className={classes.table} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>{t("tooltip.changes")}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        <TableRow >
                            <TableCell component="th" scope="row">
                                {changes}
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </div>
        </Popper>
    </React.Fragment>


}

export default C1Version;