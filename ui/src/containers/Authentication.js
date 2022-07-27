/*
 * Copyright 2018 - 2021 Swiss Federal Institute of Technology Lausanne (EPFL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This open source software code was developed in part or in whole in the
 * Human Brain Project, funded from the European Union's Horizon 2020
 * Framework Programme for Research and Innovation under
 * Specific Grant Agreements No. 720270, No. 785907, and No. 945539
 * (Human Brain Project SGA1, SGA2 and SGA3).
 *
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import {useLocation, useNavigate, matchPath} from "react-router-dom";
import * as actionsAuth from "../actions/actions.auth";
import * as actionsGroups from "../actions/actions.groups";

import { FetchingPanel } from "../components/Fetching/FetchingPanel";
import { BgError } from "../components/BgError/BgError";
import Groups from "./Groups";

const Authentication = ({ authEndpoint, error, authenticatedMode, isLoading, authenticationInitialized, authenticationInitializing, isAuthenticated, isAuthenticating, isLogingOut, login, setUpAuthenticationAndLogin, loadAuthEndpoint, setAuthMode }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const isLogout = !!matchPath({path:"/logout"}, location.pathname);
  const isLive = !!matchPath({path:"/live/*"}, location.pathname);

  const authenticate = () => {
    if (authEndpoint) {
      if (authenticationInitialized) {
        login();
      } else {
        setUpAuthenticationAndLogin(authEndpoint);
      }
    } else {
      loadAuthEndpoint();
    }
  };

  useEffect(() => {
    if (!error && authenticatedMode && !isLoading && !authenticationInitializing && !isAuthenticating && !isAuthenticated && !isLogingOut) {
      if (isLogout) {
        navigate("/");
      }
      authenticate();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [authEndpoint, error, authenticatedMode, isLoading, authenticationInitialized, authenticationInitializing, isAuthenticated, isAuthenticating, isLogingOut, isLogout]);

  const loginBack = () => {
    setAuthMode(true);
  };

  const cancelLogin = () => {
    if (isLive) {
      navigate(location.pathname.replace("/live/", "/instances/"));
    }
    setAuthMode(false);
  };

  if (error) {
    return (
      <BgError message={error} onCancelClick={cancelLogin} cancelLabel="Cancel authentication"  onRetryClick={loginBack} retryLabel="Login" retryVariant="primary" />
    );
  }

  if (isLogout) {
    return (
      <BgError message="You have been successfully logged out" onRetryClick={loginBack} retryLabel="Login" retryVariant="primary" />
    );
  }

  if(isLoading) {
    return (
      <FetchingPanel message="Retrieving authentication endpoint..." />
    );
  }

  if (authenticationInitializing) {
    return (
      <FetchingPanel message="Initalizing authentication..." />
    );
  }

  if (isAuthenticating) {
    return (
      <FetchingPanel message="Authenicating..." />
    );
  }

  if (isLogingOut) {
    return (
      <FetchingPanel message="Loging out..." />
    );
  }

  if (!authenticatedMode || isAuthenticated) {
    return (
      <Groups />
    );
  }

  return null;
};

export default connect(
  state => ({
    authEndpoint: state.auth.authEndpoint,
    error: state.auth.error,
    authenticatedMode: state.auth.authenticatedMode,
    isLoading: state.auth.isLoading,
    authenticationInitialized: state.auth.authenticationInitialized,
    authenticationInitializing: state.auth.authenticationInitializing,
    isAuthenticated: state.auth.isAuthenticated,
    isAuthenticating: state.auth.isAuthenticating,
    isLogingOut: state.auth.isLogingOut
  }),
  dispatch => ({
    setAuthMode: active => {
      if (!active) {
        dispatch(actionsGroups.resetGroups());
      }
      dispatch(actionsAuth.setAuthMode(active));
    },
    login: () => {
      dispatch(actionsAuth.login);
    },
    setUpAuthenticationAndLogin: authEndpoint => {
      dispatch(actionsAuth.setUpAuthenticationAndLogin(authEndpoint));
    },
    loadAuthEndpoint: () => {
      dispatch(actionsAuth.loadAuthEndpoint());
    }
  })
)(Authentication);