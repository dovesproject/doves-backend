package io.github.dovesproject.advicedovesbackend.model;

import java.util.Comparator;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-04
 */
public record Outcome(Term term) implements Comparable<Outcome> {

    @Override
    public int compareTo(Outcome o) {
        return this.term().compareTo(o.term());
    }
}
