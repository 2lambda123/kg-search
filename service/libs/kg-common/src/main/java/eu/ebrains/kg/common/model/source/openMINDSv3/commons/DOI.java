package eu.ebrains.kg.common.model.source.openMINDSv3.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DOI {
    private ExtendedFullNameRefForResearchProductVersion researchProduct;
    private String identifier;
}