package com.es.esindexfiledemo.repository;

import com.es.esindexfiledemo.entity.IndexContent;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface IndexContentRepository extends ElasticsearchRepository<IndexContent, String> {

    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"uuid\" : \"?0\"}}}}")
    Optional<IndexContent> findByUuid(String q);

}