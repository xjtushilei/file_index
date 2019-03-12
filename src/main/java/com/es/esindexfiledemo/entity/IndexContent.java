package com.es.esindexfiledemo.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "file_index", type = "file", shards = 3, replicas = 0, refreshInterval = "-1")
public class IndexContent {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String uuid;

    @Field(type = FieldType.Keyword)
    private String realPath;

    @Field(type = FieldType.Long)
    private long space;


    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Date,
            format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;


    @Override
    public String toString() {

        return "IndexContent{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", space=" + space +
                ", content='" + (content.length() > 20 ? content.substring(0, 20) : content) + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                '}';
    }

    public IndexContent() {
    }

    public IndexContent(String id, String title, String uuid, String realPath, long space, String content, String type, Date date) {
        this.id = id;
        this.title = title;
        this.uuid = uuid;
        this.realPath = realPath;
        this.space = space;
        this.content = content;
        this.type = type;
        this.date = date;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
