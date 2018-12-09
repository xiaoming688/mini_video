package com.mini_video.controller;


import com.mini_video.pojo.Videos;
import com.mini_video.service.VideoService;
import com.mini_video.utils.Constants;
import com.mini_video.utils.MData;
import com.mini_video.utils.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/miniVideo/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     *
     * @Description: 分页和搜索查询视频列表
     * isSaveRecord：1 - 需要保存
     * 				 0 - 不需要保存 ，或者为空的时候
     */
    @PostMapping(value="/showAll")
    public MData showAll(@RequestBody Videos video, Integer isSaveRecord,
                         Integer page, Integer pageSize) throws Exception {

        MData result = new MData();
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = Constants.PAGE_SIZE;
        }

        PagedResult result1 = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
        result.put("data", result1);
        return result;
    }

}
