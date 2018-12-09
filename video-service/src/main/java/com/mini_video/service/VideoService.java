package com.mini_video.service;

import com.mini_video.pojo.Videos;
import com.mini_video.utils.PagedResult;

public interface VideoService {

    /**
     * @Description: 分页查询视频列表
     */
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord,
                                    Integer page, Integer pageSize);
}
