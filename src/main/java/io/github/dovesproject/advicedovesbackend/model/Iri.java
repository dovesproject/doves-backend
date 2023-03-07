package io.github.dovesproject.advicedovesbackend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Iri(String lexicalValue) {

    @JsonCreator
    public static Iri fromJson(String iri) {
        return new Iri(iri);
    }

    @JsonValue
    public String lexicalValue() {
        return this.lexicalValue;
    }

}
