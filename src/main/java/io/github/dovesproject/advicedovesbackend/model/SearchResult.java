package io.github.dovesproject.advicedovesbackend.model;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-07
 */
public record SearchResult(SearchSpecification searchSpecification,
                           List<DigitalHealthAppDocument> matches) {

}
