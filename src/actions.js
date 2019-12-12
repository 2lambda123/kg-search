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

import * as types from "./actions.types";
import API from "./services/API";
import axios from "axios";
import ReactPiwik from "react-piwik";
import { ElasticSearchHelpers } from "./helpers/ElasticSearchHelpers";

export const setApplicationReady = isReady => {
  return {
    type: types.SET_APPLICATION_READY,
    isReady: isReady
  };
};

export const agreeTermsShortNotice = () => {
  return {
    type: types.AGREE_TERMS_SHORT_NOTICE
  };
};

export const setInfo = text => {
  return {
    type: types.SET_INFO,
    text: text
  };
};

export const setLayoutMode = gridLayoutMode => {
  return {
    type: types.SET_LAYOUT_MODE,
    gridLayoutMode: gridLayoutMode
  };
};

export const initializeConfig = options => {
  return {
    type: types.INITIALIZE_CONFIG,
    options: options
  };
};

export const loadDefinitionRequest = () => {
  return {
    type: types.LOAD_DEFINITION_REQUEST
  };
};

export const loadDefinitionSuccess = definition => {
  return {
    type: types.LOAD_DEFINITION_SUCCESS,
    definition: definition
  };
};

export const loadDefinitionFailure = error => {
  return {
    type: types.LOAD_DEFINITION_FAILURE,
    error: error
  };
};

export const loadGroupsRequest = () => {
  return {
    type: types.LOAD_GROUPS_REQUEST
  };
};

export const loadGroupsSuccess = groups => {
  return {
    type: types.LOAD_GROUPS_SUCCESS,
    groups: groups
  };
};

export const loadGroupsFailure = error => {
  return {
    type: types.LOAD_GROUPS_FAILURE,
    error: error
  };
};

export const loadSearchBadRequest = status => {
  return {
    type: types.LOAD_SEARCH_BAD_REQUEST,
    status: status
  };
};

export const loadSearchServiceFailure = (status, group) => {
  return {
    type: types.LOAD_SEARCH_SERVICE_FAILURE,
    status: status,
    group: group
  };
};

export const loadSearchSessionFailure = status => {
  return {
    type: types.LOAD_SEARCH_SESSION_FAILURE,
    status: status
  };
};

export const loadSearchRequest = () => {
  return {
    type: types.LOAD_SEARCH_REQUEST
  };
};

export const loadSearchResult = (results, group, from) => {
  return {
    type: types.LOAD_SEARCH_SUCCESS,
    results: results,
    group: group,
    from: from
  };
};

export const cancelSearch = () => {
  return {
    type: types.CANCEL_SEARCH
  };
};

export const setGroup = (group, initialize) => {
  return {
    type: types.SET_GROUP,
    group: group,
    initialize: initialize
  };
};

export const loadInstance = reference => {
  return {
    type: types.LOAD_INSTANCE,
    reference: reference
  };
};

export const loadInstanceRequest = () => {
  return {
    type: types.LOAD_INSTANCE_REQUEST
  };
};

export const loadInstanceSuccess = data => {
  return {
    type: types.LOAD_INSTANCE_SUCCESS,
    data: data
  };
};

export const loadInstanceNoData = reference => {
  return {
    type: types.LOAD_INSTANCE_NO_DATA,
    reference: reference
  };
};

export const loadInstanceFailure = (id, error) => {
  return {
    type: types.LOAD_INSTANCE_FAILURE,
    id: id,
    error: error
  };
};

export const setInstance = data => {
  return {
    type: types.SET_INSTANCE,
    data: data
  };
};

export const cancelInstanceLoading = () => {
  return {
    type: types.CANCEL_INSTANCE_LOADING
  };
};

export const setPreviousInstance = () => {
  return {
    type: types.SET_PREVIOUS_INSTANCE
  };
};

export const clearAllInstances = () => {
  return {
    type: types.CLEAR_ALL_INSTANCES
  };
};

export const setCurrentInstanceFromBrowserLocation = () => {
  return {
    type: types.SET_CURRENT_INSTANCE_FROM_BROWSER_LOCATION
  };
};

