package io.github.dovesproject.advicedovesbackend.db;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-07
 */
@Component
public class DigitalHealthAppOntologyAxiomsGenerator {

    private final DigitalHealthAppAxiomsGenerator axiomsGenerator;

    private final DigitalHealthAppsLoader loader;

    public DigitalHealthAppOntologyAxiomsGenerator(DigitalHealthAppAxiomsGenerator axiomsGenerator,
                                                   DigitalHealthAppsLoader loader) {
        this.axiomsGenerator = axiomsGenerator;
        this.loader = loader;
    }

    public Set<OWLAxiom> generateOntologyAxioms() throws IOException {
        return loader.loadApps()
                .stream()
                .flatMap(app -> axiomsGenerator.generateAxioms(app).stream())
                .collect(Collectors.toSet());
    }
}
