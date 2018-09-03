package com.aaron.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {

    private String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public  void getCover(String videoInputPath,String coverOutputPath) throws IOException {
        //ffmpeg.exe -ss 00:00:01 -y -i demo1.mp4 -vframes 1 new.png
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);
        command.add("-ss");
        command.add("00:00:01");
        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);
        command.add("-vframes");
        command.add("1");
        command.add(coverOutputPath);
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
        FetchVideoCover ffMpegTest = new FetchVideoCover("E:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
           ffMpegTest.getCover("E:\\ffmpeg\\bin\\demo1.mp4","E:\\newsdemo.jpg");
        } catch (IOException e) {
            //ffmpeg.exe -ss 00:00:01 -y -i demo1.mp4 -vframes 1 new.png
            e.printStackTrace();
        }
    }
}
