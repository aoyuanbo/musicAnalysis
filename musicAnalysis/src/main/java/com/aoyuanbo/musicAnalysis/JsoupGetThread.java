package com.aoyuanbo.musicAnalysis;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupGetThread implements Runnable {
    private Music music;
    private final String url;
    private final String singer;
    private int pageNum = 1;

    JsoupGetThread(String url, String singer) {
        this.url = url;
        this.singer = singer;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (App.musics) {
                if (App.musics.size() == 0) {
                    Connection conn = Jsoup.connect(url);
                    conn.data("mp3", singer);
                    conn.data("p", String.valueOf(pageNum));
                    Document doc;
                    boolean timeOut = true;
                    while (timeOut) {
                        try {
                            doc = conn.timeout(120_000).get();
                            System.out.println(conn);
                            final Elements ele = doc.select("div#wlsong li a");
                            for (Element element : ele) {
                                System.out.println("---------name---------");
                                String name = element.text().replaceAll("(.*-)", "");
                                System.out.println(name);
                                String id = element.attr("onclick").replaceAll(".+(?<=,)", "").replaceAll("'|\\);", "");
                                System.out.println("---------id---------");
                                System.out.println(id);
                                music = new Music(name, id);
                                App.musics.add(music);
                            }
                            pageNum++;
                            System.out.println("page:" + pageNum);
                            System.out.println("生产" + App.musics.size());
                            timeOut = false;
                        } catch (final IOException e) {
                            System.out.println("生产超时");
                            pageNum--;
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        App.musics.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}