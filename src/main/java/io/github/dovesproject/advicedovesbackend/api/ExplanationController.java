package io.github.dovesproject.advicedovesbackend.api;

import io.github.dovesproject.advicedovesbackend.model.Explanation;
import io.github.dovesproject.advicedovesbackend.model.ExplanationRequest;
import io.github.dovesproject.advicedovesbackend.model.SearchResult;
import io.github.dovesproject.advicedovesbackend.model.SearchSpecification;
import io.github.dovesproject.advicedovesbackend.service.ExplanationService;
import io.github.dovesproject.advicedovesbackend.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-09
 */
@RestController
public class ExplanationController {

    private final ExplanationService service;

    public ExplanationController(ExplanationService service) {
        this.service = service;
    }

    @PostMapping(value = "/explain", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<Explanation> search(@RequestBody ExplanationRequest explanationRequest) {
        return service.getExplanation(explanationRequest.searchSpecification(),
                                      explanationRequest.applicationId());
    }
}
