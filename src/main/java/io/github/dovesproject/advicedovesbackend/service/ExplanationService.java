package io.github.dovesproject.advicedovesbackend.service;

import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppReasoner;
import io.github.dovesproject.advicedovesbackend.model.Explanation;
import io.github.dovesproject.advicedovesbackend.model.SearchSpecification;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-09
 */
@Component
public class ExplanationService {

    private final DigitalHealthAppReasoner reasoner;

    public ExplanationService(DigitalHealthAppReasoner reasoner) {
        this.reasoner = reasoner;
    }

    public List<Explanation> getExplanation(SearchSpecification searchRequest,
                                            String applicationId) {

        return reasoner.getExplanations(searchRequest, applicationId);
    }
}
