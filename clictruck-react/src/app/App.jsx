import React from "react";
import "../index.css";
import "../fake-db";
import { Provider } from "react-redux";
import { Router, Switch, Route } from "react-router-dom";
import MatxTheme from "./MatxLayout/MatxTheme/MatxTheme";
import AppContext from "./appContext";
import history from "history.js";

import routes from "./RootRoutes";
import { Store } from "./redux/Store";
import { GlobalCss, MatxSuspense } from "matx";
import sessionRoutes from "./views/sessions/SessionRoutes";
import landingRoutes from "./views/landing/LandingRoutes";
import { AuthProvider } from "app/contexts/JWTAuthContext";
import MatxLayout from "./MatxLayout/MatxLayoutSFC";
import AuthGuard from "./auth/AuthGuard";
import C1GlobalCss from "./c1component/C1GlobalCss";
import SsoLogin from "app/views/sessions/SsoLogin";
import C1RefreshCachedData from "./c1component/C1RefreshCachedData";
import "i18n.js";

const App = () => {
  return (
    <AppContext.Provider value={{ routes }}>
      <Provider store={Store}>
        <MatxTheme>
          <GlobalCss>
            <C1GlobalCss>
              <Router history={history}>
                <Route
                  key={1}
                  path={"/session/sso"}
                  component={SsoLogin}
                />
                <AuthProvider>
                  <MatxSuspense>
                    <Switch>
                      {/* AUTHENTICATION PAGES */}
                      {sessionRoutes.map((item, ind) => (
                        <Route
                          key={ind}
                          path={item.path}
                          component={item.component}
                        />
                      ))}

                      {/* LANDING PAGES */}
                      {landingRoutes.map((item, ind) => (
                        <Route
                          key={ind}
                          path={item.path}
                          component={item.component}
                        />
                      ))}

                      {/* AUTH PROTECTED PAGES */}
                      <AuthGuard>
                        <MatxLayout />
                        <C1RefreshCachedData />
                      </AuthGuard>
                    </Switch>
                  </MatxSuspense>
                </AuthProvider>
              </Router>
            </C1GlobalCss>
          </GlobalCss>
        </MatxTheme>
      </Provider>
    </AppContext.Provider>


  );
};

export default App;
