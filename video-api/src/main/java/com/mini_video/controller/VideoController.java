package com.mini_video.controller;


import com.mini_video.pojo.Bgm;
import com.mini_video.pojo.Videos;
import com.mini_video.service.BgmService;
import com.mini_video.service.VideoService;
import com.mini_video.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import static com.mini_video.utils.Constants.FILE_SPACE;

@RestController
@RequestMapping("/miniVideo/video")
public class VideoController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private VideoService videoService;

    @Autowired
    private AliyunOSSUtil aliyunOSSUtil;

    @Autowired
    private BgmService bgmService;

    @Autowired
    private CommonConstants commonConstants;

    @PostMapping(value = "/testVideo")
    public MData testVideo(HttpServletRequest request) {
        MData result = new MData();
        String outFilePath = request.getSession().getServletContext().getRealPath("/WEB-INF/tmp");
        log.info("outFilePath: " + outFilePath);
        MergeVideoMp3 tool = new MergeVideoMp3("/usr/local/ffmpeg-4.1/ffmpeg");

        try {
            tool.convertor("http://timg.inabook.cn/pictureframe/mini_video/video/test1.mp4",
                    "http://timg.inabook.cn/pictureframe/mini_video/bgm/test1.mp3", 14,
                    "/var/www/temp/t.mp4");

            String res = aliyunOSSUtil.uploadFileRequest("pictureframe/mini_video/video/test2.mp4", "/var/www/temp/t.mp4");
            // 删除临时文件
            File file = new File("/var/www/temp/t.mp4");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("outFilePath", outFilePath);
        return result;
    }


    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public MData upload(Integer userId,
                        Integer bgmId, double videoSeconds,
                        int videoWidth, int videoHeight,
                        String desc,
                        MultipartFile file) throws Exception {

        MData result = new MData();
        if (StringUtil.isBlank(userId)) {
            result.error("用户id不能为空...");
            return result;
        }

        // 保存到数据库中的相对路径
        // 文件上传的最终保存路径
        String uploadPathDB = "";
        String ossFileName = "";
        try {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    long currentTimeMillis = System.currentTimeMillis();
                    ossFileName = currentTimeMillis + fileExtension;
                    // 文件上传的最终保存路径
                    String key = Constants.OSS_VIDEO_FOLDER + userId + "/" + currentTimeMillis + fileExtension;
                    log.info("key: " + key);
                    String uploadRes = aliyunOSSUtil.uploadInputStream(key, file.getInputStream());
                    result.put("data", key);
                    log.info("uploadRes: " + uploadRes);
                    uploadPathDB = key;
                }
            } else {
                result.error("upload error");
                return result;
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            result.error("upload error");
            return result;
        }
        // 判断bgmId是否为空，如果不为空，
        // 那就查询bgm的信息，并且合并视频，生产新的视频
        if (StringUtil.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = commonConstants.getOssHost() + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(Constants.FFMPEG_EXE);
            String videoInputPath = Constants.TEMP_PATH + ossFileName;
            tool.convertor(commonConstants.getOssHost() + uploadPathDB, mp3InputPath, videoSeconds, videoInputPath);
            String res = aliyunOSSUtil.uploadFileRequest(uploadPathDB, videoInputPath);
        }

//        System.out.println("uploadPathDB=" + uploadPathDB);
//        System.out.println("finalVideoPath=" + finalVideoPath);

        // 保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(uploadPathDB + Constants.VIDEO_FRAME_PREFIX);
        video.setStatus(Constants.SUCCESS);
        video.setCreateTime(new Date());

        Videos videoId = videoService.saveVideo(video);

        result.put("data", videoId);
        return result;
    }


    /**
     * @Description: 分页和搜索查询视频列表
     * isSaveRecord：1 - 需要保存
     * 0 - 不需要保存 ，或者为空的时候
     */
    @PostMapping(value = "/showAll")
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
