/*
*   Copyright (c) 2018, EPFL/Human Brain Project PCO
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

import API from "./API";
import * as actions from "../actions";
import SearchManager from "./search.manager";
import { generateKey, getHashKey, getSearchKey, searchToObj, getAuthUrl } from "../helpers/OIDCHelpers";

const regReferenceHash = /^#(.+)$/;
const regPreviewReference = /^(((.+)\/(.+)\/(.+)\/(.+))\/(.+))$/;

export default class AppManager {
  constructor(store, options) {
    this.store = store;
    this.isStarted = false;
    this.initialGroup = null;
    this.initialInstanceReference = null;
    this.hitIssuer = null;
    this.previousStateInstances = store.getState().instances;
    this.isEventFiredByBrowserNav = false;
    this.isEventFiredByAppNav = false;
    this.locationHref = window.location.href;
    this.unsubscribe = null;
    this.searchInterfaceIsDisabled = false;
    let startHistory = null;
    const accessToken = getHashKey("access_token");
    let initialGroup = null;
    let initialInstanceReference = null;
    if (accessToken) {
      const aState = getHashKey("state");
      const state = aState?JSON.parse(atob(aState)):null;
      if (state) {
        if (state.group && state.group !== API.defaultGroup) {
          initialGroup = state.group;
        }
        if (state.instanceReference) {
          initialInstanceReference = state.instanceReference;
        }
        this.searchInterfaceIsDisabled = initialInstanceReference && (state.search === "false" || regPreviewReference.test(initialInstanceReference));
        startHistory = window.location.protocol + "//" + window.location.host + window.location.pathname + Object.entries(state).reduce((result, [key, value]) => {
          return (key === "instanceReference"?result:(result + (result === ""?"?":"&") + key + "=" + value));
        }, "");
      }
      //const expiresIn = getHashKey("expires_in");
    } else {
      initialGroup = getSearchKey("group");
      initialInstanceReference = regReferenceHash.test(window.location.hash)?window.location.hash.match(regReferenceHash)[1]:null;
      this.searchInterfaceIsDisabled = initialInstanceReference && getSearchKey("search", true) === "false";
      startHistory = window.location.protocol + "//" + window.location.host + window.location.pathname + window.location.search;
    }

    if (!accessToken && ((initialGroup && initialGroup !== API.defaultGroup) || regPreviewReference.test(initialInstanceReference))) {
      this.authenticate();

    } else {

      this.search = new SearchManager(store, this.searchInterfaceIsDisabled);

      if (initialGroup) {
        this.initialGroup = initialGroup;
      }
      if (initialInstanceReference) {
        this.initialInstanceReference = initialInstanceReference;
      }

      if (accessToken) {
        store.dispatch(actions.authenticate(accessToken));
      }

      const historyState = window.history.state;
      window.history.replaceState(historyState, "Knowledge Graph Search", startHistory);

      this.start(options);
    }
  }

  start(options) {
    if (!this.isStarted) {
      this.isStarted = true;
      const store = this.store;
      this.unsubscribe = store.subscribe(() => {this.handleStateChange();});
      window.addEventListener("hashchange", this.catchBrowserNavigationChange.bind(this), false);
      store.dispatch(actions.initializeConfig(options));
    }
  }

  stop() {
    this.unsubscribe && this.unsubscribe();
    window.removeEventListener("hashchange", this.catchBrowserNavigationChange);
  }

  authenticate() {
    const config = this.store.getState().configuration;
    const reference = regReferenceHash.test(window.location.hash)?window.location.hash.match(regReferenceHash)[1]:null;
    const state1 = regPreviewReference.test(reference)?null:searchToObj(window.location.search);
    const state2 = reference?{instanceReference: reference}:null;
    const state = {...state1, ...state2};
    const stateKey = btoa(JSON.stringify(state));
    const nonceKey = generateKey();
    window.location.href = getAuthUrl(config.oidcUri, config.oidcClientId, stateKey, nonceKey);
  }

  handleStateChange = () => {
    const store = this.store;
    const state = store.getState();

    if (state.auth.authenticate) {
      this.authenticate();
    }

    if (!state.application.isReady) {

      if (state.search.isReady) {

        if (!state.definition.isReady) {
          if (!state.definition.isLoading && !state.definition.hasError) {
            store.dispatch(actions.loadDefinition(state.configuration.searchApiHost));
          }
          return;
        }

        if (!state.groups.isReady) {
          if (!state.groups.hasRequest && !state.groups.isLoading && !state.groups.hasError) {
            store.dispatch(actions.loadGroups());
          }
          return;
        }

        if (state.groups.isReady && this.initialGroup && this.initialGroup !== API.defaultGroup) {
          const group = this.initialGroup;
          this.initialGroup = null;
          if (state.groups.groups.some(e => e.value === group)) {
            store.dispatch(actions.setGroup(group, true));
            return;
          }
        }

        store.dispatch(actions.setApplicationReady(true));
      }

      return;
    }

    if (this.initialInstanceReference && (this.searchInterfaceIsDisabled || state.search.initialRequestDone)) {
      const reference = this.initialInstanceReference;
      this.initialInstanceReference = null;
      //window.console.log("AppManager load initial instance: " + reference);
      store.dispatch(actions.loadInstance(reference));

      return;
    }

    //window.console.debug("App Manager state change", state);

    //Remove the ability to scroll the body when the modal is open
    this.setBodyScrolling(!state.instances.currentInstance && !state.application.info);

    // store detail view laucher button in order to set back focus to it when detail popup close
    this.manageHitFocus(state.instances.currentInstance);

    this.manageHistory(state);
  }

  setBodyScrolling(enableScrolling) {
    //Remove the ability to scroll the body when the modal is open
    if (enableScrolling) {
      document.documentElement.style.overflow = "";
      document.body.style.overflow = "";
    } else {
      document.documentElement.style.overflow = "hidden";
      document.body.style.overflow = "hidden";
    }
  }

  manageHitFocus(hit) {
    if (hit) {
      // store detail view laucher button in order to set back focus to it when detail popup close
      this.hitIssuer = hit;

    } else if (this.hitIssuer) {
      // on detail popup close put back focus to issuer
      const node = document.body.querySelector(`button[data-reference="${this.hitIssuer._type}/${this.hitIssuer._id}"]`);
      node && node.focus();
      this.hitIssuer = null;
    }
  }

  manageHistory(state) {
    // check history todos
    const pauseSearchkitHistoryListening = state.instances.currentInstance && !this.previousStateInstances.currentInstance;
    const resumeSearchkitHistoryListening = !state.instances.currentInstance && this.previousStateInstances.currentInstance;
    const pushHistoryState = (state.instances.currentInstance && !this.previousStateInstances.currentInstance)
        || state.instances.previousInstances.length > this.previousStateInstances.previousInstances.length;
    const backHistoryCounts = ((context, previousState, state) => {
      if (context.isEventFiredByBrowserNav) {
        context.isEventFiredByBrowserNav = false;
        return 0;
      }
      let backs = previousState.previousInstances.length - state.previousInstances.length;
      if (backs < 0) {
        backs = 0;
      }
      if (previousState.currentInstance && !state.currentInstance) {
        backs++;
      }
      return backs<0?0:backs;
    })(this, this.previousStateInstances, state.instances);

    this.previousStateInstances = Object.assign({}, state.instances);

    // apply history todos
    if (pauseSearchkitHistoryListening) {
      this.search && this.search.searchkit && this.search.searchkit.unlistenHistory();
    }
    if (pushHistoryState) {
      //window.console.debug(new Date().toLocaleTimeString() + ": new history");
      const historyState = window.history.state;
      window.history.pushState(historyState, "Knowledge Graph Search", window.location.href.replace(/#.*$/,"") + "#" + (regPreviewReference.test(state.instances.currentInstance._id)?state.instances.currentInstance._id:`${state.instances.currentInstance._type }/${state.instances.currentInstance._id}`));
    }
    if (backHistoryCounts) {
      //window.console.debug(new Date().toLocaleTimeString() + ": back history: " + backHistoryCounts);
      this.isEventFiredByAppNav = true;
      [...Array(backHistoryCounts)].forEach(() => window.history.back());
    }
    if (resumeSearchkitHistoryListening && this.search.searchkit && this.searchkit.history) {
      setTimeout(() => {
        this.search && this.search.searchkit && this.search.searchkit.listenToHistory.call(this.search.searchkit);
      },0);
    }
  }

  setCurrentInstanceFromBrowserLocation() {
    if (this.isStarted) {
      const store = this.store;
      store.dispatch(actions.setCurrentInstanceFromBrowserLocation());
    }
  }

  catchBrowserNavigationChange() {
    if (this.isEventFiredByAppNav) {
      this.isEventFiredByAppNav = false;
    } else {
      //window.console.debug(new Date().toLocaleTimeString() + ": nav change");
      this.isEventFiredByBrowserNav = true;
      this.setCurrentInstanceFromBrowserLocation();
    }
  }
}