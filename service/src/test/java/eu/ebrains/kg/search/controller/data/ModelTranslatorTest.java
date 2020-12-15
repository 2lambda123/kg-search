package eu.ebrains.kg.search.controller.data;

import eu.ebrains.kg.search.controller.utils.TranslatorTestHelper;
import eu.ebrains.kg.search.model.DatabaseScope;
import eu.ebrains.kg.search.model.source.ResultOfKGv2;
import eu.ebrains.kg.search.model.source.openMINDSv2.ModelV2;
import eu.ebrains.kg.search.model.target.elasticsearch.ElasticSearchDocument;
import eu.ebrains.kg.search.services.KGServiceClient;
import eu.ebrains.kg.search.services.LegacySearchServiceClient;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelTranslatorTest {
    private final KGServiceClient kgServiceClient;

    public ModelTranslatorTest(KGServiceClient kgServiceClient) {
        this.kgServiceClient = kgServiceClient;
    }

    @Value("${test.token}")
    String token;

    private static class ModelV2Result extends ResultOfKGv2<ModelV2> { }

    @Test
    public void compareReleasedModels() {
        compareModels(DatabaseScope.RELEASED, false);
    }

    @Test
    public void compareInferredModels() {
        compareModels(DatabaseScope.INFERRED, false);
    }

    @Test
    public void compareInferredLiveModels() {
        compareModels(DatabaseScope.INFERRED, true);
    }

    private void compareModels(DatabaseScope databaseScope, boolean liveMode) {
        List<String> result = new ArrayList<>();
        ModelV2Result queryResult = kgServiceClient.executeQuery("query/uniminds/core/modelinstance/v1.0.0/search", databaseScope, ModelV2Result.class, token);
        queryResult.getResults().forEach(project -> {
            String id = liveMode?project.getEditorId():project.getIdentifier();
            ElasticSearchDocument doc;
            if (liveMode) {
                doc = LegacySearchServiceClient.getLiveDocument(id, ElasticSearchDocument.class);
            } else {
                doc = LegacySearchServiceClient.getDocument(databaseScope, "Model", id, ElasticSearchDocument.class);
            }
            if (doc == null) {
                result.add("\n\n\tModel: " + project.getIdentifier() + " (Fail to get expected document!)");
            } else {
                Map<String, Object> expected = doc.getSource();
                List<String> messages = TranslatorTestHelper.compareModel(project, expected, databaseScope, liveMode);
                if (!messages.isEmpty()) {
                    result.add("\n\n\tModel: " + project.getIdentifier() + "\n\t\t" + String.join("\n\t\t", messages));
                }
            }
        });
        if (!result.isEmpty()) {
            Assert.fail(String.join("", result));
        }
    }

    @Test
    public void compareReleasedModel() throws IOException {
        String sourceJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/modelReleasedSource.json"), StandardCharsets.UTF_8);
        String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/modelReleasedTarget.json"), StandardCharsets.UTF_8);
        List<String> result = TranslatorTestHelper.compareModel(sourceJson, expectedJson, DatabaseScope.RELEASED, false);
        if (!result.isEmpty()) {
            Assert.fail("\n\t" + String.join("\n\t", result));
        }
    }

    @Test
    public void compareInferredSoftware() throws IOException {
        String sourceJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/modelInferredSource.json"), StandardCharsets.UTF_8);
        String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/modelInferredTarget.json"), StandardCharsets.UTF_8);
        List<String> result = TranslatorTestHelper.compareModel(sourceJson, expectedJson, DatabaseScope.INFERRED, false);
        if (!result.isEmpty()) {
            Assert.fail("\n\t" + String.join("\n\t", result));
        }

    }

    @Test
    public void compareInferredLiveSoftware() throws IOException {
        String sourceJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/modelInferredSource.json"), StandardCharsets.UTF_8);
        String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/modelInferredLiveTarget.json"), StandardCharsets.UTF_8);
        List<String> result = TranslatorTestHelper.compareModel(sourceJson, expectedJson, DatabaseScope.INFERRED, true);
        if (!result.isEmpty()) {
            Assert.fail("\n\t" + String.join("\n\t", result));
        }
    }
}