package io.github.dovesproject.advicedovesbackend.service;

import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppReasoner;
import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppsLoader;
import io.github.dovesproject.advicedovesbackend.model.*;
import io.github.dovesproject.advicedovesbackend.ontology.OwlClassTranslator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-07
 */
@Component
public class SearchService {

    private final DigitalHealthAppReasoner reasoner;

    private final OWLDataFactory dataFactory;

    private final DigitalHealthAppsLoader appsLoader;

    private final OwlClassTranslator classTranslator;

    public SearchService(DigitalHealthAppReasoner reasoner,
                         OWLDataFactory dataFactory,
                         DigitalHealthAppsLoader appsLoader, OwlClassTranslator classTranslator) {
        this.reasoner = reasoner;
        this.dataFactory = dataFactory;
        this.appsLoader = appsLoader;
        this.classTranslator = classTranslator;
    }

    public SearchResult performSearch(SearchSpecification searchSpecification) {
        var matches = reasoner.getAppsMatching(searchSpecification);
        var matchingApps = toApps(matches);
        return new SearchResult(searchSpecification, matchingApps);

    }

    private List<DigitalHealthAppResolvedDocument> toApps(Collection<OWLClass> matches) {
        return matches.stream()
                      .map(OWLNamedObject::getIRI)
                      .map(IRI::toString)
                      .map(s -> s.substring(s.lastIndexOf("/") + 1))
                      .flatMap(this::joinWithApps)
                      .toList();
    }

    private List<Term> toTerms(List<Iri> iris) {
        return iris.stream()
                .map(iri -> IRI.create(iri.lexicalValue()))
                .map(classTranslator::toTerm)
                .toList();
    }

    private Stream<DigitalHealthAppResolvedDocument> joinWithApps(String id) {
        try {
            return appsLoader.loadApps().stream().filter(app -> app.id().equals(id))
                    .map(appDoc -> {
                        var conditions = toTerms(appDoc.conditions());
                        var outcomes = toTerms(appDoc.outcomes());
                        var technologies = toTerms(appDoc.technologies());
                        var users = toTerms(appDoc.users());
                        return new DigitalHealthAppResolvedDocument(
                                appDoc.id(),
                                appDoc.applicationName(),
                                appDoc.company(),
                                appDoc.applicationEmail(),
                                appDoc.url(),
                                appDoc.applicationDescription(),
                                conditions,
                                outcomes,
                                technologies,
                                users
                        );
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
