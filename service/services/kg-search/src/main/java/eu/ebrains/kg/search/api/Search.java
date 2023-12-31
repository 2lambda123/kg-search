/*
 *  Copyright 2018 - 2021 Swiss Federal Institute of Technology Lausanne (EPFL)
 *  Copyright 2021 - 2023 EBRAINS AISBL
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  This open source software code was developed in part or in whole in the
 *  Human Brain Project, funded from the European Union's Horizon 2020
 *  Framework Programme for Research and Innovation under
 *  Specific Grant Agreements No. 720270, No. 785907, and No. 945539
 *  (Human Brain Project SGA1, SGA2 and SGA3).
 */

package eu.ebrains.kg.search.api;

import eu.ebrains.kg.common.controller.kg.KGv3;
import eu.ebrains.kg.common.controller.translators.TranslationController;
import eu.ebrains.kg.common.model.DataStage;
import eu.ebrains.kg.common.model.TranslatorModel;
import eu.ebrains.kg.common.model.target.elasticsearch.TargetInstance;
import eu.ebrains.kg.common.services.DOICitationFormatter;
import eu.ebrains.kg.common.services.KGV3ServiceClient;
import eu.ebrains.kg.common.utils.MetaModelUtils;
import eu.ebrains.kg.common.utils.TranslationException;
import eu.ebrains.kg.search.controller.search.SearchController;
import eu.ebrains.kg.search.controller.settings.SettingsController;
import eu.ebrains.kg.search.model.FacetValue;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.security.Principal;
import java.util.*;

@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@SuppressWarnings("java:S1452") // we keep the generics intentionally
public class Search {
    private final KGV3ServiceClient kgv3ServiceClient;
    private final SettingsController definitionController;
    private final SearchController searchController;
    private final TranslationController translationController;
    private final KGv3 kgV3;
    private final DOICitationFormatter doiCitationFormatter;

    public Search(KGV3ServiceClient kgv3ServiceClient, SettingsController definitionController, SearchController searchController, TranslationController translationController, KGv3 kgV3, DOICitationFormatter doiCitationFormatter) {
        this.kgv3ServiceClient = kgv3ServiceClient;
        this.definitionController = definitionController;
        this.searchController = searchController;
        this.translationController = translationController;
        this.kgV3 = kgV3;
        this.doiCitationFormatter = doiCitationFormatter;
    }

