
import React from "react";

// https://stackoverflow.com/questions/20851533/react-js-wrapping-one-component-into-another

const C1WrappedWithLabel = ({label, children}) => (
    <div>
      <div>{label}</div>
      <div>{children}</div>
    </div>
  );
  
export default C1WrappedWithLabel;