package com.mini_video.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "bgm")
public class Bgm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String author;

    private String name;

    /**
     * 播放地址
     */
    private String path;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取播放地址
     *
     * @return path - 播放地址
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置播放地址
     *
     * @param path 播放地址
     */
    public void setPath(String path) {
        this.path = path;
    }
}