export const authenticate = accessToken => {
  return {
    type: types.AUTHENTICATE,
    accessToken: accessToken
  };
};

export const requestAuthentication = () => {
  return {
    type: types.REQUEST_AUTHENTICATION
  };
};

export const logout = () => {
  return {
    type: types.LOGOUT
  };
};

export const showImage = (url, label) => {
  return {
    type: types.SHOW_IMAGE,
    url: url,
    label: label
  };
};

export const setQueryString = value => {
  return {
    type: types.SET_QUERY_STRING,
    queryString: value
  };
};

export const setType = value => {
  return {
    type: types.SET_TYPE,
    value: value
  };
};

export const setSort = value => {
  return {
    type: types.SET_SORT,
    value: value
  };
};

export const setFacet = (id, active, keyword) => {
  return {
    type: types.SET_FACET,
    id: id,
    active: active,
    keyword: keyword,
  };
};

export const resetFacets = () => {
  return {
    type: types.RESET_FACETS
  };
};

export const doSearch = (searchParams, group) => {
  return dispatch => {
    dispatch(loadSearchRequest());
    ReactPiwik.push(["setCustomUrl", window.location.href]);
    ReactPiwik.push(["trackPageView"]);
    axios
      .post(API.endpoints.search(), ElasticSearchHelpers.buildRequest(searchParams))
      .then(response => {
        const index = response.headers["X-Selected-Index"];
        dispatch(loadSearchResult(response.data, index ? index.slice(3) : group, searchParams.from));
      })
      .catch(error => {
        const { response } = error;
        const { status } = response;
        switch (status) {
        case 400: // Bad Request
        case 404: // Not Found
          dispatch(loadSearchBadRequest(status));
          break;
        case 401: // Unauthorized
        case 403: // Forbidden
        case 511: // Network Authentication Required
          dispatch(loadSearchSessionFailure(status));
          break;
        default:
        {
          const index = response.headers["X-Selected-Index"];
          dispatch(loadSearchServiceFailure(status, index ? index.slice(3) : group));
        }
        }
      });
  };
};

