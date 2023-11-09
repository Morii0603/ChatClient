package com.example.chatclient;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private final int BUFFER_SIZE = 1024;
    private TextView showmsg;
    private EditText sendmsged;
    private Button sendmsgbt;

    private Handler handler;
    private BufferedReader in;
    private DataOutputStream out;
    private Socket socket;
    private String receiveTxt,SendMsg="";//接收文本 发送文本
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        connectionSeverThread.start();
        SendMsgThread.start();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==24){
                    showmsg.append("我："+sendmsged.getText().toString()+"\n");
                    sendmsged.setText("");
                }else if(msg.what==98){
                    showmsg.append(receiveTxt);
                }
            }
        };

        sendmsgbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(24);
                SendMsg = sendmsged.getText().toString();
            }
        });
    }
    private void InitView() {
        showmsg = findViewById(R.id.showmsg);
        sendmsged = findViewById(R.id.sendmsged);
        sendmsgbt = findViewById(R.id.sendmsgbt);
    }

    Thread connectionSeverThread = new Thread(
            //连接服务器
            new Runnable() {
                @Override
                public void run() {
                    try {
                        //新建socket对象
                        socket = new Socket("192.168.1.104", 8000);
                        //连接服务器
                        out= new DataOutputStream(socket.getOutputStream());
                        // 创建DataOutputStream对象 发送数据
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        while (true) {
                            char[] buffer = new char[BUFFER_SIZE];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                receiveTxt = new String(buffer, 0, bytesRead);
                                System.out.println(receiveTxt);
                                handler.sendEmptyMessage(98);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    Thread SendMsgThread = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    while (true){
                        if(SendMsg != "" && out!=null){
                            try {
                                    out.writeUTF(SendMsg);//发送消息

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
}