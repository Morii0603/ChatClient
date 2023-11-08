package com.example.chatclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView showmsg;
    private EditText sendmsged;
    private EditText ipv4ed;
    private EditText ported;
    private Button sendmsgbt;
    private Button connectbt;
    private Handler handler;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private String receiveTxt,SendMsg="";//接收文本 发送文本
    private static String[] PERMISSIONS_STORAGE = {
            //依次权限申请
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applypermission();//权限申请
        InitView();

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==24){
                    showmsg.append("客户端:"+sendmsged.getText().toString()+"\n");
                    sendmsged.setText("");
                }else if(msg.what==98){
                    showmsg.append(receiveTxt);
                }
            }
        };

        connectbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionSeverThread.start();
                SendMsgThread.start();
            }
        });

        sendmsgbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(24);
                SendMsg = sendmsged.getText().toString();
            }
        });

    }

    private void InitView() {
        showmsg = (TextView) findViewById(R.id.showmsg);
        sendmsged = (EditText) findViewById(R.id.sendmsged);
        ipv4ed = (EditText) findViewById(R.id.ipv4ed);
        ported = (EditText) findViewById(R.id.ported);
        sendmsgbt = (Button) findViewById(R.id.sendmsgbt);
        connectbt = (Button) findViewById(R.id.connectbt);
        //showmsg.setMovementMethod(ScrollingMovementMethod.getInstance());//textview滚动 但控件会随键盘浮动
//        用此功能xml的textview控件添加如下两段
//        android:scrollbars="vertical"
//        android:fadeScrollbars="false"
    }


    Thread connectionSeverThread = new Thread(
            //连接服务器
            new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取编辑框组件
                        socket = new Socket(ipv4ed.getText().toString(), Integer.valueOf(ported.getText().toString()));
                        //连接服务器
                        out= new DataOutputStream(socket.getOutputStream());
                        // 创建DataOutputStream对象 发送数据
                        in = new DataInputStream(socket.getInputStream());
                    } catch (Exception e) {
                        // TODO Auto-generatetd catch block
                        e.printStackTrace();
                    }
                    while(true) {
                        try {
                            receiveTxt = in.readUTF()+"\n";
                            handler.sendEmptyMessage(98);
                            //发送空消息  1主要为了区分消息好执行改变组件信息的内容
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            }
    );

    Thread SendMsgThread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if(SendMsg != ""){
                            try {
                                out.writeUTF("客户端:"+SendMsg);//发送消息
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            SendMsg = "";
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
    );

    //定义判断权限申请的函数，在onCreat中调用就行
    public void applypermission(){
        if(Build.VERSION.SDK_INT>=23){
            boolean needapply=false;
            for(int i=0;i<PERMISSIONS_STORAGE.length;i++){
                int chechpermission= ContextCompat.checkSelfPermission(getApplicationContext(),
                        PERMISSIONS_STORAGE[i]);
                if(chechpermission!= PackageManager.PERMISSION_GRANTED){
                    needapply=true;
                }
            }
            if(needapply){
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_STORAGE,1);
            }
        }
    }
}