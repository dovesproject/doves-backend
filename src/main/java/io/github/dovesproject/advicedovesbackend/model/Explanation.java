package io.github.dovesproject.advicedovesbackend.model;

import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-09
 */
public record Explanation(String entailment,
                          List<String> axioms) {

}
