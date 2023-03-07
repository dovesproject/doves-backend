package io.github.dovesproject.advicedovesbackend;

import io.github.dovesproject.advicedovesbackend.db.DigitalHealthAppsLoader;
import io.github.dovesproject.advicedovesbackend.ontology.DovesOwlOntologyLoader;
import io.github.dovesproject.advicedovesbackend.search.TermCategory;
import io.github.dovesproject.advicedovesbackend.search.TermsIndexLoader;
import io.github.dovesproject.advicedovesbackend.search.TermsRepository;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

@SpringBootApplication
//@EnableMongoRepositories
public class AdviceDovesBackendApplication implements ApplicationRunner {

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
        return new Reasoner.ReasonerFactory();
    }

    @Bean
    OWLDataFactory dataFactory() {
        return new OWLDataFactoryImpl();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var appsLoader = applicationContext.getBean(DigitalHealthAppsLoader.class);
        appsLoader.loadApps();
        var loader = applicationContext.getBean(TermsIndexLoader.class);
        loader.loadTerms();
        var rep = applicationContext.getBean(TermsRepository.class);
        var result = rep.findByCustomQuery("diabetes", TermCategory.CONDITION);
        System.out.println(result);
    }
}
