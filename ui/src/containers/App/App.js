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

import React, { Suspense, useEffect } from "react";
import { connect } from "react-redux";
import { Route, Routes, useLocation, useNavigate } from "react-router-dom";

import * as actions from "../../actions/actions";
import { Notification } from "../Notification/Notification";

import { FetchingPanel } from "../Fetching/FetchingPanel";
import { InfoPanel } from "../Info/InfoPanel";
import { SessionExpiredErrorPanel } from "../Error/ErrorPanel";
import Footer from "../Footer/Footer";
import Header from "../Header/Header";
import Theme from "../Theme/Theme";

const Search = React.lazy(() => import("../Search/Search"));
const Instance = React.lazy(() => import("../Instance/Instance"));
const Preview = React.lazy(() => import("../Instance/Preview"));
const NotFound = React.lazy(() => import("../../components/NotFound/NotFound"));

import "./App.css";

const App = ({ initialize, isReady }) => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    initialize(location, navigate);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);


  return (
    <>
      <Theme />
      <Header />
      <main>
        <Notification />
        {isReady && (
          <Suspense>
            <Routes>
              <Route path="/" element={<Search />} />
              <Route path="/instances/:id" element={<Instance />} />
              <Route path="/instances/:type/:id" element={<Instance />} />
              <Route path="/live/:org/:domain/:schema/:version/:id" element={<Preview />} />
              <Route path="/live/:id" element={<Preview />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </Suspense>
        )}
        <FetchingPanel />
        <SessionExpiredErrorPanel />
        <InfoPanel />
      </main>
      <Footer />
    </>
  );
};

export default connect(
  state => ({
    isReady: state.application.isReady && !state.auth.error
  }),
  dispatch => ({
    initialize: (location, navigate) => {
      dispatch(actions.initialize(location, navigate));
    }
  })
)(App);