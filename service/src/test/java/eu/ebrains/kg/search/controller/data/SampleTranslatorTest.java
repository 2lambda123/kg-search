package eu.ebrains.kg.search.controller.data;

import eu.ebrains.kg.search.controller.utils.TranslatorTestHelper;
import eu.ebrains.kg.search.model.DataStage;
import eu.ebrains.kg.search.model.source.ResultsOfKGv2;
import eu.ebrains.kg.search.model.source.openMINDSv1.SampleV1;
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

public class SampleTranslatorTest {
    private final KGServiceClient kgServiceClient;

    public SampleTranslatorTest(KGServiceClient kgServiceClient) {
        this.kgServiceClient = kgServiceClient;
    }

    private static class SampleV1Results extends ResultsOfKGv2<SampleV1> {}

    @Value("${test.token}")
    String token;

    @Test
    public void compareReleasedSamples() { compareSamples(DataStage.RELEASED, false); }

    @Test
    public void compareInferredSamples() {
        compareSamples(DataStage.IN_PROGRESS, false);
    }

    @Test
    public void compareInferredLiveSamples() {
        compareSamples(DataStage.IN_PROGRESS, true);
    }

    private void compareSamples(DataStage dataStage, boolean liveMode) {
        List<String> result = new ArrayList<>();
        SampleV1Results queryResult = kgServiceClient.executeQueryForIndexing("query/minds/experiment/sample/v1.0.0/search", dataStage, SampleV1Results.class, token);
        queryResult.getResults().forEach(sample -> {
            String id = liveMode?sample.getEditorId():sample.getIdentifier();
            ElasticSearchDocument doc;
            if (liveMode) {
                doc = LegacySearchServiceClient.getLiveDocument(id, ElasticSearchDocument.class);
            } else {
                doc = LegacySearchServiceClient.getDocument(dataStage, "Sample", id, ElasticSearchDocument.class);
            }
            if (doc == null) {
                result.add("\n\n\tSample: " + sample.getIdentifier() + " (Fail to get expected document!)");
            } else {
                Map<String, Object> expected = doc.getSource();
                List<String> messages = TranslatorTestHelper.compareSample(sample, expected, dataStage, liveMode);
                if (!messages.isEmpty()) {
                    result.add("\n\n\tSample: " + sample.getIdentifier() + "\n\t\t" + String.join("\n\t\t", messages));
                }
            }
        });
        if (!result.isEmpty()) {
            Assert.fail(String.join("", result));
        }
    }

    @Test
    public void compareReleasedSample() throws IOException {
        String sourceJson = IOUtils.toString(this.getClass().getResourceAsStream("/v1/sampleReleasedSource.json"), StandardCharsets.UTF_8);
        String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/sampleReleasedTarget.json"), StandardCharsets.UTF_8);
        List<String> result = TranslatorTestHelper.compareSample(sourceJson, expectedJson, DataStage.RELEASED, false);
        if (!result.isEmpty()) {
            Assert.fail("\n\t" + String.join("\n\t", result));
        }
    }

    @Test
    public void compareInferredSample() throws IOException {
        String sourceJson = IOUtils.toString(this.getClass().getResourceAsStream("/v1/sampleInferredSource.json"), StandardCharsets.UTF_8);
        String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/sampleInferredTarget.json"), StandardCharsets.UTF_8);
        List<String> result = TranslatorTestHelper.compareSample(sourceJson, expectedJson, DataStage.IN_PROGRESS, false);
        if (!result.isEmpty()) {
            Assert.fail("\n\t" + String.join("\n\t", result));
        }
    }

    @Test
    public void compareInferredLiveSample() throws IOException {
        String sourceJson = IOUtils.toString(this.getClass().getResourceAsStream("/v1/sampleInferredSource.json"), StandardCharsets.UTF_8);
        String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream("/v2/sampleInferredLiveTarget.json"), StandardCharsets.UTF_8);
        List<String> result = TranslatorTestHelper.compareSample(sourceJson, expectedJson, DataStage.IN_PROGRESS, true);
        if (!result.isEmpty()) {
            Assert.fail("\n\t" + String.join("\n\t", result));
        }
    }
}
