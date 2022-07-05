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
import { connect } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import ReactPiwik from "react-piwik";

import * as actionsSearch from "../../../actions/actions.search";

import "./HitsInfo.css";

const Suggestion =  ({word, searchTerm, isSecondLast, isLast=true, onClick}) => {

  const location = useLocation();
  const navigate = useNavigate();

  const handleOnClick = () => onClick(searchTerm, location, navigate);

  return (
    <li><button className="kgs-suggestion__btn" role="link" onClick={handleOnClick} title={`search for ${word}`}>{word}</button>{isSecondLast?" or ":isLast?"":", "}</li>
  );
};

const Viewing = ({ hitCount, from, to }) => (
  <>
    Viewing <span className="kgs-hitsInfos-highlight">{from}-{to}</span> of <span className="kgs-hitsInfos-highlight">{hitCount}</span> results.
  </>
);

const Suggestions =  ({words, onClick}) => (
  <ul className="kgs-suggestions">
    {Object.keys(words).map((word, idx) => (
      <Suggestion key={word} word={word} searchTerm={words[word]} isSecondLast={idx === Object.keys(words).length -2} isLast={idx === Object.keys(words).length -1} onClick={onClick} />
    ))}
  </ul>
);

export const HitsInfoBase = ({show, message, suggestions, hitCount, from, to, setQueryString}) => {
  if (!show) {
    return null;
  }

  if (message) {
    return (
      <span className="kgs-hitsInfos">{message}</span>
    );
  }

  if (Object.keys(suggestions).length>0) {
    return (
      <span className="kgs-hitsInfos">
        {hitCount === 0?
          "No results were found. "
          :
          <Viewing hitCount={hitCount} from={from} to={to} />
        }<br/>Did you mean <Suggestions words={suggestions} onClick={setQueryString} />?</span>
    );
  }
  if (hitCount === 0) {
    return (
      <span className="kgs-hitsInfos">No results were found. Please refine your search.</span>
    );
  }
  if (from > hitCount) {
    return (
      <span className="kgs-hitsInfos">No results were found. Only {hitCount} results are availalbe. Please navigate to previous page(s).</span>
    );
  }
  return (
    <span className="kgs-hitsInfos"><Viewing hitCount={hitCount} from={from} to={to} /></span>
  );
};

export const HitsInfo = connect(
  state => {
    const from = (state.search.from?state.search.from:0) + 1;
    const count = state.search.hits?state.search.hits.length:0;
    const to = from + count - 1;
    return {
      show: !state.search.isLoading,
      message: state.search.message,
      suggestions: state.search.suggestions,
      hitCount: state.search.total?state.search.total:0,
      from: from,
      to: to
    };
  },
  dispatch => ({
    setQueryString: (value, location, navigate) => {
      ReactPiwik.push(["trackEvent", "Search", "Refine search using suggestion", value]);
      let to = "/";
      const find = location.search.split("&").find(p => p.match(/\??q=.*/));
      if (find) {
        to = location.search.replace(find, `${find.startsWith("?")?"?":""}q=${encodeURIComponent(value)}`);
      } else {
        to = location.search + location.search.endsWith("?")?"q=":"&q=" + encodeURIComponent(value);
      }
      navigate(to);
      dispatch(actionsSearch.setQueryString(value));
    }
  })
)(HitsInfoBase);