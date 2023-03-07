package io.github.dovesproject.advicedovesbackend.search;

import io.github.dovesproject.advicedovesbackend.ontology.DovesOntologyReasoner;
import io.github.dovesproject.advicedovesbackend.ontology.OwlClassTranslator;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-05
 */
@Component
public class TermsIndexLoader {

    private static final Logger logger = LoggerFactory.getLogger(TermsIndexLoader.class);

    private final DovesOntologyReasoner reasoner;

    private final OwlClassTranslator translator;

    private final TermsRepository repository;

    public TermsIndexLoader(DovesOntologyReasoner reasoner,
                            OwlClassTranslator translator,
                            TermsRepository repository) {
        this.reasoner = reasoner;
        this.translator = translator;
        this.repository = repository;
    }

    public void loadTerms() {
        repository.deleteAll();
        add(reasoner.getDiseases(), TermCategory.CONDITION);
        add(reasoner.getOutcomes(), TermCategory.OUTCOME);
        add(reasoner.getTechnologyTypes(), TermCategory.TECHNOLOGY_TYPE);
        add(reasoner.getSpecialties(), TermCategory.SPECIALTY);
        add(reasoner.getTechnicalFeatures(), TermCategory.TECHNICAL_FEATURE);
    }



    private void add(Collection<OWLClass> clses, TermCategory category) {
        var docs = clses
                           .stream()
                           .map(cls -> toTermDocument(cls, category))
                           .toList();
        repository.saveAll(docs);
        logger.info("Loaded {} ({} classes)", category.name(), clses.size());
    }

    private TermDocument toTermDocument(OWLClass cls,
                                               TermCategory category) {
        var term = translator.toTerm(cls);
        return new TermDocument(category, term.iri().lexicalValue(), term.label(), term.synonyms());
    }
}
