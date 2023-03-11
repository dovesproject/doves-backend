package io.github.dovesproject.advicedovesbackend.db;

import io.github.dovesproject.advicedovesbackend.model.Explanation;
import io.github.dovesproject.advicedovesbackend.model.SearchSpecification;
import io.github.dovesproject.advicedovesbackend.ontology.DovesOwlOntologyLoader;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.PropertyAssertionValueShortFormProvider;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.springframework.stereotype.Component;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.Tree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-07
 */
@Component
public class DigitalHealthAppReasoner {

    private static String iriPrefix = "https://purl.org/doves/doves-ontology/";

    private final OWLReasonerFactory reasonerFactory;

    private final DovesOwlOntologyLoader ontologyLoader;

    private final DigitalHealthAppOntologyAxiomsGenerator axiomsGenerator;

    private final OWLDataFactory dataFactory;

    public DigitalHealthAppReasoner(OWLReasonerFactory reasonerFactory,
                                    DovesOwlOntologyLoader ontologyLoader,
                                    DigitalHealthAppOntologyAxiomsGenerator axiomsGenerator,
                                    OWLDataFactory dataFactory) {
        this.reasonerFactory = reasonerFactory;
        this.ontologyLoader = ontologyLoader;
        this.axiomsGenerator = axiomsGenerator;
        this.dataFactory = dataFactory;
    }

    @Nullable
    private OWLReasoner reasoner = null;

    public Collection<OWLClass> getAppsMatching(SearchSpecification searchSpecification) {
        var queryExpression = getQueryClassExpression(searchSpecification);
        System.out.println(queryExpression);
        return getReasoner().getSubClasses(queryExpression, false)
                            .getFlattened()
                            .stream()
                            .filter(cls -> !cls.isOWLNothing())
                            .toList();
    }

    public List<Explanation> getExplanations(SearchSpecification searchSpecification,
                                             String applicationId) {
        var appCls = dataFactory.getOWLClass(IRI("https://purl.org/doves/doves-ontology/" + applicationId));
        var queryClsExpression = getQueryClassExpression(searchSpecification);
        var entailedAxiom = SubClassOf(appCls,
                                       queryClsExpression);
        System.out.println("Explain: " + entailedAxiom);
        var expGenFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
        var ontology = getOntology();
        var expGen = expGenFac.createExplanationGenerator(ontology);
        var explanations = expGen.getExplanations(entailedAxiom, 1);
        var explanationOrderer = new ExplanationOrdererImpl(ontology.getOWLOntologyManager());
        var expls = explanations.stream()
                .map(org.semanticweb.owl.explanation.api.Explanation::getAxioms)
//                .map(axioms -> {
//                    var orderedExplanation = explanationOrderer.getOrderedExplanation(entailedAxiom, axioms);
//                    var orderedAxioms = new ArrayList<OWLAxiom>();
//                    flatten(orderedExplanation, orderedAxioms);
//                    return orderedAxioms;
//                })
                .map(axioms -> axioms.stream().map(this::render).toList())
                .map(axioms -> new Explanation(render(entailedAxiom), axioms))
                .toList();
        System.out.println("Got explanations: " + expls);
        return expls;
    }

    private void flatten(Tree<OWLAxiom> tree, List<OWLAxiom> list) {
        var ax = tree.getUserObject();
        list.add(ax);
        tree.getChildren().forEach(child -> flatten(tree, list));
    }

    private String render(OWLAxiom ax) {
        var ren = new ManchesterOWLSyntaxOWLObjectRendererImpl();
        ren.setShortFormProvider(new AnnotationValueShortFormProvider(
                List.of(dataFactory.getRDFSLabel()),
                Map.of(dataFactory.getRDFSLabel(), List.of("en", "")),
                new OWLOntologySetProvider() {
                    @Nonnull
                    @Override
                    public Set<OWLOntology> getOntologies() {
                        return Set.of(getOntology());
                    }
                }
        ));

        return ren.render(ax);
    }

    private OWLClassExpression getQueryClassExpression(SearchSpecification searchSpecification) {
        var applicationClass = dataFactory.getOWLClass(IRI.create(iriPrefix + "DigitalHealthApplication"));

        var conjuncts = new HashSet<OWLClassExpression>();

        var hasOutcome = ObjectProperty(IRI.create(iriPrefix + "hasOutcome"));
        var hasCondition = ObjectProperty(IRI.create(iriPrefix + "hasCondition"));
        var hasUserWithRole = ObjectProperty(IRI.create(iriPrefix + "hasUserWithRole"));
        conjuncts.add(applicationClass);

        var users = new HashSet<OWLClass>();
        if(searchSpecification.appUserNode().isAnyChecked()) {
            var mostGeneralUsers = searchSpecification.appUserNode().getMostGeneralCheckedIris();
            users.addAll(mostGeneralUsers.stream()
                                         .map(IRI::create)
                                         .map(dataFactory::getOWLClass)
                                         .collect(Collectors.toSet()));
        }

        searchSpecification.outcomes().stream()
                .map(term -> term.iri().lexicalValue())
                           .map(this::toClasses).map(filler -> ObjectSomeValuesFrom(hasOutcome, filler)).forEach(conjuncts::add);

        var diseaseArisesFromFeature = ObjectProperty(IRI("http://purl.obolibrary.org/obo/RO_0004022"));
        searchSpecification.conditions().stream()
                .map(term -> term.iri().lexicalValue())
                           .map(this::toClasses)
                .map(filler -> ObjectUnionOf(filler, ObjectSomeValuesFrom(diseaseArisesFromFeature, filler)))
                           .map(filler -> ObjectSomeValuesFrom(hasCondition, filler)).forEach(conjuncts::add);
        users.stream().map(filler -> ObjectSomeValuesFrom(hasUserWithRole, filler)).forEach(conjuncts::add);

        return dataFactory.getOWLObjectIntersectionOf(conjuncts);
    }


    private OWLClass toClasses(String iri) {
        return Stream.of(iri)
                   .filter(Objects::nonNull)
                   .map(IRI::create)
                   .map(dataFactory::getOWLClass)
                .findFirst().get();
    }


    private OWLOntology getOntology() {
        try {
            var dovesOntology = ontologyLoader.getOntology();
            var appAxioms = axiomsGenerator.generateOntologyAxioms();
            var allAxioms = new HashSet<OWLAxiom>();
            allAxioms.addAll(dovesOntology.getAxioms());
            allAxioms.addAll(appAxioms);
            var ax = SubClassOf(Class(IRI("https://purl.org/doves/doves-ontology/DigitalHealthApplication")),
                       ObjectSomeValuesFrom(ObjectProperty(IRI("https://purl.org/doves/doves-ontology/hasUserWithRole")),
                                            Class(IRI("https://purl.org/doves/38552094-2ddf-4e81-b33d-080f5b904cd7"))));
            allAxioms.add(ax);
            var ontologyManager = OWLManager.createOWLOntologyManager();
            return ontologyManager.createOntology(allAxioms, IRI.create("http://dovesapps.com"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private synchronized OWLReasoner getReasoner() {
        if (this.reasoner == null) {
            var ontology = getOntology();
            try (var os = new BufferedOutputStream(new FileOutputStream("/tmp/doves-apps.owl"))) {
                ontology.saveOntology(os);
            } catch (OWLOntologyStorageException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            this.reasoner = reasonerFactory.createReasoner(ontology);
        }
        return this.reasoner;
    }
}
