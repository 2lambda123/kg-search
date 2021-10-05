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

import eu.ebrains.kg.search.model.DataStage;
import eu.ebrains.kg.search.model.source.ResultsOfKGv3;
import eu.ebrains.kg.search.model.source.openMINDSv3.ControlledTermV3;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.ControlledTerm;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.TargetExternalReference;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.Value;
import eu.ebrains.kg.search.utils.IdUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ControlledTermV3Translator extends TranslatorV3<ControlledTermV3, ControlledTerm, ControlledTermV3Translator.Result> {
    private static final String CONTROLLED_TERM_QUERY_ID = "42405db5-6f86-4dea-baba-95a94105e74e";

    private static final List<String> CONTROLLED_TERM_TYPES = Stream.of(
            "ActionStatusType",
            "AgeCategory",
            "AnatomicalAxesOrientation",
            "BiologicalOrder",
            "BiologicalSex",
            "CellType",
            "ContributionType",
            "CriteriaQualityType",
            "DataType",
            "DeviceType",
            "Disease",
            "DiseaseModel",
            "EthicsAssessment",
            "ExperimentalApproach",
            "FileBundleGrouping",
            "FileRepositoryType",
            "FileUsageRole",
            "Handedness",
            "Language",
            "Laterality",
            "MetaDataModelType",
            "ModelAbstractionLevel",
            "ModelScope",
            "OperatingDevice",
            "OperatingSystem",
            "Organ",
            "Phenotype",
            "PreparationType",
            "ProductAccessibility",
            "ProgrammingLanguage",
            "QualitativeOverlap",
            "SemanticDataType",
            "Service",
            "SoftwareApplicationCategory",
            "SoftwareFeature",
            "Species",
            "StimulationApproach",
            "StimulusType",
            "Strain",
            "Technique",
            "TermSuggestion",
            "Terminology",
            "TissueSampleType",
            "TypeOfUncertainty",
            "UBERONParcellation",
            "UnitOfMeasurement").map(s -> String.format("%s/%s", "https://openminds.ebrains.eu/controlledTerms", s)).collect(Collectors.toList());

    @Override
    public List<String> semanticTypes() {
        return CONTROLLED_TERM_TYPES;
    }

    @Override
    public String getQueryIdByType(String type) {
        if(CONTROLLED_TERM_TYPES.contains(type)){
            return IdUtils.getTemplateQueryId(CONTROLLED_TERM_QUERY_ID, type);
        }
        return null;
    }

    private static final List<String> queryIds = CONTROLLED_TERM_TYPES.stream().map(t -> IdUtils.getTemplateQueryId(CONTROLLED_TERM_QUERY_ID, t)).collect(Collectors.toList());

    public static class Result extends ResultsOfKGv3<ControlledTermV3> {
    }

    @Override
    public Class<Result> getResultType() {
        return Result.class;
    }

    @Override
    public Class<ControlledTermV3> getSourceType() {
        return ControlledTermV3.class;
    }

    @Override
    public Class<ControlledTerm> getTargetType() {
        return ControlledTerm.class;
    }

    @Override
    public List<String> getQueryIds() {
        return queryIds;
    }

    public ControlledTerm translate(ControlledTermV3 controlledTerm, DataStage dataStage, boolean liveMode) {
        ControlledTerm t = new ControlledTerm();
        t.setId(IdUtils.getUUID(controlledTerm.getId()));
        t.setIdentifier(IdUtils.getUUID(controlledTerm.getIdentifier()));
        t.setTitle(StringUtils.isBlank(controlledTerm.getName()) ? null : new Value<>(controlledTerm.getName()));
        t.setDescription(StringUtils.isBlank(controlledTerm.getDescription()) ? null : new Value<>(controlledTerm.getDescription()));
        t.setDefinition(StringUtils.isBlank(controlledTerm.getDefinition()) ? null : new Value<>(controlledTerm.getDefinition()));
        if(controlledTerm.getSynonym()!=null){
            t.setSynonyms(controlledTerm.getSynonym().stream().filter(StringUtils::isNotBlank).map(Value::new).collect(Collectors.toList()));
        }
        List<TargetExternalReference> externalDefinitions = new ArrayList<>();
        if(controlledTerm.getInterlexIdentifier()!=null){
            externalDefinitions.add(new TargetExternalReference(controlledTerm.getInterlexIdentifier(), "Interlex"));
        }
        if(controlledTerm.getKnowledgeSpaceLink()!=null){
            externalDefinitions.add(new TargetExternalReference(controlledTerm.getKnowledgeSpaceLink(), "Knowledge Space"));
        }
        if(!externalDefinitions.isEmpty()){
            t.setExternalDefinitions(externalDefinitions);
        }
        t.setOntologyIdentifier(StringUtils.isBlank(controlledTerm.getPreferredOntologyIdentifier()) ? null : new Value<>(controlledTerm.getPreferredOntologyIdentifier()));
        return t;
    }
}
