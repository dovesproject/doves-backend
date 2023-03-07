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
public class SpecialtiesController {

    private final TermsService service;

    public SpecialtiesController(TermsService service) {
        this.service = service;
    }

    @GetMapping(value = "/specialties", produces = APPLICATION_JSON_VALUE)
    public List<Term> specialties(@RequestParam String query) {
        return service.getTerms(query, TermCategory.SPECIALTY);
    }
}
