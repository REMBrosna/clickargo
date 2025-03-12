import { combineReducers } from "redux";
import LoginReducer from "./LoginReducer";
import UserReducer from "./UserReducer";
import LayoutReducer from "./LayoutReducer";
import NotificationReducer from "./NotificationReducer";
import NavigationReducer from "./NavigationReducer";
import UserGuideReducer from "./UserGuideReducer";

const RootReducer = combineReducers({
  login: LoginReducer,
  user: UserReducer,
  layout: LayoutReducer,
  notifications: NotificationReducer,
  navigations: NavigationReducer,
  userGuide: UserGuideReducer
});

export default RootReducer;
