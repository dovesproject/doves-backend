package io.github.dovesproject.advicedovesbackend.ontology;

import com.google.common.base.Optional;
import io.github.dovesproject.advicedovesbackend.model.Iri;
import io.github.dovesproject.advicedovesbackend.model.Term;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-04
 */
@Component
public class OwlClassTranslator {

    private final DovesOwlOntologyLoader loader;

    public OwlClassTranslator(DovesOwlOntologyLoader loader) {
        this.loader = loader;
    }

    public Term toTerm(OWLClass cls) {
        var ontology = loader.getOntology();
        var annotationAssertions = ontology.getAnnotationAssertionAxioms(cls.getIRI());
        var label = getStringValues(annotationAssertions, ax -> ax.getProperty().isLabel())
                .findFirst()
                .orElse("").trim();
        var synonyms = getStringValues(ontology.getAnnotationAssertionAxioms(cls.getIRI()),
                                       ax -> ax.getProperty().getIRI().equals(SKOSVocabulary.ALTLABEL.getIRI()))
                .toList();
        if(synonyms.isEmpty()) {
            synonyms = getStringValues(ontology.getAnnotationAssertionAxioms(cls.getIRI()),
                                       ax -> ax.getProperty().getIRI().equals(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym")))
                    .toList();
        }

        var definition = getStringValues(ontology.getAnnotationAssertionAxioms(cls.getIRI()),
                                         ax -> ax.getProperty().getIRI().equals(SKOSVocabulary.DEFINITION.getIRI()))
                .findFirst().orElse("");
        if(definition.isEmpty()) {
            definition = getStringValues(ontology.getAnnotationAssertionAxioms(cls.getIRI()),
                                         ax -> ax.getProperty().getIRI().equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI()))
                    .findFirst().orElse("");
        }
        return new Term(label, synonyms, definition, new Iri(cls.getIRI().toString()));
    }

    private Stream<String> getStringValues(Set<OWLAnnotationAssertionAxiom> annotationAssertions,
                                           Predicate<OWLAnnotationAssertionAxiom> filter) {
        return annotationAssertions.stream()
                                   .filter(filter)
                                   .map(OWLAnnotationAssertionAxiom::getValue)
                .filter(val -> val instanceof OWLLiteral)
                                   .map(val -> (OWLLiteral) val)
                                   .map(OWLLiteral::getLiteral);
    }
}
