package io.github.dovesproject.advicedovesbackend.db;

import io.github.dovesproject.advicedovesbackend.model.DigitalHealthAppDocument;
import io.github.dovesproject.advicedovesbackend.model.Iri;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-07
 */
@Component
public class DigitalHealthAppAxiomsGenerator {

    private static String iriPrefix = "https://purl.org/doves/doves-ontology/";

    private final OWLDataFactory dataFactory;

    public DigitalHealthAppAxiomsGenerator(OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    public Collection<OWLAxiom> generateAxioms(DigitalHealthAppDocument document) {
        var applicationClass = dataFactory.getOWLClass(IRI.create(iriPrefix + "DigitalHealthApplication"));
        var appClass = Class(IRI.create(iriPrefix + document.id()));

        var axioms = new ArrayList<OWLAxiom>();

        var positioningAxiom = SubClassOf(appClass, applicationClass);
        var labelAxiom = AnnotationAssertion(dataFactory.getRDFSLabel(),
                            appClass.getIRI(),
                            Literal(Optional.ofNullable(document.applicationName()).orElse("")));
        Optional.ofNullable(document.applicationDescription())
                .ifPresent(description -> {
                    var descriptionAxiom = AnnotationAssertion(dataFactory.getRDFSComment(),
                                                               appClass.getIRI(),
                                                               Literal(description));
                    axioms.add(descriptionAxiom);
                });
        Optional.ofNullable(document.url())
                .ifPresent(url -> {
                    var urlAxiom = AnnotationAssertion(dataFactory.getRDFSSeeAlso(),
                                                       appClass.getIRI(),
                                                       IRI.create(url));
                    axioms.add(urlAxiom);
                });
        var outcomeAxioms = document.outcomes()
                .stream()
                .map(outcomeIri -> relationship(appClass, "hasOutcome", outcomeIri))
                .toList();
        var conditionAxioms = document.conditions()
                .stream()
                .map(conditionIri -> relationship(appClass, "hasCondition", conditionIri))
                .toList();
        var userAxioms = document.users()
                .stream()
                .map(userIri -> relationship(appClass, "hasUserWithRole", userIri))
                .toList();



        axioms.add(positioningAxiom);
        axioms.add(labelAxiom);
        axioms.addAll(outcomeAxioms);
        axioms.addAll(conditionAxioms);
        axioms.addAll(userAxioms);

        return axioms;

    }

    private OWLSubClassOfAxiom relationship(OWLClass appClass, String propertyLocalName, Iri outcomeIri) {
        return SubClassOf(appClass,
                          ObjectSomeValuesFrom(ObjectProperty(IRI(iriPrefix + propertyLocalName)),
                                        Class(IRI(outcomeIri.lexicalValue()))));
    }

}
