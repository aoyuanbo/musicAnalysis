package com.aoyuanbo.musicAnalysis;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;


/**
 * Hello world!
 *
 */
public class App {
    public static final int MAX_POOL=300;
    public static String lock = "lock";
    protected static ArrayBlockingQueue<Music> musics=new ArrayBlockingQueue<>(MAX_POOL);
    public static void main(String[] args) throws Exception{
        Properties prop = new Properties();
        InputStream inputStream=App.class.getResourceAsStream("/config.properties");
        InputStreamReader reader=new InputStreamReader(inputStream,"UTF-8");
        prop.load(reader);
        String savePath=prop.getProperty("savePath");
        String singer=prop.getProperty("singer");
        Stream.of("p1","p2","p3").forEach(name->{
            new Thread(new JsoupGetThread1("http://mp34.butterfly.mopaasapp.com",singer),name).start();
        });
        Stream.of("c1","c2","c3","c4","c5").forEach(name->{
            new Thread(new DownLoadThread1("http://mp34.butterfly.mopaasapp.com",savePath),name).start();
        });
    }
}

