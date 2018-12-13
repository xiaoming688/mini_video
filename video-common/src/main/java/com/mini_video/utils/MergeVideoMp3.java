package com.mini_video.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath, String mp3InputPath,
                          double seconds, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 苏州大裤衩.mp4 -i bgm.mp3 -t 7 -y 新的视频.mp4

        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(mp3InputPath);

        command.add("-i");
        command.add(videoInputPath);


        command.add("-t");
        command.add(String.valueOf(seconds));

//        command.add("-qscale");
//        command.add(String.valueOf(6));
        command.add("-vcodec");
        command.add("copy");

        command.add("-y");
        command.add(videoOutputPath);

        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public void convertorNoAudio(String videoInputPath, Double seconds, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 苏州大裤衩.mp4 -i bgm.mp3 -t 7 -y 新的视频.mp4

        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);

        if (seconds != null) {
            command.add("-t");
            command.add(String.valueOf(seconds));
        }
        command.add("-vcodec");
        command.add("copy");
        command.add("-an");

        command.add("-y");
        command.add(videoOutputPath);

        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) {
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("D:\\soft\\ffmpeg-win64\\bin\\ffmpeg.exe");
        try {
            ffmpeg.convertor("D:\\soft\\test1.mp4",
                    "D:\\soft\\test1.mp3", 14, "D:\\soft\\t222.mp4");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
