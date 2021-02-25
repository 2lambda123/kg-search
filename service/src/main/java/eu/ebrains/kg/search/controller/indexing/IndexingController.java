package eu.ebrains.kg.search.controller.indexing;

import eu.ebrains.kg.search.controller.Constants;
import eu.ebrains.kg.search.controller.elasticsearch.ElasticSearchController;
import eu.ebrains.kg.search.controller.mapping.MappingController;
import eu.ebrains.kg.search.controller.translators.TranslationController;
import eu.ebrains.kg.search.model.DataStage;
import eu.ebrains.kg.search.model.target.elasticsearch.TargetInstances;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class IndexingController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MappingController mappingController;
    private final ElasticSearchController elasticSearchController;
    private final TranslationController translationController;

    public IndexingController(MappingController mappingController, ElasticSearchController elasticSearchController, TranslationController translationController) {
        this.mappingController = mappingController;
        this.elasticSearchController = elasticSearchController;
        this.translationController = translationController;
    }

    public void incrementalUpdateAll(DataStage dataStage, String authorization, String legacyAuthorization){
        Constants.TARGET_MODELS_MAP.forEach((type, clazz) -> incrementalUpdateByType(dataStage, type, authorization, legacyAuthorization));
    }

    public void incrementalUpdateByType(DataStage dataStage, String type, String authorization, String legacyAuthorization) {
        TargetInstances instances = translationController.createInstances(dataStage, false, type, authorization, legacyAuthorization);
        if (!CollectionUtils.isEmpty(instances.getSearchableInstances())) {
            elasticSearchController.indexSearchDocuments(instances.getSearchableInstances(), type, dataStage);
        }
        if (!CollectionUtils.isEmpty(instances.getAllInstances())) {
            elasticSearchController.indexIdentifierDocuments(instances.getAllInstances(), dataStage);
        }
    }

    public void fullReplacementByType(DataStage dataStage, String type, String authorization, String legacyAuthorization, Class<?> clazz) {
        TargetInstances instances = translationController.createInstances(dataStage, false, type, authorization, legacyAuthorization);
        logger.info(String.format("Now indexing: %s", type));
        recreateSearchIndex(dataStage, type, clazz);
        if (!CollectionUtils.isEmpty(instances.getSearchableInstances())) {
            elasticSearchController.indexSearchDocuments(instances.getSearchableInstances(), type, dataStage);
            logger.info("Done indexing in search index");
        }
        if (!CollectionUtils.isEmpty(instances.getAllInstances())) {
            elasticSearchController.indexIdentifierDocuments(instances.getAllInstances(), dataStage);
            logger.info("Done indexing in identifiers index");
        }
    }

    public void recreateIdentifiersIndex(DataStage dataStage) {
        Map<String, Object> mapping = mappingController.generateIdentifierMapping();
        Map<String, Object> mappingResult = Map.of("mappings", mapping);
        elasticSearchController.recreateIdentifiersIndex(mappingResult, dataStage);
    }

    private void recreateSearchIndex(DataStage dataStage, String type, Class<?> clazz) {
        Map<String, Object> mapping = mappingController.generateMapping(clazz);
        Map<String, Object> mappingResult = Map.of("mappings", mapping);
        elasticSearchController.recreateSearchIndex(mappingResult, type, dataStage);
    }
}
