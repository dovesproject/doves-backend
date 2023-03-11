package io.github.dovesproject.advicedovesbackend.service;

import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppReasoner;
import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppsLoader;
import io.github.dovesproject.advicedovesbackend.model.DigitalHealthAppDocument;
import io.github.dovesproject.advicedovesbackend.model.SearchResult;
import io.github.dovesproject.advicedovesbackend.model.SearchSpecification;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Collectors;
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

    public SearchService(DigitalHealthAppReasoner reasoner,
                         OWLDataFactory dataFactory,
                         DigitalHealthAppsLoader appsLoader) {
        this.reasoner = reasoner;
        this.dataFactory = dataFactory;
        this.appsLoader = appsLoader;
    }

    public SearchResult performSearch(SearchSpecification searchSpecification) {
        var matches = reasoner.getAppsMatching(searchSpecification);
        var matchingApps = toApps(matches);


        return new SearchResult(searchSpecification, matchingApps);

    }

    private List<DigitalHealthAppDocument> toApps(Collection<OWLClass> matches) {
        return matches.stream()
                      .map(OWLNamedObject::getIRI)
                      .map(IRI::toString)
                      .map(s -> s.substring(s.lastIndexOf("/") + 1))
                      .flatMap(this::joinWithApps)
                      .toList();
    }

    private Stream<DigitalHealthAppDocument> joinWithApps(String id) {
        try {
            return appsLoader.loadApps().stream().filter(app -> app.id().equals(id));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
