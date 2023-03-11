package io.github.dovesproject.advicedovesbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-07
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchSpecification(List<Term> outcomes,
                                  List<Term> conditions,
                                  CheckableHierarchyNode ageGroupNode,
                                  CheckableHierarchyNode assignedSexNode,
                                  @JsonProperty("appUserNode") CheckableHierarchyNode appUserNode) {

}
