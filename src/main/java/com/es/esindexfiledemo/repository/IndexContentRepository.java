package com.es.esindexfiledemo.repository;

import com.es.esindexfiledemo.entity.IndexContent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IndexContentRepository extends ElasticsearchRepository<IndexContent, String> {

    List<IndexContent> findByTitleOrContent(String q, String q1);

}