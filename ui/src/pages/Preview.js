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

import React, { useEffect, useRef } from "react";
import { useParams } from "react-router-dom";

import { setCustomUrl, trackPageView } from "../app/services/api";
import InstanceContainer from "./Instance/InstanceContainer";

const Preview = () => {

  const initializedRef = useRef(false);

  const {org, domain, schema, version, id} = useParams();

  const instanceId = (org && domain && schema && version && id)?`${org}/${domain}/${schema}/${version}/${id}`:id;

  useEffect(() => {
    if (!initializedRef.current) {
      initializedRef.current = true;
      setCustomUrl(window.location.href);
      trackPageView();
    }
  }, []);

  return (
    <InstanceContainer
      instanceId={instanceId}
      path="/live/"
      isPreview={true}
      watermark="Preview"
      warning="This is a preview of how this card will look like, once it's published. Please note, that some elements (like links to other resources such as term definitions, etc.) depend on the publication state of the related instances and might not be available/active in published mode."
    />
  );
};

export default Preview;