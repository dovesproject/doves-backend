package io.github.dovesproject.advicedovesbackend.api;

import io.github.dovesproject.advicedovesbackend.model.Outcome;
import io.github.dovesproject.advicedovesbackend.model.Specialty;
import io.github.dovesproject.advicedovesbackend.model.Term;
import io.github.dovesproject.advicedovesbackend.service.OutcomesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-04
 */
@RestController
public class OutcomesController {

    private final OutcomesService service;

    public OutcomesController(OutcomesService service) {
        this.service = service;
    }

    @GetMapping(value = "/outcomes", produces = APPLICATION_JSON_VALUE)
    public List<Term> specialties(@RequestParam String query) {
        return service.getOutcomes(query);
    }
}
