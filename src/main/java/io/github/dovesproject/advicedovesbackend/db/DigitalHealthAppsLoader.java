package io.github.dovesproject.advicedovesbackend.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dovesproject.advicedovesbackend.model.DigitalHealthAppDocument;
import io.github.dovesproject.advicedovesbackend.ontology.OwlClassTranslator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-06
 */
@Component
public class DigitalHealthAppsLoader {

    private final ObjectMapper objectMapper;

    private final OwlClassTranslator translator;

    private final OWLDataFactory dataFactory;

    public DigitalHealthAppsLoader(ObjectMapper objectMapper, OwlClassTranslator translator, OWLDataFactory dataFactory) {
        this.objectMapper = objectMapper;
        this.translator = translator;
        this.dataFactory = dataFactory;
    }

    public List<DigitalHealthAppDocument> loadApps() throws IOException {
        var is = DigitalHealthAppsLoader.class.getResourceAsStream("/application-list.json");
        return objectMapper.readValue(is, new TypeReference<>() {});

    }
}
