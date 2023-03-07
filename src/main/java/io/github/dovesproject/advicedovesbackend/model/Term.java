package io.github.dovesproject.advicedovesbackend.model;


import java.util.Comparator;
import java.util.List;

public record Term(String label, List<String> synonyms,
                   String definition,
                   Iri iri) implements Comparable<Term> {

    private static final Comparator<Term> COMPARATOR = Comparator.comparing((Term t) -> t.label,
                                                                            String::compareToIgnoreCase);

    @Override
    public int compareTo(Term t) {
        return COMPARATOR.compare(this, t);
    }
}
