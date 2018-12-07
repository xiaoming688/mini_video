package com.mini_video.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Data
@Entity
@Table(name = "users_report")
public class UsersReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 被举报用户id
     */
    @Column(name = "deal_user_id")
    private Integer dealUserId;

    @Column(name = "deal_video_id")
    private Integer dealVideoId;

    /**
     * 类型标题，让用户选择，详情见 枚举
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 举报人的id
     */
    private Integer userid;

    /**
     * 举报时间
     */
    @Column(name = "create_date")
    private Date createDate;
}