    @GetMapping("/settings")
    public Map<String, Object> getSettings(
            @Value("${eu.ebrains.kg.commit}") String commit,
            @Value("${keycloak.realm}") String keycloakRealm,
            @Value("${keycloak.resource}") String keycloakClientId,
            @Value("${sentry.dsn.ui}") String sentryDsnUi,
            @Value("${sentry.environment}") String sentryEnvironment,
            @Value("${matomo.url}") String matomoUrl,
            @Value("${matomo.siteId}") String matomoSiteId
    ) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isNotBlank(commit) && !commit.equals("\"\"")) {
            result.put("commit", commit);

            // Only provide sentry when commit is available, ie on deployed env
            if (StringUtils.isNotBlank(sentryDsnUi)) {
                result.put("sentry", Map.of(
                        "dsn", sentryDsnUi,
                        "release", commit,
                        "environment", sentryEnvironment
                ));
            }
        }

        String authEndpoint = kgv3ServiceClient.getAuthEndpoint();
        if (StringUtils.isNotBlank(authEndpoint)) {
            result.put("keycloak", Map.of(
                    "realm", keycloakRealm,
                    "url", authEndpoint,
                    "clientId", keycloakClientId
            ));
        }
        if (StringUtils.isNotBlank(matomoUrl) && StringUtils.isNotBlank(matomoSiteId)) {
            result.put("matomo", Map.of(
                    "url", matomoUrl,
                    "siteId", matomoSiteId
            ));
        }
        result.put("types", definitionController.generateTypes());
        result.put("typeMappings", definitionController.generateTypeMappings());
        return result;
    }

    @GetMapping("/citation")
    public ResponseEntity<String> getCitation(@RequestParam("doi") String doiWithoutPrefix, @RequestParam("style") String style, @RequestParam("contentType") String contentType) {
        String doi = String.format("https://doi.org/%s", doiWithoutPrefix);
        String citation = doiCitationFormatter.getDOICitation(doi, style, contentType);
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(citation);
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getGroups(Principal principal) {
        if (searchController.isInInProgressRole(principal)) {
            return ResponseEntity.ok(Arrays.asList(
                    Map.of("value", "curated",
                            "label", "in progress"),
                    Map.of("value", "public",
                            "label", "publicly released")
            ));
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }


    @SuppressWarnings("java:S3740") // we keep the generics intentionally
    @GetMapping("/{id}/live")
    public ResponseEntity<Map> translate(@PathVariable("id") String id, @RequestParam(required = false, defaultValue = "false") boolean skipReferenceCheck) throws TranslationException {
        try {
            final List<String> typesOfInstance = kgV3.getTypesOfInstance(id, DataStage.IN_PROGRESS, false);
            final TranslatorModel<?, ?> translatorModel = TranslatorModel.MODELS.stream().filter(m -> m.getV3translator() != null && m.getV3translator().semanticTypes().stream().anyMatch(typesOfInstance::contains)).findFirst().orElse(null);
            if (translatorModel != null) {
                final String queryId = typesOfInstance.stream().map(type -> translatorModel.getV3translator().getQueryIdByType(type)).findFirst().orElse(null);
                if (queryId != null) {
                    final TargetInstance v = translationController.translateToTargetInstanceForLiveMode(kgV3, translatorModel.getV3translator(), queryId, DataStage.IN_PROGRESS, id, false, !skipReferenceCheck);
                    if (v != null) {
                        return ResponseEntity.ok(searchController.getLiveDocument(v));
                    }
                }
            }
            return ResponseEntity.notFound().build();
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/groups/public/documents/{id}")
    public ResponseEntity<?> getDocumentForPublic(@PathVariable("id") String id) {
        try {
            Map<String, Object> res = searchController.getSearchDocument(DataStage.RELEASED, id);
            if (res != null) {
                return ResponseEntity.ok(res);
            }
            return ResponseEntity.notFound().build();
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }


    @GetMapping("/repositories/{id}/files/live")
    public ResponseEntity<?> getFilesFromRepoForLive(@PathVariable("id") String repositoryId,
                                                     @RequestParam(required = false, defaultValue = "", name = "format") String format,
                                                     @RequestParam(required = false, defaultValue = "", name = "groupingType") String groupingType,
                                                     Principal principal) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if(repositoryUUID == null){
            return ResponseEntity.badRequest().build();
        }
        if (searchController.canReadLiveFiles(principal, repositoryUUID)) {
            try {
                return searchController.getFilesFromRepo(DataStage.IN_PROGRESS, repositoryUUID, format, groupingType);
            } catch (WebClientResponseException e) {
                return ResponseEntity.status(e.getStatusCode()).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/repositories/{id}/files/formats/live")
    public ResponseEntity<?> getFileFormatsFromRepoForLive(@PathVariable("id") String repositoryId,
                                                           Principal principal) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if (repositoryUUID == null) {
            return ResponseEntity.badRequest().build();
        }
        if (searchController.canReadLiveFiles(principal, repositoryUUID)) {
            return searchController.getFileFormatsFromRepo(DataStage.IN_PROGRESS, repositoryUUID);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/repositories/{id}/files/groupingTypes/live")
    public ResponseEntity<?> getGroupingTypesFromRepoForLive(@PathVariable("id") String repositoryId,
                                                             Principal principal) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if (repositoryUUID == null) {
            return ResponseEntity.badRequest().build();
        }
        if (searchController.canReadLiveFiles(principal, repositoryUUID)) {
            //kgV3.executeQueryForInstance("clazz", DataStage.IN_PROGRESS, "queryId", repositoryId, false)
            return searchController.getGroupingTypesFromRepo(DataStage.IN_PROGRESS, repositoryUUID);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/groups/public/repositories/{id}/files/formats")
    public ResponseEntity<?> getFileFormatsFromRepoForPublic(@PathVariable("id") String repositoryId) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if (repositoryUUID == null) {
            return ResponseEntity.badRequest().build();
        }
        return searchController.getFileFormatsFromRepo(DataStage.RELEASED, repositoryUUID);
    }

    @GetMapping("/groups/curated/repositories/{id}/files/formats")
    public ResponseEntity<?> getFileFormatsFromRepoForCurated(@PathVariable("id") String repositoryId,
                                                              Principal principal) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if (repositoryUUID == null) {
            return ResponseEntity.badRequest().build();
        }
        if (searchController.isInInProgressRole(principal)) {
            return searchController.getFileFormatsFromRepo(DataStage.IN_PROGRESS, repositoryUUID);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/groups/public/repositories/{id}/files/groupingTypes")
    public ResponseEntity<?> getGroupingTypesFromRepoForPublic(@PathVariable("id") String repositoryId) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if (repositoryUUID == null) {
            return ResponseEntity.badRequest().build();
        }
        return searchController.getGroupingTypesFromRepo(DataStage.RELEASED, repositoryUUID);
    }

    @GetMapping("/groups/curated/repositories/{id}/files/groupingTypes")
    public ResponseEntity<?> getGroupingTypesFromRepoForCurated(@PathVariable("id") String repositoryId,
                                                                Principal principal) {
        final UUID repositoryUUID = MetaModelUtils.castToUUID(repositoryId);
        if (repositoryUUID == null) {
            return ResponseEntity.badRequest().build();
        }
        if (searchController.isInInProgressRole(principal)) {
            return searchController.getGroupingTypesFromRepo(DataStage.IN_PROGRESS, repositoryUUID);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/groups/public/repositories/{id}/files")
    public ResponseEntity<?> getFilesFromRepoForPublic(@PathVariable("id") String id,
                                                       @RequestParam(required = false, defaultValue = "", name = "format") String format,
                                                       @RequestParam(required = false, defaultValue = "", name = "groupingType") String groupingType) {
        final UUID uuid = MetaModelUtils.castToUUID(id);
        if (uuid == null) {
            return ResponseEntity.badRequest().build();
        }
        return searchController.getFilesFromRepo(DataStage.RELEASED, uuid, format, groupingType);
    }

    @GetMapping("/groups/curated/repositories/{id}/files")
    public ResponseEntity<?> getFilesFromRepoForCurated(@PathVariable("id") String id,
                                                        @RequestParam(required = false, defaultValue = "", name = "format") String format,
                                                        @RequestParam(required = false, defaultValue = "", name = "groupingType") String groupingType,
                                                        Principal principal) {
        final UUID uuid = MetaModelUtils.castToUUID(id);
        if (uuid == null) {
            return ResponseEntity.badRequest().build();
        }
        if (searchController.isInInProgressRole(principal)) {
            return searchController.getFilesFromRepo(DataStage.IN_PROGRESS, uuid, format, groupingType);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/groups/public/documents/{type}/{id}")
    public ResponseEntity<?> getDocumentForPublic(@PathVariable("type") String type, @PathVariable("id") String id) {
        try {
            Map<String, Object> res = searchController.getSearchDocument(DataStage.RELEASED, String.format("%s/%s", type, id));
            if (res != null) {
                return ResponseEntity.ok(res);
            }
            return ResponseEntity.notFound().build();
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/groups/curated/documents/{id}")
    public ResponseEntity<?> getDocumentForCurated(@PathVariable("id") String id, Principal principal) {
        if (searchController.isInInProgressRole(principal)) {
            try {
                Map<String, Object> res = searchController.getSearchDocument(DataStage.IN_PROGRESS, id);
                if (res != null) {
                    return ResponseEntity.ok(res);
                }
                return ResponseEntity.notFound().build();
            } catch (WebClientResponseException e) {
                return ResponseEntity.status(e.getStatusCode()).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/groups/curated/documents/{type}/{id}")
    public ResponseEntity<?> getDocumentForCurated(@PathVariable("type") String type, @PathVariable("id") String id, Principal principal) {
        if (searchController.isInInProgressRole(principal)) {
            try {
                Map<String, Object> res = searchController.getSearchDocument(DataStage.IN_PROGRESS, String.format("%s/%s", type, id));
                if (res != null) {
                    return ResponseEntity.ok(res);
                }
                return ResponseEntity.notFound().build();
            } catch (WebClientResponseException e) {
                return ResponseEntity.status(e.getStatusCode()).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/groups/public/search")
    public ResponseEntity<?> searchPublic(
            @RequestParam(required = false, defaultValue = "", name = "q") String q,
            @RequestParam(required = false, defaultValue = "", name = "type") String type,
            @RequestParam(required = false, defaultValue = "0", name = "from") int from,
            @RequestParam(required = false, defaultValue = "20", name = "size") int size,
            @RequestBody Map<String, FacetValue> facetValues,
            Principal principal
    ) {
        try {
            Map<String, Object> result = searchController.search(q, type, from, size, facetValues, DataStage.RELEASED,  (KeycloakAuthenticationToken) principal);
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PostMapping("/groups/curated/search")
    public ResponseEntity<?> searchCurated(
            @RequestParam(required = false, defaultValue = "", name = "q") String q,
            @RequestParam(required = false, defaultValue = "", name = "type") String type,
            @RequestParam(required = false, defaultValue = "0", name = "from") int from,
            @RequestParam(required = false, defaultValue = "20", name = "size") int size,
            @RequestBody Map<String, FacetValue> facetValues,
            Principal principal) {
        if (searchController.isInInProgressRole(principal)) {
            try {
                Map<String, Object> result = searchController.search(q, type, from, size, facetValues, DataStage.IN_PROGRESS, (KeycloakAuthenticationToken) principal);
                return ResponseEntity.ok(result);
            } catch (WebClientResponseException e) {
                return ResponseEntity.status(e.getStatusCode()).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}/bookmark")
    public ResponseEntity<?> userBookmarkedInstance(@PathVariable("id") String id, Principal principal) {
        KeycloakAuthenticationToken authenticationToken = (KeycloakAuthenticationToken) principal;
        if(authenticationToken==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        KeycloakSecurityContext keycloakSecurityContext = authenticationToken.getAccount().getKeycloakSecurityContext();
        if (keycloakSecurityContext.getToken().isExpired()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UUID uuid = MetaModelUtils.castToUUID(id);
        if (uuid == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Map<String, Object> result = Map.of(
                    "id", id,
                    "bookmarked", searchController.isBookmarked(uuid)
            );
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<?> userDeleteBookmarkOfInstance(@PathVariable("id") String id, Principal principal) {
        KeycloakAuthenticationToken authenticationToken = (KeycloakAuthenticationToken) principal;
        if(authenticationToken==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        KeycloakSecurityContext keycloakSecurityContext = authenticationToken.getAccount().getKeycloakSecurityContext();
        if (keycloakSecurityContext.getToken().isExpired()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UUID uuid = MetaModelUtils.castToUUID(id);
        if (uuid == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            searchController.deleteBookmark(uuid);
            Map<String, Object> result = Map.of(
                    "id", id,
                    "bookmarked", false
            );
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PutMapping("/{id}/bookmark")
    public ResponseEntity<?> userBookmarkInstance(@PathVariable("id") String id, Principal principal) {
        KeycloakAuthenticationToken authenticationToken = (KeycloakAuthenticationToken) principal;
        if(authenticationToken==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        KeycloakSecurityContext keycloakSecurityContext = authenticationToken.getAccount().getKeycloakSecurityContext();
        if (keycloakSecurityContext.getToken().isExpired()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UUID uuid = MetaModelUtils.castToUUID(id);
        if (uuid == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            searchController.addBookmark(MetaModelUtils.castToUUID(id));
            Map<String, Object> result = Map.of(
                    "id", id,
                    "bookmarked", true
            );
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}
