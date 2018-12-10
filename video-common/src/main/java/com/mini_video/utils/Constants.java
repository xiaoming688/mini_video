package com.mini_video.utils;

/**
 * @author MM
 * @create 2018-12-07 14:15
 **/
public class Constants {

    public static final String USER_REDIS_SESSION = "user-redis-session";

    // 文件保存的命名空间
    public static final String FILE_SPACE = "C:/imooc_videos_dev";

    // ffmpeg所在目录
//    public static final String FFMPEG_EXE = "C:\\ffmpeg\\bin\\ffmpeg.exe";

    public static final String FFMPEG_EXE = "/usr/local/ffmpeg-4.1/ffmpeg";

    public static final String TEMP_PATH = "/var/www/temp/";

    // 每页分页的记录数
    public static final Integer PAGE_SIZE = 5;

    public final static String OSS_FACE_FOLDER = "pictureframe/mini_video/face/";

    public final static String OSS_VIDEO_FOLDER = "pictureframe/mini_video/video/";

    public static final String VIDEO_FRAME_PREFIX = "?x-oss-process=video/snapshot,t_1,f_jpg,w_800,h_600";

    public static final Integer SUCCESS = 1;

}
