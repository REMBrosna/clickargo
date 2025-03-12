import { SET_USER_GUIDE } from "../actions/UserGuideAction";

// const initialState = [...navigations];
const initialState = [];

const UserGuideReducer = function (state = initialState, action) {
  switch (action.type) {
    case SET_USER_GUIDE: {
      if (action?.payload) return [...action?.payload];
    }
    default: {
      return [...state];
    }
  }
};

export default UserGuideReducer;
