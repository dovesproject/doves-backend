package io.github.dovesproject.advicedovesbackend.search;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-03-05
 */
public interface TermsRepository extends ElasticsearchRepository<TermDocument, String>, CrudRepository<TermDocument, String> {

//    @Query("category: ?1 AND (label:\"?0\" OR synonyms:\"?0\")")
    List<TermDocument> findByLabelOrSynonymsAndCategory(String label, String synonyms, TermCategory termCategory);

    @Query("""
            {
                "bool" : {
                    "must" : [
                        {
                            "dis_max" : {
                                "queries" : [
                                    {
                                        "match" : {
                                            "label" : {
                                                "query" : "?0",
                                                "operator" : "AND"
                                            }
                                        }
                                    },
                                    {
                                        "multi_match": {
                                              "query": "?0",
                                              "type": "bool_prefix",
                                              "fields": [
                                                "label",
                                                "label._2gram",
                                                "label._3gram"
                                              ]
                                            }
                                    },
                                    { "match": { "synonyms": { "query": "?0", "operator" : "AND" } } }
                                ]
                            }
                        },
                        {
                            "match" : { "category" :  "?1"}
                        }
                    
                    ]
                }
            }
            """)
    List<TermDocument> findByCustomQuery(String query, TermCategory type);
}
