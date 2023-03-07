package io.github.dovesproject.advicedovesbackend.api;

import io.github.dovesproject.advicedovesbackend.model.Term;
import io.github.dovesproject.advicedovesbackend.service.TermsService;
import io.github.dovesproject.advicedovesbackend.search.TermCategory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-06
 */
@RestController
public class TechnologyTypeController {

    private final TermsService termsService;

    public TechnologyTypeController(TermsService termsService) {
        this.termsService = termsService;
    }

    @GetMapping(path = "/technology-types", produces = APPLICATION_JSON_VALUE)
    public List<Term> conditions(@RequestParam String query) {
        return termsService.getTerms(query, TermCategory.TECHNOLOGY_TYPE);
    }
}
