package com.mini_video.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "comments")
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "father_comment_id")
    private Integer fatherCommentId;

    @Column(name = "to_user_id")
    private Integer toUserId;

    /**
     * 视频id
     */
    @Column(name = "video_id")
    private Integer videoId;

    /**
     * 留言者，评论的用户id
     */
    @Column(name = "from_user_id")
    private Integer fromUserId;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 评论内容
     */
    private String comment;

}