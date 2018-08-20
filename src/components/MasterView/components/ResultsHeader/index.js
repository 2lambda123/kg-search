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

import React from "react";
import { ActionBar, ActionBarRow } from "searchkit";
import { HitStatsPanel } from "../HitStatsPanel";
import { PaginationPanel } from "../PaginationPanel";
import { LayoutModeSwitcherToggle } from "../LayoutModeSwitcherToggle";
import { SortByPanel } from "../SortByPanel";
import "./styles.css";

export function ResultsHeader() {
  return (
    <div className="kgs-result-header">
      <ActionBar>
        <ActionBarRow>
          <HitStatsPanel/>
          <PaginationPanel className="kgs-header-pagination" />
          <LayoutModeSwitcherToggle/>
          <SortByPanel/>
        </ActionBarRow>
      </ActionBar>
    </div>
  );
}
