package com.mini_video.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "search_records")
public class SearchRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 搜索的内容
     */
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}