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

package eu.ebrains.kg.search.controller.translators.kgv3;

import eu.ebrains.kg.search.controller.translators.Translator;
import eu.ebrains.kg.search.model.source.ResultsOfKG;
import eu.ebrains.kg.search.model.source.openMINDSv3.commons.FullNameRef;
import eu.ebrains.kg.search.model.source.openMINDSv3.commons.FullNameRefForResearchProductVersion;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.Children;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.TargetExternalReference;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.TargetInternalReference;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.Value;
import eu.ebrains.kg.search.utils.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class TranslatorV3<Source, Target, Result extends ResultsOfKG<Source>> extends Translator<Source, Target, Result> {
    public abstract List<String> semanticTypes();

    public String getQueryIdByType(String type){
        if(type!=null && semanticTypes().contains(type)){
            if(getQueryIds().size() == 1){
                return getQueryIds().get(0);
            }
            else{
                throw new RuntimeException("There are ambiguous query ids for this translator -> please override the getQueryIdByType method");
            }
        }
        return type;
    }

}