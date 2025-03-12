import React from "react";
import PropTypes from 'prop-types';

const C1Container = ({
    id,
    rendered,
    children
}) => {

    return <div id={id}>
        {rendered ? children : ""}
    </div>;
}

C1Container.propTypes = {
    id: PropTypes.string,
    rendered: PropTypes.bool,
}

export default C1Container;