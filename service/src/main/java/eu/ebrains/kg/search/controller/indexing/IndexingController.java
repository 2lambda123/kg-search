package eu.ebrains.kg.search.controller.indexing;

import eu.ebrains.kg.search.controller.Constants;
import eu.ebrains.kg.search.controller.elasticsearch.ElasticSearchController;
import eu.ebrains.kg.search.controller.mapping.MappingController;
import eu.ebrains.kg.search.controller.sitemap.SitemapController;
import eu.ebrains.kg.search.controller.translators.TranslationController;
import eu.ebrains.kg.search.model.DatabaseScope;
import eu.ebrains.kg.search.model.target.elasticsearch.TargetInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class IndexingController {

    private final MappingController mappingController;
    private final ElasticSearchController elasticSearchController;
    private final TranslationController translationController;

    public IndexingController(MappingController mappingController, ElasticSearchController elasticSearchController, TranslationController translationController) {
        this.mappingController = mappingController;
        this.elasticSearchController = elasticSearchController;
        this.translationController = translationController;
    }

    public void incrementalUpdateAll(DatabaseScope databaseScope, String authorization){
        Constants.TARGET_MODELS_MAP.forEach((type, clazz) -> incrementalUpdateByType(databaseScope, type, authorization));
    }

    public void incrementalUpdateByType(DatabaseScope databaseScope, String type, String authorization) {
        List<TargetInstance> instances = translationController.createInstances(databaseScope, false, type, authorization);
        elasticSearchController.updateIndex(instances, type, databaseScope);
    }

    public void fullReplacementByType(DatabaseScope databaseScope, String type, String authorization, Class<?> clazz) {
        List<TargetInstance> instances = translationController.createInstances(databaseScope, false, type, authorization);
        recreateIndex(databaseScope, type, clazz);
        elasticSearchController.indexDocuments(instances, type, databaseScope);
    }

    private void recreateIndex(DatabaseScope databaseScope, String type, Class<?> clazz) {
        Map<String, Object> mapping = mappingController.generateMapping(clazz);
        Map<String, Object> mappingResult = Map.of("mappings", mapping);
        elasticSearchController.recreateIndex(mappingResult, type, databaseScope);
    }
}
