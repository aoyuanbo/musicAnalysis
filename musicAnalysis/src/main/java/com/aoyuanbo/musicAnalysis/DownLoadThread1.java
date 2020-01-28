package com.aoyuanbo.musicAnalysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DownLoadThread1 implements Runnable {
    private Music music;
    private final String url;
    private final String savePath;

    DownLoadThread1(String url, String savePath) {
        this.url = url;
        this.savePath = savePath;
    }

    @Override
    public void run() {
        while (true) {
            try {
                music = App.musics.take();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("-----------" + music.getName() + "------------");
            Connection conn = Jsoup.connect(url);
            conn.data("v", music.getId());
            Document doc = null;
            boolean timeOut = true;
            while (timeOut) {
                try {
                    doc = conn.timeout(120_000).get();
                    if (doc != null) {
                        Element ele = doc.getElementById("audio");
                        String src = ele.attr("src");
                        System.out.println("开始下载");
                        URL url = new URL(src);
                        URLConnection connection = url.openConnection();
                        System.out.println("获取音乐流");
                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream fs;
                        String path = savePath + music.getName().replace(" ", "") + ".mp3";
                        System.out.println("path:" + path);
                        File file = new File(path);
                        if (!file.exists()) {
                            fs = new FileOutputStream(path);
                            byte[] buffer = new byte[1204];
                            int byteread = 0;
                            while ((byteread = inputStream.read(buffer)) != -1) {
                                fs.write(buffer, 0, byteread);
                            }
                            fs.flush();
                            fs.close();
                            System.out.println("保存成功！");
                            System.out.println("消费" + App.musics.size());
                        }
                        timeOut = false;
                    }
                } catch (IOException e) {
                    System.out.println("消费超时");
                    e.printStackTrace();
                }
            }
            System.out.println("app的size" + App.musics.size());
        }

    }
}