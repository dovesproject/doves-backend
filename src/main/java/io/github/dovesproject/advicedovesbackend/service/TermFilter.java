package io.github.dovesproject.advicedovesbackend.service;

import io.github.dovesproject.advicedovesbackend.model.Term;

import java.util.function.Predicate;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-05
 */
public class TermFilter implements Predicate<Term> {

    private final String query;

    public TermFilter(String query) {
        this.query = query;
    }

    @Override
    public boolean test(Term term) {
        return isIncluded(term);
    }

    private boolean isIncluded(Term t) {
        return matches(t.label()) ||
                t.synonyms().stream().anyMatch(this::matches);
    }

    private boolean matches(String s) {
        var index = s.toLowerCase().indexOf(query.toLowerCase());
        if(index == -1) {
            return false;
        }
        if(index == 0) {
            return true;
        }
        return Character.isWhitespace(s.charAt(index - 1));
    }
}
