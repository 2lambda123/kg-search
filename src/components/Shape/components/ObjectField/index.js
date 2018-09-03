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
import { Field } from "../Field";
import "./styles.css";

const DefaultList = ({className, children}) => {
  return (
    <ul className={className}>
      {children}
    </ul>
  );
};

const CustomList = ({className, children}) => (
  <span className={className}>
    {children}
  </span>
);

const DefaultListItem = ({children}) => (
  <li>
    {children}
  </li>
);

const CustomListItem = ({index, separator, children}) => (
  <span>
    {index===0?null:separator}
    {children}
  </span>
);

const ObjectFieldComponent = ({fields, separator}) => {
  const List = separator?CustomList:DefaultList;
  const ListItem = separator?CustomListItem:DefaultListItem;
  return (
    <List className="kgs-shape__object">
      {
        fields.map(({name, value, mapping, showSmartContent}, index) => (
          <ListItem key={name} separator={separator} index={index}>
            <Field name={name} value={value} mapping={mapping} showSmartContent={showSmartContent} />
          </ListItem>
        ))
      }
    </List>
  );
};

export function ObjectField({show, data, mapping, showSmartContent}) {
  if (!show || !mapping || !mapping.visible) {
    return null;
  }

  const fields = Object.entries(mapping.children)
    .filter(([name, mapping]) =>
      mapping
            && (mapping.showIfEmpty || (data && data[name]))
            && mapping.visible
    )
    .map(([name, mapping]) => ({
      name: name,
      value: data && data[name],
      mapping: mapping,
      showSmartContent: showSmartContent
    }));

  return (
    <ObjectFieldComponent show={show} fields={fields} separator={mapping.separator} />
  );
}