export const loadDefinition = (onSuccess, onError) => {

  const GRAPHQUERY_NAMESPACE = "https://schema.hbp.eu/graphQuery/";
  const SEARCHUI_NAMESPACE = "https://schema.hbp.eu/searchUi/";
  const SCHEMA_ORG = "http://schema.org/";

  const getFieldAndRemove = (field, key, defaultValue) => {
    let valueFromField = field[key];
    if (valueFromField !== undefined && valueFromField !== null) {
      delete field[key];
      return valueFromField;
    }
    return defaultValue;
  };

  const simplifySemanticKeysForField = field => {
    field.value = getFieldAndRemove(field, GRAPHQUERY_NAMESPACE + "label", null);
    field.sort = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "sort", false);
    field.markdown = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "markdown", false);
    field.labelHidden = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "labelHidden", false);
    field.tagIcon = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "tag_icon", null);
    field.linkIcon = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "link_icon", null);
    field.visible = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "visible", true);
    field.isTable = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "isTable", false);
    field.showIfEmpty = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "showIfEmpty", false);
    field.layout = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "layout", null);
    field.hint = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "hint", null);
    field.overviewMaxDisplay = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "overviewMaxDisplay", null);
    field.termsOfUse = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "termsOfUse", false);
    field.separator = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "separator", null);
    field.overview = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "overview", false);
    field.boost = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "boost", 1);
    field.order = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "order", null);
    field.facet = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "facet", null);
    field.facetOrder = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "facet_order", "bycount");
    field.aggregate = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "aggregate", null);
    field.type = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "type", null);
    field.detail_label = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "detail_label", null);
    field.facetExclusiveSelection = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "facetExclusiveSelection", false);
    field.count = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "count", false);
    field.collapsible = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "collapsible", false);
    field.ignoreForSearch = getFieldAndRemove(field, SEARCHUI_NAMESPACE + "ignoreForSearch", false);
  };

  const simplifySemantics = source => {
    if (source instanceof Object) {
      Object.keys(source).forEach(key => {
        simplifySemantics(source[key]);
      });
      if (source[SCHEMA_ORG + "identifier"]) {
        source.identifier = source[SCHEMA_ORG + "identifier"];
        delete source[SCHEMA_ORG + "identifier"];
      }
      if (source[SCHEMA_ORG + "name"]) {
        source.name = source[SCHEMA_ORG + "name"];
        delete source[SCHEMA_ORG + "name"];
      }
      if (source[SEARCHUI_NAMESPACE + "order"]) {
        source.order = source[SEARCHUI_NAMESPACE + "order"];
        delete source[SEARCHUI_NAMESPACE + "order"];
      }
      if (source[SEARCHUI_NAMESPACE + "boost"]) {
        source.boost = source[SEARCHUI_NAMESPACE + "boost"];
        delete source[SEARCHUI_NAMESPACE + "boost"];
      }
      if (source[SEARCHUI_NAMESPACE + "defaultSelection"]) {
        source.defaultSelection = source[SEARCHUI_NAMESPACE + "defaultSelection"];
        delete source[SEARCHUI_NAMESPACE + "defaultSelection"];
      }
      if (source[SEARCHUI_NAMESPACE + "icon"]) {
        source.icon = source[SEARCHUI_NAMESPACE + "icon"];
        delete source[SEARCHUI_NAMESPACE + "icon"];
      }
      if (source.fields) {
        Object.keys(source.fields).forEach(field => {
          simplifySemanticKeysForField(source.fields[field]);
        });
      }
      if (source.children) {
        Object.keys(source.children).forEach(field => {
          simplifySemanticKeysForField(source.children[field]);
        });
      }

    }
  };

  return dispatch => {
    dispatch(loadDefinitionRequest());
    axios
      .get(API.endpoints.definition())
      .then(({ data }) => {
        const definition = data && data._source;
        let serviceUrl = "";
        if (definition && definition.serviceUrl) {
          serviceUrl = definition.serviceUrl;
          delete definition.serviceUrl;
        }
        simplifySemantics(definition);
        dispatch(loadDefinitionSuccess(definition, serviceUrl));
      })
      .catch(error => {
        dispatch(loadDefinitionFailure(error));
        throw error;
      });
  };
};


export const loadGroups = groups => {
  return dispatch => {
    if (!groups.isReady && !groups.isLoading) {
      dispatch(loadGroupsRequest());
      axios
        .get(API.endpoints.groups())
        .then(response => {
          dispatch(loadGroupsSuccess(response.groups));
        })
        .catch(error => {
          dispatch(loadGroupsFailure(error));
        });
      // dispatch(loadGroupsSuccess([]));// TODO: check if we need that if(!token)
    }
  };
};

export const loadReference = (type, id) => {
  return dispatch => {
    dispatch(loadInstanceRequest());
    axios
      .get(API.endpoints.instance(type, id))
      .then(response => {
        if (response.data.found) {
          dispatch(loadInstanceSuccess(response.data));
        } else {
          dispatch(loadInstanceNoData(id));
        }
      })
      .catch(error => {
        dispatch(loadInstanceFailure(id, error));
      });
  };
};

export const loadPreview = reference => {
  return dispatch => {
    dispatch(loadInstanceRequest());
    const path = ""; //TODO: fetch from the router
    const id = ""; //TODO: fetch from the router
    axios
      .get(API.endpoints.preview(path, id))
      .then(response => {
        if (response.data && !response.data.error) {
          response.data._id = reference;
          dispatch(loadInstanceSuccess(response.data));
        } else if (response.data && response.data.error) {
          dispatch(loadInstanceFailure(reference, response.data.message ? response.data.message : response.data.error));
        } else {
          dispatch(loadInstanceNoData(reference));
        }
      })
      .catch(error => {
        if (error.stack === "SyntaxError: Unexpected end of JSON input" || error.message === "Unexpected end of JSON input") {
          dispatch(loadInstanceNoData(reference));
        } else {
          dispatch(loadInstanceFailure(reference, error));
        }
      });
  };
};