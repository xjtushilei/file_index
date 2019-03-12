package com.es.esindexfiledemo.controller;


import com.es.esindexfiledemo.entity.IndexContent;
import com.es.esindexfiledemo.repository.IndexContentRepository;
import com.es.esindexfiledemo.service.FileSearvice;
import org.apache.commons.io.FileUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class MainController {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    FileSearvice fileSearvice;

    @Autowired
    IndexContentRepository indexContentRepository;


    @GetMapping("/download")
    public org.springframework.http.ResponseEntity download(String uuid) throws UnsupportedEncodingException {
        org.springframework.http.ResponseEntity responseEntity = null;
        Optional<IndexContent> indexContent = indexContentRepository.findByUuid(uuid);
        if (indexContent.isPresent()) {
            String filePath = indexContent.get().getRealPath();
            System.out.println("downloading:" + filePath);

            File file = new File(filePath);
            if (file.exists()) { //判断文件目录是否存在
                Object att = null;
                try {
                    att = FileUtils.readFileToByteArray(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    responseEntity = org.springframework.http.ResponseEntity
                            .status(HttpStatus.OK)
                            .header("Content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"))
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(att);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return responseEntity;
            }
        }
        responseEntity = org.springframework.http.ResponseEntity
                .status(HttpStatus.OK)
                .body("文件不存在");
        return responseEntity;
    }

    @GetMapping("/reIndex")
    public String reIndex() {
        long count = fileSearvice.index();
        return "reindex end. count:" + count;
    }

    @GetMapping("/deleteAll")
    public String deleteAll() {
        indexContentRepository.deleteAll();
        return "deleteAll done";
    }

    @GetMapping("/q")
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
                String id = null;
                try {
                    id = URLEncoder.encode(hit.getId(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringBuilder title = new StringBuilder(smap.get("title").toString());
                String type = smap.get("type").toString();
                String uuid = smap.get("uuid").toString();
                String realPath = smap.get("realPath").toString();

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
                IndexContent indexContent = new IndexContent(id, title.toString(), uuid, realPath, space, content.toString(), type, date);
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
