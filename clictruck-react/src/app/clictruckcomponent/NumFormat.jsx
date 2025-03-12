import React from 'react';
import PropTypes from 'prop-types';
import { NumericFormat } from 'react-number-format';

const NumFormat = React.forwardRef((props, ref) => {
    const { onChange, ...other } = props;

    return (
        <NumericFormat
            {...other}
            inputRef={ref}
            onValueChange={(values) => {
                onChange({
                    target: {
                        name: props.name,
                        value: values.value,
                    },
                });
            }}
            thousandSeparator="."
            decimalSeparator=","
            valueIsNumericString
        />
    );
});

NumFormat.propTypes = {
    name: PropTypes.string.isRequired,
    onChange: PropTypes.func.isRequired
};

export default NumFormat;
