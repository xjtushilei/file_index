package com.es.esindexfiledemo.controller;


import com.es.esindexfiledemo.entity.IndexContent;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class MainController {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @GetMapping("q")
    public HashMap<String, Object> q(String q, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pagesize) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.multiMatchQuery(q, "title", "content"));
        PageRequest pageRequest = PageRequest.of(page - 1, pagesize);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withIndices("file_index")
                .withTypes("file")
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<span style=\"color:red\">").postTags("</span>"),
                        new HighlightBuilder.Field("content").preTags("<span style=\"color:red\">").postTags("</span>"))
                .withPageable(pageRequest)
                .build();

        HashMap<String, Object> hashMap = elasticsearchTemplate.query(searchQuery, s -> {
            HashMap<String, Object> res = new HashMap<>();
            res.put("total", s.getHits().getTotalHits());
            res.put("usetime", s.getTook().toString());
            List<IndexContent> result = new ArrayList<>();
            for (SearchHit hit : s.getHits().getHits()) {
                Map smap = hit.getSourceAsMap();
                String id = hit.getId();
                StringBuilder title = new StringBuilder(smap.get("title").toString());
                String type = smap.get("type").toString();
                StringBuilder content = new StringBuilder(smap.get("content").toString().length() > 200 ? smap.get("content").toString().substring(0, 200) : smap.get("content").toString());
                long space = Long.valueOf(smap.get("space").toString());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(smap.get("date").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (hit.getHighlightFields().get("title") != null) {
                    title = new StringBuilder();
                    for (Text fragment : hit.getHighlightFields().get("title").getFragments()) {
                        title.append(fragment.string());
                    }
                }
                if (hit.getHighlightFields().get("content") != null) {
                    content = new StringBuilder();
                    for (Text fragment : hit.getHighlightFields().get("content").getFragments()) {
                        content.append(fragment.string());
                    }
                }
                IndexContent indexContent = new IndexContent(id, title.toString(), space, content.toString(), type, date);
                result.add(indexContent);
            }
            res.put("data", result);
            return res;
        });

        hashMap.put("page", page);
        hashMap.put("pagesize", pagesize);
        return hashMap;
    }
}
