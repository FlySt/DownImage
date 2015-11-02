package com.example.adm.downimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView)findViewById(R.id.iv);

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            iv.setImageBitmap((Bitmap)msg.obj);
        }
    };
    public  void click(View view){
        final String path = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
        final File file = new File(getCacheDir(),getFilename(path));
        if(file.exists()){
            System.out.println("从缓存读取图片");
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            iv.setImageBitmap(bm);
        }
        else {
            Thread t = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        //  StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                        // String path = "http://192.168.1.116/tomcat.png";

                        URL url = new URL(path);
                        //获取链接
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);
                        conn.connect();
                        if(conn.getResponseCode() == 200){
                            System.out.println("连接成功");
                            InputStream is = conn.getInputStream();

                            System.out.println(file.getAbsolutePath());
                            FileOutputStream fos = new FileOutputStream(file);
                            byte b[] = new byte[1024];
                            int len = 0;
                            while ((len = is.read(b))!=-1){
                                fos.write(b,0,len);
                            }
                            fos.close();
                            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                            Message msg = new Message();
                            msg.obj = bm;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }

    }

    private String getFilename(String path){
        int index = path.lastIndexOf("/");
        return path.substring(index);
    }
}
