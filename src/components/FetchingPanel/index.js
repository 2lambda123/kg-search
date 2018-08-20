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

import React, { PureComponent } from "react";
import { store } from "../../store";
import "./styles.css";

export class FetchingPanel extends PureComponent {
  constructor(props) {
    super(props);
    this.state = this.getState();
  }
  getState() {
    const globalState = store.getState();
    return {
      show: globalState.fetching.active,
      message: globalState.fetching.message
    };
  }
  handleStateChange() {
    setTimeout(() => {
      const nextState = this.getState();
      this.setState(nextState);
    });
  }
  componentDidMount() {
    document.addEventListener("state", this.handleStateChange.bind(this), false);
    this.handleStateChange();
  }
  componentWillUnmount() {
    document.removeEventListener("state", this.handleStateChange);
  }
  render() {
    if (!this.state.show) {
      return null;
    }
    //window.console.debug("FetchingPanel rendering...");
    return (
      <div className="kgs-fetching-container" >
        <div className="kgs-fetching-panel">
          <span className="kgs-spinner">
            <div className="kgs-spinner-logo"></div>
          </span>
          <span className="kgs-spinner-label">{this.state.message}</span>
        </div>
      </div>
    );
  }
}