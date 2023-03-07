package io.github.dovesproject.advicedovesbackend.ontology;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-04
 */
@Component
public class DovesOntologyReasoner {

    private static final IRI outcomesClassIri = IRI.create("https://purl.org/doves/8081940e-b77f-4aa1-bfcf-0b979f0a2158");

    private final OWLReasonerFactory reasonerFactory;

    private final OWLDataFactory dataFactory;

    private final DovesOwlOntologyLoader ontologyLoader;

    private OWLReasoner cachedReasoner = null;

    public DovesOntologyReasoner(OWLReasonerFactory reasonerFactory,
                                 OWLDataFactory dataFactory,
                                 DovesOwlOntologyLoader ontologyLoader) {
        this.reasonerFactory = reasonerFactory;
        this.dataFactory = dataFactory;
        this.ontologyLoader = ontologyLoader;
    }

    public Collection<OWLClass> getOutcomes() {
        var outcomesClass = dataFactory.getOWLClass(outcomesClassIri);
        return getOwlClasses(outcomesClass);
    }

    private List<OWLClass> getOwlClasses(OWLClass outcomesClass) {
        return getReasoner().getSubClasses(outcomesClass, false)
                .getFlattened()
                .stream()
                .distinct()
                .filter(cls -> !cls.isOWLNothing())
                .toList();
    }

    public Collection<OWLClass> getDiseases() {
        var diseaseClass = dataFactory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/MONDO_0700096"));
        return getOwlClasses(diseaseClass);
    }

    public Collection<OWLClass> getSpecialties() {
        var cls = dataFactory.getOWLClass(IRI.create("https://purl.org/doves/1d62d176-3b08-4743-819c-ba79585f3a38"));
        return getOwlClasses(cls);
    }


    public Collection<OWLClass> getTechnologyTypes() {
        var cls = dataFactory.getOWLClass(IRI.create("https://purl.org/doves/894b0e16-237d-40f2-87f0-adaa02cab8c8"));
        return getOwlClasses(cls);
    }

    public Collection<OWLClass> getTechnicalFeatures() {
        var cls = dataFactory.getOWLClass(IRI.create("https://purl.org/doves/1ce5511a-c36f-4fb4-97a6-8e1b0642a27d"));
        return getOwlClasses(cls);
    }

    private synchronized OWLReasoner getReasoner() {
        if(this.cachedReasoner != null) {
            return cachedReasoner;
        }
        var ontology = ontologyLoader.getOntology();
        this.cachedReasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
        return this.cachedReasoner;
    }
}
