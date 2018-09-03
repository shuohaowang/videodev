package com.aaron.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {

    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String videoOutputPath) throws IOException {

        //
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutputPath);
        for(String c : command){
            System.out.println(c);
        }
        ProcessBuilder process = new ProcessBuilder(command);
         process.start();



    }

    public static void main(String[] args) {
        FFMpegTest ffMpegTest = new FFMpegTest("E:\\素材\\ffmpeg\\bin\\ffmpeg.exe");

        try {
            ffMpegTest.convertor("E:\\素材\\video\\demo.mp4","F:\\Temp\\hello.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
