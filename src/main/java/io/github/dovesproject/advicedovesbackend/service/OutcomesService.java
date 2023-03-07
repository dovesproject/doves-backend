package io.github.dovesproject.advicedovesbackend.service;

import io.github.dovesproject.advicedovesbackend.model.Term;
import io.github.dovesproject.advicedovesbackend.ontology.DovesOntologyReasoner;
import io.github.dovesproject.advicedovesbackend.ontology.OwlClassTranslator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-04
 */
@Component
public class OutcomesService {

    private final DovesOntologyReasoner reasoner;

    private final OwlClassTranslator translator;


    public OutcomesService(DovesOntologyReasoner reasoner, OwlClassTranslator translator) {
        this.reasoner = reasoner;
        this.translator = translator;
    }

    public List<Term> getOutcomes(String query) {
        var outcomes = reasoner.getOutcomes();
        return outcomes.stream()
                .map(translator::toTerm)
                .filter(t -> t.label().endsWith("Outcome"))
                .filter(new TermFilter(query))
                .map(t -> new Term(t.label().substring(0, t.label().length() - "Outcome".length()).trim(),
                                   t.synonyms(), t.definition() , t.iri()))
                .sorted()
                .toList();
    }

}
