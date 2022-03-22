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

package eu.ebrains.kg.search.model.source.openMINDSv3;

import eu.ebrains.kg.search.model.source.openMINDSv3.commons.ExtendedFullNameRefForResearchProductVersion;
import eu.ebrains.kg.search.model.source.openMINDSv3.commons.FullNameRef;
import eu.ebrains.kg.search.model.source.openMINDSv3.commons.FullNameRefForResearchProductVersion;
import eu.ebrains.kg.search.model.source.openMINDSv3.commons.RelatedPublication;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectV3 extends SourceInstanceV3 {
    private String title;
    private String description;
    private List<RelatedPublication> publications;
    private List<ExtendedFullNameRefForResearchProductVersion> datasets;
    private List<ExtendedFullNameRefForResearchProductVersion> models;
    private List<ExtendedFullNameRefForResearchProductVersion> software;
    private List<ExtendedFullNameRefForResearchProductVersion> metaDataModels;

    @Getter
    @Setter
    public static class Publication {
        private String doi;
        private String howToCite;
    }
}
