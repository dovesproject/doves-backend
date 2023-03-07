package io.github.dovesproject.advicedovesbackend.ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.UncheckedIOException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-04
 */
@Component
public class DovesOwlOntologyLoader {

    private OWLOntology ontology = null;

    public synchronized OWLOntology getOntology() {
        if(this.ontology != null) {
            return ontology;
        }
        try {
            var is = DovesOwlOntologyLoader.class.getResourceAsStream("/doves-ontology-merged.owl");
            var bufferedIs = new BufferedInputStream(is);
            var manager = OWLManager.createOWLOntologyManager();
            return this.ontology = manager.loadOntologyFromOntologyDocument(bufferedIs);
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

}
