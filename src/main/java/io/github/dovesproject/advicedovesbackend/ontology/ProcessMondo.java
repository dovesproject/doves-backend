package io.github.dovesproject.advicedovesbackend.ontology;

import com.github.jsonldjava.shaded.com.google.common.collect.Streams;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-08
 */
public class ProcessMondo {

    public Set<OWLAxiom> processMondoAxioms(OWLOntology ontology) {
        var deprecatedClassAxioms = getDeprecatedClassAxioms(ontology);
        var nonHumanDisease = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create("http://purl.obolibrary.org/obo/MONDO_0005583"));
        var branchAxioms = getBranchAxioms(nonHumanDisease, ontology);
        var deprecatedClasses = getDeprecatedClasses(ontology).collect(Collectors.toSet());
        return ontology.getAxioms().stream()
                .filter(ax -> isNotAnnotationOn(ax, "http://www.geneontology.org/formats/oboInOwl#hasDbXref"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.w3.org/2004/02/skos/core#exactMatch"))
                .filter(ax -> isNotAnnotationOn(ax, "http://purl.obolibrary.org/obo/mondo#exactMatch"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.w3.org/2004/02/skos/core#closeMatch"))
                .filter(ax -> isNotAnnotationOn(ax, "http://purl.obolibrary.org/obo/mondo#closeMatch"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.w3.org/2004/02/skos/core#narrowMatch"))
                .filter(ax -> isNotAnnotationOn(ax, "http://purl.org/dc/terms/conformsTo"))
                .filter(ax -> isNotAnnotationOn(ax, "http://purl.obolibrary.org/obo/mondo#excluded_subClassOf"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.geneontology.org/formats/oboInOwl#id"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.geneontology.org/formats/oboInOwl#inSubset"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.geneontology.org/formats/oboInOwl#hasAlternativeId"))
                .filter(ax -> isNotAnnotationOn(ax, "http://www.w3.org/2004/02/skos/core#notation"))
                .filter(ax -> isNotAnnotationOn(ax, "http://data.bioontology.org/metadata/treeView"))
                       .filter(ax -> !deprecatedClassAxioms.contains(ax))
                       .filter(ax -> !branchAxioms.contains(ax))
                       .filter(ax -> {
                    if(ax instanceof OWLDeclarationAxiom declAx) {
                        return !deprecatedClasses.contains(declAx.getEntity());
                    }
                    else {
                        return true;
                    }
                })
                .filter(ax -> isNotOfType(ax, AxiomType.DECLARATION))
                       .filter(ax -> isNotOfType(ax, AxiomType.OBJECT_PROPERTY_ASSERTION))
                       .map(this::withoutAnnotations)
                .collect(Collectors.toSet());
    }

    private boolean isNotOfType(OWLAxiom ax, AxiomType<?> axiomType) {
        return !ax.getAxiomType().equals(axiomType);
    }

    private Set<OWLAxiom> getBranchAxioms(OWLClass cls, OWLOntology ontology) {
        var reasoner = new StructuralReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
        return reasoner.getSubClasses(cls, false)
                .getFlattened()
                .stream()
                .filter(c -> !c.isOWLNothing())
                .flatMap(c -> {
                    return Stream.concat(
                            ontology.getAxioms(c, Imports.INCLUDED).stream(),
                            ontology.getAnnotationAssertionAxioms(c.getIRI()).stream()
                    );
                })
                .collect(Collectors.toSet());
    }


    private Set<OWLAxiom> getDeprecatedClassAxioms(OWLOntology ontology) {
        return getDeprecatedClasses(ontology)
                       .flatMap(cls -> Streams.concat(ontology.getAnnotationAssertionAxioms(cls.getIRI()).stream(),
                                                         ontology.getAxioms(cls, Imports.INCLUDED).stream(),
                                                         ontology.getReferencingAxioms(cls).stream(),
                                                         ontology.getDeclarationAxioms(cls).stream()))
                       .collect(Collectors.toSet());
    }

    private Stream<OWLClass> getDeprecatedClasses(OWLOntology ontology) {
        return ontology.getClassesInSignature()
                       .stream()
                       .filter(cls -> ontology.getAnnotationAssertionAxioms(cls.getIRI())
                                              .stream()
                                              .anyMatch(OWLAnnotationAssertionAxiom::isDeprecatedIRIAssertion));
    }

    private OWLAxiom withoutAnnotations(OWLAxiom ax) {
        return ax.getAxiomWithoutAnnotations();
    }

    private boolean isNotAnnotationOn(OWLAxiom ax, String s) {
        return !(ax instanceof OWLAnnotationAssertionAxiom && ((OWLAnnotationAssertionAxiom) ax).getProperty().getIRI().toString().equals(s));
    }

    public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        var pathToMondo = Path.of(args[0]);
        var ontologyManager = OWLManager.createOWLOntologyManager();
        var ontology = ontologyManager.loadOntologyFromOntologyDocument(new BufferedInputStream(Files.newInputStream(pathToMondo)));
        ProcessMondo processMondo = new ProcessMondo();
        var filteredAxioms = processMondo.processMondoAxioms(ontology);

        var outputPath = pathToMondo.getParent().resolve("mondo-extract.owl");
        var outManager = OWLManager.createOWLOntologyManager();
        var extractedOntology = outManager.createOntology(filteredAxioms, ontology.getOntologyID().getOntologyIRI().get());
        var refs = extractedOntology.getReferencingAxioms(ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("http://purl.obolibrary.org/obo/MONDO_0002680")));
        refs.forEach(System.out::println);
        System.out.println(extractedOntology.containsClassInSignature(IRI.create("http://purl.obolibrary.org/obo/MONDO_0002680")));
        extractedOntology
                .saveOntology(new BufferedOutputStream(Files.newOutputStream(outputPath)));

    }

}
