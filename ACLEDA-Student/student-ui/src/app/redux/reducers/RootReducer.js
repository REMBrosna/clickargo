import { combineReducers } from "redux";
import LoginReducer from "./LoginReducer";
import UserReducer from "./UserReducer";
import LayoutReducer from "./LayoutReducer";
import NotificationReducer from "./NotificationReducer";
import NavigationReducer from "./NavigationReducer";

const RootReducer = combineReducers({
  login: LoginReducer,
  user: UserReducer,
  layout: LayoutReducer,
  notifications: NotificationReducer,
  navigations: NavigationReducer
});

export default RootReducer;
