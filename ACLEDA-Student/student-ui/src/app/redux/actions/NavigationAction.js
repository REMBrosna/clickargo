import { navigations } from "../../navigations";

export const SET_USER_NAVIGATION = "SET_USER_NAVIGATION";


const getfilteredNavigations = (navList = [], role) => {
  return navList.reduce((array, nav) => {
    if (nav.auth) {
      if (nav.auth.includes(role)) {
        array.push(nav);
      }
    } else {
      if (nav.children) {
        nav.children = getfilteredNavigations(nav.children, role);
        array.push(nav);
      } else {
        array.push(nav);
      }
    }
    return array;
  }, []);
};

export function getNavigationByUser(auth) {
  return (dispatch, getState) => {
    let { navigations = [] } = getState();
    const role = auth?.authorities?.[0];

    let filteredNavigations = getfilteredNavigations(navigations, role);

    dispatch({
      type: SET_USER_NAVIGATION,
      // payload: navigations
      payload: filteredNavigations
    });
  };
}

/**Retrieves the menu from backend. */
export function getMenuByUser(user) {
  return (dispatch, getState) => {
    dispatch({
      type: SET_USER_NAVIGATION,
      payload: navigations
      // payload: navigations
    });

  };
}

