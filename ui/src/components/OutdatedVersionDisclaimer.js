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

import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faInfoCircle} from "@fortawesome/free-solid-svg-icons/faInfoCircle";

const OutdatedVersionDisclaimer = ({ type, overviewVersion, latestVersion, isOutdated, onVersionChange }) => {

  return (
    <div className="kgs-outdated-version-disclaimer" >
      {isOutdated && overviewVersion && (
        <div className="alert alert-secondary" role="alert">
          <FontAwesomeIcon icon={faInfoCircle} />&nbsp;This is not the newest version of this {type.toLowerCase()}.
          <button className="kgs-instance-link" onClick={() => onVersionChange(latestVersion.value)}>
          &nbsp;Visit {latestVersion.label}
          </button> for the latest version or
          <button className="kgs-instance-link" onClick={() => onVersionChange(overviewVersion.reference)}>
          &nbsp;get an overview of all available versions
          </button>.
        </div>
      )}
    </div>
  );
};

export default OutdatedVersionDisclaimer;