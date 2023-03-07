package io.github.dovesproject.advicedovesbackend.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-02
 */
@Document(indexName = "termindex")
public class TermDocument {

        @Id
        private String id = "";

        @Field(type = FieldType.Search_As_You_Type)
        private String label = "";

        @Field(type = FieldType.Search_As_You_Type)
        private List<String> synonyms = new ArrayList<>();

        @Field(type = FieldType.Text, name = "category")
        private TermCategory category;


        public TermDocument() {

        }

        public TermDocument(TermCategory category, String iri, String label, List<String> synonyms) {
                this.category = category;
                this.id = iri;
                this.label = label;
                this.synonyms = synonyms;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getLabel() {
                return label;
        }

        public void setLabel(String label) {
                this.label = label;
        }

        public List<String> getSynonyms() {
                return synonyms;
        }

        @Override
        public String toString() {
                return "TermDocument{" + "id='" + id + '\'' + ", label='" + label + '\'' + ", synonyms=" + synonyms + ", category=" + category + '}';
        }
}
