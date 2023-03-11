package io.github.dovesproject.advicedovesbackend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-11
 */
public record DigitalHealthAppResolvedDocument(String id,
                                               String applicationName,
                                               String company,
                                               String applicationEmail,
                                               String url,
                                               String applicationDescription,
                                               List<Term> conditions,
                                               List<Term> outcomes,
                                               List<Term> technologies,
                                               List<Term> users) {


    @JsonCreator
    public DigitalHealthAppResolvedDocument(String id,
                                            String applicationName,
                                            String company,
                                            String applicationEmail,
                                            String url,
                                            String applicationDescription,
                                            @Nullable List<Term> conditions,
                                            @Nullable List<Term> outcomes,
                                            @Nullable List<Term> technologies,
                                            @Nullable List<Term> users) {
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
