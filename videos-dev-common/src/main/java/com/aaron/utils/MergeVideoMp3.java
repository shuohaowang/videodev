package com.aaron.utils;

import java.io.BufferedReader;
import java.io.IOException;
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

    public  void convertor(String videoInputPath,String mp3InputPath,double seconds,String videoOutputPath) throws IOException {
        //ffmpeg.exe -i demo.mp4 -i bgm.mp3 -t 7 -y news.mp4
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(mp3InputPath);
        command.add("-t");
        command.add(String.valueOf(seconds));
        command.add("-y");
        command.add(videoOutputPath);
        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = "";
        while ( (line = br.readLine()) != null ) {
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
        MergeVideoMp3 ffMpegTest = new MergeVideoMp3("E:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("E:\\素材\\jibo.mp4","D:\\videos_dev\\bgm\\demo02.mp3"
                    ,22,"F:\\Temp\\demo11131.mp4");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
