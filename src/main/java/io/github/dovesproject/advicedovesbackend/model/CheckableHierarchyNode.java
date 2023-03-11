package io.github.dovesproject.advicedovesbackend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-09
 */
public record CheckableHierarchyNode(String iri,
                                     String label,
                                     boolean checked,
                                     List<CheckableHierarchyNode> children) {

    public boolean isAnyChecked() {
        if(this.checked) {
            return true;
        }
        return children.stream().anyMatch(CheckableHierarchyNode::isAnyChecked);
    }

    public List<String> getMostGeneralCheckedIris() {
        var result = new ArrayList<String>();
        if(this.checked) {
            result.add(this.iri());
            return result;
        }
        return this.children.stream()
                .flatMap(n -> n.getMostGeneralCheckedIris().stream())
                .toList();
    }
}
