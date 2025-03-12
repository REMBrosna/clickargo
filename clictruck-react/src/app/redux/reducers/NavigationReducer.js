import { navigations } from "app/navigations";
import { SET_USER_NAVIGATION } from "../actions/NavigationAction";

// const initialState = [...navigations];
const initialState = [];

const NavigationReducer = function (state = initialState, action) {
  // console.log("NAVIGATION REDUCER CALLED", action.type);
  switch (action.type) {
    case SET_USER_NAVIGATION: {
      // console.log(action.payload);
      return [...action.payload];
    }
    default: {
      return [...state];
    }
  }
};

export default NavigationReducer;
