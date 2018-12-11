package com.mini_video.service;

import com.mini_video.pojo.Comments;
import com.mini_video.pojo.Videos;
import com.mini_video.utils.PagedResult;

import java.util.List;

public interface VideoService {

    /**
     * @Description: 分页查询视频列表
     */
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord,
                                    Integer page, Integer pageSize);

    Videos saveVideo(Videos video);

    PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize);

    PagedResult queryMyLikeVideos(String userId, Integer page, int pageSize);

    List<String> queryHostWords();

    void userLikeVideo(String userId, String videoId, String videoCreaterId);

    void userUnLikeVideo(String userId, String videoId, String videoCreaterId);

    Comments saveComment(Comments comment);

    PagedResult getAllComments(Integer videoId, Integer page, Integer pageSize);
}
