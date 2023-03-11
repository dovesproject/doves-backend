package io.github.dovesproject.advicedovesbackend;

import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppReasoner;
import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppsLoader;
import io.github.dovesproject.advicedovesbackend.ontology.DovesOwlOntologyLoader;
import io.github.dovesproject.advicedovesbackend.search.TermCategory;
import io.github.dovesproject.advicedovesbackend.search.TermsIndexLoader;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.util.Set;

@SpringBootApplication
//@EnableMongoRepositories
public class AdviceDovesBackendApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdviceDovesBackendApplication.class);

    @Autowired
    ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(AdviceDovesBackendApplication.class, args);
    }

    @Bean
    OWLOntology ontology(DovesOwlOntologyLoader loader) {
        return loader.getOntology();
    }

    @Bean
    OWLReasonerFactory reasonerFactory() {
        return new ElkReasonerFactory();
    }

    @Bean
    OWLDataFactory dataFactory() {
        return new OWLDataFactoryImpl();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Indexing applications");
        var appsLoader = applicationContext.getBean(DigitalHealthAppsLoader.class);
        appsLoader.loadApps();
        logger.info("Indexing terms...");
        var loader = applicationContext.getBean(TermsIndexLoader.class);
        loader.loadTerms();
    }
}
