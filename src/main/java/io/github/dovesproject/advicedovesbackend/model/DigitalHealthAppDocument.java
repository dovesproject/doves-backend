package io.github.dovesproject.advicedovesbackend.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Nullable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DigitalHealthAppDocument(String id,
                                       String applicationName,
                                       String company,
                                       String applicationEmail,
                                       String url,
                                       String applicationDescription,
                                       List<Iri> conditions,
                                       List<Iri> outcomes,
                                       List<Iri> technologies,
                                       List<Iri> users) {

    @JsonCreator
    public DigitalHealthAppDocument(String id,
                                    String applicationName,
                                    String company,
                                    String applicationEmail,
                                    String url,
                                    String applicationDescription,
                                    @Nullable List<Iri> conditions,
                                    @Nullable List<Iri> outcomes,
                                    @Nullable List<Iri> technologies,
                                    @Nullable List<Iri> users) {
        this.id = id;
        this.applicationName = applicationName;
        this.company = company;
        this.applicationEmail = applicationEmail;
        this.url = url;
        this.applicationDescription = applicationDescription;
        this.conditions = conditions != null ? conditions : List.of();
        this.outcomes = outcomes != null ? outcomes : List.of();
        this.technologies = technologies != null ? technologies : List.of();
        this.users = users != null ? users : List.of();
    }
}
