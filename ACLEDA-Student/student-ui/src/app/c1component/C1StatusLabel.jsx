
import React from "react";
import { green, orange, blue, red, purple, cyan } from '@material-ui/core/colors';

const C1StatusLabel = ({ value }) => {
    //console.log("value= " + value, JSON.stringify(value));

    switch (value) {
        case 'Draft':
        case 'D':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: blue[200], color: blue[800] }}>
                    Draft
                </small>
            );
        case 'Submit':
        case 'Submitted':
        case 'Pending Verification':
        case 'Pending Acknowledgement':
        case 'Pending':
        case 'P':
        case 'S':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: orange[200], color: orange[800] }}>
                    Submitted
                </small>
            );
        case 'Pending Approval':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: blue[200], color: blue[800] }}>
                    Verified
                </small>
            );
        case 'Approved':
        case 'A':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: green[200], color: green[800] }}>
                    Approved
                </small>
            );
        case 'Acknowledged':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: green[200], color: green[800] }}>
                    Acknowledged
                </small>
            );
        case 'Rejected':
        case 'R':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: red[200], color: red[800] }}>
                    Rejected
                </small>
            );
        case 'Active':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: green[200], color: green[800] }}>
                    Active
                </small>
            );
        case 'Inactive':
            return (
                <small className="px-1 py-2px border-radius-4" style={{ backgroundColor: red[200], color: red[800] }}>
                    Inactive
                </small>
            );
        default: return <div> {value}</div>;
    }
}

export default C1StatusLabel;