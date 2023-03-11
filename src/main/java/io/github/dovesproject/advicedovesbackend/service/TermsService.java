package io.github.dovesproject.advicedovesbackend.service;

import io.github.dovesproject.advicedovesbackend.model.Iri;
import io.github.dovesproject.advicedovesbackend.model.Term;
import io.github.dovesproject.advicedovesbackend.search.TermCategory;
import io.github.dovesproject.advicedovesbackend.search.TermsRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-06
 */
@Component
public class TermsService {

    private final TermsRepository termsRepository;

    public TermsService(TermsRepository termsRepository) {
        this.termsRepository = termsRepository;
    }

    public List<Term> getTerms(String query, TermCategory category) {
        if(query.contains("[")) {
            return List.of();
        }


        return termsRepository.findByCustomQuery(query, category)
                              .stream()
                              .limit(10)
                              .map(d -> new Term(d.getLabel(), d.getSynonyms(), "", new Iri(d.getId())))
                              .toList();
    }
}
