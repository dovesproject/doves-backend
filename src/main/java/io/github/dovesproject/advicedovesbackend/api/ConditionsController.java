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
 * 2023-02-24
 */
@RestController
public class ConditionsController {

    private final TermsService termsService;

    public ConditionsController(TermsService termsService) {
        this.termsService = termsService;
    }

    @GetMapping(path = "/conditions", produces = APPLICATION_JSON_VALUE)
    public List<Term> conditions(@RequestParam String query) {
        return termsService.getTerms(query, TermCategory.CONDITION);
    }
}
