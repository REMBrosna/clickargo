import { createContext, useContext, useState } from "react";

export const ListEventContext = createContext();
export const useListEvents = () => useContext(ListEventContext);

export const ListEventProvider = ({ children }) => {
  const [showSpecialInst, setShowSpecialInst] = useState(false);
  const [showDetailed, setShowDetailed] = useState(false);

  const toggleSpecialIns = () => {
    setShowSpecialInst(!showSpecialInst);
  };

  const toggleDetailed = () => {
    setShowDetailed(!showDetailed);
  };

  return (
    <ListEventContext.Provider
      value={{
        showSpecialInst,
        toggleSpecialIns,
        showDetailed,
        toggleDetailed,
      }}
    >
      {children}
    </ListEventContext.Provider>
  );
};
